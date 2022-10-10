package com.sixsprints.eclinic.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;
import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.service.AbstractCrudService;
import com.sixsprints.core.utils.DateUtil;
import com.sixsprints.eclinic.domain.Appointment;
import com.sixsprints.eclinic.domain.ChatPackage;
import com.sixsprints.eclinic.domain.Order;
import com.sixsprints.eclinic.domain.user.Doctor;
import com.sixsprints.eclinic.domain.user.Patient;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.OrderDto;
import com.sixsprints.eclinic.dto.OrderProductDto;
import com.sixsprints.eclinic.dto.PaymentDto;
import com.sixsprints.eclinic.enums.AppointmentStatus;
import com.sixsprints.eclinic.enums.OrderStatus;
import com.sixsprints.eclinic.enums.PaymentMode;
import com.sixsprints.eclinic.enums.ProductType;
import com.sixsprints.eclinic.repository.OrderRepository;
import com.sixsprints.eclinic.service.AppointmentService;
import com.sixsprints.eclinic.service.ChatPackageService;
import com.sixsprints.eclinic.service.ChatSessionService;
import com.sixsprints.eclinic.service.OrderService;
import com.sixsprints.eclinic.service.user.DoctorService;
import com.sixsprints.eclinic.service.user.PatientService;
import com.sixsprints.eclinic.util.ThreadContext;
import com.sixsprints.eclinic.util.csv.OrderFieldData;
import com.sixsprints.eclinic.util.initdata.TenantData;
import com.sixsprints.notification.dto.MessageDto;
import com.sixsprints.notification.dto.ShortURLDto;
import com.sixsprints.notification.service.NotificationService;
import com.sixsprints.notification.service.URLShorteningService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderServiceImpl extends AbstractCrudService<Order> implements OrderService {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private DoctorService doctorService;

  @Autowired
  private ChatPackageService chatPackageService;

  @Autowired
  private ChatSessionService chatSessionService;

  @Autowired
  private PatientService patientService;

  @Autowired
  private AppointmentService appointmentService;

  @Autowired
  @Qualifier("sms")
  private NotificationService smsService;

  @Autowired
  @Qualifier("email")
  private NotificationService emailService;

  @Autowired
  private DateUtil dateUtil;

  @Autowired
  private URLShorteningService urlShorteningService;

  @Override
  protected OrderRepository repository() {
    return orderRepository;
  }

  @Override
  protected MetaData<Order> metaData() {
    return MetaData.<Order>builder()
      .classType(Order.class).dtoClassType(OrderDto.class)
      .defaultSort(Sort.by(Direction.DESC, "dateCreated"))
      .fields(OrderFieldData.fields()).build();
  }

  @Override
  protected Order findDuplicate(Order entity) {
    return orderRepository.findBySlug(entity.getSlug());
  }
  
  @Override
  public String sendMessage(String mobileNumber, String message) {
	  try {
	      smsService.sendMessage(
	        MessageDto.builder()
	          .to(mobileNumber)
	          .subject("Payment Accepted")
	          .templateId("1207161467099671169")
	          .content(message)
	          .build());
	      log.info("Message sent");
	    } catch (Exception ex) {
	      log.error("Unable to send notification");
	      log.error(ex.getMessage(), ex);
	    }
	  return "Success";
  }

  @Override
  public Order buyChatPackage(User patient, String doctorSlug, String chatPackageSlug)
    throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException {

    ChatPackage chatPackage = validateChatOrder(doctorSlug, chatPackageSlug);

    Order order = create(Order.builder()
      .doctorSlug(doctorSlug)
      .patientSlug(patient.getSlug())
      .status(OrderStatus.INITIATED)
      .products(
        OrderProductDto.builder()
          .productId(chatPackageSlug)
          .productType(ProductType.CHAT_PACKAGE)
          .quantity(1)
          .build())
      .build());

    if (chatPackage.getAmount().equals(BigDecimal.ZERO)) {
      PaymentDto paymentDetails = PaymentDto.builder().amount(BigDecimal.ZERO).mode(PaymentMode.PAY_AT_HOSPITAL)
        .build();
      order.setPaymentDetails(paymentDetails);
      save(order);
      changeStatusBulk(ImmutableList.of(order.getId()), OrderStatus.CONFIRMED, false);
    }

    return order;
  }

  
  @Override
  public Order buyChatPackageByDoctor(User doctor, String patientSlug, String chatPackageSlug)
    throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException {

    ChatPackage chatPackage = validateChatOrder(doctor.getSlug(), chatPackageSlug);

    Order order = create(Order.builder()
      .doctorSlug(doctor.getSlug())
      .patientSlug(patientSlug)
      .status(OrderStatus.INITIATED)
      .products(
        OrderProductDto.builder()
          .productId(chatPackageSlug)
          .productType(ProductType.CHAT_PACKAGE)
          .quantity(1)
          .build())
      .build());

    if (chatPackage.getAmount().equals(BigDecimal.ZERO)) {
      PaymentDto paymentDetails = PaymentDto.builder().amount(BigDecimal.ZERO).mode(PaymentMode.PAY_AT_HOSPITAL)
        .build();
      order.setPaymentDetails(paymentDetails);
      save(order);
      changeStatusBulk(ImmutableList.of(order.getId()), OrderStatus.CONFIRMED, true);
    }

    return order;
  }
  @Override
  public Order addPaymentDetails(String orderSlug, PaymentDto paymentDto) throws EntityNotFoundException {
    Order order = findBySlug(orderSlug);
    
    Appointment app = appointmentService.findBySlug(order.getProducts().getProductId());
    order.setPaymentDetails(paymentDto);
    order.setStatus(OrderStatus.PENDING_APPROVAL);
    if (PaymentMode.PAY_AT_HOSPITAL.equals(paymentDto.getMode())) {
    	//sms and email
      sendAppointmentNotificationPayAtHospital(order,  app,   "accepted");
      order.setStatus(OrderStatus.CONFIRMED);
    }
    updateProduct(order.getProducts(), paymentDto, order);
    return save(order);
  }

  private void updateProduct(OrderProductDto product, PaymentDto paymentDto, Order order) {
    if (ProductType.APPOINTMENT.equals(product.getProductType())) {
      try {
        Appointment app = appointmentService.findBySlug(product.getProductId());
        app.setStatus(AppointmentStatus.PENDING_APPROVAL);
        if (PaymentMode.PAY_AT_HOSPITAL.equals(paymentDto.getMode())) {
          app.setStatus(AppointmentStatus.OPEN);
          sendAppointmentNotificationPayAtHospital(order,  app,   "accepted");
        }
        appointmentService.save(app);
      } catch (Exception ex) {
        log.error("Unable to change payment status for product {}", product);
        log.error(ex.getMessage(), ex);
      }
    }
  }

  @Override
  public void changeStatusBulk(List<String> ids, OrderStatus status, boolean isDoctor) throws EntityNotFoundException {
    Iterable<Order> orders = orderRepository.findAllById(ids);
    for (Order order : orders) {
      if (order.getStatus().equals(status)) {
        continue;
      }
      order.setStatus(status);
      order = save(order);
      if (!isDoctor) {
    	  processOrder(order);
      }
    }
  }

  @Override
  protected void preCreate(Order entity) {
    super.preCreate(entity);
    entity.setTenantId(ThreadContext.getCurrentTenant().getId());
  }

  /**
   * Will throw exception if slugs are not correct
   * 
   * @param doctorSlug
   * @param chatPackageSlug
   * @return
   * @throws EntityNotFoundException
   */
  private ChatPackage validateChatOrder(String doctorSlug, String chatPackageSlug) throws EntityNotFoundException {
    doctorService.findBySlug(doctorSlug);
    return chatPackageService.findBySlug(chatPackageSlug);
  }

  private void processOrder(Order order) {
    try {
      if (OrderStatus.CONFIRMED.equals(order.getStatus())) {
        processSuccess(order, order.getProducts());
      } else if (OrderStatus.REJECTED.equals(order.getStatus())) {
        processRejected(order, order.getProducts());
      }

    } catch (Exception ex) {
      log.error("Unable to process order {}", order);
      log.error(ex.getMessage(), ex);
    }
  }

  private void processSuccess(Order order, OrderProductDto product) {
    try {
      if (product.getProductType().equals(ProductType.CHAT_PACKAGE)) {
        ChatPackage chatPackage = chatPackageService.findBySlug(product.getProductId());
        chatSessionService.topUpOnPurchase(chatPackage, order.getPatientSlug());
         //sending email instead of sms
         sendChatPurchaseNotification(order);
      } else if (product.getProductType().equals(ProductType.APPOINTMENT)) {
        Appointment appointment = appointmentService.confirmPayment(product.getProductId(), order.getPaymentDetails());
        //sending email and sms
        sendAppointmentNotification(order, appointment, "accepted");
      }
    } catch (Exception ex) {
      log.error("Unable to process order with Product ID {}, order slug {} ", product.getProductId(),
        order.getSlug());
      log.error(ex.getMessage(), ex);
    }
  }

  private void processRejected(Order order, OrderProductDto product) {
    try {
      if (product.getProductType().equals(ProductType.APPOINTMENT)) {
        Appointment appointment = appointmentService.changeStatus(product.getProductId(), AppointmentStatus.CANCELLED);
        sendAppointmentNotification(order, appointment, "rejected");
      }
    } catch (Exception ex) {
      log.error("Unable to process rejected order with Product ID {}, order slug {} ", product.getProductId(),
        order.getSlug());
      log.error(ex.getMessage(), ex);
    }
  }

  private void sendChatPurchaseNotification(Order order) {
    try {
      Patient patient = patientService.findBySlug(order.getPatientSlug());
      Doctor doc = doctorService.findBySlug(order.getDoctorSlug());

      String url = buildChatUrl(doc.getWebsiteUrl(), TenantData.search(doc.getTenantId()).getAuthWebUrl(),
        patient.getMobileNumber());

      if (patient.getEmail() != null || !patient.getEmail().equals("")) {
    	  emailService.sendMessage(
          MessageDto.builder()
            .to(patient.getEmail())
            .subject("Payment Accepted")
            .content( "Your payment for chat package purchase has been accepted. Please feel free to connect with Dr. "
                    + doc.getName() + " by visting the link below:\n"
                    + url)
            .build());
      }
     

    } catch (Exception ex) {
      log.error("Unable to send notification for Order {}", order);
      log.error(ex.getMessage(), ex);
    }
  }

  private void sendAppointmentNotification(Order order, Appointment appointment, String status) {
    try {
      Patient patient = patientService.findBySlug(order.getPatientSlug());
      Doctor doc = doctorService.findBySlug(order.getDoctorSlug());
      
      
      smsService.sendMessage(
        MessageDto.builder().to(patient.getMobileNumber()).subject("Payment Accepted")
          .templateId("1207161467089870099")
          .content("Your payment for appointment with Dr. " + doc.getName() + " scheduled on "
              + dateUtil.dateToStringWithFormat(appointment.getDate(),
                  DateUtil.DEFAULT_SHORT_DATE_PATTERN)
              + " " + appointment.getTime() + " has been " + status + ".")
          .build());
    
      if (patient.getEmail() != null || !patient.getEmail().equals("")) {
        emailService.sendMessage(
          MessageDto.builder()
            .to(patient.getEmail())
            .subject("Payment Accepted")
            .content( "Your payment for appointment with Dr. "
                    + doc.getName() + " scheduled on "
                    + dateUtil.dateToStringWithFormat(appointment.getDate(), DateUtil.DEFAULT_SHORT_DATE_PATTERN) + " "
                    + appointment.getTime() + " has been " + status + ".")
            .build());
      }

    } catch (Exception ex) {
      log.error("Unable to send notification for Order {}", order);
      log.error(ex.getMessage(), ex);
    }
  }
  
  private void sendAppointmentNotificationPayAtHospital(Order order, Appointment appointment, String status) {
	    try {
	      Patient patient = patientService.findBySlug(order.getPatientSlug());
	      Doctor doc = doctorService.findBySlug(order.getDoctorSlug());
	      
	      smsService.sendMessage(
	        MessageDto.builder()
	          .to(patient.getMobileNumber())
	          .subject("Appointment Accepted")
	          .templateId("1207161467099671169")
	          .content(
	            "Your appointment with Dr. "
	              + doc.getName() + " scheduled on "
	              + dateUtil.dateToStringWithFormat(appointment.getDate(), DateUtil.DEFAULT_SHORT_DATE_PATTERN) + " "
	              + appointment.getTime() + " has been " + status + ".")
	          .build());
	      
	      if (patient.getEmail() != null || !patient.getEmail().equals("")) {
	       emailService.sendMessage(
	              MessageDto.builder()
	                .to(patient.getEmail())
	                .subject("Appointment Accepted")
	                .content( "Your appointment with Dr. "
	                        + doc.getName() + " scheduled on "
	                        + dateUtil.dateToStringWithFormat(appointment.getDate(), DateUtil.DEFAULT_SHORT_DATE_PATTERN) + " "
	                        + appointment.getTime() + " has been " + status + ".")
	                .build());
	      }

	    } catch (Exception ex) {
	      log.error("Unable to send notification for Order {}", order);
	      log.error(ex.getMessage(), ex);
	    }
	  }

  @Override
  public Order bookAppointment(User patient, String doctorSlug, String appointmentSlug)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException {
    validateAppointmentOrder(doctorSlug, appointmentSlug);

    return create(Order.builder()
      .doctorSlug(doctorSlug)
      .patientSlug(patient.getSlug())
      .status(OrderStatus.INITIATED)
      .creatorName(patient.getName().toString())
      .creatorRole(patient.getRoleName().toString())
      .products(
        OrderProductDto.builder()
          .productId(appointmentSlug)
          .productType(ProductType.APPOINTMENT)
          .quantity(1)
          .build())
      .build());
  }

  private void validateAppointmentOrder(String doctorSlug, String appointMentSlug) throws EntityNotFoundException {
    doctorService.findBySlug(doctorSlug);
    appointmentService.findBySlug(appointMentSlug);
  }

  private String buildChatUrl(String docWebsite, String authUrl, String patientMobile) {
    String url = new StringBuilder(authUrl).append("/auth/login?redirectUrl=").append(docWebsite)
      .append("/home/callback").append("&redirectPath=/app/chat").append("&mobileNumber=")
      .append(patientMobile).toString();

    ShortURLDto shorten = urlShorteningService.shorten(url);
    if (shorten == null || StringUtils.isBlank(shorten.getShortLink())) {
      return url;
    }
    return shorten.getShortLink();

  }

  @Override
  public Order createOrderEntry(Order order, OrderStatus status)
    throws EntityAlreadyExistsException, EntityInvalidException {
    Order fromDb = orderRepository.findByStatusAndProducts_productId(status,
      order.getProducts().getProductId());
    if (fromDb != null) {
      fromDb.setPaymentDetails(order.getPaymentDetails());
      return save(fromDb);
    }
    return create(order);
  }

  @Override
  public void setReceiptPrinted(OrderStatus status, String appointmentSlug) {
    Order fromDb = orderRepository.findByStatusAndProducts_productId(status, appointmentSlug);
    if (fromDb != null && fromDb.getPaymentDetails() != null) {
      PaymentDto details = fromDb.getPaymentDetails();
      details.setIsReceiptPrinted(Boolean.TRUE);
      fromDb.setPaymentDetails(details);
      save(fromDb);
    }
  }

}
