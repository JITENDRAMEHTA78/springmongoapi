package com.sixsprints.eclinic.service.user;

import java.util.List;

import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.eclinic.domain.user.Patient;
import com.sixsprints.eclinic.dto.user.PatientDto;
import com.sixsprints.eclinic.service.user.abs.GenericUserService;

public interface PatientService extends GenericUserService<Patient, PatientDto> {

  Patient generateUhid(String patientSlug) throws EntityNotFoundException;

  List<Patient> searchPatient();

}