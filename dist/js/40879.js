"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[40879],{
/***/640261:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return InspectionPdfWidget}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/inspection/individual-inspection/InspectionPdfWidget.vue?vue&type=template&id=2237024c
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",[_vm.loading?_c("div",{staticClass:"flex-middle justify-content-center"},[_c("spinner",{attrs:{show:_vm.loading,size:80}})],1):_c("div",{ref:"qanda-section",staticClass:"qanda-response-widget"},[_c("div",{staticClass:"header"},[_c("div",{staticClass:"d-flex justify-between width100 pL5 pR5 pB20 align-center"},[_vm.$org.logoUrl?_c("div",{staticClass:"fc-quotation-logo"},[_c("img",{staticStyle:{width:"100px"},attrs:{src:_vm.$org.logoUrl}})]):_vm._e(),_c("div",{staticClass:"fw6 text-uppercase f16"},[_vm._v(" "+_vm._s(_vm.headerText)+" ")])]),_c("div",{staticClass:"d-flex flex-col width100 pL5 pR5 pB20"},[_c("div",{staticClass:"page-name"},[_vm._v(_vm._s(_vm.responseName))]),(_vm.templateRecord||{}).description?_c("div",{staticClass:"page-description "},[_vm._v(" "+_vm._s((_vm.templateRecord||{}).description)+" ")]):_vm._e()]),_c("div",{staticClass:"field-details"},[_c("div",{staticClass:"d-flex flex-col justify-center"},[_vm.$validation.isEmpty(_vm.siteName)?_vm._e():_c("div",{staticClass:"field-container"},[_c("span",{staticClass:"field-label"},[_vm._v(" "+_vm._s(_vm.$t("qanda.response.site")))]),_c("span",{staticClass:"field-value"},[_vm._v(_vm._s(_vm.siteName))])]),_vm.$validation.isEmpty(_vm.$getProperty(_vm.details,"resource.name"))?_vm._e():_c("div",{staticClass:"field-container"},[_c("span",{staticClass:"field-label"},[_vm._v(_vm._s(_vm.$t("qanda.response.space")))]),_c("span",{staticClass:"field-value"},[_vm._v(_vm._s(_vm.$getProperty(_vm.details,"resource.primaryValue",null)||_vm.$getProperty(_vm.details,"resource.name","")))])]),_vm.$validation.isEmpty(_vm.startedTime)?_vm._e():_c("div",{staticClass:"field-container"},[_c("span",{staticClass:"field-label"},[_vm._v(_vm._s(_vm.$t("qanda.response.started_at")))]),_c("span",{staticClass:"field-value"},[_vm._v(_vm._s(_vm.startedTime))])]),_c("div",{staticClass:"field-container"},[_c("span",{staticClass:"field-label"},[_vm._v(_vm._s(_vm.$t("qanda.response.completed_at")))]),_c("span",{staticClass:"field-value"},[_vm._v(_vm._s(_vm.completedTime))])])]),_c("div",{staticClass:"d-flex flex-col"},[_vm.$validation.isEmpty(_vm.details.id)?_vm._e():_c("div",{staticClass:"field-container pT5"},[_c("span",{staticClass:"field-label"},[_vm._v(_vm._s(_vm.$t("qanda.response.id")))]),_c("span",{staticClass:"fc-id"},[_vm._v("#"+_vm._s(_vm.details.id))])]),!_vm.$validation.isEmpty(_vm.$getProperty(_vm.details,"parent.qandAType"))&&_vm.isInspectionModule?_c("div",{staticClass:"field-container"},[_c("span",{staticClass:"field-label"},[_vm._v(_vm._s(_vm.$t("qanda.response.category")))]),_c("span",{staticClass:"field-value"},[_vm._v(_vm._s(_vm.$getProperty(_vm.details,"category.displayName")))])]):_vm._e(),_c("div",{staticClass:"field-container"},[_c("span",{staticClass:"field-label"},[_vm._v(_vm._s(_vm.isInspectionModule?_vm.$t("qanda.response.conducted_by"):_vm.$t("qanda.response.attended_by")))]),_c("span",{staticClass:"field-value"},[_vm._v(_vm._s(_vm.attenderName))])]),_vm.$validation.isEmpty(_vm.responseScore)?_vm._e():_c("div",{staticClass:"field-container pB0"},[_c("span",{staticClass:"field-label"},[_vm._v(_vm._s(_vm.$t("qanda.response.score")))]),_c("span",{staticClass:"score-text f14"},[_vm._v(_vm._s(_vm.responseScore))])])])])]),_c("table",{staticClass:"width100 border-tlbr-none"},[_c("tbody",[_c("tr",[_c("td",{attrs:{colspan:"100%"}},[_vm.$validation.isEmpty(_vm.pages)?_vm._e():_c("div",{staticClass:"qanda-section"},_vm._l(_vm.pages,(function(page,pageIndex){return _c("div",{key:page.id,class:["page-section",!_vm.isLastItem(pageIndex,page.pages)&&"border-bottom"]},[_c("div",{staticClass:"page-details-header"},[_c("div",[_c("div",{staticClass:"page-name"},[_vm._v(_vm._s(page.name))]),_c("div",{staticClass:"page-description"},[_vm._v(_vm._s(page.description))])]),_vm.$validation.isEmpty(_vm.getPageScore(page))?_vm._e():_c("div",{staticClass:"score-text"},[_vm._v(" "+_vm._s(_vm.getPageScore(page))+" ")])]),_vm.$validation.isEmpty(page.questions)?_c("div",{staticClass:"question-empty-state"},[_c("inline-svg",{attrs:{src:"svgs/inspection-empty",iconClass:"icon text-center icon-80"}}),_c("div",{staticClass:"questions-empty-text"},[_vm._v(" "+_vm._s(_vm.$t("qanda.response.no_questions"))+" ")])],1):[_c("table",{staticClass:"border-tlbr-none print-inner-table width100"},[_c("tbody",_vm._l(page.questions,(function(question,questionIndex){return _c("tr",{key:question.id,class:["fc-tr-border ",!_vm.canShowQuestion(question)&&"display-none"]},[_c("td",{attrs:{colspan:"100%"}},[_c("div",{class:["qandacontainer",!_vm.isLastItem(questionIndex,page.questions)&&"border-bottom"]},[_c("div",{staticClass:"fc__layout__align"},[_c("div",{staticClass:"question"},[_vm._v(" "+_vm._s(question.questionNo)+" "+_vm._s(question.question)+" ")]),_vm.$validation.isEmpty(_vm.getQuestionScore(question))?_vm._e():_c("div",{staticClass:"score-text"},[_vm._v(" "+_vm._s(_vm.getQuestionScore(question))+" ")])]),_vm.$validation.isEmpty(question.description)?_vm._e():_c("div",{staticClass:"question-description pT5"},[_vm._v(" "+_vm._s(question.description)+" ")]),_c("div",[question.answerRequired?_c("div",{staticClass:"answer-heading-text mL27"},[_vm._v(" "+_vm._s(_vm.$t("qanda.response.answer"))+" ")]):_vm._e(),_c("FieldDisplayValue",{attrs:{question:question}}),(question.answer||{}).comments?[_c("div",{staticClass:"bold f14 pT15 text-fc-grey remarks-field mL27"},[_vm._v(" "+_vm._s((question||{}).commentsLabel)+" ")]),_c("div",{staticClass:"answer mL27 text-justify",staticStyle:{"white-space":"pre-line"}},[_vm._v(" "+_vm._s((question.answer||{}).comments)+" ")])]:_vm._e(),_vm.$validation.isEmpty((question.answer||{}).attachmentList)?_vm._e():[_c("div",{staticClass:"bold f14 pT15 text-fc-grey remarks-field mL27"},[_vm._v(" "+_vm._s((question||{}).attachmentLabel)+" ")]),_c("FileFieldPreview",{staticClass:"mL27",attrs:{attachments:(question.answer||{}).attachmentList}})]],2)])])])})),0),_c("tbody")])]],2)})),0)])])])])])])},staticRenderFns=[],regeneratorRuntime=__webpack_require__(150124),objectSpread2=__webpack_require__(595082),asyncToGenerator=__webpack_require__(548534),validation=(__webpack_require__(634338),__webpack_require__(976801),__webpack_require__(843843),__webpack_require__(434284),__webpack_require__(260228),__webpack_require__(947522),__webpack_require__(450886),__webpack_require__(670560),__webpack_require__(821694),__webpack_require__(76265),__webpack_require__(990260)),FieldDisplayValuevue_type_template_id_1c4f4d48_scoped_true_render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _vm.question.answerRequired?_c("div",{staticClass:"mL27 scrollView"},[_vm.$validation.isEmpty(_vm.question.answer)?_c("div",{staticClass:"text-fc-grey mT5"},[_vm._v(" "+_vm._s(_vm.$t("qanda.response.no_answer"))+" ")]):_c("div",[_vm.isFileTypeField(_vm.question)?_c("FileFieldPreview",{attrs:{attachments:_vm.answer}}):_vm.checkFieldType("MULTIPLE_CHOICE_MANY")?_vm._l(_vm.answer,(function(choice,index){return _c("div",{key:choice.label+"-"+index,staticClass:"answer d-flex items-center"},[_c("div",{staticClass:"green-dot"}),_c("div",[_vm._v(_vm._s(choice.label))])])})):_vm.checkFieldType("SMILEY_RATING")?[_c("EmojiIconRenderer",{staticClass:"emoji-icon-layout mL_7",attrs:{rating:_vm.getEmojiAnswer(),isActive:!0}})]:_vm.checkFieldType("STAR_RATING")?_c("div",{staticClass:"flex mL_7 mT3"},[_vm._l(_vm.answer,(function(star){return _c("inline-svg",{key:star,staticClass:"vertical-middle fill-blue5",attrs:{src:"svgs/star-yellow",iconClass:"icon icon-xxxl"}})})),_vm._l(_vm.unselectedRatingStars,(function(unselectedStar){return _c("inline-svg",{key:unselectedStar,staticClass:"vertical-middle fill-blue5",attrs:{src:"svgs/star-line-yellow",iconClass:"icon icon-xxxl"}})}))],2):_vm.checkFieldType("MATRIX")?_c("MatrixQuestionDisplay",{attrs:{answer:_vm.answer,question:_vm.question}}):_vm.checkFieldType("MULTI_QUESTION")?_c("MultiQuestionDisplay",{attrs:{question:_vm.question}}):_c("div",{staticClass:"answer"},[_vm._v(" "+_vm._s(_vm.answer)+" ")])],2)]):_vm.question.answerRequired?_vm._e():_c("div",[_c("div",{staticClass:"inspection-heading-field",domProps:{innerHTML:_vm._s(_vm.sanitizeHtml(_vm.question.richText))}})])},FieldDisplayValuevue_type_template_id_1c4f4d48_scoped_true_staticRenderFns=[],sanitize=(__webpack_require__(538077),__webpack_require__(425728),__webpack_require__(678042)),dist=__webpack_require__(635120),FileFieldPreviewvue_type_template_id_316aba02_scoped_true_render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"flex flex-col"},[_vm._l(_vm.files,(function(attachment,index){return _c("div",{key:"remarks-"+index+"-"+attachment.id,staticClass:"flex flex-col width50"},[_vm.$getProperty(attachment,"contentType",[]).includes("image")?[_c("div",{staticClass:"image-container",on:{click:function($event){return $event.stopPropagation(),_vm.openFile(attachment)}}},[_c("img",{staticClass:"fc-avatar-square image-preview",class:{overlay:_vm.canShowOverlay(index)},attrs:{src:_vm.getImageUrl(attachment)}})])]:[_c("div",{staticClass:"position-relative cursor-pointer"},[_vm.canShowOverlay(index)?_c("div",{staticClass:"overlay-count f24"},[_vm._v(" +"+_vm._s(_vm.hiddenAttachmentCount)+" ")]):_vm._e(),_c("div",{class:["file-icon flex flex-row p10",_vm.canShowOverlay(index)&&"svg-overlay"],on:{click:function($event){return _vm.openFile(attachment)}}},[_c("InlineSvg",{staticClass:"d-flex ",attrs:{src:_vm.getFileIcon(attachment),iconClass:"icon icon-40"}}),_c("div",{staticClass:"f14 primary-font font-bold mL10"},[_vm._v(" "+_vm._s(attachment.fileName)+" ")])],1)])],attachment.remarks?_c("div",{staticClass:"remarks"},[_vm._v(" "+_vm._s(attachment.remarks)+" ")]):_vm._e()],2)})),_vm.previewVisiblity?_c("PreviewFile",{attrs:{visibility:_vm.previewVisiblity,previewFile:_vm.attachment,files:[_vm.attachment]},on:{"update:visibility":function($event){_vm.previewVisiblity=$event}}}):_vm._e()],2)},FileFieldPreviewvue_type_template_id_316aba02_scoped_true_staticRenderFns=[],toConsumableArray=__webpack_require__(488478),PreviewFile=(__webpack_require__(689730),__webpack_require__(339772),__webpack_require__(720096)),FileFieldPreviewvue_type_script_lang_js={props:["attachments"],components:{PreviewFile:PreviewFile/* default */.Z},data:function(){return{previewVisiblity:!1,attachment:null,maxCount:5}},computed:{files:function(){var answer=this.$getProperty(this,"attachments");if((0,validation/* isArray */.kJ)(answer)){var imageFiles=answer.filter((function(file){return file.contentType.includes("image")})),otherFiles=answer.filter((function(file){return!file.contentType.includes("image")}));return[].concat((0,toConsumableArray/* default */.Z)(imageFiles),(0,toConsumableArray/* default */.Z)(otherFiles))}return[answer]},previewAttachments:function(){var files=this.files,maxCount=this.maxCount;return files.slice(0,maxCount+1)},hiddenAttachmentCount:function(){var _this$files=this.files,_this$files2=void 0===_this$files?{}:_this$files,length=_this$files2.length,maxCount=this.maxCount;return Math.abs(length-maxCount)}},methods:{openFile:function(file){var _ref=file||{},contentType=_ref.contentType,fileName=_ref.fileName,url=_ref.url,previewUrl=_ref.previewUrl,downloadUrl=_ref.downloadUrl,currentUrl=url||previewUrl;this.previewVisiblity=!0,this.attachment={contentType:contentType,fileName:fileName,previewUrl:currentUrl,downloadUrl:downloadUrl}},getFileIcon:function(attachment){var contentType=attachment.contentType,FILE_TYPE_ICONS=this.$constants.FILE_TYPE_ICONS,selectedIndex=FILE_TYPE_ICONS.findIndex((function(icons){var fileTypes=icons.fileTypes;return fileTypes.some((function(type){return contentType.includes(type)}))}));return(0,validation/* isEmpty */.xb)(selectedIndex)?FILE_TYPE_ICONS[0].path:FILE_TYPE_ICONS[selectedIndex].path},getImageUrl:function(attachment){var currentUrl=attachment.url||attachment.previewUrl;return this.$prependBaseUrl(currentUrl)},canShowOverlay:function(index){var maxCount=this.maxCount,hiddenAttachmentCount=this.hiddenAttachmentCount;return index===maxCount&&hiddenAttachmentCount>1}}},common_FileFieldPreviewvue_type_script_lang_js=FileFieldPreviewvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(common_FileFieldPreviewvue_type_script_lang_js,FileFieldPreviewvue_type_template_id_316aba02_scoped_true_render,FileFieldPreviewvue_type_template_id_316aba02_scoped_true_staticRenderFns,!1,null,"316aba02",null)
/* harmony default export */,FileFieldPreview=component.exports,MatrixQuestionDisplayvue_type_template_id_0932d56c_scoped_true_render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"mT8"},_vm._l(_vm.matrixQuestion.rowAnswer,(function(row,rowIndex){return _c("div",{key:rowIndex,staticClass:"flex flex-nowrap "},_vm._l(row.columnAnswer,(function(column,colIndex){return _c("div",{key:colIndex,class:[0==rowIndex?"table-content-row":"table-header-row","flex flex-nowrap flex-col justify-center items-center matrix-cell",_vm.getCellClass(rowIndex,colIndex),""+rowIndex+colIndex]},[0===rowIndex||0===colIndex?[0!=rowIndex?_c("p",{staticClass:"m-0  matrix-row-display-label"},[_vm._v(" "+_vm._s(row.name)+" ")]):0!=colIndex?_c("p",{staticClass:"m-0  matrix-col-display-label"},[_vm._v(" "+_vm._s(column.name)+" ")]):_vm._e()]:[_vm.$validation.isEmpty(column.answer)?_c("div",{directives:[{name:"tippy",rawName:"v-tippy"}],staticClass:"text-fc-grey  mT5 matrix-row-display-label",attrs:{title:_vm.$t("qanda.response.no_answer")}},[_vm._v(" "+_vm._s(_vm.$t("qanda.response.no_answer"))+" ")]):_c("div",{directives:[{name:"tippy",rawName:"v-tippy"}],staticClass:"answer break-word ",attrs:{title:column.answer}},[_vm._v(" "+_vm._s(column.answer)+" ")])]],2)})),0)})),0)},MatrixQuestionDisplayvue_type_template_id_0932d56c_scoped_true_staticRenderFns=[],slicedToArray=__webpack_require__(554621),MatrixQuestionDisplayvue_type_script_lang_js={name:"MatrixQuestionDisplay",props:["answer","question"],computed:{matrixQuestion:function(){var _this=this,_ref=this||{},question=_ref.question,_ref2=question||[],rows=_ref2.rows,columns=_ref2.columns,answer=[];return rows.forEach((function(row){var rowObj={row:row.id,columnAnswer:[{}],name:row.name};columns.forEach((function(column){var prevColObj=_this.getPrevColObj({row:row.id,col:column.id}),_ref3=prevColObj||{},answer=_ref3.answer;rowObj.columnAnswer.push({name:column.name,column:column.id,answer:answer,field:column.field})})),answer.push(rowObj)})),{rowAnswer:[{columnAnswer:[{}].concat((0,toConsumableArray/* default */.Z)(columns))}].concat(answer)}},matrixLength:function(){var _ref4=this||{},question=_ref4.question,_ref5=question||{},rows=_ref5.rows,_ref6=question||{},columns=_ref6.columns,_ref7=columns||{},columnLength=_ref7.length,_ref8=rows||{},rowLength=_ref8.length;return[rowLength+1,columnLength+1]}},methods:{getPrevColObj:function(props){var currColObj,_ref9=this||{},answer=_ref9.answer,_ref10=props||{},row=_ref10.row,col=_ref10.col,_ref11=answer||{},rowAnswer=_ref11.rowAnswer,currRowObj=(rowAnswer||[]).find((function(rowObj){return rowObj.row===row}));if(!(0,validation/* isEmpty */.xb)(currRowObj)){var _ref12=currRowObj||{},columnAnswer=_ref12.columnAnswer;currColObj=(columnAnswer||[]).find((function(colObj){return colObj.column===col}))}return currColObj},getCellClass:function(row,col){var _ref13=this||{},matrixLength=_ref13.matrixLength,_ref14=matrixLength||{},_ref15=(0,slicedToArray/* default */.Z)(_ref14,2),rowLength=_ref15[0],columnLength=_ref15[1];return rowLength-1===row&&columnLength-1===col?"last-row-cell last-column-cell":rowLength-1===row?"last-row-cell":columnLength-1===col?"last-column-cell":""}}},common_MatrixQuestionDisplayvue_type_script_lang_js=MatrixQuestionDisplayvue_type_script_lang_js,MatrixQuestionDisplay_component=(0,componentNormalizer/* default */.Z)(common_MatrixQuestionDisplayvue_type_script_lang_js,MatrixQuestionDisplayvue_type_template_id_0932d56c_scoped_true_render,MatrixQuestionDisplayvue_type_template_id_0932d56c_scoped_true_staticRenderFns,!1,null,"0932d56c",null)
/* harmony default export */,MatrixQuestionDisplay=MatrixQuestionDisplay_component.exports,MultiQuestionDisplayvue_type_template_id_20ef5db6_scoped_true_render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:" float-container pT20"},_vm._l(_vm.questionResponse,(function(column,index){return _c("div",{key:index,staticClass:"d-flex pB10",class:[!_vm.canShowAnswer(column)&&"display-none"]},[_c("div",{staticClass:"pT10 multi-col"},[_vm._v(" "+_vm._s(column.name)+" ")]),_c("div",{staticClass:"answer-column"},[_vm._v(" "+_vm._s(_vm.getColumnAnswer(column))+" ")])])})),0)},MultiQuestionDisplayvue_type_template_id_20ef5db6_scoped_true_staticRenderFns=[],filters=__webpack_require__(273861),dataType={5:"dateTime",6:"date",8:"enum"},MultiQuestionDisplayvue_type_script_lang_js={name:"MultiQuestionDisplay",props:["question"],created:function(){this.constructModel()},data:function(){return{questionResponse:[],dataType:dataType}},methods:{constructModel:function(){var question=this.question,_ref=question||{},answer=_ref.answer,_ref$columns=_ref.columns,columns=void 0===_ref$columns?[]:_ref$columns,_ref2=answer||{},actualAnswer=_ref2.answer,_ref3=actualAnswer||{},rowAnswer=_ref3.rowAnswer,_ref4=rowAnswer[0]||{},_ref4$columnAnswer=_ref4.columnAnswer,columnAnswer=void 0===_ref4$columnAnswer?[]:_ref4$columnAnswer,multiQuestionResponse=[];multiQuestionResponse=columns.map((function(column){var _ref5=column||{},name=_ref5.name,id=_ref5.id,answerObj=columnAnswer.find((function(answer){return answer.column===id})),_ref6=answerObj||{},_ref6$answer=_ref6.answer,answer=void 0===_ref6$answer?"":_ref6$answer;return{name:name,answer:answer,id:id}})),this.questionResponse=multiQuestionResponse||[]},canShowAnswer:function(column){var _ref7=column||{},columnId=_ref7.id,question=this.question,_ref8=question||{},columns=_ref8.columns,currAnsColumn=columns.find((function(col){return col.id===columnId})),_ref9=currAnsColumn||{},displayLogicMeta=_ref9.displayLogicMeta;if(!(0,validation/* isEmpty */.xb)(displayLogicMeta)){var _ref10=displayLogicMeta[0]||{},action=_ref10.action;return"hide"!==action}return!0},getColumnAnswer:function(column){var _ref11=column||{},answer=_ref11.answer;return"enum"===this.isChooserType(column)?this.getEnumAnswer(column):"date"===this.isChooserType(column)?this.getDateTime(column,!0):"dateTime"===this.isChooserType(column)?this.getDateTime(column,!1):answer},isChooserType:function(colAnswer){var _ref12=colAnswer||{},columnId=_ref12.id,question=this.question,_ref13=question||{},columnVsFieldMap=_ref13.columnVsFieldMap,currObj=columnVsFieldMap[columnId],_ref14=currObj||{},dataType=_ref14.dataType;return this.dataType[dataType]},getDateTime:function(colAnswer,onlyDate){return onlyDate?(0,filters.formatDate)(colAnswer):(0,filters.formatDate)(colAnswer,!0)},getEnumAnswer:function(colAnswer){var _ref15=colAnswer||{},columnId=_ref15.id,answerIndex=_ref15.answer,question=this.question,_ref16=question||{},columnVsFieldMap=_ref16.columnVsFieldMap,currObj=columnVsFieldMap[columnId]||{},_ref17=currObj||{},values=_ref17.values,actualValue=values.find((function(val){return val.index===answerIndex})),_ref18=actualValue||{},value=_ref18.value;return value},canDisableColumnValue:function(column){var _ref19=column||{},answer=_ref19.answer;return answer.length<=100},canDisableColumn:function(column){var _ref20=column||{},_ref20$name=_ref20.name,name=void 0===_ref20$name?null:_ref20$name;return name.length<=30}}},common_MultiQuestionDisplayvue_type_script_lang_js=MultiQuestionDisplayvue_type_script_lang_js,MultiQuestionDisplay_component=(0,componentNormalizer/* default */.Z)(common_MultiQuestionDisplayvue_type_script_lang_js,MultiQuestionDisplayvue_type_template_id_20ef5db6_scoped_true_render,MultiQuestionDisplayvue_type_template_id_20ef5db6_scoped_true_staticRenderFns,!1,null,"20ef5db6",null)
/* harmony default export */,MultiQuestionDisplay=MultiQuestionDisplay_component.exports,QUESTION_TYPE_ANSWER_RENDERER={MULTIPLE_CHOICE_MANY:function(answer,question){var _ref=question||{},options=_ref.options,_ref2=answer||{},selected=_ref2.selected,other=_ref2.other;return selected=options.filter((function(option){return selected.includes(option.id)})).map((function(option){return option.other?(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},option),{},{label:"".concat(option.label," - ").concat(other)}):option})),selected},MULTIPLE_CHOICE_ONE:function(answer,question){var _ref3=question||{},options=_ref3.options,_ref4=answer||{},selected=_ref4.selected,other=_ref4.other;return selected=options.find((function(option){return selected===option.id})),(0,validation/* isEmpty */.xb)(selected)||(0,validation/* isEmpty */.xb)(selected.other)||(selected.label="".concat(selected.label," - ").concat(other)),selected.label},BOOLEAN:function(answer,question){var _ref5=question||{},trueLabel=_ref5.trueLabel,falseLabel=_ref5.falseLabel;return answer?trueLabel:falseLabel},DATE_TIME:function(answer,question){return question.showTime?(0,filters.formatDate)(answer):(0,filters.formatDate)(answer,!0)}},FieldDisplayValuevue_type_script_lang_js={components:{FileFieldPreview:FileFieldPreview,EmojiIconRenderer:dist/* EmojiIconRenderer */.ZI,MatrixQuestionDisplay:MatrixQuestionDisplay,MultiQuestionDisplay:MultiQuestionDisplay},props:["question"],computed:{answer:function(){var question=this.question,questionType=question.questionType,answer=this.$getProperty(this,"question.answer.answer",null);return(0,validation/* isEmpty */.xb)(QUESTION_TYPE_ANSWER_RENDERER[questionType])?answer:QUESTION_TYPE_ANSWER_RENDERER[question.questionType](answer,question)},unselectedRatingStars:function(){var question=this.question,answer=this.answer,_ref6=question||{},questionType=_ref6.questionType;if("STAR_RATING"===questionType){var _ref7=question||{},ratingScale=_ref7.ratingScale;return Math.abs(ratingScale-answer)||0}return 0}},methods:{isFileTypeField:function(question){var _ref8=question||{},questionType=_ref8.questionType;return["MULTI_FILE_UPLOAD","FILE_UPLOAD"].includes(questionType)},checkFieldType:function(type){var question=this.question,_ref9=question||{},questionType=_ref9.questionType;return questionType===type},sanitizeHtml:function(richText){return(0,sanitize/* sanitize */.N)(richText)},getEmojiAnswer:function(){var _ref10=this||{},question=_ref10.question,answer=_ref10.answer,ratingScale=question.ratingScale,halfScal=ratingScale/2,startEmoji=Math.round(5-halfScal);return Math.abs(answer+startEmoji-1)}}},common_FieldDisplayValuevue_type_script_lang_js=FieldDisplayValuevue_type_script_lang_js,FieldDisplayValue_component=(0,componentNormalizer/* default */.Z)(common_FieldDisplayValuevue_type_script_lang_js,FieldDisplayValuevue_type_template_id_1c4f4d48_scoped_true_render,FieldDisplayValuevue_type_template_id_1c4f4d48_scoped_true_staticRenderFns,!1,null,"1c4f4d48",null)
/* harmony default export */,FieldDisplayValue=FieldDisplayValue_component.exports,helpers=__webpack_require__(778390),relatedFieldUtil=__webpack_require__(850558),moment=helpers/* default */.ZP.getOrgMoment,InspectionPdfWidgetvue_type_script_lang_js={props:["details","resizeWidget"],data:function(){return{loading:!1,pages:[]}},components:{FieldDisplayValue:FieldDisplayValue,FileFieldPreview:FileFieldPreview},created:function(){this.loadTemplate()},computed:{attenderName:function(){var _ref=this||{},details=_ref.details,assignedToName=this.$getProperty(details,"assignedTo.name");return(0,validation/* isEmpty */.xb)(assignedToName)?this.$getProperty(details,"people.name","---"):assignedToName||"---"},startedTime:function(){var details=this.details,_ref2=details||{},_ref2$actualWorkStart=_ref2.actualWorkStart,actualWorkStart=void 0===_ref2$actualWorkStart?null:_ref2$actualWorkStart;return(0,validation/* isEmpty */.xb)(actualWorkStart)?actualWorkStart:moment(actualWorkStart).format("DD MMM, YYYY HH:mm")},completedTime:function(){var details=this.details,actualWorkEnd=details.actualWorkEnd;return(0,validation/* isEmpty */.xb)(actualWorkEnd)?"N/A":moment(actualWorkEnd).format("DD MMM, YYYY HH:mm")},responseScore:function(){var details=this.details,_ref3=details||{},fullScore=_ref3.fullScore,totalScore=_ref3.totalScore,scorePercent=_ref3.scorePercent;return(0,validation/* isEmpty */.xb)(totalScore)||(0,validation/* isEmpty */.xb)(scorePercent)?null:"".concat(totalScore,"/").concat(fullScore,", ").concat(scorePercent,"%")},moduleName:function(){return this.$getProperty(this,"$attrs.moduleName")},templateModuleName:function(){var moduleName=this.moduleName;return moduleName.includes("induction")?"inductionTemplate":"inspectionTemplate"},headerText:function(){var moduleName=this.moduleName;return moduleName.includes("induction")?"Induction":"Inspection"},siteName:function(){var details=this.details,_ref4=details||{},siteId=_ref4.siteId,site=this.$store.getters.getSite(siteId);return(site||{}).name||""},isInspectionModule:function(){var moduleName=this.moduleName;return moduleName.includes("inspection")},templateRecord:function(){var details=this.details,template=details.template;return template},responseName:function(){var details=this.details,_ref5=details||{},name=_ref5.name;return name}},methods:{loadTemplate:function(){var _arguments=arguments,_this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var responseModuleName,details,_ref6,template,_ref7,pages,response,finalQandA;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:if(responseModuleName=_arguments.length>0&&void 0!==_arguments[0]?_arguments[0]:null,_this.loading=!0,details=_this.details,_ref6=details||{},template=_ref6.template,_ref7=template||{},pages=_ref7.pages,(0,validation/* isEmpty */.xb)(pages)){_context.next=12;break}return _context.next=8,_this.loadQAndA(pages,responseModuleName);case 8:response=_context.sent,finalQandA=[],response.forEach((function(qandaResponse){var error=qandaResponse.error,data=qandaResponse["qandaPage"],questions=data.questions,count=0;questions=(0,validation/* isEmpty */.xb)(questions)?[]:questions.map((function(question){return question.answerRequired?(count+=1,(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},question),{},{questionNo:"Q".concat(count,".")})):question})),error?_this.$message.error(error.messge||"Error Occured"):finalQandA.push((0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},data),{},{questions:questions}))})),_this.pages=finalQandA;case 12:_this.loading=!1,_this.autoResize();case 14:case"end":return _context.stop()}}),_callee)})))()},loadQAndA:function(pages,responseModuleName){var _this2=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee2(){var details,moduleName,_ref8,responseId,promise;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context2){while(1)switch(_context2.prev=_context2.next){case 0:return details=_this2.details,moduleName=_this2.moduleName,_ref8=details||{},responseId=_ref8.id,promise=[],(0,validation/* isEmpty */.xb)(responseId)||pages.forEach((function(page){var params={response:responseId};params=(0,validation/* isEmpty */.xb)(responseModuleName)?(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},params),{},{responseModuleName:moduleName}):(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},params),{},{responseModuleName:responseModuleName});var _ref9=page||{},id=_ref9.id,qandaResponse=(0,relatedFieldUtil/* getUnRelatedModuleSummary */.$B)(moduleName,"qandaPage",id,params);promise.push(qandaResponse)})),_context2.abrupt("return",Promise.all(promise));case 5:case"end":return _context2.stop()}}),_callee2)})))()},autoResize:function(){var _this3=this;this.$nextTick((function(){var container=_this3.$refs["qanda-section"];if(container){var height=container.scrollHeight,width=container.scrollWidth;_this3.resizeWidget&&_this3.resizeWidget({height:height+60,width:width})}}))},isLastItem:function(index,array){return Array.isArray(array)&&index===array.length-1},getPageScore:function(page){var _ref10=page||{},fullScore=_ref10.fullScore,totalScore=_ref10.totalScore;return(0,validation/* isEmpty */.xb)(totalScore)?"":"".concat(totalScore,"/").concat(fullScore)},getQuestionScore:function(question){var _ref11=question||{},fullScore=_ref11.fullScore,score=_ref11.score,validateScore=!(0,validation/* isEmpty */.xb)(score)&&!(0,validation/* isEmpty */.xb)(fullScore);return validateScore?"".concat(score,"/").concat(fullScore):""},canShowQuestion:function(question){var responseStatus=this.details.responseStatus,_ref12=question||{},displayLogicMeta=_ref12.displayLogicMeta,_ref12$answer=_ref12.answer,answer=void 0===_ref12$answer?null:_ref12$answer;if(!(0,validation/* isEmpty */.xb)(displayLogicMeta)){var _ref13=displayLogicMeta[0]||{},action=_ref13.action;return"hide"!==action||!(0,validation/* isEmpty */.xb)(answer)&&4==responseStatus}return!0}}},individual_inspection_InspectionPdfWidgetvue_type_script_lang_js=InspectionPdfWidgetvue_type_script_lang_js,InspectionPdfWidget_component=(0,componentNormalizer/* default */.Z)(individual_inspection_InspectionPdfWidgetvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,InspectionPdfWidget=InspectionPdfWidget_component.exports},
/***/640879:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return IndividualSurveyResult}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/setup/survey/IndividualSurveyResult.vue?vue&type=template&id=40c604d4&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("el-dialog",{staticStyle:{"z-index":"999999"},attrs:{visible:_vm.showResultDialog,"append-to-body":!0,"custom-class":"add-new-form-animated slideInRight fc-dialog-form fc-dialog-right setup-dialog40 add-new-form","before-close":_vm.closeDialog}},[_vm.surveyLoading||_vm.loading?_c("div",{staticClass:"mT20"},[_c("spinner",{attrs:{show:_vm.surveyLoading||_vm.loading,size:"80"}})],1):_c("div",{ref:"qanda-section",staticClass:"qanda-response-widget survey-response"},[_c("div",{staticClass:"template-name"},[_vm._v(_vm._s(_vm.surveyName||_vm.surveyTitle))]),_c("el-row",{staticClass:"survey-details"},[_c("el-col",{staticClass:"details-section",attrs:{span:24}},[_c("span",{staticClass:"details-header"},[_vm._v(" "+_vm._s(_vm.moduleDisplayName||_vm.recordModuleName)+" ")]),_c("span",{directives:[{name:"tippy",rawName:"v-tippy"}],staticClass:"survey-detail truncate-text",attrs:{title:_vm.recordName}},[_vm._v(_vm._s(_vm.recordName))])])],1),_c("el-row",{staticClass:"survey-details"},[_c("el-col",{staticClass:"details-section mR20",attrs:{span:8}},[_c("span",{staticClass:"details-header"},[_vm._v(" "+_vm._s(_vm.$t("survey.survey_respondent"))+" ")]),_c("span",{staticClass:"survey-detail"},[_vm._v(_vm._s(_vm.attenderName))])]),_c("el-col",{staticClass:"details-section",attrs:{span:8}},[_c("span",{staticClass:"details-header"},[_vm._v(" "+_vm._s(_vm.$t("survey.response_date"))+" ")]),_c("span",{staticClass:"survey-detail"},[_vm._v(_vm._s(_vm.responseTime))])]),_vm.$validation.isEmpty(_vm.responseScore)?_vm._e():_c("el-col",{staticClass:"details-section",attrs:{span:8}},[_c("span",{staticClass:"details-header"},[_vm._v(" "+_vm._s(_vm.$t("survey.total_score"))+" ")]),_c("span",{staticClass:"survey-detail"},[_vm._v(_vm._s(_vm.responseScore))])])],1),_c("div",{staticClass:"height680 overflow-y-scroll"},[_c("table",{staticClass:"width100 border-tlbr-none"},[_c("tbody",[_c("tr",[_c("td",{attrs:{colspan:"100%"}},[_vm.$validation.isEmpty(_vm.pages)?_vm._e():_c("div",{staticClass:"qanda-section"},_vm._l(_vm.pages,(function(page,pageIndex){return _c("div",{key:page.id,class:["page-section",!_vm.isLastItem(pageIndex,page.pages)&&"border-bottom"]},[_c("div",{staticClass:"page-details-header"},[_c("div",[_c("div",{staticClass:"page-name"},[_vm._v(_vm._s(page.name))]),_c("div",{staticClass:"page-description"},[_vm._v(" "+_vm._s(page.description)+" ")])]),_vm.$validation.isEmpty(_vm.getPageScore(page))?_vm._e():_c("div",{staticClass:"score-text"},[_vm._v(" "+_vm._s(_vm.getPageScore(page))+" ")])]),_vm.$validation.isEmpty(page.questions)?_c("div",{staticClass:"question-empty-state"},[_c("inline-svg",{attrs:{src:"svgs/inspection-empty",iconClass:"icon text-center icon-80"}}),_c("div",{staticClass:"questions-empty-text"},[_vm._v(" "+_vm._s(_vm.$t("qanda.response.no_questions"))+" ")])],1):[_c("table",{staticClass:"width100 border-tlbr-none print-inner-table mB150"},[_c("tbody",_vm._l(page.questions,(function(question,questionIndex){return _c("tr",{key:question.id,staticClass:"fc-tr-border"},[_c("td",{attrs:{colspan:"100%"}},[_c("div",{class:["qandacontainer",!_vm.isLastItem(questionIndex,page.questions)&&"border-bottom"]},[_c("div",{staticClass:"fc__layout__align"},[_c("div",{staticClass:"question"},[_vm._v(" "+_vm._s(question.questionNo)+" "+_vm._s(question.question)+" ")]),_vm.$validation.isEmpty(_vm.getQuestionScore(question))?_vm._e():_c("div",{staticClass:"score-text"},[_vm._v(" "+_vm._s(_vm.getQuestionScore(question))+" ")])]),_vm.$validation.isEmpty(question.description)?_vm._e():_c("div",{staticClass:"question-description pT5"},[_vm._v(" "+_vm._s(question.description)+" ")]),_c("div",[question.answerRequired?_c("div",{staticClass:"answer-heading-text mL27"},[_vm._v(" "+_vm._s(_vm.$t("qanda.response.answer"))+" ")]):_vm._e(),_c("FieldDisplayValue",{attrs:{question:question}}),(question.answer||{}).comments?[_c("div",{staticClass:"bold f14 pT15 text-fc-grey remarks-field mL27"},[_vm._v(" "+_vm._s((question||{}).commentsLabel)+" ")]),_c("div",{staticClass:"answer mL27"},[_vm._v(" "+_vm._s((question.answer||{}).comments)+" ")])]:_vm._e(),_vm.$validation.isEmpty((question.answer||{}).attachmentList)?_vm._e():[_c("div",{staticClass:"bold f14 pT15 text-fc-grey remarks-field mL27"},[_vm._v(" "+_vm._s((question||{}).attachmentLabel)+" ")]),_c("FileFieldPreview",{staticClass:"mL27",attrs:{attachments:(question.answer||{}).attachmentList}})]],2)])])])})),0),_c("tbody")])]],2)})),0)])])])])])],1)])},staticRenderFns=[],regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),InspectionPdfWidget=(__webpack_require__(434284),__webpack_require__(640261)),validation=__webpack_require__(990260),helpers=__webpack_require__(778390),moment=helpers/* default */.ZP.getOrgMoment,IndividualSurveyResultvue_type_script_lang_js={name:"IndividualSurveyResult",extends:InspectionPdfWidget["default"],props:["showResultDialog","details","surveyLoading","recordName","module","moduleDisplayName","surveyName"],watch:{details:{handler:function(newVal){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:if((0,validation/* isEmpty */.xb)(newVal)){_context.next=3;break}return _context.next=3,_this.loadTemplate("surveyResponse");case 3:case"end":return _context.stop()}}),_callee)})))()},immediate:!0}},computed:{moduleName:function(){var moduleName=this.$attrs.moduleName;return moduleName},surveyTitle:function(){var details=this.details,_ref=details||{},name=_ref.name;return(0,validation/* isEmpty */.xb)(name)?"":name},recordModuleName:function(){var moduleName=this.$attrs.moduleName;return moduleName},responseTime:function(){var details=this.details,_ref2=details||{},sysModifiedTime=_ref2.sysModifiedTime;return(0,validation/* isEmpty */.xb)(sysModifiedTime)?"N/A":moment(sysModifiedTime).format("DD/MM/YYYY")}},methods:{closeDialog:function(){this.$emit("update:showResultDialog",!1)}}},survey_IndividualSurveyResultvue_type_script_lang_js=IndividualSurveyResultvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(survey_IndividualSurveyResultvue_type_script_lang_js,render,staticRenderFns,!1,null,"40c604d4",null)
/* harmony default export */,IndividualSurveyResult=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/40879.js.map