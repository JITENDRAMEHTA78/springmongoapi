package com.sixsprints.eclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sixsprints.core.dto.PageDto;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.utils.RestResponse;
import com.sixsprints.core.utils.RestUtil;
import com.sixsprints.eclinic.auth.Authenticated;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.ChatSessionDto;
import com.sixsprints.eclinic.service.ChatSessionService;
import com.sixsprints.eclinic.util.Constants;
import com.sixsprints.eclinic.util.transformer.ChatSessionMapper;

@RestController
@RequestMapping(value = Constants.API_PREFIX + "/chat-session")
public class ChatSessionController {

  @Autowired
  private ChatSessionService chatSessionService;

  @Autowired
  private ChatSessionMapper chatSessionMapper;

  @GetMapping("/my")
  public ResponseEntity<RestResponse<PageDto<ChatSessionDto>>> sessions(@Authenticated User user,
    @RequestParam(required = false, defaultValue = "0") int page,
    @RequestParam(required = false, defaultValue = "20") int size) {
    PageDto<ChatSessionDto> sessions = chatSessionMapper
      .pageEntityToPageDtoDto(chatSessionService.findMySessions(user.getSlug(), page, size));
    return RestUtil.successResponse(sessions, "success");
  }

  @GetMapping("/doc/my")
  public ResponseEntity<RestResponse<ChatSessionDto>> sessionWithDoctor(@Authenticated User user,
    @RequestParam String docSlug) {
    ChatSessionDto session = chatSessionMapper
      .toDto(chatSessionService.findByDoctorSlugAndPatientSlug(docSlug, user.getSlug()));
    return RestUtil.successResponse(session, "success");
  }

  @PutMapping("/mark-resolve")
  public ResponseEntity<RestResponse<ChatSessionDto>> markResolved(@Authenticated User user,
    @RequestParam String sessionSlug)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException {
    ChatSessionDto chatPackage = chatSessionMapper.toDto(chatSessionService.markResolve(sessionSlug));
    return RestUtil.successResponse(chatPackage, "success");
  }
  
  @PutMapping("/mark-unresolve")
  public ResponseEntity<RestResponse<ChatSessionDto>> markUnResolved(@Authenticated User user,
    @RequestParam String sessionSlug)
    throws BaseException {
    ChatSessionDto chatPackage = chatSessionMapper.toDto(chatSessionService.markUnResolve(sessionSlug));
    return RestUtil.successResponse(chatPackage, "success");
  }

}
