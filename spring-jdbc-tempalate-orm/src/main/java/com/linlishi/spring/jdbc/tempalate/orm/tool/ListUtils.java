package com.linlishi.spring.jdbc.tempalate.orm.tool;

import java.util.List;
import java.util.stream.Collectors;

public class ListUtils {
	public static final String join(List<?> list, String delimiter) {
		return list.parallelStream().map(item -> item.toString()).collect(Collectors.joining(delimiter));
	}
}
