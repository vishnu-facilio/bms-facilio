package com.facilio.relation.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationUtil;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetAllRelationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module == null) {
            throw new IllegalArgumentException("Invalid module");
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getRelationModule().getTableName())
                .innerJoin(ModuleFactory.getRelationMappingModule().getTableName())
                .on(ModuleFactory.getRelationModule().getTableName() + ".ID = " + ModuleFactory.getRelationMappingModule().getTableName() + ".RELATION_ID")
                .select(FieldFactory.getRelationFields())
                .andCondition(CriteriaAPI.getCondition("FROM_MODULE_ID", "fromModuleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
        List<RelationContext> relationList = FieldUtil.getAsBeanListFromMapList(builder.get(), RelationContext.class);
        RelationUtil.fillRelation(relationList);

        List<RelationRequestContext> relationRequests = RelationUtil.convertToRelationRequest(relationList, module.getModuleId());
        context.put(FacilioConstants.ContextNames.RELATION_LIST, relationRequests);

        return false;
    }
}
