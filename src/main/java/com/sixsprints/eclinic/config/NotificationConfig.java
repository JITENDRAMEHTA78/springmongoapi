package com.sixsprints.eclinic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sixsprints.notification.dto.MessageAuthDto;
import com.sixsprints.notification.service.NotificationService;
import com.sixsprints.notification.service.impl.EmailServiceSmtp;
import com.sixsprints.notification.service.impl.SmsService;

@Configuration
public class NotificationConfig {

  @Value(value = "${email.from}")
  private String from;

  @Value(value = "${email.hostname}")
  private String hostName;

  @Value(value = "${email.username}")
  private String username;

  @Value(value = "${email.password}")
  private String password;

  @Value(value = "${email.ssl.enabled}")
  private boolean sslEnabled;

  @Value(value = "${sms.from}")
  private String smsFrom;

  @Value(value = "${sms.password}")
  private String smsPassword;

  @Value(value = "${sms.username}")
  private String smsUsername;

  public MessageAuthDto emailAuth() {
    return MessageAuthDto.builder().from(from).hostName(hostName)
      .username(username).password(password).sslEnabled(sslEnabled).build();
  }

  public MessageAuthDto smsAuth() {
    return MessageAuthDto.builder().from(smsFrom)
      .username(smsUsername).password(smsPassword).build();
  }

  @Bean("email")
  public NotificationService emailService() {
    return new EmailServiceSmtp(emailAuth());
  }

  @Bean("sms")
  public NotificationService smsService() {
    return new SmsService(smsAuth());
  }

}
