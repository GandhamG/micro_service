package com.oito;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.KeyGenerator;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.oito.auth.common.Constants;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
@ConditionalOnProperty(name = "action", havingValue = Constants.TOKEN_GENERATION)
public class TokenGenerationApplication implements CommandLineRunner {

	public static void main(final String[] args) {
		SpringApplication.run(TokenGenerationApplication.class, args).close();
	}

	@Override
	public void run(final String... args) throws Exception {
		final var keyGenerator = KeyGenerator.getInstance("AES");
		final var secureRandom = new SecureRandom();
		final var keyBitSize = 256;

		keyGenerator.init(keyBitSize, secureRandom);
		final var secretKey = keyGenerator.generateKey();
		final var encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
		log.info("#####################Key is: {}", encodedKey);
	}

}
