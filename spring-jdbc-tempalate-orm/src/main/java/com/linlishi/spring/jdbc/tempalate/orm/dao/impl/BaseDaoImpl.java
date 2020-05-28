package com.linlishi.spring.jdbc.tempalate.orm.dao.impl;

import java.lang.reflect.ParameterizedType;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import com.linlishi.spring.jdbc.tempalate.orm.bean.BeanSnakeCaseMap;
import com.linlishi.spring.jdbc.tempalate.orm.consts.ModelFieldConsts;
import com.linlishi.spring.jdbc.tempalate.orm.consts.SqlKeys;
import com.linlishi.spring.jdbc.tempalate.orm.dao.BaseDao;
import com.linlishi.spring.jdbc.tempalate.orm.domain.UpdateTimeDO;
import com.linlishi.spring.jdbc.tempalate.orm.sql.Sql;
import com.linlishi.spring.jdbc.tempalate.orm.sql.SqlBuilder;
import com.linlishi.spring.jdbc.tempalate.orm.tool.SqlTools;

public class BaseDaoImpl<T extends UpdateTimeDO> implements BaseDao<T>{
	private BeanSnakeCaseMap beanMap;	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	protected SqlTools sqlTools;
	@Autowired
	protected Sql sql;
	private String model;
	
	private RowMapper<T> rowMapper = (rs, rowNum) -> {
		Object result = beanMap.newBeanInstance();
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			String key = JdbcUtils.lookupColumnName(rsmd, i);
			Object obj = JdbcUtils.getResultSetValue(rs, i);
			beanMap.put(result, key, obj);
		}
		return (T) result;
	};
	
	public BaseDaoImpl() {
		Class<T> tClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		beanMap = BeanSnakeCaseMap.create(tClass);
	}
	
	public BaseDaoImpl(String model) {
		this();
		this.model = model;
	}
	
	
	
	public void insert(T entity) {
		SqlBuilder sqlBuilder = SqlBuilder.insert();
		StringBuilder stringBuilder = new StringBuilder();
		List<Object> paras = new ArrayList<>();
		for (int i = beanMap.getKeys().length - 1; i >= 0; i--) {
			String key = beanMap.getKeys()[i];
			Object value = beanMap.get(entity, key);
			if (value != null) {
				stringBuilder.append(key);
				paras.add(value);
			}
			if ( i != 0) {
				stringBuilder.append(SqlKeys.COMMA_);
			}
		}
		sqlBuilder.fields(stringBuilder.toString());
		
		jdbcTemplate.update(sql.build(sqlBuilder, model), paras.toArray());
	}
	
	
	public int update(T entity) {
		SqlBuilder sqlBuilder = SqlBuilder.update();
		StringBuilder stringBuilder = new StringBuilder();
		List<Object> paras = new ArrayList<>();
		for (int i = beanMap.getKeys().length - 1; i >= 0; i--) {
			String key = beanMap.getKeys()[i];
			Object value = beanMap.get(entity, key);
			if (value != null) {
				stringBuilder.append(key);
				if (i != 0) {
					stringBuilder.append(SqlKeys._EQ_PARA);
				}
				paras.add(value);
			}
		}
		sqlBuilder.set(stringBuilder.toString());
		return jdbcTemplate.update(sql.build(sqlBuilder, model), paras.toArray());
	};
	
	
	public void insert(List<T> entities) {
		for (T entity : entities) {
			insert(entity);
		}
    }
	
	public int[] update(List<T> entities) {
		int[] resultArr = new int[entities.size()];
		for (int i = 0; i < resultArr.length; i++) {
			resultArr[i] = update(entities.get(i));
		}
		return resultArr;
	}
	
	
	public int delete(Long id) {
		SqlBuilder sqlBuilder = SqlBuilder.delete();
		return jdbcTemplate.update(sql.build(sqlBuilder, model), id);
	}
	
	public int delete(List<Long> ids) {
		SqlBuilder sqlBuilder = SqlBuilder.delete()
				.where(sqlTools.buildIn(ModelFieldConsts.ID, ids));
		
		return jdbcTemplate.update(sql.build(sqlBuilder, model));
	}
	
	
	
	public int count() {
		SqlBuilder sqlBuilder = SqlBuilder.select(SqlKeys.COUNT_ALL);
		return jdbcQueryObject(sqlBuilder, null, Integer.class);
	}
	
	protected T jdbcQueryOne(SqlBuilder sqlBuilder, Object... paras) {
		List<T> entities = jdbcQuery(sqlBuilder, paras);
		if (entities.size() > 0) {
			return entities.get(0);
		} else {
			return null;
		}
	}
	
	protected List<T> jdbcQuery(SqlBuilder sqlBuilder, Object... paras) {
		return jdbcTemplate.query(sql.build(sqlBuilder, this.model), paras, rowMapper);
	}
	
	protected <K> K jdbcQueryObject(SqlBuilder sqlBuilder, Object[] paras, Class<K> cls) {
		return jdbcTemplate.queryForObject(sql.build(sqlBuilder, model), paras, cls);
	}
	
	protected int jdbcUpdate(SqlBuilder sqlBuilder, Object... paras) {
		sqlBuilder.setForward(ModelFieldConsts.UPDATE_TIME);
		int length;
		if (paras == null) {
			length = 1;
		} else {
			length = paras.length + 1;
		}
		Object[] dest = new Object[length];
		if (paras != null) {
			System.arraycopy(paras, 0, dest, 1, paras.length);
		}
		sqlTools.setUpdateTime(dest);
		paras = dest;
		return jdbcTemplate.update(sql.build(sqlBuilder, this.model), paras);
	}
	
	protected int[] jdbcBatchUpdate(SqlBuilder sqlBuilder, List<Object[]> parasList) {
		
		sqlBuilder.setForward(ModelFieldConsts.UPDATE_TIME);
		for (int i = parasList.size() - 1; i >= 0; i--) {
			Object[] paras = parasList.get(i);
			Object[] dest = new Object[paras.length + 2];
			System.arraycopy(paras, 0, dest, 1, paras.length);
			sqlTools.setUpdateTime(dest);
			parasList.set(i, dest);
		}
		
		return jdbcTemplate.batchUpdate(sql.build(sqlBuilder, this.model), parasList);
	}

	public T get(Long id) {
		SqlBuilder sqlBuilder = SqlBuilder.select();
		StringBuilder stringBuilder = new StringBuilder();
		List<Object> paras = new ArrayList<>();
		for (int i = beanMap.getKeys().length - 1; i >= 0; i--) { 
			String key = beanMap.getKeys()[i];
			stringBuilder.append(key);
			if (i == 0) {
				stringBuilder.append(SqlKeys.COMMA_);
			}
		}
		sqlBuilder.fields(stringBuilder.toString());
		sqlBuilder.where(ModelFieldConsts.ID + SqlKeys._EQ_PARA);
		
		return jdbcQueryOne(sqlBuilder, id);
	}
}
