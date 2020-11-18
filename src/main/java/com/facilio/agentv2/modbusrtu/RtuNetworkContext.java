package com.facilio.agentv2.modbusrtu;

import com.facilio.agentv2.AgentConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RtuNetworkContext implements Serializable {
    private static final Logger LOGGER = LogManager.getLogger(RtuNetworkContext.class.getName());

    private static final FacilioModule MODULE = ModuleFactory.getRtuNetworkModule();
    private long id;
    private String comPort;
    private Long baudRate;
    private Integer dataBits;
    private Integer stopBits;
    private Integer parity;
    private String name;
    private Long agentId;

    /*public static boolean addRtuNetwork(RtuNetworkContext rtuNetworkContext) {
        try {
            return addRtuNetworkCommand(rtuNetworkContext);
        } catch (Exception e) {
            LOGGER.info("Exception while adding rtuNetworkContext ", e);
        }
        return false;
    }*/

    public static long addRtuNetworkCommand(RtuNetworkContext rtuNetworkContext) throws Exception {
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(MODULE.getTableName())
                .fields(FieldFactory.getRtuNetworkFields());
        return builder.insert(FieldUtil.getAsProperties(rtuNetworkContext));
    }

    public static RtuNetworkContext getRtuNetworkContext(long id) {

        try {
            return getRtuNetworkContextCommand(id);
        } catch (Exception e) {
            LOGGER.info("Exception while getting rtuNetworkContext ", e);
        }
        return null;
    }

    private static RtuNetworkContext getRtuNetworkContextCommand(long id) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(MODULE.getTableName())
                .select(FieldFactory.getRtuNetworkFields())
                .andCondition(CriteriaAPI.getIdCondition(id, MODULE));
        List<Map<String, Object>> results = builder.get();
        if ((results != null) && (!results.isEmpty())) {
            if (results.size() == 1) {
                return FieldUtil.getAsBeanFromMap(results.get(0), RtuNetworkContext.class);
            } else {
                throw new Exception(" unexpected results cant get more than one record");
            }
        } else {
            throw new Exception(" record null ");
        }
    }

    public static RtuNetworkContext getRtuNetworkContext(long agentId, String port) throws Exception {

        Map<String, FacilioField> fields = FieldFactory.getAsMap(FieldFactory.getRtuNetworkFields());
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(MODULE.getTableName())
                .select(FieldFactory.getRtuNetworkFields())
                .andCondition(CriteriaAPI.getCondition(fields.get(AgentConstants.AGENT_ID), Collections.singleton(agentId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fields.get(AgentConstants.COM_PORT), port, StringOperators.IS));
        List<Map<String, Object>> results = builder.get();
        if ((results != null) && (!results.isEmpty())) {
            if (results.size() == 1) {
                return FieldUtil.getAsBeanFromMap(results.get(0), RtuNetworkContext.class);
            } else {
                throw new Exception(" unexpected results cant get more than one record");
            }
        } else {
            LOGGER.info("RTU Network not found ");
            return null;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public String getComPort() {
        return comPort;
    }

    public void setComPort(String comPort) {
        this.comPort = comPort;
    }

    public Long getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(Long baudRate) {
        this.baudRate = baudRate;
    }

    public Integer getDataBits() {
        return dataBits;
    }

    public void setDataBits(Integer dataBits) {
        this.dataBits = dataBits;
    }

    public Integer getStopBits() {
        return stopBits;
    }

    public void setStopBits(Integer stopBits) {
        this.stopBits = stopBits;
    }

    public Integer getParity() {
        return parity;
    }

    public void setParity(Integer parity) {
        this.parity = parity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JSONObject toJson() {
        JSONObject object = new JSONObject();
        object.put(AgentConstants.COM_PORT, comPort);
        object.put(AgentConstants.NAME, name);
        object.put(AgentConstants.PARITY, parity);
        object.put(AgentConstants.STOP_BITS, stopBits);
        object.put(AgentConstants.BAUD_RATE, baudRate);
        object.put(AgentConstants.DATA_BITS,dataBits);
        return object;
    }

}
