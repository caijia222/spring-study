package com.caijia.bean;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {

	private int id;
	private String name;
	private String password;
	private String email;
}
