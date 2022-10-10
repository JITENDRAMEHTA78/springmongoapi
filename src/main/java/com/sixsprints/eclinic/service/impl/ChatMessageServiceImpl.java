package com.sixsprints.eclinic.service.impl;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.service.AbstractCrudService;
import com.sixsprints.core.utils.DateUtil;
import com.sixsprints.eclinic.domain.ChatMessage;
import com.sixsprints.eclinic.domain.ChatSession;
import com.sixsprints.eclinic.domain.user.Doctor;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.ChatMessageDto;
import com.sixsprints.eclinic.enums.MessageType;
import com.sixsprints.eclinic.enums.UserRole;
import com.sixsprints.eclinic.repository.ChatMessageRepository;
import com.sixsprints.eclinic.service.ChatMessageService;
import com.sixsprints.eclinic.service.ChatSessionService;
import com.sixsprints.eclinic.service.user.DoctorService;
import com.sixsprints.eclinic.service.user.UserService;
import com.sixsprints.eclinic.util.transformer.ChatMessageMapper;
import com.sixsprints.notification.dto.MessageDto;
import com.sixsprints.notification.service.NotificationService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatMessageServiceImpl extends AbstractCrudService<ChatMessage> implements ChatMessageService {

  private static final int MESSAGE_PREVIEW_LENGTH = 120;

  @Autowired
  private ChatMessageRepository chatMessageRepository;

  @Autowired
  private ChatSessionService chatSessionService;

  @Autowired
  private UserService userService;

  @Autowired
  private DoctorService doctorService;

  @Autowired
  private ChatMessageMapper mapper;

  @Autowired
  private SimpMessagingTemplate template;

  @Autowired
  private DateUtil dateUtil;

  @Autowired
  @Qualifier("email")
  private NotificationService emailService;

  @Override
  protected ChatMessageRepository repository() {
    return chatMessageRepository;
  }

  @Override
  protected MetaData<ChatMessage> metaData() {
    return MetaData.<ChatMessage>builder()
      .classType(ChatMessage.class)
      .dtoClassType(ChatMessageDto.class)
      .build();
  }

  @Override
  protected ChatMessage findDuplicate(ChatMessage chatMessage) {
    return null;
  }

  @Override
  protected void preCreate(ChatMessage chat) {
    if (chat.getType() == null) {
      chat.setType(MessageType.USER_GENERATED);
    }
  }

  @Override
  public ChatMessage send(ChatMessageDto dto, String sessionSlug)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException {
    ChatSession session = chatSessionService.findBySlug(sessionSlug);
    checkUserAndMarkResolved(dto, session);
    ChatMessage message = create(mapper.toDomain(dto));
    updateSession(session, message);
    template.convertAndSend("/app/topic/messages/" + sessionSlug, mapper.toDto(message));
    return message;
  }

  private void checkUserAndMarkResolved(ChatMessageDto dto, ChatSession session) {
    try {
      if ("SYSTEM".equals(dto.getFromUserSlug())) {
        return;
      }
      User user = userService.findBySlug(dto.getFromUserSlug());
      if (user.getRoleName().equals(UserRole.PATIENT.name())) {
        session.setHasResolvedMessages(true);
      } else {
        session.setHasResolvedMessages(false);
        sendMailIfRequired(session, user);
      }
    } catch (Exception ex) {
      log.warn(ex.getMessage(), ex);
    }
  }

  private void sendMailIfRequired(ChatSession chatSession, User user) throws EntityNotFoundException {
    ChatMessage message = chatMessageRepository
      .findTop1ByChatSessionSlugAndFromUserSlugOrderByDateModifiedDesc(chatSession.getSlug(), user.getSlug());
    if (message == null || message.getDateCreated() == null) {
      return;
    }
    DateTime date = dateUtil.initDateFromDate(message.getDateCreated());
    DateTime now = dateUtil.now();
    log.info("Checking to send email {}, {}", date, now.minusHours(4));
    if (now.minusHours(4).isAfter(date)) {
      sendMailForChatMessage(chatSession, user);
    }
  }

  private void sendMailForChatMessage(ChatSession chatSession, User userDoc) throws EntityNotFoundException {
    User patient = userService.findBySlug(chatSession.getPatientSlug());
    Doctor doctor = doctorService.findBySlug(userDoc.getSlug());
    log.info("Sending email for char message to {}", patient.getName());
    emailService.sendMessage(
      MessageDto.builder()
        .to(patient.getEmail())
        .subject("Dr. " + doctor.getName() + " has replied to your chat message.")
        .content("Hello " + patient.getName() + ",\n" +
          "Dr. " + doctor.getName() + " has replied to your chat message. Kindly visit the link below to check.\n" +
          doctor.getWebsiteUrl() + "/app/chat")
        .build());
  }

  private void updateSession(ChatSession session, ChatMessage message) {
    String preview = !StringUtils.isEmpty(message.getAttachmentUrl()) ? "📷 Photo" : message.getText();
    if (preview != null && preview.length() > MESSAGE_PREVIEW_LENGTH) {
      preview = preview.substring(0, MESSAGE_PREVIEW_LENGTH) + "...";
    }
    session.setLatestMessagePreview(preview);
    chatSessionService.save(session);
  }

  @Override
  public Page<ChatMessage> findBySessionSlug(String sessionSlug, int page, int size) {
    return chatMessageRepository.findByChatSessionSlug(sessionSlug,
      PageRequest.of(page, size, Sort.by(Direction.DESC, "dateCreated")));
  }

}
