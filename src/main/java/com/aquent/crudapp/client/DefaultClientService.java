package com.aquent.crudapp.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link ClientService}.
 */
@Component
public class DefaultClientService implements ClientService {

    private final ClientDao clientDao;
   // private final ClientPersonDao clientPersonDao;
    private final Validator validator;

    public DefaultClientService(ClientDao clientDao,Validator validator) {
        this.clientDao = clientDao;
        this.validator = validator;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Client> listClients() {
        return clientDao.listClients();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Client readClient(Integer id) {
        return clientDao.readClient(id);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
    public Integer createClient(Client client, List<Integer> personIds) {
        return clientDao.createClient(client, personIds);
    }
    
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
    public void updateClient(Client client, List<Integer> newPersonIds) {
    	clientDao.updateClient(client, newPersonIds);
    }
    

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
    public void deleteClient(Integer id) {
    	clientDao.deleteClient(id);
    }
    
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Set<Integer> listAssociatedPersonIds(Integer id)
    {
    	return clientDao.listAssociatedPersonIds(id);
    }
    
    @Override
    public List<String> validateClient(Client client) {
        Set<ConstraintViolation<Client>> violations = validator.validate(client);
        List<String> errors = new ArrayList<String>(violations.size());
        for (ConstraintViolation<Client> violation : violations) {
            errors.add(violation.getMessage());
        }
        Collections.sort(errors);
        return errors;
    }
}
