package com.facilio.agentv2.upgrade;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.AgentUtilities;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.*;

public class AgentVersionApi {
    private static final Logger LOGGER = LogManager.getLogger(AgentVersionApi.class.getName());

    public static long addAgentVersion(String version, String description, String createdBy, String url) throws Exception {
        Map<String, Object> versionContext = new HashMap<>();
        versionContext.put(AgentConstants.VERSION, version);
        versionContext.put(AgentConstants.DESCRIPTION, description);
        versionContext.put(AgentConstants.CREATED_BY, createdBy);
        versionContext.put(AgentConstants.CREATED_TIME, System.currentTimeMillis());
        versionContext.put(AgentConstants.URL, url);
        GenericInsertRecordBuilder genericInsertRecordBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getAgentVersionModule().getTableName())
                .fields(FieldFactory.getAgentVersionFields());
        long versionId = genericInsertRecordBuilder.insert(versionContext);
        LOGGER.info("new version added "+versionId);
        return versionId ;
    }

    public static Map<String, Object> getAgentVersion(long versionId) throws Exception {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(ModuleFactory.getAgentVersionModule()), String.valueOf(versionId), NumberOperators.EQUALS));
       return fetchAgentVersion(criteria,new FacilioContext()).get(0);
    }

    public static List<Map<String,Object>> listAgentVersions(FacilioContext context) throws Exception {
        return fetchAgentVersion(null,context);
    }

    private static List<Map<String, Object>> fetchAgentVersion(Criteria criteria, FacilioContext context) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAgentVersionModule().getTableName())
                .select(FieldFactory.getAgentVersionFields());
        Boolean isLatest = (Boolean) context.get(AgentConstants.IS_LATEST_VERSION);
        
        if(criteria != null && !criteria.isEmpty()){
            selectRecordBuilder.andCriteria(criteria);
        }
        if(isLatest != null && isLatest) {
        	return selectRecordBuilder.orderBy("ID DESC").limit(1).get();
        }else if(context != null && !context.isEmpty()){
        	selectRecordBuilder.limit(AgentUtilities.getlimit(context));
            selectRecordBuilder.offset(AgentUtilities.getOffset(context));
        }
        return selectRecordBuilder.get();

    }

    public static boolean logAgentUpgrateRequest(FacilioAgent agent, long versionId, String authKey, long orgIg) throws Exception {
        Map<String, Object> versionLog = new HashMap<>();
        Objects.requireNonNull(agent, "Agent cant be null");
        versionLog.put(AgentConstants.AGENT_ID, agent.getId());
        versionLog.put(AgentConstants.VERSION_ID, versionId);
        versionLog.put(AgentConstants.CREATED_TIME, System.currentTimeMillis());
        versionLog.put(AgentConstants.AUTH_KEY, authKey);
        versionLog.put(AgentConstants.ORGID, orgIg);
        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getAgentVersionLogModule().getTableName())
                .fields(FieldFactory.getAgentVersionLogFields());
        return insertRecordBuilder.insert(versionLog) > 0;


    }

    public static String getAuthKey() {
        return RandomStringUtils.randomAlphanumeric(17) + System.currentTimeMillis();
    }

    /*public static String getJWT(long url, String randomKey) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Date date = new Date(System.currentTimeMillis());
        Date expiration = new Date(System.currentTimeMillis()+(60000*10));
        JwtBuilder jwtBuilder = Jwts.builder()
                .setIssuedAt(date)
                .setExpiration(expiration)
                .claim(AgentConstants.URL,url)
                .signWith(signatureAlgorithm,randomKey);
        return jwtBuilder.compact();
    }

    public static String getUrlFromJWT(String jwt,String authKey) throws Exception {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey("facilio")
                .parseClaimsJws(authKey);
        if (claimsJws.getBody().containsKey(AgentConstants.URL)) {
            return (String) claimsJws.getBody().get(AgentConstants.URL);
        }else {
            throw new Exception(" key url not present ");
        }
    }*/

    public static String getAgentDownloadUrl() {
        return FacilioProperties.getClientAppUrl() + "/api/agent/download/downloadAgent";
    }

    public static boolean markVersionLogUpdated(String token) throws SQLException {
        FacilioModule agentVersionLogModule = ModuleFactory.getAgentVersionLogModule();
        Map<String, Object> toUpdate = new HashMap<>();
        toUpdate.put(AgentConstants.UPDATED_TIME, System.currentTimeMillis());
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(agentVersionLogModule.getTableName())
                .fields(new ArrayList<>(Arrays.asList(FieldFactory.getUpdatedTimeField(agentVersionLogModule))))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAuthKeyField(agentVersionLogModule), token, StringOperators.IS));
        return updateRecordBuilder.update(toUpdate) > 0;

    }
}
