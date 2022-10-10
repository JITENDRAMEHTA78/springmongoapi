package com.sixsprints.eclinic.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sixsprints.core.dto.PageDto;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.eclinic.dto.ChatMessageDto;
import com.sixsprints.eclinic.service.ChatMessageService;
import com.sixsprints.eclinic.util.transformer.ChatMessageMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ChatMessageController {

  @Autowired
  private ChatMessageService service;

  @Autowired
  private ChatMessageMapper mapper;

  @MessageMapping("/chat/{sessionSlug}")
  public ChatMessageDto send(ChatMessageDto message, @DestinationVariable String sessionSlug)
    throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException {
    log.info(message.toString());
    return mapper.toDto(service.send(message, sessionSlug));
  }

  @SubscribeMapping("/topic/messages/{sessionSlug}")
  public PageDto<ChatMessageDto> rateSubscription(@DestinationVariable String sessionSlug) {
    log.info("Subscribing to topic messages for Session {}", sessionSlug);
    PageDto<ChatMessageDto> chats = mapper.pageEntityToPageDtoDto(service.findBySessionSlug(sessionSlug, 0, 30));
    List<ChatMessageDto> content = chats.getContent();
    Collections.sort(content, new Comparator<ChatMessageDto>() {
      @Override
      public int compare(ChatMessageDto o1, ChatMessageDto o2) {
        return (int) (o1.getSequence() - o2.getSequence());
      }
    });
    return chats;
  }

  @RequestMapping("/api/v1/all/messages/{sessionSlug}")
  public PageDto<ChatMessageDto> getMessages(@PathVariable String sessionSlug, @RequestParam Integer page) {
    PageDto<ChatMessageDto> chats = mapper.pageEntityToPageDtoDto(service.findBySessionSlug(sessionSlug, page, 30));
    List<ChatMessageDto> content = chats.getContent();
    Collections.sort(content, new Comparator<ChatMessageDto>() {
      @Override
      public int compare(ChatMessageDto o1, ChatMessageDto o2) {
        return (int) (o1.getSequence() - o2.getSequence());
      }
    });
    return chats;
  }

}
