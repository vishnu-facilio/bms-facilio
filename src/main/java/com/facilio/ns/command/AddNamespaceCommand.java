package com.facilio.ns.command;

import com.facilio.command.FacilioCommand;
import com.facilio.connected.ResourceType;
import com.facilio.constants.FacilioConstants;
import com.facilio.ns.NamespaceConstants;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

public class AddNamespaceCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        NameSpaceContext ns = (NameSpaceContext) context.get(NamespaceConstants.NAMESPACE);
        ResourceType resourceType=(ResourceType) context.get(FacilioConstants.ContextNames.RESOURCE_TYPE);

        V3Util.throwRestException(ns == null, ErrorCode.VALIDATION_ERROR, "Namespace cannot be null.");
        V3Util.throwRestException(ns.getParentRuleId() == null, ErrorCode.VALIDATION_ERROR, "Parent rule id cannot be null.");

        Long id = Constants.getNsBean().addNamespace(ns,resourceType);

        context.put(NamespaceConstants.NAMESPACE_ID, id);
        return Boolean.FALSE;
    }
}
