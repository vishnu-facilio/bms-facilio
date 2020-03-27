<%@page import="com.facilio.db.builder.DBUtil"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.facilio.constants.FacilioConstants"%>
<%@page import="com.facilio.modules.fields.FacilioField.FieldDisplayType"%>
<%@page import="com.facilio.modules.FacilioModule.ModuleType"%>
<%@page import="com.facilio.modules.FacilioModule"%>
<%@page import="org.apache.commons.collections.CollectionUtils"%>
<%@page import="com.facilio.bmsconsole.util.ApplicationApi"%>
<%@ page import="com.facilio.bmsconsole.commands.FacilioCommand" %>
<%@ page import="org.apache.commons.chain.Context" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="org.apache.log4j.LogManager" %>
<%@ page import="com.facilio.accounts.dto.Organization" %>
<%@ page import="com.facilio.accounts.util.AccountUtil" %>
<%@ page import="com.facilio.chain.FacilioChain" %>
<%@ page import="com.facilio.db.builder.GenericSelectRecordBuilder" %>
<%@ page import="com.facilio.accounts.util.AccountConstants" %>
<%@ page import="com.facilio.db.criteria.CriteriaAPI" %>
<%@ page import="com.facilio.db.criteria.operators.NumberOperators" %>
<%@ page import="org.apache.commons.lang3.tuple.Pair" %>
<%@ page import="com.google.cloud.Tuple" %>
<%@ page import="com.facilio.iam.accounts.util.IAMUtil" %>
<%@ page import="com.facilio.iam.accounts.util.IAMUserUtil" %>
<%@ page import="com.facilio.accounts.util.UserUtil" %>
<%@ page import="com.facilio.modules.FieldUtil" %>
<%@ page import="com.facilio.modules.FieldFactory" %>
<%@ page import="com.facilio.modules.FieldType" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="java.util.*" %>
<%@ page import="com.facilio.db.builder.GenericInsertRecordBuilder" %>
<%@ page import="com.facilio.db.builder.GenericUpdateRecordBuilder" %>
<%@ page import="com.facilio.db.criteria.operators.StringOperators" %>
<%@ page import="org.apache.commons.lang3.tuple.MutablePair" %>
<%@ page import="com.facilio.modules.ModuleFactory" %>
<%@ page import="com.facilio.aws.util.FacilioProperties" %>
<%@ page import="com.facilio.db.transaction.FacilioConnectionPool" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="com.facilio.beans.ModuleBean" %>
<%@ page import="com.facilio.fw.BeanFactory" %>
<%@page import="com.facilio.iam.accounts.util.IAMAccountConstants"%>
<%@page import="java.sql.Statement"%>
<%@page import="com.facilio.service.FacilioService"%>
<%@ page import="com.facilio.accounts.dto.Account" %>
<%@ page import="com.facilio.modules.fields.*" %>


