package com.aquent.crudapp.client;

import java.util.List;

import org.springframework.stereotype.Repository;


/**
 * Operations on the "client" table.
 */
@Repository
public interface ClientDao {

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
     * Updates an existing client record.
     *
     * @param client the new values to save
     */
    void updateClient(Client client, List<Integer> newPersonIds);

}
