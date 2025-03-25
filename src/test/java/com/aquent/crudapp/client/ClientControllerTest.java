package com.aquent.crudapp.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import com.aquent.crudapp.person.Person;
import com.aquent.crudapp.person.PersonService;


public class ClientControllerTest {

    @Mock
    private ClientService clientService;
    
    @Mock
    private PersonService personService;

    @InjectMocks
    private ClientController clientController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void edit_GET_ShouldReturnCorrectModelAndView() {
    	
        Client client = new Client();
        client.setClientId(1);
        client.setName("Test Client");

        Person person1 = new Person();
        person1.setPersonId(101);
        person1.setFirstName("John");
        person1.setLastName("Smith");

        client.setContacts(Arrays.asList(person1));


        when(clientService.readClient(1)).thenReturn(client);
        when(personService.listPeople()).thenReturn(Arrays.asList(person1));

        ModelAndView result = clientController.edit(1);

        assertEquals("client/edit", result.getViewName());

        assertEquals(client, result.getModel().get("client"));

        Set<Integer> expectedPersonIds = new HashSet<>(Arrays.asList(101));
        assertEquals(expectedPersonIds, result.getModel().get("selectedPersonIds"));

        List<Person> allPersons = (List<Person>) result.getModel().get("allPersons");
        assertTrue(allPersons.contains(person1));

        assertEquals(new ArrayList<String>(), result.getModel().get("errors"));
    }

    @Test
    public void edit_POST_WithValidData_ShouldRedirect() {
    	
        Client client = new Client();
        client.setClientId(1);
        client.setName("Test Client");

        List<Integer> personIds = Arrays.asList(101, 102);
        
        when(clientService.validateClient(client)).thenReturn(Collections.emptyList());

        ModelAndView result = clientController.edit(client, personIds);

        assertEquals("redirect:/client/list", result.getViewName());
        
        verify(clientService, times(1)).updateClient(client, personIds);
    }
    
    @Test
    public void edit_POST_InvalidData_ShouldReturnEditPageWithErrors() {
    	
        Client client = new Client();
        client.setClientId(1);
        
        Person person1 = new Person();
        person1.setPersonId(101);
        person1.setFirstName("John");
        person1.setLastName("Smith");

        Person person2 = new Person();
        person2.setPersonId(102);
        person2.setFirstName("Jane");
        person2.setLastName("Smith");

        client.setContacts(Arrays.asList(person1, person2));

        List<Integer> personIds = Arrays.asList(101, 102);
        
        when(personService.listPeople()).thenReturn(Arrays.asList(person1, person2));
        
        List<String> errors = Arrays.asList("Client name is required", 
        					"Website URL is required",
        					"Phone number is required", 
        					"Street address is required", 
        					"City is required",
        					"State is required",
        					"Zip code is required"
        					);
        when(clientService.validateClient(client)).thenReturn(errors);
        
        ModelAndView result = clientController.edit(client, personIds);
        
        assertEquals("Should return the edit page on validation failure", "client/edit", result.getViewName());
        assertTrue("Errors should be present in the model", result.getModel().containsKey("errors"));
        assertEquals("Error messages should match expected errors", errors, result.getModel().get("errors"));
        assertEquals("Client should be returned in the model",client, result.getModel().get("client"));   
        
        verify(clientService, times(0)).updateClient(client, personIds);  
    }
    
    @Test
    public void delete_GET_ShouldReturnDeletePageWithClient() {
  
        Integer clientId = 1;
        Client client = new Client();
        client.setClientId(clientId);
        client.setName("Test Client");
        
        when(clientService.readClient(clientId)).thenReturn(client);

        ModelAndView result = clientController.delete(clientId);

        assertEquals( "Should return the delete page view", "client/delete", result.getViewName());
        assertEquals("The client should be added to the model", client, result.getModel().get("client")) ;
    }

    @Test
    public void delete_POST_ShouldDeleteClientWhenCommandIsDelete() {
    	
        Integer clientId = 1;
        String command = "delete";  
        Client client = new Client();
        client.setClientId(clientId);
        
    
        when(clientService.readClient(clientId)).thenReturn(client);

        doNothing().when(clientService).deleteClient(clientId);
    
        String result = clientController.delete(command, clientId);

        assertEquals("Should redirect to the client list page", "redirect:/client/list", result);
    }

    @Test
    public void delete_POST_ShouldNotDeleteClientWhenCommandIsCancel() {
   
        Integer clientId = 1;
        String command = "cancel";  
        Client client = new Client();
        client.setClientId(clientId);
        
        when(clientService.readClient(clientId)).thenReturn(client);

        String result = clientController.delete(command, clientId);

        assertEquals( "Should redirect to the client list page", "redirect:/client/list", result);

        verify(clientService, times(0)).deleteClient(clientId);
    }
    
    @Test
    public void list_GET_ShouldReturnModelAndView() {
    	
        List<Client> clients = new ArrayList<Client>();
        clients.add(new Client());

        when(clientService.listClients()).thenReturn(clients);

        ModelAndView result = clientController.list();
        
        assertEquals("client/list", result.getViewName());
        assertEquals(clients, result.getModel().get("clients"));       

        verify(clientService, times(1)).listClients();
    }

}
