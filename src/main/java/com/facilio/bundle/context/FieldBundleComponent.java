package com.facilio.bundle.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Priority;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bundle.interfaces.BundleComponentInterface;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
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
	
	@Override
	public void getParentDetails(FacilioContext context) throws Exception {
		
		Long fieldId = (Long) context.get(BundleConstants.COMPONENT_ID);
		FacilioField fieldContext = Constants.getModBean().getField(fieldId);
		
		context.put(BundleConstants.PARENT_COMPONENT_ID, fieldContext.getModule().getModuleId());
		context.put(BundleConstants.PARENT_COMPONENT_NAME, fieldContext.getModule().getDisplayName());
	}
	
	@Override
	public String getFileName(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		Long fieldId = (Long)context.get(BundleConstants.COMPONENT_ID);
		
		FacilioField field = ((ModuleBean) BeanFactory.lookup("ModuleBean")).getField(fieldId);
		
		return field.getModule().getName()+"_"+field.getName();
	}

	@Override
	public void getFormatedObject(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleChangeSetContext componentChange = (BundleChangeSetContext) context.get(BundleConstants.BUNDLE_CHANGE);
		BundleFolderContext componentFolder = (BundleFolderContext) context.get(BundleConstants.COMPONENTS_FOLDER);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioField field = modBean.getField(componentChange.getComponentId());
		
		String fileName = getFileName(context);
		
		BundleFolderContext fieldFolder = componentFolder.getOrAddFolder(componentChange.getComponentTypeEnum().getName());
		
		BundleFileContext moduleFieldsFile = fieldFolder.getFile(fileName+"."+BundleConstants.XML_FILE_EXTN);
		
		if(moduleFieldsFile == null) {
			moduleFieldsFile = new BundleFileContext(fileName, BundleConstants.XML_FILE_EXTN, FIELDS, null);
			fieldFolder.addFile(fileName +"."+ BundleConstants.XML_FILE_EXTN, moduleFieldsFile);
		}
		
		
		XMLBuilder xmlBuilder = moduleFieldsFile.getXmlContent();
		
		xmlBuilder.element(componentChange.getComponentTypeEnum().getName())
					.attr(BundleConstants.Components.MODE, componentChange.getModeEnum().getName())
					.attr(MODULE_NAME, field.getModule().getName())
				  .element(BundleConstants.Components.NAME)
					.text(field.getName())
					.p()
				  .element(BundleConstants.Components.DISPLAY_NAME)
				  	.text(field.getDisplayName())
				  	.p();
		
	}

//	@Override
//	public JSONArray getAllFormatedObject(FacilioContext context) throws Exception {
//		// TODO Auto-generated method stub
//		JSONObject parentObject = (JSONObject)context.get(BundleConstants.PARENT_COMPONENT_OBJECT);
//		String moduleName = (String) parentObject.get("name");
//		
//		List<FacilioField> fields = Constants.getModBean().getAllFields(moduleName).stream().filter((field) -> { if(field.getModule().getName().equals(moduleName)) {return true;} return false;}).collect(Collectors.toList());
//		
//		JSONArray returnList = new JSONArray();
//		for(FacilioField field : fields) {
//			context.put(BundleConstants.COMPONENT_OBJECT, field);
//			context.put(BundleConstants.COMPONENT_ID, field.getFieldId());
//			
//			JSONObject formattedObject = getFormatedObject(context);
//			
//			returnList.add(formattedObject);
//		}
//		
//		
//		return returnList;
//	}

	@Override
	public void install(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		JSONObject parentModule = (JSONObject) context.get(BundleConstants.PARENT_COMPONENT_OBJECT);
		
		FacilioModule module = modBean.getModule((String)parentModule.get("name"));
		
		JSONObject fieldJSON = (JSONObject) context.get(BundleConstants.COMPONENT_OBJECT);
		
		FacilioField field = FieldUtil.parseFieldJson(fieldJSON);
		
		if(field instanceof LineItemField) {
			return;
		}
		
		field.setOrgId(AccountUtil.getCurrentOrg().getId());
		field.setModule(module);
		
		if(field instanceof LookupField || field instanceof MultiLookupField) {
			BaseLookupField lookupField = (BaseLookupField) field;
			
			String lookupModuleName = lookupField.getLookupModule().getName();
			
			FacilioModule lookupModule = modBean.getModule(lookupModuleName);
			
			lookupField.setLookupModule(lookupModule);
			if(lookupModule.getModuleId() > 0) {
				lookupField.setLookupModuleId(lookupModule.getModuleId());
			}
			else {
				lookupField.setSpecialType(lookupModuleName);
			}
			
		}
		
		if(field instanceof EnumField) {
			
			EnumField enumField = (EnumField) field;
			
			if(enumField.getValues() != null) {
				for(EnumFieldValue<Integer> values : enumField.getValues()) {
					if(values.getIndex() == null) {
						values.setIndex(0);
					}
				}
			}
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
