package com.example.Resource.Microservices.Controller;

import com.example.Resource.Microservices.Model.ResourcePerson;
import com.example.Resource.Microservices.Service.ResourcePersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resource-persons")
public class ResourcePersonController {

    @Autowired
    private ResourcePersonService resourcePersonService;

    // Register a new resource person
    @PostMapping
    public ResponseEntity<ResourcePerson> registerResourcePerson(@RequestBody ResourcePerson resourcePerson) {
        ResourcePerson savedResourcePerson = resourcePersonService.registerResourcePerson(resourcePerson);
        return new ResponseEntity<>(savedResourcePerson, HttpStatus.CREATED);
    }

    // Get resource person by ID
    @GetMapping("/{id}")
    public ResponseEntity<ResourcePerson> getResourcePersonById(@PathVariable String id) {
        ResourcePerson resourcePerson = resourcePersonService.getResourcePersonById(id);
        if (resourcePerson != null) {
            return new ResponseEntity<>(resourcePerson, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get all resource persons
    @GetMapping
    public ResponseEntity<Iterable<ResourcePerson>> getAllResourcePersons() {
        Iterable<ResourcePerson> resourcePersons = resourcePersonService.getAllResourcePersons();
        return new ResponseEntity<>(resourcePersons, HttpStatus.OK);
    }

    // Update availability status of a resource person
    @PutMapping("/{id}/availability")
    public ResponseEntity<ResourcePerson> updateAvailability(
            @PathVariable String id,
            @RequestParam boolean isAvailable) {
        ResourcePerson updatedResourcePerson = resourcePersonService.updateAvailability(id, isAvailable);
        if (updatedResourcePerson != null) {
            return new ResponseEntity<>(updatedResourcePerson, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete a resource person by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResourcePerson(@PathVariable String id) {
        boolean isDeleted = resourcePersonService.deleteResourcePerson(id);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
