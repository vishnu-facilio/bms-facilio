(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[54310,83382],{
/***/683382:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return NewFCustomModuleType}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/NewFCustomModuleType.vue?vue&type=template&id=2a14eb80
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"container-scroll"},[_c("div",{staticClass:"row setting-Rlayout"},[_c("div",{staticClass:"col-lg-12 col-md-12"},[_c("table",{class:["setting-list-view-table","ticketPriority"===_vm.module?"mT70":"mT30"]},[_c("thead",[_c("tr",[_c("th",{staticClass:"setting-table-th setting-th-text width100px"}),_c("th",{staticClass:"setting-table-th setting-th-text pL0 width200px"},[_vm._v(" "+_vm._s(_vm.$t("common.header.priority"))+" ")]),_c("th",{staticClass:"setting-table-th setting-th-text width250px"},[_vm._v(" "+_vm._s(_vm.$t("common._common.description"))+" ")]),_c("th",{staticClass:"setting-table-th setting-th-text width200px"},[_vm._v(" "+_vm._s(_vm.$t("common.header.priority_color"))+" ")]),_c("th",{staticClass:"setting-table-th setting-th-text width150px"}),_c("th",{staticClass:"setting-table-th setting-th-text width100px"})])]),_vm.loading?_c("tbody",[_c("tr",[_c("td",{staticClass:"text-center",attrs:{colspan:"100%"}},[_c("spinner",{attrs:{show:_vm.loading,size:"80"}})],1)])]):_vm.$validation.isEmpty(_vm.moduleTypeList)?_c("tbody",[_c("tr",[_c("td",{staticClass:"text-center",attrs:{colspan:"100%"}},[_vm._v(" "+_vm._s(_vm.$t("common.header.no_priorities_yet"))+" ")])])]):_c("draggable",{attrs:{options:{draggable:_vm.moveIt?".asd":"",handle:".dragable",ghostClass:"drag-ghost",dragClass:"custom-drag",animation:150},element:"tbody"},on:{change:_vm.updateTypesOrder},model:{value:_vm.moduleTypeList,callback:function($$v){_vm.moduleTypeList=$$v},expression:"moduleTypeList"}},_vm._l(_vm.moduleTypeList,(function(moduleType,index){return _c("tr",{key:index,staticClass:"asd visibility-visible-actions",class:{tablerow:!_vm.moveIt,activedrag:_vm.moveIt}},[_c("td",{staticClass:"width100px"}),_c("td",{staticClass:"pL0 pR0 width200px"},[moduleType.isNew?_c("div",{staticClass:"display-flex-between-space"},[_c("el-input",{staticClass:"fc-border-select width250px",model:{value:moduleType[_vm.moduleFields.displayName],callback:function($$v){_vm.$set(moduleType,_vm.moduleFields.displayName,$$v)},expression:"moduleType[moduleFields.displayName]"}})],1):_c("div",[_c("span",[_vm._v(_vm._s(moduleType[_vm.moduleFields.displayName]))])])]),_c("td",{staticClass:"width250px"},[moduleType.isNew?_c("div",{staticClass:"display-flex-between-space"},[_c("el-input",{staticClass:"fc-border-select width250px",model:{value:moduleType[_vm.moduleFields.description],callback:function($$v){_vm.$set(moduleType,_vm.moduleFields.description,$$v)},expression:"moduleType[moduleFields.description]"}}),_c("i",{staticClass:"el-icon-check pointer fR fc-icon-grey priority-check-icon visibility-hide-actions",on:{click:function($event){return _vm.addEditType(moduleType)}}})],1):_c("div",[_c("span",[_vm._v(_vm._s(moduleType[_vm.moduleFields.description]))]),_c("i",{staticClass:"el-icon-edit pointer fR fc-icon-grey visibility-hide-actions",on:{click:function($event){return _vm.enableEdit(moduleType,index)}}})])]),_c("td",{staticClass:"width200px"},[moduleType.isNew?_c("div",{staticClass:"fc-color-picker"},[moduleType.isNew?_c("el-color-picker",{staticClass:"mR10",attrs:{"popper-class":"fc-color-pallete"},model:{value:moduleType[_vm.moduleFields.color],callback:function($$v){_vm.$set(moduleType,_vm.moduleFields.color,$$v)},expression:"moduleType[moduleFields.color]"}}):_vm._e(),_c("el-input",{staticClass:"width130px fc-border-select",attrs:{placeholder:"Pick the priority color"},model:{value:moduleType[_vm.moduleFields.color],callback:function($$v){_vm.$set(moduleType,_vm.moduleFields.color,$$v)},expression:"moduleType[moduleFields.color]"}})],1):_c("span",[_c("i",{staticClass:"fa fa-circle priorityMediumtag",style:{color:moduleType[_vm.moduleFields.color]},attrs:{"aria-hidden":"true"}}),_vm._v(" "+_vm._s(moduleType[_vm.moduleFields.color]?moduleType[_vm.moduleFields.color]:"")+" ")])]),_c("td",{staticClass:"width150px"},[_c("div",{staticClass:"text-left visibility-hide-actions",staticStyle:{"margin-top":"-3px","margin-right":"15px","text-align":"center"}},[_c("img",{staticClass:"mR10",staticStyle:{height:"18px",width:"18px"},attrs:{src:__webpack_require__(353654)},on:{click:function($event){return _vm.addType(moduleType,index)}}}),moduleType.isDefault?_vm._e():_c("img",{staticStyle:{height:"18px",width:"18px","margin-right":"3px"},attrs:{src:__webpack_require__(385769)},on:{click:function($event){return _vm.deleteType(moduleType,index)}}})])]),_c("td",{staticClass:"width100px"})])})),0)],1)])])])},staticRenderFns=[],defineProperty=__webpack_require__(482482),slicedToArray=__webpack_require__(554621),objectSpread2=__webpack_require__(595082),api=(__webpack_require__(260228),__webpack_require__(821694),__webpack_require__(76265),__webpack_require__(947522),__webpack_require__(336585),__webpack_require__(162506),__webpack_require__(865137),__webpack_require__(32284)),vuedraggable_umd=__webpack_require__(909980),vuedraggable_umd_default=__webpack_require__.n(vuedraggable_umd),vuex_esm=__webpack_require__(420629),cloneDeep=__webpack_require__(150361),cloneDeep_default=__webpack_require__.n(cloneDeep),NewFCustomModuleTypevue_type_script_lang_js={props:["module"],components:{draggable:vuedraggable_umd_default()},title:function(){return this.title},data:function(){return{loading:!1,moduleFields:{},moduleTypeList:null,moveIt:!0,priorities:this.ticketPriority,title:null}},created:function(){this.fetchData(),this.$store.dispatch("loadAlarmSeverity")},computed:(0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapState */.rn)({alarmSeverity:function(state){return state.alarmSeverity},ticketPriority:function(state){return state.ticketPriority}})),methods:{fetchData:function(){var _this=this,forceFetch=arguments.length>0&&void 0!==arguments[0]&&arguments[0];this.loading=!0,Promise.all([this.$store.dispatch("loadTicketPriority",forceFetch),this.$store.dispatch("loadAlarmSeverity",forceFetch)]).then((function(){_this.initData(),_this.loading=!1}))},initData:function(){var _this2=this,fields={ticketPriority:{displayName:"displayName",description:"description",typeName:"priority",color:"colour",sequenceNumber:"sequenceNumber",title:"priority"},alarmSeverity:{displayName:"displayName",typeName:"severity",color:"color",sequenceNumber:"cardinality",title:"severity"}};this.moduleFields.displayName="displayName",this.moduleTypeList=cloneDeep_default()(this[this.module]),this.title=fields[this.module].title,Object.entries(fields[this.module]).forEach((function(_ref){var _ref2=(0,slicedToArray/* default */.Z)(_ref,2),key=_ref2[0],value=_ref2[1];"title"!==key&&(_this2.moduleFields[key]=value)}))},updateTypesOrder:function(){var _this3=this,order=0;
// draggable action handled
this.moduleTypeList.forEach((function(element){element[_this3.moduleFields.sequenceNumber]=++order})),this.updateTypes()},updateTypes:function(){var url,_this4=this,params={},updateModule={ticketPriority:{url:"updateTicketPriorities",paramObj:"ticketPriorties"},alarmSeverity:{url:"updateAlarmSeverities",paramObj:"alarmSeverities"}};
// update multiple types
url="/v2/picklist/".concat(updateModule[this.module].url),params[updateModule[this.module].paramObj]=this.moduleTypeList,api/* API */.bl.post(url,params).then((function(_ref3){var error=_ref3.error;error?_this4.$message.error(error.message||"Error Occured"):(_this4.fetchData(!0),_this4.$dialog.notify("Successfully updated the "+_this4.title))}))},addEditType:function(moduleType){var url,_this5=this,params={},addOrUpdateType={ticketPriority:{paramObj:"ticketPriority",addUrl:"addTicketPriority",updateUrl:"updateTicketPriority"},alarmSeverity:{paramObj:"assetSeverity",addUrl:"addAlarmSeverity",updateUrl:"updateAlarmSeverity"}};params[addOrUpdateType[this.module].paramObj]=moduleType,url="/v2/picklist/".concat(moduleType.id?addOrUpdateType[this.module].updateUrl:addOrUpdateType[this.module].addUrl),api/* API */.bl.post(url,params).then((function(_ref4){var data=_ref4.data,error=_ref4.error;error?_this5.$message.error(error.message||"Something went wrong"):(moduleType.id=data.record.id,moduleType.isNew=!1,_this5.$store.commit("GENERIC_ADD_OR_UPDATE",{type:_this5.module,data:data.record}),_this5.$dialog.notify("Successfully updated the "+_this5.title),_this5.fetchData(!0))}))},addType:function(prior,index){var _data,data=(_data={displayName:null,description:null},(0,defineProperty/* default */.Z)(_data,this.moduleFields.sequenceNumber,prior[this.moduleFields.sequenceNumber]+1),(0,defineProperty/* default */.Z)(_data,"isNew",!0),(0,defineProperty/* default */.Z)(_data,this.moduleFields.color,"#E6E6EA"),_data);
// Detele type dummy object
this.moduleTypeList.splice(index+1,0,data)},enableEdit:function(moduleType,index){var moduleTypeList=this.moduleTypeList;moduleType.isNew=!0,moduleTypeList.splice(index,1,moduleType),this.$set(this,"moduleTypeList",moduleTypeList)},deleteType:function(moduleType,index){var _this6=this;
// Detele  type api call
if(moduleType.id){var url,deleteModule={ticketPriority:{url:"deleteTicketPriority",paramObj:"ticketPriority"},alarmSeverity:{url:"deleteAlarmSeverity",paramObj:"assetSeverity"}},params={};url="/v2/picklist/".concat(deleteModule[this.module].url),params[deleteModule[this.module].paramObj]={id:moduleType.id},this.$dialog.confirm({title:"Delete"+this.module,message:"Are you sure you want to delete this "+this.title+" ?",rbDanger:!0,rbLabel:"Delete"}).then((function(value){value&&api/* API */.bl.post(url,params).then((function(_ref5){var error=_ref5.error;error?_this6.$message.error(error.message||"Error Occured"):(_this6.$dialog.notify("Successfully deleted the"+_this6.title),_this6.moduleTypeList.splice(index,1),_this6.$store.commit("GENERIC_DELETE",{type:_this6.module,matches:index}))}))}))}else this.moduleTypeList.splice(index,1)},sortType:function(array,key){
// ordering according to sequence or cordinality
return array.sort((function(a,b){var x=a[key],y=b[key];return x<y?-1:x>y?1:0}))}}},components_NewFCustomModuleTypevue_type_script_lang_js=NewFCustomModuleTypevue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_NewFCustomModuleTypevue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,NewFCustomModuleType=component.exports},
/***/785869:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return Subheader}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/Subheader.vue?vue&type=template&id=fa337eb6
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"subheader-section",class:{filterclass:!0===_vm.querry}},[_c("div",{staticClass:"subheader-div"},[_c("ul",{staticClass:"subheader-tabs pull-left"},[_vm._t("prefix"),_vm._l(_vm.activeMenuList,(function(item,index){return[item.multilevel?_c("li",{key:index,staticClass:"sh-multilevel-header",class:{"router-link-exact-active active":_vm.getTabTitlenew(item)!==item.label||_vm.$route.params&&"chilleroverview"===_vm.$route.params.dashboardlink}},[_c("el-popover",{attrs:{placement:"bottom",width:"200","popper-class":"sh-popup-tree",trigger:"hover"},model:{value:_vm.closepop,callback:function($$v){_vm.closepop=$$v},expression:"closepop"}},[_vm._l(item.children,(function(d,index){return item.children?_c("div",{key:index,staticClass:"p5 parent-sh"},[_c("div",{staticClass:"sh-pop-parent p5 pointer",class:{active:d.path.path===_vm.$route.path},on:{click:function($event){return _vm.getparent(d)}}},[_vm._v(" "+_vm._s(d.label)+" ")]),d.children?_c("div",{staticClass:"p5 sh-con-header"},_vm._l(d.children,(function(c,idx){return d.children?_c("div",{key:idx,staticClass:"child-sh"},[_c("div",{staticClass:"sh-pop-child p5 pointer",class:{active:c.path.path===_vm.$route.path},on:{click:function($event){return _vm.getchild(c)}}},[_c("i",{staticClass:"fa fa-circle sh-indicater",class:{active:c.path.path===_vm.$route.path}}),_vm._v(_vm._s(c.label)+" ")])]):_vm._e()})),0):_vm._e()]):_vm._e()})),_c("a",{attrs:{slot:"reference"},slot:"reference"},[_vm._v(" "+_vm._s(_vm.getTabTitlenew(item))+" "),_c("i",{staticClass:"switch-icon-dropdown el-icon-arrow-down",staticStyle:{float:"right",margin:"5px"}})])],2),_c("div",{staticClass:"sh-selection-bar"})],1):item.disable?_c("li",{key:index,class:{"router-link-exact-active active":_vm.getTabTitle(item)!==item.label}},[_c("el-dropdown",{on:{command:_vm.openChild}},[_c("a",[_vm._v(" "+_vm._s(_vm.getTabTitle(item))+" "),_c("i",{staticClass:"switch-icon-dropdown el-icon-arrow-down",staticStyle:{float:"right",margin:"5px"}})]),_c("div",{staticClass:"sh-selection-bar"}),_c("el-dropdown-menu",{attrs:{slot:"dropdown"},slot:"dropdown"},_vm._l(item.childrens,(function(child,childindex){return _c("el-dropdown-item",{key:childindex,attrs:{command:{curentItem:child,parentItem:item},disabled:child.disable}},[_vm._v(_vm._s(child.label))])})),1)],1)],1):item.childrens||!_vm.showCurrentViewOnly||_vm.currentView===item.name?_c("router-link",{key:index,attrs:{tag:"li",to:item.childrens&&item.childrens.find((function(cl){return cl.path.path===_vm.$route.path}))?item.childrens.find((function(cl){return cl.path.path===_vm.$route.path})).path:item.path}},[_c("a",[_vm._v(_vm._s(_vm.getTabTitle(item)))]),item.childrens?_c("div",{staticStyle:{float:"right",margin:"3px"}},[_c("el-dropdown",{on:{command:_vm.openChild}},[_c("i",{staticClass:"switch-icon-dropdown el-icon-arrow-down"}),_c("el-dropdown-menu",{attrs:{slot:"dropdown"},slot:"dropdown"},_vm._l(item.childrens,(function(child,childindex){return _c("el-dropdown-item",{key:childindex,attrs:{command:{curentItem:child,parentItem:item},disabled:child.disable}},[_vm._v(_vm._s(child.label))])})),1)],1)],1):_vm._e(),item.disable?_vm._e():_c("div",{staticClass:"sh-selection-bar"})]):_vm._e()]})),_vm.moreMenuList.length&&!_vm.showCurrentViewOnly?_c("li",{staticClass:"pointer",class:{active:_vm.isMoreMenuActive}},[_c("div",[_c("svg",{staticStyle:{"enable-background":"new 0 0 408 408"},attrs:{xmlns:"http://www.w3.org/2000/svg","xmlns:xlink":"http://www.w3.org/1999/xlink",version:"1.1",id:"Capa_1",x:"0px",y:"0px",width:"18px",height:"18px",viewBox:"0 0 408 408","xml:space":"preserve"}},[_c("g",[_c("g",{attrs:{id:"more-horiz"}},[_c("path",{attrs:{d:"M51,153c-28.05,0-51,22.95-51,51s22.95,51,51,51s51-22.95,51-51S79.05,153,51,153z M357,153c-28.05,0-51,22.95-51,51    s22.95,51,51,51s51-22.95,51-51S385.05,153,357,153z M204,153c-28.05,0-51,22.95-51,51s22.95,51,51,51s51-22.95,51-51    S232.05,153,204,153z",fill:"#b1bdc9"}})])])])]),_c("q-popover",{ref:"shpopover",on:{open:_vm.moreMenuOpened,close:_vm.moreMenuClosed}},[_c("q-list",{staticClass:"scroll q-list-item",staticStyle:{"min-width":"150px"},attrs:{link:""}},[_vm._l(_vm.moreMenuList,(function(item,index){return _c("q-item",{key:index,on:{click:function($event){return _vm.openSubTab(item,index)}}},[_c("q-item-main",{staticStyle:{"font-size":"13px"},attrs:{label:item.label}})],1)})),_vm.showRearrange?_c("q-item",{staticClass:"rearrange-button",attrs:{menu:_vm.menu},on:{click:_vm.rearrange}},[_c("q-item-main",{staticStyle:{"font-size":"13px"},attrs:{label:_vm.$t("setup.users_management.rearrange")}})],1):_vm._e()],2)],1)],1):_vm._e()],2),_vm._t("suffix"),_c("div",{class:_vm.positionsheaders},[_vm._t("default")],2)],2)])},staticRenderFns=[],toConsumableArray=__webpack_require__(488478),quasar_esm=(__webpack_require__(670560),__webpack_require__(425728),__webpack_require__(260228),__webpack_require__(538077),__webpack_require__(162506),__webpack_require__(564043),__webpack_require__(857267),__webpack_require__(634338),__webpack_require__(902918),__webpack_require__(450886),__webpack_require__(434284),__webpack_require__(641358)),Subheadervue_type_script_lang_js={computed:{currentView:function(){return this.$route.params.viewname},querry:function(){return""===this.$route.query.filters||!!this.$route.query.filters}},props:["menu","filtersRetain","newbtn","type","parent","positionsheader","showRearrange","listCount","showCurrentViewOnly","maxVisibleMenu"],components:{QPopover:quasar_esm/* QPopover */.oe,QList:quasar_esm/* QList */.tu,QItem:quasar_esm/* QItem */.ry,QItemMain:quasar_esm/* QItemMain */.Ui},data:function(){return{positionsheaders:"pull-right",closepop:!1,activeMenuList:[],moreMenuList:[],seletedvalue:null,isMoreMenuActive:!1}},watch:{$route:function(to,from){if(this.filtersRetain&&from.path!==to.path)for(var i=0;i<this.filtersRetain.length;i++){var key1=from.query.hasOwnProperty(this.filtersRetain[i]);key1&&this.$router.push({query:from.query})}this.setFirstMenu(),this.$emit("tabChange")},menu:function(newVal){this.multilevelheader()}},mounted:function(){this.multilevelheader()},methods:{getTabTitle:function(item){var _this=this;if(item.path.path!==this.$route.path&&item.childrens&&item.childrens.length){var child=item.childrens.find((function(it){return it.path.path===_this.$route.path}));if(child)return child.label}return item.label},getTabTitlenew:function(item){var _this2=this;if(item.path.path!==this.$route.path&&item.children&&item.children.length){var child=item.children.find((function(it){return it.path.path===_this2.$route.path})),ch=item.children.filter((function(it){return it.children}));if(child)return child.label;for(var i=0;i<ch.length;i++){var c=ch[i].children&&ch[i].children.find((function(rt){return rt.path.path===_this2.$route.path}));if(c)return c.label}}return item.label},getparent:function(data){data.path&&data.path.path&&(this.$router.push({path:data.path.path}),this.closepop=!1)},getchild:function(data){data.path&&data.path.path&&(this.$router.push({path:data.path.path}),this.closepop=!1)},selectheader:function(value,event){this.seletedvalue=value,this.$router.push(value[value.length-1])},multilevelheader:function(){this.seletedvalue=["/app/em/newdashboard/chilleroverview/391"],this.loadMenuList()},multiheaderClickEvent:function(){},callbackMethod:function(newVal){},handleFcAfterDateBack:function(data,event){},onClickChild:function(value){},openChild:function(obj){this.$router.push(obj.curentItem.path)},companyListSelected:function(label,id){},loadMenuList:function(){var self=this;self.positionsheaders=self.positionsheader||"pull-right",self.activeMenuList=[],self.moreMenuList=[];var accessibleMenu=this.menu.filter((function(m){return"undefined"===typeof m.permission||self.$hasPermission(m.permission)?m:void 0}));for(var idx in accessibleMenu)parseInt(idx)<(this.maxVisibleMenu||4)?this.activeMenuList.push(accessibleMenu[idx]):this.moreMenuList.push(accessibleMenu[idx]);this.setFirstMenu()},openSubTab:function(item,index,keepQuery){if(item&&item.disable&&item.childrens.length){var data=[];if(data=item.childrens.filter((function(rt){return rt.label!==item.label})),data.length){this.moreMenuList.splice(index,1);var lastMenu=this.activeMenuList.pop();this.moreMenuList.splice(0,0,lastMenu),this.activeMenuList.push(item),this.$router.push({path:data[0].path.path}),this.isMoreMenuActive&&this.$refs.shpopover.close()}}else{this.moreMenuList.splice(index,1);var _lastMenu=this.activeMenuList.pop();this.moreMenuList.splice(0,0,_lastMenu),this.activeMenuList.push(item),keepQuery?this.$router.replace({path:item.path.path,query:this.$route.query}):this.$router.push(item.path),this.isMoreMenuActive&&this.$refs.shpopover.close()}},setFirstMenu:function(){var _this3=this;if(this.parent&&(this.$route.path===this.parent||this.$route.path+"/"===this.parent)&&this.activeMenuList.length){var primaryView=this.activeMenuList.find((function(menu){return menu.primary}));primaryView||(primaryView=this.activeMenuList[0]),this.$router.replace({path:primaryView.path.path,query:this.$route.query,params:primaryView.path.params})}else this.checkAndSetMoreList(this.currentView);if(![].concat((0,toConsumableArray/* default */.Z)(this.activeMenuList),(0,toConsumableArray/* default */.Z)(this.moreMenuList)).find((function(menu){return menu.path.path===_this3.$route.path}))){var portfolio=[].concat((0,toConsumableArray/* default */.Z)(this.activeMenuList),(0,toConsumableArray/* default */.Z)(this.moreMenuList)).find((function(menu){return menu.path.path.endsWith("portfolio")}));if(portfolio&&null!==portfolio&&void 0!==portfolio&&portfolio.childrens){var currentBuilding=portfolio.childrens.find((function(menu){return menu.path.path===_this3.$route.path}));currentBuilding&&(currentBuilding.childrens=portfolio.childrens,this.activeMenuList.find((function(menu){return menu.path.path.endsWith("portfolio")}))?this.activeMenuList=this.activeMenuList.map((function(ele){return ele.path.path===portfolio.path.path?currentBuilding:ele})):this.moreMenuList=this.moreMenuList.map((function(ele){return ele.path.path===portfolio.path.path?currentBuilding:ele})))}else this.menu.length&&"/app/em/newdashboard"===this.$route.path&&(this.menu[0].disable&&this.menu[0].childrens&&this.menu[0].childrens.length>1&&this.menu[0].childrens[0].disable?this.$router.push(this.menu[0].childrens[1].path.path):this.$router.push(this.menu[0].path.path))}},moreMenuOpened:function(){this.isMoreMenuActive=!0},moreMenuClosed:function(){this.isMoreMenuActive=!1},checkAndSetMoreList:function(newView){if(newView){var index,menu=this.moreMenuList.find((function(menu,idx){if(menu.name===newView)return index=idx,!0}));menu&&this.openSubTab(menu,index,!0)}},rearrange:function(){this.$refs.shpopover.close(),this.$emit("rearrange")}}},components_Subheadervue_type_script_lang_js=Subheadervue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_Subheadervue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,Subheader=component.exports},
/***/954310:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return NewWorkOrderPriorityList}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/setup/NewWorkOrderPriorityList.vue?vue&type=template&id=28f060bb
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"height100 user-layout"},[_vm._m(0),_c("subheader",{attrs:{menu:_vm.subheaderMenu,parent:"app/setup/workordersettings/",maxVisibleMenu:"5"}}),_c("new-f-custom-module-type",{attrs:{module:"ticketPriority"}})],1)},staticRenderFns=[function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"setting-header2"},[_c("div",{staticClass:"setting-title-block"},[_c("div",{staticClass:"setting-form-title"},[_vm._v("Work Order Priority")]),_c("div",{staticClass:"heading-description"},[_vm._v("List of all Priority")])])])}],NewFCustomModuleType=__webpack_require__(683382),Subheader=__webpack_require__(785869),SetupWorkorderMixin=__webpack_require__(595915),NewWorkOrderPriorityListvue_type_script_lang_js={components:{NewFCustomModuleType:NewFCustomModuleType["default"],Subheader:Subheader/* default */.Z},title:function(){},mixins:[SetupWorkorderMixin/* default */.Z],data:function(){
// let subheaderMenu = Constants.WORKORDER_SUB_HEADER_MENU
return{}},methods:{}},setup_NewWorkOrderPriorityListvue_type_script_lang_js=NewWorkOrderPriorityListvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(setup_NewWorkOrderPriorityListvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,NewWorkOrderPriorityList=component.exports},
/***/353654:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"d7f1f315aba97cd14240ee6f3d34465f.svg";
/***/},
/***/385769:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"4a9843791f38c4a28fdf9f0a4584780d.svg";
/***/}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/54310.js.map