package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class AddOrUpdateScopingMetaCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ScopingContext scopingContext = (ScopingContext)context.get(FacilioConstants.ContextNames.SCOPING_CONTEXT);
        if (scopingContext.isDefault()) {
            throw new IllegalArgumentException("There can be only one default scoping for an application");
        }
        if(scopingContext.getId() <= 0) {
            ApplicationApi.addScoping(scopingContext);
        }
        else {
            ScopingContext sc = ApplicationApi.getScoping(scopingContext.getId());
            if(sc != null && sc.isDefault()) {
                throw new IllegalArgumentException("Updation of default Scoping is not permitted");
            }
            else if (scopingContext.isDefault()) {
                throw new IllegalArgumentException("There can be only one default scoping for an application");
            }
            update(FieldUtil.getAsProperties(scopingContext), ModuleFactory.getScopingModule(), FieldFactory.getScopingFields(), scopingContext.getId());
        }
        return false;
    }

    private void update(Map<String, Object> map, FacilioModule module, List<FacilioField> fields, long id) throws Exception {
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(fields).andCondition(
                        CriteriaAPI.getIdCondition(id, module));
        builder.update(map);
    }
}
