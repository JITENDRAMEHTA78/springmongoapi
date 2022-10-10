package com.sixsprints.eclinic.controller;

import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sixsprints.core.controller.AbstractCrudController;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.dto.filter.ColumnFilter;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.utils.RestResponse;
import com.sixsprints.core.utils.RestUtil;
import com.sixsprints.eclinic.auth.Authenticated;
import com.sixsprints.eclinic.domain.Appointment;
import com.sixsprints.eclinic.domain.Order;
import com.sixsprints.eclinic.domain.Report;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.ReportDto;
import com.sixsprints.eclinic.enums.AppointmentStatus;
import com.sixsprints.eclinic.enums.ProductType;
import com.sixsprints.eclinic.service.AppointmentService;
import com.sixsprints.eclinic.service.OrderService;
import com.sixsprints.eclinic.service.ReportService;
import com.sixsprints.eclinic.service.user.DoctorService;
import com.sixsprints.eclinic.util.Constants;
import com.sixsprints.eclinic.util.transformer.ReportMapper;

@RestController
@RequestMapping(value = Constants.API_PREFIX + "/report")
public class ReportController extends AbstractCrudController<Report, ReportDto, User> {

  private AppointmentService appointmentService;
  private OrderService orderService;
  private DoctorService doctorService;
  
  private ReportService service;

  private ReportMapper mapper;

  public ReportController(ReportService service, ReportMapper mapper, AppointmentService appointmentService, OrderService orderService, DoctorService doctorService) {
    super(service, mapper);
    this.service = service;
    this.mapper = mapper;
    this.appointmentService = appointmentService;
    this.orderService = orderService;
    this.doctorService = doctorService;
  }

  // @Valid
  @Override
  public ResponseEntity<RestResponse<ReportDto>> add(@Authenticated User user, @RequestBody ReportDto dto)
    throws BaseException {
    if (StringUtils.isEmpty(dto.getPatientSlug())) {
      dto.setPatientSlug(user.getSlug());
    }
    
    if (dto.getAction().equals("REVIEW") || dto.getAction().equals("CHAT")) {
    	Appointment appointment = appointmentService.findBySlug(dto.getAppointmentSlug());
    	appointment.setStatus(AppointmentStatus.REPORT_READY);
    	if (dto.getAction().equals("CHAT")) {
          service.handleChatAction(appointment, user);
    	}
      appointmentService.patchUpdate(appointment.getId(), appointment, "status");
    }

    return RestUtil.successResponse(mapper.toDto(service.create(mapper.toDomain(dto))), "Report successfully created.");
  }

  @PostMapping("/appointment/{appointmentSlug}")
  public ResponseEntity<RestResponse<ReportDto>> appointmentReport(@RequestBody ReportDto reportDto,
    @PathVariable String appointmentSlug)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException {
    return RestUtil.successResponse(
      mapper.toDto(service.appointmentReport(mapper.toDomain(reportDto), appointmentSlug)),
      "Report successfully created");
  }

  @PutMapping("/update")
  public ResponseEntity<RestResponse<ReportDto>> update(@RequestBody ReportDto reportDto)
    throws EntityNotFoundException, EntityAlreadyExistsException {
    return RestUtil.successResponse(mapper.toDto(service.update(reportDto.getId(), mapper.toDomain(reportDto))),
      "success");
  }
}
