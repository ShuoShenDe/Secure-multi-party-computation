package com.localmoran.example.demo.mapper;

import com.localmoran.example.demo.entity.Input;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface InputMapper extends Mapper<Input> {
	
	@Select("select * from linput where id = #{id}")
    Input seletByCond(@Param("id") String id);
	
	@Insert("insert into linput(id,input) values (#{id},#{input}) ")
	Integer insertData(@Param("id") String id, @Param("input") String input);
}