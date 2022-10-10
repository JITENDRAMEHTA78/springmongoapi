package com.sixsprints.eclinic.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.activation.FileTypeMap;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.util.IOUtils;
import com.sixsprints.core.utils.RestUtil;
import com.sixsprints.eclinic.auth.Authenticated;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.service.CloudStorageService;
import com.sixsprints.eclinic.util.Constants;

@RestController
@RequestMapping(Constants.API_PREFIX + "/file")
public class FileController {

  @Autowired
  private CloudStorageService cloudStorage;

  @GetMapping
  public void serve(HttpServletResponse response, @RequestParam String key, @RequestParam String bucket,
    @RequestParam(required = false, defaultValue = "false") Boolean download)
    throws IOException {
    Path path = cloudStorage.download(key);
    File file = path.toFile();
    MediaType mediaType = MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(file));
    if (file.getName().endsWith("pdf")) {
      mediaType = MediaType.APPLICATION_PDF;
    }
    response.setContentType(mediaType.toString());
    response.setContentLengthLong(Files.size(path));
    if (download != null && download) {
      response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
    }
    IOUtils.copy(new FileInputStream(file), response.getOutputStream());
  }

  @PostMapping("/upload/secure")
  public ResponseEntity<?> uploadToCloud(@Authenticated User user,
    @RequestParam(value = "file", required = true) MultipartFile file, @RequestParam(required = false) String dir)
    throws IOException {
    return RestUtil.successResponse(cloudStorage.upload(file, dir));
  }

}
