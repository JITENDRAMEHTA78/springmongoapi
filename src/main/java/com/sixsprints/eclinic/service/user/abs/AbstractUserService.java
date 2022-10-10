package com.sixsprints.eclinic.service.user.abs;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.aggregation.DateOperators.IsoDayOfWeek;
import org.springframework.http.HttpStatus;

import com.sixsprints.auth.domain.Otp;
import com.sixsprints.auth.dto.AuthResponseDto;
import com.sixsprints.auth.service.Impl.AbstractAuthService;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.transformer.GenericTransformer;
import com.sixsprints.core.utils.EncryptionUtil;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.AuthDto;
import com.sixsprints.eclinic.dto.user.UserDto;
import com.sixsprints.eclinic.repository.user.UserRepository;
import com.sixsprints.eclinic.util.AppMessages;
import com.sixsprints.eclinic.util.PhoneNumberUtil;
import com.sixsprints.eclinic.util.ThreadContext;
import com.sixsprints.notification.dto.MessageDto;
import com.sixsprints.notification.service.NotificationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractUserService<U extends User, D extends UserDto> extends AbstractAuthService<U, D>
  implements GenericUserService<U, D> {

  private GenericTransformer<U, D> mapper;

  @Autowired
  @Qualifier("email")
  private NotificationService emailService;

  public AbstractUserService(GenericTransformer<U, D> mapper, NotificationService notificationService) {
    super(mapper, notificationService);
    this.mapper = mapper;
  }

  @Override
  public U findByMobileNumber(String mobileNumber) {
    return findByAuthId(mobileNumber);
  }

  @Override
  protected abstract UserRepository<U> repository();

  @Override
  protected U findByAuthId(String authId) {
    return repository().findByMobileNumberAndTenantId(PhoneNumberUtil.cleanNumber(authId),
      ThreadContext.getCurrentTenant().getId());
  }

  protected U findByAuthIdWithoutCode(String authId) {
    return repository().findByMobileNumberAndTenantId(PhoneNumberUtil.cleanNumberWithoutCode(authId),
      ThreadContext.getCurrentTenant().getId());
  }

  @Override
  protected U findDuplicate(U entity) {
    return repository().findByMobileNumberAndTenantId(entity.authId(), ThreadContext.getCurrentTenant().getId());
  }

  @Override
  protected void preCreate(U entity) {
    super.preCreate(entity);
    if (StringUtils.isEmpty(entity.getPassword())) {
      entity.setPassword(entity.authId());
    }
    entity.setPassword(EncryptionUtil.encrypt(entity.getPassword()));
    entity.setTenantId(ThreadContext.getCurrentTenant().getId());
    entity.setMobileNumber(PhoneNumberUtil.cleanNumberWithoutCode(entity.getMobileNumber()));
  }

  @Override
  protected EntityNotFoundException notFoundException(String string) {
    return EntityNotFoundException.childBuilder().error(AppMessages.User.NOT_FOUND).arg(string).build();
  }

  @Override
  protected EntityAlreadyExistsException alreadyExistsException(U domain) {
    return EntityAlreadyExistsException.childBuilder().error(AppMessages.User.ALREADY_EXISTS)
      .arg(domain.getMobileNumber())
      .build();
  }

  @Override
  public Otp validateOtp(String authId, String otp) throws EntityInvalidException {
    try {
      return super.validateOtp(authId, otp);
    } catch (EntityInvalidException e) {
      throw EntityInvalidException.childBuilder().error(AppMessages.User.OTP_INVALID).arg(otp).build();
    }
  }

  @Override
  public D sendOtpForAuth(String authId, String countryCode, String email)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException {
    U user = findByAuthId(authId);
    if (user == null) {
      user = findByAuthIdWithoutCode(authId);
    }
    if (user == null) {
      user = newUser(authId);
      create(user);
    }
    Otp otp = super.sendOtp(authId);
    
    MessageDto message = otpMessage(otp);
    message.setTo(email);
    if (!countryCode.equals("+91")) {
		 if (email != null && email.length() > 8) {
			 emailService.sendMessage(message);
		 } else {
			 throw new EntityInvalidException(HttpStatus.BAD_REQUEST, "Email is mandatory if country is not india", null, null);
		 }
	 } else {
		 //send otp because it is from india
		 if (email != null && email.length() > 8) {
			 emailService.sendMessage(message);
		 }
	 }
    log.info("OTP generated for {} is {}", authId, otp.getOtp());
    if (user != null && StringUtils.isNotBlank(user.getEmail())) {
      MessageDto messageNew = otpMessage(otp);
      message.setTo(user.getEmail());
      emailService.sendMessage(messageNew);
    }
    if (user != null && user.getMobileNumber() != null) {
    	 String mobileNumberWithoutCode = user.getMobileNumber().replace("+91", "");
    	 user.setMobileNumber(mobileNumberWithoutCode);
    }
   
    return mapper.toDto(user);
  }

  @Override
  public AuthResponseDto<D> loginOrSignup(AuthDto authDto) throws EntityNotFoundException, EntityInvalidException {
    U user = findByAuthId(authDto.authId());
    if (user == null) {
      user = findByAuthIdWithoutCode(authDto.authId());
    }
    if (user == null) {
      throw notFoundException(authDto.authId());
    }
    if (StringUtils.isEmpty(user.getName())) {
      user.setName(authDto.getName());
      user.setEmail(authDto.getEmail());
      user = save(user);
    }
    validateOtp(authDto.authId(), authDto.passcode());
    return generateToken(user);
  }

  @Override
  protected MessageDto otpMessage(Otp otp) {
    MessageDto message = MessageDto.builder().to(otp.getAuthId()).subject("OTP Generated Successfully")
      .content(String.format("%s is your confidential OTP for Logging in to your Proclinic Account.", otp.getOtp()))
      .templateId("1207161779479384687")
      .build();
    return message;
  }

  protected abstract U newUser(String mobileNumber);

}
