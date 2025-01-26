package com.example.WorkSpace.Service.Service;

import com.example.WorkSpace.Service.Config.EmployeeServiceClient;
import com.example.WorkSpace.Service.DTO.EmployeeDTO;
import com.example.WorkSpace.Service.Model.Workspace;
import com.example.WorkSpace.Service.Repository.WorkspaceRepository;
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
import java.util.List;

@Service
public class WorkspaceService {

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private EmployeeServiceClient employeeServiceClient;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${workspace.booking.queue}")
    private String queueName;

    // Create a new workspace
    public Workspace createWorkspace(Workspace workspace) {
        return workspaceRepository.save(workspace);
    }

    // Retrieve a workspace by ID
    public Workspace getWorkspaceById(String id) {
        return workspaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workspace not found with ID: " + id));
    }

    // Retrieve all workspaces
    public List<Workspace> getAllWorkspaces() {
        return workspaceRepository.findAll();
    }

    // Update a workspace
    public Workspace updateWorkspace(String id, Workspace workspaceDetails) {
        Workspace existingWorkspace = getWorkspaceById(id);

        existingWorkspace.setFloor(workspaceDetails.getFloor());
        existingWorkspace.setRoom(workspaceDetails.getRoom());
        existingWorkspace.setSeatNumber(workspaceDetails.getSeatNumber());
        existingWorkspace.setProjectId(workspaceDetails.getProjectId());
        existingWorkspace.setBooked(workspaceDetails.isBooked());
        existingWorkspace.setEmployeeId(workspaceDetails.getEmployeeId());

        return workspaceRepository.save(existingWorkspace);
    }

    // Delete a workspace
    public void deleteWorkspace(String id) {
        Workspace workspace = getWorkspaceById(id);
        workspaceRepository.delete(workspace);
    }

    public String bookWorkspace(String floor, String room, String seatNumber, String employeeId) {
        Workspace workspace = workspaceRepository.findByFloorAndRoomAndSeatNumber(floor, room, seatNumber);

        if (workspace == null) {
            throw new RuntimeException("Workspace not found");
        }

        if (workspace.isBooked()) {
            throw new RuntimeException("Workspace is already booked");
        }

        EmployeeDTO employee = employeeServiceClient.getEmployeeById(employeeId);

        if (workspace.getProjectId() != null) {
            if (employee.getProjectId() == null || !workspace.getProjectId().equals(employee.getProjectId())) {
                throw new RuntimeException("You can only book workspaces assigned to your project.");
            }
        } else {
            if (employee.getProjectId() != null) {
                throw new RuntimeException("General workspaces are for employees without projects.");
            }
        }

        workspace.setBooked(true);
        workspace.setEmployeeId(employeeId);
        workspaceRepository.save(workspace);

        try {
            String qrData = "Workspace ID: " + workspace.getId() + "\nFloor: " + workspace.getFloor() + "\nRoom: " + workspace.getRoom() + "\nSeatNumber: " + workspace.getSeatNumber();
            byte[] qrCode = generateQRCode(qrData);
            sendWorkspaceAllocationEmail(employee.getEmail(), workspace, qrCode);
        } catch (Exception e) {
            throw new RuntimeException("Error generating QR code or sending email", e);
        }

        return "Workspace booked successfully!";
    }

    public String unbookWorkspace(String workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));

        workspace.setBooked(false);
        workspace.setEmployeeId(null);
        workspaceRepository.save(workspace);

        return "Workspace unbooked successfully!";
    }

    public List<Workspace> getAvailableWorkspaces(String projectId) {
        if (projectId == null) {
            return workspaceRepository.findByIsBookedAndProjectId(false, null);
        } else {
            return workspaceRepository.findByIsBookedAndProjectId(false, projectId);
        }
    }
    @Scheduled(cron = "0 0 0 * * ?") // Run at midnight every day
    public void unbookWorkspacesAfter24Hours() {
        // Get all booked workspaces
        List<Workspace> bookedWorkspaces = workspaceRepository.findByIsBooked(true);

        // Loop through each booked workspace
        for (Workspace workspace : bookedWorkspaces) {
            // If the workspace was booked more than 24 hours ago, unbook it
            if (workspace.isBookedForMoreThan24Hours()) {
                unbookWorkspace(workspace.getId()); // Unbook the workspace
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

    private void sendWorkspaceAllocationEmail(String email, Workspace workspace, byte[] qrCode) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(email);
        helper.setSubject("Workspace Allocation Confirmation");
        helper.setText("Dear User,\n\n" +
                "Your workspace \"" + workspace.getId() + "\" at \"" + workspace.getRoom() + "\" has been successfully allocated.\n" +
                "Please find the attached QR code for your records.\n\n" +
                "Best regards,\nWorkspace Management Team");

        ByteArrayDataSource qrCodeAttachment = new ByteArrayDataSource(qrCode, "image/png");
        helper.addAttachment("WorkspaceQR.png", qrCodeAttachment);

        javaMailSender.send(mimeMessage);
    }
}
