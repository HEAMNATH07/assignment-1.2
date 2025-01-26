package com.example.Vendor.services.Model;

import com.example.Vendor.services.Enum.PaymentStatus;
import com.example.Vendor.services.Enum.PaymentType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
	private String id;   
	private String vendorId;
	private Double amount;
	private String referenceNumber;

	@Temporal(TemporalType.DATE)
	@DateTimeFormat(style = "dd-MM-yyyy")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy")
	Date paymentDate;
	
	PaymentStatus paymentStatus;
	PaymentType paymentType;
}