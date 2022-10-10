package com.sixsprints.eclinic.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sixsprints.core.controller.AbstractCrudController;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.dto.PageDto;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.utils.RestResponse;
import com.sixsprints.core.utils.RestUtil;
import com.sixsprints.eclinic.auth.Authenticated;
import com.sixsprints.eclinic.domain.user.Patient;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.user.PatientDto;
import com.sixsprints.eclinic.enums.AccessPermission;
import com.sixsprints.eclinic.enums.EntityPermission;
import com.sixsprints.eclinic.service.user.PatientService;
import com.sixsprints.eclinic.util.Constants;
import com.sixsprints.eclinic.util.transformer.PatientMapper;

@RestController
@RequestMapping(value = Constants.API_PREFIX + "/patient")
public class PatientController extends AbstractCrudController<Patient, PatientDto, User> {

  public PatientController(PatientService service, PatientMapper mapper) {
    super(service, mapper);
    this.service = service;
    this.mapper = mapper;
  }

  private PatientService service;

  private PatientMapper mapper;

  @Override
  public ResponseEntity<RestResponse<PageDto<PatientDto>>> filter(
    @Authenticated(entity = EntityPermission.ANY, access = AccessPermission.READ) User user,
    @RequestBody FilterRequestDto filterRequestDto) {
    return super.filter(user, filterRequestDto);
  }

  @PutMapping("/profile")
  public ResponseEntity<?> update(
    @Authenticated(entity = EntityPermission.PATIENT, access = AccessPermission.UPDATE) User user,
    @RequestBody @Valid PatientDto dto) throws BaseException {
    Patient domain = mapper.toDomain(dto);
    return RestUtil.successResponse(service.update(dto.getId(), domain));
  }
  @GetMapping("/search/all")
  public ResponseEntity<RestResponse<List<PatientDto>>> searchAllPatient() {
    return RestUtil.successResponse(mapper.toDto(service.searchPatient()));
  }
}