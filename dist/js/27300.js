(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[27300],{
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
/***/227300:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return NextScheduledCard}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/page/widget/pm/NextScheduledCard.vue?vue&type=template&id=168cae7c
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"p30 d-flex flex-direction-column"},[_c("div",{staticClass:"flex-middle justify-content-space"},[_c("div",{staticClass:"f13 bold text-uppercase text-uppercase fc-black-13 text-left"},[_vm._v(" "+_vm._s(_vm.$t("asset.maintenance.next_scheduled_pm"))+" ")]),_c("InlineSvg",{attrs:{src:"svgs/flag",iconClass:"icon icon-xl"}})],1),_vm.$validation.isEmpty(_vm.workorder)?_c("div",{staticClass:"empty-grey-13 pT50 text-center"},[_c("InlineSvg",{attrs:{src:"svgs/emptystate/scheduled",iconClass:"icon text-center icon-xxxlg"}}),_c("div",{staticClass:"empty-grey-desc2"},[_vm._v(" "+_vm._s(_vm.$t("asset.maintenance.no_scheduled_pm"))+" ")])],1):[_c("div",{staticClass:"fc-black-13 pT15 text-left pointer",on:{click:function($event){return _vm.goToSummary(_vm.workorder.id)}}},[_vm._v(" "+_vm._s(_vm.workorder.subject)+" ")]),_c("div",{staticClass:"mT25 pointer",on:{click:function($event){return _vm.goToSummary(_vm.workorder.id)}}},[_c("div",{staticClass:"d-flex flex-direction-row mB15 flex-middle"},[_c("div",{staticClass:"mR15 summary-widget-icon-bg light-green"},[_c("InlineSvg",{attrs:{src:"svgs/calendar",iconClass:"icon icon-lg fc-white-color text-center pL3 vertical-baseline"}})],1),_c("div",[_c("p",{staticClass:"fc-black3-16 bold",staticStyle:{"margin-bottom":"2px"}},[_vm._v(" "+_vm._s(_vm._f("formatDate")(_vm.workorder.scheduledStart))+" ")]),_c("div",{staticClass:"f12",staticStyle:{color:"#8ca1ad"}},[_vm._v(" "+_vm._s(_vm.$t("asset.maintenance.scheduled_on"))+" ")])])]),_c("div",{staticClass:"d-flex flex-direction-row mT5 mB15"},[_c("user-avatar",{attrs:{size:"md",user:_vm.workorder.assignedTo,group:_vm.workorder.assignmentGroup,showPopover:!0,showLabel:!0,moduleName:"workorder"}})],1),_c("div",{staticClass:"d-flex flex-direction-row mB15 flex-middle"},[_c("div",{staticClass:"mR15 summary-widget-icon-bg dark-green"},[_c("InlineSvg",{attrs:{src:"svgs/clock",iconClass:"icon icon-sm fc-white-color mT4"}})],1),_c("div",[_c("p",{staticClass:"m0 bold"},[_vm._v(_vm._s(_vm.formattedResolveTime))]),_c("div",{staticClass:"f12",staticStyle:{color:"#8ca1ad"}},[_vm._v(" "+_vm._s(_vm.$t("asset.maintenance.est_resolve_time"))+" ")])])])])]],2)},staticRenderFns=[],User=(__webpack_require__(648324),__webpack_require__(434284),__webpack_require__(670560),__webpack_require__(426803)),moment_timezone=__webpack_require__(480008),moment_timezone_default=__webpack_require__.n(moment_timezone),validation=__webpack_require__(990260),InlineSvg=__webpack_require__(586166),router=__webpack_require__(329435),NextScheduledCardvue_type_script_lang_js={components:{UserAvatar:User/* default */.Z,InlineSvg:InlineSvg/* default */.Z},props:["details","layoutParams","hideTitleSection","activeTab","widget","resizeWidget"],data:function(){return{workorder:null}},mounted:function(){this.loadData()},methods:{loadData:function(){var _this=this,filters=encodeURIComponent(JSON.stringify({resource:{operatorId:36,value:[this.details.id+""]},sourceType:{operatorId:9,value:["5"]},createdTime:{operatorId:73}})),url="/v2/workorders/view/all?filters=".concat(filters,"&fetchAllType=true&orderBy=createdTime&orderType=asc&overrideViewOrderBy=true&page=1&perPage=1");this.$http.get(url).then((function(response){0===response.data.responseCode?_this.workorder=response.data.result.workorders?response.data.result.workorders[0]:{}:(
// TODO handle errors
_this.workorder={},_this.loading=!1)}))},goToSummary:function(id){if((0,router/* isWebTabsEnabled */.tj)()){var _ref=(0,router/* findRouteForModule */.Jp)("workorder",router/* pageTypes */.As.OVERVIEW)||{},name=_ref.name;name&&this.$router.push({name:name,params:{viewname:"all",id:id}})}else this.$router.push({path:"/app/wo/orders/summary/".concat(id)})}},computed:{formattedResolveTime:function(){if((0,validation/* isEmpty */.xb)(this.workorder)||(0,validation/* isEmpty */.xb)(this.workorder.dueDate)||(0,validation/* isEmpty */.xb)(this.workorder.scheduledStart))return"---";var duration=moment_timezone_default().duration(moment_timezone_default()(this.workorder.dueDate).diff(moment_timezone_default()(this.workorder.scheduledStart)));return this.$helpers.getFormattedDuration(duration,null,!0)}}},pm_NextScheduledCardvue_type_script_lang_js=NextScheduledCardvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(pm_NextScheduledCardvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,NextScheduledCard=component.exports},
/***/702638:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"763b584b7aa649bd99722d2be3c8036a.svg";
/***/},
/***/47424:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"400bdd76d58614975db74618a6f968ef.svg";
/***/}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/27300.js.map