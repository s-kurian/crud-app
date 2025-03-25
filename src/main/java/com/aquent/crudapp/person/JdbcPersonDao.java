package com.aquent.crudapp.person;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aquent.crudapp.client.Client;

/**
 * Spring JDBC implementation of {@link PersonDao}.
 */
@Component
public class JdbcPersonDao implements PersonDao {

	private static final String SQL_LIST_PEOPLE = "SELECT p.person_id, p.first_name, p.last_name, p.email_address, p.street_address, p.city, p.state, p.zip_code, c.name, c.client_id FROM person p "
			+ "LEFT JOIN client_person cp ON cp.person_id = p.person_id "
			+ "LEFT JOIN client c ON c.client_id = cp.client_id " + "ORDER BY p.first_name, p.last_name";

	private static final String SQL_DELETE_PERSON = "DELETE FROM person WHERE person_id = :personId";

	private static final String SQL_UPDATE_PERSON = "UPDATE person SET (first_name, last_name, email_address, street_address, city, state, zip_code)"
			+ " = (:firstName, :lastName, :emailAddress, :streetAddress, :city, :state, :zipCode)"
			+ " WHERE person_id = :personId";

	private static final String SQL_CREATE_PERSON = "INSERT INTO person (first_name, last_name, email_address, street_address, city, state, zip_code)"
			+ " VALUES (:firstName, :lastName, :emailAddress, :streetAddress, :city, :state, :zipCode)";

	private static final String SQL_LIST_PERSON_CLIENT_ASSOCIATIONS = "select p.person_id, p.first_name, p.last_name, p.email_address, p.street_address, p.city, p.state, p.zip_code, c.name, c.client_id FROM person p "
			+ "LEFT JOIN client_person cp ON cp.person_id = p.person_id "
			+ "LEFT JOIN client c ON c.client_id = cp.client_id " + "WHERE p.person_id = :personId";

	private static final String SQL_CREATE_PERSON_CLIENT_ASSOCIATION = "INSERT INTO client_person (client_id, person_id) "
			+ "VALUES (:clientId, :personId)";

	private static final String SQL_DELETE_PERSON_CLIENT_ASSOCIATION = "DELETE FROM client_person WHERE person_id = :personId and client_id = :clientId";
	
	private static final String SQL_DELETE_PERSON_ALL_CLIENT_ASSOCIATION = "DELETE FROM client_person WHERE person_id = :personId";
	
