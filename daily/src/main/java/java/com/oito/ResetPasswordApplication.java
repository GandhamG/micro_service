package com.oito;

import java.io.File;
import java.nio.file.Files;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.oito.auth.common.Constants;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.auth.service.UserService;
import com.oito.common.exception.ApiException;
import com.oito.common.json.JsonHandler;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility to generate reset password links for given list of users in a file
 * and send emails.
 *
 *
 * Usage: java -cp build/libs/authentication-service-1.0.0.jar
 * -Dspring.profiles.active=dev -Daction=RESET_PASSWORD
 * -Dspring.main.web-application-type=none -Deureka.client.enabled=false
 * -DrequestFilePath="/home/sujeesh/Sujeesh/Docs/Workspace/SCG/BIG
 * TH/reset-password/request.json"
 * -Dloader.main=com.oito.ResetPasswordApplication
 * org.springframework.boot.loader.PropertiesLauncher
 *
 * @author sujeesh
 *
 */
@SpringBootApplication
@Slf4j
@ConditionalOnProperty(name = "action", havingValue = Constants.RESET_PASSWORD_ACTION)
public class ResetPasswordApplication implements CommandLineRunner {

	@Getter
	@Setter
	@ToString
	public static class BulkResetPasswordRequest {
		private String userInfofilePath;
		private String emailSubjectKey;
		private String emailTemplate;
		private String smsTemplate;
		private String locale;
		private String resetLinkPrefix;
		private String userContext;
	}

	@Autowired
	private UserService userService;

	@Autowired
	private JsonHandler jsonHandler;

	public static void main(final String[] args) {
		SpringApplication.run(ResetPasswordApplication.class, args).close();
	}

	@Override
	public void run(final String... args) throws Exception {
		final var requestFilePath = System.getProperty(Constants.REQUEST_FILE_PATH);
		final var jsonContent = Files.readString(new File(requestFilePath).toPath());
		final var request = jsonHandler.fromJSON(jsonContent, BulkResetPasswordRequest.class);
		validate(request);
		final var emailList = Files.readAllLines(new File(request.getUserInfofilePath()).toPath());
		for (var i = 0; i < emailList.size(); i++) {
			final var userIdentifier = emailList.get(i);
			log.info("Processing user {} count {} of {}", userIdentifier, Integer.valueOf(i + 1),
					Integer.valueOf(emailList.size()));
			userService.resetPasswordAndNotify(userIdentifier, request);
		}
		log.info("Done executing reset password utility");
	}

	private void validate(final BulkResetPasswordRequest request) {
		log.info("Request {}", request);
		if (StringUtils.isAnyBlank(request.getUserInfofilePath(), request.getEmailSubjectKey(),
				request.getEmailTemplate(), request.getSmsTemplate(), request.getLocale(),
				request.getResetLinkPrefix())) {
			throw new ApiException(AuthErrorCode.MISSING_PARAMS);
		}
	}

}
