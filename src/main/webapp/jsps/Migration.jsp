<%@ page import="com.facilio.sql.*,java.util.*,com.facilio.bmsconsole.modules.FieldFactory" %>


<%

GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder();
genericSelectRecordBuilder.select(FieldFactory.getReportFolderFields())
.table("Report_Folder");

List<Map<String, Object>> props = genericSelectRecordBuilder.get();

out.println(props);

if(!props.isEmpty()) {
	for(Map<String, Object> prop:props) {
		
		Map<String, Object> map = new HashMap<>();
		map.put("orgId", prop.get("orgId"));
		map.put("moduleId", prop.get("moduleId"));
		GenericUpdateRecordBuilder genericUpdateRecordBuilder = new GenericUpdateRecordBuilder();
		genericUpdateRecordBuilder.table("Report");
		genericUpdateRecordBuilder.fields(FieldFactory.getReportFields());
		genericUpdateRecordBuilder.andCustomWhere("Report.REPORT_FOLDER_ID = "+prop.get("id"));
		
		genericUpdateRecordBuilder.update(map);
		
	}
}



%>