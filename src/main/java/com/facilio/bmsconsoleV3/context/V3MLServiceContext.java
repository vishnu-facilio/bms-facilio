package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.context.MLResponseContext;
import com.facilio.v3.context.V3Context;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author seenimohamed
 */
@Data
@JsonIgnoreProperties(value = {"readingVariables", "modelReadings"} , allowSetters = true)
public class V3MLServiceContext extends V3Context {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(V3MLServiceContext.class.getName());

	/**
	 * User given input variables 
	 */
	private String modelName;
	private String projectName;
	private String serviceType;
	private String inputType="default";
	private Long parentAssetId;
	private List<Long> childAssetIds;
	private JSONObject workflowInfo;
	private Long startTime;
	private Long endTime;
	private JSONObject mlModelVariables;

	//for custom ml models
	private JSONObject groupingMethod;
	private JSONArray filteringMethod;

	/**
	 * Module variables
	 */
	private String mlModelMeta;
	private boolean failed;
	private String status;
	private Long workflowId;
	private boolean isHistoric;

	/**
	 * Internal usage variables
	 */
	private Long mlID;
	private List<Long> parentMlIdList;
	private List<Long> childMlIdList;

	private List<String> readingVariables;
	private List<List<Map<String, Object>>> modelReadings;
	private Long trainingSamplingPeriod;
	private JSONObject trainingSamplingJson;
	private List<JSONArray> dataObjectList;
	private List<MLResponseContext> mlResponseList;

	public JSONObject getMlModelMetaJson(){
		JSONObject mlModelMeta = new JSONObject();
		try {
			if(this.mlModelMeta!=null) {

				mlModelMeta = (JSONObject) new JSONParser().parse(this.mlModelMeta);
			}
		} catch (ParseException e) {
			LOGGER.error("Not able to parse mlModelMeta for mlservice id ::"+this.getId());
		}
		return mlModelMeta;
	}
	public void setModelReadings(List<List<Map<String,Object>>> modelReadings){
		this.modelReadings = modelReadings;
	}
}
