package com.facilio.workflows.util;

import java.util.List;
import java.util.Map;

public interface ExpressionAggregateInterface {

	public Object getAggregateResult(List<Map<String, Object>> props,String fieldName);

}
