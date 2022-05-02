package com.facilio.bmsconsoleV3.commands.tool;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class UpdateIsUnderStockedCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3ToolContext> toolContexts =  (List<V3ToolContext>) context.get(FacilioConstants.ContextNames.TOOLS);
        if(CollectionUtils.isNotEmpty(toolContexts)){
            V3ToolContext tool = toolContexts.get(0);
            if(tool.getCurrentQuantity()!=null &&  tool.getMinimumQuantity()!=null && (tool.getCurrentQuantity() <= tool.getMinimumQuantity())){

                tool.setIsUnderstocked(true);

                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);

                UpdateRecordBuilder<V3ToolContext> updateBuilder = new UpdateRecordBuilder<V3ToolContext>()
                        .module(module).fields(modBean.getAllFields(module.getName()))
                        .andCondition(CriteriaAPI.getIdCondition(tool.getId(), module));

                updateBuilder.update(tool);
            }
        }
        return false;
    }
}
