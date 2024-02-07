"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[10622],{
/***/410622:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return Categories}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/dashboard/widget/static/Categories.vue?vue&type=template&id=e60f91ce
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",[_vm.categorySummary.loading?_vm._e():_c("div",{staticClass:"q-list categories-chart mobile-categories-chart"},[_c("div",{staticClass:"row db-test-2",staticStyle:{margin:"5px"}},[_c("div",{staticClass:"col-md-6 col-lg-6 db-test-2-l mobile-categories-fL",staticStyle:{"text-align":"center","padding-left":"10px"}},[_c("div",{staticClass:"q-item-main q-item-section"},[_c("div",{staticClass:"q-item-label mobile-categories-label",staticStyle:{"font-size":"25px","text-align":"center"}},[_vm._v(" "+_vm._s(_vm.categorySummary.data.categories)+" ")]),_c("div",{staticClass:"secondary-color mobile-categories-txt f13"},[_vm._v(" "+_vm._s(_vm.$t("panel.card.categories"))+" ")])])]),_c("div",{staticClass:"col-md-6 col-lg-6 db-test-2-r mobile-categories-fR",staticStyle:{"text-align":"center","padding-right":"10px"}},[_c("div",{staticClass:"q-item-main q-item-section"},[_c("div",{staticClass:"q-item-label mobile-categories-label",staticStyle:{"font-size":"25px"}},[_vm._v(" "+_vm._s(_vm.categorySummary.data.workorders)+" ")]),_c("div",{staticClass:"secondary-color mobile-categories-txt f13"},[_vm._v(" "+_vm._s(_vm.$t("panel.card.work_orders"))+" ")])])])]),_c("div",{staticClass:"db-prog-container mobile-db-prog-container"},_vm._l(_vm.categorySummary.data.stats,(function(category,index){return _c("div",{directives:[{name:"show",rawName:"v-show",value:index<8,expression:"index < 8"}],key:index,staticClass:"row db-prog-row"},[_c("div",{staticClass:"col-4 db-prog-l mobile-db-prog-l"},[_vm._v(" "+_vm._s(category.label)+" ")]),_c("div",{staticClass:"col-6 db-prog-c"},[_c("div",{staticClass:"q-progress db-progress-1 mobile-q-progress",staticStyle:{"margin-left":"5px"}},[_c("div",{staticClass:"q-progress-track db-progress-track",staticStyle:{width:"100%"}},[_vm._v("   ")]),_c("div",{staticClass:"q-progress-model db-progress-1",style:{width:_vm.percent(_vm.categorySummary.data.workorders,category.value)+"%"}},[_vm._v("   ")])])]),_c("div",{staticClass:"col-2 db-prog-r clickable mobile-db-prog-r",on:{click:function($event){return _vm.filterByCategory(category)}}},[_vm._v(" "+_vm._s(category.value)+" ")])])})),0)])])},staticRenderFns=[],objectSpread2=__webpack_require__(595082),vuex_esm=(__webpack_require__(538077),__webpack_require__(260228),__webpack_require__(947522),__webpack_require__(169358),__webpack_require__(425728),__webpack_require__(670560),__webpack_require__(865137),__webpack_require__(648324),__webpack_require__(434284),__webpack_require__(730381),__webpack_require__(420629)),router=__webpack_require__(329435),log_map_state=__webpack_require__(766331),Categoriesvue_type_script_lang_js={data:function(){return{categorySummary:{loading:!0,isdemodata:!1,data:{workorders:0,categories:0,stats:[]}}}},mounted:function(){this.initdata()},created:function(){this.$store.dispatch("loadTicketCategory"),this.$store.dispatch("loadTicketStatus","workorder")},computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({today:function(){return this.$options.filters.formatDate(new Date,!0,!1)}},(0,vuex_esm/* mapGetters */.Se)(["getTicketCategoryPickList"])),(0,vuex_esm/* mapState */.rn)({users:function(state){return state.users},ticketcategory:function(state){return state.ticketCategory},ticketstatus:function(state){return state.ticketStatus.workorder}})),(0,log_map_state/* mapStateWithLogging */.g)({spaces:function(state){return state.spaces}})),methods:{initdata:function(){var self=this;self.categorySummary.loading=!0,self.$http.get("/report/workorder/summary?type=category").then((function(response){var reportData=response.data.reportData;if(reportData&&reportData.length){var totalWorkOrders=0,totalCategories=0;reportData=reportData.filter((function(row){return row.label?(row.id=row.label,row.label=self.getTicketCategoryPickList()[row.label]):row.label="Unknown",totalWorkOrders+=row.value,!0})),totalWorkOrders<=0?(self.categorySummary.isdemodata=!0,self.categorySummary.data=self.demoCategorySummary.data):(Object.keys(self.getTicketCategoryPickList()).forEach((function(key){var cate=self.getTicketCategoryPickList()[key],thisCategory=reportData.find((function(row){return row.label===cate}));thisCategory||(thisCategory={},thisCategory.id=key,thisCategory.label=cate,thisCategory.value=0,reportData.push(thisCategory)),totalCategories++})),reportData.sort((function(a,b){return a.value-b.value})),self.categorySummary.data.workorders=totalWorkOrders,self.categorySummary.data.categories=totalCategories,self.categorySummary.data.stats=reportData.reverse(),self.categorySummary.isdemodata=!1)}else self.categorySummary.isdemodata=!0,self.categorySummary.data=self.demoCategorySummary.data;self.categorySummary.loading=!1})).catch((function(error){self.categorySummary.loading=!1}))},percent:function(total,value){var percentage=value/total*100;return isNaN(percentage)?0:percentage},filterByCategory:function(category){if(this.categorySummary.isdemodata)alert("This is a demo data, so you can't drill down the results.");else{var filterJson={category:{module:"workorder",operator:"is",value:[category.id+"_"+category.label]}};category.id||(filterJson.category.operator="is empty",filterJson.category.value=[]);var filterPath={path:"/app/wo/orders/open",query:{search:JSON.stringify(filterJson)}};if(!this.$mobile)if((0,router/* isWebTabsEnabled */.tj)()){var _ref=(0,router/* findRouteForModule */.Jp)("workorder",router/* pageTypes */.As.LIST)||{},name=_ref.name;name&&this.$router.push({name:name,params:{viewname:"open"},query:{search:JSON.stringify(filterJson)}})}else this.$router.push(filterPath)}}}},static_Categoriesvue_type_script_lang_js=Categoriesvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(static_Categoriesvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,Categories=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/10622.js.map