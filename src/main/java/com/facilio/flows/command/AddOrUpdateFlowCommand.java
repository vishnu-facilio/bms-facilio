package com.facilio.flows.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.flows.context.FlowContext;
import com.facilio.flows.context.ParameterContext;
import com.facilio.flows.util.FlowUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddOrUpdateFlowCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        FlowContext flowContext = (FlowContext) context.get(FacilioConstants.ContextNames.FLOW);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        if (flowContext != null) {
            if ((flowContext.getModuleId() > 0) && (modBean.getModule(flowContext.getModuleId()) == null)) {
                throw new IllegalArgumentException("Invalid module");
            }

            if ((flowContext.getFlowType() == 2) && (flowContext.getModuleId() <= 0)){
                throw new IllegalArgumentException("Module Id cannot be null");
            }

            flowContext.setModifiedBy(AccountUtil.getCurrentUser().getOuid());
            flowContext.setSysModifiedTime(DateTimeUtil.getCurrenTime());

            Map<String, Object> props = FieldUtil.getAsProperties(flowContext);

            if (flowContext.getId() > 0) {

                GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                        .table(ModuleFactory.getFlowModule().getTableName())
                        .fields(FieldFactory.getFlowFields())
                        .andCondition(CriteriaAPI.getIdCondition(flowContext.getId(),ModuleFactory.getFlowModule()));

                builder.update(props);

            } else {

                flowContext.setCreatedBy(AccountUtil.getCurrentUser().getOuid());
                flowContext.setSysCreatedTime(DateTimeUtil.getCurrenTime());

                GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                        .table(ModuleFactory.getFlowModule().getTableName())
                        .fields(FieldFactory.getFlowFields());

                builder.addRecord(props);
                builder.save();
            }

            flowContext.setId((long) props.get("id"));
            List<ParameterContext> parameters = flowContext.getParameters();

            addOrUpdateParameters(parameters, flowContext.getId());

        }

        context.put(FacilioConstants.ContextNames.FLOW,flowContext);

        return false;
    }
    private void addOrUpdateParameters(List<ParameterContext> parameters,long flowId) throws Exception {
        if(CollectionUtils.isEmpty(parameters)){
            return;
        }
       for (ParameterContext parameter : parameters){
           parameter.setFlowId(flowId);
           FlowUtil.addOrUpdateParameter(parameter);
       }
    }
}
