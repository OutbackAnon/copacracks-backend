package com.copacracks.application.usecases.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.copacracks.application.dto.NewUserDto;
import com.copacracks.application.security.PasswordHasher;
import com.copacracks.domain.exception.UserValidationException;
import com.copacracks.domain.model.user.User;
import com.copacracks.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateUserCaseImpl Tests")
class CreateUserCaseImplTest {

	private static final String VALID_USERNAME = "john_doe";
	private static final String VALID_PASSWORD = "SecurePass123!";
	private static final String VALID_EMAIL = "john@example.com";
	private static final String HASHED_PASSWORD = "$2a$10$hashedPasswordExample123";

	@Mock
    private UserRepository userRepository;

	@Mock
    private PasswordHasher passwordHasher;

	private CreateUserCaseImpl createUserCase;

	@BeforeEach
	void setUp() {
		createUserCase = new CreateUserCaseImpl(userRepository, passwordHasher);
	}

	@Test
	@DisplayName("Deve criar usuário com sucesso quando dados válidos são fornecidos")
	void shouldCreateUserSuccessfully() {
		// Given
		final NewUserDto requestDto = new NewUserDto(VALID_USERNAME, VALID_PASSWORD, VALID_EMAIL);

		when(passwordHasher.createHash(VALID_PASSWORD)).thenReturn(HASHED_PASSWORD);

		// When
		createUserCase.execute(requestDto);

		// Then
		final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(passwordHasher, times(1)).createHash(VALID_PASSWORD);
		verify(userRepository, times(1)).save(userCaptor.capture());

		final User capturedUser = userCaptor.getValue();
		assertThat(capturedUser).as("Usuário capturado não deve ser nulo").isNotNull();
		assertThat(capturedUser.getUsername())
				.as("Username deve ser preservado")
				.isEqualTo(VALID_USERNAME);
		assertThat(capturedUser.getEmail()).as("Email deve ser preservado").isEqualTo(VALID_EMAIL);
		assertThat(capturedUser.getPassword())
				.as("Senha hasheada deve ser definida")
				.isEqualTo(HASHED_PASSWORD);
		assertThat(capturedUser.isNew()).as("Usuário deve ser marcado como novo").isTrue();
	}

