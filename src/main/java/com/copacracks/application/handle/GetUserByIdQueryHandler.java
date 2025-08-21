package com.copacracks.application.handle;

import com.copacracks.application.query.GetUserByIdQuery;
import com.copacracks.domain.model.user.User;
import com.copacracks.domain.repository.UserRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GetUserByIdQueryHandler {
  private final UserRepository userRepository;

  public Optional<User> handle(GetUserByIdQuery query) {
    return userRepository.findById(query.userId());
  }
}
