package com.sixsprints.eclinic.controller.auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sixsprints.eclinic.domain.user.Patient;
import com.sixsprints.eclinic.dto.AuthDto;
import com.sixsprints.eclinic.dto.ResetPasswordDto;
import com.sixsprints.eclinic.dto.user.PatientDto;
import com.sixsprints.eclinic.service.user.PatientService;
import com.sixsprints.eclinic.util.Constants;
import com.sixsprints.eclinic.util.transformer.PatientMapper;

@RestController
@RequestMapping(Constants.API_PREFIX + "/auth/patient")
public class PatientAuthController extends AbstractUserAuthController<Patient, PatientDto, AuthDto, ResetPasswordDto> {

  public PatientAuthController(PatientService service, PatientMapper mapper) {
    super(service, mapper);
  }

}