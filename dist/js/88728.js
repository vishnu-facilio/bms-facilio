"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[88728,38254],{
/***/125434:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return PageLayout}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/PortalTenant/components/PageLayout.vue?vue&type=template&id=057f2152
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"portal-layout"},[_c("div",{staticClass:"portal-layout-header"},[_c("div",{staticClass:"header-details"},[_c("div",{staticClass:"tab-header"},[_c("div",{staticClass:"tab-name"},[_vm._v(" "+_vm._s(_vm.currentTab.name)+" ")]),_c("div",{staticClass:"header-section"},[_vm._t("header")],2)]),_c("div",{staticClass:"d-flex align-center justify-content-space mT10 height30"},[_c("div",[_vm._t("views",(function(){return[_c("ViewHeader",{attrs:{moduleName:_vm.moduleName,canShowViewsSidePanel:_vm.canShowViewsSidePanel,groupViews:_vm.groupViews,getRoute:_vm.getRoute,retainFilters:_vm.retainFilters},on:{"update:canShowViewsSidePanel":function($event){_vm.canShowViewsSidePanel=$event},"update:can-show-views-side-panel":function($event){_vm.canShowViewsSidePanel=$event},onChange:_vm.goToView}})]}))],2),_c("div",{staticClass:"d-flex align-center"},[_vm._t("header-2")],2)])])]),_c("div",{staticClass:"portal-layout-container"},[_vm._t("views-list",(function(){return[_vm.canShowViewsSidePanel?_c("ViewSideBar",{attrs:{canShowViewsSidePanel:_vm.canShowViewsSidePanel,groupViews:_vm.groupViews,moduleName:_vm.moduleName},on:{"update:canShowViewsSidePanel":function($event){_vm.canShowViewsSidePanel=$event},"update:can-show-views-side-panel":function($event){_vm.canShowViewsSidePanel=$event},onChange:_vm.goToView}}):_vm._e()]})),_c("div",{staticClass:"module-list-container"},[_vm._t("default")],2)],2)])},staticRenderFns=[],regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),objectSpread2=__webpack_require__(595082),vuex_esm=(__webpack_require__(434284),__webpack_require__(976801),__webpack_require__(843843),__webpack_require__(425728),__webpack_require__(260228),__webpack_require__(564043),__webpack_require__(857267),__webpack_require__(420629)),validation=__webpack_require__(990260),router=__webpack_require__(329435),ViewSideBar=__webpack_require__(65865),ViewHeaderWithoutGroupsvue_type_template_id_54f371f0_render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"view-header-wg",on:{click:_vm.toggleViewsSidePanel}},[_c("inline-svg",{class:["hamburger-icon",_vm.canShowViewsSidePanel&&"active"],attrs:{src:"svgs/hamburger-menu",iconClass:"icon icon-sm"}}),_vm.$validation.isEmpty(_vm.currentViewDisplayName)?_c("div",{staticClass:"view-name-loading loading-shimmer"}):_c("div",{staticClass:"view-name"},[_vm._v(_vm._s(_vm.currentViewDisplayName))])],1)},ViewHeaderWithoutGroupsvue_type_template_id_54f371f0_staticRenderFns=[],ViewHeadervue_type_template_id_031ebc9c_render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"flex-middle mT17"},[_vm.canShowOpenViewSideBar?[_c("inline-svg",{class:["portal-view-header-burger-icon",_vm.canShowViewsSidePanel&&"active"],attrs:{src:"svgs/hamburger-menu",iconClass:"icon icon-sm"},nativeOn:{click:function($event){return _vm.openViewsSidePanel.apply(null,arguments)}}}),_c("div",{staticClass:"grp-view-name"},[_vm._v(" "+_vm._s(_vm.currentGroup?_vm.currentGroup.displayName:"")+" ")]),_vm.$validation.isEmpty(_vm.groupViews)?_vm._e():_c("div",{staticClass:"fc-separator-lg mL30 mR30"})]:_vm._e(),_c("div",{staticClass:"flex-middle flex-direction-row"},[_vm._l(_vm.activeViews,(function(view,index){return _c("router-link",{key:view.name+index,attrs:{to:_vm.getRoute(view)},scopedSlots:_vm._u([{key:"default",fn:function(ref){var navigate=ref.navigate;return[_c("a",{class:[view.name===_vm.currentView&&"router-link-exact-active active"],on:{click:navigate}},[_c("div",{staticClass:"label-txt-black pointer mR30"},[_vm._v(" "+_vm._s(view.displayName)+" "),_c("div",{staticClass:"portal-active-tab"})])])]}}],null,!0)})})),_vm.$validation.isEmpty(_vm.moreViews)?_vm._e():_c("el-dropdown",{staticClass:"mT3 pointer more-views-dropdown",attrs:{trigger:"click"},on:{command:_vm.goToView}},[_c("span",{staticClass:"el-dropdown-link"},[_c("inline-svg",{attrs:{src:"svgs/header-more"}})],1),_c("el-dropdown-menu",{attrs:{slot:"dropdown"},slot:"dropdown"},_vm._l(_vm.moreViews,(function(view,index){return _c("el-dropdown-item",{key:view.name+index,attrs:{command:view}},[_vm._v(" "+_vm._s(view.displayName)+" ")])})),1)],1)],2)],2)},ViewHeadervue_type_template_id_031ebc9c_staticRenderFns=[],slicedToArray=__webpack_require__(554621),ViewHeadervue_type_script_lang_js=(__webpack_require__(339772),__webpack_require__(689730),__webpack_require__(670560),__webpack_require__(450886),__webpack_require__(538077),{props:["canShowViewsSidePanel","moduleName","groupViews","getRoute"],computed:{canShowOpenViewSideBar:function(){var groupViews=this.groupViews;return!(0,validation/* isEmpty */.xb)(groupViews)},activeViews:function(){var currentGroup=this.currentGroup,currentView=this.currentView,_ref=currentGroup||{},views=_ref.views,maxViewCount=3,activeViews=[];if(!(0,validation/* isEmpty */.xb)(views)){var currentViewIdx=(views||[]).findIndex((function(view){return view.name===currentView}));currentViewIdx<maxViewCount?activeViews=views.slice(0,maxViewCount):(activeViews=views.slice(0,maxViewCount-1),activeViews.push(views[currentViewIdx]))}return activeViews},moreViews:function(){var currentGroup=this.currentGroup,activeViews=this.activeViews,_ref2=currentGroup||{},views=_ref2.views,activeViewNames=(activeViews||[]).map((function(view){return view.name}));return(views||[]).filter((function(view){return!activeViewNames.includes(view.name)}))||[]},currentView:function(){return this.$route.params.viewname},currentGroup:function(){var _this=this,groupViews=this.groupViews,selectedGroup=(groupViews||[]).find((function(group){var _ref3=group||{},views=_ref3.views,selectedView=(views||[]).find((function(v){return v.name===_this.currentView}));return!(0,validation/* isEmpty */.xb)(selectedView)}));return selectedGroup}},methods:{openViewsSidePanel:function(){this.$emit("update:canShowViewsSidePanel",!this.canShowViewsSidePanel)},openGroup:function(command){var group=(this.groupViews||[]).find((function(g){return g.displayName===command})),_ref4=group||{},views=_ref4.views,_ref5=views||[],_ref6=(0,slicedToArray/* default */.Z)(_ref5,1),initialView=_ref6[0];!(0,validation/* isEmpty */.xb)(initialView)&&this.goToView(initialView)},goToView:function(view){this.$emit("onChange",view)}}}),components_ViewHeadervue_type_script_lang_js=ViewHeadervue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_ViewHeadervue_type_script_lang_js,ViewHeadervue_type_template_id_031ebc9c_render,ViewHeadervue_type_template_id_031ebc9c_staticRenderFns,!1,null,null,null)
/* harmony default export */,ViewHeader=component.exports,ViewHeaderWithoutGroupsvue_type_script_lang_js={extends:ViewHeader,computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapGetters */.Se)({getCurrentViewDetail:"view/getCurrentViewDetail"})),{},{currentViewDisplayName:function(){var currentGroup=this.currentGroup,_this$currentView=this.currentView,currentView=void 0===_this$currentView?"":_this$currentView,currentViewDetail=this.currentViewDetail,_ref=currentGroup||{},currentViewList=_ref.views,_ref2=(currentViewList||[]).find((function(view){return view.name===currentView}))||{},displayName=_ref2.displayName;return displayName||(null===currentViewDetail||void 0===currentViewDetail?void 0:currentViewDetail.displayName)||""},currentViewDetail:function(){return this.getCurrentViewDetail()}}),methods:{toggleViewsSidePanel:function(){this.$emit("update:canShowViewsSidePanel",!this.canShowViewsSidePanel)}}},components_ViewHeaderWithoutGroupsvue_type_script_lang_js=ViewHeaderWithoutGroupsvue_type_script_lang_js,ViewHeaderWithoutGroups_component=(0,componentNormalizer/* default */.Z)(components_ViewHeaderWithoutGroupsvue_type_script_lang_js,ViewHeaderWithoutGroupsvue_type_template_id_54f371f0_render,ViewHeaderWithoutGroupsvue_type_template_id_54f371f0_staticRenderFns,!1,null,null,null)
/* harmony default export */,ViewHeaderWithoutGroups=ViewHeaderWithoutGroups_component.exports,debounce=__webpack_require__(23279),debounce_default=__webpack_require__.n(debounce),LIST=router/* pageTypes */.As.LIST,PageLayoutvue_type_script_lang_js={props:["moduleName"],components:{ViewSideBar:ViewSideBar/* default */.Z,ViewHeader:ViewHeaderWithoutGroups},data:function(){return{canShowViewsSidePanel:!1}},computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapState */.rn)("view",{groupViews:function(state){return(0,validation/* isEmpty */.xb)(state.groupViews)?[]:state.groupViews},metaInfo:function(state){return state.metaInfo}})),(0,vuex_esm/* mapState */.rn)("webtabs",{currentTab:function(state){return state.selectedTab}})),{},{currentView:function(){return this.$route.params.viewname},retainFilters:function(){return["search","includeParentFilter"]}}),watch:{moduleName:{handler:function(newVal,oldVal){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var _ref,name;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:if(_ref=_this.metaInfo||{},name=_ref.name,[oldVal,name].includes(newVal)||(0,validation/* isEmpty */.xb)(newVal)){_context.next=5;break}return _context.next=4,_this.$store.dispatch("view/clearViews");case 4:_this.loadViews();case 5:case"end":return _context.stop()}}),_callee)})))()},immediate:!0},currentView:{handler:function(value){
// If current view is removed from URL, loadViews again and navigate to first group
(0,validation/* isEmpty */.xb)(value)?this.loadViews():this.loadViewDetails()},immediate:!0}},methods:{loadViews:debounce_default()((0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee2(){return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context2){while(1)switch(_context2.prev=_context2.next){case 0:return _context2.abrupt("return",this.$store.dispatch("view/loadGroupViews",{moduleName:this.moduleName}).then(this.initViews));case 1:case"end":return _context2.stop()}}),_callee2,this)}))),200),loadViewDetails:debounce_default()((0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee3(){var viewName,moduleName;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context3){while(1)switch(_context3.prev=_context3.next){case 0:return viewName=this.currentView,moduleName=this.moduleName,_context3.abrupt("return",this.$store.dispatch("view/loadViewDetail",{viewName:viewName,moduleName:moduleName}).catch((function(){})));case 2:case"end":return _context3.stop()}}),_callee3,this)}))),200),initViews:function(){if(!this.currentView){var _ref4=this.groupViews.find((function(group){return!(0,validation/* isEmpty */.xb)(group.views)}))||{},views=_ref4.views;(0,validation/* isEmpty */.xb)(views)||this.goToView(views[0])}},goToView:function(view){var route=this.getRoute(view);this.$router.replace(route)},getRoute:function(view){if(!(0,validation/* isEmpty */.xb)(view&&view.name)){var _ref5=this.$route||{},query=_ref5.query,id=this.currentTab.id,_ref6=(0,router/* findRouteForTab */.OD)(id,{pageType:LIST})||{},_ref6$name=_ref6.name,name=void 0===_ref6$name?null:_ref6$name;return name?{name:name,params:{viewname:view.name},query:query}:null}}}},components_PageLayoutvue_type_script_lang_js=PageLayoutvue_type_script_lang_js,PageLayout_component=(0,componentNormalizer/* default */.Z)(components_PageLayoutvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,PageLayout=PageLayout_component.exports},
/***/65865:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return ViewSideBar}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/PortalTenant/components/ViewSideBar.vue?vue&type=template&id=2be7bd24
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("transition",{attrs:{name:"fade"}},[_c("div",{staticClass:"portal-view-sidebar-fixed"},[_c("div",{staticClass:"side-bar-view"},[_vm._v(" "+_vm._s(_vm.$t("common._common.views"))+" ")]),_c("div",{staticClass:"portal-view-search-bar"},[_c("el-input",{staticClass:"fc-input-full-border2",attrs:{placeholder:"Search",type:"search","prefix-icon":"el-icon-search"},model:{value:_vm.searchText,callback:function($$v){_vm.searchText=$$v},expression:"searchText"}})],1),_c("div",{staticClass:"portal-view-sidebar"},_vm._l(_vm.filteredList,(function(group,grpIndex){return _c("div",{key:group.id+"_"+grpIndex,class:[_vm.$validation.isEmpty(_vm.searchText)&&"mB10"]},[_vm.$validation.isEmpty(group.views)?_vm._e():[_vm.$validation.isEmpty(_vm.searchText)?_c("div",{staticClass:"view-grp-name"},[_vm._v(" "+_vm._s(group.displayName||group.name)+" ")]):_vm._e(),_vm._l(group.views,(function(view,index){return _c("div",{key:index,staticClass:"view-item",class:_vm.isActiveView(view)?"active":"",on:{click:function($event){return _vm.changeView(view,group)}}},[_c("el-tooltip",{attrs:{content:view.displayName,placement:"top",manual:view.displayName.length<25,"open-delay":1e3}},[_c("div",{staticClass:"ellipsis"},[_vm._v(" "+_vm._s(view.displayName)+" ")])])],1)}))]],2)})),0)])])},staticRenderFns=[],ViewsSidePanel=__webpack_require__(409886),ViewSideBarvue_type_script_lang_js={extends:ViewsSidePanel/* default */.Z},components_ViewSideBarvue_type_script_lang_js=ViewSideBarvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_ViewSideBarvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,ViewSideBar=component.exports},
/***/588728:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return ClientPortalQuotationList}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/PortalTenant/custom-module/ClientPortalQuotationList.vue?vue&type=template&id=48936a83
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("PageLayout",{attrs:{moduleName:_vm.moduleName}},[_c("template",{slot:"title"},[_vm._v(" "+_vm._s(_vm.moduleDisplayName)+" ")]),_c("template",{slot:"header"},[_vm.isEmpty(_vm.viewname)?_vm._e():[_c("AdvancedSearchWrapper",{key:"ftags-list-"+_vm.moduleName,attrs:{filters:_vm.filters,moduleName:_vm.moduleName,moduleDisplayName:_vm.moduleDisplayName,hideSaveView:!0}})],_vm.isBulkActionLicenseEnabled?_c("CustomButton",{key:_vm.moduleName+"_"+_vm.viewname+"_"+_vm.POSITION.LIST_TOP,attrs:{moduleName:_vm.moduleName,position:_vm.POSITION.LIST_TOP},on:{onSuccess:_vm.onCustomButtonSuccess}}):_vm._e(),_c("CreateButton",{on:{click:_vm.redirectToFormCreation}},[_vm._v(" "+_vm._s(_vm.createBtnText)+" ")])],2),_c("template",{slot:"header-2"},[_vm.isEmpty(_vm.viewname)?_vm._e():[_c("pagination",{attrs:{total:_vm.recordCount,perPage:_vm.perPage,currentPageCount:_vm.currentPageCount}}),_vm.recordCount>0?_c("span",{staticClass:"separator"},[_vm._v("|")]):_vm._e(),_c("el-tooltip",{attrs:{effect:"dark",content:_vm.$t("common._common.sort"),placement:"bottom"}},[_c("Sort",{key:_vm.moduleName+"-sort",attrs:{moduleName:_vm.moduleName},on:{onSortChange:_vm.updateSort}})],1),_vm.hasPermission("EXPORT")?_c("span",{staticClass:"separator"},[_vm._v("|")]):_vm._e(),_vm.hasPermission("EXPORT")?_c("el-tooltip",{attrs:{effect:"dark",content:_vm.$t("common._common.export"),"open-delay":500,placement:"top",tabindex:-1}},[_c("FExportSettings",{attrs:{module:_vm.moduleName,viewDetail:_vm.viewDetail,showViewScheduler:!1,showMail:!1,filters:_vm.filters}})],1):_vm._e()]],2),_vm.showLoading?_c("div",{staticClass:"list-loading"},[_c("spinner",{attrs:{show:_vm.showLoading,size:"80"}})],1):_vm.isEmpty(_vm.viewname)?_c("div",{staticClass:"portal-view-empty-state-container"},[_c("inline-svg",{staticClass:"d-flex module-view-empty-state",attrs:{src:"svgs/no-configuration",iconClass:"icon"}}),_c("div",{staticClass:"line-height20 nowo-label"},[_vm._v(" "+_vm._s(_vm.$t("viewsmanager.list.no_view_config"))+" ")])],1):_vm.isEmpty(_vm.records)?_c("div",{staticClass:"list-empty-state"},[_c("inline-svg",{attrs:{src:"svgs/emptystate/workorder",iconClass:"icon text-center icon-xxxxlg height-auto"}}),_c("div",{staticClass:"line-height20 nowo-label"},[_vm._v(" "+_vm._s(_vm.emptyStateText)+" ")])],1):_c("div",{staticClass:"portal-common-list"},[_vm.isBulkActionLicenseEnabled&&!_vm.$validation.isEmpty(_vm.selectedListItemsIds)?_c("div",{staticClass:"portal-table-header-actions"},[_vm.canShowDelete?_c("button",{staticClass:"portal-bulk-action-delete",on:{click:function($event){return _vm.deleteRecords(_vm.selectedListItemsIds)}}},[_vm._v(" "+_vm._s(_vm.$t("custommodules.list.delete"))+" ")]):_vm._e(),_c("CustomButton",{key:_vm.moduleName+"_"+_vm.viewname+"_"+_vm.POSITION.LIST_BAR,attrs:{selectedRecords:_vm.selectedListItemsObj,moduleName:_vm.moduleName,position:_vm.POSITION.LIST_BAR},on:{onSuccess:_vm.onCustomButtonSuccess}})],1):_vm._e(),_c("CommonList",{attrs:{moduleName:_vm.moduleName,viewDetail:_vm.viewDetail,records:_vm.records,columnConfig:_vm.columnConfig,slotList:_vm.slotList,redirectToOverview:_vm.redirectToOverview,refreshList:_vm.onCustomButtonSuccess,canShowCustomButton:_vm.isBulkActionLicenseEnabled,hideListSelect:!_vm.isBulkActionLicenseEnabled,specialFieldDisplayValue:_vm.specialFieldDisplayValue},on:{"selection-change":_vm.selectItems},scopedSlots:_vm._u([{key:_vm.slotList[0].name,fn:function(ref){var record=ref.record;return[_c("div",{staticClass:"d-flex"},[_c("div",{staticClass:"fc-id"},[_vm._v(_vm._s("#"+record[_vm.slotList[0].name]))])])]}},{key:_vm.slotList[1].criteria,fn:function(ref){var record=ref.record;return[_c("router-link",{staticClass:"d-flex fw5 label-txt-black ellipsis main-field-column",attrs:{to:_vm.redirectToOverview(record.id)}},[record[_vm.photoFieldName]>0?_c("div",[_c("img",{staticClass:"img-container",attrs:{src:record.getImage(_vm.photoFieldName)}})]):_vm._e(),_c("el-tooltip",{attrs:{effect:"dark",content:_vm.$getProperty(record,_vm.mainFieldName,"---")||"---",placement:"top-end","open-delay":600}},[_c("div",{staticClass:"self-center width200px"},[_c("span",{staticClass:"list-main-field"},[_vm._v(" "+_vm._s(_vm.$getProperty(record,_vm.mainFieldName,"---")||"---")+" ")])])])],1)]}},_vm.hasActionPermissions?{key:_vm.slotList[2].name,fn:function(ref){var record=ref.record;return[_c("div",{staticClass:"d-flex text-center"},[_vm.canShowEdit&&record.canEdit?_c("i",{directives:[{name:"tippy",rawName:"v-tippy"}],staticClass:"el-icon-edit pointer edit-icon-color visibility-hide-actions mL10",attrs:{"data-arrow":"true",title:_vm.$t("common._common.edit")},on:{click:function($event){return _vm.editModule(record)}}}):_vm._e(),_vm.canShowDelete?_c("i",{directives:[{name:"tippy",rawName:"v-tippy"}],staticClass:"el-icon-delete pointer edit-icon-color visibility-hide-actions mL10",attrs:{"data-arrow":"true",title:_vm.$t("common._common.delete")},on:{click:function($event){return _vm.deleteRecords([record.id])}}}):_vm._e()])]}}:null],null,!0)})],1)],2)},staticRenderFns=[],ModuleList=(__webpack_require__(976801),__webpack_require__(843843),__webpack_require__(434284),__webpack_require__(634338),__webpack_require__(138254)),validation=__webpack_require__(990260),ClientPortalQuotationListvue_type_script_lang_js={extends:ModuleList["default"],methods:{specialFieldDisplayValue:function(_ref){var field=_ref.field,value=_ref.value;if(this.$constants.currencyFieldsList.includes(field.name)){var val=(0,validation/* isEmpty */.xb)(value)||"---"===value?"0":this.$d3.format(",.2f")(value);return["$","₹"].includes(this.$currency)?this.$currency+val:"".concat(val," ").concat(this.$currency)}return value}}},custom_module_ClientPortalQuotationListvue_type_script_lang_js=ClientPortalQuotationListvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(custom_module_ClientPortalQuotationListvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,ClientPortalQuotationList=component.exports},
/***/138254:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return ModuleList}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/PortalTenant/custom-module/ModuleList.vue?vue&type=template&id=8baa5a96&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("PageLayout",{attrs:{moduleName:_vm.moduleName}},[_c("template",{slot:"title"},[_vm._v(" "+_vm._s(_vm.moduleDisplayName)+" ")]),_c("template",{slot:"header"},[_vm.isEmpty(_vm.viewname)?_vm._e():[_c("AdvancedSearchWrapper",{key:"ftags-list-"+_vm.moduleName,attrs:{filters:_vm.filters,moduleName:_vm.moduleName,moduleDisplayName:_vm.moduleDisplayName,hideSaveView:!0}})],_vm.isBulkActionLicenseEnabled?_c("CustomButton",{key:_vm.moduleName+"_"+_vm.viewname+"_"+_vm.POSITION.LIST_TOP,attrs:{moduleName:_vm.moduleName,position:_vm.POSITION.LIST_TOP},on:{onSuccess:_vm.onCustomButtonSuccess}}):_vm._e(),_c("CreateButton",{on:{click:_vm.redirectToFormCreation}},[_vm._v(" "+_vm._s(_vm.createBtnText)+" ")])],2),_c("template",{slot:"header-2"},[_vm.isEmpty(_vm.viewname)?_vm._e():[_c("pagination",{attrs:{total:_vm.recordCount,perPage:_vm.perPage,currentPageCount:_vm.currentPageCount}}),_vm.recordCount>0?_c("span",{staticClass:"separator"},[_vm._v("|")]):_vm._e(),_c("el-tooltip",{attrs:{effect:"dark",content:_vm.$t("common._common.sort"),placement:"bottom"}},[_c("Sort",{key:_vm.moduleName+"-sort",attrs:{moduleName:_vm.moduleName},on:{onSortChange:_vm.updateSort}})],1),_vm.hasPermission("EXPORT")?_c("span",{staticClass:"separator"},[_vm._v("|")]):_vm._e(),_vm.hasPermission("EXPORT")?_c("el-tooltip",{attrs:{effect:"dark",content:_vm.$t("common._common.export"),"open-delay":500,placement:"top",tabindex:-1}},[_c("FExportSettings",{attrs:{module:_vm.moduleName,viewDetail:_vm.viewDetail,showViewScheduler:!1,showMail:!1,filters:_vm.filters}})],1):_vm._e()]],2),_vm.showLoading?_c("div",{staticClass:"list-loading"},[_c("spinner",{attrs:{show:_vm.showLoading,size:"80"}})],1):_vm.isEmpty(_vm.viewname)?_c("div",{staticClass:"portal-view-empty-state-container"},[_c("inline-svg",{staticClass:"d-flex module-view-empty-state",attrs:{src:"svgs/no-configuration",iconClass:"icon"}}),_c("div",{staticClass:"line-height20 nowo-label"},[_vm._v(" "+_vm._s(_vm.$t("viewsmanager.list.no_view_config"))+" ")])],1):_vm.isEmpty(_vm.records)?_c("div",{staticClass:"list-empty-state"},[_c("inline-svg",{attrs:{src:"svgs/emptystate/workorder",iconClass:"icon text-center icon-xxxxlg height-auto"}}),_c("div",{staticClass:"line-height20 nowo-label"},[_vm._v(" "+_vm._s(_vm.emptyStateText)+" ")])],1):_c("div",{staticClass:"portal-common-list"},[_vm.isBulkActionLicenseEnabled&&!_vm.$validation.isEmpty(_vm.selectedListItemsIds)?_c("div",{staticClass:"portal-table-header-actions"},[_vm.canShowDelete?_c("button",{staticClass:"portal-bulk-action-delete",on:{click:function($event){return _vm.deleteRecords(_vm.selectedListItemsIds)}}},[_vm._v(" "+_vm._s(_vm.$t("custommodules.list.delete"))+" ")]):_vm._e(),_c("CustomButton",{key:_vm.moduleName+"_"+_vm.viewname+"_"+_vm.POSITION.LIST_BAR,attrs:{selectedRecords:_vm.selectedListItemsObj,moduleName:_vm.moduleName,position:_vm.POSITION.LIST_BAR},on:{onSuccess:_vm.onCustomButtonSuccess}})],1):_vm._e(),_c("CommonList",{attrs:{moduleName:_vm.moduleName,viewDetail:_vm.viewDetail,records:_vm.records,columnConfig:_vm.columnConfig,slotList:_vm.slotList,redirectToOverview:_vm.redirectToOverview,refreshList:_vm.onCustomButtonSuccess,canShowCustomButton:_vm.isBulkActionLicenseEnabled,hideListSelect:!_vm.isBulkActionLicenseEnabled},on:{"selection-change":_vm.selectItems},scopedSlots:_vm._u([{key:_vm.slotList[0].name,fn:function(ref){var record=ref.record;return[_c("div",{staticClass:"d-flex"},[_c("div",{staticClass:"fc-id"},[_vm._v(_vm._s("#"+record[_vm.slotList[0].name]))])])]}},{key:_vm.slotList[1].criteria,fn:function(ref){var record=ref.record;return[_c("router-link",{staticClass:"d-flex fw5 label-txt-black ellipsis main-field-column",attrs:{to:_vm.redirectToOverview(record.id)}},[record[_vm.photoFieldName]>0?_c("div",[_c("img",{staticClass:"img-container",attrs:{src:record.getImage(_vm.photoFieldName)}})]):_vm._e(),_c("el-tooltip",{attrs:{effect:"dark",content:_vm.$getProperty(record,_vm.mainFieldName,"---")||"---",placement:"top-end","open-delay":600}},[_c("div",{staticClass:"self-center width200px"},[_c("span",{staticClass:"list-main-field"},[_vm._v(" "+_vm._s(_vm.$getProperty(record,_vm.mainFieldName,"---")||"---")+" ")])])])],1)]}},_vm.hasActionPermissions?{key:_vm.slotList[2].name,fn:function(ref){var record=ref.record;return[_c("div",{staticClass:"d-flex text-center"},[_vm.canShowEdit&&record.canEdit?_c("i",{directives:[{name:"tippy",rawName:"v-tippy"}],staticClass:"el-icon-edit pointer edit-icon-color visibility-hide-actions mL10",attrs:{"data-arrow":"true",title:_vm.$t("common._common.edit")},on:{click:function($event){return _vm.editModule(record)}}}):_vm._e(),_vm.canShowDelete?_c("i",{directives:[{name:"tippy",rawName:"v-tippy"}],staticClass:"el-icon-delete pointer edit-icon-color visibility-hide-actions mL10",attrs:{"data-arrow":"true",title:_vm.$t("common._common.delete")},on:{click:function($event){return _vm.deleteRecords([record.id])}}}):_vm._e()])]}}:null],null,!0)})],1)],2)},staticRenderFns=[],toConsumableArray=__webpack_require__(488478),objectSpread2=__webpack_require__(595082),CommonModuleList=(__webpack_require__(538077),__webpack_require__(260228),__webpack_require__(450886),__webpack_require__(434284),__webpack_require__(976801),__webpack_require__(843843),__webpack_require__(634338),__webpack_require__(347446)),PageLayout=__webpack_require__(125434),CreateButtonvue_type_template_id_ae030fbc_render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _vm.hasPermission?_c("el-button",{staticClass:"fc-create-btn create-btn uppercase",staticStyle:{background:"#ef508f"},on:{click:_vm.handleClick}},[_vm.$validation.isEmpty(_vm.buttonDisplayName)?_vm._t("default"):[_vm._v(" "+_vm._s(_vm.buttonDisplayName)+" ")]],2):_vm._e()},CreateButtonvue_type_template_id_ae030fbc_staticRenderFns=[],vuex_esm=(__webpack_require__(670560),__webpack_require__(420629)),validation=__webpack_require__(990260),CreateButtonvue_type_script_lang_js={props:["to"],computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapState */.rn)("webtabs",{currentTab:function(state){return state.selectedTab}})),(0,vuex_esm/* mapGetters */.Se)("webtabs",["tabHasPermission"])),{},{hasPermission:function(){var currentTab=this.currentTab,tabHasPermission=this.tabHasPermission;return tabHasPermission("CREATE",currentTab)},buttonDisplayName:function(){var currentTab=this.currentTab,configJSON=currentTab.configJSON,_ref=configJSON||{},actionName=_ref.actionName;return actionName}}),methods:{handleClick:function(){(0,validation/* isEmpty */.xb)(this.to)||this.$router.push(this.to),this.$emit("click")}}},components_CreateButtonvue_type_script_lang_js=CreateButtonvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_CreateButtonvue_type_script_lang_js,CreateButtonvue_type_template_id_ae030fbc_render,CreateButtonvue_type_template_id_ae030fbc_staticRenderFns,!1,null,null,null)
/* harmony default export */,CreateButton=component.exports,AdvancedSearchWrapper=__webpack_require__(207001),ModuleListvue_type_script_lang_js={extends:CommonModuleList["default"],components:{PageLayout:PageLayout/* default */.Z,CreateButton:CreateButton,AdvancedSearchWrapper:AdvancedSearchWrapper/* default */.Z},data:function(){return{columnConfig:{fixedColumns:["id","name"],fixedSelectableColumns:["photo"],availableColumns:[],showLookupColumns:!1}}},computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapState */.rn)("webtabs",{currentTab:function(state){return state.selectedTab}})),(0,vuex_esm/* mapGetters */.Se)("webtabs",["tabHasPermission"])),{},{mainFieldName:function(){return this.mainFieldNamesList[0]||"name"},mainFieldNamesList:function(){var _ref=this.metaInfo||{},fields=_ref.fields,mainFields=(fields||[]).filter((function(fld){return fld.mainField})),mainFieldsNames=(mainFields||[]).map((function(fld){return fld.name}));return mainFieldsNames},canShowEdit:function(){var currentTab=this.currentTab,tabHasPermission=this.tabHasPermission;return tabHasPermission("UPDATE",currentTab)},canShowDelete:function(){var currentTab=this.currentTab,tabHasPermission=this.tabHasPermission;return tabHasPermission("DELETE",currentTab)},hasActionPermissions:function(){var canShowEdit=this.canShowEdit,canShowDelete=this.canShowDelete;return canShowEdit||canShowDelete},hasPagination:function(){var currentPageCount=this.currentPageCount,recordCount=this.recordCount;return!(0,validation/* isEmpty */.xb)(currentPageCount)&&parseInt(currentPageCount)>0||!(0,validation/* isEmpty */.xb)(recordCount)&&parseInt(recordCount)>0},isBulkActionLicenseEnabled:function(){return this.$helpers.isLicenseEnabled("BULK_ACTION_IN_PORTAL")}}),watch:{mainFieldNamesList:{handler:function(newVal){if(!(0,validation/* isEmpty */.xb)(newVal)){var _ref2=this.columnConfig||{},fixedColumns=_ref2.fixedColumns,newFixedCol=newVal.filter((function(fldName){return!fixedColumns.includes(fldName)}));fixedColumns=[].concat((0,toConsumableArray/* default */.Z)(fixedColumns),(0,toConsumableArray/* default */.Z)(newFixedCol)),this.$set(this.columnConfig,"fixedColumns",fixedColumns)}},immediate:!0}}},custom_module_ModuleListvue_type_script_lang_js=ModuleListvue_type_script_lang_js,ModuleList_component=(0,componentNormalizer/* default */.Z)(custom_module_ModuleListvue_type_script_lang_js,render,staticRenderFns,!1,null,"8baa5a96",null)
/* harmony default export */,ModuleList=ModuleList_component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/88728.js.map