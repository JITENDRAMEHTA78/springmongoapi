package com.sixsprints.eclinic.util.initdata;

import java.time.LocalDate;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.sixsprints.eclinic.domain.embed.ColorPalette;
import com.sixsprints.eclinic.dto.ContactInfo;
import com.sixsprints.eclinic.dto.TenantDto;

public class TenantData {

  public static final String DEFAULT_TENANT = "jyotihospital";

  private static final String BASE_IMG_URL = "https://storage.googleapis.com/eclinic_assets/tenants/";

//  private static final String DOMAIN = "ec.dev.sixsprintscloud.com";
  private static final String DOMAIN = "proclinic.in";

  public static final List<TenantDto> TENANTS = ImmutableList.<TenantDto>builder()
    .add(jyoti())
    .build();

  public static final TenantDto search(String id) {
    return TENANTS.stream().filter(tenant -> tenant.getId().equals(id)).findFirst().orElse(jyoti());
  }

  private static TenantDto jyoti() {

    LocalDate date = LocalDate.now();

    final String id = DEFAULT_TENANT;
    return TenantDto.builder()
      .id(id)
      .name("Jyoti Hospital")
      .uhidPrefix("JH" + date.getYear() + month(date.getMonthValue()))
      .domainName("jyotihospital." + DOMAIN)
      .logo(BASE_IMG_URL + id + "/logo.png")
      .banner(BASE_IMG_URL + id + "/banner.svg")
      .adminWebUrl("https://jyotihospital.admin." + DOMAIN)
      .authWebUrl("https://jyotihospital.auth." + DOMAIN)
      .theme(ColorPalette.builder()
        .primary(new ColorPalette.Color("#C5CAE9", "#2784FF", "#303F9F", "#FFFFFF"))
        .secondary(new ColorPalette.Color("#0066ff", "#dc004e", "", "#FFFFFF")).build())
      .contactInfo(ContactInfo.builder().addressLine1("28, Ganapati Enclave")
        .addressLine2("Jharsa Rd, Housing Board Colony").city("Gurugram").state("Haryana").country("India")
        .pincode("122001")
        .email("urocentreindia@gmail.com").contactNumber("0124 232 2673").build())
      .shortTitle("Jyoti Hospital & Urology Center")
      .aboutUs(
        "Jyoti hospital is not only a hospital but a landmark in Gurgaon. Prior to the commercialisation of healthcare in Gurgaon, Jyoti Hospital stood as on of the leading hospitals of the city with state of the art facilities. The hospital continues to provide genuine healthcare facilities to the community of Gurgaon with an experience of over 30 years backing it up.")
      .build();
  }

  private static String month(int monthValue) {
    return monthValue < 10 ? "0" + monthValue : monthValue + "";
  }

}
