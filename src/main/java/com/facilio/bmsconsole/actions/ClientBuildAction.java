package com.facilio.bmsconsole.actions;

import com.amazonaws.services.s3.AmazonS3;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import static com.facilio.aws.util.AwsUtil.getAmazonS3Client;


public class ClientBuildAction extends ActionSupport {
    private static final Logger LOGGER = LogManager.getLogger(ClientBuildAction.class.getName());

    private String version;
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    public boolean checkIfBuildExists() {
        if (FacilioProperties.getEnvironment() == null) {
            return false;
        }
        if (FacilioProperties.isDevelopment()) {
            return true;
        }
        boolean objectExists = false;
        String staticBucket = FacilioProperties.getConfig("static.bucket");
        if(staticBucket != null) {
            AmazonS3 s3Client = getAmazonS3Client();
            objectExists = s3Client.doesObjectExist(staticBucket, this.version+"/js/app.js");
        }
        return objectExists;
    }

    public String updateVersion() throws Exception{
        Connection conn = null;
        PreparedStatement stmt = null;
        int updatedRows = 0;
        try {
            if(checkIfBuildExists()) {
                FacilioTransactionManager.INSTANCE.getTransactionManager().begin();

                conn = FacilioConnectionPool.INSTANCE.getConnection();

                stmt = conn.prepareStatement("Update ClientApp set version=?, updatedTime=?, is_new_client_build=?  WHERE environment=?");
                stmt.setString(1, this.version);
                stmt.setLong(2, System.currentTimeMillis());
                stmt.setBoolean(3, true);
                stmt.setString(4, FacilioProperties.getEnvironment());

                updatedRows = stmt.executeUpdate();
                FacilioTransactionManager.INSTANCE.getTransactionManager().commit();
            } else {
                LOGGER.info("Client version not found in S3: " + this.version + ", for environment: " + FacilioProperties.getEnvironment());
            }
        } catch (Exception e) {
            FacilioTransactionManager.INSTANCE.getTransactionManager().rollback();
            LOGGER.info("Exception while updating client version " + this.version + ", for environment: " + FacilioProperties.getEnvironment(), e);
        } finally {
            DBUtil.closeAll(conn, stmt);
        }

        if(updatedRows > 0) {
            LOGGER.info("Updated client build version : " + this.version + ", for environment: " + FacilioProperties.getEnvironment());
            return SUCCESS;
        } else {
            return ERROR;
        }
    }
}
