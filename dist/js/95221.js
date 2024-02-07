"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[95221],{
/***/339137:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return JobPlanItems}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/workorder/jobplan/plans/JobPlanItems.vue?vue&type=template&id=73fe7f24
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",[_c("LineItemList",_vm._b({ref:"lineItemList",staticClass:"height-100",attrs:{config:_vm.listConfiguration,moduleName:_vm.moduleName,moduleDisplayName:_vm.moduleDisplayName,additionalParams:_vm.additionalParams,widgetDetails:_vm.widgetDetails,viewname:"all"}},"LineItemList",_vm.$attrs,!1))],1)},staticRenderFns=[],LineItemList=__webpack_require__(197724),JobPlanPlansCommon=__webpack_require__(745537),JobPlanItemsvue_type_script_lang_js={components:{LineItemList:LineItemList/* default */.Z},extends:JobPlanPlansCommon/* default */.Z,props:["details","widget"],computed:{moduleName:function(){return"jobPlanItems"},moduleDisplayName:function(){return"Job Plan Item"}}},plans_JobPlanItemsvue_type_script_lang_js=JobPlanItemsvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(plans_JobPlanItemsvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,JobPlanItems=component.exports},
/***/745537:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return JobPlanPlansCommon}});
// EXTERNAL MODULE: ./node_modules/@babel/runtime/helpers/esm/regeneratorRuntime.js
var render,staticRenderFns,regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),objectSpread2=__webpack_require__(595082),CustomModuleData=(__webpack_require__(434284),__webpack_require__(689565)),pm_utils=__webpack_require__(195123),JobPlanPlansCommonvue_type_script_lang_js={props:["widget","details"],computed:{workorder:function(){var _ref=this||{},details=_ref.details,_ref2=details||{},workorder=_ref2.workorder;return workorder},additionalParams:function(){var _ref3=this||{},details=_ref3.details,_ref4=details||{},id=_ref4.id;return{jobPlan:{id:id}}},filters:function(){var _ref5=this||{},details=_ref5.details,_ref6=details||{},id=_ref6.id,filter={jobPlan:{operatorId:9,value:["".concat(id)]}};return filter},hideFooter:function(){var _ref7=this||{},details=_ref7.details,_ref8=details||{},jpStatus=_ref8.jpStatus,canHideFooter="Published"===pm_utils/* PUBLISHED_STATUS */.Iq[jpStatus];return{canHideFooter:canHideFooter}},editConfig:function(){var _ref9=this||{},hideFooter=_ref9.hideFooter,_ref10=hideFooter||{},canHideFooter=_ref10.canHideFooter;return{canHideEdit:canHideFooter}},deleteConfig:function(){var _ref11=this||{},hideFooter=_ref11.hideFooter,_ref12=hideFooter||{},canHideFooter=_ref12.canHideFooter;return{canHideDelete:canHideFooter}},listConfiguration:function(){var filters=this.filters,formConfig=this.formConfig,editConfig=this.editConfig,deleteConfig=this.deleteConfig,hideFooter=this.hideFooter;return(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({filters:filters},editConfig||{}),deleteConfig||{}),hideFooter||{}),formConfig||{}),{},{canHideColumnConfig:!0,hideListSelect:!0})},formConfig:function(){var _this=this;return{formType:"POP_UP_FORM",modifyFieldPropsHook:function(field){var _ref13=field||{},name=_ref13.name;if("itemType"===name&&"jobPlanItems"===_this.moduleName||"toolType"===name&&"jobPlanTools"===_this.moduleName||"service"===name&&"jobPlanServices"===_this.moduleName)return(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},field),{},{isDisabled:!0})}}},widgetDetails:function(){var widget=this.widget,moduleDisplayName=this.moduleDisplayName,emptyStateBtnList=this.emptyStateBtnList,_ref14=widget||{},relatedList=_ref14.relatedList,_ref15=relatedList||{},summaryWidgetName=_ref15.summaryWidgetName,emptyStateText=this.$t("setup.relationship.no_module_available",{moduleName:moduleDisplayName});return{canHideTitle:!0,perPage:5,summaryWidgetName:summaryWidgetName,emptyStateText:emptyStateText,actionButtonList:emptyStateBtnList}},emptyStateBtnList:function(){var moduleName=this.moduleName,details=this.details,_ref16=details||{},jpStatus=_ref16.jpStatus,emptyStateBtnList=[];return"Unpublished"===pm_utils/* PUBLISHED_STATUS */.Iq[jpStatus]&&("jobPlanItems"===moduleName?emptyStateBtnList=[{label:this.$t("common.inventory.select_item"),value:{lookupModuleName:"itemTypes",lookupModuleDisplayName:this.$t("common.inventory._item_types"),getRecordDetails:function(){var _getRecordDetails=(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(payload){var _ref17,id,moduleName,jobPlanItems;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return _ref17=payload||{},id=_ref17.id,moduleName=_ref17.moduleName,jobPlanItems={itemType:{id:id}},_context.abrupt("return",new CustomModuleData/* CustomModuleData */.u((0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},jobPlanItems),{},{moduleName:moduleName})));case 3:case"end":return _context.stop()}}),_callee)})));function getRecordDetails(_x){return _getRecordDetails.apply(this,arguments)}return getRecordDetails}()}}]:"jobPlanTools"===moduleName?emptyStateBtnList=[{label:this.$t("common.inventory.select_tool"),value:{lookupModuleName:"toolTypes",lookupModuleDisplayName:this.$t("common.inventory._tool_types"),getRecordDetails:function(){var _getRecordDetails2=(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee2(payload){var _ref18,id,moduleName,jobPlanTools;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context2){while(1)switch(_context2.prev=_context2.next){case 0:return _ref18=payload||{},id=_ref18.id,moduleName=_ref18.moduleName,jobPlanTools={toolType:{id:id}},_context2.abrupt("return",new CustomModuleData/* CustomModuleData */.u((0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},jobPlanTools),{},{moduleName:moduleName})));case 3:case"end":return _context2.stop()}}),_callee2)})));function getRecordDetails(_x2){return _getRecordDetails2.apply(this,arguments)}return getRecordDetails}()}}]:"jobPlanServices"===moduleName&&(emptyStateBtnList=[{label:this.$t("common.inventory.select_service"),value:{lookupModuleName:"service",lookupModuleDisplayName:this.$t("common.inventory._services"),getRecordDetails:function(){var _getRecordDetails3=(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee3(payload){var _ref19,id,moduleName,jobPlanServices;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context3){while(1)switch(_context3.prev=_context3.next){case 0:return _ref19=payload||{},id=_ref19.id,moduleName=_ref19.moduleName,jobPlanServices={service:{id:id}},_context3.abrupt("return",new CustomModuleData/* CustomModuleData */.u((0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},jobPlanServices),{},{moduleName:moduleName})));case 3:case"end":return _context3.stop()}}),_callee3)})));function getRecordDetails(_x3){return _getRecordDetails3.apply(this,arguments)}return getRecordDetails}()}}])),emptyStateBtnList}}},plans_JobPlanPlansCommonvue_type_script_lang_js=JobPlanPlansCommonvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(plans_JobPlanPlansCommonvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,JobPlanPlansCommon=component.exports;
// EXTERNAL MODULE: ./node_modules/@babel/runtime/helpers/esm/asyncToGenerator.js
}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/95221.js.map