<%@page import="java.net.URL"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="com.facilio.db.builder.GenericUpdateRecordBuilder"%>
<%@page import="com.facilio.modules.FieldUtil"%>
<%@page import="com.facilio.modules.FieldFactory"%>
<%@page import="com.facilio.modules.fields.FacilioField"%>
<%@page import="java.util.Map"%>
<%@page import="com.facilio.modules.ModuleFactory"%>
<%@page import="com.facilio.modules.FacilioModule"%>
<%@page import="java.io.OutputStream"%>
<%@page import="com.amazonaws.services.s3.model.PutObjectResult"%>
<%@page import="com.facilio.aws.util.AwsUtil"%>
<%@page import="com.facilio.aws.util.FacilioProperties"%>
<%@page import="java.io.File"%>
<%@page import="com.facilio.bmsconsole.util.ImageScaleUtil"%>
<%@page import="javax.imageio.ImageIO"%>
<%@page import="java.awt.image.BufferedImage"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="java.io.ByteArrayInputStream"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="com.facilio.services.factory.FacilioFactory"%>
<%@page import="com.facilio.services.filestore.FileStore"%>
<%@page import="java.io.InputStream"%>
<%@page import="org.apache.commons.collections4.CollectionUtils"%>
<%@page import="java.sql.SQLException"%>
<%@page import="com.facilio.db.builder.DBUtil"%>
<%@page import="com.facilio.db.transaction.FacilioConnectionPool"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.facilio.fs.FileInfo"%>
<%@page import="java.util.ArrayList"%>
<%@ page import="com.facilio.accounts.dto.Organization" %>
<%@ page import="com.facilio.accounts.util.AccountUtil" %>
<%@ page import="com.facilio.bmsconsole.commands.FacilioCommand" %>
<%@ page import="com.facilio.chain.FacilioChain" %>
<%@ page import="org.apache.commons.chain.Context" %>
<%@ page import="org.apache.log4j.LogManager" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.util.List" %>

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
	final String orgParam = request.getParameter("orgId");
    final class OrgLevelMigrationCommand extends FacilioCommand {
        private final Logger LOGGER = LogManager.getLogger(OrgLevelMigrationCommand.class.getName());
        @Override
        public boolean executeCommand(Context context) throws Exception {

            // Have migration commands for each org
            // Transaction is only org level. If failed, have to continue from the last failed org and not from first
            long orgId = AccountUtil.getCurrentOrg().getOrgId();
            try{
            long paramOrgId = Long.parseLong(orgParam);
            if (orgId != paramOrgId) {
            		return false;
            }
            
            LOGGER.info("Mig for file started");
            
            List<FileInfo> fileInfos = getFileInfos(orgId);
            if (CollectionUtils.isNotEmpty(fileInfos)) {
            		FileStore fs = FacilioFactory.getFileStore();
            		String rootPath = orgId + File.separator + "files";
	    			String bucketName = FacilioProperties.getConfig("s3.bucket.name");
	    			
            		int i = 0;
            		List<FileInfo> compressedInfos = new ArrayList<>();
	            	for(FileInfo fileInfo: fileInfos) {
	    				if (fileInfo.getFileId() > 0) {
	    					if (i % 1000 == 0) {
	    						LOGGER.info(i + " done for - " + orgId);
	    					}
	    					i++;
	    					if (fileInfo != null && fileInfo.getContentType().contains("image/")) {
	    						try(InputStream downloadStream = fs.readFile(fileInfo);) {
	    							if (downloadStream != null) {
		    							
		    							try(ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
		    								
		    								BufferedImage imBuff = ImageIO.read(downloadStream);
			    							ImageScaleUtil.compressImage(imBuff, baos, fileInfo.getContentType());
			    							
	    									byte[] imageInByte = baos.toByteArray();
	    									if (imageInByte != null && imageInByte.length < fileInfo.getFileSize()) {
	    										
	    										baos.flush();
	    										imBuff.flush();
	    										
	    										FileInfo info = new FileInfo();
	    										info.setFileId(fileInfo.getFileId());
    											info.setCompressedFileSize(imageInByte.length);
    											
    											if (!FacilioProperties.isDevelopment()) {
    												String resizedFilePath = rootPath + File.separator + fileInfo.getFileId()+"-compressed";
    												info.setCompressedFilePath(resizedFilePath);
    												try (ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);) {
	    												PutObjectResult rs = AwsUtil.getAmazonS3Client().putObject(bucketName, resizedFilePath, bis, null);
		    			    								if (rs != null) {
		    			    									compressedInfos.add(info);
		    			    								}
	    											}
    											}
    											else {
    												rootPath = FacilioProperties.getLocalFileStorePath();
    												if (StringUtils.isEmpty(rootPath)) {
    													ClassLoader classLoader = OrgLevelMigrationCommand.class.getClassLoader();
    													URL fcDataFolder = classLoader.getResource("");
    													rootPath = fcDataFolder.getFile();
    												}
    												rootPath += File.separator + "facilio-data" + File.separator + orgId + File.separator + "files";
    												String resizedFilePath = rootPath + File.separator + fileInfo.getFileId()+"-compressed";
    												info.setCompressedFilePath(resizedFilePath);
	    			    								File createFile = new File(resizedFilePath);
	    			    								createFile.createNewFile();
	    			    								try(OutputStream outputStream = new FileOutputStream(createFile)) {
	    			    									baos.writeTo(outputStream);
	    			    								}
	    			    								compressedInfos.add(info);
	    			    							}
	    									}
	    								}
	    							}
	    						}
	    						catch(Exception e) {
	    							LOGGER.error("error reading file - " + fileInfo.getFileId(), e);
	    							e.printStackTrace();
	    						}
	    					}
	    				}									
	    			}
	            	
	            	int count = updateCompressedEntries(compressedInfos, orgId);
	            	LOGGER.info("file mig done for org - " + orgId + " :: " + count);
            }
            }
            catch(Exception e) {
            		LOGGER.error("error migrating org - " + orgId, e);
            		e.printStackTrace();
            }
            return false;
        }
        
        List<FileInfo> getFileInfos(long orgId) throws Exception {
    		
	    		PreparedStatement pstmt = null;
	    		ResultSet rs = null;
	    		List<FileInfo> fileInfos = new ArrayList<>();
	    		try (Connection conn = FacilioConnectionPool.INSTANCE.getConnection();) {
	    			pstmt = conn.prepareStatement("SELECT * FROM FacilioFile WHERE ORGID=? AND (IS_DELETED IS NULL OR IS_DELETED = 0) AND COMPRESSED_FILE_PATH IS NULL AND CONTENT_TYPE LIKE 'image/%' ORDER BY FILE_ID");
	    			pstmt.setLong(1, orgId);
	    			
	    			rs = pstmt.executeQuery();
	    			while(rs.next()) {
	    				FileInfo fileInfo = new FileInfo();
	    				fileInfo.setOrgId(rs.getLong("ORGID"));
	    				fileInfo.setFileId(rs.getLong("FILE_ID"));
	    				if (rs.getString("FILE_NAME") != null) {
	    					fileInfo.setFileName(rs.getString("FILE_NAME").trim());
	    				}
	    				if (rs.getString("FILE_PATH") != null) {
    						fileInfo.setFilePath(rs.getString("FILE_PATH").trim());
    					}
    					fileInfo.setFileSize(rs.getLong("FILE_SIZE"));
	    				fileInfo.setContentType(rs.getString("CONTENT_TYPE"));
	    				fileInfo.setUploadedBy(rs.getLong("UPLOADED_BY"));
	    				fileInfo.setUploadedTime(rs.getLong("UPLOADED_TIME"));
	    				fileInfos.add(fileInfo);
	    			}
	    			return fileInfos;
	    		}
	    		catch(SQLException e) {
	    			LOGGER.error("Error in migration while fetching files for org " + orgId, e);
	    					e.printStackTrace();
	    		}
	    		finally {
	    			DBUtil.closeAll(pstmt, rs);
	    		}
	    		return null;
	    	}
        
        int updateCompressedEntries(List<FileInfo> fileInfos, long orgId) throws Exception {
        		if (fileInfos.isEmpty()) {
        			return 0;
        		}
        	
	        	FacilioModule module = ModuleFactory.getFilesModule();
	    		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFileFields());
	    		
	    		List<FacilioField> updateFields = new ArrayList<>();
	    		updateFields.add(fieldMap.get("compressedFilePath"));
	    		updateFields.add(fieldMap.get("compressedFileSize"));
	
	    		List<FacilioField> whereFields = new ArrayList<>();
	    		whereFields.add(fieldMap.get("orgId"));
	    		whereFields.add(fieldMap.get("fileId"));
	    		
	    		List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = fileInfos.stream().map(fileInfo -> {
	    			GenericUpdateRecordBuilder.BatchUpdateContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
	    			updateVal.addUpdateValue("compressedFilePath", fileInfo.getCompressedFilePath());
	    			updateVal.addUpdateValue("compressedFileSize", fileInfo.getCompressedFileSize());

	    			updateVal.addWhereValue("orgId", orgId);
	    			updateVal.addWhereValue("fileId", fileInfo.getFileId());
	    			return updateVal;
	    		}).collect(Collectors.toList());

	    		return new GenericUpdateRecordBuilder()
	    				.table(module.getTableName())
	    				.fields(updateFields)
	    				.batchUpdate(whereFields, batchUpdateList)
	    				;
        }
    }
%>

<%
    List<Organization> orgs = AccountUtil.getOrgBean().getOrgs();
    for (Organization org : orgs) {
        AccountUtil.setCurrentAccount(org.getOrgId());

        FacilioChain c = FacilioChain.getTransactionChain(7200_000);
        c.addCommand(new OrgLevelMigrationCommand());
        c.execute();

        AccountUtil.cleanCurrentAccount();
    }
%>