package com.facilio.bmsconsole.commands;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.GraphicsContext;
import com.facilio.bmsconsole.context.GraphicsFolderContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddDefaultGraphicsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ClassLoader classLoader = AddDefaultGraphicsCommand.class.getClassLoader();
		try(FileReader reader = new FileReader(classLoader.getResource("conf/graphics/defaultGraphics.json").getFile());) {
			long orgId = (long) context.get("orgId");

			JSONParser jsonParser = new JSONParser();

			JSONArray defaultGraphics = (JSONArray) jsonParser.parse(reader);

			if (defaultGraphics != null) {
				for (int i = 0; i < defaultGraphics.size(); i++) {
					JSONObject graphicsObj = (JSONObject) defaultGraphics.get(i);

					String name = (String) graphicsObj.get("name");
					String description = (String) graphicsObj.get("description");
					String assetCategory = (String) graphicsObj.get("assetCategory");
					String variables = (String) graphicsObj.get("variables");
					String canvasJSON = (String) graphicsObj.get("canvasJSON");
					String parentFolder = (String) graphicsObj.get("parentFolder");
					Boolean publish = (Boolean) graphicsObj.get("publish");

					if (publish != null && publish == false) {
						continue;
					}

					try (
						FileReader canvasReader = new FileReader(classLoader.getResource("conf/graphics/" + canvasJSON).getFile());
						BufferedReader in = new BufferedReader(canvasReader);
					) {
						String inputLine;
						StringBuffer response = new StringBuffer();
						while ((inputLine = in.readLine()) != null) {
							response.append(inputLine);
						}

						GraphicsFolderContext folderContext = getGraphicsFolder(orgId, parentFolder);

						AssetCategoryContext assetCategoryContext = AssetsAPI.getCategory(assetCategory);
						if (assetCategoryContext != null) {

							GraphicsContext graphicsContext = new GraphicsContext();
							graphicsContext.setOrgId(orgId);
							graphicsContext.setName(name);
							graphicsContext.setDescription(description);
							graphicsContext.setAssetCategoryId(assetCategoryContext.getId());
							if (variables != null && !"".equals(variables.trim())) {
								graphicsContext.setVariables(variables);
							}
							graphicsContext.setCanvas(response.toString());
							graphicsContext.setDefault(true);
							graphicsContext.setParentFolderId(folderContext.getId());

							Map<String, Object> prop = FieldUtil.getAsProperties(graphicsContext);
							GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
									.table(ModuleFactory.getGraphicsModule().getTableName())
									.fields(FieldFactory.getGraphicsFields())
									.addRecord(prop);

							insertRecordBuilder.save();
						}
					}
				}
			}
			return false;
		}
	}

	private GraphicsFolderContext getGraphicsFolder(long orgId, String folderName) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(FieldFactory.getGraphicsFolderFields())
				.table(ModuleFactory.getGraphicsFolderModule().getTableName());

		selectBuilder.andCondition(CriteriaAPI.getOrgIdCondition(orgId, ModuleFactory.getGraphicsFolderModule()));
		selectBuilder.andCondition(CriteriaAPI.getCondition("NAME", "name", folderName, StringOperators.IS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return FieldUtil.getAsBeanFromMap(props.get(0), GraphicsFolderContext.class);
		}
		else {
			GraphicsFolderContext graphicsFolderContext = new GraphicsFolderContext();
			graphicsFolderContext.setOrgId(orgId);
			graphicsFolderContext.setName(folderName);
			
			Map<String, Object> prop = FieldUtil.getAsProperties(graphicsFolderContext);
			GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getGraphicsFolderModule().getTableName())
					.fields(FieldFactory.getGraphicsFolderFields())
					.addRecord(prop);
					
			insertRecordBuilder.save();
			
			long recorId = (Long) prop.get("id");
			graphicsFolderContext.setId(recorId);
			
			return graphicsFolderContext;
		}
	}
}
