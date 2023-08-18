package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.DashboardReadingWidgetFilterContext;
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

public class AddOrUpdateReadingFilterCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long targetWidgetId = (Long) context.get(FacilioConstants.ContextNames.WIDGET_ID);
        Map<String, List<DashboardReadingWidgetFilterContext>> filterMappings = ( Map<String, List<DashboardReadingWidgetFilterContext>>) context.get("readingWidgetMappings");
        if(filterMappings != null && !filterMappings.isEmpty()){
            for(String triggerWidgetId: filterMappings.keySet()) {
                List<Long> ids = DashboardFilterUtil.getReadingFilterMappingIdForFilterId(targetWidgetId, Long.valueOf(triggerWidgetId));
                if(!(ids.isEmpty())){
                    GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                            .table(ModuleFactory.getDashboardFilterReadingWidgetFieldMappingModule().getTableName())
                            .andCondition(CriteriaAPI.getIdCondition(ids,ModuleFactory.getDashboardFilterReadingWidgetFieldMappingModule()));
                    builder.delete();
                }
                for (DashboardReadingWidgetFilterContext mappings : filterMappings.get(triggerWidgetId)) {
                    GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                            .table(ModuleFactory.getDashboardFilterReadingWidgetFieldMappingModule().getTableName())
                            .fields(FieldFactory.getDashboardFilterReadingWidgetFieldMappingFields());
                    mappings.setOrgId(AccountUtil.getCurrentOrg().getId());
                    Map<String, Object> props = FieldUtil.getAsProperties(mappings);
                    insertBuilder.addRecord(props);
                    insertBuilder.save();
                }
            }
        }
        return false;
    }
}
