package com.aquent.crudapp.client;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

/**
 * Client operations.
 */
@Service
public interface ClientService {

    /**
     * Retrieves all of the client records.
     *
     * @return list of client records
     */
    List<Client> listClients();

    /**
     * Retrieves a client record by ID.
     *
     * @param id the client ID
     * @return the client record
     */
    Client readClient(Integer id);
    
    /**
     * Retrieves all associated personIDs by ID.
     *
     * @param id the client ID
     * @return list of IDs of all persons associated with client.
     */
    Set<Integer> listAssociatedPersonIds(Integer id);
    
    /**
     * Creates a new client record.
     *
     * @param person the values to save
     * @return the new client ID
     */
    Integer createClient(Client client, List<Integer> personIds);
    /**
     * Updates an existing client record.
     *
     * @param client the new values to save
     */
    void updateClient(Client client, List<Integer> newPersonIds);
      
    /**
     * Deletes a person record by ID.
     *
     * @param id the person ID
     */
    void deleteClient(Integer id);
    
    /**
     * Validates populated client data.
     *
     * @param client the values to validate
     * @return list of error messages
     */
    List<String> validateClient(Client client);
}
