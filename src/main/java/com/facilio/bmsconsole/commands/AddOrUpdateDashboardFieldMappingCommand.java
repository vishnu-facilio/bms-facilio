package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.DashboardFieldMappingContext;
import com.facilio.bmsconsole.context.DashboardFilterContext;
import com.facilio.bmsconsole.util.DashboardFilterUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

public class AddOrUpdateDashboardFieldMappingCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long userFilterId = (Long) context.get(FacilioConstants.ContextNames.DASHBOARD_USER_FILTER_ID);
        List<DashboardFieldMappingContext> filterMappings = (List<DashboardFieldMappingContext>) context.get("fieldMappings");
        List<Long> existingIds = new ArrayList<>();
        if(userFilterId != null && userFilterId > 0){
            if(filterMappings != null && !filterMappings.isEmpty()){
                for(DashboardFieldMappingContext mappings: filterMappings){
                    if(mappings.getId() <=0){
                        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                                .table(ModuleFactory.getDashboardFieldMappingModule().getTableName())
                                .fields(FieldFactory.getDashboardFieldMappingsFields());
                        mappings.setOrgId(AccountUtil.getCurrentOrg().getId());
                        mappings.setDashboardUserFilterId(userFilterId);
                        Map<String, Object> props = FieldUtil.getAsProperties(mappings);
                        insertBuilder.addRecord(props);
                        insertBuilder.save();
                        existingIds.add((Long) props.get("id"));
                    } else {
                        existingIds.add(mappings.getId());
                    }
                }
            }

            List<Long> ids = DashboardFilterUtil.getFilterMappingIdForFilterId(userFilterId).stream().filter(id -> !existingIds.contains(id)).distinct().collect(Collectors.toList());
            if(!(ids.isEmpty())){
                GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                        .table(ModuleFactory.getDashboardFieldMappingModule().getTableName())
                        .andCondition(CriteriaAPI.getIdCondition(ids,ModuleFactory.getDashboardFieldMappingModule()));
                builder.delete();
            }
        }

        return false;
    }
}
