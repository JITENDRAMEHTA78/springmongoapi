package com.sixsprints.eclinic.service;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import com.sixsprints.auth.dto.AuthResponseDto;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.exception.NotAuthenticatedException;
import com.sixsprints.eclinic.ApplicationTests;
import com.sixsprints.eclinic.domain.user.Patient;
import com.sixsprints.eclinic.dto.user.PatientDto;
import com.sixsprints.eclinic.enums.Gender;
import com.sixsprints.eclinic.service.user.PatientService;
import com.sixsprints.eclinic.util.initdata.TenantData;

public class PatientServiceTest extends ApplicationTests {

  @Resource
  private PatientService patientService;

  @Test
  public void shouldLogin()
    throws EntityAlreadyExistsException, EntityInvalidException, NotAuthenticatedException, EntityNotFoundException {

    patientService
      .create(Patient.builder().gender(Gender.MALE).password("password").mobileNumber("9069169514")
        .tenantId(TenantData.DEFAULT_TENANT).build());

    AuthResponseDto<PatientDto> authResponseDTO = patientService
      .login(PatientDto.builder().mobileNumber("9069169514").password("password").build());

    Assert.assertNotNull(authResponseDTO.getToken());
    Assert.assertNotNull(authResponseDTO.getData());
    Assert.assertTrue(authResponseDTO.getData().getMobileNumber().equals("+919069169514"));
    Assert.assertTrue(authResponseDTO.getData().getGender().equals(Gender.MALE));

  }

}
