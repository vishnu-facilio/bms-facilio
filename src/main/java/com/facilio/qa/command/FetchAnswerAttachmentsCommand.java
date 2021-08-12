package com.facilio.qa.command;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.PageContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.v3.context.Constants;

public class FetchAnswerAttachmentsCommand extends FacilioCommand {


	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<AnswerContext> answers = Constants.getRecordList((FacilioContext) context);
		
		for(AnswerContext answer : answers) {
			
			FacilioChain getAttachmentsChain = FacilioChainFactory.getAttachmentsChain();
			FacilioContext newContext = getAttachmentsChain.getContext();
			newContext.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.QAndA.Answers.ATTACHMENT);
			newContext.put(FacilioConstants.ContextNames.RECORD_ID, answer.getId());
			getAttachmentsChain.execute();
			
			List<AttachmentContext> attachmentList = (List<AttachmentContext>) newContext.get(FacilioConstants.ContextNames.ATTACHMENT_LIST);
			
			answer.setAttachmentList(attachmentList);
		}
		
		return false;
	}

}
