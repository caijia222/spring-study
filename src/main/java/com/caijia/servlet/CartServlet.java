package com.caijia.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caijia.bean.Book;
import com.caijia.bean.User;
import com.caijia.service.BookService;
import com.caijia.service.CartService;
import com.caijia.service.UserService;

@WebServlet("/addCart")
public class CartServlet extends HttpServlet {

	private static final long serialVersionUID = -8287276703066813991L;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private UserService userService;
	private BookService bookService;
	private CartService cartService;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setBookService(BookService bookService) {
		this.bookService = bookService;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		int currentUserId = getFromCookie(req);
		User user = userService.getUser(currentUserId);
		if (user != null) {
			Book book = bookService.getBook(Integer.valueOf(req.getParameter("bookId")));
			cartService.addToCart(user, book);
		}
	}

	private int getFromCookie(HttpServletRequest req) {
		Cookie[] cookies = req.getCookies();
		for (Cookie cookie : cookies) {
			if ("userId".equals(cookie.getName())) {
				return Integer.parseInt(cookie.getValue());
			}
		}
		logger.info("从cookie获取userId失败");
		return 0;
	}

}
