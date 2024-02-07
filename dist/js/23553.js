(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[23553],{
/***/723553:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return ApprovalRulesList}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/setup/approvals/ApprovalRulesList.vue?vue&type=template&id=d400c932&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"height100 overflow-hidden"},[_c("div",{staticClass:"setting-header2"},[_c("div",{staticClass:"setting-title-block"},[_c("div",{staticClass:"setting-form-title text-capitalize"},[_vm._v(" "+_vm._s(_vm.$t("common.products.approval_process"))+" ")]),_c("div",{staticClass:"heading-description"},[_vm._v(" "+_vm._s(_vm.$t("common._common.list_of_approval_processes_for",{moduleName:_vm.moduleDisplayName||_vm.moduleName}))+" ")])]),_c("div",{staticClass:"action-btn setting-page-btn"},[_c("el-button",{staticClass:"setup-el-btn",attrs:{type:"primary"},on:{click:_vm.newApproval}},[_vm._v(" "+_vm._s(_vm.$t("common.products.add_approval_process"))+" ")])],1)]),_c("div",{staticClass:"pL30 pB15"},[_c("portal-target",{attrs:{name:"automation-modules"}})],1),_vm._t("subHeaderMenu"),_c("div",{staticClass:"container-scroll mT15",staticStyle:{height:"calc(100vh - 250px)"}},[_c("div",{staticClass:"setting-Rlayout setting-list-view-table"},[_c("el-row",{staticClass:"header-row"},[_c("el-col",{staticClass:"setting-table-th setting-th-text",attrs:{span:5}},[_vm._v(" "+_vm._s(_vm.$t("common.products.name"))+" ")]),_c("el-col",{staticClass:"setting-table-th setting-th-text",attrs:{span:11}},[_vm._v(" "+_vm._s(_vm.$t("maintenance._workorder.description"))+" ")]),_c("el-col",{staticClass:"setting-table-th setting-th-text text-center",attrs:{span:4}},[_vm._v(" "+_vm._s(_vm.$t("common._common.status"))+" ")]),_c("el-col",{staticClass:"setting-table-th setting-th-text",attrs:{span:4}},[_vm._v(" "+_vm._s(" ")+" ")])],1),_vm.loading?_c("el-row",[_c("el-col",{staticClass:"text-center",attrs:{span:24}},[_c("spinner",{attrs:{show:_vm.loading,size:"80"}})],1)],1):_vm.$validation.isEmpty(_vm.approvals)?_c("el-row",[_c("el-col",{staticClass:"body-row-cell text-center",attrs:{span:24}},[_vm._v(" "+_vm._s(_vm.$t("common.products.no_approval_process_found"))+" ")])],1):_c("draggable",_vm._b({attrs:{draggable:".is-draggable",handle:".task-handle"},on:{change:function($event){return _vm.reorder()}},model:{value:_vm.approvals,callback:function($$v){_vm.approvals=$$v},expression:"approvals"}},"draggable",_vm.dragOptions,!1),_vm._l(_vm.approvals,(function(policy){return _c("el-row",{key:policy.id,class:["is-draggable","body-row tablerow"]},[_c("el-tooltip",{attrs:{content:policy.name,"open-delay":1500,manual:policy.name.length<25,placement:"top"}},[_c("el-col",{staticClass:"body-row-cell d-flex",attrs:{span:5}},[_c("div",{staticClass:"vertical-middle task-handle inline pR10"},[_c("img",{attrs:{src:__webpack_require__(271791)}})]),_c("div",{staticClass:"pointer",on:{click:function($event){return _vm.edit(policy)}}},[_vm._v(" "+_vm._s(policy.name)+" ")])])],1),_c("el-tooltip",{attrs:{content:policy.description,placement:"top","open-delay":1200,manual:_vm.$validation.isEmpty(policy.description)||policy.description.length<30}},[_c("el-col",{staticClass:"body-row-cell",attrs:{span:11}},[_vm._v(" "+_vm._s(policy.description||"---")+" ")])],1),_c("el-col",{staticClass:"body-row-cell text-center",attrs:{span:4}},[_c("el-switch",{staticClass:"Notification-toggle",attrs:{"active-color":"rgba(57, 178, 194, 0.8)","inactive-color":"#e5e5e5"},on:{change:function($event){return _vm.changeStatus(policy)}},model:{value:policy.active,callback:function($$v){_vm.$set(policy,"active",$$v)},expression:"policy.active"}})],1),_c("el-col",{staticClass:"body-row-cell",attrs:{span:4}},[_c("div",{staticClass:"text-left actions mT0 text-center d-flex"},[_c("div",{staticClass:"mR15",on:{click:function($event){return _vm.edit(policy)}}},[_c("i",{staticClass:"el-icon-edit pointer"})]),_vm.canShowDelete?_c("div",{on:{click:function($event){return _vm.remove(policy)}}},[_c("inline-svg",{staticClass:"f-delete pointer",attrs:{src:"svgs/delete",iconClass:"icon icon-sm icon-remove"}})],1):_vm._e()])])],1)})),1)],1)])],2)},staticRenderFns=[],objectSpread2=__webpack_require__(595082),vuedraggable_umd=(__webpack_require__(434284),__webpack_require__(564043),__webpack_require__(857267),__webpack_require__(260228),__webpack_require__(670560),__webpack_require__(821057),__webpack_require__(450886),__webpack_require__(909980)),vuedraggable_umd_default=__webpack_require__.n(vuedraggable_umd),lodash_remove=__webpack_require__(82729),remove_default=__webpack_require__.n(lodash_remove),vuex_esm=__webpack_require__(420629),ApprovalRulesListvue_type_script_lang_js={props:["moduleName","moduleDisplayName"],components:{draggable:vuedraggable_umd_default()},title:function(){return"Approval Processes"},data:function(){return{module:null,loading:!1,approvals:[],dragOptions:{animation:150,easing:"cubic-bezier(1, 0, 0, 1)",group:"tasksection",sort:!0}}},created:function(){this.$store.dispatch("loadSites"),this.getData()},computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapState */.rn)({sites:function(state){return state.sites}})),{},{canShowDelete:function(){return!1},selectedModule:{get:function(){return this.$route.params.moduleName},set:function(moduleName){var name=this.$route.name;this.$router.replace({name:name,params:{moduleName:moduleName}})}}}),methods:{getData:function(){var _this=this;return this.loading=!0,this.$http.post("v2/modules/rules/list",{ruleType:21,moduleName:this.moduleName}).then((function(_ref){var data=_ref.data;0==data.responseCode&&(_this.approvals=data.result.workflowRuleList||[])})).catch((function(_ref2){var _ref2$message=_ref2.message,message=void 0===_ref2$message?"Error loading approvals":_ref2$message;_this.$message.error(message)})).finally((function(){return _this.loading=!1}))},newApproval:function(){this.$router.push({name:"approvals.new",params:{moduleName:this.moduleName}})},edit:function(approval){this.$router.push({name:"approvals.edit",params:{id:approval.id,moduleName:this.moduleName}})},remove:function(approval){var _this2=this;return this.$http.post("/v2/modules/rules/delete",{ids:[approval.id]}).then((function(_ref3){var data=_ref3.data;if(0!==data.responseCode)throw new Error;var approvals=_this2.approvals;remove_default()(approvals,["id",approval.id]),_this2.$set(_this2,"approvals",approvals),_this2.$message.success(_this2.$t("common.products.approval_deleted")),_this2.$forceUpdate()})).catch((function(){_this2.$message.error(_this2.$t("common._common.could_not_delete_approval"))}))},reorder:function(){var _this3=this;return this.$http.post("/v2/approval/reorder",{moduleName:this.moduleName,ids:this.approvals.map((function(policy){return policy.id}))}).then((function(response){0!==response.data.responseCode?_this3.$message.error(response.data.message||"Error Occurred"):_this3.$message.success(_this3.$t("common._common.reorder_successfull"))})).catch((function(response){var message=_this3.$getProperty(response,"data.message",null);_this3.$message.error(message||"Error Occurred")}))},changeStatus:function(policy){var _this4=this,id=policy.id,isActive=policy.active,url=isActive?"setup/turnonrule":"setup/turnoffrule";return this.$http.post(url,{workflowId:id}).then((function(_ref4){var data=_ref4.data;if("success"!==data.result)throw new Error;_this4.$message.success(isActive?_this4.$t("common.products.approval_marked_as_active"):_this4.$t("common.products.approval_marked_as_inactive"))})).catch((function(){_this4.$set(policy,"active",!isActive),_this4.$message.error(_this4.$t("common._common.could_not_change_status"))}))}}},approvals_ApprovalRulesListvue_type_script_lang_js=ApprovalRulesListvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(approvals_ApprovalRulesListvue_type_script_lang_js,render,staticRenderFns,!1,null,"d400c932",null)
/* harmony default export */,ApprovalRulesList=component.exports},
/***/271791:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"8f181d382ffbfa8849763c3ada75c3c6.svg";
/***/},
/***/15742:
/***/function(module,__unused_webpack_exports,__webpack_require__){var baseUnset=__webpack_require__(457406),isIndex=__webpack_require__(565776),arrayProto=Array.prototype,splice=arrayProto.splice;
/** Used for built-in method references. */
/**
 * The base implementation of `_.pullAt` without support for individual
 * indexes or capturing the removed elements.
 *
 * @private
 * @param {Array} array The array to modify.
 * @param {number[]} indexes The indexes of elements to remove.
 * @returns {Array} Returns `array`.
 */
function basePullAt(array,indexes){var length=array?indexes.length:0,lastIndex=length-1;while(length--){var index=indexes[length];if(length==lastIndex||index!==previous){var previous=index;isIndex(index)?splice.call(array,index,1):baseUnset(array,index)}}return array}module.exports=basePullAt},
/***/457406:
/***/function(module,__unused_webpack_exports,__webpack_require__){var castPath=__webpack_require__(671811),last=__webpack_require__(610928),parent=__webpack_require__(340292),toKey=__webpack_require__(240327);
/**
 * The base implementation of `_.unset`.
 *
 * @private
 * @param {Object} object The object to modify.
 * @param {Array|string} path The property path to unset.
 * @returns {boolean} Returns `true` if the property is deleted, else `false`.
 */function baseUnset(object,path){return path=castPath(path,object),object=parent(object,path),null==object||delete object[toKey(last(path))]}module.exports=baseUnset},
