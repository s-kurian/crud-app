package com.aquent.crudapp.person;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.web.servlet.ModelAndView;

import com.aquent.crudapp.client.Client;
import com.aquent.crudapp.client.ClientService;

import java.util.*;

public class PersonControllerTest {

    @Mock
    private PersonService personService;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private PersonController personController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void list_GET_ShouldReturnModelAndView() {
        List<Person> people = Arrays.asList(new Person());
        when(personService.listPeople()).thenReturn(people);

        ModelAndView mav = personController.list();

        assertEquals("person/list", mav.getViewName());
        assertEquals(people, mav.getModel().get("persons"));
        verify(personService, times(1)).listPeople();
    }

    @Test
    public void create_GET_ShouldReturnModelAndView() {
     
    	List<Client> clients = Arrays.asList(new Client());
        clients.get(0).setClientId(1);
        clients.get(0).setName("Client1");
        when(clientService.listClients()).thenReturn(clients);

        ModelAndView mav = personController.create();

        assertEquals("person/create", mav.getViewName());
        assertNotNull(mav.getModel().get("person"));
        assertEquals(clients, mav.getModel().get("allClients"));
        assertTrue(((List<String>) mav.getModel().get("errors")).isEmpty());
        verify(clientService, times(1)).listClients();
    }

    @Test
    public void create_POST_WithValidData_ShouldRedirect() {

        Person person = new Person();
        person.setFirstName("John Doe");
        List<String> errors = new ArrayList<>();
        when(personService.validatePerson(person)).thenReturn(errors);

 
        ModelAndView mav = personController.create(person, 1);
    
        assertEquals("redirect:/person/list", mav.getViewName());
        verify(personService, times(1)).createPerson(person, 1);
    }

    @Test
    public void create_POST_WithValidationErrors_ShouldReturnCreateView() {

        Person person = new Person();
        person.setFirstName("John Doe");
        List<String> errors = Arrays.asList("Name cannot be empty");
        when(personService.validatePerson(person)).thenReturn(errors);
        List<Client> clients = Arrays.asList(new Client());
        clients.get(0).setClientId(1);
        clients.get(0).setName("Client1");
        when(clientService.listClients()).thenReturn(clients);

        ModelAndView mav = personController.create(person, 1);

        assertEquals("person/create", mav.getViewName());
        assertEquals(errors, mav.getModel().get("errors"));
        verify(clientService, times(1)).listClients();
    }

    @Test
    public void edit_GET_ShouldReturnModelAndView() {
        Person person = new Person();
        List<Client> clients = Arrays.asList(new Client());
        when(personService.readPerson(1)).thenReturn(person);
        when(clientService.listClients()).thenReturn(clients);


        ModelAndView mav = personController.edit(1);

        assertEquals("person/edit", mav.getViewName());
        assertEquals(person, mav.getModel().get("person"));
        assertEquals(clients, mav.getModel().get("allClients"));
        assertTrue(((List<String>) mav.getModel().get("errors")).isEmpty());
        verify(personService, times(1)).readPerson(1);
        verify(clientService, times(1)).listClients();
    }

    @Test
    public void edit_POST_WithValidData_ShouldRedirect() {
        // Given
        Person person = new Person();
        List<String> errors = new ArrayList<>();
        when(personService.validatePerson(person)).thenReturn(errors);

        ModelAndView mav = personController.edit(person, 1);

        assertEquals("redirect:/person/list", mav.getViewName());
        verify(personService, times(1)).updatePerson(person, 1);
    }

    @Test
    public void edit_POST_WithValidationErrors_ShouldReturnEditView() {

        Person person = new Person();
        List<String> errors = Arrays.asList("Name cannot be empty");
        when(personService.validatePerson(person)).thenReturn(errors);

        ModelAndView mav = personController.edit(person, 1);

        assertEquals("person/edit", mav.getViewName());
        assertEquals(errors, mav.getModel().get("errors"));
        verify(personService, times(1)).validatePerson(person);
    }

    @Test
    public void delete_GET_ShouldReturnModelAndView() {

        Person person = new Person();
        when(personService.readPerson(1)).thenReturn(person);

        ModelAndView mav = personController.delete(1);

        assertEquals("person/delete", mav.getViewName());
        assertEquals(person, mav.getModel().get("person"));
        verify(personService, times(1)).readPerson(1);
    }

    @Test
    public void delete_POST_ShouldRedirect() {
        Integer personId = 1;
        String command = PersonController.COMMAND_DELETE;

        String viewName = personController.delete(command, personId);

        assertEquals("redirect:/person/list", viewName);
        verify(personService, times(1)).deletePerson(personId);
    }
}
