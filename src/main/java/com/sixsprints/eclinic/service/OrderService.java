package com.sixsprints.eclinic.service;

import java.util.List;

import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.service.GenericCrudService;
import com.sixsprints.eclinic.domain.Order;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.PaymentDto;
import com.sixsprints.eclinic.enums.OrderStatus;

public interface OrderService extends GenericCrudService<Order> {

  Order buyChatPackage(User patient, String doctorSlug, String chatPackageSlug)
    throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException;
  
  public Order buyChatPackageByDoctor(User doctor, String patientSlug, String chatPackageSlug)
		    throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException;

  Order addPaymentDetails(String orderSlug, PaymentDto paymentDto) throws EntityNotFoundException;

  void changeStatusBulk(List<String> ids, OrderStatus status, boolean isDoctor) throws EntityNotFoundException;

  Order bookAppointment(User patient, String doctorSlug, String appointMentSlug)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException;

  Order createOrderEntry(Order order, OrderStatus status) throws EntityAlreadyExistsException, EntityInvalidException;

  void setReceiptPrinted(OrderStatus status, String appointmentSlug);
  
  String sendMessage(String mobileNumber, String message);

}
