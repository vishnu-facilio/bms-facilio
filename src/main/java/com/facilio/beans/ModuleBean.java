package com.facilio.beans;

import java.util.ArrayList;

import com.facilio.bmsconsole.commands.data.ServicePortalInfo;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;

public interface ModuleBean extends RootBean {

	public FacilioModule getModule(String moduleName) throws Exception;
	
	public FacilioField getPrimaryField(String moduleName) throws Exception;
	
	public ArrayList<FacilioField> getAllFields(String moduleName) throws Exception;
	
	public FacilioField getField(long fieldId) throws Exception;
	
	public long addField(FacilioField field) throws Exception;
	
	public ServicePortalInfo getServicePortalInfo() throws Exception;

}