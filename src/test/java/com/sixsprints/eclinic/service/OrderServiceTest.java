package com.sixsprints.eclinic.service;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableList;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.exception.NotAuthenticatedException;
import com.sixsprints.eclinic.ApplicationTests;
import com.sixsprints.eclinic.domain.ChatPackage;
import com.sixsprints.eclinic.domain.ChatSession;
import com.sixsprints.eclinic.domain.Order;
import com.sixsprints.eclinic.domain.user.Doctor;
import com.sixsprints.eclinic.domain.user.Patient;
import com.sixsprints.eclinic.dto.PaymentDto;
import com.sixsprints.eclinic.enums.Gender;
import com.sixsprints.eclinic.enums.OrderStatus;
import com.sixsprints.eclinic.enums.PaymentMode;
import com.sixsprints.eclinic.service.user.DoctorService;
import com.sixsprints.eclinic.service.user.PatientService;
import com.sixsprints.eclinic.service.util.DataBuilderService;
import com.sixsprints.eclinic.util.initdata.TenantData;

public class OrderServiceTest extends ApplicationTests {

  @Resource
  private PatientService patientService;

  @Resource
  private DoctorService doctorService;

  @Resource
  private ChatPackageService chatPackageService;

  @Autowired
  private DataBuilderService dataService;

  @Autowired
  private OrderService orderService;

  @Autowired
  private ChatSessionService chatSessionService;

  @Test
  public void shouldCreateChatSession()
    throws EntityAlreadyExistsException, EntityInvalidException, NotAuthenticatedException, EntityNotFoundException {

    dataService.init();

    Patient pat = patientService
      .create(Patient.builder().gender(Gender.MALE).password("password").mobileNumber("9810306710")
        .tenantId(TenantData.DEFAULT_TENANT).build());

    Doctor doc = doctorService.findAll(0, 1).getContent().get(0);

    ChatPackage chatPackage = chatPackageService.create(ChatPackage.builder()
      .amount(BigDecimal.valueOf(100))
      .doctorSlug(doc.getSlug())
      .packageName("5 Questions")
      .totalDays(2)
      .totalQuestions(5)
      .build());

    buyChatPackage(pat, doc, chatPackage);
    List<ChatSession> sessions = chatSessionService.findAll();
    Assert.assertEquals(1, sessions.size());
    ChatSession session = sessions.get(0);
    Assert.assertTrue(session.getTotalDays().equals(2));
    Assert.assertTrue(session.getTotalQuestions().equals(5));

    buyChatPackage(pat, doc, chatPackage);
    sessions = chatSessionService.findAll();
    Assert.assertEquals(1, sessions.size());
    session = sessions.get(0);
    Assert.assertTrue(session.getTotalDays().equals(4));
    Assert.assertTrue(session.getTotalQuestions().equals(10));
  }

  private void buyChatPackage(Patient pat, Doctor doc, ChatPackage chatPackage)
    throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException {
    Order order = orderService.buyChatPackage(pat, doc.getSlug(), chatPackage.getSlug());
    orderService.addPaymentDetails(order.getSlug(), PaymentDto.builder()
      .amount(BigDecimal.valueOf(100))
      .mode(PaymentMode.GOOGLEPAY)
      .utr("001")
      .build());
    orderService.changeStatusBulk(ImmutableList.of(order.getId()), OrderStatus.CONFIRMED, true);
  }

}
