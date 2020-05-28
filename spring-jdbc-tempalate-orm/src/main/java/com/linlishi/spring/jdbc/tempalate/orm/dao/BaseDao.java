package com.linlishi.spring.jdbc.tempalate.orm.dao;

import java.util.List;

import com.linlishi.spring.jdbc.tempalate.orm.domain.UpdateTimeDO;

/**
 * 基础dao所有的dao接口都要继承这个接口
 * @author linlishi
 *
 * @param <T>	dao关联的DO类
 */
public interface BaseDao <T extends UpdateTimeDO> {
	/**
	 * 获取全部的字段
	 * @param id
	 * @return
	 */
	T get(Long id);
	/**
	 * 插入一个entity
	 * @param entity
	 * @return
	 */
	void insert(T entity);
	/**
	 * 插入多个entity
	 * @param doos
	 * @return
	 */
	void insert(List<T> doos);
	/**
	 * 更新entity
	 * @param entity
	 * @return
	 */
	int update(T entity);
	/**
	 * 更新多个entity
	 * @param doos
	 * @return
	 */
	int[] update(List<T> doos);
	
	/**
	 * 删除一个实体
	 * @param id
	 * @return
	 */
	int delete(Long id);
	/**
	 * 删除多个实体
	 * @param ids
	 * @return
	 */
	int delete(List<Long> ids);
	
	
	/**
	 * 获取表内全部实体数量
	 * @return
	 */
	int count();
}
