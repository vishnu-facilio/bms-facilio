"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[28322],{
/***/574752:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return Avatar}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/Avatar.vue?vue&type=template&id=7f0d5689
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _vm.user||_vm.name?_c("div",{staticClass:"fc-avatar-inline"},[_vm.avatarUrl?_c("img",{staticClass:"fc-avatar",class:_vm.sizeClass,attrs:{src:_vm.avatarUrl,alt:"Profile Avatar"}}):_c("div",{staticClass:"fc-avatar",class:_vm.sizeClass,style:_vm.bgColor},[_vm._v(" "+_vm._s(_vm.trimmedName)+" ")])]):_vm._e()},staticRenderFns=[],objectSpread2=__webpack_require__(595082),vuex_esm=(__webpack_require__(425728),__webpack_require__(260228),__webpack_require__(434284),__webpack_require__(564043),__webpack_require__(709873),__webpack_require__(228436),__webpack_require__(450886),__webpack_require__(677049),__webpack_require__(821694),__webpack_require__(420629)),Avatarvue_type_script_lang_js={props:{size:{type:String},user:{type:Object},avatarStyle:{type:Object},color:{type:String},name:{type:Boolean}},data:function(){return{sizeClass:"fc-avatar-"+this.size,fullName:"",trimmedName:null,avatarUrl:null,bgColor:"background-color: "+(this.color?this.color:this.getRandomBgColor())}},computed:(0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapState */.rn)({users:function(state){return state.users}})),mounted:function(){var _this=this;if(this.user){var userName="";if(this.user.avatarUrl)this.avatarUrl=this.user.avatarUrl;else if(this.user.id){var userObj=this.users.find((function(user){return user.id===_this.user.id}));userObj&&userObj.avatarUrl&&(this.avatarUrl=userObj.avatarUrl),userObj&&userObj.name&&(userName=userObj.name)}userName=userName||this.user.name,userName&&(this.fullName=userName,this.trimmedName=this.getAvatarName(userName),this.bgColor="background-color: "+(this.color?this.color:this.getRandomBgColor()))}else this.name&&(this.trimmedName=this.getAvatarName(this.name),this.bgColor="background-color: "+(this.color?this.color:this.getRandomBgColor()))},watch:{user:function(){var _this2=this;if(this.user.avatarUrl)this.avatarUrl=this.user.avatarUrl;else if(this.user.id){var userObj=this.users.find((function(user){return user.id===_this2.user.id}));userObj&&userObj.avatarUrl?this.avatarUrl=userObj.avatarUrl:this.avatarUrl=null}else this.avatarUrl=null;this.user.name&&(this.fullName=this.user.name,this.trimmedName=this.getAvatarName(this.user.name),this.bgColor="background-color: "+(this.color?this.color:this.getRandomBgColor()))},"user.name":function(){this.user.name&&(this.fullName=this.user.name,this.trimmedName=this.getAvatarName(this.user.name),this.bgColor="background-color: "+(this.color?this.color:this.getRandomBgColor()))},"user.avatarUrl":function(){this.user.avatarUrl?this.avatarUrl=this.user.avatarUrl:this.avatarUrl=null},"user.id":function(){var _this3=this;if(this.user.id){var userObj=this.users.find((function(user){return user.id===_this3.user.id}));userObj&&userObj.avatarUrl?this.avatarUrl=userObj.avatarUrl:this.avatarUrl=null}else this.avatarUrl=null}},methods:{getAvatarName:function(name){for(var parts=name.split(/[ -]/),initials="",initialLen=2,count=0,i=0;i<parts.length;i++)if(""!==parts[i].trim()&&(initials+=parts[i].charAt(0),count++,count>=initialLen))break;initials.length<initialLen&&name.length>=initialLen&&(initials=name.trim().substring(0,initialLen));var avatarName=initials.toUpperCase();return avatarName},getRandomBgColor:function(){var colors=["#FFBA51","#34BFA3","#FF2F82","#29D9A7","#ECDC74","#927FED","#FF61A8","#fbf383","#ac4352","#6db1f4"],userKey=this.user.email?this.user.email:this.user.name,userUniqueNum=Array.from(userKey).map((function(letter){return letter.charCodeAt(0)})).reduce((function(current,previous){return previous+current})),colorIndex=userUniqueNum%colors.length,color=colors[colorIndex];return color}}},components_Avatarvue_type_script_lang_js=Avatarvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_Avatarvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,Avatar=component.exports},
/***/128322:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return FireAlarmNotifications}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/setup/FireAlarmNotifications.vue?vue&type=template&id=32a5f7fc
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"notification-layout row"},[_vm._m(0),_c("div",{staticClass:"container-scroll"},[_c("div",{staticClass:"setting-Rlayout mT20"},[_c("div",{staticClass:"notificationDiv"},[_c("table",{attrs:{border:"0",cellpadding:"0",cellspacing:"0"}},[_vm._m(1),_c("tbody",_vm._l(_vm.rules,(function(rule){return _c("tr",{key:rule.id},["Create Alarm - EMail"===rule.name?_c("td",[_vm._v("Email")]):_c("td",[_vm._v("SMS")]),_c("td",[_c("q-toggle",{on:{input:function($event){return _vm.changeStatus(rule)}},model:{value:rule.status,callback:function($$v){_vm.$set(rule,"status",$$v)},expression:"rule.status"}})],1),"Create Alarm - EMail"===rule.name?_c("td",[_c("div",[_vm._l(_vm.emails,(function(emailId,index){return _c("span",{key:index,staticStyle:{padding:"1px"}},[_c("avatar",{attrs:{size:"md",user:{name:emailId}}})],1)})),_c("span",{staticStyle:{padding:"1px"}},[_c("div",{staticClass:"fc-avatar fc-avatar-md",staticStyle:{"background-color":"#e2e2e2",color:"grey"}},[_c("i",{staticClass:"fa fa-plus"}),_c("q-tooltip",{attrs:{anchor:"top middle",self:"bottom middle",offset:[10,10]}},[_vm._v("Notify user")]),_c("q-popover",{ref:"emailPopover",refInFor:!0,staticStyle:{"min-width":"350px"}},[_c("q-list",{staticClass:"no-border",attrs:{link:""}},[_c("q-item",{attrs:{tag:"label"}},[_c("q-item-main",[_c("q-input",{attrs:{"float-label":"Email",type:"textarea"},model:{value:_vm.email,callback:function($$v){_vm.email=$$v},expression:"email"}})],1)],1),_c("q-item",{staticClass:"catagoryFooter",attrs:{tag:"label"}},[_c("q-item-side",[_c("q-btn",{staticClass:"catagoryFooterBtn",staticStyle:{"font-weight":"500","font-size":"13px"},attrs:{outline:"",color:"primary",small:""},on:{click:function($event){return _vm.addEmailTemplate()}}},[_vm._v("Add")])],1)],1)],1)],1)],1)])],2)]):_c("td",[_c("div",[_vm._l(_vm.phoneNumbers,(function(phoneNo,index){return _c("span",{key:index,staticStyle:{padding:"1px"}},[_c("avatar",{attrs:{size:"md",user:{name:phoneNo}}})],1)})),_c("span",{staticStyle:{padding:"1px"}},[_c("div",{staticClass:"fc-avatar fc-avatar-md",staticStyle:{"background-color":"#e2e2e2",color:"grey"}},[_c("i",{staticClass:"fa fa-plus"}),_c("q-tooltip",{attrs:{anchor:"top middle",self:"bottom middle",offset:[10,10]}},[_vm._v("Notify user")]),_c("q-popover",{ref:"phPopover",refInFor:!0,staticStyle:{"min-width":"350px"}},[_c("q-list",{staticClass:"no-border",attrs:{link:""}},[_c("q-item",{attrs:{tag:"label"}},[_c("q-item-main",[_c("q-input",{attrs:{"float-label":"Mobile Number",type:"textarea"},model:{value:_vm.phone,callback:function($$v){_vm.phone=$$v},expression:"phone"}})],1)],1),_c("q-item",{staticClass:"catagoryFooter",attrs:{tag:"label"}},[_c("q-item-side",[_c("q-btn",{staticClass:"catagoryFooterBtn",staticStyle:{"font-weight":"500","font-size":"13px"},attrs:{outline:"",color:"primary",small:""},on:{click:function($event){return _vm.addSMSTemplate()}}},[_vm._v("Add")])],1)],1)],1)],1)],1)])],2)])])})),0)])])])])])},staticRenderFns=[function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"setting-header"},[_c("div",{staticClass:"setting-title-block"},[_c("div",{staticClass:"setting-form-title"},[_vm._v("Alarm Notifications")]),_c("div",{staticClass:"heading-description"},[_vm._v("List of all Alarm notifications")])])])},function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("thead",[_c("tr",[_c("th",{staticStyle:{width:"25%"}},[_vm._v("Notification")]),_c("th",{staticStyle:{width:"20%"}},[_vm._v("Status")]),_c("th",{staticStyle:{width:"55%"}},[_vm._v("Notify To")])])])}],Avatar=(__webpack_require__(434284),__webpack_require__(670560),__webpack_require__(574752)),quasar_esm=__webpack_require__(641358),FireAlarmNotificationsvue_type_script_lang_js={title:function(){return"New Alarm Notifications"},components:{QTooltip:quasar_esm/* QTooltip */.DB,QPopover:quasar_esm/* QPopover */.oe,QList:quasar_esm/* QList */.tu,QItem:quasar_esm/* QItem */.ry,QItemMain:quasar_esm/* QItemMain */.Ui,QInput:quasar_esm/* QInput */.Ke,QBtn:quasar_esm/* QBtn */.OV,QItemSide:quasar_esm/* QItemSide */.hJ,QToggle:quasar_esm/* QToggle */.Sr,Avatar:Avatar["default"]},data:function(){return{rules:[],emails:[],phoneNumbers:[],email:null,phone:null,headerJson:["Workorder","Alarm"]}},mounted:function(){this.loadNotifications()},methods:{loadNotifications:function(){var self=this;self.$http.get("/setup/alarmrules").then((function(response){self.rules=response["data"].alarmCreationRules,self.emails=response["data"].emails,self.phoneNumbers=response["data"].phoneNumbers})).catch((function(error){}))},changeStatus:function(rule){var url;url=rule.status?"turnonrule":"turnoffrule",this.$http.post("/setup/"+url,{workflowId:rule.id}).then((function(response){})).catch((function(error){}))},selectSubSection:function(section){this.subSection=section.name,"Workorder"===this.subSection?this.loadWorkFlowRules("workorder"):"Alarm"===this.subSection&&
// this.loadWorkFlowRules('alarm')
this.$router.push({path:"/app/setup/general/alarmnotifications"})},addEmailTemplate:function(){var self=this;self.$http.post("/setup/addalarmemail",{email:this.email}).then((function(response){self.emails.push(self.email),self.$refs.emailPopover.close()})).catch((function(error){}))},addSMSTemplate:function(){var self=this;self.$http.post("/setup/addalarmsms",{phone:this.phone}).then((function(response){self.phoneNumbers.push(self.phone),self.$refs.phPopover.close()})).catch((function(error){}))}}},setup_FireAlarmNotificationsvue_type_script_lang_js=FireAlarmNotificationsvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(setup_FireAlarmNotificationsvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,FireAlarmNotifications=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/28322.js.map