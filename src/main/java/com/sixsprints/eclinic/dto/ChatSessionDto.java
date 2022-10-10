package com.sixsprints.eclinic.dto;

import java.util.Date;

import com.sixsprints.eclinic.dto.user.DoctorDto;
import com.sixsprints.eclinic.dto.user.PatientDto;
import com.sixsprints.eclinic.enums.ChatSessionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionDto {

  private String slug;

  private Date dateCreated;

  private Date dateModified;

  private String doctorSlug;

  private String patientSlug;

  private DoctorDto doctor;

  private PatientDto patient;

  private Integer totalQuestions;

  private Integer totalDays;

  private String latestMessagePreview;

  private ChatSessionStatus status;

  @Builder.Default
  private Boolean hasResolvedMessages = Boolean.FALSE;

}
