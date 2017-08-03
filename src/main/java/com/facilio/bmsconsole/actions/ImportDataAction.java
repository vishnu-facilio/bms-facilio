package com.facilio.bmsconsole.actions;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.transaction.FacilioConnectionPool;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.opensymphony.xwork2.ActionSupport;

public class ImportDataAction extends ActionSupport {
	
	// Upload xls , store the file and meta data(modulename/column heading/), generate importid
	public String upload() throws Exception
	{
		
		System.out.println("entry code"+ this.hashCode());

		// Test code 
		
		if(getImportprocessid()!=0)
		{
			System.out.println("secondtime code"+ this.hashCode());

			metainfo= ImportMetaInfo.getInstance(getImportprocessid());
			return SUCCESS;
		}
		else
		{System.out.println("code"+ this.hashCode());
			System.out.print("IMport id "+getImportprocessid());
		}
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		Connection conn =null;
		try {
			JSONArray columnheadings = new JSONArray();
			//columnheadings.
			columnheadings.add(0, "Power KWS");
			columnheadings.add(1, "Voltage");
			
			columnheadings.add( "Current R");
			columnheadings.add( "Voltage");
			
			columnheadings.add( "Current L");
			columnheadings.add( "Voltage");
			
			columnheadings.add( "Current Y");
			columnheadings.add( "Frequency");
			
			setColumnheading(columnheadings.toJSONString());
			System.out.println("Uploaded file "+fileUpload);
			long fileid = fs.addFile(fileUploadFileName, fileUpload, fileUploadContentType);
			String  insert = INSERTQUERY.replaceAll("#orguserid#", fs.getUserId()+"").replaceAll("#fileid#", fileid+"").replaceAll("#COLUMN_HEADING#", getColumnheading().replaceAll("\"", "\\\""));
			insert =insert.replaceAll("#module#", "1"); // 1 for energy data
			 conn  = FacilioConnectionPool.INSTANCE.getConnection();
			System.out.println(insert);
			PreparedStatement pstmt = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);	
			pstmt.executeUpdate();
			
			ResultSet rs = pstmt.getGeneratedKeys();
			long id = 0;
			if(rs.next())
			{
				id = rs.getLong(1);
				setImportprocessid(id);
				System.out.println("Added  object with id : "+id);
			}
			rs.close();
			pstmt.close();
			return upload();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try {
				if(conn!=null)
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return null;
		
		
	}
	// importid is argument, display the excel column heading vs module field names,
	public String displayColumnFieldMapping()
	{
		return SUCCESS;
	}
	// column heading vs field name mapping + importid
	
	public String processImport()
	{
		// based on the option selected in displayColumnFieldMapping, load the data from excel file and get it imported
		return SUCCESS;
	}
	public String showformupload()
	{
		
		System.out.println("Displaying formupload");
		return SUCCESS;
	}

	private File fileUpload;
	public File getFileUpload() {
		return fileUpload;
	}
	public void setFileUpload(File fileUpload) {
		this.fileUpload = fileUpload;
	}
	public String getFileUploadContentType() {
		return fileUploadContentType;
	}
	public void setFileUploadContentType(String fileUploadContentType) {
		this.fileUploadContentType = fileUploadContentType;
	}
	public String getFileUploadFileName() {
		return fileUploadFileName;
	}
	public void setFileUploadFileName(String fileUploadFileName) {
		this.fileUploadFileName = fileUploadFileName;
	}

	public String getColumnheading() {
		return columnheading;
	}
	public void setColumnheading(String columnheading) {
		this.columnheading = columnheading;
	}

	private String fileUploadContentType;
	private String fileUploadFileName;
	private String columnheading;
	public long getImportprocessid() {
		return importprocessid;
	}
	public void setImportprocessid(long importprocessid) {
		System.out.println("setting the id "+importprocessid);
		this.importprocessid = importprocessid;
	}

	private long importprocessid=0;
	private static String INSERTQUERY = "insert into ImportProcess(ORG_USERID,INSTANCE_ID,STATUS,FILEID,COLUMN_HEADING,IMPORT_TIME,IMPORT_TYPE) values (#orguserid#,#module#,1,#fileid#,'#COLUMN_HEADING#',UNIX_TIMESTAMP(),1)";

	public ImportMetaInfo getMetainfo() {
		return metainfo;
	}
	public void setMetainfo(ImportMetaInfo metainfo) {
		this.metainfo = metainfo;
	}

	ImportMetaInfo metainfo =new ImportMetaInfo();
}
class ImportMetaInfo
{
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
	public JSONArray getColumnHeadings()
	{
		return columnheadings;
	}
	public String toString()
	{
		return columnheadings.toJSONString() + "\n" + fields.toJSONString();
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
	
}
