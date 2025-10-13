package com.copacracks.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppEnv {
    @JsonProperty("SECURITY_PEPPER")
    private final String securityPepper = "security_pepper";

    @JsonProperty("APP_ENV")
    private final String appEnv = "local";

    @JsonProperty("DB_URL")
    private final String dbUrl = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";

    @JsonProperty("DB_USERNAME")
    private final String dbUsername = "admin";

    @JsonProperty("DB_PASSWORD")
    private final String dbPassword = "";

    public static AppEnv load() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().ignoreIfMalformed().load();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Map<String, String> envMap = dotenv.entries()
                .stream()
                .collect(Collectors.toMap(DotenvEntry::getKey, DotenvEntry::getValue));

        return mapper.convertValue(envMap, AppEnv.class);
    }
}
