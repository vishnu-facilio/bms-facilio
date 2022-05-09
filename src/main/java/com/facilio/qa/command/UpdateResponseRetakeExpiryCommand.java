package com.facilio.qa.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.scoringrule.ScoreContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.qa.context.ResponseContext;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.concurrent.TimeUnit;

public class UpdateResponseRetakeExpiryCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception{

		ResponseContext response = (ResponseContext) context.get(FacilioConstants.QAndA.RESPONSE);

		ModuleBean moduleBean = Constants.getModBean();
		FacilioModule module = moduleBean.getModule(FacilioConstants.QAndA.RESPONSE);

		if(response.getRetakeExpiryDuration() != null && response.getRetakeExpiryDuration() > 0){
			response.setRetakeExpiry(calculateRetakeExpiry(response));

			UpdateRecordBuilder<ResponseContext> builder = new UpdateRecordBuilder<ResponseContext>()
					.module(module)
					.fields(moduleBean.getAllFields(module.getName()))
					.andCondition(CriteriaAPI.getIdCondition(response.getId(), module));
			builder.update(response);
		}

		return false;
	}

	private long calculateRetakeExpiry(ResponseContext response){

		return DateTimeUtil.getCurrenTime() + TimeUnit.MINUTES.toMillis(response.getRetakeExpiryDuration());
	}
}
