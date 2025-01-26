package com.example.Event.services.Service;


import com.example.Event.services.Config.*;
import com.example.Event.services.DTO.*;
import com.example.Event.services.Model.Event;
import com.example.Event.services.Repository.EventRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AdminFeignClient adminFeignClient;

    @Autowired
    private EmployeeFeignClient employeeFeignClient;

    @Autowired
    private ResourcePersonFeignClient resourcePersonFeignClient;

    @Autowired
    private VendorFeignClient vendorFeignClient;

    @Autowired
    private VenueFeignClient venueFeignClient;

    @Autowired
    private JavaMailSender javaMailSender;

    // Create a new event
    public Event createEvent(Event event) {
        // Send a request to the Admin for event approval
        event.setStatus("Pending");
        Event createdEvent = eventRepository.save(event);
        return createdEvent;
    }

    // Get event details
    public Event getEvent(String eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isPresent()) {
            return event.get();
        } else {
            throw new RuntimeException("Event not found");
        }
    }

    // Approve event
    public Event approveEvent(String eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isPresent()) {
            Event approvedEvent = event.get();
            approvedEvent.setStatus("Approved");
            return eventRepository.save(approvedEvent);
        } else {
            throw new RuntimeException("Event not found");
        }
    }

    // Add vendor to the event
    public Event addVendor(String eventId, String vendorId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isPresent()) {
            Event updatedEvent = event.get();
            updatedEvent.getVendorIds().add(vendorId);
            return eventRepository.save(updatedEvent);
        } else {
            throw new RuntimeException("Event not found");
        }
    }

    // Request resource person for the event (e.g., cleaner, security)
    public Event requestResourcePerson(String eventId, String resourcePersonId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isPresent()) {
            Event updatedEvent = event.get();
            updatedEvent.getResourcePersonIds().add(resourcePersonId);
            return eventRepository.save(updatedEvent);
        } else {
            throw new RuntimeException("Event not found");
        }
    }

//    // Book seats for the event
//    public Event bookSeats(String eventId, int seats) {
//        Optional<Event> event = eventRepository.findById(eventId);
//        if (event.isPresent()) {
//            Event updatedEvent = event.get();
//            int availableSeats = updatedEvent.getTotalSeats() - updatedEvent.getSeatsBooked();
//
//            if (availableSeats >= seats) {
//                updatedEvent.setSeatsBooked(updatedEvent.getSeatsBooked() + seats);
//                return eventRepository.save(updatedEvent);
//            } else {
//                throw new RuntimeException("Not enough seats available");
//            }
//        } else {
//            throw new RuntimeException("Event not found");
//        }
//    }
public Event bookSeats(String eventId, int seats, String userEmail) throws Exception {
    Optional<Event> event = eventRepository.findById(eventId);
    if (event.isPresent()) {
        Event updatedEvent = event.get();
        int availableSeats = updatedEvent.getTotalSeats() - updatedEvent.getSeatsBooked();

        if (availableSeats >= seats) {
            updatedEvent.setSeatsBooked(updatedEvent.getSeatsBooked() + seats);

            // Save the updated event details
            Event savedEvent = eventRepository.save(updatedEvent);

            // Generate QR code for the event booking
            String qrCodeData = "Event ID: " + eventId + "\nSeats Booked: " + seats;
            byte[] qrCode = generateQRCode(qrCodeData);

            // Send the email notification with QR code attached
            sendEventBookingEmail(userEmail, savedEvent, qrCode);

            return savedEvent;
        } else {
            throw new RuntimeException("Not enough seats available");
        }
    } else {
        throw new RuntimeException("Event not found");
    }
}

    // Send email notification with QR code
    private void sendEventBookingEmail(String userEmail, Event event, byte[] qrCode) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(userEmail);
        helper.setSubject("Event Booking Confirmation");
        helper.setText("Dear User,\n\n"
                + "Your booking for the event \"" + event.getName() + "\" with ID: \"" + event.getId() + "\" has been successfully confirmed.\n"
                + "Seats booked: " + event.getSeatsBooked() + "\n"
                + "Total Seats: " + event.getTotalSeats() + "\n\n"
                + "Please find the attached QR code for your records.\n\n"
                + "Best regards,\nEvent Management Team");

        // Create ByteArrayDataSource from the generated QR code
        ByteArrayDataSource qrCodeAttachment = new ByteArrayDataSource(qrCode, "image/png");
        helper.addAttachment("EventQRCode.png", qrCodeAttachment);

        // Send the email
        javaMailSender.send(mimeMessage);
    }

    // Generate QR Code from data
    private byte[] generateQRCode(String data) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return outputStream.toByteArray();
    }
    // Check venue availability before event booking
    public boolean isVenueAvailable(String venueId, int seatsRequired) {
        VenueDTO venue = venueFeignClient.getVenueById(venueId);
        if (venue != null) {
            return venue.getCapacity() >= seatsRequired && venue.isAvailable();
        } else {
            throw new RuntimeException("Venue not found");
        }
    }

    // Send event approval request to Admin service
    public AdminDTO sendEventApprovalRequest(String adminId) {
        return adminFeignClient.getAdminDetails(adminId);
    }

    // Fetch employee details
    public EmployeeDTO getEmployeeDetails(String employeeId) {
        return employeeFeignClient.getEmployeeById(employeeId);
    }

    // Fetch vendor details
    public VendorDTO getVendorDetails(String vendorId) {
        return vendorFeignClient.getById(vendorId);
    }

    // Fetch resource person details
    public ResourcePersonDTO getResourcePersonDetails(String resourcePersonId) {
        return resourcePersonFeignClient.getResourcePersonById(resourcePersonId);
    }

    // Fetch venue details
    public VenueDTO getVenueDetails(String venueId) {
        return venueFeignClient.getVenueById(venueId);
    }
}
