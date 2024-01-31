package com.facilio.bmsconsole.actions;

import com.amazonaws.services.s3.AmazonS3;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.client.app.beans.ClientAppBean;
import com.facilio.client.app.pojo.ClientAppConfig;
import com.facilio.client.app.util.ClientAppUtil;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.opensymphony.xwork2.ActionSupport;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.MessageFormat;

import static com.facilio.aws.util.AwsUtil.getAmazonS3Client;

@Log4j
@Getter
@Setter
public class ClientBuildAction extends ActionSupport {

    private String version;
    private String appName;
    private String orgGrouping;

    public boolean checkIfBuildExists(ClientAppConfig config, String version) throws Exception {
        String indexUrl = ClientAppUtil.clientAppBean().getClientAppInfo(config.getName(), version, false).getStaticUrl() + config.getAppType().getFileName();
        try (InputStream inputStream = new URL(indexUrl).openStream())
        {
            String html = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            return true;
        }
        catch (Exception e) {
            LOGGER.error("Error occurred while checking if client index.html is presenting while updating version", e);
            return false;
        }
    }

    public String updateVersion() throws Exception {
        if (StringUtils.isEmpty(version)) {
            LOGGER.info("Version cannot be for updating client app");
            return ERROR;
        }
        String appName = StringUtils.isEmpty(getAppName()) ? ClientAppBean.DEFAULT_CLIENT_APP : getAppName();
        String orgGrouping = StringUtils.isEmpty(getOrgGrouping()) ? ClientAppBean.DEFAULT_ORG_GROUPING : getOrgGrouping();
        ClientAppConfig config = ClientAppUtil.getClientAppConfig(appName);
        try {
            if (config == null) {
                LOGGER.info(MessageFormat.format("Invalid appName ({0}) for updating client app", appName));
                return ERROR;
            }
            if (checkIfBuildExists(config, version)) {
                ClientAppUtil.clientAppBean().insertOrUpdateClientVersion(version, appName, orgGrouping);
                return SUCCESS;
            } else {
                LOGGER.info("Client version not found in S3: " + this.version + ", for environment: " + FacilioProperties.getEnvironment());
                return ERROR;
            }
        }
        catch (Exception e) {
            LOGGER.error("Error occurred while trying to update client app version");
            return ERROR;
        }
    }
}
