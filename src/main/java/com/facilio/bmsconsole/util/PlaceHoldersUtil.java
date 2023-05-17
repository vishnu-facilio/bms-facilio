package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PlaceHoldersUtil {
    private static class PlaceHoldersBeanPair {
        private Object bean;
        private Map<String, Object> placeHolders;

        PlaceHoldersBeanPair (Object bean, Map<String, Object> placeHolders) {
            this.bean = bean;
            this.placeHolders = placeHolders;
        }
    }

    private static final Logger LOGGER = LogManager.getLogger(PlaceHoldersUtil.class.getName());

    private static ThreadLocal<Map<String, PlaceHoldersBeanPair>> fieldPlaceHoldersCache = new ThreadLocal<>();

    private static final String FIELD_PLACEHOLDERS_CACHE_KEY_SEPARATOR = "#";
    private static String constructCacheKey (String moduleName, long id) {
        return new StringBuilder(moduleName).append(FIELD_PLACEHOLDERS_CACHE_KEY_SEPARATOR).append(id).toString();
    }
    private static void addToFieldPlaceHoldersCache(String moduleName, long id, PlaceHoldersBeanPair pair) {
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(moduleName), "Invalid module name while adding place holders to local cache");
        Map<String, PlaceHoldersBeanPair> cache = fieldPlaceHoldersCache.get();
        if (cache == null) {
            cache = new HashMap<>();
            fieldPlaceHoldersCache.set(cache);
        }
        cache.put(constructCacheKey(moduleName, id), pair);
    }
    private static PlaceHoldersBeanPair getFromFieldPlaceHoldersCache(String moduleName, long id) {
        Map<String, PlaceHoldersBeanPair> cache = fieldPlaceHoldersCache.get();
        return cache == null ? null : cache.get(constructCacheKey(moduleName, id));
    }

    private static Map<String, Object> constructFieldPlaceHolders(String moduleName, String prefix, Map<String, Object> beanMap, int level) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        Map<String, Object> placeHolders = null;
        if(fields != null && !fields.isEmpty()) {
            placeHolders = new HashMap<>();
            for(FacilioField field : fields) {
                if(field.getDataTypeEnum() == FieldType.LOOKUP && !FieldUtil.isGeoLocationField(field)) {
                    Map<String, Object> props = (Map<String, Object>) beanMap.remove(field.getName());
                    if(MapUtils.isNotEmpty(props)) {
                        Long lookupId = (Long) props.get("id");
                        if (lookupId != null) {
                            Object lookupVal = null;
                            Map<String, Object> fieldPlaceHolders = null;
                            LookupField lookupField = (LookupField) field;
                            String childModuleName = lookupField.getLookupModule() == null ? lookupField.getSpecialType() : lookupField.getLookupModule().getName();
                            PlaceHoldersBeanPair pair = getFromFieldPlaceHoldersCache(childModuleName, lookupId);

                            if (pair == null) {
                                lookupVal = FieldUtil.getLookupVal(lookupField, lookupId);
                                props = FieldUtil.getAsProperties(lookupVal);
                                fieldPlaceHolders = constructPlaceholders(childModuleName, null, props, level + 1);
                                if (fieldPlaceHolders == null) {
                                    fieldPlaceHolders = props;
                                }
                                else {
                                    addToFieldPlaceHoldersCache(childModuleName, lookupId, new PlaceHoldersBeanPair(lookupVal, fieldPlaceHolders));
                                    LOGGER.debug(MessageFormat.format("Adding to cache for {0}", constructCacheKey(childModuleName, lookupId)));
                                }
                            }
                            else {
                                LOGGER.debug(MessageFormat.format("Getting from cache for {0}", constructCacheKey(childModuleName, lookupId)));
                                lookupVal = pair.bean;
                                fieldPlaceHolders = pair.placeHolders;
                            }

                            if (lookupVal != null) {
                                String childPrefix = constructKeyWithPrefix(prefix,field.getName());
                                placeHolders.put(childPrefix, lookupVal);
                                if (MapUtils.isNotEmpty(fieldPlaceHolders)) {
                                    addPropsWithPrefix(childPrefix, fieldPlaceHolders, placeHolders);
                                }
                            }
                        }
                    }
                }
                else {
                    placeHolders.put(constructKeyWithPrefix(prefix,field.getName()), beanMap.remove(field.getName()));
                }
            }
        }
        return placeHolders;
    }

    private static void addPropsWithPrefix(String prefix, Map<String, Object> beanMap, Map<String, Object> placeHolders) {
        for(Map.Entry<String, Object> entry : beanMap.entrySet()) {
            if(entry.getValue() instanceof Map<?, ?>) {
                addPropsWithPrefix(constructKeyWithPrefix(prefix, entry.getKey()), (Map<String, Object>) entry.getValue(), placeHolders);
            }
            else {
                placeHolders.put(constructKeyWithPrefix(prefix, entry.getKey()), entry.getValue());
            }
        }
    }

    private static String constructKeyWithPrefix (String prefix, String key) {
        StringBuilder prefixKey = new StringBuilder();
        if (prefix != null) {
            prefixKey.append(prefix).append('.');
        }
        prefixKey.append(key);
        return prefixKey.toString();
    }


    private static final int MAX_LEVEL_FOR_PLACEHOLDERS = 2;
    public static Map<String, Object> constructPlaceholders(String moduleName, String prefix, Map<String, Object> beanMap, int level) throws Exception {
        if (FacilioProperties.isProduction()
                && AccountUtil.getCurrentUser() != null && AccountUtil.getCurrentUser().getId() == 1339162) {
            LOGGER.info("Skipping the method call for the user: " + 1339162);
            return null;
        }
        if (level >= MAX_LEVEL_FOR_PLACEHOLDERS) {
            return null;
        }
        long time = System.currentTimeMillis();
        int selectCount = AccountUtil.getCurrentSelectQuery(), pSelectCount = AccountUtil.getCurrentPublicSelectQuery();
        Map<String, Object> placeHolders = null;
        if(beanMap != null) {
            Map<String, Object> clonedBeanMap = new HashMap<>(beanMap); 
            if (StringUtils.isNotEmpty(moduleName) && !LookupSpecialTypeUtil.isSpecialType(moduleName)) {
                placeHolders = constructFieldPlaceHolders(moduleName, prefix, clonedBeanMap, level);
            }
            placeHolders = placeHolders == null ? new HashMap<>() : placeHolders;
            addPropsWithPrefix(prefix, clonedBeanMap, placeHolders);
        }
        long timeTaken = System.currentTimeMillis() - time;
        int totalSelect = AccountUtil.getCurrentSelectQuery() - selectCount;
        int totalPublicSelect = AccountUtil.getCurrentPublicSelectQuery() - pSelectCount;
        if (level == 0 && (timeTaken > 50 || totalSelect > 10 || totalPublicSelect > 10)) {
            String msg = MessageFormat.format("### time taken in first level of appendModuleNameInKey with ModuleName ({0}) is {1}, select : {2}, pSelect : {3}", String.valueOf(moduleName), timeTaken, totalSelect, totalPublicSelect);
            if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 274) {
                LOGGER.info(msg);
            }
            else {
                LOGGER.debug(msg);
            }
        }
        return placeHolders;
    }
}
