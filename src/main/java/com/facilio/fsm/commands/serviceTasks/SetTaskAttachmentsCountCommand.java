package com.facilio.fsm.commands.serviceTasks;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.AttachmentV3Context;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetTaskAttachmentsCountCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceTaskContext> serviceTasks = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(serviceTasks)){
            for(ServiceTaskContext serviceTask : serviceTasks){
                ModuleBean modBean = Constants.getModBean();
                FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_ATTACHMENTS);
                List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_ATTACHMENTS);
                SelectRecordsBuilder<AttachmentV3Context> recordsBuilder = new SelectRecordsBuilder<AttachmentV3Context>()
                        .module(module)
                        .select(fields)
                        .table(module.getTableName())
                        .beanClass(AttachmentV3Context.class)
                        .andCondition(CriteriaAPI.getCondition("PARENT","parentId",String.valueOf(serviceTask.getId()), NumberOperators.EQUALS))
                        .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(module))
                        .groupBy("ID");
                ;
                List<Map<String, Object>> props = recordsBuilder.getAsProps();
                Integer count = 0;
                if (CollectionUtils.isNotEmpty(props)) {
                    count = ((Number) props.get(0).get("id")).intValue();
                }
                serviceTask.setAttachmentsCount(count);
            }
        }
        return false;
    }
}
