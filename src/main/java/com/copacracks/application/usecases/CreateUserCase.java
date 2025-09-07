package com.copacracks.application.usecases;

import com.copacracks.application.dto.CreateUserRequestDto;

public interface CreateUserCase {
	void execute(CreateUserRequestDto user);
}
