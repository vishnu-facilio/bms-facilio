(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[65033,32686,80865,53021],{
/***/349318:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return Comments}});
// EXTERNAL MODULE: ./node_modules/@babel/runtime/helpers/esm/objectSpread2.js
var render,staticRenderFns,objectSpread2=__webpack_require__(595082),esm_typeof=__webpack_require__(103336),regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),vuex_esm=(__webpack_require__(634338),__webpack_require__(670560),__webpack_require__(843097),__webpack_require__(420629)),api=__webpack_require__(32284),Commentsvue_type_script_lang_js={props:["module","record","notify","parentModule","isServicePortal","setNoOfNotesLength"],created:function(){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return _context.next=2,_this.availablePortalApps();case 2:_this.canShowSharing&&(_this.newComment.commentSharing=_this.defaultSharingApps),_this.loadComments();case 4:case"end":return _context.stop()}}),_callee)})))()},data:function(){return{isAddingNotes:!1,loading:!0,selectedPortal:[],portalApps:[],defaultSharingApps:[],comments:[],newComment:{parentModuleLinkName:this.module,parentId:this.record.id,body:null,notifyRequester:!1,commentSharing:[]},commentFocus:!1}},watch:{record:function(recordObj){this.loadComments()},comments:function(newVal){this.record&&"object"===(0,esm_typeof/* default */.Z)(this.record)&&this.setNoOfNotesLength&&
//this.record.noOfNotes = this.comments.length
this.setNoOfNotesLength(this.comments.length)}},computed:(0,objectSpread2/* default */.Z)({showNotifyRequester:function(){return"undefined"===typeof this.notify||this.notify}},(0,vuex_esm/* mapGetters */.Se)(["getCurrentUser"])),methods:{availablePortalApps:function(){var _this2=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee2(){var _yield$API$get,error,data,licensedPortalApps,commentSharingPreferences;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context2){while(1)switch(_context2.prev=_context2.next){case 0:return _context2.next=2,api/* API */.bl.get("/v3/commentPreferences/sharingOptions");case 2:_yield$API$get=_context2.sent,error=_yield$API$get.error,data=_yield$API$get.data,error?_this2.$message.error(error.message||_this2.$t("common._common.error_occured")):(licensedPortalApps=data.licensedPortalApps,commentSharingPreferences=data.commentSharingPreferences,_this2.portalApps=licensedPortalApps,_this2.defaultSharingApps=commentSharingPreferences);case 6:case"end":return _context2.stop()}}),_callee2)})))()},loadComments:function(){var _this3=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee3(){var url;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context3){while(1)switch(_context3.prev=_context3.next){case 0:if(_this3.record&&"object"===(0,esm_typeof/* default */.Z)(_this3.record)){_context3.next=2;break}return _context3.abrupt("return");case 2:return _this3.loading=!0,_this3.newComment.parentId=_this3.record.id,url="/note/get?module=",_this3.isServicePortal&&(url="/notelist?module="),_context3.abrupt("return",_this3.$http.get(url+_this3.module+"&parentId="+_this3.record.id+"&parentModuleName="+_this3.parentModule).then((function(response){_this3.loading=!1,_this3.comments=response.data})).catch((function(error){_this3.loading=!1,_this3.comments=[]})));case 7:case"end":return _context3.stop()}}),_callee3)})))()},reset:function(){this.newComment={parentModuleLinkName:this.module,parentId:this.record.id,body:null,notifyRequester:!1},this.canShowSharing&&(this.newComment.commentSharing=this.defaultSharingApps)},addComment:function(){var _this4=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee4(){var param,url,record,parentModule,module,_yield$API$post,data,error;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context4){while(1)switch(_context4.prev=_context4.next){case 0:if(!_this4.newComment.body){_context4.next=13;break}return _this4.isServicePortal&&(_this4.newComment.notifyRequester=!0),param={note:_this4.newComment,module:_this4.module,parentModuleName:_this4.parentModule},"basealarmnotes"===_this4.module&&(param.alarmOccurrenceId=_this4.record.occurrence?_this4.record.occurrence.id:-1),_this4.isAddingNotes=!0,url="v2/notes/add",_this4.$helpers.isLicenseEnabled("THROW_403_WEBTAB")&&(record=_this4.record,parentModule=_this4.parentModule,module=_this4.module,url="v2/notes/".concat(module,"/").concat(parentModule,"/add/").concat(record.id)),_context4.next=9,api/* API */.bl.post(url,param);case 9:_yield$API$post=_context4.sent,data=_yield$API$post.data,error=_yield$API$post.error,error?_this4.$message.error(error.message||"Error Occured"):(_this4.reset(),_this4.comments.push(data.Notes),_this4.closeComment(),_this4.isAddingNotes=!1);case 13:case"end":return _context4.stop()}}),_callee4)})))()}}},base_Commentsvue_type_script_lang_js=Commentsvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(base_Commentsvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,Comments=component.exports;
// EXTERNAL MODULE: ./node_modules/@babel/runtime/helpers/esm/typeof.js
},
/***/147105:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return relatedlist_Comments}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/relatedlist/Comments.vue?vue&type=template&id=9c5de910
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"fc-comments"},[_vm.loading?_c("div",{staticClass:"text-center"},[_c("spinner",{attrs:{show:_vm.loading}})],1):_c("div",{staticClass:"comment-msg-container"},_vm._l(_vm.comments,(function(comment){return _c("div",{key:comment.id,staticClass:"fc-comment-row"},[_c("div",{staticClass:"flLeft f12 secondary-color comment-by"},[_c("user-avatar",{staticClass:"comments-avatar",attrs:{size:"md",user:comment.createdBy,id:comment.createdBy.id}}),_c("div",{staticClass:"pL45 comment-time"},[_c("i",{staticClass:"fa fa-circle",staticStyle:{opacity:"0.4px",color:"rgba(140,161,173,0.30)","font-size":"8px","padding-right":"5px"},attrs:{"aria-hidden":"true"}}),_vm._v(" "+_vm._s(_vm._f("fromNow")(comment.createdTime))+" ")])],1),null!=comment.bodyHTML?_c("div",{staticClass:"markdown-style",domProps:{innerHTML:_vm._s(comment.bodyHTML)}}):_vm._e(),_c("div",{staticClass:"clearboth"})])})),0),_c("div",{staticClass:"row new-comment-area width100 mT20",attrs:{id:"commentBoxPar"}},[_c("form",{staticClass:"col-12",on:{submit:function($event){return $event.preventDefault(),_vm.addComment.apply(null,arguments)}}},[_c("div",{staticClass:"markdown-inside"},[_c("textarea",{directives:[{name:"model",rawName:"v-model",value:_vm.newComment.body,expression:"newComment.body"}],ref:"commentBoxRef",staticClass:"comment-box pT10",class:{height75:_vm.commentFocus,height35:!_vm.commentFocus},staticStyle:{"font-size":"13px","letter-spacing":"0.3px",color:"#333333","border-color":"#d0d9e2 !important","border-radius":"3px",width:"100%",height:"40px"},attrs:{placeholder:_vm.placeholder?_vm.placeholder:_vm.$t("common._common.write_comment")},domProps:{value:_vm.newComment.body},on:{focus:_vm.focusCommentBox,input:function($event){$event.target.composing||_vm.$set(_vm.newComment,"body",$event.target.value)}}}),_vm._v(" "),_c("button",{class:[_vm.commentFocus?"show":"hide"],on:{click:_vm.markdownhelplist}},[_c("img",{staticClass:"pull-right pointer",attrs:{src:__webpack_require__(773339)}})])]),this.markdownshow?[_c("markdown-help-list",{attrs:{markdownshow:_vm.markdownshow},on:{"update:markdownshow":function($event){_vm.markdownshow=$event}}})]:_vm._e(),_vm.commentFocus?_c("div",[_c("div",{staticClass:"flRight"},[_c("button",{staticClass:"comment-btn"},[_vm._v(" "+_vm._s(_vm.btnLabel?_vm.btnLabel:_vm.$t("common._common.comment"))+" ")])]),!_vm.isServicePortal&&_vm.showNotifyRequester?_c("div",{staticClass:"flLeft"},[_c("input",{directives:[{name:"model",rawName:"v-model",value:_vm.newComment.notifyRequester,expression:"newComment.notifyRequester"}],staticClass:"notify-requester-checkbox",attrs:{type:"checkbox",name:"notifyRequester"},domProps:{checked:Array.isArray(_vm.newComment.notifyRequester)?_vm._i(_vm.newComment.notifyRequester,null)>-1:_vm.newComment.notifyRequester},on:{change:function($event){var $$a=_vm.newComment.notifyRequester,$$el=$event.target,$$c=!!$$el.checked;if(Array.isArray($$a)){var $$v=null,$$i=_vm._i($$a,$$v);$$el.checked?$$i<0&&_vm.$set(_vm.newComment,"notifyRequester",$$a.concat([$$v])):$$i>-1&&_vm.$set(_vm.newComment,"notifyRequester",$$a.slice(0,$$i).concat($$a.slice($$i+1)))}else _vm.$set(_vm.newComment,"notifyRequester",$$c)}}}),_c("label",{staticClass:"notify-req",attrs:{for:"notifyRequester"}},[_vm._v(_vm._s(_vm.$t("common._common.notify")))])]):_vm._e(),_c("div",{staticClass:"clearboth"})]):_vm._e()],2)])])},staticRenderFns=[],User=(__webpack_require__(538077),__webpack_require__(260228),__webpack_require__(228436),__webpack_require__(426803)),Comments=__webpack_require__(349318),MarkdownHelpList=__webpack_require__(566302),Commentsvue_type_script_lang_js={extends:Comments/* default */.Z,props:["placeholder","btnLabel"],components:{UserAvatar:User/* default */.Z,MarkdownHelpList:MarkdownHelpList/* default */.Z},data:function(){return{markdownshow:!1}},created:function(){window.addEventListener("click",this.blurCommentBox)},methods:{markdownhelplist:function(){this.markdownshow=!0},focusCommentBox:function(){this.$refs.commentBoxRef.focus(),this.commentFocus=!0},blurCommentBox:function(e){var itTargetPar=e.path.filter((function(ele){if("commentBoxPar"===ele.id)return!0}));this.newComment.body&&""!==this.newComment.body.trim()||itTargetPar.length||(this.commentFocus=!1)}}},relatedlist_Commentsvue_type_script_lang_js=Commentsvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(relatedlist_Commentsvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,relatedlist_Comments=component.exports},
/***/773339:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"2ec28172b828295676202641adbd2566.svg";
/***/},
/***/862620:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"99feee13c865562d98860641f93e90d9.svg";
/***/}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/65033.js.map