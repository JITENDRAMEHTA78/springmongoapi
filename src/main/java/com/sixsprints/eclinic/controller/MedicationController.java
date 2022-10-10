package com.sixsprints.eclinic.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sixsprints.core.controller.AbstractCrudController;
import com.sixsprints.core.utils.RestResponse;
import com.sixsprints.core.utils.RestUtil;
import com.sixsprints.eclinic.domain.Medication;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.MedicationDto;
import com.sixsprints.eclinic.service.MedicationService;
import com.sixsprints.eclinic.util.Constants;
import com.sixsprints.eclinic.util.transformer.MedicationMapper;

@RestController
@RequestMapping(value = Constants.API_PREFIX + "/medication")
public class MedicationController extends AbstractCrudController<Medication, MedicationDto, User> {

  private MedicationService service;
  private MedicationMapper mapper;

  public MedicationController(MedicationService service, MedicationMapper mapper) {
    super(service, mapper);
    this.service = service;
    this.mapper = mapper;
  }

  @GetMapping("search/all")
  public ResponseEntity<RestResponse<List<MedicationDto>>> searchAll() {
    return RestUtil.successResponse(mapper.toDto(service.findAll()),"success");
  }
}
