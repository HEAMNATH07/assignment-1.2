package com.example.Resource.Microservices.Service;

import com.example.Resource.Microservices.Model.ResourcePerson;
import com.example.Resource.Microservices.Repository.ResourcePersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourcePersonService {

    @Autowired
    private ResourcePersonRepository resourcePersonRepository;

    // Register a new resource person
    public ResourcePerson registerResourcePerson(ResourcePerson resourcePerson) {
        return resourcePersonRepository.save(resourcePerson);
    }

    // Get resource person by ID
    public ResourcePerson getResourcePersonById(String id) {
        return resourcePersonRepository.findById(id).orElse(null); // Return null if not found
    }

    // Get all resource persons
    public Iterable<ResourcePerson> getAllResourcePersons() {
        return resourcePersonRepository.findAll();
    }

    // Update availability status of a resource person
    public ResourcePerson updateAvailability(String id, boolean isAvailable) {
        ResourcePerson resourcePerson = resourcePersonRepository.findById(id).orElse(null);
        if (resourcePerson != null) {
            resourcePerson.setAvailable(isAvailable);
            return resourcePersonRepository.save(resourcePerson);
        }
        return null; // Return null if resource person is not found
    }

    // Delete a resource person by ID
    public boolean deleteResourcePerson(String id) {
        ResourcePerson resourcePerson = resourcePersonRepository.findById(id).orElse(null);
        if (resourcePerson != null) {
            resourcePersonRepository.delete(resourcePerson);
            return true;
        }
        return false; // Return false if the resource person is not found
    }
}
