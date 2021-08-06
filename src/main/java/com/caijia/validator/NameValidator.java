package com.caijia.validator;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class NameValidator implements Validator {

	@Override
	public void validate(String email, String password, String name) {
		if (!password.matches("^.{6,20}$")) {
			throw new IllegalArgumentException("invalid password: " + password);
		}
	}

}
