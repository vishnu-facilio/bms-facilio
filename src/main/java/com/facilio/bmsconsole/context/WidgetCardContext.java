package com.facilio.bmsconsole.context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.cards.util.CardUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;

public class WidgetCardContext extends DashboardWidgetContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String cardLayout;
	public String getCardLayout() {
		return cardLayout;
	}
	public void setCardLayout(String cardLayout) {
		this.cardLayout = cardLayout;
	}
	
	private ScriptMode scriptMode = ScriptMode.NONE;
	
	public void setScriptMode(ScriptMode scriptMode) {
		this.scriptMode = scriptMode;
	}
	
	public ScriptMode getScriptMode() {
		return this.scriptMode;
	}
	
	public void setScriptModeInt(int scriptModeInt) {

		 if (scriptModeInt == 3) {
			this.scriptMode = ScriptMode.NONE;
		}
		else if(scriptModeInt == 1) {
			this.scriptMode = ScriptMode.DEFAULT_SCRIPT;
		}
		else if(scriptModeInt == 2) {
			this.scriptMode = ScriptMode.CUSTOM_SCRIPT;
		}
		else {
			this.scriptMode = ScriptMode.NONE;
		}
	}
	
	public int getScriptModeInt() {
		return this.scriptMode.getValue();
	}
	
	private Long customScriptId;
	
	public Long getCustomScriptId() {
		return this.customScriptId;
	}
	
	public void setCustomScriptId(Long customScriptId) {
		this.customScriptId = customScriptId;
	}
	
	private String customScript;
	
	public void setCustomScript(String customScript) {
		this.customScript = customScript;
	}
	
	public String getCustomScript() {
		return this.customScript;
	}
	private Long parentId;
	public void setParentId(Long parentId) { this.parentId = parentId; }
	public Long getParentId() { return this.parentId; }
	private Long criteriaId;
	public void setCriteriaId(Long criteriaId) { this.criteriaId = criteriaId; }
	public Long getCriteriaId() { return this.criteriaId; }
	private Long categoryId;
	public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
	public Long getCategoryId() { return this.categoryId; }
	private List<WidgetCardContext> childCards;
	public void setChildCards(List<WidgetCardContext> childCards) { this.childCards = childCards; }
	public List<WidgetCardContext> getChildCards() { return this.childCards; }

	private JSONObject cardParams;
	
	public JSONObject getCardParams() {
		return cardParams;
	}
	public void setCardParams(JSONObject cardParams) {
		this.cardParams = cardParams;
	}
	public String getCardParamsJSON() {
		if (cardParams != null) {
			return cardParams.toJSONString();
		}
		return null;
	}
	public void setCardParamsJSON(String cardParams) throws Exception {
		if (cardParams != null) {
			this.cardParams = (JSONObject) new JSONParser().parse(cardParams);
		}
	}
	
	private JSONObject cardState;
	
	public JSONObject getCardState() {
		return cardState;
	}
	public void setCardState(JSONObject cardState) {
		this.cardState = cardState;
	}
	public String getCardStateJSON() {
		if (cardState != null) {
			return cardState.toJSONString();
		}
		return null;
	}
	public void setCardStateJSON(String cardState) throws Exception {
		if (cardState != null) {
			this.cardState = (JSONObject) new JSONParser().parse(cardState);
		}
	}
	
	private JSONObject conditionalFormatting;
	
	public JSONObject getConditionalFormatting() {
		return conditionalFormatting;
	}
	public void setConditionalFormatting(JSONObject conditionalFormatting) {
		this.conditionalFormatting = conditionalFormatting;
	}
	public String getConditionalFormattingJSON() {
		if (conditionalFormatting != null) {
			return conditionalFormatting.toJSONString();
		}
		return null;
	}
	public void setConditionalFormattingJSON(String conditionalFormatting) throws Exception {
		if (conditionalFormatting != null) {
			this.conditionalFormatting = (JSONObject) new JSONParser().parse(conditionalFormatting);
		}
	}
	
	private JSONObject cardDrilldown;
	
	public JSONObject getCardDrilldown() {
		return cardDrilldown;
	}
	public void setCardDrilldown(JSONObject cardDrilldown) {
		this.cardDrilldown = cardDrilldown;
	}
	public String getCardDrilldownJSON() {
		if (cardDrilldown != null) {
			return cardDrilldown.toJSONString();
		}
		return null;
	}
	public void setCardDrilldownJSON(String cardDrilldown) throws Exception {
		if (cardDrilldown != null) {
			this.cardDrilldown = (JSONObject) new JSONParser().parse(cardDrilldown);
		}
	}
	
	private JSONObject cardFilters;
	
	public JSONObject getCardFilters() {
		return cardFilters;
	}
	public void setCardFilters(JSONObject cardFilters) {
		this.cardFilters = cardFilters;
	}
	
