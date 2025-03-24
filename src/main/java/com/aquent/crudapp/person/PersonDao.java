package com.aquent.crudapp.person;

import java.util.List;

import org.springframework.stereotype.Repository;

/**
 * Operations on the "person" table.
 */
@Repository
public interface PersonDao {

    /**
     * Retrieves all of the person records.
     *
     * @return list of person records
     */
    List<Person> listPeople();
    
    /**
     * Creates a new person record.
     *
     * @param person the values to save
     * @param clientIds the client associations to save
     * @return the new person ID
     */
    Integer createPerson(Person person, List<Integer> clientIds);

    /**
     * Retrieves a person record by ID.
     *
     * @param id the person ID
     * @return the person record
     */
    Person readPerson(Integer id);

    /**
     * Updates an existing person record.
     *
     * @param person the new person values to save
     * @param clientIds the client associations to save
     */
    void updatePerson(Person person,List<Integer> clientIds);

    /**
     * Deletes a person record by ID.
     *
     * @param id the person ID
     */
    void deletePerson(Integer id);
}
