package com.aquent.crudapp.person;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aquent.crudapp.client.Client;
import com.aquent.crudapp.client.ClientService;

/**
 * Controller for handling basic person management operations.
 */
@Controller
@RequestMapping("person")
public class PersonController {

    public static final String COMMAND_DELETE = "Delete";
    
    private final PersonService personService;
    private final ClientService clientService;

    public PersonController(PersonService personService, ClientService clientService) {
        this.personService = personService;
        this.clientService = clientService;
    }

    /**
     * Renders the listing page.
     *
     * @return list view populated with the current list of people
     */
    @GetMapping(value = "list")
    public ModelAndView list() {
        ModelAndView mav = new ModelAndView("person/list");
        mav.addObject("persons", personService.listPeople());
        return mav;
    }

    /**
     * Renders an empty form used to create a new person record.
     *
     * @return create view populated with an empty person
     */
    @GetMapping(value = "create")
    public ModelAndView create() {
        ModelAndView mav = new ModelAndView("person/create");
        mav.addObject("person", new Person());
        mav.addObject("allClients", clientService.listClients());
        mav.addObject("errors", new ArrayList<String>());
        return mav;
    }

    /**
     * Validates and saves a new person.
     * On success, the user is redirected to the listing page.
     * On failure, the form is redisplayed with the validation errors.
     *
     * @param person populated form bean for the person
     * @return redirect, or create view with errors
     */
    @PostMapping(value = "create")
    public ModelAndView create(Person person, @RequestParam(required=false) Integer clientId) {
        List<String> errors = personService.validatePerson(person);
        if (errors.isEmpty()) {
            personService.createPerson(person, clientId);
            return new ModelAndView("redirect:/person/list");
        } else {
            ModelAndView mav = new ModelAndView("person/create");
            mav.addObject("person", person);
            mav.addObject("allClients", clientService.listClients());
            mav.addObject("errors", errors);
            return mav;
        }
    }

    /**
     * Renders an edit form for an existing person record.
     *
     * @param personId the ID of the person to edit
     * @return edit view populated from the person record
     */
    @GetMapping(value = "edit/{personId}")
    public ModelAndView edit(@PathVariable Integer personId) {
        ModelAndView mav = new ModelAndView("person/edit");
        
        Person person = personService.readPerson(personId);
        
        List<Client> clients = person.getClients();
        
        if( clients!= null && !clients.isEmpty()) {
        	// UI supports a single associated client for a person. So, we just get the first here.
            mav.addObject("selectedClientId", clients.getFirst().getClientId());   	
        }
        mav.addObject("person", person);
        mav.addObject("allClients", clientService.listClients());
        mav.addObject("errors", new ArrayList<String>());
        return mav;
    }

    /**
     * Validates and saves an edited person.
     * On success, the user is redirected to the listing page.
     * On failure, the form is redisplayed with the validation errors.
     *
     * @param person populated form bean for the person
     * @return redirect, or edit view with errors
     */
    @PostMapping(value = "edit")
    public ModelAndView edit(Person person, @RequestParam(required=false) Integer clientId) {
        List<String> errors = personService.validatePerson(person);
        if (errors.isEmpty()) {
            personService.updatePerson(person, clientId);
            return new ModelAndView("redirect:/person/list");
        } else {
            ModelAndView mav = new ModelAndView("person/edit");
            mav.addObject("person", person);
            mav.addObject("errors", errors);
            return mav;
        }
    }

    /**
     * Renders the deletion confirmation page.
     *
     * @param personId the ID of the person to be deleted
     * @return delete view populated from the person record
     */
    @GetMapping(value = "delete/{personId}")
    public ModelAndView delete(@PathVariable Integer personId) {
        ModelAndView mav = new ModelAndView("person/delete");
        mav.addObject("person", personService.readPerson(personId));
        return mav;
    }

    /**
     * Handles person deletion or cancellation, redirecting to the listing page in either case.
     *
     * @param command the command field from the form
     * @param personId the ID of the person to be deleted
     * @return redirect to the listing page
     */
    @PostMapping(value = "delete")
    public String delete(@RequestParam String command, @RequestParam Integer personId) {
        if (COMMAND_DELETE.equals(command)) {
            personService.deletePerson(personId);
        }
        return "redirect:/person/list";
    }
}
