package com.example.springwebauthn4j.service.webauthn4j;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.example.springwebauthn4j.repository.MfidoCredentialRepository;
import com.example.springwebauthn4j.repository.MuserRepository;
import com.example.springwebauthn4j.service.AssertionVerifyResult;
import com.example.springwebauthn4j.service.AttestationVerifyResult;
import com.example.springwebauthn4j.service.AuthenticateOption;
import com.example.springwebauthn4j.service.FidoCredentialService;
import com.example.springwebauthn4j.service.RegisterOption;
import com.example.springwebauthn4j.service.WebAuthnServerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.converter.exception.DataConversionException;
import com.webauthn4j.credential.CredentialRecord;
import com.webauthn4j.credential.CredentialRecordImpl;
import com.webauthn4j.data.AttestationConveyancePreference;
import com.webauthn4j.data.AuthenticationData;
import com.webauthn4j.data.AuthenticationParameters;
import com.webauthn4j.data.AuthenticationRequest;
import com.webauthn4j.data.AuthenticatorSelectionCriteria;
import com.webauthn4j.data.PublicKeyCredentialCreationOptions;
import com.webauthn4j.data.PublicKeyCredentialDescriptor;
import com.webauthn4j.data.PublicKeyCredentialParameters;
import com.webauthn4j.data.PublicKeyCredentialRequestOptions;
import com.webauthn4j.data.PublicKeyCredentialRpEntity;
import com.webauthn4j.data.PublicKeyCredentialType;
import com.webauthn4j.data.PublicKeyCredentialUserEntity;
import com.webauthn4j.data.RegistrationData;
import com.webauthn4j.data.RegistrationParameters;
import com.webauthn4j.data.RegistrationRequest;
import com.webauthn4j.data.UserVerificationRequirement;
import com.webauthn4j.data.attestation.statement.COSEAlgorithmIdentifier;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.data.extension.client.AuthenticationExtensionsClientOutputs;
import com.webauthn4j.data.extension.client.ExtensionClientOutput;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.validator.exception.ValidationException;

@Service
public class WebAuthn4JServerServiceImpl implements WebAuthnServerService {
	
	private final MuserRepository mUserRepository;
	private final FidoCredentialService mFidoCredentialService;
	private final MfidoCredentialRepository mFidoCredentialRepository;
	
	private final PublicKeyCredentialRpEntity rp = new PublicKeyCredentialRpEntity("localhost", "webauthn4j-test");
	
	private final Origin origin = Origin.create("http://localhost:8080");
	
	public WebAuthn4JServerServiceImpl(MuserRepository mUserRepository, FidoCredentialService mFidoCredentialService, MfidoCredentialRepository mFidoCredentialRepository) {
		this.mUserRepository = mUserRepository;
		this.mFidoCredentialService = mFidoCredentialService;
		this.mFidoCredentialRepository = mFidoCredentialRepository;
	}
	
