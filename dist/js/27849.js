"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[27849,61215],{
/***/561215:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return RelatedList}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/page/widget/common/line-items/RelatedList.vue?vue&type=template&id=6d6218ca
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("LineItemList",_vm._b({attrs:{config:_vm.listConfiguration,moduleDisplayName:_vm.moduleDisplayName,moduleName:_vm.moduleName,widgetDetails:_vm.widgetDetails,viewname:_vm.viewname},scopedSlots:_vm._u([{key:"header-additional-action-right",fn:function(){return[_c("span",{staticClass:"separator mL10 mR10"},[_vm._v("|")]),_c("fc-icon",{staticClass:"pointer",attrs:{group:"action",name:"maximise",size:"14",color:"#91969d"},on:{click:function($event){_vm.canExpandList=!0}}})]},proxy:!0}])},"LineItemList",_vm.$attrs,!1),[_vm.canExpandList?_c("ListPopup",{attrs:{config:_vm.currentModuleDetails},on:{onCancel:function($event){_vm.canExpandList=!1}}}):_vm._e()],1)},staticRenderFns=[],regeneratorRuntime=__webpack_require__(150124),objectSpread2=__webpack_require__(595082),asyncToGenerator=__webpack_require__(548534),LineItemList=(__webpack_require__(434284),__webpack_require__(634338),__webpack_require__(197724)),router=__webpack_require__(329435),RelatedListData=__webpack_require__(577347),RelatedListPopup=__webpack_require__(99228),RelatedListvue_type_script_lang_js={props:["widget","details"],components:{LineItemList:LineItemList/* default */.Z,ListPopup:RelatedListPopup/* default */.Z},data:function(){return{canExpandList:!1}},computed:{modelDataClass:function(){return RelatedListData/* RelatedListData */.G},relatedListObj:function(){return this.$getProperty(this.widget,"relatedList")||{}},moduleName:function(){return this.$getProperty(this.relatedListObj,"module.name")||""},perPage:function(){return 5},moduleDisplayName:function(){return this.$getProperty(this.relatedListObj,"module.displayName")||""},viewname:function(){return"hidden-all"},relatedFieldName:function(){return this.$getProperty(this.relatedListObj,"field.name")},recordId:function(){var details=this.details,id=details.id;return id},listConfiguration:function(){var redirectToOverview=this.redirectToOverview,getRecordList=this.getRecordList,getRecordCount=this.getRecordCount,modelDataClass=this.modelDataClass,modifyFieldPropsHook=this.modifyFieldPropsHook;
// Disable edit, delete and create temporarily until pageBuilder release
// let { isEditable, isDeletable, isCreateAllowed } = this.relatedListObj || {}
return{canHideEdit:!0,canHideDelete:!0,canShowAddBtn:!1,canHideFooter:!0,hideListSelect:!0,modelDataClass:modelDataClass,getRecordList:getRecordList,getRecordCount:getRecordCount,modifyFieldPropsHook:modifyFieldPropsHook,mainfieldAction:redirectToOverview}},title:function(){var moduleDisplayName=this.moduleDisplayName,relatedListObj=this.relatedListObj,_ref=(null===relatedListObj||void 0===relatedListObj?void 0:relatedListObj.field)||{},relatedListDisplayName=_ref.relatedListDisplayName;return relatedListDisplayName||moduleDisplayName},widgetDetails:function(){var relatedListObj=this.relatedListObj,moduleDisplayName=this.moduleDisplayName,perPage=this.perPage,details=this.details,title=this.title,_ref2=relatedListObj||{},summaryWidgetName=_ref2.summaryWidgetName,emptyStateText=this.$t("setup.relationship.no_module_available",{moduleName:moduleDisplayName});return{perPage:perPage,summaryWidgetName:summaryWidgetName,emptyStateText:emptyStateText,title:title,parentDetails:details}},isCustomModule:function(){return this.$getProperty(this.relatedListObj,"module.custom")},currentModuleDetails:function(){var moduleName=this.moduleName,title=this.title,getRecordList=this.getRecordList,getRecordCount=this.getRecordCount,redirectToOverview=this.redirectToOverview;return{lookupModuleName:moduleName,lookupModuleDisplayName:title,getRecordList:getRecordList,getRecordCount:getRecordCount,mainfieldAction:redirectToOverview}}},methods:{getRecordList:function(extraParams){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var moduleName,viewname,perPage,recordId,relatedFieldName,$attrs,currentModuleName,params;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return moduleName=_this.moduleName,viewname=_this.viewname,perPage=_this.perPage,recordId=_this.recordId,relatedFieldName=_this.relatedFieldName,$attrs=_this.$attrs,currentModuleName=$attrs.moduleName,params=(0,objectSpread2/* default */.Z)({moduleName:moduleName,viewname:viewname,perPage:perPage,recordId:recordId,relatedFieldName:relatedFieldName,currentModuleName:currentModuleName},extraParams),_context.next=5,_this.modelDataClass.fetchAll(params);case 5:return _context.abrupt("return",_context.sent);case 6:case"end":return _context.stop()}}),_callee)})))()},getRecordCount:function(){return this.modelDataClass.recordListCount},modifyFieldPropsHook:function(field){var details=this.details,$attrs=this.$attrs,actualModuleName=$attrs.moduleName,_ref3=field||{},displayTypeEnum=_ref3.displayTypeEnum,lookupModuleName=_ref3.lookupModuleName,isRelatedModuleField="LOOKUP_SIMPLE"===displayTypeEnum&&lookupModuleName===actualModuleName;if(isRelatedModuleField){var _ref4=details||{},id=_ref4.id;return(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},field),{},{isDisabled:!0,value:id})}},redirectToOverview:function(record){var route,moduleName=this.moduleName,isCustomModule=this.isCustomModule,_ref5=record||{},id=_ref5.id;if((0,router/* isWebTabsEnabled */.tj)()){var _ref6=(0,router/* findRouteForModule */.Jp)(moduleName,router/* pageTypes */.As.OVERVIEW)||{},name=_ref6.name;name&&(route=this.$router.resolve({name:name,params:{viewname:"all",id:id}}).href)}else{var routerMap={workorder:{name:"wosummarynew",params:{id:id}},workpermit:{name:"workPermitSummary",params:{id:id,viewname:"all"}},purchaserequest:{name:"prSummary",params:{id:id,viewname:"all"}},purchaseorder:{name:"poSummary",params:{id:id,viewname:"all"}},tenantcontact:{name:"tenantcontact",params:{id:id,viewname:"all"}},tenantunit:{path:"/app/tm/tenantunit/".concat(id,"/overview")},vendors:{name:"vendorsSummary",params:{id:id,viewname:"all"}},asset:{name:"assetsummary",params:{assetid:id,viewname:"all"}},tenantspaces:{name:"tenant",params:{id:(record.tenant||{}).id,viewname:"all"}},serviceRequest:{name:"serviceRequestSummary",params:{id:id,viewname:"all"}},client:{name:"clientSummary",params:{id:id,viewname:"all"}},vendorcontact:{name:"vendorContactsSummary",params:{id:id,viewname:"all"}},insurance:{name:"insurancesSummary",params:{id:id,viewname:"all"}},quote:{path:"/app/tm/quotation/all/".concat(id,"/overview")},item:{path:"/app/inventory/item/all/".concat(id,"/summary")},tool:{path:"/app/inventory/tool/all/".concat(id,"/summary")}};if(isCustomModule)route=this.$router.resolve({path:"/app/ca/modules/".concat(moduleName,"/all/").concat(id,"/summary")}).href;else{var moduleRouterObj=routerMap[moduleName]||null;route=moduleRouterObj&&this.$router.resolve(routerMap[moduleName]).href}}route&&window.open(route,"_blank")}}},line_items_RelatedListvue_type_script_lang_js=RelatedListvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(line_items_RelatedListvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,RelatedList=component.exports},
/***/99228:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return RelatedListPopup}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/page/widget/common/line-items/RelatedListPopup.vue?vue&type=template&id=b0e1e94c
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("el-dialog",{staticClass:"related-list-popup",attrs:{visible:!0,"append-to-body":!0,"before-close":_vm.cancel,"custom-class":"related-list-popup-dialog"},scopedSlots:_vm._u([{key:"title",fn:function(){return[_c("div",{staticClass:"related-list-popup-title-header"},[_c("div",{staticClass:"related-list-popup-title-text"},[_vm._v(_vm._s(_vm.title))]),_c("div",{staticClass:"related-list-popup-title-header-filter"},[_c("div",{staticClass:"related-list-popup-title-header-filter-inbuilt"},[_vm.config.canHideSearch?_vm._e():_c("MainFieldSearch",{key:"search-"+_vm.wizardModuleName,staticClass:"related-list-popup-title-header-filter-inbuilt-mainField",attrs:{search:_vm.searchText,mainFieldObj:_vm.mainFieldObj},on:{"update:search":function($event){_vm.searchText=$event},onSearch:function($event){return _vm.loadRecords(!0)}}}),_vm.canShowPagination?_c("span",{staticClass:"related-list-popup-separator"},[_vm._v("|")]):_vm._e(),_vm.canShowPagination?_c("Pagination",{key:"pagination-"+_vm.wizardModuleName,attrs:{total:_vm.recordCount,currentPage:_vm.page,currentPageCount:(_vm.recordList||[]).length,perPage:_vm.perPage},on:{"update:currentPage":function($event){_vm.page=$event},"update:current-page":function($event){_vm.page=$event}}}):_vm._e()],1)])])]},proxy:!0}])},[_c("div",{staticClass:"related-list-popup-container"},[_vm.isEmpty(_vm.appliedFilter)?_vm._e():_c("div",{staticClass:"related-list-popup-filter-ftags"},[_c("div",{staticClass:"related-list-popup-filter-text"},[_vm._v(" "+_vm._s(_vm.$t("commissioning.sheet.filter"))+" ")]),_c("FTags",{key:"ftag-"+_vm.wizardModuleName,staticClass:"flex-shrink-0",attrs:{moduleName:_vm.wizardModuleName,filters:_vm.appliedFilter,hideQuery:!0,hideSaveAs:!0},on:{updateFilters:_vm.setAppliedfilter,clearFilters:function($event){return _vm.setAppliedfilter({})}}})],1),_vm.showLoading?_c("spinner",{attrs:{show:_vm.showLoading,size:"80"}}):_vm.isEmpty(_vm.recordList)?_c("div",{staticClass:"related-list-popup-empty-state"},[_c("inline-svg",{attrs:{src:"svgs/list-empty",iconClass:"icon icon-130"}}),_c("div",{staticClass:"related-list-popup-empty-state-text"},[_vm._v(" "+_vm._s(_vm.$t("common.products.no_module_available",{moduleName:_vm.wizardModuleDisplayName||_vm.wizardModuleName}))+" ")])],1):_c("div",{staticClass:"related-list-table"},[_c("CommonList",{key:"related-list-table-"+_vm.wizardModuleName,ref:"relatedListTable",attrs:{viewDetail:_vm.viewDetail,records:_vm.recordList,moduleName:_vm.wizardModuleName,slotList:_vm.slotList,hideListSelect:!0,tableRowClick:_vm.tableRowClick,hideGlimpse:!0},on:{mainField:_vm.mainfieldAction}})],1)],1)])},staticRenderFns=[],LineItemWizard=(__webpack_require__(425728),__webpack_require__(260228),__webpack_require__(514739)),MainFieldSearch=__webpack_require__(212078),validation=__webpack_require__(990260),RelatedListPopupvue_type_script_lang_js={extends:LineItemWizard/* default */.Z,components:{MainFieldSearch:MainFieldSearch/* default */.Z},data:function(){return{searchText:null}},computed:{slotList:function(){return[]},title:function(){var wizardModuleDisplayName=this.wizardModuleDisplayName,wizardModuleName=this.wizardModuleName;return wizardModuleDisplayName||wizardModuleName},mainFieldObj:function(){var _ref=this.viewDetail||{},fields=_ref.fields,_ref2=(fields||[]).find((function(viewFld){var _viewFld$field;return null===viewFld||void 0===viewFld||null===(_viewFld$field=viewFld.field)||void 0===_viewFld$field?void 0:_viewFld$field.mainField}))||{},mainField=_ref2.field;return mainField||{}},mainfieldAction:function(){var _ref3=this.config||{},mainfieldAction=_ref3.mainfieldAction;return(0,validation/* isFunction */.mf)(mainfieldAction)?mainfieldAction:function(){}}}},line_items_RelatedListPopupvue_type_script_lang_js=RelatedListPopupvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(line_items_RelatedListPopupvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,RelatedListPopup=component.exports},
/***/212078:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return MainFieldSearch}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/newapp/components/search/MainFieldSearch.vue?vue&type=template&id=e007b35a
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _vm.canShowMainFieldSearch?_c("div",[_vm.showMainFieldSearch?_c("el-input",{ref:"mainFieldSearchInput",staticClass:"fc-input-full-border2 width-auto mL-auto",attrs:{size:"mini",placeholder:_vm.$t("common._common.search")},on:{blur:_vm.hideMainFieldSearch,change:_vm.emitSearchText},model:{value:_vm.searchText,callback:function($$v){_vm.searchText=$$v},expression:"searchText"}}):_c("span",{on:{click:_vm.openSearch}},[_c("InlineSvg",{staticClass:"d-flex cursor-pointer",attrs:{src:"svgs/search",iconClass:"icon icon-sm search-icon"}})],1)],1):_vm._e()},staticRenderFns=[],validation=__webpack_require__(990260),MainFieldSearchvue_type_script_lang_js={props:["mainFieldObj","debounceTime","search"],data:function(){return{showMainFieldSearch:!1,searchText:null}},computed:{canShowMainFieldSearch:function(){var _ref=this.mainFieldObj||{},dataTypeEnum=_ref.dataTypeEnum;return"STRING"===dataTypeEnum}},watch:{search:{handler:function(newVal){newVal!==this.searchText&&(this.searchText=newVal),this.hideMainFieldSearch()},immediate:!0}},methods:{openSearch:function(){var _this=this;this.showMainFieldSearch=!0,this.$nextTick((function(){var searchBox=_this.$refs["mainFieldSearchInput"];!(0,validation/* isEmpty */.xb)(searchBox)&&searchBox.focus()}))},hideMainFieldSearch:function(){(0,validation/* isEmpty */.xb)(this.searchText)&&(this.showMainFieldSearch=!1)},emitSearchText:function(){this.$emit("update:search",this.searchText),this.$emit("onSearch",this.searchText)}// clearSearch() {
//   this.emitSearchText()
//   this.hideMainFieldSearch()
// },
}},search_MainFieldSearchvue_type_script_lang_js=MainFieldSearchvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(search_MainFieldSearchvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,MainFieldSearch=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/27849.js.map