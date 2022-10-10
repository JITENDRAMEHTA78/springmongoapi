package com.sixsprints.eclinic.util.transformer;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.transformer.GenericTransformer;
import com.sixsprints.core.utils.DateUtil;
import com.sixsprints.eclinic.domain.Appointment;
import com.sixsprints.eclinic.domain.embed.ColorPalette;
import com.sixsprints.eclinic.domain.user.Patient;
import com.sixsprints.eclinic.dto.AppointmentDto;
import com.sixsprints.eclinic.dto.StyledComponent;
import com.sixsprints.eclinic.enums.AppointmentStatus;
import com.sixsprints.eclinic.enums.ParentPaymentMode;
import com.sixsprints.eclinic.service.user.DoctorService;
import com.sixsprints.eclinic.service.user.PatientService;

@Mapper(componentModel = "spring")
public abstract class AppointmentMapper extends GenericTransformer<Appointment, AppointmentDto> {

  private static final StyledComponent NEW = StyledComponent.builder().label("New")
    .color(new ColorPalette.Color("", "#EAAD52", "", "#FFFFFF")).build();

  private static final StyledComponent OLD = StyledComponent.builder().label("Old")
    .color(new ColorPalette.Color("", "#800080", "", "#FFFFFF")).build();

  private static final StyledComponent LATE = StyledComponent.builder().label("Late")
    .color(new ColorPalette.Color("", "#d02114", "", "#FFFFFF")).build();

  private static final StyledComponent ON_TIME = StyledComponent.builder().label("On Time")
    .color(new ColorPalette.Color("", "#2884ff", "", "#FFFFFF")).build();

  private static final StyledComponent CALLED = StyledComponent.builder().label("Called")
    .color(new ColorPalette.Color("", "#505f79", "", "#FFFFFF")).build();

  private static final StyledComponent DETAILED_CONSULT = StyledComponent.builder().label("Detailed Consult")
    .color(new ColorPalette.Color("", "#c22736", "", "#FFFFFF")).build();

  @Resource
  private DoctorService doctorService;

  @Resource
  private DoctorMapper doctorMapper;

  @Resource
  private PatientService patientService;

  @Resource
  private PatientMapper patientMapper;

  @Resource
  private DateUtil dateUtil;

  @Override
  public abstract Appointment toDomain(AppointmentDto dto);

  @Override
  public abstract AppointmentDto toDto(Appointment domain);

  @AfterMapping
  protected void afterToDto(Appointment domain, @MappingTarget AppointmentDto.AppointmentDtoBuilder builder) {
    addLabels(domain, builder);
    try {
      builder.doctor(doctorMapper.toDto(doctorService.findBySlug(domain.getDoctorSlug())));
    } catch (EntityNotFoundException e) {
    }

    try {
      Patient patient = patientService.findBySlug(domain.getPatientSlug());
      builder.patient(patientMapper.toDto(patient));
      if (patient.getIsOld() == null || !patient.getIsOld()) {
        builder.label(NEW);
      } else {
        builder.label(OLD);
      }
    } catch (EntityNotFoundException e) {
    }

    if (StringUtils.isBlank(domain.getMode()) && domain.getPaymentMode() != null) {
      builder.mode(domain.getPaymentMode().getMode().getLabel());
    } else if (StringUtils.isBlank(domain.getMode())) {
      builder.mode(ParentPaymentMode.CASH.getLabel());
    }
  }

  private void addLabels(Appointment domain, AppointmentDto.AppointmentDtoBuilder builder) {
    if (domain.getStatus() != null) {
      builder.label(StyledComponent.builder()
        .label(domain.getStatus().getLabel())
        .color(new ColorPalette.Color("", getColor(domain.getStatus()), "", "#FFFFFF")).build());
    }
    if (domain.getCheckInTime() != null) {
      if (dateUtil.now().isBefore(domain.getDateTime().getTime())
        || domain.getCheckInTime().minusMinutes(5).isBefore(domain.getTime())) {
        builder.label(ON_TIME);
      } else {
        builder.label(LATE);
      }
    }

    if (domain.getCalled() != null && domain.getCalled()) {
      builder.label(CALLED);
    }
    if(domain.getIsDetailedConsult() != null && domain.getIsDetailedConsult()) {
      builder.label(DETAILED_CONSULT);
    }

  }

  private String getColor(AppointmentStatus status) {
    if (AppointmentStatus.CHECKED_IN.equals(status)) {
      return "#73B504";
    } else if (AppointmentStatus.TEST_NEEDED.equals(status)) {
      return "#B52404";
    } else if (AppointmentStatus.REPORT_READY.equals(status)) {
      return "#04A5B5";
    }
    return "#797979";
  }

}
