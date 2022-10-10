package com.sixsprints.eclinic.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.eclinic.enums.ChatSessionStatus;

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
@CompoundIndex(unique = true, name = "doctorSlug_patientSlug", def = "{'doctorSlug': 1, 'patientSlug': 1}")
public class ChatSession extends AbstractMongoEntity {

  private static final long serialVersionUID = 101L;

  @Indexed
  private String doctorSlug;

  @Indexed
  private String patientSlug;

  private Integer totalQuestions;

  private Integer totalDays;

  private String latestMessagePreview;

  private ChatSessionStatus status;

  @Builder.Default
  private Boolean hasResolvedMessages = Boolean.FALSE;

}
