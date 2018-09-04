package com.brcm.poc.file.controllers;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/supportlink/inventory")

public class FileUploadController {

	@PutMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
			@RequestHeader(value = "User-Agent") String userAgent) {
		ResponseEntity<String> resEntity = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getName();
		System.out.println(currentPrincipalName);
		System.out.println(userAgent);
		if (file != null) {
			
			String dateTimeFormatPattern = "yyyyMMddHHmmss";
			
			final DateTimeFormatter formatter =
				      DateTimeFormatter.ofPattern(dateTimeFormatPattern);
			
			Date date = new Date();
			LocalDateTime lt = LocalDateTime.now();
			System.out.println(lt.getYear()+": "+ lt.getMonth().name()+": "+formatter.format(lt));
					
					
					

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM");
			String mon = simpleDateFormat.format(date).toUpperCase();

			simpleDateFormat = new SimpleDateFormat("yyyy");
			String yr = "YEAR_" + simpleDateFormat.format(date).toUpperCase();

			simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String filePath = "//Users//vg950772//POC//" + currentPrincipalName + "//" + userAgent + "//" + yr + "//"
					+ mon + "//" + file.getOriginalFilename() + "_" + simpleDateFormat.format(date);
			try {

				File testFile = new File(filePath);
				FileUtils.writeByteArrayToFile(testFile, file.getBytes());
				resEntity = new ResponseEntity<>("Done", HttpStatus.OK);
			} catch (IOException e) {
				e.printStackTrace();
				resEntity = new ResponseEntity<>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			resEntity = new ResponseEntity<>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resEntity;
	}

}
	


