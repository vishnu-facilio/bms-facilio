package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.GraphicsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class GetGraphicsForAssetCategoryCommand implements Command {


	@Override
	public boolean execute(Context context) throws Exception {
		
		long assetCategoryId = (long) context.get(FacilioConstants.ContextNames.ASSET_CATEGORY);
		Long assetId = (Long) context.get(FacilioConstants.ContextNames.ASSET_ID);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(FieldFactory.getGraphicsFields())
				.table(ModuleFactory.getGraphicsModule().getTableName())
				.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), ModuleFactory.getGraphicsModule()));

		selectBuilder.andCondition(CriteriaAPI.getCondition("ASSET_CATEGORY_ID", "assetCategory", assetCategoryId+"", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		List<GraphicsContext> graphicsContexts = new ArrayList<>();
		
		
		if(props != null && !props.isEmpty()) {
			for(Map<String, Object> prop :props) {
				GraphicsContext graphicsContext = FieldUtil.getAsBeanFromMap(prop, GraphicsContext.class);
				try {
					if (assetId != null && assetId > 0 && graphicsContext.getApplyTo() != null && !"".equals(graphicsContext.getApplyTo().trim())) {
						JSONParser jsonParser = new JSONParser();
						JSONObject applyTo = (JSONObject) jsonParser.parse(graphicsContext.getApplyTo());
						Integer applyToType = 1;
						if (applyTo.get("applyToType") instanceof Long) {
							applyToType = ((Long) applyTo.get("applyToType")).intValue();
						}
						else if (applyTo.get("applyToType") instanceof Integer) {
							applyToType = (Integer) applyTo.get("applyToType");
						}
						if (applyToType != null && applyToType == 2) { // specific assets
							JSONArray applyToAssetIds = (JSONArray) applyTo.get("applyToAssetIds");
							if (applyToAssetIds != null) {
								if (applyToAssetIds.indexOf(assetId) < 0) {
									continue;
								}
							}
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				graphicsContexts.add(graphicsContext);
			}
		}
		
		context.put(FacilioConstants.ContextNames.GRAPHICS_LIST, graphicsContexts);
		
		return false;
	}
}
