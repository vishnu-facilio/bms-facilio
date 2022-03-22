package com.facilio.ns.command;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldUtil;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.ns.factory.NamespaceModuleAndFieldFactory;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class AddNamespaceAndFieldsCommand extends FacilioCommand {
    Context ctx;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        this.ctx = context;
        addNamespace();
        addFields();
        return Boolean.FALSE;
    }

    private void addNamespace() throws Exception {
        NameSpaceContext ns = (NameSpaceContext) ctx.get(NSContextNames.NAMESPACE);
        Long parentRuleId = (Long) ctx.get(NSContextNames.PARENT_RULE_ID);

        if (parentRuleId != null) {
            ns.setParentRuleId(parentRuleId);
        }

        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .fields(NamespaceModuleAndFieldFactory.getNamespaceFields());
        long id = insertBuilder.insert(FieldUtil.getAsProperties(ns));
        ctx.put(NSContextNames.NAMESPACE_ID, id);
        ns.setId(id);

    }

    private void addFields() throws Exception {
        long nsId = (long) ctx.get(NSContextNames.NAMESPACE_ID);
        Map<Long, ResourceContext> assetsMap = (Map<Long, ResourceContext>) ctx.get(FacilioConstants.ContextNames.ASSETS);
        List<NameSpaceField> fields = (List<NameSpaceField>) ctx.get(NSContextNames.NAMESPACE_FIELDS);
        NewReadingRuleAPI.addNamespaceFields(nsId, assetsMap, fields);
    }

}
