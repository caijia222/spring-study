package com.caijia.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.caijia.entity.User;

public interface UserMapper {
	@Select("select * from users where id = #{id}")
	User getById(@Param("id") int id);

	@Select("select * from users where email = #{email} and password = #{password}")
	User getByEmailAndPassword(@Param("email") String email, @Param("password") String password);

	@Select("select * from users limit #{limit} offset #{offset}")
	List<User> getByPage(@Param("limit") int limit, @Param("offset") int offset);

	@Select("select * from users")
	List<User> getAll();

	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	@Insert("insert into users (email, password, name, createdAt) values (#{user.email}, #{user.password}, #{user.name}, #{user.createdAt})")
	void insert(@Param("user") User user);
	
	@Update("update users set name = #{user.name} where id = #{user.id}")
	void update(@Param("user") User user);
	
	@Delete("delete from users where id = #{id}")
	void deleteById(@Param("id") int id);
	
}
