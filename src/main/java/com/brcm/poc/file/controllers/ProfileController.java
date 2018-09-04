package com.brcm.poc.file.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/supportlink/profile")

public class ProfileController {

	@GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getProfile() {
		ResponseEntity<String> resEntity = null;

		try {
			String prfData = getProfileDataStr();
			resEntity = new ResponseEntity<>(prfData, HttpStatus.OK);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
			resEntity = new ResponseEntity<>("Fail to get profile Data", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resEntity;
	}

	private String getProfileDataStr() throws URISyntaxException, IOException {
		StringBuilder contentBuilder = new StringBuilder();

		try (Stream<String> stream = Files.lines(Paths.get(this.getClass().getResource("/profile.json").toURI()),
				StandardCharsets.UTF_8)) {
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		}

		return contentBuilder.toString();
	}

}
