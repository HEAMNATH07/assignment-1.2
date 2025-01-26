package com.example.Parking.Service.Service;

import com.example.Parking.Service.Config.EmployeeServiceClient;
import com.example.Parking.Service.DTO.EmployeeDTO;
import com.example.Parking.Service.Enum.VehicleType;
import com.example.Parking.Service.Model.ParkingSlot;
import com.example.Parking.Service.Repository.ParkingSlotRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingSlotService {

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    @Autowired
    private EmployeeServiceClient employeeServiceClient;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${parking.booking.queue}")
    private String queueName;

    // Create or Update a Parking Slot
    public ParkingSlot saveParkingSlot(ParkingSlot parkingSlot) {
        return parkingSlotRepository.save(parkingSlot);
    }

    // Get all parking slots
    public List<ParkingSlot> getAllParkingSlots() {
        return parkingSlotRepository.findAll();
    }

    // Get parking slot by ID
    public Optional<ParkingSlot> getParkingSlotById(String id) {
        return parkingSlotRepository.findById(id);
    }

    // Get parking slots by floor
    public List<ParkingSlot> getParkingSlotsByFloor(String floor) {
        return parkingSlotRepository.findByFloor(floor);
    }

    // Get parking slots by vehicle type
    public List<ParkingSlot> getParkingSlotsByVehicleType(String vehicleType) {
        return parkingSlotRepository.findByVehicleType(vehicleType);
    }

    // Get parking slots by booking status
    public List<ParkingSlot> getParkingSlotsByBookingStatus(boolean isBooked) {
        return parkingSlotRepository.findByIsBooked(isBooked);
    }

    // Delete a parking slot
    public void deleteParkingSlot(String id) {
        parkingSlotRepository.deleteById(id);
    }

    // Book a parking slot
    public String bookParkingSlot(String floor, String section, String slotNumber, String employeeId, VehicleType vehicleType) {
        ParkingSlot parkingSlot = parkingSlotRepository.findByFloorAndSectionAndSlotNumber(floor, section, slotNumber);

        if (parkingSlot == null) {
            throw new RuntimeException("Parking slot not found");
        }

        if (parkingSlot.isBooked()) {
            throw new RuntimeException("Parking slot is already booked");
        }

        if (parkingSlot.getVehicleType() != vehicleType) {
            throw new RuntimeException("This parking slot is not suitable for vehicle type: " + vehicleType);
        }

        EmployeeDTO employee = employeeServiceClient.getEmployeeById(employeeId);

        parkingSlot.setBooked(true);
        parkingSlot.setEmployeeId(employeeId);
        parkingSlotRepository.save(parkingSlot);

        try {
            String qrData = "Parking Slot ID: " + parkingSlot.getId() + "\nFloor: " + parkingSlot.getFloor() +
                            "\nSection: " + parkingSlot.getSection() + "\nSlot Number: " + parkingSlot.getSlotNumber();
            byte[] qrCode = generateQRCode(qrData);
            sendParkingAllocationEmail(employee.getEmail(), parkingSlot, qrCode);
        } catch (Exception e) {
            throw new RuntimeException("Error generating QR code or sending email", e);
        }

        return "Parking slot booked successfully!";
    }

    // Unbook a parking slot
    public String unbookParkingSlot(String parkingSlotId) {
        ParkingSlot parkingSlot = parkingSlotRepository.findById(parkingSlotId)
                .orElseThrow(() -> new RuntimeException("Parking slot not found"));

        parkingSlot.setBooked(false);
        parkingSlot.setEmployeeId(null);
        parkingSlotRepository.save(parkingSlot);

        return "Parking slot unbooked successfully!";
    }

    // Get available parking slots by vehicle type
    public List<ParkingSlot> getAvailableParkingSlots(VehicleType vehicleType) {
        return parkingSlotRepository.findByVehicleTypeAndIsBooked(vehicleType, false);
    }
    @Scheduled(cron = "0 0 0 * * ?") // Run at midnight every day
    public void unbookParkingSlotsAfter24Hours() {
        // Get all booked parking slots
        List<ParkingSlot> bookedSlots = parkingSlotRepository.findByIsBooked(true);

        // Loop through each booked parking slot
        for (ParkingSlot slot : bookedSlots) {
            // If the parking slot was booked more than 24 hours ago, unbook it
            if (slot.getBookTime().plusHours(24).isBefore(LocalDateTime.now())) {
                unbookParkingSlot(slot.getId()); // Unbook the slot
            }
        }
    }

    private byte[] generateQRCode(String data) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return outputStream.toByteArray();
    }

    private void sendParkingAllocationEmail(String email, ParkingSlot parkingSlot, byte[] qrCode) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(email);
        helper.setSubject("Parking Slot Allocation Confirmation");
        helper.setText("Dear User,\n\n" +
                "Your parking slot \"" + parkingSlot.getId() + "\" at \"" + parkingSlot.getSection() + "\" has been successfully allocated.\n" +
                "Please find the attached QR code for your records.\n\n" +
                "Best regards,\nParking Management Team");

        ByteArrayDataSource qrCodeAttachment = new ByteArrayDataSource(qrCode, "image/png");
        helper.addAttachment("ParkingSlotQR.png", qrCodeAttachment);

        javaMailSender.send(mimeMessage);
    }
}
