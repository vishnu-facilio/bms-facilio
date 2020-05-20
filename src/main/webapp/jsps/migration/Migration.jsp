<%@page import="com.facilio.accounts.dto.Organization"%>
<%@ page import="com.facilio.accounts.util.AccountUtil" %>
<%@ page import="com.facilio.aws.util.FacilioProperties" %>
<%@ page import="com.facilio.beans.ModuleBean" %>
<%@ page import="com.facilio.bmsconsole.commands.FacilioChainFactory" %>
<%@ page import="com.facilio.bmsconsole.commands.FacilioCommand" %>
<%@ page import="com.facilio.bmsconsole.context.AssetCategoryContext" %>
<%@ page import="com.facilio.chain.FacilioChain" %>
<%@ page import="com.facilio.chain.FacilioContext" %>
<%@ page import="com.facilio.constants.FacilioConstants" %>
<%@ page import="com.facilio.db.builder.GenericInsertRecordBuilder" %>
<%@ page import="com.facilio.fw.BeanFactory" %>
<%@ page import="com.facilio.modules.FacilioModule" %>
<%@ page import="com.facilio.modules.FieldFactory" %>
<%@ page import="com.facilio.modules.FieldType" %>
<%@ page import="com.facilio.modules.ModuleFactory" %>
<%@ page import="com.facilio.modules.fields.FacilioField" %>
<%@ page import="com.facilio.modules.fields.NumberField" %>
<%@ page import="org.apache.commons.chain.Context" %>
<%@ page import="org.apache.log4j.LogManager" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Objects" %>


