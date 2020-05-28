package com.linlishi.spring.jdbc.tempalate.orm.tool;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.plaf.ListUI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.linlishi.spring.jdbc.tempalate.orm.consts.SqlKeys;
import com.linlishi.spring.jdbc.tempalate.orm.domain.UpdateTimeDO;



@Component
public class SqlTools <T extends UpdateTimeDO>{
	
	public void setUpdateAndCreate(T entity) {
		LocalDateTime localDateTime = LocalDateTime.now();
		entity.setCreateTime(localDateTime);
		entity.setUpdateTime(localDateTime);
	}
	public void setUpdateTime(List<T> entities) {
		LocalDateTime dateTime = LocalDateTime.now();
		for (T entity : entities) {
			entity.setUpdateTime(dateTime);
		}
	}
	public void setUpdateTime(T entity) {
		entity.setUpdateTime(LocalDateTime.now());
	}
	
	public void setUpdateTime(Object[] paras) {
		paras[0] = LocalDateTime.now();
		
	}
	public void setCreateTime(List<T> entities) {
		LocalDateTime dateTime = LocalDateTime.now();
		for (T entity : entities) {
			entity.setCreateTime(dateTime);
		}
	}
	public void setCreateTime(T entity) {
		entity.setCreateTime(LocalDateTime.now());
	}
	
	public String buildIn(String field, List<?> list) {
		return field + SqlKeys._IN_ + SqlKeys.LEFT_PARENTHESIS + ListUtils.join(list, SqlKeys.COMMA_) + SqlKeys.RIGHT_PARENTHESIS;
	}
	
	public String buildIn(String field, Object[] arr) {
		StringBuilder strBuilder = new StringBuilder(field).append(SqlKeys._IN_ + SqlKeys.LEFT_PARENTHESIS);
		for (int i = 0; i < arr.length; i++) {
			strBuilder.append(arr[i]);
			if (i != arr.length - 1) {
				strBuilder.append(SqlKeys.COMMA_);
			}
		}
		strBuilder.append(SqlKeys.RIGHT_PARENTHESIS);
		return strBuilder.toString();
	}
}
