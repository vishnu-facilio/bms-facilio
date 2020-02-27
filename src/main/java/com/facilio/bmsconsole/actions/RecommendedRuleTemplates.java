package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.templates.DefaultTemplate;
import com.facilio.bmsconsole.templates.DefaultTemplate.DefaultTemplateType;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class RecommendedRuleTemplates extends FacilioAction{
	private static final long serialVersionUID = 1L;

	private JSONObject params;
	public JSONObject getParams() {
		return params;
	}
	public void setParams(JSONObject params) {
		this.params = params;
	}
	public String GetRecommendedList() throws Exception
	{
		
		Collection<DefaultTemplate> allRules=  new ArrayList<>();
		Collection<DefaultTemplate> recRules=  new ArrayList<>();
		Map<String,Collection<DefaultTemplate>> Rules=new HashMap<>();
		List<Long> ruleIds=new ArrayList<>();
		Collection<DefaultTemplate> json=(Collection<DefaultTemplate>) TemplateAPI.getAllRuleLibraryTemplate();;
		
		
		List<FacilioField> ff=new ArrayList<>();
		ff.add(FieldFactory.getField("RULE_ID", "RULE_ID", FieldType.NUMBER));
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getRecommendedRuleModule().getTableName())
				.select(ff);
		List<Map<String, Object>> prop = builder.get();
		for(Map<String,Object> ruleid: prop)
		{
			ruleIds.add((Long)ruleid.get("RULE_ID"));
		}
		Iterator i=json.iterator();
		while(i.hasNext())
		{			
			DefaultTemplate defaultTemplateMap;
			defaultTemplateMap=(DefaultTemplate) i.next();
			if(ruleIds.contains(defaultTemplateMap.getId()))
			{
				recRules.add(defaultTemplateMap);
			}
			allRules.add(defaultTemplateMap);
		}
		Rules.put("recommended", recRules);
		Rules.put("all", allRules);
		setResult("templates",Rules);
		return SUCCESS;
	}
	
	
	public String GetAssetsAvailability() throws Exception
	{
		Map<String,Long> ruleAppliedDetails=new HashMap<>();
		List<FacilioField> categoryFields=new ArrayList<>();
		
		
		categoryFields.add(FieldFactory.getField("categoryid", "ID", FieldType.NUMBER));
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY);
		SelectRecordsBuilder<AssetCategoryContext> selectBuilder = new SelectRecordsBuilder<AssetCategoryContext>()
				.select(categoryFields)
				.module(module)
				.beanClass(AssetCategoryContext.class)
				.andCustomWhere("NAME=?", params.get("category"));
		Long category_id=(Long) selectBuilder.fetchFirst().getDatum("categoryid");

		
		List<FacilioField> fields=new ArrayList<>();
		fields.add(FieldFactory.getField("field","FIELD_ID",FieldType.NUMBER));
		fields.add(FieldFactory.getField("resource","RESOURCE_ID",FieldType.NUMBER));
		GenericSelectRecordBuilder builder=new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getPointsModule().getTableName())
				.andCustomWhere("ASSET_CATEGORY_ID=?", category_id);
		
		//RULE FIELDS VS ASSET FIELDS COMPARISON

		Map<String,Long> values=new HashMap<>();
		List<Object> temp=new ArrayList<>();
		Map<Long,List<String>> grouped=new HashMap<>();
		ArrayList<String> unavailableFields=	(ArrayList<String>) params.get("Fields");
		for(String unavail:unavailableFields)
		{
			values.put(unavail, 0l);
		}
		System.out.println(values);
		int notSetCount=0;
		for(Map<String,Object> asset:builder.get())
		{
			int notSetFlag=0;
			List<String> assetFields=new ArrayList<>();
			if(!(temp.contains(asset.get("resource")))) {
			for(Map<String,Object> asset2:builder.get())
			{
				if(asset.get("resource").equals(asset2.get("resource")))
				{
					FacilioField beanField = modBean.getField((long) asset2.get("field"));
					assetFields.add((String) beanField.getName());
				}
			}
			grouped.put((Long) asset.get("resource"), assetFields);
			temp.add(asset.get("resource"));
			for(String unavail:unavailableFields)
			{
				if(!(assetFields.contains(unavail)))
				{
					values.put(unavail,values.get(unavail)+1 );
					notSetFlag=1;
				}
			}
			if(notSetFlag==1)
			{
				notSetCount++;
			}
			}
		}
		ruleAppliedDetails.put("available",(long) (grouped.size()-notSetCount));
		ruleAppliedDetails.put("unavailable",(long) (notSetCount));
		setResult("index",ruleAppliedDetails);
		return SUCCESS;
	
	}
}
