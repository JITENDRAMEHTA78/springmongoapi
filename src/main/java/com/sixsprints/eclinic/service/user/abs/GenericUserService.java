package com.sixsprints.eclinic.service.user.abs;

import com.sixsprints.auth.dto.AuthResponseDto;
import com.sixsprints.auth.service.AuthService;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.AuthDto;
import com.sixsprints.eclinic.dto.user.UserDto;

public interface GenericUserService<U extends User, D extends UserDto> extends AuthService<U, D> {

  U findByMobileNumber(String mobileNumber);

  D sendOtpForAuth(String authId, String countryCode, String email) throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException;

  AuthResponseDto<D> loginOrSignup(AuthDto authDto) throws EntityNotFoundException, EntityInvalidException;

}
