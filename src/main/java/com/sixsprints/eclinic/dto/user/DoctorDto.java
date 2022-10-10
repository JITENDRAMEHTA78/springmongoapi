package com.sixsprints.eclinic.dto.user;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.sixsprints.eclinic.dto.AppointmentTypeDto;
import com.sixsprints.eclinic.dto.AvailSummaryDto;
import com.sixsprints.eclinic.dto.DoctorConfig;
import com.sixsprints.eclinic.dto.PaymentOptionDto;
import com.sixsprints.eclinic.dto.SlotConfigDto;
import com.sixsprints.eclinic.dto.SocialMediaDto;
import com.sixsprints.eclinic.enums.AppointmentType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DoctorDto extends UserDto {

  private List<String> branchSlugList;

  private String contactNumber;

  private String speciality;

  private BigDecimal fees;

  private String roomNumber;

  private DoctorConfig config;

  private Map<AppointmentType, Map<DayOfWeek, List<LocalTime>>> slots;

  private Map<AppointmentType, SlotConfigDto> availabilitySummary;

  private String profilePic;

  private String info;

  private String shortTitle;

  private String bio;

  private String registrationNo;

  private String signature;

  private String websiteUrl;

  private List<SocialMediaDto> socialMedia;

  private List<PaymentOptionDto> paymentOptions;

  private List<AvailSummaryDto> availSummary;

  private String designation;

  private List<AppointmentTypeDto> appointmentType;

}
