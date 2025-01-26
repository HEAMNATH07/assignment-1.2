package com.example.Event.services.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "events")
public class Event {
    @Id
    private String id;
    private String name;
    private String description;
    private String status; // Pending / Approved
    private String employeeId; // The employee who created the event
    private String venueId;
    private int seatsBooked;
    private int totalSeats;
    private List<String> vendorIds; // List of vendor IDs
    private List<String> resourcePersonIds; // List of resource person IDs (cleaners, security, etc.)


}
