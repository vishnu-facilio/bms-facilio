"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[28184],{
/***/228184:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return MyWorkorderSummary}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/dashboard/widget/static/MyWorkorderSummary.vue?vue&type=template&id=b8b257ce
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",[_vm.countSummary.loading?_vm._e():_c("div",{staticClass:"row db-test-2 count-grid-container mobile-count-grid-container1"},[_c("div",{staticClass:"col-md-6 col-lg-6",staticStyle:{"text-align":"center","padding-left":"10px"}},[_c("div",{staticClass:"q-item-main q-item-section"},[_c("div",{on:{click:function($event){return _vm.redirect("/app/wo/orders/myduetoday")}}},[_c("div",{staticClass:"q-item-label",staticStyle:{"font-size":"40px","letter-spacing":"0.2px",color:"#e4ba2d","text-align":"center"}},[_vm._v(" "+_vm._s(_vm.countSummary.data.dueToday)+" ")]),_c("div",{staticClass:"count-header"},[_vm._v(_vm._s(_vm.$t("panel.card.due_today")))])])])]),_c("div",{staticClass:"col-md-6 col-lg-6 mobile-count-grid-container1-col1",staticStyle:{"text-align":"center","padding-right":"10px"}},[_c("div",{staticClass:"q-item-main q-item-section"},[_c("div",{on:{click:function($event){return _vm.redirect("/app/wo/orders/myoverdue")}}},[_c("div",{staticClass:"q-item-label",staticStyle:{"font-size":"40px","letter-spacing":"0.2px",color:"#e07575","text-align":"center"}},[_vm._v(" "+_vm._s(_vm.countSummary.data.overdue)+" ")]),_c("div",{staticClass:"count-header"},[_vm._v(_vm._s(_vm.$t("panel.card.overdue")))])])])])]),_c("div",{staticClass:"en-divider",staticStyle:{margin:"0 auto"}}),_c("div",{staticClass:"row db-test-2 count-grid-container mobile-count-grid-container2"},[_c("div",{staticClass:"col-md-6 col-lg-6",staticStyle:{"text-align":"center","padding-left":"10px"}},[_c("div",{staticClass:"q-item-main q-item-section"},[_c("div",{on:{click:function($event){return _vm.redirect("/app/wo/orders/myopen")}}},[_c("div",{staticClass:"q-item-label",staticStyle:{"font-size":"40px","letter-spacing":"0.2px",color:"#5dc6d5","text-align":"center"}},[_vm._v(" "+_vm._s(_vm.countSummary.data.open)+" ")]),_c("div",{staticClass:"count-header"},[_vm._v(_vm._s(_vm.$t("panel.card.open")))])])])]),_c("div",{staticClass:"col-md-6 col-lg-6",staticStyle:{"text-align":"center","padding-right":"10px"}},[_c("div",{staticClass:"q-item-main q-item-section"},[_c("div",{on:{click:function($event){return _vm.redirect("/app/wo/orders/myopen",_vm.filterObj)}}},[_c("div",{staticClass:"q-item-label",staticStyle:{"font-size":"40px","letter-spacing":"0.2px",color:"#e08c42","text-align":"center"}},[_vm._v(" "+_vm._s(_vm.countSummary.data.openHighPriority)+" ")]),_c("div",{staticClass:" count-header"},[_vm._v(_vm._s(_vm.$t("panel.tyre.high")))])])])])])])},staticRenderFns=[],objectSpread2=__webpack_require__(595082),vuex_esm=(__webpack_require__(434284),__webpack_require__(670560),__webpack_require__(420629)),router=__webpack_require__(329435),MyWorkorderSummaryvue_type_script_lang_js={data:function(){return{countSummary:{loading:!0,isdemodata:!1,data:{dueToday:0,overdue:0,openHighPriority:0,open:0}}}},mounted:function(){this.initdata()},created:function(){this.$store.dispatch("loadTicketStatus","workorder"),this.$store.dispatch("loadTicketPriority")},computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({today:function(){return this.$options.filters.formatDate(new Date,!0,!1)}},(0,vuex_esm/* mapState */.rn)({ticketstatus:function(state){return state.ticketStatus.workorder}})),(0,vuex_esm/* mapGetters */.Se)(["getTicketPriorityByLabel"])),{},{reportFilters:function(){return this.$route.query.filters?this.$route.query.filters:null},filterObj:function(){var filterObj={includeParentFilter:!0,search:{priority:{operatorId:36,value:[this.getTicketPriorityByLabel("High").id+""]}}};return filterObj}}),watch:{reportFilters:{handler:function(newData,oldData){this.initdata()},immediate:!0}},methods:{redirect:function(url){var query=arguments.length>1&&void 0!==arguments[1]?arguments[1]:{};if(url&&!this.$mobile)if((0,router/* isWebTabsEnabled */.tj)()){var _ref=(0,router/* findRouteForModule */.Jp)("workorder",router/* pageTypes */.As.LIST)||{},name=_ref.name;if(name){var viewname=url.split("orders/")[1];this.$router.push({name:name,params:{viewname:viewname},query:query})}}else this.$router.push({path:url,query:query})},initdata:function(){var self=this;self.countSummary.loading=!0;var params={staticKey:"mywosummary"};self.$http.post("dashboard/getCardData",params).then((function(response){if(response.data&&response.data.cardResult&&response.data.cardResult.result){var result=response.data.cardResult.result;self.countSummary.data["dueToday"]=result.dueToday,self.countSummary.data["overdue"]=result.overdue,self.countSummary.data["openHighPriority"]=result.openHighPriority,self.countSummary.data["open"]=result.open}self.countSummary.loading=!1})).catch((function(error){self.countSummary.loading=!1}))}}},static_MyWorkorderSummaryvue_type_script_lang_js=MyWorkorderSummaryvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(static_MyWorkorderSummaryvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,MyWorkorderSummary=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/28184.js.map