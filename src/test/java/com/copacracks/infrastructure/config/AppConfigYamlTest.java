package com.copacracks.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.copacracks.infrastructure.exception.ConfigurationException;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("AppConfigYaml Tests")
public class AppConfigYamlTest {

	@Nested
	@DisplayName("When loading a valid YAML file")
	class ValidYamlTests {

		@Test
		@DisplayName("It should load all database configurations correctly")
		void shouldLoadDatabaseConfigurationsSuccessfully() {
			// Arrange & Act
			AppConfigYaml config = AppConfigYaml.load("configs/local-config.yaml");

			// Assert
			assertThat(config).as("Config object should not be null").isNotNull();

			assertThat(config.getDatabase())
					.as("Database configuration should be loaded")
					.isNotNull()
					.satisfies(
							db -> {
								assertThat(db.driver())
										.as("Driver should match YAML value")
										.isEqualTo("org.h2.Driver");

								assertThat(db.maximumPoolSize()).as("Maximum pool size should be 20").isEqualTo(20);

								assertThat(db.minimumIdle())
										.as("Minimum idle should be 5")
										.isEqualTo(5)
										.isPositive()
										.isLessThan(db.maximumPoolSize());

								assertThat(db.connectionTimeout())
										.as("Connection timeout should be 30000ms (30 seconds)")
										.isEqualTo(30000)
										.isPositive();

								assertThat(db.idleTimeout())
										.as("Idle timeout should be 600000ms (10 minutes)")
										.isEqualTo(600000)
										.isPositive();

								assertThat(db.maxLifetime())
										.as("Max lifetime should be 1800000ms (30 minutes)")
										.isEqualTo(1800000)
										.isGreaterThan(db.idleTimeout());
							});
		}

		@Test
		@DisplayName("It should load Flyway configurations correctly")
		void shouldLoadFlywayConfigurationsSuccessfully() {
			// Arrange & Act
			AppConfigYaml config = AppConfigYaml.load("configs/local-config.yaml");

			assertThat(config.getDatabase())
					.isNotNull()
					.extracting(AppConfigYaml.Database::flyway)
					.isNotNull()
					.satisfies(
							flyway -> {
								assertThat(flyway.cleanMigrationOnStart())
										.as("cleanMigrationOnStart should be true for testing")
										.isTrue();

								assertThat(flyway.cleanOnDisabled())
										.as("cleanOnDisabled should be false in production")
										.isFalse();
							});
		}

		@Test
		@DisplayName("It should load Server configurations correctly")
		void shouldLoadServerConfigurationsSuccessfully() {
			// Arrange & Act
			AppConfigYaml config = AppConfigYaml.load("configs/local-config.yaml");

			assertThat(config.getDatabase())
					.isNotNull()
					.extracting(AppConfigYaml.Database::server)
					.isNotNull()
					.satisfies(
							server -> {
								assertThat(server.port())
										.as("Server port should be 8082")
										.isEqualTo("8082")
										.isNotEmpty()
										.matches("\\d+");
							});
		}

		@Test
		@DisplayName("It should load the complete nested structure")
		void shouldLoadCompleteNestedStructure() {
			// Arrange & Act
			AppConfigYaml config = AppConfigYaml.load("configs/local-config.yaml");

			// Assert
			assertThat(config)
					.as("Root config should be loaded")
					.isNotNull()
					.extracting(AppConfigYaml::getDatabase)
					.isNotNull()
					.satisfies(
							db -> {
								assertThat(db.flyway())
										.as("Flyway config should be nested inside database")
										.isNotNull();

								assertThat(db.server())
										.as("Server config should be nested inside database")
										.isNotNull();

								assertThat(db.maximumPoolSize())
										.as("maximumPoolSize should be configured")
										.isPositive();

								assertThat(db.driver())
										.as("driver should not be empty")
										.isNotEmpty()
										.contains("Driver");
							});
		}