	@Test
	@DisplayName("Deve lançar UserValidationException quando username é inválido")
	void shouldThrowExceptionWhenUsernameIsInvalid() {
		// Given
		final NewUserDto userDto = new NewUserDto("ab", VALID_PASSWORD, VALID_EMAIL);

		// When & Then
		assertThatThrownBy(() -> createUserCase.execute(userDto))
				.as("Deve lançar UserValidationException para username inválido")
				.isInstanceOf(UserValidationException.class);

		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	@DisplayName("Deve lançar UserValidationException quando email é inválido")
	void shouldThrowExceptionWhenEmailIsInvalid() {
		// Given
		final NewUserDto newUserDto = new NewUserDto(VALID_USERNAME, VALID_PASSWORD, "email-invalido");

		// When & Then
		assertThatThrownBy(() -> createUserCase.execute(newUserDto))
				.as("Deve lançar UserValidationException para email inválido")
				.isInstanceOf(UserValidationException.class);

		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	@DisplayName("Deve lançar UserValidationException quando password é inválido")
	void shouldThrowExceptionWhenPasswordIsInvalid() {
		// Given
		final NewUserDto userDto = new NewUserDto(VALID_USERNAME, "weak", VALID_EMAIL); // senha fraca

		// When & Then
		assertThatThrownBy(() -> createUserCase.execute(userDto))
				.as("Deve lançar UserValidationException para password inválido")
				.isInstanceOf(UserValidationException.class);

		verify(passwordHasher, never()).createHash(anyString());
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	@DisplayName("Deve lançar UserValidationException quando username é nulo")
	void shouldThrowExceptionWhenUsernameIsNull() {
		// Given
		final NewUserDto userDto = new NewUserDto(null, VALID_PASSWORD, VALID_EMAIL);

		// When & Then
		assertThatThrownBy(() -> createUserCase.execute(userDto))
				.as("Deve lançar UserValidationException para username nulo")
				.isInstanceOf(UserValidationException.class);

		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	@DisplayName("Deve lançar UserValidationException quando email é nulo")
	void shouldThrowExceptionWhenEmailIsNull() {
		// Given
		final NewUserDto userDto = new NewUserDto(VALID_USERNAME, VALID_PASSWORD, null);

		// When & Then
		assertThatThrownBy(() -> createUserCase.execute(userDto))
				.as("Deve lançar UserValidationException para email nulo")
				.isInstanceOf(UserValidationException.class);

		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	@DisplayName("Deve lançar UserValidationException quando password é nulo")
	void shouldThrowExceptionWhenPasswordIsNull() {
		// Given
		final NewUserDto userDto = new NewUserDto(VALID_USERNAME, null, VALID_EMAIL);

		// When & Then
		assertThatThrownBy(() -> createUserCase.execute(userDto))
				.as("Deve lançar UserValidationException para password nulo")
				.isInstanceOf(UserValidationException.class);

		verify(passwordHasher, never()).createHash(anyString());
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	@DisplayName("Deve propagar exceção quando PasswordHasher falha")
	void shouldPropagateExceptionWhenPasswordHasherFails() {
		// Given
		final NewUserDto userDto = new NewUserDto(VALID_USERNAME, VALID_PASSWORD, VALID_EMAIL);

		final RuntimeException hashingException = new RuntimeException("Erro no hash da senha");
		when(passwordHasher.createHash(VALID_PASSWORD)).thenThrow(hashingException);

		// When & Then
		assertThatThrownBy(() -> createUserCase.execute(userDto))
				.as("Deve propagar exceção do PasswordHasher")
				.isInstanceOf(RuntimeException.class)
				.hasMessage("Erro no hash da senha");

		verify(passwordHasher, times(1)).createHash(VALID_PASSWORD);
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	@DisplayName("Deve propagar exceção quando UserRepository falha")
	void shouldPropagateExceptionWhenUserRepositoryFails() {
		// Given
		final NewUserDto userDto = new NewUserDto(VALID_USERNAME, VALID_PASSWORD, VALID_EMAIL);

		when(passwordHasher.createHash(VALID_PASSWORD)).thenReturn(HASHED_PASSWORD);
		final RuntimeException repositoryException = new RuntimeException("Erro no repositório");
		doThrow(repositoryException).when(userRepository).save(any(User.class));

		// When & Then
		assertThatThrownBy(() -> createUserCase.execute(userDto))
				.as("Deve propagar exceção do UserRepository")
				.isInstanceOf(RuntimeException.class)
				.hasMessage("Erro no repositório");

		verify(passwordHasher, times(1)).createHash(VALID_PASSWORD);
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	@DisplayName("Deve criar usuário com dados mínimos válidos")
	void shouldCreateUserWithMinimalValidData() {
		// Given
		final String minimalUsername = "abc";
		final String minimalEmail = "a@b.co";
		final String minimalPassword = "Pass1!@740f";

		final NewUserDto userDto = new NewUserDto(minimalUsername, minimalPassword, minimalEmail);

		when(passwordHasher.createHash(minimalPassword)).thenReturn(HASHED_PASSWORD);

		// When
		createUserCase.execute(userDto);

		// Then
		final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository, times(1)).save(userCaptor.capture());

		final User capturedUser = userCaptor.getValue();
		assertThat(capturedUser.getUsername()).isEqualTo(minimalUsername);
		assertThat(capturedUser.getEmail()).isEqualTo(minimalEmail);
		assertThat(capturedUser.getPassword()).isEqualTo(HASHED_PASSWORD);
	}

	@Test
	@DisplayName("Deve verificar se hash da senha é chamado com a senha correta")
	void shouldCallPasswordHasherWithCorrectPassword() {
		// Given
		final NewUserDto userDto = new NewUserDto(VALID_USERNAME, VALID_PASSWORD, VALID_EMAIL);

		when(passwordHasher.createHash(VALID_PASSWORD)).thenReturn(HASHED_PASSWORD);

		// When
		createUserCase.execute(userDto);

		// Then
		verify(passwordHasher, times(1)).createHash(eq(VALID_PASSWORD));
	}

	@Test
	@DisplayName("Deve preservar todos os dados do DTO na criação do usuário")
	void shouldPreserveAllDtoDataInUserCreation() {
		// Given
		final String specificUsername = "specific_user";
		final String specificEmail = "specific@test.com";
		final String specificPassword = "SpecificPass123!";

		final NewUserDto userDto = new NewUserDto(specificUsername, specificPassword, specificEmail);

		when(passwordHasher.createHash(specificPassword)).thenReturn(HASHED_PASSWORD);

		// When
		createUserCase.execute(userDto);

		// Then
		final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository, times(1)).save(userCaptor.capture());

		final User capturedUser = userCaptor.getValue();
		assertThat(capturedUser.getUsername())
				.as("Username específico deve ser preservado")
				.isEqualTo(specificUsername);
		assertThat(capturedUser.getEmail())
				.as("Email específico deve ser preservado")
				.isEqualTo(specificEmail);
		assertThat(capturedUser.getPassword())
				.as("Hash da senha deve ser aplicado corretamente")
				.isEqualTo(HASHED_PASSWORD);
		assertThat(capturedUser.getCreateAt())
				.as("Data de criação deve ser definida automaticamente")
				.isNotNull();
	}
}
