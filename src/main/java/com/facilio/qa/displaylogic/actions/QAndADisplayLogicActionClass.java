package com.facilio.qa.displaylogic.actions;

import org.json.simple.JSONArray;

import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.command.QAndATransactionChainFactory;
import com.facilio.qa.displaylogic.context.DisplayLogicContext;
import com.facilio.qa.displaylogic.util.DisplayLogicUtil;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QAndADisplayLogicActionClass extends V3Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	DisplayLogicContext displayLogic;
	Long questionId;
	Long pageId;
	
	public String execute() throws Exception {
		 
		 FacilioChain addChain = QAndATransactionChainFactory.executeDisplayLogicChain();
		 
		 addChain.getContext().put(FacilioConstants.QAndA.Command.ANSWER_DATA, this.getData());
	
		 addChain.execute();
		 
		 setData("result", addChain.getContext().get(DisplayLogicUtil.DISPLAY_LOGIC_RULE_RESULT_JSON));
	     return SUCCESS;
	 }
	
	public String list() throws Exception {
		 
		 FacilioChain addChain = QAndATransactionChainFactory.getDisplayLogicListChain();
		 
		 addChain.getContext().put(DisplayLogicUtil.QUESTION_ID, getQuestionId());
		 addChain.getContext().put(DisplayLogicUtil.PAGE_ID, getPageId());
	
		 addChain.execute();
		 
		 setData("result", addChain.getContext().get(DisplayLogicUtil.DISPLAY_LOGIC_CONTEXTS));
	     return SUCCESS;
	 }
	
	 public String add() throws Exception {
		 
		 V3Util.throwRestException(displayLogic == null, ErrorCode.VALIDATION_ERROR, "Display Logic cannot be empty during save");
		 
		 FacilioChain addChain = QAndATransactionChainFactory.addOrUpdateDisplayLogicChain();
		 
		 addChain.getContext().put(DisplayLogicUtil.DISPLAY_LOGIC_CONTEXT, displayLogic);
	
		 addChain.execute();
		 
		 setData("result", displayLogic);
	     return SUCCESS;
	 }
	 
	 public String update() throws Exception {
		 
		 V3Util.throwRestException(displayLogic == null || displayLogic.getId() == null || displayLogic.getId() <= 0, ErrorCode.VALIDATION_ERROR, "Display Logic or Id cannot be empty during update");

		 if(displayLogic.getId()!=null && (displayLogic.getCriteria() == null || displayLogic.getCriteria().isEmpty())){
			delete();
		 } else {
			 FacilioChain addChain = QAndATransactionChainFactory.addOrUpdateDisplayLogicChain();

			 addChain.getContext().put(DisplayLogicUtil.DISPLAY_LOGIC_CONTEXT, displayLogic);
			 addChain.execute();

			 setData("result", displayLogic);
		 }
	     return SUCCESS;
	 }
	 
	 public String delete() throws Exception {
		 
		 FacilioChain addChain = QAndATransactionChainFactory.deleteDisplayLogicChain();
		 
		 addChain.getContext().put(DisplayLogicUtil.DISPLAY_LOGIC_CONTEXT, displayLogic);
	
		 addChain.execute();
		 
		 setData("result", displayLogic);
	     return SUCCESS;
	 }

}
