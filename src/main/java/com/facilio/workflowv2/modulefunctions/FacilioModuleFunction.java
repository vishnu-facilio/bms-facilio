package com.facilio.workflowv2.modulefunctions;

import java.util.List;
import java.util.Map;

import com.facilio.db.criteria.Criteria;

public interface FacilioModuleFunction {
	public void add(Map<String,Object> globalParams,List<Object> objects) throws Exception;
	public Map<String, Object> addTemplateData(Map<String,Object> globalParams,List<Object> objects) throws Exception;
	public void update(Map<String,Object> globalParams,List<Object> objects) throws Exception;
	public void delete(Map<String,Object> globalParams,List<Object> objects) throws Exception;
	public Object fetch(Map<String,Object> globalParams,List<Object> objects) throws Exception;
	public String export(Map<String,Object> globalParams,List<Object> objects) throws Exception;
	public Map<String, Object> asMap(Map<String,Object> globalParams,List<Object> objects) throws Exception;
	public Criteria getViewCriteria(Map<String,Object> globalParams,List<Object> objects) throws Exception;
	public Long getId(Map<String,Object> globalParams,List<Object> objects) throws Exception;
}
