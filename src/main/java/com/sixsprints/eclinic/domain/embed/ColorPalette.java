package com.sixsprints.eclinic.domain.embed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColorPalette {

  private Color primary;

  private Color secondary;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Color {
    private String light;
    private String main;
    private String dark;
    private String contrastText;
  }

}
