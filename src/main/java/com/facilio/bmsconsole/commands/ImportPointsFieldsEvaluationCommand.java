package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class ImportPointsFieldsEvaluationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<Map<String,Object>> firstRow = (List<Map<String, Object>>) context.get("IMPORT_POINTS_DATA");
		long controllerId = (long) context.get("CONTROLLER_ID");
		List<Map<String,Object>> finalList = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> itr:firstRow) {
			String categoryVal = (String) itr.get("Asset Category");
			String fieldVal = (String) itr.get("Reading");
			String resource = (String) itr.get("Assets");

			long val = checkCategory(categoryVal);
			if(val != 0 ) {
				itr.put("Asset Category",String.valueOf(val));
			}else {
				throw new IllegalArgumentException("This Asset_Category doesn't exists : "+categoryVal);
			}

			long valasset = checkResources(val, resource);
			if(valasset != 0 ) {
				itr.put("Assets",String.valueOf(valasset));
			}else {
				throw new IllegalArgumentException("This Asset "+valasset +" doesn't exists for this Asset_Category : "+categoryVal);
			}

			long valfield = checkFields(val,fieldVal);
			if(valfield != 0 ) {
				itr.put("Reading",String.valueOf(valfield));
			}else {
				throw new IllegalArgumentException("Field "+fieldVal +" doesn't exists for this Asset_Category : "+categoryVal);
			}

		finalList.add(itr);
		
		}
//		FacilioContext updatePoinsts = new FacilioContext();
		context.put("POINTS_LIST", finalList);
		context.put("CONTROLLER_ID",controllerId);
		
		return false;
	}

	public  long checkCategory(String categoryVal) throws Exception {
		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", AccountUtil.getCurrentOrg().getOrgId());
		List<AssetCategoryContext> assetCategory = bean.getCategoryList();
		for (AssetCategoryContext list : assetCategory) {
			if(list.getDisplayName().equals(categoryVal)) {
				return list.getId();
			}
		}

		return 0;

	}

	public long checkResources(long categoryId ,  String asset) throws Exception, IllegalAccessException {

		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", AccountUtil.getCurrentOrg().getOrgId());
		List<AssetContext> AssetListForCategory = bean.getAssetListOfCategory(categoryId);

		for(AssetContext list : AssetListForCategory) {
			if(list.getName().equals(asset)) {
				return list.getId();
			}
		}
		
		return 0;

	}
	public long checkFields(long categoryId, String fieldVal) throws Exception {

		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", AccountUtil.getCurrentOrg().getOrgId());
		List<FacilioModule> fieldsList = bean.getAssetReadings(categoryId);
		
		for (FacilioModule list : fieldsList) {
			for (FacilioField fields : list.getFields()) {
				if(fields.getDisplayName().equals(fieldVal)) {
					return fields.getId();
				}
			}
		}
		
		return 0;

	}

}
