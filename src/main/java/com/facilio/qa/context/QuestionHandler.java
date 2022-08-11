package com.facilio.qa.context;

import com.facilio.qa.displaylogic.context.DisplayLogicContext;
import com.facilio.v3.commands.SaveOptions;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface QuestionHandler<Q extends QuestionContext> {
    default SaveOptions defaultSaveOption() throws Exception { // For now will use the same one for insert and update. We can have separate ones if needed later
        return null;
    }

    public void validateSave (List<Q> questions) throws Exception;

    public void afterSave (List<Q> questions) throws Exception;

    public void validateUpdate (List<Q> questions) throws Exception;

    public void afterUpdate (List<Q> questions) throws Exception;

    public void afterFetch (List<Q> questions) throws Exception;

    /**
     *
     * Remember to override fetchSummaryOfResponse method in corresponding AnswerHandler
     *
     * @param question
     * @return
     * @throws Exception
     */
    default boolean showSummaryOfResponses (QuestionContext question) throws Exception {
        return false;
    }

    default boolean hideShowResponseButton (QuestionContext question) throws Exception {
        return false;
    }
    
    default void addQuestionDisplayLogicActions(QuestionContext question, DisplayLogicContext displayLogic, JSONObject actionJson) {
		
    	JSONArray displayLogicMeta = question.getDisplayLogicMeta() == null ? new JSONArray() : question.getDisplayLogicMeta(); 
		displayLogicMeta.add(actionJson);
		
		question.setDisplayLogicMeta(displayLogicMeta);
    }
    
    default void addAnswerDisplayLogicActions(QuestionContext question, DisplayLogicContext displayLogic, JSONObject actionJson) {
		
    }
}
