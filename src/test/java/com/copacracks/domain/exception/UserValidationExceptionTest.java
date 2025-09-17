package com.copacracks.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UserValidationException")
class UserValidationExceptionTest {

	@Test
	@DisplayName("Deve criar exceção com mensagem")
	void shouldCreateExceptionWithMessage() {
		// Given
		String message = "Username inválido";

		// When
		UserValidationException exception = new UserValidationException(message);

		// Then
		assertThat(exception).isNotNull();
		assertThat(exception.getMessage()).isEqualTo(message);
	}

	@Test
	@DisplayName("Deve criar exceção com mensagem vazia")
	void shouldCreateExceptionWithEmptyMessage() {
		// Given
		String message = "";

		// When
		UserValidationException exception = new UserValidationException(message);

		// Then
		assertThat(exception).isNotNull();
		assertThat(exception.getMessage()).isEqualTo(message);
	}

	@Test
	@DisplayName("Deve criar exceção com mensagem nula")
	void shouldCreateExceptionWithNullMessage() {
		// Given
		String message = null;

		// When
		UserValidationException exception = new UserValidationException(message);

		// Then
		assertThat(exception).isNotNull();
		assertThat(exception.getMessage()).isNull();
	}

	@Test
	@DisplayName("Deve criar exceção com mensagem longa")
	void shouldCreateExceptionWithLongMessage() {
		// Given
		String message =
				"Esta é uma mensagem de erro muito longa que contém muitos detalhes sobre o que deu errado durante a validação do usuário";

		// When
		UserValidationException exception = new UserValidationException(message);

		// Then
		assertThat(exception).isNotNull();
		assertThat(exception.getMessage()).isEqualTo(message);
	}

	@Test
	@DisplayName("Deve criar exceção com mensagem contendo caracteres especiais")
	void shouldCreateExceptionWithSpecialCharacters() {
		// Given
		String message = "Erro: Username 'user@name' contém caracteres inválidos!";

		// When
		UserValidationException exception = new UserValidationException(message);

		// Then
		assertThat(exception).isNotNull();
		assertThat(exception.getMessage()).isEqualTo(message);
	}

	@Test
	@DisplayName("Deve criar exceção com mensagem contendo acentos")
	void shouldCreateExceptionWithAccents() {
		// Given
		String message = "Erro: Usuário com nome 'João' não encontrado";

		// When
		UserValidationException exception = new UserValidationException(message);

		// Then
		assertThat(exception).isNotNull();
		assertThat(exception.getMessage()).isEqualTo(message);
	}

	@Test
	@DisplayName("Deve criar exceção com mensagem contendo emojis")
	void shouldCreateExceptionWithEmojis() {
		// Given
		String message = "Erro: Username inválido 😀";

		// When
		UserValidationException exception = new UserValidationException(message);

		// Then
		assertThat(exception).isNotNull();
		assertThat(exception.getMessage()).isEqualTo(message);
	}

	@Test
	@DisplayName("Deve ser instância de RuntimeException")
	void shouldBeInstanceOfRuntimeException() {
		// Given
		String message = "Teste";

		// When
		UserValidationException exception = new UserValidationException(message);

		// Then
		assertThat(exception).isInstanceOf(RuntimeException.class);
	}

	@Test
	@DisplayName("Deve ser instância de Exception")
	void shouldBeInstanceOfException() {
		// Given
		String message = "Teste";

		// When
		UserValidationException exception = new UserValidationException(message);

		// Then
		assertThat(exception).isInstanceOf(Exception.class);
	}

	@Test
	@DisplayName("Deve ser instância de Throwable")
	void shouldBeInstanceOfThrowable() {
		// Given
		String message = "Teste";

		// When
		UserValidationException exception = new UserValidationException(message);

		// Then
		assertThat(exception).isInstanceOf(Throwable.class);
	}

	@Test
	@DisplayName("Deve ter nome de classe correto")
	void shouldHaveCorrectClassName() {
		// Given
		String message = "Teste";

		// When
		UserValidationException exception = new UserValidationException(message);

		// Then
		assertThat(exception.getClass().getSimpleName()).isEqualTo("UserValidationException");
	}

	@Test
	@DisplayName("Deve ter nome de classe completo correto")
	void shouldHaveCorrectFullClassName() {
		// Given
		String message = "Teste";

		// When
		UserValidationException exception = new UserValidationException(message);

		// Then
		assertThat(exception.getClass().getName())
				.isEqualTo("com.copacracks.domain.exception.UserValidationException");
	}
}
