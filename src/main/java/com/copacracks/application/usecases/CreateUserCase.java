package com.copacracks.application.usecases;

import com.copacracks.application.dto.NewUserDto;

@FunctionalInterface
public interface CreateUserCase {
	void execute(NewUserDto user);
}
