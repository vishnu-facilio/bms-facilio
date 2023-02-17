package com.facilio.datamigration.action;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.datamigration.commands.DataMigrationChainFactory;
import com.facilio.datamigration.util.DataMigrationConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
    private Map<Long, Long> siteIdMapping = new HashMap<>();
    private Map<Long, Long> userIdMapping = new HashMap<>();
    private Map<Long, Long> groupIdMapping = new HashMap<>();
    private Map<Long, Long> roleIdMapping = new HashMap<>();
    private Map<Long,Long> stateFlowMapping = new HashMap<>();
    private Map<Long,Long> slaMapping = new HashMap<>();
    private Map<Long,Long> formMapping = new HashMap<>();
    private List<String> fromSiteIds, toSiteIds, fromUserIds, toUserIds, fromGroupIds, toGroupIds, fromRoleIds, toRoleIds;
    private List<String> fromStateFlowIds, toStateFlowIds, fromSlaIds, toSlaIds, fromFormIds, toFormIds;

    public String execute() throws Exception{
            constructRequestData();

        try {
            FacilioChain dataMigrationChain = DataMigrationChainFactory.getDataMigrationChain();
            FacilioContext dataMigrationContext = dataMigrationChain.getContext();

            if(sourceOrgId == 10l && targetOrgId == 615l) {
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

                userIdMapping.put(5150l, 12318l);
                userIdMapping.put(2895l, 12319l);
                userIdMapping.put(2885l, 12320l);
                userIdMapping.put(11950l, 12321l);
                userIdMapping.put(5156l, 12322l);
                userIdMapping.put(2877l, 12323l);
                userIdMapping.put(2893l, 12324l);
                userIdMapping.put(2878l, 12326l);
                userIdMapping.put(2897l, 12330l);
                userIdMapping.put(2856l, 12339l);
                userIdMapping.put(2898l, 12346l);
                userIdMapping.put(2851l, 12349l);
                userIdMapping.put(2890l, 12350l);
                userIdMapping.put(2846l, 12351l);
                userIdMapping.put(2899l, 12362l);
                userIdMapping.put(2892l, 12366l);
                userIdMapping.put(925l, 12367l);
                userIdMapping.put(2876l, 12365l);
                userIdMapping.put(9533l, 12364l);
                userIdMapping.put(9532l, 12361l);
                userIdMapping.put(2875l, 12360l);
                userIdMapping.put(2879l, 12359l);
                userIdMapping.put(2882l, 12358l);
                userIdMapping.put(2844l, 12357l);
                userIdMapping.put(9478l, 12356l);
                userIdMapping.put(2900l, 12355l);
                userIdMapping.put(2903l, 12354l);
                userIdMapping.put(2870l, 12353l);
                userIdMapping.put(2871l, 12352l);
                userIdMapping.put(2881l, 12348l);
                userIdMapping.put(2894l, 12347l);
                userIdMapping.put(2848l, 12345l);
                userIdMapping.put(10381l, 12344l);
                userIdMapping.put(2880l, 12343l);
                userIdMapping.put(2874l, 12342l);
                userIdMapping.put(2901l, 12341l);
                userIdMapping.put(2889l, 12340l);
                userIdMapping.put(2902l, 12338l);
                userIdMapping.put(2888l, 12337l);
                userIdMapping.put(2873l, 12336l);
                userIdMapping.put(2896l, 12335l);
                userIdMapping.put(9275l, 12334l);
                userIdMapping.put(12333l, 12333l);
                userIdMapping.put(921l, 12332l);
                userIdMapping.put(2849l, 12331l);
                userIdMapping.put(2847l, 12329l);
                userIdMapping.put(2859l, 12328l);
                userIdMapping.put(2860l, 12327l);
                userIdMapping.put(922l, 12325l);

                siteIdMapping.put(294l, 12304l);

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

                slaMapping.put(2147l, 68495l);
                slaMapping.put(2149l, 68497l);
                slaMapping.put(13903l, 68499l);

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

            dataMigrationChain.execute();
        }catch(Exception e) {
            LOGGER.error("Error from Data MigrationAction : ", e);
            throw e;
        }

        return SUCCESS;
    }

    private void constructRequestData() {
        if (CollectionUtils.isNotEmpty(fromSiteIds) && CollectionUtils.isNotEmpty(toSiteIds)) {
            for (int i=0; i < fromSiteIds.size() ; i++) {
                if (StringUtils.isNotEmpty(fromSiteIds.get(i)) && StringUtils.isNotEmpty(toSiteIds.get(i))) {
                    siteIdMapping.put(Long.parseLong(fromSiteIds.get(i)), Long.parseLong(toSiteIds.get(i)));
                }
            }
        }

        if (CollectionUtils.isNotEmpty(fromUserIds) && CollectionUtils.isNotEmpty(toUserIds)) {
            for (int i=0; i < fromUserIds.size() ; i++) {
                if (StringUtils.isNotEmpty(fromUserIds.get(i)) && StringUtils.isNotEmpty(toUserIds.get(i))) {
                    userIdMapping.put(Long.parseLong(fromUserIds.get(i)), Long.parseLong(toUserIds.get(i)));
                }
            }
        }

        if (CollectionUtils.isNotEmpty(fromGroupIds) && CollectionUtils.isNotEmpty(toGroupIds)) {
            for (int i=0; i < fromGroupIds.size() ; i++) {
                if (StringUtils.isNotEmpty(fromGroupIds.get(i)) && StringUtils.isNotEmpty(toGroupIds.get(i))) {
                    groupIdMapping.put(Long.parseLong(fromGroupIds.get(i)), Long.parseLong(toGroupIds.get(i)));
                }
            }
        }

        if (CollectionUtils.isNotEmpty(fromRoleIds) && CollectionUtils.isNotEmpty(toRoleIds)) {
            for (int i=0; i < fromRoleIds.size() ; i++) {
                if (StringUtils.isNotEmpty(fromRoleIds.get(i)) && StringUtils.isNotEmpty(toRoleIds.get(i))) {
                    roleIdMapping.put(Long.parseLong(fromRoleIds.get(i)), Long.parseLong(toRoleIds.get(i)));
                }
            }
        }

        if (CollectionUtils.isNotEmpty(fromStateFlowIds) && CollectionUtils.isNotEmpty(toStateFlowIds)) {
            for (int i=0; i < fromStateFlowIds.size() ; i++) {
                if (StringUtils.isNotEmpty(fromStateFlowIds.get(i)) && StringUtils.isNotEmpty(toStateFlowIds.get(i))) {
                    stateFlowMapping.put(Long.parseLong(fromStateFlowIds.get(i)), Long.parseLong(toStateFlowIds.get(i)));
                }
            }
        }

        if (CollectionUtils.isNotEmpty(fromSlaIds) && CollectionUtils.isNotEmpty(toSlaIds)) {
            for (int i=0; i < fromSlaIds.size() ; i++) {
                if (StringUtils.isNotEmpty(fromSlaIds.get(i)) && StringUtils.isNotEmpty(toSlaIds.get(i))) {
                    slaMapping.put(Long.parseLong(fromSlaIds.get(i)), Long.parseLong(toSlaIds.get(i)));
                }
            }
        }

        if (CollectionUtils.isNotEmpty(fromFormIds) && CollectionUtils.isNotEmpty(toFormIds)) {
            for (int i=0; i < fromFormIds.size() ; i++) {
                if (StringUtils.isNotEmpty(fromFormIds.get(i)) && StringUtils.isNotEmpty(toFormIds.get(i))) {
                    formMapping.put(Long.parseLong(fromFormIds.get(i)), Long.parseLong(toFormIds.get(i)));
                }
            }
        }
    }
}
