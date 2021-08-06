package com.caijia.bean;

import java.util.ArrayList;
import java.util.List;

public class Cart {

	private User user;
	private List<Book> bookList = new ArrayList<>();

	public Cart() {
	}

	public Cart(User user, List<Book> bookList) {
		super();
		this.user = user;
		this.bookList = bookList;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public List<Book> getBookList() {
		return bookList;
	}

	public void addToBookList(Book book) {
		bookList.add(book);
	}
}
