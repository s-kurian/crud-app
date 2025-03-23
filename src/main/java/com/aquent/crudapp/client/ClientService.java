package com.aquent.crudapp.client;

import java.util.List;

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
     * Retrieves all of the person records associated with a client.
     *
     * @return list of person records
     */
    //List<Person> listPersonsForClient(Integer clientId);
    

    /**
     * Retrieves a client record by ID.
     *
     * @param id the client ID
     * @return the client record
     */
    Client readClient(Integer id);
    
    /**
     * Updates an existing client record.
     *
     * @param client the new values to save
     */
    void updateClient(Client client, List<Integer> newPersonIds);
    
    /**
     * Validates populated client data.
     *
     * @param client the values to validate
     * @return list of error messages
     */
    List<String> validateClient(Client client);
}