/***/340292:
/***/function(module,__unused_webpack_exports,__webpack_require__){var baseGet=__webpack_require__(297786),baseSlice=__webpack_require__(314259);
/**
 * Gets the parent value at `path` of `object`.
 *
 * @private
 * @param {Object} object The object to query.
 * @param {Array} path The path to get the parent value of.
 * @returns {*} Returns the parent value.
 */function parent(object,path){return path.length<2?object:baseGet(object,baseSlice(path,0,-1))}module.exports=parent},
/***/82729:
/***/function(module,__unused_webpack_exports,__webpack_require__){var baseIteratee=__webpack_require__(267206),basePullAt=__webpack_require__(15742);
/**
 * Removes all elements from `array` that `predicate` returns truthy for
 * and returns an array of the removed elements. The predicate is invoked
 * with three arguments: (value, index, array).
 *
 * **Note:** Unlike `_.filter`, this method mutates `array`. Use `_.pull`
 * to pull elements from an array by value.
 *
 * @static
 * @memberOf _
 * @since 2.0.0
 * @category Array
 * @param {Array} array The array to modify.
 * @param {Function} [predicate=_.identity] The function invoked per iteration.
 * @returns {Array} Returns the new array of removed elements.
 * @example
 *
 * var array = [1, 2, 3, 4];
 * var evens = _.remove(array, function(n) {
 *   return n % 2 == 0;
 * });
 *
 * console.log(array);
 * // => [1, 3]
 *
 * console.log(evens);
 * // => [2, 4]
 */function remove(array,predicate){var result=[];if(!array||!array.length)return result;var index=-1,indexes=[],length=array.length;predicate=baseIteratee(predicate,3);while(++index<length){var value=array[index];predicate(value,index,array)&&(result.push(value),indexes.push(index))}return basePullAt(array,indexes),result}module.exports=remove}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/23553.js.map