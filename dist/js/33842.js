"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[33842],{
/***/310682:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return ModuleListOverview}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/base-module-v2/ModuleListOverview.vue?vue&type=template&id=69c27a3d
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",[_c("div",{staticClass:"height100 d-flex"},[_c("div",{staticStyle:{flex:"0 0 300px","max-width":"300px"}},[_c("div",{staticClass:"height100vh full-layout-white fc-border-left fc-border-right"},[_c("div",{staticClass:"row p15 fc-border-bottom pointer"},[_c("div",{staticClass:"col-1 text-left"},[_c("i",{staticClass:"el-icon-back fw6",staticStyle:{"vertical-align":"sub"},on:{click:_vm.back}})]),_c("el-popover",{attrs:{placement:"bottom",width:"250","popper-class":"popover-height asset-popover ",trigger:"click","visible-arrow":"true"},model:{value:_vm.toggle,callback:function($$v){_vm.toggle=$$v},expression:"toggle"}},[_c("ul",_vm._l(_vm.views,(function(view,index){return _c("li",{key:index,class:{active:_vm.currentView===view.name},on:{click:function($event){return _vm.switchView(view)}}},[_vm._v(" "+_vm._s(view.displayName)+" ")])})),0),_c("span",{staticClass:"line-height20",attrs:{slot:"reference"},slot:"reference"},[_vm._v(" "+_vm._s(_vm.currentViewDetail.displayName)+" "),_c("i",{staticClass:"el-icon-arrow-down el-icon-arrow-down-tv",staticStyle:{"padding-left":"8px"}})])]),_vm.showQuickSearch?_c("div",{staticClass:"row"},[_c("div",{staticClass:"col-12 fc-list-search"},[_c("div",{staticClass:"fc-list-search-wrapper fc-list-search-wrapper-asset"},[_c("svg",{staticClass:"search-icon-asset",attrs:{xmlns:"http://www.w3.org/2000/svg",width:"32",height:"32",viewBox:"0 0 32 32"}},[_c("title",[_vm._v("search")]),_c("path",{attrs:{d:"M21.487 22.927l7.037 7.037c.473.473 1.238.475 1.71.003s.47-1.237-.003-1.71l-7.037-7.037c3.96-4.82 3.675-11.967-.846-16.487C17.539-.076 9.757-.092 4.966 4.699S.191 17.272 5 22.081c4.52 4.52 11.667 4.805 16.487.846zM6.679 6.413c3.848-3.848 10.099-3.836 13.962.027s3.875 10.114.027 13.962c-3.848 3.848-10.099 3.836-13.962-.027S2.831 10.261 6.679 6.413z"}})]),_c("input",{directives:[{name:"model",rawName:"v-model",value:_vm.quickSearchQuery,expression:"quickSearchQuery"}],ref:"quickSearchQuery",staticClass:"quick-search-input-asset",attrs:{autofocus:"",type:"text",placeholder:"Search"},domProps:{value:_vm.quickSearchQuery},on:{keyup:function($event){return!$event.type.indexOf("key")&&_vm._k($event.keyCode,"enter",13,$event.key,"Enter")?null:_vm.quickSearch.apply(null,arguments)},input:function($event){$event.target.composing||(_vm.quickSearchQuery=$event.target.value)}}}),_c("svg",{staticClass:"close-icon-asset",attrs:{xmlns:"http://www.w3.org/2000/svg",width:"32",height:"32",viewBox:"0 0 32 32","aria-hidden":"true"},on:{click:_vm.closeSearch}},[_c("title",[_vm._v("close")]),_c("path",{attrs:{d:"M17.992 16l8.796-8.796a1.409 1.409 0 0 0-1.992-1.992L16 14.008 7.204 5.212a1.409 1.409 0 0 0-1.992 1.992L14.008 16l-8.796 8.796a1.409 1.409 0 0 0 1.992 1.992L16 17.992l8.796 8.796a1.409 1.409 0 0 0 1.992-1.992L17.992 16z"}})])])])]):_vm._e()],1),_c("div",{staticClass:"row sp-navbar2"},[_c("ul",{staticClass:"sp-ul"},[_c("v-infinite-scroll",{staticStyle:{height:"100vh","padding-bottom":"100px","overflow-y":"scroll"},attrs:{loading:_vm.loading,offset:20},on:{bottom:_vm.nextPage}},_vm._l(_vm.customModuleList,(function(row){return _c("div",{key:row.id,staticClass:"menu-item space-secondary-color sp-li ellipsis f12 pointer asset-item p20",class:{active:_vm.id===row.id},on:{click:function($event){return _vm.getLink(row.id)}}},[_c("span",{directives:[{name:"tippy",rawName:"v-tippy",value:{placement:"top",animation:"shift-away",arrow:!0},expression:"{\n                    placement: 'top',\n                    animation: 'shift-away',\n                    arrow: true,\n                  }"}],staticClass:"label",attrs:{title:row[_vm.mainFieldKey]}},[_vm._v(_vm._s(row[_vm.mainFieldKey]))])])})),0)],1),_vm.loading?_c("ul",{staticClass:"sp-ul"},[_c("spinner",{attrs:{show:_vm.loading,size:"80"}})],1):_vm._e(),_vm.loadingLists?_c("ul",{staticClass:"sp-ul"},[_c("spinner",{attrs:{show:_vm.loadingLists,size:"60"}})],1):_vm._e()])])]),_c("div",{staticStyle:{flex:"1"}},[_c("router-view",{key:_vm.id})],1)])])},staticRenderFns=[],toConsumableArray=__webpack_require__(488478),objectSpread2=__webpack_require__(595082),v_infinite_scroll=(__webpack_require__(260228),__webpack_require__(947522),__webpack_require__(670560),__webpack_require__(425728),__webpack_require__(434284),__webpack_require__(564043),__webpack_require__(61514),__webpack_require__(634338),__webpack_require__(821694),__webpack_require__(76265),__webpack_require__(339772),__webpack_require__(162506),__webpack_require__(857267),__webpack_require__(169358),__webpack_require__(561364)),v_infinite_scroll_default=__webpack_require__.n(v_infinite_scroll),validation=__webpack_require__(990260),router=__webpack_require__(329435),vuex_esm=__webpack_require__(420629),ModuleListOverviewvue_type_script_lang_js={data:function(){return{toggle:!1,selectindex:!1,loading:!0,loadingLists:!1,page:1,showQuickSearch:!1,quickSearchQuery:null,fetchingMore:!1}},components:{VInfiniteScroll:v_infinite_scroll_default()},watch:{currentView:function(newVal,oldVal){newVal&&oldVal!==newVal&&this.init()},$route:{handler:function(newVal,oldVal){newVal&&oldVal!==newVal&&this.init()}},moduleName:function(newVal,oldVal){newVal&&oldVal!==newVal&&this.loadData()},customModuleList:function(newVal){this.id||this.getLink(newVal[0].id)}},created:function(){this.initial()},computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapState */.rn)({canLoadMore:function(state){return state.customModule.canLoadMore},groupViews:function(state){return state.view.groupViews},customModuleList:function(state){return state.customModule.customModuleList},searchQuery:function(state){return state.customModule.quickSearchQuery}})),{},{parentPath:function(){return this.$helpers.isEtisalat()?"/app/".concat(this.getEtisalatRouterName().modulePath):"/app/ca/modules"},scrollDisabled:function(){return this.loading||!this.canLoadMore||this.fetchingMore},id:function(){var paramId=this.$attrs.id||this.$route.params.id,id=paramId&&"null"!==paramId?parseInt(this.$route.params.id):"";return id},views:function(){var views=[];return this.groupViews&&Array.isArray(this.groupViews)&&this.groupViews.forEach((function(group){views.push.apply(views,(0,toConsumableArray/* default */.Z)(group.views))})),views},moduleName:function(){return this.$route.params.moduleName?this.$route.params.moduleName:this.$attrs.moduleName||""},currentView:function(){var filteredListView=this.views.find((function(view){return"filteredList"===view.name}));return this.$route.query.search&&(0,validation/* isEmpty */.xb)(filteredListView)&&this.views.push({displayName:"Filtered List",name:"filteredList"}),this.$route.query.search?"filteredList":(0,validation/* isEmpty */.xb)(this.$attrs.viewname)?(0,validation/* isEmpty */.xb)(this.$route.params.viewname)&&(0,validation/* isEmpty */.xb)(this.$route.params.viewName)?"all":this.$route.params.viewname||this.$route.params.viewName:this.$attrs.viewname},currentViewDetail:function(){var _this=this;return this.$route.query.search?{displayName:"Filtered List",name:"filteredList"}:this.views.find((function(view){return view.name===_this.currentView}))||{}},mainFieldKey:function(){return"name"}}),methods:{getLink:function(id){var view;if(null!==id)if(view="filteredList"===this.currentView?this.$attrs.viewname||this.$route.params.viewname||this.$route.params.viewName:this.currentView,(0,router/* isWebTabsEnabled */.tj)()){var _ref=(0,router/* findRouteForModule */.Jp)(this.moduleName,router/* pageTypes */.As.OVERVIEW)||{},name=_ref.name;name&&this.$router.push({name:name,params:{id:id,viewname:view},query:this.$route.query})}else this.$router.push({path:"".concat(this.parentPath,"/").concat(this.moduleName,"/").concat(view,"/").concat(id,"/summary"),query:this.$route.query})},init:function(){this.loadData()},initial:function(){var _this2=this;this.$store.dispatch("view/clearViews"),this.loadData();var params={id:this.id,moduleName:this.moduleName},promises=[this.loadViews(),this.$store.dispatch("customModule/fetch",params)];Promise.all(promises).then((function(){_this2.loadModuleMeta(_this2.moduleName)}))},loadModuleMeta:function(moduleName){return this.$store.dispatch("view/loadModuleMeta",moduleName)},switchView:function(view){var _this3=this,id=this.id,parentPath=this.parentPath,moduleName=this.moduleName,filterIndex=this.views.findIndex((function(view){return"filteredList"===view.name}));-1!==filterIndex&&this.views.splice(filterIndex,1),this.loadModuleMeta(view.moduleName).then((function(){if((0,router/* isWebTabsEnabled */.tj)()){var _findRouteForModule=(0,router/* findRouteForModule */.Jp)(moduleName,router/* pageTypes */.As.OVERVIEW),name=_findRouteForModule.name;name&&_this3.$router.replace({name:name,params:{viewname:view.name,id:null}})}else _this3.$router.replace({path:"".concat(parentPath,"/").concat(moduleName,"/").concat(view.name,"/").concat(id,"/summary")})})),this.page=1,this.toggle=!1},loadViews:function(){var moduleName=this.moduleName,param={moduleName:moduleName};return this.$store.dispatch("view/loadGroupViews",param)},nextPage:function(){this.scrollDisabled||(this.loadingLists=!0,this.fetchingMore=!0,this.loadData(!0))},loadData:function(loadMore){var viewName,_this4=this,moduleName=this.moduleName,currentView=this.currentView,page=this.page;this.page=loadMore?this.page:1,viewName="filteredList"===currentView?this.$route.params.viewname||this.$route.params.viewName:currentView;var queryObj={viewname:viewName,page:page,filters:this.$route.query.search?JSON.parse(this.$route.query.search):"",includeParentFilter:!0,moduleName:moduleName};loadMore?this.fetchingMore=!0:this.loading=!0,this.$store.dispatch("customModule/fetchCustomModuleList",queryObj).then((function(response){_this4.loading=!1,_this4.loadingLists=!1,_this4.fetchingMore=!1,_this4.page++})).catch((function(error){error&&(_this4.loading=!1,_this4.fetchingMore=!1)}))},toggleQuickSearch:function(){this.showQuickSearch=!this.showQuickSearch},closeSearch:function(){this.toggleQuickSearch(),this.quickSearchQuery=null,this.quickSearch()},quickSearch:function(){this.$store.dispatch("customModule/updateSearchQuery",this.quickSearchQuery)},back:function(){var moduleName=this.moduleName,currentView=this.currentView,$route=this.$route;if("filteredList"===currentView&&(currentView=this.$attrs.viewname||this.$route.params.viewname||this.$route.params.viewName),(0,router/* isWebTabsEnabled */.tj)()){var _findRouteForModule2=(0,router/* findRouteForModule */.Jp)(moduleName,router/* pageTypes */.As.LIST),name=_findRouteForModule2.name;name&&this.$router.push({name:name,params:{viewname:currentView},query:$route.query})}else this.$router.push({path:"".concat(this.parentPath,"/").concat(moduleName,"/").concat(currentView)})},getEtisalatRouterName:function(){var data={new:"custommodules-new",edit:"custommodules-edit",list:"custommodules-list",summary:"custommodules-summary"};if(this.$route&&this.$route.name){var name=this.$route.name;"et1custommodules-list"===name||"et1custommodules-new"===name||"et1custommodules-edit"===name||"et1custommodules-summary"===name?(data["new"]="et1custommodules-new",data["edit"]="et1custommodules-edit",data["list"]="et1custommodules-edit",data["summary"]="et1custommodules-summary",data["modulePath"]="supp"):"et-custommodules-list"===name||"et-custommodules-edit"===name||"et-custommodules-new"===name||"et-custommodules-summary"===name?(data["new"]="et-custommodules-new",data["edit"]="et-custommodules-edit",data["list"]="et-custommodules-list",data["summary"]="et-custommodules-summary",data["modulePath"]="al"):"et2-custommodules-list"!==name&&"et2-custommodules-edit"!==name&&"et2-custommodules-new"!==name&&"et2-custommodules-summary"!==name||(data["new"]="et2-custommodules-new",data["edit"]="et2-custommodules-edit",data["list"]="et2-custommodules-list",data["summary"]="et2-custommodules-summary",data["modulePath"]="home")}return data}}},base_module_v2_ModuleListOverviewvue_type_script_lang_js=ModuleListOverviewvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(base_module_v2_ModuleListOverviewvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,ModuleListOverview=component.exports},
/***/33842:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return BookingSummaryList}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/facilitybooking/booking/BookingSummaryList.vue?vue&type=template&id=398d4103
var render=function(){var this$1=this,_vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",[_c("div",{staticClass:"height100 d-flex"},[_c("div",{staticStyle:{flex:"0 0 300px","max-width":"300px"}},[_c("div",{staticClass:"height100vh full-layout-white fc-border-left fc-border-right"},[_c("div",{staticClass:"row p15 border-bottom21 pointer"},[_c("div",{staticClass:"col-1 text-left"},[_c("i",{staticClass:"el-icon-back fw6",staticStyle:{"vertical-align":"sub"},on:{click:_vm.back}})]),_c("el-popover",{attrs:{placement:"bottom",width:"250","popper-class":"popover-height asset-popover ",trigger:"click","visible-arrow":"true"},model:{value:_vm.toggle,callback:function($$v){_vm.toggle=$$v},expression:"toggle"}},[_c("ul",_vm._l(_vm.views,(function(view,index){return _c("li",{key:index,class:{active:_vm.currentView===view.name},on:{click:function($event){return _vm.switchView(view)}}},[_vm._v(" "+_vm._s(view.displayName)+" ")])})),0),_c("span",{staticClass:"line-height20 bold",attrs:{slot:"reference"},slot:"reference"},[_vm._v(" "+_vm._s(_vm.currentViewDetail.displayName)+" "),_c("i",{staticClass:"el-icon-arrow-down el-icon-arrow-down-tv",staticStyle:{"padding-left":"8px"}})])]),_vm.showQuickSearch?_c("div",{staticClass:"row"},[_c("div",{staticClass:"col-12 fc-list-search"},[_c("div",{staticClass:"fc-list-search-wrapper fc-list-search-wrapper-asset"},[_c("svg",{staticClass:"search-icon-asset",attrs:{xmlns:"http://www.w3.org/2000/svg",width:"32",height:"32",viewBox:"0 0 32 32"}},[_c("title",[_vm._v("search")]),_c("path",{attrs:{d:"M21.487 22.927l7.037 7.037c.473.473 1.238.475 1.71.003s.47-1.237-.003-1.71l-7.037-7.037c3.96-4.82 3.675-11.967-.846-16.487C17.539-.076 9.757-.092 4.966 4.699S.191 17.272 5 22.081c4.52 4.52 11.667 4.805 16.487.846zM6.679 6.413c3.848-3.848 10.099-3.836 13.962.027s3.875 10.114.027 13.962c-3.848 3.848-10.099 3.836-13.962-.027S2.831 10.261 6.679 6.413z"}})]),_c("input",{directives:[{name:"model",rawName:"v-model",value:_vm.quickSearchQuery,expression:"quickSearchQuery"}],ref:"quickSearchQuery",staticClass:"quick-search-input-asset",attrs:{autofocus:"",type:"text",placeholder:"Search"},domProps:{value:_vm.quickSearchQuery},on:{keyup:function($event){return!$event.type.indexOf("key")&&_vm._k($event.keyCode,"enter",13,$event.key,"Enter")?null:_vm.quickSearch.apply(null,arguments)},input:function($event){$event.target.composing||(_vm.quickSearchQuery=$event.target.value)}}}),_c("svg",{staticClass:"close-icon-asset",attrs:{xmlns:"http://www.w3.org/2000/svg",width:"32",height:"32",viewBox:"0 0 32 32","aria-hidden":"true"},on:{click:_vm.closeSearch}},[_c("title",[_vm._v("close")]),_c("path",{attrs:{d:"M17.992 16l8.796-8.796a1.409 1.409 0 0 0-1.992-1.992L16 14.008 7.204 5.212a1.409 1.409 0 0 0-1.992 1.992L14.008 16l-8.796 8.796a1.409 1.409 0 0 0 1.992 1.992L16 17.992l8.796 8.796a1.409 1.409 0 0 0 1.992-1.992L17.992 16z"}})])])])]):_vm._e()],1),_c("div",{staticClass:"row sp-navbar2"},[_vm.loading?_c("ul",{staticClass:"sp-ul"},[_c("spinner",{attrs:{show:!0,size:"80"}})],1):_vm._e(),_c("ul",{staticClass:"sp-ul mT0"},[_c("v-infinite-scroll",{staticStyle:{height:"100vh","padding-bottom":"100px","overflow-y":"scroll"},attrs:{loading:_vm.loading,offset:20},on:{bottom:_vm.nextPage}},_vm._l(_vm.customModuleList,(function(row,index){return _c("div",{key:index,staticClass:"menu-item space-secondary-color sp-li ellipsis f12 pointer asset-item p20 border-bottom21",class:{active:_vm.id===row.id},on:{click:function($event){return _vm.getLink(row.id)}}},[_c("el-row",[_c("el-col",{attrs:{span:24}},[_c("div",{staticClass:"booking-id"},[_vm._v(" "+_vm._s("#"+row.localId)+" ")]),_c("div",{directives:[{name:"tippy",rawName:"v-tippy",value:{placement:"top",animation:"shift-away",arrow:!0},expression:"{\n                        placement: 'top',\n                        animation: 'shift-away',\n                        arrow: true,\n                      }"}],staticClass:"booking-black-txt truncate-text",attrs:{title:_vm.$getProperty(row,"facility.name","---")}},[_vm._v(" "+_vm._s(_vm.$getProperty(row,"facility.name","---"))+" ")]),_c("div",{staticClass:"booking-black-txt d-flex flex-row"},[_vm._v(" Booked By "),_c("div",{staticClass:"booking-blue-txt textoverflow-ellipsis mL5"},[_vm._v(" "+_vm._s(_vm.$getProperty(row,"reservedFor.name","---"))+" ")])])])],1)],1)})),0)],1),_vm.loadingLists?_c("ul",{staticClass:"sp-ul"},[_c("spinner",{attrs:{show:!0,size:"80"}})],1):_vm._e()])])]),_c("div",{staticStyle:{flex:"1"}},[_c("router-view",{key:_vm.id,on:{refreshSummaryList:function(){this$1.loadData(!1,!0)}}})],1)])])},staticRenderFns=[],regeneratorRuntime=__webpack_require__(150124),toConsumableArray=__webpack_require__(488478),asyncToGenerator=__webpack_require__(548534),objectSpread2=__webpack_require__(595082),ModuleListOverview=(__webpack_require__(260228),__webpack_require__(821694),__webpack_require__(76265),__webpack_require__(434284),__webpack_require__(670560),__webpack_require__(339772),__webpack_require__(162506),__webpack_require__(564043),__webpack_require__(857267),__webpack_require__(61514),__webpack_require__(648324),__webpack_require__(169358),__webpack_require__(634338),__webpack_require__(310682)),api=__webpack_require__(32284),vuex_esm=__webpack_require__(420629),router=__webpack_require__(329435),BookingSummaryListvue_type_script_lang_js={extends:ModuleListOverview/* default */.Z,data:function(){return{facilityBookingList:[],quickSearchQuery:"",canLoadMoreList:!1}},computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapGetters */.Se)(["getTicketStatus"])),{},{moduleName:function(){return"facilitybooking"},customModuleList:function(){return this.facilityBookingList},searchQuery:function(){return this.quickSearchQuery},canLoadMore:function(){return this.canLoadMoreList},mainFieldKey:function(){return"facility.name"}}),watch:{searchQuery:function(){this.loadData()}},components:{},methods:{initial:function(){var _this=this,promises=[this.loadViews(),this.$store.dispatch("view/clearViews"),this.loadData(!1,!0)];Promise.all(promises).then((function(){_this.loadModuleMeta(_this.moduleName)}))},getLink:function(id){if(null!==id)if((0,router/* isWebTabsEnabled */.tj)()){var _ref=(0,router/* findRouteForModule */.Jp)(this.moduleName,router/* pageTypes */.As.OVERVIEW)||{},name=_ref.name;name&&this.$router.push({name:name,params:{id:id,viewname:this.currentView}})}else this.$router.push({name:"bookingSummary",params:{viewName:this.currentView,id:id}})},nextPage:function(){!this.scrollDisabled&&this.canLoadMoreList&&(this.loadingLists=!0,this.fetchingMore=!0,this.page++,this.loadData(!0))},switchView:function(view){var filterIndex=this.views.findIndex((function(view){return"filteredList"===view.name}));if(-1!==filterIndex&&this.views.splice(filterIndex,1),(0,router/* isWebTabsEnabled */.tj)()){var _findRouteForModule=(0,router/* findRouteForModule */.Jp)(this.moduleName,router/* pageTypes */.As.OVERVIEW),name=_findRouteForModule.name;name&&this.$router.replace({name:name,params:{viewname:view.name,id:null}})}else{var url="/app/bk/facilitybooking/".concat(view.name,"/",null,"/overview");this.$router.replace({path:url})}this.page=1,this.toggle=!1},loadData:function(loadMore,forceFetch){var _this2=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var currentView,page,queryObj,_yield$API$fetchAll,list,error,meta,_error$message,message,listCount;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return currentView=_this2.currentView,page=_this2.page,queryObj={viewName:currentView,page:page,filters:_this2.$route.query.search?JSON.stringify(JSON.parse(_this2.$route.query.search)):"",search:_this2.searchQuery,includeParentFilter:"filteredList"===currentView,perPage:50,withCount:!0},loadMore?_this2.fetchingMore=!0:_this2.loading=!0,_context.next=5,api/* API */.bl.fetchAll(_this2.moduleName,queryObj,{force:forceFetch});case 5:_yield$API$fetchAll=_context.sent,list=_yield$API$fetchAll.list,error=_yield$API$fetchAll.error,meta=_yield$API$fetchAll.meta,error?(_error$message=error.message,message=void 0===_error$message?"Error Occured while fetching Booking list":_error$message,_this2.$message.error(message)):(forceFetch?_this2.facilityBookingList=list:loadMore&&(_this2.facilityBookingList=[].concat((0,toConsumableArray/* default */.Z)(_this2.facilityBookingList||[]),(0,toConsumableArray/* default */.Z)(list))),listCount=_this2.$getProperty(meta,"pagination.totalCount",null),_this2.canLoadMoreList=listCount>(_this2.facilityBookingList||[]).length),_this2.loading=!1,_this2.loadingLists=!1,_this2.fetchingMore=!1;case 13:case"end":return _context.stop()}}),_callee)})))()},back:function(){var currentView=this.currentView,moduleName=this.moduleName;if((0,router/* isWebTabsEnabled */.tj)()){var _ref2=(0,router/* findRouteForModule */.Jp)(moduleName,router/* pageTypes */.As.LIST)||{},name=_ref2.name;name&&this.$router.push({name:name,params:{viewname:currentView}})}else this.$router.push({name:"bookingList",params:{viewName:currentView}})},getTicketStatusDisplayName:function(record){return(this.getTicketStatus(this.$getProperty(record,"moduleState.id",-1),this.moduleName)||{}).displayName}}},booking_BookingSummaryListvue_type_script_lang_js=BookingSummaryListvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(booking_BookingSummaryListvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,BookingSummaryList=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/33842.js.map