package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DigestMailTemplateMapContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.DigestTemplateFactory;

public class GetAllDigestConfigCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<DigestMailTemplateMapContext> list = DigestTemplateFactory.getAllDigestTemplates();
		context.put(FacilioConstants.ContextNames.DIGEST_CONFIG_LIST, list);
		return false;
	}

}
