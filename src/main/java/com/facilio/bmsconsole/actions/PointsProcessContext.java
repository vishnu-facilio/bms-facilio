/**
 * 
 */
package com.facilio.bmsconsole.actions;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.simple.JSONArray;

import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

/**
 * @author facilio
 *
 */
public class PointsProcessContext extends ImportProcessContext{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long controllerId=-1;

	public long getControllerId() {
		return controllerId;
	}

	public void setControllerId(long controllerId) {
		this.controllerId = controllerId;
	}
	
	private HashMap<String, String> fieldMapping = new LinkedHashMap <String, String>();
	
	public void populatePointsFieldMapping() throws Exception
	{
		for(Object field: getFields())
		{
			this.fieldMapping.put((String)field,"-1");
		}
	}
	
	public JSONArray getFields()
	{
		JSONArray fields = new JSONArray();

		try {
			List<FacilioField> fieldsList = FieldFactory.getPointsFields();

			for(FacilioField field : fieldsList)
			{
				if(field.getName().equals("device") || field.getName().equals("instance") || field.getName().equals("categoryId") || field.getName().equals("resourceId") || field.getName().equals("fieldId") || field.getName().equals("unit")){
					fields.add(field.getName());
				}
			}
		}
		catch(Exception e) {
//			log.info("Exception occurred ", e);
		}
		return fields;
	}
	
}

