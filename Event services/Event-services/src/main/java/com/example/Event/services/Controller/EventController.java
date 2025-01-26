package com.example.Event.services.Controller;


import com.example.Event.services.Model.Event;
import com.example.Event.services.Service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return eventService.createEvent(event);
    }

    @GetMapping("/{id}")
    public Event getEvent(@PathVariable String id) {
        return eventService.getEvent(id);
    }

    @PostMapping("/{eventId}/approve")
    public Event approveEvent(@PathVariable String eventId) {
        return eventService.approveEvent(eventId);
    }

    @PostMapping("/{eventId}/add-vendor")
    public Event addVendor(@PathVariable String eventId, @RequestBody String vendorId) {
        return eventService.addVendor(eventId, vendorId);
    }

    @PostMapping("/{eventId}/request-resource")
    public Event requestResourcePerson(@PathVariable String eventId, @RequestBody String resourcePersonId) {
        return eventService.requestResourcePerson(eventId, resourcePersonId);
    }


    @PostMapping("/{eventId}/book-seats")
    public Event bookSeats(@PathVariable String eventId, @RequestParam int seats,@RequestParam String email) throws Exception {
        return eventService.bookSeats(eventId, seats,email);
    }
}
