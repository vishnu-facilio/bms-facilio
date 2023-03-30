package com.facilio.agentv2.migration.beans;

import com.facilio.agent.FacilioAgent;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.point.Point;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

import java.util.List;
import java.util.Map;

public interface AgentMigrationBean {

    FacilioAgent getAgent(long agentId);

    Map<FacilioControllerType,List<Long>> fetchControllers(long agentId) throws Exception ;

    void updateController(FacilioControllerType type, long sourceAgentId, long targetAgentId) throws Exception;

    List<Point> fetchPoints(long agentId, List<Long> controllerIds) throws Exception;

    void addPoints(FacilioControllerType controllerType,  List<Map<String, Object>> points) throws Exception;

    List<FacilioField> fetchReadings(long categoryId) throws Exception;

    List<FacilioModule> fetchModuleReadings(long categoryId) throws Exception;

    Map<Long, List<FacilioField>> fetchAllReadings() throws Exception;
    FacilioModule addReadingModule(List<FacilioModule> modules, FacilioModule assetModule) throws Exception;

    FacilioModule addReadingModule(FacilioModule module, FacilioModule assetModule) throws Exception;

    void updateFields(List<FacilioField> fields) throws Exception;
}
