package com.facilio.bmsconsole.commands;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.templates.DefaultTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;

public class RecommendedRuleCommand extends FacilioCommand implements Serializable{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		final Logger logger = Logger.getLogger(RecommendedRuleCommand.class.getName());

		logger.log(Level.INFO, "processing Recommend Templates");
		Collection<DefaultTemplate> json=null;
		Collection<DefaultTemplate> Result=   new ArrayList<>();
		List<Map<String, Object>> combinedvalues=new ArrayList<>();
		List<Map<String, Object>> instances = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.INSTANCE_INFO);
		for (Map<String, Object> instanceMap: instances) {
			Map<String,Object> combinedValuesFields=new HashMap<>();
			long fieldId = (long) instanceMap.get("fieldId");
			combinedValuesFields.put("FIELDID", fieldId);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField beanField = modBean.getField(fieldId);	
			combinedValuesFields.put("FIELD_NAME",beanField.getName());
			combinedValuesFields.put("MODULE_NAME",beanField.getModule().getName());
			combinedValuesFields.put("CATEGORY_ID",(long) instanceMap.get("categoryId"));
			combinedvalues.add(combinedValuesFields);
		}	
	
		
		
		List<FacilioField> Fields = new ArrayList<>();
		Fields.add(FieldFactory.getField("ruleid", "RULE_ID",FieldType.NUMBER ));

		
		GenericSelectRecordBuilder builder=new GenericSelectRecordBuilder()
				.select(Fields)
				.table(ModuleFactory.getRecommendedRuleModule().getTableName());
		List<Map<String, Object>> existRuleIds =builder.get();
		List<Long> existingRule=new ArrayList<>();
		for(Map<String, Object> ruleId: existRuleIds)
		{
			existingRule.add((Long)ruleId.get("ruleid"));
		}
		
		List<FacilioField> Points = new ArrayList<>();
		Points.add(FieldFactory.getField("fieldid", "FIELD_ID",FieldType.NUMBER ));
		Points.add(FieldFactory.getField("resourceid", "RESOURCE_ID",FieldType.NUMBER ));		
		GenericSelectRecordBuilder pointsbuilder=new GenericSelectRecordBuilder().select(Points).table(ModuleFactory.getPointsModule().getTableName());
		List<Map<String, Object>> PointsData =pointsbuilder.get();
		List<Map<String,Object>> fieldresource=new ArrayList<>();
		
		
		
		for(Map<String,Object> value:PointsData) {
		Map<String,Object> fieldresourceMap=new HashMap<>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField beanField = modBean.getField((long) value.get("fieldid"));	
		fieldresourceMap.put("fieldname",beanField.getName());
		fieldresourceMap.put("modulename",beanField.getModule().getName());
		fieldresourceMap.put("resourceid",value.get("resourceid"));
		fieldresource.add(fieldresourceMap);
		}
		List<Long> comp=new ArrayList<>();
		Map<Map<Long,String>,List<String>> groupedList=new HashMap<>();
		for(Map<String,Object> firstiter:fieldresource)
		{
			Map<Long,String> resourceidwithmodule=new HashMap<>();
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
			resourceidwithmodule.put((Long) firstiter.get("resourceid"),(String) firstiter.get("modulename"));
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
				for(Map<String,Object> cv:combinedvalues)
				{
					if(jsonArray.contains(cv.get("FIELD_NAME")) && cv.get("MODULE_NAME").equals(jsonObject.get("moduleName").toString().toLowerCase()))
					{
							for(Map<Long,String> str:groupedList.keySet())
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
									if(flag==0 && !(finalruleids.contains(defaultTemplateMap.getId())) && !(existingRule.contains(defaultTemplateMap.getId())))
									{
										Map<String,Object> insert=new HashMap<>();
										finalruleids.add(defaultTemplateMap.getId());
										insert.put("RULE_ID",defaultTemplateMap.getId());
										insert.put("CATEGORY_ID",cv.get("CATEGORY_ID"));
										tobeinsert.add(insert);
									}
								}
							}
					}
				}
				
			}
		
		if(tobeinsert.size()>0)
		{

			List<FacilioField> ff=new ArrayList<>();
			ff.add(FieldFactory.getField("RULE_ID", "RULE_ID", FieldType.NUMBER));
			ff.add(FieldFactory.getField("CATEGORY_ID", "CATEGORY_ID", FieldType.NUMBER));
			GenericInsertRecordBuilder builderinsert = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getRecommendedRuleModule().getTableName())
					.fields(ff);
			builderinsert.addRecords(tobeinsert);
			builderinsert.save();

		}
		return false;
	}	

}
