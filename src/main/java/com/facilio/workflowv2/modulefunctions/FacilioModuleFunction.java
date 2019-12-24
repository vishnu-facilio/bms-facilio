package com.facilio.workflowv2.modulefunctions;

import java.util.List;
import java.util.Map;

import com.facilio.db.criteria.Criteria;

public interface FacilioModuleFunction {
	public void add(List<Object> objects) throws Exception;
	public Map<String, Object> addTemplateData(List<Object> objects) throws Exception;
	public void update(List<Object> objects) throws Exception;
	public void delete(List<Object> objects) throws Exception;
	public Object fetch(List<Object> objects) throws Exception;
	public String export(List<Object> objects) throws Exception;
	public Map<String, Object> asMap(List<Object> objects) throws Exception;
	public Criteria getViewCriteria(List<Object> objects) throws Exception;
}
