package com.facilio.v3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.MultiCurrencyField;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j
public class ConvertSortFieldsToColumnsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
        if (StringUtils.isEmpty(orderBy)) {
            return false;
        }

        try {
            JSONObject sorting = (JSONObject) context.get(FacilioConstants.ContextNames.SORTING);
            if (sorting != null && !sorting.isEmpty()) {
                String sortBy = (String) sorting.get("orderBy");

                String moduleName = Constants.getModuleName(context);
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioField sortField = modBean.getField(sortBy, moduleName);

                if (sortField != null) {
                    sortBy = (sortField instanceof MultiCurrencyField)
                            ? getCurrencySortColumn((MultiCurrencyField) sortField)
                            : getCompleteColumnName(sortField);
                    if(StringUtils.isNotEmpty(sortBy)) {
                        sorting.put("orderBy", sortBy);
                    }
                }

            }
        } catch(Exception e) {
            LOGGER.log(Level.ERROR, "Error while changing sort from field to column",e);
        }
        return false;
    }

    private String getCurrencySortColumn(MultiCurrencyField multiCurrencyField) throws Exception {
        String baseCurrencyColumnName = multiCurrencyField.getBaseCurrencyValueColumnName();
        if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY) || StringUtils.isEmpty(baseCurrencyColumnName)){
            return getCompleteColumnName(multiCurrencyField);
        }
        String tableName = multiCurrencyField.getTableName();
        String alias = multiCurrencyField.getTableAlias();

        if (StringUtils.isNotEmpty(alias)) {
            return alias + "." + baseCurrencyColumnName;
        } else if (StringUtils.isNotEmpty(tableName)) {
            return tableName + "." + baseCurrencyColumnName;
        } else {
            return baseCurrencyColumnName;
        }
    }
    private String getCompleteColumnName(FacilioField field) {
        return StringUtils.isNotEmpty(field.getColumnName()) ? field.getCompleteColumnName() : null;
    }

}
