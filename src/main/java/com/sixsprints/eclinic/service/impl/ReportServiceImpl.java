package com.sixsprints.eclinic.service.impl;

import com.sixsprints.eclinic.domain.*;
import com.sixsprints.eclinic.dto.ChatMessageDto;
import com.sixsprints.eclinic.enums.ChatSessionStatus;
import com.sixsprints.eclinic.enums.MessageType;
import com.sixsprints.eclinic.enums.UserRole;
import com.sixsprints.eclinic.service.*;
import com.sixsprints.eclinic.service.user.UserService;
import com.sixsprints.eclinic.util.SlugUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.core.service.AbstractCrudService;
import com.sixsprints.eclinic.dto.ReportDto;
import com.sixsprints.eclinic.repository.ReportRepository;
import com.sixsprints.eclinic.domain.user.User;


import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.text.DateFormat;

@Service
public class ReportServiceImpl extends AbstractCrudService<Report> implements ReportService {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private ReportRepository reportRepository;
  @Autowired
  private AppointmentService appointmentService;

  @Autowired
  private ChatSessionService chatSessionService;

  @Autowired
  private ChatMessageService chatMessageService;

  @Autowired
  private OrderService orderService;

  @Autowired
  private UserService userService;

  @Override
  protected GenericRepository<Report> repository() {
    return reportRepository;
  }

  @Override
  protected MetaData<Report> metaData() {
    return MetaData.<Report>builder()
      .classType(Report.class).dtoClassType(ReportDto.class).defaultSort(Sort.by(Direction.ASC, "date")).build();
  }

  @Override
  protected Report findDuplicate(Report entity) {
    return reportRepository.findBySlug(entity.getSlug());
  }

  @Override
  public Report appointmentReport(Report report, String appointmentSlug)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException {
    Appointment appointment = appointmentService.findBySlug(appointmentSlug);
    Report createReport = super.create(report);
    appointmentService.save(appointment);
    return createReport;
  }

  @Override
  public void handleChatAction(Appointment appointment, User doctor) throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException {
    String paitentSlug = appointment.getPatientSlug();
    String doctorSlug = appointment.getDoctorSlug();
    String messageText = "Your Report has been Generated";
    Query chatSesionQuery = new Query();
    chatSesionQuery.addCriteria(new Criteria().andOperator(
            Criteria.where("patientSlug").is(paitentSlug),
            Criteria.where("doctorSlug").is(doctorSlug)
    ));
    ChatSession chatSession = mongoTemplate.findOne(chatSesionQuery, ChatSession.class);
    if (chatSession == null) {
      // fetch free chat pacakage
      Query chatPackageQuery = new Query();
      chatPackageQuery.addCriteria(Criteria.where("packageName").is("Free"));
      ChatPackage chatPackage = mongoTemplate.findOne(chatPackageQuery, ChatPackage.class);
      if (chatPackage == null) {
        throw new Error("Free Chat Package not Available");
      }
      User user = userService.findBySlug(doctorSlug);
      if (user == null) {
        throw new Error("User with Slug " + doctorSlug + " not Found!");
      }
      if (!user.getRoleName().equals(UserRole.DOCTOR.toString())) {
        throw new Error("Buy Order Package Action can be done via Doctor only");
      }
      Order order = orderService.buyChatPackageByDoctor(user, paitentSlug, chatPackage.getSlug());
      // create chat session
      ChatSession newChatSession = new ChatSession();
      newChatSession.setDoctorSlug(doctorSlug);
      newChatSession.setPatientSlug(paitentSlug);
      newChatSession.setStatus(ChatSessionStatus.ACTIVE);
      newChatSession.setHasResolvedMessages(false);
      newChatSession.setLatestMessagePreview(messageText);
      newChatSession.setDateCreated(new Date());
      newChatSession.setCreatedBy(user.getMobileNumber());
      newChatSession.setTotalQuestions(chatPackage.getTotalQuestions());
      newChatSession.setTotalDays(chatPackage.getTotalDays());
      chatSessionService.create(newChatSession);
      // create chat Message
      ChatMessageDto chatMessageDto = new ChatMessageDto();
      chatMessageDto.setText(messageText);
      chatMessageDto.setChatSessionSlug(newChatSession.getSlug());
      chatMessageDto.setFromUserSlug(doctorSlug);
      chatMessageDto.setType(MessageType.USER_GENERATED);
      chatMessageService.send(chatMessageDto, newChatSession.getSlug());
      return;
    }
    chatSession.setLatestMessagePreview(messageText);
    chatSessionService.patchUpdate(chatSession.getId(), chatSession, "latestMessagePreview");
    // create chat Message
    ChatMessageDto chatMessageDto = new ChatMessageDto();
    chatMessageDto.setText(messageText);
    chatMessageDto.setChatSessionSlug(chatSession.getSlug());
    chatMessageDto.setFromUserSlug(doctorSlug);
    chatMessageDto.setType(MessageType.USER_GENERATED);
    chatMessageService.send(chatMessageDto, chatSession.getSlug());
  }
}
