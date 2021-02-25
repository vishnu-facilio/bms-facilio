package com.facilio.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.SystemException;

import com.facilio.constants.FacilioConstants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.service.FacilioService;
import com.facilio.services.factory.FacilioFactory;

public class ServerUtils {


    private static final Logger LOGGER = LogManager.getLogger(ServerUtils.class.getName());
    public static Map<String, Object> getClientInfoAsService() throws Exception {
        return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> getClientInfo());
    }
    public static Map<String, Object> getClientInfo() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs;
        String clientVersion = null;
        boolean isNewClientBuild=false;

        try {
            if(FacilioProperties.getEnvironment() != null ) {
                conn = FacilioConnectionPool.INSTANCE.getConnection();
                pstmt = conn.prepareStatement("SELECT * FROM ClientApp WHERE environment=?");
                pstmt.setString(1, FacilioProperties.getEnvironment());
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    clientVersion = rs.getString("version");
                    isNewClientBuild=rs.getBoolean("is_new_client_build");

                }
            }
        } catch(SQLException | RuntimeException e) {
            LOGGER.info("Exception while verifying password, ", e);
        } finally {
            DBUtil.closeAll(conn, pstmt);
        }
        Map<String, Object> clientInfo=new HashMap<String, Object>();
        clientInfo.put("version",clientVersion);
        clientInfo.put("isNewClientBuild", isNewClientBuild);
        return clientInfo;
    }

    public static int updateClientVersion(String newVersion, boolean isNewClientBuild) throws Exception {
        com.facilio.accounts.dto.User currentUser = AccountUtil.getCurrentUser();
        if (currentUser != null) {
            return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> updateClientVersionervice(newVersion, isNewClientBuild, currentUser.getId()));
        }
        else {
            throw new IllegalArgumentException("Current User cannot be null while updating Client Version");
        }
    }

    private static int updateClientVersionervice(String newVersion,boolean isNewClientBuild, long userId) throws SystemException {
        int updatedRows = 0;
        if(newVersion != null) {
            newVersion = newVersion.trim();
            newVersion = newVersion.replace("/", "");
            Connection conn = null;
            PreparedStatement pstmt = null;
            try {
                if(FacilioFactory.getFileStore().isFileExists(newVersion)) {
                    if (FacilioProperties.getEnvironment() != null && userId != -1) {
                        FacilioTransactionManager.INSTANCE.getTransactionManager().begin();
                        conn = FacilioConnectionPool.INSTANCE.getConnection();
                        pstmt = conn.prepareStatement("Update ClientApp set version=?, updatedTime=?, updatedBy=?, is_new_client_build=?  WHERE environment=?");
                        pstmt.setString(1, newVersion);
                        pstmt.setLong(2, System.currentTimeMillis());
                        pstmt.setLong(3, userId);
                        pstmt.setBoolean(4, isNewClientBuild);
                        pstmt.setString(5, FacilioProperties.getEnvironment());

                        updatedRows = pstmt.executeUpdate();
                        if(updatedRows > 0) {
                            LOGGER.info("Updated client version successfully");
                        }
                        FacilioTransactionManager.INSTANCE.getTransactionManager().commit();
                    }
                }
            } catch (Exception e) {
                FacilioTransactionManager.INSTANCE.getTransactionManager().rollback();
                LOGGER.info("Exception while updating client version, ", e);
            } finally {
                DBUtil.closeAll(conn, pstmt);
            }
        }
        return updatedRows;
    }

    public static String getUserId() {
        return FacilioProperties.getConfig("user.id");
    }



}
