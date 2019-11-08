package com.facilio.agentv2.device;

import com.facilio.agentv2.AgentConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.*;

public class FieldDeviceApi
{
    private static final FacilioModule fieldDeviceModule = ModuleFactory.getFieldDeviceModule();

    private static final Logger LOGGER = LogManager.getLogger(FieldDeviceApi.class.getName());


    public static int deleteDevices(Collection<Long> deviceId) throws SQLException {
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder().table(fieldDeviceModule.getTableName())
                .fields(FieldFactory.getFieldDeviceFields())
                .andCondition(CriteriaAPI.getIdCondition(deviceId, ModuleFactory.getFieldDeviceModule()));
        Map<String,Object> toUpdateMap = new HashMap<>();
        toUpdateMap.put(AgentConstants.DELETED_TIME,System.currentTimeMillis());
       return builder.update(toUpdateMap);
    }

    public static void addFieldDevicecs(List<Device> devices) throws Exception{
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(fieldDeviceModule.getTableName())
                .fields(FieldFactory.getFieldDeviceFields())
                .addRecords(FieldUtil.getAsMapList(devices,Device.class));
        builder.save();
    }

    public static List<Map<String, Object>> getDevices(Long agentId, List<Long> ids)  {
        try {
           GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                   .table(fieldDeviceModule.getTableName())
                   .select(FieldFactory.getFieldDeviceFields());
           Criteria criteria = new Criteria();
           Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFieldDeviceFields());
           criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.DELETED_TIME),"NULL", CommonOperators.IS_EMPTY));
           if( (agentId != null) && (agentId > 0) ){
               criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.AGENT_ID),String.valueOf(agentId),NumberOperators.EQUALS));
           }
           if( (ids != null) && ( ! ids.isEmpty() ) ){
               criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.ID), StringUtils.join(ids, ","), NumberOperators.EQUALS));
           }
           List<Map<String, Object>> rows = builder.andCriteria(criteria).get();
           return rows;
       }catch (Exception e){
            LOGGER.info(" Exception occurred ",e);
        }
        return new ArrayList<>();
    }
}
