package com.facilio.bmsconsole.actions;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

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
	public static ImportMetaInfo getInstance(long processid) throws Exception
	{
		
		Connection c = null;
		Statement s = null;
		ResultSet rs =null;
		try {
			 c = FacilioConnectionPool.getInstance().getConnection();
			 s = c.createStatement();
			 rs = s.executeQuery("select * from ImportProcess where IMPORTID_ID="+processid);
			if(rs.next())
			{
				ImportMetaInfo iminfo = new ImportMetaInfo();
				JSONParser parser = new JSONParser();
				iminfo.columnheadings =(JSONArray)parser.parse(rs.getString("COLUMN_HEADING"));
				System.out.println("columnheadeing"+iminfo.columnheadings);
				int instanceid = rs.getInt("INSTANCE_ID");
				iminfo.fields = getFields(instanceid);
				iminfo.setImportprocessid(processid);
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
		
		return columnheadings==null?"":columnheadings.toJSONString() + "\n" + (fields==null?"":fields.toJSONString())+"\n Field mapping"+fieldMapping;
	}
	public static JSONArray getFields(int moduletype)
	{
		JSONArray fields = new JSONArray();
		switch(moduletype)
		{
		case 1:
			fields.add("TOTAL_ENERGY_CONSUMPTION");
			fields.add("TOTAL_ENERGY_CONSUMPTION_DELTA");
			fields.add("LINE_VOLTAGE_R");
			fields.add("TOTAL_ENERGY_CONSUMPTION");
			fields.add("TOTAL_ENERGY_CONSUMPTION_DELTA");
			fields.add("LINE_VOLTAGE_R");
			fields.add("LINE_VOLTAGE_Y");
			fields.add("LINE_VOLTAGE_B");
			fields.add("PHASE_VOLTAGE_R");
			
			return fields;
		}
		return null;
			
			
	}
	
	public static JSONArray getColumnHeadings(File excelfile)
	{
		JSONArray columnheadings = new JSONArray();
		//columnheadings.
		columnheadings.add(0, "PowerKWS");
		columnheadings.add(1, "Voltage");
		
		columnheadings.add( "Current R");
		columnheadings.add( "Voltage");
		
		columnheadings.add( "Current  L");
		columnheadings.add( "phase Voltage");
		
		columnheadings.add( "Current Y");
		columnheadings.add( "Frequency");
		return columnheadings;
	}
	
	private HashMap<String,Object> fieldMapping = new HashMap<String,Object>();
	public HashMap<String,Object> getFieldMapping() {
		return fieldMapping;
	}
	public void setFieldMapping(HashMap<String,Object> fieldMapping) {
		this.fieldMapping = fieldMapping;
	}
	public String getFieldMapping(String key) {

		return (String)fieldMapping.get(key);
	}
	public void setFieldMapping(String key,String Value) {
		this.fieldMapping.put(key, Value);
	}
	
	
	
}