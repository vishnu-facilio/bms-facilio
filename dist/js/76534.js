"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[76534],{
/***/835002:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return JPWarningDialog}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/workorder/jobplan/JPWarningDialog.vue?vue&type=template&id=dca5fbce&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("el-dialog",{staticClass:"fc-dialog-center-container dialog-header-remove",attrs:{visible:_vm.showDialog,width:"35%","append-to-body":!0,"before-close":_vm.closeDialog},on:{"update:visible":function($event){_vm.showDialog=$event}}},[_c("div",{staticClass:"height180 d-flex"},[_c("i",{staticClass:"fa fa-exclamation-triangle pR5 pL10",attrs:{"aria-hidden":"true"}}),_c("div",{staticClass:"d-flex flex-direction-column mL10 width100"},[_c("div",{staticClass:"dialog-heading"},[_vm._v(" "+_vm._s(_vm.dialogHeader)+" ")]),_c("div",{staticClass:"dialog-content mT15",domProps:{innerHTML:_vm._s(_vm.dialogContent)}})])]),_c("div",{staticClass:"modal-dialog-footer"},[_c("el-button",{staticClass:"modal-btn-cancel",on:{click:_vm.closeDialog}},[_vm._v(_vm._s(_vm.$t("agent.agent.cancel")))]),_c("el-button",{staticClass:"modal-btn-save",attrs:{type:"primary",loading:_vm.stateUpdating},on:{click:_vm.saveDialog}},[_vm._v(_vm._s(_vm.buttonText))])],1)])},staticRenderFns=[],dialogHeaderHash=(__webpack_require__(634338),{jobplan:{Edit:"jobplan.edit_jp",Publish:"jobplan.publish_jp",Unpublish:"jobplan.unpublish_jp",Disable:"jobplan.disable_jp"},plannedmaintenance:{Edit:"maintenance.pm._edit_pm",Publish:"maintenance.pm.publish_pm",Unpublish:"maintenance.pm.unpublish_pm",Disable:"maintenance.pm.disable_pm"}}),dialogContentHash={jobplan:{Edit:"jobplan.edit_jp_content",Publish:"jobplan.publish_jp_content",Unpublish:"jobplan.unpublish_jp_content",Disable:"jobplan.disable_jp_content"},plannedmaintenance:{Edit:"maintenance.pm.edit_pm_content",Publish:"maintenance.pm.publish_pm_content",Unpublish:"maintenance.pm.unpublish_pm_content",Disable:"maintenance.pm.disable_pm_content"}},buttonTextHash={jobplan:{Edit:"jobplan.edit",Publish:"jobplan.publish",Unpublish:"jobplan.unpublish",Disable:"jobplan.disable"},plannedmaintenance:{Edit:"jobplan.edit",Publish:"jobplan.publish",Unpublish:"jobplan.unpublish",Disable:"jobplan.disable"}},JPWarningDialogvue_type_script_lang_js={props:["showDialog","dialogType","moduleName","stateUpdating","recordId"],computed:{dialogHeader:function(){var dialogType=this.dialogType,moduleName=this.moduleName,dialogHeader=this.$getProperty(dialogHeaderHash,"".concat(moduleName,".").concat(dialogType),"jobplan.edit");return this.$t("".concat(dialogHeader))},dialogContent:function(){var dialogType=this.dialogType,moduleName=this.moduleName,dialogContent=this.$getProperty(dialogContentHash,"".concat(moduleName,".").concat(dialogType),"jobplan.edit_content");return this.$t("".concat(dialogContent))},buttonText:function(){var dialogType=this.dialogType,moduleName=this.moduleName,buttonText=this.$getProperty(buttonTextHash,"".concat(moduleName,".").concat(dialogType),"jobplan.edit");return this.$t("".concat(buttonText))}},methods:{closeDialog:function(){this.$emit("closeDialog")},saveDialog:function(){var dialogType=this.dialogType;this.$emit("saveAction",dialogType)}}},jobplan_JPWarningDialogvue_type_script_lang_js=JPWarningDialogvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(jobplan_JPWarningDialogvue_type_script_lang_js,render,staticRenderFns,!1,null,"dca5fbce",null)
/* harmony default export */,JPWarningDialog=component.exports},
/***/876534:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return JobPlanList}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/workorder/jobplan/JobPlanList.vue?vue&type=template&id=4b4a9eeb&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _vm.isSummaryOpen?_c("div",{staticClass:"height100"},[_vm.showLoading?_c("div",{staticClass:"flex-middle fc-empty-white"},[_c("spinner",{attrs:{show:_vm.showLoading,size:"80"}})],1):_c("div",{staticClass:"flex flex-row"},[_c("div",{staticClass:"flex-1"},[_c("router-view",{attrs:{viewname:_vm.viewname}})],1)])]):_c("CommonListLayout",{key:_vm.moduleName+"-list-layout",staticClass:"custom-module-list-layout",attrs:{moduleName:_vm.moduleName,showViewRearrange:!0,showViewEdit:!0,visibleViewCount:3,getPageTitle:function(){return _vm.moduleDisplayName},pathPrefix:_vm.parentPath,hideSubHeader:_vm.canHideSubHeader,recordCount:_vm.recordCount,recordLoading:_vm.showLoading},scopedSlots:_vm._u([{key:"header",fn:function(){return[_vm.canHideFilter?_vm._e():_c("AdvancedSearchWrapper",{key:"ftags-list-"+_vm.moduleName,attrs:{filters:_vm.filters,moduleName:_vm.moduleName,moduleDisplayName:_vm.moduleDisplayName}}),_vm.canShowVisualSwitch?[_c("visual-type",{on:{onSwitchVisualize:function(val){return _vm.canShowListView=val}}})]:_vm._e(),_c("CustomButton",{staticClass:"custom-button",attrs:{moduleName:_vm.moduleName,position:_vm.POSITION.LIST_TOP},on:{onSuccess:_vm.onCustomButtonSuccess,onError:function(){}}}),_vm.hasPermission("CREATE")?[_c("button",{staticClass:"fc-create-btn ",on:{click:function($event){return _vm.redirectToFormCreation()}}},[_vm._v(" "+_vm._s(_vm.$t("custommodules.list.new"))+" "+_vm._s(_vm.moduleDisplayName?_vm.moduleDisplayName:"")+" ")])]:_vm._e()]},proxy:!0},{key:"sub-header-actions",fn:function(){return[!_vm.isEmpty(_vm.records)&&_vm.showListView?[_c("pagination",{staticClass:"pL15 fc-black-small-txt-12",attrs:{total:_vm.recordCount,currentPageCount:_vm.currentPageCount,perPage:_vm.perPage,skipTotalCount:!0}}),_vm.recordCount>0?_c("span",{staticClass:"separator"},[_vm._v("|")]):_vm._e(),_c("el-tooltip",{attrs:{effect:"dark",content:_vm.$t("common._common.sort"),"open-delay":500,placement:"top",tabindex:-1}},[_c("Sort",{key:_vm.moduleName+"-sort",attrs:{moduleName:_vm.moduleName},on:{onSortChange:_vm.updateSort}})],1),_vm.hasPermission("EXPORT")?_c("span",{staticClass:"separator"},[_vm._v("|")]):_vm._e(),_vm.hasPermission("EXPORT")?_c("el-tooltip",{attrs:{effect:"dark",content:_vm.$t("common._common.export"),"open-delay":500,placement:"top",tabindex:-1}},[_c("f-export-settings",{attrs:{module:_vm.moduleName,viewDetail:_vm.viewDetail,showViewScheduler:!1,showMail:!1,filters:_vm.filters}})],1):_vm._e()]:_vm._e()]},proxy:!0},{key:"content",fn:function(){return[_vm.showLoading?_c("Spinner",{staticClass:"mT40",attrs:{show:_vm.showLoading}}):_vm._e(),_vm.$validation.isEmpty(_vm.records)&&!_vm.showLoading?_c("div",{staticClass:"cm-empty-state-container"},[_c("img",{staticClass:"mT20 self-center",attrs:{src:__webpack_require__(15482),width:"100",height:"100"}}),_c("div",{staticClass:"mT10 label-txt-black f14 self-center"},[_vm._v(" "+_vm._s(_vm.emptyStateText)+" ")])]):_vm._e(),_vm.showLoading||_vm.$validation.isEmpty(_vm.records)?_vm._e():[_c("div",{staticClass:"cm-list-container"},[_c("div",{staticClass:"column-customization-icon",attrs:{disabled:!_vm.isColumnCustomizable},on:{click:_vm.toShowColumnSettings}},[_c("el-tooltip",{attrs:{disabled:_vm.isColumnCustomizable,placement:"top",content:_vm.$t("common._common.you_dont_have_permission")}},[_c("inline-svg",{staticClass:"text-center position-absolute icon",attrs:{src:"column-setting"}})],1)],1),_vm.$validation.isEmpty(_vm.selectedListItemsIds)?_vm._e():_c("div",{staticClass:"pull-left table-header-actions"},[_c("div",{staticClass:"action-btn-slide btn-block"},[_vm.hasPermission("DELETE")?_c("button",{staticClass:"btn btn--tertiary pointer",on:{click:function($event){return _vm.deleteRecords(_vm.selectedListItemsIds,!0)}}},[_vm._v(" "+_vm._s(_vm.$t("custommodules.list.delete"))+" ")]):_vm._e(),_c("button",{staticClass:"btn btn--tertiary pointer mL10",class:{disabled:_vm.actions.Publish.loading},on:{click:function($event){return _vm.showJPStatusUpdateDialog("Publish")}}},[_vm._v(" "+_vm._s(_vm.$t("maintenance.pm.publish"))+" ")]),_vm._e(),_vm._e()]),_c("CustomButton",{key:_vm.moduleName+"_"+_vm.viewname+"_"+_vm.POSITION.LIST_BAR,staticClass:"custom-button",attrs:{modelDataClass:_vm.modelDataClass,moduleName:_vm.moduleName,position:_vm.POSITION.LIST_BAR,selectedRecords:_vm.selectedListItemsObj},on:{onSuccess:_vm.onCustomButtonSuccess,onError:function(){}}})],1),_c("CommonList",{attrs:{viewDetail:_vm.viewDetail,records:_vm.records,moduleName:_vm.moduleName,redirectToOverview:_vm.redirectToOverview,slotList:_vm.slotList,canShowCustomButton:"true",refreshList:_vm.onCustomButtonSuccess},on:{"selection-change":_vm.selectItems},scopedSlots:_vm._u([{key:_vm.slotList[0].name,fn:function(ref){var record=ref.record;return[_c("div",{staticClass:"d-flex"},[_c("div",{staticClass:"fc-id"},[_vm._v(_vm._s("#"+record[_vm.slotList[0].name]))])])]}},{key:_vm.slotList[1].criteria,fn:function(ref){var record=ref.record;return[_c("router-link",{staticClass:"d-flex fw5 label-txt-black ellipsis main-field-column",attrs:{to:_vm.redirectToOverview(record)}},[_c("el-tooltip",{attrs:{effect:"dark",content:record.name||"---",placement:"top-end","open-delay":600}},[_c("div",{staticClass:"self-center width200px"},[_c("span",{staticClass:"list-main-field"},[_vm._v(_vm._s(record.name||"---"))])])])],1)]}},{key:_vm.slotList[2].name,fn:function(ref){var record=ref.record;return[_vm.canShowActionColumn(record)?_c("div",{staticClass:"d-flex text-center"},[_vm.hasPermission("EDIT,UPDATE")&&record.canEdit()?_c("i",{directives:[{name:"tippy",rawName:"v-tippy"}],staticClass:"el-icon-edit pointer edit-icon-color visibility-hide-actions mL10",attrs:{"data-arrow":"true",title:_vm.$t("common._common.edit")},on:{click:function($event){return _vm.editModule(record)}}}):_vm._e(),_vm.hasPermission("DELETE")?_c("i",{directives:[{name:"tippy",rawName:"v-tippy"}],staticClass:"el-icon-delete pointer edit-icon-color visibility-hide-actions mL10",attrs:{"data-arrow":"true",title:_vm.$t("common._common.delete")},on:{click:function($event){return _vm.deleteRecords([record.id])}}}):_vm._e()]):_vm._e()]}}],null,!0)})],1)],_c("column-customization",{attrs:{visible:_vm.showColumnSettings,moduleName:_vm.moduleName,viewName:_vm.viewname},on:{"update:visible":function($event){_vm.showColumnSettings=$event}}})]},proxy:!0}])},[_c("portal",{attrs:{to:"view-manager-link"}},[_c("router-link",{staticClass:"view-manager-btn",attrs:{tag:"div",to:"/app/wo/"+_vm.moduleName+"/viewmanager"}},[_c("inline-svg",{staticClass:"d-flex",attrs:{src:"svgs/hamburger-menu",iconClass:"icon icon-sm"}}),_c("span",{staticClass:"label mL10 text-uppercase"},[_vm._v(" "+_vm._s(_vm.$t("viewsmanager.list.views_manager"))+" ")])],1)],1),_vm.showDialog?_c("JPWarningDialog",{attrs:{showDialog:_vm.showDialog,dialogType:_vm.dialogType,moduleName:_vm.moduleName,stateUpdating:_vm.stateUpdating},on:{closeDialog:_vm.closeDialog,saveAction:_vm.saveAction}}):_vm._e()],1)},staticRenderFns=[],defineProperty=__webpack_require__(482482),objectSpread2=__webpack_require__(595082),regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),CommonModuleList=(__webpack_require__(976801),__webpack_require__(434284),__webpack_require__(450886),__webpack_require__(670560),__webpack_require__(347446)),api=__webpack_require__(32284),router=__webpack_require__(329435),pm_utils=__webpack_require__(195123),JPWarningDialog=__webpack_require__(835002),JobPlanListvue_type_script_lang_js={extends:CommonModuleList["default"],data:function(){return{actions:{delete:{loading:!1},Publish:{loading:!1},Unpublish:{loading:!1},Disable:{loading:!1}},showDialog:!1,dialogType:null,stateUpdating:!1}},components:{JPWarningDialog:JPWarningDialog/* default */.Z},computed:{moduleDisplayName:function(){return"Job Plan"},parentPath:function(){return"/app/wo/jobplan/"}},methods:{canShowActionColumn:function(record){var _ref=record||{},jpStatus=_ref.jpStatus;return!["Published","Disabled"].includes(pm_utils/* PUBLISHED_STATUS */.Iq[jpStatus])},redirectToOverview:function(record){var moduleName=this.moduleName,viewname=this.viewname,_ref2=record||{},group=_ref2.group,version=_ref2.jobPlanVersion,groupId=this.$getProperty(group,"id",null);if(version="v".concat(version),(0,router/* isWebTabsEnabled */.tj)()){var _ref3=(0,router/* findRouteForModule */.Jp)(moduleName,router/* pageTypes */.As.OVERVIEW)||{},name=_ref3.name,route={name:name,params:{viewname:viewname,id:groupId},query:{version:version}};return name&&route}return{name:"jobPlanSummary",params:{moduleName:moduleName,viewname:viewname,id:groupId},query:{version:version}}},
//To Bulk Publish JobPlan(s)
bulkPublishJP:function(){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var selectedListItemsIds,_yield$API$post,error;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return _this.stateUpdating=!0,selectedListItemsIds=_this.selectedListItemsIds,_context.next=4,api/* API */.bl.post("v3/jobPlan/bulkPublish",{jobPlanIds:selectedListItemsIds});case 4:if(_yield$API$post=_context.sent,error=_yield$API$post.error,!error){_context.next=10;break}_this.$message.error(error.message||"Error Occured"),_context.next=15;break;case 10:return _this.$message.success(_this.$t("jobplan.jp_published")),_context.next=13,_this.loadRecords();case 13:_this.selectedListItemsIds=[],_this.selectedListItemsObj=[];case 15:_this.$set(_this.actions,"Publish",!1),_this.stateUpdating=!1,_this.closeDialog();case 18:case"end":return _context.stop()}}),_callee)})))()},
//To Bulk UnPublish,Disable JobPlan
updateJPStatus:function(status,selectedJP){var _this2=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee2(){var serializedJP;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context2){while(1)switch(_context2.prev=_context2.next){case 0:return _this2.stateUpdating=!0,_this2.$set(_this2.actions,"".concat(status),!0),serializedJP=selectedJP.map((function(jp){return(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},jp),{},{jpStatus:pm_utils/* PUBLISH_STATUS */.C7["".concat(status)]})})),_context2.next=5,_this2.bulkPatchJP(serializedJP,_this2.$t("jobplan.".concat(status,"_success")));case 5:_this2.$set(_this2.actions,"".concat(status),!1);case 6:case"end":return _context2.stop()}}),_callee2)})))()},bulkPatchJP:function(jpList,successMessage){var _this3=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee3(){var moduleName,data,url,params,_yield$API$post2,error;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context3){while(1)switch(_context3.prev=_context3.next){case 0:return moduleName=_this3.moduleName,data=(0,defineProperty/* default */.Z)({},moduleName,jpList),url="v3/modules/bulkPatch/".concat(moduleName),params={data:data,moduleName:moduleName},_context3.next=6,api/* API */.bl.post(url,params);case 6:if(_yield$API$post2=_context3.sent,error=_yield$API$post2.error,!error){_context3.next=12;break}_this3.$message.error(error.message||"Error Occured"),_context3.next=17;break;case 12:return _this3.$message.success(successMessage),_context3.next=15,_this3.loadRecords();case 15:_this3.selectedListItemsIds=[],_this3.selectedListItemsObj=[];case 17:_this3.stateUpdating=!1,_this3.closeDialog();case 19:case"end":return _context3.stop()}}),_callee3)})))()},editModule:function(record){var moduleName=this.moduleName,_ref4=record||{},group=_ref4.group,version=_ref4.jobPlanVersion,groupId=this.$getProperty(group,"id",null);if(version="v".concat(version),(0,router/* isWebTabsEnabled */.tj)()){var _ref5=(0,router/* findRouteForModule */.Jp)(moduleName,router/* pageTypes */.As.EDIT)||{},name=_ref5.name;name&&this.$router.push({name:name,params:{id:groupId},query:{version:version}})}else this.$router.push({name:"edit-jobplan",params:{id:groupId},query:{version:version}})},deleteRecords:function(idList,isBulk){var _this4=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee4(){var value,moduleName,_yield$API$deleteReco,error,message;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context4){while(1)switch(_context4.prev=_context4.next){case 0:return _context4.next=2,_this4.$dialog.confirm({title:_this4.$t("jobplan.delete_job_plan"),message:_this4.$t("jobplan.are_you_sure_delete_job_plan"),rbLabel:_this4.$t("common._common.delete"),rbClass:"jp-delete-dialog-btn",className:"jp-delete-dialog"});case 2:if(value=_context4.sent,!value){_context4.next=22;break}return isBulk&&(_this4.actions.delete=!0),moduleName=_this4.moduleName,_context4.next=8,api/* API */.bl.deleteRecord(moduleName,idList);case 8:if(_yield$API$deleteReco=_context4.sent,error=_yield$API$deleteReco.error,!error){_context4.next=16;break}message=error.message,_this4.$message.error(message||"Error Occured while deleting Job Plan"),_this4.loading=!1,_context4.next=22;break;case 16:return _this4.$message.success(_this4.$t("jobplan.job_plan_deleted_successfully")),isBulk&&(_this4.actions.delete=!1),_context4.next=20,_this4.refreshRecordDetails(!0);case 20:_this4.selectedListItemsIds=[],_this4.selectedListItemsObj=[];case 22:case"end":return _context4.stop()}}),_callee4)})))()},redirectToFormCreation:function(){var moduleName=this.moduleName;if((0,router/* isWebTabsEnabled */.tj)()){var _ref6=(0,router/* findRouteForModule */.Jp)(moduleName,router/* pageTypes */.As.CREATE)||{},name=_ref6.name;name&&this.$router.push({name:name})}else this.$router.push({name:"new-jobplan"})},showJPStatusUpdateDialog:function(status){this.showDialog=!0,this.dialogType=status},closeDialog:function(){this.showDialog=!1,this.dialogType=null},saveAction:function(status){var _this5=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee5(){var selectedListItemsObj;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context5){while(1)switch(_context5.prev=_context5.next){case 0:if(selectedListItemsObj=_this5.selectedListItemsObj,"Publish"!==status){_context5.next=6;break}return _context5.next=4,_this5.bulkPublishJP();case 4:_context5.next=8;break;case 6:return _context5.next=8,_this5.updateJPStatus(status,selectedListItemsObj);case 8:case"end":return _context5.stop()}}),_callee5)})))()}}},jobplan_JobPlanListvue_type_script_lang_js=JobPlanListvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(jobplan_JobPlanListvue_type_script_lang_js,render,staticRenderFns,!1,null,"4b4a9eeb",null)
/* harmony default export */,JobPlanList=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/76534.js.map