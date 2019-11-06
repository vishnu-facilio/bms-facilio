package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ServiceCatalogContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class GetAllServiceCatalogCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long groupId = (long) context.get(FacilioConstants.ContextNames.GROUP_ID);
        if (groupId <= 0) {
            throw new IllegalArgumentException("Group Id cannot be empty");
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getServiceCatalogModule().getTableName())
                .select(FieldFactory.getServiceCatalogFields())
                .andCondition(CriteriaAPI.getCondition("GROUP_ID", "groupId", String.valueOf(groupId), NumberOperators.EQUALS));
        List<Map<String, Object>> maps = builder.get();
        List<ServiceCatalogContext> serviceCatalogs = FieldUtil.getAsBeanListFromMapList(maps, ServiceCatalogContext.class);

        context.put(FacilioConstants.ContextNames.SERVICE_CATALOGS, serviceCatalogs);

        return false;
    }
}
