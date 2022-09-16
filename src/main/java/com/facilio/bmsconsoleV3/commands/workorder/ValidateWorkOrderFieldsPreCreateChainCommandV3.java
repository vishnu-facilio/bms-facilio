package com.facilio.bmsconsoleV3.commands.workorder;


import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.util.V3TicketAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;

import java.util.List;
import java.util.Map;

/**
 * ValidateWorkOrderFieldsPreCreateChainCommandV3 does
 * - validation
 * - set default values
 *  while creating workorders in PRE_OPEN state, after publishing the PM.
 *
 */
public class ValidateWorkOrderFieldsPreCreateChainCommandV3 extends FacilioCommand {

    private static org.apache.log4j.Logger log = LogManager.getLogger(ValidateWorkOrderFieldsPreCreateChainCommandV3.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);

        if (CollectionUtils.isNotEmpty(wos)) {
            for (V3WorkOrderContext woContext : wos) {
                // Site Validation
                V3TicketAPI.validateSiteSpecificData(woContext);
                if (woContext.getSiteId() == -1) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Please select site");
                }

                // Subject Validation
                if (woContext.getSubject() == null || woContext.getSubject().isEmpty()) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Subject is invalid");
                } else {
                    woContext.setSubject(woContext.getSubject().trim());
                }

                // Description trimming
                if (woContext.getDescription() != null && !woContext.getDescription().isEmpty()) {
                    woContext.setDescription(woContext.getDescription().trim());
                }

                // Setting up default Priority to Low, it isn't set already
                if (woContext.getPriority() == null || woContext.getPriority().getId() == -1) {
                    woContext.setPriority(V3TicketAPI.getPriority(AccountUtil.getCurrentOrg().getId(), "Low"));
                }
            }
        }
        return false;
    }
}
