package com.sixsprints.eclinic.service.user;

import java.util.List;

import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.eclinic.domain.user.Doctor;
import com.sixsprints.eclinic.dto.AppointmentTypeDto;
import com.sixsprints.eclinic.dto.PaymentOptionDto;
import com.sixsprints.eclinic.dto.user.DoctorDto;
import com.sixsprints.eclinic.service.user.abs.GenericUserService;

public interface DoctorService extends GenericUserService<Doctor, DoctorDto> {

  List<PaymentOptionDto> doctorPaymentOptions(String doctorSlug) throws EntityNotFoundException, BaseException;

  Doctor findByWebsiteKey(String websiteKey);

  List<Doctor> searchDoctors();

  List<AppointmentTypeDto> appointmentType(String doctorSlug) throws EntityNotFoundException, BaseException;

  Doctor updateDoctor(Doctor domain) throws EntityNotFoundException, BaseException;
}
