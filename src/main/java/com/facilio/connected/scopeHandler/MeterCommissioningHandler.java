package com.facilio.connected.scopeHandler;


import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.util.CommissioningApi;
import com.facilio.bmsconsole.util.MetersAPI;
import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;

import java.util.*;

public class MeterCommissioningHandler implements ScopeCommissioningHandler {

    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(MeterCommissioningHandler.class.getName());

    @Override
    public String getModuleName() {
        return FacilioConstants.Meter.METER;
    }

    @Override
    public String getSubModuleName() {
        return FacilioConstants.Meter.UTILITY_TYPE;
    }

    @Override
    public String getDisplayName() {
        return "Meter";
    }

    @Override
    public String getTypeDisplayName() {
        return "Utility";
    }

    @Override
    public void updateConnectionStatus(Set<Long> meterIds, boolean isCommissioned) throws Exception {
        MetersAPI.updateMeterConnectionStatus(meterIds, isCommissioned);
    }

    @Override
    public String getResourceName(Long meterId) throws Exception {
        V3MeterContext meter = MetersAPI.getMeter(meterId);
        return meter.getName();
    }

    @Override
    public Map<Long, String> getParent(Set<Long> meterIds) throws Exception {
        return CommissioningApi.getParent(meterIds, getModuleName());
    }

    @Override
    public Map<Long, String> getChildTypes(Set<Long> utilityIds) throws Exception {
        return CommissioningApi.getParent(utilityIds, getSubModuleName());
    }

    @Override
    public Map<String, FacilioField> getReadings(Long utilityType) throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.EXCLUDE_EMPTY_FIELDS, false);
        context.put(FacilioConstants.Meter.PARENT_UTILITY_TYPE_ID, utilityType);
        context.put(FacilioConstants.ContextNames.FILTER, "available");

        FacilioChain getCategoryReadingChain = FacilioChainFactory.getUtilityTypeReadingsChain();
        getCategoryReadingChain.execute(context);

        Map<String, FacilioField> fieldsMap = new HashMap<>();
        List<FacilioModule> facilioModules = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
        for (FacilioModule module : facilioModules) {
            List<Long> fieldIds = new ArrayList<>();
            List<FacilioField> fields = module.getFields();
            for (FacilioField field : fields) {
                fieldsMap.put(field.getName(), field);
                fieldIds.add(field.getFieldId());
            }
            LOGGER.info("Module name: " + module.getName() + ", ID : " + module.getModuleId() + ", Field Ids : " + fieldIds);
        }
        return fieldsMap;
    }

    @Override
    public Pair<Long, Long> getParentIdAndCategoryId(V3Context parent) {
        V3MeterContext meterContext = (V3MeterContext) parent;
        return Pair.of(meterContext.getUtilityType().getId(), meterContext.getId());
    }

    @Override
    public FacilioField getTypeField() throws Exception {
        return FieldFactory.getField("utilityType", "UTILITY_TYPE", Constants.getModBean().getModule(getModuleName()), FieldType.LOOKUP);
    }
}
