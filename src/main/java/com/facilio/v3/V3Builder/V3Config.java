package com.facilio.v3.V3Builder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.interfaces.customfields.ModuleCustomFieldsCount;
import com.facilio.chain.FacilioChain;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class V3Config implements V3Builder {
    private String moduleName;
    private Class beanClass;
    private CreateHandler createHandler;
    private UpdateHandler updateHandler;
    private DeleteHandler deleteHandler;
    private ListHandler listHandler;
    private SummaryHandler summaryHandler;
    private ModuleCustomFieldsCount customFieldsCount;
    private boolean dontUseInScript = false;

    public V3Config dontUseInScript() {
        this.dontUseInScript = true;
        return this;
    }

    public boolean canUseInScript() {
        return !dontUseInScript;
    }

    public SummaryHandler getSummaryHandler() {
        return summaryHandler;
    }

    public void setSummaryHandler(SummaryHandler summaryHandler) {
        this.summaryHandler = summaryHandler;
    }

    public ListHandler getListHandler() {
        return listHandler;
    }

    public void setListHandler(ListHandler listHandler) {
        this.listHandler = listHandler;
    }

    public DeleteHandler getDeleteHandler() {
        return deleteHandler;
    }

    public void setDeleteHandler(DeleteHandler deleteHandler) {
        this.deleteHandler = deleteHandler;
    }

    public UpdateHandler getUpdateHandler() {
        return updateHandler;
    }

    public CreateHandler getCreateHandler() {
        return createHandler;
    }

    public void setCreateHandler(CreateHandler createHandler) {
        this.createHandler = createHandler;
    }

    public V3Config(Class bean, ModuleCustomFieldsCount customFieldsCount) {
        this (bean, customFieldsCount, null);
    }

    public V3Config(Class bean, ModuleCustomFieldsCount customFieldsCount, V3Config config) {
        this.beanClass = bean;
        this.customFieldsCount = customFieldsCount;
        if (config != null) {
            this.createHandler = config.createHandler;
            this.updateHandler = config.updateHandler;
            this.deleteHandler = config.deleteHandler;
            this.listHandler = config.listHandler;
            this.summaryHandler = config.summaryHandler;
        }
    }

    public UpdateHandler update() {
        if (this.updateHandler == null) {
            this.updateHandler = new UpdateHandler(this);
        }
        return this.updateHandler;
    }

    public CreateHandler create() {
        if (this.createHandler == null) {
            this.createHandler = new CreateHandler(this);
        }
        return this.createHandler;
    }

    public DeleteHandler delete() {
        if (this.deleteHandler == null) {
            this.deleteHandler = new DeleteHandler(this);
        }
        return this.deleteHandler;
    }

    public SummaryHandler summary() {
        if (this.summaryHandler == null) {
            this.summaryHandler = new SummaryHandler(this);
        }
        return this.summaryHandler;
    }

    public ListHandler list() {
        if (this.listHandler == null) {
            this.listHandler = new ListHandler(this);
        }
        return this.listHandler;
    }

    public V3Config build() {
        return this;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public ModuleCustomFieldsCount getCustomFieldsCount() {
        return customFieldsCount;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    public class CreateHandler implements CreateBuilder {
        private Command initCommand;
        private Command beforeSaveCommand;
        private Command afterSaveCommand;
        private Command afterTransactionCommand;
        private Command activitySaveCommand;
        private V3Builder parent;

        private CreateHandler(V3Builder parent) {
            this.parent = parent;
        }

        @Override
        public CreateHandler init(Command... initCommand) {
            this.initCommand = buildTransactionChain(initCommand);
            return this;
        }

        @Override
        public CreateHandler beforeSave(Command... beforeSaveCommand) {
            this.beforeSaveCommand = buildTransactionChain(beforeSaveCommand);
            return this;
        }

        @Override
        public CreateHandler afterSave(Command... afterSaveCommand) {
            this.afterSaveCommand = buildTransactionChain(afterSaveCommand);
            return this;
        }

        @Override
        public CreateBuilder afterTransaction(Command... afterTransactionCommand) {
            this.afterTransactionCommand = buildTransactionChain(afterTransactionCommand);
            return this;
        }

        @Override
        public CreateBuilder activitySaveCommand(Command... activitySaveCommand) {
            this.activitySaveCommand = buildTransactionChain(activitySaveCommand);
            return this;
        }

        @Override
        public UpdateBuilder update() {
            return this.parent.update();
        }

        @Override
        public DeleteBuilder delete() {
            return this.parent.delete();
        }

        @Override
        public ListBuilder list() {
            return this.parent.list();
        }

        @Override
        public SummaryBuilder summary() {
            return this.parent.summary();
        }

        @Override
        public V3Config build() {
            return this.parent.build();
        }

        public Command getInitCommand() {
            return initCommand;
        }

        public void setInitCommand(Command initCommand) {
            this.initCommand = initCommand;
        }

        public Command getBeforeSaveCommand() {
            return beforeSaveCommand;
        }

        public void setBeforeSaveCommand(Command beforeSaveCommand) {
            this.beforeSaveCommand = beforeSaveCommand;
        }

        public Command getAfterSaveCommand() {
            return afterSaveCommand;
        }

        public void setAfterSaveCommand(Command afterSaveCommand) {
            this.afterSaveCommand = afterSaveCommand;
        }

        public Command getAfterTransactionCommand() {
            return afterTransactionCommand;
        }

        public void setAfterTransactionCommand(Command afterTransactionCommand) {
            this.afterTransactionCommand = afterTransactionCommand;
        }

        public Command getActivitySaveCommand() {
            return activitySaveCommand;
        }
    }

    public class UpdateHandler implements UpdateBuilder {
        private Command initCommand;
        private Command beforeSaveCommand;
        private Command afterSaveCommand;
        private Command afterTransactionCommand;
        private Command activitySaveCommand;

        private V3Builder parent;

        private UpdateHandler(V3Builder parent) {
            this.parent = parent;
        }

        @Override
        public UpdateHandler init(Command... initCommand) {
            this.initCommand = buildTransactionChain(initCommand);
            return this;
        }

        @Override
        public UpdateHandler beforeSave(Command... beforeSaveCommand) {
            this.beforeSaveCommand = buildTransactionChain(beforeSaveCommand);
            return this;
        }

        @Override
        public UpdateHandler afterSave(Command... afterSaveCommand) {
            this.afterSaveCommand = buildTransactionChain(afterSaveCommand);
            return this;
        }

        @Override
        public UpdateBuilder afterTransaction(Command... afterTransactionCommand) {
            this.afterTransactionCommand = buildTransactionChain(afterTransactionCommand);
            return this;
        }

        @Override
        public UpdateBuilder activitySaveCommand(Command... activitySaveCommand) {
            this.activitySaveCommand = buildTransactionChain(activitySaveCommand);
            return this;
        }

        @Override
        public CreateBuilder create() {
            return this.parent.create();
        }

        @Override
        public DeleteBuilder delete() {
            return this.parent.delete();
        }

        @Override
        public ListBuilder list() {
            return this.parent.list();
        }

        @Override
        public SummaryBuilder summary() {
            return this.parent.summary();
        }

        @Override
        public V3Config build() {
            return this.parent.build();
        }


        public Command getInitCommand() {
            return initCommand;
        }

        public void setInitCommand(Command initCommand) {
            this.initCommand = initCommand;
        }

        public Command getBeforeSaveCommand() {
            return beforeSaveCommand;
        }

        public void setBeforeSaveCommand(Command beforeSaveCommand) {
            this.beforeSaveCommand = beforeSaveCommand;
        }

        public Command getAfterSaveCommand() {
            return afterSaveCommand;
        }

        public void setAfterSaveCommand(Command afterSaveCommand) {
            this.afterSaveCommand = afterSaveCommand;
        }

        public Command getAfterTransactionCommand() {
            return afterTransactionCommand;
        }

        public void setAfterTransactionCommand(Command afterTransactionCommand) {
            this.afterTransactionCommand = afterTransactionCommand;
        }

        public Command getActivitySaveCommand() {
            return activitySaveCommand;
        }
        public void setActivitySaveCommand(Command activitySaveCommand) {
            this.activitySaveCommand = activitySaveCommand;
        }
    }

    public class DeleteHandler implements DeleteBuilder {
        private Command initCommand;
        private Command beforeDeleteCommand;
        private Command afterDeleteCommand;
        private Command afterTransactionCommand;

        private V3Builder parent;

        private DeleteHandler(V3Builder parent) {
            this.parent = parent;
        }

        @Override
        public DeleteHandler init(Command... initCommand) {
            this.initCommand = buildTransactionChain(initCommand);
            return this;
        }

        @Override
        public DeleteHandler beforeDelete(Command... beforeDeleteCommand) {
            this.beforeDeleteCommand = buildTransactionChain(beforeDeleteCommand);
            return this;
        }

        @Override
        public DeleteHandler afterDelete(Command... afterDeleteCommand) {
            this.afterDeleteCommand = buildTransactionChain(afterDeleteCommand);
            return this;
        }

        @Override
        public DeleteBuilder afterTransaction(Command... afterTransactionCommand) {
            this.afterTransactionCommand = buildTransactionChain(afterTransactionCommand);
            return this;
        }

        @Override
        public UpdateBuilder update() {
            return this.parent.update();
        }

        @Override
        public CreateBuilder create() {
            return this.parent.create();
        }

        @Override
        public ListBuilder list() {
            return this.parent.list();
        }

        @Override
        public SummaryBuilder summary() {
            return this.parent.summary();
        }

        @Override
        public V3Config build() {
            return this.parent.build();
        }

        public Command getInitCommand() {
            return initCommand;
        }

        public void setInitCommand(Command initCommand) {
            this.initCommand = initCommand;
        }

        public Command getBeforeDeleteCommand() {
            return beforeDeleteCommand;
        }

        public void setBeforeDeleteCommand(Command beforeDeleteCommand) {
            this.beforeDeleteCommand = beforeDeleteCommand;
        }

        public Command getAfterDeleteCommand() {
            return afterDeleteCommand;
        }

        public void setAfterDeleteCommand(Command afterDeleteCommand) {
            this.afterDeleteCommand = afterDeleteCommand;
        }

        public Command getAfterTransactionCommand() {
            return afterTransactionCommand;
        }

        public void setAfterTransactionCommand(Command afterTransactionCommand) {
            this.afterTransactionCommand = afterTransactionCommand;
        }
    }



    public class ListHandler implements ListBuilder {
        private Command beforeFetchCommand;
        private Command afterFetchCommand;
        private Command beforeCountCommand;
        private List<SupplementRecord> fetchSupplements;
        private V3Builder parent;
        private boolean showStateFlowList;
        private Map<String, List<String>> lookupFieldCriteriaMap;

        private ListHandler(V3Builder parent) {
            this.parent = parent;
            this.lookupFieldCriteriaMap = new HashMap<>();
        }

        @Override
        public ListBuilder beforeFetch(Command criteriaCommand) {
            this.beforeFetchCommand = criteriaCommand;
            return this;
        }

        private void fetchSupplement(SupplementRecord supplementRecord) {
            if (fetchSupplements == null) {
                fetchSupplements = new ArrayList<>();
            }
            fetchSupplements.add(supplementRecord);
        }

        @Override
        public ListBuilder fetchSupplement(String moduleName, String fieldName) {
            try {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioField field = modBean.getField(fieldName, moduleName);
                if (!(field instanceof SupplementRecord)) {
                    throw new IllegalArgumentException("Not an supplement field");
                }
                fetchSupplement((SupplementRecord) field);
            } catch (Exception ex) {}
            return this;
        }

        @Override
        public ListHandler afterFetch(Command afterFetchCommand) {
            this.afterFetchCommand = afterFetchCommand;
            return this;
        }

        @Override
        public ListBuilder showStateFlowList() {
            this.showStateFlowList = true;
            return this;
        }

        @Override
        public ListBuilder fetchRelations(String moduleName, String lookupField) {
            if (this.lookupFieldCriteriaMap.get(moduleName) == null) {
                this.lookupFieldCriteriaMap.put(moduleName, new ArrayList<>());
            }
            this.lookupFieldCriteriaMap.get(moduleName).add(lookupField);
            return this;
        }

        @Override
        public ListBuilder beforeCount(Command beforeCountCommand) {
            this.beforeCountCommand = beforeCountCommand;
            return this;
        }

        @Override
        public UpdateBuilder update() {
            return this.parent.update();
        }

        @Override
        public CreateBuilder create() {
            return this.parent.create();
        }

        @Override
        public DeleteBuilder delete() {
            return this.parent.delete();
        }

        @Override
        public SummaryBuilder summary() {
            return this.parent.summary();
        }

        @Override
        public V3Config build() {
            return this.parent.build();
        }

        public Command getBeforeFetchCommand() {
            return beforeFetchCommand;
        }

        public void setBeforeFetchCommand(Command beforeFetchCommand) {
            this.beforeFetchCommand = beforeFetchCommand;
        }

        public Command getAfterFetchCommand() {
            return afterFetchCommand;
        }

        public List<SupplementRecord> getSupplementFields() {
            return fetchSupplements;
        }

        public void setAfterFetchCommand(Command afterFetchCommand) {
            this.afterFetchCommand = afterFetchCommand;
        }

        public boolean isShowStateFlowList() {
            return showStateFlowList;
        }

        public void setShowStateFlowList(boolean showStateFlowList) {
            this.showStateFlowList = showStateFlowList;
        }

        public Map<String, List<String>> getLookupFieldCriteriaMap() {
            return this.lookupFieldCriteriaMap;
        }

        public Command getBeforeCountCommand() {
            return beforeCountCommand;
        }
    }

    public class SummaryHandler implements SummaryBuilder {
        private Command beforeFetchCommand;
        private Command afterFetchCommand;
        private V3Builder parent;
        private List<SupplementRecord> fetchSupplements;

        private SummaryHandler(V3Builder parent) {
            this.parent = parent;
        }

        public SummaryHandler beforeFetch(Command beforeFetchCommand) {
            this.beforeFetchCommand = beforeFetchCommand;
            return this;
        }

        @Override
        public SummaryHandler afterFetch(Command afterFetchCommand) {
            this.afterFetchCommand = afterFetchCommand;
            return this;
        }

        private void fetchSupplement(SupplementRecord supplementRecord) {
            if (fetchSupplements == null) {
                fetchSupplements = new ArrayList<>();
            }
            fetchSupplements.add(supplementRecord);
        }

        @Override
        public SummaryHandler fetchSupplement(String moduleName, String fieldName) {
            try {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioField field = modBean.getField(fieldName, moduleName);
                if (!(field instanceof SupplementRecord)) {
                    throw new IllegalArgumentException("Not an supplement field");
                }
                fetchSupplement((SupplementRecord) field);
            } catch (Exception ex) {}
            return this;
        }

        @Override
        public UpdateBuilder update() {
            return this.parent.update();
        }

        @Override
        public CreateBuilder create() {
            return this.parent.create();
        }

        public List<SupplementRecord> getSupplementFields() {
            return fetchSupplements;
        }

        @Override
        public DeleteBuilder delete() {
            return this.parent.delete();
        }

        @Override
        public ListBuilder list() {
            return this.parent.list();
        }

        @Override
        public V3Config build() {
            return this.parent.build();
        }

        public Command getBeforeFetchCommand() {
            return beforeFetchCommand;
        }

        public Command getAfterFetchCommand() {
            return afterFetchCommand;
        }
    }

    private static FacilioChain buildTransactionChain(Command[] facilioCommands) {
        FacilioChain c = FacilioChain.getTransactionChain();
        for (Command facilioCommand: facilioCommands) {
            c.addCommand(facilioCommand);
        }
        return c;
    }

    private static FacilioChain buildReadChain(Command[] facilioCommands) {
        FacilioChain c = FacilioChain.getNonTransactionChain();
        for (Command facilioCommand: facilioCommands) {
            c.addCommand(facilioCommand);
        }
        return c;
    }

}
