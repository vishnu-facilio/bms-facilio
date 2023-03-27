package com.facilio.datamigration.action;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.datamigration.commands.DataMigrationChainFactory;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.modules.FieldUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.struts2.ServletActionContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter @Getter @Log4j
public class DataMigrationAction extends FacilioAction {
    private long sourceOrgId;
    private long targetOrgId;
    private Long dataMigrationId;
    private long transactionTimeout;
    private boolean siteScoped;
    private Map<Long, Long> siteIdMapping;
    private Map<Long, Long> userIdMapping;
    private Map<Long, Long> groupIdMapping;
    private Map<Long, Long> roleIdMapping;
    private Map<Long,Long> stateFlowMapping;
    private Map<Long,Long> slaMapping;
    private Map<Long,Long> formMapping;
    private String skipModuleNames;
    private String logModuleNames;
    private String runOnlyForModules;
    private String moduleSequence;

    public String execute() throws Exception{

        FacilioChain dataMigrationChain = DataMigrationChainFactory.getDataMigrationChain();
        FacilioContext dataMigrationContext = dataMigrationChain.getContext();

        this.setFetchStackTrace(true);
        if(sourceOrgId == 10l && targetOrgId == 615l) {
            if(formMapping == null) {
                formMapping = new HashMap<>();
            }
            formMapping.put(91l, 6031l);
            formMapping.put(109l, 6035l);
            formMapping.put(322l, 6033l);
            formMapping.put(323l, 6037l);
            formMapping.put(89l, 6039l);
            formMapping.put(324l, 6041l);
            formMapping.put(88l, 6043l);
            formMapping.put(90l, 6064l);
            formMapping.put(343l, 6162l);
            formMapping.put(415l, 6027l);
            formMapping.put(321l, 6089l);
            formMapping.put(92l, 6046l);
            formMapping.put(151l, 6403l);
            formMapping.put(507l, 6404l);
            formMapping.put(1650l, 6405l);
            formMapping.put(152l, 6057l);
            formMapping.put(250l, 6098l);
            formMapping.put(153l, 6076l);
            formMapping.put(1764l, 6076l);
            formMapping.put(1642l, 6061l);
            formMapping.put(154l, 6059l);
            formMapping.put(1706l, 6056l);
            formMapping.put(1733l, 6062l);
            formMapping.put(1678l, 6060l);
            formMapping.put(310l, 6394l);
            formMapping.put(311l, 6395l);
            formMapping.put(312l, 6395l);
            formMapping.put(314l, 6397l);
            formMapping.put(320l, 6398l);
            formMapping.put(325l, 6399l);
            formMapping.put(326l, 6400l);
            formMapping.put(347l, 6406l);
            formMapping.put(304l, 6401l);
            formMapping.put(288l, 6408l);
            formMapping.put(303l, 6393l);

            if(roleIdMapping == null) {
                roleIdMapping = new HashMap<>();
            }
            roleIdMapping.put(38l, 451l);
            roleIdMapping.put(39l, 444l);
            roleIdMapping.put(40l, 445l);
            roleIdMapping.put(41l, 446l);
            roleIdMapping.put(42l, 447l);
            roleIdMapping.put(43l, 448l);
            roleIdMapping.put(44l, 449l);
            roleIdMapping.put(47l, 450l);
            roleIdMapping.put(63l, 451l);
            roleIdMapping.put(203l, 452l);
            roleIdMapping.put(227l, 453l);
            roleIdMapping.put(133l, 447l);
            roleIdMapping.put(219l, 447l);
            roleIdMapping.put(220l, 447l);
            roleIdMapping.put(221l, 447l);
            roleIdMapping.put(222l, 447l);
            roleIdMapping.put(261l, 454l);
            roleIdMapping.put(291l, 455l);
            roleIdMapping.put(393l, 456l);
            roleIdMapping.put(45l, 451l);
            roleIdMapping.put(46l, 451l);
            roleIdMapping.put(133l, 451l);
            roleIdMapping.put(145l, 451l);
            roleIdMapping.put(158l, 451l);
            roleIdMapping.put(173l, 451l);
            roleIdMapping.put(174l, 451l);
            roleIdMapping.put(175l, 451l);
            roleIdMapping.put(204l, 451l);
            roleIdMapping.put(242l, 451l);
            roleIdMapping.put(253l, 451l);
            roleIdMapping.put(254l, 451l);
            roleIdMapping.put(255l, 451l);
            roleIdMapping.put(256l, 451l);

            if(siteIdMapping == null) {
                siteIdMapping = new HashMap<>();
            }
            siteIdMapping.put(294l, 12304l);

            if(stateFlowMapping == null) {
                stateFlowMapping = new HashMap<>();
            }
            stateFlowMapping.put(736l, 64644l);
            stateFlowMapping.put(740l, 66574l);
            stateFlowMapping.put(738l, 64646l);
            stateFlowMapping.put(742l, 64648l);
            stateFlowMapping.put(617l, 64514l);
            stateFlowMapping.put(747l, 64653l);
            stateFlowMapping.put(744l, 64650l);
            stateFlowMapping.put(715l, 64613l);
            stateFlowMapping.put(625l, 64522l);
            stateFlowMapping.put(20245l, 68388l);
            stateFlowMapping.put(20847l, 68389l);
            stateFlowMapping.put(20218l, 68390l);
            stateFlowMapping.put(15260l, 68391l);
            stateFlowMapping.put(1907l, 68392l);
            stateFlowMapping.put(709l, 64607l);
            stateFlowMapping.put(720l, 64618l);
            stateFlowMapping.put(5601l, 67844l);
            stateFlowMapping.put(5603l, 67846l);
            stateFlowMapping.put(5605l, 67848l);
            stateFlowMapping.put(5799l, 67850l);
            stateFlowMapping.put(6153l, 67852l);
            stateFlowMapping.put(6914l, 67854l);
            stateFlowMapping.put(6916l, 67856l);
            stateFlowMapping.put(9791l, 68384l);
            stateFlowMapping.put(5236l, 67858l);
            stateFlowMapping.put(901l, 68386l);
            stateFlowMapping.put(5234l, 67842l);

            if(slaMapping == null) {
                slaMapping = new HashMap<>();
            }
            slaMapping.put(2147l, 68495l);
            slaMapping.put(2149l, 68497l);
            slaMapping.put(13903l, 68499l);

            if(groupIdMapping == null) {
                groupIdMapping = new HashMap<>();
            }
            groupIdMapping.put(1l, 24l);
            groupIdMapping.put(2l, 25l);
            groupIdMapping.put(3l, 24l);
            groupIdMapping.put(4l, 24l);
            groupIdMapping.put(5l, 24l);
            groupIdMapping.put(14l, 24l);
            groupIdMapping.put(16l, 24l);
            groupIdMapping.put(18l, 24l);
            groupIdMapping.put(19l, 24l);
            groupIdMapping.put(20l, 24l);
            groupIdMapping.put(21l, 24l);
            groupIdMapping.put(22l, 24l);
            groupIdMapping.put(23l, 24l);
        }

        List<String> skipModuleNamesList = null;
        if (skipModuleNames != null && skipModuleNames.length() > 0) {
           skipModuleNamesList = Arrays.asList(skipModuleNames.split(","));
        }

        List<String> logModuleNamesList = null;
        if (logModuleNames != null && logModuleNames.length() > 0) {
            logModuleNamesList = Arrays.asList(logModuleNames.split(","));
        }

        List<String> runOnlyForModulesList = null;
        if (runOnlyForModules != null && runOnlyForModules.length() > 0) {
            runOnlyForModulesList = Arrays.asList(runOnlyForModules.split(","));
        }

        List<String> moduleSequenceList = null;
        if (moduleSequence!= null && moduleSequence.length() > 0) {
            moduleSequenceList = Arrays.asList(moduleSequence.split(","));
        }

        dataMigrationContext.put(DataMigrationConstants.IS_SITE_SCOPED, siteScoped);
        if (siteScoped) {
            dataMigrationContext.put(DataMigrationConstants.SITE_MAPPING, siteIdMapping);
        }
        dataMigrationContext.put(DataMigrationConstants.FORM_ID_MAPPING, formMapping);
        dataMigrationContext.put(DataMigrationConstants.STATEFLOW_ID_MAPPING, stateFlowMapping);
        dataMigrationContext.put(DataMigrationConstants.USER_ID_MAPPING, userIdMapping);
        dataMigrationContext.put(DataMigrationConstants.ROLE_ID_MAPPING, roleIdMapping);
        dataMigrationContext.put(DataMigrationConstants.GROUP_ID_MAPPING, groupIdMapping);
        dataMigrationContext.put(DataMigrationConstants.SLA_ID_MAPPING, slaMapping);

        dataMigrationContext.put(DataMigrationConstants.SOURCE_ORG_ID, sourceOrgId);
        dataMigrationContext.put(DataMigrationConstants.TARGET_ORG_ID, targetOrgId);
        dataMigrationContext.put(DataMigrationConstants.DATA_MIGRATION_ID, dataMigrationId);
        if(transactionTimeout > 0) {
            dataMigrationContext.put(DataMigrationConstants.TRANSACTION_TIME_OUT, transactionTimeout);
        }
        AccountUtil.setCurrentAccount(targetOrgId);
        dataMigrationContext.put(DataMigrationConstants.TRANSACTION_START_TIME, System.currentTimeMillis());
        dataMigrationContext.put(DataMigrationConstants.SKIP_MODULES_LIST, skipModuleNamesList);
        dataMigrationContext.put(DataMigrationConstants.LOG_MODULES_LIST, logModuleNamesList);
        dataMigrationContext.put(DataMigrationConstants.RUN_ONLY_FOR_MODULES, runOnlyForModulesList);
        dataMigrationContext.put(DataMigrationConstants.MODULE_SEQUENCE, moduleSequenceList);

        dataMigrationChain.execute();

        DataMigrationStatusContext migrationDetails = (DataMigrationStatusContext) dataMigrationContext.get(DataMigrationConstants.DATA_MIGRATION_CONTEXT);
        setResult("migrationDetails", FieldUtil.getAsProperties(migrationDetails));
        ServletActionContext.getResponse().setStatus(200);
        return SUCCESS;
    }
}
