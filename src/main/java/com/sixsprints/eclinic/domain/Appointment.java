package com.sixsprints.eclinic.domain;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.eclinic.dto.PrescriptionDto;
import com.sixsprints.eclinic.enums.AppointmentStatus;
import com.sixsprints.eclinic.enums.AppointmentType;
import com.sixsprints.eclinic.enums.PaymentMode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document
public class Appointment extends AbstractMongoEntity implements Comparable<Appointment> {

  private static final long serialVersionUID = -7248909447294060605L;

  private String patientSlug;

  private String doctorSlug;

  private Date date;

  private Date nextAppointmentDate;

  private LocalTime time;

  private Date dateTime;

  private AppointmentStatus status;

  private BigDecimal fees;

  private BigDecimal discount;

  private byte[] qrCode;

  private PaymentMode paymentMode;

  private PrescriptionDto prescription;
  
  private List<PrescriptionDto> prevPrescriptions;
  
  private String appointmentPdf;

  private String receiptPdf;
  
  private String letterHeadPdf;

  private LocalTime checkInTime;

  private String creatorName;

  private String creatorRole;

  private Boolean called;

  private Boolean isDetailedConsult;

  private Integer queue;

  private Boolean notificationSent;

  private String remarks;

  @Builder.Default
  private AppointmentType type = AppointmentType.GENERAL;

  @Builder.Default
  private Boolean isFreeChatActive = Boolean.FALSE;

  private String mode;

  private Boolean isFriends;

  private Boolean isFamily;
  
  private String lastVisited;

  @Override
  public int compareTo(Appointment o) {
    int dateCompare = date.compareTo(o.date);
    return dateCompare == 0 ? time.compareTo(o.time) : dateCompare;
  }

}