<%--

  ___                      _          _                              _   _            _                       __                           _
 |  __ \                    | |        | |                            | | | |          | |                     / _|                         | |
 | |  | | _    _ _   __ | |_    __| |_   _ _ _ _   _ _  __  | |_| |__   _  | |__   _ _ __  _  | |_ _  _ _ _ _ _   _ _| |
 | |  | |/ _ \  | '_ \ / _ \| _|  / __| ' \ / ` | ' \ / ` |/ _ \ | __| ' \ / _ \ | '_ \ / ` / __|/ _ \ |  _/ _ \| '__| ' ` _ \ / _` | __|
 | |__| | () | | | | | () | |_  | (_| | | | (_| | | | | (_| |  __/ | |_| | | |  __/ | |) | (| \_ \  _/ | || () | |  | | | | | | (| | |
 |_____/ \___/  |_| |_|\___/ \__|  \___|_| |_|\__,_|_| |_|\__, |\___|  \__|_| |_|\___| |_.__/ \__,_|___/\___| |_| \___/|_|  |_| |_| |_|\__,_|\__|
                                                           __/ |
                                                          |___/

--%>




<%
    final class OrgLevelMigrationCommand extends FacilioCommand {
        private final Logger LOGGER = LogManager.getLogger(OrgLevelMigrationCommand.class.getName());
        @Override
        public boolean executeCommand(Context context) throws Exception {

            // Have migration commands for each org
            // Transaction is only org level. If failed, have to continue from the last failed org and not from first
            //add default app domains and applications

            migrate();
            addPeopleMigration();


            return false;
        }
    }
%>

<%!

    class FaciIds {
        long uid;
        long orgid;
        long originalUserid;
        public FaciIds(long uid, long orgid) {
            this.uid = uid;
            this.orgid = orgid;
        }

        public long getOriginalUserId() {
            if (originalUserid > 0) {
                return originalUserid;
            }
            return uid;
        }
    }

    private void migrate() throws Exception {

        //adding domains

        Organization currentOrg = AccountUtil.getCurrentOrg();

        long id1 = FacilioService.runAsServiceWihReturn(() -> insertAppDomain((FacilioProperties.isProduction() ? ".facilioportal.com" : ".facilstack.com"),2, 2,currentOrg.getDomain(),currentOrg.getOrgId()));
        long id2 = FacilioService.runAsServiceWihReturn(() -> insertAppDomain(".faciliovendors.com",4, 3,currentOrg.getDomain(),currentOrg.getOrgId()));
        long id3 = FacilioService.runAsServiceWihReturn(() -> insertAppDomain(".faciliotenants.com",3, 2,currentOrg.getDomain(),currentOrg.getOrgId()));
        long id4 = FacilioService.runAsServiceWihReturn(() -> insertAppDomain(".facilioclients.com",5, 4,currentOrg.getDomain(),currentOrg.getOrgId()));

        List<Map<String, Object>> applicationMap = new ArrayList();

        GenericSelectRecordBuilder builder1 = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getApplicationModule().getTableName())
                .select(FieldFactory.getApplicationFields())
                .andCondition(CriteriaAPI.getCondition("APPLICATION_NAME", "appName", "Facilio", StringOperators.IS));
        Map<String, Object> defaultApplication = builder1.fetchFirst();

        Map<String, Object> servicePortalApp = new HashMap<String, Object>();
        servicePortalApp.put("applicationName", AccountUtil.getCurrentOrg().getDomain()+(FacilioProperties.isProduction() ? ".facilioportal.com" : ".facilstack.com" ));
        servicePortalApp.put("isDefault", false);
        servicePortalApp.put("appDomainId", id1);

        Map<String, Object> vendorPortalApp = new HashMap<String, Object>();
        vendorPortalApp.put("applicationName", AccountUtil.getCurrentOrg().getDomain()+".faciliovendors.com");
        vendorPortalApp.put("isDefault", false);
        vendorPortalApp.put("appDomainId", id2);

        Map<String, Object> tenantPortalApp = new HashMap<String, Object>();
        tenantPortalApp.put("applicationName", AccountUtil.getCurrentOrg().getDomain()+".faciliotenants.com");
        tenantPortalApp.put("isDefault", false);
        tenantPortalApp.put("appDomainId", id3);

        Map<String, Object> clientPortalApp = new HashMap<String, Object>();
        clientPortalApp.put("applicationName", AccountUtil.getCurrentOrg().getDomain()+".facilioclients.com");
        clientPortalApp.put("isDefault", false);
        clientPortalApp.put("appDomainId", id4);


        applicationMap.add(servicePortalApp);
        applicationMap.add(clientPortalApp);
        applicationMap.add(tenantPortalApp);
        applicationMap.add(vendorPortalApp);


        GenericInsertRecordBuilder insertAppbuilder = new GenericInsertRecordBuilder()
                .table("Application")
                .fields(new ArrayList<>(Arrays.asList(
                        FieldFactory.getField("applicationName", "APPLICATION_NAME", FieldType.STRING),
                        FieldFactory.getField("isDefault", "IS_DEFAULT", FieldType.BOOLEAN),
                        FieldFactory.getField("appDomainId", "APP_DOMAIN_ID", FieldType.NUMBER)
                )

                ));

        insertAppbuilder.addRecords(applicationMap);
        insertAppbuilder.save();

        //adding app users
        List<FacilioField> fields = AccountConstants.getAppOrgUserFields();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table("ORG_Users")
                ;
        selectBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getOrgId()), NumberOperators.EQUALS));
        List<Map<String, Object>> mapList = selectBuilder.get();
        UserUtil.setIAMUserProps(mapList, AccountUtil.getCurrentOrg().getId(), false);

        Map<Integer, List<Pair>> appUserIds = new HashMap<>();
        Map<String, FaciIds> userIds = new HashMap<>();
        for (Map<String, Object> map : mapList) {
            Integer appType = (Integer) map.get("appType");
            String email = (String) map.get("email");
            long userId = (long) map.get("uid");

            Long ouid = (Long) map.get("ouid");

            if (appType == null) {
                appType = 0;
            }
            List<Pair> userList = appUserIds.get(appType);
            if (userList == null) {
                userList = new ArrayList<>();
                appUserIds.put(appType, userList);
            }
            userList.add(Pair.of(userId, ouid));
        }

        //FacilioService.runAsService(() -> getNormalizedUsers(userIds));


        for (Integer appType : appUserIds.keySet()) {
            List<Pair> users = appUserIds.get(appType);
            // get application id

            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table("ORG_User_Apps")
                    .fields(new ArrayList<>(Arrays.asList(
                            FieldFactory.getField("appId", "APPLICATION_ID", FieldType.NUMBER),
                            FieldFactory.getField("ouid", "ORG_USERID", FieldType.NUMBER)
                    )
                    ));
            for (Pair user : users) {
                long ouId = (Long)user.getRight();

                long appId = -1;
                String identifier = null;
                switch(appType){
                    case 0 :
                        appId = (Long)defaultApplication.get("id");
                        identifier = "1";
                        break;
                    case 1 :
                        appId = (Long)servicePortalApp.get("id");
                        identifier = "2_" + AccountUtil.getCurrentOrg().getOrgId();
                        break;
                    case 2 :
                        appId = (Long)tenantPortalApp.get("id");
                        identifier = "2_" + AccountUtil.getCurrentOrg().getOrgId();

                        break;
                    case 3 :
                        appId = (Long)vendorPortalApp.get("id");
                        identifier = "3_" + AccountUtil.getCurrentOrg().getOrgId();

                        break;
                    case 4 :
                        appId = (Long)clientPortalApp.get("id");
                        identifier = "4_" + AccountUtil.getCurrentOrg().getOrgId();

                        break;

                }

                updateUserIdentifier((Long)user.getLeft(), identifier);

                Map<String, Object> map = new HashMap<>();
                map.put("appId", appId);
                map.put("ouid", ouId);
                builder.addRecord(map);
            }
            builder.save();
        }
    }

    private void updateUserIdentifier(long uId, String identifier) throws Exception {
        FacilioService.runAsService(() -> updateUser(identifier, uId));
    }

    private void updateUser(String identifier, long uId) throws Exception {
        FacilioField identifierField = new FacilioField();
        identifierField.setName("identifier");
        identifierField.setDataType(FieldType.STRING);
        identifierField.setColumnName("IDENTIFIER");
        identifierField.setModule(IAMAccountConstants.getAccountsUserModule());

        List<FacilioField> fields = new ArrayList<>();
        fields.add(identifierField);

        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(IAMAccountConstants.getAccountsUserModule().getTableName())
                .fields(fields);

        updateBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uId), NumberOperators.EQUALS));

        Map<String, Object> props = new HashMap<>();
        props.put("identifier", identifier);

        int updatedRows = updateBuilder.update(props);

    }
    private long insertAppDomain(String domain, int appDomainType, int groupType, String orgDomain, long orgId) throws Exception {
        List<Map<String, Object>> appDomainMap = new ArrayList();
        Map<String, Object> servicePortal = new HashMap<String, Object>();
        PreparedStatement pstmt = null;
        Connection con = null;
        long id1 = -1;
        ResultSet generatedKeys = null;
          
        try{
            con = FacilioConnectionPool.INSTANCE.getConnection();
            pstmt = con.prepareStatement("INSERT INTO App_Domain(DOMAIN,ORGID,APP_DOMAIN_TYPE, APP_GROUP_TYPE) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
    
            pstmt.setString(1, orgDomain+domain);
            pstmt.setLong(2, orgId);
            pstmt.setLong(3, appDomainType);
            pstmt.setLong(4, groupType);
            pstmt.execute();
            generatedKeys = pstmt.getGeneratedKeys();
            while(generatedKeys.next()) {
                id1 = generatedKeys.getLong(1);
            }
        }
        catch(Exception e){
        }
        finally{
            DBUtil.closeAll(con, pstmt, generatedKeys);  
        }
             
        return id1;
    }
    private void addPeopleMigration() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule bs = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);

        FacilioModule module = new FacilioModule();
        module.setName("people");
        module.setDisplayName("People");
        module.setTableName("People");
        module.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
        module.setTrashEnabled(true);
        module.setType(ModuleType.BASE_ENTITY);
        long pplModId = modBean.addModule(module);
        module.setModuleId(pplModId);

        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .table("Module_Local_ID")
                .fields(new ArrayList<>(Arrays.asList(FieldFactory.getField("moduleName", "MODULE_NAME", FieldType.STRING), FieldFactory.getField("lastLocalId", "LAST_LOCAL_ID", FieldType.NUMBER))));
        Map m = new HashMap();
        m.put("moduleName", module.getName());
        m.put("lastLocalId", 0);
        insertRecordBuilder.addRecord(m);
        insertRecordBuilder.save();

        List<FacilioField> fields = new ArrayList<>();
        FacilioField localId = FieldFactory.getField("localId","ID","LOCAL_ID",module,FieldType.NUMBER);
        localId.setDisplayType(9);
        localId.setDefault(true);
        fields.add(localId);

        FacilioField name = FieldFactory.getField("name","Name","NAME",module,FieldType.STRING);
        name.setDisplayType(1);
        name.setDefault(true);
        name.setRequired(true);
        name.setMainField(true);

        FacilioField phone = FieldFactory.getField("phone","Phone","PHONE",module,FieldType.STRING);
        phone.setDisplayType(1);
        phone.setDefault(true);
        phone.setRequired(true);

        FacilioField email = FieldFactory.getField("email","Email","EMAIL",module,FieldType.STRING);
        email.setDisplayType(1);
        email.setDefault(true);

        SystemEnumField type = (SystemEnumField) FieldFactory.getField("peopleType","People Type","PEOPLE_TYPE",module,FieldType.SYSTEM_ENUM);
        type.setEnumName("PeopleType");
        type.setDisplayType(3);
        type.setDefault(true);

        FacilioField space = FieldFactory.getField("locatedSpace","Located Space","LOCATED_SPACE",module,FieldType.LOOKUP);
        space.setDisplayType(10);
        space.setDefault(true);
        ((LookupField)space).setLookupModule(bs);

        FacilioField isActive = FieldFactory.getField("isActive","Is Active","IS_ACTIVE",module,FieldType.BOOLEAN);
        isActive.setDisplayType(5);
        isActive.setDefault(true);

        FacilioField roleId = FieldFactory.getField("roleId","Role Id","ROLE_ID",module,FieldType.NUMBER);
        roleId.setDisplayType(9);
        roleId.setDefault(true);

        modBean.addField(name);
        modBean.addField(phone);
        modBean.addField(email);
        modBean.addField(type);
        modBean.addField(space);
        modBean.addField(isActive);
        modBean.addField(roleId);

        //ppl atachments module

        FacilioModule pplAttachments = new FacilioModule();
        pplAttachments.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
        pplAttachments.setName("peopleattachments");
        pplAttachments.setDisplayName("People Attachments");
        pplAttachments.setTableName("People_Attachments");
        pplAttachments.setType(ModuleType.ATTACHMENTS);
        long mId = modBean.addModule(pplAttachments);
        pplAttachments.setModuleId(mId);
        modBean.addSubModule(pplModId, mId);

        NumberField srafileIdNF = new NumberField(pplAttachments, "fileId", "File ID", FieldDisplayType.NUMBER, "FILE_ID", FieldType.NUMBER, true, false, true, true);
        modBean.addField(srafileIdNF);

        NumberField sraparentIdNF = new NumberField(pplAttachments, "parentId", "Parent", FieldDisplayType.NUMBER, "PARENT_PEOPLE", FieldType.NUMBER, true, false, true, false);
        modBean.addField(sraparentIdNF);

        NumberField sracreatedTimeNF = new NumberField(pplAttachments, "createdTime", "Created Time", FieldDisplayType.NUMBER, "CREATED_TIME", FieldType.NUMBER, true, false, true, false);
        modBean.addField(sracreatedTimeNF);

        NumberField sratypeNF = new NumberField(pplAttachments, "type", "Type", FieldDisplayType.NUMBER, "ATTACHMENT_TYPE", FieldType.NUMBER, true, false, true, false);
        modBean.addField(sratypeNF);


        //people notes


        FacilioModule pplNotes = new FacilioModule();
        pplNotes.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
        pplNotes.setName("peoplenotes");
        pplNotes.setDisplayName("People Notes");
        pplNotes.setTableName("People_Notes");
        pplNotes.setType(ModuleType.NOTES);
        long notesModId = modBean.addModule(pplNotes);
        pplNotes.setModuleId(notesModId);
        modBean.addSubModule(pplModId, notesModId);

        FacilioField srncreatedTime = new FacilioField(pplNotes, "createdTime", "Created Time", FieldDisplayType.NUMBER, "CREATED_TIME", FieldType.DATE_TIME, true, false, true, false);
        modBean.addField(srncreatedTime);

        LookupField srncreatedByLF = new LookupField(pplNotes, "createdBy", "Created By", FieldDisplayType.LOOKUP_POPUP, "CREATED_BY", FieldType.LOOKUP, false, false, true, false, "users");
        modBean.addField(srncreatedByLF);

        NumberField srnparentIdNF = new NumberField(pplNotes, "parentId", "Parent", FieldDisplayType.NUMBER, "PARENT_ID", FieldType.NUMBER, false, false, true, false);
        modBean.addField(srnparentIdNF);

        FacilioField srntitle = new FacilioField(pplNotes, "title", "Title", FieldDisplayType.TEXTBOX, "TITLE", FieldType.STRING,false, false, true, true);
        modBean.addField(srntitle);

        FacilioField srnbody = new FacilioField(pplNotes, "body", "Body", FieldDisplayType.TEXTAREA, "BODY", FieldType.STRING,false, false, true, false);
        modBean.addField(srnbody);


        //vendor contact

        FacilioModule vendorModule = modBean.getModule(FacilioConstants.ContextNames.VENDORS);
        if(vendorModule != null && vendorModule.getModuleId() > 0){
        FacilioModule vc = new FacilioModule();
        vc.setName("vendorcontact");
        vc.setDisplayName("Vendor Contact");
        vc.setTableName("Vendor_Contacts");
        vc.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
        vc.setTrashEnabled(true);
        vc.setType(ModuleType.BASE_ENTITY);
        vc.setExtendModule(module);
        long vcModId = modBean.addModule(vc);
        vc.setModuleId(vcModId);
//        modBean.addSubModule(vendorModule.getModuleId(), vcModId);


        GenericInsertRecordBuilder insertRecordBuilder2 = new GenericInsertRecordBuilder()
                .table("Module_Local_ID")
                .fields(new ArrayList<>(Arrays.asList(FieldFactory.getField("moduleName", "MODULE_NAME", FieldType.STRING), FieldFactory.getField("lastLocalId", "LAST_LOCAL_ID", FieldType.NUMBER))));
        Map m2 = new HashMap();
        m2.put("moduleName", vc.getName());
        m2.put("lastLocalId", 0);
        insertRecordBuilder2.addRecord(m2);
        insertRecordBuilder2.save();

        FacilioField localId2 = FieldFactory.getField("localId","ID","LOCAL_ID",vc,FieldType.NUMBER);
        localId2.setDisplayType(9);
        localId2.setDefault(true);
        modBean.addField(localId2);

        FacilioField vendor = FieldFactory.getField("vendor","Vendor Id","VENDOR_ID",vc,FieldType.LOOKUP);
        vendor.setDisplayType(10);
        vendor.setDefault(true);
        vendor.setRequired(true);
        ((LookupField)vendor).setLookupModule(vendorModule);
        modBean.addField(vendor);

        FacilioField isPrimaryContact = FieldFactory.getField("isPrimaryContact","Is Primary Contact","IS_PRIMARY_CONTACT",vc,FieldType.BOOLEAN);
        isPrimaryContact.setDisplayType(5);
        isPrimaryContact.setDefault(true);
        ((BooleanField)isPrimaryContact).setTrueVal("Yes");
        ((BooleanField)isPrimaryContact).setFalseVal("No");
        modBean.addField(isPrimaryContact);

        FacilioField vendorPortalAccess = FieldFactory.getField("isVendorPortalAccess","Vendor Portal Access","VENDOR_PORTAL_ACCESS",vc,FieldType.BOOLEAN);
        vendorPortalAccess.setDisplayType(5);
        vendorPortalAccess.setDefault(true);
        ((BooleanField)vendorPortalAccess).setTrueVal("Enabled");
        ((BooleanField)vendorPortalAccess).setFalseVal("Disabled");
        modBean.addField(vendorPortalAccess);

        FacilioField isLabour = FieldFactory.getField("isLabour","Is Labour","IS_LABOUR",vc,FieldType.BOOLEAN);
        isLabour.setDisplayType(5);
        isLabour.setDefault(true);
        ((BooleanField)isLabour).setTrueVal("Yes");
        ((BooleanField)isLabour).setFalseVal("No");
        modBean.addField(isLabour);
        }


        FacilioModule tenantModule = modBean.getModule(FacilioConstants.ContextNames.TENANT);
        if(tenantModule != null && tenantModule.getModuleId() > 0){
        FacilioModule tc = new FacilioModule();
        tc.setName("tenantcontact");
        tc.setDisplayName("Tenant Contact");
        tc.setTableName("Tenant_Contacts");
        tc.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
        tc.setTrashEnabled(true);
        tc.setType(ModuleType.BASE_ENTITY);
        tc.setExtendModule(module);
        long tcModId = modBean.addModule(tc);
        tc.setModuleId(tcModId);
//        modBean.addSubModule(tenantModule.getModuleId(), tcModId);


        GenericInsertRecordBuilder insertRecordBuilder3 = new GenericInsertRecordBuilder()
                .table("Module_Local_ID")
                .fields(new ArrayList<>(Arrays.asList(FieldFactory.getField("moduleName", "MODULE_NAME", FieldType.STRING), FieldFactory.getField("lastLocalId", "LAST_LOCAL_ID", FieldType.NUMBER))));
        Map m3 = new HashMap();
        m3.put("moduleName", tc.getName());
        m3.put("lastLocalId", 0);
        insertRecordBuilder3.addRecord(m3);
        insertRecordBuilder3.save();

        FacilioField localId3 = FieldFactory.getField("localId","ID","LOCAL_ID",tc,FieldType.NUMBER);
        localId3.setDisplayType(9);
        localId3.setDefault(true);
        modBean.addField(localId3);

        FacilioField tenant = FieldFactory.getField("tenant","Tenant Id","TENANT_ID",tc,FieldType.LOOKUP);
        tenant.setDisplayType(10);
        tenant.setDefault(true);
        tenant.setRequired(true);
        ((LookupField)tenant).setLookupModule(tenantModule);
        modBean.addField(tenant);

        FacilioField isPrimaryContact2 = FieldFactory.getField("isPrimaryContact","Is Primary Contact","IS_PRIMARY_CONTACT",tc,FieldType.BOOLEAN);
        isPrimaryContact2.setDisplayType(5);
        isPrimaryContact2.setDefault(true);
        ((BooleanField)isPrimaryContact2).setTrueVal("Yes");
        ((BooleanField)isPrimaryContact2).setFalseVal("No");
        modBean.addField(isPrimaryContact2);

        FacilioField tenantPortalAccess = FieldFactory.getField("isTenantPortalAccess","Tenant Portal Access","TENANT_PORTAL_ACCESS",tc,FieldType.BOOLEAN);
        tenantPortalAccess.setDisplayType(5);
        tenantPortalAccess.setDefault(true);
        ((BooleanField)tenantPortalAccess).setTrueVal("Enabled");
        ((BooleanField)tenantPortalAccess).setFalseVal("Disabled");
        modBean.addField(tenantPortalAccess);

        FacilioField occupantPortalAccess = FieldFactory.getField("isOccupantPortalAccess","Occupant Portal Access","OCCUPANT_PORTAL_ACCESS",tc,FieldType.BOOLEAN);
        occupantPortalAccess.setDisplayType(5);
        occupantPortalAccess.setDefault(true);
        ((BooleanField)occupantPortalAccess).setTrueVal("Enabled");
        ((BooleanField)occupantPortalAccess).setFalseVal("Disabled");
        modBean.addField(occupantPortalAccess);
        }

        //employee

        FacilioModule emp = new FacilioModule();
        emp.setName("employee");
        emp.setDisplayName("Employee");
        emp.setTableName("Employee");
        emp.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
        emp.setTrashEnabled(true);
        emp.setType(ModuleType.BASE_ENTITY);
        emp.setExtendModule(module);
        long empModId = modBean.addModule(emp);
        emp.setModuleId(empModId);


        GenericInsertRecordBuilder insertRecordBuilder4 = new GenericInsertRecordBuilder()
                .table("Module_Local_ID")
                .fields(new ArrayList<>(Arrays.asList(FieldFactory.getField("moduleName", "MODULE_NAME", FieldType.STRING), FieldFactory.getField("lastLocalId", "LAST_LOCAL_ID", FieldType.NUMBER))));
        Map m4 = new HashMap();
        m4.put("moduleName", emp.getName());
        m4.put("lastLocalId", 0);
        insertRecordBuilder4.addRecord(m4);
        insertRecordBuilder4.save();

        FacilioField localId4 = FieldFactory.getField("localId","ID","LOCAL_ID",emp,FieldType.NUMBER);
        localId4.setDisplayType(9);
        localId4.setDefault(true);
        modBean.addField(localId4);

        FacilioField isAssignable = FieldFactory.getField("isAssignable","Can be Assigned","CAN_BE_ASSIGNED",emp,FieldType.BOOLEAN);
        isAssignable.setDisplayType(5);
        isAssignable.setDefault(true);
        ((BooleanField)isAssignable).setTrueVal("Yes");
        ((BooleanField)isAssignable).setFalseVal("No");
        modBean.addField(isAssignable);

        FacilioField isAppAccess = FieldFactory.getField("isAppAccess","App Access","APP_ACCESS",emp,FieldType.BOOLEAN);
        isAppAccess.setDisplayType(5);
        isAppAccess.setDefault(true);
        ((BooleanField)isAppAccess).setTrueVal("Enabled");
        ((BooleanField)isAppAccess).setFalseVal("Disabled");
        modBean.addField(isAppAccess);

        FacilioField occupantPortalAccess2 = FieldFactory.getField("isOccupantPortalAccess","Occupant Portal Access","OCCUPANT_PORTAL_ACCESS",emp,FieldType.BOOLEAN);
        occupantPortalAccess2.setDisplayType(5);
        occupantPortalAccess2.setDefault(true);
        ((BooleanField)occupantPortalAccess2).setTrueVal("Enabled");
        ((BooleanField)occupantPortalAccess2).setFalseVal("Disabled");
        modBean.addField(occupantPortalAccess2);

        FacilioField isLabour2 = FieldFactory.getField("isLabour","Is Labour","IS_LABOUR",emp,FieldType.BOOLEAN);
        isLabour2.setDisplayType(5);
        isLabour2.setDefault(true);
        ((BooleanField)isLabour2).setTrueVal("Yes");
        ((BooleanField)isLabour2).setFalseVal("No");
        modBean.addField(isLabour2);

        FacilioModule clientModule = modBean.getModule(FacilioConstants.ContextNames.CLIENT);
        if(clientModule != null && clientModule.getModuleId() > 0){
        FacilioModule cc = new FacilioModule();
        cc.setName("clientcontact");
        cc.setDisplayName("Client Contact");
        cc.setTableName("client_Contacts");
        cc.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
        cc.setTrashEnabled(true);
        cc.setType(ModuleType.BASE_ENTITY);
        cc.setExtendModule(module);
        long ccModId = modBean.addModule(cc);
        cc.setModuleId(ccModId);
//        modBean.addSubModule(clientModule.getModuleId(), ccModId);


        GenericInsertRecordBuilder insertRecordBuilder5 = new GenericInsertRecordBuilder()
                .table("Module_Local_ID")
                .fields(new ArrayList<>(Arrays.asList(FieldFactory.getField("moduleName", "MODULE_NAME", FieldType.STRING), FieldFactory.getField("lastLocalId", "LAST_LOCAL_ID", FieldType.NUMBER))));
        Map m5 = new HashMap();
        m5.put("moduleName", cc.getName());
        m5.put("lastLocalId", 0);
        insertRecordBuilder5.addRecord(m5);
        insertRecordBuilder5.save();

        FacilioField localId5 = FieldFactory.getField("localId","ID","LOCAL_ID",cc,FieldType.NUMBER);
        localId5.setDisplayType(9);
        localId5.setDefault(true);
        modBean.addField(localId5);

        FacilioField client = FieldFactory.getField("client","Client Id","CLIENT_ID",cc,FieldType.LOOKUP);
        client.setDisplayType(10);
        client.setDefault(true);
        client.setRequired(true);
        ((LookupField)client).setLookupModule(clientModule);
        modBean.addField(client);

        FacilioField isPrimaryContact5 = FieldFactory.getField("isPrimaryContact","Is Primary Contact","IS_PRIMARY_CONTACT",cc,FieldType.BOOLEAN);
        isPrimaryContact5.setDisplayType(5);
        isPrimaryContact5.setDefault(true);
        ((BooleanField)isPrimaryContact5).setTrueVal("Yes");
        ((BooleanField)isPrimaryContact5).setFalseVal("No");
        modBean.addField(isPrimaryContact5);

        FacilioField clientPortalAccess = FieldFactory.getField("isClientPortalAccess","Client Portal Access","CLIENT_PORTAL_ACCESS",cc,FieldType.BOOLEAN);
        clientPortalAccess.setDisplayType(5);
        clientPortalAccess.setDefault(true);
        ((BooleanField)clientPortalAccess).setTrueVal("Enabled");
        ((BooleanField)clientPortalAccess).setFalseVal("Disabled");
        modBean.addField(clientPortalAccess);
        
        }


    }
   
%>

<%
    List<Organization> orgs = AccountUtil.getOrgBean().getOrgs();
    
    ArrayList<Long> prodOrgIds = new ArrayList<Long>(Arrays.asList(1L,2L,53L,58L,60L,63L,75L,78L,81L,88L,90L,92L,93L,95L,99L,104L,107L));
    ArrayList<Long> stageOrgIds = new ArrayList<Long>(Arrays.asList(1L,2L,53L,58L,60L,63L,75L,78L,81L,88L,90L,92L,93L,95L,99L,104L,107L,108L,109L,111L,112L,113L,114L,116L,120L,122L,125L,129L,133L,134L,135L,136L,137L,138L,139L,140L,141L,142L,143L,144L,145L,146L,147L,148L,149L,150L,151L,152L,153L,154L,155L,156L,157L,158L,163L,164L,165L,168L,169L,170L,171L,172L,173L,174L,176L,177L,178L,179L,180L,181L,182L,183L,184L,185L,186L,187L,188L,190L,191L,192L,193L,194L,195L,196L,197L,198L,199L,200L,201L,203L,204L,205L,206L,207L,208L,209L,210L,211L,212L,213L,214L,218L,219L,220L,223L,224L,225L,226L,227L,228L,231L,232L,233L,236L,237L,238L,239L,240L,241L,242L,243L,244L,245L,246L,247L,248L,249L,250L,251L,252L,253L,254L,255L,256L,257L,258L,259L,260L,261L,262L,263L,264L,265L,266L,267L,268L,269L,270L,271L,272L,273L,274L,275L,276L,277L,278L,279L,280L,281L,282L,283L,284L,285L,286L,287L,288L,290L,291L));
    
    for (Organization org : orgs) {
        if(FacilioProperties.isProduction() && prodOrgIds.contains(org.getOrgId())){
            continue;
        }
        if(!FacilioProperties.isProduction() && stageOrgIds.contains(org.getOrgId())){
            continue;
        }
        AccountUtil.setCurrentAccount(org.getOrgId());

        FacilioChain c = FacilioChain.getTransactionChain();
        c.addCommand(new OrgLevelMigrationCommand());
        c.execute();

        AccountUtil.cleanCurrentAccount();
    }
%>