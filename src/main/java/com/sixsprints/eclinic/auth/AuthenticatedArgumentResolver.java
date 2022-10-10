package com.sixsprints.eclinic.auth;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.exception.NotAuthenticatedException;
import com.sixsprints.core.utils.AuthUtil;
import com.sixsprints.eclinic.domain.Role;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.enums.AccessPermission;
import com.sixsprints.eclinic.enums.EntityPermission;
import com.sixsprints.eclinic.service.RoleService;
import com.sixsprints.eclinic.service.user.UserService;
import com.sixsprints.eclinic.util.Constants;
import com.sixsprints.eclinic.util.PermissionUtil;
import com.sixsprints.eclinic.util.ThreadContext;

public class AuthenticatedArgumentResolver implements HandlerMethodArgumentResolver {

  @Resource
  private UserService userService;

  @Resource
  private RoleService roleService;

  @Override
  public User resolveArgument(MethodParameter param, ModelAndViewContainer mavContainer, NativeWebRequest request,
    WebDataBinderFactory binderFactory) throws Exception {
    Annotation[] paramAnns = param.getParameterAnnotations();
    for (Annotation annotation : paramAnns) {
      if (Authenticated.class.isInstance(annotation)) {
        Authenticated authAnnotation = (Authenticated) annotation;
        HttpServletRequest httprequest = (HttpServletRequest) request.getNativeRequest();
        String token = httprequest.getHeader(Constants.AUTH_TOKEN);
        if (StringUtils.isEmpty(token)) {
          token = httprequest.getParameter(Constants.AUTH_TOKEN);
        }
        User user = checkUser(authAnnotation, token);
        setDataInThreadContext(user);
        return user;
      }
    }
    return null;
  }

  private void setDataInThreadContext(User user) {
    if (user != null) {
      ThreadContext.setCurrentUser(user.authId());
      ThreadContext.setCurrentUserData(user);
    }
  }

  private User checkUser(Authenticated authAnnotation, String token)
    throws NotAuthenticatedException, EntityNotFoundException {
    Boolean tokenEmpty = checkIfTokenEmpty(authAnnotation, token);
    if (tokenEmpty) {
      return null;
    }
    User user = decodeUser(token, authAnnotation);
    if (user == null) {
      return null;
    }
    checkIfTokenInvalid(user.getInvalidTokens(), token, authAnnotation);
    checkIfActive(user, authAnnotation);
    checkUserPermissions(user, authAnnotation);
    return user;
  }

  private void checkUserPermissions(User user, Authenticated authAnnotation)
    throws NotAuthenticatedException, EntityNotFoundException {
    EntityPermission entityPermission = authAnnotation.entity();
    AccessPermission accessPermission = authAnnotation.access();
    if (PermissionUtil.allAny(entityPermission, accessPermission)) {
      return;
    }
    Role role = roleService.findByName(user.getRoleName());
    Boolean hasAccess = PermissionUtil.hasAccess(role, entityPermission, accessPermission);
    if (!hasAccess) {
      throwException(authAnnotation, "You are not authorized to take this action");
    }
  }

  private void checkIfActive(User user, Authenticated authAnnotation) throws NotAuthenticatedException {
    if (!user.getActive()) {
      throwException(authAnnotation, "User account is not active.");
    }
  }

  private User decodeUser(String token, Authenticated authAnnotation) throws NotAuthenticatedException {
    User user = null;
    try {
      String userId = AuthUtil.decodeToken(token);
      user = userService.findOne(userId);
    } catch (BaseException ex) {
      throwException(authAnnotation, ex.getMessage());
    }
    return user;
  }

  private void checkIfTokenInvalid(List<String> invalidTokens, String token, Authenticated authAnnotation)
    throws NotAuthenticatedException {
    if (!CollectionUtils.isEmpty(invalidTokens) && invalidTokens.contains(token)) {
      throwException(authAnnotation, "Token is invalid!");
    }
  }

  private Boolean checkIfTokenEmpty(Authenticated authAnnotation, String token) throws NotAuthenticatedException {
    if (StringUtils.isEmpty(token)) {
      throwException(authAnnotation, "Token is empty!");
      return true;
    }
    return false;
  }

  private void throwException(Authenticated authAnnotation, String message) throws NotAuthenticatedException {
    if (authAnnotation.required()) {
      throw NotAuthenticatedException.childBuilder().error(message).build();
    }
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(Authenticated.class);
  }

}