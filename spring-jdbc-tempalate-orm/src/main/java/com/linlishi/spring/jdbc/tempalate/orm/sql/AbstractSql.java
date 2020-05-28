package com.linlishi.spring.jdbc.tempalate.orm.sql;

import com.linlishi.spring.jdbc.tempalate.orm.consts.SqlKeys;

/**
 * 对build进行处理，各个数据库适配者应该继承这个类
 * @author linlishi
 *
 */
public abstract class AbstractSql implements Sql {

	@Override
	public String build(SqlBuilder builder, String model) {
		if (builder.isSelect()) {
			return select(builder, model);
		}
		else if(builder.isUpdate()) {
			return update(builder, model);
		}
		else if(builder.isInsert()) {
			return insert(builder, model);
		}
		else if (builder.isDelete()) {
			return delete(builder, model);
		}
		else {
			throw new RuntimeException("unknown SqlBuilder type");
		}
	}

	/*
	 * select语句封装, 一般格式为 select ... FROM ... [JOINS ...] [WHERE ...] [GROUP BY ...] [HAVING ...] [ORDER BY ...] [LIMIT/OFFSET ...] [FOR UPDATE]
	 *  对于oracle LIMIT/OFFSET需要做非常特殊的处理
	 */

	public String select(SqlBuilder builder, String model) {
		StringBuilder sb = new StringBuilder(SqlKeys.SELECT_);
		builder.appendSelectFieldsClause(sb);
		builder.appendFromClause(sb, model);
		builder.appendWhereClause(sb);
		builder.appendGroupByClause(sb);
		builder.appendHavingClause(sb);
		builder.appendOrderByClause(sb);
		builder.appendLimitClause(sb,this);
		builder.appendForUpdateClause(sb);
		return sb.toString();
	}


	private String update(SqlBuilder builder, String model) {
		StringBuilder sb = new StringBuilder(SqlKeys.UPDATE_);
		sb.append(model).append(" ");
		builder.appendSetClause(sb);
		builder.appendWhereClause(sb);
		return sb.toString();
	}


	private String insert(SqlBuilder builder, String model) {
		StringBuilder sb = new StringBuilder(SqlKeys.INSERT_INTO_);
		sb.append(model).append(" (");
		builder.appendFieldsClause(sb);
		sb.append(")").append(SqlKeys._VALUES_).append("(");
		builder.appendValuesClause(sb);
		sb.append(")");
		return sb.toString();
	}

	private String delete(SqlBuilder builder, String model) {
		StringBuilder sb = new StringBuilder(SqlKeys.DELETE_).append(SqlKeys.FROM_);
		sb.append(model).append(" ");
		builder.appendWhereClause(sb);
		return sb.toString();
	}

}
