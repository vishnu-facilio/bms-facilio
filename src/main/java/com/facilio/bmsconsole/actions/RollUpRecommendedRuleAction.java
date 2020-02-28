package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.templates.DefaultTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class RollUpRecommendedRuleAction extends FacilioAction{
	private static final long serialVersionUID = 1L;
	Collection<DefaultTemplate> json=null;
	Collection<DefaultTemplate> Result=   new ArrayList<>();
	public String getRecommendedList() throws Exception
	{
		List<Long> ids=new ArrayList<>();
		List<FacilioField> categoryIds = new ArrayList<>();
		categoryIds.add(FieldFactory.getField("categoryid", "ID",FieldType.NUMBER ));
		GenericSelectRecordBuilder AssetCategoriesbuilder=new GenericSelectRecordBuilder()
				.select(categoryIds)
				.table(ModuleFactory.getAssetCategoryModule().getTableName())
				.andCustomWhere("NAME=? or NAME=? or NAME=?", "AHU","FAHU","FCU");
		
		for(Map<String,Object> id:AssetCategoriesbuilder.get())
		{
			ids.add((Long) id.get("categoryid"));
		}		
		
	List<FacilioField> Points = new ArrayList<>();
	Points.add(FieldFactory.getField("fieldid", "FIELD_ID",FieldType.NUMBER ));
	Points.add(FieldFactory.getField("resourceid", "RESOURCE_ID",FieldType.NUMBER ));	
	Points.add(FieldFactory.getField("categoryid", "ASSET_CATEGORY_ID",FieldType.NUMBER ));		
	GenericSelectRecordBuilder pointsbuilder=new GenericSelectRecordBuilder()
			.select(Points)
			.table(ModuleFactory.getPointsModule().getTableName())
			.andCondition((CriteriaAPI.getCondition(FieldFactory.getField("categoryid", "ASSET_CATEGORY_ID",FieldType.NUMBER ), ids, NumberOperators.EQUALS)));
	List<Map<String, Object>> PointsData =pointsbuilder.get();
	List<Map<String,Object>> fieldresource=new ArrayList<>();
	
	
	
	for(Map<String,Object> value:PointsData) {
	Map<String,Object> fieldresourceMap=new HashMap<>();
	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	FacilioField beanField = modBean.getField((long) value.get("fieldid"));	
	fieldresourceMap.put("fieldname",beanField.getName());
	fieldresourceMap.put("modulename",beanField.getModule().getName());
	fieldresourceMap.put("resourceid",value.get("resourceid"));
	fieldresourceMap.put("categoryid",value.get("categoryid"));
	fieldresource.add(fieldresourceMap);
	}
	List<Long> comp=new ArrayList<>();
	Map<Map<List<Long>,String>,List<String>> groupedList=new HashMap<>();
	for(Map<String,Object> firstiter:fieldresource)
	{
		List<Long> categoryResourceList=new ArrayList<>();
		Map<List<Long>,String> resourceidwithmodule=new HashMap<>();
		List<String>  fieldNames=new ArrayList<>();
		if(!(comp.contains(firstiter.get("resourceid"))))
		{
		for(Map<String,Object> seconditer:fieldresource) 
		{
			if(firstiter.get("resourceid").equals(seconditer.get("resourceid")))
			{
				fieldNames.add((String) seconditer.get("fieldname"));
			}
		}
		categoryResourceList.add((Long) firstiter.get("resourceid"));
		categoryResourceList.add((Long) firstiter.get("categoryid"));
		resourceidwithmodule.put(categoryResourceList,(String) firstiter.get("modulename"));
		groupedList.put(resourceidwithmodule, fieldNames);
		}
		comp.add((Long) firstiter.get("resourceid"));
	} 
	

	List<Map<String,Object>> tobeinsert=new ArrayList<>();
	json= (Collection<DefaultTemplate>) TemplateAPI.getAllRuleLibraryTemplate();
	Iterator i=json.iterator();
	int flag;
	List<Long> finalruleids=new ArrayList<>();
	while(i.hasNext())
	{			
		DefaultTemplate defaultTemplateMap;
		defaultTemplateMap=(DefaultTemplate) i.next();
		JSONObject jsonObject=(JSONObject)defaultTemplateMap.getJson();
		jsonObject=(JSONObject)jsonObject.get("fdd_rule");
		JSONArray jsonArray=(JSONArray)jsonObject.get("field_metric");
					for(Map<List<Long>,String> str:groupedList.keySet())
					{
						flag=0;
						if(str.containsValue(jsonObject.get("moduleName").toString().toLowerCase()))
						{
							for(int j=0;j<jsonArray.size();j++)
							{
								if(!(groupedList.get(str).contains(jsonArray.get(j))))
								{
									flag=1;
								}
							}
							if(flag==0 && !(finalruleids.contains(defaultTemplateMap.getId())) )
							{
								Map<String,Object> insert=new HashMap<>();
								finalruleids.add(defaultTemplateMap.getId());
								insert.put("RULE_ID",defaultTemplateMap.getId());
								Set<List<Long>> groupedListKeySets=str.keySet();
								Long categoryid=0l;
								for(List<Long> groupedListKeySet:groupedListKeySets)
								{
									categoryid=groupedListKeySet.get(1);
								}
								insert.put("CATEGORY_ID",categoryid);
								tobeinsert.add(insert);
							}
						}
					}
			}
		
		if(tobeinsert.size()>0)
		{

			List<FacilioField> Rulefields=new ArrayList<>();
			Rulefields.add(FieldFactory.getField("RULE_ID", "RULE_ID", FieldType.NUMBER));
			Rulefields.add(FieldFactory.getField("CATEGORY_ID", "CATEGORY_ID", FieldType.NUMBER));
			GenericInsertRecordBuilder builderinsert = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getRecommendedRuleModule().getTableName())
					.fields(Rulefields);
			builderinsert.addRecords(tobeinsert);
			builderinsert.save();
		}
	
	return SUCCESS;

	}
}