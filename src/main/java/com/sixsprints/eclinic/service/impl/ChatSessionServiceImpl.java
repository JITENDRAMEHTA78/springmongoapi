package com.sixsprints.eclinic.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.service.AbstractCrudService;
import com.sixsprints.eclinic.domain.ChatPackage;
import com.sixsprints.eclinic.domain.ChatSession;
import com.sixsprints.eclinic.dto.ActionDto;
import com.sixsprints.eclinic.dto.ChatMessageDto;
import com.sixsprints.eclinic.dto.ChatSessionDto;
import com.sixsprints.eclinic.enums.ActionType;
import com.sixsprints.eclinic.enums.ChatSessionStatus;
import com.sixsprints.eclinic.enums.MessageType;
import com.sixsprints.eclinic.repository.ChatSessionRepository;
import com.sixsprints.eclinic.service.ChatMessageService;
import com.sixsprints.eclinic.service.ChatSessionService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatSessionServiceImpl extends AbstractCrudService<ChatSession> implements ChatSessionService {

  @Autowired
  private ChatSessionRepository chatSessionRepository;

  @Autowired
  @Lazy
  private ChatMessageService chatMessageService;

  @Override
  protected ChatSessionRepository repository() {
    return chatSessionRepository;
  }

  @Override
  protected MetaData<ChatSession> metaData() {
    return MetaData.<ChatSession>builder()
      .classType(ChatSession.class).dtoClassType(ChatSessionDto.class)
      .build();
  }

  @Override
  protected ChatSession findDuplicate(ChatSession entity) {
    return chatSessionRepository.findByDoctorSlugAndPatientSlug(entity.getDoctorSlug(), entity.getPatientSlug());
  }

  @Override
  protected void preCreate(ChatSession entity) {
    super.preCreate(entity);
    entity.setStatus(ChatSessionStatus.ACTIVE);
  }

  @Override
  public Page<ChatSession> findMySessions(String slug, int page, int size) {
    return chatSessionRepository.findByDoctorSlugOrPatientSlug(slug, slug,
      PageRequest.of(page, size, Sort.by(Order.asc("status"), Order.desc("dateModified"))));
  }

  @Override
  public ChatSession topUpOnPurchase(ChatPackage chatPackage, String patientSlug) {
    try {
      boolean isNew = false;
      ChatSession existing = chatSessionRepository.findByDoctorSlugAndPatientSlug(chatPackage.getDoctorSlug(),
        patientSlug);
      if (existing == null) {
        existing = create(ChatSession.builder()
          .doctorSlug(chatPackage.getDoctorSlug())
          .patientSlug(patientSlug)
          .totalDays(0)
          .totalQuestions(0)
          .build());
        isNew = true;
      }
      existing
        .setTotalQuestions(Math.max(existing.getTotalQuestions(), 0) + Math.max(chatPackage.getTotalQuestions(), 0));
      existing
        .setTotalDays(Math.max(existing.getTotalDays(), 0) + Math.max(chatPackage.getTotalDays(), 0));
      existing.setStatus(ChatSessionStatus.ACTIVE);
      existing = save(existing);

      if (!isNew) {
        chatMessageService.send(systemMessageForTopup(existing.getSlug(), existing.getTotalQuestions()),
          existing.getSlug());
      }

      return existing;

    } catch (Exception ex) {
      log.error("Unable to topup for Chat Package {}, for patient {} ", chatPackage, patientSlug);
      log.error(ex.getMessage(), ex);
    }
    return null;
  }

  @Override
  public ChatSession markResolve(String sessionSlug)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException {
    ChatSession session = findBySlug(sessionSlug);
    Integer questions = session.getTotalQuestions();
    if (questions != null && questions > 0) {
      session.setTotalQuestions(questions - 1);
    }

    if (questions - 1 <= 0) {
      session.setStatus(ChatSessionStatus.INACTIVE);
      chatMessageService.send(systemMessage(sessionSlug), sessionSlug);
    } else {
      chatMessageService.send(systemMessageForOneResolve(sessionSlug, questions - 1), sessionSlug);
    }
    session.setHasResolvedMessages(Boolean.FALSE);
    return save(session);
  }

  @Override
  public ChatSession findByDoctorSlugAndPatientSlug(String doctorSlug, String patientSlug) {
    return chatSessionRepository.findByDoctorSlugAndPatientSlug(doctorSlug, patientSlug);
  }

  private ChatMessageDto systemMessage(String sessionSlug) {
    return ChatMessageDto.builder()
      .chatSessionSlug(sessionSlug)
      .fromUserSlug("SYSTEM")
      .type(MessageType.AUTOMATED)
      .text("You have exhausted all your questions. Choose below to renew your plan to chat again.")
      .action(ActionDto.builder()
        .name("Renew now")
        .actionUrl("/home/chat/subscription")
        .type(ActionType.REDIRECT)
        .build())
      .action(ActionDto.builder()
        .name("I'll do it later")
        .actionUrl("/home")
        .type(ActionType.REDIRECT)
        .build())
      .build();
  }

  private ChatMessageDto systemMessageForOneResolve(String sessionSlug, int questions) {
    return ChatMessageDto.builder()
      .chatSessionSlug(sessionSlug)
      .fromUserSlug("SYSTEM")
      .type(MessageType.AUTOMATED)
      .text("One of the questions have been marked as resolved. Questions remaining: " + questions)
      .build();
  }

  private ChatMessageDto systemMessageForTopup(String sessionSlug, int questions) {
    return ChatMessageDto.builder()
      .chatSessionSlug(sessionSlug)
      .fromUserSlug("SYSTEM")
      .type(MessageType.AUTOMATED)
      .text("Chat package top-up successful. Questions remaining: " + questions)
      .build();
  }

  @Override
  public ChatSession markUnResolve(String sessionSlug) throws EntityNotFoundException {
    ChatSession session = findBySlug(sessionSlug);
    Integer questions = session.getTotalQuestions();
    if (questions == null || questions < 1) {
      questions = 0;
      session.setStatus(ChatSessionStatus.ACTIVE);
    }
    session.setTotalQuestions(questions + 1);
    return save(session);
  }

}
