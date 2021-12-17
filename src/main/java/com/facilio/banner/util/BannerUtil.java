package com.facilio.banner.util;

import com.facilio.banner.context.BannerContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class BannerUtil {

    private static final Logger LOGGER = Logger.getLogger(BannerUtil.class.getName());

    public static void addBanner(String uniqueID, long userId, String subject, long startDate, long endDate) throws Exception {
        addBanner(uniqueID, userId, subject, startDate, endDate, null, null);
    }

    public static void addBanner(String uniqueID, long userId, String subject, long startDate, long endDate, BannerContext.Type type, BannerContext.Priority priority) throws Exception {
        BannerContext banner = new BannerContext();
        banner.setUniqueId(uniqueID);
        banner.setUserId(userId);
        banner.setSubject(subject);
        banner.setStartDate(startDate);
        banner.setEndDate(endDate);
        banner.setType(type);
        banner.setPriority(priority);
        addBanner(banner);
    }

    public static void addBanner(BannerContext banner) {
        if (banner == null) {
            return;
        }

        try {
            if (StringUtils.isEmpty(banner.getUniqueId())) {
                throw new IllegalArgumentException("Unique Id cannot be empty");
            }
            if (banner.getUserId() < 0) {
                throw new IllegalArgumentException("User cannot be empty");
            }

            deleteBanner(banner.uniqueId, banner.getUserId());

            if (StringUtils.isEmpty(banner.getSubject())) {
                throw new IllegalArgumentException("Subject cannot be empty");
            }
            if (banner.getStartDate() <= 0 || banner.getEndDate() <= 0) {
                throw new IllegalArgumentException("Start date and end date are mandatory");
            }

            if (banner.getTypeEnum() == null) {
                banner.setType(BannerContext.Type.CANCEL);
            }
            if (banner.getPriorityEnum() == null) {
                banner.setPriority(BannerContext.Priority.LOW);
            }

            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getBannerModule().getTableName())
                    .fields(FieldFactory.getBannerFields());
            builder.insert(FieldUtil.getAsProperties(banner));
        } catch (Exception ex) {
            LOGGER.info("Error in adding banner", ex);
        }
    }

    public static void deleteBanner(String uniqueId, long userId) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getBannerModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("UNIQUE_ID", "uniqueId", uniqueId, StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition("USER_ID", "userId", String.valueOf(userId), NumberOperators.EQUALS));
        builder.delete();
    }

    public static void deleteBanner(String uniqueId, boolean throwError) throws Exception {
        try {
            GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getBannerModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("UNIQUE_ID", "uniqueId", uniqueId, StringOperators.IS));
            builder.delete();
        } catch (Exception ex) {
            LOGGER.error("Error in deleting Banner", ex);
            if (throwError) {
                throw ex;
            }
        }
    }

    private static BannerContext getBanner(String uniqueId, long userId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getBannerModule().getTableName())
                .select(FieldFactory.getBannerFields())
                .andCondition(CriteriaAPI.getCondition("UNIQUE_ID", "uniqueId", uniqueId, StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition("USER_ID", "userId", String.valueOf(userId), NumberOperators.EQUALS));
        return FieldUtil.getAsBeanFromMap(builder.fetchFirst(), BannerContext.class);
    }

    public static BannerContext getBanner(long bannerId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getBannerModule().getTableName())
                .select(FieldFactory.getBannerFields())
                .andCondition(CriteriaAPI.getIdCondition(bannerId, ModuleFactory.getBannerModule()));
        return FieldUtil.getAsBeanFromMap(builder.fetchFirst(), BannerContext.class);
    }
}
