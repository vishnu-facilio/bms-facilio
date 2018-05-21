package com.facilio.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.data.ServicePortalInfo;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FormulaField;

public interface ModuleBean extends RootBean {

	public FacilioModule getModule(long moduleId) throws Exception;
	
	public FacilioModule getModule(String moduleName) throws Exception;
	
	public List<FacilioModule> getAllSubModules(long moduleId) throws Exception;
	
	public List<FacilioModule> getAllSubModules(String moduleName) throws Exception;
	
	public List<FacilioModule> getSubModules(long moduleId, FacilioModule.ModuleType type) throws Exception;
	
	public List<FacilioModule> getSubModules(String moduleName, FacilioModule.ModuleType type) throws Exception;
	
	public FacilioModule getParentModule(long moduleId) throws Exception;
	
	public FacilioField getPrimaryField(String moduleName) throws Exception;
	
	public ArrayList<FacilioField> getAllFields(String moduleName) throws Exception;
	
	public FacilioField getField(long fieldId) throws Exception;
	
	public FacilioField getFieldFromDB(long fieldId) throws Exception;
	
	public FacilioField getField(String fieldName, String moduleName) throws Exception;
	
	public List<FormulaField> getFormulaFields (Collection<Long> dependentFields) throws Exception;
	
	public long addField(FacilioField field) throws Exception;
	
	public int updateField(FacilioField field) throws Exception;
	
	public int deleteField(long fieldId) throws Exception;
	
	public int deleteFields(List<Long> fieldIds) throws Exception;
	
	public int deleteModule(String moduleName) throws Exception;
	
	public long addModule(FacilioModule module) throws Exception;
	
	public void addSubModule(long parentModuleId, long childModuleId) throws Exception;
	
	public ServicePortalInfo getServicePortalInfo() throws Exception;
	
	public JSONObject getStateFlow(String module) throws  Exception;
}