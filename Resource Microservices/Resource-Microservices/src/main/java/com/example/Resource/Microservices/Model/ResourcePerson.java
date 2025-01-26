package com.example.Resource.Microservices.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "resource_persons")
public class ResourcePerson {
    @Id
    private String id;
    private String name;
    private String role;
    private String contactInfo;
    private boolean isAvailable;
    private String username;
    private String password;
}