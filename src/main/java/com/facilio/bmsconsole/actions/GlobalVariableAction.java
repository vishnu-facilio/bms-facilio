package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.automation.context.GlobalVariableContext;
import com.facilio.bmsconsole.automation.context.GlobalVariableGroupContext;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;

public class GlobalVariableAction extends V3Action {

    private GlobalVariableGroupContext variableGroup;
    public GlobalVariableGroupContext getVariableGroup() {
        return variableGroup;
    }
    public void setVariableGroup(GlobalVariableGroupContext variableGroup) {
        this.variableGroup = variableGroup;
    }

    private GlobalVariableContext variable;
    public GlobalVariableContext getVariable() {
        return variable;
    }
    public void setVariable(GlobalVariableContext variable) {
        this.variable = variable;
    }

    public String addOrUpdateGroup() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.addOrUpdateGlobalVariableGroupChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.GLOBAL_VARIABLE_GROUP, variableGroup);
        chain.execute();

        Object o = context.get(FacilioConstants.ContextNames.GLOBAL_VARIABLE_GROUP);
        setData("variableGroup", FieldUtil.getAsJSON(o));
        return SUCCESS;
    }

    public String listGroup() throws Exception {
        FacilioChain chain = ReadOnlyChainFactoryV3.getListGlobalVariableGroupChain();
        chain.execute();
        FacilioContext context = chain.getContext();

        setData("list", context.get(FacilioConstants.ContextNames.GLOBAL_VARIABLE_GROUP_LIST));
        return SUCCESS;
    }

    public String deleteGroup() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.deleteGlobalVariableGroupChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.ID, getId());
        chain.execute();

        return SUCCESS;
    }

    public String addOrUpdate() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.addOrUpdateGlobalVariableChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.GLOBAL_VARIABLE, variable);
        chain.execute();

        Object o = context.get(FacilioConstants.ContextNames.GLOBAL_VARIABLE);
        setData("variable", FieldUtil.getAsJSON(o));
        return SUCCESS;
    }

    public String list() throws Exception {

        return SUCCESS;
    }
}
