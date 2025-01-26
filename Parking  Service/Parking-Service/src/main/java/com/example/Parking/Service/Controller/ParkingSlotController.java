package com.example.Parking.Service.Controller;

import com.example.Parking.Service.Enum.VehicleType;
import com.example.Parking.Service.Model.ParkingSlot;
import com.example.Parking.Service.Service.ParkingSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/parking-slots")
public class ParkingSlotController {

    @Autowired
    private ParkingSlotService parkingSlotService;

    // Create or Update Parking Slot
    @PostMapping
    public ResponseEntity<ParkingSlot> createOrUpdateParkingSlot(@RequestBody ParkingSlot parkingSlot) {
        ParkingSlot savedParkingSlot = parkingSlotService.saveParkingSlot(parkingSlot);
        return ResponseEntity.ok(savedParkingSlot);
    }

    // Get All Parking Slots
    @GetMapping
    public ResponseEntity<List<ParkingSlot>> getAllParkingSlots() {
        List<ParkingSlot> parkingSlots = parkingSlotService.getAllParkingSlots();
        return ResponseEntity.ok(parkingSlots);
    }

    // Get Parking Slot by ID
    @GetMapping("/{id}")
    public ResponseEntity<ParkingSlot> getParkingSlotById(@PathVariable String id) {
        Optional<ParkingSlot> parkingSlot = parkingSlotService.getParkingSlotById(id);
        return parkingSlot.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get Parking Slots by Floor
    @GetMapping("/floor/{floor}")
    public ResponseEntity<List<ParkingSlot>> getParkingSlotsByFloor(@PathVariable String floor) {
        List<ParkingSlot> parkingSlots = parkingSlotService.getParkingSlotsByFloor(floor);
        return ResponseEntity.ok(parkingSlots);
    }

    // Get Parking Slots by Vehicle Type
    @GetMapping("/vehicle-type/{vehicleType}")
    public ResponseEntity<List<ParkingSlot>> getParkingSlotsByVehicleType(@PathVariable String vehicleType) {
        VehicleType type = VehicleType.valueOf(vehicleType.toUpperCase());
        List<ParkingSlot> parkingSlots = parkingSlotService.getParkingSlotsByVehicleType(type.name());
        return ResponseEntity.ok(parkingSlots);
    }

    // Get Parking Slots by Booking Status
    @GetMapping("/status/{isBooked}")
    public ResponseEntity<List<ParkingSlot>> getParkingSlotsByBookingStatus(@PathVariable boolean isBooked) {
        List<ParkingSlot> parkingSlots = parkingSlotService.getParkingSlotsByBookingStatus(isBooked);
        return ResponseEntity.ok(parkingSlots);
    }

    // Delete Parking Slot
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteParkingSlot(@PathVariable String id) {
        parkingSlotService.deleteParkingSlot(id);
        return ResponseEntity.ok("Parking slot deleted successfully");
    }

    // Book a Parking Slot
    @PostMapping("/book")
    public ResponseEntity<String> bookParkingSlot(@RequestParam String floor,
                                                  @RequestParam String section,
                                                  @RequestParam String slotNumber,
                                                  @RequestParam String employeeId,
                                                  @RequestParam String vehicleType) {
        VehicleType type = VehicleType.valueOf(vehicleType.toUpperCase());
        String result = parkingSlotService.bookParkingSlot(floor, section, slotNumber, employeeId, type);
        return ResponseEntity.ok(result);
    }

    // Unbook a Parking Slot
    @PostMapping("/unbook")
    public ResponseEntity<String> unbookParkingSlot(@RequestParam String parkingSlotId) {
        String result = parkingSlotService.unbookParkingSlot(parkingSlotId);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/unbook-after-24-hours")
    public String unbookParkingSlotsAfter24Hours() {
        parkingSlotService.unbookParkingSlotsAfter24Hours();  // Call the service method
        return "Parking slots unbooked successfully if they were booked more than 24 hours ago.";
    }

    // Get Available Parking Slots by Vehicle Type
    @GetMapping("/available/{vehicleType}")
    public ResponseEntity<List<ParkingSlot>> getAvailableParkingSlots(@PathVariable String vehicleType) {
        VehicleType type = VehicleType.valueOf(vehicleType.toUpperCase());
        List<ParkingSlot> availableParkingSlots = parkingSlotService.getAvailableParkingSlots(type);
        return ResponseEntity.ok(availableParkingSlots);
    }
}
