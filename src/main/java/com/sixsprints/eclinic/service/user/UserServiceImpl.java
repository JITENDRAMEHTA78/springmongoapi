package com.sixsprints.eclinic.service.user;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sixsprints.core.dto.MetaData;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.user.UserDto;
import com.sixsprints.eclinic.repository.user.UserRepository;
import com.sixsprints.eclinic.service.user.abs.AbstractUserService;
import com.sixsprints.eclinic.util.transformer.UserMapper;
import com.sixsprints.notification.service.NotificationService;

@Service("user")
public class UserServiceImpl extends AbstractUserService<User, UserDto> implements UserService {

  public UserServiceImpl(UserMapper mapper, @Qualifier("sms") NotificationService notificationService) {
    super(mapper, notificationService);
  }

  @Resource
  private UserRepository<User> userRepository;

  @Override
  protected UserRepository<User> repository() {
    return userRepository;
  }

  @Override
  protected MetaData<User> metaData() {
    return MetaData.<User>builder()
      .classType(User.class).dtoClassType(UserDto.class)
      .build();
  }

  @Override
  protected void preCreate(User entity) {
    super.preCreate(entity);
  }

  @Override
  protected User newUser(String mobileNumber) {
    return User.builder().mobileNumber(mobileNumber).build();
  }

}
