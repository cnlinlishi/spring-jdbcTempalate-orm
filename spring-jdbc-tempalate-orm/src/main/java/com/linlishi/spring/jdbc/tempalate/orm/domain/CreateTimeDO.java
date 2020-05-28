package com.linlishi.spring.jdbc.tempalate.orm.domain;

import java.time.LocalDateTime;

public class CreateTimeDO extends IdDO {
	private LocalDateTime createTime;

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}
	
}
