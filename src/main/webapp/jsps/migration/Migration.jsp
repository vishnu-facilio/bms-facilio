<%@ page import="com.facilio.bmsconsole.commands.FacilioCommand" %>
<%@ page import="com.facilio.chain.FacilioChain" %>
<%@ page import="org.apache.commons.chain.Context" %>
<%@ page import="com.facilio.fw.BeanFactory" %>
<%@ page import="com.facilio.beans.ModuleBean" %>
<%@ page import="com.facilio.modules.FacilioModule" %>
<%@ page import="com.facilio.accounts.dto.Organization" %>
<%@ page import="com.facilio.accounts.util.AccountUtil" %>
<%@ page import="com.facilio.modules.fields.FacilioField" %>
<%@ page import="com.facilio.modules.fields.LookupField" %>
<%@ page import="com.facilio.db.builder.GenericSelectRecordBuilder" %>
<%@ page import="com.facilio.modules.FieldFactory" %>
<%@ page import="com.facilio.modules.FieldType" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="com.facilio.accounts.dto.Account" %>
<%@ page import="com.facilio.db.builder.GenericUpdateRecordBuilder" %>
<%@ page import="org.apache.commons.collections4.CollectionUtils" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="com.facilio.db.criteria.CriteriaAPI" %>
<%@ page import="com.facilio.db.criteria.operators.NumberOperators" %>


<%!
    public void updateField(Long fieldId, Long lookupModuleId) throws Exception {
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table("LookupFields")
                .fields(Collections.singletonList(FieldFactory.getField("lookupModuleId", "LOOKUP_MODULE_ID", FieldType.NUMBER)))
                .andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", String.valueOf(fieldId), NumberOperators.EQUALS))
                ;
        Map<String, Object> map = new HashMap<>();
        map.put("lookupModuleId", lookupModuleId);
        updateRecordBuilder.update(map);
    }
%>

<%

    FacilioChain chain = FacilioChain.getTransactionChain();
    chain.addCommand(new FacilioCommand() {
        @Override
        public boolean executeCommand(Context context) throws Exception {
            System.out.println("Hi da");

            GenericSelectRecordBuilder orgBuilder = new GenericSelectRecordBuilder()
                    .table("Organizations")
                    .select(Collections.singletonList(FieldFactory.getField("orgId", "ORGID", FieldType.NUMBER )));
            List<Map<String, Object>> maps = orgBuilder.get();
            System.out.println(maps);

            for (Map<String, Object> map : maps) {
                AccountUtil.setCurrentAccount((long) map.get("orgId"));

                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule basespace = modBean.getModule("basespace");

                System.out.println(basespace);

                LookupField buildingField = (LookupField) modBean.getField("building", basespace.getName());
                if (buildingField.getLookupModule().getName().equals("building")) {
                    System.out.println("Semma");
                }
                else {
                    System.out.println("Do migration");
                    FacilioModule building = modBean.getModule("building");
                    FacilioModule floor = modBean.getModule("floor");
                    FacilioModule space = modBean.getModule("space");

                    LookupField floorField = (LookupField) modBean.getField("floor", basespace.getName());
                    LookupField space1Field = (LookupField) modBean.getField("space1", basespace.getName());
                    LookupField space2Field = (LookupField) modBean.getField("space2", basespace.getName());
                    LookupField space3Field = (LookupField) modBean.getField("space3", basespace.getName());
                    LookupField space4Field = (LookupField) modBean.getField("space4", basespace.getName());

                    updateField(buildingField.getFieldId(), building.getModuleId());
                    updateField(floorField.getFieldId(), floor.getModuleId());
                    updateField(space1Field.getFieldId(), space.getModuleId());
                    updateField(space2Field.getFieldId(), space.getModuleId());
                    updateField(space3Field.getFieldId(), space.getModuleId());
                    updateField(space4Field.getFieldId(), space.getModuleId());
                }
            }
            return false;
        }
    });
    chain.execute();
    System.out.println("Ena da");
%>
