"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[32447],{
/***/947748:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return FSearch}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/FSearch.vue?vue&type=template&id=aca29d08
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"f-search-container mR20"},[void 0!=_vm.boolval?_c("div",[_c("el-input",{ref:"quickSearchQuery",staticClass:"fc-input-full-border-h35 mT15 mR10 wo-assigned-search",attrs:{autofocus:!0,placeholder:"Search","prefix-icon":"el-icon-search"},nativeOn:{keyup:function($event){return!$event.type.indexOf("key")&&_vm._k($event.keyCode,"enter",13,$event.key,"Enter")?null:_vm.quickSearch.apply(null,arguments)}},model:{value:_vm.quickSearchQuery,callback:function($$v){_vm.quickSearchQuery=$$v},expression:"quickSearchQuery"}},[_vm.quickSearchQuery?_c("i",{staticClass:"el-icon-close pointer",attrs:{slot:"suffix"},on:{click:_vm.closeSearch},slot:"suffix"}):_vm._e()])],1):_vm._e(),1==_vm.showQuickSearch&&void 0==_vm.boolval&&void 0==_vm.remote?_c("div",[_c("el-input",{directives:[{name:"show",rawName:"v-show",value:_vm.showQuickSearch,expression:"showQuickSearch"}],ref:"quickSearchQuery",staticClass:"f-quick-search-input",attrs:{autofocus:!0,placeholder:"Search","prefix-icon":"el-icon-search"},nativeOn:{keyup:function($event){return!$event.type.indexOf("key")&&_vm._k($event.keyCode,"enter",13,$event.key,"Enter")?null:_vm.quickSearch.apply(null,arguments)}},model:{value:_vm.quickSearchQuery,callback:function($$v){_vm.quickSearchQuery=$$v},expression:"quickSearchQuery"}},[_c("i",{staticClass:"el-icon-close pointer",attrs:{slot:"suffix"},on:{click:_vm.closeSearch},slot:"suffix"})])],1):_vm._e(),1==_vm.showQuickSearch&&void 0==_vm.boolval&&1==_vm.remote?_c("div",[_c("el-input",{directives:[{name:"show",rawName:"v-show",value:_vm.showQuickSearch,expression:"showQuickSearch"}],ref:"quickSearchQuery",staticClass:"f-quick-search-input ",attrs:{autofocus:!0,placeholder:"Search","prefix-icon":"el-icon-search"},on:{change:_vm.remoteSearch},model:{value:_vm.quickSearchQuery,callback:function($$v){_vm.quickSearchQuery=$$v},expression:"quickSearchQuery"}},[_c("i",{staticClass:"el-icon-close pointer",attrs:{slot:"suffix"},on:{click:_vm.closeSearch},slot:"suffix"})])],1):void 0==_vm.boolval&&_vm.searchHide?_c("div",{staticClass:"pointer flRight",on:{click:_vm.toggleQuickSearch}},[_c("i",{staticClass:"el-icon-search"})]):_vm._e()])},staticRenderFns=[],FSearchvue_type_script_lang_js=(__webpack_require__(538077),__webpack_require__(260228),{props:["value","searchKey","","boolval","remote"],data:function(){return{showQuickSearch:!1,quickSearchQuery:null,initialList:null,searchHide:!0}},beforeDestroy:function(){this.closeSearch()},mounted:function(){void 0!=this.boolval&&(this.showQuickSearch=this.boolval)},methods:{quickSearch:function(){var list,_this=this;this.value&&(this.quickSearchQuery?(this.initialList||(this.initialList=this.$helpers.cloneObject(this.value)),list=this.initialList.filter((function(item){return item[_this.searchKey||"name"].toLowerCase().indexOf(_this.quickSearchQuery.toLowerCase())>=0}))):(list=this.initialList,this.initialList=null),list&&this.$emit("input",list))},remoteSearch:function(){this.$emit("search",this.quickSearchQuery)},toggleQuickSearch:function(){var _this2=this;this.searchHide=!1,this.showQuickSearch=!this.showQuickSearch,this.showQuickSearch&&this.$nextTick((function(){_this2.$refs.quickSearchQuery&&_this2.$refs.quickSearchQuery.focus()}))},clearSearch:function(){this.showQuickSearch=!1,this.quickSearchQuery=null,this.searchHide=!0},closeSearch:function(){void 0===this.boolval&&this.toggleQuickSearch(),this.quickSearchQuery=null,this.remote?this.remoteSearch():this.quickSearch(),this.searchHide=!0,this.showQuickSearch=!1,this.$emit("close")}}}),components_FSearchvue_type_script_lang_js=FSearchvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_FSearchvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,FSearch=component.exports},
/***/928885:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return ExistingUserForm}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/setup/portal/ExistingUserForm.vue?vue&type=template&id=4c1ce376
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _vm.canShowUsersList?_c("el-dialog",{staticClass:"fc-dialog-center-container fc-dialog-center-body-p0 scale-up-center fc-dialog-header-hide",attrs:{visible:!0,width:"50%","before-close":_vm.close,"append-to-body":!0}},[_c("div",{staticClass:"new-header-container pL20 pR0 height60px line-height30"},[_c("div",{staticClass:"new-header-modal"},[_c("div",{staticClass:"new-header-text flex-middle justify-content-space height30"},[_c("div",{staticClass:"setup-modal-title"},[_vm._v(" "+_vm._s(_vm.$t("common._common.add_existing_user"))+" ")]),_c("div",{staticClass:"fR fc-subheader-right flex-middle"},[_c("div",{staticClass:"flex-middle position-relative mR10"},[_vm.userSearchIcon?_c("i",{staticClass:"el-icon-search pointer fw6 fc-black3-16",on:{click:_vm.showUserSearch}}):_vm._e(),_vm.showUserSearchInput?_c("div",{staticClass:"flex-middle"},[_c("el-input",{staticClass:"fc-input-full-border2",attrs:{placeholder:"Search"},on:{change:_vm.activeUserSearch},model:{value:_vm.activeUserSearchData,callback:function($$v){_vm.activeUserSearchData=$$v},expression:"activeUserSearchData"}}),_c("div",[_c("i",{staticClass:"el-icon-close fc-close-icon-search pointer",on:{click:_vm.clearUser}})])],1):_vm._e()]),_c("pagination",{ref:"pagination",attrs:{total:_vm.userListCount,perPage:50,currentPage:_vm.page},on:{"update:currentPage":function($event){_vm.page=$event},"update:current-page":function($event){_vm.page=$event}}})],1)])])]),_vm.loading?_c("div",{staticClass:"flex-middle height450"},[_c("spinner",{attrs:{show:_vm.loading,size:"80"}})],1):_vm._e(),_vm.$validation.isEmpty(_vm.userList)&&!_vm.loading?_c("div",{staticClass:"height450 flex-middle justify-content-center flex-direction-column"},[_c("inline-svg",{attrs:{src:"svgs/emptystate/readings-empty",iconClass:"icon text-center icon-xxxxlg"}}),_c("div",{staticClass:"nowo-label"},[_vm._v(" "+_vm._s(_vm.$t("common._common.no_users_available"))+" ")])],1):_vm._e(),_vm.loading||_vm.$validation.isEmpty(_vm.userList)?_vm._e():_c("div",{staticStyle:{"padding-bottom":"52px"}},[_c("el-table",{ref:"elTable",staticClass:"fc-table-th-pLalign-reduce",staticStyle:{width:"100%"},attrs:{data:_vm.userList,height:"450"}},[_c("el-table-column",{attrs:{prop:"name",label:_vm.$t("common.products.name"),width:"270"},scopedSlots:_vm._u([{key:"default",fn:function(data){return[_c("el-radio",{staticClass:"fc-radio-btn",attrs:{label:data.row},model:{value:_vm.selectedUser,callback:function($$v){_vm.selectedUser=$$v},expression:"selectedUser"}},[_vm._v(" "+_vm._s(data.row.name)+" ")])]}}],null,!1,901707198)}),_c("el-table-column",{attrs:{prop:"email",label:_vm.$t("common.header.email"),width:"250"},scopedSlots:_vm._u([{key:"default",fn:function(data){return[_vm._v(" "+_vm._s(data.row.email)+" ")]}}],null,!1,3513958705)}),_c("el-table-column",{attrs:{prop:"phone",label:_vm.$t("common.header.phone"),width:"200"},scopedSlots:_vm._u([{key:"default",fn:function(data){return[_vm._v(" "+_vm._s(data.row.phone?data.row.phone:"---")+" ")]}}],null,!1,3666873135)})],1)],1),_c("div",{staticClass:"dialog-footer"},[_c("div",{staticClass:"modal-dialog-footer"},[_c("el-button",{staticClass:"modal-btn-cancel",on:{click:function($event){return _vm.close()}}},[_vm._v(_vm._s(_vm.$t("common._common.cancel")))]),_c("el-button",{staticClass:"modal-btn-save",attrs:{type:"primary"},on:{click:function($event){return _vm.next()}}},[_vm._v(" "+_vm._s(_vm.$t("common._common.next"))+" ")])],1)])]):_vm.canShowRolesList?_c("el-dialog",{staticClass:"fc-dialog-center-container fc-dialog-center-body-p0 scale-up-center fc-dialog-header-hide",attrs:{visible:!0,width:"35%","before-close":_vm.close,"append-to-body":!0}},[_c("div",{staticClass:"p30 pB100"},[_c("p",{staticClass:"fc-input-label-txt pB10"},[_vm._v(" "+_vm._s(_vm.$t("common.products.user"))+" ")]),_c("el-select",{staticClass:"fc-input-full-border-select2 width100",attrs:{disabled:""},model:{value:_vm.selectedUser.id,callback:function($$v){_vm.$set(_vm.selectedUser,"id",$$v)},expression:"selectedUser.id"}},_vm._l(_vm.userList,(function(user){return _c("el-option",{key:user.id,attrs:{label:user.name,value:user.id}})})),1),_c("p",{staticClass:"fc-input-label-txt pT20 pB10"},[_vm._v(" "+_vm._s(_vm.$t("common.wo_report.role"))+" ")]),_c("el-select",{staticClass:"fc-input-full-border-select2 width100",attrs:{filterable:"",clearable:""},model:{value:_vm.selectedRoleId,callback:function($$v){_vm.selectedRoleId=$$v},expression:"selectedRoleId"}},_vm._l(_vm.rolesList,(function(role){return _c("el-option",{key:role.roleId,attrs:{label:role.name,value:role.roleId}})})),1)],1),_c("div",{staticClass:"dialog-footer"},[_c("div",{staticClass:"modal-dialog-footer"},[_c("el-button",{staticClass:"modal-btn-cancel",on:{click:function($event){return _vm.back()}}},[_vm._v(_vm._s(_vm.$t("common.header.back")))]),_c("el-button",{staticClass:"modal-btn-save",attrs:{loading:_vm.saving,disabled:_vm.$validation.isEmpty(_vm.selectedRoleId),type:"primary"},on:{click:function($event){return _vm.saveRecord()}}},[_vm._v(" "+_vm._s(_vm.saving?_vm.$t("common.products.adding"):_vm.$t("common._common.add"))+" ")])],1)])]):_vm._e()},staticRenderFns=[],toConsumableArray=__webpack_require__(488478),regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),FSearch=(__webpack_require__(260228),__webpack_require__(76265),__webpack_require__(456646),__webpack_require__(821694),__webpack_require__(450886),__webpack_require__(538077),__webpack_require__(434284),__webpack_require__(947748)),api=__webpack_require__(32284),WidgetPagination=__webpack_require__(806339),ExistingUserFormvue_type_script_lang_js={props:["app"],data:function(){return{saving:!1,userListCount:"",loading:!0,userList:[],selectedUser:null,selectedRoleId:null,canShowUsersList:!0,canShowRolesList:!1,page:1,showUserSearchInput:!1,activeUserSearchData:"",userSearchIcon:!0}},created:function(){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return _this.loading=!0,_context.next=3,_this.loadApplicationUserCount();case 3:return _context.next=5,_this.loadApplicationUser();case 5:return _context.next=7,_this.loadRolesForApp();case 7:_this.loading=!1;case 8:case"end":return _context.stop()}}),_callee)})))()},components:{FSearch:FSearch/* default */.Z,Pagination:WidgetPagination/* default */.Z},computed:{appId:function(){return this.app.id}},watch:{page:function(newVal,oldVal){oldVal!=newVal&&this.loadApplicationUser()}},methods:{loadApplicationUserCount:function(){var _this2=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee2(){var search;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context2){while(1)switch(_context2.prev=_context2.next){case 0:
// let userFilters = {}
// if (searchQuery) {
//   userFilters.name = { operatorId: 5, value: [searchQuery] }
// }
_this2.activeUserSearchData&&(search=_this2.activeUserSearchData),api/* API */.bl.get("/v2/application/users/list?fetchCount=true",{appId:_this2.appId||_this2.$route.params.id,fetchNonAppUsers:!0,search:search}).then((function(_ref){var data=_ref.data,error=_ref.error;error?_this2.$message.error(error.message||"Error Occured"):_this2.userListCount=data.count}));case 2:case"end":return _context2.stop()}}),_callee2)})))()},loadApplicationUser:function(){var _this3=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee3(){var search,_yield$API$get,error,data;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context3){while(1)switch(_context3.prev=_context3.next){case 0:
// let userFilters = {}
// if (searchQuery) {
//   userFilters.name = { operatorId: 5, value: [searchQuery] }
// }
return _this3.activeUserSearchData&&(search=_this3.activeUserSearchData),_this3.loading=!0,_context3.next=4,api/* API */.bl.get("/v2/application/users/list?page=".concat(_this3.page,"&perPage=50"),{appId:_this3.appId||_this3.$route.params.id,fetchNonAppUsers:!0,search:search});case 4:_yield$API$get=_context3.sent,error=_yield$API$get.error,data=_yield$API$get.data,error?_this3.userList=[]:(_this3.userList=data.users||[],_this3.userList=(0,toConsumableArray/* default */.Z)(new Map(_this3.userList.map((function(user){return[user["uid"],user]}))).values())),_this3.loading=!1;case 9:case"end":return _context3.stop()}}),_callee3)})))()},activeUserSearch:function(){this.loadApplicationUserCount(),this.loadApplicationUser(),this.page=1},searchUsers:function(searchQuery){this.page=1,this.loadApplicationUserCount(searchQuery),this.loadApplicationUser(searchQuery)},loadRolesForApp:function(){var _this4=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee4(){var appId,_yield$API$get2,error,data;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context4){while(1)switch(_context4.prev=_context4.next){case 0:return appId=_this4.appId,_context4.next=3,api/* API */.bl.get("/setup/roles",{appId:appId});case 3:_yield$API$get2=_context4.sent,error=_yield$API$get2.error,data=_yield$API$get2.data,error?_this4.$message.error(error.message||"Error Occured"):_this4.rolesList=(data.roles||[]).filter((function(role){return"Super Administrator"!==role.name}));case 7:case"end":return _context4.stop()}}),_callee4)})))()},next:function(){this.canShowUsersList=!1,this.canShowRolesList=!0},back:function(){this.canShowRolesList=!1,this.canShowUsersList=!0},close:function(){this.$emit("close")},saveRecord:function(){var _this5=this;this.saving=!0;var selectedRoleId=this.selectedRoleId,selectedUser=this.selectedUser,userData={appId:this.appId||this.$route.params.id,user:{id:selectedUser.id,email:selectedUser.email,roleId:selectedRoleId}};api/* API */.bl.post("/v2/application/users/add",userData).then((function(_ref2){var error=_ref2.error;error?_this5.$message.error(error.message||"Error Occurred"):(_this5.$message(_this5.$t("common.header.existing_user_added")),_this5.$emit("saved"),_this5.$store.dispatch("loadUsers",!0))})).finally((function(){_this5.saving=!1,_this5.close()}))},showUserSearch:function(){this.showUserSearchInput=!0,this.userSearchIcon=!1},clearUser:function(){this.showUserSearchInput=!1,this.userSearchIcon=!0,this.activeUserSearchData=null,this.activeUserSearch()}}},portal_ExistingUserFormvue_type_script_lang_js=ExistingUserFormvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(portal_ExistingUserFormvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,ExistingUserForm=component.exports},
/***/15954:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return ApplicationSummary}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/setup/portal/v1/ApplicationSummary.vue?vue&type=template&id=0054c682
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"height100vh"},[_c("setupSummaryHeader",{scopedSlots:_vm._u([{key:"summaryLeftSide",fn:function(){return[_c("div",[_c("div",{staticClass:"flex-middle fc-setup-breadcrumb"},[_c("div",{staticClass:"fc-setup-breadcrumb-inner pointer",on:{click:_vm.setupHomeRoute}},[_vm._v(" "+_vm._s(_vm.$t("common.products.home"))+" ")]),_c("div",{staticClass:"fc-setup-breadcrumb-inner pL10 pR10"},[_c("i",{staticClass:"el-icon-arrow-right f14 fwBold"})]),_c("div",{staticClass:"fc-setup-breadcrumb-inner pointer",on:{click:_vm.setupUsersRoute}},[_vm._v(" "+_vm._s(_vm.application.name)+" ")]),_c("div",{staticClass:"fc-setup-breadcrumb-inner pL10 pR10"},[_c("i",{staticClass:"el-icon-arrow-right f14 fwBold"})]),_c("div",{staticClass:"fc-breadcrumbBold-active"},[_vm._v(" "+_vm._s(_vm.$t("setup.portal.summary"))+" ")])]),_c("div",{staticClass:"setting-title-block flex-middle justify-content-space"},[_c("div",{staticClass:"flex-middle pT15"},[_c("div",{staticClass:"fc-portal-icon fc-portal-icon-common-bg flex-center-vH align-center"},[_c("inline-svg",{attrs:{src:"svgs/apps/agent",iconClass:"icon icon-xl fill-white"}})],1),_c("div",{staticClass:"visibility-visible-actions"},[_c("div",{staticClass:"fc-grey-txt18 pL10 pB5"},[_vm._v(" "+_vm._s(_vm.$t("common._common.appname_summary",{name:_vm.application.name}))+" ")]),_c("div",{staticClass:"flex-middle"},[_c("a",{staticClass:"fc-app-link-color pL10 f14",attrs:{href:"https://"+_vm.application.appDomain.domain+"/"+_vm.application.linkName,target:"_blank"}},[_vm._v(" "+_vm._s("https://"+_vm.application.appDomain.domain+"/"+_vm.application.linkName)+" ")]),_c("div",{staticClass:"pointer pL10 visibility-hide-actions",on:{click:function($event){return _vm.copyLinkName("https://"+_vm.application.appDomain.domain+"/"+_vm.application.linkName)}}},[_c("inline-svg",{attrs:{src:"svgs/link-copy",iconClass:"icon icon-sm-md vertical-bottom op5"}})],1)])])])])])]},proxy:!0},{key:"summaryRightSide",fn:function(){return[_c("portal-target",{staticClass:"portal-summary",attrs:{name:"header-buttons"}})]},proxy:!0}])}),_c("div",{staticClass:"fc-main d-flex relative"},[_vm.showSidebar?_c("setupSummarySidebar",{scopedSlots:_vm._u([{key:"setupsidebar",fn:function(){return _vm._l(_vm.sidebarMenuList,(function(sidebar,list){return _c("div",{key:list},_vm._l(sidebar.menuList,(function(headingName,inex){return _c("div",{key:inex},[_c("div",{staticClass:"fc-pink f14 bold pB10"},[_vm._v(" "+_vm._s(headingName.name)+" ")]),_vm._l(headingName.submenuList,(function(subMenu,iex){return _c("div",{key:iex},[_c("div",{staticClass:"label-txt-black fc-list-label pointer",class:{isActive:_vm.sidebarTabList==subMenu.link},on:{click:function($event){return _vm.showHideMenu(subMenu.link)}}},[_c("InlineSvg",{attrs:{src:subMenu.icon,iconClass:subMenu.iconClass}}),_vm._v(" "+_vm._s(subMenu.name)+" ")],1)])}))],2)})),0)}))},proxy:!0}],null,!1,1733647257)}):_vm._e(),_c("setupSummaryMainSection",{class:{widthChangeCon:0==_vm.showSidebar},scopedSlots:_vm._u([{key:"setupMain",fn:function(){return[_c("div",{directives:[{name:"tippy",rawName:"v-tippy",value:{arrow:!0,arrowType:"round"},expression:"{ arrow: true, arrowType: 'round' }"}],staticClass:"fc-open-arrow pointer",attrs:{content:"Open Sidebar"},on:{click:_vm.showArrow}},[_vm.showSideBarOpen?_c("i",{staticClass:"el-icon-d-arrow-right f18"}):_vm._e()]),"users"===_vm.sidebarTabList?[_c("div",{staticClass:"fc-portal-inner-summary-header flex-middle justify-content-space height50 fc-application-summary-search"},[_c("div",{staticClass:"fc-black2-18 text-left bold"},[_vm._v(" "+_vm._s(_vm.$t("setup.users_management.users"))+" ")]),_c("div",{staticClass:"flex-middle"},[_c("pagination",{ref:"pagination",staticClass:"flex-middle justify-content-end p0 pL10",attrs:{total:_vm.userlistCount,perPage:_vm.perPage,currentPage:_vm.page},on:{"update:currentPage":function($event){_vm.page=$event},"update:current-page":function($event){_vm.page=$event}}}),_c("div",{directives:[{name:"tippy",rawName:"v-tippy",value:{arrow:!0,arrowType:"round",animation:"fade"},expression:"{\n                  arrow: true,\n                  arrowType: 'round',\n                  animation: 'fade',\n                }"}],staticClass:"fc-portal-filter-border",class:{filterActive:_vm.showFilter},attrs:{content:"Advanced filters"},on:{click:_vm.showExtraFilter}},[_c("inline-svg",{staticClass:"pointer",attrs:{src:"svgs/dashboard/filter",iconClass:"icon icon-md fc-fill-path-grey"}}),_c("div",{class:{"dot-active-pink":_vm.activeUserSearchData}})],1)],1)]),_vm.showFilter?_c("div",{staticClass:"fc-show-filter-con relative"},[_c("div",{staticClass:"flex-middle"},[_c("div",{staticClass:"relative"},[_c("div",{staticClass:"fc-black-13 text-left bold pB5"},[_vm._v("Search")]),_c("el-input",{staticClass:"fc-input-full-border2 width280px",attrs:{placeholder:"Search name or email"},on:{change:_vm.activeUserSearch},model:{value:_vm.activeUserSearchData,callback:function($$v){_vm.activeUserSearchData=$$v},expression:"activeUserSearchData"}})],1)])]):_vm._e(),_c("div",{staticClass:"occupantPortal-tab"},[_vm.loading?_c("setup-loader",{staticClass:"m10 width98",scopedSlots:_vm._u([{key:"setupLoading",fn:function(){return[_c("spinner",{attrs:{show:_vm.loading,size:"80"}})]},proxy:!0}],null,!1,3199698086)}):_vm.$validation.isEmpty(_vm.userlist)&&!_vm.loading?_c("setup-empty",{staticClass:"m10 width98",scopedSlots:_vm._u([{key:"emptyImage",fn:function(){return[_c("inline-svg",{attrs:{src:"svgs/copy2",iconClass:"icon icon-sm-md"}})]},proxy:!0},{key:"emptyHeading",fn:function(){return[_vm._v(" "+_vm._s(_vm.$t("setup.empty.empty_user"))+" ")]},proxy:!0}],null,!1,731712003)}):_c("div",{staticClass:"occupantPortal-tabrow"},[_c("div",{staticClass:"col-lg-12 col-md-12"},[_c("table",{staticClass:"setting-list-view-table",attrs:{width:"100%"}},[_c("thead",[_c("tr",[_c("th",{staticClass:"setting-table-th setting-th-text uppercase"},[_vm._v(" "+_vm._s(_vm.$t("setup.approvalprocess.name"))+" ")]),_c("th",{staticClass:"setting-table-th setting-th-text uppercase"},[_vm._v(" "+_vm._s(_vm.$t("setup.setup_profile.email"))+" ")]),_c("th",{staticClass:"setting-table-th setting-th-text uppercase"},[_vm._v(" "+_vm._s(_vm.$t("setup.setup_profile.phone"))+" ")]),_c("th",{staticClass:"setting-table-th setting-th-text uppercase"},[_vm._v(" "+_vm._s(_vm.$t("common.roles.role"))+" ")]),_c("th",{staticClass:"setting-table-th setting-th-text"})])]),_c("tbody",_vm._l(_vm.userlist,(function(user,index){return _c("tr",{key:index,staticClass:"tablerow visibility-visible-actions",on:{click:function($event){return _vm.openSummary(user)}}},[_c("td",[_c("user-avatar",{staticClass:"width200px",attrs:{size:"md",user:user}})],1),_c("td",[_vm._v(" "+_vm._s(user.email?user.email:"---")+" ")]),_c("td",[_vm._v(" "+_vm._s(user.phone?user.phone:"---")+" ")]),_c("td",[_vm._v(" "+_vm._s(_vm.getRoleName(user))+" ")]),_c("td",{staticClass:"width200px pL0 pR0 nowrap"},[_c("i",{staticClass:"visibility-hide-actions el-icon-edit fc-setup-list-edit",on:{click:function($event){return _vm.editUser(user)}}}),_c("i",{staticClass:"visibility-hide-actions el-icon-delete fc-setup-list-delete",on:{click:function($event){return _vm.deleteUser(user)}}})])])})),0)])])]),"Users"===_vm.activeName?_c("portal",{attrs:{to:"header-buttons"}},[_c("div",{staticClass:"position-relative"},[_c("el-dropdown",{on:{command:_vm.addNewForm}},[_c("el-button",{staticClass:"setup-el-btn",attrs:{type:"primary"}},[_vm._v(" "+_vm._s(_vm.$t("common.products.add_user"))+" "),_c("i",{staticClass:"el-icon-arrow-down el-icon--right"})]),_c("el-dropdown-menu",{attrs:{slot:"dropdown"},slot:"dropdown"},[_c("el-dropdown-item",{attrs:{command:"new"}},[_vm._v(_vm._s(_vm.$t("common.products.new_user")))]),_c("el-dropdown-item",{attrs:{command:"existing"}},[_vm._v(" "+_vm._s(_vm.$t("common.header.existing_user"))+" ")])],1)],1)],1)]):_vm._e()],1)]:"roles"===_vm.sidebarTabList?[_c("div",{staticClass:"fc-portal-inner-summary-header"},[_c("div",{staticClass:"fc-black2-18 text-left bold"},[_vm._v(" "+_vm._s(_vm.$t("setup.setup.roles"))+" ")])]),_c("portalRoles",{staticClass:"portal-summary",attrs:{app:_vm.application,isActiveRoles:"roles"===_vm.sidebarTabList}})]:"tabsLayout"===_vm.sidebarTabList?[_c("TabsAndLayouts",{staticClass:"portal-summary fc-occupant-portal-tabs tabs-layout-table",attrs:{appId:_vm.appId,isActive:"tabsLayout"===_vm.sidebarTabList}})]:_vm._e()]},proxy:!0}])})],1),_vm.showDialog?_c("UserForm",{attrs:{isNew:_vm.isNew,formType:"workCenter",user:_vm.currentUser,app:_vm.application,save:_vm.saveUser},on:{onClose:function($event){_vm.showDialog=!1}}}):_vm._e(),_vm.showExistingDialog?_c("AddExistingUser",{attrs:{app:_vm.application},on:{close:function($event){_vm.showExistingDialog=!1},saved:_vm.reloadUser}}):_vm._e()],1)},staticRenderFns=[],objectSpread2=__webpack_require__(595082),regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),api=(__webpack_require__(670560),__webpack_require__(634338),__webpack_require__(434284),__webpack_require__(821057),__webpack_require__(564043),__webpack_require__(857267),__webpack_require__(538077),__webpack_require__(260228),__webpack_require__(32284)),ExistingUserForm=__webpack_require__(928885),User=__webpack_require__(426803),PortalUserForm=__webpack_require__(466919),TabsAndLayouts=__webpack_require__(161693),pick=__webpack_require__(478718),pick_default=__webpack_require__.n(pick),vuex_esm=__webpack_require__(420629),SetupSummaryMainSection=__webpack_require__(825550),SetupSummarySidebar=__webpack_require__(426998),SetupSummaryHeader=__webpack_require__(202313),SetupLoader=__webpack_require__(233432),SetupEmptyState=__webpack_require__(622957),PortalRoles=__webpack_require__(543354),router=__webpack_require__(329435),WidgetPagination=__webpack_require__(806339),ApplicationSummaryvue_type_script_lang_js={props:["application"],components:{UserAvatar:User/* default */.Z,UserForm:PortalUserForm/* default */.Z,AddExistingUser:ExistingUserForm/* default */.Z,TabsAndLayouts:TabsAndLayouts/* default */.Z,setupSummaryMainSection:SetupSummaryMainSection/* default */.Z,setupSummarySidebar:SetupSummarySidebar/* default */.Z,setupSummaryHeader:SetupSummaryHeader/* default */.Z,SetupLoader:SetupLoader/* default */.Z,SetupEmpty:SetupEmptyState/* default */.Z,portalRoles:PortalRoles/* default */.Z,Pagination:WidgetPagination/* default */.Z},title:function(){return"Application User List"},data:function(){return{activeName:"Users",showDialog:!1,loading:!0,showExistingDialog:!1,userlist:[],isNew:!1,activeUserSearchData:"",currentUser:null,sidebarTabList:["users","roles","tabsLayout"],showSidebar:!0,showSideBarOpen:!1,showUserSearchInput:!1,userSearchIcon:!0,showFilter:!1,page:1,perPage:50,userlistCount:"",rolesListData:null,rolesList:[],statusData:"all",statusList:[{label:"All Users",value:"all"},{label:"Active Users",value:"active"},{label:"Pending Users",value:"pending"}],sidebarMenuList:[{menuList:[{name:"Manage",submenuList:[{name:"Users",link:"users",icon:"svgs/user-new1",iconClass:"icon icon-md op66 mR13"},{name:"Roles",link:"roles",icon:"svgs/roles-new",iconClass:"icon icon-xl op66 mR10 vertical-text-bottom fc-role-icon-align"}]},{name:"Customization",submenuList:[{name:"Tabs And Layouts",link:"tabsLayout",icon:"svgs/tabs",iconClass:"icon icon-sm-md op66 mR14 vertical-bottom"}]}]}]}},created:function(){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return _context.next=2,_this.$store.dispatch("loadRoles");case 2:_this.loadApplicationUser(),_this.loadRolesForApp(),_this.sidebarTabList="users",_this.loadAppicationCount();case 6:case"end":return _context.stop()}}),_callee)})))()},computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapGetters */.Se)(["getRoleNameById"])),{},{appId:function(){return this.application.id},appLinkName:function(){var appName=(0,router/* getApp */.wx)().linkName;return appName}}),watch:{page:function(newVal,oldVal){oldVal!=newVal&&this.loadApplicationUser()}},methods:{openSummary:function(user){this.$router.push({name:"userSummary",params:{appName:this.appLinkName,id:user.id,appId:this.appId}})},back:function(){this.$router.go(-1)},addNewForm:function(cmd){"new"===cmd?(this.currentUser=null,this.isNew=!0,this.showDialog=!0):"existing"===cmd&&(this.showExistingDialog=!0)},loadApplicationUser:function(){var _arguments=arguments,_this2=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee2(){var force,appId,search;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context2){while(1)switch(_context2.prev=_context2.next){case 0:return force=!(_arguments.length>0&&void 0!==_arguments[0])||_arguments[0],appId=_this2.appId,_this2.loading=!0,_this2.activeUserSearchData&&(search=_this2.activeUserSearchData),_context2.next=6,api/* API */.bl.get("/v2/application/users/list?page=".concat(_this2.page,"&perPage=").concat(_this2.perPage),{appId:appId,search:search},{force:force}).then((function(_ref){var data=_ref.data,error=_ref.error;error?_this2.$message.error(error.message||"Error Occured"):_this2.userlist=data.users||[],_this2.loading=!1}));case 6:return _context2.abrupt("return",_context2.sent);case 7:case"end":return _context2.stop()}}),_callee2)})))()},loadAppicationCount:function(){var _this3=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee3(){var appId,search;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context3){while(1)switch(_context3.prev=_context3.next){case 0:return appId=_this3.appId,_this3.loading=!0,_this3.activeUserSearchData&&(search=_this3.activeUserSearchData),_context3.next=5,api/* API */.bl.get("/v2/application/users/list?appId=".concat(appId,"&page=").concat(_this3.page,"&perPage=").concat(_this3.perPage,"&fetchCount=true"),{search:search}).then((function(_ref2){var data=_ref2.data,error=_ref2.error;error?_this3.$message.error(error.message||"Error Occured"):_this3.userlistCount=data.count||[]}));case 5:_this3.loading=!1;case 6:case"end":return _context3.stop()}}),_callee3)})))()},editUser:function(user){this.isNew=!1,this.showDialog=!0,this.currentUser=user},deleteUser:function(user){var _this4=this,appId=this.appId,name=user.name,ouid=user.ouid,params={appId:appId,user:{ouid:ouid}};this.$dialog.confirm({title:this.$t("common.header.delete_user"),htmlMessage:this.$t("common._common.do_you_want_delete_user_name",{name:name}),rbDanger:!0,rbLabel:this.$t("common._common.delete")}).then((function(value){value&&api/* API */.bl.post("/v2/application/users/delete",params).then((function(_ref3){var error=_ref3.error;error?_this4.$message.error(error.message||"Error Occured"):(_this4.loadApplicationUser(),_this4.$store.dispatch("loadUsers",!0))}))}))},saveUser:function(user,emailVerificationNeeded){var _this5=this,isNew=this.isNew,appId=this.appId,url="",successMsg="";isNew?(url="/v2/application/users/add",successMsg=this.$t("common.products.new_user_added")):(url="/setup/updateuser",successMsg=this.$t("common.products.user_updated"));var props=["name","email","phone","roleId","applicationId","language"];isNew||props.push("id");var params={appId:appId,emailVerificationNeeded:emailVerificationNeeded,user:pick_default()(user,props)};return api/* API */.bl.post(url,params).then((function(_ref4){var error=_ref4.error;if(error)throw _this5.$message.error(error.message||"Error Ocurred"),new Error;_this5.$message(successMsg),_this5.loadApplicationUser(),_this5.$store.dispatch("loadUsers",!0)}))},getRoleName:function(user){var _ref5=user||{},roleId=_ref5.roleId;return roleId?this.getRoleNameById(roleId):"---"},setupHomeRoute:function(){return this.$router.replace({path:"/".concat(this.appLinkName,"/setup/home")})},setupUsersRoute:function(){return this.$router.replace({path:"/".concat(this.appLinkName,"/setup/general/portal")})},showHideMenu:function(link){return this.sidebarTabList="users"===link?"users":"tabsLayout"===link?"tabsLayout":"roles"===link?"roles":"users"},copyLinkName:function(copy){var _this6=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee4(){return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context4){while(1)switch(_context4.prev=_context4.next){case 0:return _context4.next=2,navigator.clipboard.writeText(copy);case 2:_this6.$message({message:"Copied - "+copy,type:"success"});case 3:case"end":return _context4.stop()}}),_callee4)})))()},showHideArrow:function(){this.showSidebar=!1,this.showSideBarOpen=!0},showArrow:function(){this.showSidebar=!0,this.showSideBarOpen=!1},showUserSearch:function(){this.showUserSearchInput=!0,this.userSearchIcon=!1},clearUser:function(){this.showUserSearchInput=!1,this.userSearchIcon=!0,this.activeUserSearchData=null,this.activeUserSearch()},activeUserSearch:function(){this.page=1,this.perPage=50,this.loadApplicationUser(),this.loadAppicationCount()},reloadUser:function(){this.loadApplicationUser(),this.loadAppicationCount()},showExtraFilter:function(){this.showFilter=!this.showFilter},loadRolesForApp:function(){var _this7=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee5(){var appId,_yield$API$get,error,data;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context5){while(1)switch(_context5.prev=_context5.next){case 0:return appId=_this7.appId,_this7.loading=!0,_context5.next=4,api/* API */.bl.get("/setup/roles",{appId:appId});case 4:_yield$API$get=_context5.sent,error=_yield$API$get.error,data=_yield$API$get.data,error?_this7.$message.error(error.message||"Error Occured"):_this7.rolesList=(data.roles||[]).filter((function(role){return"Super Administrator"!==role.name})),_this7.loading=!1;case 9:case"end":return _context5.stop()}}),_callee5)})))()}}},v1_ApplicationSummaryvue_type_script_lang_js=ApplicationSummaryvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(v1_ApplicationSummaryvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,ApplicationSummary=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/32447.js.map