		@Test
		@DisplayName("It should validate relations between timeout values")
		void shouldValidateTimeoutRelationships() {
			// Arrange & Act
			AppConfigYaml config = AppConfigYaml.load("configs/local-config.yaml");
			AppConfigYaml.Database db = config.getDatabase();

			// Assert
			assertThat(db.minimumIdle())
					.as("minimumIdle should be less than maximumPoolSize")
					.isLessThan(db.maximumPoolSize());

			assertThat(db.idleTimeout())
					.as("idleTimeout should be greater than connectionTimeout")
					.isGreaterThan(db.connectionTimeout());

			assertThat(db.maxLifetime())
					.as("maxLifetime should be the longest timeout")
					.isGreaterThan(db.idleTimeout())
					.isGreaterThan(db.connectionTimeout());
		}
	}

	@Nested
	@DisplayName("When the YAML file does not exist")
	class FileNotFoundTests {
		@Test
		@DisplayName("It should throw a ConfigurationException with an appropriate message")
		void shouldThrowConfigurationExceptionWhenFileNotFound() {
			// Arrange
			String nonExistentPath = "configs/arquivo-inexistente.yaml";

			// Act & Assert
			assertThatThrownBy(() -> AppConfigYaml.load(nonExistentPath))
					.as("Should throw ConfigurationException when file doesn't exist")
					.isInstanceOf(ConfigurationException.class)
					.hasMessageContaining("Configuration file not found")
					.hasMessageContaining(nonExistentPath);
		}

		@Test
		@DisplayName("It should throw an exception for a null path")
		void shouldThrowExceptionForNullPath() {
			// Act & Assert
			assertThatThrownBy(() -> AppConfigYaml.load(null))
					.as("Should throw exception for null path")
					.isInstanceOf(Exception.class);
		}

		@Test
		@DisplayName("It should throw an exception for an empty path")
		void shouldThrowExceptionForEmptyPath() {
			// Act & Assert
			assertThatThrownBy(() -> AppConfigYaml.load(""))
					.as("Should throw exception for empty path")
					.isInstanceOf(ConfigurationException.class)
					.hasMessageContaining("Configuration path cannot be null or empty");
		}

		@Test
		@DisplayName("It should validate multiple invalid paths")
		void shouldValidateMultipleInvalidPaths() {
			// Arrange
			String[] invalidPaths = {"configs/nao-existe.yaml", "invalid/path.yaml", "arquivo.yaml"};

			// Act & Assert
			assertThat(invalidPaths)
					.as("All invalid paths should throw exceptions")
					.allSatisfy(
							path -> {
								assertThatThrownBy(() -> AppConfigYaml.load(path))
										.isInstanceOf(ConfigurationException.class);
							});
		}
	}

	@Nested
	@DisplayName("When the YAML file is invalid")
	class InvalidYamlTests {
		@Test
		@DisplayName("It should throw a ConfigurationException for malformed YAML")
		void shouldThrowConfigurationExceptionForMalformedYaml() {
			// Arrange
			String invalidPath = "configs/invalid-config.yaml";

			// Act & Assert
			assertThatThrownBy(() -> AppConfigYaml.load(invalidPath))
					.as("Should throw ConfigurationException for malformed YAML")
					.isInstanceOf(ConfigurationException.class)
					.hasMessageContaining("Failed to load configuration")
					.hasCauseInstanceOf(IOException.class); // Causa raiz
		}

		@Test
		@DisplayName("It should throw an exception for YAML with an incorrect structure")
		void shouldThrowExceptionForIncorrectStructure() {
			// Arrange
			String wrongStructurePath = "configs/wrong-structure-config.yaml";

			// Act & Assert
			assertThatThrownBy(() -> AppConfigYaml.load(wrongStructurePath))
					.as("Should throw exception for incorrect YAML structure")
					.isInstanceOf(ConfigurationException.class);
		}
	}

