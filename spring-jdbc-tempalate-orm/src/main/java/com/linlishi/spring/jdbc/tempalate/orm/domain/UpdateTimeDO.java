package com.linlishi.spring.jdbc.tempalate.orm.domain;

import java.time.LocalDateTime;

public class UpdateTimeDO extends CreateTimeDO {
	private LocalDateTime updateTime;

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}
	
}
