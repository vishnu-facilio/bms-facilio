

<%@page import="java.util.stream.Collectors"%>
<%@ page import="com.facilio.bmsconsole.commands.FacilioCommand"%>
<%@ page import="org.apache.commons.chain.Context"%>
<%@page import="com.facilio.aws.util.AwsUtil"%>
<%@page import="com.amazonaws.services.s3.model.PutObjectResult"%>
<%@page import="java.io.ByteArrayInputStream"%>
<%@page import="com.facilio.db.builder.DBUtil"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="com.facilio.db.transaction.FacilioConnectionPool"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.net.URL"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="com.facilio.aws.util.FacilioProperties"%>
<%@page import="java.io.InputStream"%>
<%@page import="com.facilio.services.filestore.LocalFileStore"%>
<%@page import="java.io.File"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.io.OutputStream"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="com.facilio.bmsconsole.util.ImageScaleUtil"%>
<%@page import="javax.imageio.ImageIO"%>
<%@page import="com.facilio.accounts.dto.Organization"%>
<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@page import="com.facilio.beans.ModuleBean"%>
<%@page import="org.apache.commons.collections4.CollectionUtils"%>
<%@page import="com.facilio.bmsconsole.util.TenantsAPI"%>
<%@page import="com.facilio.chain.FacilioChain"%>
<%@page import="java.util.Arrays"%>
<%@page import="com.facilio.fw.BeanFactory"%>
<%@page import="com.facilio.modules.FacilioModule"%>
<%@page import="com.facilio.modules.FieldType"%>
<%@page import="com.facilio.modules.SelectRecordsBuilder"%>
<%@page import="com.facilio.bmsconsole.context.AttachmentContext"%>
<%@page import="org.apache.commons.chain.Context"%>
<%@page import="org.apache.log4j.LogManager"%>
<%@ page import="org.apache.log4j.Logger"%>
<%@ page import="org.apache.log4j.LogManager"%>
<%@ page import="com.facilio.accounts.dto.Organization"%>
<%@page import="org.apache.commons.collections4.CollectionUtils"%>
<%@page import="com.facilio.bmsconsole.tenant.TenantContext"%>
<%@page import="com.facilio.modules.SelectRecordsBuilder"%>
<%@ page import="java.util.List"%>
<%@ page import="com.facilio.db.builder.GenericInsertRecordBuilder"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.facilio.modules.ModuleFactory"%>
<%@ page import="com.facilio.accounts.util.AccountUtil"%>
<%@ page import="com.facilio.chain.FacilioChain"%>
<%@ page import="com.facilio.fw.BeanFactory"%>
<%@ page import="com.facilio.beans.ModuleBean"%>
<%@ page import="com.facilio.constants.FacilioConstants"%>
<%@ page import="com.facilio.modules.FacilioModule"%>
<%@ page import="com.facilio.modules.fields.FacilioField"%>
<%@ page import="com.facilio.modules.FieldType"%>
<%@ page import="com.facilio.modules.fields.LookupField"%>
<%@ page import="com.facilio.modules.fields.FacilioField.FieldDisplayType" %>
<%@ page import="com.facilio.modules.FacilioModule"%>
<%@ page import="com.facilio.beans.ModuleBean"%>
<%@ page import="com.facilio.fw.BeanFactory"%>
<%@ page import="com.facilio.constants.FacilioConstants"%>
<%@ page import="com.facilio.modules.fields.FacilioField"%>
<%@ page import="java.util.Arrays"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.facilio.modules.FieldType"%>
<%@ page import="com.facilio.constants.FacilioConstants"%>
<%@ page import="com.facilio.modules.FieldType"%>
<%@ page import="com.facilio.beans.ModuleBean"%>
<%@ page import="com.facilio.fw.BeanFactory"%>
<%@ page import="com.facilio.modules.FacilioModule"%>
<%@ page import="com.facilio.modules.SelectRecordsBuilder"%>
<%@ page import="com.facilio.fs.FileInfo"%>
<%@ page import="com.facilio.services.factory.FacilioFactory"%>
<%@ page import="java.awt.image.BufferedImage"%>
<%@ page import="java.io.FileInputStream"%>
<%@ page import="com.facilio.services.filestore.FileStore"%>

