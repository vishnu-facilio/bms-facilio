package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ServiceCatalogContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchDeviceServiceCatalogCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long deviceId = (long) context.get(FacilioConstants.ContextNames.DEVICEID);
        if (deviceId > 0) {
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getDeviceCatalogMappingModule().getTableName())
                    .select(FieldFactory.getDeviceCatalogMappingFields())
                    .andCondition(CriteriaAPI.getCondition("DEVICE_ID", "deviceId", String.valueOf(deviceId), NumberOperators.EQUALS));
            List<Map<String, Object>> maps = builder.get();
            List<Long> serviceCatalogIds = new ArrayList<>();
            for (Map<String, Object> map : maps) {
                serviceCatalogIds.add((Long) map.get("catalogId"));
            }

            List<ServiceCatalogContext> serviceCatalogs = null;
            if (CollectionUtils.isNotEmpty(serviceCatalogIds)) {
                GenericSelectRecordBuilder serviceCatalogBuilder = new GenericSelectRecordBuilder()
                        .table(ModuleFactory.getServiceCatalogModule().getTableName())
                        .select(FieldFactory.getServiceCatalogFields())
                        .andCondition(CriteriaAPI.getIdCondition(serviceCatalogIds, ModuleFactory.getServiceCatalogModule()));
                serviceCatalogs = FieldUtil.getAsBeanListFromMapList(serviceCatalogBuilder.get(), ServiceCatalogContext.class);
            }
            if (serviceCatalogs == null) {
                serviceCatalogs = new ArrayList<>();
            }

            context.put(FacilioConstants.ContextNames.SERVICE_CATALOGS, serviceCatalogs);
        }
        return false;
    }
}
