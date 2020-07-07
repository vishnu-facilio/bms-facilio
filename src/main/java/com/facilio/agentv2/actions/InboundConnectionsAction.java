package com.facilio.agentv2.actions;

import com.amazonaws.services.dynamodbv2.xspec.L;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentUtilV2;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InboundConnectionsAction extends FacilioAction {
    private static final Logger LOGGER = LogManager.getLogger(AgentIntegrationAction.class.getName());

    private String name;
    private String sender;
    private long userId;
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @NotNull
    @Size(min = 3,max = 100,message = "name should be 3-100 characters length")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @NotNull
    @Size(min = 3,max = 100,message = "sender should be 3-100 characters length")
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
    @NotNull
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }



    public String add(){
        LOGGER.info("Adding Inbound Connections ");
        try {
            String apiKey = AgentUtilV2.generateRandomAPIKey();
            addEntryInConnections(getName(), getSender(), apiKey, getUserId(),1);
            setApiKey(apiKey);
            setResult("apiKey",getApiKey());
            return SUCCESS;
        }catch (Exception ex){
            LOGGER.info(ex.getMessage());
            setException(ex);
            return ERROR;
        }
    }

    private void addEntryInConnections(String name, String sender, String apiKey, long userId,int authType) throws Exception{
        Map<String, Object> record= new HashMap<>();
        record.put("orgId", Objects.requireNonNull(AccountUtil.getCurrentOrg()).getOrgId());
        record.put("name",name);
        record.put("sender",sender);
        record.put("apiKey",apiKey);
        record.put("userId",userId);
        record.put("authType",authType);
        new GenericInsertRecordBuilder().table(ModuleFactory.getInboundConnectionsModule().getTableName())
                .fields(FieldFactory.getInboundConnectionsFields())
                .insert(record);

    }
}
