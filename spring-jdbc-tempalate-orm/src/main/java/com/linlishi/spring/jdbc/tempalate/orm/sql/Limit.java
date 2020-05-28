package com.linlishi.spring.jdbc.tempalate.orm.sql;

public class Limit {
	public long limit = 1;
	public long offset = 0;

	public Limit(long limit) {
		this.limit = limit;
	}

	public Limit(long start, long count) {
		this.limit = count;
		this.offset = start;
	}
}
