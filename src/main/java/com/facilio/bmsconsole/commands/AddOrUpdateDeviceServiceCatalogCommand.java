package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.FeedbackKioskContext;
import com.facilio.bmsconsole.context.ServiceCatalogContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddOrUpdateDeviceServiceCatalogCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        FeedbackKioskContext kiosk = (FeedbackKioskContext) context.get(FacilioConstants.ContextNames.RECORD);
        if (kiosk != null) {
            removeDeviceCatalogMapping(kiosk.getId());

            List<ServiceCatalogContext> serviceCatalogs = kiosk.getServiceCatalogs();
            if (CollectionUtils.isNotEmpty(serviceCatalogs)) {
                GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                        .table(ModuleFactory.getDeviceCatalogMappingModule().getTableName())
                        .fields(FieldFactory.getDeviceCatalogMappingFields());
                for (ServiceCatalogContext serviceCatalog : kiosk.getServiceCatalogs()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("catalogId", serviceCatalog.getId());
                    map.put("deviceId", kiosk.getId());
                    builder.addRecord(map);
                }
                builder.save();
            }
        }
        return false;
    }

    private void removeDeviceCatalogMapping(long deviceId) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getDeviceCatalogMappingModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("DEVICE_ID", "deviceId", String.valueOf(deviceId), NumberOperators.EQUALS));
        builder.delete();
    }
}
