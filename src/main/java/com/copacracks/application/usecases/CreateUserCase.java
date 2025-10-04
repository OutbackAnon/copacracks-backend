package com.copacracks.application.usecases;

import com.copacracks.application.dto.NewUserDto;
import com.copacracks.infrastructure.dto.CreateUserRequestDto;

@FunctionalInterface
public interface CreateUserCase {
	void execute(NewUserDto user);
}
