"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[37942],{
/***/537942:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return PortalActivities}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/PortalTenant/components/PortalActivities.vue?vue&type=template&id=ba89843e
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"block p30 portal-activity-scroll"},[_c("el-timeline",{staticClass:"wo-activity"},[_c("div",[_vm.loading?_c("div",{staticClass:" width100 height100 flex-middle text-center"},[_c("div",{staticClass:"flex-middle width100"},[_c("spinner",{attrs:{show:_vm.loading,size:"80"}})],1)]):_vm.$validation.isEmpty(_vm.activities)?_c("div",[_c("div",{staticClass:"flex-middle width100 height80vh justify-center shadow-none white-bg-block flex-direction-column"},[_c("InlineSvg",{attrs:{src:"svgs/emptystate/history",iconClass:"icon icon-xxxxlg mR10"}}),_c("div",{staticClass:"nowo-label text-center pT10"},[_vm._v(" "+_vm._s(_vm.$t("asset.history.no_history_available"))+" ")])],1)]):_vm._l(_vm.activities,(function(activity,index){return[null!==_vm.getMessage(activity).message?_c("el-timeline-item",{key:index,attrs:{type:"primary",color:activity.color,timestamp:_vm._f("formatDate")(activity.ttime)}},[24===activity.type&&null!==_vm.getMessage(activity).message?_c("div",[_c("avatar",{attrs:{size:"sm",user:{name:activity.doneBy.name}}}),_vm._v(" "+_vm._s(activity.doneBy.name)+" "),_c("span",{domProps:{innerHTML:_vm._s(_vm.getMessage(activity).message)}}),_c("i",[_c("span",{staticClass:"fc-id pointer",on:{click:function($event){return _vm.openPm(activity.info.addPMWO[0].pmid)}}},[_vm._v(" # "+_vm._s(activity.info.addPMWO[0].pmid))])])],1):24!==activity.type&&null!==_vm.getMessage(activity).message?_c("div",[_c("avatar",{attrs:{size:"sm",user:{name:activity.doneBy.name}}}),_vm._v(" "+_vm._s(activity.doneBy.name)+" "),_c("span",{domProps:{innerHTML:_vm._s(_vm.getMessage(activity).message)}})],1):_vm._e()]):_vm._e()]}))],2)])],1)},staticRenderFns=[],Avatar=(__webpack_require__(564043),__webpack_require__(857267),__webpack_require__(574752)),PortalActivitiesvue_type_script_lang_js={props:["details","serviceportal","module","moduleMetaObject"],data:function(){return{activities:[],loading:!0}},components:{Avatar:Avatar["default"]},mounted:function(){this.loadServicePortalActivities()},
// computed: {
//   ...mapState({
//     users: state => state.users,
//     sites: state => state.account.data.sites,
//     ticketcategory: state => state.account.data.ticketCategory,
//     ticketPriority: state => state.account.data.ticketPriority,
//     ticketStatus: state => state.account.data.ticketStatus,
//   }),
//   ...mapGetters(['getUser']),
// },
watch:{details:function(){this.loadServicePortalActivities()}},
// created() {
//   this.loadusers()
// },
methods:{loadServicePortalActivities:function(){var _this=this;this.loading=!0,this.modulename="workorders",this.$http.get("/v2/workorders/activity?workOrderId="+this.details.id).then((function(response){_this.activities=response.data.result.activity,_this.loading=!1}))},openPm:function(id){this.$router.replace({path:"/app/wo/planned/summary/"+id})},
// loadusers() {
//   this.$http.get('setup/allPortalUsers').then(response => {
//     this.userlist = response['data'].users
//   })
// },
// getUserName(id) {
//   if (id) {
//     let usr = []
//     usr = this.users.filter(us => us.ouid === id)
//     if (usr && usr.length > 0) {
//       let userName = usr[0].name
//       return userName
//     } else if (this.userlist && this.userlist.length > 0) {
//       let us = []
//       us = this.userlist.filter(u => u.ouid === id)
//       if (us && us.length > 0) {
//         let userName = us[0].name
//         return userName
//       } else {
//         return '---'
//       }
//     } else {
//       return '---'
//     }
//   } else {
//     return '---'
//   }
// },
loadActivities:function(){var self=this;self.loading=!0;var params=null;"asset"===self.module?(self.modulename="assets",params="?assetId="):"workorder"===self.module&&(self.modulename="workorders",params="?workOrderId="),self.$http.get("/v2/"+self.modulename+"/activity"+params+self.details.id).then((function(response){try{response.data.result&&response.data.result.activity&&(self.activities=response.data.result.activity);
// eslint-disable-next-line no-empty
}catch(_unused){}self.loading=!1}))},
// getAttachmentsString(obj) {
//   let str = ''
//   for (let item = 0; item < obj.length; item++) {
//     if (item !== 0) {
//       if (item === obj.length - 1) {
//         str += ' and '
//       } else {
//         str += ', '
//       }
//     }
//     str += obj[item]
//   }
//   return str
// },
// getUserCall(names) {
//   if (this.serviceportal) {
//     if (this.$account.user.id === names) {
//       return this.$account.user.name
//     } else {
//       return this.getUser(names).name
//     }
//   } else {
//     return this.getUser(names).name
//   }
// },
// getAssignedBy(updatedFields) {
//   let user = updatedFields.find(el => el.fieldName === 'assignedTo')
//   if (user.newValue) {
//     return this.getUser(user.newValue.id).name
//   } else if (user.oldValue) {
//     return this.getUser(user.oldValue.id).name
//   }
// },
// getLocation(value) {
//   return this.$helpers.parseLocation(value)
// },
// compare(str1, str2) {
//   return !str1.localeCompare(str2)
// },
// openPM(val) {
//   this.$router.replace({ path: '/app/wo/planned/summary/' + val })
// },
// getTicket(info) {
//   if (info.field === 'type') {
//     return null
//   } else if (info.field === 'category') {
//     return (
//       '<span class="fw5">' +
//       'updated ' +
//       info.displayName +
//       '</span>' +
//       ' as ' +
//       '<i>' +
//       this.ticketcategory.filter(us => us.id === info.newValue)[0]
//         .displayName +
//       '</i>'
//     )
//   } else if (info.field === 'priority') {
//     return (
//       '<span class="fw5">' +
//       'updated ' +
//       info.displayName +
//       '</span>' +
//       ' as ' +
//       '<i>' +
//       this.ticketPriority.filter(us => us.id === info.newValue)[0]
//         .displayName +
//       '</i>'
//     )
//   } else if (info.field === 'dueDate') {
//     return (
//       '<span class="fw5">' +
//       'updated ' +
//       info.displayName +
//       '</span>' +
//       ' as ' +
//       '<i>' +
//       this.$options.filters.formatDate(info.newValue) +
//       '</i>'
//     )
//   } else if (info.field === 'site') {
//     return (
//       '<span class="fw5">' +
//       'updated ' +
//       info.displayName +
//       '</span>' +
//       ' as ' +
//       '<i>' +
//       this.getSiteName(info.newValue) +
//       '</i>'
//     )
//   } else {
//     return (
//       '<span class="fw5">' +
//       'updated ' +
//       info.displayName +
//       '</span>' +
//       ' as ' +
//       '<i>' +
//       info.newValue +
//       '</i>'
//     )
//   }
// },
// getSiteName(siteId) {
//   let site = this.sites.find(site => site.id === siteId)
//   return site ? site.name : ''
// },
getMessage:function(activity){return activity&&4===activity.type&&activity.info&&activity.info.addWO?{message:'<span class="fw5"> raised the request</span>'}:activity&&5===activity.type&&activity.info.status&&activity.info.status?{message:'<span class="fw5">'+activity.info.status.toLowerCase()+"</span> the Work Order"}:activity&&5===activity.type&&activity.info.woupdate?{message:null}:activity&&26===activity.type?{message:'<span class="fw5">updated status </span>from <i>'+activity.info.oldValue+" to "+activity.info.newValue+"</i>"}:9===activity.type?{message:'<span class="fw5"> added the Comment </span><i>('+activity.info.Comment+")</i>"}:10===activity.type?{message:"<b>attached </b><i>"+activity.info.attachment[0].Filename+"</i>"}:{message:'<span class="fw5">'+activity.message+"</span>"}}}},components_PortalActivitiesvue_type_script_lang_js=PortalActivitiesvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_PortalActivitiesvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,PortalActivities=component.exports},
/***/574752:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return Avatar}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/Avatar.vue?vue&type=template&id=7f0d5689
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _vm.user||_vm.name?_c("div",{staticClass:"fc-avatar-inline"},[_vm.avatarUrl?_c("img",{staticClass:"fc-avatar",class:_vm.sizeClass,attrs:{src:_vm.avatarUrl,alt:"Profile Avatar"}}):_c("div",{staticClass:"fc-avatar",class:_vm.sizeClass,style:_vm.bgColor},[_vm._v(" "+_vm._s(_vm.trimmedName)+" ")])]):_vm._e()},staticRenderFns=[],objectSpread2=__webpack_require__(595082),vuex_esm=(__webpack_require__(425728),__webpack_require__(260228),__webpack_require__(434284),__webpack_require__(564043),__webpack_require__(709873),__webpack_require__(228436),__webpack_require__(450886),__webpack_require__(677049),__webpack_require__(821694),__webpack_require__(420629)),Avatarvue_type_script_lang_js={props:{size:{type:String},user:{type:Object},avatarStyle:{type:Object},color:{type:String},name:{type:Boolean}},data:function(){return{sizeClass:"fc-avatar-"+this.size,fullName:"",trimmedName:null,avatarUrl:null,bgColor:"background-color: "+(this.color?this.color:this.getRandomBgColor())}},computed:(0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapState */.rn)({users:function(state){return state.users}})),mounted:function(){var _this=this;if(this.user){var userName="";if(this.user.avatarUrl)this.avatarUrl=this.user.avatarUrl;else if(this.user.id){var userObj=this.users.find((function(user){return user.id===_this.user.id}));userObj&&userObj.avatarUrl&&(this.avatarUrl=userObj.avatarUrl),userObj&&userObj.name&&(userName=userObj.name)}userName=userName||this.user.name,userName&&(this.fullName=userName,this.trimmedName=this.getAvatarName(userName),this.bgColor="background-color: "+(this.color?this.color:this.getRandomBgColor()))}else this.name&&(this.trimmedName=this.getAvatarName(this.name),this.bgColor="background-color: "+(this.color?this.color:this.getRandomBgColor()))},watch:{user:function(){var _this2=this;if(this.user.avatarUrl)this.avatarUrl=this.user.avatarUrl;else if(this.user.id){var userObj=this.users.find((function(user){return user.id===_this2.user.id}));userObj&&userObj.avatarUrl?this.avatarUrl=userObj.avatarUrl:this.avatarUrl=null}else this.avatarUrl=null;this.user.name&&(this.fullName=this.user.name,this.trimmedName=this.getAvatarName(this.user.name),this.bgColor="background-color: "+(this.color?this.color:this.getRandomBgColor()))},"user.name":function(){this.user.name&&(this.fullName=this.user.name,this.trimmedName=this.getAvatarName(this.user.name),this.bgColor="background-color: "+(this.color?this.color:this.getRandomBgColor()))},"user.avatarUrl":function(){this.user.avatarUrl?this.avatarUrl=this.user.avatarUrl:this.avatarUrl=null},"user.id":function(){var _this3=this;if(this.user.id){var userObj=this.users.find((function(user){return user.id===_this3.user.id}));userObj&&userObj.avatarUrl?this.avatarUrl=userObj.avatarUrl:this.avatarUrl=null}else this.avatarUrl=null}},methods:{getAvatarName:function(name){for(var parts=name.split(/[ -]/),initials="",initialLen=2,count=0,i=0;i<parts.length;i++)if(""!==parts[i].trim()&&(initials+=parts[i].charAt(0),count++,count>=initialLen))break;initials.length<initialLen&&name.length>=initialLen&&(initials=name.trim().substring(0,initialLen));var avatarName=initials.toUpperCase();return avatarName},getRandomBgColor:function(){var colors=["#FFBA51","#34BFA3","#FF2F82","#29D9A7","#ECDC74","#927FED","#FF61A8","#fbf383","#ac4352","#6db1f4"],userKey=this.user.email?this.user.email:this.user.name,userUniqueNum=Array.from(userKey).map((function(letter){return letter.charCodeAt(0)})).reduce((function(current,previous){return previous+current})),colorIndex=userUniqueNum%colors.length,color=colors[colorIndex];return color}}},components_Avatarvue_type_script_lang_js=Avatarvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_Avatarvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,Avatar=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/37942.js.map