package com.sixsprints.eclinic.util.transformer;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.sixsprints.core.transformer.GenericTransformer;
import com.sixsprints.core.utils.DateUtil;
import com.sixsprints.eclinic.domain.ChatMessage;
import com.sixsprints.eclinic.dto.ChatMessageDto;

@Mapper(componentModel = "spring")
public abstract class ChatMessageMapper extends GenericTransformer<ChatMessage, ChatMessageDto> {

  @Override
  public abstract ChatMessage toDomain(ChatMessageDto dto);

  @Override
  public abstract ChatMessageDto toDto(ChatMessage domain);

  @Autowired
  private DateUtil dateUtil;

  @AfterMapping
  protected void afterToDto(ChatMessage domain, @MappingTarget ChatMessageDto.ChatMessageDtoBuilder builder) {

    builder.dateCreated(dateUtil.dateToStringWithFormat(domain.getDateCreated(), "dd-MM-YYYY HH:mm a"));

  }

}
