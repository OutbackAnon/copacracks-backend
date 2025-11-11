package com.copacracks.application.usecases.impl;

import com.copacracks.application.usecases.UserLoginUseCase;
import com.copacracks.domain.exception.UserAuthException;
import com.copacracks.domain.model.auth.UserAuth;
import com.copacracks.domain.model.user.PartialUser;
import com.copacracks.domain.model.user.User;
import com.copacracks.domain.repository.UserRepository;
import com.copacracks.domain.repository.UserSessionRepository;
import com.copacracks.domain.security.JwtTokenGenerator;
import com.copacracks.domain.security.PasswordEncoder;
import com.copacracks.infrastructure.config.AppConfig;
import com.copacracks.infrastructure.security.PasswordEncoderImpl;
import com.google.inject.Inject;

import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Optional;

public class UserLoginUserCaseImpl implements UserLoginUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppConfig appConfig;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final UserSessionRepository userSessionRepository;

    @Inject
    public UserLoginUserCaseImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenGenerator jwtTokenGenerator, UserSessionRepository userSessionRepository, AppConfig appConfig) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.appConfig = appConfig;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.userSessionRepository = userSessionRepository;

    }

    @Override
    public UserAuth execute(PartialUser partialUser) {
         Optional<User> userResult = userRepository.findByUsername(partialUser.getUsername().value());

         if (userResult.isEmpty()) {
            throw new UserAuthException("Senha ou usuario incorretos");
         }

         User user = userResult.get();

         boolean isCorrectPassword = passwordEncoder.verify(partialUser.getPassword().value(), user.getPassword(), appConfig.env().getSecurityPepper());

         if (!isCorrectPassword) {
            throw new UserAuthException("Senha ou usuario incorretos");
         }

         String accessToken = jwtTokenGenerator.generateAccessToken(user.getUsername());
         String refreshToken = jwtTokenGenerator.generateRefreshToken(user.getUsername());



        return new UserAuth(accessToken, refreshToken);
    }
}
