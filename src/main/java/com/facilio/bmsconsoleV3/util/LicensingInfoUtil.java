package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.bmsconsoleV3.interfaces.fetchlicensecount.Orglicensing;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import com.facilio.bmsconsoleV3.context.licensinginfo.LicenseInfoContext;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LicensingInfoUtil {
    private static final Logger LOGGER = LogManager.getLogger(LicensingInfoUtil.class.getName());
    private static final String DEFAULT_LICENSE_PATH = FacilioUtil.normalizePath("conf/licensing.yml");
    public static Long getCurrentCount(LicenseInfoContext licenseInfoObj, String type) throws Exception {
        Yaml yaml = new Yaml();
        Map<String, Object> json = null;
        try (InputStream inputStream = FacilioIntEnum.class.getClassLoader().getResourceAsStream(DEFAULT_LICENSE_PATH);) {
            json = yaml.load(inputStream);

        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }
        List<Map<String, Object>> licenseInfo = (List<Map<String, Object>>) json.get("licenseInfo");
        Orglicensing licenseCount;
        Long usedCount = 0L;
        for (Map<String, Object> license : licenseInfo) {
            String licenseName = (String) license.get("name");
            String licenseType = (String) license.get("type");
            if (licenseName.equalsIgnoreCase(licenseInfoObj.getName()) && licenseType.equalsIgnoreCase(type)) {
                try {
                    String className = (String) license.get("handler");
                    licenseCount = (Orglicensing) Class.forName(className).newInstance();
                    usedCount = licenseCount.fetchLicenseCount(FieldUtil.getAsProperties(licenseInfoObj));
                } catch (ClassNotFoundException e) {
                    LOGGER.info(e.getMessage());
                }
            }
        }
        return usedCount;
    }
    public static Boolean checkForLicense(String name, String type, Long newCount) throws Exception {
        GenericSelectRecordBuilder selectBuilder = getSelectBuilder(name,type);
        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            Long licenseId = (Long) props.get(0).get("id");
            Long allowedLicense = (Long) props.get(0).get("allowedLicense");
            Long usedLicense = (Long) props.get(0).get("usedLicense");
            Long totalLicenseCount = usedLicense + newCount;
            if (totalLicenseCount > allowedLicense) {
                LOGGER.info("Cannot perform operation as the count is greater than the permitted count");
                return false;
            } else {
                updateActualCount(licenseId, totalLicenseCount);
                return true;
            }
        } else {
            LOGGER.info("LicensingInfo Object is empty");
            return true;
        }
    }
    public static void updateActualCount(Long id, Long totalLicenseCount) throws SQLException {
        if (id > 0) {
            Map<String, Object> finalCount = new HashMap<>();
            finalCount.put("usedLicense", totalLicenseCount);
            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getLicensingInfoModule().getTableName())
                    .fields(AccountConstants.getLicensingInfoFields())
                    .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getLicensingInfoModule()));
            builder.update(finalCount);
        }
    }
    public static void updateActualCountOnDeletion(String name, String type) throws Exception {
        GenericSelectRecordBuilder selectBuilder = getSelectBuilder(name,type);
        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            Long totalLicenseCount = getCurrentCount((LicenseInfoContext) props.get(0), type);
            Long licenseId = (Long) props.get(0).get("id");
            updateActualCount(licenseId, totalLicenseCount);
        }
    }
    public static GenericSelectRecordBuilder getSelectBuilder(String name,String type){
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder();
        selectBuilder
                .table(ModuleFactory.getLicensingInfoModule().getTableName())
                .select(AccountConstants.getLicensingInfoFields())
                .andCondition(CriteriaAPI.getCondition("LICENSING_TYPE", "licensingType", type, StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition("NAME", "name", name, StringOperators.IS));
        return selectBuilder;
    }
}