	private static final String SQL_LIST_PERSON_CLIENT_ASSOCIATION_IDS = "SELECT client_id FROM client_person WHERE person_id = :personId";

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public JdbcPersonDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Person> listPeople() {
		PersonRowMapper personRowMapper = new PersonRowMapper();
		namedParameterJdbcTemplate.getJdbcOperations().query(SQL_LIST_PEOPLE, personRowMapper);

		return personRowMapper.getPerson();
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Person readPerson(Integer personId) {
		PersonRowMapper personRowMapper = new PersonRowMapper();
		namedParameterJdbcTemplate.query(SQL_LIST_PERSON_CLIENT_ASSOCIATIONS,
				Collections.singletonMap("personId", personId), personRowMapper);
		if (personRowMapper.getPerson().isEmpty()) {
			throw new RuntimeException("Person with the given id does not exist.");
		}

		return personRowMapper.getPerson().getFirst();
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
	public void deletePerson(Integer personId) {
		Map<String, Integer> paramMap = Collections.singletonMap("personId", personId);
		namedParameterJdbcTemplate.update(SQL_DELETE_PERSON_ALL_CLIENT_ASSOCIATION, paramMap);
		namedParameterJdbcTemplate.update(SQL_DELETE_PERSON, paramMap);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
	public void updatePerson(Person person, List<Integer> clientIds) {
		
		namedParameterJdbcTemplate.update(SQL_UPDATE_PERSON, new BeanPropertySqlParameterSource(person));

		if(clientIds != null) {
	        
	        List<Integer> existingAssociatedClientIds = namedParameterJdbcTemplate.queryForList(SQL_LIST_PERSON_CLIENT_ASSOCIATION_IDS,  Collections.singletonMap("personId", person.getPersonId()), Integer.class);
	
	        // Determine client associations to add
	        List<Integer> clientsToAdd = clientIds.stream()
	                .filter(id -> !existingAssociatedClientIds.contains(id))
	                .toList();
	
	        // Determine client associations to remove
	        List<Integer> clientsToRemove = existingAssociatedClientIds.stream()
	                .filter(id -> !clientIds.contains(id))
	                .toList();
	
	        // Insert new client associations
	        if (!clientsToAdd.isEmpty()) {
	            List<SqlParameterSource> batchInsertParams = clientsToAdd.stream()
	                    .map((Integer clientId) -> new MapSqlParameterSource()
	                            .addValue("clientId", clientId)
	                            .addValue("personId", person.getPersonId()))
	                    .collect(Collectors.toList());
	            namedParameterJdbcTemplate.batchUpdate(SQL_CREATE_PERSON_CLIENT_ASSOCIATION, batchInsertParams.toArray(new SqlParameterSource[0]));
	        }
	
	        // Remove unwanted client associations
	        if (!clientsToRemove.isEmpty()) {
	            List<SqlParameterSource> batchDeleteParams = clientsToRemove.stream()
	                    .map((Integer idToDelete) -> new MapSqlParameterSource()
	                    		 .addValue("clientId", idToDelete)
		                            .addValue("personId", person.getPersonId()))
	                    .collect(Collectors.toList());
	            namedParameterJdbcTemplate.batchUpdate(SQL_DELETE_PERSON_CLIENT_ASSOCIATION, batchDeleteParams.toArray(new SqlParameterSource[0]));
	        }
        }  	
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
	public Integer createPerson(Person person, List<Integer> clientIds) {
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			namedParameterJdbcTemplate.update(SQL_CREATE_PERSON, new BeanPropertySqlParameterSource(person), keyHolder);
			Integer personId = keyHolder.getKey().intValue();

			if (!clientIds.isEmpty()) {
				List<SqlParameterSource> batchInsertParams = clientIds.stream()
						.map((Integer clientId) -> new MapSqlParameterSource().addValue("clientId", clientId)
								.addValue("personId", personId))
						.collect(Collectors.toList());
				namedParameterJdbcTemplate.batchUpdate(SQL_CREATE_PERSON_CLIENT_ASSOCIATION,
						batchInsertParams.toArray(new SqlParameterSource[0]));
			}
			return personId;
		} catch (DataAccessException e) {
			throw new RuntimeException("Database operation failed", e);
		}
	}

	/**
	 * Row mapper for client records.
	 */
	private static final class PersonRowMapper implements RowMapper<Person> {

		private final Map<Integer, Person> personMap = new HashMap<>();

		@Override
		public Person mapRow(ResultSet rs, int rowNum) throws SQLException {

			Integer personId = rs.getInt("person_id");

			Person person = personMap.get(personId);

			if (person == null) {
				person = new Person();
				person.setPersonId(personId);
				person.setFirstName(rs.getString("first_name"));
				person.setLastName(rs.getString("last_name"));
				person.setEmailAddress(rs.getString("email_address"));
				person.setStreetAddress(rs.getString("street_address"));
				person.setCity(rs.getString("city"));
				person.setState(rs.getString("state"));
				person.setZipCode(rs.getString("zip_code"));

				personMap.put(personId, person);
			}

			String companyName = rs.getString("name");
			if (companyName != null && !companyName.isEmpty()) {
				Client client = new Client();
				client.setClientId(rs.getInt("client_id"));
				client.setName(companyName);
				person.getClients().add(client);
			}
			return person;
		}

		public List<Person> getPerson() {
			return new ArrayList<>(personMap.values());
		}
	}
}
