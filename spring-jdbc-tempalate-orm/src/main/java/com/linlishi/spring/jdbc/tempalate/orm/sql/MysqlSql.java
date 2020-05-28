package com.linlishi.spring.jdbc.tempalate.orm.sql;

import com.linlishi.spring.jdbc.tempalate.orm.consts.SqlKeys;

/**
 * mysql适配
 * @author linlishi
 *
 */
public class MysqlSql extends AbstractSql {
	
	@Override
	public String limit(Limit limit) {
		return limit(limit.offset, limit.limit);
	}
	
	@Override
	public String limit(long offset, long pagesize) {
		return SqlKeys.LIMIT_ + offset + SqlKeys.COMMA_ + pagesize;
	}
}
