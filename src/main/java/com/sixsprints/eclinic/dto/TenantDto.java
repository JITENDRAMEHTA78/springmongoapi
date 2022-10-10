package com.sixsprints.eclinic.dto;

import com.sixsprints.eclinic.domain.embed.ColorPalette;
import com.sixsprints.eclinic.dto.user.DoctorDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class TenantDto {

  private String id;

  private String name;

  private String domainName;

  private String logo;

  private String banner;

  private ColorPalette theme;

  private ContactInfo contactInfo;

  private String shortTitle;

  private String aboutUs;

  private DoctorDto selectedDoctor;

  private String adminWebUrl;
  
  private String authWebUrl;

  private String uhidPrefix;

}
