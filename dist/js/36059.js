"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[36059],{
/***/703117:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return AlarmModel}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/AlarmModel.vue?vue&type=template&id=5b251d32
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticStyle:{background:"#fff",width:"400px",height:"420px !important"}},[_vm._m(0),_c("div",{staticClass:"model-container",staticStyle:{background:"#fff"}},[_c("p",{staticClass:"fc-input-label-txt"},[_vm._v("Category")]),_c("el-select",{staticClass:"fc-input-full-border2 width100",model:{value:_vm.alarmRequest.category,callback:function($$v){_vm.$set(_vm.alarmRequest,"category",$$v)},expression:"alarmRequest.category"}},_vm._l(_vm.ticketcategory,(function(category){return _c("el-option",{key:category.id,attrs:{label:category.displayName,value:category.id}})})),1),_c("p",{staticClass:"fc-input-label-txt mT20"},[_vm._v("Team/Staff")]),_c("div",{staticStyle:{border:"1px solid #d8dce5",display:"inline-block",position:"relative",width:"100%",height:"40px","line-height":"40px","padding-left":"10px","padding-right":"10px","border-radius":"3px"}},[_c("span",[_vm._v(_vm._s(_vm.getTeamStaffLabel(_vm.alarmRequest)))]),_vm._m(1),_c("f-assignment",{attrs:{model:_vm.alarmRequest,viewtype:"form"}})],1),_c("p",{staticClass:"fc-input-label-txt mT20"},[_vm._v("Priority")]),_c("el-select",{staticClass:"fc-input-full-border2 width100",model:{value:_vm.alarmRequest.priority,callback:function($$v){_vm.$set(_vm.alarmRequest,"priority",$$v)},expression:"alarmRequest.priority"}},_vm._l(_vm.ticketpriority,(function(priority){return _c("el-option",{key:priority.priority,attrs:{label:priority.displayName,value:parseInt(priority.id)}})})),1),_c("p",{staticClass:"fc-input-label-txt mT20"},[_vm._v("Site")]),_c("el-select",{directives:[{name:"tippy",rawName:"v-tippy"}],staticClass:"fc-input-full-border2 width100",attrs:{filterable:"",clearable:"","collapse-tags":"",placeholder:"Sites"},model:{value:_vm.alarmRequest.siteId,callback:function($$v){_vm.$set(_vm.alarmRequest,"siteId",$$v)},expression:"alarmRequest.siteId"}},_vm._l(_vm.sites,(function(site,index){return _c("el-option",{key:index,attrs:{label:site.name,value:site.id}})})),1)],1),_c("div",{staticClass:"modal-dialog-footer",staticStyle:{background:"#fff"}},[_c("el-button",{staticClass:"modal-btn-cancel",on:{click:_vm.closeDialog}},[_vm._v("CANCEL")]),_c("el-button",{staticClass:"modal-btn-save",on:{click:function($event){return _vm.createWO()}}},[_vm._v("CREATE")])],1)])},staticRenderFns=[function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticStyle:{padding:"20px 20px 10px 20px"},attrs:{label:"Create Workorder"}},[_c("div",{staticClass:"setup-modal-title"},[_vm._v("Create workorder")])])},function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("span",{staticStyle:{float:"right"}},[_c("i",{staticClass:"el-icon-arrow-down",staticStyle:{position:"relative",top:"-4px",right:"3px",color:"#c0c4cc"}})])}],objectSpread2=__webpack_require__(595082),vuex_esm=__webpack_require__(420629),FAssignment=__webpack_require__(383274),TeamStaffMixin=__webpack_require__(462599),AlarmModelvue_type_script_lang_js={props:["id"],components:{FAssignment:FAssignment/* default */.Z},mixins:[TeamStaffMixin/* default */.Z],data:function(){return{alarmRequest:{category:null,assignedTo:{id:-1},assignmentGroup:{id:-1},siteId:null,priority:null}}},created:function(){this.$store.dispatch("loadTicketCategory"),this.$store.dispatch("loadTicketPriority"),this.$store.dispatch("loadGroups")},computed:(0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapState */.rn)({users:function(state){return state.users},sites:function(state){return state.sites},ticketcategory:function(state){return state.ticketCategory},ticketpriority:function(state){return state.ticketPriority}})),methods:{createWO:function(event){this.$emit("submit",this.alarmRequest)},closeDialog:function(){this.reset(),this.$emit("closed")},reset:function(){this.alarmRequest={category:null,assignedTo:{id:-1},assignmentGroup:{id:-1},priority:null}}}},components_AlarmModelvue_type_script_lang_js=AlarmModelvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_AlarmModelvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,AlarmModel=component.exports},
/***/536059:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return BmsAlarmsList}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/firealarm/alarms/alarms/v3/BmsAlarmsList.vue?vue&type=template&id=74368c4e&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _vm.isSummaryOpen?_c("div",{staticClass:"height100"},[_c("div",{staticClass:"flex flex-row"},[_c("div",{staticClass:"cm-side-bar-container"},[_c("SummarySidebar",{staticClass:"purchaseorder-summary-list",attrs:{list:_vm.records,isLoading:_vm.isLoading,activeRecordId:_vm.selectedRecordId,total:_vm.recordCount,currentCount:(_vm.records||[]).length},on:{"update:isLoading":function($event){_vm.isLoading=$event},"update:is-loading":function($event){_vm.isLoading=$event}},scopedSlots:_vm._u([{key:"title",fn:function(){return[_c("el-row",{staticClass:"cm-sidebar-header"},[_c("el-col",{attrs:{span:2}},[_c("span",{on:{click:function($event){return _vm.openList()}}},[_c("inline-svg",{staticClass:"rotate-90 pointer",attrs:{src:"svgs/arrow",iconClass:"icon icon-sm"}})],1)]),_c("el-col",{attrs:{span:22}},[_c("div",{staticClass:"bold"},[_vm._v(_vm._s(_vm.currentViewDetail))])])],1)]},proxy:!0},{key:"default",fn:function(ref){var record=ref.record;return[_c("router-link",{staticClass:" label-txt-black link-hover-decoration",attrs:{tag:"div",to:_vm.redirectToOverview(record.id)}},[_c("div",{staticClass:"inline-flex justify-content-space pT20 pB10"},[_c("div",{directives:[{name:"tippy",rawName:"v-tippy",value:_vm.tippyOptions,expression:"tippyOptions"}],staticClass:"summary-item-heading",attrs:{title:record[_vm.mainFieldName]||"---"}},[_vm._v(" "+_vm._s(record[_vm.mainFieldName]||"---")+" ")]),_c("div",{staticClass:"flex-middle pR10"},[_c("span",[_c("i",{staticClass:"fa fa-circle prioritytag",style:{color:_vm.getAlarmColor(record)},attrs:{"aria-hidden":"true"}})]),_c("span",{staticClass:" uppercase secondary-color severityTagSummary"},[_vm._v(" "+_vm._s(_vm.getAlarmDisplayName(record))+" ")])])]),_c("div",{staticClass:"pL20 pB20"},[_c("div",{staticClass:"fc-grey2-text12 max-width70"},[_c("fc-icon",{staticClass:"pR10",attrs:{group:"default",name:"assets",color:"#8ca1ae",size:"17"}}),record.hasOwnProperty("agent")?_c("span",[_vm._v(_vm._s(_vm.$getProperty(record,"agent.name","---")))]):_c("span",{directives:[{name:"tippy",rawName:"v-tippy",value:_vm.tippyOptions,expression:"tippyOptions"}],staticClass:"textoverflow-ellipsis inline-block width160px",attrs:{title:_vm.getResourceName(record)}},[_vm._v(_vm._s(_vm.getResourceName(record)))])],1)])])]}}],null,!1,677440605)})],1),_c("div",{staticClass:"flex-1"},[_c("router-view",{attrs:{viewname:_vm.viewname}})],1)])]):_c("CommonListLayout",{key:_vm.moduleName+"-list-layout",staticClass:"custom-module-list-layout",attrs:{moduleName:_vm.moduleName,showViewRearrange:!0,showViewEdit:!0,visibleViewCount:3,getPageTitle:function(){return _vm.moduleDisplayName},pathPrefix:_vm.parentPath,hideSubHeader:_vm.canHideSubHeader,recordCount:_vm.recordCount,recordLoading:_vm.showLoading},scopedSlots:_vm._u([{key:"header",fn:function(){return[_vm.canHideFilter?_vm._e():_c("AdvancedSearchWrapper",{attrs:{filters:_vm.filters,moduleName:_vm.moduleName,moduleDisplayName:_vm.moduleDisplayName}}),_vm.canShowVisualSwitch?_c("visual-type",{on:{onSwitchVisualize:function(val){return _vm.canShowListView=val}}}):_vm._e(),_c("CustomButton",{staticClass:"custom-button",attrs:{moduleName:_vm.moduleName,position:_vm.POSITION.LIST_TOP},on:{onSuccess:_vm.refreshRecordDetails,onError:function(){}}})]},proxy:!0},_vm.canShowCalendarHeader?{key:"sub-header",fn:function(){return[_vm.showListView?_vm._e():_c("CalendarDateWrapper")]},proxy:!0}:null,{key:"sub-header-actions",fn:function(){return[!_vm.isEmpty(_vm.records)&&_vm.showListView?[_c("pagination",{staticClass:"pL15 fc-black-small-txt-12",attrs:{total:_vm.recordCount,currentPageCount:_vm.currentPageCount,perPage:_vm.perPage,skipTotalCount:!0}}),_vm.recordCount>0?_c("span",{staticClass:"separator"},[_vm._v("|")]):_vm._e(),_c("el-tooltip",{attrs:{effect:"dark",content:_vm.$t("common._common.sort"),"open-delay":500,placement:"top",tabindex:-1}},[_c("Sort",{key:_vm.moduleName+"-sort",attrs:{moduleName:_vm.moduleName},on:{onSortChange:_vm.updateSort}})],1),_vm.hasPermission("EXPORT")?_c("span",{staticClass:"separator"},[_vm._v("|")]):_vm._e(),_vm.hasPermission("EXPORT")?_c("el-tooltip",{attrs:{effect:"dark",content:_vm.$t("common._common.export"),"open-delay":500,placement:"top",tabindex:-1}},[_c("f-export-settings",{attrs:{module:_vm.moduleName,viewDetail:_vm.viewDetail,showViewScheduler:!1,showMail:!1,filters:_vm.filters}})],1):_vm._e()]:_vm._e()]},proxy:!0},{key:"content",fn:function(){return[_vm.showLoading?_c("Spinner",{staticClass:"mT40",attrs:{show:_vm.showLoading}}):_vm.showListView?[_vm.$validation.isEmpty(_vm.records)&&!_vm.showLoading?_c("div",{staticClass:"cm-empty-state-container"},[_c("img",{staticClass:"mT20 self-center",attrs:{src:__webpack_require__(15482),width:"100",height:"100"}}),_c("div",{staticClass:"mT10 label-txt-black f14 self-center"},[_vm._v(" "+_vm._s(_vm.emptyStateText)+" ")])]):_vm._e(),_vm.showLoading||_vm.$validation.isEmpty(_vm.records)?_vm._e():[_c("div",{staticClass:"cm-list-container"},[_c("div",{staticClass:"column-customization-icon",attrs:{disabled:!_vm.isColumnCustomizable},on:{click:_vm.toShowColumnSettings}},[_c("el-tooltip",{attrs:{disabled:_vm.isColumnCustomizable,placement:"top",content:_vm.$t("common._common.you_dont_have_permission")}},[_c("inline-svg",{staticClass:"text-center position-absolute icon",attrs:{src:"column-setting"}})],1)],1),_vm.selectedListItemsIds.length>0?_c("div",{staticClass:"pull-left table-header-actions"},[_c("div",{staticClass:"action-btn-slide btn-block"},[_vm.$hasPermission("bmsalarm:DELETE")?_c("button",{staticClass:"btn btn--tertiary pointer",class:{disabled:_vm.isLoading},on:{click:function($event){return _vm.deleteRecords(_vm.selectedListItemsIds)}}},[_vm.isLoading?_c("i",{staticClass:"fa fa-circle-o-notch b-icon fa-spin",attrs:{"aria-hidden":"true"}}):_c("i",{staticClass:"fa fa-trash-o b-icon"}),_vm._v(" "+_vm._s(_vm.$t("common._common.delete"))+" ")]):_vm._e()]),_c("CustomButton",{key:_vm.moduleName+"_"+_vm.viewname+"_"+_vm.POSITION.LIST_BAR,staticClass:"custom-button",attrs:{modelDataClass:_vm.modelDataClass,selectedRecords:_vm.selectedListItemsObj,moduleName:_vm.moduleName,position:_vm.POSITION.LIST_BAR},on:{onSuccess:_vm.refreshRecordDetails,onError:function(){}}})],1):_vm._e(),_c("CommonList",{attrs:{viewDetail:_vm.viewDetail,records:_vm.records,moduleName:_vm.moduleName,redirectToOverview:_vm.redirectToOverview,slotList:_vm.slotList,refreshList:_vm.onCustomButtonSuccess,isTableFreeze:_vm.isTableFreeze},on:{"selection-change":_vm.selectAlarm},scopedSlots:_vm._u([{key:_vm.slotList[0].name,fn:function(ref){var record=ref.record;return[_c("div",{staticClass:"self-center f10 secondary-color",staticStyle:{"min-width":"90px","margin-left":"13%"}},[_c("div",{staticClass:"q-item-label uppercase severityTag",style:{"background-color":_vm.getAlarmColor(record)}},[_vm._v(" "+_vm._s(_vm.getAlarmDisplayName(record))+" ")])])]}},{key:_vm.slotList[1].criteria,fn:function(ref){var record=ref.record;return[_c("div",{staticClass:"q-item-sublabel ellipsis",staticStyle:{"margin-top":"5px","font-size":"12px"}},[_c("span",{staticClass:"fc-id"},[_vm._v("#"+_vm._s(record.id))])]),_c("div",[_c("router-link",{staticClass:"d-flex fw5 label-txt-black ellipsis main-field-column",attrs:{to:_vm.redirectToOverview(record.id)}},[_c("el-tooltip",{attrs:{effect:"dark",content:record.subject||"---",placement:"top-end","open-delay":600}},[_c("div",{staticClass:"self-center width200px"},[_c("span",{staticClass:"list-main-field"},[_vm._v(_vm._s(record.subject||"---"))])])])],1)],1),_c("div",{staticClass:"ellipsis",staticStyle:{"font-size":"12px",color:"#8a8a8a","margin-top":"5px"}},[_c("span",{},[_vm._v(_vm._s(_vm._f("fromNow")(record.lastOccurredTime)))])])]}},{key:_vm.slotList[2].criteria,fn:function(ref){var record=ref.record;return[_c("router-link",{staticClass:"d-flex fw5 label-txt-black ellipsis main-field-column",attrs:{to:_vm.redirectToOverview(record.id)}},[_c("div",{staticStyle:{"min-width":"150px"}},[_c("div",{staticClass:"flLeft"},[_c("inline-svg",{staticClass:"flLeft",staticStyle:{width:"15px"},attrs:{src:"event"}})],1),_c("span",{staticClass:"q-item-label pL5",staticStyle:{"font-size":"13px","letter-spacing":"0.4px"}},[_vm._v(" "+_vm._s(_vm.getNoOfOccurrences(record))+" ")])])])]}},{key:_vm.slotList[3].criteria,fn:function(ref){var record=ref.record;return[_c("div",{staticStyle:{"min-width":"150px"}},[_vm.$hasPermission("bmsalarm:ACKNOWLEDGE_ALARM")?_c("div",{staticClass:"pull-left acknowledgeColumn"},[!record.acknowledged&&_vm.isActiveAlarm(record)?_c("el-button",{staticClass:"uppercase ack-btn",attrs:{size:"mini",plain:""},on:{click:function($event){return _vm.acknowledgeAlarm(record)}}},[_vm._v(_vm._s(_vm.$t("alarm.alarm.acknowledge")))]):_vm.isActiveAlarm(record)?_c("span",{staticClass:"q-item-label f11"},[_c("div",{staticClass:"self-center mL5"},[_c("user-avatar",{attrs:{size:"sm",user:record.acknowledgedBy}})],1),_c("div",{staticClass:"self-center mL5"},[_vm._v(" "+_vm._s(_vm.getAcknowledgedTime(record))+" ")])]):_c("span",{staticClass:"q-item-label"})],1):_vm._e()])]}},{key:_vm.slotList[4].name,fn:function(ref){var record=ref.record;return[_c("CustomButton",{key:_vm.moduleName+"_"+record.id+"_"+_vm.POSITION.LIST_ITEM,ref:"custom-btn-"+record.id,staticClass:"custom-button visibility-hide-actions",attrs:{moduleName:_vm.moduleName,position:_vm.POSITION.LIST_ITEM,record:record},on:{onSuccess:_vm.onCustomButtonSuccess,freezeRecord:function(val){return _vm.isTableFreeze=val},moreActionButtons:function(command){return _vm.handleDropDown(command,record)}}},[_vm.isActiveAlarm(record)&&_vm.$hasPermission("bmsalarm:CREATE_WORKORDER")?_c("el-dropdown-item",{attrs:{command:"createWo"}},[_vm.isWoCreated(record)?_c("div",[_vm._v(" "+_vm._s(_vm.$t("common._common.view_workorder"))+" ")]):_c("div",[_vm._v(" "+_vm._s(_vm.$t("common.wo_report.create_workorder"))+" ")])]):_vm._e(),_vm.$hasPermission("bmsalarm:DELETE")?_c("el-dropdown-item",{attrs:{command:"delete"}},[_vm._v(_vm._s(_vm.$t("common._common.delete"))+" ")]):_vm._e()],1)]}}],null,!0)})],1),_c("el-dialog",{attrs:{visible:_vm.dialogVisible,width:"32%","custom-class":"dialog"},on:{"update:visible":function($event){_vm.dialogVisible=$event}}},[_c("alarm-model",{ref:"confirmWoModel",on:{submit:_vm.createWO,closed:_vm.closeWoDialog}})],1)]]:_vm.showListView?_vm._e():_c("CalendarView",{ref:"calendar",attrs:{moduleName:_vm.moduleName,record:_vm.records,viewDetail:_vm.viewDetail,viewname:_vm.viewname,filters:_vm.filters}}),_c("column-customization",{attrs:{visible:_vm.showColumnSettings,moduleName:_vm.moduleName,viewName:_vm.viewname},on:{"update:visible":function($event){_vm.showColumnSettings=$event}}})]},proxy:!0}],null,!0)},[_c("portal",{attrs:{to:"view-manager-link"}},[_c("router-link",{staticClass:"view-manager-btn",attrs:{tag:"div",to:{path:"/app/fa/bmsalarms/bmsalarm/viewmanager"}}},[_c("inline-svg",{staticClass:"d-flex",attrs:{src:"svgs/hamburger-menu",iconClass:"icon icon-sm"}}),_c("span",{staticClass:"label mL10 text-uppercase"},[_vm._v(" "+_vm._s(_vm.$t("viewsmanager.list.views_manager"))+" ")])],1)],1)],1)},staticRenderFns=[],objectSpread2=__webpack_require__(595082),regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),router=(__webpack_require__(648324),__webpack_require__(450886),__webpack_require__(434284),__webpack_require__(670560),__webpack_require__(329435)),validation=__webpack_require__(990260),User=__webpack_require__(426803),api=__webpack_require__(32284),AlarmModel=__webpack_require__(703117),NewAlarmMixin=__webpack_require__(230255),CommonModuleList=__webpack_require__(347446),BmsAlarmsListvue_type_script_lang_js={extends:CommonModuleList["default"],name:"BmsAlarmsList",mixins:[NewAlarmMixin/* default */.Z],components:{UserAvatar:User/* default */.Z,AlarmModel:AlarmModel/* default */.Z},data:function(){return{dialogVisible:!1,createWoIds:[],isTableFreeze:!1,tippyOptions:{placement:"top",animation:"shift-away",arrow:!0}}},computed:{parentPath:function(){var modulePath=this.modulePath;return"/app/".concat(modulePath,"/bmsalarms/")},modulePath:function(){return"fa"},mainFieldName:function(){return"subject"},slotList:function(){return[{name:"severity",isHardcodedColumn:!0,columnAttrs:{"min-width":150,fixed:"left"}},{criteria:JSON.stringify({name:"subject"})},{criteria:JSON.stringify({name:"noOfOccurrences"})},{criteria:JSON.stringify({name:"acknowledgedBy"})},{name:"DeleteCreateWO",isActionColumn:!0,columnAttrs:{width:130,class:"visibility-visible-actions",fixed:"right",align:"right"}}]},emptyStateText:function(){return"No Data Available"}},methods:{init:function(){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:_this.$store.dispatch("loadTicketStatus",_this.moduleName||""),_this.$store.dispatch("loadTicketPriority"),_this.$store.dispatch("loadTicketCategory");case 3:case"end":return _context.stop()}}),_callee)})))()},isWoCreated:function(record){var _ref=record||{},lastWoId=_ref.lastWoId;return!(0,validation/* isEmpty */.xb)(lastWoId)},getResourceName:function(record){var _ref2=record||{},resource=_ref2.resource;return this.$getProperty(resource,"name","---")},selectAlarm:function(selectAlarmcheck){this.selectedListItemsObj=selectAlarmcheck,this.selectedListItemsIds=selectAlarmcheck.map((function(value){return value.id}))},redirectToOverview:function(id){var moduleName=this.moduleName,viewname=this.viewname;if((0,router/* isWebTabsEnabled */.tj)()){var _ref3=(0,router/* findRouteForModule */.Jp)(moduleName,router/* pageTypes */.As.OVERVIEW)||{},name=_ref3.name,route={name:name,params:{viewname:viewname,id:id},query:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},this.$route.query),{},{tab:"summary"})};return name&&route}return{name:"bmsalarm-summary",params:{moduleName:moduleName,viewname:viewname,id:id},query:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},this.$route.query),{},{tab:"summary"})}},handleDropDown:function(command,record){if("delete"===command)this.deleteRecords([record.id]);else{var woId=this.$getProperty(record,"lastWoId");woId>0?this.openWorkorder(woId):this.createWoDialog(record.lastOccurrenceId)}},acknowledgeAlarm:function(record){var _this2=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee2(){var lastOccurrenceId,dataObj,params,_yield$API$post,error;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context2){while(1)switch(_context2.prev=_context2.next){case 0:return lastOccurrenceId=record.lastOccurrenceId,dataObj={alarm:record,occurrence:{id:lastOccurrenceId||null},acknowledged:!0,acknowledgedBy:_this2.$account.user,acknowledgedTime:Date.now(),id:record.id},params={data:dataObj,moduleName:_this2.moduleName,id:dataObj.id},_context2.next=5,api/* API */.bl.post("v3/modules/data/patch",params);case 5:if(_yield$API$post=_context2.sent,error=_yield$API$post.error,!(0,validation/* isEmpty */.xb)(error)){_context2.next=10;break}return _context2.next=10,_this2.loadRecords(!0);case 10:case"end":return _context2.stop()}}),_callee2)})))()},openList:function(){var _this3=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee3(){var moduleName,viewname,$route,_ref4,query,_ref5,name;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context3){while(1)switch(_context3.prev=_context3.next){case 0:return moduleName=_this3.moduleName,viewname=_this3.viewname,$route=_this3.$route,_ref4=$route||{},query=_ref4.query,_context3.next=4,_this3.loadRecords(!0);case 4:(0,router/* isWebTabsEnabled */.tj)()?(_ref5=(0,router/* findRouteForModule */.Jp)(_this3.moduleName,router/* pageTypes */.As.LIST)||{},name=_ref5.name,name&&_this3.$router.push({name:name,params:{viewname:viewname},query:query})):_this3.$router.push({name:"bmsalarm-list",params:{moduleName:moduleName,viewname:viewname},query:query});case 5:case"end":return _context3.stop()}}),_callee3)})))()},unSelectRecords:function(){this.selectedListItemsIds=[],this.selectedListItemsObj=[]}}},v3_BmsAlarmsListvue_type_script_lang_js=BmsAlarmsListvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(v3_BmsAlarmsListvue_type_script_lang_js,render,staticRenderFns,!1,null,"74368c4e",null)
/* harmony default export */,BmsAlarmsList=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/36059.js.map