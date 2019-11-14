package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ServiceCatalogGroupContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class GetServiceCatalogGroupDetailsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long) context.get(FacilioConstants.ContextNames.ID);
        if (id > 0) {
            GenericSelectRecordBuilder selectRecord = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getServiceCatalogGroupModule().getTableName())
                    .select(FieldFactory.getServiceCatalogGroupFields())
                    .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getServiceCatalogGroupModule()));

            Map<String, Object> map = selectRecord.fetchFirst();
            ServiceCatalogGroupContext serviceCatalogGroup = FieldUtil.getAsBeanFromMap(map, ServiceCatalogGroupContext.class);

            context.put(FacilioConstants.ContextNames.SERVICE_CATALOG_GROUP, serviceCatalogGroup);
        }
        return false;
    }
}
