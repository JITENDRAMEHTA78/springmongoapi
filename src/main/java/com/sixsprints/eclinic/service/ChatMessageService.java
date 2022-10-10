package com.sixsprints.eclinic.service;

import org.springframework.data.domain.Page;

import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.service.GenericCrudService;
import com.sixsprints.eclinic.domain.ChatMessage;
import com.sixsprints.eclinic.dto.ChatMessageDto;

public interface ChatMessageService extends GenericCrudService<ChatMessage> {

  ChatMessage send(ChatMessageDto dto, String sessionSlug)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException;

  Page<ChatMessage> findBySessionSlug(String sessionSlug, int page, int size);

}