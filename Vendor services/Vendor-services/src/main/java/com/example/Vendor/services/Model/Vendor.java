package com.example.Vendor.services.Model;

import com.example.Vendor.services.Enum.VendorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Vendor { 
	@Id
	private String id; 
	private String name;
	private String contactEmail;
	private VendorType type;
	private List<Payment> payments;
	private Double totalAmount, pendingAmount;	
	private String eventId;
}