package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.interceptors.AgentDownloadInterceptor;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class AgentDownloadAction extends ActionSupport {
    private static final Logger LOGGER = LogManager.getLogger(AgentDownloadAction.class.getName());
    JSONObject result = new JSONObject();
    public String token;
    private String orgId;
    private String agentId;
    private String version;
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

    public String downloadAgent() throws Exception {
        try {
            LOGGER.info("Download Agent called");
            String url = getExeUrl(getToken());
            LOGGER.info("Agent Download " + url);
            File file = downloadExeFrom(url);
            if (file==null){
                LOGGER.info("File is null");
                return "error";
            }
            else {
                LOGGER.info("FilePath " + file.getAbsolutePath());
            }
            fileInputStream = new FileInputStream(file);
            return SUCCESS;
        }catch (Exception ex){
            LOGGER.info(ex.getMessage());
            return "error";
        }
    }

    private File downloadExeFrom(String url) throws IOException {
            BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream());
            String key = "Org_"+this.orgId
                    + "Agent_"+ this.agentId
                    + "Version_"+ this.version
                    + System.currentTimeMillis()
                    +"agent.tmp";
             FileOutputStream fileOS = new FileOutputStream(key) ;
            byte[] data = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                fileOS.write(data, 0, byteContent);
            }

            File file = new File(key);
            if (file.exists())
            return file;
            else return null;


    }

    private String getExeUrl(String token) throws Exception {
        long versionId = getVersionIdFromToken(token);
        this.version = ""+versionId;
      
        if (versionId!=-1) {
            Collection<Long> vId = new ArrayList<>();
            vId.add(versionId);
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getAgentVersionModule().getTableName())
                    .select(FieldFactory.getAgentVersionFields())
                    .andCondition(CriteriaAPI.getCondition(
                            FieldFactory.getVersionIdField
                                    (ModuleFactory.getAgentVersionModule()),vId,NumberOperators.EQUALS));
            List<Map<String, Object>> row = selectRecordBuilder.get();
            if (row.get(0).containsKey("url")){
                return row.get(0).get("url").toString();
            }
            else return null;
        }else return null;

    }

    private long getVersionIdFromToken(String token) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAgentVersionLogModule().getTableName())
                .select(FieldFactory.getAgentVersionLogFields())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAuthKeyField(ModuleFactory.getAgentVersionLogModule()),token, StringOperators.IS));
        List<Map<String, Object>> row = selectRecordBuilder.get();
        if(row.get(0).containsKey("orgId")){
            this.orgId=row.get(0).get("orgId").toString();
        }
        if(row.get(0).containsKey("agentId")){
            this.agentId=row.get(0).get("agentId").toString();
        }
        if (row.get(0).containsKey("versionId")){
            return Long.parseLong(row.get(0).get("versionId").toString());
        }
        else
        {
            return -1;
        }
    }

}
