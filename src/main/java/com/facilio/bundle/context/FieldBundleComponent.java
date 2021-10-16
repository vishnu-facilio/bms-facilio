package com.facilio.bundle.context;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Priority;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.enums.BundleModeEnum;
import com.facilio.bundle.interfaces.BundleComponentInterface;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.BaseLookupField;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.EnumFieldValue;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LineItemField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;

import lombok.extern.log4j.Log4j;

@Log4j
public class FieldBundleComponent extends CommonBundleComponent {
	
	public static final String FIELDS = "Fields";
	public static final String MODULE_NAME = "moduleName";
	
	public static final String DISPLAY_TYPE = "displayType";
	public static final String DATA_TYPE = "dataType";
	public static final String REQUIRED = "required";
	
	public static final List<String> SYSTEM_UN_PACKABLE_FIELD_NAMES = new ArrayList<String>();
	
//	public static final List<String> SYSTEM_DEFAULT_FIELD_NAMES = new ArrayList<String>();
	
	static {
		SYSTEM_UN_PACKABLE_FIELD_NAMES.add("approvalFlowId");
		SYSTEM_UN_PACKABLE_FIELD_NAMES.add("approvalStatus");
		SYSTEM_UN_PACKABLE_FIELD_NAMES.add("moduleState");
		SYSTEM_UN_PACKABLE_FIELD_NAMES.add("stateFlowId");
		
		SYSTEM_UN_PACKABLE_FIELD_NAMES.add("name");
		SYSTEM_UN_PACKABLE_FIELD_NAMES.add("photo");
		SYSTEM_UN_PACKABLE_FIELD_NAMES.add("siteId");
		SYSTEM_UN_PACKABLE_FIELD_NAMES.add("sysCreatedBy");
		SYSTEM_UN_PACKABLE_FIELD_NAMES.add("sysCreatedTime");
		SYSTEM_UN_PACKABLE_FIELD_NAMES.add("sysModifiedBy");
		SYSTEM_UN_PACKABLE_FIELD_NAMES.add("sysModifiedTime");
	}
	
	public String getFileName(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		Long fieldId = (Long)context.get(BundleConstants.COMPONENT_ID);
		
		FacilioField field = ((ModuleBean) BeanFactory.lookup("ModuleBean")).getField(fieldId);
		
		return field.getModule().getName()+"_"+field.getName();
	}
	
	public boolean isPackableComponent(FacilioContext context) throws InstantiationException, IllegalAccessException, Exception {
		Long fieldId = (Long)context.get(BundleConstants.COMPONENT_ID);
		
		FacilioField field = ((ModuleBean) BeanFactory.lookup("ModuleBean")).getField(fieldId);
		
		//check 1 - check if fields's module is in restricted list 
		
		if(ModuleBundleComponent.IGNORE_MODULE_TYPES.contains(field.getModule().getType())) {
			return false;
		}
		
		if(SYSTEM_UN_PACKABLE_FIELD_NAMES.contains(field.getName())) {
			return false;
		}
		
		return true;
		
	}
	
	@Override
	public Condition getFetchChangeSetCondition(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		Condition changeSetFetchCondition = CriteriaAPI.getCondition("NAME", "name", StringUtils.join(SYSTEM_UN_PACKABLE_FIELD_NAMES, ","), StringOperators.ISN_T);
		
		return changeSetFetchCondition;
	}
	
	@Override
	public void fillBundleXML(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		String fileName = getFileName(context)+".xml";
		XMLBuilder bundleBuilder = (XMLBuilder) context.get(BundleConstants.BUNDLE_XML_BUILDER);
		
		BundleChangeSetContext componentChange = (BundleChangeSetContext) context.get(BundleConstants.BUNDLE_CHANGE);
		
		bundleBuilder.element(BundleConstants.VALUES).attr("version", componentChange.getTempVersion()+"").text(fileName);
	}

	@Override
	public void getFormatedObject(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
			
		BundleChangeSetContext componentChange = (BundleChangeSetContext) context.get(BundleConstants.BUNDLE_CHANGE);
		BundleFolderContext componentFolder = (BundleFolderContext) context.get(BundleConstants.COMPONENTS_FOLDER);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioField field = modBean.getField(componentChange.getComponentId());
		
		String fileName = getFileName(context);
		
		BundleFileContext fieldFile = new BundleFileContext(fileName, BundleConstants.XML_FILE_EXTN, componentChange.getComponentTypeEnum().getName(), null);
		
		Boolean isRequired = field.getRequired() == null? false : field.getRequired();
		
		XMLBuilder xmlBuilder = fieldFile.getXmlContent();
		
		xmlBuilder
					.attr(MODULE_NAME, field.getModule().getName())
				  .element(BundleConstants.Components.NAME).text(field.getName()).p()
				  .element(BundleConstants.Components.DISPLAY_NAME).text(field.getDisplayName()).p()
				  .element(DATA_TYPE).text(field.getDataTypeEnum().getTypeAsString()).p()
				  .element(DISPLAY_TYPE).text(field.getDisplayTypeInt()+"").p()
				  .element(REQUIRED).text(isRequired.toString()).p()
				  ;	
		
		BundleFolderContext fieldFolder = componentFolder.getOrAddFolder(componentChange.getComponentTypeEnum().getName());
		
		fieldFolder.addFile(fileName+"."+BundleConstants.XML_FILE_EXTN, fieldFile);
	}

	
	@Override
	public void install(FacilioContext context) throws Exception {
		
		BundleFileContext changeSetXMLFile = (BundleFileContext) context.get(BundleConstants.BUNDLED_XML_COMPONENT_FILE);
		
		XMLBuilder xmlContent = changeSetXMLFile.getXmlContent();
		
		String moduleName = null;
		
		for(XMLBuilder fieldElement : xmlContent.getElementList(BundleComponentsEnum.FIELD.getName())) {
			
			BundleModeEnum modeEnum = BundleModeEnum.valueOfName(fieldElement.getAttribute(BundleConstants.Components.MODE));
			moduleName = fieldElement.getAttribute(MODULE_NAME);
			
			switch(modeEnum) {
			case ADD: 
				
				FacilioField field = new FacilioField();
				
				field.setDisplayName(fieldElement.getElement(BundleConstants.Components.DISPLAY_NAME).getText());
				field.setDataType(FieldType.getCFType(fieldElement.getElement(DATA_TYPE).getText()));
				field.setDisplayTypeInt(Integer.valueOf(fieldElement.getElement(DISPLAY_TYPE).getText()));
				field.setRequired(Boolean.valueOf(fieldElement.getElement(REQUIRED).getText()));
				
				List<FacilioField> addFields = new ArrayList<FacilioField>();
				
				addFields.add(field);
				
				FacilioChain addFieldsChain = TransactionChainFactory.getAddFieldsChain();
				
				FacilioContext newContext = addFieldsChain.getContext();
				
				newContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
				newContext.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, addFields);
				
				addFieldsChain.execute();
				
				break;
			}
		}
	}