<%--

  _____                      _          _                              _   _            _                       __                           _
 |  __ \                    | |        | |                            | | | |          | |                     / _|                         | |
 | |  | | ___    _ __   ___ | |_    ___| |__   __ _ _ __   __ _  ___  | |_| |__   ___  | |__   __ _ ___  ___  | |_ ___  _ __ _ __ ___   __ _| |_
 | |  | |/ _ \  | '_ \ / _ \| __|  / __| '_ \ / _` | '_ \ / _` |/ _ \ | __| '_ \ / _ \ | '_ \ / _` / __|/ _ \ |  _/ _ \| '__| '_ ` _ \ / _` | __|
 | |__| | (_) | | | | | (_) | |_  | (__| | | | (_| | | | | (_| |  __/ | |_| | | |  __/ | |_) | (_| \__ \  __/ | || (_) | |  | | | | | | (_| | |_
 |_____/ \___/  |_| |_|\___/ \__|  \___|_| |_|\__,_|_| |_|\__, |\___|  \__|_| |_|\___| |_.__/ \__,_|___/\___| |_| \___/|_|  |_| |_| |_|\__,_|\__|
                                                           __/ |
                                                          |___/

--%>

<%
    final class OrgLevelMigrationCommand extends FacilioCommand {
        private final Logger LOGGER = LogManager.getLogger(OrgLevelMigrationCommand.class.getName());
        @Override
        public boolean executeCommand(Context context) throws Exception {

            // Have migration commands for each org
            // Transaction is only org level. If failed, have to continue from the last failed org and not from first
            
           	List<Long> orgIds = Arrays.asList(75l,93l,116l,125l,155l,168l,172l,173l,210l);
            		long orgId = AccountUtil.getCurrentOrg().getOrgId();
			if (!orgIds.contains(orgId) && (FacilioProperties.isProduction() || (orgId  == 183))) {
				try {
		            	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		            FacilioModule ticketAttachmentModule = modBean.getModule("ticketattachments");
		                
		            	SelectRecordsBuilder<AttachmentContext> builder = new SelectRecordsBuilder<AttachmentContext>()
								.module(ticketAttachmentModule)
								.beanClass(AttachmentContext.class)
								.select(modBean.getAllFields("ticketattachments"))
								;
		             
		            	List<AttachmentContext> attachments = builder.get();
 		            	addFile(attachments);
 		            	
 		            FacilioModule taskAttachmentModule = modBean.getModule("taskattachments");
 		            	SelectRecordsBuilder<AttachmentContext> taskBuilder = new SelectRecordsBuilder<AttachmentContext>()
 								.module(taskAttachmentModule)
 								.beanClass(AttachmentContext.class)
 								.select(modBean.getAllFields("taskattachments"))
 								;
 		             
 		            	List<AttachmentContext> taskAttachments = taskBuilder.get();
 		            	addFile(taskAttachments);
 		            	
 		            	
 		            }catch (Exception e){
 		                LOGGER.error("Exception while migrating AgentMetrics fields for org: "+orgId, e);
 		                throw e;
 		            }
 				}
            
            
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule woModule = modBean.getModule("workorder");
			FacilioField field = modBean.getField("deviationTaskUniqueId", "workorder");
			if (field == null) {
				field = new FacilioField(woModule, "deviationTaskUniqueId", "Deviation Task Unique Id",FieldDisplayType.TEXTBOX, "DEVIATION_TASK_UNIQUE_ID", FieldType.STRING, false,true, true, false);
				modBean.addField(field);
			}
			
			LOGGER.info("file mig done for org - " + orgId);

            return false;
        }
        
        private void addFile(List<AttachmentContext> taskAttachments) throws Exception {
	    		if (CollectionUtils.isNotEmpty(taskAttachments)) {
	    			FileStore fs = FacilioFactory.getFileStore();
	    			List<Long> fileIds = taskAttachments.stream().map(a -> a.getFileId()).collect(Collectors.toList());
	    			List<Long> resizedIds = getResizedEntries(fileIds);
	    			List<Long> ids = fileIds.stream().filter(id -> !resizedIds.contains(id)).collect(Collectors.toList());
	    			if (ids.isEmpty()) {
	    				return;
	    			}
	    			
	    			List<FileInfo> fileInfoList = fs.getFileInfo(ids);
	    			for(FileInfo fileInfo: fileInfoList) {
	    				if (fileInfo.getFileId() > 0) {
	    					System.out.print("fileInfo" + fileInfo);
	    					if (fileInfo != null && fileInfo.getContentType().contains("image/")) {
	
	    						InputStream downloadStream = fs.readFile(fileInfo);
	
	    						if (downloadStream != null) {
	    							BufferedImage imBuff = ImageIO.read(downloadStream);
	    							BufferedImage out = ImageScaleUtil.resizeImage(imBuff, 360, 360);
	
	    							ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    							ImageIO.write(out, "png", baos);
	    							baos.flush();
	    							byte[] imageInByte = baos.toByteArray();
	    							baos.close();
	    							ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
	
	    							String rootPath = AccountUtil.getCurrentOrg().getOrgId() + File.separator + "files";
	    							String resizedFilePath = rootPath + File.separator + fileInfo.getFileId()+"-resized-"+360+"x"+360;
	    							String bucketName = FacilioProperties.getConfig("s3.bucket.name");
	    							PutObjectResult rs = AwsUtil.getAmazonS3Client().putObject(bucketName, resizedFilePath, bis, null);
	    							if (rs != null) {  
	
	    								Connection conn = null;
	    								PreparedStatement pstmt = null;
	    								try {
		    								conn = FacilioConnectionPool.INSTANCE.getConnection();
		
		    								pstmt = conn.prepareStatement("INSERT INTO ResizedFile set FILE_ID=?, ORGID=?, WIDTH=?, HEIGHT=?, FILE_PATH=?, FILE_SIZE=?, CONTENT_TYPE=?, GENERATED_TIME=?");
		    								pstmt.setLong(1, fileInfo.getFileId());
		    								pstmt.setLong(2, AccountUtil.getCurrentOrg().getOrgId());
		    								pstmt.setInt(3, 360);
		    								pstmt.setInt(4, 360);
		    								pstmt.setString(5, resizedFilePath);
		    								pstmt.setLong(6, imageInByte.length);
		    								pstmt.setString(7, "image/png");
		    								pstmt.setLong(8, System.currentTimeMillis());
		    								pstmt.executeUpdate();
	    								}
	    								catch(SQLException | RuntimeException e) {
	    									throw e;
	    								}
	    								finally {
	    									DBUtil.closeAll(conn, pstmt);
	    								}
	    							}
	
	    						}
	    					}
	    				}									
	    			}
	    		}
	    	}
        
        private List<Long> getResizedEntries(List<Long> ids) throws Exception {
	        	Connection conn = null;
	    		PreparedStatement pstmt = null;
	    		ResultSet rs = null;
	    		List<Long> fileIds = new ArrayList<>();
	    		try {
	    			conn = FacilioConnectionPool.INSTANCE.getConnection();
	    			
	    			String sql = "SELECT ResizedFile.FILE_ID FROM ResizedFile WHERE ResizedFile.ORGID=? AND FILE_ID IN (";
	    			for (int i=0; i< ids.size(); i++) {
	    				if (i != 0) {
	    					sql += ", ";
	    				}
	    				sql += ids.get(i);
	    			}
	    			sql += ")";
	    			
	    			pstmt = conn.prepareStatement(sql);
	    			pstmt.setLong(1, AccountUtil.getCurrentOrg().getOrgId());
	    			rs = pstmt.executeQuery();
	    			while(rs.next()) {
	    				fileIds.add(rs.getLong("FILE_ID"));
	    			}
	    		}
	    		catch(SQLException e) {
	    			LOGGER.error(e);
	    			throw e;
	    		}
	    		finally {
	    			DBUtil.closeAll(conn, pstmt, rs);
	    		}
	    		return fileIds;
        }
    }
%>

<%
    List<Organization> orgs = AccountUtil.getOrgBean().getOrgs();
    for (Organization org : orgs) {
        AccountUtil.setCurrentAccount(org.getOrgId());
        FacilioChain c = FacilioChain.getTransactionChain();
        c.addCommand(new OrgLevelMigrationCommand());
        c.execute();

        AccountUtil.cleanCurrentAccount();
    }
    out.println("Done");
%>
