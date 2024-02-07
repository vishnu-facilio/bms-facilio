(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[29746],{
/***/951688:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return NewGroup}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/controls/controlgroups/NewGroup.vue?vue&type=template&id=2c59845e&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"new-group-container"},[_c("div",{staticClass:"f20 mL40 mT40"},[_vm._v(" "+_vm._s(_vm.isGroupEdit?_vm.$t("common.header.edit_group"):_vm.$t("common.header.new_group"))+" ")]),_c("div",{staticClass:"new-group-sub-container"},[_vm.loading?_c("div",{staticClass:"flex-middle fc-empty-white"},[_c("spinner",{attrs:{show:_vm.loading,size:"80"}})],1):_c("div",{staticClass:"p30"},[_c("el-form",{ref:"form",attrs:{model:_vm.groupObj,rules:_vm.rules,"label-position":"left","label-width":"150px"}},[_c("el-form-item",{attrs:{label:_vm.$t("common.header.group_name"),prop:"name"}},[_c("el-input",{staticClass:"fc-input-full-border2 width65",attrs:{placeholder:_vm.$t("common._common.enter_a_group_name")},model:{value:_vm.groupObj.name,callback:function($$v){_vm.$set(_vm.groupObj,"name",$$v)},expression:"groupObj.name"}})],1),_c("el-form-item",{staticClass:"flookup-field-groups",attrs:{label:_vm.$t("common._common.location"),prop:"location"}},[_c("FLookupField",{staticClass:"width65",attrs:{model:(_vm.groupObj.location||{}).value,fetchOptionsOnLoad:!0,canShowLookupWizard:_vm.showLookupFieldWizard,field:_vm.fieldObj,hideDropDown:!0},on:{"update:model":function($event){_vm.$set(_vm.groupObj.location||{},"value",$event)},showLookupWizard:_vm.showLookupWizard,setLookupFieldValue:_vm.setLookupFieldValue}})],1),_c("el-form-item",{attrs:{label:_vm.$t("common.header.schedule"),prop:"schedule"}},[_c("el-select",{staticClass:"fc-input-full-border-select2 width65",attrs:{clearable:"",placeholder:_vm.$t("common.products.select_a_schedule_event")},model:{value:_vm.groupObj.schedule,callback:function($$v){_vm.$set(_vm.groupObj,"schedule",$$v)},expression:"groupObj.schedule"}},[_vm.scheduleList?_vm._l(_vm.scheduleList,(function(schedule){return _c("el-option",{key:schedule.id,attrs:{label:schedule.name,value:schedule.id}})})):_vm._e()],2)],1)],1),_c("div",{staticClass:"section-container"},[_vm._l(_vm.groups,(function(group,index){return[_c("div",{key:index,staticClass:"rule-border-blue mB15 position-relative",staticStyle:{"border-left":"1px solid rgb(228, 235, 241)"}},[_c("div",{staticClass:"action-group-container"},[_c("div",{staticClass:"delete-group pointer",on:{click:function($event){return _vm.removeGroup(index)}}},[_vm.groups.length>1?_c("inline-svg",{directives:[{name:"tippy",rawName:"v-tippy",value:{placement:"top",arrow:!0},expression:"{ placement: 'top', arrow: true }"}],key:"delete-"+index,staticClass:"f-delete vertical-middle",attrs:{src:"svgs/delete",iconClass:"icon icon-sm icon-remove",title:_vm.$t("common.wo_report.delete_group")}}):_vm._e()],1),_c("div",{staticClass:"arrow-group pointer",on:{click:function($event){group.isCollapsed=!group.isCollapsed}}},[_c("i",{class:{"el-icon-arrow-up fR f16 mT5":!0,rotate180:!group.isCollapsed}})])]),_c("div",{staticClass:"p20 pT30"},[_c("el-form",{attrs:{model:group,"label-width":"220px","label-position":"left"}},[_c("el-row",{staticClass:"mB10"},[_c("el-col",{attrs:{span:20}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"typeName",label:_vm.$t("common.wo_report.section_name")}},[_c("el-input",{staticClass:"fc-input-full-border2 width50",attrs:{placeholder:_vm.$t("common._common.enter_a_name")},model:{value:group.name,callback:function($$v){_vm.$set(group,"name",$$v)},expression:"group.name"}})],1)],1)],1),_c("el-collapse-transition",[_c("div",{directives:[{name:"show",rawName:"v-show",value:group.isCollapsed,expression:"group.isCollapsed"}]},[_c("el-row",{staticClass:"mB10"},[_c("el-col",{attrs:{span:20}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"type",label:_vm.$t("common.header.group_type")}},[_c("el-select",{staticClass:"fc-input-full-border2 width50",attrs:{placeholder:_vm.$t("common.products.select_a_type")},model:{value:group.type,callback:function($$v){_vm.$set(group,"type",$$v)},expression:"group.type"}},_vm._l(_vm.groupTypeOptions,(function(option){return _c("el-option",{key:option.value,attrs:{label:option.label,value:option}})})),1)],1)],1)],1),group.category?_vm._l(group.category,(function(category,index){return _c("div",{key:index,staticClass:"pT20 pB20"},[_c("CategoryTable",{attrs:{category:category,group:group,isEdit:_vm.isEdit(category)},on:{editTable:_vm.editCategory}})],1)})):_vm._e(),_c("el-row",{staticClass:"mB10"},[_c("el-col",{attrs:{span:10}},[_c("el-button",{staticClass:"task-add-btn mT5",attrs:{disabled:_vm.$validation.isEmpty(group.type)},on:{click:function($event){return _vm.openCategoryDialog(group)}}},[_c("img",{attrs:{src:__webpack_require__(511423)}}),_c("span",{staticClass:"btn-label mL5"},[_vm._v(" "+_vm._s(_vm.$t("common.header.new_category"))+" ")])])],1)],1)],2)])],1)],1)])]}))],2),_c("div",{staticClass:"flex flex-row justify-center"},[_c("el-button",{staticClass:"task-add-btn mT5",on:{click:_vm.addNewGroup}},[_c("img",{attrs:{src:__webpack_require__(511423)}}),_c("span",{staticClass:"btn-label mL5"},[_vm._v(" "+_vm._s(_vm.$t("common.header.add_new_section"))+" ")])])],1)],1),_c("div",{staticClass:"flex-grow flex-shrink white-background"}),_c("div",{staticClass:"schedule-btn-footer"},[_c("el-button",{staticClass:"modal-btn-cancel text-uppercase",on:{click:function($event){return _vm.redirectToList()}}},[_vm._v(_vm._s(_vm.$t("common._common.cancel")))]),_c("el-button",{staticClass:"modal-btn-save mL0",attrs:{loading:_vm.saving},on:{click:_vm.save}},[_vm._v(" "+_vm._s(_vm.saving?_vm.$t("common._common._saving"):_vm.$t("common._common._save"))+" ")])],1)]),_vm.showAddCategoryDialog?_c("NewCategory",{attrs:{openDialog:_vm.showAddCategoryDialog,closeDialog:_vm.closeAddAssetDialog,selectedGroup:_vm.selectedGroup,selectedCategory:_vm.selectedCategory,isEdit:_vm.isCategoryEdit},on:{onAddNewCategory:_vm.onAddNewCategory}}):_vm._e(),_vm.showLookupFieldWizard?_c("FLookupFieldWizard",{attrs:{canShowLookupWizard:_vm.showLookupFieldWizard,selectedLookupField:_vm.fieldObj,withReadings:!0},on:{"update:canShowLookupWizard":function($event){_vm.showLookupFieldWizard=$event},"update:can-show-lookup-wizard":function($event){_vm.showLookupFieldWizard=$event},setLookupFieldValue:_vm.setLookupFieldValue}}):_vm._e()],1)},staticRenderFns=[],objectSpread2=__webpack_require__(595082),regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),FLookupFieldWizard=(__webpack_require__(670560),__webpack_require__(434284),__webpack_require__(450886),__webpack_require__(425728),__webpack_require__(260228),__webpack_require__(162506),__webpack_require__(339772),__webpack_require__(947522),__webpack_require__(499751)),FLookupField=__webpack_require__(855048),cloneDeep=__webpack_require__(150361),cloneDeep_default=__webpack_require__.n(cloneDeep),validation=__webpack_require__(990260),NewCategoryvue_type_template_id_5e39a982_render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("el-dialog",{staticStyle:{"z-index":"1999"},attrs:{visible:_vm.openDialog,fullscreen:!0,"append-to-body":!0,"before-close":_vm.closeCategoryDialog,"custom-class":"new-category-container fc-dialog-form fc-dialog-right custom-rule-dialog setup-dialog60 setup-dialog"}},[_c("div",{staticClass:"mL30 mT20 new-header-modal new-header-text f22 "},[_c("div",{staticClass:"setup-modal-title"},[_vm._v(" "+_vm._s(_vm.$t("common.header.new_category"))+" ")])]),_vm.isLoading?_c("div",{staticClass:"flex-middle fc-empty-white m10 fc-agent-empty-state"},[_c("spinner",{attrs:{show:_vm.isLoading,size:"80"}})],1):_vm.$validation.isEmpty(_vm.selectedGroup)?_vm._e():_c("div",{staticClass:"new-body-modal pL30 pR30"},[_c("div",{staticClass:"f16 font-bold pT5 pB5"},[_vm._v(_vm._s(_vm.selectedGroup.typeName))]),_c("div",{staticClass:"pT8 pB20"},[_vm._v("Type: "+_vm._s(_vm.selectedGroup.type.label))]),_vm.category?_c("div",{staticClass:"rule-border-blue p20 pR10 position-relative",staticStyle:{"border-left":"1px solid rgb(228, 235, 241)"}},[_c("el-form",{ref:"form",attrs:{model:_vm.category,rules:_vm.rules,"label-width":"125px","label-position":"left"}},[_c("el-row",[_c("el-col",[_c("el-form-item",{staticClass:"mB25",attrs:{label:"Category",prop:"type"}},[_c("el-select",{staticClass:"fc-input-full-border2 width70",attrs:{placeholder:"Select a type"},model:{value:_vm.category.type,callback:function($$v){_vm.$set(_vm.category,"type",$$v)},expression:"category.type"}},_vm._l(_vm.assetCategory,(function(option){return _c("el-option",{key:option.id,attrs:{label:option.displayName,value:option.id}})})),1)],1)],1)],1),_c("el-row",[_c("el-col",[_c("el-form-item",{staticClass:"flookup-field-category mB25",attrs:{label:"Assets",prop:"assets"}},[_c("FLookupField",{ref:"assets-lookup-field",staticClass:"width70",attrs:{model:_vm.category.assets,disabled:_vm.$validation.isEmpty(_vm.category.type),fetchOptionsOnLoad:!0,canShowLookupWizard:_vm.showLookupFieldWizard,field:_vm.fieldObj,hideDropDown:!0,isClearable:!0,preHookFilterConstruction:_vm.constructCategoryFilter},on:{"update:model":function($event){return _vm.$set(_vm.category,"assets",$event)},showLookupWizard:_vm.showLookupWizard,setLookupFieldValue:_vm.setLookupFieldValue}})],1)],1)],1),_c("NewControlPoints",{attrs:{category:_vm.category,controlPointsList:_vm.controlPointsList},on:{"update:category":function($event){_vm.category=$event}}})],1)],1):_vm._e()]),_c("div",{staticClass:"modal-dialog-footer"},[_c("div",{staticStyle:{"margin-bottom":"0px"}},[_c("el-button",{staticClass:"modal-btn-cancel text-uppercase",on:{click:_vm.closeCategoryDialog}},[_vm._v(_vm._s(_vm.$t("common._common.cancel")))]),_c("el-button",{staticClass:"modal-btn-save",attrs:{type:"primary",loading:_vm.saving},on:{click:_vm.save}},[_vm._v(" "+_vm._s(_vm.saving?_vm.$t("common._common._saving"):_vm.$t("common._common._save"))+" ")])],1)]),_vm.showLookupFieldWizard?_c("FLookupFieldWizard",{attrs:{canShowLookupWizard:_vm.showLookupFieldWizard,selectedLookupField:_vm.selectedLookupField,withReadings:!0,categoryId:_vm.category.type},on:{"update:canShowLookupWizard":function($event){_vm.showLookupFieldWizard=$event},"update:can-show-lookup-wizard":function($event){_vm.showLookupFieldWizard=$event},setLookupFieldValue:_vm.setLookupFieldValue}}):_vm._e()],1)},NewCategoryvue_type_template_id_5e39a982_staticRenderFns=[],clone=__webpack_require__(766678),clone_default=__webpack_require__.n(clone),api=__webpack_require__(32284),NewControlPointsvue_type_template_id_0e717e0c_render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("el-row",[_c("el-col",{staticClass:"new-control-point-container"},[_c("el-form-item",{staticClass:"mB15",attrs:{prop:"controlPoints",label:"Control Points"}},[_c("el-table",{ref:"tableList",staticClass:"width100",attrs:{data:_vm.category.controlPoints,fit:!0}},[_c("el-table-column",{attrs:{label:"POINTS",align:"left"},scopedSlots:_vm._u([{key:"default",fn:function(data){return[_c("el-select",{staticClass:"fc-input-full-border2 width100",attrs:{placeholder:"Select point",clearable:"","value-key":"id"},model:{value:data.row.point,callback:function($$v){_vm.$set(data.row,"point",$$v)},expression:"data.row.point"}},_vm._l(_vm.controlPointsList,(function(point,index){return _c("el-option",{key:point.displayName+"-"+index,attrs:{label:point.displayName,value:point}})})),1)]}}])}),_c("el-table-column",{attrs:{label:"EVENT TRUE",align:"left"},scopedSlots:_vm._u([{key:"default",fn:function(data){return[_vm.$validation.isEmpty(data.row.point)?_c("div",[_c("el-input",{attrs:{disabled:"",placeholder:"No Points selected"}})],1):_c("div",[_vm.isBooleanOrEnumField(_vm.getPoint(data.row))?_c("div",[_c("el-select",{staticClass:"fc-input-full-border-select2 width100",model:{value:data.row.trueVal,callback:function($$v){_vm.$set(data.row,"trueVal",$$v)},expression:"data.row.trueVal"}},[_c("el-option",{attrs:{label:_vm.getPoint(data.row).trueVal||"ON",value:_vm.getPoint(data.row).trueVal}}),_c("el-option",{attrs:{label:_vm.getPoint(data.row).falseVal||"OFF",value:_vm.getPoint(data.row).falseVal}})],1)],1):_c("div",[_c("el-input",{staticClass:"fc-input-full-border2 control-action-reading-field",staticStyle:{width:"70%","text-align":"left"},attrs:{"controls-position":"right",type:_vm.isNumberOrDecimalField(_vm.getPoint(data.row))?"number":"text",disabled:_vm.$validation.isEmpty(data.row.point)},model:{value:data.row.trueVal,callback:function($$v){_vm.$set(data.row,"trueVal",$$v)},expression:"data.row.trueVal"}},[_vm.isUnitPresent(data.row)?_c("template",{slot:"suffix"},[_c("div",{staticClass:"font-bold",staticStyle:{"padding-top":"9px"}},[_vm._v(" "+_vm._s(_vm.getPoint(data.row).unit)+" ")])]):_vm._e()],2)],1)])]}}])}),_c("el-table-column",{attrs:{label:"EVENT FALSE",align:"left"},scopedSlots:_vm._u([{key:"default",fn:function(data){return[_vm.$validation.isEmpty(data.row.point)?_c("div",[_c("el-input",{attrs:{disabled:"",placeholder:"No Points selected"}})],1):_c("div",[_vm.isBooleanOrEnumField(_vm.getPoint(data.row))?_c("div",[_c("el-select",{staticClass:"fc-input-full-border-select2 width100",model:{value:data.row.falseVal,callback:function($$v){_vm.$set(data.row,"falseVal",$$v)},expression:"data.row.falseVal"}},[_c("el-option",{attrs:{label:_vm.getPoint(data.row).trueVal||"ON",value:_vm.getPoint(data.row).trueVal}}),_c("el-option",{attrs:{label:_vm.getPoint(data.row).falseVal||"OFF",value:_vm.getPoint(data.row).falseVal}})],1)],1):_c("div",[_c("el-input",{staticClass:"fc-input-full-border2 control-action-reading-field",staticStyle:{width:"70%","text-align":"left"},attrs:{"controls-position":"right",type:_vm.isNumberOrDecimalField(_vm.getPoint(data.row))?"number":"text",disabled:_vm.$validation.isEmpty(data.row.point)},model:{value:data.row.falseVal,callback:function($$v){_vm.$set(data.row,"falseVal",$$v)},expression:"data.row.falseVal"}},[_vm.isUnitPresent(data.row)?_c("template",{slot:"suffix"},[_c("div",{staticClass:"font-bold",staticStyle:{"padding-top":"9px"}},[_vm._v(" "+_vm._s(_vm.getPoint(data.row).unit)+" ")])]):_vm._e()],2)],1)])]}}])}),_c("el-table-column",{attrs:{label:"",width:"95"},scopedSlots:_vm._u([{key:"default",fn:function(data){return[_c("img",{staticClass:"delete-icon pointer schedule-split-icon",attrs:{src:__webpack_require__(353654)},on:{click:_vm.addPoint}}),_vm.$getProperty(_vm.category,"controlPoints.length")>1?_c("img",{staticClass:"delete-icon pointer schedule-split-icon mL15",attrs:{src:__webpack_require__(385769)},on:{click:function($event){return _vm.deleteControlPoint(data.$index)}}}):_vm._e()]}}])})],1)],1)],1)],1)},NewControlPointsvue_type_template_id_0e717e0c_staticRenderFns=[],field=__webpack_require__(815872),NewControlPointsvue_type_script_lang_js={props:["category","controlPointsList"],data:function(){return{dummyPoints:["Run Command","Air Temperature","Supply Air temperature"]}},computed:{controlPointObject:function(){var controlPoints=this.category.controlPoints;return{id:(0,validation/* isEmpty */.xb)(controlPoints)?1:controlPoints.length-1,point:"",trueVal:null,falseVal:null}}},methods:{addPoint:function(){var controlPoints=this.category.controlPoints,controlPointObject=this.controlPointObject;controlPoints.push(clone_default()(controlPointObject))},deleteControlPoint:function(index){var controlPoints=this.category.controlPoints;controlPoints.splice(index,1)},getPointField:function(row){var field=row.point.field;return field},getPoint:function(row){var point=row.point;if((0,validation/* isEmpty */.xb)(point)){var field=row.field;return field}return point},isUnitPresent:function(row){var unit=row.point.unit;return unit},isBooleanOrEnumField:function(point){return!(0,validation/* isEmpty */.xb)(point)&&((0,field/* isBooleanField */.Mw)(point)||(0,field/* isEnumField */.Ri)(point))},isNumberOrDecimalField:function(point){return!(0,validation/* isEmpty */.xb)(point)&&((0,field/* isNumberField */.Z2)(point)||(0,field/* isDecimalField */.No)(point))}}},components_NewControlPointsvue_type_script_lang_js=NewControlPointsvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_NewControlPointsvue_type_script_lang_js,NewControlPointsvue_type_template_id_0e717e0c_render,NewControlPointsvue_type_template_id_0e717e0c_staticRenderFns,!1,null,null,null)
/* harmony default export */,NewControlPoints=component.exports,NewCategoryvue_type_script_lang_js={props:["openDialog","closeDialog","isEdit","selectedGroup","selectedCategory"],components:{NewControlPoints:NewControlPoints,FLookupFieldWizard:FLookupFieldWizard["default"],FLookupField:FLookupField/* default */.Z},data:function(){return{category:{type:null,assets:[],controlPoints:[],assetList:[]},isAssetsLoading:!1,assetsList:[],saving:!1,assetCategory:[],loadPoints:!1,controlPointsList:[],isLoading:!1,rules:{type:[{required:!0,message:"Please select a category",trigger:"change"}],assets:[{required:!0,message:"Please select assets",trigger:"change"}],controlPoints:[{required:!0,message:"Please select atleast one control point",trigger:"change"}]},fieldObj:{isDataLoading:!1,options:[],lookupModuleName:"asset",field:{lookupModule:{name:"asset",displayName:"Assets"}},forceFetchAlways:!0,filters:{},isDisabled:!0,multiple:!0,showFilters:!1},showLookupFieldWizard:!1,selectedLookupField:null}},created:function(){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var _this$selectedCategor,id,assets,formattedAssets,controlPoints;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return _this.isLoading=!0,_context.next=3,_this.fetchTypeVsCategory();case 3:if(!_this.isEdit){_context.next=11;break}return _this$selectedCategor=_this.selectedCategory,id=_this$selectedCategor.type.id,assets=_this$selectedCategor.assets,formattedAssets=assets.map((function(asset){var assetDetails=asset.asset;if((0,validation/* isEmpty */.xb)(assetDetails))return asset;var name=assetDetails.name,_id=assetDetails.id;return{label:name,value:_id}})),_this.category=(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},_this.selectedCategory),{},{type:id,assets:formattedAssets}),_context.next=9,_this.getPoints();case 9:_context.next=13;break;case 11:controlPoints=_this.category.controlPoints,(0,validation/* isEmpty */.xb)(controlPoints)&&controlPoints.push(clone_default()(_this.controlPointObject));case 13:_this.isLoading=!1;case 14:case"end":return _context.stop()}}),_callee)})))()},computed:{controlPointObject:function(){var controlPoints=this.category.controlPoints;return{id:(0,validation/* isEmpty */.xb)(controlPoints)?1:controlPoints.length-1,point:"",trueVal:null,falseVal:null}},categoryObj:function(){return{type:null,assets:[],controlPoints:[clone_default()(this.controlPointObject)],assetList:[]}}},methods:{constructCategoryFilter:function(){var type=this.category.type,filters={};return(0,validation/* isEmpty */.xb)(type)||(filters={category:{operator:"is",value:["".concat(type)]}}),filters},showLookupWizard:function(field,canShow){this.selectedLookupField=field,this.showLookupFieldWizard=canShow},setLookupFieldValue:function(props){var field=props.field,_field$selectedItems=field.selectedItems,selectedItems=void 0===_field$selectedItems?[]:_field$selectedItems,selectedItemIds=[];(0,validation/* isEmpty */.xb)(selectedItems)||(selectedItemIds=selectedItems.map((function(item){return item.value}))),this.category.assets=selectedItemIds,this.category.assetList=selectedItems,this.$set(this,"selectedLookupField",{}),this.getPoints()},fetchTypeVsCategory:function(){var _this2=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee2(){var type,_yield$API$get,data,error,categories;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context2){while(1)switch(_context2.prev=_context2.next){case 0:return type=_this2.selectedGroup.type,_context2.next=3,api/* API */.bl.get("/v3/control/getCategoryForType",{type:type.value});case 3:_yield$API$get=_context2.sent,data=_yield$API$get.data,error=_yield$API$get.error,(0,validation/* isEmpty */.xb)(error)?(categories=data.categories,_this2.assetCategory=categories):_this2.$message.error(error.message||"Error Occured");case 7:case"end":return _context2.stop()}}),_callee2)})))()},getPoints:function(){var _this3=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee3(){var type;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context3){while(1)switch(_context3.prev=_context3.next){case 0:type=_this3.category.type,_this3.controlPointsList=[],_this3.$http.get("v2/readings/assetcategory?id=".concat((0,validation/* isObject */.Kn)(type)?type.id:type,"&excludeEmptyFields=true&fetchControllableFields=true")).then((function(response){_this3.controlPointsList=_this3.$getProperty(response,"data.result.readings")}));case 3:case"end":return _context3.stop()}}),_callee3)})))()},save:function(){var _this4=this;this.$refs["form"].validate(function(){var _ref=(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee4(valid){var controlPoints,isPointsEmpty,type,isEdit,selectedCategory;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context4){while(1)switch(_context4.prev=_context4.next){case 0:controlPoints=_this4.category.controlPoints,isPointsEmpty=!1,controlPoints.forEach((function(point){var trueVal=point.trueVal,falseVal=point.falseVal;((0,validation/* isEmpty */.xb)(trueVal)||(0,validation/* isEmpty */.xb)(falseVal))&&(isPointsEmpty=!0)})),valid&&!isPointsEmpty?(type=_this4.category.type,isEdit=_this4.isEdit,selectedCategory=_this4.assetCategory.find((function(currCategory){return currCategory.id===type})),_this4.category=(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},_this4.category),{},{type:selectedCategory}),_this4.$emit("onAddNewCategory",{category:_this4.category,group:_this4.selectedGroup,isEdit:isEdit}),_this4.category=cloneDeep_default()(_this4.categoryObj)):isPointsEmpty&&_this4.$message.error("Control Points cannot be empty");case 4:case"end":return _context4.stop()}}),_callee4)})));return function(_x){return _ref.apply(this,arguments)}}())},closeCategoryDialog:function(){this.category=cloneDeep_default()(this.categoryObj),this.closeDialog()}}},components_NewCategoryvue_type_script_lang_js=NewCategoryvue_type_script_lang_js,NewCategory_component=(0,componentNormalizer/* default */.Z)(components_NewCategoryvue_type_script_lang_js,NewCategoryvue_type_template_id_5e39a982_render,NewCategoryvue_type_template_id_5e39a982_staticRenderFns,!1,null,null,null)
/* harmony default export */,NewCategory=NewCategory_component.exports,CategoryTable=__webpack_require__(632482),router=__webpack_require__(329435),NewGroupvue_type_script_lang_js={components:{FLookupFieldWizard:FLookupFieldWizard["default"],NewCategory:NewCategory,CategoryTable:CategoryTable/* default */.Z,FLookupField:FLookupField/* default */.Z},data:function(){return{groups:[],groupObj:{name:"",location:null,schedule:null},fieldObj:{isDataLoading:!1,options:[],lookupModuleName:"basespace",field:{lookupModule:{name:"basespace",displayName:"Base space"}},forceFetchAlways:!0,filters:{},isDisabled:!1},showLookupFieldWizard:!1,scheduleList:null,showAddCategoryDialog:!1,selectedGroup:null,selectedCategory:null,groupTypeOptions:[{label:"HVAC",value:1},{label:"Lightning",value:2},{label:"Elevator",value:3}],isCategoryEdit:!1,saving:!1,loading:!1,rules:{name:[{required:!0,message:this.$t("common._common.please_enter_group_name"),trigger:"blur"}],location:[{required:!0,message:this.$t("common._common.please_select_location"),trigger:"change"}],schedule:[{required:!0,message:this.$t("common._common.please_select_schedule"),trigger:"change"}]}}},created:function(){(0,validation/* isEmpty */.xb)(this.groups)&&this.groups.push({type:null,name:"",category:[],isCollapsed:!0}),this.init(),this.recordId&&this.deserialize(this.recordId)},computed:{groupObject:function(){return{type:null,name:"",category:[],isCollapsed:!1}},recordId:function(){var id=this.$route.params.id;return id},isGroupEdit:function(){var id=this.$route.params.id;return!(0,validation/* isEmpty */.xb)(id)}},methods:{deserialize:function(id){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var _yield$API$fetchRecor,controlGroupv2,error,record,sections,space,spaceId,name,scheduleId;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return _this.loading=!0,_context.next=3,api/* API */.bl.fetchRecord("controlGroupv2",{id:id});case 3:_yield$API$fetchRecor=_context.sent,controlGroupv2=_yield$API$fetchRecor.controlGroupv2,error=_yield$API$fetchRecor.error,error?_this.$message.error(error.message||"Error Occured"):(record=controlGroupv2,sections=controlGroupv2.sections,space=controlGroupv2.space,(0,validation/* isEmpty */.xb)(space)||(spaceId=space.id,_this.groupObj.location={value:spaceId}),name=record.name,scheduleId=record.controlSchedule.id,_this.groupObj.name=name,_this.groupObj.schedule=scheduleId,(0,validation/* isEmpty */.xb)(sections)||(_this.groups=sections.map((function(section){var sectionName=section.name,type=section.type,categories=section.categories,currCategories=categories.map((function(category){var assetCategory=category.assetCategory,controlAssets=category.controlAssets,controlFields=controlAssets[0].controlFields;return{assets:controlAssets.map((function(currAsset){return currAsset.asset.id})),assetList:controlAssets.map((function(currAsset){var asset=currAsset.asset,_ref=asset||{},id=_ref.id,name=_ref.name;return{label:name,id:id}})),type:assetCategory,controlPoints:controlFields,isEditCategory:!0}}));return{type:_this.groupTypeOptions.find((function(grpType){return grpType.value===type})),name:sectionName,category:currCategories,isCollapsed:!1}})))),_this.loading=!1;case 8:case"end":return _context.stop()}}),_callee)})))()},init:function(){var _this2=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee2(){var _yield$API$fetchAll,list,error;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context2){while(1)switch(_context2.prev=_context2.next){case 0:return _context2.next=2,api/* API */.bl.fetchAll("controlSchedule");case 2:_yield$API$fetchAll=_context2.sent,list=_yield$API$fetchAll.list,error=_yield$API$fetchAll.error,(0,validation/* isEmpty */.xb)(error)||_this2.$message.error(error.message||"Error Occured"),_this2.scheduleList=list;case 7:case"end":return _context2.stop()}}),_callee2)})))()},isEdit:function(category){var isEditCategory=category.isEditCategory;return isEditCategory},setLookupFieldValue:function(value){var field=value.field,selectedItems=field.selectedItems;this.groupObj.location=selectedItems[0],this.showLookupFieldWizard=!1},addNewGroup:function(){var groups=this.groups;groups=groups||[],groups.push(cloneDeep_default()(this.groupObject))},removeGroup:function(index){var groups=this.groups;groups.splice(index,1)},closeAddAssetDialog:function(){this.showAddCategoryDialog=!1},openCategoryDialog:function(group){this.isCategoryEdit=!1,this.selectedGroup=group,this.showAddCategoryDialog=!0},onAddNewCategory:function(newCategory){this.showAddCategoryDialog=!1;var category=newCategory.category,group=newCategory.group,isEdit=newCategory.isEdit,selectedGroup=this.groups.find((function(currGroup){return currGroup.type===group.type&&currGroup.name===group.name})),currGroupCategory=this.$getProperty(selectedGroup,"category");if((0,validation/* isEmpty */.xb)(currGroupCategory))currGroupCategory.push(cloneDeep_default()(category));else{var categoryIndex=currGroupCategory.findIndex((function(currCategory){return currCategory.type===category.type}));isEdit?currGroupCategory.splice(categoryIndex,1,category):currGroupCategory.push(cloneDeep_default()(category))}},editCategory:function(_ref2){var group=_ref2.group,category=_ref2.category;this.selectedGroup=group,this.selectedCategory=category,this.isCategoryEdit=!0,this.showAddCategoryDialog=!0},showLookupWizard:function(field,canShow){this.selectedLookupField=field,this.showLookupFieldWizard=canShow},save:function(){var _this3=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee4(){var data;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context4){while(1)switch(_context4.prev=_context4.next){case 0:data=_this3.serialize(),_this3.$refs["form"].validate(function(){var _ref3=(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee3(valid){var isSectionEmpty,id,currId,_yield$API$updateReco,error,_yield$API$createReco,controlGroupv2,_error,_ref4,name,route;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context3){while(1)switch(_context3.prev=_context3.next){case 0:if(isSectionEmpty=!1,_this3.groups.forEach((function(group){var name=group.name,type=group.type,category=group.category;((0,validation/* isEmpty */.xb)(name)||(0,validation/* isEmpty */.xb)(type)||(0,validation/* isEmpty */.xb)(category))&&(isSectionEmpty=!0)})),!valid||isSectionEmpty){_context3.next=23;break}if(_this3.saving=!0,id=_this3.$route.params.id,(0,validation/* isEmpty */.xb)(id)){_context3.next=13;break}return _context3.next=8,api/* API */.bl.updateRecord("controlGroupv2",{id:id,data:data});case 8:_yield$API$updateReco=_context3.sent,error=_yield$API$updateReco.error,(0,validation/* isEmpty */.xb)(error)?(currId=id,_this3.$message.success(_this3.$t("common._common.group_edited_successfully"))):_this3.$message.error(error.message||"Error Occured"),_context3.next=19;break;case 13:return _context3.next=15,api/* API */.bl.createRecord("controlGroupv2",{data:data});case 15:_yield$API$createReco=_context3.sent,controlGroupv2=_yield$API$createReco.controlGroupv2,_error=_yield$API$createReco.error,(0,validation/* isEmpty */.xb)(_error)?(currId=_this3.$getProperty(controlGroupv2,"id"),_this3.$message.success(_this3.$t("common._common.group_created_successfully"))):_this3.$message.error(_error.message||"Error Occured");case 19:_this3.saving=!1,(0,validation/* isEmpty */.xb)(currId)||((0,router/* isWebTabsEnabled */.tj)()?(_ref4=(0,router/* findRouteForModule */.Jp)("controlGroupv2",router/* pageTypes */.As.OVERVIEW)||{},name=_ref4.name,route={name:name,params:{viewname:"all",id:currId}},_this3.$router.push(route)):_this3.$router.push({name:"group-summary",params:{id:currId,moduleName:_this3.moduleName,viewname:"all"},query:(0,objectSpread2/* default */.Z)({},_this3.$route.query)})),_context3.next=24;break;case 23:isSectionEmpty&&_this3.$message.error(_this3.$t("common._common.sections_name_type_cannot_empty"));case 24:case"end":return _context3.stop()}}),_callee3)})));return function(_x){return _ref3.apply(this,arguments)}}());case 2:case"end":return _context4.stop()}}),_callee4)})))()},serialize:function(){var _this4=this,id=this.$getProperty(this,"groupObj.location.value"),data={name:this.groupObj.name,controlSchedule:{id:this.groupObj.schedule},space:{id:id},sections:[]};return this.groups.forEach((function(group){var name=group.name,type=group.type,category=group.category,currGroupType=_this4.$getProperty(type,"value"),sectionParams={name:name,type:currGroupType,categories:[]};category.forEach((function(currCategory){var type=currCategory.type,assetList=currCategory.assetList,controlPoints=currCategory.controlPoints,id=type.id,categoryParam={name:name,assetCategory:{id:id},controlAssets:assetList.map((function(asset){var formattedControlpoints=controlPoints.map((function(currPoint){var fieldId=_this4.$getProperty(currPoint,"point.fieldId");return(0,validation/* isEmpty */.xb)(fieldId)&&(fieldId=_this4.$getProperty(currPoint,"fieldId")),{type:1,fieldId:fieldId,trueVal:currPoint.trueVal,falseVal:currPoint.falseVal}}));return{asset:{id:asset.value||asset.asset.id},controlFields:formattedControlpoints}}))};sectionParams.categories.push(categoryParam)})),data.sections.push(sectionParams)})),data},redirectToList:function(){if((0,router/* isWebTabsEnabled */.tj)()){var _ref5=(0,router/* findRouteForModule */.Jp)("controlGroupv2",router/* pageTypes */.As.LIST)||{},name=_ref5.name,route={name:name,params:{viewname:"all"}};this.$router.push(route)}else this.$router.push({name:"group-list",params:{viewname:"all"}})}}},controlgroups_NewGroupvue_type_script_lang_js=NewGroupvue_type_script_lang_js,NewGroup_component=(0,componentNormalizer/* default */.Z)(controlgroups_NewGroupvue_type_script_lang_js,render,staticRenderFns,!1,null,"2c59845e",null)
/* harmony default export */,NewGroup=NewGroup_component.exports},
/***/511423:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"b924ed3856bb4e9406aa8f8b4a5913cd.svg";
/***/},
/***/353654:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"d7f1f315aba97cd14240ee6f3d34465f.svg";
/***/},
/***/385769:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"4a9843791f38c4a28fdf9f0a4584780d.svg";
/***/}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/29746.js.map