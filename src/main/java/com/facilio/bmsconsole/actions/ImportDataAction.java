package com.facilio.bmsconsole.actions;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.json.simple.JSONArray;

import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.transaction.FacilioConnectionPool;
import com.opensymphony.xwork2.ActionSupport;

public class ImportDataAction extends ActionSupport {
	
	// Upload xls , store the file and meta data(modulename/column heading/), generate importid
	public String upload()
	{
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		
		try {
			JSONArray columnheadings = new JSONArray();
			//columnheadings.
			columnheadings.add(0, "Name");
			columnheadings.add(1, "Desc");
			setColumnheading(columnheadings.toJSONString());
			System.out.println("Uploaded file "+fileUpload);
			long fileid = fs.addFile(fileUploadFileName, fileUpload, fileUploadContentType);
			String  insert = INSERTQUERY.replaceAll("#orguserid#", fs.getUserId()+"").replaceAll("#fileid#", fileid+"").replaceAll("#COLUMN_HEADING#", getColumnheading().replaceAll("\"", "\\\""));
			insert =insert.replaceAll("#module#", "1"); // 1 for energy data
			Connection conn  = FacilioConnectionPool.INSTANCE.getConnection();
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return SUCCESS;
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
		this.importprocessid = importprocessid;
	}

	private long importprocessid;
	private static String INSERTQUERY = "insert into ImportProcess(ORG_USERID,INSTANCE_ID,STATUS,FILEID,COLUMN_HEADING,IMPORT_TIME,IMPORT_TYPE) values (#orguserid#,#module#,1,#fileid#,'#COLUMN_HEADING#',UNIX_TIMESTAMP(),1)";
}
