(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[305],{
/***/574752:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return Avatar}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/Avatar.vue?vue&type=template&id=7f0d5689
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _vm.user||_vm.name?_c("div",{staticClass:"fc-avatar-inline"},[_vm.avatarUrl?_c("img",{staticClass:"fc-avatar",class:_vm.sizeClass,attrs:{src:_vm.avatarUrl,alt:"Profile Avatar"}}):_c("div",{staticClass:"fc-avatar",class:_vm.sizeClass,style:_vm.bgColor},[_vm._v(" "+_vm._s(_vm.trimmedName)+" ")])]):_vm._e()},staticRenderFns=[],objectSpread2=__webpack_require__(595082),vuex_esm=(__webpack_require__(425728),__webpack_require__(260228),__webpack_require__(434284),__webpack_require__(564043),__webpack_require__(709873),__webpack_require__(228436),__webpack_require__(450886),__webpack_require__(677049),__webpack_require__(821694),__webpack_require__(420629)),Avatarvue_type_script_lang_js={props:{size:{type:String},user:{type:Object},avatarStyle:{type:Object},color:{type:String},name:{type:Boolean}},data:function(){return{sizeClass:"fc-avatar-"+this.size,fullName:"",trimmedName:null,avatarUrl:null,bgColor:"background-color: "+(this.color?this.color:this.getRandomBgColor())}},computed:(0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapState */.rn)({users:function(state){return state.users}})),mounted:function(){var _this=this;if(this.user){var userName="";if(this.user.avatarUrl)this.avatarUrl=this.user.avatarUrl;else if(this.user.id){var userObj=this.users.find((function(user){return user.id===_this.user.id}));userObj&&userObj.avatarUrl&&(this.avatarUrl=userObj.avatarUrl),userObj&&userObj.name&&(userName=userObj.name)}userName=userName||this.user.name,userName&&(this.fullName=userName,this.trimmedName=this.getAvatarName(userName),this.bgColor="background-color: "+(this.color?this.color:this.getRandomBgColor()))}else this.name&&(this.trimmedName=this.getAvatarName(this.name),this.bgColor="background-color: "+(this.color?this.color:this.getRandomBgColor()))},watch:{user:function(){var _this2=this;if(this.user.avatarUrl)this.avatarUrl=this.user.avatarUrl;else if(this.user.id){var userObj=this.users.find((function(user){return user.id===_this2.user.id}));userObj&&userObj.avatarUrl?this.avatarUrl=userObj.avatarUrl:this.avatarUrl=null}else this.avatarUrl=null;this.user.name&&(this.fullName=this.user.name,this.trimmedName=this.getAvatarName(this.user.name),this.bgColor="background-color: "+(this.color?this.color:this.getRandomBgColor()))},"user.name":function(){this.user.name&&(this.fullName=this.user.name,this.trimmedName=this.getAvatarName(this.user.name),this.bgColor="background-color: "+(this.color?this.color:this.getRandomBgColor()))},"user.avatarUrl":function(){this.user.avatarUrl?this.avatarUrl=this.user.avatarUrl:this.avatarUrl=null},"user.id":function(){var _this3=this;if(this.user.id){var userObj=this.users.find((function(user){return user.id===_this3.user.id}));userObj&&userObj.avatarUrl?this.avatarUrl=userObj.avatarUrl:this.avatarUrl=null}else this.avatarUrl=null}},methods:{getAvatarName:function(name){for(var parts=name.split(/[ -]/),initials="",initialLen=2,count=0,i=0;i<parts.length;i++)if(""!==parts[i].trim()&&(initials+=parts[i].charAt(0),count++,count>=initialLen))break;initials.length<initialLen&&name.length>=initialLen&&(initials=name.trim().substring(0,initialLen));var avatarName=initials.toUpperCase();return avatarName},getRandomBgColor:function(){var colors=["#FFBA51","#34BFA3","#FF2F82","#29D9A7","#ECDC74","#927FED","#FF61A8","#fbf383","#ac4352","#6db1f4"],userKey=this.user.email?this.user.email:this.user.name,userUniqueNum=Array.from(userKey).map((function(letter){return letter.charCodeAt(0)})).reduce((function(current,previous){return previous+current})),colorIndex=userUniqueNum%colors.length,color=colors[colorIndex];return color}}},components_Avatarvue_type_script_lang_js=Avatarvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_Avatarvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,Avatar=component.exports},
/***/426803:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return User}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/avatar/User.vue?vue&type=template&id=c662fbbc
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _vm.userObj?_c("div",{staticClass:"inline"},[_c("div",{staticClass:"fc-avatar-element q-item-division relative-position"},[_c("div",{staticClass:"q-item-side-left q-item-section"},[_vm.showPopover&&-1!==_vm.userObj.id?_c("avatarPopover",{attrs:{user:_vm.userObj,moduleName:_vm.moduleName}},[_c("span",{attrs:{slot:"reference"},slot:"reference"},[_c("avatar",{attrs:{size:_vm.size,user:_vm.userObj,color:_vm.color}})],1)]):_c("span",[_c("avatar",{attrs:{size:_vm.size,user:_vm.userObj,color:_vm.color}})],1)],1),_vm.showLabel?[_vm.user&&_vm.user.id>0||_vm.group&&_vm.group.id>0?_c("div",{staticClass:"wo-team-txt"},[_c("div",{staticClass:"assignment-avatar-name",class:{"color-d":!_vm.user}},[_vm._v(" "+_vm._s(_vm.user&&_vm.user.id>0?_vm.user.name||(_vm.users.find((function(usr){return usr.id===_vm.user.id}))||{}).name:"---")+" ")]),_c("div",{staticClass:"assignment-group-name",class:{"color-d":!_vm.group}},[_vm._v(" "+_vm._s(_vm.group?_vm.group.name||(_vm.$store.getters.getGroup(_vm.group.id)||{}).name:"---")+" ")])]):_c("div",{staticClass:"wo-team-txt",staticStyle:{"padding-top":"7px","font-size":"14px",color:"#aaa","letter-spacing":"0.2px"}},[_vm._v(" ---/--- ")])]:0!=_vm.name?_c("div",[_c("div",[_c("span",{staticClass:"q-item-label d-flex flex-wrap"},[_vm._v(_vm._s(_vm.userObj.name))])])]):_vm._e()],2)]):_vm._e()},staticRenderFns=[],objectSpread2=__webpack_require__(595082),Avatar=(__webpack_require__(425728),__webpack_require__(260228),__webpack_require__(574752)),AvatarPopovervue_type_template_id_8ef9b9ec_render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"avatar-popover-container"},[_c("el-popover",{ref:"popover1",staticStyle:{display:"block",padding:"0"},attrs:{placement:"top-start",trigger:"hover",width:"383",clas:"avatar-popover","popper-class":"avatar-popover",height:_vm.defaultheight,"open-delay":600},model:{value:_vm.popoverShown,callback:function($$v){_vm.popoverShown=$$v},expression:"popoverShown"}},[_c("div",{staticClass:"popover-content-block"},[_c("div",{staticClass:"popover-avatar"},[_c("avatar",{attrs:{size:_vm.size,user:_vm.user,color:_vm.color,showPopover:!1}})],1),_c("div",{staticClass:"popover-avatar-det"},[_c("div",{staticClass:"avatar-name-txt"},[_vm._v(" "+_vm._s(_vm.user.name?_vm.user.name:"----")+" ")]),_c("div",{staticClass:"mail-block"},[_c("img",{staticClass:"envelope-icon",staticStyle:{height:"13px","vertical-align":"middle"},attrs:{src:__webpack_require__(702638)}}),_c("span",{staticClass:"mail-txt"},[_vm._v(_vm._s(_vm.user.email?_vm.user.email:"----")+" ")])]),_c("div",[_c("img",{staticClass:"envelope-icon",staticStyle:{height:"15px"},attrs:{src:__webpack_require__(47424)}}),_c("span",{staticClass:"popover-phone-number"},[_vm._v(" "+_vm._s(_vm.user.mobile?_vm.user.mobile:"----")+" ")])])]),"workorder"===_vm.moduleName?_c("div",{staticClass:"popover-footer"},[_c("ul",[_c("li",{staticClass:"popover-footer-list",on:{click:function($event){return _vm.getOpen("open")}}},[_c("div",{staticClass:"open-result-txt"},[_vm._v(" "+_vm._s(_vm.result.openWorkOrdersCount)+" ")]),_c("div",{staticClass:"result-txt-black"},[_vm._v("OPEN")])]),_c("li",{staticClass:"popover-footer-list border-RL",on:{click:function($event){return _vm.getOpen("overdue")}}},[_c("div",{staticClass:"overdue-result-txt"},[_vm._v(" "+_vm._s(_vm.result.overDueWorkOrdersCount)+" ")]),_c("div",{staticClass:"result-txt-black"},[_vm._v("OVERDUE")])]),_c("li",{staticClass:"popover-footer-list",on:{click:function($event){return _vm.getOpen("duetoday")}}},[_c("div",{staticClass:"dueday-result-txt"},[_vm._v(" "+_vm._s(_vm.result.dueTodayWorkOrdersCount)+" ")]),_c("div",{staticClass:"result-txt-black"},[_vm._v("DUE TODAY")])])])]):_vm._e()]),_c("template",{slot:"reference"},[_vm._t("reference")],2)],2)],1)},AvatarPopovervue_type_template_id_8ef9b9ec_staticRenderFns=[],AvatarPopovervue_type_script_lang_js=(__webpack_require__(670560),__webpack_require__(648324),{props:["size","user","color","name","moduleName"],data:function(){return{defaultheight:"230px",result:{},popoverShown:!1,viewname:""}},components:{Avatar:Avatar["default"]},mounted:function(){
// this.showPopover = this.showpopoverFooter
this.moduleName&&(this.defaultheight="160px")},watch:{popoverShown:function(val){val&&this.userOpenDatas()}},methods:{userOpenDatas:function(){var self=this;self.$http.post("/widget/getUserCardData",{ouid:this.user.id}).then((function(response){200===response.status&&(self.result=response.data.result)}))},getOpen:function(viewName){var filter={assignedTo:{operator:"is",value:[this.user.id+""]}};this.$router.push("/app/wo/orders/"+viewName+"?search="+encodeURIComponent(JSON.stringify(filter)))}}}),components_AvatarPopovervue_type_script_lang_js=AvatarPopovervue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_AvatarPopovervue_type_script_lang_js,AvatarPopovervue_type_template_id_8ef9b9ec_render,AvatarPopovervue_type_template_id_8ef9b9ec_staticRenderFns,!1,null,null,null)
/* harmony default export */,AvatarPopover=component.exports,vuex_esm=__webpack_require__(420629),Uservue_type_script_lang_js={created:function(){this.$store.dispatch("loadGroups")},components:{Avatar:Avatar["default"],AvatarPopover:AvatarPopover},props:{size:{type:String},user:{type:Object},avatarStyle:{type:Object},name:{type:Boolean,default:function(){return!0}},showLabel:{type:Boolean},showPopover:{type:Boolean},moduleName:{type:String},group:{type:Object},hovercard:{type:Boolean}},data:function(){return{hoverCardId:"userHoverCardId-"+(this.user?this.user.id:"unkown"),loadHoverCardData:!1,labelStatus:this.hideLabel}},computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapState */.rn)({users:function(state){return state.users}})),{},{userObj:function(){var _this=this,hasUser=this.user&&this.user.id>0,user=hasUser?this.$getProperty(this.user,"name")?this.user:this.users.find((function(usr){return usr.id===_this.user.id})):null;return user||{id:-1,name:"Unknown"}},color:function(){return-1!==this.userObj.id?"":this.group?"#c3c3c3":"#e3e3e3"}}),methods:{loadHoverCard:function(){this.loadHoverCardData=!0},hideHoverCard:function(){this.loadHoverCardData=!1}}},avatar_Uservue_type_script_lang_js=Uservue_type_script_lang_js,User_component=(0,componentNormalizer/* default */.Z)(avatar_Uservue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,User=User_component.exports},
/***/874249:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return RichTextArea}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/forms/RichTextArea.vue?vue&type=template&id=7711575b
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _vm.isRichText?_c("div",{staticClass:"richtext-area"},[_c("RichText",{style:{minHeight:_vm.minHeight,maxHeight:_vm.maxHeight,overflow:_vm.overflow},attrs:{readOnly:_vm.disabled,placeholder:_vm.placeholder,hideImgTool:_vm.hideImgTool,hideUnorderedList:_vm.hideUnorderedList,hideOrderedList:_vm.hideOrderedList,secondaryToolbar:_vm.secondaryToolbar,istoolbarPositionBottom:_vm.istoolbarPositionBottom,isMoreOption:_vm.isMoreOption,customToolOrder:_vm.customToolOrder,iconColor:_vm.iconColor},scopedSlots:_vm._u([_vm.canShowModeToggle?{key:"editorMode",fn:function(){return[_c("div",{staticClass:"self-center marginL-auto mR15 cursor-pointer mode-btn",on:{click:function($event){return _vm.changeMode()}}},[_vm._v(" "+_vm._s(_vm.$t("forms.builder.plain_text_mode"))+" ")])]},proxy:!0}:null],null,!0),model:{value:_vm.content,callback:function($$v){_vm.content=$$v},expression:"content"}})],1):_c("div",{staticClass:"richtext-area plain-text-mode"},[_c("div",{staticClass:"richtext-editor"},[_c("div",{staticClass:"richtext-toolbar"},[_c("div",{staticClass:"toolbar-button marginL-auto cursor-pointer mode-btn",on:{click:function($event){return _vm.changeMode()}}},[_vm._v(" "+_vm._s(_vm.$t("forms.builder.switch_to_richtext"))+" ")])]),_c("el-input",{key:"plain-text",staticClass:"richtext-content",attrs:{type:"textarea",resize:"none",placeholder:_vm.placeholder},model:{value:_vm.content,callback:function($$v){_vm.content=$$v},expression:"content"}})],1)])},staticRenderFns=[],ui_forms=(__webpack_require__(879288),__webpack_require__(141323)),filters=__webpack_require__(638203),RichTextAreavue_type_script_lang_js={props:{value:{type:String},field:{type:Object},rows:{type:Number,default:10},maxRows:{type:Number,default:12},disabled:{type:Boolean,default:!1},placeholder:{type:String,default:"Enter text here"},isEdit:{type:Boolean,default:!1},hideImgTool:{type:Boolean,default:!1},hideOrderedList:{type:Boolean,default:!1},hideUnorderedList:{type:Boolean,default:!1},isRichTextMode:{type:Boolean,default:!0},canShowModeToggle:{type:Boolean,default:!1},istoolbarPositionBottom:{type:Boolean},customToolOrder:{type:Array},isMoreOption:{type:Boolean},secondaryToolbar:{type:Boolean},iconColor:{type:String}},components:{RichText:ui_forms/* RichText */.Ho},computed:{content:{get:function(){return this.value},set:function(value){this.$emit("input",value)}},isRichText:{get:function(){return this.isRichTextMode},set:function(value){this.$emit("update:isRichTextMode",value)}},
// Assuming a <p></p> tag inside editor
// has fonts-size 1rem and margin-bottom 1rem
minHeight:function(){return"".concat(2*this.rows,"rem")},maxHeight:function(){return"".concat(2*this.maxRows,"rem")},overflow:function(){return"hidden"}},methods:{changeMode:function(){var isRichText=this.isRichText,value=this.value;if(isRichText)this.showConfirmModeSwitch();else{var htmlContent="<p>".concat(value,"</p>");this.content=htmlContent,this.proceedChangingModes()}},showConfirmModeSwitch:function(){var _this=this,dialogObj={htmlMessage:"".concat(this.$t("forms.builder.switch_alert")),rbDanger:!0,rbLabel:"Confirm"};this.$dialog.confirm(dialogObj).then((function(canProceed){if(canProceed){var value=_this.value,textContent=(0,filters/* htmlToText */.s9)(value);_this.content=textContent,_this.proceedChangingModes()}}))},proceedChangingModes:function(){var isRichText=this.isRichText;this.isRichText=!isRichText}}},forms_RichTextAreavue_type_script_lang_js=RichTextAreavue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(forms_RichTextAreavue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,RichTextArea=component.exports},
/***/786113:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return WorkOrderSpecialFieldsList}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/list/WorkOrderSpecialFieldsList.vue?vue&type=template&id=c5129894
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",["resource"===_vm.field.name?_c("div",{staticClass:"q-item-division relative-position",staticStyle:{"min-width":"170px"}},[_vm.$validation.isEmpty(_vm.moduleData.resource)||_vm.$validation.isEmpty(_vm.moduleData.resource.id)?_c("div",[_c("span",{staticClass:"q-item-label secondary-color color-d"},[_vm._v("--- "+_vm._s(_vm.$t("maintenance.wr_list.space_asset"))+" ---")])]):_c("div",[_c("div",{staticClass:"flLeft mR7"},[1===_vm.moduleData.resource.resourceType?_c("img",{staticStyle:{height:"12px",width:"14px"},attrs:{src:__webpack_require__(572902)}}):_c("img",{staticStyle:{height:"11px",width:"14px"},attrs:{src:__webpack_require__(30684)}})]),_c("span",{directives:[{name:"tippy",rawName:"v-tippy"}],staticClass:"flLeft q-item-label ellipsis max-width140px",attrs:{small:"","data-position":"bottom",title:_vm.moduleData.resource.name}},[_vm._v(_vm._s(_vm.moduleData.resource.name))])])]):"assignedTo"===_vm.field.name?_c("div",{staticClass:"q-item-division relative-position wo_assignedto_avatarblock"},[_c("div",{staticClass:"wo-assigned-avatar fL"},[_c("user-avatar",{attrs:{size:"md",user:_vm.moduleData.assignedTo,group:_vm.moduleData.assignmentGroup,showLabel:!0,moduleName:"lookupModuleName"}})],1)]):"noOfNotes"===_vm.field.name?_c("div",[_vm._m(0),_c("span",{staticClass:"width5px q-item-sublabel comment-counnt-txt"},[_vm._v(_vm._s(_vm.notesLabel))])]):"noOfTasks"===_vm.field.name?_c("div",[_vm.canShowTaskIcon?_c("inline-svg",{staticClass:"vertical-middle cursor-pointer",attrs:{src:"svgs/task-list",iconClass:"icon icon-sm mT5 mR5 task-icon"}}):_vm._e(),_c("span",{staticClass:"q-item-sublabel pL5"},[_vm._v(_vm._s(_vm.taskLabel))])],1):"noOfAttachments"===_vm.field.name?_c("div",{staticStyle:{float:"left"}},[_vm._m(1),_c("span",{staticClass:"width5px q-item-sublabel comment-counnt-txt"},[_vm._v(_vm._s(_vm.attachmentLabel))])]):_vm._e()])},staticRenderFns=[function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("span",[_c("img",{staticStyle:{width:"14px",height:"12.8px"},attrs:{src:__webpack_require__(293656)}})])},function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("span",[_c("img",{staticStyle:{width:"14px",height:"12.8px"},attrs:{src:__webpack_require__(345265)}})])}],User=(__webpack_require__(634338),__webpack_require__(426803)),validation=__webpack_require__(990260),WorkOrderSpecialFieldsListvue_type_script_lang_js={components:{UserAvatar:User/* default */.Z},props:["field","moduleData"],computed:{canShowTaskIcon:function(){var moduleData=this.moduleData;return!(0,validation/* isEmpty */.xb)(moduleData.noOfTasks)&&0!==moduleData.noOfNotes},taskLabel:function(){var moduleData=this.moduleData,noOfTasks=moduleData.noOfTasks,noOfNotes=moduleData.noOfNotes,noOfClosedTasks=moduleData.noOfClosedTasks,taskLabel="---";return(0,validation/* isEmpty */.xb)(noOfTasks)||0===noOfNotes?taskLabel:(0,validation/* isEmpty */.xb)(noOfClosedTasks)?"0":"".concat(noOfClosedTasks,"/").concat(noOfTasks)},notesLabel:function(){var moduleData=this.moduleData,noOfNotes=moduleData.noOfNotes;return(0,validation/* isEmpty */.xb)(noOfNotes)||0===noOfNotes?"0":moduleData.noOfNotes},attachmentLabel:function(){var moduleData=this.moduleData,_ref=moduleData||{},noOfAttachments=_ref.noOfAttachments;return(0,validation/* isEmpty */.xb)(noOfAttachments)||0===noOfAttachments?"0":noOfAttachments}}},list_WorkOrderSpecialFieldsListvue_type_script_lang_js=WorkOrderSpecialFieldsListvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(list_WorkOrderSpecialFieldsListvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,WorkOrderSpecialFieldsList=component.exports},
/***/806339:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return WidgetPagination}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/page/widget/utils/WidgetPagination.vue?vue&type=template&id=6b10cd5b
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"fc-widget-pagination"},[_vm.total>0?_c("div",{class:{"fc-black-small-txt-12":!_vm.hideToggle}},[_vm.from!==_vm.to?_c("span",[_vm._v(_vm._s(_vm.from)+" -")]):_vm._e(),_c("span",[_vm._v(_vm._s(_vm.to))]),_vm.hideToggle?_vm._e():[_vm._v(" of "+_vm._s(_vm.total)+" "),_c("span",{staticClass:"el-icon-arrow-left fc-black-small-txt-12 fw-bold f16 pointer mL10 mR5",class:{disable:_vm.from<=1},on:{click:function($event){_vm.from>1&&_vm.prev()}}}),_c("span",{staticClass:"el-icon-arrow-right fc-black-small-txt-12 f16 pointer mR10 fw-bold",class:{disable:_vm.to===_vm.total},on:{click:function($event){_vm.to!==_vm.total&&_vm.next()}}})]],2):_vm._e()])},staticRenderFns=[],WidgetPaginationvue_type_script_lang_js={props:["currentPage","perPage","total","hideToggle"],data:function(){return{from:0,to:0,page:1}},mounted:function(){this.init()},watch:{total:function(){this.init()},currentPage:function(val){val!==this.page&&this.init()}},methods:{init:function(){this.page=this.currentPage||1,this.from=(this.page-1)*this.perPage+1;var to=this.from+this.perPage-1;this.to=this.total>to?to:this.total},next:function(){this.from=this.to+1,this.to+=this.perPage,this.to>this.total&&(this.to=this.total),this.page++,this.updateCurrentPage()},prev:function(){this.to=this.from-1,this.from-=this.perPage,this.from<=1?this.from=this.page=1:this.page--,this.updateCurrentPage()},updateCurrentPage:function(){
// Update currentPage value
this.$emit("update:currentPage",this.page),this.$emit("onChange")}}},utils_WidgetPaginationvue_type_script_lang_js=WidgetPaginationvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(utils_WidgetPaginationvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,WidgetPagination=component.exports},
/***/243300:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return InspectionHistory}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/inspection/inspection-template/InspectionHistory.vue?vue&type=template&id=ccb4bec8
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{ref:"relatedListContainer",staticClass:"related-list-container"},[_c("div",{staticClass:"related-list-header"},[_c("div",{staticClass:"header justify-content-space"},[_c("div",{staticClass:"widget-title d-flex flex-direction-column justify-center"},[_vm._v(" "+_vm._s(_vm.moduleDisplayName?_vm.moduleDisplayName:_vm.moduleName)+" ")]),_vm.$validation.isEmpty(_vm.modulesList)?_vm._e():[_vm.showMainFieldSearch?_c("el-input",{ref:"mainFieldSearchInput",staticClass:"fc-input-full-border2 width-auto mL-auto",attrs:{"suffix-icon":"el-icon-search"},on:{blur:_vm.hideMainFieldSearch},model:{value:_vm.searchText,callback:function($$v){_vm.searchText=$$v},expression:"searchText"}}):_c("span",{staticClass:"self-center mL-auto",on:{click:_vm.openMainFieldSearch}},[_vm.widgetParams.hideSearchField&&_vm.hideSearchFieldList.includes(_vm.moduleName)?_vm._e():_c("inline-svg",{staticClass:"vertical-middle cursor-pointer",attrs:{src:"svgs/search",iconClass:"icon icon-sm mT5 mR5 search-icon"}})],1),_vm.$validation.isEmpty(_vm.totalCount)||_vm.$validation.isEmpty(_vm.modulesList)||_vm.hideSearchFieldList.includes(_vm.moduleName)?_vm._e():_c("span",{staticClass:"separator self-center"},[_vm._v("|")]),_c("pagination",{staticClass:"self-center",attrs:{currentPage:_vm.page,total:_vm.totalCount,perPage:_vm.perPage},on:{"update:currentPage":function($event){_vm.page=$event},"update:current-page":function($event){_vm.page=$event}}})]],2)]),_c("div",{staticClass:"position-relative m10"},[_vm.isLoading?_c("div",{staticClass:"loading-container d-flex justify-content-center"},[_c("spinner",{attrs:{show:_vm.isLoading}})],1):_vm.$validation.isEmpty(_vm.modulesList)?_c("div",{staticClass:"flex-middle justify-content-center wo-flex-col flex-direction-column",staticStyle:{"margin-top":"4%"}},[_c("inline-svg",{attrs:{src:"svgs/emptystate/readings-empty",iconClass:"icon text-center icon-xxxlg"}}),_c("div",{staticClass:"pT10 fc-black-dark f18 bold pB50"},[_vm._v(" "+_vm._s(_vm.$t("common.products.no_mod_available"))+" "+_vm._s(_vm.moduleDisplayName?_vm.moduleDisplayName:_vm.moduleName)+" "+_vm._s(_vm.$t("common._common.available"))+" ")])],1):_c("div",{staticClass:"fc-list-view fc-table-td-height"},[_vm.canShowColumnIcon?_c("div",{staticClass:"view-column-chooser",on:{click:_vm.showColumnCustomization}},[_c("img",{staticClass:"column-customization-icon",attrs:{src:__webpack_require__(266707)}})]):_vm._e(),_c("el-table",{staticStyle:{width:"100%"},attrs:{data:_vm.modulesList,fit:!0,height:_vm.tableHeight,"header-cell-style":{background:"#f3f1fc"}}},[_c("el-table-column",{attrs:{label:"Name",fixed:"","min-width":"200"},scopedSlots:_vm._u([{key:"default",fn:function(inspection){return[_c("div",{staticClass:"table-subheading  main-field-column",on:{click:function($event){return _vm.redirectToOverview(inspection.row)}}},[_c("div",{staticClass:"d-flex"},[_c("div",{staticClass:"self-center name bold"},[_vm._v(" "+_vm._s((inspection.row.parent||{}).name||"---")+" ")])])])]}}])}),_vm._l(_vm.filteredViewColumns,(function(field,index){return _c("el-table-column",{key:index,attrs:{prop:field.name,label:_vm.getColumnHeaderLabel(field),align:"DECIMAL"===(field.field||{}).dataTypeEnum?"right":"left","min-width":"200"},scopedSlots:_vm._u([{key:"default",fn:function(scope){return[_vm.isFileTypeField((field||{}).field)?_c("div",[_c("FilePreviewColumn",{attrs:{field:field,record:scope.row,isV2:!0}})],1):_c("div",{staticClass:"table-subheading",class:{"text-right":"DECIMAL"===(field.field||{}).dataTypeEnum}},[_vm._v(" "+_vm._s(_vm.getColumnDisplayValue(field,scope.row)||"---")+" ")])]}}],null,!0)})})),_vm.canShowDelete||_vm.canShowEdit?_c("el-table-column",{staticClass:"visibility-visible-actions",attrs:{prop:"",label:"",width:"130"},scopedSlots:_vm._u([{key:"default",fn:function(item){return[_c("div",{staticClass:"text-center"},[_vm.canShowDelete?_c("span",{on:{click:function($event){return _vm.invokeDeleteDialog(item.row)}}},[_c("inline-svg",{staticClass:"pointer edit-icon-color visibility-hide-actions mL10",attrs:{src:"svgs/delete",iconClass:"icon icon-sm icon-remove"}})],1):_vm._e()])]}}],null,!1,4069878596)}):_vm._e()],2)],1)]),_c("ColumnCustomization",{attrs:{visible:_vm.canShowColumnCustomization,moduleName:_vm.moduleName,columnConfig:_vm.columnCustomizationConfig,relatedViewDetail:_vm.viewDetail,relatedMetaInfo:_vm.currentMetaInfo,viewName:"hidden-all"},on:{"update:visible":function($event){_vm.canShowColumnCustomization=$event},refreshRelatedList:_vm.refreshRelatedList}})],1)},staticRenderFns=[],regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),RelatedListWidget=(__webpack_require__(260228),__webpack_require__(648324),__webpack_require__(434284),__webpack_require__(77645)),validation=__webpack_require__(990260),api=__webpack_require__(32284),router=__webpack_require__(329435),isEqual=__webpack_require__(618446),isEqual_default=__webpack_require__.n(isEqual),InspectionHistoryvue_type_script_lang_js={props:["details","widget","resizeWidget"],name:"InspectionHistory",extends:RelatedListWidget/* default */.Z,watch:{page:{handler:function(newVal,oldVal){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var isSearchDataLoading;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:if(isEqual_default()(newVal,oldVal)){_context.next=9;break}if(isSearchDataLoading=_this.isSearchDataLoading,isSearchDataLoading){_context.next=9;break}return _this.isLoading=!0,_context.next=6,_this.fetchModulesData(!0);case 6:return _context.next=8,_this.loadDataCount(!0);case 8:_this.isLoading=!1;case 9:case"end":return _context.stop()}}),_callee)})))()}}},computed:{moduleName:function(){return"inspectionResponse"},moduleDisplayName:function(){return"Inspection"},moduleHeaderName:function(){return"Inspection"},filters:function(){var _ref=this||{},details=_ref.details,_ref2=details||{},id=_ref2.id;return{parent:{operatorId:36,value:["".concat(id)]}}}},methods:{fetchModulesData:function(){var _this2=this,moduleName=this.moduleName,page=this.page;return api/* API */.bl.fetchAll(moduleName,{page:page,perPage:10,withCount:!0,filters:this.filters?JSON.stringify(this.filters):null}).then((function(_ref3){var list=_ref3.list,error=_ref3.error;if(error)_this2.$message.error(error.message);else if(!(0,validation/* isEmpty */.xb)(list)){var moduleDatas=list;(0,validation/* isNullOrUndefined */.le)(moduleDatas)||_this2.$set(_this2,"modulesList",moduleDatas)}})).catch((function(_ref4){var message=_ref4.message;_this2.$message.error(message)})).finally((function(){_this2.isSearchDataLoading=!1,_this2.autoResize()}))},autoResize:function(){var _this3=this;this.$nextTick((function(){var container=_this3.$refs["relatedListContainer"];if(container){var width=container.scrollWidth;_this3.resizeWidget&&_this3.resizeWidget({height:500,width:width})}}))},redirectToOverview:function(record){var route,_ref5=record||{},id=_ref5.id,moduleName=this.moduleName;if((0,router/* isWebTabsEnabled */.tj)()){var _findRouteForModule=(0,router/* findRouteForModule */.Jp)(moduleName,router/* pageTypes */.As.OVERVIEW),name=_findRouteForModule.name;name&&(route=this.$router.resolve({name:name,params:{id:id,viewname:"all"}}))}else route=this.$router.resolve({name:"individualInspectionSummary",params:{id:id,viewname:"all"}});route&&window.open(route.href,"_blank")}}},inspection_template_InspectionHistoryvue_type_script_lang_js=InspectionHistoryvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(inspection_template_InspectionHistoryvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,InspectionHistory=component.exports},
/***/706881:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return CurrencyPopOver}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/setup/organizationSetting/currency/CurrencyPopOver.vue?vue&type=template&id=434b446a&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",[_vm.showCurrencyField?_c("div",{staticClass:"d-flex"},[_vm.showInfo?_c("span",[_vm._v(_vm._s(_vm.getMultiCurrencyFieldValue||"---"))]):_vm._e(),_vm.showMultiCurrencyInfo?_c("el-popover",{attrs:{placement:"right",title:_vm.$t("setup.currency.rate"),width:"230",trigger:"hover",content:_vm.currencyContent,"open-delay":5,disabled:_vm.isEmpty(_vm.baseValue)}},[_vm.showInfo?_vm._e():_c("div",{class:[_vm.baseValue&&"pointer"],attrs:{slot:"reference"},slot:"reference"},[_c("div",[_vm._v(_vm._s(""+_vm.getMultiCurrencyFieldValue))]),_c("div",{staticClass:"f12"},[_vm._v(_vm._s(_vm.baseValue))])]),_vm.isBaseCode||!_vm.showInfo||_vm.hideInfoBtn?_vm._e():_c("fc-icon",{staticClass:"pointer info-position mL2",attrs:{slot:"reference",group:"dsm",name:"info",size:"13"},slot:"reference"})],1):_c("span",[_vm._v(_vm._s(_vm.getMultiCurrencyFieldValue||"---"))])],1):_c("span",[_vm._v("---")])])},staticRenderFns=[],regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),objectSpread2=__webpack_require__(595082),validation=(__webpack_require__(425728),__webpack_require__(260228),__webpack_require__(634338),__webpack_require__(434284),__webpack_require__(990260)),CurrencyUtil=__webpack_require__(15426),vuex_esm=__webpack_require__(420629),CurrencyPopOvervue_type_script_lang_js={props:["field","details","showInfo","hideInfoBtn"],data:function(){return{baseValue:"",isEmpty:validation/* isEmpty */.xb}},computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapState */.rn)({account:function(state){return state.account},currencyList:function(state){return state.activeCurrencies}})),{},{showMultiCurrencyInfo:function(){var _ref=this.multiCurrency||{},multiCurrencyEnabled=_ref.multiCurrencyEnabled;return multiCurrencyEnabled},isBaseCode:function(){var _ref2=this.multiCurrency||{},baseCode=_ref2.currencyCode,_ref3=this.details||{},currencyCode=_ref3.currencyCode;return baseCode===currencyCode},showCurrencyField:function(){var currencyValue=this.currencyValue;return!(0,validation/* isEmpty */.xb)(currencyValue)&&"---"!=currencyValue},multiCurrency:function(){var _ref4=this.$getProperty(this.account,"data.currencyInfo")||{},displaySymbol=_ref4.displaySymbol,currencyCode=_ref4.currencyCode,multiCurrencyEnabled=_ref4.multiCurrencyEnabled;return{displaySymbol:displaySymbol,currencyCode:currencyCode,multiCurrencyEnabled:multiCurrencyEnabled}},currencyContent:function(){var details=this.details,currencyValue=this.currencyValue,baseSymbol=this.multiCurrency.displaySymbol;if(!(0,validation/* isEmpty */.xb)(currencyValue)){var _ref5=details||{},exchangeRate=_ref5.exchangeRate,currencyCode=_ref5.currencyCode,currency=(this.currencyList||[]).find((function(currency){return currencyCode===currency.currencyCode}));if((0,validation/* isEmpty */.xb)(currency)||currency.displaySymbol===baseSymbol)return null;var _ref6=currency||{},displaySymbol=_ref6.displaySymbol,content="".concat(baseSymbol," 1 = ").concat(displaySymbol," ").concat(exchangeRate);return content}return"---"},currencyValue:function(){var details=this.details,field=this.field,_ref7=details||{},data=_ref7.data;return field.displayValue||details[field.name]||(null===data||void 0===data?void 0:data[field.name])},getMultiCurrencyFieldValue:function(){var currencyValue=this.currencyValue,showInfo=this.showInfo,_this$multiCurrency=this.multiCurrency,baseSymbol=_this$multiCurrency.displaySymbol,baseCode=_this$multiCurrency.currencyCode,multiCurrencyEnabled=_this$multiCurrency.multiCurrencyEnabled,_ref8=this.details||{},currencyCode=_ref8.currencyCode,exchangeRate=_ref8.exchangeRate,currency=(this.currencyList||[]).find((function(currency){return currencyCode===currency.currencyCode})),baseCurrency=(this.currencyList||[]).find((function(currency){return baseCode===currency.currencyCode}));if((0,validation/* isEmpty */.xb)(currency)&&!(0,validation/* isEmpty */.xb)(currencyValue))return"".concat(baseSymbol," ").concat(currencyValue);var _ref9=currency||{},displaySymbol=_ref9.displaySymbol;displaySymbol=(0,validation/* isEmpty */.xb)(displaySymbol)?baseSymbol:displaySymbol;var baseValue="";if(multiCurrencyEnabled&&baseCode!==currencyCode){var value=(0,CurrencyUtil/* getCurrencyInDecimalValue */.Jg)(currencyValue/exchangeRate,baseCurrency);baseValue="( ".concat(baseSymbol," ").concat(value," )")}return currencyValue=(0,CurrencyUtil/* getCurrencyInDecimalValue */.Jg)(currencyValue,currency),this.setBaseValue(baseValue),showInfo?"".concat(displaySymbol," ").concat(currencyValue," ").concat(baseValue):"".concat(displaySymbol," ").concat(currencyValue)}}),created:function(){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return _context.next=2,_this.$store.dispatch("getActiveCurrencyList");case 2:case"end":return _context.stop()}}),_callee)})))()},methods:{setBaseValue:function(value){this.baseValue=value}}},currency_CurrencyPopOvervue_type_script_lang_js=CurrencyPopOvervue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(currency_CurrencyPopOvervue_type_script_lang_js,render,staticRenderFns,!1,null,"434b446a",null)
/* harmony default export */,CurrencyPopOver=component.exports},
/***/345265:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"35079cf978684c9b6a17e182df923ff5.svg";
/***/},
/***/293656:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"e179f34e6169d9164845bf6c1d403c8e.svg";
/***/},
/***/702638:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"763b584b7aa649bd99722d2be3c8036a.svg";
/***/},
/***/47424:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"400bdd76d58614975db74618a6f968ef.svg";
/***/},
/***/30684:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"52c2ff301190417db7febd08d4cd5153.svg";
/***/},
/***/572902:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"db5324b7cf9058a0aa45899e55bf6444.svg";
/***/}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/305.js.map