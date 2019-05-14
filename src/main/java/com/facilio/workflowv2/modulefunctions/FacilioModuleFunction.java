package com.facilio.workflowv2.modulefunctions;

import java.util.List;

public interface FacilioModuleFunction {
	public void add(List<Object> objects) throws Exception;
	public void update(List<Object> objects) throws Exception;
	public void delete(List<Object> objects) throws Exception;
	public Object fetch(List<Object> objects) throws Exception;
}
