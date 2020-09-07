package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.upgrade.AgentVersionApi;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.service.FacilioService;
import com.facilio.services.factory.FacilioFactory;
import com.opensymphony.xwork2.ActionSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facilio.modules.ModuleFactory.getAgentVersionLogModule;
import static com.facilio.modules.ModuleFactory.getAgentVersionModule;

public class AgentDownloadAction extends ActionSupport {
    private static final Logger LOGGER = LogManager.getLogger(AgentDownloadAction.class.getName());
    JSONObject result = new JSONObject();
    public String token;
    private String orgId;
    private String agentId;
    private String version;
    public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public FileInputStream getFileInputStream() {
        return fileInputStream;
    }

    public void setFileInputStream(FileInputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }
    //will be downloaded from the url from Agent_Version table
    FileInputStream fileInputStream ;

    public AgentDownloadAction(){
    }

    public JSONObject getResult() {
        return result;
    }

    public void setResult(JSONObject result) {
        this.result = result;
    }
    
    private String contentType="application/octet-stream";
	public String getContentType() {
		return contentType;
	}
	protected void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	private String filename;
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}

    public String downloadAgent() throws Exception {
        try {
            LOGGER.info("Download Agent called agent Id :"+agentId);
            String path = null;
          
            if(StringUtils.isNotEmpty(getToken())) {
            	path = getExeUrl(getToken());
            }
            else {
            	FacilioContext context = new FacilioContext();
            	context.put(AgentConstants.IS_LATEST_VERSION, true);
            	List<Map<String,Object>> prop = AgentVersionApi.listAgentVersions(context);
            	path = prop.get(0).get(AgentConstants.URL).toString();
            	version = prop.get(0).get(AgentConstants.VERSION).toString();
            }
            LOGGER.info("Agent Download " + path);
            File file = downloadExeFrom(path);
            if (file==null){
                LOGGER.info("File is null");
                return "error";
            }
            else {
                LOGGER.info("FilePath " + file.getAbsolutePath());
            }
            fileInputStream = new FileInputStream(file);
            if(StringUtils.isNotEmpty(getToken())) {
            	FacilioService.runAsService(() -> AgentVersionApi.markVersionLogUpdated(getToken()));
            }else {
            	String fileName = "agent-"+version+".exe";
            	setFilename(fileName);
            	setContentType("application/x-download");
            }
            return SUCCESS;
        }catch (Exception ex){
            LOGGER.info(ex.getMessage());
            return "error";
        }
    }

	private File downloadExeFrom(String url) throws Exception {
        if (FacilioProperties.isProduction()) {
        	if(StringUtils.isEmpty(orgId)) {
        		orgId = String.valueOf(AccountUtil.getCurrentOrg().getOrgId()).toString();
        	}
        	String key = "Org_" + this.orgId
                    + "Agent_" + this.agentId
                    + "Version_" + this.version
                    + System.currentTimeMillis()
                    + "agent.tmp";

            File file = new File("/tmp/" + key);

            try (FileOutputStream outputStream = new FileOutputStream(file);InputStream inputStream = FacilioFactory.getFileStore().getSecretFile(url);) {

                int read;
                byte[] bytes = new byte[1024];

                while (inputStream != null && (read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }

            } catch (IOException e) {
                LOGGER.info("Error while downloading Agent Exe " + e.getMessage());
            }
            return file;
        } else {
            LOGGER.info(" not production ");
            return null;
        }
    }

    private String getExeUrl(String token) throws Exception {
        long versionId = getVersionIdFromToken(token);
        this.version = ""+versionId;
        LOGGER.info("versionId"+versionId);
        if (versionId!=-1) {
            Collection<Long> vId = new ArrayList<>();
            vId.add(versionId);
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .table(getAgentVersionModule().getTableName())
                    .select(FieldFactory.getAgentVersionFields())
                    .andCondition(CriteriaAPI.getCondition(
                            FieldFactory.getIdField
                                    (getAgentVersionModule()), vId, NumberOperators.EQUALS));
            List<Map<String, Object>> row = selectRecordBuilder.get();
            LOGGER.info("Agent_Version Record: "+row);
            if (row.get(0).containsKey("url")){
                return row.get(0).get("url").toString();
            }
            else return null;
        }else return null;

    }

    private long getVersionIdFromToken(String token) throws Exception {
        FacilioModule agentVersionLogModule = getAgentVersionLogModule();
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(agentVersionLogModule.getTableName())
                .select(FieldFactory.getAgentVersionLogFields())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAuthKeyField(agentVersionLogModule), token, StringOperators.IS));
        List<Map<String, Object>> rows = selectRecordBuilder.get();
        LOGGER.info("TOKEN : " + token);
        LOGGER.info("Agent_VersionLog record : " + rows);
        if (rows.isEmpty()) {
            throw new Exception(" no version log found ");
        } else {
            Map<String, Object> row = rows.get(0);
            if (rows.contains(AgentConstants.UPDATED_TIME) && (row.get(AgentConstants.UPDATED_TIME) != null)) {
                throw new Exception(" version log claimed");
            }
            if (row.containsKey(AgentConstants.ORGID) && (row.get(AgentConstants.ORGID) != null)) {
                this.orgId = row.get(AgentConstants.ORGID).toString();
            }
            if (row.containsKey(AgentConstants.AGENT_ID)) {
                this.agentId = row.get(AgentConstants.AGENT_ID).toString();
            }
            if (row.containsKey(AgentConstants.VERSION_ID)) {
                return Long.parseLong(row.get(AgentConstants.VERSION_ID).toString());
            } else {
                return -1;
            }
        }
    }

}
