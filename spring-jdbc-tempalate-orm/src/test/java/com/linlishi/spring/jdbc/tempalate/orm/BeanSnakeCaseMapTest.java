package com.linlishi.spring.jdbc.tempalate.orm;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.linlishi.spring.jdbc.tempalate.orm.bean.BeanSnakeCaseMap;
import com.linlishi.spring.jdbc.tempalate.orm.domain.UpdateTimeDO;

public class BeanSnakeCaseMapTest {
	@Test
	public void testBeanSnakeCaseMapKeys() {
		BeanSnakeCaseMap beanSnakeCaseMap = BeanSnakeCaseMap.create(UpdateTimeDO.class);
		assertThat("update_time").isEqualTo(beanSnakeCaseMap.getKeys()[0]);
	}
	@Test
	public void testBeanSnakeCaseMapPut() {
		BeanSnakeCaseMap beanSnakeCaseMap = BeanSnakeCaseMap.create(UpdateTimeDO.class);
		Object result = beanSnakeCaseMap.newBeanInstance();
		beanSnakeCaseMap.put(result, "update_time", LocalDateTime.now());
		assertThat(result).isNotNull();
		assertThat(((UpdateTimeDO)result).getUpdateTime()).isNotNull();
	}
}