	@Override
	public RegisterOption getRegisterOption(String userId) {
		var mUser = mUserRepository.findByUserId(userId);
		if (mUser == null) {
			throw new RuntimeException("User not found");
		}
		
		DefaultChallenge challenge = new DefaultChallenge();
		
		PublicKeyCredentialUserEntity userInfo = new PublicKeyCredentialUserEntity(createUserId(mUser.getInternalId()), userId, mUser.getDisplayName());
		
		List<PublicKeyCredentialParameters> pubKeyCredParams = List.of(new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.ES256), new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.RS256));
		
		List<PublicKeyCredentialDescriptor> excludeCredentials = mFidoCredentialRepository.findByUserInternalId(mUser.getInternalId()).stream().map(credential -> new PublicKeyCredentialDescriptor(PublicKeyCredentialType.PUBLIC_KEY, credential.getCredentialId(), null)).collect(Collectors.toList());
		
		AuthenticatorSelectionCriteria authenticatorSelectionCriteria = new AuthenticatorSelectionCriteria(null, true, UserVerificationRequirement.REQUIRED);
		
		AttestationConveyancePreference attestation = AttestationConveyancePreference.NONE;
		
		PublicKeyCredentialCreationOptions option = new PublicKeyCredentialCreationOptions(rp, userInfo, challenge, pubKeyCredParams, TimeUnit.SECONDS.toMillis(60), excludeCredentials, authenticatorSelectionCriteria, attestation, null);
		
		return new RegisterOption(option);
	}
	
	@Override
	public AttestationVerifyResult verifyRegisterAttestation(RegisterOption registerOption, String publicKeyCredentialCreateResultJson) {
		RegistrationData registrationData = createRegistrationData(publicKeyCredentialCreateResultJson);
		RegistrationParameters registrationParameters = createRegistrationParameters(registerOption);
		
		try {
			WebAuthnManager.createNonStrictWebAuthnManager().validate(registrationData, registrationParameters);
		} catch (ValidationException e) {
			throw e;
		}
		
		CredentialRecord credentialRecord = new CredentialRecordImpl(registrationData.getAttestationObject(), registrationData.getCollectedClientData(), registrationData.getClientExtensions(), registrationData.getTransports());
		
		byte[] credentialId = registrationData.getAttestationObject().getAuthenticatorData().getAttestedCredentialData().getCredentialId();
		
		return new AttestationVerifyResult(credentialId, credentialRecord);
	}
	
	private RegistrationData createRegistrationData(String publicKeyCredentialCreateResultJson) {
		PublicKeyCredentialCreateResult pkc = PublicKeyCredentialCreateResultBuilder.build(publicKeyCredentialCreateResultJson);
		if (pkc.response == null) {
			throw new RuntimeException("response is null");
		}
		
		Base64.Decoder decoder = Base64.getUrlDecoder();
		byte[] attestationObject = decoder.decode(pkc.response.attestationObject);
		byte[] clientDataJSON = decoder.decode(pkc.response.clientDataJSON);
		
		RegistrationRequest registrationRequest = new RegistrationRequest(attestationObject, clientDataJSON, null, pkc.response.transports);
		
		try {
			return WebAuthnManager.createNonStrictWebAuthnManager().parse(registrationRequest);
		} catch (DataConversionException e) {
			throw e;
		}
	}
	
	private RegistrationParameters createRegistrationParameters(RegisterOption registerOption) {
		DefaultChallenge challenge = new DefaultChallenge(registerOption.getPublicKeyCredentialCreationOptions().getChallenge().toString());
		
		ServerProperty serverProperty = new ServerProperty(origin, rp.getId(), challenge, null);
		
		List<PublicKeyCredentialParameters> pubKeyCredParams = null;
		boolean userVerificationRequired = true;
		
		return new RegistrationParameters(serverProperty, pubKeyCredParams, userVerificationRequired);
	}
	
	@Override
	public AuthenticateOption getAuthenticateOption() {
		DefaultChallenge challenge = new DefaultChallenge();
		
		PublicKeyCredentialRequestOptions options = new PublicKeyCredentialRequestOptions(challenge, TimeUnit.SECONDS.toMillis(60), rp.getId(), null, UserVerificationRequirement.REQUIRED, null);
		
		return new AuthenticateOption(options);
	}
	
	@Override
	public AssertionVerifyResult verifyAuthenticateAssertion(AuthenticateOption authenticateOption, String publicKeyCredentialGetResultJson) {
		AuthenticationData authenticationData = createAuthenticationData(publicKeyCredentialGetResultJson);
		
		String userInternalId = new String(authenticationData.getUserHandle(), StandardCharsets.UTF_8);
		Pair<CredentialRecord, String> pair = mFidoCredentialService.load(userInternalId, authenticationData.getCredentialId());
		CredentialRecord credentialRecord = pair == null ? null : pair.getFirst();
		String userId = pair == null ? null : pair.getSecond();
		
		if (credentialRecord == null || userId == null || userId.isEmpty()) {
			return new AssertionVerifyResult(false, "");
		}
		
		AuthenticationParameters authenticationParameters = createAuthenticationParameters(authenticateOption, credentialRecord);
		
		try {
			WebAuthnManager.createNonStrictWebAuthnManager().validate(authenticationData, authenticationParameters);
		} catch (ValidationException e) {
			throw e;
		}
		
		return new AssertionVerifyResult(true, userId);
	}
	
	private AuthenticationData createAuthenticationData(String publicKeyCredentialGetResultJson) {
		PublicKeyCredentialGetResult pkc = PublicKeyCredentialGetResultBuilder.build(publicKeyCredentialGetResultJson);
		
		Base64.Decoder decoder = Base64.getUrlDecoder();
		byte[] credentialId = decoder.decode(pkc.id);
		byte[] userHandle = decoder.decode(pkc.response.userHandle);
		byte[] authenticatorData = decoder.decode(pkc.response.authenticatorData);
		byte[] clientDataJSON = decoder.decode(pkc.response.clientDataJSON);
		byte[] signature = decoder.decode(pkc.response.signature);
		
		AuthenticationRequest authenticationRequest = new AuthenticationRequest(credentialId, userHandle, authenticatorData, clientDataJSON, null, signature);
		
		try {
			return WebAuthnManager.createNonStrictWebAuthnManager().parse(authenticationRequest);
		} catch (DataConversionException e) {
			throw e;
		}
	}
	
	private AuthenticationParameters createAuthenticationParameters(AuthenticateOption authenticateOption, CredentialRecord credentialRecord) {
		DefaultChallenge challenge = new DefaultChallenge(authenticateOption.getPublicKeyCredentialRequestOptions().getChallenge().toString());
		
		ServerProperty serverProperty = new ServerProperty(origin, rp.getId(), challenge, null);
		
		return new AuthenticationParameters(serverProperty, credentialRecord, null, true, true);
	}
	
	private byte[] createUserId(String userId) {
		return userId.getBytes(StandardCharsets.UTF_8);
	}
	
	@Override
	public String toUserInternalId(String encodedUserHandle) {
		Base64.Decoder decoder = Base64.getUrlDecoder();
		byte[] userHandle = decoder.decode(encodedUserHandle);
		return new String(userHandle, StandardCharsets.UTF_8);
	}
	
	// --- Builders / DTOs used for JSON parsing ---
	public static class PublicKeyCredentialCreateResultBuilder {
		public static PublicKeyCredentialCreateResult build(String json) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				return mapper.readValue(json, PublicKeyCredentialCreateResult.class);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static class PublicKeyCredentialCreateResult {
		public String id = "";
		public AuthenticatorAttestationResponse response = null;
		public AuthenticationExtensionsClientOutputs<ExtensionClientOutput> clientExtensionResults = null;
		public String type = "";
		
		public static class AuthenticatorAttestationResponse {
			public String attestationObject = "";
			public String clientDataJSON = "";
			public Set<String> transports = null;
		}
	}
	
	public static class PublicKeyCredentialGetResultBuilder {
		public static PublicKeyCredentialGetResult build(String json) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				return mapper.readValue(json, PublicKeyCredentialGetResult.class);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static class PublicKeyCredentialGetResult {
		public String id = "";
		public AuthenticatorAssertionResponse response = null;
		public AuthenticationExtensionsClientOutputs<ExtensionClientOutput> clientExtensionResults = null;
		public String type = null;
		
		public static class AuthenticatorAssertionResponse {
			public String userHandle = "";
			public String authenticatorData = "";
			public String clientDataJSON = "";
			public String signature = "";
		}
	}
}