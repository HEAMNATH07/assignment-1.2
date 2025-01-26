package com.example.Venue.Microservices.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "venues")
public class Venue {
    @Id
    private String id;
    private String name;
    private String location;
    private int capacity;
    private boolean isAvailable;
}
