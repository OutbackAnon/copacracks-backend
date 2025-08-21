package com.copacracks.domain.repository;

import com.copacracks.domain.model.user.User;
import java.util.Optional;

public interface UserRepository {
  User save(User user);

  Optional<User> findById(Long id);

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}
