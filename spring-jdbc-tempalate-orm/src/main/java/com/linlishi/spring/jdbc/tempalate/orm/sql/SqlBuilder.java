package com.linlishi.spring.jdbc.tempalate.orm.sql;

import org.apache.commons.lang.StringUtils;

import com.linlishi.spring.jdbc.tempalate.orm.consts.SqlKeys;

public class SqlBuilder {
	public static final int TYPE_SELECT = 0;
	public static final int TYPE_UPDATE = 1;
	public static final int TYPE_INSERT = 2;
	public static final int TYPE_DELETE = 3;

	private int type = TYPE_SELECT;

	private String fields; 			//用于select或者insert
	private String values;			//用于insert
	private String set;				//用于update
	private String alias;			//用于select
	private String where;			//用于select,update,delete
	private String groupBy;			//用于select
	private String having;			//用于select
	private String orderBy;			//用于select
	private Limit limit;			//用于select
	private boolean forUpdate = false; //用于select

	public static SqlBuilder select() {
		return new SqlBuilder(TYPE_SELECT);
	}

	public static SqlBuilder select(String fields) {
		return new SqlBuilder(TYPE_SELECT).fields(fields);
	}

	public static SqlBuilder update() {
		return new SqlBuilder(TYPE_UPDATE);
	}

	public static SqlBuilder insert() {
		return new SqlBuilder(TYPE_INSERT);
	}

	public static SqlBuilder delete() {
		return new SqlBuilder(TYPE_DELETE);
	}


	protected SqlBuilder(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public boolean isSelect() {
		return type == TYPE_SELECT;
	}

	public boolean isUpdate() {
		return type == TYPE_UPDATE;
	}

	public boolean isInsert() {
		return type == TYPE_INSERT;
	}

	public boolean isDelete() {
		return type == TYPE_DELETE;
	}

	public SqlBuilder fields(String fields) {
		this.fields = fields;
		return this;
	}

	public int getFieldsCount() {
		if (fields == null || fields.length() == 0)
			throw new RuntimeException("should has field in sql");
		else
			return fields.split(",").length;
	}
	
	

	public SqlBuilder set(String set) {
		this.set = set;
		return this;
	}



	public SqlBuilder setAppend(String column, long value) {
		if (set == null || set.length() == 0)
			set = column + "=" + value;
		else
			set += SqlKeys.COMMA_ + column + "=" + value;

		return this;
	}

	public SqlBuilder setAppend(String column) {
		if (set == null || set.length() == 0)
			set = column + SqlKeys._EQ_PARA;
		else
			set += SqlKeys.COMMA_ + column + SqlKeys._EQ_PARA;

		return this;
	}
	
	public SqlBuilder setForward(String column) {
		if (set == null || set.length() == 0)
			set = column + SqlKeys._EQ_PARA;
		else
			set = column + SqlKeys._EQ_PARA + SqlKeys.COMMA_ + set;

		return this;
	}


	public SqlBuilder where(String where) {
		this.where = where;
		return this;
	}

	public SqlBuilder whereAppend(String condition) {
		if (where == null || where.length() == 0)
			where = condition;
		else
			where = new StringBuilder("(").append(where).append(")").append(SqlKeys._AND_).append(condition).toString();

		return this;
	}
	
	public SqlBuilder whereForward(String condition) {
		if (where == null || where.length() == 0)
			where = condition;
		else
			where = new StringBuilder(condition).append(SqlKeys._AND_).append("(").append(where).append(")").toString();

		return this;
	}

	public SqlBuilder groupBy(String groupBy) {
		this.groupBy = groupBy;
		return this;
	}

	public SqlBuilder having(String having) {
		this.having = having;
		return this;
	}


	public SqlBuilder orderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}

	public SqlBuilder orderBy(String orderBy, boolean asc) {
		this.orderBy = orderBy + (asc ? SqlKeys._ASC : SqlKeys._DESC);
		return this;
	}



	public SqlBuilder limit(Limit limit) {
		this.limit = limit;
		return this;
	}

	public SqlBuilder limit(long start, long count) {
		this.limit = new Limit(start, count);
		return this;
	}

	public Limit getLimit() {
		return limit;
	}

	public SqlBuilder setForUpdate(boolean forUpdate) {
		this.forUpdate = forUpdate;
		return this;
	}

	public boolean getForUpdate() {
		return forUpdate;
	}

	public SqlBuilder appendFieldsClause(StringBuilder sb) {
		if (isSelect())
			appendSelectFieldsClause(sb);
		else if(isInsert())
			appendInsertFieldsClause(sb);
		else
			throw new RuntimeException("fields clause should use only in select or insert");

		return this;
	}
	
	public SqlBuilder appendSelectFieldsClause(StringBuilder sb) {
		sb.append(fields);
		sb.append(" ");
		return this;
	}

	//用于insert
	public SqlBuilder appendInsertFieldsClause(StringBuilder sb) {
		if (fields == null || fields.length() == 0)
			throw new RuntimeException("should has field in sql");
		else
			sb.append(fields);

		return this;
	}
	
	public SqlBuilder appendFromClause(StringBuilder sb, String model) {
		if (alias == null || alias.length() == 0)
			sb.append(SqlKeys.FROM_).append(model).append(" ");
		else
			sb.append(SqlKeys.FROM_).append(model).append(" ");

		return this;
	}

	
	public SqlBuilder appendForUpdateClause(StringBuilder sb) {
		//for update应该是最后一个子句，后面不用加空格。
		if (forUpdate)
			sb.append(SqlKeys.FOR_UPDATE);

		return this;
	}
	
	public SqlBuilder appendSetClause(StringBuilder sb) {
		if (set == null || set.length() == 0)
			throw new RuntimeException("should has set in update");
		else
			sb.append(SqlKeys.SET_).append(set).append(" ");

		return this;
	}

	public SqlBuilder appendValuesClause(StringBuilder sb) {
		if (values == null || values.length() == 0)
			sb.append(StringUtils.repeat(SqlKeys.PARA, SqlKeys.COMMA_, getFieldsCount()));
		else
			sb.append(values);

		return this;
	}
	
	public void appendWhereClause(StringBuilder sb) {
		if (where == null || where.length() == 0)
			return;
		else
			sb.append(SqlKeys.WHERE_).append(where).append(" ");
	}
	
	public SqlBuilder appendLimitClause(StringBuilder sb, Sql sql) {
		if (limit != null)
			sb.append(sql.limit(limit)).append(" ");

		return this;
	}
	
	public SqlBuilder appendOrderByClause(StringBuilder sb) {
		if (orderBy != null)
			sb.append(SqlKeys.ORDER_BY_).append(orderBy).append(" ");

		return this;
	}
	
	public SqlBuilder appendGroupByClause(StringBuilder sb){
		if (groupBy != null && groupBy.length() > 0)
			sb.append(SqlKeys.GROUP_BY_).append(groupBy).append(" ");

		return this;
	}
	
	public SqlBuilder appendHavingClause(StringBuilder sb) {
		if (having != null && having.length() > 0)
			sb.append(SqlKeys.HAVING_).append(having).append(" ");

		return this;
	}

	
}
