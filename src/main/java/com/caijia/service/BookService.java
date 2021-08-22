package com.caijia.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.caijia.entity.Book;


@Component
public class BookService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Book getBook(int id) {
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement("select * from book where id = ?");
			pstmt.setLong(1, id);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String name = rs.getString("name");
				String author = rs.getString("author");
				Book book = new Book(id, name, author);
				return book;
			}
		} catch (Exception e) {
			logger.error("查询失败", e);
		}
		return null;
	}
}
