package com.sixsprints.eclinic.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "userSlug")
public class NoteDto {

  private String userName;

  private String userSlug;

  private Date dateAdded;

  private String noteDescription;

  private String userRole;
}
