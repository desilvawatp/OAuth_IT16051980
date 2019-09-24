package com.sliit.mooc;

import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.services.drive.DriveScopes;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

@Service
public class OAuthService {

	Logger logger = LoggerFactory.getLogger(OAuthService.class);

	HttpTransport httpTransport = new NetHttpTransport();
	JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

	List<String> scope = Collections.singletonList(DriveScopes.DRIVE);

	private GoogleAuthorizationCodeFlow authorizationFlow;
	private FileDataStoreFactory dataStoreFactory;

	@Autowired
	ExternalPropConfig extPropConfig;

	public com.google.api.client.auth.oauth2.Credential credentials() throws IOException {

		return authorizationFlow.loadCredential(Constant.APP_NAME);

	}

	@PostConstruct
	public void authorizationPreConfiguration() throws IOException {

		InputStreamReader reader = new InputStreamReader(extPropConfig.getSecretKeyFilePath().getInputStream());
		dataStoreFactory = new FileDataStoreFactory(extPropConfig.getCredentialsPath().getFile());

		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, reader);

		authorizationFlow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientSecrets, scope)
				.setDataStoreFactory(dataStoreFactory).build();

	}

	public String userstatus() throws IOException {

		Credential credential = credentials();
		if (credential != null) {
			credential.refreshToken();
			return "SUCCESS";
		}
		return "FAIL";
	}

	public void discardUserSession(HttpServletRequest request) throws IOException {

		dataStoreFactory.getDataStore(extPropConfig.getCredentialsPath().getFilename()).clear();
	}

	public String authenticateUser() throws IOException {

		GoogleAuthorizationCodeRequestUrl url = authorizationFlow.newAuthorizationUrl();
		String redirectUrl = url.setRedirectUri(extPropConfig.getRedirectionURI()).setAccessType("offline").build();

		return redirectUrl;
	}

	public void tokenExchange(String CODE) throws IOException {

		GoogleTokenResponse tokenResponse = authorizationFlow.newTokenRequest(CODE)
				.setRedirectUri(extPropConfig.getRedirectionURI()).execute();
		authorizationFlow.createAndStoreCredential(tokenResponse, Constant.USER_SAMPLE_KEY);
	}

}
