package com.aquent.crudapp.client;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
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

import com.aquent.crudapp.person.Person;

/**
 * Spring JDBC implementation of {@link ClientDao}.
 */
@Component
public class JdbcClientDao implements ClientDao {

    private static final String SQL_LIST_CLIENTS = "select c.client_id, c.name, c.website_url, c.phone_number, c.street_address, c.city, c.state, c.zip_code , p.first_name, p.last_name from client c "
									    		+ "left join client_person cp on cp.client_id=c.client_id "
									    		+ "left join person p on p.person_id=cp.person_id";
    
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
    	ClientRowMapper clientRowMapper = new ClientRowMapper();
        namedParameterJdbcTemplate.getJdbcOperations().query(SQL_LIST_CLIENTS, clientRowMapper);
        return clientRowMapper.getClients();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Client readClient(Integer clientId) {
		Map<String, Integer> paramMap = Collections.singletonMap("clientId", clientId);
        
    	Client client =  namedParameterJdbcTemplate.queryForObject(SQL_READ_CLIENT, paramMap, new BeanPropertyRowMapper<>(Client.class));
    	
    	if(client != null) {
    		List<Person> persons =  namedParameterJdbcTemplate.query(SQL_LIST_CLIENT_PERSON_ASSOCIATIONS, paramMap, new BeanPropertyRowMapper<>(Person.class));
        	client.setContacts(persons);
    	}
    	return client;
    }
    
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Set<Integer> listAssociatedPersonIds(Integer id)
    {
    	Client client = readClient(id);
    	Set<Integer> selectedPersonIds = client.getContacts().stream()
    			.map(Person::getPersonId)
    			.collect(Collectors.toSet());
    	return selectedPersonIds;
    }
    
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
    public Integer createClient(Client client, List<Integer> personIds) {
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

    /**
     * Row mapper for client records.
     */
    private static final class ClientRowMapper implements RowMapper<Client> {

    	private final Map<Integer, Client> clientMap = new HashMap<>(); 
    	
        @Override
        public Client mapRow(ResultSet rs, int rowNum) throws SQLException {
        	
        	
        	Integer  clientId = rs.getInt("client_id");

            Client client = clientMap.get(clientId);
            if (client == null) {
            	client = new Client();
                client.setClientId(clientId);
                client.setName(rs.getString("name"));
                client.setWebsiteUrl(rs.getString("website_url"));
                client.setPhoneNumber(rs.getString("phone_number"));
                client.setStreetAddress(rs.getString("street_address"));
                client.setCity(rs.getString("city"));
                client.setState(rs.getString("state"));
                client.setZipCode(rs.getString("zip_code"));
                
                clientMap.put(clientId, client); 
            }

            
            String contactFirstName = rs.getString("first_name");
            if (contactFirstName != null && !contactFirstName.isEmpty()) {  
                Person contact = new Person();
                contact.setFirstName(contactFirstName);
                contact.setLastName(rs.getString("last_name"));
                client.getContacts().add(contact);
            }
            return client;
        }
        
        public List<Client> getClients() {
        	return new ArrayList<>(clientMap.values());
        }
    }
}
