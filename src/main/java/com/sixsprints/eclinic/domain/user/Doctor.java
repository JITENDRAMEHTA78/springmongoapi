package com.sixsprints.eclinic.domain.user;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.sixsprints.core.utils.Subclass;
import com.sixsprints.eclinic.dto.AppointmentTypeDto;
import com.sixsprints.eclinic.dto.AvailSummaryDto;
import com.sixsprints.eclinic.dto.DatePair;
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
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "user")
@Subclass
public class Doctor extends User {

  private static final long serialVersionUID = -235307991310039397L;

  private List<String> branchSlugList;

  private String contactNumber;

  private String speciality;

  private Map<AppointmentType, Map<DayOfWeek, List<LocalTime>>> slots;

  private Map<AppointmentType, SlotConfigDto> availabilitySummary;

  private String roomNumber;

  private String profilePic;

  private String shortTitle;

  private String bio;

  private String registrationNo;

  private String signature;

  private List<SocialMediaDto> socialMedia;

  private List<PaymentOptionDto> paymentOptions;

  @Indexed(unique = true, sparse = true)
  private String websiteUrl;

  @Indexed(unique = true, sparse = true)
  private String websiteKey;

  private String designation;

  private List<AvailSummaryDto> availSummary;

  private List<AppointmentTypeDto> appointmentType;

  private List<DatePair> blockedSlots;

}
