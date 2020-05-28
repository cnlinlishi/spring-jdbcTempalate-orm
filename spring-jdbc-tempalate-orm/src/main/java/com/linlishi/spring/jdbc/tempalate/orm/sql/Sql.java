package com.linlishi.spring.jdbc.tempalate.orm.sql;

/**
 * 不同的数据库需要实现这个接口进行适配
 * @author linlishi
 *
 */
public interface Sql {
	String limit(Limit limit);
	String limit(long offset, long pagesize);
	String build(SqlBuilder builder, String model);
}
