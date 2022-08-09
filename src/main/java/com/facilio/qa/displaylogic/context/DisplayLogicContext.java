package com.facilio.qa.displaylogic.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.db.criteria.Criteria;
import com.facilio.qa.context.PageContext;
import com.facilio.qa.context.QAndATemplateContext;
import com.facilio.qa.context.QuestionContext;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
public class DisplayLogicContext {
	
	Long id;
	Long orgId;
	String name;
	Long templateId;
	Long criteriaId;
	DisplayLogicType type;
	Long pageId;
	Long questionId;
	Long rowId;
	Long columnId;
	Boolean status;
	
	private Long sysCreatedTime;
    private Long sysCreatedBy;
    private Long sysModifiedTime;
    private Long sysModifiedBy;
    
    Criteria criteria;
    QAndATemplateContext template;
    PageContext page;
    QuestionContext question;
    
	List<DisplayLogicAction> actions;
	List<DisplayLogicTriggerQuestions> triggerQuestions;
	
	public DisplayLogicType getDisplayLogicTypeEnum() {
		return type;
	}
	public int getType() {
		if(type != null) {
			return type.getIntValue();
		}
		return -1;
	}
	public void setType(int actionType) {
		this.type = DisplayLogicType.getAllType().get(actionType);
	}
	
	public void addDisplayLogicTriggerQuestions(DisplayLogicTriggerQuestions triggerQuestion) {
		triggerQuestions = triggerQuestions == null ? new ArrayList<DisplayLogicTriggerQuestions>() : triggerQuestions;
		triggerQuestions.add(triggerQuestion);
	}
	
	@Getter
	public enum DisplayLogicType {
		PAGE_SKIP(1,false),
		QUESTION_DISPLAY(2,true),
		ANSWER_DISPLAY(3,true),
		;

		int intValue;
		boolean isQuestionDependent;
		DisplayLogicType(int id,boolean isQuestionDependent) {
			this.intValue = id;
			this.isQuestionDependent = isQuestionDependent;
		}
		
		private static final Map<Integer, DisplayLogicType> optionMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, DisplayLogicType> initTypeMap() {
			Map<Integer, DisplayLogicType> typeMap = new HashMap<>();

			for (DisplayLogicType type : values()) {
				typeMap.put(type.getIntValue(), type);
			}
			return typeMap;
		}

		public static Map<Integer, DisplayLogicType> getAllType() {
			return optionMap;
		}
	}

	public void resetObjects() {
		this.template = null;
		this.page = null;
		this.question = null;
	}
}
