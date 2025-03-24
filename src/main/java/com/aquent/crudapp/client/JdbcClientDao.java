package com.aquent.crudapp.client;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aquent.crudapp.person.Person;

/**
 * Spring JDBC implementation of {@link ClientDao}.
 */
@Component
public class JdbcClientDao implements ClientDao {

    private static final String SQL_LIST_CLIENTS = "SELECT * FROM client ORDER BY name, client_id";
    
    private static final String SQL_READ_CLIENT = "SELECT * FROM client WHERE client_id = :clientId";
    
    private static final String SQL_UPDATE_CLIENT = "UPDATE client SET (name, website_url, phone_number, street_address, city, state, zip_code)"
                                                  + " = (:name, :websiteUrl, :phoneNumber, :streetAddress, :city, :state, :zipCode)"
                                                  + " WHERE client_id = :clientId";
    
    private static final String SQL_CREATE_CLIENT = "INSERT INTO client (name, website_url, phone_number, street_address, city, state, zip_code)"
                                                  + " VALUES (:name, :websiteUrl, :phoneNumber, :streetAddress, :city, :state, :zipCode)";

    private static final String SQL_DELETE_CLIENT = "DELETE FROM client WHERE client_id = :clientId";
     
    private static final String SQL_LIST_CLIENT_PERSON_ASSOCIATIONS = "SELECT p.* FROM person p"
												+" JOIN client_person cp on p.person_id = cp.person_id"
												+" WHERE cp.client_id = :clientId"
												+ "	 ORDER BY p.first_name, p.last_name";
    
    private static final String SQL_LIST_CLIENT_PERSON_ASSOCIATION_IDS = "SELECT person_id FROM client_person WHERE client_id = :clientId";
    
    private static final String SQL_CREATE_CLIENT_PERSON_ASSOCIATION = "INSERT INTO client_person (client_id, person_id) "
    																 + "VALUES (:clientId, :personId)";
    
    private static final String SQL_DELETE_CLIENT_PERSON_ASSOCIATION = "DELETE FROM client_person WHERE client_id = :clientId AND person_id = :personId";
    
    private static final String SQL_DELETE_CLIENT_ALL_PERSON_ASSOCIATION = "DELETE FROM client_person WHERE client_id = :clientId";			 
    
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public JdbcClientDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Client> listClients() {
        return namedParameterJdbcTemplate.getJdbcOperations().query(SQL_LIST_CLIENTS, new BeanPropertyRowMapper<>(Client.class));
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Client readClient(Integer clientId) {
		Map<String, Integer> paramMap = Collections.singletonMap("clientId", clientId);
        
    	Client client =  namedParameterJdbcTemplate.queryForObject(SQL_READ_CLIENT, paramMap, new BeanPropertyRowMapper<>(Client.class));
    	
    	if(client != null) {
    		List<Person> persons =  namedParameterJdbcTemplate.query(SQL_LIST_CLIENT_PERSON_ASSOCIATIONS, paramMap, new BeanPropertyRowMapper<>(Person.class));
        	client.setPersons(persons);
    	}
    	return client;
    }
    
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
    public Integer createClient(Client client, List<Integer> personIds){
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(SQL_CREATE_CLIENT, new BeanPropertySqlParameterSource(client), keyHolder);
        Integer clientId = keyHolder.getKey().intValue();
     
        if (!personIds.isEmpty()) {
            List<SqlParameterSource> batchInsertParams = personIds.stream()
                    .map((Integer personId) -> new MapSqlParameterSource()
                            .addValue("clientId", clientId)
                            .addValue("personId", personId))
                    .collect(Collectors.toList());
            namedParameterJdbcTemplate.batchUpdate(SQL_CREATE_CLIENT_PERSON_ASSOCIATION, batchInsertParams.toArray(new SqlParameterSource[0]));
        }
        return clientId;
    }
    
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
	public void updateClient(Client client, List<Integer> newPersonIds) {
    	
        namedParameterJdbcTemplate.update(SQL_UPDATE_CLIENT, new BeanPropertySqlParameterSource(client));
        
        if(newPersonIds != null) {
	        
        	// Fetch current person associations for the client
	        List<Integer> existingAssociatedPersonIds = namedParameterJdbcTemplate.queryForList(SQL_LIST_CLIENT_PERSON_ASSOCIATION_IDS,  Collections.singletonMap("clientId", client.getClientId()), Integer.class);
	
	        // Determine person associations to add
	        List<Integer> personsToAdd = newPersonIds.stream()
	                .filter(id -> !existingAssociatedPersonIds.contains(id))
	                .toList();
	
	        // Determine person associations to remove
	        List<Integer> personsToRemove = existingAssociatedPersonIds.stream()
	                .filter(id -> !newPersonIds.contains(id))
	                .toList();
	
	        // Insert new person associations
	        if (!personsToAdd.isEmpty()) {
	            List<SqlParameterSource> batchInsertParams = personsToAdd.stream()
	                    .map((Integer personId) -> new MapSqlParameterSource()
	                            .addValue("clientId", client.getClientId())
	                            .addValue("personId", personId))
	                    .collect(Collectors.toList());
	            namedParameterJdbcTemplate.batchUpdate(SQL_CREATE_CLIENT_PERSON_ASSOCIATION, batchInsertParams.toArray(new SqlParameterSource[0]));
	        }
	
	        // Remove unwanted person associations
	        if (!personsToRemove.isEmpty()) {
	            List<SqlParameterSource> batchDeleteParams = personsToRemove.stream()
	                    .map((Integer personId) -> new MapSqlParameterSource()
	                            .addValue("clientId", client.getClientId())
	                            .addValue("personId", personId))
	                    .collect(Collectors.toList());
	            namedParameterJdbcTemplate.batchUpdate(SQL_DELETE_CLIENT_PERSON_ASSOCIATION, batchDeleteParams.toArray(new SqlParameterSource[0]));
	        }
        }  	
    }
    

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
    public void deleteClient(Integer clientId) {
    	Map<String, Integer> paramMap = Collections.singletonMap("clientId", clientId);

        namedParameterJdbcTemplate.update(SQL_DELETE_CLIENT_ALL_PERSON_ASSOCIATION, paramMap);
        namedParameterJdbcTemplate.update(SQL_DELETE_CLIENT, paramMap);
    }

}
