package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.context.ImportRowContext;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.AssetsAPI;

public class ImportProcessLogContext {
	Long id, orgId, importId, templateId,parentId;
	String assetName;
	public String getAssetName(){
		return this.assetName;
	}
	public void setAssetName(String assetName){
		this.assetName = assetName;
	}
	Integer total_rows;
	Integer error_resolved;
	public Long getTtime() {
		return ttime;
	}
	public void setTtime(Long ttime) {
		this.ttime = ttime;
	}
	List<ImportRowContext> rowContexts;
	String rowContextString;
	Long ttime;
	
	public String getRowContextString() throws Exception{
		if (rowContexts != null) {
			return FieldUtil.getAsJSONArray(rowContexts, ImportRowContext.class).toJSONString();
		}
		return null;
	}
	public void setRowContextString(String rowContextString) throws Exception{
		JSONParser parser = new JSONParser();
		JSONArray jsonarray = (JSONArray) parser.parse(rowContextString);
		List<ImportRowContext> listOfRows = new ArrayList<ImportRowContext>();
		for( Object jsonObject :jsonarray) {
			JSONObject json = (JSONObject) jsonObject;
			ImportRowContext rowContext = FieldUtil.getAsBeanFromJson(json, ImportRowContext.class);
			listOfRows.add(rowContext);
		}
		setRowContexts(listOfRows);
	}
	ImportRowContext correctedRow;
	String correctedRowString;
	
	public String getCorrectedRowString() throws Exception {
		if(correctedRow != null) {
			return FieldUtil.getAsJSON(correctedRow).toJSONString();
		}
		return null;
	}
	public void setCorrectedRowString(String correctedRowString) throws Exception {
		System.out.println("corrected row string" + correctedRowString);
		if(correctedRowString == null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject)parser.parse(getCorrectedRowString());
			setCorrectedRow(FieldUtil.getAsBeanFromJson(json, ImportRowContext.class));
			this.correctedRowString = getCorrectedRowString();
		}
		else {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject)parser.parse(correctedRowString);
			setCorrectedRow(FieldUtil.getAsBeanFromJson(json, ImportRowContext.class));
			this.correctedRowString = correctedRowString;
		}
		
	}
	public ImportRowContext getCorrectedRow() {
		return correctedRow;
	}
	public void setCorrectedRow(ImportRowContext correctedRow) {
		this.correctedRow = correctedRow;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) throws Exception {
		this.parentId = parentId;
		if(parentId != null && parentId > 0) {
			 setAssetName(AssetsAPI.getAssetInfo(parentId).getName());
		}
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public Long getImportId() {
		return importId;
	}
	public void setImportId(Long importId) {
		this.importId = importId;
	}
	public Long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
	public Integer getTotal_rows() {
		return total_rows;
	}
	public void setTotal_rows(Integer total_rows) {
		this.total_rows = total_rows;
	}
	
	public Integer getError_resolved() {
		return error_resolved;
	}
	public void setError_resolved(Integer error_resolved) {
		this.error_resolved = error_resolved;
	}
	
	public List<ImportRowContext> getRowContexts() throws Exception{
		return rowContexts;
	}
	
	public void setRowContexts(List<ImportRowContext> rowContexts) {
		this.rowContexts = rowContexts;
	}
	
}
