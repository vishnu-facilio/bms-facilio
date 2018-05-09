package com.facilio.bmsconsole.actions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.transaction.FacilioConnectionPool;

public class ImportMetaInfo
{
	public ImportMetaInfo(){
		System.out.println("Import id "+hashCode());
	}
	long importprocessid=0;
	public long getImportprocessid() {
		return importprocessid;
	}
	public void setImportprocessid(long importprocessid) {
		System.out.println("setting the id "+importprocessid);
		this.importprocessid = importprocessid;
	}
	
	long fileId=0;
	public long getFileId() {
		return fileId;
	}
	public void setFileId(long fileId) {
		this.fileId = fileId;
	}
	
	long assetId=0;
	public void setAssetId(long id)
	{
		this.assetId=id;
	}
	
	public long getAssetId()
	{
		return assetId;
	}
	public static ImportMetaInfo getInstance(long processid) throws Exception
	{
		
		Connection c = null;
		Statement s = null;
		ResultSet rs =null;
		try {
			 c = FacilioConnectionPool.getInstance().getConnection();
			 s = c.createStatement();
			 String sql = "select * from ImportProcess where IMPORTID_ID="+processid;
			 System.out.println("sql----"+sql);
			 rs = s.executeQuery(sql);
			 System.out.println("rs ---"+rs);
			if(rs.next())
			{
				System.out.println("-----------> checking getsites5----------->");
				ImportMetaInfo iminfo = new ImportMetaInfo();
				JSONParser parser = new JSONParser();
				iminfo.columnheadings =(JSONArray)parser.parse(rs.getString("COLUMN_HEADING"));
				System.out.println("columnheadeing"+iminfo.columnheadings);
				int moduleId = rs.getInt("INSTANCE_ID");
				iminfo.setFileId(rs.getLong("FILEID"));
				System.out.println("-----------> checking getsites6----------->");
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		        FacilioModule facilioModule = modBean.getModule(moduleId);
				iminfo.fields = getFields(facilioModule.getName());
				iminfo.setImportprocessid(processid);
				iminfo.setModule(facilioModule);
				iminfo.getFacilioFieldMapping(facilioModule.getName());
				System.out.println("fields"+iminfo.fields);
				return iminfo;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			rs.close();
			s.close();
			c.close();
		}
		return null;
	}
	JSONArray columnheadings = null;
	JSONArray fields = null;
	public JSONArray getFields()
	{
		return fields;
	}
	public  JSONArray getColumnHeadings()
	{
		return columnheadings;
	}
	public String toString()
	{
		System.out.println("import processid " +importprocessid);

		System.out.println("fieldMapping" +fieldMapping);
		
		System.out.println("columnheadings" +columnheadings);
		
		System.out.println("fields" +fields);
		
		return columnheadings==null?"":columnheadings.toJSONString() + "\n" + (fields==null?"":fields.toJSONString())+"\n Field mapping"+fieldMapping;
	}
	
	public FacilioModule getModule() {
		return module;
	}
	public void setModule(FacilioModule module) {
		this.module = module;
	}
	public void setModule(String moduleName) throws Exception {
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		this.module = bean.getModule(moduleName);
	}
	private FacilioModule module;
	
	
	
	public static JSONArray getFields(String module)
	{
		JSONArray fields = new JSONArray();
		
		try {
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			ArrayList<FacilioField> fieldsList= bean.getAllFields(module);

			for(FacilioField field : fieldsList)
			{
				if(!isRemovableField(field.getName()))
				{
					fields.add(field.getName());
				}
			}
			if(module.equals(FacilioConstants.ContextNames.ASSET)) {
				
				fields.remove("space");
				fields.remove("localId");
				fields.remove("resourceType");
				
				fields.add("site");
				fields.add("building");
				fields.add("floor");
				fields.add("spaceName");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return fields;
	}
	
	private Map<String, FacilioField> facilioFieldMapping = null;
	
	public Map<String, FacilioField> getFacilioFieldMapping(String module) {
		if(facilioFieldMapping == null) {
			try {
				
				facilioFieldMapping = new HashMap<String, FacilioField>();
				ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				ArrayList<FacilioField> fieldsList= bean.getAllFields(module);

				for(FacilioField field : fieldsList)
				{
					if(!isRemovableField(field.getName()))
					{
						facilioFieldMapping.put(field.getName(), field);
					}
				}
				return facilioFieldMapping;
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		return facilioFieldMapping;
	}
	
	private static boolean isRemovableField(String name) {

		switch (name){ 

		case "parentId":
		case "date":
		case "month":
		case "week":
		case "day":
		case "hour":{
			return true;
		}
		}
		return false;
	}
	public void populateFieldMapping()
	{
		for(Object field: fields)
		{
			this.fieldMapping.put((String)field,"-1");
		}
	}
	
	private HashMap<String, String> fieldMapping = new LinkedHashMap <String, String> ();
	
	public HashMap<String, String> getFieldMapping() {
		return fieldMapping;
	}
	public void setFieldMapping(HashMap<String, String> fieldMapping) {
		this.fieldMapping = fieldMapping;
	}
	public String getFieldValue(String key) {

		return (String)fieldMapping.get(key);
	}
	public void setFieldMapping(String key,String Value) {
		this.fieldMapping.put(key, Value);
	}
	
	public JSONObject getFieldMappingJSON()
	{
		JSONObject json = new JSONObject();
		Iterator<String> keys = fieldMapping.keySet().iterator();
		while(keys.hasNext())
		{
			String key = keys.next();
			String value = fieldMapping.get(key);
			json.put(key, value);
		}
		return json;
	}
	
	public HashMap<String,String> getFieldMap(String jsonString) throws Exception
	{
		HashMap <String,String> fieldMap = new LinkedHashMap<String,String>();
		JSONParser parser = new JSONParser();
		JSONObject json=(JSONObject)parser.parse(jsonString);
		Iterator keys = json.keySet().iterator();
		while(keys.hasNext())
		{
			String key =(String) keys.next();
			String values = (String) json.get(key);
			fieldMap.put(key, values);
		}
		return fieldMap;
	}
	
}