	@Nested
	@DisplayName("When validating data types")
	class DataTypeTests {

		@Test
		@DisplayName("It should validate numeric typess")
		void shouldValidateNumericTypes() {
			// Arrange & Act
			AppConfigYaml config = AppConfigYaml.load("configs/local-config.yaml");
			AppConfigYaml.Database db = config.getDatabase();

			// Assert
			assertThat(db.maximumPoolSize())
					.as("maximumPoolSize should be an integer")
					.isInstanceOf(Integer.class)
					.isBetween(1, 1000);

			assertThat(db.minimumIdle())
					.as("minimumIdle should be a positive integer")
					.isInstanceOf(Integer.class)
					.isPositive();

			assertThat(db.connectionTimeout())
					.as("connectionTimeout should be in milliseconds")
					.isInstanceOf(Integer.class)
					.isGreaterThan(1000);
		}

		@Test
		@DisplayName("It should validate boolean types")
		void shouldValidateBooleanTypes() {
			// Arrange & Act
			AppConfigYaml config = AppConfigYaml.load("configs/local-config.yaml");
			AppConfigYaml.Flyway flyway = config.getDatabase().flyway();

			// Assert
			assertThat(flyway.cleanOnDisabled())
					.as("cleanOnDisabled should be a boolean")
					.isInstanceOf(Boolean.class)
					.isIn(true, false);

			assertThat(flyway.cleanMigrationOnStart())
					.as("cleanMigrationOnStart should be a boolean")
					.isInstanceOf(Boolean.class);
		}

		@Test
		@DisplayName("It should validate string types")
		void shouldValidateStringTypes() {
			// Arrange & Act
			AppConfigYaml config = AppConfigYaml.load("configs/local-config.yaml");
			AppConfigYaml.Database db = config.getDatabase();

			// Assert
			assertThat(db.driver())
					.as("Driver should be a non-empty string")
					.isInstanceOf(String.class)
					.isNotEmpty()
					.isNotBlank()
					.startsWith("org.")
					.endsWith("Driver");

			assertThat(db.server().port())
					.as("Port should be a string with digits")
					.isInstanceOf(String.class)
					.matches("\\d{4,5}");
		}
	}

	@Nested
	@DisplayName("When testing edge values")
	class EdgeCaseTests {
		@Test
		@DisplayName("It should validate values are within expected ranges")
		void shouldValidateValuesAreWithinExpectedRanges() {
			// Arrange & Act
			AppConfigYaml config = AppConfigYaml.load("configs/local-config.yaml");
			AppConfigYaml.Database db = config.getDatabase();

			// Assert
			assertThat(db.maximumPoolSize()).as("Pool size should be reasonable").isBetween(1, 100);

			assertThat(db.minimumIdle()).as("Minimum idle should be reasonable").isBetween(0, 50);

			assertThat(db.connectionTimeout())
					.as("Timeout should be reasonable (5-120 seconds)")
					.isBetween(5000, 120000);
		}

		@Test
		@DisplayName("It should accept configuration with valid minimum values")
		void shouldAcceptMinimumValidConfiguration() {
			// Arrange & Act
			AppConfigYaml config = AppConfigYaml.load("configs/local-config.yaml");

			// Assert
			assertSoftly(
					softly -> {
						softly
								.assertThat(config.getDatabase().maximumPoolSize())
								.as("Should accept positive pool size")
								.isGreaterThan(0);

						softly
								.assertThat(config.getDatabase().minimumIdle())
								.as("Should accept non-negative idle")
								.isGreaterThanOrEqualTo(0);

						softly
								.assertThat(config.getDatabase().driver())
								.as("Should have valid driver")
								.isNotEmpty();
					});
		}

