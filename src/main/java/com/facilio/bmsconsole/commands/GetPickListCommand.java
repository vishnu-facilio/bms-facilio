package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class GetPickListCommand extends FacilioCommand {

	private static org.apache.log4j.Logger log = LogManager.getLogger(GetPickListCommand.class.getName());


	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String search = (String) context.get(FacilioConstants.ContextNames.SEARCH);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		FacilioField defaultField = (FacilioField) context.get(FacilioConstants.ContextNames.DEFAULT_FIELD);
		//Connection conn = FacilioConnectionPool.INSTANCE.getConnection();
		
		try {
			if(dataTableName != null && !dataTableName.isEmpty() && defaultField != null) {
				List<FacilioField> fields = new ArrayList<>();
				fields.add(defaultField);				
				SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
																	.table(dataTableName)
																	.moduleName(moduleName)
																	.select(fields)
																	.orderBy("ID");
				
				
				if (search != null) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioField primaryField = modBean.getPrimaryField(moduleName);
					builder.andCondition(CriteriaAPI.getCondition(primaryField, search, StringOperators.CONTAINS));

				}
				
				JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
				Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
				if (filterCriteria != null) {
					builder.andCriteria(filterCriteria);
				}
				
				JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
				if (pagination != null) {
					int page = (int) pagination.get("page");
					int perPage = (int) pagination.get("perPage");

					if (perPage != -1) {
						int offset = ((page-1) * perPage);
						if (offset < 0) {
							offset = 0;
						}

						builder.offset(offset);
						builder.limit(perPage);
					}

				}
				
				List<Map<String, Object>> records = builder.getAsProps();
				Map<Long, String> pickList = new HashMap<>();
				if(records != null && records.size() > 0) {
					for(Map<String, Object> record : records) {
						pickList.put((Long) record.get("id"), record.get(defaultField.getName()).toString());
					}
				}
				context.put(FacilioConstants.ContextNames.PICKLIST, pickList);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Exception occurred during retrieval of pick list for "+moduleName);
			log.info("Exception occurred ", e);
		}
		finally
		{
			//conn.close();
		}
		
		return false;
	}

}
