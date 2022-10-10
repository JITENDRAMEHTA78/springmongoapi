package com.sixsprints.eclinic.controller.auth;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.sixsprints.auth.controller.AbstractAuthController;
import com.sixsprints.auth.dto.AuthResponseDto;
import com.sixsprints.auth.dto.Authenticable;
import com.sixsprints.auth.dto.ResetPasscode;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.exception.NotAuthenticatedException;
import com.sixsprints.core.transformer.GenericTransformer;
import com.sixsprints.core.utils.RestResponse;
import com.sixsprints.core.utils.RestUtil;
import com.sixsprints.eclinic.auth.Authenticated;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.AuthDto;
import com.sixsprints.eclinic.dto.user.UserDto;
import com.sixsprints.eclinic.service.user.abs.GenericUserService;
import com.sixsprints.eclinic.util.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractUserAuthController<U extends User, D extends UserDto, A extends Authenticable, R extends ResetPasscode>
  extends AbstractAuthController<U, D, A, R> {

  private GenericUserService<U, D> service;

  public AbstractUserAuthController(GenericUserService<U, D> service, GenericTransformer<U, D> mapper) {
    super(service, mapper);
    this.service = service;
  }
  
  @Override
  public ResponseEntity<RestResponse<AuthResponseDto<D>>> login(@RequestBody @Valid A authDto)
    throws NotAuthenticatedException, EntityNotFoundException, EntityInvalidException {
    //System.out.println("authDto "+authDto);
    return super.login(authDto);
  }

  @Override
  public ResponseEntity<RestResponse<String>> sendOtp(@RequestBody @Valid A auth) throws EntityNotFoundException {
    return super.sendOtp(auth);
  }

  @PostMapping("/send-otp-login")
  public ResponseEntity<RestResponse<D>> sendOtpFrAuth(@RequestBody @Valid AuthDto auth)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException {
    log.info("Request to send otp for {}", auth.authId());
    
    D user = service.sendOtpForAuth(auth.getMobileNumber(), auth.getCountryCode(), auth.getEmail());
    return RestUtil.successResponse(user);
  }

  @PostMapping("/signup")
  public ResponseEntity<RestResponse<AuthResponseDto<D>>> loginOrSignup(@RequestBody @Valid AuthDto authDto)
    throws NotAuthenticatedException, EntityNotFoundException, EntityInvalidException {
    log.info("Request to login {}", authDto.authId());
    return RestUtil.successResponse(service.loginOrSignup(authDto));
  }

  @Override
  public ResponseEntity<RestResponse<String>> resetPassword(@RequestBody @Valid R resetDto)
    throws EntityInvalidException {
    return super.resetPassword(resetDto);
  }

  @Override
  public ResponseEntity<RestResponse<D>> validateToken(@Authenticated U user) {
    return super.validateToken(user);
  }

  @Override
  public ResponseEntity<?> logout(@Authenticated U user, @RequestHeader(value = Constants.AUTH_TOKEN) String token) {
    return super.logout(user, token);
  }

}