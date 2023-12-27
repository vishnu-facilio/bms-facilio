package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.cache.CacheUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.cache.LRUCache;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Getter @Setter
public class MultiCurrencyFieldMigrationAction extends FacilioAction{
    public static Logger LOGGER = LogManager.getLogger(MultiCurrencyFieldMigrationAction.class.getName());

    private File file;
    public String migrateMultiCurrencyField() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();

        long orgId = Long.parseLong(request.getParameter("orgId"));
        if (orgId <= 0) {
            throw new IllegalArgumentException("orgId is required");
        }
        AccountUtil.setCurrentAccount(orgId);

        String moduleName = request.getParameter("moduleName");
        String fieldName = request.getParameter("fieldName");
        String baseCurrencyValueColumnName = request.getParameter("baseCurrencyValueColumnName");
        boolean migrateViaFile = Boolean.parseBoolean(request.getParameter("migrateViaFile"));
        boolean revert = Boolean.parseBoolean(request.getParameter("revert"));
        int transactionTimeOut = StringUtils.isNotEmpty(request.getParameter("transactionTimeOut")) ? Integer.parseInt(request.getParameter("transactionTimeOut")) : -1;
        if (transactionTimeOut < 0) {
            transactionTimeOut = 1800000; // by default 30 min
        }

        if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY)) {
            setResult("result", "Multi Currency License Not Enabled");
            AccountUtil.cleanCurrentAccount();
            return "Multi Currency License Not Enabled";
        }

        if (migrateViaFile) {
            List<DataMap> dataMap = readFile(file);
            if (CollectionUtils.isNotEmpty(dataMap)) {
                for (DataMap map : dataMap) {
                    try {
                        LOGGER.info(String.format("Started for ModuleName : %s FieldName : %s BaseCurrencyValueColumnName : %s", map.getModuleName(), map.getFieldName(), map.getBaseCurrencyValueColumnName()));
                        long start = System.currentTimeMillis();
                        runMigration(orgId, map.getModuleName(), map.getFieldName(), map.getBaseCurrencyValueColumnName(), transactionTimeOut, revert);
                        LOGGER.info(String.format("Ended for ModuleName : %s FieldName : %s BaseCurrencyValueColumnName : %s TimeTaken : %s", map.getModuleName(), map.getFieldName(), map.getBaseCurrencyValueColumnName(), (System.currentTimeMillis() - start)));
                    } catch (Exception e) {
                        LOGGER.info(String.format("*************Not Migrated************* ModuleName : %s FieldName : %s BaseCurrencyValueColumnName : %s", map.getModuleName(), map.getFieldName(), map.getBaseCurrencyValueColumnName()));
                        LOGGER.info(e.getMessage());
                    }
                }
            }
        } else {
            LOGGER.info(String.format("Stated for ModuleName : %s FieldName : %s BaseCurrencyValueColumnName : %s", moduleName, fieldName, baseCurrencyValueColumnName));
            long start = System.currentTimeMillis();
            runMigration(orgId, moduleName, fieldName, baseCurrencyValueColumnName, transactionTimeOut, revert);
            LOGGER.info(String.format("Ended for ModuleName : %s FieldName : %s BaseCurrencyValueColumnName : %s TimeTaken : %s", moduleName, fieldName, baseCurrencyValueColumnName, (System.currentTimeMillis() - start)));
        }

        setResult("result", "success");
        ServletActionContext.getResponse().setStatus(200);
        AccountUtil.cleanCurrentAccount();

        return SUCCESS;
    }

    private static void runMigration(long orgId, String moduleName, String fieldName, String baseCurrencyValueColumnName, int transactionTimeOut, boolean revert) throws Exception {
        if(StringUtils.isNotEmpty(moduleName) && StringUtils.isNotEmpty(fieldName) && StringUtils.isNotEmpty(baseCurrencyValueColumnName)){

            FacilioChain updateFieldChain = TransactionChainFactory.getMultiCurrencyFieldUpdateChain(transactionTimeOut);
            FacilioContext context = updateFieldChain.getContext();
            context.put(FacilioConstants.ContextNames.ORGID, orgId);
            context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
            context.put(FacilioConstants.ContextNames.MODULE_FIELD_NAME, fieldName);
            context.put("baseCurrencyValueColumnName", baseCurrencyValueColumnName);
            context.put("revert", revert);
            updateFieldChain.setContext(context);
            updateFieldChain.execute();

            LRUCache.getModuleCache().removeStartsWith(CacheUtil.ORG_KEY(orgId));
            LRUCache.getFieldsCache().removeStartsWith(CacheUtil.ORG_KEY(orgId));
        } else {
            throw new IllegalArgumentException("Props for Migration not defined");
        }
    }

    private List<DataMap> readFile(File file) {
        List<DataMap> dataMap = new ArrayList<>();
        String filePath = file.getAbsolutePath();

        try (FileInputStream fileInputStream = new FileInputStream(filePath);
            Workbook workbook = new XSSFWorkbook(fileInputStream)) {

            dataMap = processSpreadsheet(workbook);

            // printing mapped values
//            printMappedValues(dataMap);

        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return dataMap;
    }
    private static void printMappedValues(List<DataMap> dataMapList) {
        LOGGER.info("**************Printing Mapped Values**************");
        for (DataMap dataMap : dataMapList) {
            LOGGER.info("ModuleName : " + dataMap.getModuleName() + " FieldName : " + dataMap.getFieldName() + " BaseCurrencyValueColumnName : " + dataMap.getBaseCurrencyValueColumnName());
        }
        LOGGER.info("**************End Printing Mapped Values**************");
    }
    private static List<DataMap> processSpreadsheet(Workbook workbook) {
        List<DataMap> dataMapList = new ArrayList<>();

        for (Sheet sheet : workbook) {

            for (Row row : sheet) {
                Cell moduleNameCell = row.getCell(0);
                Cell fieldNameCell = row.getCell(1);
                Cell baseCurrencyValueColumnNameCell = row.getCell(2);

                if (moduleNameCell != null && fieldNameCell != null && baseCurrencyValueColumnNameCell != null) {
                    dataMapList.add(new DataMap(moduleNameCell.getStringCellValue(), fieldNameCell.getStringCellValue(), baseCurrencyValueColumnNameCell.getStringCellValue()));
                }
            }
        }

        return dataMapList;
    }
    @Getter @Setter
    private static class DataMap {
        private final String moduleName;
        private final String fieldName;
        private final String baseCurrencyValueColumnName;
        public DataMap(String moduleName, String fieldName, String baseCurrencyValueColumnName) {
            this.moduleName = moduleName;
            this.fieldName = fieldName;
            this.baseCurrencyValueColumnName = baseCurrencyValueColumnName;
        }
    }
}
