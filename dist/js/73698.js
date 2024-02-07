"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[73698,32355],{
/***/674369:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return PortalApprovalActivities}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/PortalTenant/approvals/PortalApprovalActivities.vue?vue&type=template&id=c7926ab6
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("PageLayout",{scopedSlots:_vm._u([{key:"views",fn:function(){return[_c("ApprovalViews",{attrs:{moduleName:_vm.moduleName,isActivityView:!0,showEditIcon:!1,pathPrefix:_vm.pathPrefix,canShowViewsSidePanel:_vm.canShowViewsSidePanel},on:{"update:canShowViewsSidePanel":function($event){_vm.canShowViewsSidePanel=$event},"update:can-show-views-side-panel":function($event){_vm.canShowViewsSidePanel=$event}}})]},proxy:!0},{key:"header",fn:function(){return[_vm.totalCount?_c("pagination",{staticClass:"pL15 fc-black-small-txt-12",attrs:{total:_vm.totalCount,perPage:_vm.perPage}}):_vm._e()]},proxy:!0}])},[_c("div",{staticClass:"width100"},[_c("FTags",{key:"ftags-list-"+_vm.moduleName,attrs:{hideSaveView:!0}})],1),_c("div",{class:["f-list-view height100vh",{m10:_vm.loading}]},[_vm.loading?_c("div",{staticClass:"full-layout-white text-center height100vh"},[_c("spinner",{attrs:{show:_vm.loading,size:"80"}})],1):_vm._e(),_vm.$validation.isEmpty(_vm.activities)?_c("div",{staticClass:"flex-middle width100 m10 justify-center shadow-none white-bg-block flex-direction-column approval-activity-container"},[_c("InlineSvg",{attrs:{src:"svgs/emptystate/history",iconClass:"icon icon-xxxxlg mR10"}}),_c("div",{staticClass:"nowo-label text-center pT10"},[_vm._v(" "+_vm._s(_vm.$t("asset.history.no_history_available"))+" ")])],1):_c("div",{staticClass:"fc-list-view fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser p10 pB100 approval-activities"},[_c("el-table",{ref:"tableList",staticClass:"width100",attrs:{data:_vm.activities,height:"auto",fit:!0,"row-class-name":"activity-row no-hover"}},[_c("el-table-column",{attrs:{fixed:"",prop:"",label:"Time",width:"200px"},scopedSlots:_vm._u([{key:"default",fn:function(data){return[_c("div",[_vm._v(_vm._s(_vm._f("formatDate")(data.row.ttime)))])]}}])}),_c("el-table-column",{attrs:{fixed:"",prop:"",label:"User",width:"220px"},scopedSlots:_vm._u([{key:"default",fn:function(data){return[_c("avatar",{attrs:{size:"sm",user:{name:_vm.getUserName(data.row.doneBy.ouid)}}}),_vm._v(" "+_vm._s(_vm.getUserName(data.row.doneBy.ouid))+" ")]}}])}),_c("el-table-column",{attrs:{prop:"",label:"Action","min-width":"320px"},scopedSlots:_vm._u([{key:"default",fn:function(data){return[_c("div",[_c("span",{domProps:{innerHTML:_vm._s(_vm.getActivityMessage(data.row).message)}})])]}}])}),_c("el-table-column",{attrs:{prop:"",label:"Approval Process",width:"230px"},scopedSlots:_vm._u([{key:"default",fn:function(data){return[_vm._v(" "+_vm._s(_vm.getRuleName(data.row))+" ")]}}])}),_c("el-table-column",{attrs:{prop:"",label:_vm.getPrimaryFieldName(_vm.moduleName),"min-width":"350px"},scopedSlots:_vm._u([{key:"default",fn:function(data){return[_c("div",{staticClass:"position-relative"},[_c("div",{staticClass:"ellipsis textoverflow-ellipsis"},[_vm._v(" "+_vm._s(_vm.getModulePrimaryField(data.row))+" ")]),_c("a",{staticClass:"summary-link pointer",on:{click:function($event){return _vm.goToModuleSummary({id:data.row.parentId})}}},[_vm._v(" "+_vm._s(_vm.$t("common.header.open"))+" "+_vm._s(_vm.$t("common._common.summary"))+" "),_c("inline-svg",{attrs:{src:"svgs/new-tab",iconClass:"icon vertical-middle icon-sm mL3"}})],1)])]}}])})],1)],1)])])},staticRenderFns=[],ApprovalActivities=__webpack_require__(770098),PageLayout=__webpack_require__(125434),PortalApprovalViews=__webpack_require__(7237),PortalApprovalActivitiesvue_type_script_lang_js={extends:ApprovalActivities["default"],components:{PageLayout:PageLayout/* default */.Z,ApprovalViews:PortalApprovalViews/* default */.Z}},approvals_PortalApprovalActivitiesvue_type_script_lang_js=PortalApprovalActivitiesvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(approvals_PortalApprovalActivitiesvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,PortalApprovalActivities=component.exports},
/***/7237:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return PortalApprovalViews}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/PortalTenant/approvals/PortalApprovalViews.vue?vue&type=template&id=60f102d5
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"flex-middle mT17"},[_c("div",{staticClass:"flex-middle flex-direction-row"},_vm._l(_vm.filteredViews.list,(function(view,index){return _c("a",{key:view.name+index,class:[view.name===_vm.currentView&&"router-link-exact-active active"],on:{click:function($event){return _vm.goToView(view,_vm.currentGroup)}}},[_c("div",{staticClass:"label-txt-black pointer mR30"},[_vm._v(" "+_vm._s(view.label)+" "),_c("div",{staticClass:"portal-active-tab"})])])})),0),_vm.$validation.isEmpty(_vm.filteredViews.more)||_vm.showCurrentViewOnly?_vm._e():_c("el-dropdown",{staticClass:"mT3 pointer more-views-dropdown",attrs:{trigger:"click"},on:{command:_vm.goToView}},[_c("span",{staticClass:"el-dropdown-link"},[_c("inline-svg",{attrs:{src:"svgs/header-more"}})],1),_c("el-dropdown-menu",{attrs:{slot:"dropdown"},slot:"dropdown"},_vm._l(_vm.filteredViews.more,(function(item,index){return _c("el-dropdown-item",{key:item.name+index,attrs:{command:item}},[_vm._v(" "+_vm._s(item.label)+" ")])})),1)],1)],1)},staticRenderFns=[],ApprovalHeader=__webpack_require__(493887),PortalApprovalViewsvue_type_script_lang_js={extends:ApprovalHeader/* default */.Z},approvals_PortalApprovalViewsvue_type_script_lang_js=PortalApprovalViewsvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(approvals_PortalApprovalViewsvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,PortalApprovalViews=component.exports},
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
/* harmony default export */,ViewSideBar=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/73698.js.map