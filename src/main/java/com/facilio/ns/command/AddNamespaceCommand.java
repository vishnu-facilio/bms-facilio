package com.facilio.ns.command;

import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldUtil;
import com.facilio.ns.NamespaceConstants;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.factory.NamespaceModuleAndFieldFactory;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

public class AddNamespaceCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        NameSpaceContext ns = (NameSpaceContext) context.get(NamespaceConstants.NAMESPACE);
        V3Util.throwRestException(ns == null, ErrorCode.VALIDATION_ERROR, "Namespace cannot be null.");
        Long parentRuleId = (Long) context.get(NamespaceConstants.PARENT_RULE_ID);

        if (parentRuleId != null) {
            ns.setParentRuleId(parentRuleId);
        }
        
        Long id = NewReadingRuleAPI.addNamespace(ns);

        context.put(NamespaceConstants.NAMESPACE_ID, id);
        return Boolean.FALSE;
    }
}
