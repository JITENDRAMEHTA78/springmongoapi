package com.sixsprints.eclinic.domain;

import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.eclinic.dto.ActionDto;
import com.sixsprints.eclinic.enums.MessageType;

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
@Document
public class ChatMessage extends AbstractMongoEntity {

  private static final long serialVersionUID = 102L;

  @Indexed
  private String chatSessionSlug;

  @Indexed
  private String fromUserSlug;

  private String text;

  private MessageType type;

  private List<ActionDto> actions;

  private String attachmentUrl;

}