		@Test
		@DisplayName("It should reject empty strings in required fields")
		void shouldRejectEmptyStringsInRequiredFields() {
			// Arrange & Act
			AppConfigYaml config = AppConfigYaml.load("configs/local-config.yaml");
			AppConfigYaml.Database db = config.getDatabase();

			// Assert
			assertThat(db.driver())
					.as("Driver should not be empty or blank")
					.isNotEmpty()
					.isNotBlank()
					.hasSizeGreaterThan(5);
		}

		@Nested
		@DisplayName("When testing immutability and consistency")
		class ImmutabilityTests {

			@Test
			@DisplayName("Multiple loads should produce equal values")
			void multipleLoadsShouldProduceEqualValues() {
				// Arrange & Act
				AppConfigYaml config1 = AppConfigYaml.load("configs/local-config.yaml");
				AppConfigYaml config2 = AppConfigYaml.load("configs/local-config.yaml");

				// Assert
				assertThat(config1.getDatabase())
						.as("Multiple loads should produce equal configurations")
						.usingRecursiveComparison()
						.isEqualTo(config2.getDatabase());
			}

			@Test
			@DisplayName("Records should be immutable")
			void recordsShouldBeImmutable() {
				// Arrange
				AppConfigYaml config = AppConfigYaml.load("configs/local-config.yaml");
				AppConfigYaml.Database db1 = config.getDatabase();
				AppConfigYaml.Database db2 = config.getDatabase();

				// Assert
				assertThat(db1)
						.as("Same getter call should return consistent values")
						.satisfies(
								database -> {
									assertThat(database.driver()).isEqualTo(db2.driver());
									assertThat(database.maximumPoolSize()).isEqualTo(db2.maximumPoolSize());
								});
			}

			@Test
			@DisplayName("It should maintain consistency between different config files")
			void shouldMaintainConsistencyBetweenConfigFiles() {
				// Arrange & Act
				AppConfigYaml localConfig = AppConfigYaml.load("configs/local-config.yaml");
				AppConfigYaml prodConfig = AppConfigYaml.load("configs/prod-config.yaml");

				// Assert
				assertThat(localConfig.getDatabase())
						.as("Local and prod should have same structure")
						.hasFieldOrProperty("driver")
						.hasFieldOrProperty("maximumPoolSize")
						.hasFieldOrProperty("flyway")
						.hasFieldOrProperty("server");

				assertThat(localConfig.getDatabase().driver())
						.as("But different values")
						.isNotEqualTo(prodConfig.getDatabase().driver());
			}
		}

		@Nested
		@DisplayName("When testing environment-specific configurations")
		class EnvironmentSpecificTests {
			@Test
			@DisplayName("Production config should have appropriate values")
			void prodConfigShouldHaveAppropriateValues() {
				// Arrange & Act
				AppConfigYaml config = AppConfigYaml.load("configs/prod-config.yaml");
				AppConfigYaml.Database db = config.getDatabase();

				// Assert
				assertThat(db.driver()).as("Production should use PostgreSQL").contains("postgresql");

				assertThat(db.maximumPoolSize()).as("Production should have larger pool").isGreaterThan(20);

				assertThat(db.flyway().cleanOnDisabled())
						.as("Production should never clean database")
						.isFalse();

				assertThat(db.flyway().cleanMigrationOnStart())
						.as("Production should never clean on start")
						.isFalse();
			}

			@Test
			@DisplayName("Local config should have development values")
			void localConfigShouldHaveDevelopmentValues() {
				// Arrange & Act
				AppConfigYaml config = AppConfigYaml.load("configs/local-config.yaml");
				AppConfigYaml.Database db = config.getDatabase();

				// Assert
				assertThat(db.driver()).as("Local should use H2 for testing").contains("h2");

				assertThat(db.maximumPoolSize())
						.as("Local should have smaller pool")
						.isLessThanOrEqualTo(20);

				assertThat(db.flyway().cleanMigrationOnStart())
						.as("Local can clean database for fresh start")
						.isTrue();
			}
		}
	}
}
