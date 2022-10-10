package com.sixsprints.eclinic.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;
import com.sixsprints.core.controller.AbstractCrudController;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.dto.PageDto;
import com.sixsprints.core.dto.filter.ColumnFilter;
import com.sixsprints.core.dto.filter.ExactMatchColumnFilter;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.utils.RestResponse;
import com.sixsprints.core.utils.RestUtil;
import com.sixsprints.eclinic.auth.Authenticated;
import com.sixsprints.eclinic.domain.user.Doctor;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.AppointmentTypeDto;
import com.sixsprints.eclinic.dto.PaymentOptionListDto;
import com.sixsprints.eclinic.dto.user.DoctorDto;
import com.sixsprints.eclinic.enums.AccessPermission;
import com.sixsprints.eclinic.enums.EntityPermission;
import com.sixsprints.eclinic.service.user.DoctorService;
import com.sixsprints.eclinic.util.Constants;
import com.sixsprints.eclinic.util.ThreadContext;
import com.sixsprints.eclinic.util.transformer.DoctorMapper;

@RestController
@RequestMapping(value = Constants.API_PREFIX + "/doctor")
public class DoctorController extends AbstractCrudController<Doctor, DoctorDto, User> {

  private DoctorService service;
  private DoctorMapper mapper;

  public DoctorController(DoctorService service, DoctorMapper mapper) {
    super(service, mapper);
    this.service = service;
    this.mapper = mapper;
  }

  @Override
  public ResponseEntity<RestResponse<PageDto<DoctorDto>>> filter(
    @Authenticated(entity = EntityPermission.ANY, access = AccessPermission.READ) User user,
    @RequestBody FilterRequestDto filterRequestDto) {
    return super.filter(user, filterRequestDto);
  }

  @GetMapping("/speciality/{speciality}")
  public ResponseEntity<RestResponse<PageDto<DoctorDto>>> doctorBySpeciality(@PathVariable String speciality) {
    return super.filter(null, buildFilter(speciality));
  }

  private FilterRequestDto buildFilter(String speciality) {
    Map<String, ColumnFilter> filterModel = ImmutableMap.<String, ColumnFilter>of("tenantId",
      ExactMatchColumnFilter.builder().filter(ThreadContext.getCurrentTenant().getId()).build(), "speciality",
      ExactMatchColumnFilter.builder().filter(speciality).build());
    return FilterRequestDto.builder().filterModel(filterModel).page(0).size(25).build();
  }

  @GetMapping("/payment-options/{doctorSlug}")
  public ResponseEntity<RestResponse<PaymentOptionListDto>> paymentOptions(@PathVariable String doctorSlug)
    throws BaseException {
    return RestUtil
      .successResponse(PaymentOptionListDto.builder().paymentOptions(service.doctorPaymentOptions(doctorSlug)).build());
  }

  @GetMapping("/search/all")
  public ResponseEntity<RestResponse<List<DoctorDto>>> searchAllDoctor() {
    return RestUtil.successResponse(mapper.toDto(service.searchDoctors()));
  }

  @PutMapping("/update")
  public ResponseEntity<RestResponse<DoctorDto>> update(@RequestBody DoctorDto doctorDto)
    throws BaseException {
    return RestUtil.successResponse(mapper.toDto(service.updateDoctor(mapper.toDomain(doctorDto))),
      "successfully updated.");

  }

  @GetMapping("/appointment-type/{doctorSlug}")
  public ResponseEntity<RestResponse<List<AppointmentTypeDto>>> appointmentType(@PathVariable String doctorSlug)
    throws BaseException {
    return RestUtil.successResponse(service.appointmentType(doctorSlug));
  }
}