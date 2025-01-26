package com.example.WorkSpace.Service.Controller;

import com.example.WorkSpace.Service.Model.Workspace;
import com.example.WorkSpace.Service.Service.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    // Create a new workspace
    @PostMapping
    public ResponseEntity<Workspace> createWorkspace(@RequestBody Workspace workspace) {
        Workspace createdWorkspace = workspaceService.createWorkspace(workspace);
        return ResponseEntity.ok(createdWorkspace);
    }

    // Retrieve a workspace by ID
    @GetMapping("/{id}")
    public ResponseEntity<Workspace> getWorkspaceById(@PathVariable String id) {
        Workspace workspace = workspaceService.getWorkspaceById(id);
        return ResponseEntity.ok(workspace);
    }

    // Retrieve all workspaces
    @GetMapping
    public ResponseEntity<List<Workspace>> getAllWorkspaces() {
        List<Workspace> workspaces = workspaceService.getAllWorkspaces();
        return ResponseEntity.ok(workspaces);
    }

    // Update a workspace
    @PutMapping("/{id}")
    public ResponseEntity<Workspace> updateWorkspace(
            @PathVariable String id,
            @RequestBody Workspace workspaceDetails) {
        Workspace updatedWorkspace = workspaceService.updateWorkspace(id, workspaceDetails);
        return ResponseEntity.ok(updatedWorkspace);
    }

    // Delete a workspace
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWorkspace(@PathVariable String id) {
        workspaceService.deleteWorkspace(id);
        return ResponseEntity.ok("Workspace deleted successfully.");
    }

    // Book a workspace
    @PostMapping("/book")
    public ResponseEntity<String> bookWorkspace(
            @RequestParam String floor,
            @RequestParam String room,
            @RequestParam String seatNumber,
            @RequestParam String employeeId) {
        String response = workspaceService.bookWorkspace(floor, room, seatNumber, employeeId);
        return ResponseEntity.ok(response);
    }

    // Unbook a workspace
    @PostMapping("/unbook/{workspaceId}")
    public ResponseEntity<String> unbookWorkspace(@PathVariable String workspaceId) {
        String response = workspaceService.unbookWorkspace(workspaceId);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/unbookAll")
    public String unbookAllWorkspaces() {
        workspaceService.unbookWorkspacesAfter24Hours();
        return "Unbooking process started.";
    }

    // Get available workspaces
    @GetMapping("/available")
    public ResponseEntity<List<Workspace>> getAvailableWorkspaces(@RequestParam(required = false) String projectId) {
        List<Workspace> availableWorkspaces = workspaceService.getAvailableWorkspaces(projectId);
        return ResponseEntity.ok(availableWorkspaces);
    }
}
