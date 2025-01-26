package com.example.Parking.Service.Model;

import com.example.Parking.Service.Enum.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "parking_slots")
public class ParkingSlot {

    @Id
    private String id;
    private String floor;
    private String section;
    private String slotNumber;
    private VehicleType vehicleType; // Enum type for vehicle type
    private boolean isBooked;
    private String employeeId;
    private LocalDateTime bookTime;

    public boolean isBookedForMoreThan24Hours() {
        return bookTime != null && bookTime.plusHours(24).isBefore(LocalDateTime.now());
    }
}