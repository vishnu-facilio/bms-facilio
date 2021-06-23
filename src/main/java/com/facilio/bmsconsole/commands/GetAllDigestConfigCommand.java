package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DigestMailTemplateMapContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.DigestTemplateFactory;

public class GetAllDigestConfigCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<DigestMailTemplateMapContext> list = DigestTemplateFactory.getAllDigestTemplates();
		context.put(FacilioConstants.ContextNames.DIGEST_CONFIG_LIST, list);
		return false;
	}

}
