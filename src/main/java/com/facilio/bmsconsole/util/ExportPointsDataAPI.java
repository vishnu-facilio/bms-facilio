/**
 * 
 */
package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

/**
 * @author facilio
 *
 */
public class ExportPointsDataAPI {

	private static final Logger LOGGER = Logger.getLogger(ExportPointsDataAPI.class.getName());

	public static Map<String,Object> getTableData(List<String> columns, List<Map<String,Object>> recorddata) throws Exception {

		List<String> headers = new ArrayList<String>(); 

		headers.add("device");
		headers.add("instance");
		headers.add("categoryId");
		headers.add("resourceId");
		headers.add("fieldId");
		headers.add("unit");

		List<Long> category = new ArrayList<>();
		List<Long> field = new ArrayList<>();
		List<Long> asset =new ArrayList<>();

		for(Map<String,Object> map : recorddata) {
			Long categoryId  = (Long) map.get("categoryId");
			Long fieldId = (Long) map.get("fieldId");
			Long assetId = (Long) map.get("resourceId");
			if(categoryId != null) {
				category.add(categoryId);
			}
			if(fieldId != null) {
				field.add(fieldId);
			}
			if(assetId != null) {
				asset.add(assetId);
			}

		}
		System.out.println("category id is "+ category);

		if(!category.isEmpty() && category!=null) {

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

			FacilioModule assetCategoryModule = modBean.getModule("resource");
			SelectRecordsBuilder<AssetCategoryContext> builder = new SelectRecordsBuilder<AssetCategoryContext>()
					.module(assetCategoryModule).beanClass(AssetCategoryContext.class)
					.select(modBean.getAllFields(assetCategoryModule.getName()))
					.andCondition(CriteriaAPI.getIdCondition(asset, assetCategoryModule));

			List<Map<String, Object>> props = builder.getAsProps();
			List<Map<String,Object>> nam =new ArrayList<>();

			if(props!=null && !props.isEmpty()) {
				for(Map<String,Object> map : props) {
					Map<String, Object> n =new HashMap<>();
					String id = map.get("id").toString();
					String name = (String) map.get("name");
					n.put("id",id);
					n.put("name", name);
					nam.add(n);
				}

			}
			for(Map<String,Object> map1 : recorddata) {
				Long mapid= (Long) map1.get("resourceId");
				if(mapid!=null) {
					for(Map<String,Object> map : nam) {
						long id = Long.parseLong((String) map.get("id"));
						String assetname = (String) map.get("name");
						if(mapid != id) {
							continue;
						}
						else {
							map1.put("resourceId", assetname);
							break;
						}

					}
				}
				else {
					continue;
				}
			}
			for(Map<String,Object> map1 : recorddata) {
				String fieldName= (String) map1.get("displayName");
				String categoryName =(String) map1.get("name") ;
				map1.put("fieldId",fieldName);
				map1.put("categoryId",categoryName);
			}
		}

		Map<String,Object> table = new HashMap<String, Object>();
		table.put("headers", headers);
		table.put("records", recorddata);
		return table;

	}

	public static List<Map<String, Object>> getPointsData(long controllerId) throws Exception {
		// TODO Auto-generated method stub

		List<FacilioField> field = FieldFactory.getPointsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(field);
		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getDeviceField(ModuleFactory.getPointsModule()));
		fields.add(FieldFactory.getInstanceField(ModuleFactory.getPointsModule()));
		fields.add(FieldFactory.getField("categoryId","ASSET_CATEGORY_ID",ModuleFactory.getPointsModule(),FieldType.NUMBER));
		fields.add(FieldFactory.getField("resourceId","RESOURCE_ID",ModuleFactory.getPointsModule(),FieldType.NUMBER));
		fields.add(FieldFactory.getField("fieldId","FIELD_ID",ModuleFactory.getPointsModule(),FieldType.NUMBER));
		fields.add(FieldFactory.getField("displayName","DISPLAY_NAME",ModuleFactory.getFieldsModule(),FieldType.STRING));
		fields.add(FieldFactory.getField("name","NAME",ModuleFactory.getAssetCategoryModule(),FieldType.STRING));

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getPointsModule().getTableName())
				.leftJoin(ModuleFactory.getAssetCategoryModule().getTableName())
				.on("Points.ASSET_CATEGORY_ID="+"Asset_Categories.ID")
				.leftJoin(ModuleFactory.getFieldsModule().getTableName())
				.on("Points.FIELD_ID="+"Fields.FIELDID")
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("controllerId"),String.valueOf(controllerId) ,StringOperators.IS));

		List<Map<String, Object>> props = builder.get();

		LOGGER.info("Select query for ExportPoints  "+builder.toString());

		return props;



	}
}
