package com.sixsprints.eclinic.dto;

import java.util.List;

import com.sixsprints.eclinic.enums.MessageType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

  private String slug;

  private String dateCreated;

  private String chatSessionSlug;

  private String fromUserSlug;

  private String text;

  private MessageType type;

  @Singular
  private List<ActionDto> actions;

  private Long sequence;

  private String attachmentUrl;

}