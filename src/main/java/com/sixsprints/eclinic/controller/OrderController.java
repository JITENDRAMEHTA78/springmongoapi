package com.sixsprints.eclinic.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sixsprints.core.controller.AbstractCrudController;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.dto.PageDto;
import com.sixsprints.core.dto.filter.ColumnFilter;
import com.sixsprints.core.dto.filter.ExactMatchColumnFilter;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.utils.RestResponse;
import com.sixsprints.core.utils.RestUtil;
import com.sixsprints.eclinic.auth.Authenticated;
import com.sixsprints.eclinic.domain.Order;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.OrderDto;
import com.sixsprints.eclinic.dto.PaymentDto;
import com.sixsprints.eclinic.enums.AccessPermission;
import com.sixsprints.eclinic.enums.EntityPermission;
import com.sixsprints.eclinic.enums.OrderStatus;
import com.sixsprints.eclinic.enums.UserRole;
import com.sixsprints.eclinic.service.OrderService;
import com.sixsprints.eclinic.util.Constants;
import com.sixsprints.eclinic.util.transformer.OrderMapper;

@RestController
@RequestMapping(value = Constants.API_PREFIX + "/order")
public class OrderController
  extends AbstractCrudController<Order, OrderDto, User> {

  private OrderMapper mapper;

  private OrderService service;

  public OrderController(OrderService service, OrderMapper mapper) {
    super(service, mapper);
    this.mapper = mapper;
    this.service = service;
  }

  @Override
  public ResponseEntity<RestResponse<PageDto<OrderDto>>> filter(
    @Authenticated(entity = EntityPermission.PAYMENT, access = AccessPermission.READ) User user,
    @RequestBody FilterRequestDto filterRequestDto) {
    checkUserAndUpdateFilter(filterRequestDto, user);
    return super.filter(user, filterRequestDto);
  }

  @PostMapping("/initiate/chat-package")
  public ResponseEntity<RestResponse<OrderDto>> buy(
    @Authenticated(entity = EntityPermission.CHAT_SESSION, access = AccessPermission.CREATE) User patient,
    @RequestParam String doctorSlug, @RequestParam String chatPackageSlug)
    throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException {
    return RestUtil.successResponse(mapper.toDto(service.buyChatPackage(patient, doctorSlug, chatPackageSlug)));
  }
  
  @PostMapping("/initiate/chat-package/doctor")
  public ResponseEntity<RestResponse<OrderDto>> buyByDoctor(
    @Authenticated(entity = EntityPermission.CHAT_SESSION, access = AccessPermission.CREATE) User doctor,
    @RequestParam String patientSlug, @RequestParam String chatPackageSlug)
    throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException {
    return RestUtil.successResponse(mapper.toDto(service.buyChatPackageByDoctor(doctor, patientSlug, chatPackageSlug)));
  }

  @PostMapping("/initiate/book-appointment")
  public ResponseEntity<RestResponse<OrderDto>> bookAppointment(
    @Authenticated(entity = EntityPermission.APPOINTMENT, access = AccessPermission.CREATE) User patient,
    @RequestParam String doctorSlug, @RequestParam String appointMentSlug)
    throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException {
    return RestUtil.successResponse(mapper.toDto(service.bookAppointment(patient, doctorSlug, appointMentSlug)));

  }

  @PutMapping("/payment/{orderSlug}")
  public ResponseEntity<RestResponse<OrderDto>> addPaymentToOrder(
    @Authenticated(entity = EntityPermission.PAYMENT, access = AccessPermission.CREATE) User patient,
    @RequestBody @Valid PaymentDto paymentDto, @PathVariable String orderSlug) throws EntityNotFoundException {
    return RestUtil.successResponse(mapper.toDto(service.addPaymentDetails(orderSlug, paymentDto)));
  }

  @PutMapping("/status/{status}")
  public ResponseEntity<?> changeOrderStatus(
    @Authenticated(entity = EntityPermission.PAYMENT, access = AccessPermission.UPDATE) User doctor,
    @PathVariable OrderStatus status, @RequestBody List<String> ids)
    throws EntityNotFoundException {
    service.changeStatusBulk(ids, status, true);
    return RestUtil.successResponse(null);
  }
  
  @GetMapping("/send/message/{mobile}/{message}")
  public ResponseEntity<?> sendMessage(
    @Authenticated(entity = EntityPermission.PAYMENT, access = AccessPermission.UPDATE) User doctor,
    @PathVariable String mobile,@PathVariable String message)
    throws EntityNotFoundException {
    service.sendMessage(mobile, message);
    return RestUtil.successResponse(null);
  }

  private FilterRequestDto checkUserAndUpdateFilter(FilterRequestDto filters, User user) {
    if (filters == null) {
      filters = FilterRequestDto.builder().build();
    }
    if (isDoctor(user)) {
      Map<String, ColumnFilter> filterModel = filters.getFilterModel();
      if (filterModel == null) {
        filterModel = new HashMap<>();
      }
      filterModel.put("doctorSlug", ExactMatchColumnFilter.builder().filter(user.getSlug()).build());
      filters.setFilterModel(filterModel);
    }
    return filters;
  }

  private boolean isDoctor(User user) {
    return UserRole.DOCTOR.toString().equals(user.getRoleName());
  }
}