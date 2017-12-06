package com.facilio.bmsconsole.actions;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.json.simple.JSONArray;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.data.ProcessXLS;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.transaction.FacilioConnectionPool;
import com.opensymphony.xwork2.ActionSupport;

public class ImportDataAction extends ActionSupport {
	
	// Upload xls , store the file and meta data(modulename/column heading/), generate importid
	public String upload() throws Exception
	{
		
		System.out.println("entry code"+ this.hashCode());

		if(getImportprocessid()!=0)
		{
			System.out.println("secondtime code"+ this.hashCode());
			metainfo= ImportMetaInfo.getInstance(getImportprocessid());
			
			//Here: We are checking whether the field mapping exists for the same column headings..
			//if already exists.. setting the old fieldMapping to display the mappings in the form set with old mappings 
			// when users upload similar set of data with same mapping frequently..
			long orgId = AccountUtil.getCurrentOrg().getOrgId();
			String checkQuery="select FIELD_MAPPING from ImportProcess where FIELD_MAPPING IS NOT NULL AND ORG_USERID=? AND COLUMN_HEADING=? AND INSTANCE_ID=? ORDER BY IMPORT_TIME DESC LIMIT 1";

			try(Connection conn  = FacilioConnectionPool.INSTANCE.getConnection();
					PreparedStatement pstmtCheck=conn.prepareStatement(checkQuery))
			{
				pstmtCheck.setObject(1, orgId);
				pstmtCheck.setObject(2, getColumnheading());
				pstmtCheck.setObject(3, metainfo.getModule().getModuleId());
				//getColumnheading is used instead of metainfo.columnheading to avoid unnecesssary JSONParsing.
				try(ResultSet fieldMapSet= pstmtCheck.executeQuery())
				{
					if (fieldMapSet.next())
					{
						String fieldMapping	= fieldMapSet.getString(1);
						System.out.println("Obtained from db"+fieldMapping);
						if(fieldMapping!=null && !fieldMapping.trim().isEmpty())
						{
							metainfo.setFieldMapping(metainfo.getFieldMap(fieldMapping));
						}
					}
					else
					{
						metainfo.populateFieldMapping();
					}
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			return SUCCESS;
		}
		else
		{System.out.println("code"+ this.hashCode());
			System.out.print("IMport id "+getImportprocessid());
		}
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		Connection conn =null;
		try {
			JSONArray columnheadings = ProcessXLS.getColumnHeadings(fileUpload);
						
			setColumnheading(columnheadings.toJSONString().replaceAll("\"", "\\\""));
			long fileid = fs.addFile(fileUploadFileName, fileUpload, fileUploadContentType);
			String  insert = INSERTQUERY.replaceAll("#orguserid#", fs.getUserId()+"").replaceAll("#fileid#", fileid+"").replaceAll("#COLUMN_HEADING#", getColumnheading());
	        
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	        FacilioModule facilioModule = modBean.getModule(getModuleName());
			
			System.out.println("Module -- "+metainfo.getModule());
			insert =insert.replaceAll("#module#", String.valueOf(facilioModule.getModuleId()));
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
	
	
	public String getAssets() throws Exception
	{
		switch(moduleName)
		{
		  
		case "Energy" : 
			this.energyMeters=DeviceAPI.getAllEnergyMeters();
		
		}
		return SUCCESS;
	}
	
	
	public String processImport() throws Exception
	{
		System.out.println("Meta info"+getMetainfo());
		System.out.println("Meta info"+getMetainfo().getFieldMapping());
		// based on the option selected in displayColumnFieldMapping, load the data from excel file and get it imported
		
		String updatequery = UPDATEQUERY.replaceAll("#FIELD_MAPPING#", getMetainfo().getFieldMappingJSON().toJSONString()).replaceAll("#IMPORTID#", String.valueOf(getMetainfo().getImportprocessid()));
		java.sql.Connection c = FacilioConnectionPool.getInstance().getConnection();
		Statement stmt = c.createStatement();
		System.out.println("the column mapping is "+updatequery);
		stmt.executeUpdate(updatequery);
		stmt.close();
		c.close();
		
		long fileId = ImportMetaInfo.getInstance(getMetainfo().getImportprocessid()).getFileId();
		metainfo.setFileId(fileId);
		metainfo.setAssetId(assetId);
		ProcessXLS.processImport(metainfo);
		
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

	private static String UPDATEQUERY = "update  ImportProcess set FIELD_MAPPING='#FIELD_MAPPING#' where IMPORTID_ID=#IMPORTID#";	

	public ImportMetaInfo getMetainfo() {
		return metainfo;
	}
	public void setMetainfo(ImportMetaInfo metainfo) {
		this.metainfo = metainfo;
	}

	ImportMetaInfo metainfo =new ImportMetaInfo();
	
	
	public void setModuleName(String module)
	{
		this.moduleName=module;
	}
	
	public String getModuleName()
	{
		return this.moduleName;
	}
	
	private List<EnergyMeterContext> energyMeters;
	public List<EnergyMeterContext> getEnergyMeters() {
		return energyMeters;
	}
	public void setAssetId(long id) {
		this.assetId = id;
	}
	private long assetId;
	private String moduleName="Energy";
}
