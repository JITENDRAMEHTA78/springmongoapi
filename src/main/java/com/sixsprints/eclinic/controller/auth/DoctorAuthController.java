package com.sixsprints.eclinic.controller.auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sixsprints.eclinic.domain.user.Doctor;
import com.sixsprints.eclinic.dto.AuthDto;
import com.sixsprints.eclinic.dto.ResetPasswordDto;
import com.sixsprints.eclinic.dto.user.DoctorDto;
import com.sixsprints.eclinic.service.user.DoctorService;
import com.sixsprints.eclinic.util.Constants;
import com.sixsprints.eclinic.util.transformer.DoctorMapper;

@RestController
@RequestMapping(Constants.API_PREFIX + "/auth/doctor")
public class DoctorAuthController extends AbstractUserAuthController<Doctor, DoctorDto, AuthDto, ResetPasswordDto> {

  public DoctorAuthController(DoctorService service, DoctorMapper mapper) {
    super(service, mapper);
  }

}