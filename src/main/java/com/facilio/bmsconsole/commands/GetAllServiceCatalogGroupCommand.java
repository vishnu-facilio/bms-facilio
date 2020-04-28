package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ServiceCatalogGroupContext;
import com.facilio.bmsconsole.util.ServiceCatalogApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class GetAllServiceCatalogGroupCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
    		
    		List<FacilioField> fields = FieldFactory.getServiceCatalogGroupFields();
    		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
    		
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getServiceCatalogGroupModule().getTableName())
                .select(fields)
        			.andCondition(CriteriaAPI.getCondition(fieldMap.get("name") , ServiceCatalogApi.SERVICE_COMPLAINT_CATEGORY , StringOperators.ISN_T));

        List<Map<String, Object>> maps = builder.get();
        List<ServiceCatalogGroupContext> serviceCatalogGroups = FieldUtil.getAsBeanListFromMapList(maps, ServiceCatalogGroupContext.class);
        context.put(FacilioConstants.ContextNames.SERVICE_CATALOG_GROUPS, serviceCatalogGroups);

        return false;
    }
}
