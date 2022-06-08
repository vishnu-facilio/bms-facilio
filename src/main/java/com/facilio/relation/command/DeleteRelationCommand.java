package com.facilio.relation.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.util.RelationUtil;
import org.apache.commons.chain.Context;

public class DeleteRelationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long) context.get(FacilioConstants.ContextNames.ID);

        RelationContext relation = RelationUtil.getRelation(id, false);
        if (relation == null) {
            throw new IllegalArgumentException("Invalid relation");
        }

        FacilioModule module = ModuleFactory.getRelationModule();
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id, module));
        builder.delete();

        // check whether data are there, and delete them as well
        DeleteRecordBuilder deleteRecordBuilder = new DeleteRecordBuilder()
                .module(relation.getRelationModule());
        deleteRecordBuilder.delete();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        modBean.deleteModule(relation.getRelationModule().getName());


        return false;
    }
}
