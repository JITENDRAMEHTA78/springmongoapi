package com.sixsprints.eclinic.service.impl;

import static java.util.UUID.randomUUID;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sixsprints.cloudservice.dto.FileDto;
import com.sixsprints.eclinic.api.PdfApi;
import com.sixsprints.eclinic.service.CloudStorageService;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

@Service
public class PdfService {

  @Autowired
  private PdfApi pdfApi;

  @Autowired
  private CloudStorageService cloudStorageService;

  public String htmlToPdf(String html, String dir, String patientName) throws IOException {
    File file = fileDtoToFile(FileDto.builder().bytes(html.getBytes()).build());
    String margin = "0";
    RequestBody requestMargin = RequestBody.create(okhttp3.MultipartBody.FORM, margin);
    RequestBody requestFile = RequestBody.create(MediaType.parse("text/html"), file);
    MultipartBody.Part body = MultipartBody.Part.createFormData("files", "index.html", requestFile);
    Call<ResponseBody> call = pdfApi.upload(body, requestMargin, requestMargin, requestMargin, requestMargin);
    Response<ResponseBody> response = call.execute();
    dir = dir.endsWith("/") ? dir : dir + "/";
    FileDto fileDto = FileDto.builder().fileName(dir + patientName.replace(" ", "_") + ".pdf")
      .bytes(response.body().bytes()).build();
    return cloudStorageService.upload(fileDto);
  }

  protected Path createTempFile(String key, String dir) throws IOException {
    Path path = Paths.get(dir, randomUUID().toString());
    Files.createDirectories(path);
    return Paths.get(path.toAbsolutePath().toString(), key.replaceAll("/", "-"));
  }

  private File fileDtoToFile(FileDto fileDto) {
    if (fileDto.getFileToUpload() != null) {
      return fileDto.getFileToUpload();
    }
    try {
      String fileName = StringUtils.isEmpty(fileDto.getFileName()) ? "index.html"
        : fileDto.getFileName();

      return Files
        .write(createTempFile(randomUUID().toString() + fileName,
          Files.createTempDirectory(null, new FileAttribute<?>[0]).toAbsolutePath().toString()), fileDto.getBytes())
        .toFile();
    } catch (Exception ex) {
      throw new IllegalArgumentException(ex.getMessage(), ex);
    }
  }

}
