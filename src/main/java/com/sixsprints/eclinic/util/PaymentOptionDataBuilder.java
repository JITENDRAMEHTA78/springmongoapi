package com.sixsprints.eclinic.util;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sixsprints.eclinic.dto.AppointmentTypeDto;
import com.sixsprints.eclinic.dto.AppointmentTypeDto.AppointmentTypeDtoBuilder;
import com.sixsprints.eclinic.dto.PaymentOptionDto;
import com.sixsprints.eclinic.enums.AppointmentType;
import com.sixsprints.eclinic.enums.PaymentMode;

@Component
public class PaymentOptionDataBuilder {

  public List<PaymentOptionDto> paymentOptions() {

    PaymentOptionDto p1 = PaymentOptionDto.builder().paymentMode(PaymentMode.PAYTM).payTo("9868378312")
      .description("PayTm")
      .qrCode("https://storage.googleapis.com/eclinic_assets/data/doctor/ramantanwar/payment/paytm.jpeg").build();

    PaymentOptionDto p2 = PaymentOptionDto.builder().paymentMode(PaymentMode.BHIM_UPI).payTo("urocentre@upi")
      .description("BHIM UPI")
      .qrCode("https://storage.googleapis.com/eclinic_assets/data/doctor/ramantanwar/payment/upi.jpeg").build();

    return Arrays.asList(p1, p2);

  }
  
  public List<PaymentOptionDto> paymentOptions2() {

    PaymentOptionDto p1 = PaymentOptionDto.builder().paymentMode(PaymentMode.PAYTM).payTo("9053267890")
      .description("PayTm")
      .qrCode("https://storage.googleapis.com/eclinic_assets/data/doctor/ramantanwar/payment/paytm.jpeg").build();

    PaymentOptionDto p2 = PaymentOptionDto.builder().paymentMode(PaymentMode.BHIM_UPI).payTo("9053267890@upi")
      .description("BHIM UPI")
      .qrCode("https://storage.googleapis.com/eclinic_assets/data/doctor/ramantanwar/payment/upi.jpeg").build();
    
    PaymentOptionDto p3 = PaymentOptionDto.builder().paymentMode(PaymentMode.GOOGLEPAY).payTo("dr.milindtanwar@okaxis")
      .description("Google Pay UPI")
      .qrCode("https://storage.googleapis.com/eclinic_assets/data/doctor/ramantanwar/payment/upi.jpeg").build();


    return Arrays.asList(p1, p2, p3);

  }
  
  public AppointmentTypeDto appointmentTypeOptions() {
	  
	 AppointmentTypeDto appointmentType = AppointmentTypeDto.builder().label(AppointmentType.VIDEO).disclaimer("").active(false).build();
	 return appointmentType;
  }
  
}
