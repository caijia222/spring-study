package com.caijia.validator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Validators {
	@Autowired
	List<Validator> validators;

	public void validate(String email, String password, String name) {
		for (Validator validator : validators) {
			validator.validate(email, password, name);
		}
	}
}
