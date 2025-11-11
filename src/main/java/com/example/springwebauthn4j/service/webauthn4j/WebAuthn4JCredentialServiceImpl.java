package com.example.springwebauthn4j.service.webauthn4j;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.example.springwebauthn4j.repository.MfidoCredentialRepository;
import com.example.springwebauthn4j.repository.MfidoCredentialforWebAuthn4J;
import com.example.springwebauthn4j.repository.MuserRepository;
import com.example.springwebauthn4j.service.AttestationVerifyResult;
import com.example.springwebauthn4j.service.FidoCredentialService;
import com.webauthn4j.converter.AttestedCredentialDataConverter;
import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.credential.CredentialRecord;
import com.webauthn4j.credential.CredentialRecordImpl;
import com.webauthn4j.data.attestation.statement.NoneAttestationStatement;
import com.webauthn4j.data.extension.authenticator.AuthenticationExtensionsAuthenticatorOutputs;

@Service
public class WebAuthn4JCredentialServiceImpl implements FidoCredentialService {
	
	private final MuserRepository mUserRepository;
	private final MfidoCredentialRepository mFidoCredentialRepository;
	
	public WebAuthn4JCredentialServiceImpl(MuserRepository mUserRepository, MfidoCredentialRepository mFidoCredentialRepository) {
		this.mUserRepository = mUserRepository;
		this.mFidoCredentialRepository = mFidoCredentialRepository;
	}
	
	@Override
	public void save(String userId, AttestationVerifyResult attestationVerifyResult) {
		var mUser = mUserRepository.findByUserId(userId);
		if (mUser == null) {
			throw new RuntimeException("User not found");
		}
		
		ObjectConverter objectConverter = new ObjectConverter();
		AttestedCredentialDataConverter attestedCredentialDataConverter = new AttestedCredentialDataConverter(objectConverter);
		
		byte[] serializedAttestedCredentialData = attestedCredentialDataConverter.convert(attestationVerifyResult.getCredentialRecord().getAttestedCredentialData());
		
		MfidoCredentialforWebAuthn4J entity = new MfidoCredentialforWebAuthn4J(0, mUser.getInternalId(), attestationVerifyResult.getCredentialId(), attestationVerifyResult.getCredentialRecord().getCounter(), serializedAttestedCredentialData);
		
		mFidoCredentialRepository.save(entity);
	}
	
	@Override
	public Pair<CredentialRecord, String> load(String userInternalId, byte[] credentialId) {
		List<MfidoCredentialforWebAuthn4J> entityList = mFidoCredentialRepository.findByUserInternalId(userInternalId);
		Optional<MfidoCredentialforWebAuthn4J> opt = entityList.stream().filter(e -> Arrays.equals(e.getCredentialId(), credentialId)).findFirst();
		
		if (opt.isEmpty()) {
			return Pair.of(null, null);
		}
		
		MfidoCredentialforWebAuthn4J mFidoCredential = opt.get();
		var mUser = mUserRepository.findByInternalId(mFidoCredential.getUserInternalId());
		if (mUser == null) {
			return Pair.of(null, null);
		}
		
		ObjectConverter objectConverter = new ObjectConverter();
		AttestedCredentialDataConverter attestedCredentialDataConverter = new AttestedCredentialDataConverter(objectConverter);
		
		var deserializedAttestedCredentialData = attestedCredentialDataConverter.convert(mFidoCredential.getAteestedCredentialData());
		
		CredentialRecord credentialRecord = new CredentialRecordImpl(new NoneAttestationStatement(), null, null, null, mFidoCredential.getSignCount(), deserializedAttestedCredentialData, new AuthenticationExtensionsAuthenticatorOutputs<>(), null, null, null);
		
		return Pair.of(credentialRecord, mUser.getUserId());
	}
}