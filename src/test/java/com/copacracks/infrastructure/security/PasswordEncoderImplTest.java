package com.copacracks.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PasswordEncoderImpl Tests")
public class PasswordEncoderImplTest {

	@Test
	@DisplayName("Should Be True When Hash Generated With Salt and Pepper Is Validated")
	public void shouldBeTrueWhenHashGeneratedWithSaltAndPepperIsValidated() {
		// Given
		final String plainPassword = "testSenha1234@";
		final String SECURITY_PEPPER = "security_pepper";

		// When
		PasswordEncoderImpl encoder = new PasswordEncoderImpl();

		final String hash = encoder.encode(plainPassword, SECURITY_PEPPER);
		final boolean verifyPw = encoder.verify(plainPassword, hash, SECURITY_PEPPER);

		// Then
		assertThat(verifyPw).isTrue();
	}
}
