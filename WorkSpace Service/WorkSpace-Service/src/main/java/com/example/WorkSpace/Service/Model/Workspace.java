package com.example.WorkSpace.Service.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "workspaces")
public class Workspace {
    @Id
    private String id;
    private String floor;
    private String room;
    private String seatNumber;
    private boolean isBooked;
    private String projectId;
    private String employeeId;
    private LocalDateTime bookTime;

    public boolean isBookedForMoreThan24Hours() {
        return bookTime != null && bookTime.plusHours(24).isBefore(LocalDateTime.now());
    }
}