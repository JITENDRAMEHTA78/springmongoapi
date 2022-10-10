package com.sixsprints.eclinic.service.user;

import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.user.UserDto;
import com.sixsprints.eclinic.service.user.abs.GenericUserService;

public interface UserService extends GenericUserService<User, UserDto> {

}