package com.example.springwebauthn4j;

import java.io.Serial;
import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class AssertionAuthenticationToken extends AbstractAuthenticationToken {
	
	@Serial
	private static final long serialVersionUID = -838535031524726754L;
	
	private final User principal;
	private final Fido2Credentials credentials;
	
	public AssertionAuthenticationToken(User principal, Fido2Credentials credentials, Collection<SimpleGrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		this.credentials = credentials;
	}
	
	@Override
	public Object getPrincipal() {
		return principal;
	}
	
	@Override
	public Object getCredentials() {
		return credentials;
	}
	
	public static class Fido2Credentials {
		private final String publicKeyCredentialGetResultJson;
		
		public Fido2Credentials(String publicKeyCredentialGetResultJson) {
			this.publicKeyCredentialGetResultJson = publicKeyCredentialGetResultJson;
		}
		
		public String getPublicKeyCredentialGetResultJson() {
			return publicKeyCredentialGetResultJson;
		}
	}
}