private JSONObject cardUserFilters;

private DashboardCustomScriptFilter cardCustomScriptFilters;

	
	public DashboardCustomScriptFilter getCardCustomScriptFilters() {
	return cardCustomScriptFilters;
}
public void setCardCustomScriptFilters(DashboardCustomScriptFilter cardCustomScriptFilters) {
	this.cardCustomScriptFilters = cardCustomScriptFilters;
}
	public JSONObject getCardUserFilters() {
		return cardUserFilters;
	}
	public void setCardUserFilters(JSONObject cardUserFilters) {
		this.cardUserFilters = cardUserFilters;
	}
	
	

	@Override
	public JSONObject widgetJsonObject(boolean optimize) {
		JSONObject resultJson = new JSONObject();
		
		resultJson.put("id", getId());
		resultJson.put("link_name", getLinkName());
		resultJson.put("type", getWidgetType().getName());
		resultJson.put("widgetSettings",getWidgetSettings());
		resultJson.put("helpText",getHelpText());
		JSONObject layoutJson = new JSONObject();
		layoutJson.put("height", getLayoutHeight());
		layoutJson.put("width", getLayoutWidth());
		layoutJson.put("x", getxPosition());
		layoutJson.put("y", getyPosition());
		layoutJson.put("position", getLayoutPosition());
		
		resultJson.put("layout", layoutJson);
		
		JSONObject mlayoutJson = new JSONObject();
		mlayoutJson.put("height", getmLayoutHeight());
		mlayoutJson.put("width", getmLayoutWidth());
		mlayoutJson.put("x", getmXPosition());
		mlayoutJson.put("y", getmYPosition());
		mlayoutJson.put("position", getmLayoutPosition());
		
		resultJson.put("mLayout", mlayoutJson);
		
		JSONObject headerJson = new JSONObject();
		headerJson.put("title", getHeaderText());
		// Temprovery 
		if(getHeaderSubText() != null && getHeaderSubText().equals("{today}")) {
			headerJson.put("subtitle", "today");
		}
		else {
			headerJson.put("subtitle", getHeaderSubText());
		}
		
		headerJson.put("export", isHeaderIsExport());
		
		resultJson.put("header", headerJson);
		
		JSONObject dataOptionsJson = new JSONObject();
		dataOptionsJson.put("dataurl", "");
		dataOptionsJson.put("name", "dummy");
		dataOptionsJson.put("refresh_interval", getDataRefreshIntervel());
		
		dataOptionsJson.put("cardLayout", cardLayout);
		dataOptionsJson.put("cardParams", cardParams);
		dataOptionsJson.put("cardState", cardState);
		dataOptionsJson.put("cardDrilldown", cardDrilldown);
		dataOptionsJson.put("conditionalFormatting", conditionalFormatting);
		dataOptionsJson.put("scriptModeInt",getScriptModeInt());
		dataOptionsJson.put("customScriptId",getCustomScriptId());
		dataOptionsJson.put("parentId",getParentId());
		dataOptionsJson.put("childCards",getChildCards());

		resultJson.put("customActions", getCustomActions());
		resultJson.put("dataOptions", dataOptionsJson);
		
		
		JSONObject widgetJson = new JSONObject();
		widgetJson.put("widget", resultJson);
		widgetJson.put("label", getWidgetName());
//		System.out.println("resultJson -- "+widgetJson);
		return widgetJson;
	}
	@Override
	public JSONObject widgetMobileJsonObject(boolean optimize, int index) throws Exception {

		JSONObject widgetJson = new JSONObject();
		widgetJson.put("label", getWidgetName());
		widgetJson.put("id", getId());
		widgetJson.put("title", getHeaderText());
		widgetJson.put("type", getWidgetType().getName());
		widgetJson.put("helpText",getHelpText());
		widgetJson.put("link_name", getLinkName());
		widgetJson.put("sequence", index);
		widgetJson.put("cardLayout", getCardLayout());
		widgetJson.put("widgetSettings",getWidgetSettings());
		widgetJson.put("cardDrilldown", CardUtil.getDrillDownObj(getCardDrilldown(),getCardParams(),getId()));
		if(getChildCards() != null && getChildCards().size() > 0){
			widgetJson.put("childCards", CardUtil.getChildCardsResponse(getChildCards()));
		}
		if(getCardLayout().contains("web")){
			widgetJson.put("web_url", "dashboard/"+ AccountUtil.getCurrentApp().getLinkName() + "/widget/" + getId());
		}
		return widgetJson;
	}
	
	public static enum ScriptMode {
		DEFAULT_SCRIPT(1),
		CUSTOM_SCRIPT(2),
		NONE(3);
		
		private int value;
		
		ScriptMode(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	}
}
