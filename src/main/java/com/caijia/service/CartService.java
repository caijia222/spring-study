package com.caijia.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caijia.entity.Book;
import com.caijia.entity.User;

public class CartService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private DataSource dataSource;

	public void addToCart(User user, Book book) {
		try (Connection conn = dataSource.getConnection()) {
			try (PreparedStatement pstmt = conn
					.prepareStatement("select * from cart where userId = ? and bookId = ?")) {
				pstmt.setInt(1, user.getId());
				pstmt.setInt(2, book.getId());
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next()) {
						logger.info("购物车记录已存在");
					} else {
						try (PreparedStatement pstmt2 = conn
								.prepareStatement("insert into cart(userId,bookId) values (?,?)")) {
							pstmt.setInt(1, user.getId());
							pstmt.setInt(2, book.getId());
							int ret = pstmt.executeUpdate();
							if (ret == 1) {
								logger.info("添加购物车记录成功");
							} else {
								logger.info("添加购物车记录失败");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("添加购物车记录异常", e);
		}
	}
}
