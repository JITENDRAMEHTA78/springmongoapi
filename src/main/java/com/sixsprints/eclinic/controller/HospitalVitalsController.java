package com.sixsprints.eclinic.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sixsprints.core.controller.AbstractCrudController;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.utils.RestResponse;
import com.sixsprints.core.utils.RestUtil;
import com.sixsprints.eclinic.auth.Authenticated;
import com.sixsprints.eclinic.domain.HospitalVitals;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.HospitalVitalsDto;
import com.sixsprints.eclinic.enums.MasterType;
import com.sixsprints.eclinic.service.HospitalVitalsService;
import com.sixsprints.eclinic.util.Constants;
import com.sixsprints.eclinic.util.transformer.HospitalVitalsMapper;

@RestController
@RequestMapping(value = Constants.API_PREFIX + "/hospital-vitals")
public class HospitalVitalsController extends AbstractCrudController<HospitalVitals, HospitalVitalsDto, User> {

  private HospitalVitalsMapper mapper;

  private HospitalVitalsService service;

  public HospitalVitalsController(HospitalVitalsService service, HospitalVitalsMapper mapper) {
    super(service, mapper);
    this.service = service;
    this.mapper = mapper;
  }

  @Override
  public ResponseEntity<RestResponse<HospitalVitalsDto>> add(@Authenticated User user,
    @RequestBody @Valid HospitalVitalsDto hospitalVitalsDto)
    throws BaseException {
    hospitalVitalsDto.setType(MasterType.VITAL);
    return masterData(hospitalVitalsDto);
  }

  @PostMapping("/co-morbidities")
  public ResponseEntity<RestResponse<HospitalVitalsDto>> addCoMorbities(@Authenticated User user,
    @RequestBody @Valid HospitalVitalsDto hospitalVitalsDto)
    throws BaseException {
    hospitalVitalsDto.setType(MasterType.CO_MORBIDITY);
    return masterData(hospitalVitalsDto);
  }

  private ResponseEntity<RestResponse<HospitalVitalsDto>> masterData(HospitalVitalsDto hospitalVitalsDto)
    throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException {
    return RestUtil.successResponse(mapper.toDto(service.addVitals(mapper.toDomain(hospitalVitalsDto))));
  }

}
