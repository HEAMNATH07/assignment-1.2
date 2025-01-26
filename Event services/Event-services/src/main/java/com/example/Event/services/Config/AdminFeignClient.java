package com.example.Event.services.Config;


import com.example.Event.services.DTO.AdminDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "admin-service")
public interface AdminFeignClient {

    @GetMapping("/admins/{id}")
    AdminDTO getAdminDetails(@PathVariable String id);
}
