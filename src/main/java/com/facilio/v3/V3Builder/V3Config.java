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
    private PreCreateHandler preCreateHandler;
    private PostCreateHandler postCreateHandler;

    private PickListHandler pickListHandler;
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

    public PreCreateHandler getPreCreateHandler(){
        return preCreateHandler;
    }
    public void setPreCreateHandler(PreCreateHandler preCreateHandler){
        this.preCreateHandler=preCreateHandler;
    }

    public PostCreateHandler getPostCreateHandler(){
        return postCreateHandler;
    }
    public void setPostCreateHandler(PostCreateHandler postCreateHandler){
        this.postCreateHandler=postCreateHandler;
    }

    public PickListHandler getPickListHandler(){
        return pickListHandler;
    }

    public void setPickListHandler(PickListHandler pickListHandler) {
        this.pickListHandler = pickListHandler;
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
            this.preCreateHandler=config.preCreateHandler;
            this.postCreateHandler= config.postCreateHandler;
            this.pickListHandler= config.pickListHandler;
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

    public PreCreateHandler preCreate() {
        if (this.preCreateHandler == null) {
            this.preCreateHandler = new PreCreateHandler(this);
        }
        return this.preCreateHandler;
    }

    public PostCreateHandler postCreate() {
        if (this.postCreateHandler == null) {
            this.postCreateHandler = new PostCreateHandler(this);
        }
        return this.postCreateHandler;
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

    public PickListHandler pickList(){
        if(this.pickListHandler == null){
            this.pickListHandler = new PickListHandler(this);
        }
        return this.pickListHandler;
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

    public class  CreateHandler implements CreateBuilder {
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

        public PreCreateBuilder preCreate() {
            return this.parent.preCreate();
        }

        public PostCreateBuilder postCreate(){
            return this.parent.postCreate();
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
        public PickListBuilder pickList(){
            return this.parent.pickList();
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

    public class PreCreateHandler implements PreCreateBuilder{

        private Command initCommand;
        private Command beforeSaveCommand;
        private Command afterSaveCommand;
        private V3Builder parent;

        private PreCreateHandler(V3Builder parent){
            this.parent=parent;
        }
        @Override
        public PreCreateHandler init(Command... initCommand) {
            this.initCommand = buildTransactionChain(initCommand);
            return this;
        }

        @Override
        public PreCreateHandler beforeSave(Command... beforeSaveCommand) {
            this.beforeSaveCommand = buildTransactionChain(beforeSaveCommand);
            return this;
        }
        @Override
        public PreCreateHandler afterSave(Command... afterSaveCommand) {
            this.afterSaveCommand=buildTransactionChain(afterSaveCommand);
            return  this;
        }
        @Override
        public UpdateBuilder update() {
            return this.parent.update();
        }
        @Override
        public DeleteBuilder delete() { return this.parent.delete();}
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

        @Override
        public PickListBuilder pickList(){
            return this.parent.pickList();
        }


        public Command getInitCommand(){return initCommand;}
        public void setInitCommand(Command initCommand){
            this.initCommand=initCommand;
        }
        public Command getBeforeSaveCommand(){return beforeSaveCommand;}

        public void setBeforeSaveCommand(Command beforeSaveCommand){
            this.beforeSaveCommand=beforeSaveCommand;
        }
        public Command getAfterSaveCommand(){
            return afterSaveCommand;
        }
        public void setAfterSaveCommand(Command afterSaveCommand){
            this.afterSaveCommand=afterSaveCommand;
        }

    }

    public  class PostCreateHandler implements PostCreateBuilder{

        private Command afterSaveCommand;
        private Command afterTransactionCommand;
        private Command activitySaveCommand;
        private V3Builder parent;

        private PostCreateHandler(V3Builder parent) {
            this.parent = parent;
        }

        @Override
        public PostCreateHandler afterSave(Command... customSaveCommand) {
            this.afterSaveCommand = buildTransactionChain(customSaveCommand);
            return this;
        }
        @Override
        public PostCreateBuilder afterTransaction(Command... afterTransactionCommand) {
            this.afterTransactionCommand = buildTransactionChain(afterTransactionCommand);
            return this;
        }

        @Override
        public PostCreateBuilder activitySaveCommand(Command... activitySaveCommand) {
            this.activitySaveCommand = buildTransactionChain(activitySaveCommand);
            return this;
        }

        @Override
        public UpdateBuilder update() {
            return this.parent.update();
        }
        @Override
        public DeleteBuilder delete() { return this.parent.delete();}
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

        @Override
        public PickListBuilder pickList(){
            return this.parent.pickList();
        }

        public Command getAfterSaveCommand(){ return afterSaveCommand;}
        public void setAfterSaveCommand(Command afterSaveCommand){
            this.afterSaveCommand=afterSaveCommand;
        }

        public Command getAfterTransactionCommand() {
            return afterTransactionCommand;
        }
        public void setAfterTransactionCommand(Command afterTransactionCommand){
            this.afterTransactionCommand=afterTransactionCommand;
        }
        public Command getActivitySaveCommand(){ return activitySaveCommand;}
        public void setActivitySaveCommand(Command activitySaveCommand){
            this.activitySaveCommand=activitySaveCommand;
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
        public PreCreateBuilder preCreate(){
            return this.parent.preCreate();
        }
        @Override
        public  PostCreateBuilder postCreate(){
            return this.parent.postCreate();
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
        public PickListBuilder pickList(){
            return this.parent.pickList();
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
        private boolean markAsDeleteByPeople;

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

        public PreCreateBuilder preCreate(){
            return this.parent.preCreate();
        }
        public  PostCreateBuilder postCreate(){
            return this.parent.postCreate();
        }
        @Override
        public PickListBuilder pickList(){
            return this.parent.pickList();
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
        @Override
        public DeleteBuilder markAsDeleteByPeople() {
            markAsDeleteByPeople = true;
            return this;
        }

        public boolean isMarkAsDeleteByPeople() {
            return markAsDeleteByPeople;
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
        public PreCreateBuilder preCreate(){
            return this.parent.preCreate();
        }
        public  PostCreateBuilder postCreate(){
            return this.parent.postCreate();
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
        public PickListBuilder pickList(){
            return this.parent.pickList();
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

    public class PickListHandler implements PickListBuilder{

        private FacilioField mainField;
        private FacilioField secondaryField;
        private FacilioField subModuleField;
        private FacilioField fourthField;
        private FacilioField colorField;
        private FacilioField accent;
        private String severityLevel;

        private Command beforeFetchCommand;
        private Command afterFetchCommand;
        private V3Builder parent;
        private FacilioField subModuleTypeField;

        private PickListHandler(V3Builder parent) {
            this.parent = parent;
        }

        @Override
        public PickListBuilder beforeFetch(Command criteriaCommand) {
            this.beforeFetchCommand = criteriaCommand;
            return this;
        }
        @Override
        public PickListHandler afterFetch(Command afterFetchCommand) {
            this.afterFetchCommand = afterFetchCommand;
            return this;
        }

        @Override
        public PickListBuilder setMainField(String moduleName, String fieldName) {
            try {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                mainField = modBean.getField(fieldName, moduleName);
            } catch (Exception ex) {}
            return this;
        }

        @Override
        public PickListBuilder setSecondaryField(String moduleName, String fieldName) {
            try {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                secondaryField = modBean.getField(fieldName, moduleName);
            } catch (Exception ex) {}
            return this;
        }
        public PickListBuilder setFourthField(String moduleName, String fieldName) {
            try {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                fourthField = modBean.getField(fieldName, moduleName);
            } catch (Exception ex) {}
            return this;
        }
        public PickListBuilder setSubModuleTypeField(String moduleName, String fieldName) {
            try {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                subModuleTypeField = modBean.getField(fieldName, moduleName);
            } catch (Exception ex) {}
            return this;
        }

        @Override
        public PickListBuilder setSubmoduleType(String moduleName, String fieldName) {
            try {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                subModuleField = modBean.getField(fieldName, moduleName);
            } catch (Exception ex) {}
            return this;
        }

        @Override
        public PickListBuilder setColorField(String moduleName, String fieldName) {
            try {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                colorField = modBean.getField(fieldName, moduleName);
            } catch (Exception ex) {}
            return this;
        }

        @Override
        public PickListBuilder setAccentField(String moduleName, String fieldName) {
            try {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                accent = modBean.getField(fieldName, moduleName);
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
        public PreCreateBuilder preCreate(){
            return this.parent.preCreate();
        }
        public  PostCreateBuilder postCreate(){
            return this.parent.postCreate();
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
        @Override
        public ListBuilder list() {
            return this.parent.list();
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


        public void setAfterFetchCommand(Command afterFetchCommand) {
            this.afterFetchCommand = afterFetchCommand;
        }

        public FacilioField getMainField() {
            return mainField;
        }
        public FacilioField getSecondaryField(){
            return secondaryField;
        }
        public FacilioField getSubModuleField(){
            return subModuleField;
        }
        public FacilioField getFourthField(){
            return fourthField;
        }
        public FacilioField getColorField() {
            return colorField;
        }
        public FacilioField getSubModuleTypeField(){
            return subModuleTypeField;
        }

        public FacilioField getAccent() {
            return accent;
        }

        public String getSeverityLevel() {
            return severityLevel;
        }
        public void setSecondaryField(FacilioField secondaryField) {
            this.secondaryField = secondaryField;
        }
        public void setFourthField(FacilioField fourthField) {
            this.fourthField = fourthField;
        }
        public void setSubModuleField(FacilioField subModuleField) {
            this.subModuleField = subModuleField;
        }
        public void setMainField(FacilioField mainField) {
            this.mainField = mainField;
        }
        @Override
        public PickListHandler setSeverityLevel(String severityLevel) {
            this.severityLevel = severityLevel;
            return this;
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
        @Override
        public PreCreateBuilder preCreate(){
            return this.parent.preCreate();
        }
        @Override
        public  PostCreateBuilder postCreate(){
            return this.parent.postCreate();
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

        @Override
        public PickListBuilder pickList(){
            return this.parent.pickList();
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
