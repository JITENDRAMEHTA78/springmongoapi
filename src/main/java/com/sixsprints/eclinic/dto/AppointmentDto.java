package com.sixsprints.eclinic.dto;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.sixsprints.eclinic.dto.user.DoctorDto;
import com.sixsprints.eclinic.dto.user.PatientDto;
import com.sixsprints.eclinic.enums.AppointmentStatus;
import com.sixsprints.eclinic.enums.AppointmentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {

  @NotBlank
  private String patientSlug;

  @NotBlank
  private String doctorSlug;

  @NotNull
  private Date date;

  private Date dateTime;
  private Date nextAppointmentDate;

  @NotNull
  private LocalTime time;

  private AppointmentStatus status;

  private byte[] qrCode;

  private PatientDto patient;

  private DoctorDto doctor;

  private String slug;

  private String id;

  private String creatorName;

  private String creatorRole;

  private String mode;

  private BigDecimal fees;

  private BigDecimal discount;

  private PrescriptionDto prescription;
  
  private List<PrescriptionDto> prevPrescriptions;

  private String appointmentPdf;

  private String receiptPdf;
  
  private String letterHeadPdf;

  private LocalTime checkInTime;

  @Singular
  private List<StyledComponent> labels;

  private Boolean called;

  private Integer queue;

  private String remarks;

  private AppointmentType type;

  @Builder.Default
  private Boolean isFriends = false;
  @Builder.Default
  private Boolean isFamily = false;

  @Builder.Default
  private Boolean isFreeChatActive = Boolean.FALSE;

}