<%--

  _____                      _          _                              _   _            _                       __                           _
 |  __ \                    | |        | |                            | | | |          | |                     / _|                         | |
 | |  | | ___    _ __   ___ | |_    ___| |__   __ _ _ __   __ _  ___  | |_| |__   ___  | |__   __ _ ___  ___  | |_ ___  _ __ _ __ ___   __ _| |_
 | |  | |/ _ \  | '_ \ / _ \| __|  / __| '_ \ / _` | '_ \ / _` |/ _ \ | __| '_ \ / _ \ | '_ \ / _` / __|/ _ \ |  _/ _ \| '__| '_ ` _ \ / _` | __|
 | |__| | (_) | | | | | (_) | |_  | (__| | | | (_| | | | | (_| |  __/ | |_| | | |  __/ | |_) | (_| \__ \  __/ | || (_) | |  | | | | | | (_| | |_
 |_____/ \___/  |_| |_|\___/ \__|  \___|_| |_|\__,_|_| |_|\__, |\___|  \__|_| |_|\___| |_.__/ \__,_|___/\___| |_| \___/|_|  |_| |_| |_|\__,_|\__|
                                                           __/ |
                                                          |___/

--%>

<%
    final class OrgLevelMigrationCommand extends FacilioCommand {
        private final Logger LOGGER = LogManager.getLogger(OrgLevelMigrationCommand.class.getName());
        @Override
        public boolean executeCommand(Context context) throws Exception {
                long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg(), "current org can't be null").getOrgId();
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule controllerModule = modBean.getModule("controller");
                Objects.requireNonNull(controllerModule, "controller module cant be null");

                FacilioModule systemControllerModule = new FacilioModule();
                systemControllerModule.setName("systemController");
                systemControllerModule.setDisplayName("System Controller");
                systemControllerModule.setTableName("System_Controller");
                systemControllerModule.setExtendModule(controllerModule);
                systemControllerModule.setType(FacilioModule.ModuleType.BASE_ENTITY);
                systemControllerModule.setTrashEnabled(true);


                long systemControllerModuleId = modBean.addModule(systemControllerModule);
                LOGGER.info("systemController module addition " + systemControllerModuleId);
                if (systemControllerModuleId > 0) {
                    systemControllerModule.setModuleId(systemControllerModuleId);


                    FacilioModule assetcategoryModule = modBean.getModule("assetcategory");
                    Objects.requireNonNull(assetcategoryModule, "asset category module is null");
                    LOGGER.info(" adding asset category ");
                    AssetCategoryContext assetCategoryContext = new AssetCategoryContext();
                    assetCategoryContext.setName("systemController");
                    assetCategoryContext.setType(AssetCategoryContext.AssetCategoryType.CONTROLLER);
                    assetCategoryContext.setDisplayName("System Controller");
                    assetCategoryContext.setAssetModuleID(systemControllerModuleId);
                    assetCategoryContext.setIsDefault(true);

                    FacilioChain addAssetCategoryChain = FacilioChainFactory.justAddAssetCategoryChain();
                    FacilioContext context1 = addAssetCategoryChain.getContext();
                    context1.put(FacilioConstants.ContextNames.RECORD, assetCategoryContext);
                    context1.put(FacilioConstants.ContextNames.MODULE_NAME,assetcategoryModule.getName());
                    addAssetCategoryChain.execute();

                    LOGGER.info("added asset category");
                    NumberField agentIdField = new NumberField(systemControllerModule,"agentId","Agent Id",FacilioField.FieldDisplayType.NUMBER,"AGENT_ID",FieldType.NUMBER,false,false,true,false);
                    modBean.addField(agentIdField);
                    NumberField nameField = new NumberField(systemControllerModule,"name","Name",FacilioField.FieldDisplayType.TEXTBOX,"NAME",FieldType.STRING,false,false,true,false);
                    modBean.addField(nameField);
                    LOGGER.info("sysyetcontroller fields done");
                    FacilioModule controllerReadingModule = new FacilioModule();
                    controllerReadingModule.setName("controllerReadings");
                    controllerReadingModule.setDisplayName("Controller Readings");
                    controllerReadingModule.setTableName("System_Controller_Readings");
                    controllerReadingModule.setType(FacilioModule.ModuleType.READING);
                    LOGGER.info("adding controller readings module");
                    long controllerReadingModuleId = modBean.addModule(controllerReadingModule);
                    LOGGER.info(" controllerReading module added " + controllerReadingModuleId);
                    if (controllerReadingModuleId > 0) {
                        controllerModule.setModuleId(controllerReadingModuleId);
                        modBean.addSubModule(systemControllerModuleId, controllerReadingModuleId);
                        LOGGER.info("submodule added");
                        Map<String, Object> toInsertMap = new HashMap<>();

                        toInsertMap.put("parentCategoryId", assetCategoryContext.getId());
                        toInsertMap.put("readingModuleId", controllerReadingModuleId);
                        GenericInsertRecordBuilder genericInsertRecordBuilder = new GenericInsertRecordBuilder()
                                .table(ModuleFactory.getAssetCategoryReadingRelModule().getTableName())
                                .fields(FieldFactory.getAssetCategoryReadingRelFields());
                        long assCatReadingId = genericInsertRecordBuilder.insert(toInsertMap);
                        LOGGER.info("added asset category reading");
                        controllerReadingModule.setModuleId(controllerReadingModuleId);
                        List<FacilioField> defaultReadingFields = FieldFactory.getDefaultReadingFields(controllerReadingModule);
                        for (FacilioField defaultReadingField : defaultReadingFields) {
                            modBean.addField(defaultReadingField);
                        }
                        NumberField loadAvgField = new NumberField(controllerReadingModule, "loadAverage", "Lpad Average", FacilioField.FieldDisplayType.NUMBER, "LOAD_AVERAGE", FieldType.NUMBER, true, false, true, false);
                        loadAvgField.setUnitId(87);
                        loadAvgField.setMetric(16);
                        modBean.addField(loadAvgField);
                        NumberField loadedClassField = new NumberField(controllerReadingModule, "totalLoadedClassCount", "Total Loaded Class Count", FacilioField.FieldDisplayType.NUMBER, "TOTAL_LOADED_CLASS_COUNT", FieldType.NUMBER, true, false, true, false);
                        modBean.addField(loadedClassField);
                        NumberField freeHeapMemoryField = new NumberField(controllerReadingModule, "freeHeapMemory", "Free Heap Memory", FacilioField.FieldDisplayType.NUMBER, "FREE_HEAP_MEMORY", FieldType.NUMBER, true, false, true, false);
                        freeHeapMemoryField.setMetric(27);
                        freeHeapMemoryField.setUnitId(124);
                        modBean.addField(freeHeapMemoryField);
                        NumberField freeNonHeapMemory = new NumberField(controllerReadingModule, "freeNonHeapMemory", "Free Non Heap Memory", FacilioField.FieldDisplayType.NUMBER, "FREE_NON_HEAP_MEMORY", FieldType.NUMBER, true, false, true, false);
                        freeNonHeapMemory.setMetric(27);
                        freeNonHeapMemory.setUnitId(124);
                        modBean.addField(freeNonHeapMemory);
                        NumberField freeDiskSpace = new NumberField(controllerReadingModule, "freeDiskSpace", "Free Disk Space", FacilioField.FieldDisplayType.NUMBER, "FREE_DISK_SPACE", FieldType.NUMBER, true, false, true, false);
                        freeNonHeapMemory.setMetric(27);
                        freeNonHeapMemory.setUnitId(124);
                        modBean.addField(freeDiskSpace);
                        NumberField freeMemoryField = new NumberField(controllerReadingModule, "freeMemory", "Free Memory", FacilioField.FieldDisplayType.NUMBER, "FREE_MEMORY", FieldType.NUMBER, true, false, true, false);
                        freeNonHeapMemory.setMetric(27);
                        freeNonHeapMemory.setUnitId(124);
                        modBean.addField(freeMemoryField);
                        NumberField deadLockedThreadsFields = new NumberField(controllerReadingModule, "deadLockedThreads", "Dead Locked Threads", FacilioField.FieldDisplayType.NUMBER, "DEAD_LOCKED_THREADS", FieldType.NUMBER, true, false, true, false);
                        modBean.addField(deadLockedThreadsFields);
                        NumberField peakThreadCountFields = new NumberField(controllerReadingModule, "peakThreadCount", "Peak Thread Count", FacilioField.FieldDisplayType.NUMBER, "PEAK_THREAD_COUNT", FieldType.NUMBER, true, false, true, false);
                        modBean.addField(peakThreadCountFields);
                        NumberField monitorDeadLockedThreadsFields = new NumberField(controllerReadingModule, "monitorDeadLockedThreads", "Monitor Dead Locked Threads", FacilioField.FieldDisplayType.NUMBER, "MONITOR_DEAD_LOCKED_THREADS", FieldType.NUMBER, true, false, true, false);
                        modBean.addField(monitorDeadLockedThreadsFields);

                        NumberField liveThreadCountFields = new NumberField(controllerReadingModule, "liveThreadCount", "Live Thread Count", FacilioField.FieldDisplayType.NUMBER, "LIVE_THREAD_COUNT", FieldType.NUMBER, true, false, true, false);
                        modBean.addField(liveThreadCountFields);
                    } else {
                        throw new Exception("controllerREadingModule can't be added");
                    }

                } else {
                    throw new Exception("systemController module can't be added");
                }
                LOGGER.info("Completed For -- " + AccountUtil.getCurrentOrg().getId());
            return false;
        }
    }
%>

<%
    List<Organization> orgs = AccountUtil.getOrgBean().getOrgs();
    for (Organization org : orgs) {
        AccountUtil.setCurrentAccount(org.getOrgId());
        FacilioChain c = FacilioChain.getTransactionChain();
        c.addCommand(new OrgLevelMigrationCommand());
        boolean isStage = ! FacilioProperties.isProduction();
        if( (isStage) && ( (org.getOrgId() != 152) && (org.getOrgId() != 138) ) ){
            c.execute();
        }else if( ! isStage){
            c.execute();
        }
        AccountUtil.cleanCurrentAccount();
    }
%>