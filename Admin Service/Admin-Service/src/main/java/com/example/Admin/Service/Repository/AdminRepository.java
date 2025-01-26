package com.example.Admin.Service.Repository;

import com.example.Admin.Service.Model.Admin;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends MongoRepository<Admin, String> {
    Admin findByUsername(String username);

    Optional<Admin> findById(String id);


}
