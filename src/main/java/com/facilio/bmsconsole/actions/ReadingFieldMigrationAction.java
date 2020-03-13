package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

import java.util.List;

public class ReadingFieldMigrationAction extends FacilioAction {

    private static final long serialVersionUID = 1L;

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    private long categoryId ;


    public long getFromField() {
        return fromField;
    }

    public void setFromField(long fromField) {
        this.fromField = fromField;
    }

    private long fromField;


    public long getToField() {
        return toField;
    }

    public void setToField(long toField) {
        this.toField = toField;
    }

    private long toField;

    public List<Long> getAssetsId() {
        return assetsId;
    }

    public void setAssetsId(List<Long> assetsId) {
        this.assetsId = assetsId;
    }

    List<Long> assetsId;


    public String fieldDependency() throws Exception {

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.FIELD_ID, toField);
        context.put(FacilioConstants.ContextNames.CATEGORY_ID, categoryId);
        context.put(FacilioConstants.ContextNames.RESOURCE_LIST, assetsId);
        FacilioChain chain = ReadOnlyChainFactory.getFieldUsageLog();
        chain.execute(context);
        setResult(FacilioConstants.ContextNames.RULES, context.getOrDefault(FacilioConstants.ContextNames.RULES, null));
        setResult(FacilioConstants.ContextNames.READINGS, context.getOrDefault(FacilioConstants.ContextNames.READINGS, null));
        setResult(FacilioConstants.ContextNames.RESOURCE_LIST, context.getOrDefault(FacilioConstants.ContextNames.RESOURCE_LIST, null));
        return SUCCESS;
    }


    public String migrateField () throws  Exception {

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.SOURCE_ID, toField);
        context.put(FacilioConstants.ContextNames.TARGET_ID, fromField);
        context.put(FacilioConstants.ContextNames.RESOURCE_LIST, assetsId);
        FacilioChain chain = TransactionChainFactory.addFieldMigrationJob();
        // FacilioChain chain = TransactionChainFactory.migrateFieldDataChain();
        chain.execute(context);

        return SUCCESS;

    }


}
