package com.example.springwebauthn4j.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.springwebauthn4j.service.FidoCredentialService;
import com.example.springwebauthn4j.service.RegisterOption;
import com.example.springwebauthn4j.service.Status;
import com.example.springwebauthn4j.service.WebAuthnServerService;
import com.example.springwebauthn4j.util.SecurityContextUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

@RestController
public class Fido2RestController {
	
	private static final Logger logger = LoggerFactory.getLogger(Fido2RestController.class);
	
	private ObjectMapper mapper = new ObjectMapper();
	
	private final WebAuthnServerService webAuthnServerService;
	private final FidoCredentialService fidoCredentialService;
	
	public Fido2RestController(WebAuthnServerService webAuthnServerService,
	                           FidoCredentialService fidoCredentialService) {
		this.webAuthnServerService = webAuthnServerService;
		this.fidoCredentialService = fidoCredentialService;
	}
	
	@PostMapping("/register/option")
	public ServerPublicKeyCredentialCreationOptionsResponse registerOption(HttpSession session) {
		logger.info("/register/option");
		var user = SecurityContextUtil.getLoginUser();
		if (user == null) {
			return new ServerPublicKeyCredentialCreationOptionsResponse(Status.FAILED, "user not found");
		}
		
		try {
			var registerOption = webAuthnServerService.getRegisterOption(user.getUsername());
			session.setAttribute("registerOption", registerOption);
			var r = new ServerPublicKeyCredentialCreationOptionsResponse(registerOption);
			logger.info("/register/option: {}", mapper.writeValueAsString(r));
			return r;
		} catch (Exception e) {
			return new ServerPublicKeyCredentialCreationOptionsResponse(Status.FAILED, e.getMessage() == null ? "" : e.getMessage());
		}
	}
	
	@PostMapping("/register/verify")
	public ServerResponse registerVerify(@RequestBody String publicKeyCredentialCreateResultJson, HttpSession session) {
		var registerOption = (RegisterOption) session.getAttribute("registerOption");
		logger.info("/register/verify: {}, option: {}", publicKeyCredentialCreateResultJson, registerOption.getPublicKeyCredentialCreationOptions());
		if (registerOption == null) {
			return new ServerResponse(Status.FAILED, "registerOption not found");
		}
		
		var user = SecurityContextUtil.getLoginUser();
		if (user == null) {
			return new ServerPublicKeyCredentialCreationOptionsResponse(Status.FAILED, "user not found");
		}
		
		try {
			var attestationVerifyResult = webAuthnServerService.verifyRegisterAttestation(registerOption, publicKeyCredentialCreateResultJson);
			fidoCredentialService.save(user.getUsername(), attestationVerifyResult);
			return new ServerResponse(Status.OK, "");
		} catch (Exception e) {
			return new ServerResponse(Status.FAILED, e.getMessage() == null ? "" : e.getMessage());
		}
	}
	
	@PostMapping("/authenticate/option")
	public ServerPublicKeyCredentialGetOptionsResponse authenticateOption(HttpSession session) {
		try {
			var authenticateOption = webAuthnServerService.getAuthenticateOption();
			logger.info("/authenticate/option: {}", authenticateOption.getPublicKeyCredentialRequestOptions());
			session.setAttribute("authenticateOption", authenticateOption);
			return new ServerPublicKeyCredentialGetOptionsResponse(authenticateOption);
		} catch (Exception e) {
			return new ServerPublicKeyCredentialGetOptionsResponse(Status.FAILED, e.getMessage() == null ? "" : e.getMessage());
		}
	}
}