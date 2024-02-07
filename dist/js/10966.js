"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[10966],{
/***/929593:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return FPagination}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/list/FPagination.vue?vue&type=template&id=7641710c&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",[_vm.total>0?_c("div",{staticClass:"flex-center-row-space mT-5",class:{"fc-black-small-txt-12":!_vm.hideToggle}},[_c("el-popover",{attrs:{placement:"bottom",trigger:"click",disabled:_vm.disablePopup}},[_c("span",{staticClass:"p8 pointer",class:[!_vm.disablePopup&&"button-hover"],attrs:{slot:"reference"},slot:"reference"},[_vm.from!==_vm.to?_c("span",[_vm._v(_vm._s(_vm.from)+" - ")]):_vm._e(),_c("span",[_vm._v(_vm._s(_vm.to))]),_vm._v(" of "+_vm._s(_vm.total)+" ")]),_c("el-pagination",{attrs:{layout:"prev, pager, next",total:_vm.total,"pager-count":5,"page-size":_vm.perPage,"current-page":_vm.page},on:{"current-change":_vm.handleCurrentChange,"update:pageSize":function($event){_vm.perPage=$event},"update:page-size":function($event){_vm.perPage=$event},"update:currentPage":function($event){_vm.page=$event},"update:current-page":function($event){_vm.page=$event}}})],1),_vm.hideToggle?_vm._e():_c("span",[_c("span",{staticClass:"el-icon-arrow-left pagination-arrow fc-black-small-txt-12 f16 fw-bold pointer p5",class:{disable:_vm.from<=1},on:{click:function($event){_vm.from>1&&_vm.prev()}}}),_c("span",{staticClass:"el-icon-arrow-right pagination-arrow fc-black-small-txt-12 f16 fw-bold pointer p5",class:{disable:_vm.to===_vm.total},on:{click:function($event){_vm.to!==_vm.total&&_vm.next()}}})])],1):_vm._e()])},staticRenderFns=[],router=(__webpack_require__(879288),__webpack_require__(670560),__webpack_require__(329435)),FPaginationvue_type_script_lang_js={props:{total:[Number,String],perPage:Number,hideToggle:{type:Boolean,default:!1},pageNo:Number,hidePopover:{type:Boolean,default:!1}},data:function(){return{from:0,to:0,page:1}},created:function(){(0,router/* isWebTabsEnabled */.tj)()&&document.addEventListener("keydown",this.keyDownHandler)},mounted:function(){this.init()},beforeDestroy:function(){(0,router/* isWebTabsEnabled */.tj)()&&document.removeEventListener("keydown",this.keyDownHandler)},computed:{pageQuery:function(){var _ref=this.$route||{},query=_ref.query,_ref2=query||{},page=_ref2.page;return parseInt(page)||1},hasMorePage:function(){return this.total/this.perPage>1},disablePopup:function(){return this.hidePopover||!this.hasMorePage}},watch:{total:function(){this.init()},pageQuery:function(){this.init()}},methods:{init:function(){if(this.page=this.pageQuery,this.from=(this.page-1)*this.perPage+1,this.from>this.total&&this.from&&this.from>0&&this.total&&this.total>0)this.reset();else{var to=this.from+this.perPage-1;this.to=this.total>to?to:this.total}},handleCurrentChange:function(val){this.page=val,this.from=(this.page-1)*this.perPage+1,this.to=this.total>=this.from+this.perPage?this.from+this.perPage-1:this.total,this.setPage()},next:function(){this.from=this.to+1,this.to+=this.perPage,this.to>this.total&&(this.to=this.total),this.page++,this.setPage()},prev:function(){this.to=this.from-1,this.from-=this.perPage,this.from<=1?this.from=this.page=1:this.page--,this.setPage()},reset:function(){this.page=1,this.setPage(!0)},setPage:function(isReset){if(this.pageNo)this.$emit("onPageChanged",this.page);else{var query={};Object.assign(query,this.$route.query),query?query.page=this.page:query={page:this.page},this.$router.push({query:query}),isReset||this.$emit("onPageChanged",this.page)}},keyDownHandler:function(e){e.shiftKey&&("ArrowRight"===e.key&&this.to!==this.total?this.next():"ArrowLeft"===e.key&&this.from>1&&this.prev())}}},list_FPaginationvue_type_script_lang_js=FPaginationvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(list_FPaginationvue_type_script_lang_js,render,staticRenderFns,!1,null,"7641710c",null)
/* harmony default export */,FPagination=component.exports},
/***/622957:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return SetupEmptyState}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/setup/components/SetupEmptyState.vue?vue&type=template&id=839bdf38
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"fc-setup-empty"},[_c("div",{staticClass:"fc-setup-empty-image"},[_c("inline-svg",{attrs:{src:"svgs/list-empty",iconClass:"icon text-center icon-xxxxlg"}})],1),_c("div",{staticClass:"fc-setup-empty-txt"},[_vm._t("emptyHeading")],2),_c("div",{staticClass:"fc-setup-empty-txt-desc"},[_vm._t("emptyDescription")],2)])},staticRenderFns=[],componentNormalizer=__webpack_require__(801001),script={},component=(0,componentNormalizer/* default */.Z)(script,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,SetupEmptyState=component.exports},
/***/291612:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return SetupHeaderTabs}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/setup/components/SetupHeaderTabs.vue?vue&type=template&id=86381c1c
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{},[_c("el-header",{staticClass:"fc-v1-setup-header",attrs:{height:"150"}},[_c("div",{staticClass:"flex-middle justify-content-space fc-header-border-bottom p20"},[_c("div",[_c("div",{staticClass:"fc-v1-setup-heading"},[_vm._t("heading")],2),_c("div",{staticClass:"fc-v1-setup-description"},[_vm._t("description")],2)]),_c("div",{staticClass:"flex-middle"},[_vm._t("actions")],2)]),_c("div",[_c("div",{staticClass:"fc-setup-tab-content"},[_vm._t("tabs",(function(){return[_vm._t("tabFilter",(function(){return[_c("div",{staticClass:"flex-middle justify-content-space p20"},[_c("div",{},[_vm._t("filter")],2),_c("div",{},[_vm._t("searchAndPagination")],2)])]})),_vm._t("tabContent")]}))],2)])])],1)},staticRenderFns=[],componentNormalizer=__webpack_require__(801001),script={}
/* normalize component */,component=(0,componentNormalizer/* default */.Z)(script,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,SetupHeaderTabs=component.exports},
/***/233432:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return SetupLoader}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/setup/components/SetupLoader.vue?vue&type=template&id=367e612c
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"fc-setup-loader"},[_vm._t("setupLoading")],2)},staticRenderFns=[],componentNormalizer=__webpack_require__(801001),script={},component=(0,componentNormalizer/* default */.Z)(script,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,SetupLoader=component.exports},
/***/874021:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return EmailLogsSummary}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/setup/emailLogs/EmailLogsSummary.vue?vue&type=template&id=b8ff7cf4&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"emaillog-summary"},[_c("SetupHeader",{scopedSlots:_vm._u([{key:"heading",fn:function(){return[_c("div",{staticClass:"flex-middle fc-setup-breadcrumb mB10"},[_c("div",{staticClass:"fc-setup-breadcrumb-inner pointer",on:{click:_vm.setupHomeRoute}},[_vm._v(" "+_vm._s(_vm.$t("common.products.home"))+" ")]),_c("div",{staticClass:"fc-setup-breadcrumb-inner pL10 pR10"},[_c("i",{staticClass:"el-icon-arrow-right f14 fwBold"})]),_c("div",{staticClass:"fc-setup-breadcrumb-inner pointer",on:{click:_vm.setupLogsListRoute}},[_vm._v(" "+_vm._s(_vm.$t("emailLogs.email_logs"))+" ")]),_c("div",{staticClass:"fc-setup-breadcrumb-inner pL10 pR10"},[_c("i",{staticClass:"el-icon-arrow-right f14 fwBold"})]),_c("div",{staticClass:"fc-breadcrumbBold-active"},[_vm._v("#"+_vm._s(_vm.loggerId))])]),_c("p",[_vm._v(_vm._s(_vm.subject||"---"))])]},proxy:!0},{key:"tabs",fn:function(){return[_c("el-tabs",{staticClass:"fc-setup-list-tab",model:{value:_vm.currentTab,callback:function($$v){_vm.currentTab=$$v},expression:"currentTab"}},[_c("el-tab-pane",{attrs:{label:"Email Information",name:"infoTab"}},["infoTab"===_vm.currentTab?_c("div",{staticClass:"mT10 mB20"},[_c("EmailLogsInfoTab",{attrs:{loggerId:_vm.loggerId},on:{emailLogFetched:_vm.setSubject}})],1):_vm._e()]),_c("el-tab-pane",{attrs:{label:_vm.$t("emailLogs.email_dialog.recipient_list"),name:"recipientTab"}},[_c("div",{staticClass:"mT10 mB20"},[_vm.isRecipientTab?_c("EmailLogsTabList",{attrs:{loggerId:_vm.loggerId,recordsPerPage:_vm.recordsPerPage,currentPage:_vm.currentPage,searchText:_vm.searchTextProp,status:"all"},on:{paginationFetch:function(count){return _vm.totalRecords=count}}}):_vm._e()],1)]),_c("el-tab-pane",{attrs:{label:_vm.$t("emailLogs.email_dialog.delivered_list"),name:"deliveredTab"}},[_c("div",{staticClass:"mT10 mB20"},[_vm.isDeliveredTab?_c("EmailLogsTabList",{attrs:{loggerId:_vm.loggerId,recordsPerPage:_vm.recordsPerPage,currentPage:_vm.currentPage,searchText:_vm.searchTextProp,status:"delivered"},on:{paginationFetch:function(count){return _vm.totalRecords=count}}}):_vm._e()],1)]),_c("el-tab-pane",{attrs:{label:_vm.$t("emailLogs.email_dialog.bounced_list"),name:"bouncedTab"}},[_c("div",{staticClass:"mT10 mB20"},[_vm.isBouncedTab?_c("EmailLogsTabList",{attrs:{loggerId:_vm.loggerId,recordsPerPage:_vm.recordsPerPage,currentPage:_vm.currentPage,searchText:_vm.searchTextProp,status:"bounced"},on:{paginationFetch:function(count){return _vm.totalRecords=count}}}):_vm._e()],1)]),_vm.canShowPagination?_c("el-tab-pane",[_c("span",{staticClass:"pagination-container",attrs:{slot:"label"},slot:"label"},[_c("div",{staticClass:"self-center cursor-pointer",staticStyle:{width:"130px"},on:{click:_vm.prventTabClick}},[_c("Pagination",{ref:"f-page",staticClass:"nowrap",attrs:{total:_vm.totalRecords,perPage:_vm.recordsPerPage,pageNo:_vm.currentPage},on:{onPageChanged:_vm.setPage}})],1)])]):_vm._e(),_vm.canShowPagination?_c("el-tab-pane",[_c("span",{attrs:{slot:"label"},on:{click:_vm.prventTabClick},slot:"label"},[_c("div",{staticClass:"seperator"},[_vm._v("|")])])]):_vm._e(),_vm.canShowSearch?_c("el-tab-pane",[_c("span",{staticClass:"search-container",attrs:{slot:"label"},on:{click:_vm.prventTabClick},slot:"label"},[_c("div",[_vm.showSearchInput?_vm._e():_c("div",{directives:[{name:"tippy",rawName:"v-tippy",value:{arrow:!0,arrowType:"round",animation:"fade"},expression:"{\n                  arrow: true,\n                  arrowType: 'round',\n                  animation: 'fade',\n                }"}],staticClass:"fc-portal-filter-border",attrs:{content:_vm.$t("common._common.search")},on:{click:function($event){return _vm.handleSearchClick($event)}}},[_c("i",{staticClass:"el-icon-search fc-black-14 fwBold"})]),_vm.showSearchInput?_c("div",{staticClass:"relative"},[_c("input",{directives:[{name:"model",rawName:"v-model",value:_vm.searchText,expression:"searchText"}],staticClass:"fc-log-search",attrs:{placeholder:_vm.$t("emailLogs.email_dialog.search_email")},domProps:{value:_vm.searchText},on:{keyup:function($event){return!$event.type.indexOf("key")&&_vm._k($event.keyCode,"enter",13,$event.key,"Enter")?null:_vm.updateSearchText.apply(null,arguments)},input:function($event){$event.target.composing||(_vm.searchText=$event.target.value)}}}),_c("i",{staticClass:"el-icon-close fwBold fc-search-close",on:{click:function($event){return _vm.closeSearchInput($event)}}})]):_vm._e()])])]):_vm._e()],1)]},proxy:!0}])})],1)},staticRenderFns=[],SetupHeaderTabs=(__webpack_require__(564043),__webpack_require__(857267),__webpack_require__(291612)),EmailLogsTabListvue_type_template_id_5a220b84_render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"summary-tabs"},[_vm.isLoading?_c("setup-loader",{scopedSlots:_vm._u([{key:"setupLoading",fn:function(){return[_c("spinner",{attrs:{show:_vm.isLoading,size:"80"}})]},proxy:!0}],null,!1,3036156284)}):_vm.$validation.isEmpty(_vm.tableData)?_c("setup-empty",{scopedSlots:_vm._u([{key:"emptyImage",fn:function(){return[_c("inline-svg",{attrs:{src:"svgs/copy2",iconClass:"icon icon-sm-md"}})]},proxy:!0},{key:"emptyHeading",fn:function(){return[_vm._v(" "+_vm._s(_vm.$t("emailLogs.no_records_found"))+" ")]},proxy:!0},{key:"emptyDescription",fn:function(){},proxy:!0}])}):_c("div",[_c("div",{staticClass:"summary-table"},[_c("el-table",{staticClass:"width100 fc-setup-table fc-setup-table-p0 fc-setup-table-th-borderTop",staticStyle:{width:"100%"},attrs:{data:_vm.tableData,"cell-style":{padding:"12px 30px"},height:"calc(100vh - 280px)"}},[_c("el-table-column",{attrs:{type:"index",label:_vm.$t("emailLogs.email_dialog.s_no"),width:"35","class-name":"pL15"}}),_c("el-table-column",{attrs:{prop:"time",label:_vm.$getProperty(_vm.displayNameMap,"sysModifiedTime"),width:"75","class-name":"pL0"},scopedSlots:_vm._u([{key:"default",fn:function(tableSlotName){return[_c("div",[_vm._v(" "+_vm._s(_vm.getFormatter(_vm.$getProperty(tableSlotName.row,"sysModifiedTime"))||"---")+" ")])]}}])}),_c("el-table-column",{attrs:{prop:"email",label:_vm.$getProperty(_vm.displayNameMap,"recipient"),width:"130","class-name":"pL0"},scopedSlots:_vm._u([{key:"default",fn:function(tableSlotName){return[_c("div",[_vm._v(" "+_vm._s(_vm.$getProperty(tableSlotName.row,"recipient")||"---")+" ")])]}}])}),_c("el-table-column",{attrs:{prop:"name",label:_vm.$getProperty(_vm.displayNameMap,"name"),"class-name":"pL0",width:"75"},scopedSlots:_vm._u([{key:"default",fn:function(tableSlotName){return[_c("div",[_vm._v(" "+_vm._s(_vm.$getProperty(tableSlotName.row,"name")||"---")+" ")])]}}])}),_vm.isBouncedListTable?_vm._e():_c("el-table-column",{attrs:{prop:"status",label:_vm.$getProperty(_vm.displayNameMap,"status"),width:"75","class-name":"pL0"},scopedSlots:_vm._u([{key:"default",fn:function(tableSlotName){return[4===tableSlotName.row.status?_c("div",[_c("el-tooltip",{staticClass:"item",attrs:{effect:"dark",content:_vm.$getProperty(tableSlotName,"row.bounceReason"),placement:"top"}},[_c("span",{staticClass:"m0 p5 pL0"},[_vm._v(" "+_vm._s(_vm.getStatus(tableSlotName.row))+" ")])])],1):_c("div",{staticClass:"p5 pL0"},[_vm._v(" "+_vm._s(_vm.getStatus(tableSlotName.row))+" ")])]}}],null,!1,3786017104)}),_vm.isBouncedListTable?_c("el-table-column",{attrs:{prop:"bounceType",label:_vm.$getProperty(_vm.displayNameMap,"bounceType"),width:"75","class-name":"pL0"},scopedSlots:_vm._u([{key:"default",fn:function(tableSlotName){return[_c("div",[_vm._v(" "+_vm._s(_vm.getBounceType(tableSlotName.row))+" ")])]}}],null,!1,32795200)}):_vm._e(),_vm.isBouncedListTable?_c("el-table-column",{attrs:{prop:"bounceReason",label:_vm.$getProperty(_vm.displayNameMap,"bounceReason"),width:"75","class-name":"pL0"},scopedSlots:_vm._u([{key:"default",fn:function(tableSlotName){return[_c("div",[_vm._v(" "+_vm._s(_vm.$getProperty(tableSlotName.row,"bounceReason")||"---")+" ")])]}}],null,!1,2555547326)}):_vm._e()],1)],1)])],1)},EmailLogsTabListvue_type_template_id_5a220b84_staticRenderFns=[],objectSpread2=__webpack_require__(595082),regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),SetupLoader=(__webpack_require__(260228),__webpack_require__(947522),__webpack_require__(434284),__webpack_require__(634338),__webpack_require__(648324),__webpack_require__(233432)),SetupEmptyState=__webpack_require__(622957),api=__webpack_require__(32284),validation=__webpack_require__(990260),moment_timezone=__webpack_require__(480008),moment_timezone_default=__webpack_require__.n(moment_timezone),helpers=__webpack_require__(778390),vuex_esm=__webpack_require__(420629),statusMap={1:"emailLogs.email_dialog.in_progress",2:"emailLogs.email_dialog.sent",3:"emailLogs.email_dialog.delivered",4:"emailLogs.email_dialog.bounced"},bounceTypeMap={1:"emailLogs.email_dialog.hard_bounce",2:"emailLogs.email_dialog.soft_bounce",3:"emailLogs.email_dialog.unknown_bounce"},EmailLogsTabListvue_type_script_lang_js={data:function(){return{isLoading:!1,tableData:[],isBouncedListTable:!1,totalRecords:0,displayNameMap:{}}},props:["loggerId","status","recordsPerPage","currentPage","searchText"],components:{SetupEmpty:SetupEmptyState/* default */.Z,SetupLoader:SetupLoader/* default */.Z},created:function(){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var moduleName;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return _this.isLoading=!0,moduleName=_this.moduleName,_context.next=4,_this.$store.dispatch("view/loadModuleMeta",moduleName);case 4:_this.fetchTableData();case 5:case"end":return _context.stop()}}),_callee)})))()},computed:(0,objectSpread2/* default */.Z)({moduleName:function(){return"outgoingRecipientLogger"}},(0,vuex_esm/* mapState */.rn)({metaInfo:function(state){return state.view.metaInfo}})),watch:{metaInfo:{handler:function(newVal){(0,validation/* isEmpty */.xb)(newVal)||this.setDisplayNameMap()},immediate:!0},searchText:function(newVal,oldVal){newVal!==oldVal&&this.fetchTableData()},currentPage:function(newVal,oldVal){newVal!==oldVal&&this.fetchTableData()}},methods:{setDisplayNameMap:function(){var _this2=this,metaInfo=this.metaInfo,_ref=metaInfo||{},fields=_ref.fields;(0,validation/* isEmpty */.xb)(fields)||fields.forEach((function(field){var _ref2=field||{},fieldName=_ref2.name,fieldDisplayName=_ref2.displayName;_this2.$set(_this2.displayNameMap,fieldName,fieldDisplayName)}))},getFormatter:function(timeStamp){return"".concat(moment_timezone_default()(timeStamp).tz(this.$timezone).format("DD MMM YYYY"),", ").concat((0,helpers/* getTimeInOrgFormat */.eB)(timeStamp))},getStatus:function(row){var _ref3=row||{},status=_ref3.status,statusKey=statusMap[status]||null;return statusKey?this.$t(statusKey):"---"},getBounceType:function(row){var _ref4=row||{},bounceType=_ref4.bounceType,bounceTypeKey=bounceTypeMap[bounceType]||null;return bounceTypeKey?this.$t(bounceTypeKey):"---"},fetchTableData:function(){var _this3=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee2(){var status,loggerId,currentPage,recordsPerPage,searchText,moduleName,params,_yield$API$fetchAll,list,_yield$API$fetchAll$m,meta,error;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context2){while(1)switch(_context2.prev=_context2.next){case 0:return _this3.isLoading=!0,status=_this3.status,loggerId=_this3.loggerId,currentPage=_this3.currentPage,recordsPerPage=_this3.recordsPerPage,searchText=_this3.searchText,moduleName=_this3.moduleName,"bounced"===status&&(_this3.isBouncedListTable=!0),params={loggerId:loggerId,status:status,withCount:!0,page:currentPage,perPage:recordsPerPage},(0,validation/* isEmpty */.xb)(searchText)||(params["filters"]=JSON.stringify({recipient:{operatorId:5,value:[searchText]}})),_context2.next=7,api/* API */.bl.fetchAll(moduleName,params);case 7:_yield$API$fetchAll=_context2.sent,list=_yield$API$fetchAll.list,_yield$API$fetchAll$m=_yield$API$fetchAll.meta,meta=void 0===_yield$API$fetchAll$m?{}:_yield$API$fetchAll$m,error=_yield$API$fetchAll.error,error?_this3.$message.error(error.message||"Error Occured"):(_this3.tableData=list,_this3.totalRecords=_this3.$getProperty(meta,"pagination.totalCount",null),_this3.$emit("paginationFetch",_this3.totalRecords)),_this3.isLoading=!1;case 14:case"end":return _context2.stop()}}),_callee2)})))()}}},emailLogs_EmailLogsTabListvue_type_script_lang_js=EmailLogsTabListvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(emailLogs_EmailLogsTabListvue_type_script_lang_js,EmailLogsTabListvue_type_template_id_5a220b84_render,EmailLogsTabListvue_type_template_id_5a220b84_staticRenderFns,!1,null,null,null)
/* harmony default export */,EmailLogsTabList=component.exports,EmailLogsInfoTabvue_type_template_id_6db0e706_scoped_true_render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"info-tab-page"},[_vm.isLoading?_c("setup-loader",{scopedSlots:_vm._u([{key:"setupLoading",fn:function(){return[_c("spinner",{attrs:{show:_vm.isLoading,size:"80"}})]},proxy:!0}],null,!1,3036156284)}):_c("div",{staticClass:"info-tab mB100"},[_c("div",{staticClass:"fc-grey3-text14 bold"},[_vm._v(" "+_vm._s(_vm.$t("emailLogs.info_tab.basic_information"))+" ")]),_c("el-card",{staticClass:"fc-setup-summary-card heightInitial mB20 mT15",attrs:{shadow:"never"}},[_c("el-row",{attrs:{gutter:20}},[_c("el-col",{attrs:{span:8}},[_c("div",{staticClass:"fc-grey2-text12 bold line-height20"},[_vm._v(" "+_vm._s(_vm.$t("common._common.from"))+" ")]),_c("div",{staticClass:"fc-black-14 text-left bold"},[_vm._v(" "+_vm._s(_vm.emailFromAddress||"---")+" ")])]),_c("el-col",{attrs:{span:8}},[_c("div",{staticClass:"fc-grey2-text12 bold line-height20"},[_vm._v(" "+_vm._s(_vm.$t("common.products.to"))+" ")]),_vm.canShowToolTip(_vm.receiverList)?_c("div",{staticClass:"fc-black-14 text-left bold .truncate-text"},[_vm._v(" "+_vm._s(_vm.getFirstItemInList(_vm.receiverList))+" "),_c("el-tooltip",{attrs:{placement:"bottom"}},[_c("div",{attrs:{slot:"content"},slot:"content"},_vm._l(_vm.receiverList,(function(receiver,index){return _c("div",{key:index},[index?_c("div",[_vm._v(_vm._s(receiver||"---"))]):_vm._e()])})),0),_c("span",{staticClass:"plus-more"},[_vm._v(" "+_vm._s(_vm.getListToolTipText(_vm.receiverList))+" ")])])],1):_c("div",{staticClass:"fc-black-14 text-left bold .truncate-text"},[_vm._v(" "+_vm._s(_vm.getFirstItemInList(_vm.receiverList))+" ")])]),_c("el-col",{attrs:{span:8}},[_c("div",{staticClass:"fc-grey2-text12 bold line-height20"},[_vm._v(" "+_vm._s(_vm.$t("emailLogs.info_tab.cc"))+" ")]),_vm.canShowToolTip(_vm.ccList)?_c("div",{staticClass:"fc-black-14 text-left bold .truncate-text"},[_vm._v(" "+_vm._s(_vm.getFirstItemInList(_vm.ccList))+" "),_c("el-tooltip",{attrs:{placement:"bottom"}},[_c("div",{attrs:{slot:"content"},slot:"content"},_vm._l(_vm.ccList,(function(receiver,index){return _c("div",{key:index},[index?_c("div",[_vm._v(_vm._s(receiver||"---"))]):_vm._e()])})),0),_c("span",{staticClass:"plus-more"},[_vm._v(" "+_vm._s(_vm.getListToolTipText(_vm.ccList))+" ")])])],1):_c("div",{staticClass:"fc-black-14 text-left bold .truncate-text"},[_vm._v(" "+_vm._s(_vm.getFirstItemInList(_vm.ccList))+" ")])])],1),_c("el-row",{attrs:{gutter:20}},[_c("el-col",{attrs:{span:8}},[_c("div",{staticClass:"fc-grey2-text12 bold line-height20"},[_vm._v(" "+_vm._s(_vm.$t("emailLogs.info_tab.bcc"))+" ")]),_vm.canShowToolTip(_vm.bccList)?_c("div",{staticClass:"fc-black-14 text-left bold truncate-text"},[_vm._v(" "+_vm._s(_vm.getFirstItemInList(_vm.bccList))+" "),_c("el-tooltip",{attrs:{placement:"bottom"}},[_c("div",{attrs:{slot:"content"},slot:"content"},_vm._l(_vm.ccList,(function(receiver,index){return _c("div",{key:index},[index?_c("div",[_vm._v(_vm._s(receiver||"---"))]):_vm._e()])})),0),_c("span",{staticClass:"plus-more"},[_vm._v(" "+_vm._s(_vm.getListToolTipText(_vm.bccList))+" ")])])],1):_c("div",{staticClass:"fc-black-14 text-left bold truncate-text"},[_vm._v(" "+_vm._s(_vm.getFirstItemInList(_vm.bccList))+" ")])]),_c("el-col",{attrs:{span:8}},[_c("div",{staticClass:"fc-grey2-text12 bold line-height20"},[_vm._v(" "+_vm._s(_vm.$t("emailLogs.info_tab.created_time"))+" ")]),_c("div",{staticClass:"fc-black-14 text-left bold"},[_vm._v(" "+_vm._s(_vm.getFormatter(_vm.emailLog.sysCreatedTime)||"---")+" ")])])],1)],1),_vm.isEmailBodyEmpty?_vm._e():_c("div",{staticClass:"fc-grey3-text14 bold"},[_vm._v(" "+_vm._s(_vm.$t("emailLogs.info_tab.email_body"))+" ")]),_vm.isEmailBodyEmpty?_vm._e():_c("el-card",{staticClass:"fc-setup-summary-card heightInitial mB20 mT15",attrs:{shadow:"never"}},[_c("el-row",{attrs:{gutter:20}},[_c("el-col",{attrs:{span:24}},[_vm.isEmailBodyEmpty?_c("div",[_c("setup-empty",{scopedSlots:_vm._u([{key:"emptyImage",fn:function(){return[_c("inline-svg",{attrs:{src:"svgs/copy2",iconClass:"icon icon-sm-md"}})]},proxy:!0},{key:"emptyHeading",fn:function(){return[_vm._v(" "+_vm._s(_vm.$t("emailLogs.no_records_found"))+" ")]},proxy:!0},{key:"emptyDescription",fn:function(){},proxy:!0}],null,!1,2730775301)})],1):_c("div",{staticClass:"email-logs-email-body"},[_c("FHtml",{attrs:{content:_vm.getHtmlTemplate()}})],1)])],1),_vm.canShowAttachments?_c("el-row",{staticClass:"attachments-row"},[_c("p",{staticClass:"files-attached"},[_c("el-tooltip",{attrs:{placement:"top-start"}},[_c("div",{attrs:{slot:"content"},slot:"content"},_vm._l(_vm.attachmentsList,(function(attachment,index){return _c("div",{key:index},[_vm._v(" "+_vm._s(attachment.fileName||"---")+" ")])})),0),_c("span",[_vm._v(_vm._s(_vm.getFilesCount()))])])],1)]):_vm._e()],1)],1)],1)},EmailLogsInfoTabvue_type_template_id_6db0e706_scoped_true_staticRenderFns=[],sanitize=__webpack_require__(678042),app=__webpack_require__(782080),EmailLogsInfoTabvue_type_script_lang_js={name:"EmailLogsInfoTab",data:function(){return{isLoading:!1,emailLog:{}}},props:["loggerId"],computed:{moduleName:function(){return"outgoingMailLogger"},attachmentsList:function(){var emailLog=this.emailLog,_ref=emailLog||{},_ref$attachmentsList=_ref.attachmentsList,attachmentsList=void 0===_ref$attachmentsList?[]:_ref$attachmentsList;return attachmentsList},canShowAttachments:function(){var isEmailBodyEmpty=this.isEmailBodyEmpty,attachmentsList=this.attachmentsList;return!isEmailBodyEmpty&&attachmentsList.length>0},receiverList:function(){var emailLog=this.emailLog,_ref2=emailLog||{},receiverList=_ref2.receiverList;return(0,validation/* isEmpty */.xb)(receiverList)?[]:receiverList},ccList:function(){var emailLog=this.emailLog,_ref3=emailLog||{},ccList=_ref3.ccList;return(0,validation/* isEmpty */.xb)(ccList)?[]:ccList},bccList:function(){var emailLog=this.emailLog,_ref4=emailLog||{},bccList=_ref4.bccList;return(0,validation/* isEmpty */.xb)(bccList)?[]:bccList},isEmailBodyEmpty:function(){var emailLog=this.emailLog,_ref5=emailLog||{},htmlContent=_ref5.htmlContent,textContent=_ref5.textContent;return(0,validation/* isEmpty */.xb)(htmlContent)&&(0,validation/* isEmpty */.xb)(textContent)},emailFromAddress:function(){var emailLog=this.emailLog,_ref6=emailLog||{},senderName=_ref6.senderName,senderMail=_ref6.senderMail;return(0,validation/* isEmpty */.xb)(senderName)?senderMail:"".concat(senderName," <").concat(senderMail,">")}},components:{SetupLoader:SetupLoader/* default */.Z,SetupEmpty:SetupEmptyState/* default */.Z,FHtml:app/* FHtml */.J_},created:function(){this.fetchEmailInfo()},methods:{canShowToolTip:function(dataList){return!(0,validation/* isEmpty */.xb)(dataList)&&dataList.length>1},getFirstItemInList:function(dataList){return(0,validation/* isEmpty */.xb)(dataList)?"---":dataList[0]},getListToolTipText:function(dataList){return!(0,validation/* isEmpty */.xb)(dataList)&&dataList.length>1?"+ ".concat(dataList.length-1," ").concat(this.$t("common._common.more")):"---"},getListFromString:function(dataList){return(0,validation/* isEmpty */.xb)(dataList)?[]:dataList.split(",")},getFormatter:function(timeStamp){return moment_timezone_default()(timeStamp).tz(this.$timezone).format("DD MMM YYYY")},getFilesCount:function(){var _this$attachmentsList=this.attachmentsList,attachmentsList=void 0===_this$attachmentsList?[]:_this$attachmentsList;return attachmentsList.length>1?"".concat(attachmentsList.length," ").concat(this.$t("emailLogs.files_attached")):"".concat(attachmentsList.length," ").concat(this.$t("emailLogs.file_attached"))},getHtmlTemplate:function(){var emailLog=this.emailLog,_ref7=emailLog||{},htmlContent=_ref7.htmlContent,textContent=_ref7.textContent;return(0,validation/* isEmpty */.xb)(htmlContent)?(0,validation/* isEmpty */.xb)(textContent)?"":"<p>".concat(textContent,"</p>"):(0,sanitize/* sanitize */.N)(htmlContent)},deserializeData:function(){var _ref8=this||{},emailLog=_ref8.emailLog;if(!(0,validation/* isEmpty */.xb)(emailLog)){var _ref9=emailLog||{},_ref9$from=_ref9.from,from=void 0===_ref9$from?"":_ref9$from,_ref9$to=_ref9.to,to=void 0===_ref9$to?"":_ref9$to,cc=_ref9.cc,bcc=_ref9.bcc,senderName=from.substring(from.indexOf('"')+1,from.lastIndexOf('"')),senderMail=from.substring(from.indexOf("<")+1,from.lastIndexOf(">")),receiverList=this.getListFromString(to),ccList=this.getListFromString(cc),bccList=this.getListFromString(bcc);this.$set(emailLog,"senderName",senderName),this.$set(emailLog,"senderMail",senderMail),this.$set(emailLog,"receiverList",receiverList),this.$set(emailLog,"ccList",ccList),this.$set(emailLog,"bccList",bccList)}},fetchEmailInfo:function(){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var moduleName,id,_yield$API$fetchRecor,outgoingMailLogger,error,emailLog,_ref10,subject;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return _this.isLoading=!0,moduleName=_this.moduleName,id=_this.loggerId,_context.next=4,api/* API */.bl.fetchRecord(moduleName,{id:id});case 4:_yield$API$fetchRecor=_context.sent,outgoingMailLogger=_yield$API$fetchRecor.outgoingMailLogger,error=_yield$API$fetchRecor.error,error?_this.$message.error(error.message||"Error Occured"):(_this.emailLog=outgoingMailLogger,emailLog=_this.emailLog,_ref10=emailLog||{},subject=_ref10.subject,_this.$emit("emailLogFetched",subject),_this.deserializeData()),_this.isLoading=!1;case 9:case"end":return _context.stop()}}),_callee)})))()}}},emailLogs_EmailLogsInfoTabvue_type_script_lang_js=EmailLogsInfoTabvue_type_script_lang_js,EmailLogsInfoTab_component=(0,componentNormalizer/* default */.Z)(emailLogs_EmailLogsInfoTabvue_type_script_lang_js,EmailLogsInfoTabvue_type_template_id_6db0e706_scoped_true_render,EmailLogsInfoTabvue_type_template_id_6db0e706_scoped_true_staticRenderFns,!1,null,"6db0e706",null)
/* harmony default export */,EmailLogsInfoTab=EmailLogsInfoTab_component.exports,FPagination=__webpack_require__(929593),EmailLogsSummaryvue_type_script_lang_js={name:"EmailLogsSummary",data:function(){return{currentTab:"infoTab",subject:"",totalRecords:0,recordsPerPage:50,currentPage:1,showSearchInput:!1,searchText:"",searchTextProp:""}},components:{SetupHeader:SetupHeaderTabs/* default */.Z,Pagination:FPagination/* default */.Z,EmailLogsTabList:EmailLogsTabList,EmailLogsInfoTab:EmailLogsInfoTab},computed:{canShowSearch:function(){var currentTab=this.currentTab,totalRecords=this.totalRecords,searchText=this.searchText;return"infoTab"!==currentTab&&(totalRecords||searchText)},canShowPagination:function(){var currentTab=this.currentTab,totalRecords=this.totalRecords;return"infoTab"!==currentTab&&totalRecords},loggerId:function(){var route=this.$route,params=route.params,loggerId=params.loggerId;return(0,validation/* isEmpty */.xb)(loggerId)?-1:loggerId},isRecipientTab:function(){var currentTab=this.currentTab;return!(0,validation/* isEmpty */.xb)(currentTab)&&"recipientTab"===currentTab},isDeliveredTab:function(){var currentTab=this.currentTab;return!(0,validation/* isEmpty */.xb)(currentTab)&&"deliveredTab"===currentTab},isBouncedTab:function(){var currentTab=this.currentTab;return!(0,validation/* isEmpty */.xb)(currentTab)&&"bouncedTab"===currentTab}},watch:{currentTab:function(newVal,oldVal){_.isEqual(newVal,oldVal)||(this.currentPage=1,this.showSearchInput=!1,this.totalRecords=0,this.searchText="",this.searchTextProp="")}},methods:{setSubject:function(sub){this.subject=sub},prventTabClick:function(e){e.stopPropagation()},handleSearchClick:function(e){e.stopPropagation(),this.showSearchInput=!0},closeSearchInput:function(e){e.stopPropagation(),this.showSearchInput=!1,this.searchText="",this.searchTextProp=""},setPage:function(page){this.currentPage=page},updateSearchText:function(){this.searchTextProp=this.searchText},setupHomeRoute:function(){this.$router.replace({name:"setup"})},setupLogsListRoute:function(){this.$router.replace({name:"emailLogsList"})}}},emailLogs_EmailLogsSummaryvue_type_script_lang_js=EmailLogsSummaryvue_type_script_lang_js,EmailLogsSummary_component=(0,componentNormalizer/* default */.Z)(emailLogs_EmailLogsSummaryvue_type_script_lang_js,render,staticRenderFns,!1,null,"b8ff7cf4",null)
/* harmony default export */,EmailLogsSummary=EmailLogsSummary_component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/10966.js.map