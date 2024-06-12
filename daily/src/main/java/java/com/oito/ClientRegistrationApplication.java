package com.oito;

import java.io.File;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.oito.auth.common.Constants;
import com.oito.auth.mapper.ClientRequestMapper;
import com.oito.auth.service.ClientService;
import com.oito.auth.web.bean.ClientRequest;
import com.oito.common.json.JsonHandler;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
@ConditionalOnProperty(name = "action", havingValue = Constants.CLIENT_REGISTRATION)
public class ClientRegistrationApplication implements CommandLineRunner {

	@Autowired
	private ClientService clientService;

	@Autowired
	private ClientRequestMapper mapper;

	@Autowired
	private JsonHandler jsonHandler;

	public static void main(final String[] args) {
		SpringApplication.run(ClientRegistrationApplication.class, args).close();
	}

	@Override
	public void run(final String... args) throws Exception {
		final var requestPath = System.getProperty(Constants.REQUEST_FILE_PATH);
		final var jsonContent = Files.readString(new File(requestPath).toPath());
		final var clientRequest = jsonHandler.fromJSON(jsonContent, ClientRequest.class);
		final var clientTO = mapper.toVO(clientRequest);
		final var response = clientService.create(clientTO);
		log.info("ClientID:{}", response.getClientId());
		log.info("SecretKey:{}", response.getSecretKey());
		log.info("Done Client Creation");
	}

}
