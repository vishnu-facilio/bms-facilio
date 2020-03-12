package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

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


    public String fieldDependency() throws Exception {

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.FIELD_ID, toField);
        context.put(FacilioConstants.ContextNames.CATEGORY_ID, categoryId);
        FacilioChain chain = ReadOnlyChainFactory.getFieldUsageLog();
        chain.execute(context);
        setResult(FacilioConstants.ContextNames.RULES, context.getOrDefault(FacilioConstants.ContextNames.RULES, null));
        setResult(FacilioConstants.ContextNames.READINGS, context.getOrDefault(FacilioConstants.ContextNames.READINGS, null));

        return SUCCESS;
    }


    public String migrateField () throws  Exception {

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.SOURCE_ID, toField);
        context.put(FacilioConstants.ContextNames.TARGET_ID, fromField);
        FacilioChain chain = ReadOnlyChainFactory.migrateFieldDataChain();
        chain.execute(context);

        return SUCCESS;

    }


}
