
package com.sixsprints.eclinic.dto;

import java.util.Date;

import com.sixsprints.eclinic.dto.user.UserDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmrTemplateDto {

  private String id;

  private String slug;

  private String createdBy;

  private String lastModifiedBy;

  private UserDto createdByUser;

  private UserDto lastModifiedByUser;

  private Date dateCreated;

  private Date dateModified;

  private String name;

  private String specialitySlug;

  private SpecialityDto speciality;

  private PrescriptionDto template;

}
