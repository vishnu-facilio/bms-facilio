"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[36291,10321,32686,80865,53021],{
/***/42525:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return ApprovalBar}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/approval/ApprovalBar.vue?vue&type=template&id=4fea8923&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",[_c("div",{class:["sidebar-loader",(_vm.loading||_vm.isSaving)&&"on"]}),_vm.loading?_c("div",{staticClass:"d-flex approval-bar"},[_vm._v(" "+_vm._s(_vm.$t("panel.loading_load"))+" ")]):_c("div",{staticClass:"d-flex approval-bar"},[_c("div",{staticClass:"approval-desc mR30 f14"},[_vm.isRejected?[_c("span",{staticClass:"bold"},[_vm._v(_vm._s(_vm.policyName))]),_vm._v(" "+_vm._s(_vm.$t("panel.was"))+" "),_c("span",{staticClass:"bold",staticStyle:{color:"#dd7171"}},[_vm._v(_vm._s(_vm.$t("panel.rejected")))]),_vm._v(" for this "+_vm._s(_vm.moduleDisplayName||_vm.moduleName)+". "),_vm.resendApproval?[_c("span",[_vm._v("You can")]),_vm.canShowEdit?[_c("span",{staticClass:"mL5"},[_vm._v("either")]),_c("span",{class:[_vm.isSaving&&"cursor-not-allowed"]},[_c("a",{class:["mL5 bold",_vm.isSaving&&"pointer-events-none"],on:{click:function($event){return _vm.$emit("onEdit")}}},[_vm._v(" edit ")])]),_c("span",{staticClass:"mL5"},[_vm._v("or")])]:_vm._e(),_c("span",{class:[_vm.isSaving&&"cursor-not-allowed"]},[_c("a",{class:["mL5 bold",_vm.isSaving&&"pointer-events-none"],on:{click:function($event){return _vm.requestToSave(_vm.resendApproval)}}},[_vm._v(" resend ")])]),_c("span",{staticClass:"mL5"},[_vm._v("for approval.")])]:_vm._e()]:_vm.hasApprovalList&&!_vm.hideApprovers?[_c("span",{staticClass:"bold"},[_vm._v(_vm._s(_vm.policyName))]),_vm._v(" is pending from "),_c("span",{staticClass:"bold"},[_vm._v(_vm._s(_vm.approvers[0].name))]),_vm.approvers.length>1?_c("el-popover",{attrs:{placement:"bottom",width:"200",trigger:"hover"}},[_vm._t("default",(function(){return[_c("div",{staticClass:"mL5 approvers-more-icon"},[_vm._v(" +"+_vm._s(_vm.approvers.length-1)+" ")])]}),{slot:"reference"}),_c("div",_vm._l(_vm.approvers.slice(1),(function(approver){return _c("div",{key:approver.name,staticClass:"mB10"},[_c("ApproverTypeAvatar",{attrs:{size:"md",user:approver,userType:_vm.getApproverType(approver),moduleName:_vm.moduleName,approvalMeta:_vm.approvalMeta}})],1)})),0)],2):_vm._e()]:(_vm.$validation.isEmpty(_vm.transitions),[_c("span",{staticClass:"bold"},[_vm._v(_vm._s(_vm.policyName))]),_vm._v(" is pending ")])],2),_vm.$validation.isEmpty(_vm.transitions)?_vm._e():_c("div",{staticClass:"approval-links text-uppercase f12 bold"},[_c("div",{class:[_vm.isSaving&&"cursor-not-allowed"]},[_vm.approveTransition?_c("a",{class:["link-approve mR15",_vm.isSaving&&"pointer-events-none"],on:{click:function($event){return _vm.requestToSave(_vm.approveTransition)}}},[_c("inline-svg",{attrs:{src:"tick-sign",iconClass:"icon icon-xs"}}),_vm._v(" "+_vm._s(_vm.approveTransition.name)+" ")],1):_vm._e(),_vm.rejectTransition?_c("a",{class:["link-reject",_vm.isSaving&&"pointer-events-none"],on:{click:function($event){return _vm.requestToSave(_vm.rejectTransition)}}},[_c("inline-svg",{attrs:{src:"svgs/close",iconClass:"icon icon-xxs"}}),_vm._v(" "+_vm._s(_vm.rejectTransition.name)+" ")],1):_vm._e()])])]),_vm.canShowForm?[_c("TransitionForm",{attrs:{moduleName:_vm.selectedTransition.moduleName,recordId:_vm.record.recordId,formId:_vm.selectedTransition.formId,record:_vm.record,transition:_vm.selectedTransition,approvalRule:_vm.approvalRule,saveAction:_vm.saveAction,closeAction:_vm.closeStateFlowForm,isV3:_vm.isV3}})]:_vm._e()],2)},staticRenderFns=[],toConsumableArray=__webpack_require__(488478),objectSpread2=__webpack_require__(595082),ApprovalButtons=(__webpack_require__(689730),__webpack_require__(260228),__webpack_require__(947522),__webpack_require__(670560),__webpack_require__(425728),__webpack_require__(434284),__webpack_require__(976801),__webpack_require__(634338),__webpack_require__(840889)),ApprovalForm=__webpack_require__(548127),ApprovalUserAvatar=__webpack_require__(400438),validation=__webpack_require__(990260),vuex_esm=__webpack_require__(420629),ApprovalBarvue_type_script_lang_js={extends:ApprovalButtons/* default */.Z,props:["hideApprovers","canShowEdit","canHideMsg"],components:{TransitionForm:ApprovalForm/* default */.Z,ApproverTypeAvatar:ApprovalUserAvatar/* default */.Z},data:function(){return{isSaving:!1}},computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapState */.rn)({metaInfo:function(state){return state.view.metaInfo}})),{},{transitions:function(){return(0,validation/* isEmpty */.xb)(this.stateTransitions)?[]:(0,toConsumableArray/* default */.Z)(this.stateTransitions).slice(0,2)},policyName:function(){return(0,validation/* isEmpty */.xb)(this.approvalRule)?"Approval":this.$getProperty(this.approvalRule,"name")},shouldCurrentUserApprove:function(){return!(0,validation/* isEmpty */.xb)(this.transitions)},hasApprovalList:function(){return!(0,validation/* isEmpty */.xb)(this.approvers)},approvalMeta:function(){return{approvalList:this.pendingApprovalList}},approvers:function(){var approvers=[];return this.pendingApprovalList.forEach((function(approver){var value=approver.value,type=approver.type;(0,validation/* isEmpty */.xb)(value)?"TENANT"===type?approvers.push({name:"Tenant",type:"TENANT"}):"VENDOR"===type&&approvers.push({name:"Vendor",type:"VENDOR"}):(0,validation/* isArray */.kJ)(value)?approvers.push.apply(approvers,(0,toConsumableArray/* default */.Z)(value)):approvers.push(value)})),approvers},isRejected:function(){return"Rejected"===this.$getProperty(this.currentState,"status")},shouldCreateModuleRecord:function(){return!1},shouldCreateNotes:function(){return!1},approveTransition:function(){return this.transitions.find((function(t){return"Approve"===t.name}))},rejectTransition:function(){return this.transitions.find((function(t){return"Reject"===t.name}))},resendApproval:function(){return this.transitions.find((function(t){return!["Approve","Reject"].includes(t.name)}))},moduleDisplayName:function(){var _this$metaInfo;return null===(_this$metaInfo=this.metaInfo)||void 0===_this$metaInfo?void 0:_this$metaInfo.displayName}}),methods:{showMessage:function(transition){
/* override */
var name=transition.name,action="";["Approve","Reject"].includes(name)?"Approve"===name?action="approved":"Reject"===name&&(action="rejected"):action="resent for approval",this.$message.success("".concat(this.moduleDisplayName||this.moduleName," was ").concat(action))},requestToSave:function(transition){var _this=this;this.isSaving=!0,this.startTransition(transition).finally((function(){return _this.isSaving=!1}))},saveAction:function(formData){return this.transitionToState(formData)},getApproverType:function(approver){return approver.id?{type:"USER",value:approver}:approver},defaultResponseHandler:function(_ref,transition){var error=_ref.error,canShowForm=this.canShowForm;error?(!this.canHideMsg&&this.$message.error(error.message),this.$emit("onFailure",{error:error,transition:transition})):(canShowForm&&this.closeStateFlowForm(),this.fetchAvailableStates(),this.$emit("onSuccess",{error:error,transition:transition}),!this.canHideMsg&&this.showMessage(transition))}}},approval_ApprovalBarvue_type_script_lang_js=ApprovalBarvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(approval_ApprovalBarvue_type_script_lang_js,render,staticRenderFns,!1,null,"4fea8923",null)
/* harmony default export */,ApprovalBar=component.exports},
/***/760218:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return PageBuilder}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/page/PageBuilder.vue?vue&type=template&id=0dd0982f
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _vm.showPage&&_vm.pagebuilderComponent?_c(_vm.pagebuilderComponent,_vm._b({tag:"component",attrs:{module:_vm.module},on:{forceOldPage:function($event){_vm.forceOld=!0}}},"component",_vm.$attrs,!1)):_vm._e()},staticRenderFns=[],objectSpread2=__webpack_require__(595082),regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),vuex_esm=(__webpack_require__(260228),__webpack_require__(821694),__webpack_require__(76265),__webpack_require__(976801),__webpack_require__(843843),__webpack_require__(420629)),PageBuildervue_type_script_lang_js={props:["module"],data:function(){return{forceOld:!1,showPage:!1}},components:{NewPageBuilder:function(){return Promise.all(/* import() */[__webpack_require__.e(93208),__webpack_require__.e(50232),__webpack_require__.e(93765),__webpack_require__.e(95953),__webpack_require__.e(35309),__webpack_require__.e(18778),__webpack_require__.e(12303),__webpack_require__.e(84290),__webpack_require__.e(31721),__webpack_require__.e(64418),__webpack_require__.e(5603),__webpack_require__.e(57432),__webpack_require__.e(435),__webpack_require__.e(72230),__webpack_require__.e(94255),__webpack_require__.e(67683),__webpack_require__.e(44178),__webpack_require__.e(91132),__webpack_require__.e(20280),__webpack_require__.e(78256),__webpack_require__.e(22200),__webpack_require__.e(49081),__webpack_require__.e(89472),__webpack_require__.e(12085),__webpack_require__.e(55754),__webpack_require__.e(86494),__webpack_require__.e(3037),__webpack_require__.e(49908),__webpack_require__.e(22061),__webpack_require__.e(31602),__webpack_require__.e(73609),__webpack_require__.e(44119),__webpack_require__.e(88262),__webpack_require__.e(82080),__webpack_require__.e(88506)]).then(__webpack_require__.bind(__webpack_require__,110570))},OldPageBuilder:function(){return Promise.all(/* import() */[__webpack_require__.e(93208),__webpack_require__.e(50232),__webpack_require__.e(93765),__webpack_require__.e(95953),__webpack_require__.e(35309),__webpack_require__.e(18778),__webpack_require__.e(12303),__webpack_require__.e(84290),__webpack_require__.e(31721),__webpack_require__.e(64418),__webpack_require__.e(5603),__webpack_require__.e(57432),__webpack_require__.e(435),__webpack_require__.e(72230),__webpack_require__.e(94255),__webpack_require__.e(67683),__webpack_require__.e(44178),__webpack_require__.e(91132),__webpack_require__.e(20280),__webpack_require__.e(78256),__webpack_require__.e(22200),__webpack_require__.e(49081),__webpack_require__.e(89472),__webpack_require__.e(12085),__webpack_require__.e(55754),__webpack_require__.e(86494),__webpack_require__.e(3037),__webpack_require__.e(49908),__webpack_require__.e(22061),__webpack_require__.e(31602),__webpack_require__.e(73609),__webpack_require__.e(23803),__webpack_require__.e(55246)]).then(__webpack_require__.bind(__webpack_require__,223803))}},created:function(){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:if(_context.prev=0,!_this.$helpers.isLicenseEnabled("PAGE_BUILDER")){_context.next=4;break}return _context.next=4,_this.$store.dispatch("pagebuilder/loadNewPageEnabledModules");case 4:_context.next=8;break;case 6:_context.prev=6,_context.t0=_context["catch"](0);case 8:_this.$nextTick((function(){_this.showPage=!0}));case 9:case"end":return _context.stop()}}),_callee,null,[[0,6]])})))()},computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapState */.rn)({newPageEnabledModules:function(state){return state.pagebuilder.newPageEnabledModules}})),{},{pagebuilderComponent:function(){var newPageEnabledModules=this.newPageEnabledModules,module=this.module,forceOld=this.forceOld,isNew=(newPageEnabledModules||[]).includes(module),showNew=!forceOld&&(this.showNewPageBuilder||isNew);return showNew?"NewPageBuilder":"OldPageBuilder"},showNewPageBuilder:function(){var query=this.$route.query,_query$showNewPageBui=query.showNewPageBuilder,showNewPageBuilder=void 0!==_query$showNewPageBui&&_query$showNewPageBui;return!0===showNewPageBuilder||"true"===showNewPageBuilder}})},page_PageBuildervue_type_script_lang_js=PageBuildervue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(page_PageBuildervue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,PageBuilder=component.exports},
/***/822642:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return TransferRequestSummary}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/Inventory/TransferRequest/TransferRequestSummary.vue?vue&type=template&id=255c71e6
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"custom-module-overview",class:_vm.$validation.isEmpty(_vm.customClass)?"":_vm.customClass},[_vm.isLoading?_c("div",{staticClass:"flex-middle fc-empty-white"},[_c("spinner",{attrs:{show:_vm.isLoading,size:"80"}})],1):_vm.$validation.isEmpty(_vm.record)?_vm._e():[_c("div",{staticClass:"header p20"},[_c("div",{staticClass:"custom-module-details"},[_c("div",{staticClass:"d-flex flex-middle align-center"},[_c("div",{staticClass:"mL5"},[_c("div",{staticClass:"custom-module-id"},[_vm.$account.portalInfo?_c("i",{directives:[{name:"tippy",rawName:"v-tippy",value:{animateFill:!1,animation:"shift-toward"},expression:"{ animateFill: false, animation: 'shift-toward' }"}],staticClass:"el-icon-back fc-grey3-text14 fw6 pR10 pointer",attrs:{content:"back",arrow:""},on:{click:_vm.back}}):_vm._e(),_vm._v(" #"+_vm._s(_vm.record&&_vm.record.id)+" ")]),_c("div",{staticClass:"custom-module-name d-flex"},[_vm.record.isRecordLocked()?_c("i",{directives:[{name:"tippy",rawName:"v-tippy"}],staticClass:"fa fa-lock locked-wo",attrs:{"data-arrow":"true",title:_vm.$t("common._common.locked_state")}}):_vm._e(),_c("div",{staticClass:"d-flex max-width300px"},[_c("el-tooltip",{attrs:{placement:"bottom",effect:"dark",content:_vm.record[_vm.mainFieldKey]}},[_c("span",{staticClass:"whitespace-pre-wrap custom-header"},[_vm._v(_vm._s(_vm.record[_vm.mainFieldKey]))])])],1),_vm.record.isStateFlowEnabled()&&_vm.record.currentModuleState()?_c("div",{staticClass:"fc-badge text-uppercase inline vertical-middle mL15"},[_vm._v(" "+_vm._s(_vm.record.currentModuleState())+" ")]):_vm._e()])])])]),_c("div",{staticClass:"marginL-auto flex-middle"},[_c("CustomButton",{staticClass:"pR10",attrs:{record:_vm.record,moduleName:_vm.moduleName,position:_vm.POSITION.SUMMARY},on:{refresh:function($event){return _vm.refreshData()},onError:function(){}}}),_vm.record.isStateFlowEnabled()?[_c("TransitionButtons",{key:_vm.record.id+"transitions",staticClass:"mR10",attrs:{moduleName:_vm.moduleName,record:_vm.record,disabled:_vm.record.isApprovalEnabled(),buttonClass:"asset-el-btn"},on:{currentState:function(){},transitionSuccess:function($event){return _vm.refreshData()},transitionFailure:function(){}}})]:_vm._e(),_vm.record.isApprovalEnabled()?_c("portal",{attrs:{to:"pagebuilder-sticky-top"}},[_c("ApprovalBar",{key:_vm.record.id+"approval-bar",staticClass:"approval-bar-shadow",attrs:{moduleName:_vm.moduleName,record:_vm.record,hideApprovers:_vm.shouldHideApprovers},on:{onSuccess:function($event){return _vm.refreshData()},onFailure:function(){}}})],1):_vm._e(),_vm.showEdit?_c("el-button",{staticClass:"fc-wo-border-btn pL15 pR15 self-center",attrs:{type:"button"},on:{click:_vm.editRecord}},[_c("i",{staticClass:"el-icon-edit"})]):_vm._e(),_vm.record.isApprovalEnabled()?_vm._e():_c("el-dropdown",{staticClass:"mL10 self-center fc-btn-ico-lg pT3 pB3 pL8 pR8 pointer",attrs:{trigger:"click"},on:{command:function(action){return _vm.summaryDropDownAction(action,_vm.record)}}},[_c("span",{staticClass:"el-dropdown-link"},[_c("inline-svg",{staticClass:"vertical-middle",attrs:{src:"svgs/menu",iconClass:"icon icon-md"}})],1),_c("el-dropdown-menu",{attrs:{slot:"dropdown"},slot:"dropdown"},[1===_vm.checkState(_vm.record)?_c("el-dropdown-item",{key:1,attrs:{command:"stage_request"}},[_vm._v(_vm._s(_vm.$t("common.header.stage_request")))]):_vm._e(),2===_vm.checkState(_vm.record)?_c("el-dropdown-item",{key:2,attrs:{command:"ship_request"}},[_vm._v(_vm._s(_vm.$t("common.header.ship_request")))]):_vm._e(),3===_vm.checkState(_vm.record)?_c("el-dropdown-item",{key:3,attrs:{command:"complete_request"}},[_vm._v(_vm._s(_vm.$t("common.header.complete_request")))]):_vm._e(),4===_vm.checkShipmentState(_vm.record)?_c("el-dropdown-item",{key:4,attrs:{command:"go_to_shipment"}},[_vm._v(_vm._s(_vm.$t("common.products.go_to_shipment")))]):_vm._e()],1)],1)],2)]),_c("Page",{key:_vm.record.id,attrs:{module:_vm.moduleName,id:_vm.record.id,details:_vm.record,primaryFields:_vm.primaryFields,notesModuleName:_vm.notesModuleName,isV3Api:!0,attachmentsModuleName:_vm.attachmentsModuleName}})]],2)},staticRenderFns=[],regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),CustomModuleSummary=(__webpack_require__(434284),__webpack_require__(670560),__webpack_require__(976801),__webpack_require__(848277)),validation=__webpack_require__(990260),api=__webpack_require__(32284),router=__webpack_require__(329435),TransferRequestSummaryvue_type_script_lang_js={extends:CustomModuleSummary["default"],props:["viewname"],data:function(){return{notesModuleName:"transferrequestnotes",attachmentsModuleName:"transferrequestattachments",primaryFields:["localId","approvalFlowId","approvalStatus","moduleState","stateFlowId","transferFromStore","transferToStore","transferInitiatedOn","expectedCompletionDate","sysCreatedBy","transferredBy","description"]}},computed:{moduleName:function(){return"transferrequest"},mainFieldKey:function(){return"requestSubject"},showEdit:function(){var canShowEdit=this.$hasPermission("transferrequest:UPDATE"),isNotLocked=!this.isStateFlowEnabled||!this.isRecordLocked&&!this.isRequestedState;return canShowEdit&&isNotLocked&&!this.record.isStaged}},title:function(){},methods:{editRecord:function(){var id=this.id;if((0,router/* isWebTabsEnabled */.tj)()){var _ref=(0,router/* findRouteForModule */.Jp)(this.moduleName,router/* pageTypes */.As.EDIT)||{},name=_ref.name;name&&this.$router.push({name:name,params:{id:id}})}else id&&this.$router.push({name:"edit-transferrequest",params:{id:id}})},checkState:function(record){return!1===record.isStaged?1:!1===record.isShipped&&!0===record.isShipmentTrackingNeeded?2:!1===record.isCompleted?3:void 0},checkShipmentState:function(record){if(!0===record.isShipped)return 4},summaryDropDownAction:function(action,record){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var _ref2,moduleName,id,successMsg,_yield$API$updateReco,error,shipmentRecord,_yield$API$createReco,_error,_successMsg,_id,_ref3,name;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:if(_ref2=_this||{},moduleName=_ref2.moduleName,id=_ref2.id,!["stage_request","ship_request","complete_request"].includes(action)){_context.next=18;break}return"stage_request"===action?(record.isStaged=!0,successMsg="Staged Successfully"):"ship_request"===action?record.isShipped=!0:"complete_request"===action&&(record.isCompleted=!0,successMsg="Transfer Request Completed"),_context.next=5,api/* API */.bl.updateRecord(moduleName,{id:id,data:record});case 5:if(_yield$API$updateReco=_context.sent,error=_yield$API$updateReco.error,error?_this.$message.error(error.message||"Error Occured"):(0,validation/* isEmpty */.xb)(successMsg)||(_this.$message.success(successMsg),_this.loadRecord(!0)),"ship_request"!==action){_context.next=18;break}return shipmentRecord={},_this.$set(shipmentRecord,"transferRequest",{id:id}),_this.$set(shipmentRecord,"expectedCompletionDate",record.expectedCompletionDate),_this.$set(shipmentRecord,"isCompleted",record.isCompleted),_context.next=15,api/* API */.bl.createRecord("transferrequestshipment",{data:shipmentRecord});case 15:_yield$API$createReco=_context.sent,_error=_yield$API$createReco.error,_error?_this.$message.error(_error.message||"Error Occured"):(_successMsg="Shipment Record Created",_this.$message.success(_successMsg),_this.loadRecord(!0));case 18:"go_to_shipment"===action&&(_id=_this.$getProperty(_this,"record.shipmentId"),(0,router/* isWebTabsEnabled */.tj)()?(_ref3=(0,router/* findRouteForModule */.Jp)("transferrequestshipment",router/* pageTypes */.As.OVERVIEW)||{},name=_ref3.name,name&&_this.$router.push({name:name,params:{id:_id,viewname:_this.viewname,query:_this.$route.query}})):_this.$router.push({path:"/app/inventory/trShipment/all/".concat(_id,"/overview")}));case 19:case"end":return _context.stop()}}),_callee)})))()}}},TransferRequest_TransferRequestSummaryvue_type_script_lang_js=TransferRequestSummaryvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(TransferRequest_TransferRequestSummaryvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,TransferRequestSummary=component.exports},
/***/400438:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return ApprovalUserAvatar}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/approvals/components/ApprovalUserAvatar.vue?vue&type=template&id=443099a5&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"inline"},[_c("div",{staticClass:"fc-avatar-element q-item-division relative-position"},[_c("div",{staticClass:"q-item-side-left q-item-section"},[_vm.hasUser?_c("avatarPopover",{attrs:{user:_vm.userObj,moduleName:_vm.moduleName}},[_c("span",{attrs:{slot:"reference"},slot:"reference"},[_c("avatar",{attrs:{size:_vm.size,user:_vm.userObj,color:_vm.color}})],1)]):_vm.fieldType?_c("avatarPopover",{attrs:{user:_vm.fieldTypeUserValue,moduleName:_vm.moduleName}},[_c("span",{attrs:{slot:"reference"},slot:"reference"},[_c("avatar",{attrs:{size:_vm.size,user:_vm.fieldTypeUserValue,color:_vm.color}})],1)]):_c("div",{class:["icon-border",_vm.userTypeIcon[_vm.userType.type].background]},[_c("inline-svg",{class:["mT2",_vm.userTypeIcon[_vm.userType.type].marginLeft],attrs:{src:_vm.userTypeIcon[_vm.userType.type].icon,iconClass:"icon text-center fc-fill-path fill-path-white "+_vm.userTypeIcon[_vm.userType.type].iconSize}})],1)],1),_c("div",{staticClass:"flex-middle"},[_vm.$validation.isObject(_vm.typeLabel)?_c("span",{staticClass:"q-item-label d-flex flex-wrap"},[_vm._v(" "+_vm._s(_vm.typeLabel.value)+"  "),_vm.typeLabel.type?_c("span",{staticClass:"text-fc-grey mL5"},[_vm._v(" ("+_vm._s(_vm.typeLabel.type)+") ")]):_vm._e()]):_c("span",{staticClass:"q-item-label d-flex flex-wrap user-type"},[_vm._v(" "+_vm._s(_vm.typeLabel)+" ")]),_vm.approvers.length>1?_c("el-popover",{attrs:{placement:"bottom",width:"200",trigger:"hover"}},[_vm._t("default",(function(){return[_c("div",{staticClass:"approvers-more-icon"},[_vm._v("+"+_vm._s(_vm.approvers.length-1))])]}),{slot:"reference"}),_c("div",_vm._l(_vm.approvers.slice(1),(function(approver){return _c("div",{key:approver.name,staticClass:"mB10"},[_c("ApprovalUserAvatar",{attrs:{size:"md",user:approver,userType:_vm.getApproverType(approver),moduleName:_vm.moduleName,approvalMeta:_vm.approvalMeta}})],1)})),0)],2):_vm._e()],1)])])},staticRenderFns=[],validation=(__webpack_require__(425728),__webpack_require__(260228),__webpack_require__(434284),__webpack_require__(990260)),User=__webpack_require__(426803),ApprovalUserAvatarvue_type_script_lang_js={name:"ApprovalUserAvatar",extends:User/* default */.Z,props:["userType","approvalMeta","approverObj"],computed:{hasUser:function(){var id=this.$getProperty(this.userObj,"id");return!(0,validation/* isEmpty */.xb)(id)},fieldType:function(){var _this$userType=this.userType,type=_this$userType.type,value=_this$userType.value;return"FIELD"===type&&!(0,validation/* isEmpty */.xb)(value)},fieldTypeUserValue:function(){var fieldType=this.fieldType,userType=this.userType,value=userType.value;return fieldType?(0,validation/* isArray */.kJ)(value)&&!(0,validation/* isEmpty */.xb)(value)?value[0]:value:null},fieldName:function(){var approverObj=this.approverObj,approvalMeta=this.approvalMeta,_ref=approvalMeta||{},_ref$approvalList=_ref.approvalList,approvalList=void 0===_ref$approvalList?[]:_ref$approvalList;if(!(0,validation/* isEmpty */.xb)(approvalList)){var displayName=approvalList.find((function(list){return list.approverGroup===approverObj.approverGroup})).field.displayName;return displayName}return null},color:function(){return-1!==this.userObj.id||this.fieldType?"":this.group?"#c3c3c3":"#e3e3e3"},userTypeIcon:function(){var userIcon={TENANT:{icon:"svgs/tenant",background:"green",iconSize:"icon-lg",marginLeft:"mL4"},VENDOR:{icon:"svgs/apps/vendor",background:"orange",iconSize:"icon-lg",marginLeft:"mL4"},ROLE:{icon:"svgs/role",background:"yellow",iconSize:"icon-xl",marginLeft:"mL3"},GROUP:{icon:"svgs/team",background:"blue",iconSize:"icon-xl",marginLeft:"mL3"},FIELD:{icon:"user",background:"brown",iconSize:"icon-lg",marginLeft:"mL4"}};return userIcon},typeLabel:function(){var _this$userType2=this.userType,type=_this$userType2.type,value=_this$userType2.value;if("USER"===type)return{value:this.userObj.name,type:null};if("TENANT"===type)return value?{value:value.name,type:"Tenant"}:"Tenant";if("VENDOR"===type)return value?{value:value.name,type:"Vendor"}:"Vendor";if("FIELD"===type){var _ref2=this.fieldTypeUserValue||{},name=_ref2.name;return{value:name||this.fieldName,type:"Field"}}return value.name},approvers:function(){var _this$userType3=this.userType,type=_this$userType3.type,value=_this$userType3.value;return"USER"===type?[this.userObj]:(0,validation/* isArray */.kJ)(value)?value:[value]}},methods:{getApproverType:function(approver){return approver.id?{type:"USER",value:approver}:approver}}},components_ApprovalUserAvatarvue_type_script_lang_js=ApprovalUserAvatarvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_ApprovalUserAvatarvue_type_script_lang_js,render,staticRenderFns,!1,null,"443099a5",null)
/* harmony default export */,ApprovalUserAvatar=component.exports},
/***/848277:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return CustomModuleSummary}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/custom-module/CustomModuleSummary.vue?vue&type=template&id=2c44e6b2&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"custom-module-overview",class:_vm.customClass},[_vm.isLoading?_c("div",{staticClass:"flex-middle fc-empty-white"},[_c("spinner",{attrs:{show:_vm.isLoading,size:"80"}})],1):_vm.$validation.isEmpty(_vm.record)?_vm._e():[_c("div",{staticClass:"header p20"},[_c("div",{staticClass:"custom-module-details"},[_c("div",{staticClass:"d-flex flex-middle align-center"},[_vm.showPhotoField?_c("div",{staticClass:"mR5"},[_vm.record[_vm.photoFieldName]?_c("div",[_vm.$helpers.isLicenseEnabled("THROW_403_WEBTAB")?_c("img",{staticClass:"img-container",attrs:{src:_vm.record.getImageUrl("photoUrl")}}):_c("img",{staticClass:"img-container",attrs:{src:_vm.record.getImage(_vm.photoFieldName)}})]):_vm.showAvatar?_c("div",[_c("avatar",{attrs:{size:"lg",user:{name:_vm.record.name}}})],1):_vm._e()]):_vm._e(),_c("div",{staticClass:"mL5"},[_c("div",{staticClass:"custom-module-id"},[_vm.$account.portalInfo?_c("i",{directives:[{name:"tippy",rawName:"v-tippy",value:{animateFill:!1,animation:"shift-toward"},expression:"{ animateFill: false, animation: 'shift-toward' }"}],staticClass:"el-icon-back fc-grey3-text14 fw6 pR10 pointer",attrs:{content:"back",arrow:""},on:{click:_vm.back}}):_vm._e(),_vm._v(" #"+_vm._s(_vm.record&&_vm.record.id)+" ")]),_c("div",{staticClass:"custom-module-name "},[_c("div",{staticClass:"d-flex max-width300px"},[_c("el-tooltip",{attrs:{placement:"bottom",effect:"dark",content:_vm.record[_vm.mainFieldKey]}},[_c("span",{staticClass:"whitespace-pre-wrap custom-header"},[_vm._v(_vm._s(_vm.record[_vm.mainFieldKey]))])])],1),_vm.record.isStateFlowEnabled()&&_vm.record.currentModuleState()?_c("div",{staticClass:"fc-badge text-uppercase inline vertical-middle mL15"},[_vm._v(" "+_vm._s(_vm.record.currentModuleState())+" ")]):_vm._e()])])])]),_c("div",{staticClass:"marginL-auto flex-middle"},[_c("CustomButton",{staticClass:"pR10",attrs:{record:_vm.record,moduleName:_vm.moduleName,position:_vm.POSITION.SUMMARY},on:{refresh:function($event){return _vm.refreshData()},onError:function(){}}}),_vm.record.isStateFlowEnabled()?[_c("TransitionButtons",{key:_vm.record.id+"transitions",staticClass:"mR10",attrs:{moduleName:_vm.moduleName,record:_vm.record,disabled:_vm.record.isApprovalEnabled(),buttonClass:"asset-el-btn"},on:{currentState:function(){},transitionSuccess:function($event){return _vm.refreshData()},transitionFailure:function(){}}})]:_vm._e(),_vm.record.isApprovalEnabled()?_c("portal",{attrs:{to:"pagebuilder-sticky-top"}},[_c("ApprovalBar",{key:_vm.record.id+"approval-bar",staticClass:"approval-bar-shadow",attrs:{moduleName:_vm.moduleName,record:_vm.record,hideApprovers:_vm.shouldHideApprovers},on:{onSuccess:function($event){return _vm.refreshData()},onFailure:function(){}}})],1):_vm._e(),_vm.showEdit?_c("el-button",{staticClass:"fc-wo-border-btn pL15 pR15 self-center",attrs:{type:"button"},on:{click:_vm.editRecord}},[_c("i",{staticClass:"el-icon-edit"})]):_vm._e()],2)]),_c("Page",{key:_vm.record.id,attrs:{module:_vm.moduleName,id:_vm.record.id,details:_vm.record,primaryFields:_vm.primaryFields,notesModuleName:_vm.notesModuleName,isV3Api:!0,attachmentsModuleName:_vm.attachmentsModuleName}})]],2)},staticRenderFns=[],regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),PageBuilder=(__webpack_require__(425728),__webpack_require__(260228),__webpack_require__(434284),__webpack_require__(670560),__webpack_require__(760218)),api=__webpack_require__(32284),router=__webpack_require__(329435),CustomButton=__webpack_require__(623857),CustomButtonUtil=__webpack_require__(746095),TransitionButtons=__webpack_require__(987612),ApprovalBar=__webpack_require__(42525),constant=__webpack_require__(354754),CustomModuleData=__webpack_require__(689565),eventBus=__webpack_require__(398011),CustomModuleSummaryvue_type_script_lang_js={name:"CustomModuleSummary",components:{Page:PageBuilder/* default */.Z,CustomButton:CustomButton/* default */.Z,TransitionButtons:TransitionButtons/* default */.Z,ApprovalBar:ApprovalBar/* default */.Z},data:function(){return{isLoading:!1,record:null,primaryFields:[],notesModuleName:"cmdnotes",attachmentsModuleName:"cmdattachments",formFields:null,POSITION:CustomButtonUtil/* POSITION_TYPE */.Rz,customClass:""}},created:function(){this.$store.dispatch("loadApprovalStatus")},mounted:function(){eventBus/* eventBus */.Y.$on("refresh-overview",this.refreshData)},beforeDestroy:function(){eventBus/* eventBus */.Y.$off("refresh-overview",this.refreshData)},computed:{moduleName:function(){return this.$route.params.moduleName?this.$route.params.moduleName:this.$attrs.moduleName||""},id:function(){var paramId=this.$attrs.id||this.$route.params.id;return paramId&&"null"!==paramId?parseInt(this.$route.params.id):""},showEdit:function(){var $org=this.$org,$hasPermission=this.$hasPermission,record=this.record,orgIdsToCheck=constant/* default */.Z.isCustomModulePermissionsEnabled,canShowEdit=!orgIdsToCheck($org.id)&&!(0,router/* isWebTabsEnabled */.tj)()||$hasPermission("".concat(this.moduleName,":UPDATE"));return canShowEdit&&record.canEdit()},mainFieldKey:function(){return"name"},photoFieldName:function(){return"photoId"},showPhotoField:function(){var formFields=this.formFields;if(formFields){var photoField=formFields.find((function(field){return"photo"===field.name}));return!!photoField&&!photoField.hideField}return!1},showAvatar:function(){return!1},shouldHideApprovers:function(){return!1},modelDataClass:function(){return CustomModuleData/* CustomModuleData */.u}},watch:{id:{handler:function(){this.loadRecord(!0)},immediate:!0},moduleName:function(){this.loadRecord(!0)}},methods:{refreshData:function(){this.loadRecord(!0)},loadRecord:function(){var _arguments=arguments,_this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var force,moduleName,id;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return force=_arguments.length>0&&void 0!==_arguments[0]&&_arguments[0],_context.prev=1,moduleName=_this.moduleName,id=_this.id,_this.isLoading=!0,_context.next=6,_this.modelDataClass.fetch({moduleName:moduleName,id:id,force:force});case 6:return _this.record=_context.sent,_context.next=9,_this.getFormMeta();case 9:_context.next=14;break;case 11:_context.prev=11,_context.t0=_context["catch"](1),_this.$message.error((null===_context.t0||void 0===_context.t0?void 0:_context.t0.message)||_this.$t("custommodules.summary.record_summary_error"));case 14:_this.isLoading=!1;case 15:case"end":return _context.stop()}}),_callee,null,[[1,11]])})))()},editRecord:function(){var moduleName=this.moduleName,id=this.id;if((0,router/* isWebTabsEnabled */.tj)()){var _ref=(0,router/* findRouteForModule */.Jp)(moduleName,router/* pageTypes */.As.EDIT)||{},name=_ref.name;name&&this.$router.push({name:name,params:{id:id}})}else{var creationName="custommodules-edit";this.$router.push({name:creationName,params:{moduleName:moduleName,id:id}})}},getFormMeta:function(){var _this2=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee2(){var _this2$record,_this2$record2,formId,moduleName,url,_yield$API$get,data,error,formFields;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context2){while(1)switch(_context2.prev=_context2.next){case 0:return _this2$record=_this2.record,_this2$record2=void 0===_this2$record?{}:_this2$record,formId=_this2$record2.formId,moduleName=_this2.moduleName,url="/v2/forms/".concat(moduleName),_context2.next=4,api/* API */.bl.get(url,{formId:formId||-1});case 4:_yield$API$get=_context2.sent,data=_yield$API$get.data,error=_yield$API$get.error,error||(formFields=(data.form||{}).fields,_this2.formFields=formFields);case 8:case"end":return _context2.stop()}}),_callee2)})))()},back:function(){this.$router.go(-1)}}},custom_module_CustomModuleSummaryvue_type_script_lang_js=CustomModuleSummaryvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(custom_module_CustomModuleSummaryvue_type_script_lang_js,render,staticRenderFns,!1,null,"2c44e6b2",null)
/* harmony default export */,CustomModuleSummary=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/36291.js.map