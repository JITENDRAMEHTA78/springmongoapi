package com.sixsprints.eclinic.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.eclinic.domain.ChatMessage;

@Repository
public interface ChatMessageRepository extends GenericRepository<ChatMessage> {

  Page<ChatMessage> findByChatSessionSlug(String chatSessionSlug, Pageable page);

  ChatMessage findTop1ByChatSessionSlugAndFromUserSlugOrderByDateModifiedDesc(String chatSessionSlug, String userSlug);

}
