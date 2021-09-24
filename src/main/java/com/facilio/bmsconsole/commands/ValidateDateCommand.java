package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.InsuranceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateTimeUtil;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class ValidateDateCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<InsuranceContext> insurances = (List<InsuranceContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
        if (CollectionUtils.isNotEmpty(insurances)) {
            for (InsuranceContext ins : insurances) {
            	if(ins.getValidTill() < DateTimeUtil.getDayStartTime(false))
            	{
                    throw new IllegalArgumentException("Expiry date cannot be past date");
            	}
                if (ins.getValidFrom() > 0 && ins.getValidTill() > 0 && ins.getValidFrom() > ins.getValidTill()) {
                    throw new IllegalArgumentException("Please enter proper Validity Dates");
                }
            }
        }
        return false;
    }
}
