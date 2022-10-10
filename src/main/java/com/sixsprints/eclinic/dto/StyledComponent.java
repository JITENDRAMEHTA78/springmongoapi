package com.sixsprints.eclinic.dto;

import com.sixsprints.eclinic.domain.embed.ColorPalette.Color;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StyledComponent {

  private String label;

  private Color color;

}
