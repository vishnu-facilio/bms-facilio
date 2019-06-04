<<<<<<< Updated upstream
=======
<%@page import="java.lang.reflect.Array"%>
<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@ page import="com.facilio.bmsconsole.util.PreventiveMaintenanceAPI" %>
<%@ page import ="java.util.ArrayList, com.facilio.constants.FacilioConstants"%>
<%@ page import ="java.util.HashMap, org.apache.logging.log4j.util.Strings"%>
<%@ page import ="java.util.Map, com.facilio.beans.ModuleBean, com.facilio.fw.BeanFactory"%>
<%@page import = "com.facilio.db.builder.GenericSelectRecordBuilder, com.facilio.modules.ModuleFactory, com.facilio.db.builder.GenericUpdateRecordBuilder,
com.facilio.modules.FieldFactory, com.facilio.modules.fields.FacilioField,com.facilio.modules.FacilioModule" %>
<%@ page import ="java.util.List"%>
<%
/* List<Long> orgs = new ArrayList<Long>();
                orgs.add(78L); */
        Long orgs = 1L;
        AccountUtil.setCurrentAccount(orgs);
        Map<String, String> fields =  new HashMap<String, String>();  
		fields.put("Run Status", "Run Status");
		fields.put("Auto/Manual Status", "Auto/Manual Status");
		fields.put("Trip Status", "Trip Status");
		fields.put("Filter Status", "Filter Dirty Status");
		fields.put("Run Command","Run Command" );
		fields.put("Valve Command", "Cooling Coil Valve Command");
		fields.put("Valve Feedback", "Cooling Coil Valve FeedBack ");
		fields.put("Supply Temperature", "Supply Air Temperature " );
		fields.put("Supply Temperature Set Point", "Supply Air Temperature Set-point");
		String moduleName = "ahu";
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule("ahu");
		List<FacilioField> facolioFields = modBean.getModuleFields("ahu");
		List<FacilioModule> subModukes = modBean.getAllSubModules("ahu");
		 List<Long> modulesIds = new ArrayList<Long>();
 		for (FacilioModule sModule : subModukes) {
 			modulesIds.add(sModule.getModuleId());
 		}
 		String idsString = "(" + Strings.join(modulesIds, ',') + ")";
		for (String name : fields.keySet())   {
            Map<String, Object> defaultMapFields, customMapFields;

			String fieldsName = fields.get(name);
			
			
			GenericSelectRecordBuilder customSelect = new GenericSelectRecordBuilder();
			List<FacilioField> queryFields = new ArrayList<>();
			queryFields.addAll(FieldFactory.getSelectFieldFields());
			customSelect.table("Fields")
			.select(queryFields)
			.andCustomWhere("Fields.ORGID = ? AND Fields.DISPLAY_NAME LIKE ?  AND Fields.MODULEID IN " + idsString + "AND (IS_DEFAULT IS NULL OR IS_DEFAULT = false)", orgs, name);
			
			List<Map<String, Object>> customFields = customSelect.get();	

			
			if (customFields.size() > 0) {
				customMapFields = customFields.get(0);
				out.println(customFields.size() + "\n" + "???????? hash_map.keySet() ---" + customMapFields.get("moduleId") );
			}
			GenericSelectRecordBuilder defaultFieldsQuery = new GenericSelectRecordBuilder();
			defaultFieldsQuery.table("Fields")
			.select(queryFields)
			.andCustomWhere("Fields.ORGID = ? AND Fields.DISPLAY_NAME LIKE ? AND Fields.MODULEID IN " + idsString +  "AND Fields.IS_DEFAULT IS TRUE", orgs, fieldsName );
			
             List<Map<String, Object>> defaultField = defaultFieldsQuery.get();
             if (defaultField.size() > 0) {
            	 defaultMapFields = defaultField.get(0);
 				out.println(defaultField.size() + "???????? defaultField ---" + defaultField.get(0) );
 			}
             if (defaultMapFields != null && customMapFields != null) {
            	 FacilioField updateCustomField = new FacilioField();
            	 FacilioField updateDefaultField = new FacilioField();
/*             	 updateCustomField.setFieldId(defaultField)
 */             }
		}				


%>	
>>>>>>> Stashed changes
