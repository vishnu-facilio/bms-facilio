package com.facilio.agent.integration.wattsense;

import com.facilio.agent.integration.AgentIntegrationKeys;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class WattsenseApi
{
    private static final Logger LOGGER = LogManager.getLogger(WattsenseApi.class.getName());

    public static void addWattsenseApi(WattsenseClient wattsenseClient) throws Exception {
        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder().table(AgentIntegrationKeys.TABLE_NAME)
                .fields(FieldFactory.getWattsenseIntegrationField());
        long id = insertRecordBuilder.insert((Map<String, Object>) FieldUtil.getAsProperties(wattsenseClient));
        if(id>0){
            wattsenseClient.setId(id);
        }
    }

    public static WattsenseClient getWattsense(String name) throws Exception {
        Criteria criteria = new Criteria();
        FacilioModule agentIntegrationModule = ModuleFactory.getWattsenseIntegrationModule();
        FieldFactory.getAsMap(FieldFactory.getWattsenseIntegrationField());
        criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getNameField(agentIntegrationModule), name, StringOperators.IS));
        List<WattsenseClient> wattsenseClientList = getWattsense(criteria);
        if(wattsenseClientList != null &&  ( ! wattsenseClientList.isEmpty())){
            return wattsenseClientList.get(0);
        }
        return null;
    }

    public static void updateWattsenseIntegration(WattsenseClient wattsenseClient)throws Exception{
        FacilioModule agentIntegrationModule = ModuleFactory.getWattsenseIntegrationModule();
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(agentIntegrationModule.getTableName())
                .fields(FieldFactory.getWattsenseIntegrationField())
                .andCondition(CriteriaAPI.getIdCondition(wattsenseClient.getClientId(),agentIntegrationModule));
                updateRecordBuilder.update(FieldUtil.getAsProperties(wattsenseClient));
    }

    public static List<WattsenseClient> getWattsense(Criteria criteria) throws Exception {
        GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder()
                .table(AgentIntegrationKeys.TABLE_NAME)
                .select(FieldFactory.getWattsenseIntegrationField())
                .andCriteria(criteria);
        List<Map<String, Object>> maps = genericSelectRecordBuilder.get();
        return FieldUtil.getAsBeanListFromMapList(maps, WattsenseClient.class);
    }
}
