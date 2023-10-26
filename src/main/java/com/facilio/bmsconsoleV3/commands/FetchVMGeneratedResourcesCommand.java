package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationContext;
import org.apache.commons.chain.Context;

import java.util.*;

public class FetchVMGeneratedResourcesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        Long relationShipId = (Long) context.get("relationShipId");

        FacilioModule relationModule = ModuleFactory.getRelationModule();
        GenericSelectRecordBuilder selectRelationBuilder = new GenericSelectRecordBuilder()
                .table(relationModule.getTableName())
                .select(FieldFactory.getRelationFields())
                .andCondition(CriteriaAPI.getIdCondition(relationShipId, relationModule));

        Map<String, Object> props = selectRelationBuilder.fetchFirst();

        if (props != null && !props.isEmpty()) {
            RelationContext relation = FieldUtil.getAsBeanFromMap(props, RelationContext.class);
            FacilioModule relMod = modBean.getModule(relation.getRelationModuleId());
            List<FacilioField> relModFields = modBean.getAllFields(relMod.getName());

            SelectRecordsBuilder selectBuilder = new SelectRecordsBuilder()
                    .module(relMod)
                    .select(relModFields);

            List<Map<String, Object>> relModProps = selectBuilder.getAsProps();
            if(relModProps != null && !relModProps.isEmpty()) {
                List<Long> resourceList = new ArrayList<>();
                for(Map<String, Object> prop : relModProps) {
                    resourceList.add((Long) ((Map<String, Object>) prop.get("left")).get("id"));
                }
                context.put("resourceList", resourceList);
            }
        }
        return false;
    }
}
