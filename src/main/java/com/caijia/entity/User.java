package com.caijia.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;

@Data
public class User {

	private int id;
	private String name;
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;
	private String email;
	
	
}