//	@Override
//	public void install(FacilioContext context) throws Exception {
//		// TODO Auto-generated method stub
//		
//		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//		
//		JSONObject parentModule = (JSONObject) context.get(BundleConstants.PARENT_COMPONENT_OBJECT);
//		
//		FacilioModule module = modBean.getModule((String)parentModule.get("name"));
//		
//		JSONObject fieldJSON = (JSONObject) context.get(BundleConstants.COMPONENT_OBJECT);
//		
//		FacilioField field = FieldUtil.parseFieldJson(fieldJSON);
//		
//		if(field instanceof LineItemField) {
//			return;
//		}
//		
//		field.setOrgId(AccountUtil.getCurrentOrg().getId());
//		field.setModule(module);
//		
//		if(field instanceof LookupField || field instanceof MultiLookupField) {
//			BaseLookupField lookupField = (BaseLookupField) field;
//			
//			String lookupModuleName = lookupField.getLookupModule().getName();
//			
//			FacilioModule lookupModule = modBean.getModule(lookupModuleName);
//			
//			lookupField.setLookupModule(lookupModule);
//			if(lookupModule.getModuleId() > 0) {
//				lookupField.setLookupModuleId(lookupModule.getModuleId());
//			}
//			else {
//				lookupField.setSpecialType(lookupModuleName);
//			}
//			
//		}
//		
//		if(field instanceof EnumField) {
//			
//			EnumField enumField = (EnumField) field;
//			
//			if(enumField.getValues() != null) {
//				for(EnumFieldValue<Integer> values : enumField.getValues()) {
//					if(values.getIndex() == null) {
//						values.setIndex(0);
//					}
//				}
//			}
//		}
//		
//		FacilioChain addFieldsChain = TransactionChainFactory.getAddFieldsChain();
//		
//		FacilioContext addFieldContext = addFieldsChain.getContext();
//		
//		List<FacilioField> fields = new ArrayList<FacilioField>();
//		fields.add(field);
//		
//		addFieldContext.put(FacilioConstants.ContextNames.MODULE_NAME, field.getModule().getName());
//		addFieldContext.put(FacilioConstants.ContextNames.IS_SKIP_COUNTER_FIELD_ADD, true);
//		addFieldContext.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
//		
//		addFieldsChain.execute();
//		
//		Long fieldId = ((List<Long>) addFieldContext.get(FacilioConstants.ContextNames.MODULE_FIELD_IDS)).get(0);
//		
//	}

	@Override

	public void postInstall(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		JSONObject fieldJSON = (JSONObject) context.get(BundleConstants.COMPONENT_OBJECT);
		
		FacilioField field = FieldUtil.parseFieldJson(fieldJSON);
		
		if(!(field instanceof LineItemField)) {
			return;
		}
		
		field.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		if(field instanceof LineItemField) {
			LineItemField lineItem = (LineItemField) field;
			
			String childModuleName = lineItem.getChildModule().getName();;
			
			FacilioModule childModule = modBean.getModule(childModuleName);
			
			lineItem.setChildModule(childModule);
			lineItem.setChildModuleId(childModule.getModuleId());
			
			String childFieldName = lineItem.getChildLookupField().getName();
			FacilioField childfield = modBean.getField(childFieldName, childModule.getName());
			
			lineItem.setChildLookupField((LookupField)childfield);
			lineItem.setChildLookupFieldId(childfield.getFieldId());
			
		}
		
		FacilioChain addFieldsChain = TransactionChainFactory.getAddFieldsChain();
		
		FacilioContext addFieldContext = addFieldsChain.getContext();
		
		List<FacilioField> fields = new ArrayList<FacilioField>();
		fields.add(field);
		
		addFieldContext.put(FacilioConstants.ContextNames.MODULE_NAME, field.getModule().getName());
		addFieldContext.put(FacilioConstants.ContextNames.IS_SKIP_COUNTER_FIELD_ADD, true);
		addFieldContext.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
		
		addFieldsChain.execute();
		
		Long fieldId = ((List<Long>) addFieldContext.get(FacilioConstants.ContextNames.MODULE_FIELD_IDS)).get(0);
		
	}

}
