(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[50232],{
/***/679742:
/***/function(__unused_webpack_module,exports){"use strict";exports.byteLength=byteLength,exports.toByteArray=toByteArray,exports.fromByteArray=fromByteArray;for(var lookup=[],revLookup=[],Arr="undefined"!==typeof Uint8Array?Uint8Array:Array,code="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/",i=0,len=code.length;i<len;++i)lookup[i]=code[i],revLookup[code.charCodeAt(i)]=i;
// Support decoding URL-safe base64 strings, as Node.js does.
// See: https://en.wikipedia.org/wiki/Base64#URL_applications
function getLens(b64){var len=b64.length;if(len%4>0)throw new Error("Invalid string. Length must be a multiple of 4");
// Trim off extra bytes after placeholder bytes are found
// See: https://github.com/beatgammit/base64-js/issues/42
var validLen=b64.indexOf("=");-1===validLen&&(validLen=len);var placeHoldersLen=validLen===len?0:4-validLen%4;return[validLen,placeHoldersLen]}
// base64 is 4/3 + up to two characters of the original data
function byteLength(b64){var lens=getLens(b64),validLen=lens[0],placeHoldersLen=lens[1];return 3*(validLen+placeHoldersLen)/4-placeHoldersLen}function _byteLength(b64,validLen,placeHoldersLen){return 3*(validLen+placeHoldersLen)/4-placeHoldersLen}function toByteArray(b64){var tmp,i,lens=getLens(b64),validLen=lens[0],placeHoldersLen=lens[1],arr=new Arr(_byteLength(b64,validLen,placeHoldersLen)),curByte=0,len=placeHoldersLen>0?validLen-4:validLen;for(i=0;i<len;i+=4)tmp=revLookup[b64.charCodeAt(i)]<<18|revLookup[b64.charCodeAt(i+1)]<<12|revLookup[b64.charCodeAt(i+2)]<<6|revLookup[b64.charCodeAt(i+3)],arr[curByte++]=tmp>>16&255,arr[curByte++]=tmp>>8&255,arr[curByte++]=255&tmp;return 2===placeHoldersLen&&(tmp=revLookup[b64.charCodeAt(i)]<<2|revLookup[b64.charCodeAt(i+1)]>>4,arr[curByte++]=255&tmp),1===placeHoldersLen&&(tmp=revLookup[b64.charCodeAt(i)]<<10|revLookup[b64.charCodeAt(i+1)]<<4|revLookup[b64.charCodeAt(i+2)]>>2,arr[curByte++]=tmp>>8&255,arr[curByte++]=255&tmp),arr}function tripletToBase64(num){return lookup[num>>18&63]+lookup[num>>12&63]+lookup[num>>6&63]+lookup[63&num]}function encodeChunk(uint8,start,end){for(var tmp,output=[],i=start;i<end;i+=3)tmp=(uint8[i]<<16&16711680)+(uint8[i+1]<<8&65280)+(255&uint8[i+2]),output.push(tripletToBase64(tmp));return output.join("")}function fromByteArray(uint8){// must be multiple of 3
// go through the array every three bytes, we'll deal with trailing stuff later
for(var tmp,len=uint8.length,extraBytes=len%3,parts=[],maxChunkLength=16383,i=0,len2=len-extraBytes;i<len2;i+=maxChunkLength)parts.push(encodeChunk(uint8,i,i+maxChunkLength>len2?len2:i+maxChunkLength));
// pad the end with zeros, but make sure to not forget the extra bytes
return 1===extraBytes?(tmp=uint8[len-1],parts.push(lookup[tmp>>2]+lookup[tmp<<4&63]+"==")):2===extraBytes&&(tmp=(uint8[len-2]<<8)+uint8[len-1],parts.push(lookup[tmp>>10]+lookup[tmp>>4&63]+lookup[tmp<<2&63]+"=")),parts.join("")}
/***/revLookup["-".charCodeAt(0)]=62,revLookup["_".charCodeAt(0)]=63},
/***/872695:
/***/function(module,__unused_webpack_exports,__webpack_require__){
/*!
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 * 
 * billboard.js, JavaScript chart library
 * https://naver.github.io/billboard.js/
 * 
 * @version 1.12.12
 */
(function(root,factory){module.exports=factory(__webpack_require__(748866),__webpack_require__(414469),__webpack_require__(386254),__webpack_require__(785735),__webpack_require__(147878),__webpack_require__(313873),__webpack_require__(86664),__webpack_require__(396700),__webpack_require__(130201),__webpack_require__(94743),__webpack_require__(552540),__webpack_require__(207716))})(0,(function(__WEBPACK_EXTERNAL_MODULE__2__,__WEBPACK_EXTERNAL_MODULE__3__,__WEBPACK_EXTERNAL_MODULE__4__,__WEBPACK_EXTERNAL_MODULE__5__,__WEBPACK_EXTERNAL_MODULE__6__,__WEBPACK_EXTERNAL_MODULE__7__,__WEBPACK_EXTERNAL_MODULE__8__,__WEBPACK_EXTERNAL_MODULE__9__,__WEBPACK_EXTERNAL_MODULE__10__,__WEBPACK_EXTERNAL_MODULE__11__,__WEBPACK_EXTERNAL_MODULE__12__,__WEBPACK_EXTERNAL_MODULE__13__){/******/
return function(modules){// webpackBootstrap
/******/ // The module cache
/******/var installedModules={};
/******/
/******/ // The require function
/******/function __nested_webpack_require_2020__(moduleId){
/******/
/******/ // Check if module is in cache
/******/if(installedModules[moduleId])
/******/return installedModules[moduleId].exports;
/******/
/******/ // Create a new module (and put it into the cache)
/******/var module=installedModules[moduleId]={
/******/i:moduleId,
/******/l:!1,
/******/exports:{}
/******/};
/******/
/******/ // Execute the module function
/******/
/******/
/******/ // Return the exports of the module
/******/return modules[moduleId].call(module.exports,module,module.exports,__nested_webpack_require_2020__),
/******/
/******/ // Flag the module as loaded
/******/module.l=!0,module.exports;
/******/}
/******/
/******/
/******/ // expose the modules object (__webpack_modules__)
/******/
/******/
/******/
/******/ // Load entry module and return exports
/******/return __nested_webpack_require_2020__.m=modules,
/******/
/******/ // expose the module cache
/******/__nested_webpack_require_2020__.c=installedModules,
/******/
/******/ // define getter function for harmony exports
/******/__nested_webpack_require_2020__.d=function(exports,name,getter){
/******/__nested_webpack_require_2020__.o(exports,name)||
/******/Object.defineProperty(exports,name,{enumerable:!0,get:getter})
/******/},
/******/
/******/ // define __esModule on exports
/******/__nested_webpack_require_2020__.r=function(exports){
/******/"undefined"!==typeof Symbol&&Symbol.toStringTag&&
/******/Object.defineProperty(exports,Symbol.toStringTag,{value:"Module"})
/******/,Object.defineProperty(exports,"__esModule",{value:!0})},
/******/
/******/ // create a fake namespace object
/******/ // mode & 1: value is a module id, require it
/******/ // mode & 2: merge all properties of value into the ns
/******/ // mode & 4: return value when already ns object
/******/ // mode & 8|1: behave like require
/******/__nested_webpack_require_2020__.t=function(value,mode){
/******/if(
/******/1&mode&&(value=__nested_webpack_require_2020__(value)),8&mode)return value;
/******/if(4&mode&&"object"===typeof value&&value&&value.__esModule)return value;
/******/var ns=Object.create(null);
/******/
/******/if(__nested_webpack_require_2020__.r(ns),
/******/Object.defineProperty(ns,"default",{enumerable:!0,value:value}),2&mode&&"string"!=typeof value)for(var key in value)__nested_webpack_require_2020__.d(ns,key,function(key){return value[key]}.bind(null,key));
/******/return ns;
/******/},
/******/
/******/ // getDefaultExport function for compatibility with non-harmony modules
/******/__nested_webpack_require_2020__.n=function(module){
/******/var getter=module&&module.__esModule?
/******/function(){return module["default"]}:
/******/function(){return module};
/******/
/******/return __nested_webpack_require_2020__.d(getter,"a",getter),getter;
/******/},
/******/
/******/ // Object.prototype.hasOwnProperty.call
/******/__nested_webpack_require_2020__.o=function(object,property){return Object.prototype.hasOwnProperty.call(object,property)},
/******/
/******/ // __webpack_public_path__
/******/__nested_webpack_require_2020__.p="",__nested_webpack_require_2020__(__nested_webpack_require_2020__.s=0);
/******/}
/************************************************************************/
/******/([
/* 0 */
/***/function(module,exports,__nested_webpack_require_5484__){__nested_webpack_require_5484__(1),module.exports=__nested_webpack_require_5484__(14)},
/* 1 */
/***/function(module,exports,__webpack_require__){
// extracted by mini-css-extract-plugin
/***/},
/* 2 */
/***/function(module,exports){module.exports=__WEBPACK_EXTERNAL_MODULE__2__;
/***/},
/* 3 */
/***/function(module,exports){module.exports=__WEBPACK_EXTERNAL_MODULE__3__;
/***/},
/* 4 */
/***/function(module,exports){module.exports=__WEBPACK_EXTERNAL_MODULE__4__;
/***/},
/* 5 */
/***/function(module,exports){module.exports=__WEBPACK_EXTERNAL_MODULE__5__;
/***/},
/* 6 */
/***/function(module,exports){module.exports=__WEBPACK_EXTERNAL_MODULE__6__;
/***/},
/* 7 */
/***/function(module,exports){module.exports=__WEBPACK_EXTERNAL_MODULE__7__;
/***/},
/* 8 */
/***/function(module,exports){module.exports=__WEBPACK_EXTERNAL_MODULE__8__;
/***/},
/* 9 */
/***/function(module,exports){module.exports=__WEBPACK_EXTERNAL_MODULE__9__;
/***/},
/* 10 */
/***/function(module,exports){module.exports=__WEBPACK_EXTERNAL_MODULE__10__;
/***/},
/* 11 */
/***/function(module,exports){module.exports=__WEBPACK_EXTERNAL_MODULE__11__;
/***/},
/* 12 */
/***/function(module,exports){module.exports=__WEBPACK_EXTERNAL_MODULE__12__;
/***/},
/* 13 */
/***/function(module,exports){module.exports=__WEBPACK_EXTERNAL_MODULE__13__;
/***/},
/* 14 */
/***/function(module,__nested_webpack_exports__,__nested_webpack_require_7012__){"use strict";
// ESM COMPAT FLAG
// CONCATENATED MODULE: ./node_modules/@babel/runtime/helpers/esm/classCallCheck.js
function _classCallCheck(instance,Constructor){if(!(instance instanceof Constructor))throw new TypeError("Cannot call a class as a function")}
// CONCATENATED MODULE: ./node_modules/@babel/runtime/helpers/esm/arrayWithHoles.js
function _arrayWithHoles(arr){if(Array.isArray(arr))return arr}
// CONCATENATED MODULE: ./node_modules/@babel/runtime/helpers/esm/iterableToArrayLimit.js
function _iterableToArrayLimit(arr,i){if("undefined"!==typeof Symbol&&Symbol.iterator in Object(arr)){var _arr=[],_n=!0,_d=!1,_e=void 0;try{for(var _s,_i=arr[Symbol.iterator]();!(_n=(_s=_i.next()).done);_n=!0)if(_arr.push(_s.value),i&&_arr.length===i)break}catch(err){_d=!0,_e=err}finally{try{_n||null==_i["return"]||_i["return"]()}finally{if(_d)throw _e}}return _arr}}
// CONCATENATED MODULE: ./node_modules/@babel/runtime/helpers/esm/arrayLikeToArray.js
function _arrayLikeToArray(arr,len){(null==len||len>arr.length)&&(len=arr.length);for(var i=0,arr2=new Array(len);i<len;i++)arr2[i]=arr[i];return arr2}
// CONCATENATED MODULE: ./node_modules/@babel/runtime/helpers/esm/unsupportedIterableToArray.js
function _unsupportedIterableToArray(o,minLen){if(o){if("string"===typeof o)return _arrayLikeToArray(o,minLen);var n=Object.prototype.toString.call(o).slice(8,-1);return"Object"===n&&o.constructor&&(n=o.constructor.name),"Map"===n||"Set"===n?Array.from(o):"Arguments"===n||/^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n)?_arrayLikeToArray(o,minLen):void 0}}
// CONCATENATED MODULE: ./node_modules/@babel/runtime/helpers/esm/nonIterableRest.js
function _nonIterableRest(){throw new TypeError("Invalid attempt to destructure non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.")}
// CONCATENATED MODULE: ./node_modules/@babel/runtime/helpers/esm/slicedToArray.js
function _slicedToArray(arr,i){return _arrayWithHoles(arr)||_iterableToArrayLimit(arr,i)||_unsupportedIterableToArray(arr,i)||_nonIterableRest()}
// CONCATENATED MODULE: ./node_modules/@babel/runtime/helpers/esm/createClass.js
function _defineProperties(target,props){for(var i=0;i<props.length;i++){var descriptor=props[i];descriptor.enumerable=descriptor.enumerable||!1,descriptor.configurable=!0,"value"in descriptor&&(descriptor.writable=!0),Object.defineProperty(target,descriptor.key,descriptor)}}function _createClass(Constructor,protoProps,staticProps){return protoProps&&_defineProperties(Constructor.prototype,protoProps),staticProps&&_defineProperties(Constructor,staticProps),Constructor}
// EXTERNAL MODULE: external {"commonjs":"d3-time-format","commonjs2":"d3-time-format","amd":"d3-time-format","root":"d3"}
__nested_webpack_require_7012__.r(__nested_webpack_exports__),
// EXPORTS
__nested_webpack_require_7012__.d(__nested_webpack_exports__,"bb",(function(){/* binding */return bb}));var external_commonjs_d3_time_format_commonjs2_d3_time_format_amd_d3_time_format_root_d3_=__nested_webpack_require_7012__(2),external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_=__nested_webpack_require_7012__(3),external_commonjs_d3_transition_commonjs2_d3_transition_amd_d3_transition_root_d3_=__nested_webpack_require_7012__(4),external_commonjs_d3_axis_commonjs2_d3_axis_amd_d3_axis_root_d3_=__nested_webpack_require_7012__(5),external_commonjs_d3_scale_commonjs2_d3_scale_amd_d3_scale_root_d3_=__nested_webpack_require_7012__(6),config_classes={arc:"bb-arc",arcLabelLine:"bb-arc-label-line",arcs:"bb-arcs",area:"bb-area",areas:"bb-areas",axis:"bb-axis",axisX:"bb-axis-x",axisXLabel:"bb-axis-x-label",axisY:"bb-axis-y",axisY2:"bb-axis-y2",axisY2Label:"bb-axis-y2-label",axisYLabel:"bb-axis-y-label",bar:"bb-bar",bars:"bb-bars",brush:"bb-brush",button:"bb-button",buttonZoomReset:"bb-zoom-reset",chart:"bb-chart",chartArc:"bb-chart-arc",chartArcs:"bb-chart-arcs",chartArcsBackground:"bb-chart-arcs-background",chartArcsGaugeMax:"bb-chart-arcs-gauge-max",chartArcsGaugeMin:"bb-chart-arcs-gauge-min",chartArcsGaugeUnit:"bb-chart-arcs-gauge-unit",chartArcsTitle:"bb-chart-arcs-title",chartArcsGaugeTitle:"bb-chart-arcs-gauge-title",chartBar:"bb-chart-bar",chartBars:"bb-chart-bars",chartLine:"bb-chart-line",chartLines:"bb-chart-lines",chartRadar:"bb-chart-radar",chartRadars:"bb-chart-radars",chartText:"bb-chart-text",chartTexts:"bb-chart-texts",circle:"bb-circle",circles:"bb-circles",colorPattern:"bb-color-pattern",colorScale:"bb-colorscale",defocused:"bb-defocused",dragarea:"bb-dragarea",empty:"bb-empty",eventRect:"bb-event-rect",eventRects:"bb-event-rects",eventRectsMultiple:"bb-event-rects-multiple",eventRectsSingle:"bb-event-rects-single",focused:"bb-focused",gaugeValue:"bb-gauge-value",grid:"bb-grid",gridLines:"bb-grid-lines",legendBackground:"bb-legend-background",legendItem:"bb-legend-item",legendItemEvent:"bb-legend-item-event",legendItemFocused:"bb-legend-item-focused",legendItemHidden:"bb-legend-item-hidden",legendItemPoint:"bb-legend-item-point",legendItemTile:"bb-legend-item-tile",level:"bb-level",levels:"bb-levels",line:"bb-line",lines:"bb-lines",region:"bb-region",regions:"bb-regions",selectedCircle:"bb-selected-circle",selectedCircles:"bb-selected-circles",shape:"bb-shape",shapes:"bb-shapes",stanfordElements:"bb-stanford-elements",stanfordLine:"bb-stanford-line",stanfordLines:"bb-stanford-lines",stanfordRegion:"bb-stanford-region",stanfordRegions:"bb-stanford-regions",target:"bb-target",text:"bb-text",texts:"bb-texts",title:"bb-title",tooltip:"bb-tooltip",tooltipContainer:"bb-tooltip-container",tooltipName:"bb-tooltip-name",xgrid:"bb-xgrid",xgridFocus:"bb-xgrid-focus",xgridLine:"bb-xgrid-line",xgridLines:"bb-xgrid-lines",xgrids:"bb-xgrids",ygrid:"bb-ygrid",ygridFocus:"bb-ygrid-focus",ygridLine:"bb-ygrid-line",ygridLines:"bb-ygrid-lines",ygrids:"bb-ygrids",zoomBrush:"bb-zoom-brush",zoomRect:"bb-zoom-rect",EXPANDED:"_expanded_",SELECTED:"_selected_",INCLUDED:"_included_",TextOverlapping:"text-overlapping"};
// EXTERNAL MODULE: external {"commonjs":"d3-selection","commonjs2":"d3-selection","amd":"d3-selection","root":"d3"}
// CONCATENATED MODULE: ./node_modules/@babel/runtime/helpers/esm/arrayWithoutHoles.js
function _arrayWithoutHoles(arr){if(Array.isArray(arr))return _arrayLikeToArray(arr)}
// CONCATENATED MODULE: ./node_modules/@babel/runtime/helpers/esm/iterableToArray.js
function _iterableToArray(iter){if("undefined"!==typeof Symbol&&Symbol.iterator in Object(iter))return Array.from(iter)}
// CONCATENATED MODULE: ./node_modules/@babel/runtime/helpers/esm/nonIterableSpread.js
function _nonIterableSpread(){throw new TypeError("Invalid attempt to spread non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.")}
// CONCATENATED MODULE: ./node_modules/@babel/runtime/helpers/esm/toConsumableArray.js
function _toConsumableArray(arr){return _arrayWithoutHoles(arr)||_iterableToArray(arr)||_unsupportedIterableToArray(arr)||_nonIterableSpread()}
// CONCATENATED MODULE: ./node_modules/@babel/runtime/helpers/esm/typeof.js
function _typeof(obj){return _typeof="function"===typeof Symbol&&"symbol"===typeof Symbol.iterator?function(obj){return typeof obj}:function(obj){return obj&&"function"===typeof Symbol&&obj.constructor===Symbol&&obj!==Symbol.prototype?"symbol":typeof obj},_typeof(obj)}
// EXTERNAL MODULE: external {"commonjs":"d3-brush","commonjs2":"d3-brush","amd":"d3-brush","root":"d3"}
var external_commonjs_d3_brush_commonjs2_d3_brush_amd_d3_brush_root_d3_=__nested_webpack_require_7012__(7),win=function(){var def=function(o){return"undefined"!==typeof o&&o};return def(self)||def(window)||def(__webpack_require__.g)||def(globalThis)||Function("return this")()}(),browser_doc=win&&win.document,isValue=function(v){return v||0===v},isFunction=function(v){return"function"===typeof v},isString=function(v){return"string"===typeof v},isNumber=function(v){return"number"===typeof v},isUndefined=function(v){return"undefined"===typeof v},isDefined=function(v){return"undefined"!==typeof v},isBoolean=function(v){return"boolean"===typeof v},ceil10=function(v){return 10*Math.ceil(v/10)},asHalfPixel=function(n){return Math.ceil(n)+.5},diffDomain=function(d){return d[1]-d[0]},isObjectType=function(v){return"object"===_typeof(v)},isEmpty=function(o){return isUndefined(o)||null===o||isString(o)&&0===o.length||isObjectType(o)&&!(o instanceof Date)&&0===Object.keys(o).length||isNumber(o)&&isNaN(o)},notEmpty=function(o){return!isEmpty(o)},isArray=function(arr){return arr&&arr.constructor===Array},isObject=function(obj){return obj&&!obj.nodeType&&isObjectType(obj)&&!isArray(obj)},getOption=function(options,key,defaultValue){return isDefined(options[key])?options[key]:defaultValue},util_hasValue=function(dict,value){var found=!1;return Object.keys(dict).forEach((function(key){return dict[key]===value&&(found=!0)})),found},callFn=function(fn){for(var isFn=isFunction(fn),_len=arguments.length,args=Array(_len>1?_len-1:0),_key=1;_key<_len;_key++)args[_key-1]=arguments[_key];return isFn&&fn.call.apply(fn,args),isFn},sanitise=function(str){return isString(str)?str.replace(/</g,"&lt;").replace(/>/g,"&gt;"):str},setTextValue=function(node,text){var dy=arguments.length>2&&void 0!==arguments[2]?arguments[2]:[-1,1],toMiddle=!!(arguments.length>3&&void 0!==arguments[3])&&arguments[3];if(node&&isString(text))if(-1===text.indexOf("\n"))node.text(text);else{var diff=[node.text(),text].map((function(v){return v.replace(/[\s\n]/g,"")}));if(diff[0]!==diff[1]){var multiline=text.split("\n"),len=toMiddle?multiline.length-1:1;node.html(""),multiline.forEach((function(v,i){node.append("tspan").attr("x",0).attr("dy","".concat(0===i?dy[0]*len:dy[1],"em")).text(v)}))}}},getRectSegList=function(path){
/*
   * seg1 ---------- seg2
   *   |               |
   *   |               |
   *   |               |
   * seg0 ---------- seg3
   * */
var _path$getBBox=path.getBBox(),x=_path$getBBox.x,y=_path$getBBox.y,width=_path$getBBox.width,height=_path$getBBox.height;return[{x:x,y:y+height},// seg0
{x:x,y:y},// seg1
{x:x+width,y:y},// seg2
{x:x+width,y:y+height}]},getPathBox=function(path){var _path$getBoundingClie=path.getBoundingClientRect(),width=_path$getBoundingClie.width,height=_path$getBoundingClie.height,items=getRectSegList(path),x=items[0].x,y=Math.min(items[0].y,items[1].y);return{x:x,y:y,width:width,height:height}},getBrushSelection=function(ctx){var selection=null,event=external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["event"],main=ctx.context||ctx.main;return event&&"BrushEvent"===event.constructor.name?selection=event.selection:main&&(selection=main.select(".".concat(config_classes.brush)).node())&&(selection=Object(external_commonjs_d3_brush_commonjs2_d3_brush_amd_d3_brush_root_d3_["brushSelection"])(selection)),selection},getBoundingRect=function(node){return node.rect||(node.rect=node.getBoundingClientRect())},getRandom=function(){var asStr=!(arguments.length>0&&void 0!==arguments[0])||arguments[0];return Math.random()+(asStr?"":0)},brushEmpty=function(ctx){var selection=getBrushSelection(ctx);return!selection||selection[0]===selection[1]},extend=function(){var target=arguments.length>0&&void 0!==arguments[0]?arguments[0]:{},source=arguments.length>1?arguments[1]:void 0;for(var p in source)target[p]=source[p];return target},capitalize=function(str){return str.charAt(0).toUpperCase()+str.slice(1)},toArray=function(v){return[].slice.call(v)},getCssRules=function(styleSheets){var rules=[];return styleSheets.forEach((function(sheet){try{sheet.cssRules&&sheet.cssRules.length&&(rules=rules.concat(toArray(sheet.cssRules)))}catch(e){}})),rules},getTranslation=function(node){var transform=node?node.transform:null,baseVal=transform&&transform.baseVal;return baseVal&&baseVal.numberOfItems?baseVal.getItem(0).matrix:{a:0,b:0,c:0,d:0,e:0,f:0}},getUnique=function(data){var isDate=data[0]instanceof Date,d=(isDate?data.map(Number):data).filter((function(v,i,self){return self.indexOf(v)===i}));return isDate?d.map((function(v){return new Date(v)})):d},mergeArray=function(arr){return arr&&arr.length?arr.reduce((function(p,c){return p.concat(c)})):[]},mergeObj=function(_mergeObj){function mergeObj(){return _mergeObj.apply(this,arguments)}return mergeObj.toString=function(){return _mergeObj.toString()},mergeObj}((function(target){for(var _len2=arguments.length,objectN=Array(_len2>1?_len2-1:0),_key2=1;_key2<_len2;_key2++)objectN[_key2-1]=arguments[_key2];if(!objectN.length||1===objectN.length&&!objectN[0])return target;var source=objectN.shift();return isObject(target)&&isObject(source)&&Object.keys(source).forEach((function(key){var value=source[key];isObject(value)?(!target[key]&&(target[key]={}),target[key]=mergeObj(target[key],value)):target[key]=isArray(value)?value.concat():value})),mergeObj.apply(void 0,[target].concat(objectN))})),sortValue=function(data){var fn,isAsc=!(arguments.length>1&&void 0!==arguments[1])||arguments[1];return data[0]instanceof Date?fn=isAsc?function(a,b){return a-b}:function(a,b){return b-a}:isAsc&&!data.every(isNaN)?fn=function(a,b){return a-b}:!isAsc&&(fn=function(a,b){return(a>b?-1:a<b&&1)||a===b&&0}),data.concat().sort(fn)},getMinMax=function(type,data){var res=data.filter((function(v){return notEmpty(v)}));return res.length?isNumber(res[0])?res=Math[type].apply(Math,_toConsumableArray(res)):res[0]instanceof Date&&(res=sortValue(res,"min"===type)[0]):res=void 0,res},getRange=function(start,end){for(var step=arguments.length>2&&void 0!==arguments[2]?arguments[2]:1,res=[],n=0|Math.max(0,Math.ceil((end-start)/step)),i=start;i<n;i++)res.push(start+i*step);return res},emulateEvent={mouse:function(){var getParams=function(){return{bubbles:!1,cancelable:!1,screenX:0,screenY:0,clientX:0,clientY:0}};try{return new MouseEvent("t"),function(el,eventType){var params=arguments.length>2&&void 0!==arguments[2]?arguments[2]:getParams();el.dispatchEvent(new MouseEvent(eventType,params))}}catch(e){
// Polyfills DOM4 MouseEvent
return function(el,eventType){var params=arguments.length>2&&void 0!==arguments[2]?arguments[2]:getParams(),mouseEvent=browser_doc.createEvent("MouseEvent");mouseEvent.initMouseEvent(eventType,params.bubbles,params.cancelable,win,0,// the event's mouse click count
params.screenX,params.screenY,params.clientX,params.clientY,!1,!1,!1,!1,0,null),el.dispatchEvent(mouseEvent)}}}(),touch:function(el,eventType,params){var touchObj=new Touch(mergeObj({identifier:Date.now(),target:el,radiusX:2.5,radiusY:2.5,rotationAngle:10,force:.5},params));el.dispatchEvent(new TouchEvent(eventType,{cancelable:!0,bubbles:!0,shiftKey:!0,touches:[touchObj],targetTouches:[],changedTouches:[touchObj]}))}},tplProcess=function(tpl,data){var res=tpl;for(var x in data)res=res.replace(new RegExp("{=".concat(x,"}"),"g"),data[x]);return res},AxisRendererHelper_AxisRendererHelper=function(){function AxisRendererHelper(owner){_classCallCheck(this,AxisRendererHelper);var scale=Object(external_commonjs_d3_scale_commonjs2_d3_scale_amd_d3_scale_root_d3_["scaleLinear"])(),config=owner.config,params=owner.params;this.owner=owner,this.config=config,this.scale=scale,(config.noTransition||!params.config.transition_duration)&&(config.withoutTransition=!0),config.range=scale.rangeExtent?scale.rangeExtent():this.scaleExtent((params.orgXScale||scale).range())}
/**
   * Compute a character dimension
   * @param {d3.selection} node
   * @return {{w: number, h: number}}
   * @private
   */return _createClass(AxisRendererHelper,[{key:"getTickTransformSetter",
/**
     * Get tick transform setter function
     * @param {String} id Axis id
     * @private
     */
value:function(id){var config=this.config,fn="x"===id?function(value){return"translate(".concat(value+config.tickOffset,",0)")}:function(value){return"translate(0,".concat(value,")")};return function(selection,scale){selection.attr("transform",(function(d){return fn(Math.ceil(scale(d)))}))}}},{key:"scaleExtent",value:function(domain){var start=domain[0],stop=domain[domain.length-1];return start<stop?[start,stop]:[stop,start]}},{key:"generateTicks",value:function(scale,isYAxes){var tickStepSize=this.owner.params.tickStepSize,ticks=[];
// When 'axis[y|y2].tick.stepSize' option is set
if(isYAxes&&tickStepSize)for(var _scale$domain=scale.domain(),_scale$domain2=_slicedToArray(_scale$domain,2),start=_scale$domain2[0],end=_scale$domain2[1],interval=start;interval<=end;)ticks.push(interval),interval+=tickStepSize;else if(scale.ticks)ticks=scale.ticks.apply(scale,_toConsumableArray(this.config.tickArguments||[])).map((function(v){// round the tick value if is number
return isString(v)&&isNumber(v)&&!isNaN(v)&&Math.round(10*v)/10||v}));else{for(var domain=scale.domain(),i=Math.ceil(domain[0]);i<domain[1];i++)ticks.push(i);ticks.length>0&&ticks[0]>0&&ticks.unshift(ticks[0]-(ticks[1]-ticks[0]))}return ticks}},{key:"copyScale",value:function(){var newScale=this.scale.copy();return newScale.domain().length||newScale.domain(this.scale.domain()),newScale}},{key:"textFormatted",value:function(v){var tickFormat=this.config.tickFormat,value=/\d+\.\d+0{5,}\d$/.test(v)?+(v+"").replace(/0+\d$/,""):v,formatted=tickFormat?tickFormat(value):value;// to round float numbers from 'binary floating point'
// https://en.wikipedia.org/wiki/Double-precision_floating-point_format
// https://stackoverflow.com/questions/17849101/laymans-explanation-for-why-javascript-has-weird-floating-math-ieee-754-stand
return isDefined(formatted)?formatted:""}},{key:"transitionise",value:function(selection){var config=this.config;return config.withoutTransition?selection.interrupt():selection.transition(config.transition)}}],[{key:"getSizeFor1Char",value:function(node){
// default size for one character
var size={w:5.5,h:11.5};return node.empty()||node.select("text").text("0").call((function(el){try{var _el$node$getBBox=el.node().getBBox(),width=_el$node$getBBox.width,height=_el$node$getBBox.height;width&&height&&(size.w=width,size.h=height)}catch(e){}finally{el.text("")}})),this.getSizeFor1Char=function(){return size},size}}]),AxisRendererHelper}(),AxisRenderer_AxisRenderer=function(){function AxisRenderer(){var params=arguments.length>0&&void 0!==arguments[0]?arguments[0]:{};_classCallCheck(this,AxisRenderer);var config={innerTickSize:6,outerTickSize:params.outerTick?6:0,orient:"bottom",range:[],tickArguments:null,tickCentered:null,tickCulling:!0,tickFormat:null,tickLength:9,tickOffset:0,tickPadding:3,tickValues:null,transition:null,noTransition:params.noTransition};config.tickLength=Math.max(config.innerTickSize,0)+config.tickPadding,this.config=config,this.params=params,this.helper=new AxisRendererHelper_AxisRendererHelper(this)}
/**
   * Create axis element
   * @param {d3.selection} g
   * @private
   */return _createClass(AxisRenderer,[{key:"create",value:function(g){var ctx=this,config=this.config,helper=this.helper,params=this.params,scale=helper.scale,orient=config.orient,splitTickText=this.splitTickText.bind(this),isLeftRight=/^(left|right)$/.test(orient),isTopBottom=/^(top|bottom)$/.test(orient),tickTransform=helper.getTickTransformSetter(isTopBottom?"x":"y"),axisPx=tickTransform===helper.axisX?"y":"x",sign=/^(top|left)$/.test(orient)?-1:1,rotate=params.tickTextRotate;this.config.range=scale.rangeExtent?scale.rangeExtent():helper.scaleExtent((params.orgXScale||scale).range());var _config2=config,innerTickSize=_config2.innerTickSize,tickLength=_config2.tickLength,range=_config2.range,name=params.name,tickTextPos=name&&/^(x|y|y2)$/.test(name)?params.config["axis_".concat(name,"_tick_text_position")]:{x:0,y:0},prefix="subX"===name?"subchart_axis_x":"axis_".concat(name),axisShow=params.config["".concat(prefix,"_show")],tickShow={tick:!!axisShow&&params.config["".concat(prefix,"_tick_show")],text:!!axisShow&&params.config["".concat(prefix,"_tick_text_show")]},$g=null;// // get the axis' tick position configuration
g.each((function(){var g=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this),scale0=this.__chart__||scale,scale1=helper.copyScale();$g=g,this.__chart__=scale1,config.tickOffset=params.isCategory?Math.ceil((scale1(1)-scale1(0))/2):0;
// update selection - data join
var path=g.selectAll(".domain").data([0]);// enter + update selection
if(path.enter().append("path").attr("class","domain").merge(helper.transitionise(path)).attr("d",(function(){var outerTickSized=config.outerTickSize*sign;return isTopBottom?"M".concat(range[0],",").concat(outerTickSized,"V0H").concat(range[1],"V").concat(outerTickSized):"M".concat(outerTickSized,",").concat(range[0],"H0V").concat(range[1],"H").concat(outerTickSized)})),tickShow.tick||tickShow.text){
// count of tick data in array
var ticks=config.tickValues||helper.generateTicks(scale1,isLeftRight),tick=g.selectAll(".tick").data(ticks,scale1),tickEnter=tick.enter().insert("g",".domain").attr("class","tick").style("opacity","1"),tickExit=tick.exit().remove();// update selection
tick=tickEnter.merge(tick),tickShow.tick&&tickEnter.append("line"),tickShow.text&&tickEnter.append("text");var sizeFor1Char=AxisRendererHelper_AxisRendererHelper.getSizeFor1Char(tick),counts=[],tspan=tick.select("text").selectAll("tspan").data((function(d,index){var split=params.tickMultiline?splitTickText(d,scale1,ticks,isLeftRight,sizeFor1Char.w):isArray(helper.textFormatted(d))?helper.textFormatted(d).concat():[helper.textFormatted(d)];return counts[index]=split.length,split.map((function(splitted){return{index:index,splitted:splitted}}))}));tspan.exit().remove(),tspan=tspan.enter().append("tspan").merge(tspan).text((function(d){return d.splitted})),tspan.attr("x",isTopBottom?0:tickLength*sign).attr("dx",function(){var dx=0;return/(top|bottom)/.test(orient)&&rotate&&(dx=8*Math.sin(Math.PI*(rotate/180))*("top"===orient?-1:1)),dx+(tickTextPos.x||0)}()).attr("dy",(function(d,i){var dy=0;return"top"!==orient&&(dy=sizeFor1Char.h,0===i&&(dy=isLeftRight?-((counts[d.index]-1)*(sizeFor1Char.h/2)-3):0===tickTextPos.y?".71em":0)),isNumber(dy)&&tickTextPos.y?dy+tickTextPos.y:dy||".71em"}));var lineUpdate=tick.select("line"),textUpdate=tick.select("text");
// Append <title> for tooltip display
if(tickEnter.select("line").attr("".concat(axisPx,"2"),innerTickSize*sign),tickEnter.select("text").attr(axisPx,tickLength*sign),ctx.setTickLineTextPosition(lineUpdate,textUpdate),params.tickTitle){var title=textUpdate.select("title");(title.empty()?textUpdate.append("title"):title).text((function(index){return params.tickTitle[index]}))}if(scale1.bandwidth){var x=scale1,dx=x.bandwidth()/2;scale0=function(d){return x(d)+dx},scale1=scale0}else scale0.bandwidth?scale0=scale1:tickTransform(tickExit,scale1);tickTransform(tickEnter,scale0),tickTransform(helper.transitionise(tick).style("opacity","1"),scale1)}})),this.g=$g}
/**
     * Get tick x/y coordinate
     * @return {{x: number, y: number}}
     * @private
     */},{key:"getTickXY",value:function(){var config=this.config,pos={x:0,y:0};return this.params.isCategory&&(pos.x=config.tickCentered?0:config.tickOffset,pos.y=config.tickCentered?config.tickOffset:0),pos
/**
     * Get tick size
     * @param d
     * @return {number}
     * @private
     */}},{key:"getTickSize",value:function(d){var scale=this.helper.scale,config=this.config,innerTickSize=config.innerTickSize,range=config.range,tickPosition=scale(d)+(config.tickCentered?0:config.tickOffset);return range[0]<tickPosition&&tickPosition<range[1]?innerTickSize:0}
/**
     * Set tick's line & text position
     * @param lineUpdate
     * @param textUpdate
     * @param scale
     * @private
     */},{key:"setTickLineTextPosition",value:function(lineUpdate,textUpdate){var tickPos=this.getTickXY(),_this$config=this.config,innerTickSize=_this$config.innerTickSize,orient=_this$config.orient,tickLength=_this$config.tickLength,tickOffset=_this$config.tickOffset,rotate=this.params.tickTextRotate,textAnchorForText=function(r){var value=["start","end"];return"top"===orient&&value.reverse(),r?r>0?value[0]:value[1]:"middle"},textTransform=function(r){return r?"rotate(".concat(r,")"):null},yForText=function(r){var r2=r/("bottom"===orient?15:23);return r?11.5-2.5*r2*(r>0?1:-1):tickLength};"bottom"===orient?(lineUpdate.attr("x1",tickPos.x).attr("x2",tickPos.x).attr("y2",this.getTickSize.bind(this)),textUpdate.attr("x",0).attr("y",yForText(rotate)).style("text-anchor",textAnchorForText(rotate)).attr("transform",textTransform(rotate))):"top"===orient?(lineUpdate.attr("x2",0).attr("y2",-innerTickSize),textUpdate.attr("x",0).attr("y",2*-yForText(rotate)).style("text-anchor",textAnchorForText(rotate)).attr("transform",textTransform(rotate))):"left"===orient?(lineUpdate.attr("x2",-innerTickSize).attr("y1",tickPos.y).attr("y2",tickPos.y),textUpdate.attr("x",-tickLength).attr("y",tickOffset).style("text-anchor","end")):"right"===orient&&(lineUpdate.attr("x2",innerTickSize).attr("y2",0),textUpdate.attr("x",tickLength).attr("y",0).style("text-anchor","start"))}// this should be called only when category axis
},{key:"splitTickText",value:function(d,scale,ticks,isLeftRight,charWidth){function split(splitted,text){for(var subtext,spaceIndex,textWidth,i=1;i<text.length;i++)// if text width gets over tick width, split by space index or current index
if(" "===text.charAt(i)&&(spaceIndex=i),subtext=text.substr(0,i+1),textWidth=charWidth*subtext.length,tickWidth<textWidth)return split(splitted.concat(text.substr(0,spaceIndex||i)),text.slice(spaceIndex?spaceIndex+1:i));return splitted.concat(text)}var params=this.params,tickText=this.helper.textFormatted(d),splitted=isString(tickText)&&tickText.indexOf("\n")>-1?tickText.split("\n"):[];if(splitted.length)return splitted;if(isArray(tickText))return tickText;var tickWidth=params.tickWidth;return(!tickWidth||tickWidth<=0)&&(tickWidth=isLeftRight?95:params.isCategory?Math.ceil(scale(ticks[1])-scale(ticks[0]))-12:110),split(splitted,tickText+"")}},{key:"scale",value:function(x){return arguments.length?(this.helper.scale=x,this):this.helper.scale}},{key:"orient",value:function(x){return arguments.length?(this.config.orient=x in{top:1,right:1,bottom:1,left:1}?x+"":"bottom",this):this.config.orient}},{key:"tickFormat",value:function(format){return arguments.length?(this.config.tickFormat=format,this):this.config.tickFormat}},{key:"tickCentered",value:function(isCentered){var config=this.config;return arguments.length?(config.tickCentered=isCentered,this):config.tickCentered}
/**
     * Return tick's offset value.
     * The value will be set for 'category' axis type.
     * @return {number}
     * @private
     */},{key:"tickOffset",value:function(){return this.config.tickOffset}
/**
     * Get tick interval count
     * @private
     * @param {Number} size Total data size
     * @return {number}
     */},{key:"tickInterval",value:function(size){var interval;if(this.params.isCategory)interval=2*this.config.tickOffset;else{var length=this.g.select("path.domain").node().getTotalLength()-2*this.config.outerTickSize;interval=length/(size||this.g.selectAll("line").size())}return interval===1/0?0:interval}},{key:"ticks",value:function(){for(var config=this.config,_len=arguments.length,args=Array(_len),_key=0;_key<_len;_key++)args[_key]=arguments[_key];return args.length?(config.tickArguments=toArray(args),this):config.tickArguments}},{key:"tickCulling",value:function(culling){var config=this.config;return arguments.length?(config.tickCulling=culling,this):config.tickCulling}},{key:"tickValues",value:function(x){var _this=this,config=this.config;if(isFunction(x))config.tickValues=function(){return x(_this.helper.scale.domain())};else{if(!arguments.length)return config.tickValues;config.tickValues=x}return this}},{key:"setTransition",value:function(t){return this.config.transition=t,this}}]),AxisRenderer}(),isHorizontal=function($$,forHorizontal){var isRotated=$$.config.axis_rotated;return forHorizontal?isRotated:!isRotated},getAxisClassName=function(id){return"".concat(config_classes.axis," ").concat(config_classes["axis".concat(capitalize(id))])},Axis_Axis=function(){function Axis(owner){_classCallCheck(this,Axis),this.owner=owner,this.setOrient()}return _createClass(Axis,[{key:"init",value:function(){var _this=this,$$=this.owner,config=$$.config,isRotated=config.axis_rotated,main=$$.main,target=["x","y"];config.axis_y2_show&&target.push("y2"),$$.axesList={},target.forEach((function(v){var classAxis=getAxisClassName(v),classLabel=config_classes["axis".concat(v.toUpperCase(),"Label")];$$.axes[v]=main.append("g").attr("class",classAxis).attr("clip-path",(function(){var res=null;return"x"===v?res=$$.clipPathForXAxis:"y"===v&&config.axis_y_inner&&(res=$$.clipPathForYAxis),res})).attr("transform",$$.getTranslate(v)).style("visibility",config["axis_".concat(v,"_show")]?"visible":"hidden"),$$.axes[v].append("text").attr("class",classLabel).attr("transform",["rotate(-90)",null]["x"===v?+!isRotated:+isRotated]).style("text-anchor",(function(){return _this.textAnchorForAxisLabel(v)})),_this.generateAxes(v)}))}
/**
     * Set axis orient according option value
     * @private
     */},{key:"setOrient",value:function(){var $$=this.owner,config=$$.config,isRotated=config.axis_rotated,yInner=config.axis_y_inner,y2Inner=config.axis_y2_inner;$$.xOrient=isRotated?"left":"bottom",$$.yOrient=isRotated?yInner?"top":"bottom":yInner?"right":"left",$$.y2Orient=isRotated?y2Inner?"bottom":"top":y2Inner?"left":"right",$$.subXOrient=isRotated?"left":"bottom"}
/**
     * Generate axes
     * It's used when axis' axes option is set
     * @param {String} id Axis id
     * @private
     */},{key:"generateAxes",value:function(id){var d3Axis,$$=this.owner,config=$$.config,axes=[],axesConfig=config["axis_".concat(id,"_axes")],isRotated=config.axis_rotated;"x"===id?d3Axis=isRotated?external_commonjs_d3_axis_commonjs2_d3_axis_amd_d3_axis_root_d3_["axisLeft"]:external_commonjs_d3_axis_commonjs2_d3_axis_amd_d3_axis_root_d3_["axisBottom"]:"y"===id?d3Axis=isRotated?external_commonjs_d3_axis_commonjs2_d3_axis_amd_d3_axis_root_d3_["axisBottom"]:external_commonjs_d3_axis_commonjs2_d3_axis_amd_d3_axis_root_d3_["axisLeft"]:"y2"===id&&(d3Axis=isRotated?external_commonjs_d3_axis_commonjs2_d3_axis_amd_d3_axis_root_d3_["axisTop"]:external_commonjs_d3_axis_commonjs2_d3_axis_amd_d3_axis_root_d3_["axisRight"]),axesConfig.length&&axesConfig.forEach((function(v){var tick=v.tick||{},scale=$$[id].copy();v.domain&&scale.domain(v.domain),axes.push(d3Axis(scale).ticks(tick.count).tickFormat(tick.format||function(x){return x}).tickValues(tick.values).tickSizeOuter(!1===tick.outer?0:6))})),$$.axesList[id]=axes}
/**
     * Update axes nodes
     * @private
     */},{key:"updateAxes",value:function(){var $$=this.owner,config=$$.config;Object.keys($$.axesList).forEach((function(id){var axesConfig=config["axis_".concat(id,"_axes")],scale=$$[id].copy(),range=scale.range();$$.axesList[id].forEach((function(v,i){var axisRange=v.scale().range();// adjust range value with the current
// https://github.com/naver/billboard.js/issues/859
range.every((function(v,i){return v===axisRange[i]}))||v.scale().range(range);var className="".concat(getAxisClassName(id),"-").concat(i+1),g=$$.main.select(".".concat(className.replace(/\s/,".")));g.empty()?g=$$.main.append("g").attr("class",className).style("visibility",config["axis_".concat(id,"_show")]?"visible":"hidden").call(v):(axesConfig[i].domain&&scale.domain(axesConfig[i].domain),$$.xAxis.helper.transitionise(g).call(v.scale(scale))),g.attr("transform",$$.getTranslate(id,i+1))}))}))}// called from : updateScales() & getMaxTickWidth()
},{key:"getAxis",value:function(name,scale,outerTick,noTransition,noTickTextRotate){var $$=this.owner,config=$$.config,isX=/^(x|subX)$/.test(name),type=isX?"x":name,isCategory=isX&&$$.isCategorized(),orient=$$["".concat(name,"Orient")],tickFormat=isX?$$.xAxisTickFormat:config["axis_".concat(name,"_tick_format")],tickTextRotate=noTickTextRotate?0:$$.getAxisTickRotate(type),tickValues=isX?$$.xAxisTickValues:$$["".concat(name,"AxisTickValues")],axisParams=mergeObj({outerTick:outerTick,noTransition:noTransition,config:config,name:name,tickTextRotate:tickTextRotate},isX&&{isCategory:isCategory,tickMultiline:config.axis_x_tick_multiline,tickWidth:config.axis_x_tick_width,tickTitle:isCategory&&config.axis_x_tick_tooltip&&$$.api.categories(),orgXScale:$$.x});isX||(axisParams.tickStepSize=config["axis_".concat(type,"_tick_stepSize")]);var axis=new AxisRenderer_AxisRenderer(axisParams).scale(isX&&$$.zoomScale||scale).orient(orient);isX&&$$.isTimeSeries()&&tickValues&&!isFunction(tickValues)?tickValues=tickValues.map((function(v){return $$.parseDate(v)})):!isX&&$$.isTimeSeriesY()&&(axis.ticks(config.axis_y_tick_time_value),tickValues=null),tickValues&&axis.tickValues(tickValues),axis.tickFormat(tickFormat||!isX&&$$.isStackNormalized()&&function(x){return"".concat(x,"%")}),isCategory&&(axis.tickCentered(config.axis_x_tick_centered),isEmpty(config.axis_x_tick_culling)&&(config.axis_x_tick_culling=!1));var tickCount=config["axis_".concat(type,"_tick_count")];return tickCount&&axis.ticks(tickCount),axis}},{key:"updateXAxisTickValues",value:function(targets,axis){var values,$$=this.owner,config=$$.config,fit=config.axis_x_tick_fit,count=config.axis_x_tick_count;return(fit||count&&fit)&&(values=$$.mapTargetsToUniqueXs(targets),$$.isCategorized()&&count>values.length&&(count=values.length),values=this.generateTickValues(values,count,$$.isTimeSeries())),axis?axis.tickValues(values):$$.xAxis&&($$.xAxis.tickValues(values),$$.subXAxis.tickValues(values)),values}},{key:"getId",value:function(id){var config=this.owner.config;return id in config.data_axes?config.data_axes[id]:"y"}},{key:"getXAxisTickFormat",value:function(){var format,$$=this.owner,config=$$.config,tickFormat=config.axis_x_tick_format,isTimeSeries=$$.isTimeSeries(),isCategorized=$$.isCategorized();return tickFormat?isFunction(tickFormat)?format=tickFormat:isTimeSeries&&(format=function(date){return date?$$.axisTimeFormat(tickFormat)(date):""}):format=isTimeSeries?$$.defaultAxisTimeFormat:isCategorized?$$.categoryName:function(v){return v<0?v.toFixed(0):v},isFunction(format)?function(v){return format.apply($$,isCategorized?[v,$$.categoryName(v)]:[v])}:format}},{key:"getTickValues",value:function(id){var $$=this.owner,tickValues=$$.config["axis_".concat(id,"_tick_values")],axis=$$["".concat(id,"Axis")];return(isFunction(tickValues)?tickValues():tickValues)||(axis?axis.tickValues():void 0)}},{key:"getLabelOptionByAxisId",value:function(id){return this.owner.config["axis_".concat(id,"_label")]}},{key:"getLabelText",value:function(id){var option=this.getLabelOptionByAxisId(id);return isString(option)?option:option?option.text:null}},{key:"setLabelText",value:function(id,text){var $$=this.owner,config=$$.config,option=this.getLabelOptionByAxisId(id);isString(option)?config["axis_".concat(id,"_label")]=text:option&&(option.text=text)}},{key:"getLabelPosition",value:function(id,defaultPosition){var isRotated=this.owner.config.axis_rotated,option=this.getLabelOptionByAxisId(id),position=isObjectType(option)&&option.position?option.position:defaultPosition[+!isRotated],has=function(v){return!!~position.indexOf(v)};return{isInner:has("inner"),isOuter:has("outer"),isLeft:has("left"),isCenter:has("center"),isRight:has("right"),isTop:has("top"),isMiddle:has("middle"),isBottom:has("bottom")}}},{key:"getAxisLabelPosition",value:function(id){return this.getLabelPosition(id,"x"===id?["inner-top","inner-right"]:["inner-right","inner-top"])}},{key:"getLabelPositionById",value:function(id){return this.getAxisLabelPosition(id)}},{key:"xForAxisLabel",value:function(id){var $$=this.owner,position=this.getAxisLabelPosition(id),x=position.isMiddle?-$$.height/2:0;return isHorizontal($$,"x"!==id)?x=position.isLeft?0:position.isCenter?$$.width/2:$$.width:position.isBottom&&(x=-$$.height),x}},{key:"dxForAxisLabel",value:function(id){var $$=this.owner,position=this.getAxisLabelPosition(id),dx=position.isBottom?"0.5em":"0";return isHorizontal($$,"x"!==id)?dx=position.isLeft?"0.5em":position.isRight?"-0.5em":"0":position.isTop&&(dx="-0.5em"),dx}},{key:"textAnchorForAxisLabel",value:function(id){var $$=this.owner,position=this.getAxisLabelPosition(id),anchor=position.isMiddle?"middle":"end";return isHorizontal($$,"x"!==id)?anchor=position.isLeft?"start":position.isCenter?"middle":"end":position.isBottom&&(anchor="start"),anchor}},{key:"dyForAxisLabel",value:function(id){var dy,$$=this.owner,config=$$.config,isRotated=config.axis_rotated,isInner=this.getAxisLabelPosition(id).isInner,tickRotate=config["axis_".concat(id,"_tick_rotate")]?$$.getHorizontalAxisHeight(id):0,maxTickWidth=this.getMaxTickWidth(id);if("x"===id){var xHeight=config.axis_x_height;dy=isRotated?isInner?"1.2em":-25-maxTickWidth:isInner?"-0.5em":xHeight?xHeight-10:tickRotate?tickRotate-10:"3em"}else dy={y:["-0.5em",10,"3em","1.2em",10],y2:["1.2em",-20,"-2.2em","-0.5em",15]}[id],dy=isRotated?isInner?dy[0]:tickRotate?tickRotate*("y2"===id?-1:1)-dy[1]:dy[2]:isInner?dy[3]:(dy[4]+(config["axis_".concat(id,"_inner")]?0:maxTickWidth+dy[4]))*("y"===id?-1:1);return dy}},{key:"getMaxTickWidth",value:function(id,withoutRecompute){var $$=this.owner,config=$$.config,currentTickMax=$$.currentMaxTickWidths[id],maxWidth=0;if(withoutRecompute||!config["axis_".concat(id,"_show")]||0===$$.filterTargetsToShow().length)return currentTickMax.size;if($$.svg){var isYAxis=/^y2?$/.test(id),targetsToShow=$$.filterTargetsToShow($$.data.targets),scale=$$[id].copy().domain($$["get".concat(isYAxis?"Y":"X","Domain")](targetsToShow,id)),domain=scale.domain();
// do not compute if domain is same
if(domain[0]===domain[1]&&domain.every((function(v){return v>0}))||isArray(currentTickMax.domain)&&currentTickMax.domain[0]===currentTickMax.domain[1])return currentTickMax.size;currentTickMax.domain=domain;var axis=this.getAxis(id,scale,!1,!1,!0),tickCount=config["axis_".concat(id,"_tick_count")],tickValues=config["axis_".concat(id,"_tick_values")];!tickValues&&tickCount&&axis.tickValues(this.generateTickValues(domain,tickCount,isYAxis?$$.isTimeSeriesY():$$.isTimeSeries())),isYAxis||this.updateXAxisTickValues(targetsToShow,axis);var dummy=$$.selectChart.append("svg").style("visibility","hidden").style("position","fixed").style("top","0px").style("left","0px");axis.create(dummy),dummy.selectAll("text").each((function(d,i){var currentTextWidth=this.getBoundingClientRect().width;maxWidth=Math.max(maxWidth,currentTextWidth),"x"===id&&($$.currentMaxTickWidths.x.ticks[i]=currentTextWidth)})),dummy.remove()}return maxWidth>0&&(currentTickMax.size=maxWidth),currentTickMax.size}},{key:"getXAxisTickTextY2Overflow",value:function(defaultPadding){var $$=this.owner,config=$$.config,xAxisTickRotate=$$.getAxisTickRotate("x");if(($$.isCategorized()||$$.isTimeSeries())&&config.axis_x_tick_fit&&!config.axis_x_tick_culling&&!config.axis_x_tick_multiline&&xAxisTickRotate>0&&xAxisTickRotate<90){var widthWithoutCurrentPaddingLeft=$$.currentWidth-$$.getCurrentPaddingLeft(),maxOverflow=this.getXAxisTickMaxOverflow(xAxisTickRotate,widthWithoutCurrentPaddingLeft-defaultPadding),xAxisTickTextY2Overflow=Math.max(0,maxOverflow)+defaultPadding;
// for display inconsistencies between browsers
return Math.min(xAxisTickTextY2Overflow,widthWithoutCurrentPaddingLeft/2)}return 0}},{key:"getXAxisTickMaxOverflow",value:function(xAxisTickRotate,widthWithoutCurrentPaddingLeft){for(var $$=this.owner,config=$$.config,isTimeSeries=$$.isTimeSeries(),tickTextWidths=$$.currentMaxTickWidths.x.ticks,tickCount=tickTextWidths.length,_this$x$padding=this.x.padding,left=_this$x$padding.left,right=_this$x$padding.right,maxOverflow=0,remaining=tickCount-(isTimeSeries&&config.axis_x_tick_fit?.5:0),i=0;i<tickCount;i++){var tickIndex=i+1,rotatedTickTextWidth=Math.cos(Math.PI*xAxisTickRotate/180)*tickTextWidths[i],ticksBeforeTickText=tickIndex-(isTimeSeries?1:.5)+left;
// Skip ticks if there are no ticks before them
if(!(ticksBeforeTickText<=0)){var tickLength=(widthWithoutCurrentPaddingLeft-rotatedTickTextWidth)/ticksBeforeTickText;maxOverflow=Math.max(maxOverflow,rotatedTickTextWidth-tickLength/2-((remaining-tickIndex)*tickLength+right*tickLength))}}var tickOffset=0;if(!isTimeSeries){var scale=Object(external_commonjs_d3_scale_commonjs2_d3_scale_amd_d3_scale_root_d3_["scaleLinear"])().domain([-1*left,$$.getXDomainMax($$.data.targets)+1+right]).range([0,widthWithoutCurrentPaddingLeft-maxOverflow]);tickOffset=Math.ceil((scale(1)-scale(0))/2)}return maxOverflow+tickOffset}
/**
     * Get x Axis padding
     * @param {Number} tickCount Tick count
     * @return {Object} Padding object values with 'left' & 'right' key
     * @private
     */},{key:"getXAxisPadding",value:function(tickCount){var $$=this.owner,padding=$$.config.axis_x_padding;if(isEmpty(padding)?padding={left:0,right:0}:(padding.left=padding.left||0,padding.right=padding.right||0),$$.isTimeSeries()){var firstX=+$$.getXDomainMin($$.data.targets),lastX=+$$.getXDomainMax($$.data.targets),timeDiff=lastX-firstX,range=timeDiff+padding.left+padding.right,relativeTickWidth=timeDiff/tickCount/range,left=padding.left/range/relativeTickWidth||0,_right=padding.right/range/relativeTickWidth||0;padding={left:left,right:_right}}return padding}},{key:"updateLabels",value:function(withTransition){var _this2=this,$$=this.owner,labels={x:$$.main.select(".".concat(config_classes.axisX," .").concat(config_classes.axisXLabel)),y:$$.main.select(".".concat(config_classes.axisY," .").concat(config_classes.axisYLabel)),y2:$$.main.select(".".concat(config_classes.axisY2," .").concat(config_classes.axisY2Label))};Object.keys(labels).filter((function(id){return!labels[id].empty()})).forEach((function(v){var node=labels[v];(withTransition?node.transition():node).attr("x",(function(){return _this2.xForAxisLabel(v)})).attr("dx",(function(){return _this2.dxForAxisLabel(v)})).attr("dy",(function(){return _this2.dyForAxisLabel(v)})).text((function(){return _this2.getLabelText(v)}))}))}},{key:"getPadding",value:function(padding,key,defaultValue,domainLength){var p=isNumber(padding)?padding:padding[key];return isValue(p)?this.convertPixelsToAxisPadding(p,domainLength):defaultValue}},{key:"convertPixelsToAxisPadding",value:function(pixels,domainLength){var $$=this.owner,length=$$.config.axis_rotated?$$.width:$$.height;return domainLength*(pixels/length)}},{key:"generateTickValues",value:function(values,tickCount,forTimeSeries){var tickValues=values;if(tickCount){var targetCount=isFunction(tickCount)?tickCount():tickCount;// compute ticks according to tickCount
if(1===targetCount)tickValues=[values[0]];else if(2===targetCount)tickValues=[values[0],values[values.length-1]];else if(targetCount>2){var tickValue,isCategorized=this.owner.isCategorized(),count=targetCount-2,start=values[0],end=values[values.length-1];tickValues=[start];for(var i=0;i<count;i++)tickValue=+start+(end-start)/(count+1)*(i+1),tickValues.push(forTimeSeries?new Date(tickValue):isCategorized?Math.round(tickValue):tickValue);tickValues.push(end)}}return forTimeSeries||(tickValues=tickValues.sort((function(a,b){return a-b}))),tickValues}},{key:"generateTransitions",value:function(duration){var $$=this.owner,axes=$$.axes,_map=["x","y","y2","subx"].map((function(v){var axis=axes[v];return axis&&duration&&(axis=axis.transition().duration(duration)),axis})),_map2=_slicedToArray(_map,4),axisX=_map2[0],axisY=_map2[1],axisY2=_map2[2],axisSubX=_map2[3];return{axisX:axisX,axisY:axisY,axisY2:axisY2,axisSubX:axisSubX}}},{key:"redraw",value:function(transitions,isHidden,isInit){var $$=this.owner,opacity=isHidden?"0":"1";["x","y","y2","subX"].forEach((function(id){var axis=$$["".concat(id,"Axis")];axis&&(!isInit&&(axis.config.withoutTransition=!$$.config.transition_duration),$$.axes[id.toLowerCase()].style("opacity",opacity),axis.create(transitions["axis".concat(capitalize(id))]))})),this.updateAxes()}
/**
     * Redraw axis
     * @param {Object} targetsToShow targets data to be shown
     * @param {Object} wth
     * @param {Ojbect} transitions
     * @param {Object} flow
     * @private
     */},{key:"redrawAxis",value:function(targetsToShow,wth,transitions,flow,isInit){var xDomainForZoom,_this3=this,$$=this.owner,config=$$.config,hasZoom=!!$$.zoomScale;!hasZoom&&$$.isCategorized()&&0===targetsToShow.length&&$$.x.domain([0,$$.axes.x.selectAll(".tick").size()]),$$.x&&targetsToShow.length?(!hasZoom&&$$.updateXDomain(targetsToShow,wth.UpdateXDomain,wth.UpdateOrgXDomain,wth.TrimXDomain),!config.axis_x_tick_values&&this.updateXAxisTickValues(targetsToShow)):$$.xAxis&&($$.xAxis.tickValues([]),$$.subXAxis.tickValues([])),config.zoom_rescale&&!flow&&(xDomainForZoom=$$.x.orgDomain()),["y","y2"].forEach((function(key){var axis=$$[key];if(axis){var tickValues=config["axis_".concat(key,"_tick_values")],tickCount=config["axis_".concat(key,"_tick_count")];if(axis.domain($$.getYDomain(targetsToShow,key,xDomainForZoom)),!tickValues&&tickCount){var domain=axis.domain();$$["".concat(key,"Axis")].tickValues(_this3.generateTickValues(domain,domain.every((function(v){return 0===v}))?1:tickCount,$$.isTimeSeriesY()))}}})),this.redraw(transitions,$$.hasArcType(),isInit),this.updateLabels(wth.Transition),(wth.UpdateXDomain||wth.UpdateXAxis||wth.Y)&&targetsToShow.length&&this.setCulling(),wth.Y&&($$.subY&&$$.subY.domain($$.getYDomain(targetsToShow,"y")),$$.subY2&&$$.subY2.domain($$.getYDomain(targetsToShow,"y2")))}
/**
     * Set manual culling
     * @private
     */},{key:"setCulling",value:function(){var $$=this.owner,config=$$.config;["subx","x","y","y2"].forEach((function(type){var axis=$$.axes[type],id="subx"===type?"x":type,toCull=config["axis_".concat(id,"_tick_culling")];// subchart x axis should be aligned with x axis culling
if(axis&&toCull){var intervalForCulling,tickText=axis.selectAll(".tick text"),tickValues=sortValue(tickText.data()),tickSize=tickValues.length,cullingMax=config["axis_".concat(id,"_tick_culling_max")];if(tickSize){for(var i=1;i<tickSize;i++)if(tickSize/i<cullingMax){intervalForCulling=i;break}tickText.each((function(d){this.style.display=tickValues.indexOf(d)%intervalForCulling?"none":"block"}))}else tickText.style("display","block");// set/unset x_axis_tick_clippath
if("x"===type){var clipPath=$$.clipXAxisTickMaxWidth?$$.clipPathForXAxisTickTexts:null;$$.svg.selectAll(".".concat(config_classes.axisX," .tick text")).attr("clip-path",clipPath)}}}))}}]),Axis}(),ChartInternal_ChartInternal=function(){function ChartInternal(api){_classCallCheck(this,ChartInternal);var $$=this;$$.api=api,$$.config=$$.getOptions(),$$.data={},$$.cache={},$$.axes={},$$.rendered=!1}return _createClass(ChartInternal,[{key:"beforeInit",value:function(){var $$=this;$$.callPluginHook("$beforeInit"),callFn($$.config.onbeforeinit,$$,$$.api)}},{key:"afterInit",value:function(){var $$=this;$$.callPluginHook("$afterInit"),callFn($$.config.onafterinit,$$,$$.api)}},{key:"init",value:function(){var $$=this,config=$$.config;$$.initParams();var bindto={element:config.bindto,classname:"bb"};isObject(config.bindto)&&(bindto.element=config.bindto.element||"#chart",bindto.classname=config.bindto.classname||bindto.classname),$$.selectChart=isFunction(bindto.element.node)?config.bindto.element:Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(bindto.element||[]),$$.selectChart.empty()&&($$.selectChart=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(browser_doc.body.appendChild(browser_doc.createElement("div")))),$$.selectChart.html("").classed(bindto.classname,!0),$$.initToRender()}
/**
     * Initialize the rendering process
     * @param {Boolean} forced Force to render process
     * @private
     */},{key:"initToRender",value:function(forced){var $$=this,config=$$.config,target=$$.selectChart,isHidden=function(){return"none"===target.style("display")||"hidden"===target.style("visibility")},isLazy=config.render.lazy||isHidden(),MutationObserver=win.MutationObserver;if(isLazy&&MutationObserver&&!1!==config.render.observe&&!forced&&new MutationObserver((function(mutation,observer){isHidden()||(observer.disconnect(),!$$.rendered&&$$.initToRender(!0))})).observe(target.node(),{attributes:!0,attributeFilter:["class","style"]}),!isLazy||forced){var convertedData=$$.convertData(config,$$.initWithData);convertedData&&$$.initWithData(convertedData),$$.afterInit()}}},{key:"initParams",value:function(){var _this=this,$$=this,config=$$.config,isRotated=config.axis_rotated;$$.datetimeId="bb-".concat(+new Date),$$.initClip(),$$.dragStart=null,$$.dragging=!1,$$.flowing=!1,$$.cancelClick=!1,$$.mouseover=!1,$$.transiting=!1,$$.color=$$.generateColor(),$$.levelColor=$$.generateLevelColor(),$$.point=$$.generatePoint(),$$.extraLineClasses=$$.generateExtraLineClass(),$$.dataTimeFormat=config.data_xLocaltime?external_commonjs_d3_time_format_commonjs2_d3_time_format_amd_d3_time_format_root_d3_["timeParse"]:external_commonjs_d3_time_format_commonjs2_d3_time_format_amd_d3_time_format_root_d3_["utcParse"],$$.axisTimeFormat=config.axis_x_localtime?external_commonjs_d3_time_format_commonjs2_d3_time_format_amd_d3_time_format_root_d3_["timeFormat"]:external_commonjs_d3_time_format_commonjs2_d3_time_format_amd_d3_time_format_root_d3_["utcFormat"];var isDragZoom=$$.config.zoom_enabled&&"drag"===$$.config.zoom_enabled.type;$$.defaultAxisTimeFormat=function(d){var isZoomed=isDragZoom?_this.zoomScale:_this.zoomScale&&$$.x.orgDomain().toString()!==_this.zoomScale.domain().toString(),specifier=(d.getMilliseconds()?".%L":d.getSeconds()&&".:%S")||d.getMinutes()&&"%I:%M"||d.getHours()&&"%I %p"||1!==d.getDate()&&"%b %d"||isZoomed&&1===d.getDate()&&"%b'%y"||d.getMonth()&&"%-m/%-d"||"%Y";return $$.axisTimeFormat(specifier)(d)},$$.hiddenTargetIds=[],$$.hiddenLegendIds=[],$$.focusedTargetIds=[],$$.defocusedTargetIds=[],$$.isLegendRight="right"===config.legend_position,$$.isLegendInset="inset"===config.legend_position,$$.isLegendTop="top-left"===config.legend_inset_anchor||"top-right"===config.legend_inset_anchor,$$.isLegendLeft="top-left"===config.legend_inset_anchor||"bottom-left"===config.legend_inset_anchor,$$.legendStep=0,$$.legendItemWidth=0,$$.legendItemHeight=0,$$.currentMaxTickWidths={x:{size:0,ticks:[],domain:""},y:{size:0,domain:""},y2:{size:0,domain:""}},$$.rotated_padding_left=30,$$.rotated_padding_right=isRotated&&!config.axis_x_show?0:30,$$.rotated_padding_top=5,$$.withoutFadeIn={},$$.inputType=$$.convertInputType(),$$.axes.subx=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["selectAll"])([])}},{key:"initWithData",value:function(data){var $$=this,config=$$.config;if($$.axis=new Axis_Axis($$),config.zoom_enabled&&$$.initZoom(),$$.data.xs={},$$.data.targets=$$.convertDataToTargets(data),config.data_filter&&($$.data.targets=$$.data.targets.filter(config.data_filter)),config.data_hide&&$$.addHiddenTargetIds(!0===config.data_hide?$$.mapToIds($$.data.targets):config.data_hide),config.legend_hide&&$$.addHiddenLegendIds(!0===config.legend_hide?$$.mapToIds($$.data.targets):config.legend_hide),$$.updateSizes(),$$.updateScales(!0),$$.x&&($$.x.domain(sortValue($$.getXDomain($$.data.targets))),$$.subX.domain($$.x.domain()),$$.orgXDomain=$$.x.domain()),$$.y&&($$.y.domain($$.getYDomain($$.data.targets,"y")),$$.subY.domain($$.y.domain())),$$.y2&&($$.y2.domain($$.getYDomain($$.data.targets,"y2")),$$.subY2&&$$.subY2.domain($$.y2.domain())),$$.svg=$$.selectChart.append("svg").style("overflow","hidden").style("display","block"),config.interaction_enabled&&$$.inputType){var isTouch="touch"===$$.inputType;$$.svg.on(isTouch?"touchstart":"mouseenter",(function(){return callFn(config.onover,$$,$$.api)})).on(isTouch?"touchend":"mouseleave",(function(){return callFn(config.onout,$$,$$.api)}))}config.svg_classname&&$$.svg.attr("class",config.svg_classname),$$.defs=$$.svg.append("defs"),$$.clipChart=$$.appendClip($$.defs,$$.clipId),$$.clipXAxis=$$.appendClip($$.defs,$$.clipIdForXAxis),$$.clipYAxis=$$.appendClip($$.defs,$$.clipIdForYAxis),$$.clipGrid=$$.appendClip($$.defs,$$.clipIdForGrid),isFunction(config.color_tiles)&&$$.patterns&&$$.patterns.forEach((function(p){return $$.defs.append((function(){return p.node}))})),$$.updateSvgSize(),$$.bindResize();
// Define regions
var main=$$.svg.append("g").attr("transform",$$.getTranslate("main"));
// data.onmin/max callback
if($$.main=main,config.subchart_show&&$$.initSubchart(),$$.initTooltip&&$$.initTooltip(),$$.initLegend&&$$.initLegend(),$$.initTitle&&$$.initTitle(),config.data_empty_label_text&&main.append("text").attr("class","".concat(config_classes.text," ").concat(config_classes.empty)).attr("text-anchor","middle").attr("dominant-baseline","middle"),$$.initRegion(),config.clipPath||$$.axis.init(),main.append("g").attr("class",config_classes.chart).attr("clip-path",$$.clipPath),$$.callPluginHook("$init"),$$.initEventRect(),$$.initChartElements(),$$.initGrid(),main.insert("rect",config.zoom_privileged?null:"g.".concat(config_classes.regions)).attr("class",config_classes.zoomRect).attr("width",$$.width).attr("height",$$.height).style("opacity","0").on("dblclick.zoom",null),config.clipPath&&$$.axis.init(),$$.updateTargets($$.data.targets),$$.updateDimension(),callFn(config.oninit,$$,$$.api),$$.setBackground(),$$.redraw({withTransition:!1,withTransform:!0,withUpdateXDomain:!0,withUpdateOrgXDomain:!0,withTransitionForAxis:!1,initializing:!0}),config.data_onmin||config.data_onmax){var minMax=$$.getMinMaxData();callFn(config.data_onmin,$$,minMax.min),callFn(config.data_onmax,$$,minMax.max)}// export element of the chart
$$.api.element=$$.selectChart.node(),$$.rendered=!0}},{key:"initChartElements",value:function(){var $$=this;["Bar","Radar","Line","Bubble","Arc","Gauge","Pie"].forEach((function(v){$$["init".concat(v)]()})),notEmpty($$.config.data_labels)&&$$.initText()}},{key:"setChartElements",value:function(){var $$=this;$$.api.$={chart:$$.selectChart,svg:$$.svg,defs:$$.defs,main:$$.main,tooltip:$$.tooltip,legend:$$.legend,title:$$.title,grid:$$.grid,arc:$$.arcs,bar:{bars:$$.mainBar},line:{lines:$$.mainLine,areas:$$.mainArea,circles:$$.mainCircle},text:{texts:$$.mainText}}}
/**
     * Set background element/image
     * @private
     */},{key:"setBackground",value:function(){var $$=this,bg=$$.config.background;if(notEmpty(bg)){var element=$$.svg.select(".".concat(config_classes[$$.hasArcType()?"chart":"regions"])).insert(bg.imgUrl?"image":"rect",":first-child");bg.imgUrl?element.attr("href",bg.imgUrl):bg.color&&element.style("fill",bg.color),element.attr("class",bg["class"]||null).attr("width","100%").attr("height","100%")}}},{key:"smoothLines",value:function(el,type){"grid"===type&&el.each((function(){var g=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this);["x1","x2","y1","y2"].forEach((function(v){return g.attr(v,Math.ceil(g.attr(v)))}))}))}
/**
     * Update size values
     * @param {Boolean} isInit If is called at initialization
     * @private
     */},{key:"updateSizes",value:function(isInit){var $$=this;isInit||$$.setContainerSize();var config=$$.config,isRotated=config.axis_rotated,hasArc=$$.hasArcType(),legend={width:$$.legend?$$.getLegendWidth():0,height:$$.legend?$$.getLegendHeight():0},legendHeightForBottom=$$.isLegendRight||$$.isLegendInset?0:legend.height,xAxisHeight=isRotated||hasArc?0:$$.getHorizontalAxisHeight("x"),subchartXAxisHeight=config.subchart_axis_x_show&&config.subchart_axis_x_tick_text_show?xAxisHeight:30,subchartHeight=config.subchart_show&&!hasArc?config.subchart_size_height+subchartXAxisHeight:0;$$.margin=isRotated?{top:$$.getHorizontalAxisHeight("y2")+$$.getCurrentPaddingTop(),right:hasArc?0:$$.getCurrentPaddingRight(),bottom:$$.getHorizontalAxisHeight("y")+legendHeightForBottom+$$.getCurrentPaddingBottom(),left:subchartHeight+(hasArc?0:$$.getCurrentPaddingLeft())}:{top:4+$$.getCurrentPaddingTop(),
// for top tick text
right:hasArc?0:$$.getCurrentPaddingRight(),bottom:xAxisHeight+subchartHeight+legendHeightForBottom+$$.getCurrentPaddingBottom(),left:hasArc?0:$$.getCurrentPaddingLeft()},$$.margin2=isRotated?{top:$$.margin.top,right:NaN,bottom:20+legendHeightForBottom,left:$$.rotated_padding_left}:{top:$$.currentHeight-subchartHeight-legendHeightForBottom,right:NaN,bottom:subchartXAxisHeight+legendHeightForBottom,left:$$.margin.left},$$.margin3={top:0,right:NaN,bottom:0,left:0},$$.updateSizeForLegend&&$$.updateSizeForLegend(legend),$$.width=$$.currentWidth-$$.margin.left-$$.margin.right,$$.height=$$.currentHeight-$$.margin.top-$$.margin.bottom,$$.width<0&&($$.width=0),$$.height<0&&($$.height=0),$$.width2=isRotated?$$.margin.left-$$.rotated_padding_left-$$.rotated_padding_right:$$.width,$$.height2=isRotated?$$.height:$$.currentHeight-$$.margin2.top-$$.margin2.bottom,$$.width2<0&&($$.width2=0),$$.height2<0&&($$.height2=0),$$.arcWidth=$$.width-($$.isLegendRight?legend.width+10:0),$$.arcHeight=$$.height-($$.isLegendRight?0:10),$$.hasType("gauge")&&!config.gauge_fullCircle&&($$.arcHeight+=$$.height-$$.getGaugeLabelHeight()),$$.updateRadius&&$$.updateRadius(),$$.isLegendRight&&hasArc&&($$.margin3.left=$$.arcWidth/2+1.1*$$.radiusExpanded),!hasArc&&config.axis_x_show&&config.axis_x_tick_autorotate&&$$.updateXAxisTickClip()}
/**
     * Update targeted element with given data
     * @param {Object} targets Data object formatted as 'target'
     * @private
     */},{key:"updateTargets",value:function(targets){var $$=this;// Text
$$.updateTargetsForText(targets),$$.updateTargetsForBar(targets),$$.updateTargetsForLine(targets),$$.hasArcType(targets)&&($$.hasType("radar")?$$.updateTargetsForRadar(targets):$$.updateTargetsForArc(targets)),$$.updateTargetsForSubchart&&$$.updateTargetsForSubchart(targets),$$.showTargets()}
/**
     * Display targeted elements
     * @private
     */},{key:"showTargets",value:function(){var $$=this;$$.svg.selectAll(".".concat(config_classes.target)).filter((function(d){return $$.isTargetToShow(d.id)})).transition().duration($$.config.transition_duration).style("opacity","1")}},{key:"getWithOption",value:function(options){var withOptions={Y:!0,Subchart:!0,Transition:!0,EventRect:!0,Dimension:!0,TrimXDomain:!0,Transform:!1,UpdateXDomain:!1,UpdateOrgXDomain:!1,Legend:!1,UpdateXAxis:"UpdateXDomain",TransitionForExit:"Transition",TransitionForAxis:"Transition"};return Object.keys(withOptions).forEach((function(key){var defVal=withOptions[key];isString(defVal)&&(defVal=withOptions[defVal]),withOptions[key]=getOption(options,"with".concat(key),defVal)})),withOptions}},{key:"redraw",value:function(){var options=arguments.length>0&&void 0!==arguments[0]?arguments[0]:{},transitionsValue=arguments.length>1?arguments[1]:void 0,$$=this,main=$$.main,config=$$.config,targetsToShow=$$.filterTargetsToShow($$.data.targets),initializing=options.initializing,flow=options.flow,wth=$$.getWithOption(options),duration=wth.Transition?config.transition_duration:0,durationForExit=wth.TransitionForExit?duration:0,durationForAxis=wth.TransitionForAxis?duration:0,transitions=transitionsValue||$$.axis.generateTransitions(durationForAxis);$$.updateSizes(initializing),wth.Legend&&config.legend_show?$$.updateLegend($$.mapToIds($$.data.targets),options,transitions):wth.Dimension&&$$.updateDimension(!0),$$.axis.redrawAxis(targetsToShow,wth,transitions,flow,initializing),$$.updateCircleY(),config.data_empty_label_text&&main.select("text.".concat(config_classes.text,".").concat(config_classes.empty)).attr("x",$$.width/2).attr("y",$$.height/2).text(config.data_empty_label_text).style("display",targetsToShow.length?"none":null),$$.updateGrid(duration),$$.updateRegion(duration),$$.updateBar(durationForExit),$$.updateLine(durationForExit),$$.updateArea(durationForExit),$$.updateCircle(),$$.hasDataLabel()&&$$.updateText(durationForExit),$$.redrawTitle&&$$.redrawTitle(),$$.arcs&&$$.redrawArc(duration,durationForExit,wth.Transform),$$.radars&&$$.redrawRadar(durationForExit),$$.mainText&&main.selectAll(".".concat(config_classes.selectedCircles)).filter($$.isBarType.bind($$)).selectAll("circle").remove(),config.interaction_enabled&&!flow&&wth.EventRect&&$$.bindZoomEvent(),initializing&&$$.setChartElements(),$$.generateRedrawList(targetsToShow,flow,duration,wth.Subchart),$$.callPluginHook("$redraw",options,duration)}
/**
     * Generate redraw list
     * @param {Object} targets targets data to be shown
     * @param {Object} flow
     * @param {Object} duration
     * @param {Boolean} withSubchart whether or not to show subchart
     * @private
     */},{key:"generateRedrawList",value:function(targets,flow,duration,withSubchart){var $$=this,config=$$.config,shape=$$.getDrawShape();config.subchart_show&&$$.redrawSubchart(withSubchart,duration,shape);
// generate flow
var flowFn=flow&&$$.generateFlow({targets:targets,flow:flow,duration:flow.duration,shape:shape,xv:$$.xv.bind($$)}),isTransition=(duration||flowFn)&&$$.isTabVisible(),redrawList=$$.getRedrawList(shape,flow,flowFn,isTransition),afterRedraw=flow||config.onrendered?function(){flowFn&&flowFn(),callFn(config.onrendered,$$,$$.api)}:null;if(afterRedraw)// Only use transition when current tab is visible.
if(isTransition&&redrawList.length){
// Wait for end of transitions for callback
var waitForDraw=$$.generateWait();// transition should be derived from one transition
Object(external_commonjs_d3_transition_commonjs2_d3_transition_amd_d3_transition_root_d3_["transition"])().duration(duration).each((function(){redrawList.reduce((function(acc,t1){return acc.concat(t1)}),[]).forEach((function(t){return waitForDraw.add(t)}))})).call(waitForDraw,afterRedraw)}else $$.transiting||afterRedraw();// update fadein condition
$$.mapToIds($$.data.targets).forEach((function(id){$$.withoutFadeIn[id]=!0}))}
/**
     * Get the shape draw function
     * @return {Object}
     * @private
     */},{key:"getDrawShape",value:function(){var $$=this,isRotated=$$.config.axis_rotated,hasRadar=$$.hasType("radar"),shape={type:{},indices:{}};
// setup drawer - MEMO: these must be called after axis updated
if($$.hasTypeOf("Line")||$$.hasType("bubble")||$$.hasType("scatter")){var indices=$$.getShapeIndices($$.isLineType);if(shape.indices.line=indices,shape.type.line=$$.generateDrawLine?$$.generateDrawLine(indices,!1):void 0,$$.hasTypeOf("Area")){var _indices=$$.getShapeIndices($$.isAreaType);shape.indices.area=_indices,shape.type.area=$$.generateDrawArea?$$.generateDrawArea(_indices,!1):void 0}}if($$.hasType("bar")){var _indices2=$$.getShapeIndices($$.isBarType);shape.indices.bar=_indices2,shape.type.bar=$$.generateDrawBar?$$.generateDrawBar(_indices2):void 0}return shape.pos={xForText:$$.generateXYForText(shape.indices,!0),yForText:$$.generateXYForText(shape.indices,!1),
// generate circle x/y functions depending on updated params
cx:(hasRadar?$$.radarCircleX:isRotated?$$.circleY:$$.circleX).bind($$),cy:(hasRadar?$$.radarCircleY:isRotated?$$.circleX:$$.circleY).bind($$)},shape}},{key:"getRedrawList",value:function(shape,flow,flowFn,isTransition){var $$=this,config=$$.config,hasArcType=$$.hasArcType(),_shape$pos=shape.pos,cx=_shape$pos.cx,cy=_shape$pos.cy,xForText=_shape$pos.xForText,yForText=_shape$pos.yForText,list=[];if(!hasArcType){var _shape$type=shape.type,area=_shape$type.area,bar=_shape$type.bar,line=_shape$type.line;(config.grid_x_lines.length||config.grid_y_lines.length)&&list.push($$.redrawGrid(isTransition)),config.regions.length&&list.push($$.redrawRegion(isTransition)),$$.hasTypeOf("Line")&&(list.push($$.redrawLine(line,isTransition)),$$.hasTypeOf("Area")&&list.push($$.redrawArea(area,isTransition))),$$.hasType("bar")&&list.push($$.redrawBar(bar,isTransition)),flow||list.push($$.updateGridFocus())}return(!hasArcType||$$.hasType("radar"))&&(notEmpty(config.data_labels)&&list.push($$.redrawText(xForText,yForText,flow,isTransition)),list.push($$.redrawCircle(cx,cy,isTransition,flowFn))),list}},{key:"updateAndRedraw",value:function(){var transitions,options=arguments.length>0&&void 0!==arguments[0]?arguments[0]:{},$$=this,config=$$.config;options.withTransition=getOption(options,"withTransition",!0),options.withTransform=getOption(options,"withTransform",!1),options.withLegend=getOption(options,"withLegend",!1),options.withUpdateXDomain=!0,options.withUpdateOrgXDomain=!0,options.withTransitionForExit=!1,options.withTransitionForTransform=getOption(options,"withTransitionForTransform",options.withTransition),options.withLegend&&config.legend_show||(transitions=$$.axis.generateTransitions(options.withTransitionForAxis?config.transition_duration:0),$$.updateScales(),$$.updateSvgSize(),$$.transformAll(options.withTransitionForTransform,transitions)),$$.redraw(options,transitions)}},{key:"redrawWithoutRescale",value:function(){this.redraw({withY:!1,withSubchart:!1,withEventRect:!1,withTransitionForAxis:!1})}},{key:"isTimeSeries",value:function(){return"timeseries"===this.config.axis_x_type}},{key:"isCategorized",value:function(){return this.config.axis_x_type.indexOf("category")>=0||this.hasType("radar")}},{key:"isCustomX",value:function(){var $$=this,config=$$.config;return!$$.isTimeSeries()&&(config.data_x||notEmpty(config.data_xs))}},{key:"isTimeSeriesY",value:function(){return"timeseries"===this.config.axis_y_type}},{key:"getTranslate",value:function(target){var x,y,index=arguments.length>1&&void 0!==arguments[1]?arguments[1]:0,$$=this,config=$$.config,isRotated=config.axis_rotated,hasGauge=$$.hasType("gauge"),padding=0;if(index&&/^(x|y2?)$/.test(target)&&(padding=$$.getAxisSize(target)*index),"main"===target)x=asHalfPixel($$.margin.left),y=asHalfPixel($$.margin.top);else if("context"===target)x=asHalfPixel($$.margin2.left),y=asHalfPixel($$.margin2.top);else if("legend"===target)x=$$.margin3.left,y=$$.margin3.top+(hasGauge?10:0);else if("x"===target)x=isRotated?-padding:0,y=isRotated?0:$$.height+padding;else if("y"===target)x=isRotated?0:-padding,y=isRotated?$$.height+padding:0;else if("y2"===target)x=isRotated?0:$$.width+padding,y=isRotated?1-padding:0;else if("subx"===target)x=0,y=isRotated?0:$$.height2;else if("arc"===target)x=$$.arcWidth/2,y=$$.arcHeight/2;else if("radar"===target){var _$$$getRadarSize=$$.getRadarSize(),_$$$getRadarSize2=_slicedToArray(_$$$getRadarSize,1),width=_$$$getRadarSize2[0];x=$$.width/2-width,y=asHalfPixel($$.margin.top)}return"translate(".concat(x,", ").concat(y,")")}},{key:"initialOpacity",value:function(d){return null!==this.getBaseValue(d)&&this.withoutFadeIn[d.id]?"1":"0"}},{key:"initialOpacityForCircle",value:function(d){return null!==this.getBaseValue(d)&&this.withoutFadeIn[d.id]?this.opacityForCircle(d):"0"}},{key:"opacityForCircle",value:function(d){var opacity=this.config.point_show?"1":"0";return isValue(this.getBaseValue(d))?this.isBubbleType(d)||this.isScatterType(d)?"0.5":opacity:"0"}},{key:"opacityForText",value:function(){return this.hasDataLabel()?"1":"0"}
/**
     * Get the zoom or unzoomed scaled value
     * @param {Date|Number|Object} d Data value
     * @private
     */},{key:"xx",value:function(d){var $$=this,fn=$$.config.zoom_enabled&&$$.zoomScale?$$.zoomScale:this.x;return d?fn(isValue(d.x)?d.x:d):null}},{key:"xv",value:function(d){var $$=this,value=$$.getBaseValue(d);return $$.isTimeSeries()?value=$$.parseDate(value):$$.isCategorized()&&isString(value)&&(value=$$.config.axis_x_categories.indexOf(value)),Math.ceil($$.x(value))}},{key:"yv",value:function(d){var $$=this,yScale=d.axis&&"y2"===d.axis?$$.y2:$$.y;return Math.ceil(yScale($$.getBaseValue(d)))}},{key:"subxx",value:function(d){return d?this.subX(d.x):null}},{key:"transformMain",value:function(withTransition,transitions){var xAxis,yAxis,y2Axis,$$=this;transitions&&transitions.axisX?xAxis=transitions.axisX:(xAxis=$$.main.select(".".concat(config_classes.axisX)),withTransition&&(xAxis=xAxis.transition())),transitions&&transitions.axisY?yAxis=transitions.axisY:(yAxis=$$.main.select(".".concat(config_classes.axisY)),withTransition&&(yAxis=yAxis.transition())),transitions&&transitions.axisY2?y2Axis=transitions.axisY2:(y2Axis=$$.main.select(".".concat(config_classes.axisY2)),withTransition&&(y2Axis=y2Axis.transition())),(withTransition?$$.main.transition():$$.main).attr("transform",$$.getTranslate("main")),xAxis.attr("transform",$$.getTranslate("x")),yAxis.attr("transform",$$.getTranslate("y")),y2Axis.attr("transform",$$.getTranslate("y2")),$$.main.select(".".concat(config_classes.chartArcs)).attr("transform",$$.getTranslate("arc"))}},{key:"transformAll",value:function(withTransition,transitions){var $$=this;$$.transformMain(withTransition,transitions),$$.config.subchart_show&&$$.transformContext(withTransition,transitions),$$.legend&&$$.transformLegend(withTransition)}},{key:"updateSvgSize",value:function(){var $$=this,brush=$$.svg.select(".".concat(config_classes.brush," .overlay")),brushSize={width:0,height:0};brush.size()&&(brushSize.width=+brush.attr("width"),brushSize.height=+brush.attr("height")),$$.svg.attr("width",$$.currentWidth).attr("height",$$.currentHeight),$$.svg.selectAll(["#".concat($$.clipId),"#".concat($$.clipIdForGrid)]).select("rect").attr("width",$$.width).attr("height",$$.height),$$.svg.select("#".concat($$.clipIdForXAxis)).select("rect").attr("x",$$.getXAxisClipX.bind($$)).attr("y",$$.getXAxisClipY.bind($$)).attr("width",$$.getXAxisClipWidth.bind($$)).attr("height",$$.getXAxisClipHeight.bind($$)),$$.svg.select("#".concat($$.clipIdForYAxis)).select("rect").attr("x",$$.getYAxisClipX.bind($$)).attr("y",$$.getYAxisClipY.bind($$)).attr("width",$$.getYAxisClipWidth.bind($$)).attr("height",$$.getYAxisClipHeight.bind($$)),$$.svg.select("#".concat($$.clipIdForSubchart)).select("rect").attr("width",$$.width).attr("height",brushSize.height),$$.svg.select(".".concat(config_classes.zoomRect)).attr("width",$$.width).attr("height",$$.height)}},{key:"updateDimension",value:function(withoutAxis){var $$=this;withoutAxis||($$.xAxis&&$$.config.axis_rotated?($$.xAxis.create($$.axes.x),$$.subXAxis.create($$.axes.subx)):($$.yAxis&&$$.yAxis.create($$.axes.y),$$.y2Axis&&$$.y2Axis.create($$.axes.y2))),$$.updateScales(withoutAxis),$$.updateSvgSize(),$$.transformAll(!1)}},{key:"bindResize",value:function(){var $$=this,config=$$.config,resizeFunction=$$.generateResize(),list=[];list.push((function(){return callFn(config.onresize,$$,$$.api)})),config.resize_auto&&list.push((function(){return $$.api.flush(!1,!0)})),list.push((function(){return callFn(config.onresized,$$,$$.api)})),list.forEach((function(v){return resizeFunction.add(v)})),win.addEventListener("resize",$$.resizeFunction=resizeFunction)}},{key:"generateResize",value:function(){function callResizeFn(){callResizeFn.timeout&&(win.clearTimeout(callResizeFn.timeout),callResizeFn.timeout=null),callResizeFn.timeout=win.setTimeout((function(){fn.forEach((function(f){return f()}))}),200)}var fn=[];return callResizeFn.add=function(f){return fn.push(f)},callResizeFn.remove=function(f){return fn.splice(fn.indexOf(f),1)},callResizeFn}},{key:"endall",value:function(transition,callback){var n=0;transition.each((function(){return++n})).on("end",(function(){for(var _len=arguments.length,args=Array(_len),_key=0;_key<_len;_key++)args[_key]=arguments[_key];--n||callback.apply.apply(callback,[this].concat(args))}))}},{key:"generateWait",value:function(){var transitionsToWait=[],f=function(transition,callback){function loop(){for(var t,done=0,i=0;t=transitionsToWait[i];i++)if(!0===t||t.empty&&t.empty())done++;else try{t.transition()}catch(e){done++}timer&&clearTimeout(timer),done===transitionsToWait.length?callback&&callback():timer=setTimeout(loop,50)}var timer;loop()};return f.add=function(transition){isArray(transition)?transitionsToWait=transitionsToWait.concat(transition):transitionsToWait.push(transition)},f}},{key:"parseDate",value:function(date){var parsedDate,$$=this;return date instanceof Date?parsedDate=date:isString(date)?parsedDate=$$.dataTimeFormat($$.config.data_xFormat)(date):isNumber(date)&&!isNaN(date)&&(parsedDate=new Date(+date)),(!parsedDate||isNaN(+parsedDate))&&console&&console.error,parsedDate}},{key:"isTabVisible",value:function(){return!browser_doc.hidden}},{key:"convertInputType",value:function(){var $$=this,config=$$.config,isMobile=!1;
// https://developer.mozilla.org/en-US/docs/Web/HTTP/Browser_detection_using_the_user_agent#Mobile_Tablet_or_Desktop
if(/Mobi/.test(win.navigator.userAgent)&&config.interaction_inputType_touch){
// Some Edge desktop return true: https://developer.microsoft.com/en-us/microsoft-edge/platform/issues/20417074/
var hasTouchPoints=win.navigator&&"maxTouchPoints"in win.navigator&&win.navigator.maxTouchPoints>0,hasTouch="ontouchmove"in win||win.DocumentTouch&&browser_doc instanceof win.DocumentTouch;// Ref: https://github.com/Modernizr/Modernizr/blob/master/feature-detects/touchevents.js
// On IE11 with IE9 emulation mode, ('ontouchstart' in window) is returning true
isMobile=hasTouchPoints||hasTouch}var hasMouse=config.interaction_inputType_mouse&&!isMobile&&"onmouseover"in win;return(hasMouse?"mouse":isMobile&&"touch")||null}
/**
     * Call plugin hook
     * @param {String} phase The lifecycle phase
     * @private
     */},{key:"callPluginHook",value:function(phase){for(var _this2=this,_len2=arguments.length,args=Array(_len2>1?_len2-1:0),_key2=1;_key2<_len2;_key2++)args[_key2-1]=arguments[_key2];this.config.plugins.forEach((function(v){"$beforeInit"===phase&&(v.$$=_this2,_this2.api.plugins.push(v)),v[phase].apply(v,args)}))}}]),ChartInternal}(),Chart_Chart=function Chart(config){_classCallCheck(this,Chart);var $$=new ChartInternal_ChartInternal(this);
/**
   * Plugin instance array
   * @member {Array} plugins
   * @memberof Chart
   * @instance
   	 * @example
   *  var chart = bb.generate({
   *     ...
   *     plugins: [
   *        new bb.plugin.stanford({ ... }),
   *        new PluginA()
   *     ]
   *  });
   *
   *  chart.plugins; // [Stanford, PluginA] - instance array
   */
// bind "this" to nested API
this.plugins=[],this.internal=$$,$$.loadConfig(config),$$.beforeInit(config),$$.init(),function bindThis(fn,target,argThis){Object.keys(fn).forEach((function(key){target[key]=fn[key].bind(argThis),Object.keys(fn[key]).length&&bindThis(fn[key],target[key],argThis)}))}(Chart.prototype,this,this)},Options_Options=function Options(){return _classCallCheck(this,Options),{
/**
     * Specify the CSS selector or the element which the chart will be set to. D3 selection object can be specified also.<br>
     * If other chart is set already, it will be replaced with the new one (only one chart can be set in one element).
     * - **NOTE:** In case of element doesn't exist or not specified, will add a `<div>` element to the body.
     * @name bindto
     * @memberof Options
     * @property {String|HTMLElement|d3.selection} bindto=#chart Specify the element where chart will be drawn.
     * @property {String|HTMLElement|d3.selection} bindto.element=#chart Specify the element where chart will be drawn.
     * @property {String} [bindto.classname=bb] Specify the class name of bind element.<br>
     *     **NOTE:** When class name isn't `bb`, then you also need to update the default CSS to be rendered correctly.
     * @default #chart
     * @example
     * bindto: "#myContainer"
     *
     * // or HTMLElement
     * bindto: document.getElementById("myContainer")
     *
     * // or D3 selection object
     * bindto: d3.select("#myContainer")
     *
     * // or to change default classname
     * bindto: {
     *    element: "#chart",
     *    classname: "bill-board"  // ex) <div id='chart' class='bill-board'>
     * }
     */
bindto:"#chart",
/**
     * Set chart background.
     * @name background
     * @memberof Options
     * @property {String} background.class Specify the class name for background element.
     * @property {String} background.color Specify the fill color for background element.<br>**NOTE:** Will be ignored if `imgUrl` option is set.
     * @property {String} background.imgUrl Specify the image url string for background.
     * @see [Demo](https://naver.github.io/billboard.js/demo/#ChartOptions.Background)
     * @example
     * background: {
     *    class: "myClass",
     *    color: "red",
     *
     *    // Set image url for background.
     *    // If specified, 'color' option will be ignored.
     *    imgUrl: "https://naver.github.io/billboard.js/img/logo/billboard.js.svg",
     * }
     */
background:{},
/**
     * Set 'clip-path' attribute for chart element
     * - **NOTE:**
     *  > When is false, chart node element is positioned after the axis node in DOM tree hierarchy.
     *  > Is to make chart element positioned over axis element.
     * @name clipPath
     * @memberof Options
     * @type {Boolean}
     * @default true
     * @see [Demo](https://naver.github.io/billboard.js/demo/#ChartOptions.clipPath)
     * @example
     * // don't set 'clip-path' attribute
     * clipPath: false
     */
clipPath:!0,
/**
     * Set svg element's class name
     * @name svg
     * @memberof Options
     * @type {Object}
     * @property {String} [svg.classname] class name for svg element
     * @example
     * svg: {
              *   classname: "test_class"
     * }
     */
svg_classname:void 0,
/**
     * The desired size of the chart element.
     * If value is not specified, the width of the chart will be calculated by the size of the parent element it's appended to.
     * @name size
     * @memberof Options
     * @type {Object}
     * @property {Number} [size.width] width of the chart element
     * @property {Number} [size.height] height of the chart element
     * @see [Demo](https://naver.github.io/billboard.js/demo/#ChartOptions.ChartSize)
     * @example
     * size: {
              *   width: 640,
              *   height: 480
     * }
     */
size_width:void 0,size_height:void 0,
/**
     * The padding of the chart element.
     * @name padding
     * @memberof Options
     * @type {Object}
     * @property {Number} [padding.top] padding on the top of chart
     * @property {Number} [padding.right] padding on the right of chart
     * @property {Number} [padding.bottom] padding on the bottom of chart
     * @property {Number} [padding.left] padding on the left of chart
     * @example
     * padding: {
              *   top: 20,
              *   right: 20,
              *   bottom: 20,
              *   left: 20
     * }
     */
padding_left:void 0,padding_right:void 0,padding_top:void 0,padding_bottom:void 0,
/**
     * Set chart resize options
     * @name resize
     * @memberof Options
     * @type {Object}
     * @property {Boolean} [resize.auto=true] Set chart resize automatically on viewport changes.
     * @example
     *  resize: {
     *      auto: false
     *  }
     */
resize_auto:!0,
/**
     * Set zoom options
     * @name zoom
     * @memberof Options
     * @type {Object}
     * @property {Boolean} [zoom.enabled=false] Enable zooming.
     * @property {String} [zoom.enabled.type='wheel'] Set zoom interaction type.
     *  - **Available types:**
     *    - wheel
     *    - drag
     * @property {Boolean} [zoom.rescale=false] Enable to rescale after zooming.<br>
     *  If true set, y domain will be updated according to the zoomed region.
     * @property {Array} [zoom.extent=[1, 10]] Change zoom extent.
     * @property {Number|Date} [zoom.x.min] Set x Axis minimum zoom range
     * @property {Number|Date} [zoom.x.max] Set x Axis maximum zoom range
     * @property {Function} [zoom.onzoomstart=undefined] Set callback that is called when zooming starts.<br>
     *  Specified function receives the zoom event.
     * @property {Function} [zoom.onzoom=undefined] Set callback that is called when the chart is zooming.<br>
     *  Specified function receives the zoomed domain.
     * @property {Function} [zoom.onzoomend=undefined] Set callback that is called when zooming ends.<br>
     *  Specified function receives the zoomed domain.
     * @property {Boolean|Object} [zoom.resetButton=true] Set to display zoom reset button for 'drag' type zoom
     * @property {Function} [zoom.resetButton.onclick] Set callback when clicks the reset button. The callback will receive reset button element reference as argument.
     * @property {String} [zoom.resetButton.text='Reset Zoom'] Text value for zoom reset button.
     * @see [Demo:zoom](https://naver.github.io/billboard.js/demo/#Interaction.Zoom)
     * @see [Demo:drag zoom](https://naver.github.io/billboard.js/demo/#Interaction.DragZoom)
     * @example
     *  zoom: {
     *      enabled: {
              *          type: "drag"
              *      },
     *      rescale: true,
     *      extent: [1, 100]  // enable more zooming
     *      x: {
     *          min: -1,  // set min range
     *          max: 10  // set max range
     *      },
     *      onzoomstart: function(event) { ... },
     *      onzoom: function(domain) { ... },
     *      onzoomend: function(domain) { ... },
     *
     *      // show reset button when is zoomed-in
     *      resetButton: true,
     *
     *      resetButton: {
     *          // onclick callback when reset button is clicked
     *          onclick: function(button) {
     *            button; // Reset button element reference
     *            ...
     *          },
     *
     *          // customized text value for reset zoom button
     *          text: "Unzoom"
     *      }
     *  }
     */
zoom_enabled:void 0,zoom_extent:void 0,zoom_privileged:!1,zoom_rescale:!1,zoom_onzoom:void 0,zoom_onzoomstart:void 0,zoom_onzoomend:void 0,zoom_resetButton:!0,zoom_x_min:void 0,zoom_x_max:void 0,
/**
     * Interaction options
     * @name interaction
     * @memberof Options
     * @type {Object}
     * @property {Boolean} [interaction.enabled=true] Indicate if the chart should have interactions.<br>
     *     If `false` is set, all of interactions (showing/hiding tooltip, selection, mouse events, etc) will be disabled.
     * @property {Boolean} [interaction.brighten=true] Make brighter for the selected area (ex. 'pie' type data selected area)
     * @property {Boolean} [interaction.inputType.mouse=true] enable or disable mouse interaction
     * @property {Boolean} [interaction.inputType.touch=true] enable or disable  touch interaction
     * @property {Boolean|Number} [interaction.inputType.touch.preventDefault=false] enable or disable to call event.preventDefault on touchstart & touchmove event. It's usually used to prevent document scrolling.
     * @see [Demo: touch.preventDefault](https://naver.github.io/billboard.js/demo/#Interaction.PreventScrollOnTouch)
     * @example
     * interaction: {
              *    enabled: false,
              *    brighten: false,
              *    inputType: {
              *        mouse: true,
              *        touch: false
              *
              *        // or declare preventDefault explicitly.
              *        // In this case touch inputType is enabled by default
              *        touch: {
              *            preventDefault: true
              *
              *            // or threshold pixel value (pixel moved from touchstart to touchmove)
              *            preventDefault: 5
              *        }
              *    }
     * }
     */
interaction_enabled:!0,interaction_brighten:!0,interaction_inputType_mouse:!0,interaction_inputType_touch:{},
/**
     * Set a callback to execute when mouse/touch enters the chart.
     * @name onover
     * @memberof Options
     * @type {Function}
     * @default undefined
     * @example
     * // @param {Chart} ctx - Instance itself
     * onover: function(ctx) {
     *   ...
     * }
     */
onover:void 0,
/**
     * Set a callback to execute when mouse/touch leaves the chart.
     * @name onout
     * @memberof Options
     * @type {Function}
     * @default undefined
     * @example
     * // @param {Chart} ctx - Instance itself
     * onout: function(ctx) {
     *   ...
     * }
     */
onout:void 0,
/**
     * Set a callback to execute when user resizes the screen.
     * @name onresize
     * @memberof Options
     * @type {Function}
     * @default undefined
     * @example
     * // @param {Chart} ctx - Instance itself
     * onresize: function(ctx) {
     *   ...
     * }
     */
onresize:void 0,
/**
     * Set a callback to execute when screen resize finished.
     * @name onresized
     * @memberof Options
     * @type {Function}
     * @default undefined
     * @example
     * // @param {Chart} ctx - Instance itself
     * onresized: function(ctx) {
     *   ...
     * }
     */
onresized:void 0,
/**
     * Set a callback to execute before the chart is initialized
     * @name onbeforeinit
     * @memberof Options
     * @type {Function}
     * @default undefined
     * @example
     * // @param {Chart} ctx - Instance itself
     * onbeforeinit: function(ctx) {
     *   ...
     * }
     */
onbeforeinit:void 0,
/**
     * Set a callback to execute when the chart is initialized.
     * @name oninit
     * @memberof Options
     * @type {Function}
     * @default undefined
     * @example
     * // @param {Chart} ctx - Instance itself
     * oninit: function(ctx) {
     *   ...
     * }
     */
oninit:void 0,
/**
     * Set a callback to execute after the chart is initialized
     * @name onafterinit
     * @memberof Options
     * @type {Function}
     * @default undefined
     * @example
     * // @param {Chart} ctx - Instance itself
     * onafterinit: function(ctx) {
     *   ...
     * }
     */
onafterinit:void 0,
/**
     * Set a callback which is executed when the chart is rendered. Basically, this callback will be called in each time when the chart is redrawed.
     * @name onrendered
     * @memberof Options
     * @type {Function}
     * @default undefined
     * @example
     * // @param {Chart} ctx - Instance itself
     * onrendered: function(ctx) {
     *   ...
     * }
     */
onrendered:void 0,
/**
     * Set duration of transition (in milliseconds) for chart animation.<br><br>
     * - **NOTE:** If `0 `or `null` set, transition will be skipped. So, this makes initial rendering faster especially in case you have a lot of data.
     * @name transition
     * @memberof Options
     * @type {Object}
     * @property {Number} [transition.duration=350] duration in milliseconds
     * @example
     * transition: {
     *    duration: 500
     * }
     */
transition_duration:350,
/**
     * Specify the key of x values in the data.<br><br>
     * We can show the data with non-index x values by this option. This option is required when the type of x axis is timeseries. If this option is set on category axis, the values of the data on the key will be used for category names.
     * @name data․x
     * @memberof Options
     * @type {String}
     * @default undefined
     * @example
     * data: {
              *   x: "date"
     * }
     */
data_x:void 0,
/**
     * Specify the keys of the x values for each data.<br><br>
     * This option can be used if we want to show the data that has different x values.
     * @name data․xs
     * @memberof Options
     * @type {Object}
     * @default {}
     * @example
     * data: {
              *   xs: {
              *      data1: "x1",
              *      data2: "x2"
              *   }
     * }
     */
data_xs:{},
/**
     * Set a format specifier to parse string specifed as x.
     * @name data․xFormat
     * @memberof Options
     * @type {String}
     * @default %Y-%m-%d
     * @example
     * data: {
     *    x: "x",
     *    columns: [
     *        ["x", "01012019", "02012019", "03012019"],
     *        ["data1", 30, 200, 100]
     *    ],
     *    // Format specifier to parse as datetime for given 'x' string value
     *    xFormat: "%m%d%Y"
     * },
     * axis: {
     *    x: {
     *        type: "timeseries"
     *    }
     * }
     * @see [D3's time specifier](https://github.com/d3/d3-time-format#locale_format)
     */
data_xFormat:"%Y-%m-%d",
/**
     * Set localtime format to parse x axis.
     * @name data․xLocaltime
     * @memberof Options
     * @type {Boolean}
     * @default true
     * @example
     * data: {
              *   xLocaltime: false
     * }
     */
data_xLocaltime:!0,
/**
     * Sort on x axis.
     * @name data․xSort
     * @memberof Options
     * @type {Boolean}
     * @default true
     * @example
     * data: {
              *   xSort: false
     * }
     */
data_xSort:!0,
/**
     * Converts data id value
     * @name data․idConverter
     * @memberof Options
     * @type {Function}
     * @default function(id) { return id; }
     * @example
     * data: {
              *    idConverter: function(id) {
              *       // when id is 'data1', converts to be 'data2'
              *       // 'data2' should be given as the initial data value
              *       if (id === "data1") {
              *          return "data2";
              *       } else {
              *          return id;
              *       }
              *    }
     * }
     */
data_idConverter:function(id){return id},
/**
     * Set custom data name.
     * @name data․names
     * @memberof Options
     * @type {Object}
     * @default {}
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Data.DataName)
     * @example
     * data: {
              *   names: {
              *     data1: "Data Name 1",
              *     data2: "Data Name 2"
              *   }
     * }
     */
data_names:{},
/**
     * Set custom data class.<br><br>
     * If this option is specified, the element g for the data has an additional class that has the prefix 'bb-target-' (eg. bb-target-additional-data1-class).
     * @name data․classes
     * @memberof Options
     * @type {Object}
     * @default {}
     * @example
     * data: {
              *   classes: {
              *     data1: "additional-data1-class",
              *     data2: "additional-data2-class"
              *   }
     * }
     */
data_classes:{},
/**
     * Set groups for the data for stacking.
     * @name data․groups
     * @memberof Options
     * @type {Array}
     * @default []
     * @example
     * data: {
              *   groups: [
              *     ["data1", "data2"],
              *     ["data3"]
              *   ]
     * }
     */
data_groups:[],
/**
     * Set y axis the data related to. y and y2 can be used.
     * - **NOTE:** If all data is related to one of the axes, the domain of axis without related data will be replaced by the domain from the axis with related data
     * @name data․axes
     * @memberof Options
     * @type {Object}
     * @default {}
     * @example
     * data: {
     *   axes: {
     *     data1: "y",
     *     data2: "y2"
     *   }
     * }
     */
data_axes:{},
/**
     * Set chart type at once.<br><br>
     * If this option is specified, the type will be applied to every data. This setting can be overwritten by data.types.<br><br>
     * **Available Values:**
     * - area
     * - area-line-range
     * - area-spline
     * - area-spline-range
     * - area-step
     * - bar
     * - bubble
     * - donut
     * - gauge
     * - line
     * - pie
     * - radar
     * - scatter
     * - spline
     * - step
     * @name data․type
     * @memberof Options
     * @type {String}
     * @default line
     * @example
     * data: {
     *    type: "bar"
     * }
     */
data_type:void 0,
/**
     * Set chart type for each data.<br>
     * This setting overwrites data.type setting.
     * - **NOTE:** `radar` type can't be combined with other types.
     * @name data․types
     * @memberof Options
     * @type {Object}
     * @default {}
     * @example
     * data: {
     *   types: {
     *     data1: "bar",
     *     data2: "spline"
     *   }
     * }
     */
data_types:{},
/**
     * Set labels options
     * @name data․labels
     * @memberof Options
     * @type {Object}
     * @property {Boolean} [data.labels=false] Show or hide labels on each data points
     * @property {Boolean} [data.labels.centered=false] Centerize labels on `bar` shape. (**NOTE:** works only for 'bar' type)
     * @property {Function} [data.labels.format] Set formatter function for data labels.<br>
     * The formatter function receives 4 arguments such as v, id, i, j and it must return a string that will be shown as the label. The arguments are:<br>
     *  - `v` is the value of the data point where the label is shown.
     *  - `id` is the id of the data where the label is shown.
     *  - `i` is the index of the data point where the label is shown.
     *  - `j` is the sub index of the data point where the label is shown.<br><br>
     * Formatter function can be defined for each data by specifying as an object and D3 formatter function can be set (ex. d3.format('$'))
    	 * @property {String|Object} [data.labels.colors] Set label text colors.
     * @property {Object} [data.labels.position] Set each dataset position, relative the original.
     * @property {Number} [data.labels.position.x=0] x coordinate position, relative the original.
     * @property {Number} [data.labels.position.y=0] y coordinate position, relative the original.
     * @memberof Options
     * @type {Object}
     * @default {}
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Data.DataLabel)
     * @see [Demo: label colors](https://naver.github.io/billboard.js/demo/#Data.DataLabelColors)
     * @see [Demo: label format](https://naver.github.io/billboard.js/demo/#Data.DataLabelFormat)
     * @see [Demo: label overlap](https://naver.github.io/billboard.js/demo/#Data.DataLabelOverlap)
     * @see [Demo: label position](https://naver.github.io/billboard.js/demo/#Data.DataLabelPosition)
     * @example
     * data: {
     *   labels: true,
     *
     *   // or set specific options
     *   labels: {
     *     format: function(v, id, i, j) { ... },
     *
     *     // it's possible to set for each data
     *     format: {
     *         data1: function(v, id, i, j) { ... },
     *         ...
     *     },
     *
     *     // align text to center of the 'bar' shape (works only for 'bar' type)
     *     centered: true,
     *
     *     // apply for all label texts
     *     colors: "red",
     *
     *     // or set different colors per dataset
     *     // for not specified dataset, will have the default color value
     *     colors: {
     *        data1: "yellow",
     *        data3: "green"
     *     },
     *
     *     // set x, y coordinate position
     *     position: {
     *        x: -10,
     *        y: 10
     *     },
     *
     *     // or set x, y coordinate position by each dataset
     *     position: {
     *        data1: {x: 5, y: 5},
     *        data2: {x: 10, y: -20}
     *     }
     *   }
     * }
     */
data_labels:{},data_labels_colors:void 0,data_labels_position:{},
/**
     *  This option changes the order of stacking data and pieces of pie/donut.
     *  - If `null` specified, it will be the order the data loaded.
     *  - If function specified, it will be used as [Array.sort compareFunction](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/sort#Parameters)<br><br>
     *
     *  **Available Values:**
     *  - `desc`: In descending order
     *  - `asc`: In ascending order
     *  - `null`: It keeps the data load order
     *  - `function(data1, data2) { ... }`: Array.sort compareFunction
     * @name data․order
     * @memberof Options
     * @type {String|Function|null}
     * @default desc
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Data.DataOrder)
     * @example
     * data: {
     *   // in descending order (default)
     *   order: "desc"
     *
     *   // in ascending order
     *   order: "asc"
     *
     *   // keeps data input order
     *   order: null
     *
     *   // specifying sort function
     *   order: function(a, b) {
     *       // param data passed format
     *       {
     *          id: "data1", id_org: "data1", values: [
     *              {x: 5, value: 250, id: "data1", index: 5, name: "data1"},
     *              ...
     *          ]
     *       }
     *   }
     * }
     */
data_order:"desc",
/**
     * Define regions for each data.<br>
     * The values must be an array for each data and it should include an object that has `start`, `end` and `style`.
     * - The object type should be as:
     *   - start {Number}: Start data point number. If not set, the start will be the first data point.
     *   - [end] {Number}: End data point number. If not set, the end will be the last data point.
     *   - [style.dasharray="2 2"] {Object}: The first number specifies a distance for the filled area, and the second a distance for the unfilled area.
     * - **NOTE:** Currently this option supports only line chart and dashed style. If this option specified, the line will be dashed only in the regions.
     * @name data․regions
     * @memberof Options
     * @type {Object}
     * @default {}
     * @example
     * data: {
     *   regions: {
     *     data1: [{
     *         start: 1,
     *         end: 2,
     *         style: {
     *             dasharray: "5 2"
     *         }
     *     }, {
     *         start: 3
     *     }],
     *     ...
     *   }
     * }
     */
data_regions:{},
/**
     * Set color converter function.<br><br>
     * This option should a function and the specified function receives color (e.g. '#ff0000') and d that has data parameters like id, value, index, etc. And it must return a string that represents color (e.g. '#00ff00').
     * @name data․color
     * @memberof Options
     * @type {Function}
     * @default undefined
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Data.DataColor)
     * @example
     * data: {
     *   color: function(color, d) { ... }
     * }
     */
data_color:void 0,
/**
     * Set color for each data.
     * @name data․colors
     * @memberof Options
     * @type {Object}
     * @default {}
     * @example
     * data: {
     *   colors: {
     *     data1: "#ff0000",
     *     data2: function(d) {
     *        return "#000";
     *     }
     *     ...
     *   }
     * }
     */
data_colors:{},
/**
     * Hide each data when the chart appears.<br><br>
     * If true specified, all of data will be hidden. If multiple ids specified as an array, those will be hidden.
     * @name data․hide
     * @memberof Options
     * @type {Boolean|Array}
     * @default false
     * @example
     * data: {
     *   // all of data will be hidden
     *   hide: true
     *
     *   // specified data will be hidden
     *   hide: ["data1", ...]
     * }
     */
data_hide:!1,
/**
     * Filter values to be shown
     * The data value is the same as the returned by `.data()`.
     * @see https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/filter
     * @name data․filter
     * @memberof Options
     * @type {Function}
     * @default undefined
     * @example
     * data: {
     *   // filter for id value
     *   filter: function(v) {
     *      // v: [{id: "data1", id_org: "data1", values: [
     *      //      {x: 0, value: 130, id: "data2", index: 0}, ...]
     *      //    }, ...]
     *      return v.id !== "data1";
     *   }
     */
data_filter:void 0,
/**
     * Set the stacking to be normalized
     * - **NOTE:**
     *   - For stacking, '[data.groups](#.data%25E2%2580%25A4groups)' option should be set
     *   - y Axis will be set in percentage value (0 ~ 100%)
     *   - Must have postive values
     * @name data․stack․normalize
     * @memberof Options
     * @type {Boolean}
     * @default false
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Data.DataStackNormalized)
     * @example
     * data: {
        *   stack: {
        *      normalize: true
        *   }
        * }
     */
data_stack_normalize:!1,
/**
     * Set data selection enabled<br><br>
     * If this option is set true, we can select the data points and get/set its state of selection by API (e.g. select, unselect, selected).
     * @name data․selection․enabled
     * @memberof Options
     * @type {Boolean}
     * @default false
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Data.DataSelection)
     * @example
     * data: {
     *    selection: {
     *       enabled: true
     *    }
     * }
     */
data_selection_enabled:!1,
/**
     * Set grouped selection enabled.<br><br>
     * If this option set true, multiple data points that have same x value will be selected by one selection.
     * @name data․selection․grouped
     * @memberof Options
     * @type {Boolean}
     * @default false
     * @example
     * data: {
     *    selection: {
     *       grouped: true
     *    }
     * }
     */
data_selection_grouped:!1,
/**
     * Set a callback for each data point to determine if it's selectable or not.<br><br>
     * The callback will receive d as an argument and it has some parameters like id, value, index. This callback should return boolean.
     * @name data․selection․isselectable
     * @memberof Options
     * @type {Function}
     * @default function() { return true; }
     * @example
     * data: {
     *    selection: {
     *       isselectable: function(d) { ... }
     *    }
     * }
     */
data_selection_isselectable:function(){return!0},
/**
     * Set multiple data points selection enabled.<br><br>
     * If this option set true, multile data points can have the selected state at the same time. If false set, only one data point can have the selected state and the others will be unselected when the new data point is selected.
     * @name data․selection․multiple
     * @memberof Options
     * @type {Boolean}
     * @default true
     * @example
     * data: {
     *    selection: {
     *       multiple: false
     *    }
     * }
     */
data_selection_multiple:!0,
/**
     * Enable to select data points by dragging.
     * If this option set true, data points can be selected by dragging.
     * - **NOTE:** If this option set true, scrolling on the chart will be disabled because dragging event will handle the event.
     * @name data․selection․draggable
     * @memberof Options
     * @type {Boolean}
     * @default false
     * @example
     * data: {
     *    selection: {
     *       draggable: true
     *   }
     * }
     */
data_selection_draggable:!1,
/**
     * Set a callback for click event on each data point.<br><br>
     * This callback will be called when each data point clicked and will receive `d` and element as the arguments.
     * - `d` is the data clicked and element is the element clicked.
     * - `element` is the current interacting svg element.
     * - In this callback, `this` will be the Chart object.
     * @name data․onclick
     * @memberof Options
     * @type {Function}
     * @default function() {}
     * @example
     * data: {
     *     onclick: function(d, element) {
     *        // d - ex) {x: 4, value: 150, id: "data1", index: 4, name: "data1"}
     *        // element - <circle>
     *        ...
     *     }
     * }
     */
data_onclick:function(){},
/**
     * Set a callback for mouse/touch over event on each data point.<br><br>
     * This callback will be called when mouse cursor or via touch moves onto each data point and will receive `d` and `element` as the argument.
     * - `d` is the data where mouse cursor moves onto.
     * - `element` is the current interacting svg element.
     * - In this callback, `this` will be the Chart object.
     * @name data․onover
     * @memberof Options
     * @type {Function}
     * @default function() {}
     * @example
     * data: {
     *     onover: function(d, element) {
     *        // d - ex) {x: 4, value: 150, id: "data1", index: 4}
     *        // element - <circle>
     *        ...
     *     }
     * }
     */
data_onover:function(){},
/**
     * Set a callback for mouse/touch out event on each data point.<br><br>
     * This callback will be called when mouse cursor or via touch moves out each data point and will receive `d` as the argument.
     * - `d` is the data where mouse cursor moves out.
     * - `element` is the current interacting svg element.
     * - In this callback, `this` will be the Chart object.
     * @name data․onout
     * @memberof Options
     * @type {Function}
     * @default function() {}
     * @example
     * data: {
     *     onout: function(d, element) {
     *        // d - ex) {x: 4, value: 150, id: "data1", index: 4}
     *        // element - <circle>
     *        ...
     *     }
     * }
     */
data_onout:function(){},
/**
     * Set a callback for on data selection.
     * @name data․onselected
     * @memberof Options
     * @type {Function}
     * @default function() {}
     * @example
     * data: {
     *     onselected: function(d, element) {
     *        // d - ex) {x: 4, value: 150, id: "data1", index: 4, name: "data1"}
     *        // element - <circle>
     *        ...
     *    }
     * }
     */
data_onselected:function(){},
/**
     * Set a callback for on data un-selection.
     * @name data․onunselected
     * @memberof Options
     * @type {Function}
     * @default function() {}
     * @example
     * data: {
     *     onunselected: function(d, element) {
     *        // d - ex) {x: 4, value: 150, id: "data1", index: 4, name: "data1"}
     *        // element - <circle>
     *        ...
     *    }
     * }
     */
data_onunselected:function(){},
/**
     * Set a callback for minimum data
     * - **NOTE:** For 'area-line-range' and 'area-spline-range', `mid` data will be taken for the comparison
     * @name data․onmin
     * @memberof Options
     * @type {Function}
     * @default undefined
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Data.OnMinMaxCallback)
     * @example
     *  onmin: function(data) {
     *    // data - ex) [{x: 3, value: 400, id: "data1", index: 3}, ... ]
        *    ...
     *  }
     */
data_onmin:void 0,
/**
     * Set a callback for maximum data
     * - **NOTE:** For 'area-line-range' and 'area-spline-range', `mid` data will be taken for the comparison
     * @name data․onmax
     * @memberof Options
     * @type {Function}
     * @default undefined
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Data.OnMinMaxCallback)
     * @example
     *  onmax: function(data) {
     *    // data - ex) [{x: 3, value: 400, id: "data1", index: 3}, ... ]
        *    ...
     *  }
     */
data_onmax:void 0,
/**
     * Load a CSV or JSON file from a URL. NOTE that this will not work if loading via the "file://" protocol as the most browsers will block XMLHTTPRequests.
     * @name data․url
     * @memberof Options
     * @type {String}
     * @default undefined
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Data.LoadData)
     * @example
     * data: {
     *     url: "/data/test.csv"
     * }
     */
data_url:void 0,
/**
     * XHR header value
     * - **NOTE:** Should be used with `data.url` option
     * @name data․headers
     * @memberof Options
     * @type {String}
     * @default undefined
     * @see https://developer.mozilla.org/en-US/docs/Web/API/XMLHttpRequest/setRequestHeader
     * @example
     * data: {
     *     url: "/data/test.csv",
     *     headers: {
     *        "Content-Type": "text/xml",
     *        ...
     *     }
     * }
     */
data_headers:void 0,
/**
     * Parse a JSON object for data. See also data.keys.
     * @name data․json
     * @memberof Options
     * @type {Object}
     * @default undefined
     * @see [data․keys](#.data%25E2%2580%25A4keys)
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Data.JSONData)
     * @example
     * data: {
     *     json: [
     *       {name: "www.site1.com", upload: 200, download: 200, total: 400},
     *       {name: "www.site2.com", upload: 100, download: 300, total: 400},
     *       {name: "www.site3.com", upload: 300, download: 200, total: 500},
     *       {name: "www.site4.com", upload: 400, download: 100, total: 500}
     *     ],
     *     keys: {
     *       // x: "name", // it's possible to specify 'x' when category axis
     *       value: ["upload", "download"]
     *     }
     * }
     */
data_json:void 0,
/**
     * Load data from a multidimensional array, with the first element containing the data names, the following containing related data in that order.
     * @name data․rows
     * @memberof Options
     * @type {Array}
     * @default undefined
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Data.RowOrientedData)
     * @example
     * data: {
     *   rows: [
     *     ["A", "B", "C"],
     *     [90, 120, 300],
     *     [40, 160, 240],
     *     [50, 200, 290],
     *     [120, 160, 230],
     *     [80, 130, 300],
     *     [90, 220, 320]
     *   ]
     * }
     *
     * // for 'range' types('area-line-range' or 'area-spline-range'), data should contain:
     * // - an array of [high, mid, low] data following the order
     * // - or an object with 'high', 'mid' and 'low' key value
     * data: {
     *   rows: [
     *      ["data1", "data2"],
     *      [
     *        // or {high:150, mid: 140, low: 110}, 120
     *        [150, 140, 110], 120
     *      ],
     *      [[155, 130, 115], 55],
     *      [[160, 135, 120], 60]
     *   ],
     *   types: {
     *       data1: "area-line-range",
     *       data2: "line"
     *   }
     * }
     *
     * // for 'bubble' type, data can contain dimension value:
     * // - an array of [y, z] data following the order
     * // - or an object with 'y' and 'z' key value
     * // 'y' is for y axis coordination and 'z' is the bubble radius value
     * data: {
     *   rows: [
     *      ["data1", "data2"],
     *      [
     *        // or {y:10, z: 140}, 120
     *        [10, 140], 120
     *      ],
     *      [[100, 30], 55],
     *      [[50, 100], 60]
     *   ],
     *   types: {
     *       data1: "bubble",
     *       data2: "line"
     *   }
     * }
     */
data_rows:void 0,
/**
     * Load data from a multidimensional array, with each element containing an array consisting of a datum name and associated data values.
     * @name data․columns
     * @memberof Options
     * @type {Array}
     * @default undefined
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Data.ColumnOrientedData)
     * @example
     * data: {
     *   columns: [
     *      ["data1", 30, 20, 50, 40, 60, 50],
     *      ["data2", 200, 130, 90, 240, 130, 220],
     *      ["data3", 300, 200, 160, 400, 250, 250]
     *   ]
     * }
     *
     * // for 'range' types('area-line-range' or 'area-spline-range'), data should contain:
     * // - an array of [high, mid, low] data following the order
     * // - or an object with 'high', 'mid' and 'low' key value
     * data: {
     *   columns: [
     *      ["data1",
     *          [150, 140, 110],  // or {high:150, mid: 140, low: 110}
     *          [150, 140, 110],
     *          [150, 140, 110]
     *      ]
     *   ],
     *   type: "area-line-range"
     * }
     *
     * // for 'bubble' type, data can contain dimension value:
     * // - an array of [y, z] data following the order
     * // - or an object with 'y' and 'z' key value
     * // 'y' is for y axis coordination and 'z' is the bubble radius value
     * data: {
     *   columns: [
     *      ["data1",
     *          [10, 140],  // or {y:10, z: 140}
     *          [100, 30],
     *          [50, 100]
     *      ]
     *   ],
     *   type: "bubble"
     * }
     */
data_columns:void 0,
/**
     * Used if loading JSON via data.url.
     * - **Available Values:**
     *   - json
     *   - csv
     *   - tsv
     * @name data․mimeType
     * @memberof Options
     * @type {String}
     * @default csv
     * @example
     * data: {
     *     mimeType: "json"
     * }
     */
data_mimeType:"csv",
/**
     * Choose which JSON object keys correspond to desired data.
     * - **NOTE:** Only for JSON object given as array.
     * @name data․keys
     * @memberof Options
     * @type {String}
     * @default undefined
     * @example
     * data: {
     *     json: [
     *       {name: "www.site1.com", upload: 200, download: 200, total: 400},
     *       {name: "www.site2.com", upload: 100, download: 300, total: 400},
     *       {name: "www.site3.com", upload: 300, download: 200, total: 500},
     *       {name: "www.site4.com", upload: 400, download: 100, total: 500}
     *     ],
     *     keys: {
     *       // x: "name", // it's possible to specify 'x' when category axis
     *       value: ["upload", "download"]
     *     }
     * }
     */
data_keys:void 0,
/**
     * Set text label to be displayed when there's no data to show.
     * - ex. Toggling all visible data to not be shown, unloading all current data, etc.
     * @name data․empty․label․text
     * @memberof Options
     * @type {String}
     * @default ""
     * @example
     * data: {
     *   empty: {
     *     label: {
     *       text: "No Data"
     *     }
     *   }
     * }
     */
data_empty_label_text:"",
/**
     * Set subchart options
     * @name subchart
     * @memberof Options
     * @type {Object}
     * @property {Boolean} [subchart.show=false] Show sub chart on the bottom of the chart.
     * @property {Boolean} [subchart.axis.x.show=true] Show or hide x axis.
     * @property {Boolean} [subchart.axis.x.tick.show=true] Show or hide x axis tick line.
     * @property {Boolean} [subchart.axis.x.tick.text.show=true] Show or hide x axis tick text.
     * @property {Number} [subchart.size.height] Change the height of the subchart.
     * @property {Function} [subchart.onbrush] Set callback for brush event.<br>
     *  Specified function receives the current zoomed x domain.
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Interaction.SubChart)
     * @example
     *  subchart: {
     *      axis: {
     *      	x: {
     *      	  show: true,
     *      	    tick: {
     *      	      show: true,
     *      	      text: {
     *      	        show: false
     *      	      }
     *      	    }
     *      	}
     *      },
     *      show: true,
     *      size: {
     *          height: 20
     *      },
     *      onbrush: function(domain) { ... }
     *  }
     */
subchart_show:!1,subchart_size_height:60,subchart_axis_x_show:!0,subchart_axis_x_tick_show:!0,subchart_axis_x_tick_text_show:!0,subchart_onbrush:function(){},
/**
     * Set color of the data values
     * @name color
     * @memberof Options
     * @type {Object}
     * @property {String|Object|Function} [color.onover] Set the color value for each data point when mouse/touch onover event occurs.
     * @property {Array|null} [color.pattern=[]] Set custom color pattern. Passing `null` will not set a color for these elements, which requires the usage of custom CSS-based theming to work.
     * @property {Function} [color.tiles] if defined, allows use svg's patterns to fill data area. It should return an array of [SVGPatternElement](https://developer.mozilla.org/en-US/docs/Web/API/SVGPatternElement).
     *  - **NOTE:** The pattern element's id will be defined as `bb-colorize-pattern-$COLOR-VALUE`.<br>
     *    ex. When color pattern value is `['red', '#fff']` and defined 2 patterns,then ids for pattern elements are:<br>
     *    - `bb-colorize-pattern-red`
     *    - `bb-colorize-pattern-fff`
     * @property {Object} [color.threshold] color threshold for gauge and tooltip color
     * @property {String} [color.threshold.unit] If set to `value`, the threshold will be based on the data value. Otherwise it'll be based on equation of the `threshold.max` option value.
     * @property {Array} [color.threshold.values] Threshold values for each steps
     * @property {Number} [color.threshold.max=100] The base value to determine threshold step value condition. When the given value is 15 and max 10, then the value for threshold is `15*100/10`.
     * @example
     *  color: {
     *      pattern: ["#1f77b4", "#aec7e8", ...],
     *
     *      // Set colors' patterns
     *      // it should return an array of SVGPatternElement
     *      tiles: function() {
     *         var pattern = document.createElementNS("http://www.w3.org/2000/svg", "pattern");
     *         var g = document.createElementNS("http://www.w3.org/2000/svg", "g");
     *         var circle1 = document.createElementNS("http://www.w3.org/2000/svg", "circle");
     *
     *         pattern.setAttribute("patternUnits", "userSpaceOnUse");
     *         pattern.setAttribute("width", "32");
     *         pattern.setAttribute("height", "32");
     *
     *         g.style.fill = "#000";
     *         g.style.opacity = "0.2";
              *
     *         circle1.setAttribute("cx", "3");
     *         circle1.setAttribute("cy", "3");
     *         circle1.setAttribute("r", "3");
              *
     *         g.appendChild(circle1);
     *         pattern.appendChild(g);
     *
     *         return [pattern];
     *      },
     *
     *      // for threshold usage, pattern values should be set for each steps
     *      pattern: ["grey", "green", "yellow", "orange", "red"],
     *      threshold: {
     *          unit: "value",
     *
     *          // when value is 20 => 'green', value is 40 => 'orange' will be set.
     *          values: [10, 20, 30, 40, 50],
     *
     *          // the equation for max:
     *          // - unit == 'value': max => 30
     *          // - unit != 'value': max => value*100/30
     *          max: 30
     *      },
     *
     *      // set all data to 'red'
     *      onover: "red",
     *
     *      // set different color for data
     *      onover: {
     *          data1: "red",
     *          data2: "yellow"
     *      },
     *
     *      // will pass data object to the callback
     *      onover: function(d) {
     *          return d.id === "data1" ? "red" : "green";
     *      }
     *  }
     */
color_pattern:[],color_tiles:void 0,color_threshold:{},color_onover:void 0,
/**
     * Legend options
     * @name legend
     * @memberof Options
     * @type {Object}
     * @property {Boolean} [legend.show=true] Show or hide legend.
     * @property {Boolean} [legend.hide=false] Hide legend
     *  If true given, all legend will be hidden. If string or array given, only the legend that has the id will be hidden.
     * @property {String|HTMLElement} [legend.contents.bindto=undefined] Set CSS selector or element reference to bind legend items.
     * @property {String|Function} [legend.contents.template=undefined] Set item's template.<br>
     *  - If set `string` value, within template the 'color' and 'title' can be replaced using template-like syntax string:
     *    - {=COLOR}: data color value
     *    - {=TITLE}: data title value
     *  - If set `function` value, will pass following arguments to the given function:
     *   - title {String}: data's id value
     *   - color {String}: color string
     *   - data {Array}: data array
     * @property {String} [legend.position=bottom] Change the position of legend.<br>
     *  Available values are: `bottom`, `right` and `inset` are supported.
     * @property {Object} [legend.inset={anchor: 'top-left',x: 10,y: 0,step: undefined}] Change inset legend attributes.<br>
     *  This option accepts object that has the keys `anchor`, `x`, `y` and `step`.
     *  - **anchor** decides the position of the legend:
     *   - top-left
     *   - top-right
     *   - bottom-left
     *   - bottom-right
     *  - **x** and **y**:
     *   - set the position of the legend based on the anchor.
     *  - **step**:
     *   - defines the max step the legend has (e.g. If 2 set and legend has 3 legend item, the legend 2 columns).
     * @property {Boolean} [legend.equally=false] Set to all items have same width size.
     * @property {Boolean} [legend.padding=0] Set padding value
     * @property {Function} [legend.item.onclick=undefined] Set click event handler to the legend item.
     * @property {Function} [legend.item.onover=undefined] Set mouse/touch over event handler to the legend item.
     * @property {Function} [legend.item.onout=undefined] Set mouse/touch out event handler to the legend item.
     * @property {Number} [legend.item.tile.width=10] Set width of item tile element
     * @property {Number} [legend.item.tile.height=10] Set height of item tile element
     * @property {Boolean} [legend.usePoint=false] Whether to use custom points in legend.
     * @see [Demo: position](https://naver.github.io/billboard.js/demo/#Legend.LegendPosition)
     * @see [Demo: contents.template](https://naver.github.io/billboard.js/demo/#Legend.LegendTemplate1)
     * @see [Demo: usePoint](https://naver.github.io/billboard.js/demo/#Legend.usePoint)
     * @example
     *  legend: {
     *      show: true,
     *      hide: true,
     *      //or hide: "data1"
              *      //or hide: ["data1", "data2"]
     *      contents: {
     *          bindto: "#legend",   // <ul id='legend'></ul>
     *
     *          // will be as: <li style='background-color:#1f77b4'>data1</li>
     *          template: "<li style='background-color:{=COLOR}'>{=TITLE}</li>"
     *
     *          // or using function
     *          template: function(id, color, data) {
     *               // if you want omit some legend, return falsy value
     *               if (title !== "data1") {
     *                    return "<li style='background-color:"+ color +">"+ title +"</li>";
     *               }
     *          }
     *      },
              *      position: "bottom",  // bottom, right, inset
     *      inset: {
     *          anchor: "top-right"  // top-left, top-right, bottom-left, bottom-right
     *          x: 20,
     *          y: 10,
     *          step: 2
     *      },
              *      equally: false,
              *      padding: 10,
              *      item: {
     *          onclick: function(id) { ... },
     *          onover: function(id) { ... },
     *          onout: function(id) { ... },
     *
     *          // set tile's size
     *          tile: {
     *              width: 20,
     *              height: 15
     *          }
     *      },
     *      usePoint: true
     *  }
     */
legend_show:!0,legend_hide:!1,legend_contents_bindto:void 0,legend_contents_template:void 0,legend_position:"bottom",legend_inset_anchor:"top-left",legend_inset_x:10,legend_inset_y:0,legend_inset_step:void 0,legend_item_onclick:void 0,legend_item_onover:void 0,legend_item_onout:void 0,legend_equally:!1,legend_padding:0,legend_item_tile_width:10,legend_item_tile_height:10,legend_usePoint:!1,
/**
     * Switch x and y axis position.
     * @name axis․rotated
     * @memberof Options
     * @type {Boolean}
     * @default false
     * @example
     * axis: {
     *   rotated: true
     * }
     */
axis_rotated:!1,
/**
     * Set clip-path attribute for x axis element
     * @name axis․x․clipPath
     * @memberof Options
     * @type {Boolean}
     * @default true
     * @see [Demo]()
     * @example
     * // don't set 'clip-path' attribute
     * clipPath: false
     */
axis_x_clipPath:!0,
/**
     * Show or hide x axis.
     * @name axis․x․show
     * @memberof Options
     * @type {Boolean}
     * @default true
     * @example
     * axis: {
     *   x: {
     *     show: false
     *   }
     * }
     */
axis_x_show:!0,
/**
     * Set type of x axis.<br><br>
     * **Available Values:**
     * - timeseries
     * - category
     * - indexed
     * @name axis․x․type
     * @memberof Options
     * @type {String}
     * @default indexed
     * @see [Demo: indexed](https://naver.github.io/billboard.js/demo/#Chart.AreaChart)
     * @see [Demo: timeseries](https://naver.github.io/billboard.js/demo/#Chart.TimeseriesChart)
     * @see [Demo: category](https://naver.github.io/billboard.js/demo/#Data.CategoryData)
     * @example
     * axis: {
     *   x: {
     *     type: "timeseries"
     *   }
     * }
     */
axis_x_type:"indexed",
/**
     * Set how to treat the timezone of x values.<br>
     * If true, treat x value as localtime. If false, convert to UTC internally.
     * @name axis․x․localtime
     * @memberof Options
     * @type {Boolean}
     * @default true
     * @example
     * axis: {
     *   x: {
     *     localtime: false
     *   }
     * }
     */
axis_x_localtime:!0,
/**
     * Set category names on category axis.
     * This must be an array that includes category names in string. If category names are included in the date by data.x option, this is not required.
     * @name axis․x․categories
     * @memberof Options
     * @type {Array}
     * @default []
     * @example
     * axis: {
     *   x: {
     *     categories: ["Category 1", "Category 2", ...]
     *   }
     * }
     */
axis_x_categories:[],
/**
     * centerize ticks on category axis.
     * @name axis․x․tick․centered
     * @memberof Options
     * @type {Boolean}
     * @default false
     * @example
     * axis: {
     *   x: {
     *     tick: {
     *       centered: true
     *     }
     *   }
     * }
     */
axis_x_tick_centered:!1,
/**
     * A function to format tick value. Format string is also available for timeseries data.
     * @name axis․x․tick․format
     * @memberof Options
     * @type {Function|String}
     * @default undefined
     * @see [D3's time specifier](https://github.com/d3/d3-time-format#locale_format)
     * @example
     * axis: {
     *   x: {
     *     tick: {
     *        // for timeseries, a 'datetime' object is given as parameter
     *       format: function(x) {
     *           return x.getFullYear();
     *       }
     *
     *       // for category, index(Number) and categoryName(String) are given as parameter
     *       format: function(index, categoryName) {
     *           return categoryName.substr(0, 10);
     *       },
     *
     *        // for timeseries format specifier
     *        format: "%Y-%m-%d %H:%M:%S"
     *     }
     *   }
     * }
     */
axis_x_tick_format:void 0,
/**
     * Setting for culling ticks.<br><br>
     * If true is set, the ticks will be culled, then only limitted tick text will be shown. This option does not hide the tick lines. If false is set, all of ticks will be shown.<br><br>
     * We can change the number of ticks to be shown by axis.x.tick.culling.max.
     * @name axis․x․tick․culling
     * @memberof Options
     * @type {Boolean}
     * @default
     * - true for indexed axis and timeseries axis
     * - false for category axis
     * @example
     * axis: {
     *   x: {
     *     tick: {
     *       culling: false
     *     }
     *   }
     * }
     */
axis_x_tick_culling:{},
/**
     * The number of tick texts will be adjusted to less than this value.
     * @name axis․x․tick․culling․max
     * @memberof Options
     * @type {Number}
     * @default 10
     * @example
     * axis: {
     *   x: {
     *     tick: {
     *       culling: {
     *           max: 5
     *       }
     *     }
     *   }
     * }
     */
axis_x_tick_culling_max:10,
/**
     * The number of x axis ticks to show.<br><br>
     * This option hides tick lines together with tick text. If this option is used on timeseries axis, the ticks position will be determined precisely and not nicely positioned (e.g. it will have rough second value).
     * @name axis․x․tick․count
     * @memberof Options
     * @type {Number}
     * @default undefined
     * @example
     * axis: {
     *   x: {
     *     tick: {
     *       count: 5
     *     }
     *   }
     * }
     */
axis_x_tick_count:void 0,
/**
     * Show or hide x axis tick line.
     * @name axis․x․tick․show
     * @memberof Options
     * @type {Boolean}
     * @default true
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Axis.HideTickLineText)
     * @example
     * axis: {
     *   x: {
     *     tick: {
     *       show: false
     *     }
     *   }
     * }
     */
axis_x_tick_show:!0,
/**
     * Show or hide x axis tick text.
     * @name axis․x․tick․text․show
     * @memberof Options
     * @type {Boolean}
     * @default true
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Axis.HideTickLineText)
     * @example
     * axis: {
     *   x: {
     *     tick: {
     *       text: {
     *           show: false
     *       }
     *     }
     *   }
     * }
     */
axis_x_tick_text_show:!0,
/**
     * Set the x Axis tick text's position relatively its original position
     * @name axis․x․tick․text․position
     * @memberof Options
     * @type {Object}
     * @default {x: 0, y:0}
     * @example
     * axis: {
     *   x: {
     *     tick: {
     *       text: {
     *         position: {
     *           x: 10,
     *           y: 10
     *         }
     *       }
     *     }
     *   }
     * }
     */
axis_x_tick_text_position:{x:0,y:0},
/**
     * Fit x axis ticks.
     * - **true**: ticks will be positioned nicely to have same intervals.
     * - **false**: ticks will be positioned according to x value of the data points.
     * @name axis․x․tick․fit
     * @memberof Options
     * @type {Boolean}
     * @default true
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Axis.XAxisTickFitting)
     * @see [Demo: for timeseries zoom](https://naver.github.io/billboard.js/demo/#Axis.XAxisTickTimeseries)
     * @example
     * axis: {
     *   x: {
     *     tick: {
     *       fit: false
     *     }
     *   }
     * }
     */
axis_x_tick_fit:!0,
/**
     * Set the x values of ticks manually.<br><br>
     * If this option is provided, the position of the ticks will be determined based on those values.<br>
     * This option works with `timeseries` data and the x values will be parsed accoding to the type of the value and data.xFormat option.
     * @name axis․x․tick․values
     * @memberof Options
     * @type {Array|Function}
     * @default null
     * @example
     * axis: {
     *   x: {
     *     tick: {
     *       values: [1, 2, 4, 8, 16, 32, ...],
     *
     *       // an Array value should be returned
     *       values: function() {
     *       	return [ ... ];
     *       }
     *     }
     *   }
     * }
     */
axis_x_tick_values:null,
/**
     * Rotate x axis tick text if there is not enough space for 'category' and 'timeseries' type axis.
     * - **NOTE:** The conditions where `autorotate` is enabled are:
     *   - axis.x.type='category' or 'timeseries
     *   - axis.x.tick.multiline=false
     *   - axis.x.tick.culling=false
     *   - axis.x.tick.fit=true
     * @name axis․x․tick․autorotate
     * @memberof Options
     * @type {Boolean}
     * @default false
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Axis.XAxisTickAutorotate)
     * @example
     * axis: {
     *   x: {
     *     tick: {
     *       rotate: 15,
     *       autorotate: true,
     *       multiline: false,
     *       culling: false,
     *       fit: true
     *     }
     *   }
     * }
     */
axis_x_tick_autorotate:!1,
/**
     * Rotate x axis tick text.
     * - If you set negative value, it will rotate to opposite direction.
     * - Applied when [`axis.rotated`](#.axis%25E2%2580%25A4rotated) option is `false`.
     * - As long as `axis_x_tick_fit` is set to `true` it will calculate an overflow for the y2 axis and add this value to the right padding.
     * @name axis․x․tick․rotate
     * @memberof Options
     * @type {Number}
     * @default 0
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Axis.RotateXAxisTickText)
     * @example
     * axis: {
     *   x: {
     *     tick: {
     *       rotate: 60
     *     }
     *   }
     * }
     */
axis_x_tick_rotate:0,
/**
     * Show x axis outer tick.
     * @name axis․x․tick․outer
     * @memberof Options
     * @type {Boolean}
     * @default true
     * @example
     * axis: {
     *   x: {
     *     tick: {
     *       outer: false
     *     }
     *   }
     * }
     */
axis_x_tick_outer:!0,
/**
     * Set tick text to be multiline
     * - **NOTE:**
     *  > When x tick text contains `\n`, it's used as line break and 'axis.x.tick.width' option is ignored.
     * @name axis․x․tick․multiline
     * @memberof Options
     * @type {Boolean}
     * @default true
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Axis.XAxisTickMultiline)
     * @example
     * axis: {
     *   x: {
     *     tick: {
     *       multiline: false
     *     }
     *   }
     * }
     * @example
     * // example of line break with '\n'
     * // In this case, 'axis.x.tick.width' is ignored
     * data: {
     *    x: "x",
     *    columns: [
     *        ["x", "long\ntext", "Another\nLong\nText"],
     *        ...
     *    ],
     * }
     */
axis_x_tick_multiline:!0,
/**
     * Set tick width
     * - **NOTE:**
     *  > When x tick text contains `\n`, this option is ignored.
     * @name axis․x․tick․width
     * @memberof Options
     * @type {Number}
     * @default null
     * @example
     * axis: {
     *   x: {
     *     tick: {
     *       width: 50
     *     }
     *   }
     * }
     */
axis_x_tick_width:null,
/**
     * Set to display system tooltip(via 'title' attribute) for tick text
     * - **NOTE:** Only available for category axis type (`axis.x.type='category'`)
     * @name axis․x․tick․tooltip
     * @memberof Options
     * @type {Boolean}
     * @default false
     * @example
     * axis: {
     *   x: {
     *     tick: {
     *       tooltip: true
     *     }
     *   }
     * }
     */
axis_x_tick_tooltip:!1,
/**
     * Set max value of x axis range.
     * @name axis․x․max
     * @memberof Options
     * @property {Number} max Set the max value
     * @property {Boolean} [max.fit=false] When specified `max.value` is greater than the bound data value, setting `true` will make x axis max to be fitted to the bound data max value.
     * - **NOTE:** If the bound data max value is greater than the `max.value`, the x axis max will be limited as the given `max.value`.
     * @property {Number} [max.value] Set the max value
     * @example
     * axis: {
     *   x: {
     *     max: 100,
     *
     *     max: {
     *       // 'fit=true' will make x axis max to be limited as the bound data value max when 'max.value' is greater.
     *       // - when bound data max is '10' and max.value: '100' ==>  x axis max will be '10'
     *       // - when bound data max is '1000' and max.value: '100' ==> x axis max will be '100'
     *       fit: true,
     *       value: 100
     *     }
     *   }
     * }
     */
axis_x_max:void 0,
/**
     * Set min value of x axis range.
     * @name axis․x․min
     * @memberof Options
     * @property {Number} min Set the min value
     * @property {Boolean} [min.fit=false] When specified `min.value` is lower than the bound data value, setting `true` will make x axis min to be fitted to the bound data min value.
     * - **NOTE:** If the bound data min value is lower than the `min.value`, the x axis min will be limited as the given `min.value`.
     * @property {Number} [min.value] Set the min value
     * @example
     * axis: {
     *   x: {
     *     min: -100,
     *
     *     min: {
     *       // 'fit=true' will make x axis min to be limited as the bound data value min when 'min.value' is lower.
     *       // - when bound data min is '-10' and min.value: '-100' ==>  x axis min will be '-10'
     *       // - when bound data min is '-1000' and min.value: '-100' ==> x axis min will be '-100'
     *       fit: true,
     *       value: -100
     *     }
     *   }
     * }
     */
axis_x_min:void 0,
/**
     * Set padding for x axis.<br><br>
     * If this option is set, the range of x axis will increase/decrease according to the values.
     * If no padding is needed in the rage of x axis, 0 should be set.
     * - **NOTE:**
     *   The padding values aren't based on pixels. It differs according axis types<br>
     *   - **category:** The unit of tick value
     *     ex. the given value `1`, is same as the width of 1 tick width
     *   - **timeseries:** Numeric time value
     *     ex. the given value `1000*60*60*24`, which is numeric time equivalent of a day, is same as the width of 1 tick width
     * @name axis․x․padding
     * @memberof Options
     * @type {Object|Number}
     * @default {}
     * @example
     * axis: {
     *   x: {
     *     padding: {
     *       // when axis type is 'category'
     *       left: 1,  // set left padding width of equivalent value of a tick's width
     *       right: 0.5  // set right padding width as half of equivalent value of tick's width
     *
     *       // when axis type is 'timeseries'
     *       left: 1000*60*60*24,  // set left padding width of equivalent value of a day tick's width
     *       right: 1000*60*60*12   // set right padding width as half of equivalent value of a day tick's width
     *     },
     *
     *     // or set both values at once.
     *     padding: 10
     *   }
     * }
     */
axis_x_padding:{},
/**
     * Set height of x axis.<br><br>
     * The height of x axis can be set manually by this option. If you need more space for x axis, please use this option for that. The unit is pixel.
     * @name axis․x․height
     * @memberof Options
     * @type {Number}
     * @default undefined
     * @example
     * axis: {
     *   x: {
     *     height: 20
     *   }
     * }
     */
axis_x_height:void 0,
/**
     * Set default extent for subchart and zoom. This can be an array or function that returns an array.
     * @name axis․x․extent
     * @memberof Options
     * @type {Array|Function}
     * @default undefined
     * @example
     * axis: {
     *   x: {
     *     // extent range as a pixel value
     *     extent: [0, 200],
     *
     *     // when axis is 'timeseries', parsable datetime string
     *     extent: ["2019-03-01", "2019-03-05"],
     *
     *     // return extent value
     *     extent: function(domain, scale) {
     *    	 var extent = domain.map(function(v) {
     *     	    return scale(v);
     *     	 });
     *
     *   	 // it should return a format of array
     *   	 // ex) [0, 584]
     *     	 return extent;
     *     }
     *   }
     * }
     */
axis_x_extent:void 0,
/**
     * Set label on x axis.<br><br>
     * You can set x axis label and change its position by this option.
     * `string` and `object` can be passed and we can change the poisiton by passing object that has position key.<br>
     * Available position differs according to the axis direction (vertical or horizontal).
     * If string set, the position will be the default.
     *
     *  - **If it's horizontal axis:**
     *    - inner-right [default]
     *    - inner-center
     *    - inner-left
     *    - outer-right
     *    - outer-center
     *    - outer-left
     *  - **If it's vertical axis:**
     *    - inner-top [default]
     *    - inner-middle
     *    - inner-bottom
     *    - outer-top
     *    - outer-middle
     *    - outer-bottom
     * @name axis․x․label
     * @memberof Options
     * @type {String|Object}
     * @default undefined
     * @example
     * axis: {
     *   x: {
     *     label: "Your X Axis"
     *   }
     * }
     *
     * axis: {
     *   x: {
     *     label: {
     *        text: "Your X Axis",
     *        position: "outer-center"
     *     }
     *   }
     * }
     */
axis_x_label:{},
/**
     * Set additional axes for x Axis.
     * - **NOTE:** Axis' scale is based on x Axis value if domain option isn't set.
     *
     * Each axis object should consist with following options:
     *
     * | Name | Type | Default | Description |
     * | --- | --- | --- | --- |
     * | domain | Array | - | Set the domain value |
     * | tick.outer | Boolean | true | Show outer tick |
     * | tick.format | Function | - | Set formatter for tick text |
     * | tick.count | Number | - | Set the number of y axis ticks |
     * | tick.values | Array | - | Set tick values manually |
     * @name axis․x․axes
     * @memberof Options
     * @type {Array}
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Axis.MultiAxes)
     * @see [Demo: Domain](https://naver.github.io/billboard.js/demo/#Axis.MultiAxesDomain)
     * @example
     * x: {
     *    axes: [
     *      {
     *        // if set, will not be correlated with the main x Axis domain value
    	 *        domain: [0, 1000],
     *        tick: {
     *          outer: false,
     *          format: function(x) {
     *             return x + "%";
     *          },
     *          count: 2,
     *          values: [10, 20, 30]
     *        }
     *      },
     *      ...
     *    ]
     * }
     */
axis_x_axes:[],
/**
     * Set clip-path attribute for y axis element
     * - **NOTE**: `clip-path` attribute for y Axis is set only when `axis.y.inner` option is true.
     * @name axis․y․clipPath
     * @memberof Options
     * @type {Boolean}
     * @default true
     * @example
     * // don't set 'clip-path' attribute
     * clipPath: false
     */
axis_y_clipPath:!0,
/**
     * Show or hide y axis.
     * @name axis․y․show
     * @memberof Options
     * @type {Boolean}
     * @default true
     * @example
     * axis: {
     *   y: {
     *     show: false
     *   }
     * }
     */
axis_y_show:!0,
/**
     * Set type of y axis.<br><br>
     * **Available Values:**
     *   - timeseries
     *   - indexed
     * @name axis․y․type
     * @memberof Options
     * @type {String}
     * @default "indexed"
     * @example
     * axis: {
     *   y: {
     *     type: "timeseries"
     *   }
     * }
     */
axis_y_type:void 0,
/**
     * Set max value of y axis.
     * - **NOTE:** Padding will be added based on this value, so if you don't need the padding, please set axis.y.padding to disable it (e.g. axis.y.padding = 0).
     * @name axis․y․max
     * @memberof Options
     * @type {Number}
     * @default undefined
     * @example
     * axis: {
     *   y: {
     *     max: 1000
     *   }
     * }
     */
axis_y_max:void 0,
/**
     * Set min value of y axis.
     * - **NOTE:**
     *   Padding will be added based on this value, so if you don't need the padding, please set axis.y.padding to disable it (e.g. axis.y.padding = 0).
     * @name axis․y․min
     * @memberof Options
     * @type {Number}
     * @default undefined
     * @example
     * axis: {
     *   y: {
     *     min: 1000
     *   }
     * }
     */
axis_y_min:void 0,
/**
     * Change the direction of y axis.<br><br>
     * If true set, the direction will be from the top to the bottom.
     * @name axis․y․inverted
     * @memberof Options
     * @type {Boolean}
     * @default false
     * @example
     * axis: {
     *   y: {
     *     inverted: true
     *   }
     * }
     */
axis_y_inverted:!1,
/**
     * Set center value of y axis.
     * @name axis․y․center
     * @memberof Options
     * @type {Number}
     * @default undefined
     * @example
     * axis: {
     *   y: {
     *     center: 0
     *   }
     * }
     */
axis_y_center:void 0,
/**
     * Show y axis inside of the chart.
     * @name axis․y․inner
     * @memberof Options
     * @type {Boolean}
     * @default false
     * @example
     * axis: {
     *   y: {
     *     inner: true
     *   }
     * }
     */
axis_y_inner:!1,
/**
     * Set label on y axis.<br><br>
     * You can set y axis label and change its position by this option. This option works in the same way as [axis.x.label](#.axis%25E2%2580%25A4x%25E2%2580%25A4label).
     * @name axis․y․label
     * @memberof Options
     * @type {String|Object}
     * @default {}
     * @see [axis.x.label](#.axis%25E2%2580%25A4x%25E2%2580%25A4label) for position string value.
     * @example
     * axis: {
     *   y: {
     *     label: "Your Y Axis"
     *   }
     * }
     *
     * axis: {
     *   y: {
     *     label: {
     *        text: "Your Y Axis",
     *        position: "outer-middle"
     *     }
     *   }
     * }
     */
axis_y_label:{},
/**
     * Set formatter for y axis tick text.<br><br>
     * This option accepts d3.format object as well as a function you define.
     * @name axis․y․tick․format
     * @memberof Options
     * @type {Function}
     * @default undefined
     * @example
     * axis: {
     *   y: {
     *     tick: {
     *       format: function(x) {
     *           return x.getFullYear();
     *       }
     *     }
     *   }
     * }
     */
axis_y_tick_format:void 0,
/**
     * Setting for culling ticks.<br><br>
     * If true is set, the ticks will be culled, then only limitted tick text will be shown. This option does not hide the tick lines. If false is set, all of ticks will be shown.<br><br>
     * We can change the number of ticks to be shown by axis.y.tick.culling.max.
     * @name axis․y․tick․culling
     * @memberof Options
     * @type {Boolean}
     * @default false
     * @example
     * axis: {
     *   y: {
     *     tick: {
     *       culling: false
     *     }
     *   }
     * }
     */
axis_y_tick_culling:!1,
/**
     * The number of tick texts will be adjusted to less than this value.
     * @name axis․y․tick․culling․max
     * @memberof Options
     * @type {Number}
     * @default 5
     * @example
     * axis: {
     *   y: {
     *     tick: {
     *       culling: {
     *           max: 5
     *       }
     *     }
     *   }
     * }
     */
axis_y_tick_culling_max:5,
/**
     * Show y axis outer tick.
     * @name axis․y․tick․outer
     * @memberof Options
     * @type {Boolean}
     * @default true
     * @example
     * axis: {
     *   y: {
     *     tick: {
     *       outer: false
     *     }
     *   }
     * }
     */
axis_y_tick_outer:!0,
/**
     * Set y axis tick values manually.
     * @name axis․y․tick․values
     * @memberof Options
     * @type {Array|Function}
     * @default null
     * @example
     * axis: {
     *   y: {
     *     tick: {
     *       values: [100, 1000, 10000],
     *
     *       // an Array value should be returned
     *       values: function() {
     *       	return [ ... ];
     *       }
     *     }
     *   }
     * }
     */
axis_y_tick_values:null,
/**
     * Rotate y axis tick text.
     * - If you set negative value, it will rotate to opposite direction.
     * - Applied when [`axis.rotated`](#.axis%25E2%2580%25A4rotated) option is `true`.
     * @name axis․y․tick․rotate
     * @memberof Options
     * @type {Number}
     * @default 0
     * @example
     * axis: {
     *   y: {
     *     tick: {
     *       rotate: 60
     *     }
     *   }
     * }
     */
axis_y_tick_rotate:0,
/**
     * Set the number of y axis ticks.<br><br>
     * - **NOTE:** The position of the ticks will be calculated precisely, so the values on the ticks will not be rounded nicely. In the case, axis.y.tick.format or axis.y.tick.values will be helpful.
     * @name axis․y․tick․count
     * @memberof Options
     * @type {Number}
     * @default undefined
     * @example
     * axis: {
     *   y: {
     *     tick: {
     *       count: 5
     *     }
     *   }
     * }
     */
axis_y_tick_count:void 0,
/**
     * Show or hide y axis tick line.
     * @name axis․y․tick․show
     * @memberof Options
     * @type {Boolean}
     * @default true
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Axis.HideTickLineText)
     * @example
     * axis: {
     *   y: {
     *     tick: {
     *       show: false
     *     }
     *   }
     * }
     */
axis_y_tick_show:!0,
/**
     * Set axis tick step(interval) size.
     * - **NOTE:** Will be ignored if `axis.y.tick.count` or `axis.y.tick.values` options are set.
     * @name axis․y․tick․stepSize
     * @memberof Options
     * @type {Number}
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Axis.StepSizeForYAxis)
     * @example
     * axis: {
     *   y: {
     *     tick: {
     *       // tick value will step as indicated interval value.
     *       // ex) 'stepSize=15' ==> [0, 15, 30, 45, 60]
     *       stepSize: 15
     *     }
     *   }
     * }
     */
axis_y_tick_stepSize:null,
/**
    * Show or hide y axis tick text.
    * @name axis․y․tick․text․show
    * @memberof Options
    * @type {Boolean}
    * @default true
    * @see [Demo](https://naver.github.io/billboard.js/demo/#Axis.HideTickLineText)
    * @example
    * axis: {
    *   y: {
    *     tick: {
    *       text: {
    *           show: false
    *       }
    *     }
    *   }
    * }
    */
axis_y_tick_text_show:!0,
/**
     * Set the y Axis tick text's position relatively its original position
     * @name axis․y․tick․text․position
     * @memberof Options
     * @type {Object}
     * @default {x: 0, y:0}
     * @example
     * axis: {
     *   y: {
     *     tick: {
     *       text: {
     *         position: {
     *           x: 10,
     *           y: 10
     *         }
     *       }
     *     }
     *   }
     * }
     */
axis_y_tick_text_position:{x:0,y:0},
/**
     * Set the number of y axis ticks.<br><br>
     * - **NOTE:** The position of the ticks will be calculated precisely, so the values on the ticks will not be rounded nicely. In the case, axis.y.tick.format or axis.y.tick.values will be helpful.
     * @name axis․y․tick․time
     * @memberof Options
     * @private
     * @type {Object}
     * @property {Function} [time.value] D3's time interval function (https://github.com/d3/d3-time#intervals)
     * @example
     * axis: {
     *   y: {
     *     tick: {
     *       time: {
     *          // ticks at 15-minute intervals
     *          // https://github.com/d3/d3-scale/blob/master/README.md#time_ticks
     *          value: d3.timeMinute.every(15)
     *       }
     *     }
     *   }
     * }
     */
// @TODO: not fully implemented yet
axis_y_tick_time_value:void 0,
/**
     * Set padding for y axis.<br><br>
     * You can set padding for y axis to create more space on the edge of the axis.
     * This option accepts object and it can include top and bottom. top, bottom will be treated as pixels.
     *
     * - **NOTE:**
     *   - Given values are translated relative to the y Axis domain value for padding
     *   - For area and bar type charts, [area.zerobased](#.area) or [bar.zerobased](#.bar) options should be set to 'false` to get padded bottom.
     * @name axis․y․padding
     * @memberof Options
     * @type {Object|Number}
     * @default {}
     * @example
     * axis: {
     *   y: {
     *     padding: {
     *       top: 0,
     *       bottom: 0
     *     },
     *
     *     // or set both values at once.
     *     padding: 10
     *   }
     * }
     */
axis_y_padding:{},
/**
     * Set default range of y axis.<br><br>
     * This option set the default value for y axis when there is no data on init.
     * @name axis․y․default
     * @memberof Options
     * @type {Array}
     * @default undefined
     * @example
     * axis: {
     *   y: {
     *     default: [0, 1000]
     *   }
     * }
     */
axis_y_default:void 0,
/**
     * Set additional axes for y Axis.
     * - **NOTE:** Axis' scale is based on y Axis value if domain option isn't set.
     *
     * Each axis object should consist with following options:
     *
     * | Name | Type | Default | Description |
     * | --- | --- | --- | --- |
     * | domain | Array | - | Set the domain value |
     * | tick.outer | Boolean | true | Show outer tick |
     * | tick.format | Function | - | Set formatter for tick text |
     * | tick.count | Number | - | Set the number of y axis ticks |
     * | tick.values | Array | - | Set tick values manually |
     * @name axis․y․axes
     * @memberof Options
     * @type {Array}
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Axis.MultiAxes)
     * @see [Demo: Domain](https://naver.github.io/billboard.js/demo/#Axis.MultiAxesDomain)
     * @example
     * y: {
     *    axes: [
     *      {
     *        // if set, will not be correlated with the main y Axis domain value
     *        domain: [0, 1000],
     *        tick: {
     *          outer: false,
     *          format: function(x) {
     *             return x + "%";
     *          },
     *          count: 2,
     *          values: [10, 20, 30]
     *        }
     *      },
     *      ...
     *    ]
     * }
     */
axis_y_axes:[],
/**
     * Show or hide y2 axis.
     * - **NOTE**:
     *   - When set to `false` will not generate y2 axis node. In this case, all 'y2' axis related functionality won't work properly.
     *   - If need to use 'y2' related options while y2 isn't visible, set the value `true` and control visibility by css display property.
     * @name axis․y2․show
     * @memberof Options
     * @type {Boolean}
     * @default false
     * @example
     * axis: {
     *   y2: {
     *     show: true
     *   }
     * }
     */
axis_y2_show:!1,
/**
     * Set max value of y2 axis.
     * @name axis․y2․max
     * @memberof Options
     * @type {Number}
     * @default undefined
     * @example
     * axis: {
     *   y2: {
     *     max: 1000
     *   }
     * }
     */
axis_y2_max:void 0,
/**
     * Set min value of y2 axis.
     * @name axis․y2․min
     * @memberof Options
     * @type {Number}
     * @default undefined
     * @example
     * axis: {
     *   y2: {
     *     min: -1000
     *   }
     * }
     */
axis_y2_min:void 0,
/**
     * Change the direction of y2 axis.<br><br>
     * If true set, the direction will be from the top to the bottom.
     * @name axis․y2․inverted
     * @memberof Options
     * @type {Boolean}
     * @default false
     * @example
     * axis: {
     *   y2: {
     *     inverted: true
     *   }
     * }
     */
axis_y2_inverted:!1,
/**
     * Set center value of y2 axis.
     * @name axis․y2․center
     * @memberof Options
     * @type {Number}
     * @default undefined
     * @example
     * axis: {
     *   y2: {
     *     center: 0
     *   }
     * }
     */
axis_y2_center:void 0,
/**
     * Show y2 axis inside of the chart.
     * @name axis․y2․inner
     * @memberof Options
     * @type {Boolean}
     * @default false
     * @example
     * axis: {
     *   y2: {
     *     inner: true
     *   }
     * }
     */
axis_y2_inner:!1,
/**
     * Set label on y2 axis.<br><br>
     * You can set y2 axis label and change its position by this option. This option works in the same way as [axis.x.label](#.axis%25E2%2580%25A4x%25E2%2580%25A4label).
     * @name axis․y2․label
     * @memberof Options
     * @type {String|Object}
     * @default {}
     * @see [axis.x.label](#.axis%25E2%2580%25A4x%25E2%2580%25A4label) for position string value.
     * @example
     * axis: {
     *   y2: {
     *     label: "Your Y2 Axis"
     *   }
     * }
     *
     * axis: {
     *   y2: {
     *     label: {
     *        text: "Your Y2 Axis",
     *        position: "outer-middle"
     *     }
     *   }
     * }
     */
axis_y2_label:{},
/**
     * Set formatter for y2 axis tick text.<br><br>
     * This option works in the same way as axis.y.format.
     * @name axis․y2․tick․format
     * @memberof Options
     * @type {Function}
     * @default undefined
     * @example
     * axis: {
     *   y2: {
     *     tick: {
     *       format: d3.format("$,")
     *       //or format: function(d) { return "$" + d; }
     *     }
     *   }
     * }
     */
axis_y2_tick_format:void 0,
/**
     * Setting for culling ticks.<br><br>
     * If true is set, the ticks will be culled, then only limitted tick text will be shown. This option does not hide the tick lines. If false is set, all of ticks will be shown.<br><br>
     * We can change the number of ticks to be shown by axis.y.tick.culling.max.
     * @name axis․y2․tick․culling
     * @memberof Options
     * @type {Boolean}
     * @default false
     * @example
     * axis: {
     *   y2: {
     *     tick: {
     *       culling: false
     *     }
     *   }
     * }
     */
axis_y2_tick_culling:!1,
/**
     * The number of tick texts will be adjusted to less than this value.
     * @name axis․y2․tick․culling․max
     * @memberof Options
     * @type {Number}
     * @default 5
     * @example
     * axis: {
     *   y2: {
     *     tick: {
     *       culling: {
     *           max: 5
     *       }
     *     }
     *   }
     * }
     */
axis_y2_tick_culling_max:5,
/**
     * Show or hide y2 axis outer tick.
     * @name axis․y2․tick․outer
     * @memberof Options
     * @type {Boolean}
     * @default true
     * @example
     * axis: {
     *   y2: {
     *     tick: {
     *       outer: false
     *     }
     *   }
     * }
     */
axis_y2_tick_outer:!0,
/**
     * Set y2 axis tick values manually.
     * @name axis․y2․tick․values
     * @memberof Options
     * @type {Array|Function}
     * @default null
     * @example
     * axis: {
     *   y2: {
     *     tick: {
     *       values: [100, 1000, 10000],
     *
     *       // an Array value should be returned
     *       values: function() {
     *       	return [ ... ];
     *       }
     *     }
     *   }
     * }
     */
axis_y2_tick_values:null,
/**
     * Rotate y2 axis tick text.
     * - If you set negative value, it will rotate to opposite direction.
     * - Applied when [`axis.rotated`](#.axis%25E2%2580%25A4rotated) option is `true`.
     * @name axis․y2․tick․rotate
     * @memberof Options
     * @type {Number}
     * @default 0
     * @example
     * axis: {
     *   y2: {
     *     tick: {
     *       rotate: 60
     *     }
     *   }
     * }
     */
axis_y2_tick_rotate:0,
/**
     * Set the number of y2 axis ticks.
     * - **NOTE:** This works in the same way as axis.y.tick.count.
     * @name axis․y2․tick․count
     * @memberof Options
     * @type {Number}
     * @default undefined
     * @example
     * axis: {
     *   y2: {
     *     tick: {
     *       count: 5
     *     }
     *   }
     * }
     */
axis_y2_tick_count:void 0,
/**
     * Show or hide y2 axis tick line.
     * @name axis․y2․tick․show
     * @memberof Options
     * @type {Boolean}
     * @default true
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Axis.HideTickLineText)
     * @example
     * axis: {
     *   y2: {
     *     tick: {
     *       show: false
     *     }
     *   }
     * }
     */
axis_y2_tick_show:!0,
/**
     * Set axis tick step(interval) size.
     * - **NOTE:** Will be ignored if `axis.y2.tick.count` or `axis.y2.tick.values` options are set.
     * @name axis․y2․tick․stepSize
     * @memberof Options
     * @type {Number}
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Axis.StepSizeForYAxis)
     * @example
     * axis: {
     *   y2: {
     *     tick: {
     *       // tick value will step as indicated interval value.
     *       // ex) 'stepSize=15' ==> [0, 15, 30, 45, 60]
     *       stepSize: 15
     *     }
     *   }
     * }
     */
axis_y2_tick_stepSize:null,
/**
     * Show or hide y2 axis tick text.
     * @name axis․y2․tick․text․show
     * @memberof Options
     * @type {Boolean}
     * @default true
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Axis.HideTickLineText)
     * @example
     * axis: {
     *   y2: {
     *     tick: {
     *       text: {
     *           show: false
     *       }
     *     }
     *   }
     * }
     */
axis_y2_tick_text_show:!0,
/**
     * Set the y2 Axis tick text's position relatively its original position
     * @name axis․y2․tick․text․position
     * @memberof Options
     * @type {Object}
     * @default {x: 0, y:0}
     * @example
     * axis: {
     *   y2: {
     *     tick: {
     *       text: {
     *         position: {
     *           x: 10,
     *           y: 10
     *         }
     *       }
     *     }
     *   }
     * }
     */
axis_y2_tick_text_position:{x:0,y:0},
/**
     * Set padding for y2 axis.<br><br>
     * You can set padding for y2 axis to create more space on the edge of the axis.
     * This option accepts object and it can include top and bottom. top, bottom will be treated as pixels.
     *
     * - **NOTE:**
     *   - Given values are translated relative to the y2 Axis domain value for padding
     *   - For area and bar type charts, [area.zerobased](#.area) or [bar.zerobased](#.bar) options should be set to 'false` to get padded bottom.
     * @name axis․y2․padding
     * @memberof Options
     * @type {Object|Number}
     * @default {}
     * @example
     * axis: {
     *   y2: {
     *     padding: {
     *       top: 100,
     *       bottom: 100
     *     }
     *
     *     // or set both values at once.
     *     padding: 10
     * }
     */
axis_y2_padding:{},
/**
     * Set default range of y2 axis.<br><br>
     * This option set the default value for y2 axis when there is no data on init.
     * @name axis․y2․default
     * @memberof Options
     * @type {Array}
     * @default undefined
     * @example
     * axis: {
     *   y2: {
     *     default: [0, 1000]
     *   }
     * }
     */
axis_y2_default:void 0,
/**
     * Set additional axes for y2 Axis.
     * - **NOTE:** Axis' scale is based on y2 Axis value if domain option isn't set.
     *
     * Each axis object should consist with following options:
     *
     * | Name | Type | Default | Description |
     * | --- | --- | --- | --- |
     * | domain | Array | - | Set the domain value |
     * | tick.outer | Boolean | true | Show outer tick |
     * | tick.format | Function | - | Set formatter for tick text |
     * | tick.count | Number | - | Set the number of y axis ticks |
     * | tick.values | Array | - | Set tick values manually |
     * @name axis․y2․axes
     * @memberof Options
     * @type {Array}
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Axis.MultiAxes)
     * @see [Demo: Domain](https://naver.github.io/billboard.js/demo/#Axis.MultiAxesDomain)
     * @example
     * y2: {
     *    axes: [
     *      {
     *        // if set, will not be correlated with the main y2 Axis domain value
     *        domain: [0, 1000],
     *        tick: {
     *          outer: false,
     *          format: function(x) {
     *             return x + "%";
     *          },
     *          count: 2,
     *          values: [10, 20, 30]
     *        }
     *      },
     *      ...
     *    ]
     * }
     */
axis_y2_axes:[],
/**
     * Set related options
     * @name grid
     * @memberof Options
     * @type {Object}
     * @property {Boolean} [front=false] Set 'grid & focus lines' to be positioned over grid lines and chart elements.
     * @property {Boolean} [x.show=false] Show grids along x axis.
     * @property {Array} [x.lines=[]] Show additional grid lines along x axis.<br>
     *  This option accepts array including object that has value, text, position and class. text, position and class are optional. For position, start, middle and end (default) are available.
     *  If x axis is category axis, value can be category name. If x axis is timeseries axis, value can be date string, Date object and unixtime integer.
     * @property {Boolean} [y.show=false] Show grids along x axis.
     * @property {Array} [y.lines=[]] Show additional grid lines along y axis.<br>
     *  This option accepts array including object that has value, text, position and class.
     * @property {Number} [y.ticks=10] Number of y grids to be shown.
     * @property {Boolean} [focus.edge=false] Show edged focus grid line.<br>**NOTE:** Available when [`tooltip.grouped=false`](#.tooltip) option is set.
     * @property {Boolean} [focus.show=true] Show grid line when focus.
     * @property {Boolean} [focus.y=false] Show y coordinate focus grid line.<br>**NOTE:** Available when [`tooltip.grouped=false`](#.tooltip) option is set.
     * @property {Boolean} [lines.front=true] Set grid lines to be positioned over chart elements.
     * @default undefined
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Grid.GridLines)
     * @see [Demo: X Grid Lines](https://naver.github.io/billboard.js/demo/#Grid.OptionalXGridLines)
     * @see [Demo: Y Grid Lines](https://naver.github.io/billboard.js/demo/#Grid.OptionalYGridLines)
     * @example
     * grid: {
     *   x: {
     *     show: true,
     *     lines: [
     *       {value: 2, text: "Label on 2"},
     *       {value: 5, text: "Label on 5", class: "label-5"},
     *       {value: 6, text: "Label on 6", position: "start"}
     *     ]
     *   },
     *   y: {
     *     show: true,
     *     lines: [
     *       {value: 100, text: "Label on 100"},
     *       {value: 200, text: "Label on 200", class: "label-200"},
     *       {value: 300, text: "Label on 300", position: 'middle'}
     *     ],
     *     ticks: 5
     *   },
     *   front: true,
     *   focus: {
     *      show: false,
     *
     *      // Below options are available when 'tooltip.grouped=false' option is set
     *      edge: true,
     *      y: true
     *   },
     *   lines: {
     *      front: false
     *   }
     * }
     */
grid_x_show:!1,grid_x_type:"tick",grid_x_lines:[],grid_y_show:!1,grid_y_lines:[],grid_y_ticks:10,grid_focus_edge:!1,grid_focus_show:!0,grid_focus_y:!1,grid_front:!1,grid_lines_front:!0,
/**
     * Set point options
     * @name point
     * @memberof Options
     * @type {Object}
     * @property {Boolean} [point.show=true] Whether to show each point in line.
     * @property {Number|Function} [point.r=2.5] The radius size of each point.
     *  - **NOTE:** Disabled for 'bubble' type
     * @property {Boolean} [point.focus.expand.enabled=true] Whether to expand each point on focus.
     * @property {Number} [point.focus.expand.r=point.r*1.75] The radius size of each point on focus.
     *  - **NOTE:** For 'bubble' type, the default is `bubbleSize*1.15`
     * @property {Number} [point.sensitivity=10] The senstivity value for interaction boundary.
     * @property {Number} [point.select.r=point.r*4] The radius size of each point on selected.
     * @property {String} [point.type="circle"] The type of point to be drawn
     * - **NOTE:**
     *   - If chart has 'bubble' type, only circle can be used.
     *   - For IE, non circle point expansions are not supported due to lack of transform support.
     * - **Available Values:**
     *   - circle
     *   - rectangle
     * @property {Array} [point.pattern=[]] The type of point or svg shape as string, to be drawn for each line
     * - **NOTE:**
     *   - This is an `experimental` feature and can have some unexpected behaviors.
     *   - If chart has 'bubble' type, only circle can be used.
     *   - For IE, non circle point expansions are not supported due to lack of transform support.
     * - **Available Values:**
     *   - circle
     *   - rectangle
     *   - svg shape tag interpreted as string<br>
     *     (ex. `<polygon points='2.5 0 0 5 5 5'></polygon>`)
     * @see [Demo: point type](https://naver.github.io/billboard.js/demo/#Point.RectanglePoints)
     * @example
     *  point: {
     *      show: false,
     *      r: 5,
     *
     *      // or customize the radius
     *      r: function(d) {
     *          ...
     *          return r;
     *      },
     *
     *      focus: {
     *          expand: {
     *              enabled: true,
     *              r: 1
     *          }
     *      },
     *      select: {
     *          r: 3
     *      },
     *
     *      // having lower value, means how closer to be for interaction
     *      sensitivity: 3,
     *
     *      // valid values are "circle" or "rectangle"
     *      type: "rectangle",
     *
     *      // or indicate as pattern
    	 *      pattern: [
    	 *        "circle",
    	 *        "rectangle",
    	 *        "<polygon points='0 6 4 0 -4 0'></polygon>"
    	 *     ],
     *  }
     */
point_show:!0,point_r:2.5,point_sensitivity:10,point_focus_expand_enabled:!0,point_focus_expand_r:void 0,point_pattern:[],point_select_r:void 0,point_type:"circle",
/**
     * Set line options
     * @name line
     * @memberof Options
     * @type {Object}
     * @property {Boolean} [line.connectNull=false] Set if null data point will be connected or not.<br>
     *  If true set, the region of null data will be connected without any data point. If false set, the region of null data will not be connected and get empty.
     * @property {Array}   [line.classes=undefined] If set, used to set a css class on each line.
     * @property {Boolean} [line.step.type=step] Change step type for step chart.<br>
     * **Available values:**
     * - step
     * - step-before
     * - step-after
     * @property {Boolean|Array} [line.point=true] Set to false to not draw points on linecharts. Or pass an array of line ids to draw points for.
     * @property {Boolean} [line.zerobased=false] Set if min or max value will be 0 on line chart.
     * @example
     *  line: {
     *      connectNull: true,
     *      classes: [
     *          "line-class1",
     *          "line-class2"
     *      ],
     *      step: {
     *          type: "step-after"
     *      },
     *
     *      // hide all data points ('point.show=false' also has similar effect)
     *      point: false,
     *
     *      // show data points for only indicated datas
     *      point: [
     *          "data1", "data3"
     *      ],
     *
     *      zerobased: false
     *  }
     */
line_connectNull:!1,line_step_type:"step",line_zerobased:!1,line_classes:void 0,line_point:!0,
/**
    	* Set scatter options
    	* @name scatter
    	* @memberof Options
    	* @type {Object}
    	* @property {Boolean} [scatter.zerobased=false] Set if min or max value will be 0 on scatter chart.
    	* @example
    	*  scatter: {
    	*      connectNull: true,
    	*      step: {
    	*          type: "step-after"
    	*      },
    	*
    	*      // hide all data points ('point.show=false' also has similar effect)
    	*      point: false,
    	*
    	*      // show data points for only indicated datas
    	*      point: [
    	*          "data1", "data3"
    	*      ],
    	*
    	*      zerobased: false
    	*  }
    	*/
scatter_zerobased:!1,
/**
     * Set bar options
     * @name bar
     * @memberof Options
     * @type {Object}
     * @property {Number} [bar.padding=0] The padding pixel value between each bar.
     * @property {Number} [bar.radius] Set the radius of bar edge in pixel.
     * - **NOTE:** Works only for non-stacked bar
     * @property {Number} [bar.radius.ratio] Set the radius ratio of bar edge in relative the bar's width.
    	 * @property {Number} [bar.sensitivity=2] The senstivity offset value for interaction boundary.
     * @property {Number} [bar.width] Change the width of bar chart.
     * @property {Number} [bar.width.ratio=0.6] Change the width of bar chart by ratio.
     * @property {Number} [bar.width.max] The maximum width value for ratio.
     * @property {Number} [bar.width.dataname] Change the width of bar for indicated dataset only.
     * - **NOTE:**
     *   - Works only for non-stacked bar
     *   - Bars are centered accoding its total width value
     * @property {Number} [bar.width.dataname.ratio=0.6] Change the width of bar chart by ratio.
     * @property {Number} [bar.width.dataname.max] The maximum width value for ratio.
     * @property {Boolean} [bar.zerobased=true] Set if min or max value will be 0 on bar chart.
     * @see [Demo: bar padding](https://naver.github.io/billboard.js/demo/#BarChartOptions.BarPadding)
     * @see [Demo: bar radius](https://naver.github.io/billboard.js/demo/#BarChartOptions.BarRadius)
     * @see [Demo: bar width](https://naver.github.io/billboard.js/demo/#BarChartOptions.BarWidth)
     * @see [Demo: bar width variant](https://naver.github.io/billboard.js/demo/#BarChartOptions.BarWidthVariant)
     * @example
     *  bar: {
     *      padding: 1,
     *
     *      // the 'radius' option can be used only for non-stacking bars
     *      radius: 10,
     *      // or
     *      radius: {
     *          ratio: 0.5
     *      }
     *
     *      // will not have offset between each bar elements for interaction
     *      sensitivity: 0,
     *
     *      width: 10,
     *
     *      // or
     *      width: {
     *          ratio: 0.2,
     *          max: 20
     *      },
     *
     *      // or specify width per dataset
     *      width: {
     *          data1: 20,
     *          data2: {
     *              ratio: 0.2,
     *              max: 20
     *          }
     *      },
     *
     *      zerobased: false
     *  }
     */
bar_padding:0,bar_radius:void 0,bar_radius_ratio:void 0,bar_sensitivity:2,bar_width:void 0,bar_width_ratio:.6,bar_width_max:void 0,bar_zerobased:!0,
/**
     * Set bubble options
     * @name bubble
     * @memberof Options
     * @type {Object}
     * @property {Number|Function} [bubble.maxR=35] Set the max bubble radius value
     * @property {Boolean} [bubble.zerobased=false] Set if min or max value will be 0 on bubble chart.
     * @example
     *  bubble: {
     *      // ex) If 100 is the highest value among data bound, the representation bubble of 100 will have radius of 50.
     *      // And the lesser will have radius relatively from tha max value.
     *      maxR: 50,
     *
     *      // or set radius callback
     *      maxR: function(d) {
     *          // ex. of d param - {x: Fri Oct 06 2017 00:00:00 GMT+0900, value: 80, id: "data2", index: 5}
     *          ...
     *          return Math.sqrt(d.value * 2);
     *      },
     *      zerobased: false
     *  }
     */
bubble_maxR:35,bubble_zerobased:!1,
/**
     * Set area options
     * @name area
     * @memberof Options
     * @type {Object}
     * @property {Boolean} [area.zerobased=true] Set if min or max value will be 0 on area chart.
     * @property {Boolean} [area.above=false] Set background area above the data chart line.
     * @property {Boolean|Object} [area.linearGradient=false] Set the linear gradient on area.<br><br>
     * Or customize by giving below object value:
     *  - x {Array}: `x1`, `x2` value
     *  - y {Array}: `y1`, `y2` value
     *  - stops {Array}: Each item should be having `[offset, stop-color, stop-opacity]` values.
     * @see [MDN's &lt;linearGradient>](https://developer.mozilla.org/en-US/docs/Web/SVG/Element/linearGradient), [&lt;stop>](https://developer.mozilla.org/en-US/docs/Web/SVG/Element/stop)
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Chart.AreaChart)
     * @see [Demo: above](https://naver.github.io/billboard.js/demo/#AreaChartOptions.Above)
     * @see [Demo: linearGradient](https://naver.github.io/billboard.js/demo/#AreaChartOptions.LinearGradient)
     * @example
     *  area: {
     *      zerobased: false,
     *      above: true,
     *
     *      // will generate follwing linearGradient:
     *      // <linearGradient x1="0" x2="0" y1="0" y2="1">
     *      //    <stop offset="0" stop-color="$DATA_COLOR" stop-opacity="1"></stop>
     *      //    <stop offset="1" stop-color="$DATA_COLOR" stop-opacity="0"></stop>
     *      // </linearGradient>
     *      linearGradient: true,
     *
     *      // Or customized gradient
     *      linearGradient: {
     *      	x: [0, 0],  // x1, x2 attributes
     *      	y: [0, 0],  // y1, y2 attributes
     *      	stops: [
     *      	  // offset, stop-color, stop-opacity
     *      	  [0, "#7cb5ec", 1],
     *
     *      	  // setting 'null' for stop-color, will set its original data color
     *      	  [0.5, null, 0],
     *
     *      	  // setting 'function' for stop-color, will pass data id as argument.
     *      	  // It should return color string or null value
     *      	  [1, function(id) { return id === "data1" ? "red" : "blue"; }, 0],
     *      	]
     *      }
     *  }
     */
area_zerobased:!0,area_above:!1,area_linearGradient:!1,
/**
     * Set pie options
     * @name pie
     * @memberof Options
     * @type {Object}
     * @property {Boolean} [pie.label.show=true] Show or hide label on each pie piece.
     * @property {Function} [pie.label.format] Set formatter for the label on each pie piece.
     * @property {Number} [pie.label.threshold=0.05] Set threshold to show/hide labels.
     * @property {Number|Function} [pie.label.ratio=undefined] Set ratio of labels position.
     * @property {Boolean|Object} [pie.expand=true] Enable or disable expanding pie pieces.
     * @property {Number} [pie.expand.rate=0.98] Set expand rate.
     * @property {Number} [pie.expand.duration=50] Set expand transition time in ms.
     * @property {Number|Object} [pie.innerRadius=0] Sets the inner radius of pie arc.
     * @property {Number} [pie.padAngle=0] Set padding between data.
     * @property {Number} [pie.padding=0] Sets the gap between pie arcs.
    	 * @property {Number} [donut.startingAngle=0] Set starting angle where data draws.
     * @example
     *  pie: {
     *      label: {
     *          show: false,
     *          format: function(value, ratio, id) {
     *              return d3.format("$")(value);
     *
     *              // to multiline, return with '\n' character
     *              // return value +"%\nLine1\n2Line2";
     *          },
     *          threshold: 0.1,
     *
     *          // set ratio callback. Should return ratio value
     *          ratio: function(d, radius, h) {
     *              ...
     *              return ratio;
     *          },
     *          // or set ratio number
     *          ratio: 0.5
     *      },
     *
     *      // disable expand transition for interaction
     *      expand: false,
     *
     *      expand: {
     *      	// set duration of expand transition to 500ms.
     *          duration: 500,
     *
     *      	// set expand area rate
     *          rate: 1
     *      },
     *
     *      innerRadius: 0,
     *
     *      // set different innerRadius for each data
     *      innerRadius: {
     *      	data1: 10,
     *      	data2: 0
     *      }
     *
     *      padAngle: 0.1,
     *      padding: 0,
     *      startingAngle: 1
     *  }
     */
pie_label_show:!0,pie_label_format:void 0,pie_label_threshold:.05,pie_label_ratio:void 0,pie_expand:{},pie_expand_rate:.98,pie_expand_duration:50,pie_innerRadius:0,pie_padAngle:0,pie_padding:0,pie_startingAngle:0,
/**
     * Set plugins
     * @name plugins
     * @memberof Options
     * @type {Array}
     * @example
     *  plugins: [
     *    new bb.plugin.stanford({ ... }),
     *    new PluginA(),
     *    ...
     * ]
     */
plugins:[],
/**
     * Set gauge options
     * @name gauge
     * @memberof Options
     * @type {Object}
     * @property {Boolean} [gauge.fullCircle=false] Show full circle as donut. When set to 'true', the max label will not be showed due to start and end points are same location.
     * @property {Boolean} [gauge.label.show=true] Show or hide label on gauge.
     * @property {Function} [gauge.label.format] Set formatter for the label on gauge. Label text can be multilined with `\n` character.
     * @property {Function} [gauge.label.extents] Set customized min/max label text.
     * @property {Boolean} [gauge.expand=true] Enable or disable expanding gauge.
    	 * @property {Number} [gauge.expand.rate=0.98] Set expand rate.
     * @property {Number} [gauge.expand.duration=50] Set the expand transition time in milliseconds.
     * @property {Number} [gauge.min=0] Set min value of the gauge.
     * @property {Number} [gauge.max=100] Set max value of the gauge.
     * @property {Number} [gauge.startingAngle=-1 * Math.PI / 2] Set starting angle where data draws.
     * @property {String} [gauge.title=""] Set title of gauge chart. Use `\n` character to enter line break.
     * @property {String} [gauge.units] Set units of the gauge.
     * @property {Number} [gauge.width] Set width of gauge chart.
     * @property {String} [gauge.type="single"] Set type of gauge to be displayed.<br><br>
     * **Available Values:**
     * - single
     * - multi
     * @property {String} [gauge.arcs.minWidth=5] Set minimal width of gauge arcs until the innerRadius disappears.
     * @example
     *  gauge: {
     *      fullCircle: false,
     *      label: {
     *          show: false,
     *          format: function(value, ratio) {
     *              return value;
     *
     *              // to multiline, return with '\n' character
     *              // return value +"%\nLine1\n2Line2";
     *          },
     *          extents: function(value, isMax) {
    	 *              return (isMax ? "Max:" : "Min:") + value;
     *          }
     *      },
     *
     *      // disable expand transition for interaction
     *      expand: false,
     *
     *      expand: {
     *      	// set duration of expand transition to 500ms.
     *          duration: 500,
     *
     *      	// set expand area rate
     *          rate: 1
     *      },
     *
     *      min: -100,
     *      max: 200,
     *      type: "single"  // or 'multi'
     *      title: "Title Text",
     *      units: "%",
     *      width: 10,
     *      arcs: {
     *          minWidth: 5
     *      }
     *  }
     */
gauge_fullCircle:!1,gauge_label_show:!0,gauge_label_format:void 0,gauge_min:0,gauge_max:100,gauge_type:"single",gauge_startingAngle:-1*Math.PI/2,gauge_label_extents:void 0,gauge_title:"",gauge_units:void 0,gauge_width:void 0,gauge_arcs_minWidth:5,gauge_expand:{},gauge_expand_rate:.98,gauge_expand_duration:50,
/**
     * Set donut options
     * @name donut
     * @memberof Options
     * @type {Object}
     * @property {Boolean} [donut.label.show=true] Show or hide label on each donut piece.
     * @property {Function} [donut.label.format] Set formatter for the label on each donut piece.
     * @property {Number} [donut.label.threshold=0.05] Set threshold to show/hide labels.
     * @property {Number|Function} [donut.label.ratio=undefined] Set ratio of labels position.
     * @property {Boolean} [donut.expand=true] Enable or disable expanding donut pieces.
     * @property {Number} [donut.expand.rate=0.98] Set expand rate.
     * @property {Number} [donut.expand.duration=50] Set expand transition time in ms.
     * @property {Number} [donut.width] Set width of donut chart.
     * @property {String} [donut.title=""] Set title of donut chart. Use `\n` character to enter line break.
     * @property {Number} [donut.padAngle=0] Set padding between data.
     * @property {Number} [donut.startingAngle=0] Set starting angle where data draws.
     * @example
     *  donut: {
     *      label: {
     *          show: false,
     *          format: function(value, ratio, id) {
     *              return d3.format("$")(value);
     *
     *              // to multiline, return with '\n' character
     *              // return value +"%\nLine1\n2Line2";
     *          },
     *          threshold: 0.1,
     *
     *          // set ratio callback. Should return ratio value
     *          ratio: function(d, radius, h) {
     *          	...
     *          	return ratio;
     *          },
     *          // or set ratio number
     *          ratio: 0.5
     *      },
     *
     *      // disable expand transition for interaction
     *      expand: false,
     *
     *      expand: {
     *      	// set duration of expand transition to 500ms.
     *          duration: 500,
     *
     *      	// set expand area rate
     *          rate: 1
     *      },
     *
     *      width: 10,
     *      padAngle: 0.2,
     *      startingAngle: 1,
     *      title: "Donut Title"
     *
     *      // title with line break
     *      title: "Title1\nTitle2"
     *  }
     */
donut_label_show:!0,donut_label_format:void 0,donut_label_threshold:.05,donut_label_ratio:void 0,donut_width:void 0,donut_title:"",donut_expand:{},donut_expand_rate:.98,donut_expand_duration:50,donut_padAngle:0,donut_startingAngle:0,
/**
     * Set spline options
     * - **Available interpolation type values:**
     *  - basis (d3.curveBasis)
     *  - basis-closed (d3.curveBasisClosed)
     *  - basis-open (d3.curveBasisOpen)
     *  - bundle (d3.curveBundle)
     *  - cardinal (d3.curveCardinal)
     *  - cardinal-closed (d3.curveCardinalClosed)
     *  - cardinal-open (d3.curveCardinalOpen)
     *  - catmull-rom (d3.curveCatmullRom)
     *  - catmull-rom-closed (d3.curveCatmullRomClosed)
     *  - catmull-rom-open (d3.curveCatmullRomOpen)
     *  - monotone-x (d3.curveMonotoneX)
     *  - monotone-y (d3.curveMonotoneY)
     *  - natural (d3.curveNatural)
     *  - linear-closed (d3.curveLinearClosed)
     *  - linear (d3.curveLinear)
     *  - step (d3.curveStep)
     *  - step-after (d3.curveStepAfter)
     *  - step-before (d3.curveStepBefore)
     * @name spline
     * @memberof Options
     * @type {Object}
     * @property {String} [spline.interpolation.type="cardinal"]
     * @see [Interpolation (d3 v4)](http://bl.ocks.org/emmasaunders/c25a147970def2b02d8c7c2719dc7502)
     * @example
     *  spline: {
     *      interpolation: {
     *          type: "cardinal"
     *      }
     *  }
     */
spline_interpolation_type:"cardinal",
/**
     * Set radar options
     * - **NOTE:**
     *  > When x tick text contains `\n`, it's used as line break.
     * @name radar
     * @memberof Options
     * @type {Object}
     * @property {Number} [radar.axis.max=undefined] The max value of axis. If not given, it'll take the max value from the given data.
     * @property {Boolean} [radar.axis.line.show=true] Show or hide axis line.
     * @property {Number} [radar.axis.text.position.x=0] x coordinate position, relative the original.
     * @property {NUmber} [radar.axis.text.position.y=0] y coordinate position, relative the original.
     * @property {Boolean} [radar.axis.text.show=true] Show or hide axis text.
     * @property {Boolean} [radar.direction.clockwise=false] Set the direction to be drawn.
     * @property {Number} [radar.level.depth=3] Set the level depth.
     * @property {Boolean} [radar.level.show=true] Show or hide level.
     * @property {Function} [radar.level.text.format=(x) => (x % 1 === 0 ? x : x.toFixed(2))] Set format function for the level value.
     * @property {Boolean} [radar.level.text.show=true] Show or hide level text.
     * @property {Number} [radar.size.ratio=0.87] Set size ratio.
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Chart.RadarChart)
     * @see [Demo: radar axis](https://naver.github.io/billboard.js/demo/#RadarChartOptions.RadarAxis)
     * @see [Demo: radar level](https://naver.github.io/billboard.js/demo/#RadarChartOptions.RadarLevel)
     * @see [Demo: radar size](https://naver.github.io/billboard.js/demo/#RadarChartOptions.RadarSize)
     * @see [Demo: radar axis multiline](https://naver.github.io/billboard.js/demo/#RadarChartOptions.RadarAxisMultiline)
     * @example
     *  radar: {
     *      axis: {
     *          max: 50,
     *          line: {
     *              show: false
     *          },
     *          text: {
     *              position: {
     *              	x: 0,
     *              	y: 0
     *              },
     *              show: false
     *          }
     *      },
     *      direction: {
     *          clockwise: true
     *      },
     *      level: {
     *          show: false,
     *          text: {
     *              format: function(x) {
     *                  return x + "%";
     *              },
     *              show: true
     *          }
     *      },
     *      size: {
     *          ratio: 0.7
     *      }
     *  }
     */
radar_axis_max:void 0,radar_axis_line_show:!0,radar_axis_text_show:!0,radar_axis_text_position:{},radar_level_depth:3,radar_level_show:!0,radar_level_text_format:function(x){return x%1===0?x:x.toFixed(2)},radar_level_text_show:!0,radar_size_ratio:.87,radar_direction_clockwise:!1,
/**
     * Control the render timing
     * @name render
     * @memberof Options
     * @type {Object}
     * @property {Boolean} [render.lazy=true] Make to not render at initialization (enabled by default when bind element's visibility is hidden).
     * @property {Boolean} [render.observe=true] Observe bind element's visibility(`display` or `visiblity` inline css property or class value) & render when is visible automatically (for IEs, only works IE11+). When set to **false**, call [`.flush()`](./Chart.html#flush) to render.
     * @see [Demo](https://naver.github.io/billboard.js/demo/#ChartOptions.LazyRender)
     * @example
     *  render: {
     *    lazy: true,
     *    observe: true
     * }
     *
     * @example
     *	// <!-- render.lazy will detect visibility defined -->
     *  // (a) <div id='chart' class='hide'></div>
     *  // (b) <div id='chart' style='display:none'></div>
     *
     *  // render.lazy enabled by default when element is hidden
     *  var chart = bb.generate({ ... });
     *
     *  // chart will be rendered automatically when element's visibility changes
     *  // Note: works only for inlined css property or class attribute changes
     *  document.getElementById('chart').classList.remove('hide')  // (a)
     *  document.getElementById('chart').style.display = 'block';  // (b)
     *
     * @example
     *	// chart won't be rendered and not observing bind element's visiblity changes
     *  var chart = bb.generate({
     *     render: {
     *          lazy: true,
     *          observe: false
     *     }
     *  });
     *
     *  // call at any point when you want to render
     *  chart.flush();
     */
render:{},
/**
     * Show rectangles inside the chart.<br><br>
     * This option accepts array including object that has axis, start, end and class.
     * The keys start, end and class are optional.
     * axis must be x, y or y2. start and end should be the value where regions start and end.
     * If not specified, the edge values will be used.
     * If timeseries x axis, date string, Date object and unixtime integer can be used.
     * If class is set, the region element will have it as class.
     * @name regions
     * @memberof Options
     * @type {Array}
     * @default []
     * @example
     *  regions: [
     *    {
     *      axis: "x",
     *      start: 1,
     *      end: 4,
     *      class: "region-1-4"
     *    }
     *  ]
     */
regions:[],
/**
     * Tooltip options
     * @name tooltip
     * @memberof Options
     * @type {Object}
     * @property {Boolean} [tooltip.show=true] Show or hide tooltip.
     * @property {Boolean} [tooltip.doNotHide=false] Make tooltip keep showing not hiding on interaction.
     * @property {Boolean} [tooltip.grouped=true] Set if tooltip is grouped or not for the data points.
     *   - **NOTE:** The overlapped data points will be displayed as grouped even if set false.
     * @property {Boolean} [tooltip.linked=false] Set if tooltips on all visible charts with like x points are shown together when one is shown.
     * @property {String} [tooltip.linked.name=""] Groping name for linked tooltip.<br>If specified, linked tooltip will be groped interacting to be worked only with the same name.
     * @property {Function} [tooltip.format.title] Set format for the title of tooltip.<br>
     *  Specified function receives x of the data point to show.
     * @property {Function} [tooltip.format.name] Set format for the name of each data in tooltip.<br>
     *  Specified function receives name, ratio, id and index of the data point to show. ratio will be undefined if the chart is not donut/pie/gauge.
     * @property {Function} [tooltip.format.value] Set format for the value of each data in tooltip.<br>
     *  Specified function receives name, ratio, id and index of the data point to show. ratio will be undefined if the chart is not donut/pie/gauge.
     *  If undefined returned, the row of that value will be skipped.
     * @property {Function} [tooltip.position] Set custom position function for the tooltip.<br>
     *  This option can be used to modify the tooltip position by returning object that has top and left.
     * @property {Function|Object} [tooltip.contents] Set custom HTML for the tooltip.<br>
     *  Specified function receives data, defaultTitleFormat, defaultValueFormat and color of the data point to show. If tooltip.grouped is true, data includes multiple data points.
     * @property {String|HTMLElement} [tooltip.contents.bindto=undefined] Set CSS selector or element reference to bind tooltip.
     *  - **NOTE:** When is specified, will not be updating tooltip's position.
     * @property {String} [tooltip.contents.template=undefined] Set tooltip's template.<br><br>
     *  Within template, below syntax will be replaced using template-like syntax string:
     *    - **{{ ... }}**: the doubly curly brackets indicate loop block for data rows.
     *    - **{=CLASS_TOOLTIP}**: default tooltip class name `bb-tooltip`.
     *    - **{=CLASS_TOOLTIP_NAME}**: default tooltip data class name (ex. `bb-tooltip-name-data1`)
     *    - **{=TITLE}**: title value.
     *    - **{=COLOR}**: data color.
     *    - **{=VALUE}**: data value.
     * @property {Object} [tooltip.contents.text=undefined] Set additional text content within data loop, using template syntax.
     *  - **NOTE:** It should contain `{ key: Array, ... }` value
     *    - 'key' name is used as substitution within template as '{=KEY}'
     *    - The value array length should match with the data length
     * @property {Boolean} [tooltip.init.show=false] Show tooltip at the initialization.
     * @property {Number} [tooltip.init.x=0] Set x Axis index to be shown at the initialization.
     * @property {Object} [tooltip.init.position={top: "0px",left: "50px"}] Set the position of tooltip at the initialization.
     * @property {Function} [tooltip.onshow] Set a callback that will be invoked before the tooltip is shown.
     * @property {Function} [tooltip.onhide] Set a callback that will be invoked before the tooltip is hidden.
     * @property {Function} [tooltip.onshown] Set a callback that will be invoked after the tooltip is shown
     * @property {Function} [tooltip.onhidden] Set a callback that will be invoked after the tooltip is hidden.
     * @property {String|Function|null} [tooltip.order=null] Set tooltip data display order.<br><br>
     *  **Available Values:**
     *  - `desc`: In descending data value order
     *  - `asc`: In ascending data value order
     *  - `null`: It keeps the data display order<br>
     *     **NOTE:** When `data.groups` is set, the order will follow as the stacked graph order.<br>
     *      If want to order as data bound, set any value rather than asc, desc or null. (ex. empty string "")
     *  - `function(data1, data2) { ... }`: [Array.sort compareFunction](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/sort#Parameters)
     * @see [Demo: Hide Tooltip](https://naver.github.io/billboard.js/demo/#Tooltip.HideTooltip)
     * @see [Demo: Tooltip Grouping](https://naver.github.io/billboard.js/demo/#Tooltip.TooltipGrouping)
     * @see [Demo: Tooltip Format](https://naver.github.io/billboard.js/demo/#Tooltip.TooltipFormat)
     * @see [Demo: Linked Tooltip](https://naver.github.io/billboard.js/demo/#Tooltip.LinkedTooltips)
     * @see [Demo: Tooltip Template](https://naver.github.io/billboard.js/demo/#Tooltip.TooltipTemplate)
     * @example
     *  tooltip: {
     *      show: true,
     *      doNotHide: true,
     *      grouped: false,
     *      format: {
     *          title: function(x) { return "Data " + x; },
     *          name: function(name, ratio, id, index) { return name; },
     *          value: function(value, ratio, id, index) { return ratio; }
     *      },
     *      position: function(data, width, height, element) {
     *      	// return with unit or without. If the value is number, is treated as 'px'.
     *      	return {top: "10%", left: 20}  // top:10%; left: 20px;
    		 *      },
    		 *      contents: function(d, defaultTitleFormat, defaultValueFormat, color) {
    		 *          return ... // formatted html as you want
     		 *      },
     *
     *       // specify tooltip contents using template
     *       // - example of HTML returned:
     *       // <ul class="bb-tooltip">
     *       //   <li class="bb-tooltip-name-data1"><span>250</span><br><span style="color:#00c73c">data1</span></li>
     *       //   <li class="bb-tooltip-name-data2"><span>50</span><br><span style="color:#fa7171">data2</span></li>
     *       // </ul>
     *       contents: {
     *      	bindto: "#tooltip",
     *      	template: '<ul class={=CLASS_TOOLTIP}>{{' +
     *      			'<li class="{=CLASS_TOOLTIP_NAME}"><span>{=VALUE}</span><br>' +
     *      			'<span style=color:{=COLOR}>{=NAME}</span></li>' +
     *      		'}}</ul>'
     *      }
     *
     *       // with additional text value
     *       // - example of HTML returned:
     *       // <ul class="bb-tooltip">
     *       //   <li class="bb-tooltip-name-data1"><span>250</span><br>comment1<span style="color:#00c73c">data1</span>text1</li>
     *       //   <li class="bb-tooltip-name-data2"><span>50</span><br>comment2<span style="color:#fa7171">data2</span>text2</li>
     *       // </ul>
     *       contents: {
     *      	bindto: "#tooltip",
     *      	text: {
     *      		// a) 'key' name is used as substitution within template as '{=KEY}'
     *      		// b) the length should match with the data length
     *      		VAR1: ["text1", "text2"],
     *      		VAR2: ["comment1", "comment2"],
     *      	},
     *      	template: '<ul class={=CLASS_TOOLTIP}>{{' +
     *      			'<li class="{=CLASS_TOOLTIP_NAME}"><span>{=VALUE}</span>{=VAR2}<br>' +
     *      			'<span style=color:{=COLOR}>{=NAME}</span>{=VAR1}</li>' +
     *      		'}}</ul>'
     *      }
     		 *
     		 *      // sort tooltip data value display in ascending order
     		 *      order: "asc",
     		 *
     *      // specifying sort function
     *      order: function(a, b) {
     *         // param data passed format
     *         {x: 5, value: 250, id: "data1", index: 5, name: "data1"}
     *           ...
     *      },
     *
     *      // show at the initialization
     *      init: {
     *          show: true,
     *          x: 2,
     *          position: {
     *              top: "150px",
     *              left: "250px"
     *          }
     *      },
     *
     *      // fires prior tooltip is shown
     *      onshow: function(ctx, selectedData) {
     *      	ctx; // current chart instance
     *
     *      	// current dataset selected
     *      	// ==> [{x: 4, value: 150, id: "data2", index: 4, name: "data2"}, ...]
     *      	selectedData;
     *      },
     *
     *      // fires prior tooltip is hidden
     *      onhide: function(ctx, selectedData) {
     *      	ctx; // current chart instance
     *
     *      	// current dataset selected
     *      	// ==> [{x: 4, value: 150, id: "data2", index: 4, name: "data2"}, ...]
     *      	selectedData;
     *      },
     *
     *      // fires after tooltip is shown
     *      onshown: function(ctx, selectedData) {
     *      	ctx; // current chart instance
     *
     *      	// current dataset selected
     *      	// ==> [{x: 4, value: 150, id: "data2", index: 4, name: "data2"}, ...]
     *      	selectedData;
     *      },
     *
     *      // fires after tooltip is hidden
     *      onhidden: function(ctx, selectedData) {
     *      	ctx; // current chart instance
     *
     *      	// current dataset selected
     *      	// ==> [{x: 4, value: 150, id: "data2", index: 4, name: "data2"}, ...]
     *      	selectedData;
     *      },
     *
     *      // Link any tooltips when multiple charts are on the screen where same x coordinates are available
     *      // Useful for timeseries correlation
     *      linked: true,
     *
     *      // Specify name to interact those with the same name only.
     *      linked: {
     *          name: "some-group"
     *      }
     *  }
     */
tooltip_show:!0,tooltip_doNotHide:!1,tooltip_grouped:!0,tooltip_format_title:void 0,tooltip_format_name:void 0,tooltip_format_value:void 0,tooltip_position:void 0,tooltip_contents:{},tooltip_init_show:!1,tooltip_init_x:0,tooltip_init_position:{top:"0px",left:"50px"},tooltip_linked:!1,tooltip_linked_name:"",tooltip_onshow:function(){},tooltip_onhide:function(){},tooltip_onshown:function(){},tooltip_onhidden:function(){},tooltip_order:null,
/**
     * Set title options
     * @name title
     * @memberof Options
     * @type {Object}
     * @property {String} [title.text] Title text. If contains `\n`, it's used as line break allowing multiline title.
     * @property {Number} [title.padding.top=0] Top padding value.
     * @property {Number} [title.padding.right=0] Right padding value.
     * @property {Number} [title.padding.bottom=0] Bottom padding value.
     * @property {Number} [title.padding.left=0] Left padding value.
     * @property {String} [title.position=center] Available values are: 'center', 'right' and 'left'.
     * @see [Demo](https://naver.github.io/billboard.js/demo/#Title.MultilinedTitle)
     * @example
     *  title: {
     *      text: "Title Text",
     *
     *      // or Multiline title text
     *      text: "Main title text\nSub title text",
     *
     *      padding: {
     *          top: 10,
     *          right: 10,
     *          bottom: 10,
     *          left: 10
     *      },
     *      position: "center"
     *  }
     */
title_text:void 0,title_padding:{top:0,right:0,bottom:0,left:0},title_position:"center"}};
// CONCATENATED MODULE: ./src/internals/browser.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
/**
 * Window object
 * @module
 * @ignore
 */
/* eslint-disable no-new-func, no-undef */
// CONCATENATED MODULE: ./src/config/config.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(ChartInternal_ChartInternal.prototype,{getOptions:function(){return new Options_Options},
/**
   * Load configuration option
   * @param {Object} config User's generation config value
   * @private
   */
loadConfig:function(config){var target,keys,read,thisConfig=this.config,find=function(){var key=keys.shift();return key&&target&&isObjectType(target)&&key in target?(target=target[key],find()):key?void 0:target};Object.keys(thisConfig).forEach((function(key){target=config,keys=key.split("_"),read=find(),isDefined(read)&&(thisConfig[key]=read)}))}}),
// CONCATENATED MODULE: ./src/internals/scale.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(ChartInternal_ChartInternal.prototype,{getScale:function(min,max,forTimeseries){return(forTimeseries?Object(external_commonjs_d3_scale_commonjs2_d3_scale_amd_d3_scale_root_d3_["scaleTime"])():Object(external_commonjs_d3_scale_commonjs2_d3_scale_amd_d3_scale_root_d3_["scaleLinear"])()).range([min,max])},
/**
   * Get x Axis scale function
   * @param {Number} min
   * @param {Number} max
   * @param {Number} domain
   * @param {Function} offset The offset getter to be sum
   * @return {Function} scale
   * @private
   */
getX:function(min,max,domain,offset){var $$=this,scale=$$.zoomScale||$$.getScale(min,max,$$.isTimeSeries());return $$.getCustomizedScale(domain?scale.domain(domain):scale,offset)},
/**
   * Get y Axis scale function
   * @param {Number} min
   * @param {Number} max
   * @param {Number} domain
   * @return {Function} scale
   * @private
   */
getY:function(min,max,domain){var scale=this.getScale(min,max,this.isTimeSeriesY());return domain&&scale.domain(domain),scale},
/**
   * Get customized scale
   * @param {d3.scaleLinear|d3.scaleTime} scaleValue
   * @param {Function} offsetValue Offset getter to be sum
   * @return {} scale
   * @private
   */
getCustomizedScale:function(scaleValue,offsetValue){var $$=this,offset=offsetValue||function(){return $$.xAxis.tickOffset()},scale=function(d,raw){var v=scaleValue(d)+offset();return raw?v:Math.ceil(v)};
// copy original scale methods
for(var key in scaleValue)scale[key]=scaleValue[key];return scale.orgDomain=function(){return scaleValue.domain()},scale.orgScale=function(){return scaleValue},$$.isCategorized()&&(scale.domain=function(domainValue){var domain=domainValue;return arguments.length?(scaleValue.domain(domain),scale):(domain=this.orgDomain(),[domain[0],domain[1]+1])}),scale},getYScale:function(id){return"y2"===this.axis.getId(id)?this.y2:this.y},getSubYScale:function(id){return"y2"===this.axis.getId(id)?this.subY2:this.subY},
/**
   * Update scale
   * @private
   * @param {Boolean} isInit - param is given at the init rendering
   */
updateScales:function(isInit){var updateXDomain=!(arguments.length>1&&void 0!==arguments[1])||arguments[1],$$=this,config=$$.config,isRotated=config.axis_rotated;$$.xMin=isRotated?1:0,$$.xMax=isRotated?$$.height:$$.width,$$.yMin=isRotated?0:$$.height,$$.yMax=isRotated?$$.width:1,$$.subXMin=$$.xMin,$$.subXMax=$$.xMax,$$.subYMin=isRotated?0:$$.height2,$$.subYMax=isRotated?$$.width2:1;
// update scales
// x Axis
var xDomain=updateXDomain&&$$.x&&$$.x.orgDomain(),xSubDomain=updateXDomain&&$$.orgXDomain;
// y Axis
// update for arc
$$.x=$$.getX($$.xMin,$$.xMax,xDomain,(function(){return $$.xAxis.tickOffset()})),$$.subX=$$.getX($$.xMin,$$.xMax,xSubDomain,(function(d){return d%1?0:$$.subXAxis.tickOffset()})),$$.xAxisTickFormat=$$.axis.getXAxisTickFormat(),$$.xAxisTickValues=$$.axis.getTickValues("x"),$$.xAxis=$$.axis.getAxis("x",$$.x,config.axis_x_tick_outer,isInit),$$.subXAxis=$$.axis.getAxis("subX",$$.subX,config.axis_x_tick_outer,isInit),$$.y=$$.getY($$.yMin,$$.yMax,$$.y?$$.y.domain():config.axis_y_default),$$.subY=$$.getY($$.subYMin,$$.subYMax,$$.subY?$$.subY.domain():config.axis_y_default),$$.yAxisTickValues=$$.axis.getTickValues("y"),$$.yAxis=$$.axis.getAxis("y",$$.y,config.axis_y_tick_outer,isInit),config.axis_y2_show&&($$.y2=$$.getY($$.yMin,$$.yMax,$$.y2?$$.y2.domain():config.axis_y2_default),$$.subY2=$$.getY($$.subYMin,$$.subYMax,$$.subY2?$$.subY2.domain():config.axis_y2_default),$$.y2AxisTickValues=$$.axis.getTickValues("y2"),$$.y2Axis=$$.axis.getAxis("y2",$$.y2,config.axis_y2_tick_outer,isInit)),$$.updateArc&&$$.updateArc()}}),
// CONCATENATED MODULE: ./src/internals/domain.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(ChartInternal_ChartInternal.prototype,{getYDomainMinMax:function(targets,type){var $$=this,config=$$.config,isMin="min"===type,dataGroups=config.data_groups,ids=$$.mapToIds(targets),ys=$$.getValuesAsIdKeyed(targets);return dataGroups.length>0&&function(){for(var idsInGroup,hasValue=$$["has".concat(isMin?"Negative":"Positive","ValueInTargets")](targets),_loop=function(j,_idsInGroup){if(_idsInGroup=_idsInGroup.filter((function(v){return ids.indexOf(v)>=0})),0===_idsInGroup.length)return idsInGroup=_idsInGroup,"continue";var baseId=_idsInGroup[0],baseAxisId=$$.axis.getId(baseId);hasValue&&ys[baseId]&&(ys[baseId]=ys[baseId].map((function(v){return(isMin?v<0:v>0)?v:0})));for(var id,_loop2=function(k,id){if(!ys[id])return"continue";var axisId=$$.axis.getId(id);ys[id].forEach((function(v,i){var val=+v,meetCondition=isMin?val>0:val<0;axisId!==baseAxisId||hasValue&&meetCondition||(ys[baseId][i]+=val)}))},k=1;id=_idsInGroup[k];k++)_loop2(k,id);idsInGroup=_idsInGroup},j=0;idsInGroup=dataGroups[j];j++)_loop(j,idsInGroup)}(),getMinMax(type,Object.keys(ys).map((function(key){return getMinMax(type,ys[key])})))},getYDomainMin:function(targets){return this.getYDomainMinMax(targets,"min")},getYDomainMax:function(targets){return this.getYDomainMinMax(targets,"max")},
/**
   * Check if hidden targets bound to the given axis id
   * @return {Boolean}
   * @private
   */
isHiddenTargetWithYDomain:function(id){var $$=this;return $$.hiddenTargetIds.some((function(v){return $$.axis.getId(v)===id}))},getYDomain:function(targets,axisId,xDomain){var $$=this,config=$$.config,pfx="axis_".concat(axisId);if($$.isStackNormalized())return[0,100];var targetsByAxisId=targets.filter((function(t){return $$.axis.getId(t.id)===axisId})),yTargets=xDomain?$$.filterByXDomain(targetsByAxisId,xDomain):targetsByAxisId;if(0===yTargets.length)return $$.isHiddenTargetWithYDomain(axisId)?$$[axisId].domain():"y2"===axisId?$$.y.domain():// When all data bounds to y2, y Axis domain is called prior y2.
// So, it needs to call to get y2 domain here
$$.getYDomain(targets,"y2",xDomain);var yMin=config["".concat(pfx,"_min")],yMax=config["".concat(pfx,"_max")],yDomainMin=$$.getYDomainMin(yTargets),yDomainMax=$$.getYDomainMax(yTargets),center=config["".concat(pfx,"_center")],isZeroBased=["area","bar","bubble","line","scatter"].some((function(v){return $$.hasType(v,yTargets)&&config["".concat(v,"_zerobased")]})),isInverted=config["".concat(pfx,"_inverted")],showHorizontalDataLabel=$$.hasDataLabel()&&config.axis_rotated,showVerticalDataLabel=$$.hasDataLabel()&&!config.axis_rotated;yDomainMin=isValue(yMin)?yMin:isValue(yMax)?yDomainMin<yMax?yDomainMin:yMax-10:yDomainMin,yDomainMax=isValue(yMax)?yMax:isValue(yMin)?yMin<yDomainMax?yDomainMax:yMin+10:yDomainMax,isNaN(yDomainMin)&&(yDomainMin=0),isNaN(yDomainMax)&&(yDomainMax=yDomainMin),yDomainMin===yDomainMax&&(yDomainMin<0?yDomainMax=0:yDomainMin=0);var isAllPositive=yDomainMin>=0&&yDomainMax>=0,isAllNegative=yDomainMin<=0&&yDomainMax<=0;(isValue(yMin)&&isAllPositive||isValue(yMax)&&isAllNegative)&&(isZeroBased=!1),isZeroBased&&(isAllPositive&&(yDomainMin=0),isAllNegative&&(yDomainMax=0));var domainLength=Math.abs(yDomainMax-yDomainMin),padding={top:.1*domainLength,bottom:.1*domainLength};if(isDefined(center)){var yDomainAbs=Math.max(Math.abs(yDomainMin),Math.abs(yDomainMax));yDomainMax=center+yDomainAbs,yDomainMin=center-yDomainAbs}// add padding for data label
if(showHorizontalDataLabel){var diff=diffDomain($$.y.range()),ratio=$$.getDataLabelLength(yDomainMin,yDomainMax,"width").map((function(v){return v/diff}));["bottom","top"].forEach((function(v,i){padding[v]+=domainLength*(ratio[i]/(1-ratio[0]-ratio[1]))}))}else if(showVerticalDataLabel){var lengths=$$.getDataLabelLength(yDomainMin,yDomainMax,"height");["bottom","top"].forEach((function(v,i){padding[v]+=$$.axis.convertPixelsToAxisPadding(lengths[i],domainLength)}))}// if padding is set, the domain will be updated relative the current domain value
// ex) $$.height=300, padding.top=150, domainLength=4  --> domain=6
var p=config["".concat(pfx,"_padding")];notEmpty(p)&&["bottom","top"].forEach((function(v){padding[v]=$$.axis.getPadding(p,v,padding[v],domainLength)})),isZeroBased&&(isAllPositive&&(padding.bottom=yDomainMin),isAllNegative&&(padding.top=-yDomainMax));var domain=[yDomainMin-padding.bottom,yDomainMax+padding.top];return isInverted?domain.reverse():domain},getXDomainMinMax:function(targets,type){var $$=this,configValue=$$.config["axis_x_".concat(type)],dataValue=getMinMax(type,targets.map((function(t){return getMinMax(type,t.values.map((function(v){return v.x})))}))),value=isObject(configValue)?configValue.value:configValue;return value=isDefined(value)&&$$.isTimeSeries()?$$.parseDate(value):value,isObject(configValue)&&configValue.fit&&("min"===type&&value<dataValue||"max"===type&&value>dataValue)&&(value=void 0),isDefined(value)?value:dataValue},getXDomainMin:function(targets){return this.getXDomainMinMax(targets,"min")},getXDomainMax:function(targets){return this.getXDomainMinMax(targets,"max")},getXDomainPadding:function(domain){var maxDataCount,padding,$$=this,config=$$.config,diff=domain[1]-domain[0],xPadding=config.axis_x_padding;$$.isCategorized()?padding=0:$$.hasType("bar")?(maxDataCount=$$.getMaxDataCount(),padding=maxDataCount>1?diff/(maxDataCount-1)/2:.5):padding=.01*diff;var left=padding,right=padding;return isObject(xPadding)&&notEmpty(xPadding)?(left=isValue(xPadding.left)?xPadding.left:padding,right=isValue(xPadding.right)?xPadding.right:padding):isNumber(config.axis_x_padding)&&(left=xPadding,right=xPadding),{left:left,right:right}},getXDomain:function(targets){var $$=this,xDomain=[$$.getXDomainMin(targets),$$.getXDomainMax(targets)],_xDomain2=_slicedToArray(xDomain,2),firstX=_xDomain2[0],lastX=_xDomain2[1],padding=$$.getXDomainPadding(xDomain),min=0,max=0;return firstX-lastX!==0||$$.isCategorized()||($$.isTimeSeries()?(firstX=new Date(.5*firstX.getTime()),lastX=new Date(1.5*lastX.getTime())):(firstX=0===firstX?1:.5*firstX,lastX=0===lastX?-1:1.5*lastX)),(firstX||0===firstX)&&(min=$$.isTimeSeries()?new Date(firstX.getTime()-padding.left):firstX-padding.left),(lastX||0===lastX)&&(max=$$.isTimeSeries()?new Date(lastX.getTime()+padding.right):lastX+padding.right),[min,max]},updateXDomain:function(targets,withUpdateXDomain,withUpdateOrgXDomain,withTrim,domain){var $$=this,config=$$.config,zoomEnabled=config.zoom_enabled;if(withUpdateOrgXDomain&&($$.x.domain(domain||sortValue($$.getXDomain(targets))),$$.orgXDomain=$$.x.domain(),zoomEnabled&&$$.zoom.updateScaleExtent(),$$.subX.domain($$.x.domain()),$$.brush&&$$.brush.scale($$.subX)),withUpdateXDomain){var domainValue=domain||!$$.brush||brushEmpty($$)?$$.orgXDomain:getBrushSelection($$).map($$.subX.invert);$$.x.domain(domainValue),zoomEnabled&&$$.zoom.updateScaleExtent()}// Trim domain when too big by zoom mousemove event
return withTrim&&$$.x.domain($$.trimXDomain($$.x.orgDomain())),$$.x.domain()},trimXDomain:function(domain){var zoomDomain=this.getZoomDomain(),_zoomDomain=_slicedToArray(zoomDomain,2),min=_zoomDomain[0],max=_zoomDomain[1];return domain[0]<=min&&(domain[1]=+domain[1]+(min-domain[0]),domain[0]=min),max<=domain[1]&&(domain[0]=+domain[0]-(domain[1]-max),domain[1]=max),domain}}),
// CONCATENATED MODULE: ./src/data/data.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(ChartInternal_ChartInternal.prototype,{isX:function(key){var $$=this,config=$$.config,dataKey=config.data_x&&key===config.data_x,existValue=notEmpty(config.data_xs)&&util_hasValue(config.data_xs,key);return dataKey||existValue},isNotX:function(key){return!this.isX(key)},isStackNormalized:function(){var config=this.config;return config.data_stack_normalize&&this.isGrouped()},isGrouped:function(id){var groups=this.config.data_groups;return id?groups.some((function(v){return v.indexOf(id)>=0&&v.length>1})):groups.length>0},getXKey:function(id){var $$=this,config=$$.config;return config.data_x?config.data_x:notEmpty(config.data_xs)?config.data_xs[id]:null},getXValuesOfXKey:function(key,targets){var xValues,$$=this,ids=targets&&notEmpty(targets)?$$.mapToIds(targets):[];return ids.forEach((function(id){$$.getXKey(id)===key&&(xValues=$$.data.xs[id])})),xValues},
/**
   * Get index number based on given x Axis value
   * @param {Date|Number|String} x x Axis to be compared
   * @param {Array} basedX x Axis list to be based on
   * @return {Number} index number
   * @private
   */
getIndexByX:function(x,basedX){var $$=this;return basedX?basedX.indexOf(isString(x)?x:+x):($$.filterByX($$.data.targets,x)[0]||{index:null}).index},getXValue:function(id,i){var $$=this;return id in $$.data.xs&&$$.data.xs[id]&&isValue($$.data.xs[id][i])?$$.data.xs[id][i]:i},getOtherTargetXs:function(){var $$=this,idsForX=Object.keys($$.data.xs);return idsForX.length?$$.data.xs[idsForX[0]]:null},getOtherTargetX:function(index){var xs=this.getOtherTargetXs();return xs&&index<xs.length?xs[index]:null},addXs:function(xs){var $$=this;Object.keys(xs).forEach((function(id){$$.config.data_xs[id]=xs[id]}))},isMultipleX:function(){return notEmpty(this.config.data_xs)||!this.config.data_xSort||this.hasType("bubble")||this.hasType("scatter")},addName:function(data){var name,$$=this;return data&&(name=$$.config.data_names[data.id],data.name=void 0===name?data.id:name),data},getAllValuesOnIndex:function(index){var $$=this;return $$.filterTargetsToShow($$.data.targets).map((function(t){return $$.addName($$.getValueOnIndex(t.values,index))}))},getValueOnIndex:function(values,index){var valueOnIndex=values.filter((function(v){return v.index===index}));return valueOnIndex.length?valueOnIndex[0]:null},updateTargetX:function(targets,x){var $$=this;targets.forEach((function(t){t.values.forEach((function(v,i){v.x=$$.generateTargetX(x[i],t.id,i)})),$$.data.xs[t.id]=x}))},updateTargetXs:function(targets,xs){var $$=this;targets.forEach((function(t){xs[t.id]&&$$.updateTargetX([t],xs[t.id])}))},generateTargetX:function(rawX,id,index){var $$=this,x=$$.isCategorized()?index:rawX||index;return $$.isTimeSeries()?x=rawX?$$.parseDate(rawX):$$.parseDate($$.getXValue(id,index)):$$.isCustomX()&&!$$.isCategorized()&&(x=isValue(rawX)?+rawX:$$.getXValue(id,index)),x},cloneTarget:function(target){return{id:target.id,id_org:target.id_org,values:target.values.map((function(d){return{x:d.x,value:d.value,id:d.id}}))}},updateXs:function(values){values.length&&(this.xs=values.map((function(v){return v.x})))},getPrevX:function(i){var x=this.xs[i-1];return isDefined(x)?x:null},getNextX:function(i){var x=this.xs[i+1];return isDefined(x)?x:null},
/**
   * Get base value isAreaRangeType
   * @param data Data object
   * @return {Number}
   * @private
   */
getBaseValue:function(data){var $$=this,value=data.value;return value&&($$.isAreaRangeType(data)?value=$$.getAreaRangeData(data,"mid"):$$.isBubbleZType(data)&&(value=$$.getBubbleZData(value,"y"))),value},
/**
   * Get min/max value from the data
   * @private
   * @param {Array} data array data to be evaluated
   * @return {{min: {Number}, max: {Number}}}
   */
getMinMaxValue:function(data){var min,max,getBaseValue=this.getBaseValue.bind(this);return(data||this.data.targets.map((function(t){return t.values}))).forEach((function(v,i){var value=v.map(getBaseValue).filter(isNumber);min=Math.min.apply(Math,[i?min:1/0].concat(_toConsumableArray(value))),max=Math.max.apply(Math,[i?max:-1/0].concat(_toConsumableArray(value)))})),{min:min,max:max}},
/**
   * Get the min/max data
   * @private
   * @return {{min: Array, max: Array}}
   */
getMinMaxData:function(){var $$=this,cacheKey="$minMaxData",minMaxData=$$.getCache(cacheKey);if(!minMaxData){var data=$$.data.targets.map((function(t){return t.values})),minMax=$$.getMinMaxValue(data),min=[],max=[];
// update the cached data
data.forEach((function(v){var minData=$$.getFilteredDataByValue(v,minMax.min),maxData=$$.getFilteredDataByValue(v,minMax.max);minData.length&&(min=min.concat(minData)),maxData.length&&(max=max.concat(maxData))})),$$.addCache(cacheKey,minMaxData={min:min,max:max})}return minMaxData},
/**
   * Get sum of data per index
   * @private
   * @return {Array}
   */
getTotalPerIndex:function(){var $$=this,sum=$$.getCache("$totalPerIndex");return $$.isStackNormalized()&&!sum&&(sum=[],$$.data.targets.forEach((function(row){row.values.forEach((function(v,i){sum[i]||(sum[i]=0),sum[i]+=isNumber(v.value)?v.value:0}))}))),sum},
/**
   * Get total data sum
   * @param {boolean} subtractHidden Subtract hidden data from total
   * @return {Number}
  	 * @private
   */
getTotalDataSum:function(subtractHidden){var $$=this,cacheKey="$totalDataSum",total=$$.getCache(cacheKey);if(!isNumber(total)){var sum=mergeArray($$.data.targets.map((function(t){return t.values}))).map((function(v){return v.value})).reduce((function(p,c){return p+c}));$$.addCache(cacheKey,total=sum)}return subtractHidden&&(total-=$$.getHiddenTotalDataSum()),total},
/**
   * Get total hidden data sum
   * @return {Number}
  	 * @private
   */
getHiddenTotalDataSum:function(){var $$=this,api=$$.api,hiddenTargetIds=$$.hiddenTargetIds,total=0;return hiddenTargetIds.length&&(total=api.data.values.bind(api)(hiddenTargetIds).reduce((function(p,c){return p+c}))),total},
/**
   * Get filtered data by value
   * @param {Object} data
   * @param {Number} value
   * @return {Array} filtered array data
   * @private
   */
getFilteredDataByValue:function(data,value){var _this=this;return data.filter((function(t){return _this.getBaseValue(t)===value}))},
/**
   * Return the max length of the data
   * @return {Number} max data length
   * @private
   */
getMaxDataCount:function(){return Math.max.apply(Math,_toConsumableArray(this.data.targets.map((function(t){return t.values.length}))))},getMaxDataCountTarget:function(){var target=this.filterTargetsToShow()||[],length=target.length;return length>1?(target=target.map((function(t){return t.values})).reduce((function(a,b){return a.concat(b)})).map((function(v){return v.x})),target=sortValue(getUnique(target)).map((function(x,index){return{x:x,index:index}}))):length&&(target=target[0].values),target},mapToIds:function(targets){return targets.map((function(d){return d.id}))},mapToTargetIds:function(ids){var $$=this;return ids?isArray(ids)?ids.concat():[ids]:$$.mapToIds($$.data.targets)},hasTarget:function(targets,id){for(var val,ids=this.mapToIds(targets),i=0;val=ids[i];i++)if(val===id)return!0;return!1},isTargetToShow:function(targetId){return this.hiddenTargetIds.indexOf(targetId)<0},isLegendToShow:function(targetId){return this.hiddenLegendIds.indexOf(targetId)<0},filterTargetsToShow:function(targets){var $$=this;return(targets||$$.data.targets).filter((function(t){return $$.isTargetToShow(t.id)}))},mapTargetsToUniqueXs:function(targets){var $$=this,xs=[];return targets&&targets.length&&(xs=getUnique(mergeArray(targets.map((function(t){return t.values.map((function(v){return+v.x}))})))),xs=$$.isTimeSeries()?xs.map((function(x){return new Date(+x)})):xs.map((function(x){return+x}))),sortValue(xs)},addHiddenTargetIds:function(targetIds){this.hiddenTargetIds=this.hiddenTargetIds.concat(targetIds)},removeHiddenTargetIds:function(targetIds){this.hiddenTargetIds=this.hiddenTargetIds.filter((function(id){return targetIds.indexOf(id)<0}))},addHiddenLegendIds:function(targetIds){this.hiddenLegendIds=this.hiddenLegendIds.concat(targetIds)},removeHiddenLegendIds:function(targetIds){this.hiddenLegendIds=this.hiddenLegendIds.filter((function(id){return targetIds.indexOf(id)<0}))},getValuesAsIdKeyed:function(targets){var $$=this,ys={},isMultipleX=$$.isMultipleX(),xs=isMultipleX?$$.mapTargetsToUniqueXs(targets).map((function(v){return isString(v)?v:+v})):null;return targets.forEach((function(t){var data=[];t.values.forEach((function(v){var value=v.value;isArray(value)?data.push.apply(data,_toConsumableArray(value)):isObject(value)&&"high"in value?data.push.apply(data,_toConsumableArray(Object.values(value))):$$.isBubbleZType(v)?data.push($$.getBubbleZData(value,"y")):isMultipleX?data[$$.getIndexByX(v.x,xs)]=value:data.push(value)})),ys[t.id]=data})),ys},checkValueInTargets:function(targets,checker){for(var values,ids=Object.keys(targets),i=0;i<ids.length;i++){values=targets[ids[i]].values;for(var j=0;j<values.length;j++)if(checker(values[j].value))return!0}return!1},hasMultiTargets:function(){return this.filterTargetsToShow().length>1},hasNegativeValueInTargets:function(targets){return this.checkValueInTargets(targets,(function(v){return v<0}))},hasPositiveValueInTargets:function(targets){return this.checkValueInTargets(targets,(function(v){return v>0}))},_checkOrder:function(type){var config=this.config,order=config.data_order;return isString(order)&&order.toLowerCase()===type},isOrderDesc:function(){return this._checkOrder("desc")},isOrderAsc:function(){return this._checkOrder("asc")},
/**
   * Sort targets data
   * @param {Array} targetsValue
   * @return {Array}
   * @private
   */
orderTargets:function(targetsValue){var $$=this,config=$$.config,targets=_toConsumableArray(targetsValue),orderAsc=$$.isOrderAsc(),orderDesc=$$.isOrderDesc();
// TODO: accept name array for order
return orderAsc||orderDesc?targets.sort((function(t1,t2){var reducer=function(p,c){return p+Math.abs(c.value)},t1Sum=t1.values.reduce(reducer,0),t2Sum=t2.values.reduce(reducer,0);return orderAsc?t2Sum-t1Sum:t1Sum-t2Sum})):isFunction(config.data_order)&&targets.sort(config.data_order),targets},filterByX:function(targets,x){return mergeArray(targets.map((function(t){return t.values}))).filter((function(v){return v.x-x===0}))},filterRemoveNull:function(data){var _this2=this;return data.filter((function(d){return isValue(_this2.getBaseValue(d))}))},filterByXDomain:function(targets,xDomain){return targets.map((function(t){return{id:t.id,id_org:t.id_org,values:t.values.filter((function(v){return xDomain[0]<=v.x&&v.x<=xDomain[1]}))}}))},hasDataLabel:function(){var dataLabels=this.config.data_labels;return isBoolean(dataLabels)&&dataLabels||isObjectType(dataLabels)&&notEmpty(dataLabels)},getDataLabelLength:function(min,max,key){var $$=this,lengths=[0,0];return $$.selectChart.select("svg").selectAll(".dummy").data([min,max]).enter().append("text").text((function(d){return $$.dataLabelFormat(d.id)(d)})).each((function(d,i){lengths[i]=1.3*this.getBoundingClientRect()[key]})).remove(),lengths},isNoneArc:function(d){return this.hasTarget(this.data.targets,d.id)},isArc:function(d){return"data"in d&&this.hasTarget(this.data.targets,d.data.id)},findSameXOfValues:function(values,index){var i,targetX=values[index].x,sames=[];for(i=index-1;i>=0&&targetX===values[i].x;i--)sames.push(values[i]);for(i=index;i<values.length&&targetX===values[i].x;i++)sames.push(values[i]);return sames},findClosestFromTargets:function(targets,pos){var $$=this,candidates=targets.map((function(target){return $$.findClosest(target.values,pos)}));
// map to array of closest points of each target
// decide closest point and return
return $$.findClosest(candidates,pos)},findClosest:function(values,pos){var closest,$$=this,data=values.filter((function(v){return v&&isValue(v.value)})),minDist=$$.config.point_sensitivity;return data.filter((function(v){return $$.isBarType(v.id)})).forEach((function(v){var shape=$$.main.select(".".concat(config_classes.bars).concat($$.getTargetSelectorSuffix(v.id)," .").concat(config_classes.bar,"-").concat(v.index)).node();!closest&&$$.isWithinBar(shape)&&(closest=v)})),data.filter((function(v){return!$$.isBarType(v.id)})).forEach((function(v){var d=$$.dist(v,pos);d<minDist&&(minDist=d,closest=v)})),closest},dist:function(data,pos){var $$=this,isRotated=$$.config.axis_rotated,xIndex=isRotated?1:0,yIndex=isRotated?0:1,y=$$.circleY(data,data.index),x=($$.zoomScale||$$.x)(data.x);return Math.sqrt(Math.pow(x-pos[xIndex],2)+Math.pow(y-pos[yIndex],2))},
/**
   * Convert data for step type
   * @param {Array} values Object data values
   * @return {Array}
   * @private
   */
convertValuesToStep:function(values){var $$=this,config=$$.config,isRotated=config.axis_rotated,stepType=config.line_step_type,isCategorized=$$.isCategorized(),converted=isArray(values)?values.concat():[values];if(!isRotated&&!isCategorized)return values;// insert & append cloning first/last value to be fully rendered covering on each gap sides
var id=converted[0].id,x=converted[0].x-1,value=converted[0].value;// insert
return isCategorized&&converted.unshift({x:x,value:value,id:id}),"step-after"===stepType&&converted.unshift({x:x-1,value:value,id:id}),x=converted.length-1,value=converted[x].value,isCategorized&&converted.push({x:x,value:value,id:id}),"step-before"===stepType&&converted.push({x:x+1,value:value,id:id}),converted},convertValuesToRange:function(values){var converted=isArray(values)?values.concat():[values],ranges=[];return converted.forEach((function(range){var x=range.x,id=range.id;ranges.push({x:x,id:id,value:range.value[0]}),ranges.push({x:x,id:id,value:range.value[2]})})),ranges},updateDataAttributes:function(name,attrs){var $$=this,config=$$.config,current=config["data_".concat(name)];return isUndefined(attrs)||(Object.keys(attrs).forEach((function(id){current[id]=attrs[id]})),$$.redraw({withLegend:!0})),current},getAreaRangeData:function(d,type){var value=d.value;if(isArray(value)){var index=["high","mid","low"].indexOf(type);return-1===index?null:value[index]}return value[type]},
/**
   * Get ratio value
   * @param {String} type Ratio for given type
   * @param {Object} d Data value object
   * @param {Boolean} asPercent Convert the return as percent or not
   * @return {Number} Ratio value
   * @private
   */
getRatio:function(type,d,asPercent){var $$=this,config=$$.config,api=$$.api,ratio=0;if(d&&api.data.shown.call(api).length)if(ratio=d.ratio||d.value,"arc"===type)ratio=$$.pie.padAngle()()?d.value/$$.getTotalDataSum(!0):(d.endAngle-d.startAngle)/(Math.PI*($$.hasType("gauge")&&!config.gauge_fullCircle?1:2));else if("index"===type){var dataValues=api.data.values.bind(api),total=this.getTotalPerIndex();if($$.hiddenTargetIds.length){var hiddenSum=dataValues($$.hiddenTargetIds,!1);hiddenSum.length&&(hiddenSum=hiddenSum.reduce((function(acc,curr){return acc.map((function(v,i){return(isNumber(v)?v:0)+curr[i]}))})),total=total.map((function(v,i){return v-hiddenSum[i]})))}d.ratio=isNumber(d.value)&&total&&total[d.index]>0?d.value/total[d.index]:0,ratio=d.ratio}else"radar"===type&&(ratio=parseFloat(Math.max(d.value,0))/$$.maxValue*config.radar_size_ratio);return asPercent&&ratio?100*ratio:ratio},
/**
   * Sort data index to be aligned with x axis.
   * @param {Array} tickValues Tick array values
   * @private
   */
updateDataIndexByX:function(tickValues){var $$=this,tickValueMap=tickValues.reduce((function(out,tick,index){return out[+tick.x]=index,out}),{});$$.data.targets.forEach((function(t){t.values.forEach((function(value,valueIndex){var index=tickValueMap[+value.x];void 0===index&&(index=valueIndex),value.index=index}))}))}});
// EXTERNAL MODULE: external {"commonjs":"d3-dsv","commonjs2":"d3-dsv","amd":"d3-dsv","root":"d3"}
var external_commonjs_d3_dsv_commonjs2_d3_dsv_amd_d3_dsv_root_d3_=__nested_webpack_require_7012__(8),convertCsvTsvToData=function(parser,xsv){var d,rows=parser.rows(xsv);return 1===rows.length?(d=[{}],rows[0].forEach((function(id){d[0][id]=null}))):d=parser.parse(xsv),d};
// CONCATENATED MODULE: ./src/data/data.convert.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
/**
 * Convert CSV/TSV data
 * @param {Object} parser Parser object
 * @param {Object} xsv Data
 * @private
 * @return {Object}
 */extend(ChartInternal_ChartInternal.prototype,{
/**
   * Convert data according its type
   * @param {Object} args data object
   * @param {Function} [callback] callback for url(XHR) type loading
   * @return {Object}
   * @private
   */
convertData:function(args,callback){var data,$$=this;if(args.bindto?(data={},["url","mimeType","headers","keys","json","keys","rows","columns"].forEach((function(v){var key="data_".concat(v);key in args&&(data[v]=args[key])}))):data=args,data.url&&callback)$$.convertUrlToData(data.url,data.mimeType,data.headers,data.keys,callback);else if(data.json)data=$$.convertJsonToData(data.json,data.keys);else if(data.rows)data=$$.convertRowsToData(data.rows);else if(data.columns)data=$$.convertColumnsToData(data.columns);else if(args.bindto)throw Error("url or json or rows or columns is required.");return isArray(data)&&data},
/**
   * Convert URL data
   * @param {String} url Remote URL
   * @param {String} mimeType MIME type string: json | csv | tsv
   * @param {Object} headers Header object
   * @param {Object} keys Key object
   * @param {Function} done Callback function
   * @private
   */
convertUrlToData:function(url){var _this=this,mimeType=arguments.length>1&&void 0!==arguments[1]?arguments[1]:"csv",headers=arguments.length>2?arguments[2]:void 0,keys=arguments.length>3?arguments[3]:void 0,done=arguments.length>4?arguments[4]:void 0,req=new XMLHttpRequest;req.open("GET",url),headers&&Object.keys(headers).forEach((function(key){req.setRequestHeader(key,headers[key])})),req.onreadystatechange=function(){if(4===req.readyState){if(200!==req.status)throw new Error("".concat(url,": Something went wrong loading!"));var response=req.responseText;response&&done.call(_this,_this["convert".concat(capitalize(mimeType),"ToData")]("json"===mimeType?JSON.parse(response):response,keys))}},req.send()},convertCsvToData:function(xsv){return convertCsvTsvToData({rows:external_commonjs_d3_dsv_commonjs2_d3_dsv_amd_d3_dsv_root_d3_["csvParseRows"],parse:external_commonjs_d3_dsv_commonjs2_d3_dsv_amd_d3_dsv_root_d3_["csvParse"]},xsv)},convertTsvToData:function(tsv){return convertCsvTsvToData({rows:external_commonjs_d3_dsv_commonjs2_d3_dsv_amd_d3_dsv_root_d3_["tsvParseRows"],parse:external_commonjs_d3_dsv_commonjs2_d3_dsv_amd_d3_dsv_root_d3_["tsvParse"]},tsv)},convertJsonToData:function(json,keysParam){var targetKeys,data,_this2=this,config=this.config,newRows=[];if(isArray(json)){var keys=keysParam||config.data_keys;keys.x?(targetKeys=keys.value.concat(keys.x),config.data_x=keys.x):targetKeys=keys.value,newRows.push(targetKeys),json.forEach((function(o){var newRow=targetKeys.map((function(key){
// convert undefined to null because undefined data will be removed in convertDataToTargets()
var v=_this2.findValueInJson(o,key);return isUndefined(v)&&(v=null),v}));newRows.push(newRow)})),data=this.convertRowsToData(newRows)}else Object.keys(json).forEach((function(key){var tmp=json[key].concat();tmp.unshift(key),newRows.push(tmp)})),data=this.convertColumnsToData(newRows);return data},findValueInJson:function(object,path){if(void 0!==object[path])return object[path];var convertedPath=path.replace(/\[(\w+)\]/g,".$1"),pathArray=convertedPath.replace(/^\./,"").split("."),target=object;// convert indexes to properties (replace [] with .)
return pathArray.some((function(k){return!(target=target&&k in target?target[k]:void 0)})),target},convertRowsToData:function(rows){var keys=rows[0],newRows=[];return rows.forEach((function(row,i){if(i>0){var newRow={};row.forEach((function(v,j){if(isUndefined(v))throw new Error("Source data is missing a component at (".concat(i,", ").concat(j,")!"));newRow[keys[j]]=v})),newRows.push(newRow)}})),newRows},convertColumnsToData:function(columns){var newRows=[];return columns.forEach((function(col,i){var key=col[0];col.forEach((function(v,j){if(j>0){if(isUndefined(newRows[j-1])&&(newRows[j-1]={}),isUndefined(v))throw new Error("Source data is missing a component at (".concat(i,", ").concat(j,")!"));newRows[j-1][key]=v}}))})),newRows},convertDataToTargets:function(data,appendXs){var xsData,_this3=this,$$=this,config=$$.config,isTimeSeries=$$.isTimeSeries(),dataKeys=Object.keys(data[0]||{}),ids=dataKeys.length?dataKeys.filter($$.isNotX,$$):[],xs=dataKeys.length?dataKeys.filter($$.isX,$$):[];ids.forEach((function(id){var xKey=_this3.getXKey(id);_this3.isCustomX()||isTimeSeries?xs.indexOf(xKey)>=0?xsData=(appendXs&&$$.data.xs[id]||[]).concat(data.map((function(d){return d[xKey]})).filter(isValue).map((function(rawX,i){return $$.generateTargetX(rawX,id,i)}))):config.data_x?xsData=_this3.getOtherTargetXs():notEmpty(config.data_xs)&&(xsData=$$.getXValuesOfXKey(xKey,$$.data.targets)):xsData=data.map((function(d,i){return i})),xsData&&(_this3.data.xs[id]=xsData)})),ids.forEach((function(id){if(!_this3.data.xs[id])throw new Error('x is not defined for id = "'.concat(id,'".'))}));
// convert to target
var targets=ids.map((function(id,index){var convertedId=config.data_idConverter(id),xKey=$$.getXKey(id),isCategorized=$$.isCustomX()&&$$.isCategorized(),hasCategory=isCategorized&&data.map((function(v){return v.x})).every((function(v){return config.axis_x_categories.indexOf(v)>-1}));return{id:convertedId,id_org:id,values:data.map((function(d,i){var x,rawX=d[xKey],value=d[id];return value=null===value||isNaN(value)||isObject(value)?isArray(value)||isObject(value)?value:null:+value,isCategorized&&0===index&&!isUndefined(rawX)?(!hasCategory&&0===index&&0===i&&(config.axis_x_categories=[]),x=config.axis_x_categories.indexOf(rawX),-1===x&&(x=config.axis_x_categories.length,config.axis_x_categories.push(rawX))):x=$$.generateTargetX(rawX,id,i),(isUndefined(value)||$$.data.xs[id].length<=i)&&(x=void 0),{x:x,value:value,id:convertedId}})).filter((function(v){return isDefined(v.x)}))}}));// finish targets
return targets.forEach((function(t){config.data_xSort&&(t.values=t.values.sort((function(v1,v2){var x1=v1.x||0===v1.x?v1.x:1/0,x2=v2.x||0===v2.x?v2.x:1/0;return x1-x2}))),t.values.forEach((function(v,i){return v.index=i})),$$.data.xs[t.id].sort((function(v1,v2){return v1-v2}))})),$$.hasNegativeValue=$$.hasNegativeValueInTargets(targets),$$.hasPositiveValue=$$.hasPositiveValueInTargets(targets),config.data_type&&$$.setTargetType($$.mapToIds(targets).filter((function(id){return!(id in config.data_types)})),config.data_type),targets.forEach((function(d){return $$.addCache(d.id_org,d,!0)})),targets}}),
// CONCATENATED MODULE: ./src/data/data.load.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(ChartInternal_ChartInternal.prototype,{load:function(rawTargets,args){var $$=this,targets=rawTargets;
// Set targets
// Redraw with new targets
targets&&(args.filter&&(targets=targets.filter(args.filter)),(args.type||args.types)&&targets.forEach((function(t){var type=args.types&&args.types[t.id]||args.type;$$.setTargetType(t.id,type)})),$$.data.targets.forEach((function(d){for(var i=0;i<targets.length;i++)if(d.id===targets[i].id){d.values=targets[i].values,targets.splice(i,1);break}})),$$.data.targets=$$.data.targets.concat(targets)),$$.updateTargets($$.data.targets),$$.redraw({withUpdateOrgXDomain:!0,withUpdateXDomain:!0,withLegend:!0}),args.done&&args.done()},loadFromArgs:function(args){var $$=this;// prevent load when chart is already destroyed
if($$.config){$$.resetCache();var data=args.data||$$.convertData(args,(function(d){return $$.load($$.convertDataToTargets(d),args)}));data&&$$.load($$.convertDataToTargets(data),args)}// reset internally cached data
},unload:function(rawTargetIds,customDoneCb){var $$=this,done=customDoneCb,targetIds=rawTargetIds;
// If no target, call done and return
return $$.resetCache(),done||(done=function(){}),targetIds=targetIds.filter((function(id){return $$.hasTarget($$.data.targets,id)})),targetIds&&0!==targetIds.length?($$.svg.selectAll(targetIds.map((function(id){return $$.selectorTarget(id)}))).transition().style("opacity","0").remove().call($$.endall,done),void targetIds.forEach((function(id){$$.withoutFadeIn[id]=!1,$$.legend&&$$.legend.selectAll(".".concat(config_classes.legendItem).concat($$.getTargetSelectorSuffix(id))).remove(),$$.data.targets=$$.data.targets.filter((function(t){return t.id!==id}))}))):void done()}}),
// CONCATENATED MODULE: ./src/internals/category.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(ChartInternal_ChartInternal.prototype,{
/**
   * Category Name
   * @private
   * @param {Number} index
   * @returns {String} gategory Name
   */
categoryName:function(i){var config=this.config;return i<config.axis_x_categories.length?config.axis_x_categories[i]:i}});
// EXTERNAL MODULE: external {"commonjs":"d3-drag","commonjs2":"d3-drag","amd":"d3-drag","root":"d3"}
var external_commonjs_d3_drag_commonjs2_d3_drag_amd_d3_drag_root_d3_=__nested_webpack_require_7012__(9);
// CONCATENATED MODULE: ./src/interactions/interaction.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */extend(ChartInternal_ChartInternal.prototype,{
/**
   * Initialize the area that detects the event.
   * Add a container for the zone that detects the event.
   * @private
   */
initEventRect:function(){var $$=this;$$.main.select(".".concat(config_classes.chart)).append("g").attr("class",config_classes.eventRects).style("fill-opacity","0")},
/**
   * Redraws the area that detects the event.
   * @private
   */
redrawEventRect:function(){var eventRectUpdate,$$=this,config=$$.config,isMultipleX=$$.isMultipleX(),zoomEnabled=config.zoom_enabled,eventRects=$$.main.select(".".concat(config_classes.eventRects)).style("cursor",zoomEnabled&&"drag"!==zoomEnabled.type?config.axis_rotated?"ns-resize":"ew-resize":null).classed(config_classes.eventRectsMultiple,isMultipleX).classed(config_classes.eventRectsSingle,!isMultipleX);if(eventRects.selectAll(".".concat(config_classes.eventRect)).remove(),$$.eventRect=eventRects.selectAll(".".concat(config_classes.eventRect)),isMultipleX)eventRectUpdate=$$.eventRect.data([0]),eventRectUpdate=$$.generateEventRectsForMultipleXs(eventRectUpdate.enter()).merge(eventRectUpdate);else{
// Set data and update $$.eventRect
var xAxisTickValues=$$.getMaxDataCountTarget();// update data's index value to be alinged with the x Axis
$$.updateDataIndexByX(xAxisTickValues),$$.updateXs(xAxisTickValues),$$.updatePointClass(!0),eventRects.datum(xAxisTickValues),$$.eventRect=eventRects.selectAll(".".concat(config_classes.eventRect)),eventRectUpdate=$$.eventRect.data((function(d){return d})),eventRectUpdate.exit().remove(),eventRectUpdate=$$.generateEventRectsForSingleX(eventRectUpdate.enter()).merge(eventRectUpdate)}$$.eventRect=eventRectUpdate,$$.updateEventRect(eventRectUpdate),"touch"!==$$.inputType||$$.svg.on("touchstart.eventRect")||$$.hasArcType()||$$.bindTouchOnEventRect(isMultipleX)},bindTouchOnEventRect:function(isMultipleX){var startPx,$$=this,config=$$.config,getEventRect=function(){var touch=external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["event"].changedTouches[0];return Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(browser_doc.elementFromPoint(touch.clientX,touch.clientY))},getIndex=function(eventRect){var index=eventRect&&eventRect.attr("class")&&1*eventRect.attr("class").replace(new RegExp("(".concat(config_classes.eventRect,"-?|s)"),"g"),"");return(isNaN(index)||null===index)&&(index=-1),index},selectRect=function(context){if(isMultipleX)$$.selectRectForMultipleXs(context);else{var eventRect=getEventRect(),index=getIndex(eventRect);$$.callOverOutForTouch(index),-1===index?$$.unselectRect():$$.selectRectForSingle(context,eventRect,index)}},preventDefault=config.interaction_inputType_touch.preventDefault,isPrevented=isBoolean(preventDefault)&&preventDefault||!1,preventThreshold=!isNaN(preventDefault)&&preventDefault||null,preventEvent=function(event){var eventType=event.type,touch=event.changedTouches[0],currentXY=touch["client".concat(config.axis_rotated?"Y":"X")];"touchstart"===eventType?isPrevented?event.preventDefault():null!==preventThreshold&&(startPx=currentXY):"touchmove"===eventType&&(isPrevented||!0===startPx||null!==preventThreshold&&Math.abs(startPx-currentXY)>=preventThreshold)&&(startPx=!0,event.preventDefault())};
// bind touch events
$$.svg.on("touchstart.eventRect touchmove.eventRect",(function(){var eventRect=getEventRect(),event=external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["event"];if(!eventRect.empty()&&eventRect.classed(config_classes.eventRect)){
// if touch points are > 1, means doing zooming interaction. In this case do not execute tooltip codes.
if($$.dragging||$$.flowing||$$.hasArcType()||event.touches.length>1)return;preventEvent(event),selectRect(this)}else $$.unselectRect(),$$.callOverOutForTouch()}),!0).on("touchend.eventRect",(function(){var eventRect=getEventRect();!eventRect.empty()&&eventRect.classed(config_classes.eventRect)&&($$.hasArcType()||!$$.toggleShape||$$.cancelClick)&&$$.cancelClick&&($$.cancelClick=!1)}),!0)},
/**
   * Updates the location and size of the eventRect.
   * @private
   * @param {Object} d3.select(CLASS.eventRects) object.
   */
updateEventRect:function(eventRectUpdate){var x,y,w,h,$$=this,config=$$.config,xScale=$$.zoomScale||$$.x,eventRectData=eventRectUpdate||$$.eventRect.data(),isRotated=config.axis_rotated;if($$.isMultipleX())// TODO: rotated not supported yet
x=0,y=0,w=$$.width,h=$$.height;else{var rectW,rectX;if($$.isCategorized())rectW=$$.getEventRectWidth(),rectX=function(d){return xScale(d.x)-rectW/2};else{var getPrevNextX=function(d){var index=d.index;return{prev:$$.getPrevX(index),next:$$.getNextX(index)}};rectW=function(d){var x=getPrevNextX(d);// if there this is a single data point make the eventRect full width (or height)
return null===x.prev&&null===x.next?isRotated?$$.height:$$.width:(null===x.prev&&(x.prev=xScale.domain()[0]),null===x.next&&(x.next=xScale.domain()[1]),Math.max(0,(xScale(x.next)-xScale(x.prev))/2))},rectX=function(d){var x=getPrevNextX(d),thisX=d.x;
// if there this is a single data point position the eventRect at 0
return null===x.prev&&null===x.next?0:(null===x.prev&&(x.prev=xScale.domain()[0]),(xScale(thisX)+xScale(x.prev))/2)}}x=isRotated?0:rectX,y=isRotated?rectX:0,w=isRotated?$$.width:rectW,h=isRotated?rectW:$$.height}eventRectData.attr("class",$$.classEvent.bind($$)).attr("x",x).attr("y",y).attr("width",w).attr("height",h)},selectRectForSingle:function(context,eventRect,index){var $$=this,config=$$.config,isSelectionEnabled=config.data_selection_enabled,isSelectionGrouped=config.data_selection_grouped,isTooltipGrouped=config.tooltip_grouped,selectedData=$$.getAllValuesOnIndex(index);isTooltipGrouped&&($$.showTooltip(selectedData,context),$$.showGridFocus(selectedData),!isSelectionEnabled||isSelectionGrouped)||$$.main.selectAll(".".concat(config_classes.shape,"-").concat(index)).each((function(){Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this).classed(config_classes.EXPANDED,!0),isSelectionEnabled&&eventRect.style("cursor",isSelectionGrouped?"pointer":null),isTooltipGrouped||($$.hideGridFocus(),$$.hideTooltip(),!isSelectionGrouped&&$$.expandCirclesBars(index))})).filter((function(d){return $$.isWithinShape(this,d)})).call((function(selected){var d=selected.data();isSelectionEnabled&&(isSelectionGrouped||config.data_selection_isselectable(d))&&eventRect.style("cursor","pointer"),isTooltipGrouped||($$.showTooltip(d,context),$$.showGridFocus(d),$$.unexpandCircles(),selected.each((function(d){return $$.expandCirclesBars(index,d.id)})))}))},expandCirclesBars:function(index,id,reset){var $$=this,config=$$.config;config.point_focus_expand_enabled&&$$.expandCircles(index,id,reset),$$.expandBars(index,id,reset)},selectRectForMultipleXs:function(context){var $$=this,config=$$.config,targetsToShow=$$.filterTargetsToShow($$.data.targets);
// do nothing when dragging
if(!$$.dragging&&!$$.hasArcType(targetsToShow)){var mouse=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["mouse"])(context),closest=$$.findClosestFromTargets(targetsToShow,mouse);if($$.mouseover&&(!closest||closest.id!==$$.mouseover.id)&&(config.data_onout.call($$.api,$$.mouseover),$$.mouseover=void 0),!closest)return void $$.unselectRect();var sameXData=$$.isBubbleType(closest)||$$.isScatterType(closest)||!config.tooltip_grouped?[closest]:$$.filterByX(targetsToShow,closest.x),selectedData=sameXData.map((function(d){return $$.addName(d)}));// show tooltip when cursor is close to some point
$$.showTooltip(selectedData,context),$$.expandCirclesBars(closest.index,closest.id,!0),$$.showGridFocus(selectedData),($$.isBarType(closest.id)||$$.dist(closest,mouse)<config.point_sensitivity)&&($$.svg.select(".".concat(config_classes.eventRect)).style("cursor","pointer"),!$$.mouseover&&(config.data_onover.call($$.api,closest),$$.mouseover=closest))}},
/**
   * Unselect EventRect.
   * @private
   */
unselectRect:function(){var $$=this;$$.svg.select(".".concat(config_classes.eventRect)).style("cursor",null),$$.hideGridFocus(),$$.hideTooltip(),$$._handleLinkedCharts(!1),$$.unexpandCircles(),$$.unexpandBars()},
/**
   * Handle data.onover/out callback options
   * @param {Boolean} isOver
   * @param {Number|Object} d
   * @private
   */
setOverOut:function(isOver,d){var $$=this,config=$$.config,isArc=isObject(d);
// Call event handler
if(isArc||-1!==d){var callback=config[isOver?"data_onover":"data_onout"].bind($$.api);if(config.color_onover&&$$.setOverColor(isOver,d,isArc),isArc)callback(d,$$.main.select(".".concat(config_classes.arc).concat($$.getTargetSelectorSuffix(d.id))).node());else if(config.tooltip_grouped)isOver&&$$.expandCirclesBars(d,null,!0),$$.isMultipleX()||$$.main.selectAll(".".concat(config_classes.shape,"-").concat(d)).each((function(d){callback(d,this)}));else{var callee=$$.setOverOut,last=callee.last||[],shape=$$.main.selectAll(".".concat(config_classes.shape,"-").concat(d)).filter((function(d){return $$.isWithinShape(this,d)}));shape.each((function(d){var _this=this;(0===last.length||last.every((function(v){return v!==_this})))&&(callback(d,this),last.push(this))})),last.length>0&&shape.empty()&&(callback=config.data_onout.bind($$.api),last.forEach((function(v){return callback(Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(v).datum(),v)})),last=[]),callee.last=last}}},
/**
   * Call data.onover/out callback for touch event
   * @param {Number|Object} d target index or data object for Arc type
   * @private
   */
callOverOutForTouch:function(d){var $$=this,callee=$$.callOverOutForTouch,last=callee.last;(isObject(d)&&last?d.id!==last.id:d!==last)&&((last||isNumber(last))&&$$.setOverOut(!1,last),(d||isNumber(d))&&$$.setOverOut(!0,d),callee.last=d)},
/**
   * Return draggable selection function
   * @return {Function}
   * @private
   */
getDraggableSelection:function(){var $$=this,config=$$.config;return config.interaction_enabled&&config.data_selection_draggable&&$$.drag?Object(external_commonjs_d3_drag_commonjs2_d3_drag_amd_d3_drag_root_d3_["drag"])().on("drag",(function(){$$.drag(Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["mouse"])(this))})).on("start",(function(){$$.dragstart(Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["mouse"])(this))})).on("end",(function(){$$.dragend()})):function(){}},
/**
   * Create eventRect for each data on the x-axis.
   * Register touch and drag events.
   * @private
   * @param {Object} d3.select(CLASS.eventRects) object.
   * @returns {Object} d3.select(CLASS.eventRects) object.
   */
generateEventRectsForSingleX:function(eventRectEnter){var $$=this,config=$$.config,rect=eventRectEnter.append("rect").attr("class",$$.classEvent.bind($$)).style("cursor",config.data_selection_enabled&&config.data_selection_grouped?"pointer":null).on("click",(function(d){$$.clickHandlerForSingleX.bind(this)(d,$$)})).call($$.getDraggableSelection());return"mouse"===$$.inputType&&rect.on("mouseover",(function(d){$$.dragging||$$.flowing||$$.hasArcType()||$$.config.tooltip_grouped&&$$.setOverOut(!0,d.index)})).on("mousemove",(function(d){
// do nothing while dragging/flowing
if(!($$.dragging||$$.flowing||$$.hasArcType())){var index=d.index,eventRect=$$.svg.select(".".concat(config_classes.eventRect,"-").concat(index));$$.isStepType(d)&&"step-after"===$$.config.line_step_type&&Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["mouse"])(this)[0]<$$.x($$.getXValue(d.id,index))&&(index-=1),-1===index?$$.unselectRect():$$.selectRectForSingle(this,eventRect,index),$$.config.tooltip_grouped||$$.setOverOut(-1!==index,d.index)}})).on("mouseout",(function(d){!$$.config||$$.hasArcType()||($$.unselectRect(),$$.setOverOut(!1,d.index))})),rect},clickHandlerForSingleX:function(d,ctx){var $$=ctx,config=$$.config;if($$.hasArcType()||!$$.toggleShape||$$.cancelClick)$$.cancelClick&&($$.cancelClick=!1);else{var index=d.index;$$.main.selectAll(".".concat(config_classes.shape,"-").concat(index)).each((function(d2){(config.data_selection_grouped||$$.isWithinShape(this,d2))&&($$.toggleShape(this,d2,index),config.data_onclick.call($$.api,d2,this))}))}},
/**
   * Create an eventRect,
   * Register touch and drag events.
   * @private
   * @param {Object} d3.select(CLASS.eventRects) object.
   * @returns {Object} d3.select(CLASS.eventRects) object.
   */
generateEventRectsForMultipleXs:function(eventRectEnter){var $$=this,rect=eventRectEnter.append("rect").attr("x",0).attr("y",0).attr("width",$$.width).attr("height",$$.height).attr("class",config_classes.eventRect).on("click",(function(){$$.clickHandlerForMultipleXS.bind(this)($$)})).call($$.getDraggableSelection());return"mouse"===$$.inputType&&rect.on("mouseover mousemove",(function(){$$.selectRectForMultipleXs(this)})).on("mouseout",(function(){!$$.config||$$.hasArcType()||$$.unselectRect()})),rect},clickHandlerForMultipleXS:function(ctx){var $$=ctx,config=$$.config,targetsToShow=$$.filterTargetsToShow($$.data.targets);if(!$$.hasArcType(targetsToShow)){var mouse=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["mouse"])(this),closest=$$.findClosestFromTargets(targetsToShow,mouse);!closest||($$.isBarType(closest.id)||$$.dist(closest,mouse)<config.point_sensitivity)&&$$.main.selectAll(".".concat(config_classes.shapes).concat($$.getTargetSelectorSuffix(closest.id))).selectAll(".".concat(config_classes.shape,"-").concat(closest.index)).each((function(){(config.data_selection_grouped||$$.isWithinShape(this,closest))&&($$.toggleShape(this,closest,closest.index),config.data_onclick.call($$.api,closest,this))}))}// select if selection enabled
},
/**
   * Dispatch a mouse event.
   * @private
   * @param {String} type event type
   * @param {Number} index Index of eventRect
   * @param {Array} mouse x and y coordinate value
   */
dispatchEvent:function(type,index,mouse){var $$=this,isMultipleX=$$.isMultipleX(),selector=".".concat(isMultipleX?config_classes.eventRect:"".concat(config_classes.eventRect,"-").concat(index)),eventRect=$$.main.select(selector).node(),_eventRect$getBoundin=eventRect.getBoundingClientRect(),width=_eventRect$getBoundin.width,left=_eventRect$getBoundin.left,top=_eventRect$getBoundin.top,x=left+(mouse?mouse[0]:0)+(isMultipleX||$$.config.axis_rotated?0:width/2),y=top+(mouse?mouse[1]:0);emulateEvent[/^(mouse|click)/.test(type)?"mouse":"touch"](eventRect,type,{screenX:x,screenY:y,clientX:x,clientY:y})}}),
// CONCATENATED MODULE: ./src/internals/size.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(ChartInternal_ChartInternal.prototype,{
/**
   * Update container size
   * @private
   */
setContainerSize:function(){var $$=this;$$.currentWidth=$$.getCurrentWidth(),$$.currentHeight=$$.getCurrentHeight()},getCurrentWidth:function(){var $$=this;return $$.config.size_width||$$.getParentWidth()},getCurrentHeight:function(){var $$=this,config=$$.config,h=config.size_height||$$.getParentHeight();return h>0?h:320/($$.hasType("gauge")&&!config.gauge_fullCircle?2:1)},
/**
   * Get Axis size according its position
   * @param {String} id Axis id value - x, y or y2
   * @return {number} size Axis size value
   * @private
   */
getAxisSize:function(id){var $$=this,config=$$.config,isRotated=config.axis_rotated;return isRotated&&"x"===id||!isRotated&&/y2?/.test(id)?$$.getAxisWidthByAxisId(id,!0):$$.getHorizontalAxisHeight(id)},getCurrentPaddingTop:function(){var $$=this,config=$$.config,axesLen=config.axis_y2_axes.length,padding=isValue(config.padding_top)?config.padding_top:0;return $$.title&&$$.title.node()&&(padding+=$$.getTitlePadding()),axesLen&&config.axis_rotated&&(padding+=$$.getHorizontalAxisHeight("y2")*axesLen),padding},getCurrentPaddingBottom:function(){var $$=this,config=$$.config,axisId=config.axis_rotated?"y":"x",axesLen=config["axis_".concat(axisId,"_axes")].length,padding=isValue(config.padding_bottom)?config.padding_bottom:0;return padding+(axesLen?$$.getHorizontalAxisHeight(axisId)*axesLen:0)},getCurrentPaddingLeft:function(withoutRecompute){var padding,$$=this,config=$$.config,isRotated=config.axis_rotated,axisId=isRotated?"x":"y",axesLen=config["axis_".concat(axisId,"_axes")].length,axisWidth=$$.getAxisWidthByAxisId(axisId,withoutRecompute);return padding=isValue(config.padding_left)?config.padding_left:isRotated?config.axis_x_show?Math.max(ceil10(axisWidth),40):1:!config.axis_y_show||config.axis_y_inner?$$.axis.getAxisLabelPosition("y").isOuter?30:1:ceil10(axisWidth),padding+axisWidth*axesLen},getCurrentPaddingRight:function(){var padding,withoutTickTextOverflow=!!(arguments.length>0&&void 0!==arguments[0])&&arguments[0],$$=this,config=$$.config,defaultPadding=10,legendWidthOnRight=$$.isLegendRight?$$.getLegendWidth()+20:0,axesLen=config.axis_y2_axes.length,axisWidth=$$.getAxisWidthByAxisId("y2"),xAxisTickTextOverflow=withoutTickTextOverflow?0:$$.axis.getXAxisTickTextY2Overflow(defaultPadding);return padding=isValue(config.padding_right)?config.padding_right+1:config.axis_rotated?defaultPadding+legendWidthOnRight:!config.axis_y2_show||config.axis_y2_inner?Math.max(2+legendWidthOnRight+($$.axis.getAxisLabelPosition("y2").isOuter?20:0),xAxisTickTextOverflow):Math.max(ceil10(axisWidth)+legendWidthOnRight,xAxisTickTextOverflow),padding+axisWidth*axesLen},
/**
   * Get the parent rect element's size
   * @param {String} key property/attribute name
   * @private
   */
getParentRectValue:function(key){for(var v,offsetName="offset".concat(capitalize(key)),parent=this.selectChart.node();!v&&parent&&"BODY"!==parent.tagName;){try{v=parent.getBoundingClientRect()[key]}catch(e){offsetName in parent&&(v=parent[offsetName])}parent=parent.parentNode}if("width"===key){
// Sometimes element's width value is incorrect(ex. flex container)
// In this case, use body's offsetWidth instead.
var bodyWidth=browser_doc.body.offsetWidth;v>bodyWidth&&(v=bodyWidth)}return v},getParentWidth:function(){return this.getParentRectValue("width")},getParentHeight:function(){var h=this.selectChart.style("height");return h.indexOf("px")>0?parseInt(h,10):0},getSvgLeft:function(withoutRecompute){var $$=this,config=$$.config,hasLeftAxisRect=config.axis_rotated||!config.axis_rotated&&!config.axis_y_inner,leftAxisClass=config.axis_rotated?config_classes.axisX:config_classes.axisY,leftAxis=$$.main.select(".".concat(leftAxisClass)).node(),svgRect=leftAxis&&hasLeftAxisRect?leftAxis.getBoundingClientRect():{right:0},chartRect=$$.selectChart.node().getBoundingClientRect(),hasArc=$$.hasArcType(),svgLeft=svgRect.right-chartRect.left-(hasArc?0:$$.getCurrentPaddingLeft(withoutRecompute));return svgLeft>0?svgLeft:0},getAxisWidthByAxisId:function(id,withoutRecompute){var $$=this,position=$$.axis.getLabelPositionById(id);return $$.axis.getMaxTickWidth(id,withoutRecompute)+(position.isInner?20:40)},getHorizontalAxisHeight:function(id){var $$=this,config=$$.config,isRotated=config.axis_rotated,h=30;if("x"===id&&!config.axis_x_show)return 8;if("x"===id&&config.axis_x_height)return config.axis_x_height;if("y"===id&&!config.axis_y_show)return!config.legend_show||$$.isLegendRight||$$.isLegendInset?1:10;if("y2"===id&&!config.axis_y2_show)return $$.rotated_padding_top;// const rotate = config[`axis_${id}_tick_rotate`];
var rotate=$$.getAxisTickRotate(id);// Calculate x/y axis height when tick rotated
return("x"===id&&!isRotated||/y2?/.test(id)&&isRotated)&&rotate&&(h=30+$$.axis.getMaxTickWidth(id)*Math.cos(Math.PI*(90-rotate)/180),!config.axis_x_tick_multiline&&$$.currentHeight&&h>$$.currentHeight/2&&(h=$$.currentHeight/2)),h+($$.axis.getLabelPositionById(id).isInner?0:10)+("y2"!==id||isRotated?0:-10)},getEventRectWidth:function(){return Math.max(0,this.xAxis.tickInterval())},
/**
   * Get axis tick test rotate value
   * @param {String} id
   * @return {Number} rotate value
   * @private
   */
getAxisTickRotate:function(id){var $$=this,config=$$.config,rotate=config["axis_".concat(id,"_tick_rotate")];if("x"===id){var isCategorized=$$.isCategorized(),isTimeSeries=$$.isTimeSeries(),allowedXAxisTypes=isCategorized||isTimeSeries,tickCount=0;config.axis_x_tick_fit&&allowedXAxisTypes&&($$.axis.x={padding:{left:0,right:0},tickCount:0},tickCount=$$.currentMaxTickWidths.x.ticks.length+(isTimeSeries?-1:1),tickCount!==$$.axis.x.tickCount&&($$.axis.x.padding=$$.axis.getXAxisPadding(tickCount)),$$.axis.x.tickCount=tickCount),$$.svg&&config.axis_x_tick_fit&&!config.axis_x_tick_multiline&&!config.axis_x_tick_culling&&config.axis_x_tick_autorotate&&allowedXAxisTypes&&(rotate=$$.needToRotateXAxisTickTexts()?config.axis_x_tick_rotate:0)}return rotate},
/**
   * Check weather axis tick text needs to be rotated
   * @private
   */
needToRotateXAxisTickTexts:function(){var $$=this,xAxisLength=$$.currentWidth-$$.getCurrentPaddingLeft(!1)-$$.getCurrentPaddingRight(!0),tickCountWithPadding=$$.axis.x.tickCount+$$.axis.x.padding.left+$$.axis.x.padding.right,maxTickWidth=$$.axis.getMaxTickWidth("x");return maxTickWidth>(xAxisLength/tickCountWithPadding||0)}});
// EXTERNAL MODULE: external {"commonjs":"d3-shape","commonjs2":"d3-shape","amd":"d3-shape","root":"d3"}
var external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_=__nested_webpack_require_7012__(10);
// CONCATENATED MODULE: ./src/shape/shape.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */extend(ChartInternal_ChartInternal.prototype,{getShapeIndices:function(typeFilter){var $$=this,config=$$.config,xs=config.data_xs,hasXs=notEmpty(xs),indices={},i=hasXs?{}:0;return hasXs&&getUnique(Object.keys(xs).map((function(v){return xs[v]}))).forEach((function(v){i[v]=0,indices[v]={}})),$$.filterTargetsToShow($$.data.targets.filter(typeFilter,$$)).forEach((function(d){for(var groups,xKey=(d.id in xs?xs[d.id]:""),ind=xKey?indices[xKey]:indices,j=0;groups=config.data_groups[j];j++)if(!(groups.indexOf(d.id)<0))for(var _row4,_k4=0;_row4=groups[_k4];_k4++)if(_row4 in ind){ind[d.id]=ind[_row4];break}isUndefined(ind[d.id])&&(ind[d.id]=xKey?i[xKey]++:i++,ind.__max__=(xKey?i[xKey]:i)-1)})),indices},
/**
   * Get indices value based on data ID value
   * @param {Object} indices Indices object
   * @param {String} id Data id value
   * @return {Object} Indices object
   * @private
   */
getIndices:function(indices,id){var xs=this.config.data_xs;return notEmpty(xs)?indices[xs[id]]:indices},
/**
   * Get indices max number
   * @param {Object} indices Indices object
   * @return {Number} Max number
   * @private
   */
getIndicesMax:function(indices){return notEmpty(this.config.data_xs)?// if is multiple xs, return total sum of xs' __max__ value
Object.keys(indices).map((function(v){return indices[v].__max__||0})).reduce((function(acc,curr){return acc+curr})):indices.__max__},getShapeX:function(offset,indices,isSub){var $$=this,scale=isSub?$$.subX:$$.zoomScale||$$.x,barPadding=$$.config.bar_padding,sum=function(p,c){return p+c},halfWidth=isObjectType(offset)&&offset.total.length?offset.total.reduce(sum)/2:0;return function(d){var ind=$$.getIndices(indices,d.id),index=d.id in ind?ind[d.id]:0,targetsNum=(ind.__max__||0)+1,x=0;if(notEmpty(d.x)){var xPos=scale(d.x);x=halfWidth?xPos-(offset[d.id]||offset.width)+offset.total.slice(0,index+1).reduce(sum)-halfWidth:xPos-(isNumber(offset)?offset:offset.width)*(targetsNum/2-index)}// adjust x position for bar.padding optionq
return offset&&x&&targetsNum>1&&barPadding&&(index&&(x+=barPadding*index),targetsNum>2?x-=(targetsNum-1)*barPadding/2:2===targetsNum&&(x-=barPadding/2)),x}},getShapeY:function(isSub){var $$=this,isStackNormalized=$$.isStackNormalized();return function(d){var value=isStackNormalized?$$.getRatio("index",d,!0):$$.isBubbleZType(d)?$$.getBubbleZData(d.value,"y"):d.value;return(isSub?$$.getSubYScale(d.id):$$.getYScale(d.id))(value)}},
/**
   * Get shape based y Axis min value
   * @param {String} id Data id
   * @return {Number}
   * @private
   */
getShapeYMin:function(id){var $$=this,_$$$$$$axis$getId$dom=$$[$$.axis.getId(id)].domain(),_$$$$$$axis$getId$dom2=_slicedToArray(_$$$$$$axis$getId$dom,1),yMin=_$$$$$$axis$getId$dom2[0];return!$$.isGrouped(id)&&yMin>0?yMin:0},
/**
   * Get Shape's offset data
   * @param {function(Object): boolean} typeFilter
   * @return {{shapeOffsetTargets: ShapeOffsetTarget[], indexMapByTargetId: object}}
   * @private
   */
getShapeOffsetData:function(typeFilter){var $$=this,targets=$$.orderTargets($$.filterTargetsToShow($$.data.targets.filter(typeFilter,$$))),isStackNormalized=$$.isStackNormalized(),shapeOffsetTargets=targets.map((function(target){var rowValues=target.values,values={};$$.isStepType(target)&&(rowValues=$$.convertValuesToStep(rowValues));var rowValueMapByXValue=rowValues.reduce((function(out,d){var key=+d.x;return out[key]=d,values[key]=isStackNormalized?$$.getRatio("index",d,!0):d.value,out}),{});return{id:target.id,rowValues:rowValues,rowValueMapByXValue:rowValueMapByXValue,values:values}})),indexMapByTargetId=targets.reduce((function(out,_ref,index){var id=_ref.id;return out[id]=index,out}),{});return{indexMapByTargetId:indexMapByTargetId,shapeOffsetTargets:shapeOffsetTargets}},getShapeOffset:function(typeFilter,indices,isSub){var $$=this,_$$$getShapeOffsetDat=$$.getShapeOffsetData(typeFilter),shapeOffsetTargets=_$$$getShapeOffsetDat.shapeOffsetTargets,indexMapByTargetId=_$$$getShapeOffsetDat.indexMapByTargetId;return function(d,idx){var ind=$$.getIndices(indices,d.id),scale=isSub?$$.getSubYScale(d.id):$$.getYScale(d.id),y0=scale($$.getShapeYMin(d.id)),dataXAsNumber=+d.x,offset=y0;return shapeOffsetTargets.filter((function(t){return t.id!==d.id})).forEach((function(t){if(ind[t.id]===ind[d.id]&&indexMapByTargetId[t.id]<indexMapByTargetId[d.id]){var row=t.rowValues[idx];// check if the x values line up
row&&+row.x===dataXAsNumber||(row=t.rowValueMapByXValue[dataXAsNumber]),row&&row.value*d.value>=0&&(offset+=scale(t.values[dataXAsNumber])-y0)}})),offset}},isWithinShape:function(that,d){var isWithin,$$=this,shape=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(that);return $$.isTargetToShow(d.id)?$$.hasValidPointType(that.nodeName)?isWithin=$$.isStepType(d)?$$.isWithinStep(that,$$.getYScale(d.id)(d.value)):$$.isWithinCircle(that,$$.isBubbleType(d)?1.5*$$.pointSelectR(d):0):"path"===that.nodeName&&(isWithin=!shape.classed(config_classes.bar)||$$.isWithinBar(that)):isWithin=!1,isWithin},getInterpolate:function(d){var $$=this,interpolation=$$.getInterpolateType(d);return{basis:external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_["curveBasis"],"basis-closed":external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_["curveBasisClosed"],"basis-open":external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_["curveBasisOpen"],bundle:external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_["curveBundle"],cardinal:external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_["curveCardinal"],"cardinal-closed":external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_["curveCardinalClosed"],"cardinal-open":external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_["curveCardinalOpen"],"catmull-rom":external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_["curveCatmullRom"],"catmull-rom-closed":external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_["curveCatmullRomClosed"],"catmull-rom-open":external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_["curveCatmullRomOpen"],"monotone-x":external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_["curveMonotoneX"],"monotone-y":external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_["curveMonotoneY"],natural:external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_["curveNatural"],"linear-closed":external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_["curveLinearClosed"],linear:external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_["curveLinear"],step:external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_["curveStep"],"step-after":external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_["curveStepAfter"],"step-before":external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_["curveStepBefore"]}[interpolation]},getInterpolateType:function(d){var $$=this,type=$$.config.spline_interpolation_type,interpolation=$$.isInterpolationType(type)?type:"cardinal";return $$.isSplineType(d)?interpolation:$$.isStepType(d)?$$.config.line_step_type:"linear"}});
// EXTERNAL MODULE: external {"commonjs":"d3-interpolate","commonjs2":"d3-interpolate","amd":"d3-interpolate","root":"d3"}
var external_commonjs_d3_interpolate_commonjs2_d3_interpolate_amd_d3_interpolate_root_d3_=__nested_webpack_require_7012__(11);
// CONCATENATED MODULE: ./src/shape/arc.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
// CONCATENATED MODULE: ./src/shape/radar.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
/**
 * Get the position value
 * @param {Boolean} isClockwise If the direction is clockwise
 * @param {String} type Coordinate type 'x' or 'y'
 * @param {Number} edge Number of edge
 * @param {Number} pos The indexed position
 * @param {Number} range
 * @param {Number} ratio
 * @return {number}
 * @private
 */
function getPosition(isClockwise,type,edge,pos,range,ratio){var index=isClockwise&&pos>0?edge-pos:pos,r=2*Math.PI,func="x"===type?Math.sin:Math.cos;return range*(1-ratio*func(index*r/edge))}// cache key
extend(ChartInternal_ChartInternal.prototype,{initPie:function(){var $$=this,config=$$.config,dataType=config.data_type,padding=config.pie_padding,startingAngle=config["".concat(dataType,"_startingAngle")]||0,padAngle=($$.hasType("pie")&&padding?.01*padding:config["".concat(dataType,"_padAngle")])||0;$$.pie=Object(external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_["pie"])().startAngle(startingAngle).endAngle(startingAngle+2*Math.PI).padAngle(padAngle).sortValues($$.isOrderAsc()||$$.isOrderDesc()?function(a,b){return $$.isOrderAsc()?a-b:b-a}:null).value((function(d){return d.values.reduce((function(a,b){return a+b.value}),0)}))},updateRadius:function(){var $$=this,config=$$.config,radius=config.pie_innerRadius,padding=config.pie_padding,w=config.gauge_width||config.donut_width,gaugeArcWidth=$$.filterTargetsToShow($$.data.targets).length*config.gauge_arcs_minWidth;$$.radiusExpanded=Math.min($$.arcWidth,$$.arcHeight)/2*($$.hasMultiArcGauge()?.85:1),$$.radius=.95*$$.radiusExpanded,$$.innerRadiusRatio=w?($$.radius-w)/$$.radius:.6,$$.gaugeArcWidth=w||(gaugeArcWidth<=$$.radius-$$.innerRadius?$$.radius-$$.innerRadius:gaugeArcWidth<=$$.radius?gaugeArcWidth:$$.radius);var innerRadius=radius||(padding?padding*($$.innerRadiusRatio+.1):0);// NOTE: innerRadius can be an object by user setting, only for 'pie' type
$$.innerRadius=$$.hasType("donut")||$$.hasType("gauge")?$$.radius*$$.innerRadiusRatio:innerRadius},getInnerRadius:function(d){var $$=this,radius=$$.innerRadius;return!isNumber(radius)&&d&&(radius=radius[d.data.id]||0),radius},updateArc:function(){var $$=this;$$.svgArc=$$.getSvgArc(),$$.svgArcExpanded=$$.getSvgArcExpanded()},updateAngle:function(dValue){var $$=this,config=$$.config,pie=$$.pie,d=dValue,found=!1;if(!config)return null;var radius=Math.PI*(config.gauge_fullCircle?2:1),gStart=config.gauge_startingAngle;if(d.data&&$$.isGaugeType(d.data)&&!$$.hasMultiArcGauge()){
// to prevent excluding total data sum during the init(when data.hide option is used), use $$.rendered state value
var totalSum=$$.getTotalDataSum($$.rendered);// if gauge_max less than totalSum, make totalSum to max value
totalSum>config.gauge_max&&(config.gauge_max=totalSum);var gEnd=radius*(totalSum/(config.gauge_max-config.gauge_min));pie=pie.startAngle(gStart).endAngle(gEnd+gStart)}if(pie($$.filterTargetsToShow()).forEach((function(t,i){found||t.data.id!==d.data.id||(found=!0,d=t,d.index=i)})),isNaN(d.startAngle)&&(d.startAngle=0),isNaN(d.endAngle)&&(d.endAngle=d.startAngle),d.data&&$$.hasMultiArcGauge()){var maxValue=$$.getMinMaxData().max[0].value;// if gauge_max less than maxValue, make maxValue to max value
maxValue>config.gauge_max&&(config.gauge_max=maxValue);var gMin=config.gauge_min,gMax=config.gauge_max,gValue=d.value<gMin?0:d.value<gMax?d.value-gMin:gMax-gMin;d.startAngle=gStart,d.endAngle=gStart+radius/(gMax-gMin)*gValue}return found?d:null},getSvgArc:function(){var $$=this,ir=$$.getInnerRadius(),singleArcWidth=$$.gaugeArcWidth/$$.filterTargetsToShow($$.data.targets).length,hasMultiArcGauge=$$.hasMultiArcGauge(),arc=Object(external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_["arc"])().outerRadius((function(d){return hasMultiArcGauge?$$.radius-singleArcWidth*d.index:$$.radius})).innerRadius((function(d){return hasMultiArcGauge?$$.radius-singleArcWidth*(d.index+1):isNumber(ir)?ir:0})),newArc=function(d,withoutUpdate){var path="M 0 0";if(d.value||d.data){isNumber(ir)||(arc=arc.innerRadius($$.getInnerRadius(d)));var updated=!withoutUpdate&&$$.updateAngle(d);withoutUpdate?path=arc(d):updated&&(path=arc(updated))}return path};return newArc.centroid=arc.centroid,newArc},getSvgArcExpanded:function(rate){var $$=this,newRate=rate||1,singleArcWidth=$$.gaugeArcWidth/$$.filterTargetsToShow($$.data.targets).length,hasMultiArcGauge=$$.hasMultiArcGauge(),expandWidth=Math.min($$.radiusExpanded*newRate-$$.radius,.8*singleArcWidth-100*(1-newRate)),arc=Object(external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_["arc"])().outerRadius((function(d){return hasMultiArcGauge?$$.radius-singleArcWidth*d.index+expandWidth:$$.radiusExpanded*newRate})).innerRadius((function(d){return hasMultiArcGauge?$$.radius-singleArcWidth*(d.index+1):$$.innerRadius}));return function(d){var updated=$$.updateAngle(d);return updated?(hasMultiArcGauge?arc:arc.innerRadius($$.getInnerRadius(d)))(updated):"M 0 0"}},getArc:function(d,withoutUpdate,force){return force||this.isArcType(d.data)?this.svgArc(d,withoutUpdate):"M 0 0"},transformForArcLabel:function(d){var $$=this,config=$$.config,updated=$$.updateAngle(d),translate="";if(updated)if($$.hasMultiArcGauge()){var y1=Math.sin(updated.endAngle-Math.PI/2),x=Math.cos(updated.endAngle-Math.PI/2)*($$.radiusExpanded+25),y=y1*($$.radiusExpanded+15-Math.abs(10*y1))+3;translate="translate(".concat(x,",").concat(y,")")}else if(!$$.hasType("gauge")||$$.data.targets.length>1){var c=this.svgArc.centroid(updated),h=(x=isNaN(c[0])?0:c[0],y=isNaN(c[1])?0:c[1],Math.sqrt(x*x+y*y)),ratio=$$.hasType("donut")&&config.donut_label_ratio||$$.hasType("pie")&&config.pie_label_ratio;ratio=ratio?isFunction(ratio)?ratio(d,$$.radius,h):ratio:$$.radius&&(h?(36/$$.radius>.375?1.175-36/$$.radius:.8)*$$.radius/h:0),translate="translate(".concat(x*ratio,",").concat(y*ratio,")")}return translate},convertToArcData:function(d){return this.addName({id:d.data.id,value:d.value,ratio:this.getRatio("arc",d),index:d.index})},textForArcLabel:function(selection){var $$=this,hasGauge=$$.hasType("gauge");$$.shouldShowArcLabel()&&selection.each((function(d){var node=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this),updated=$$.updateAngle(d),ratio=$$.getRatio("arc",updated),isUnderThreshold=hasGauge||$$.meetsArcLabelThreshold(ratio);if(isUnderThreshold){var value=(updated||d).value,text=($$.getArcLabelFormat()||$$.defaultArcValueFormat)(value,ratio,d.data.id).toString();setTextValue(node,text,[-1,1],hasGauge)}else node.text("")}))},textForGaugeMinMax:function(value,isMax){var format=this.getGaugeLabelExtents();return format?format(value,isMax):value},expandArc:function(targetIds){var $$=this;// MEMO: avoid to cancel transition
if($$.transiting)var interval=setInterval((function(){$$.transiting||(clearInterval(interval),$$.legend.selectAll(".".concat(config_classes.legendItemFocused)).size()>0&&$$.expandArc(targetIds))}),10);else{var newTargetIds=$$.mapToTargetIds(targetIds);$$.svg.selectAll($$.selectorTargets(newTargetIds,".".concat(config_classes.chartArc))).each((function(d){if($$.shouldExpand(d.data.id)){var expandDuration=$$.getExpandConfig(d.data.id,"duration"),svgArcExpandedSub=$$.getSvgArcExpanded($$.getExpandConfig(d.data.id,"rate"));Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this).selectAll("path").transition().duration(expandDuration).attr("d",$$.svgArcExpanded).transition().duration(2*expandDuration).attr("d",svgArcExpandedSub)}}))}},unexpandArc:function(targetIds){var $$=this;if(!$$.transiting){var newTargetIds=$$.mapToTargetIds(targetIds);$$.svg.selectAll($$.selectorTargets(newTargetIds,".".concat(config_classes.chartArc))).selectAll("path").transition().duration((function(d){return $$.getExpandConfig(d.data.id,"duration")})).attr("d",$$.svgArc),$$.svg.selectAll("".concat(config_classes.arc)).style("opacity","1")}},
/**
   * Get expand config value
   * @param {String} id data ID
   * @param {String} key config key: 'duration | rate'
   * @return {Number}
   * @private
   */
getExpandConfig:function(id,key){var type,$$=this,config=$$.config;return $$.isDonutType(id)?type="donut":$$.isGaugeType(id)?type="gauge":$$.isPieType(id)&&(type="pie"),type?config["".concat(type,"_expand_").concat(key)]:{duration:50,rate:.98}[key]},shouldExpand:function(id){var $$=this,config=$$.config;return $$.isDonutType(id)&&config.donut_expand||$$.isGaugeType(id)&&config.gauge_expand||$$.isPieType(id)&&config.pie_expand},shouldShowArcLabel:function(){var $$=this,config=$$.config;return["pie","donut","gauge"].some((function(v){return $$.hasType(v)&&config["".concat(v,"_label_show")]}))},meetsArcLabelThreshold:function(ratio){var $$=this,config=$$.config,threshold=$$.hasType("donut")?config.donut_label_threshold:config.pie_label_threshold;return ratio>=threshold},getArcLabelFormat:function(){var $$=this,config=$$.config,format=config.pie_label_format;return $$.hasType("gauge")?format=config.gauge_label_format:$$.hasType("donut")&&(format=config.donut_label_format),format},getGaugeLabelExtents:function(){var config=this.config;return config.gauge_label_extents},getArcTitle:function(){var $$=this,type=$$.hasType("donut")?"donut":$$.hasType("gauge")&&"gauge";return type?$$.config["".concat(type,"_title")]:""},updateTargetsForArc:function(targets){var $$=this,main=$$.main,hasGauge=$$.hasType("gauge"),classChartArc=$$.classChartArc.bind($$),classArcs=$$.classArcs.bind($$),classFocus=$$.classFocus.bind($$),mainPieUpdate=main.select(".".concat(config_classes.chartArcs)).selectAll(".".concat(config_classes.chartArc)).data($$.pie(targets)).attr("class",(function(d){return classChartArc(d)+classFocus(d.data)})),mainPieEnter=mainPieUpdate.enter().append("g").attr("class",classChartArc);mainPieEnter.append("g").attr("class",classArcs).merge(mainPieUpdate),mainPieEnter.append("text").attr("dy",hasGauge&&!$$.hasMultiTargets()?"-.1em":".35em").style("opacity","0").style("text-anchor","middle").style("pointer-events","none")},initArc:function(){var $$=this;$$.arcs=$$.main.select(".".concat(config_classes.chart)).append("g").attr("class",config_classes.chartArcs).attr("transform",$$.getTranslate("arc")),$$.setArcTitle()},
/**
   * Set arc title text
   * @private
   */
setArcTitle:function(){var $$=this,title=$$.getArcTitle(),hasGauge=$$.hasType("gauge");if(title){var text=$$.arcs.append("text").attr("class",config_classes[hasGauge?"chartArcsGaugeTitle":"chartArcsTitle"]).style("text-anchor","middle");hasGauge&&text.attr("dy","-0.3em").style("font-size","27px"),setTextValue(text,title,hasGauge?void 0:[-.6,1.35],!0)}},redrawArc:function(duration,durationForExit,withTransform){var $$=this,config=$$.config,main=$$.main,hasInteraction=config.interaction_enabled,mainArc=main.selectAll(".".concat(config_classes.arcs)).selectAll(".".concat(config_classes.arc)).data($$.arcData.bind($$));
// bind arc events
mainArc.exit().transition().duration(durationForExit).style("opacity","0").remove(),mainArc=mainArc.enter().append("path").attr("class",$$.classArc.bind($$)).style("fill",(function(d){return $$.color(d.data)})).style("cursor",(function(d){return hasInteraction&&config.data_selection_isselectable(d)?"pointer":null})).style("opacity","0").each((function(d){$$.isGaugeType(d.data)&&(d.startAngle=config.gauge_startingAngle,d.endAngle=config.gauge_startingAngle),this._current=d})).merge(mainArc),$$.hasMultiArcGauge()&&$$.redrawMultiArcGauge(),mainArc.attr("transform",(function(d){return!$$.isGaugeType(d.data)&&withTransform?"scale(0)":""})).style("opacity",(function(d){return d===this._current?"0":"1"})).each((function(){$$.transiting=!0})).transition().duration(duration).attrTween("d",(function(d){var updated=$$.updateAngle(d);if(!updated)return function(){return"M 0 0"};isNaN(this._current.startAngle)&&(this._current.startAngle=0),isNaN(this._current.endAngle)&&(this._current.endAngle=this._current.startAngle);var interpolate=Object(external_commonjs_d3_interpolate_commonjs2_d3_interpolate_amd_d3_interpolate_root_d3_["interpolate"])(this._current,updated);return this._current=interpolate(0),function(t){var interpolated=interpolate(t);
// data.id will be updated by interporator
return interpolated.data=d.data,$$.getArc(interpolated,!0)}})).attr("transform",withTransform?"scale(1)":"").style("fill",(function(d){var color;return $$.levelColor?(color=$$.levelColor(d.data.values[0].value),config.data_colors[d.data.id]=color):color=$$.color(d.data.id),color})).style("opacity","1").call($$.endall,(function(){if($$.levelColor){var path=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this),d=path.datum();$$.updateLegendItemColor(d.data.id,path.style("fill"))}$$.transiting=!1,callFn(config.onrendered,$$,$$.api)})),hasInteraction&&$$.bindArcEvent(mainArc),$$.redrawArcText(duration)},redrawMultiArcGauge:function(){var $$=this,config=$$.config,arcLabelLines=$$.main.selectAll(".".concat(config_classes.arcs)).selectAll(".".concat(config_classes.arcLabelLine)).data($$.arcData.bind($$)),mainArcLabelLine=arcLabelLines.enter().append("rect").attr("class",(function(d){return"".concat(config_classes.arcLabelLine," ").concat(config_classes.target," ").concat(config_classes.target,"-").concat(d.data.id)})).merge(arcLabelLines);mainArcLabelLine.style("fill",(function(d){return $$.levelColor?$$.levelColor(d.data.values[0].value):$$.color(d.data)})).style("display",config.gauge_label_show?"":"none").each((function(d){var lineLength=0,lineThickness=2,x=0,y=0,transform="";if($$.hiddenTargetIds.indexOf(d.data.id)<0){var updated=$$.updateAngle(d),innerLineLength=$$.gaugeArcWidth/$$.filterTargetsToShow($$.data.targets).length*(updated.index+1),lineAngle=updated.endAngle-Math.PI/2,arcInnerRadius=$$.radius-innerLineLength,linePositioningAngle=lineAngle-(0===arcInnerRadius?0:1/arcInnerRadius);lineLength=$$.radiusExpanded-$$.radius+innerLineLength,x=Math.cos(linePositioningAngle)*arcInnerRadius,y=Math.sin(linePositioningAngle)*arcInnerRadius,transform="rotate(".concat(180*lineAngle/Math.PI,", ").concat(x,", ").concat(y,")")}Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this).attr("x",x).attr("y",y).attr("width",lineLength).attr("height",lineThickness).attr("transform",transform).style("stroke-dasharray","0, ".concat(lineLength+lineThickness,", 0"))}))},bindArcEvent:function(arc){function selectArc(_this,arcData,id){$$.expandArc(id),$$.api.focus(id),$$.toggleFocusLegend(id,!0),$$.showTooltip([arcData],_this)}function unselectArc(arcData){var id=arcData&&arcData.id||void 0;$$.unexpandArc(id),$$.api.revert(),$$.revertLegend(),$$.hideTooltip()}var $$=this,isTouch="touch"===$$.inputType,isMouse="mouse"===$$.inputType;
// touch events
if(arc.on("click",(function(d,i){var arcData,updated=$$.updateAngle(d);updated&&(arcData=$$.convertToArcData(updated),$$.toggleShape&&$$.toggleShape(this,arcData,i),$$.config.data_onclick.call($$.api,arcData,this))})),isMouse&&arc.on("mouseover",(function(d){if(!$$.transiting)// skip while transiting
{var updated=$$.updateAngle(d),arcData=updated?$$.convertToArcData(updated):null,id=arcData&&arcData.id||void 0;selectArc(this,arcData,id),$$.setOverOut(!0,arcData)}})).on("mouseout",(function(d){if(!$$.transiting)// skip while transiting
{var updated=$$.updateAngle(d),arcData=updated?$$.convertToArcData(updated):null;unselectArc(),$$.setOverOut(!1,arcData)}})).on("mousemove",(function(d){var updated=$$.updateAngle(d),arcData=updated?$$.convertToArcData(updated):null;$$.showTooltip([arcData],this)})),isTouch&&$$.hasArcType()&&!$$.radars){var getEventArc=function(){var touch=external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["event"].changedTouches[0],eventArc=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(browser_doc.elementFromPoint(touch.clientX,touch.clientY));return eventArc},handler=function(){if(!$$.transiting)// skip while transiting
{var eventArc=getEventArc(),datum=eventArc.datum(),updated=datum&&datum.data&&datum.data.id?$$.updateAngle(datum):null,arcData=updated?$$.convertToArcData(updated):null,id=arcData&&arcData.id||void 0;$$.callOverOutForTouch(arcData),isUndefined(id)?unselectArc():selectArc(this,arcData,id)}};$$.svg.on("touchstart",handler).on("touchmove",handler)}},redrawArcText:function(duration){var text,$$=this,config=$$.config,main=$$.main,hasGauge=$$.hasType("gauge"),hasMultiArcGauge=$$.hasMultiArcGauge();if(hasGauge&&1===$$.data.targets.length&&config.gauge_title||(text=main.selectAll(".".concat(config_classes.chartArc)).select("text").style("opacity","0").attr("class",(function(d){return $$.isGaugeType(d.data)?config_classes.gaugeValue:null})).call($$.textForArcLabel.bind($$)).attr("transform",$$.transformForArcLabel.bind($$)).style("font-size",(function(d){return $$.isGaugeType(d.data)&&1===$$.data.targets.length&&!hasMultiArcGauge?"".concat(Math.round($$.radius/5),"px"):null})).transition().duration(duration).style("opacity",(function(d){return $$.isTargetToShow(d.data.id)&&$$.isArcType(d.data)?"1":"0"})),hasMultiArcGauge&&text.attr("dy","-.1em")),main.select(".".concat(config_classes.chartArcsTitle)).style("opacity",$$.hasType("donut")||hasGauge?"1":"0"),hasGauge){var isFullCircle=config.gauge_fullCircle,startAngle=-1*Math.PI/2,endAngle=(isFullCircle?-4:-1)*startAngle;isFullCircle&&text&&text.attr("dy","".concat(Math.round($$.radius/14)));var backgroundArc=$$.arcs.select("".concat(hasMultiArcGauge?"g":"",".").concat(config_classes.chartArcsBackground));if(hasMultiArcGauge){var index=0;backgroundArc=backgroundArc.selectAll("path.".concat(config_classes.chartArcsBackground)).data($$.data.targets),backgroundArc.enter().append("path").attr("class",(function(d,i){return"".concat(config_classes.chartArcsBackground," ").concat(config_classes.chartArcsBackground,"-").concat(i)})).merge(backgroundArc).attr("d",(function(d1){if($$.hiddenTargetIds.indexOf(d1.id)>=0)return"M 0 0";var d={data:[{value:config.gauge_max}],startAngle:startAngle,endAngle:endAngle,index:index++};return $$.getArc(d,!0,!0)})),backgroundArc.exit().remove()}else backgroundArc.attr("d",(function(){var d={data:[{value:config.gauge_max}],startAngle:startAngle,endAngle:endAngle};return $$.getArc(d,!0,!0)}));$$.arcs.select(".".concat(config_classes.chartArcsGaugeUnit)).attr("dy",".75em").text(config.gauge_label_show?config.gauge_units:""),config.gauge_label_show&&($$.arcs.select(".".concat(config_classes.chartArcsGaugeMin)).attr("dx","".concat(-1*($$.innerRadius+($$.radius-$$.innerRadius)/(isFullCircle?1:2)),"px")).attr("dy","1.2em").text($$.textForGaugeMinMax(config.gauge_min,!1)),!isFullCircle&&$$.arcs.select(".".concat(config_classes.chartArcsGaugeMax)).attr("dx","".concat($$.innerRadius+($$.radius-$$.innerRadius)/2,"px")).attr("dy","1.2em").text($$.textForGaugeMinMax(config.gauge_max,!0)))}},initGauge:function(){var $$=this,config=$$.config,arcs=$$.arcs,appendText=function(className){arcs.append("text").attr("class",className).style("text-anchor","middle").style("pointer-events","none")};$$.hasType("gauge")&&(arcs.append($$.hasMultiArcGauge()?"g":"path").attr("class",config_classes.chartArcsBackground),config.gauge_units&&appendText(config_classes.chartArcsGaugeUnit),config.gauge_label_show&&(appendText(config_classes.chartArcsGaugeMin),!config.gauge_fullCircle&&appendText(config_classes.chartArcsGaugeMax)))},getGaugeLabelHeight:function(){return this.config.gauge_label_show?20:0}}),
// CONCATENATED MODULE: ./src/shape/bar.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(ChartInternal_ChartInternal.prototype,{initBar:function(){var $$=this;$$.main.select(".".concat(config_classes.chart)).append("g").attr("class",config_classes.chartBars)},updateTargetsForBar:function(targets){var $$=this,config=$$.config,classChartBar=$$.classChartBar.bind($$),classBars=$$.classBars.bind($$),classFocus=$$.classFocus.bind($$),mainBarUpdate=$$.main.select(".".concat(config_classes.chartBars)).selectAll(".".concat(config_classes.chartBar)).data(targets).attr("class",(function(d){return classChartBar(d)+classFocus(d)})),mainBarEnter=mainBarUpdate.enter().append("g").attr("class",classChartBar).style("opacity","0").style("pointer-events","none");
// Bars for each data
mainBarEnter.append("g").attr("class",classBars).style("cursor",(function(d){return config.data_selection_isselectable(d)?"pointer":null}))},updateBar:function(durationForExit){var $$=this,barData=$$.barData.bind($$),classBar=$$.classBar.bind($$),initialOpacity=$$.initialOpacity.bind($$);$$.mainBar=$$.main.selectAll(".".concat(config_classes.bars)).selectAll(".".concat(config_classes.bar)).data(barData),$$.mainBar.exit().transition().duration(durationForExit).style("opacity","0").remove(),$$.mainBar=$$.mainBar.enter().append("path").attr("class",classBar).style("fill",$$.color).merge($$.mainBar).style("opacity",initialOpacity)},redrawBar:function(drawBar,withTransition){return[(withTransition?this.mainBar.transition(getRandom()):this.mainBar).attr("d",drawBar).style("fill",this.color).style("opacity","1")]},getBarW:function(axis,barTargetsNum){var result,$$=this,config=$$.config,maxDataCount=$$.getMaxDataCount(),isGrouped=$$.isGrouped(),tickInterval=!$$.zoomScale&&!$$||$$.isCategorized()?axis.tickInterval(maxDataCount):$$.xx($$.subX.domain()[1])/maxDataCount,getWidth=function(id){var width=id?config.bar_width[id]:config.bar_width,ratio=id?width.ratio:config.bar_width_ratio,max=id?width.max:config.bar_width_max,w=isNumber(width)?width:barTargetsNum?tickInterval*ratio/barTargetsNum:0;return max&&w>max?max:w};return result=getWidth(),!isGrouped&&isObjectType(config.bar_width)&&(result={width:result,total:[]},$$.filterTargetsToShow($$.data.targets).forEach((function(v){config.bar_width[v.id]&&(result[v.id]=getWidth(v.id),result.total.push(result[v.id]||result.width))}))),result},getBars:function(i,id){var $$=this,suffix=isValue(i)?"-".concat(i):"";return(id?$$.main.selectAll(".".concat(config_classes.bars).concat($$.getTargetSelectorSuffix(id))):$$.main).selectAll(".".concat(config_classes.bar).concat(suffix))},expandBars:function(i,id,reset){var $$=this;reset&&$$.unexpandBars(),$$.getBars(i,id).classed(config_classes.EXPANDED,!0)},unexpandBars:function(i){this.getBars(i).classed(config_classes.EXPANDED,!1)},generateDrawBar:function(barIndices,isSub){var $$=this,config=$$.config,getPoints=$$.generateGetBarPoints(barIndices,isSub),isRotated=config.axis_rotated,isGrouped=$$.isGrouped(),barRadius=config.bar_radius,barRadiusRatio=config.bar_radius_ratio,getRadius=isNumber(barRadius)&&barRadius>0?function(){return barRadius}:isNumber(barRadiusRatio)?function(w){return w*barRadiusRatio}:null;return function(d,i){
// 4 points that make a bar
var points=getPoints(d,i),indexX=+isRotated,indexY=+!indexX,isNegative=d.value<0,pathRadius=["",""],radius=0;// switch points if axis is rotated, not applicable for sub chart
if(getRadius&&!isGrouped){var index=isRotated?indexY:indexX,barW=points[2][index]-points[0][index];radius=getRadius(barW);var arc="a".concat(radius,",").concat(radius," ").concat(isNegative?"1 0 0":"0 0 1"," ");pathRadius[+!isRotated]="".concat(arc).concat(radius,",").concat(radius),pathRadius[+isRotated]="".concat(arc).concat([-radius,radius][isRotated?"sort":"reverse"]()),isNegative&&pathRadius.reverse()}// path string data shouldn't be containing new line chars
// https://github.com/naver/billboard.js/issues/530
var path=isRotated?"H".concat(points[1][indexX]-radius," ").concat(pathRadius[0],"V").concat(points[2][indexY]-radius," ").concat(pathRadius[1],"H").concat(points[3][indexX]):"V".concat(points[1][indexY]+(isNegative?-radius:radius)," ").concat(pathRadius[0],"H").concat(points[2][indexX]-radius," ").concat(pathRadius[1],"V").concat(points[3][indexY]);return"M".concat(points[0][indexX],",").concat(points[0][indexY]).concat(path,"z")}},generateGetBarPoints:function(barIndices,isSub){var $$=this,axis=isSub?$$.subXAxis:$$.xAxis,barTargetsNum=$$.getIndicesMax(barIndices)+1,barW=$$.getBarW(axis,barTargetsNum),barX=$$.getShapeX(barW,barIndices,!!isSub),barY=$$.getShapeY(!!isSub),barOffset=$$.getShapeOffset($$.isBarType,barIndices,!!isSub),yScale=isSub?$$.getSubYScale:$$.getYScale;return function(d,i){var y0=yScale.call($$,d.id)($$.getShapeYMin(d.id)),offset=barOffset(d,i)||y0,width=isNumber(barW)?barW:barW[d.id]||barW.width,posX=barX(d),posY=barY(d);
// 4 points that make a bar
return $$.config.axis_rotated&&(d.value>0&&posY<y0||d.value<0&&y0<posY)&&(posY=y0),posY-=y0-offset,[[posX,offset],[posX,posY],[posX+width,posY],[posX+width,offset]]}},isWithinBar:function(that){var mouse=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["mouse"])(that),list=getRectSegList(that),_list2=_slicedToArray(list,2),seg0=_list2[0],seg1=_list2[1],x=Math.min(seg0.x,seg1.x),y=Math.min(seg0.y,seg1.y),offset=this.config.bar_sensitivity,_that$getBBox=that.getBBox(),width=_that$getBBox.width,height=_that$getBBox.height;return x-offset<mouse[0]&&mouse[0]<x+width+offset&&y-offset<mouse[1]&&mouse[1]<y+height+offset}}),
// CONCATENATED MODULE: ./src/shape/bubble.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(ChartInternal_ChartInternal.prototype,{
/**
   * Initializer
   * @private
   */
initBubble:function(){var $$=this,config=$$.config;$$.hasType("bubble")&&(config.point_show=!0,config.point_type="circle",config.point_sensitivity=25)},
/**
   * Get user agent's computed value for the total length of the path in user units
   * https://developer.mozilla.org/en-US/docs/Web/API/SVGGeometryElement/getTotalLength
   * @return {Number}
   * @private
   */
getBaseLength:function(){var $$=this,cacheKey="$baseLength",baseLength=$$.getCache(cacheKey);return baseLength||$$.addCache(cacheKey,baseLength=getMinMax("min",[$$.axes.x.select("path").node().getTotalLength(),$$.axes.y.select("path").node().getTotalLength()])),baseLength},
/**
   * Get the radius value for bubble circle
   * @param {Object} d
   * @return {Number}
   * @private
  	 */
getBubbleR:function(d){var $$=this,maxR=$$.config.bubble_maxR;isFunction(maxR)?maxR=maxR(d):!isNumber(maxR)&&(maxR=$$.getBaseLength()/(2*$$.getMaxDataCount())+12);var max=getMinMax("max",$$.getMinMaxData().max.map((function(d){return $$.isBubbleZType(d)?$$.getBubbleZData(d.value,"y"):isObject(d.value)?d.value.mid:d.value}))),maxArea=maxR*maxR*Math.PI,area=($$.isBubbleZType(d)?$$.getBubbleZData(d.value,"z"):d.value)*(maxArea/max);return Math.sqrt(area/Math.PI)},
/**
   * Get bubble dimension data
   * @param {Object|Array} d data value
   * @param {String} type - y or z
   * @return {Number}
   * @private
   */
getBubbleZData:function(d,type){return isObject(d)?d[type]:d["y"===type?0:1]},
/**
   * Determine if bubble has dimension data
   * @param {Object|array} d data value
   * @return {Boolean}
   * @private
   */
isBubbleZType:function(d){var $$=this;return $$.isBubbleType(d)&&(isObject(d.value)&&("z"in d.value||"y"in d.value)||isArray(d.value)&&2===d.value.length)}}),
// CONCATENATED MODULE: ./src/shape/line.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(ChartInternal_ChartInternal.prototype,{initLine:function(){var $$=this;$$.main.select(".".concat(config_classes.chart)).append("g").attr("class",config_classes.chartLines)},updateTargetsForLine:function(targets){var $$=this,config=$$.config,classChartLine=$$.classChartLine.bind($$),classLines=$$.classLines.bind($$),classAreas=$$.classAreas.bind($$),classCircles=$$.classCircles.bind($$),classFocus=$$.classFocus.bind($$),mainLineUpdate=$$.main.select(".".concat(config_classes.chartLines)).selectAll(".".concat(config_classes.chartLine)).data(targets).attr("class",(function(d){return classChartLine(d)+classFocus(d)})),mainLineEnter=mainLineUpdate.enter().append("g").attr("class",classChartLine).style("opacity","0").style("pointer-events","none");
// Lines for each data
// Areas
// Update date for selected circles
mainLineEnter.append("g").attr("class",classLines),mainLineEnter.append("g").attr("class",classAreas),config.point_show&&(config.data_selection_enabled&&mainLineEnter.append("g").attr("class",(function(d){return $$.generateClass(config_classes.selectedCircles,d.id)})),mainLineEnter.append("g").attr("class",classCircles).style("cursor",(function(d){return config.data_selection_isselectable(d)?"pointer":null}))),targets.forEach((function(t){$$.main.selectAll(".".concat(config_classes.selectedCircles).concat($$.getTargetSelectorSuffix(t.id))).selectAll("".concat(config_classes.selectedCircle)).each((function(d){d.value=t.values[d.index].value}))}))},updateLine:function(durationForExit){var $$=this;$$.mainLine=$$.main.selectAll(".".concat(config_classes.lines)).selectAll(".".concat(config_classes.line)).data($$.lineData.bind($$)),$$.mainLine.exit().transition().duration(durationForExit).style("opacity","0").remove(),$$.mainLine=$$.mainLine.enter().append("path").attr("class",(function(d){return"".concat($$.classLine.bind($$)(d)," ").concat($$.extraLineClasses(d)||"")})).style("stroke",$$.color).merge($$.mainLine).style("opacity",$$.initialOpacity.bind($$)).style("shape-rendering",(function(d){return $$.isStepType(d)?"crispEdges":""})).attr("transform",null)},redrawLine:function(drawLine,withTransition){return[(withTransition?this.mainLine.transition(getRandom()):this.mainLine).attr("d",drawLine).style("stroke",this.color).style("opacity","1")]},
/**
   * Get the curve interpolate
   * @param {Array} d Data object
   * @return {Function}
   * @private
   */
getCurve:function(d){var $$=this,isRotatedStepType=$$.config.axis_rotated&&$$.isStepType(d);
// when is step & rotated, should be computed in different way
// https://github.com/naver/billboard.js/issues/471
return isRotatedStepType?function(context){var step=$$.getInterpolate(d)(context);// keep the original method
return step.orgPoint=step.point,step.pointRotated=function(x,y){1===this._point&&(this._point=2);var y1=this._y*(1-this._t)+y*this._t;this._context.lineTo(this._x,y1),this._context.lineTo(x,y1),this._x=x,this._y=y},step.point=function(x,y){0===this._point?this.orgPoint(x,y):this.pointRotated(x,y)},step}:$$.getInterpolate(d)},generateDrawLine:function(lineIndices,isSub){var $$=this,config=$$.config,lineConnectNull=config.line_connectNull,isRotated=config.axis_rotated,getPoints=$$.generateGetLinePoints(lineIndices,isSub),yScaleGetter=isSub?$$.getSubYScale:$$.getYScale,xValue=function(d){return(isSub?$$.subxx:$$.xx).call($$,d)},yValue=function(d,i){return $$.isGrouped(d.id)?getPoints(d,i)[0][1]:yScaleGetter.call($$,d.id)($$.getBaseValue(d))},line=Object(external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_["line"])();line=isRotated?line.x(yValue).y(xValue):line.x(xValue).y(yValue),lineConnectNull||(line=line.defined((function(d){return null!==$$.getBaseValue(d)})));var x=isSub?$$.subX:$$.x;return function(d){var path,y=yScaleGetter.call($$,d.id),values=lineConnectNull?$$.filterRemoveNull(d.values):d.values,x0=0,y0=0;if($$.isLineType(d)){var regions=config.data_regions[d.id];regions?path=$$.lineWithRegions(values,x,y,regions):($$.isStepType(d)&&(values=$$.convertValuesToStep(values)),path=line.curve($$.getCurve(d))(values))}else values[0]&&(x0=x(values[0].x),y0=y(values[0].value)),path=isRotated?"M ".concat(y0," ").concat(x0):"M ".concat(x0," ").concat(y0);return path||"M 0 0"}},generateGetLinePoints:function(lineIndices,isSubValue){
// partial duplication of generateGetBarPoints
var $$=this,config=$$.config,isSub=!!isSubValue,x=$$.getShapeX(0,lineIndices,isSub),y=$$.getShapeY(isSub),lineOffset=$$.getShapeOffset($$.isLineType,lineIndices,isSub),yScale=isSub?$$.getSubYScale:$$.getYScale;return function(d,i){var y0=yScale.call($$,d.id)($$.getShapeYMin(d.id)),offset=lineOffset(d,i)||y0,posX=x(d),posY=y(d);config.axis_rotated&&(d.value>0&&posY<y0||d.value<0&&y0<posY)&&(posY=y0);
// 1 point that marks the line position
var point=[posX,posY-(y0-offset)];return[point,point,// from here and below, needed for compatibility
point,point]}},lineWithRegions:function(d,x,y,_regions){var xp,yp,diff,diffx2,$$=this,config=$$.config,isRotated=config.axis_rotated,isTimeSeries=$$.isTimeSeries(),xOffset=$$.isCategorized()?.5:0,regions=[],dasharray="2 2",isWithinRegions=function(withinX,withinRegions){for(var reg,i=0;reg=withinRegions[i];i++)if(reg.start<withinX&&withinX<=reg.end)return reg.style;return!1};
// Check start/end of regions
if(isDefined(_regions))for(var reg,getValue=function(v,def){return isUndefined(v)?def:isTimeSeries?$$.parseDate(v):v},i=0;reg=_regions[i];i++){var start=getValue(reg.start,d[0].x),end=getValue(reg.end,d[d.length-1].x),style=reg.style||{dasharray:dasharray};regions[i]={start:start,end:end,style:style}}// Set scales
for(var data,xValue=isRotated?function(dt){return y(dt.value)}:function(dt){return x(dt.x)},yValue=isRotated?function(dt){return x(dt.x)}:function(dt){return y(dt.value)},generateM=function(points){return"M".concat(points[0][0],",").concat(points[0][1],"L").concat(points[1][0],",").concat(points[1][1])},sWithRegion=isTimeSeries?function(d0,d1,k,timeseriesDiff){var x0=d0.x.getTime(),xDiff=d1.x-d0.x,xv0=new Date(x0+xDiff*k),xv1=new Date(x0+xDiff*(k+timeseriesDiff)),points=isRotated?[[y(yp(k)),x(xv0)],[y(yp(k+diff)),x(xv1)]]:[[x(xv0),y(yp(k))],[x(xv1),y(yp(k+diff))]];return generateM(points)}:function(d0,d1,k,otherDiff){var points=isRotated?[[y(yp(k),!0),x(xp(k))],[y(yp(k+otherDiff),!0),x(xp(k+otherDiff))]]:[[x(xp(k),!0),y(yp(k))],[x(xp(k+otherDiff),!0),y(yp(k+otherDiff))]];return generateM(points)},path="",_i=0;data=d[_i];_i++){var prevData=d[_i-1],hasPrevData=prevData&&isValue(prevData.value);
// https://github.com/naver/billboard.js/issues/1172
style=isWithinRegions(data.x,regions);if(isValue(data.value))// Draw as normal
if(!isUndefined(regions)&&style&&hasPrevData){if(hasPrevData){try{style=style.dasharray.split(" ")}catch(e){style=dasharray.split(" ")}// Draw with region // TODO: Fix for horizotal charts
xp=$$.getScale(prevData.x+xOffset,data.x+xOffset,isTimeSeries),yp=$$.getScale(prevData.value,data.value);var dx=x(data.x)-x(prevData.x),dy=y(data.value)-y(prevData.value),dd=Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2));diff=style[0]/dd,diffx2=diff*style[1];for(var _j=diff;_j<=1;_j+=diffx2)path+=sWithRegion(prevData,data,_j,diff),_j+diffx2>=1&&(path+=sWithRegion(prevData,data,1,0))}}else path+="".concat(_i&&hasPrevData?"L":"M").concat(xValue(data),",").concat(yValue(data))}return path},updateAreaGradient:function(){var $$=this;$$.data.targets.forEach((function(d){var id="".concat($$.datetimeId,"-areaGradient").concat($$.getTargetSelectorSuffix(d.id));if($$.isAreaType(d)&&$$.defs.select("#".concat(id)).empty()){var color=$$.color(d),_$$$config$area_linea=$$.config.area_linearGradient,_$$$config$area_linea2=_$$$config$area_linea.x,x=void 0===_$$$config$area_linea2?[0,0]:_$$$config$area_linea2,_$$$config$area_linea3=_$$$config$area_linea.y,y=void 0===_$$$config$area_linea3?[0,1]:_$$$config$area_linea3,_$$$config$area_linea4=_$$$config$area_linea.stops,stops=void 0===_$$$config$area_linea4?[[0,color,1],[1,color,0]]:_$$$config$area_linea4,linearGradient=$$.defs.append("linearGradient").attr("id","".concat(id)).attr("x1",x[0]).attr("x2",x[1]).attr("y1",y[0]).attr("y2",y[1]);stops.forEach((function(v){var stopColor=isFunction(v[1])?v[1](d.id):v[1];linearGradient.append("stop").attr("offset",v[0]).attr("stop-color",stopColor||color).attr("stop-opacity",v[2])}))}}))},updateAreaColor:function(d){var $$=this;return $$.config.area_linearGradient?"url(#".concat($$.datetimeId,"-areaGradient").concat($$.getTargetSelectorSuffix(d.id),")"):$$.color(d)},updateArea:function(durationForExit){var $$=this;$$.config.area_linearGradient&&$$.updateAreaGradient(),$$.mainArea=$$.main.selectAll(".".concat(config_classes.areas)).selectAll(".".concat(config_classes.area)).data($$.lineData.bind($$)),$$.mainArea.exit().transition().duration(durationForExit).style("opacity","0").remove(),$$.mainArea=$$.mainArea.enter().append("path").attr("class",$$.classArea.bind($$)).style("fill",$$.updateAreaColor.bind($$)).style("opacity",(function(){return $$.orgAreaOpacity=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this).style("opacity"),"0"})).merge($$.mainArea),$$.mainArea.style("opacity",$$.orgAreaOpacity)},redrawArea:function(drawArea,withTransition){var $$=this;return[(withTransition?$$.mainArea.transition(getRandom()):$$.mainArea).attr("d",drawArea).style("fill",$$.updateAreaColor.bind($$)).style("opacity",(function(d){return($$.isAreaRangeType(d)?$$.orgAreaOpacity/1.75:$$.orgAreaOpacity)+""}))]},
/**
   * Generate area path data
   * @param areaIndices
   * @param isSub
   * @return {function(*=): (*|string)}
   * @private
   */
generateDrawArea:function(areaIndices,isSub){var $$=this,config=$$.config,lineConnectNull=config.line_connectNull,isRotated=config.axis_rotated,getPoints=$$.generateGetAreaPoints(areaIndices,isSub),yScaleGetter=isSub?$$.getSubYScale:$$.getYScale,xValue=function(d){return(isSub?$$.subxx:$$.xx).call($$,d)},value0=function(d,i){return $$.isGrouped(d.id)?getPoints(d,i)[0][1]:yScaleGetter.call($$,d.id)($$.isAreaRangeType(d)?$$.getAreaRangeData(d,"high"):$$.getShapeYMin(d.id))},value1=function(d,i){return $$.isGrouped(d.id)?getPoints(d,i)[1][1]:yScaleGetter.call($$,d.id)($$.isAreaRangeType(d)?$$.getAreaRangeData(d,"low"):d.value)};return function(d){var path,values=lineConnectNull?$$.filterRemoveNull(d.values):d.values,x0=0,y0=0;if($$.isAreaType(d)){var area=Object(external_commonjs_d3_shape_commonjs2_d3_shape_amd_d3_shape_root_d3_["area"])();area=isRotated?area.y(xValue).x0(value0).x1(value1):area.x(xValue).y0(config.area_above?0:value0).y1(value1),lineConnectNull||(area=area.defined((function(d){return null!==$$.getBaseValue(d)}))),$$.isStepType(d)&&(values=$$.convertValuesToStep(values)),path=area.curve($$.getCurve(d))(values)}else values[0]&&(x0=$$.x(values[0].x),y0=$$.getYScale(d.id)(values[0].value)),path=isRotated?"M ".concat(y0," ").concat(x0):"M ".concat(x0," ").concat(y0);return path||"M 0 0"}},generateGetAreaPoints:function(areaIndices,isSub){
// partial duplication of generateGetBarPoints
var $$=this,config=$$.config,x=$$.getShapeX(0,areaIndices,!!isSub),y=$$.getShapeY(!!isSub),areaOffset=$$.getShapeOffset($$.isAreaType,areaIndices,!!isSub),yScale=isSub?$$.getSubYScale:$$.getYScale;return function(d,i){var y0=yScale.call($$,d.id)($$.getShapeYMin(d.id)),offset=areaOffset(d,i)||y0,posX=x(d),posY=y(d);
// 1 point that marks the area position
return config.axis_rotated&&(d.value>0&&posY<y0||d.value<0&&y0<posY)&&(posY=y0),[[posX,offset],[posX,posY-(y0-offset)],[posX,posY-(y0-offset)],// needed for compatibility
[posX,offset]]}},updateCircle:function(){var $$=this;if($$.config.point_show){$$.mainCircle=$$.main.selectAll(".".concat(config_classes.circles)).selectAll(".".concat(config_classes.circle)).data((function(d){return!$$.isBarType(d)&&(!$$.isLineType(d)||$$.shouldDrawPointsForLine(d))&&$$.labelishData(d)})),$$.mainCircle.exit().remove();var fn=$$.point("create",this,$$.pointR.bind($$),$$.color);$$.mainCircle=$$.mainCircle.enter().append(fn).merge($$.mainCircle).style("stroke",$$.color).style("opacity",$$.initialOpacityForCircle.bind($$))}},redrawCircle:function(cx,cy,withTransition,flow){var $$=this,selectedCircles=$$.main.selectAll(".".concat(config_classes.selectedCircle));if(!$$.config.point_show)return[];var mainCircles=[];$$.mainCircle.each((function(d){var fn=$$.point("update",$$,cx,cy,$$.opacityForCircle.bind($$),$$.color,withTransition,flow,selectedCircles).bind(this),result=fn(d);mainCircles.push(result)}));var posAttr=$$.isCirclePoint()?"c":"";return[mainCircles,selectedCircles.attr("".concat(posAttr,"x"),cx).attr("".concat(posAttr,"y"),cy)]},circleX:function(d){var $$=this,hasValue=isValue(d.x);return $$.config.zoom_enabled&&$$.zoomScale?hasValue?$$.zoomScale(d.x):null:hasValue?$$.x(d.x):null},updateCircleY:function(){var $$=this,getPoints=$$.generateGetLinePoints($$.getShapeIndices($$.isLineType),!1);$$.circleY=function(d,i){var id=d.id;return $$.isGrouped(id)?getPoints(d,i)[0][1]:$$.getYScale(id)($$.getBaseValue(d))}},getCircles:function(i,id){var $$=this,suffix=isValue(i)?"-".concat(i):"";return(id?$$.main.selectAll(".".concat(config_classes.circles).concat($$.getTargetSelectorSuffix(id))):$$.main).selectAll(".".concat(config_classes.circle).concat(suffix))},expandCircles:function(i,id,reset){var $$=this,r=$$.pointExpandedR.bind($$);reset&&$$.unexpandCircles();var circles=$$.getCircles(i,id).classed(config_classes.EXPANDED,!0),scale=r(circles)/$$.config.point_r,ratio=1-scale;$$.isCirclePoint()?circles.attr("r",r):circles.each((function(){var point=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this);if("circle"===this.tagName)point.attr("r",r);else{var _this$getBBox=this.getBBox(),width=_this$getBBox.width,height=_this$getBBox.height,x=ratio*(+point.attr("x")+width/2),y=ratio*(+point.attr("y")+height/2);point.attr("transform","translate(".concat(x," ").concat(y,") scale(").concat(scale,")"))}}))},unexpandCircles:function(i){var $$=this,r=$$.pointR.bind($$),circles=$$.getCircles(i).filter((function(){return Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this).classed(config_classes.EXPANDED)})).classed(config_classes.EXPANDED,!1);circles.attr("r",r),$$.isCirclePoint()||circles.attr("transform","scale(".concat(r(circles)/$$.config.point_r,")"))},pointR:function(d){var $$=this,config=$$.config,pointR=config.point_r,r=pointR;return $$.isStepType(d)?r=0:$$.isBubbleType(d)?r=$$.getBubbleR(d):isFunction(pointR)&&(r=pointR(d)),r},pointExpandedR:function(d){var $$=this,config=$$.config,scale=$$.isBubbleType(d)?1.15:1.75;return config.point_focus_expand_enabled?config.point_focus_expand_r||$$.pointR(d)*scale:$$.pointR(d)},pointSelectR:function(d){var $$=this,selectR=$$.config.point_select_r;return isFunction(selectR)?selectR(d):selectR||4*$$.pointR(d)},isWithinCircle:function(node,r){var mouse=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["mouse"])(node),element=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(node),prefix=this.isCirclePoint()?"c":"",cx=+element.attr("".concat(prefix,"x")),cy=+element.attr("".concat(prefix,"y"));
// if node don't have cx/y or x/y attribute value
if(!cx&&!cy&&1===node.nodeType){var _ref=node.getBBox?node.getBBox():node.getBoundingClientRect(),x=_ref.x,y=_ref.y;cx=x,cy=y}return Math.sqrt(Math.pow(cx-mouse[0],2)+Math.pow(cy-mouse[1],2))<(r||this.config.point_sensitivity)},isWithinStep:function(that,y){return Math.abs(y-Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["mouse"])(that)[1])<30},shouldDrawPointsForLine:function(d){var linePoint=this.config.line_point;return!0===linePoint||isArray(linePoint)&&-1!==linePoint.indexOf(d.id)}}),
// CONCATENATED MODULE: ./src/shape/point.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(ChartInternal_ChartInternal.prototype,{hasValidPointType:function(type){return/^(circle|rect(angle)?|polygon|ellipse|use)$/i.test(type||this.config.point_type)},hasValidPointDrawMethods:function(type){var pointType=type||this.config.point_type;return isObjectType(pointType)&&isFunction(pointType.create)&&isFunction(pointType.update)},insertPointInfoDefs:function(point,id){var $$=this,copyAttr=function(from,target){for(var name,attribs=from.attributes,i=0;name=attribs[i];i++)name=name.name,target.setAttribute(name,from.getAttribute(name))},doc=(new DOMParser).parseFromString(point,"image/svg+xml"),node=doc.documentElement,clone=browser_doc.createElementNS(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["namespaces"].svg,node.nodeName.toLowerCase());if(clone.id=id,clone.style.fill="inherit",clone.style.stroke="inherit",copyAttr(node,clone),node.childNodes&&node.childNodes.length){var parent=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(clone);"innerHTML"in clone?parent.html(node.innerHTML):toArray(node.childNodes).forEach((function(v){copyAttr(v,parent.append(v.tagName).node())}))}$$.defs.node().appendChild(clone)},pointFromDefs:function(id){return this.defs.select("#".concat(id))},updatePointClass:function(d){var $$=this,pointClass=!1;return(isObject(d)||$$.mainCircle)&&(pointClass=!0===d?$$.mainCircle.each((function(d){var className=$$.classCircle.bind($$)(d);this.getAttribute("class").indexOf(config_classes.EXPANDED)>-1&&(className+=" ".concat(config_classes.EXPANDED)),this.setAttribute("class",className)})):$$.classCircle(d)),pointClass},generatePoint:function(){var $$=this,config=$$.config,ids=[],pattern=notEmpty(config.point_pattern)?config.point_pattern:[config.point_type];return function(method,context){for(var _len=arguments.length,args=Array(_len>2?_len-2:0),_key=2;_key<_len;_key++)args[_key-2]=arguments[_key];return function(d){var id=d.id||d.data&&d.data.id||d,element=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this);ids.indexOf(id)<0&&ids.push(id);var point=pattern[ids.indexOf(id)%pattern.length];if($$.hasValidPointType(point))point=$$[point];else if(!$$.hasValidPointDrawMethods(point)){var pointId="".concat($$.datetimeId,"-point-").concat(id),pointFromDefs=$$.pointFromDefs(pointId);if(pointFromDefs.size()<1&&$$.insertPointInfoDefs(point,pointId),"create"===method)return $$.custom.create.bind(context).apply(void 0,[element,pointId].concat(args));if("update"===method)return $$.custom.update.bind(context).apply(void 0,[element].concat(args))}return point[method].bind(context).apply(void 0,[element].concat(args))}}},getTransitionName:function(){return getRandom()},custom:{create:function(element,id,sizeFn,fillStyleFn){return element.append("use").attr("xlink:href","#".concat(id)).attr("class",this.updatePointClass.bind(this)).style("fill",fillStyleFn).node()},update:function(element,xPosFn,yPosFn,opacityStyleFn,fillStyleFn,withTransition,flow,selectedCircles){var $$=this,_element$node$getBBox=element.node().getBBox(),width=_element$node$getBBox.width,height=_element$node$getBBox.height,xPosFn2=function(d){return xPosFn(d)-width/2},mainCircles=element;if(withTransition){var transitionName=$$.getTransitionName();flow&&mainCircles.attr("x",xPosFn2),mainCircles=mainCircles.transition(transitionName),selectedCircles.transition($$.getTransitionName())}return mainCircles.attr("x",xPosFn2).attr("y",(function(d){return yPosFn(d)-height/2})).style("opacity",opacityStyleFn).style("fill",fillStyleFn)}},
// 'circle' data point
circle:{create:function(element,sizeFn,fillStyleFn){return element.append("circle").attr("class",this.updatePointClass.bind(this)).attr("r",sizeFn).style("fill",fillStyleFn).node()},update:function(element,xPosFn,yPosFn,opacityStyleFn,fillStyleFn,withTransition,flow,selectedCircles){var $$=this,mainCircles=element;if($$.hasType("bubble")&&mainCircles.attr("r",$$.pointR.bind($$)),withTransition){var transitionName=$$.getTransitionName();flow&&mainCircles.attr("cx",xPosFn),mainCircles.attr("cx")&&(mainCircles=mainCircles.transition(transitionName)),selectedCircles.transition($$.getTransitionName())}return mainCircles.attr("cx",xPosFn).attr("cy",yPosFn).style("opacity",opacityStyleFn).style("fill",fillStyleFn)}},
// 'rectangle' data point
rectangle:{create:function(element,sizeFn,fillStyleFn){var rectSizeFn=function(d){return 2*sizeFn(d)};return element.append("rect").attr("class",this.updatePointClass.bind(this)).attr("width",rectSizeFn).attr("height",rectSizeFn).style("fill",fillStyleFn).node()},update:function(element,xPosFn,yPosFn,opacityStyleFn,fillStyleFn,withTransition,flow,selectedCircles){var $$=this,r=$$.config.point_r,rectXPosFn=function(d){return xPosFn(d)-r},mainCircles=element;if(withTransition){var transitionName=$$.getTransitionName();flow&&mainCircles.attr("x",rectXPosFn),mainCircles=mainCircles.transition(transitionName),selectedCircles.transition($$.getTransitionName())}return mainCircles.attr("x",rectXPosFn).attr("y",(function(d){return yPosFn(d)-r})).style("opacity",opacityStyleFn).style("fill",fillStyleFn)}}});var radar_cacheKey="$radarPoints";extend(ChartInternal_ChartInternal.prototype,{initRadar:function(){var $$=this,config=$$.config;$$.hasType("radar")&&($$.radars=$$.main.select(".".concat(config_classes.chart)).append("g").attr("class",config_classes.chartRadars),$$.radars.levels=$$.radars.append("g").attr("class",config_classes.levels),$$.radars.axes=$$.radars.append("g").attr("class",config_classes.axis),$$.radars.shapes=$$.radars.append("g").attr("class",config_classes.shapes),$$.maxValue=config.radar_axis_max||$$.getMinMaxData().max[0].value)},getRadarSize:function(){var $$=this,config=$$.config,padding=config.axis_x_categories.length<4?-20:10,size=(Math.min($$.arcWidth,$$.arcHeight)-padding)/2;return[size,size]},updateTargetsForRadar:function(targets){var $$=this,config=$$.config;isEmpty(config.axis_x_categories)&&(config.axis_x_categories=getRange(0,getMinMax("max",targets.map((function(v){return v.values.length}))))),$$.generateRadarPoints()},getRadarPosition:function(type,index,range,ratio){var $$=this,config=$$.config,_$$$getRadarSize=$$.getRadarSize(),_$$$getRadarSize2=_slicedToArray(_$$$getRadarSize,2),width=_$$$getRadarSize2[0],height=_$$$getRadarSize2[1],edge=config.axis_x_categories.length,isClockwise=config.radar_direction_clockwise,pos=toArray(type).map((function(v){return getPosition(isClockwise,v,edge,index,isDefined(range)?range:"x"===type?width:height,isNumber(ratio)?ratio:config.radar_size_ratio)}));return 1===pos.length?pos[0]:pos},
/**
   * Generate data points
   * @private
   */
generateRadarPoints:function(){var $$=this,targets=$$.data.targets,_$$$getRadarSize3=$$.getRadarSize(),_$$$getRadarSize4=_slicedToArray(_$$$getRadarSize3,2),width=_$$$getRadarSize4[0],height=_$$$getRadarSize4[1],points=$$.getCache(radar_cacheKey)||{},size=points._size;size&&(size.width===width||size.height===height)||(targets.forEach((function(d){points[d.id]=d.values.map((function(v,i){return $$.getRadarPosition(["x","y"],i,void 0,$$.getRatio("radar",v))}))})),points._size={width:width,height:height},$$.addCache(radar_cacheKey,points))},redrawRadar:function(durationForExit){var $$=this,translate=$$.getTranslate("radar");translate&&($$.radars.attr("transform",translate),$$.main.selectAll(".".concat(config_classes.circles)).attr("transform",translate),$$.main.select(".".concat(config_classes.chartTexts)).attr("transform",translate),$$.generateRadarPoints(),$$.updateRadarLevel(),$$.updateRadarAxes(),$$.updateRadarShape(durationForExit))},generateGetRadarPoints:function(){var $$=this,points=$$.getCache(radar_cacheKey);return function(d,i){var point=points[d.id][i];return[point,point,point,point]}},updateRadarLevel:function(){var $$=this,config=$$.config,_$$$getRadarSize5=$$.getRadarSize(),_$$$getRadarSize6=_slicedToArray(_$$$getRadarSize5,2),width=_$$$getRadarSize6[0],height=_$$$getRadarSize6[1],depth=config.radar_level_depth,edge=config.axis_x_categories.length,showText=config.radar_level_text_show,radarLevels=$$.radars.levels,levelData=getRange(0,depth),radius=config.radar_size_ratio*Math.min(width,height),levelRatio=levelData.map((function(l){return radius*((l+1)/depth)})),levelTextFormat=config.radar_level_text_format,points=levelData.map((function(v){var range=levelRatio[v],pos=getRange(0,edge).map((function(i){return $$.getRadarPosition(["x","y"],i,range,1).join(",")}));return pos.join(" ")})),level=radarLevels.selectAll(".".concat(config_classes.level)).data(levelData);level.exit().remove();var levelEnter=level.enter().append("g").attr("class",(function(d,i){return"".concat(config_classes.level," ").concat(config_classes.level,"-").concat(i)}));levelEnter.append("polygon").style("visibility",config.radar_level_show?null:"hidden"),showText&&(radarLevels.select("text").empty()&&radarLevels.append("text").attr("dx","-.5em").attr("dy","-.7em").style("text-anchor","end").text((function(){return levelTextFormat(0)})),levelEnter.append("text").attr("dx","-.5em").style("text-anchor","end").text((function(d){return levelTextFormat($$.maxValue/levelData.length*(d+1))}))),levelEnter.merge(level).attr("transform",(function(d){return"translate(".concat(width-levelRatio[d],", ").concat(height-levelRatio[d],")")})).selectAll("polygon").attr("points",(function(d){return points[d]})),showText&&radarLevels.selectAll("text").attr("x",(function(d){return isUndefined(d)?width:points[d].split(",")[0]})).attr("y",(function(d){return isUndefined(d)?height:0}))},updateRadarAxes:function(){var $$=this,config=$$.config,_$$$getRadarSize7=$$.getRadarSize(),_$$$getRadarSize8=_slicedToArray(_$$$getRadarSize7,2),width=_$$$getRadarSize8[0],height=_$$$getRadarSize8[1],categories=config.axis_x_categories,axis=$$.radars.axes.selectAll("g").data(categories);axis.exit().remove();var axisEnter=axis.enter().append("g").attr("class",(function(d,i){return"".concat(config_classes.axis,"-").concat(i)}));
// axis text
if(config.radar_axis_line_show&&axisEnter.append("line"),config.radar_axis_text_show&&axisEnter.append("text"),axis=axisEnter.merge(axis),config.radar_axis_line_show&&axis.select("line").attr("x1",width).attr("y1",height).attr("x2",(function(d,i){return $$.getRadarPosition("x",i)})).attr("y2",(function(d,i){return $$.getRadarPosition("y",i)})),config.radar_axis_text_show){var _config$radar_axis_te=config.radar_axis_text_position,_config$radar_axis_te2=_config$radar_axis_te.x,x=void 0===_config$radar_axis_te2?0:_config$radar_axis_te2,_config$radar_axis_te3=_config$radar_axis_te.y,y=void 0===_config$radar_axis_te3?0:_config$radar_axis_te3;axis.select("text").style("text-anchor","middle").attr("dy",".5em").call((function(selection){selection.each((function(d){setTextValue(Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this),d+"",[-.6,1.2])}))})).datum((function(d,i){return{index:i}})).attr("transform",(function(d){isUndefined(this.width)&&(this.width=this.getBoundingClientRect().width/2);var posX=$$.getRadarPosition("x",d.index,void 0,1),posY=Math.round($$.getRadarPosition("y",d.index,void 0,1));return posX>width?posX+=this.width+x:Math.round(posX)<width&&(posX-=this.width+x),posY>height?(posY/2===height&&"tspan"===this.firstChild.tagName&&this.firstChild.setAttribute("dy","0em"),posY+=y):posY<height&&(posY-=y),"translate(".concat(posX," ").concat(posY,")")}))}$$.bindEvent()},bindEvent:function(){var _this=this,$$=this,config=$$.config;if(config.interaction_enabled){var isMouse="mouse"===$$.inputType,getIndex=function(){var target=external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["event"].target;// in case of multilined axis text
/tspan/i.test(target.tagName)&&(target=target.parentNode);var d=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(target).datum();return d&&1===Object.keys(d).length?d.index:void 0},hide=function(){var index=getIndex(),noIndex=isUndefined(index);(isMouse||noIndex)&&(_this.hideTooltip(),_this.unexpandCircles(),isMouse?$$.setOverOut(!1,index):noIndex&&$$.callOverOutForTouch())};$$.radars.select(".".concat(config_classes.axis)).on(isMouse?"mouseover ":"touchstart",(function(){if(!$$.transiting)// skip while transiting
{var index=getIndex();$$.selectRectForSingle($$.svg.node(),null,index),isMouse?$$.setOverOut(!0,index):$$.callOverOutForTouch(index)}})).on("mouseout",isMouse?hide:null),isMouse||$$.svg.on("touchstart",hide)}},updateRadarShape:function(durationForExit){var $$=this,targets=$$.data.targets,points=$$.getCache(radar_cacheKey),areas=$$.radars.shapes.selectAll("polygon").data(targets),areasEnter=areas.enter().append("g").attr("class",$$.classChartRadar.bind($$));areas.exit().transition().duration(durationForExit).remove(),areasEnter.append("polygon").merge(areas).style("fill",$$.color).style("stroke",$$.color).attr("points",(function(d){return points[d.id].join(" ")}))},
/**
   * Get data point x coordinate
   * @param {Object} d Data object
   * @return {Number}
   * @private
   */
radarCircleX:function(d){return this.getCache(radar_cacheKey)[d.id][d.index][0]},
/**
   * Get data point y coordinate
   * @param {Object} d Data object
   * @return {Number}
   * @private
   */
radarCircleY:function(d){return this.getCache(radar_cacheKey)[d.id][d.index][1]}}),
// CONCATENATED MODULE: ./src/internals/text.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(ChartInternal_ChartInternal.prototype,{
/**
   * Initializes the text
   * @private
   */
initText:function(){var $$=this;$$.main.select(".".concat(config_classes.chart)).append("g").attr("class",config_classes.chartTexts),$$.mainText=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["selectAll"])([])},
/**
   * Update chartText
   * @private
   * @param {Object} $$.data.targets
   */
updateTargetsForText:function(targets){var $$=this,classChartText=$$.classChartText.bind($$),classTexts=$$.classTexts.bind($$),classFocus=$$.classFocus.bind($$),mainTextUpdate=$$.main.select(".".concat(config_classes.chartTexts)).selectAll(".".concat(config_classes.chartText)).data(targets).attr("class",(function(d){return classChartText(d)+classFocus(d)})),mainTextEnter=mainTextUpdate.enter().append("g").attr("class",classChartText).style("opacity","0").style("pointer-events","none");mainTextEnter.append("g").attr("class",classTexts)},
/**
   * Update text
   * @private
   * @param {Number} Fade-out transition duration
   */
updateText:function(durationForExit){var _this=this,$$=this,config=$$.config,dataFn=$$.labelishData.bind($$),classText=$$.classText.bind($$);$$.mainText=$$.main.selectAll(".".concat(config_classes.texts)).selectAll(".".concat(config_classes.text)).data((function(d){return _this.isRadarType(d)?d.values:dataFn(d)})),$$.mainText.exit().transition().duration(durationForExit).style("fill-opacity","0").remove(),$$.mainText=$$.mainText.enter().append("text").merge($$.mainText).attr("class",classText).attr("text-anchor",(function(d){return config.axis_rotated?d.value<0?"end":"start":"middle"})).style("fill",$$.updateTextColor.bind($$)).style("fill-opacity","0").text((function(d,i,j){var value=$$.isBubbleZType(d)?$$.getBubbleZData(d.value,"z"):d.value;return $$.dataLabelFormat(d.id)(value,d.id,i,j)}))},updateTextColor:function(d){var color,$$=this,labelColors=$$.config.data_labels_colors;return isString(labelColors)?color=labelColors:isObject(labelColors)&&(color=labelColors[d.id]),color||$$.color(d)},
/**
   * Redraw chartText
   * @param {Function} x Positioning function for x
   * @param {Function} y Positioning function for y
   * @param {Boolean} forFlow
   * @param {Boolean} withTransition transition is enabled
   * @private
   */
redrawText:function(x,y,forFlow,withTransition){var $$=this,t=getRandom(),opacityForText=forFlow?0:$$.opacityForText.bind($$);
// need to return 'true' as of being pushed to the redraw list
// ref: getRedrawList()
return $$.mainText.each((function(d,i){var text=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this);// do not apply transition for newly added text elements
(withTransition&&text.attr("x")?text.transition(t):text).attr("x",x.bind(this)(d,i)).attr("y",y.bind(this)(d,i)).style("fill",$$.updateTextColor.bind($$)).style("fill-opacity",opacityForText)})),!0},
/**
   * Gets the getBoundingClientRect value of the element
   * @private
   * @param {HTMLElement|d3.selection} element
   * @param {String} className
   * @returns {Object} value of element.getBoundingClientRect()
   */
getTextRect:function(element,className){var $$=this,base=element.node?element.node():element;/text/i.test(base.tagName)||(base=base.querySelector("text"));var text=base.textContent,cacheKey="$".concat(text.replace(/\W/g,"_")),rect=$$.getCache(cacheKey);return rect||($$.svg.append("text").style("visibility","hidden").style("font",Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(base).style("font")).classed(className,!0).text(text).call((function(v){rect=getBoundingRect(v.node())})).remove(),$$.addCache(cacheKey,rect)),rect},
/**
   * Gets the x or y coordinate of the text
   * @param {Object} indices Indices values
   * @param {Boolean} forX whether or not to x
   * @returns {Number} coordinates
   * @private
   */
generateXYForText:function(indices,forX){var $$=this,types=Object.keys(indices),points={},getter=forX?$$.getXForText:$$.getYForText;return $$.hasType("radar")&&types.push("radar"),types.forEach((function(v){points[v]=$$["generateGet".concat(capitalize(v),"Points")](indices[v],!1)})),function(d,i){var type=($$.isAreaType(d)?"area":$$.isBarType(d)&&"bar")||$$.isRadarType(d)&&"radar"||"line";return getter.call($$,points[type](d,i),d,this)}},
/**
   * Get centerized text position for bar type data.label.text
   * @private
   * @param {Object} d Data object
   * @param {Array} points Data points position
   * @param {HTMLElement} textElement Data label text element
   * @returns {Number} Position value
   */
getCenteredTextPos:function(d,points,textElement){var $$=this,config=$$.config,isRotated=config.axis_rotated;if(config.data_labels.centered&&$$.isBarType(d)){var rect=getBoundingRect(textElement),isPositive=d.value>=0;if(isRotated){var w=(isPositive?points[1][1]-points[0][1]:points[0][1]-points[1][1])/2+rect.width/2;return isPositive?-w-3:w+2}var h=(isPositive?points[0][1]-points[1][1]:points[1][1]-points[0][1])/2+rect.height/2;return isPositive?h:-h-2}return 0},
/**
   * Get data.labels.position value
   * @param {String} id Data id value
   * @param {String} type x | y
   * @return {Number} Position value
   * @private
   */
getTextPos:function(id,type){var pos=this.config.data_labels_position;return(id in pos?pos[id]:pos)[type]||0},
/**
   * Gets the x coordinate of the text
   * @private
   * @param {Object} points
   * @param {Object} data
   * @param {HTMLElement} element
   * @returns {Number} x coordinate
   */
getXForText:function(points,d,textElement){var xPos,padding,$$=this,config=$$.config,isRotated=config.axis_rotated;
// show labels regardless of the domain if value is null
if(isRotated?(padding=$$.isBarType(d)?4:6,xPos=points[2][1]+padding*(d.value<0?-1:1)):xPos=$$.hasType("bar")?(points[2][0]+points[0][0])/2:points[0][0],null===d.value)if(xPos>$$.width){var _getBoundingRect=getBoundingRect(textElement),width=_getBoundingRect.width;xPos=$$.width-width}else xPos<0&&(xPos=4);return isRotated&&(xPos+=$$.getCenteredTextPos(d,points,textElement)),xPos+$$.getTextPos(d.id,"x")},
/**
   * Gets the y coordinate of the text
   * @private
   * @param {Object} points
   * @param {Object} data
   * @param {HTMLElement} element
   * @returns {Number} y coordinate
   */
getYForText:function(points,d,textElement){var yPos,$$=this,config=$$.config,isRotated=config.axis_rotated,r=config.point_r,rect=getBoundingRect(textElement),baseY=3;if(isRotated)yPos=(points[0][0]+points[2][0]+.6*rect.height)/2;else if(yPos=points[2][1],isNumber(r)&&r>5&&($$.isLineType(d)||$$.isScatterType(d))&&(baseY+=config.point_r/2.3),d.value<0||0===d.value&&!$$.hasPositiveValue&&$$.hasNegativeValue)yPos+=rect.height+($$.isBarType(d)?-baseY:baseY);else{var diff=2*-baseY;$$.isBarType(d)?diff=-baseY:$$.isBubbleType(d)&&(diff=baseY),yPos+=diff}// show labels regardless of the domain if value is null
if(null===d.value&&!isRotated){var boxHeight=rect.height;yPos<boxHeight?yPos=boxHeight:yPos>this.height&&(yPos=this.height-4)}return isRotated||(yPos+=$$.getCenteredTextPos(d,points,textElement)),yPos+$$.getTextPos(d.id,"y")},
/**
   * Calculate if two or more text nodes are overlapping
   * Mark overlapping text nodes with "text-overlapping" class
   * @private
   * @param {number} id
   * @param {ChartInternal} $$
   * @param {string} selector
   */
markOverlapped:function(id,$$,selector){var textNodes=$$.arcs.selectAll(selector),filteredTextNodes=textNodes.filter((function(node){return node.data.id!==id})),textNode=textNodes.filter((function(node){return node.data.id===id})),translate=getTranslation(textNode.node()),calcHypo=function(x,y){return Math.sqrt(Math.pow(x,2)+Math.pow(y,2))};textNode.node()&&filteredTextNodes.each((function(){var coordinate=getTranslation(this),filteredTextNode=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this),nodeForWidth=calcHypo(translate.e,translate.f)>calcHypo(coordinate.e,coordinate.f)?textNode:filteredTextNode,overlapsX=Math.ceil(Math.abs(translate.e-coordinate.e))<Math.ceil(nodeForWidth.node().getComputedTextLength()),overlapsY=Math.ceil(Math.abs(translate.f-coordinate.f))<parseInt(textNode.style("font-size"),10);filteredTextNode.classed(config_classes.TextOverlapping,overlapsX&&overlapsY)}))},
/**
   * Calculate if two or more text nodes are overlapping
   * Remove "text-overlapping" class on selected text nodes
   * @private
   * @param {ChartInternal} $$
   * @param {string} selector
   */
undoMarkOverlapped:function($$,selector){$$.arcs.selectAll(selector).each((function(){Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["selectAll"])([this,this.previousSibling]).classed(config_classes.TextOverlapping,!1)}))}});
// CONCATENATED MODULE: ./src/internals/type.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
// defined chart types as category
var TYPES={Area:["area","area-spline","area-spline-range","area-line-range","area-step"],AreaRange:["area-spline-range","area-line-range"],Arc:["pie","donut","gauge","radar"],Line:["line","spline","area","area-spline","area-spline-range","area-line-range","step","area-step"],Step:["step","area-step"],Spline:["spline","area-spline","area-spline-range"]};extend(ChartInternal_ChartInternal.prototype,{setTargetType:function(targetIds,type){var $$=this,config=$$.config;$$.mapToTargetIds(targetIds).forEach((function(id){$$.withoutFadeIn[id]=type===config.data_types[id],config.data_types[id]=type})),targetIds||(config.data_type=type)},hasType:function(type,targetsValue){var $$=this,types=$$.config.data_types,targets=targetsValue||$$.data.targets,has=!1;return targets&&targets.length?targets.forEach((function(target){var t=types[target.id];(t&&t.indexOf(type)>=0||!t&&"line"===type)&&(has=!0)})):Object.keys(types).length?Object.keys(types).forEach((function(id){types[id]===type&&(has=!0)})):has=$$.config.data_type===type,has},
/**
   * Check if contains given chart types
   * @param {String} type Type key
   * @param {Object} targets
   * @param {Array} exclude Excluded types
   * @return {boolean}
   * @private
   */
hasTypeOf:function(type,targets){var _this=this,exclude=arguments.length>2&&void 0!==arguments[2]?arguments[2]:[];return!TYPES[type].filter((function(v){return-1===exclude.indexOf(v)})).every((function(v){return!_this.hasType(v,targets)}))},
/**
   * Check if given data is certain chart type
   * @param {Object} d Data object
   * @param {String} type chart type
   * @return {Boolean}
   * @private
   */
isTypeOf:function(d,type){var id=isString(d)?d:d.id,dataType=this.config.data_types[id];return isArray(type)?type.indexOf(dataType)>=0:dataType===type},
/**
   * Check if contains arc types chart
   * @param {Object} targets
   * @param {Array} exclude Excluded types
   * @return {boolean}
   * @private
   */
hasArcType:function(targets,exclude){return this.hasTypeOf("Arc",targets,exclude)},hasMultiArcGauge:function(){return this.hasType("gauge")&&"multi"===this.config.gauge_type},isLineType:function(d){var id=isString(d)?d:d.id;return!this.config.data_types[id]||this.isTypeOf(id,TYPES.Line)},isStepType:function(d){return this.isTypeOf(d,TYPES.Step)},isSplineType:function(d){return this.isTypeOf(d,TYPES.Spline)},isAreaType:function(d){return this.isTypeOf(d,TYPES.Area)},isAreaRangeType:function(d){return this.isTypeOf(d,TYPES.AreaRange)},isBarType:function(d){return this.isTypeOf(d,"bar")},isBubbleType:function(d){return this.isTypeOf(d,"bubble")},isScatterType:function(d){return this.isTypeOf(d,"scatter")},isPieType:function(d){return this.isTypeOf(d,"pie")},isGaugeType:function(d){return this.isTypeOf(d,"gauge")},isDonutType:function(d){return this.isTypeOf(d,"donut")},isRadarType:function(d){return this.isTypeOf(d,"radar")},isArcType:function(d){return this.isPieType(d)||this.isDonutType(d)||this.isGaugeType(d)||this.isRadarType(d)},
// determine if is 'circle' data point
isCirclePoint:function(){var config=this.config,pattern=config.point_pattern;return"circle"===config.point_type&&(!pattern||isArray(pattern)&&0===pattern.length)},lineData:function(d){return this.isLineType(d)?[d]:[]},arcData:function(d){return this.isArcType(d.data)?[d]:[]},barData:function(d){return this.isBarType(d)?d.values:[]},
/**
   * Get data adapt for data label showing
   * @param {Object} d Data object
   * @return {Array}
   * @private
   */
labelishData:function(d){return this.isBarType(d)||this.isLineType(d)||this.isScatterType(d)||this.isBubbleType(d)||this.isRadarType(d)?d.values:[]},barLineBubbleData:function(d){return this.isBarType(d)||this.isLineType(d)||this.isBubbleType(d)?d.values:[]},
// https://github.com/d3/d3-shape#curves
isInterpolationType:function(type){return["basis","basis-closed","basis-open","bundle","cardinal","cardinal-closed","cardinal-open","catmull-rom","catmull-rom-closed","catmull-rom-open","linear","linear-closed","monotone-x","monotone-y","natural"].indexOf(type)>=0}});
// CONCATENATED MODULE: ./src/internals/grid.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
// Grid position and text anchor helpers
var getGridTextAnchor=function(d){return isValue(d.position)||"end"},getGridTextDx=function(d){return"start"===d.position?4:"middle"===d.position?0:-4},getGridTextX=function(isX,width,height){return function(d){var x=isX?0:width;return"start"===d.position?x=isX?-height:0:"middle"===d.position&&(x=(isX?-height:width)/2),x}};extend(ChartInternal_ChartInternal.prototype,{initGrid:function(){var $$=this;$$.xgrid=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["selectAll"])([]),$$.initGridLines(),$$.initFocusGrid()},initGridLines:function(){var $$=this,config=$$.config;(config.grid_x_lines.length||config.grid_y_lines.length)&&($$.gridLines=$$.main.insert("g",".".concat(config_classes.chart).concat(config.grid_lines_front?" + *":"")).attr("clip-path",$$.clipPathForGrid).attr("class","".concat(config_classes.grid," ").concat(config_classes.gridLines)),$$.gridLines.append("g").attr("class",config_classes.xgridLines),$$.gridLines.append("g").attr("class",config_classes.ygridLines),$$.xgridLines=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["selectAll"])([]))},updateXGrid:function(withoutUpdate){var $$=this,config=$$.config,isRotated=config.axis_rotated,xgridData=$$.generateGridData(config.grid_x_type,$$.x),tickOffset=$$.isCategorized()?$$.xAxis.tickOffset():0,pos=function(d){return($$.zoomScale||$$.x)(d)+tickOffset*(isRotated?-1:1)};$$.xgridAttr=isRotated?{x1:0,x2:$$.width,y1:pos,y2:pos}:{x1:pos,x2:pos,y1:0,y2:$$.height},$$.xgrid=$$.main.select(".".concat(config_classes.xgrids)).selectAll(".".concat(config_classes.xgrid)).data(xgridData),$$.xgrid.exit().remove(),$$.xgrid=$$.xgrid.enter().append("line").attr("class",config_classes.xgrid).merge($$.xgrid),withoutUpdate||$$.xgrid.each((function(){var grid=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this);Object.keys($$.xgridAttr).forEach((function(id){grid.attr(id,$$.xgridAttr[id]).style("opacity",(function(){return grid.attr(isRotated?"y1":"x1")===(isRotated?$$.height:0)?"0":"1"}))}))}))},updateYGrid:function(){var $$=this,config=$$.config,isRotated=config.axis_rotated,gridValues=$$.yAxis.tickValues()||$$.y.ticks(config.grid_y_ticks),pos=function(d){return Math.ceil($$.y(d))};$$.ygrid=$$.main.select(".".concat(config_classes.ygrids)).selectAll(".".concat(config_classes.ygrid)).data(gridValues),$$.ygrid.exit().remove(),$$.ygrid=$$.ygrid.enter().append("line").attr("class",config_classes.ygrid).merge($$.ygrid),$$.ygrid.attr("x1",isRotated?pos:0).attr("x2",isRotated?pos:$$.width).attr("y1",isRotated?0:pos).attr("y2",isRotated?$$.height:pos),$$.smoothLines($$.ygrid,"grid")},updateGrid:function(duration){var $$=this;
// hide if arc type
$$.gridLines||$$.initGridLines(),$$.grid.style("visibility",$$.hasArcType()?"hidden":"visible"),$$.hideGridFocus(),$$.updateXGridLines(duration),$$.updateYGridLines(duration)},
/**
   * Update X Grid lines
   * @param {Number} duration
   * @private
   */
updateXGridLines:function(duration){var $$=this,main=$$.main,config=$$.config,isRotated=config.axis_rotated;config.grid_x_show&&$$.updateXGrid(),$$.xgridLines=main.select(".".concat(config_classes.xgridLines)).selectAll(".".concat(config_classes.xgridLine)).data(config.grid_x_lines),$$.xgridLines.exit().transition().duration(duration).style("opacity","0").remove();
// enter
var xgridLine=$$.xgridLines.enter().append("g");xgridLine.append("line").style("opacity","0"),xgridLine.append("text").attr("transform",isRotated?"":"rotate(-90)").attr("dy",-5).style("opacity","0"),$$.xgridLines=xgridLine.merge($$.xgridLines),$$.xgridLines.attr("class",(function(d){return"".concat(config_classes.xgridLine," ").concat(d["class"]||"").trim()})).select("text").attr("text-anchor",getGridTextAnchor).attr("dx",getGridTextDx).transition().duration(duration).text((function(d){return d.text})).transition().style("opacity","1")},
/**
   * Update Y Grid lines
   * @param {Number} duration
   * @private
   */
updateYGridLines:function(duration){var $$=this,main=$$.main,config=$$.config,isRotated=config.axis_rotated;config.grid_y_show&&$$.updateYGrid(),$$.ygridLines=main.select(".".concat(config_classes.ygridLines)).selectAll(".".concat(config_classes.ygridLine)).data(config.grid_y_lines),$$.ygridLines.exit().transition().duration(duration).style("opacity","0").remove();
// enter
var ygridLine=$$.ygridLines.enter().append("g");ygridLine.append("line").style("opacity","0"),ygridLine.append("text").attr("transform",isRotated?"rotate(-90)":"").style("opacity","0"),$$.ygridLines=ygridLine.merge($$.ygridLines);
// update
var yv=$$.yv.bind($$);$$.ygridLines.attr("class",(function(d){return"".concat(config_classes.ygridLine," ").concat(d["class"]||"").trim()})).select("line").transition().duration(duration).attr("x1",isRotated?yv:0).attr("x2",isRotated?yv:$$.width).attr("y1",isRotated?0:yv).attr("y2",isRotated?$$.height:yv).transition().style("opacity","1"),$$.ygridLines.select("text").attr("text-anchor",getGridTextAnchor).attr("dx",getGridTextDx).transition().duration(duration).attr("dy",-5).attr("x",getGridTextX(isRotated,$$.width,$$.height)).attr("y",yv).text((function(d){return d.text})).transition().style("opacity","1")},redrawGrid:function(withTransition){var $$=this,isRotated=$$.config.axis_rotated,xv=$$.xv.bind($$),lines=$$.xgridLines.select("line"),texts=$$.xgridLines.select("text");return lines=(withTransition?lines.transition():lines).attr("x1",isRotated?0:xv).attr("x2",isRotated?$$.width:xv).attr("y1",isRotated?xv:0).attr("y2",isRotated?xv:$$.height),texts=(withTransition?texts.transition():texts).attr("x",getGridTextX(!isRotated,$$.width,$$.height)).attr("y",xv).text((function(d){return d.text})),[(withTransition?lines.transition():lines).style("opacity","1"),(withTransition?texts.transition():texts).style("opacity","1")]},initFocusGrid:function(){var $$=this,config=$$.config,isFront=config.grid_front,className=".".concat(config_classes[isFront&&$$.gridLines?"gridLines":"chart"]).concat(isFront?" + *":"");$$.grid=$$.main.insert("g",className).attr("clip-path",$$.clipPathForGrid).attr("class",config_classes.grid),config.grid_x_show&&$$.grid.append("g").attr("class",config_classes.xgrids),config.grid_y_show&&$$.grid.append("g").attr("class",config_classes.ygrids),config.grid_focus_show&&($$.grid.append("g").attr("class",config_classes.xgridFocus).append("line").attr("class",config_classes.xgridFocus),config.grid_focus_y&&!config.tooltip_grouped&&$$.grid.append("g").attr("class",config_classes.ygridFocus).append("line").attr("class",config_classes.ygridFocus))},
/**
   * Show grid focus line
   * @param {Array} selectedData
   * @private
   */
showGridFocus:function(selectedData){var $$=this,config=$$.config,isRotated=config.axis_rotated,dataToShow=selectedData.filter((function(d){return d&&isValue($$.getBaseValue(d))}));
// Hide when bubble/scatter/stanford plot exists
if(config.tooltip_show&&0!==dataToShow.length&&!$$.hasType("bubble")&&!$$.hasArcType()){var focusEl=$$.main.selectAll("line.".concat(config_classes.xgridFocus,", line.").concat(config_classes.ygridFocus)),isEdge=config.grid_focus_edge&&!config.tooltip_grouped,xx=$$.xx.bind($$);focusEl.style("visibility","visible").data(dataToShow.concat(dataToShow)).each((function(d){var xy,el=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this),pos={x:xx(d),y:$$.getYScale(d.id)(d.value)};if(el.classed(config_classes.xgridFocus))xy=isRotated?[null,// x1
pos.x,// y1
isEdge?pos.y:$$.width,// x2
pos.x]:[pos.x,isEdge?pos.y:null,pos.x,$$.height];else{var isY2="y2"===$$.axis.getId(d.id);xy=isRotated?[pos.y,// x1
isEdge&&!isY2?pos.x:null,// y1
pos.y,// x2
isEdge&&isY2?pos.x:$$.height]:[isEdge&&isY2?pos.x:null,pos.y,isEdge&&!isY2?pos.x:$$.width,pos.y]}["x1","y1","x2","y2"].forEach((function(v,i){return el.attr(v,xy[i])}))})),$$.smoothLines(focusEl,"grid")}},hideGridFocus:function(){var $$=this;"mouse"===$$.inputType&&$$.main.selectAll("line.".concat(config_classes.xgridFocus,", line.").concat(config_classes.ygridFocus)).style("visibility","hidden")},updateGridFocus:function(){var $$=this,xgridFocus=$$.grid.select("line.".concat(config_classes.xgridFocus));if("touch"!==$$.inputType){var _isRotated=$$.config.axis_rotated;xgridFocus.attr("x1",_isRotated?0:-10).attr("x2",_isRotated?$$.width:-10).attr("y1",_isRotated?-10:0).attr("y2",_isRotated?-10:$$.height)}else if(!xgridFocus.empty()){var d=xgridFocus.datum();d&&$$.showGridFocus([d])}// need to return 'true' as of being pushed to the redraw list
// ref: getRedrawList()
return!0},generateGridData:function(type,scale){var $$=this,tickNum=$$.main.select(".".concat(config_classes.axisX)).selectAll(".tick").size(),gridData=[];if("year"===type)for(var xDomain=$$.getXDomain(),firstYear=xDomain[0].getFullYear(),lastYear=xDomain[1].getFullYear(),i=firstYear;i<=lastYear;i++)gridData.push(new Date("".concat(i,"-01-01 00:00:00")));else gridData=scale.ticks(10),gridData.length>tickNum&&(gridData=gridData.filter((function(d){return(d+"").indexOf(".")<0})));return gridData},getGridFilterToRemove:function(params){return params?function(line){var found=!1;return(isArray(params)?params.concat():[params]).forEach((function(param){("value"in param&&line.value===param.value||"class"in param&&line["class"]===param["class"])&&(found=!0)})),found}:function(){return!0}},removeGridLines:function(params,forX){var $$=this,config=$$.config,toRemove=$$.getGridFilterToRemove(params),classLines=forX?config_classes.xgridLines:config_classes.ygridLines,classLine=forX?config_classes.xgridLine:config_classes.ygridLine;$$.main.select(".".concat(classLines)).selectAll(".".concat(classLine)).filter(toRemove).transition().duration(config.transition_duration).style("opacity","0").remove();var gridLines="grid_".concat(forX?"x":"y","_lines");config[gridLines]=config[gridLines].filter((function(line){return!toRemove(line)}))}}),
// CONCATENATED MODULE: ./src/internals/tooltip.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(ChartInternal_ChartInternal.prototype,{
/**
   * Initializes the tooltip
   * @private
   */
initTooltip:function(){var $$=this,config=$$.config,bindto=config.tooltip_contents.bindto;
// Show tooltip if needed
if($$.tooltip=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(bindto),$$.tooltip.empty()&&($$.tooltip=$$.selectChart.style("position","relative").append("div").attr("class",config_classes.tooltipContainer).style("position","absolute").style("pointer-events","none").style("display","none")),config.tooltip_init_show){if($$.isTimeSeries()&&isString(config.tooltip_init_x)){var i,val,targets=$$.data.targets[0];for(config.tooltip_init_x=$$.parseDate(config.tooltip_init_x),i=0;(val=targets.values[i])&&val.x-config.tooltip_init_x!==0;i++);config.tooltip_init_x=i}$$.tooltip.html($$.getTooltipHTML($$.data.targets.map((function(d){return $$.addName(d.values[config.tooltip_init_x])})),$$.axis.getXAxisTickFormat(),$$.getYFormat($$.hasArcType(null,["radar"])),$$.color)),bindto||$$.tooltip.style("top",config.tooltip_init_position.top).style("left",config.tooltip_init_position.left).style("display","block")}$$.bindTooltipResizePos()},
/**
   * Get the tooltip HTML string
   * @param  {...any} args
   * @private
   * @return {String} Formatted HTML string
   */
getTooltipHTML:function(){for(var _config$tooltip_conte,_$$,$$=this,config=$$.config,_len=arguments.length,args=Array(_len),_key=0;_key<_len;_key++)args[_key]=arguments[_key];return isFunction(config.tooltip_contents)?(_config$tooltip_conte=config.tooltip_contents).call.apply(_config$tooltip_conte,[$$].concat(args)):(_$$=$$).getTooltipContent.apply(_$$,args)},
/**
   * Returns the tooltip content(HTML string)
   * @param {Object} d data
   * @param {Function} defaultTitleFormat Default title format
   * @param {Function} defaultValueFormat Default format for each data value in the tooltip.
   * @param {Function} color Color function
   * @returns {String} html
   * @private
   */
getTooltipContent:function(d,defaultTitleFormat,defaultValueFormat,color){var $$=this,config=$$.config,titleFormat=config.tooltip_format_title||defaultTitleFormat,nameFormat=config.tooltip_format_name||function(name){return name},valueFormat=config.tooltip_format_value||($$.isStackNormalized()?function(v,ratio){return"".concat((100*ratio).toFixed(2),"%")}:defaultValueFormat),order=config.tooltip_order,getRowValue=function(row){return $$.isBubbleZType(row)?$$.getBubbleZData(row.value,"z"):$$.getBaseValue(row)},getBgColor=$$.levelColor?function(row){return $$.levelColor(row.value)}:function(row){return color(row)},contents=config.tooltip_contents,tplStr=contents.template,targetIds=$$.mapToTargetIds();if(null===order&&$$.isGrouped()){
// for stacked data, order should aligned with the visually displayed data
var ids=$$.orderTargets($$.data.targets).map((function(i2){return i2.id})).reverse();d.sort((function(a,b){var v1=a?a.value:null,v2=b?b.value:null;return v1>0&&v2>0&&(v1=a.id?ids.indexOf(a.id):null,v2=b.id?ids.indexOf(b.id):null),v1-v2}))}else/^(asc|desc)$/.test(order)?d.sort((function(a,b){var v1=a?getRowValue(a):null,v2=b?getRowValue(b):null;return"asc"===order?v1-v2:v2-v1})):isFunction(order)&&d.sort(order);var text,row,param,value,i,tpl=$$.getTooltipContentTemplate(tplStr),len=d.length;for(i=0;i<len;i++)if(row=d[i],row&&(getRowValue(row)||0===getRowValue(row))){if(isUndefined(text)){var title=sanitise(titleFormat?titleFormat(row.x):row.x);text=tplProcess(tpl[0],{CLASS_TOOLTIP:config_classes.tooltip,TITLE:isValue(title)?tplStr?title:'<tr><th colspan="2">'.concat(title,"</th></tr>"):""})}if(param=[row.ratio,row.id,row.index,d],value=sanitise(valueFormat.apply(void 0,[getRowValue(row)].concat(_toConsumableArray(param)))),$$.isAreaRangeType(row)){var _map=["high","low"].map((function(v){return sanitise(valueFormat.apply(void 0,[$$.getAreaRangeData(row,v)].concat(_toConsumableArray(param))))})),_map2=_slicedToArray(_map,2),high=_map2[0],low=_map2[1];value="<b>Mid:</b> ".concat(value," <b>High:</b> ").concat(high," <b>Low:</b> ").concat(low)}if(void 0!==value){var _ret=function(){
// Skip elements when their name is set to null
if(null===row.name)return"continue";var name=sanitise(nameFormat.apply(void 0,[row.name].concat(_toConsumableArray(param)))),color=getBgColor(row),contentValue={CLASS_TOOLTIP_NAME:config_classes.tooltipName+$$.getTargetSelectorSuffix(row.id),COLOR:tplStr||!$$.patterns?color:'<svg><rect style="fill:'.concat(color,'" width="10" height="10"></rect></svg>'),NAME:name,VALUE:value};if(tplStr&&isObject(contents.text)){var index=targetIds.indexOf(row.id);Object.keys(contents.text).forEach((function(key){contentValue[key]=contents.text[key][index]}))}text+=tplProcess(tpl[1],contentValue)}();if("continue"===_ret)continue}}return"".concat(text,"</table>")},
/**
   * Get the content template string
   * @param {String} tplStr
   * @return {String} Template string
   * @private
   */
getTooltipContentTemplate:function(tplStr){return(tplStr||'<table class="{=CLASS_TOOLTIP}"><tbody>\n\t\t\t\t{=TITLE}\n\t\t\t\t{{<tr class="{=CLASS_TOOLTIP_NAME}">\n\t\t\t\t\t<td class="name">'.concat(this.patterns?"{=COLOR}":'<span style="background-color:{=COLOR}"></span>','{=NAME}</td>\n\t\t\t\t\t<td class="value">{=VALUE}</td>\n\t\t\t\t</tr>}}\n\t\t\t</tbody></table>')).replace(/(\r?\n|\t)/g,"").split(/{{(.*)}}/)},
/**
   * Returns the position of the tooltip
   * @param {Object} dataToShow data
   * @param {String} tWidth Width value of tooltip element
   * @param {String} tHeight Height value of tooltip element
   * @param {HTMLElement} element
   * @returns {Object} top, left value
   * @private
   */
tooltipPosition:function(dataToShow,tWidth,tHeight,element){var $$=this,config=$$.config,hasGauge=$$.hasType("gauge")&&!config.gauge_fullCircle,svgLeft=$$.getSvgLeft(!0),_d3Mouse=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["mouse"])(element),_d3Mouse2=_slicedToArray(_d3Mouse,2),left=_d3Mouse2[0],top=_d3Mouse2[1],chartRight=svgLeft+$$.currentWidth-$$.getCurrentPaddingRight(!0),chartLeft=$$.getCurrentPaddingLeft(!0),size=20;
// Determine tooltip position
if(top+=size,$$.hasArcType()){var raw="touch"===$$.inputType||$$.hasType("radar");raw||(top+=hasGauge?$$.height:$$.height/2,left+=($$.width-($$.isLegendRight?$$.getLegendWidth():0))/2)}else{var dataScale=$$.x(dataToShow[0].x);config.axis_rotated?(top=dataScale+size,left+=svgLeft+100,chartRight-=svgLeft):(top-=5,left=svgLeft+chartLeft+size+($$.zoomScale?left:dataScale))}// when tooltip left + tWidth > chart's width
left+tWidth+15>chartRight&&(left-=tWidth+chartLeft),top+tHeight>$$.currentHeight&&(top-=hasGauge?3*tHeight:tHeight+30);var pos={top:top,left:left};// make sure to not be positioned out of viewport
return Object.keys(pos).forEach((function(v){pos[v]<0&&(pos[v]=0)})),pos},
/**
   * Show the tooltip
   * @private
   * @param {Object} selectedData
   * @param {HTMLElement} element
   */
showTooltip:function(selectedData,element){var $$=this,config=$$.config,bindto=config.tooltip_contents.bindto,forArc=$$.hasArcType(null,["radar"]),dataToShow=selectedData.filter((function(d){return d&&isValue($$.getBaseValue(d))}));if(0!==dataToShow.length&&config.tooltip_show){var datum=$$.tooltip.datum(),_ref=datum||{},_ref$width=_ref.width,width=void 0===_ref$width?0:_ref$width,_ref$height=_ref.height,height=void 0===_ref$height?0:_ref$height,dataStr=JSON.stringify(selectedData);if(!datum||datum.current!==dataStr){var index=selectedData.concat().sort()[0].index;callFn(config.tooltip_onshow,$$,$$.api,selectedData),$$.tooltip.html($$.getTooltipHTML(selectedData,$$.axis.getXAxisTickFormat(),$$.getYFormat(forArc),$$.color)).style("display",null).style("visibility",null).datum(datum={index:index,current:dataStr,width:width=$$.tooltip.property("offsetWidth"),height:height=$$.tooltip.property("offsetHeight")}),callFn(config.tooltip_onshown,$$,$$.api,selectedData),$$._handleLinkedCharts(!0,index)}if(!bindto){var fnPos=config.tooltip_position||$$.tooltipPosition,pos=fnPos.call(this,dataToShow,width,height,element);// Get tooltip dimensions
["top","left"].forEach((function(v){var value=pos[v];$$.tooltip.style(v,"".concat(value,"px")),"left"!==v||datum.xPosInPercent||(datum.xPosInPercent=value/$$.currentWidth*100)}))}}},
/**
   * Adjust tooltip position on resize event
   * @private
   */
bindTooltipResizePos:function(){var $$=this,resizeFunction=$$.resizeFunction,tooltip=$$.tooltip;resizeFunction.add((function(){if("block"===tooltip.style("display")){var currentWidth=$$.currentWidth,_tooltip$datum=tooltip.datum(),width=_tooltip$datum.width,xPosInPercent=_tooltip$datum.xPosInPercent,_value=currentWidth/100*xPosInPercent,diff=currentWidth-(_value+width);diff<0&&(_value+=diff),tooltip.style("left","".concat(_value,"px"))}}))},
/**
   * Hide the tooltip
   * @param {Boolean} force Force to hide
   * @private
   */
hideTooltip:function(force){var $$=this,api=$$.api,config=$$.config,tooltip=$$.tooltip;if("none"!==tooltip.style("display")&&(!config.tooltip_doNotHide||force)){var selectedData=JSON.parse(this.tooltip.datum().current);
// hide tooltip
callFn(config.tooltip_onhide,$$,api,selectedData),tooltip.style("display","none").style("visibility","hidden").datum(null),callFn(config.tooltip_onhidden,$$,api,selectedData)}},
/**
   * Toggle display for linked chart instances
   * @param {Boolean} show true: show, false: hide
   * @param {Number} index x Axis index
   * @private
   */
_handleLinkedCharts:function(show,index){var $$=this;if($$.config.tooltip_linked){var linkedName=$$.config.tooltip_linked_name;($$.api.internal.charts||[]).forEach((function(c){if(c!==$$.api){var _config=c.internal.config,isLinked=_config.tooltip_linked,name=_config.tooltip_linked_name,isInDom=browser_doc.body.contains(c.element);if(isLinked&&linkedName===name&&isInDom){var data=c.internal.tooltip.data()[0],isNotSameIndex=index!==(data&&data.index);
// prevent throwing error for non-paired linked indexes
try{show&&isNotSameIndex?c.tooltip.show({index:index}):!show&&c.tooltip.hide()}catch(e){}}}}))}}}),
// CONCATENATED MODULE: ./src/internals/legend.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(ChartInternal_ChartInternal.prototype,{
/**
   * Initialize the legend.
   * @private
   */
initLegend:function(){var $$=this,config=$$.config;$$.legendItemTextBox={},$$.legendHasRendered=!1,$$.legend=$$.svg.append("g"),config.legend_show?($$.legend.attr("transform",$$.getTranslate("legend")),$$.updateLegend()):($$.legend.style("visibility","hidden"),$$.hiddenLegendIds=$$.mapToIds($$.data.targets))},
/**
   * Update legend element
   * @param {Array} targetIds ID's of target
   * @param {Object} options withTransform : Whether to use the transform property / withTransitionForTransform: Whether transition is used when using the transform property / withTransition : whether or not to transition.
   * @param {Object} transitions Return value of the generateTransitions
   * @private
   */
updateLegend:function(targetIds,options,transitions){var $$=this,config=$$.config,optionz=options||{withTransform:!1,withTransitionForTransform:!1,withTransition:!1};
// toggle legend state
// Update size and scale
// Update g positions
optionz.withTransition=getOption(optionz,"withTransition",!0),optionz.withTransitionForTransform=getOption(optionz,"withTransitionForTransform",!0),config.legend_contents_bindto&&config.legend_contents_template?$$.updateLegendTemplate():$$.updateLegendElement(targetIds||$$.mapToIds($$.data.targets),optionz,transitions),$$.legend.selectAll(".".concat(config_classes.legendItem)).classed(config_classes.legendItemHidden,(function(id){return!$$.isTargetToShow(id)})),$$.updateScales(!1,!$$.zoomScale),$$.updateSvgSize(),$$.transformAll(optionz.withTransitionForTransform,transitions),$$.legendHasRendered=!0},
/**
   * Update legend using template option
   * @private
   */
updateLegendTemplate:function(){var $$=this,config=$$.config,wrapper=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(config.legend_contents_bindto),template=config.legend_contents_template;if(!wrapper.empty()){var targets=$$.mapToIds($$.data.targets),ids=[],html="";targets.forEach((function(v){var content=isFunction(template)?template.call($$,v,$$.color(v),$$.api.data(v)[0].values):tplProcess(template,{COLOR:$$.color(v),TITLE:v});content&&(ids.push(v),html+=content)}));var legendItem=wrapper.html(html).selectAll((function(){return this.childNodes})).data(ids);$$.setLegendItem(legendItem),$$.legend=wrapper}},
/**
   * Update the size of the legend.
   * @private
   * @param {Obejct} size S
   */
updateSizeForLegend:function(size){var $$=this,config=$$.config,width=size.width,height=size.height,insetLegendPosition={top:$$.isLegendTop?$$.getCurrentPaddingTop()+config.legend_inset_y+5.5:$$.currentHeight-height-$$.getCurrentPaddingBottom()-config.legend_inset_y,left:$$.isLegendLeft?$$.getCurrentPaddingLeft()+config.legend_inset_x+.5:$$.currentWidth-width-$$.getCurrentPaddingRight()-config.legend_inset_x+.5};$$.margin3={top:$$.isLegendRight?0:$$.isLegendInset?insetLegendPosition.top:$$.currentHeight-height,right:NaN,bottom:0,left:$$.isLegendRight?$$.currentWidth-width:$$.isLegendInset?insetLegendPosition.left:0}},
/**
   * Transform Legend
   * @private
   * @param {Boolean} whether or not to transition.
   */
transformLegend:function(withTransition){var $$=this;(withTransition?$$.legend.transition():$$.legend).attr("transform",$$.getTranslate("legend"))},
/**
   * Update the legend step
   * @private
   * @param {Number} step
   */
updateLegendStep:function(step){this.legendStep=step},
/**
   * Update legend item width
   * @private
   * @param {Number} width
   */
updateLegendItemWidth:function(w){this.legendItemWidth=w},
/**
   * Update legend item height
   * @private
   * @param {Number} height
   */
updateLegendItemHeight:function(h){this.legendItemHeight=h},
/**
   * Update legend item color
   * @private
   * @param {String} id Corresponding data ID value
   * @param {String} color Color value
   */
updateLegendItemColor:function(id,color){this.legend.select(".".concat(config_classes.legendItem,"-").concat(id," line")).style("stroke",color)},
/**
   * Get the width of the legend
   * @private
   * @return {Number} width
   */
getLegendWidth:function(){var $$=this;return $$.config.legend_show?$$.isLegendRight||$$.isLegendInset?$$.legendItemWidth*($$.legendStep+1):$$.currentWidth:0},
/**
   * Get the height of the legend
   * @return {Number} height
   * @private
   */
getLegendHeight:function(){var $$=this;return $$.config.legend_show?$$.isLegendRight?$$.currentHeight:Math.max(20,$$.legendItemHeight)*($$.legendStep+1):0},
/**
   * Get the opacity of the legend
   * @private
   * @param {Object} d3.Select
   * @returns {Number} opacity
   */
opacityForLegend:function(legendItem){return legendItem.classed(config_classes.legendItemHidden)?null:"1"},
/**
   * Get the opacity of the legend that is unfocused
   * @private
   * @param {Object} legendItem, d3.Select
   * @returns {Number} opacity
   */
opacityForUnfocusedLegend:function(legendItem){return legendItem.classed(config_classes.legendItemHidden)?null:"0.3"},
/**
   * Toggles the focus of the legend
   * @private
   * @param {Array} ID's of target
   * @param {Boolean} whether or not to focus.
   */
toggleFocusLegend:function(targetIds,focus){var $$=this,targetIdz=$$.mapToTargetIds(targetIds);$$.legend.selectAll(".".concat(config_classes.legendItem)).filter((function(id){return targetIdz.indexOf(id)>=0})).classed(config_classes.legendItemFocused,focus).transition().duration(100).style("opacity",(function(){return(focus?$$.opacityForLegend:$$.opacityForUnfocusedLegend).call($$,Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this))}))},
/**
   * Revert the legend to its default state
   * @private
   */
revertLegend:function(){var $$=this;$$.legend.selectAll(".".concat(config_classes.legendItem)).classed(config_classes.legendItemFocused,!1).transition().duration(100).style("opacity",(function(){return $$.opacityForLegend(Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this))}))},
/**
   * Shows the legend
   * @private
   * @param {Array} ID's of target
   */
showLegend:function(targetIds){var $$=this,config=$$.config;config.legend_show||(config.legend_show=!0,$$.legend.style("visibility","visible"),!$$.legendHasRendered&&$$.updateLegend()),$$.removeHiddenLegendIds(targetIds),$$.legend.selectAll($$.selectorLegends(targetIds)).style("visibility","visible").transition().style("opacity",(function(){return $$.opacityForLegend(Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this))}))},
/**
   * Hide the legend
   * @private
   * @param {Array} ID's of target
   */
hideLegend:function(targetIds){var $$=this,config=$$.config;config.legend_show&&isEmpty(targetIds)&&(config.legend_show=!1,$$.legend.style("visibility","hidden")),$$.addHiddenLegendIds(targetIds),$$.legend.selectAll($$.selectorLegends(targetIds)).style("opacity","0").style("visibility","hidden")},
/**
   * Clear the LegendItemTextBox cache.
   * @private
   */
clearLegendItemTextBoxCache:function(){this.legendItemTextBox={}},
/**
   * Set legend item style & bind events
   * @private
   * @param {d3.selection} item
   */
setLegendItem:function(item){var $$=this,config=$$.config,isTouch="touch"===$$.inputType,hasGauge=$$.hasType("gauge");item.attr("class",(function(id){var node=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this),itemClass=!node.empty()&&node.attr("class")||"";return itemClass+$$.generateClass(config_classes.legendItem,id)})).style("visibility",(function(id){return $$.isLegendToShow(id)?"visible":"hidden"})).style("cursor","pointer").on("click",(function(id){callFn(config.legend_item_onclick,$$,id)||(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["event"].altKey?($$.api.hide(),$$.api.show(id)):($$.api.toggle(id),!isTouch&&$$.isTargetToShow(id)?$$.api.focus(id):$$.api.revert())),isTouch&&$$.hideTooltip()})),isTouch||item.on("mouseout",(function(id){callFn(config.legend_item_onout,$$,id)||(Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this).classed(config_classes.legendItemFocused,!1),hasGauge&&$$.undoMarkOverlapped($$,".".concat(config_classes.gaugeValue)),$$.api.revert())})).on("mouseover",(function(id){callFn(config.legend_item_onover,$$,id)||(Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this).classed(config_classes.legendItemFocused,!0),hasGauge&&$$.markOverlapped(id,$$,".".concat(config_classes.gaugeValue)),!$$.transiting&&$$.isTargetToShow(id)&&$$.api.focus(id))}))},
/**
   * Update the legend
   * @param {Array} targetIds ID's of target
   * @param {Object} options withTransform : Whether to use the transform property / withTransitionForTransform: Whether transition is used when using the transform property / withTransition : whether or not to transition.
  	 * @private
   */
updateLegendElement:function(targetIds,options){var xForLegend,yForLegend,background,$$=this,config=$$.config,posMin=10,tileWidth=config.legend_item_tile_width+5,maxWidth=0,maxHeight=0,totalLength=0,offsets={},widths={},heights={},margins=[0],steps={},step=0,isLegendRightOrInset=$$.isLegendRight||$$.isLegendInset,targetIdz=targetIds.filter((function(id){return!isDefined(config.data_names[id])||null!==config.data_names[id]})),withTransition=options.withTransition,getTextBox=function(textElement,id){return $$.legendItemTextBox[id]||($$.legendItemTextBox[id]=$$.getTextRect(textElement,config_classes.legendItem)),$$.legendItemTextBox[id]},updatePositions=function(textElement,id,index){var margin,isLast=index===targetIdz.length-1,box=getTextBox(textElement,id),itemWidth=box.width+tileWidth+(isLast&&!isLegendRightOrInset?0:10)+config.legend_padding,itemHeight=box.height+4,itemLength=isLegendRightOrInset?itemHeight:itemWidth,areaLength=isLegendRightOrInset?$$.getLegendHeight():$$.getLegendWidth(),updateValues=function(id2,withoutStep){withoutStep||(margin=(areaLength-totalLength-itemLength)/2,margin<posMin&&(margin=(areaLength-itemLength)/2,totalLength=0,step++)),steps[id2]=step,margins[step]=$$.isLegendInset?10:margin,offsets[id2]=totalLength,totalLength+=itemLength};if(0===index&&(totalLength=0,step=0,maxWidth=0,maxHeight=0),config.legend_show&&!$$.isLegendToShow(id))return widths[id]=0,heights[id]=0,steps[id]=0,void(offsets[id]=0);widths[id]=itemWidth,heights[id]=itemHeight,(!maxWidth||itemWidth>=maxWidth)&&(maxWidth=itemWidth),(!maxHeight||itemHeight>=maxHeight)&&(maxHeight=itemHeight);var maxLength=isLegendRightOrInset?maxHeight:maxWidth;config.legend_equally?(Object.keys(widths).forEach((function(id2){return widths[id2]=maxWidth})),Object.keys(heights).forEach((function(id2){return heights[id2]=maxHeight})),margin=(areaLength-maxLength*targetIdz.length)/2,margin<posMin?(totalLength=0,step=0,targetIdz.forEach((function(id2){return updateValues(id2)}))):updateValues(id,!0)):updateValues(id)};$$.isLegendInset&&(step=config.legend_inset_step?config.legend_inset_step:targetIdz.length,$$.updateLegendStep(step)),$$.isLegendRight?(xForLegend=function(id){return maxWidth*steps[id]},yForLegend=function(id){return margins[steps[id]]+offsets[id]}):$$.isLegendInset?(xForLegend=function(id){return maxWidth*steps[id]+10},yForLegend=function(id){return margins[steps[id]]+offsets[id]}):(xForLegend=function(id){return margins[steps[id]]+offsets[id]},yForLegend=function(id){return maxHeight*steps[id]});var xForLegendText=function(id,i){return xForLegend(id,i)+4+config.legend_item_tile_width},xForLegendRect=function(id,i){return xForLegend(id,i)},x1ForLegendTile=function(id,i){return xForLegend(id,i)-2},x2ForLegendTile=function(id,i){return xForLegend(id,i)-2+config.legend_item_tile_width},yForLegendText=function(id,i){return yForLegend(id,i)+9},yForLegendRect=function(id,i){return yForLegend(id,i)-5},yForLegendTile=function(id,i){return yForLegend(id,i)+4},pos=-200,l=$$.legend.selectAll(".".concat(config_classes.legendItem)).data(targetIdz).enter().append("g");$$.setLegendItem(l),l.append("text").text((function(id){return isDefined(config.data_names[id])?config.data_names[id]:id})).each((function(id,i){updatePositions(this,id,i)})).style("pointer-events","none").attr("x",isLegendRightOrInset?xForLegendText:pos).attr("y",isLegendRightOrInset?pos:yForLegendText),l.append("rect").attr("class",config_classes.legendItemEvent).style("fill-opacity","0").attr("x",isLegendRightOrInset?xForLegendRect:pos).attr("y",isLegendRightOrInset?pos:yForLegendRect);var usePoint=$$.config.legend_usePoint;if(usePoint){var ids=[];l.append((function(d){var pattern=notEmpty(config.point_pattern)?config.point_pattern:[config.point_type];-1===ids.indexOf(d)&&ids.push(d);var point=pattern[ids.indexOf(d)%pattern.length];return"rectangle"===point&&(point="rect"),browser_doc.createElementNS(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["namespaces"].svg,$$.hasValidPointType(point)?point:"use")})).attr("class",config_classes.legendItemPoint).style("fill",(function(d){return $$.color(d)})).style("pointer-events","none").attr("href",(function(data,idx,selection){var node=selection[idx],nodeName=node.nodeName.toLowerCase();return"use"===nodeName?"#".concat($$.datetimeId,"-point-").concat(data):void 0}))}else l.append("line").attr("class",config_classes.legendItemTile).style("stroke",$$.color).style("pointer-events","none").attr("x1",isLegendRightOrInset?x1ForLegendTile:pos).attr("y1",isLegendRightOrInset?pos:yForLegendTile).attr("x2",isLegendRightOrInset?x2ForLegendTile:pos).attr("y2",isLegendRightOrInset?pos:yForLegendTile).attr("stroke-width",config.legend_item_tile_height);// Set background for inset legend
background=$$.legend.select(".".concat(config_classes.legendBackground," rect")),$$.isLegendInset&&maxWidth>0&&0===background.size()&&(background=$$.legend.insert("g",".".concat(config_classes.legendItem)).attr("class",config_classes.legendBackground).append("rect"));var texts=$$.legend.selectAll("text").data(targetIdz).text((function(id){return isDefined(config.data_names[id])?config.data_names[id]:id})).each((function(id,i){updatePositions(this,id,i)}));(withTransition?texts.transition():texts).attr("x",xForLegendText).attr("y",yForLegendText);var rects=$$.legend.selectAll("rect.".concat(config_classes.legendItemEvent)).data(targetIdz);if((withTransition?rects.transition():rects).attr("width",(function(id){return widths[id]})).attr("height",(function(id){return heights[id]})).attr("x",xForLegendRect).attr("y",yForLegendRect),usePoint){var tiles=$$.legend.selectAll(".".concat(config_classes.legendItemPoint)).data(targetIdz);(withTransition?tiles.transition():tiles).each((function(){var radius,width,height,nodeName=this.nodeName.toLowerCase(),pointR=$$.config.point_r,x="x",y="y",xOffset=2,yOffset=2.5;if("circle"===nodeName){var size=.2*pointR;x="cx",y="cy",radius=pointR+size,xOffset=2*pointR,yOffset=-size}else if("rect"===nodeName){var _size=2.5*pointR;width=_size,height=_size,yOffset=3}Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this).attr(x,(function(d){return x1ForLegendTile(d)+xOffset})).attr(y,(function(d){return yForLegendTile(d)-yOffset})).attr("r",radius).attr("width",width).attr("height",height)}))}else{var _tiles=$$.legend.selectAll("line.".concat(config_classes.legendItemTile)).data(targetIdz);(withTransition?_tiles.transition():_tiles).style("stroke",$$.levelColor?function(id){return $$.levelColor($$.cache[id].values[0].value)}:$$.color).attr("x1",x1ForLegendTile).attr("y1",yForLegendTile).attr("x2",x2ForLegendTile).attr("y2",yForLegendTile)}background&&(withTransition?background.transition():background).attr("height",$$.getLegendHeight()-12).attr("width",maxWidth*(step+1)+10),$$.updateLegendItemWidth(maxWidth),$$.updateLegendItemHeight(maxHeight),$$.updateLegendStep(step)}});
// CONCATENATED MODULE: ./src/internals/title.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
/**
 * Get the text position
 * @param {String} pos right, left or center
 * @param {Number} width chart width
 * @return {String|Number} text-anchor value or position in pixel
 * @private
 */
var getTextPos=function(){var position,pos=arguments.length>0&&void 0!==arguments[0]?arguments[0]:"left",width=arguments.length>1?arguments[1]:void 0,isNum=isNumber(width);return position=pos.indexOf("center")>-1?isNum?width/2:"middle":pos.indexOf("right")>-1?isNum?width:"end":isNum?0:"start",position};extend(ChartInternal_ChartInternal.prototype,{
/**
   * Initializes the title
   * @private
   */
initTitle:function(){var $$=this;if($$.config.title_text){$$.title=$$.svg.append("g");var text=$$.title.append("text").style("text-anchor",getTextPos($$.config.title_position)).attr("class",config_classes.title);setTextValue(text,$$.config.title_text,[.3,1.5])}},
/**
   * Redraw title
   * @private
   */
redrawTitle:function(){var $$=this;if($$.title){var y=$$.yForTitle.call($$);/g/i.test($$.title.node().tagName)?$$.title.attr("transform","translate(".concat(getTextPos($$.config.title_position,$$.currentWidth),", ").concat(y,")")):$$.title.attr("x",$$.xForTitle.call($$)).attr("y",y)}},
/**
   * Returns the x attribute value of the title
   * @private
   * @returns {Number} x attribute value
   */
xForTitle:function(){var x,$$=this,config=$$.config,position=config.title_position||"left",textRectWidth=$$.getTextRect($$.title,config_classes.title).width;return/(right|center)/.test(position)?(x=$$.currentWidth-textRectWidth,position.indexOf("right")>=0?x=$$.currentWidth-textRectWidth-config.title_padding.right:position.indexOf("center")>=0&&(x=($$.currentWidth-textRectWidth)/2)):x=config.title_padding.left||0,x},
/**
   * Returns the y attribute value of the title
   * @private
   * @returns {Number} y attribute value
   */
yForTitle:function(){var $$=this;return($$.config.title_padding.top||0)+$$.getTextRect($$.title,config_classes.title).height},
/**
   * Get title padding
   * @private
   * @returns {Number} padding value
   */
getTitlePadding:function(){var $$=this;return $$.yForTitle()+($$.config.title_padding.bottom||0)}}),
// CONCATENATED MODULE: ./src/internals/clip.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(ChartInternal_ChartInternal.prototype,{initClip:function(){var $$=this;// MEMO: clipId needs to be unique because it conflicts when multiple charts exist
// Define 'clip-path' attribute values
$$.clipId="".concat($$.datetimeId,"-clip"),$$.clipIdForXAxis="".concat($$.clipId,"-xaxis"),$$.clipIdForYAxis="".concat($$.clipId,"-yaxis"),$$.clipIdForGrid="".concat($$.clipId,"-grid"),$$.clipPath=$$.getClipPath($$.clipId),$$.clipPathForXAxis=$$.getClipPath($$.clipIdForXAxis),$$.clipPathForYAxis=$$.getClipPath($$.clipIdForYAxis),$$.clipPathForGrid=$$.getClipPath($$.clipIdForGrid)},getClipPath:function(id){var $$=this,config=$$.config;if(!config.clipPath&&/-clip$/.test(id)||!config.axis_x_clipPath&&/-clip-xaxis$/.test(id)||!config.axis_y_clipPath&&/-clip-yaxis$/.test(id))return null;var isIE9=!!win.navigator&&win.navigator.appVersion.toLowerCase().indexOf("msie 9.")>=0;return"url(".concat(isIE9?"":browser_doc.URL.split("#")[0],"#").concat(id,")")},appendClip:function(parent,id){return parent.append("clipPath").attr("id",id).append("rect")},getAxisClipX:function(forHorizontal){
// axis line width + padding for left
var left=Math.max(30,this.margin.left);return forHorizontal?-(1+left):-(left-1)},getAxisClipY:function(forHorizontal){return forHorizontal?-20:-this.margin.top},getXAxisClipX:function(){var $$=this;return $$.getAxisClipX(!$$.config.axis_rotated)},getXAxisClipY:function(){var $$=this;return $$.getAxisClipY(!$$.config.axis_rotated)},getYAxisClipX:function(){var $$=this;return $$.config.axis_y_inner?-1:$$.getAxisClipX($$.config.axis_rotated)},getYAxisClipY:function(){var $$=this;return $$.getAxisClipY($$.config.axis_rotated)},getAxisClipWidth:function(forHorizontal){var $$=this,left=Math.max(30,$$.margin.left),right=Math.max(30,$$.margin.right);
// width + axis line width + padding for left/right
return forHorizontal?$$.width+2+left+right:$$.margin.left+20},getAxisClipHeight:function(forHorizontal){
// less than 20 is not enough to show the axis label 'outer' without legend
return(forHorizontal?this.margin.bottom:this.margin.top+this.height)+20},getXAxisClipWidth:function(){var $$=this;return $$.getAxisClipWidth(!$$.config.axis_rotated)},getXAxisClipHeight:function(){var $$=this;return $$.getAxisClipHeight(!$$.config.axis_rotated)},getYAxisClipWidth:function(){var $$=this;return $$.getAxisClipWidth($$.config.axis_rotated)+($$.config.axis_y_inner?20:0)},getYAxisClipHeight:function(){var $$=this;return $$.getAxisClipHeight($$.config.axis_rotated)},updateXAxisTickClip:function(){var $$=this,newXAxisHeight=$$.getHorizontalAxisHeight("x");if($$.defs&&!$$.clipXAxisTickTexts){var clipId="".concat($$.clipId,"-xaxisticktexts");$$.clipXAxisTickTexts=$$.appendClip($$.defs,clipId),$$.clipPathForXAxisTickTexts=$$.getClipPath(clipId),$$.clipIdForXAxisTickTexts=clipId}!$$.config.axis_x_tick_multiline&&$$.getAxisTickRotate("x")&&newXAxisHeight!==$$.xAxisHeight&&($$.setXAxisTickClipWidth(),$$.setXAxisTickTextClipPathWidth()),$$.xAxisHeight=newXAxisHeight},setXAxisTickClipWidth:function(){var $$=this,config=$$.config,xAxisTickRotate=$$.getAxisTickRotate("x");if(!config.axis_x_tick_multiline&&xAxisTickRotate){var sinRotation=Math.sin(Math.PI/180*Math.abs(xAxisTickRotate));$$.xAxisTickClipPathMaxWidth=($$.getHorizontalAxisHeight("x")-20)/sinRotation}else $$.xAxisTickClipPathMaxWidth=null},setXAxisTickTextClipPathWidth:function(){var $$=this;$$.svg&&$$.svg.select("#".concat($$.clipIdForXAxisTickTexts," rect")).attr("width",$$.xAxisTickClipPathMaxWidth).attr("height",30)}}),
// CONCATENATED MODULE: ./src/internals/region.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
// selection
extend(ChartInternal_ChartInternal.prototype,{initRegion:function(){var $$=this;$$.region=$$.main.append("g").attr("clip-path",$$.clipPath).attr("class",config_classes.regions)},updateRegion:function(duration){var $$=this,config=$$.config;
// hide if arc type
// select <g> element
$$.region.style("visibility",$$.hasArcType()?"hidden":"visible"),$$.mainRegion=$$.main.select(".".concat(config_classes.regions)).selectAll(".".concat(config_classes.region)).data(config.regions),$$.mainRegion.exit().transition().duration(duration).style("opacity","0").remove(),$$.mainRegion=$$.mainRegion.enter().append("g").merge($$.mainRegion).attr("class",$$.classRegion.bind($$)),$$.mainRegion.append("rect").style("fill-opacity","0")},redrawRegion:function(withTransition){var $$=this,regions=$$.mainRegion.select("rect");return regions=(withTransition?regions.transition():regions).attr("x",$$.regionX.bind($$)).attr("y",$$.regionY.bind($$)).attr("width",$$.regionWidth.bind($$)).attr("height",$$.regionHeight.bind($$)),[(withTransition?regions.transition():regions).style("fill-opacity",(function(d){return isValue(d.opacity)?d.opacity:"0.1"})).on("end",(function(){Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this.parentNode).selectAll("rect:not([x])").remove()}))]},getRegionXY:function(type,d){var scale,$$=this,config=$$.config,isRotated=config.axis_rotated,isX="x"===type,key="start",pos=0;return"y"===d.axis||"y2"===d.axis?(!isX&&(key="end"),(isX?isRotated:!isRotated)&&key in d&&(scale=$$[d.axis],pos=scale(d[key]))):(isX?!isRotated:isRotated)&&key in d&&(scale=$$.zoomScale||$$.x,pos=scale($$.isTimeSeries()?$$.parseDate(d[key]):d[key])),pos},regionX:function(d){return this.getRegionXY("x",d)},regionY:function(d){return this.getRegionXY("y",d)},getRegionSize:function(type,d){var scale,$$=this,config=$$.config,isRotated=config.axis_rotated,isWidth="width"===type,start=$$[isWidth?"regionX":"regionY"](d),key="end",end=$$[type];return"y"===d.axis||"y2"===d.axis?(!isWidth&&(key="start"),(isWidth?isRotated:!isRotated)&&key in d&&(scale=$$[d.axis],end=scale(d[key]))):(isWidth?!isRotated:isRotated)&&key in d&&(scale=$$.zoomScale||$$.x,end=scale($$.isTimeSeries()?$$.parseDate(d[key]):d[key])),end<start?0:end-start},regionWidth:function(d){return this.getRegionSize("width",d)},regionHeight:function(d){return this.getRegionSize("height",d)},isRegionOnX:function(d){return!d.axis||"x"===d.axis}}),
// CONCATENATED MODULE: ./src/interactions/drag.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(ChartInternal_ChartInternal.prototype,{
/**
   * Called when dragging.
   * Data points can be selected.
   * @private
   * @param {Object} mouse Object
   */
drag:function(mouse){var $$=this,config=$$.config,main=$$.main;if(!$$.hasArcType()&&config.data_selection_enabled&&(// do nothing if not selectable
!config.zoom_enabled||$$.zoom.altDomain)&&config.data_selection_multiple){var _$$$dragStart=_slicedToArray($$.dragStart,2),sx=_$$$dragStart[0],sy=_$$$dragStart[1],_mouse=_slicedToArray(mouse,2),mx=_mouse[0],my=_mouse[1],minX=Math.min(sx,mx),maxX=Math.max(sx,mx),minY=config.data_selection_grouped?$$.margin.top:Math.min(sy,my),maxY=config.data_selection_grouped?$$.height:Math.max(sy,my);main.select(".".concat(config_classes.dragarea)).attr("x",minX).attr("y",minY).attr("width",maxX-minX).attr("height",maxY-minY),main.selectAll(".".concat(config_classes.shapes)).selectAll(".".concat(config_classes.shape)).filter((function(d){return config.data_selection_isselectable(d)})).each((function(d,i){var toggle,shape=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this),isSelected=shape.classed(config_classes.SELECTED),isIncluded=shape.classed(config_classes.INCLUDED),isWithin=!1;if(shape.classed(config_classes.circle)){var x=1*shape.attr("cx"),y=1*shape.attr("cy");toggle=$$.togglePoint,isWithin=minX<x&&x<maxX&&minY<y&&y<maxY}else{if(!shape.classed(config_classes.bar))// line/area selection not supported yet
return;var _getPathBox=getPathBox(this),_x=_getPathBox.x,width=(y=_getPathBox.y,_getPathBox.width),height=_getPathBox.height;toggle=$$.togglePath,isWithin=!(maxX<_x||_x+width<minX)&&!(maxY<y||y+height<minY)}isWithin^isIncluded&&(shape.classed(config_classes.INCLUDED,!isIncluded),shape.classed(config_classes.SELECTED,!isSelected),toggle.call($$,!isSelected,shape,d,i))}))}},
/**
   * Called when the drag starts.
   * Adds and Shows the drag area.
   * @private
   * @param {Object} mouse Object
   */
dragstart:function(mouse){var $$=this,config=$$.config;$$.hasArcType()||!config.data_selection_enabled||($$.dragStart=mouse,$$.main.select(".".concat(config_classes.chart)).append("rect").attr("class",config_classes.dragarea).style("opacity","0.1"),$$.setDragStatus(!0))},
/**
   * Called when the drag finishes.
   * Removes the drag area.
   * @private
   */
dragend:function(){var $$=this,config=$$.config;$$.hasArcType()||!config.data_selection_enabled||($$.main.select(".".concat(config_classes.dragarea)).transition().duration(100).style("opacity","0").remove(),$$.main.selectAll(".".concat(config_classes.shape)).classed(config_classes.INCLUDED,!1),$$.setDragStatus(!1))},setDragStatus:function(isDragging){this.dragging=isDragging}}),
// CONCATENATED MODULE: ./src/internals/selection.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(ChartInternal_ChartInternal.prototype,{
/**
   * Select a point
   * @private
   * @param {Object} target point
   * @param {Object} data
   * @param {Number} index
   */
selectPoint:function(target,d,i){var $$=this,config=$$.config,isRotated=config.axis_rotated,cx=(isRotated?$$.circleY:$$.circleX).bind($$),cy=(isRotated?$$.circleX:$$.circleY).bind($$),r=$$.pointSelectR.bind($$);
// add selected-circle on low layer g
callFn(config.data_onselected,$$.api,d,target.node()),$$.main.select(".".concat(config_classes.selectedCircles).concat($$.getTargetSelectorSuffix(d.id))).selectAll(".".concat(config_classes.selectedCircle,"-").concat(i)).data([d]).enter().append("circle").attr("class",(function(){return $$.generateClass(config_classes.selectedCircle,i)})).attr("cx",cx).attr("cy",cy).attr("stroke",$$.color).attr("r",(function(d2){return 1.4*$$.pointSelectR(d2)})).transition().duration(100).attr("r",r)},
/**
   * Unelect a point
   * @private
   * @param {Object} target point
   * @param {Object} data
   * @param {Number} index
   */
unselectPoint:function(target,d,i){var $$=this;
// remove selected-circle from low layer g
callFn($$.config.data_onunselected,$$.api,d,target.node()),$$.main.select(".".concat(config_classes.selectedCircles).concat($$.getTargetSelectorSuffix(d.id))).selectAll(".".concat(config_classes.selectedCircle,"-").concat(i)).transition().duration(100).attr("r",0).remove()},
/**
   * Toggles the selection of points
   * @private
   * @param {Boolean} whether or not to select.
   * @param {Object} target point
   * @param {Object} data
   * @param {Number} index
   */
togglePoint:function(selected,target,d,i){var method="".concat(selected?"":"un","selectPoint");this[method](target,d,i)},
/**
   * Select a path
   * @private
   * @param {Object} target path
   * @param {Object} data
   */
selectPath:function(target,d){var $$=this,config=$$.config;callFn(config.data_onselected,$$,d,target.node()),config.interaction_brighten&&target.style("filter","brightness(1.25)")},
/**
   * Unelect a path
   * @private
   * @param {Object} target path
   * @param {Object} data
   */
unselectPath:function(target,d){var $$=this,config=$$.config;callFn(config.data_onunselected,$$,d,target.node()),config.interaction_brighten&&target.transition().duration(100).style("fill",(function(){return $$.color(d)}))},
/**
   * Toggles the selection of lines
   * @private
   * @param {Boolean} whether or not to select.
   * @param {Object} target shape
   * @param {Object} data
   * @param {Number} index
   */
togglePath:function(selected,target,d,i){this["".concat(selected?"":"un","selectPath")](target,d,i)},
/**
   * Returns the toggle method of the target
   * @private
   * @param {Object} target shape
   * @param {Object} data
   * @returns {Function} toggle method
   */
getToggle:function(that,d){var $$=this;return"path"===that.nodeName?$$.togglePath:$$.isStepType(d)?function(){}:// circle is hidden in step chart, so treat as within the click area
$$.togglePoint},
/**
   * Toggles the selection of shapes
   * @private
   * @param {Object} target shape
   * @param {Object} data
   * @param {Number} index
   */
toggleShape:function(that,d,i){var toggledShape,$$=this,config=$$.config,shape=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(that),isSelected=shape.classed(config_classes.SELECTED),toggle=$$.getToggle(that,d).bind($$);if(config.data_selection_enabled&&config.data_selection_isselectable(d)){if(!config.data_selection_multiple){var selector=".".concat(config_classes.shapes);config.data_selection_grouped&&(selector+=$$.getTargetSelectorSuffix(d.id)),$$.main.selectAll(selector).selectAll(".".concat(config_classes.shape)).each((function(d,i){var shape=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this);shape.classed(config_classes.SELECTED)&&(toggledShape=shape,toggle(!1,shape.classed(config_classes.SELECTED,!1),d,i))}))}toggledShape&&toggledShape.node()===shape.node()||(shape.classed(config_classes.SELECTED,!isSelected),toggle(!isSelected,shape,d,i))}}}),
// CONCATENATED MODULE: ./src/interactions/subchart.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(ChartInternal_ChartInternal.prototype,{
/**
   * Initialize the brush.
   * @private
   */
initBrush:function(){var $$=this,config=$$.config,isRotated=config.axis_rotated;$$.brush=isRotated?Object(external_commonjs_d3_brush_commonjs2_d3_brush_amd_d3_brush_root_d3_["brushY"])():Object(external_commonjs_d3_brush_commonjs2_d3_brush_amd_d3_brush_root_d3_["brushX"])();
// set "brush" event
var lastDomain,timeout,brushHandler=function(){$$.redrawForBrush()},getBrushSize=function(){var brush=$$.svg.select(".".concat(config_classes.brush," .overlay")),brushSize={width:0,height:0};return brush.size()&&(brushSize.width=+brush.attr("width"),brushSize.height=+brush.attr("height")),brushSize[isRotated?"width":"height"]};
// set the brush extent
$$.brush.on("start",(function(){"touch"===$$.inputType&&$$.hideTooltip(),brushHandler()})).on("brush",brushHandler).on("end",(function(){lastDomain=$$.x.orgDomain()})),$$.brush.updateResize=function(){var _this=this;timeout&&clearTimeout(timeout),timeout=setTimeout((function(){var selection=_this.getSelection();lastDomain&&Object(external_commonjs_d3_brush_commonjs2_d3_brush_amd_d3_brush_root_d3_["brushSelection"])(selection.node())&&_this.move(selection,lastDomain.map($$.subX.orgScale()))}),0)},$$.brush.update=function(){var extent=this.extent()();return 0===extent[1].filter((function(v){return isNaN(v)})).length&&$$.context&&$$.context.select(".".concat(config_classes.brush)).call(this),this},$$.brush.scale=function(scale){var h=config.subchart_size_height||getBrushSize(),extent=$$.getExtent();
// [[x0, y0], [x1, y1]], where [x0, y0] is the top-left corner and [x1, y1] is the bottom-right corner
// when extent updates, brush selection also be re-applied
// https://github.com/d3/d3/issues/2918
!extent&&scale.range?extent=[[0,0],[scale.range()[1],h]]:isArray(extent)&&(extent=extent.map((function(v,i){return[v,i>0?h:i]}))),isRotated&&extent[1].reverse(),this.extent(extent),this.update()},$$.brush.getSelection=function(){return $$.context?$$.context.select(".".concat(config_classes.brush)):Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])([])}},
/**
   * Initialize the subchart.
   * @private
   */
initSubchart:function(){var $$=this,config=$$.config,visibility=config.subchart_show?"visible":"hidden",clipId="".concat($$.clipId,"-subchart"),clipPath=$$.getClipPath(clipId);$$.clipIdForSubchart=clipId,$$.appendClip($$.defs,clipId),$$.initBrush(),$$.context=$$.svg.append("g").attr("transform",$$.getTranslate("context"));var context=$$.context;
// Define g for chart area
// Define g for bar chart area
// Define g for line chart area
// Add extent rect for Brush
// ATTENTION: This must be called AFTER chart added
// Add Axis
context.style("visibility",visibility),context.append("g").attr("clip-path",clipPath).attr("class",config_classes.chart),$$.hasType("bar")&&context.select(".".concat(config_classes.chart)).append("g").attr("class",config_classes.chartBars),context.select(".".concat(config_classes.chart)).append("g").attr("class",config_classes.chartLines),context.append("g").attr("clip-path",clipPath).attr("class",config_classes.brush).call($$.brush),$$.axes.subx=context.append("g").attr("class",config_classes.axisX).attr("transform",$$.getTranslate("subx")).attr("clip-path",config.axis_rotated?"":$$.clipPathForXAxis).style("visibility",config.subchart_axis_x_show?visibility:"hidden")},
/**
   * Update sub chart
   * @private
   * @param {Object} $$.data.targets
   */
updateTargetsForSubchart:function(targets){var $$=this,context=$$.context,config=$$.config,classChartBar=$$.classChartBar.bind($$),classBars=$$.classBars.bind($$),classChartLine=$$.classChartLine.bind($$),classLines=$$.classLines.bind($$),classAreas=$$.classAreas.bind($$);if(config.subchart_show){
// -- Bar --//
var contextBarUpdate=context.select(".".concat(config_classes.chartBars)).selectAll(".".concat(config_classes.chartBar)).data(targets).attr("class",classChartBar),contextBarEnter=contextBarUpdate.enter().append("g").style("opacity","0").attr("class",classChartBar).merge(contextBarUpdate);contextBarEnter.append("g").attr("class",classBars);
// -- Line --//
var contextLineUpdate=context.select(".".concat(config_classes.chartLines)).selectAll(".".concat(config_classes.chartLine)).data(targets).attr("class",classChartLine),contextLineEnter=contextLineUpdate.enter().append("g").style("opacity","0").attr("class",classChartLine).merge(contextLineUpdate);
// Lines for each data
// Area
// -- Brush --//
contextLineEnter.append("g").attr("class",classLines),$$.hasType("area")&&contextLineEnter.append("g").attr("class",classAreas),context.selectAll(".".concat(config_classes.brush," rect")).attr(config.axis_rotated?"width":"height",config.axis_rotated?$$.width2:$$.height2)}},
/**
   * Update the bar of the sub chart
   * @private
   * @param {Object} durationForExit
   */
updateBarForSubchart:function(durationForExit){var $$=this;$$.contextBar=$$.context.selectAll(".".concat(config_classes.bars)).selectAll(".".concat(config_classes.bar)).data($$.barData.bind($$)),$$.contextBar.exit().transition().duration(durationForExit).style("opacity","0").remove(),$$.contextBar=$$.contextBar.enter().append("path").attr("class",$$.classBar.bind($$)).style("stroke","none").style("fill",$$.color).merge($$.contextBar).style("opacity",$$.initialOpacity.bind($$))},
/**
   * Redraw the bar of the subchart
   * @private
   * @param {String} path in subchart bar
   * @param {Boolean} whether or not to transition.
   * @param {Number} transition duration
   */
redrawBarForSubchart:function(drawBarOnSub,withTransition,duration){var contextBar=withTransition?this.contextBar.transition(getRandom()).duration(duration):this.contextBar;contextBar.attr("d",drawBarOnSub).style("opacity","1")},
/**
   * Update the line of the sub chart
   * @private
   * @param {Number} Fade-out transition duration
   */
updateLineForSubchart:function(durationForExit){var $$=this;$$.contextLine=$$.context.selectAll(".".concat(config_classes.lines)).selectAll(".".concat(config_classes.line)).data($$.lineData.bind($$)),$$.contextLine.exit().transition().duration(durationForExit).style("opacity","0").remove(),$$.contextLine=$$.contextLine.enter().append("path").attr("class",$$.classLine.bind($$)).style("stroke",$$.color).merge($$.contextLine).style("opacity",$$.initialOpacity.bind($$))},
/**
   * Redraw the line of the subchart
   * @private
   * @param {String} path in subchart line
   * @param {Boolean} whether or not to transition
   * @param {Number} transition duration
   */
redrawLineForSubchart:function(drawLineOnSub,withTransition,duration){var contextLine=withTransition?this.contextLine.transition(getRandom()).duration(duration):this.contextLine;contextLine.attr("d",drawLineOnSub).style("opacity","1")},
/**
   * Update the area of the sub chart
   * @private
   * @param {Number} Fade-out transition duration
   */
updateAreaForSubchart:function(durationForExit){var $$=this;$$.contextArea=$$.context.selectAll(".".concat(config_classes.areas)).selectAll(".".concat(config_classes.area)).data($$.lineData.bind($$)),$$.contextArea.exit().transition().duration(durationForExit).style("opacity","0").remove(),$$.contextArea=$$.contextArea.enter().append("path").attr("class",$$.classArea.bind($$)).style("fill",$$.color).style("opacity",(function(){return $$.orgAreaOpacity=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this).style("opacity"),"0"})).merge($$.contextArea).style("opacity","0")},
/**
   * Redraw the area of the subchart
   * @private
   * @param {String} path in subchart line
   * @param {Boolean} whether or not to transition
   * @param {Number} transition duration
   */
redrawAreaForSubchart:function(drawAreaOnSub,withTransition,duration){var contextArea=withTransition?this.contextArea.transition(getRandom()).duration(duration):this.contextArea;contextArea.attr("d",drawAreaOnSub).style("fill",this.color).style("opacity",this.orgAreaOpacity)},
/**
   * Redraw subchart.
   * @private
   * @param {Boolean} withSubchart whether or not to show subchart
   * @param {Number} duration duration
   * @param {Object} shape Shape's info
   */
redrawSubchart:function(withSubchart,duration,shape){var $$=this,config=$$.config;$$.context.style("visibility",config.subchart_show?"visible":"hidden"),config.subchart_show&&(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["event"]&&"zoom"===external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["event"].type&&$$.brush.update(),withSubchart&&(!brushEmpty($$)&&$$.brush.update(),Object.keys(shape.type).forEach((function(v){var name=capitalize(v),draw=$$["generateDraw".concat(name)](shape.indices[v],!0);$$["update".concat(name,"ForSubchart")](duration),$$["redraw".concat(name,"ForSubchart")](draw,duration,duration)}))))},
/**
   * Redraw the brush.
   * @private
   */
redrawForBrush:function(){var $$=this;$$.redraw({withTransition:!1,withY:$$.config.zoom_rescale,withSubchart:!1,withUpdateXDomain:!0,withDimension:!1}),$$.config.subchart_onbrush.call($$.api,$$.x.orgDomain())},
/**
   * Transform context
   * @private
   * @param {Boolean} indicates transition is enabled
   * @param {Object} The return value of the generateTransitions method of Axis.
   */
transformContext:function(withTransition,transitions){var subXAxis,$$=this;transitions&&transitions.axisSubX?subXAxis=transitions.axisSubX:(subXAxis=$$.context.select(".".concat(config_classes.axisX)),withTransition&&(subXAxis=subXAxis.transition())),$$.context.attr("transform",$$.getTranslate("context")),subXAxis.attr("transform",$$.getTranslate("subx"))},
/**
   * Get extent value
   * @private
   * @returns {Array} default extent
   */
getExtent:function(){var $$=this,extent=$$.config.axis_x_extent;return extent&&(isFunction(extent)?extent=extent($$.getXDomain($$.data.targets),$$.subX):$$.isTimeSeries()&&extent.every(isNaN)&&(extent=extent.map((function(v){return $$.subX($$.parseDate(v))})))),extent}});
// EXTERNAL MODULE: external {"commonjs":"d3-zoom","commonjs2":"d3-zoom","amd":"d3-zoom","root":"d3"}
var external_commonjs_d3_zoom_commonjs2_d3_zoom_amd_d3_zoom_root_d3_=__nested_webpack_require_7012__(12);
// CONCATENATED MODULE: ./src/interactions/zoom.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */extend(ChartInternal_ChartInternal.prototype,{
/**
   * Initialize zoom.
   * @private
   */
initZoom:function(){var $$=this;$$.zoomScale=null,$$.generateZoom(),$$.initZoomBehaviour()},
/**
   * Bind zoom event
   * @param {Boolean} bind Weather bind or unbound
   * @private
   */
bindZoomEvent:function(){var bind=!(arguments.length>0&&void 0!==arguments[0])||arguments[0],$$=this,zoomEnabled=$$.config.zoom_enabled;$$.redrawEventRect();var eventRects=$$.main.select(".".concat(config_classes.eventRects));zoomEnabled&&bind?!$$.config.subchart_show&&$$.bindZoomOnEventRect(eventRects,zoomEnabled.type):!1===bind&&($$.api.unzoom(),eventRects.on(".zoom",null).on(".drag",null))},
/**
   * Generate zoom
   * @private
   */
generateZoom:function(){var $$=this,config=$$.config,zoom=Object(external_commonjs_d3_zoom_commonjs2_d3_zoom_amd_d3_zoom_root_d3_["zoom"])().duration(0).on("start",$$.onZoomStart.bind($$)).on("zoom",$$.onZoom.bind($$)).on("end",$$.onZoomEnd.bind($$));
// get zoom extent
/**
     * Update scale according zoom transform value
     * @param {Object} transform
     * @private
     */zoom.orgScaleExtent=function(){var extent=config.zoom_extent||[1,10];return[extent[0],Math.max($$.getMaxDataCount()/extent[1],extent[1])]},zoom.updateScaleExtent=function(){var ratio=diffDomain($$.x.orgDomain())/diffDomain($$.getZoomDomain()),extent=this.orgScaleExtent();return this.scaleExtent([extent[0]*ratio,extent[1]*ratio]),this},zoom.updateTransformScale=function(transform){$$.orgXScale&&$$.orgXScale.range($$.x.range());
// rescale from the original scale
var newScale=transform[config.axis_rotated?"rescaleY":"rescaleX"]($$.orgXScale||$$.x),domain=$$.trimXDomain(newScale.domain()),rescale=config.zoom_rescale;newScale.domain(domain,$$.orgXDomain),$$.zoomScale=$$.getCustomizedScale(newScale),$$.xAxis.scale($$.zoomScale),rescale&&(!$$.orgXScale&&($$.orgXScale=$$.x.copy()),$$.x.domain(domain))},$$.zoom=zoom},
/**
   * 'start' event listener
   * @private
   */
onZoomStart:function(){var $$=this,event=external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["event"].sourceEvent;event&&($$.zoom.startEvent=event,callFn($$.config.zoom_onzoomstart,$$.api,event))},
/**
   * 'zoom' event listener
   * @private
   */
onZoom:function(){var $$=this,config=$$.config,event=external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["event"],sourceEvent=event.sourceEvent;if(config.zoom_enabled&&event.sourceEvent&&0!==$$.filterTargetsToShow($$.data.targets).length&&($$.zoomScale||!(sourceEvent.type.indexOf("touch")>-1)||1!==sourceEvent.touches.length)){var isMousemove="mousemove"===sourceEvent.type,isZoomOut=sourceEvent.wheelDelta<0,transform=event.transform;!isMousemove&&isZoomOut&&$$.x.domain().every((function(v,i){return v!==$$.orgXDomain[i]}))&&$$.x.domain($$.orgXDomain),$$.zoom.updateTransformScale(transform),$$.isCategorized()&&$$.x.orgDomain()[0]===$$.orgXDomain[0]&&$$.x.domain([$$.orgXDomain[0]-1e-10,$$.x.orgDomain()[1]]),$$.redraw({withTransition:!1,withY:config.zoom_rescale,withSubchart:!1,withEventRect:!1,withDimension:!1}),$$.cancelClick=isMousemove,callFn(config.zoom_onzoom,$$.api,$$.zoomScale.domain())}},
/**
   * 'end' event listener
   * @private
   */
onZoomEnd:function(){var $$=this,startEvent=$$.zoom.startEvent,event=external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["event"]&&external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["event"].sourceEvent;startEvent&&startEvent.type.indexOf("touch")>-1&&(startEvent=startEvent.changedTouches[0],event=event.changedTouches[0]),
// if click, do nothing. otherwise, click interaction will be canceled.
!startEvent||event&&startEvent.clientX===event.clientX&&startEvent.clientY===event.clientY||($$.redrawEventRect(),$$.updateZoom(),callFn($$.config.zoom_onzoomend,$$.api,$$[$$.zoomScale?"zoomScale":"subX"].domain()))},
/**
   * Get zoom domain
   * @returns {Array} zoom domain
  	 * @private
   */
getZoomDomain:function(){var $$=this,config=$$.config,_$$$orgXDomain=_slicedToArray($$.orgXDomain,2),min=_$$$orgXDomain[0],max=_$$$orgXDomain[1];return isDefined(config.zoom_x_min)&&(min=getMinMax("min",[min,config.zoom_x_min])),isDefined(config.zoom_x_max)&&(max=getMinMax("max",[max,config.zoom_x_max])),[min,max]},
/**
   * Update zoom
   * @param {Boolean} force Force unzoom
   * @private
   */
updateZoom:function(force){var $$=this;if($$.zoomScale){var zoomDomain=$$.zoomScale.domain(),xDomain=$$.subX.domain(),delta=.015,isfullyShown=(zoomDomain[0]<=xDomain[0]||zoomDomain[0]-delta<=xDomain[0])&&(xDomain[1]<=zoomDomain[1]||xDomain[1]<=zoomDomain[1]-delta);(force||isfullyShown)&&($$.xAxis.scale($$.subX),$$.x.domain($$.subX.orgDomain()),$$.zoomScale=null)}},
/**
   * Attach zoom event on <rect>
   * @private
   */
bindZoomOnEventRect:function(eventRects,type){var $$=this,behaviour="drag"===type?$$.zoomBehaviour:$$.zoom;eventRects.call(behaviour).on("dblclick.zoom",null)},
/**
   * Initialize the drag behaviour used for zooming.
   * @private
   */
initZoomBehaviour:function(){var $$=this,config=$$.config,isRotated=config.axis_rotated,start=0,end=0,zoomRect=null,prop={axis:isRotated?"y":"x",attr:isRotated?"height":"width",index:isRotated?1:0};$$.zoomBehaviour=Object(external_commonjs_d3_drag_commonjs2_d3_drag_amd_d3_drag_root_d3_["drag"])().clickDistance(4).on("start",(function(){$$.setDragStatus(!0),zoomRect||(zoomRect=$$.main.append("rect").attr("clip-path",$$.clipPath).attr("class",config_classes.zoomBrush).attr("width",isRotated?$$.width:0).attr("height",isRotated?0:$$.height)),start=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["mouse"])(this)[prop.index],end=start,zoomRect.attr(prop.axis,start).attr(prop.attr,0),$$.onZoomStart()})).on("drag",(function(){end=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["mouse"])(this)[prop.index],zoomRect.attr(prop.axis,Math.min(start,end)).attr(prop.attr,Math.abs(end-start))})).on("end",(function(){var _ref,scale=$$.zoomScale||$$.x;if($$.setDragStatus(!1),zoomRect.attr(prop.axis,0).attr(prop.attr,0),start>end&&(_ref=[end,start],start=_ref[0],end=_ref[1]),start<0&&(end+=Math.abs(start),start=0),start!==end)$$.api.zoom([start,end].map((function(v){return scale.invert(v)}))),$$.onZoomEnd();else if($$.isMultipleX())$$.clickHandlerForMultipleXS.bind(this)($$);else{var _event=external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["event"].sourceEvent||external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["event"],_ref2="clientX"in _event?[_event.clientX,_event.clientY]:[_event.x,_event.y],_ref3=_slicedToArray(_ref2,2),x=_ref3[0],y=_ref3[1],target=browser_doc.elementFromPoint(x,y);$$.clickHandlerForSingleX.bind(target)(Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(target).datum(),$$)}}))},setZoomResetButton:function(){var $$=this,config=$$.config,resetButton=config.zoom_resetButton;resetButton&&"drag"===config.zoom_enabled.type&&($$.zoom.resetBtn?$$.zoom.resetBtn.style("display",null):$$.zoom.resetBtn=$$.selectChart.append("div").classed(config_classes.button,!0).append("span").on("click",(function(){isFunction(resetButton.onclick)&&resetButton.onclick(this),$$.api.unzoom.call($$)})).classed(config_classes.buttonZoomReset,!0).text(resetButton.text||"Reset Zoom"))}});
// CONCATENATED MODULE: ./src/internals/color.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
/**
 * Set pattern's background color
 * (it adds a <rect> element to simulate bg-color)
 * @param {SVGPatternElement} pattern SVG pattern element
 * @param {String} color Color string
 * @param {String} id ID to be set
 * @return {{id: string, node: SVGPatternElement}}
 * @private
 */
var colorizePattern=function(pattern,color,id){var node=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(pattern.cloneNode(!0));return node.attr("id",id).insert("rect",":first-child").attr("width",node.attr("width")).attr("height",node.attr("height")).style("fill",color),{id:id,node:node.node()}},schemeCategory10=["#1f77b4","#ff7f0e","#2ca02c","#d62728","#9467bd","#8c564b","#e377c2","#7f7f7f","#bcbd22","#17becf"];// Replacement of d3.schemeCategory10.
// Contained differently depend on d3 version: v4(d3-scale), v5(d3-scale-chromatic)
extend(ChartInternal_ChartInternal.prototype,{
/**
   * Get color pattern from CSS file
   * CSS should be defined as: background-image: url("#00c73c;#fa7171; ...");
   * @return {Array}
   * @private
   */
getColorFromCss:function(){var body=browser_doc.body,pattern=body["__colorPattern__"];if(!pattern){var span=browser_doc.createElement("span");span.className=config_classes.colorPattern,span.style.display="none",body.appendChild(span);var content=win.getComputedStyle(span).backgroundImage;span.parentNode.removeChild(span),content.indexOf(";")>-1&&(pattern=content.replace(/url[^#]*|["'()]|(\s|%20)/g,"").split(";").map((function(v){return v.trim().replace(/[\"'\s]/g,"")})).filter(Boolean),body["__colorPattern__"]=pattern)}return pattern},generateColor:function(){var $$=this,config=$$.config,colors=config.data_colors,callback=config.data_color,ids=[],pattern=notEmpty(config.color_pattern)?config.color_pattern:Object(external_commonjs_d3_scale_commonjs2_d3_scale_amd_d3_scale_root_d3_["scaleOrdinal"])($$.getColorFromCss()||schemeCategory10).range(),originalColorPattern=pattern;if(isFunction(config.color_tiles)){var tiles=config.color_tiles(),colorizedPatterns=pattern.map((function(p,index){var color=p.replace(/[#\(\)\s,]/g,""),id="".concat($$.datetimeId,"-pattern-").concat(color,"-").concat(index);return colorizePattern(tiles[index%tiles.length],p,id)}));// Add background color to patterns
pattern=colorizedPatterns.map((function(p){return"url(#".concat(p.id,")")})),$$.patterns=colorizedPatterns}return function(d){var color,id=d.id||d.data&&d.data.id||d,isLine=$$.isTypeOf(id,["line","spline","step"])||!$$.config.data_types[id];return isFunction(colors[id])?color=colors[id](d):colors[id]?color=colors[id]:(ids.indexOf(id)<0&&ids.push(id),color=isLine?originalColorPattern[ids.indexOf(id)%originalColorPattern.length]:pattern[ids.indexOf(id)%pattern.length],colors[id]=color),isFunction(callback)?callback(color,d):color}},generateLevelColor:function(){var $$=this,config=$$.config,colors=config.color_pattern,threshold=config.color_threshold,asValue="value"===threshold.unit,max=threshold.max||100,values=threshold.values&&threshold.values.length?threshold.values:[];return notEmpty(threshold)?function(value){for(var v=asValue?value:100*value/max,color=colors[colors.length-1],i=0,l=values.length;i<l;i++)if(v<=values[i]){color=colors[i];break}return color}:null},
/**
   * Set the data over color.
   * When is out, will restore in its previous color value
   * @param {Boolean} isOver true: set overed color, false: restore
   * @param {Number|Object} d target index or data object for Arc type
   * @private
   */
setOverColor:function(isOver,d){var $$=this,config=$$.config,onover=config.color_onover,color=isOver?onover:$$.color;isObject(color)?color=function(_ref){var id=_ref.id;return id in onover?onover[id]:$$.color(id)}:isString(color)&&(color=function(){return onover}),isObject(d)?$$.main.selectAll(".".concat(config_classes.arc).concat($$.getTargetSelectorSuffix(d.id))).style("fill",color(d)):$$.main.selectAll(".".concat(config_classes.shape,"-").concat(d)).style("fill",color)}});
// CONCATENATED MODULE: ./src/internals/format.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
var getFormat=function($$,typeValue,v){var config=$$.config,type="axis_".concat(typeValue,"_tick_format"),format=config[type]?config[type]:$$.defaultValueFormat;return format(v)};extend(ChartInternal_ChartInternal.prototype,{getYFormat:function(forArc){var $$=this,formatForY=$$.yFormat,formatForY2=$$.y2Format;return forArc&&!$$.hasType("gauge")&&(formatForY=$$.defaultArcValueFormat,formatForY2=$$.defaultArcValueFormat),function(v,ratio,id){var format="y2"===$$.axis.getId(id)?formatForY2:formatForY;return format.call($$,v,ratio)}},yFormat:function(v){return getFormat(this,"y",v)},y2Format:function(v){return getFormat(this,"y2",v)},defaultValueFormat:function(v){return isValue(v)?+v:""},defaultArcValueFormat:function(v,ratio){return"".concat((100*ratio).toFixed(1),"%")},dataLabelFormat:function(targetId){var $$=this,dataLabels=$$.config.data_labels,defaultFormat=function(v){return isValue(v)?+v:""},format=defaultFormat;return isFunction(dataLabels.format)?format=dataLabels.format:isObjectType(dataLabels.format)&&(format=dataLabels.format[targetId]?!0===dataLabels.format[targetId]?defaultFormat:dataLabels.format[targetId]:function(){return""}),format}}),
// CONCATENATED MODULE: ./src/internals/cache.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(ChartInternal_ChartInternal.prototype,{
/**
   * Add cache
   * @param {String} key
   * @param {*} value
   * @param {Boolean} isDataType
   * @private
   */
addCache:function(key,value){var isDataType=!!(arguments.length>2&&void 0!==arguments[2])&&arguments[2];this.cache[key]=isDataType?this.cloneTarget(value):value},
/**
   * Remove cache
   * @param {String|Array} key
   * @private
   */
removeCache:function(key){var _this=this;toArray(key).forEach((function(v){return delete _this.cache[v]}))},
/**
   * Get cahce
   * @param {String|Array} key
   * @param {Boolean} isDataType
   * @return {*}
   * @private
   */
getCache:function(key){var isDataType=!!(arguments.length>1&&void 0!==arguments[1])&&arguments[1];if(isDataType){for(var id,targets=[],i=0;id=key[i];i++)id in this.cache&&targets.push(this.cloneTarget(this.cache[id]));return targets}return this.cache[key]||null},
/**
   * reset cached data
   * @param {Boolean} all true: reset all data, false: reset only '$' prefixed key data
   * @private
  	 */
resetCache:function(all){var $$=this;for(var x in $$.cache)(all||/^\$/.test(x))&&($$.cache[x]=null)}}),
// CONCATENATED MODULE: ./src/internals/class.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(ChartInternal_ChartInternal.prototype,{generateClass:function(prefix,targetId){return" ".concat(prefix," ").concat(prefix+this.getTargetSelectorSuffix(targetId))},classText:function(d){return this.generateClass(config_classes.text,d.index)},classTexts:function(d){return this.generateClass(config_classes.texts,d.id)},classShape:function(d){return this.generateClass(config_classes.shape,d.index)},classShapes:function(d){return this.generateClass(config_classes.shapes,d.id)},generateExtraLineClass:function(){var $$=this,classes=$$.config.line_classes||[],ids=[];return function(d){var id=d.id||d.data&&d.data.id||d;return ids.indexOf(id)<0&&ids.push(id),classes[ids.indexOf(id)%classes.length]}},classLine:function(d){return this.classShape(d)+this.generateClass(config_classes.line,d.id)},classLines:function(d){return this.classShapes(d)+this.generateClass(config_classes.lines,d.id)},classCircle:function(d){return this.classShape(d)+this.generateClass(config_classes.circle,d.index)},classCircles:function(d){return this.classShapes(d)+this.generateClass(config_classes.circles,d.id)},classBar:function(d){return this.classShape(d)+this.generateClass(config_classes.bar,d.index)},classBars:function(d){return this.classShapes(d)+this.generateClass(config_classes.bars,d.id)},classArc:function(d){return this.classShape(d.data)+this.generateClass(config_classes.arc,d.data.id)},classArcs:function(d){return this.classShapes(d.data)+this.generateClass(config_classes.arcs,d.data.id)},classArea:function(d){return this.classShape(d)+this.generateClass(config_classes.area,d.id)},classAreas:function(d){return this.classShapes(d)+this.generateClass(config_classes.areas,d.id)},classRegion:function(d,i){return"".concat(this.generateClass(config_classes.region,i)," ").concat("class"in d?d["class"]:"")},classEvent:function(d){return this.generateClass(config_classes.eventRect,d.index)},classTarget:function(id){var additionalClassSuffix=this.config.data_classes[id],additionalClass="";return additionalClassSuffix&&(additionalClass=" ".concat(config_classes.target,"-").concat(additionalClassSuffix)),this.generateClass(config_classes.target,id)+additionalClass},classFocus:function(d){return this.classFocused(d)+this.classDefocused(d)},classFocused:function(d){return" ".concat(this.focusedTargetIds.indexOf(d.id)>=0?config_classes.focused:"")},classDefocused:function(d){return" ".concat(this.defocusedTargetIds.indexOf(d.id)>=0?config_classes.defocused:"")},classChartText:function(d){return config_classes.chartText+this.classTarget(d.id)},classChartLine:function(d){return config_classes.chartLine+this.classTarget(d.id)},classChartBar:function(d){return config_classes.chartBar+this.classTarget(d.id)},classChartArc:function(d){return config_classes.chartArc+this.classTarget(d.data.id)},classChartRadar:function(d){return config_classes.chartRadar+this.classTarget(d.id)},getTargetSelectorSuffix:function(targetId){return targetId||0===targetId?"-".concat(targetId).replace(/[\s?!@#$%^&*()_=+,.<>'":;\[\]\/|~`{}\\]/g,"-"):""},selectorTarget:function(id,prefix){return"".concat(prefix||"",".").concat(config_classes.target+this.getTargetSelectorSuffix(id))},selectorTargets:function(idsValue,prefix){var $$=this,ids=idsValue||[];return ids.length?ids.map((function(id){return $$.selectorTarget(id,prefix)})):null},selectorLegend:function(id){return".".concat(config_classes.legendItem+this.getTargetSelectorSuffix(id))},selectorLegends:function(ids){var $$=this;return ids&&ids.length?ids.map((function(id){return $$.selectorLegend(id)})):null}}),
// CONCATENATED MODULE: ./src/api/api.focus.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(Chart_Chart.prototype,{
/**
   * This API highlights specified targets and fade out the others.<br><br>
   * You can specify multiple targets by giving an array that includes id as String. If no argument is given, all of targets will be highlighted.
   * @method focus
   * @instance
   * @memberof Chart
   * @param {String|Array} targetIdsValue Target ids to be highlighted.
   * @example
   *  // data1 will be highlighted and the others will be faded out
   *  chart.focus("data1");
   *
   * // data1 and data2 will be highlighted and the others will be faded out
   * chart.focus(["data1", "data2"]);
   *
   * // all targets will be highlighted
   * chart.focus();
   */
focus:function(targetIdsValue){var $$=this.internal,targetIds=$$.mapToTargetIds(targetIdsValue),candidates=$$.svg.selectAll($$.selectorTargets(targetIds.filter($$.isTargetToShow,$$)));this.revert(),this.defocus(),candidates.classed(config_classes.focused,!0).classed(config_classes.defocused,!1),$$.hasArcType()&&($$.expandArc(targetIds),$$.hasType("gauge")&&$$.markOverlapped(targetIdsValue,$$,".".concat(config_classes.gaugeValue))),$$.toggleFocusLegend(targetIds,!0),$$.focusedTargetIds=targetIds,$$.defocusedTargetIds=$$.defocusedTargetIds.filter((function(id){return targetIds.indexOf(id)<0}))},
/**
   * This API fades out specified targets and reverts the others.<br><br>
   * You can specify multiple targets by giving an array that includes id as String. If no argument is given, all of targets will be faded out.
   * @method defocus
   * @instance
   * @memberof Chart
   * @param {String|Array} Target ids to be faded out.
   * @example
   * // data1 will be faded out and the others will be reverted.
   * chart.defocus("data1");
   *
   * // data1 and data2 will be faded out and the others will be reverted.
   * chart.defocus(["data1", "data2"]);
   *
   * // all targets will be faded out.
   * chart.defocus();
   */
defocus:function(targetIdsValue){var $$=this.internal,targetIds=$$.mapToTargetIds(targetIdsValue),candidates=$$.svg.selectAll($$.selectorTargets(targetIds.filter($$.isTargetToShow,$$)));candidates.classed(config_classes.focused,!1).classed(config_classes.defocused,!0),$$.hasArcType()&&($$.unexpandArc(targetIds),$$.hasType("gauge")&&$$.undoMarkOverlapped($$,".".concat(config_classes.gaugeValue))),$$.toggleFocusLegend(targetIds,!1),$$.focusedTargetIds=$$.focusedTargetIds.filter((function(id){return targetIds.indexOf(id)<0})),$$.defocusedTargetIds=targetIds},
/**
   * This API reverts specified targets.<br><br>
   * You can specify multiple targets by giving an array that includes id as String. If no argument is given, all of targets will be reverted.
   * @method revert
   * @instance
   * @memberof Chart
   * @param {String|Array} Target ids to be reverted
   * @example
   * // data1 will be reverted.
   * chart.revert("data1");
   *
   * // data1 and data2 will be reverted.
   * chart.revert(["data1", "data2"]);
   *
   * // all targets will be reverted.
   * chart.revert();
   */
revert:function(targetIdsValue){var $$=this.internal,targetIds=$$.mapToTargetIds(targetIdsValue),candidates=$$.svg.selectAll($$.selectorTargets(targetIds));
// should be for all targets
candidates.classed(config_classes.focused,!1).classed(config_classes.defocused,!1),$$.hasArcType()&&$$.unexpandArc(targetIds),$$.config.legend_show&&($$.showLegend(targetIds.filter($$.isLegendToShow.bind($$))),$$.legend.selectAll($$.selectorLegends(targetIds)).filter((function(){return Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this).classed(config_classes.legendItemFocused)})).classed(config_classes.legendItemFocused,!1)),$$.focusedTargetIds=[],$$.defocusedTargetIds=[]}}),
// CONCATENATED MODULE: ./src/api/api.show.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(Chart_Chart.prototype,{
/**
   * Show/Hide data series
   * @private
   */
_showHide:function(show,targetIdsValue,options){var $$=this.internal,targetIds=$$.mapToTargetIds(targetIdsValue);$$["".concat(show?"remove":"add","HiddenTargetIds")](targetIds);var targets=$$.svg.selectAll($$.selectorTargets(targetIds)),opacity=show?"1":"0";targets.transition().style("opacity",opacity,"important").call($$.endall,(function(){targets.style("opacity",null).style("opacity",opacity)})),options.withLegend&&$$["".concat(show?"show":"hide","Legend")](targetIds),$$.redraw({withUpdateOrgXDomain:!0,withUpdateXDomain:!0,withLegend:!0})},
/**
   * Show data series on chart
   * @method show
   * @instance
   * @memberof Chart
   * @param {String|Array} [targetIdsValue=all] The target id value.
   * @param {Object} [options] The object can consist with following members:<br>
   *
   *    | Key | Type | default | Description |
   *    | --- | --- | --- | --- |
   *    | withLegend | Boolean | false | whether or not display legend |
   *
   * @example
   * // show 'data1'
   * chart.show("data1");
   *
   * // show 'data1' and 'data3'
   * chart.show(["data1", "data3"]);
   */
show:function(targetIdsValue){var options=arguments.length>1&&void 0!==arguments[1]?arguments[1]:{};this._showHide(!0,targetIdsValue,options)},
/**
   * Hide data series from chart
   * @method hide
   * @instance
   * @memberof Chart
   * @param {String|Array} [targetIdsValue=all] The target id value.
   * @param {Object} [options] The object can consist with following members:<br>
   *
   *    | Key | Type | default | Description |
   *    | --- | --- | --- | --- |
   *    | withLegend | Boolean | false | whether or not display legend |
   *
   * @example
   * // hide 'data1'
   * chart.hide("data1");
   *
   * // hide 'data1' and 'data3'
   * chart.hide(["data1", "data3"]);
   */
hide:function(targetIdsValue){var options=arguments.length>1&&void 0!==arguments[1]?arguments[1]:{};this._showHide(!1,targetIdsValue,options)},
/**
   * Toggle data series on chart. When target data is hidden, it will show. If is shown, it will hide in vice versa.
   * @method toggle
   * @instance
   * @memberof Chart
   * @param {String|Array} [targetIdsValue=all] The target id value.
   * @param {Object} [options] The object can consist with following members:<br>
   *
   *    | Key | Type | default | Description |
   *    | --- | --- | --- | --- |
   *    | withLegend | Boolean | false | whether or not display legend |
   *
   * @example
   * // toggle 'data1'
   * chart.toggle("data1");
   *
   * // toggle 'data1' and 'data3'
   * chart.toggle(["data1", "data3"]);
   */
toggle:function(targetIds){var _this=this,options=arguments.length>1&&void 0!==arguments[1]?arguments[1]:{},$$=this.internal,targets={show:[],hide:[]};
// sort show & hide target ids
// perform show & hide task separately
// https://github.com/naver/billboard.js/issues/454
$$.mapToTargetIds(targetIds).forEach((function(id){return targets[$$.isTargetToShow(id)?"hide":"show"].push(id)})),targets.show.length&&this.show(targets.show,options),targets.hide.length&&setTimeout((function(){return _this.hide(targets.hide,options)}),0)}});
// CONCATENATED MODULE: ./src/api/api.zoom.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
/**
 * Check if the given domain is within zoom range
 * @param {Array} domain
 * @return {Boolean}
 * @private
 */
var withinRange=function(domain,range){var _range=_slicedToArray(range,2),min=_range[0],max=_range[1];return domain.every((function(v,i){return 0===i?v>=min:v<=max}))},api_zoom_zoom=function(domainValue){var resultDomain,$$=this.internal,domain=domainValue;if($$.config.zoom_enabled&&domain&&withinRange(domain,$$.getZoomDomain())){var isTimeSeries=$$.isTimeSeries();if(isTimeSeries&&(domain=domain.map((function(x){return $$.parseDate(x)}))),$$.config.subchart_show){var xScale=$$.zoomScale||$$.x;$$.brush.getSelection().call($$.brush.move,[xScale(domain[0]),xScale(domain[1])]),resultDomain=domain}else $$.x.domain(domain),$$.zoomScale=$$.x,$$.xAxis.scale($$.zoomScale),resultDomain=$$.zoomScale.orgDomain();$$.redraw({withTransition:!0,withY:$$.config.zoom_rescale,withDimension:!1}),$$.setZoomResetButton(),callFn($$.config.zoom_onzoom,$$.api,resultDomain)}else resultDomain=$$.zoomScale?$$.zoomScale.domain():$$.x.orgDomain();return resultDomain};
/**
 * Zoom by giving x domain.
 * - **NOTE:**
 *  - For `wheel` type zoom, the minimum zoom range will be set as the given domain. To get the initial state, [.unzoom()](#unzoom) should be called.
 *  - To be used [zoom.enabled](Options.html#.zoom) option should be set as `truthy`.
 * @method zoom
 * @instance
 * @memberof Chart
 * @param {Array} domainValue If domain is given, the chart will be zoomed to the given domain. If no argument is given, the current zoomed domain will be returned.
 * @return {Array} domain value in array
 * @example
 *  // Zoom to specified domain
 *  chart.zoom([10, 20]);
 *
 *  // Get the current zoomed domain
 *  chart.zoom();
 */extend(api_zoom_zoom,{
/**
   * Enable and disable zooming.
   * @method zoom․enable
   * @instance
   * @memberof Chart
   * @param {String|Boolean} enabled Possible string values are "wheel" or "drag". If enabled is true, "wheel" will be used. If false is given, zooming will be disabled.<br>When set to false, the current zooming status will be reset.
   * @example
   *  // Enable zooming using the mouse wheel
   *  chart.zoom.enable(true);
   *  // Or
   *  chart.zoom.enable("wheel");
   *
   *  // Enable zooming by dragging
   *  chart.zoom.enable("drag");
   *
   *  // Disable zooming
   *  chart.zoom.enable(false);
   */
enable:function(){var enabled=arguments.length>0&&void 0!==arguments[0]?arguments[0]:"wheel",$$=this.internal,config=$$.config,enableType=enabled;enabled&&(enableType=isString(enabled)&&/^(drag|wheel)$/.test(enabled)?{type:enabled}:enabled),config.zoom_enabled=enableType,$$.zoom?!1===enabled&&$$.bindZoomEvent(!1):($$.initZoom(),$$.bindZoomEvent()),$$.updateAndRedraw()},
/**
   * Set or get x Axis maximum zoom range value
   * @method zoom․max
   * @instance
   * @memberof Chart
   * @param {Number} [max] maximum value to set for zoom
   * @return {Number} zoom max value
   * @example
   *  // Set maximum range value
   *  chart.zoom.max(20);
   */
max:function(_max){var $$=this.internal,config=$$.config;return(0===_max||_max)&&(config.zoom_x_max=getMinMax("max",[$$.orgXDomain[1],_max])),config.zoom_x_max},
/**
   * Set or get x Axis minimum zoom range value
   * @method zoom․min
   * @instance
   * @memberof Chart
   * @param {Number} [min] minimum value to set for zoom
   * @return {Number} zoom min value
   * @example
   *  // Set minimum range value
   *  chart.zoom.min(-1);
   */
min:function(_min){var $$=this.internal,config=$$.config;return(0===_min||_min)&&(config.zoom_x_min=getMinMax("min",[$$.orgXDomain[0],_min])),config.zoom_x_min},
/**
   * Set zoom range
   * @method zoom․range
   * @instance
   * @memberof Chart
   * @param {Object} [range]
   * @return {Object} zoom range value
   * {
   *   min: 0,
   *   max: 100
   * }
   * @example
   *  chart.zoom.range({
   *      min: 10,
   *      max: 100
   *  });
   */
range:function(_range2){var zoom=this.zoom;if(isObject(_range2)){var min=_range2.min,max=_range2.max;isDefined(min)&&zoom.min(min),isDefined(max)&&zoom.max(max)}return{min:zoom.min(),max:zoom.max()}}}),extend(Chart_Chart.prototype,{zoom:api_zoom_zoom,
/**
   * Unzoom zoomed area
   * @method unzoom
   * @instance
   * @memberof Chart
   * @example
   *  chart.unzoom();
   */
unzoom:function(){var $$=this.internal,config=$$.config;if($$.zoomScale){config.subchart_show?$$.brush.getSelection().call($$.brush.move,null):$$.zoom.updateTransformScale(external_commonjs_d3_zoom_commonjs2_d3_zoom_amd_d3_zoom_root_d3_["zoomIdentity"]),$$.updateZoom(!0),$$.zoom.resetBtn&&$$.zoom.resetBtn.style("display","none");
// reset transform
var eventRects=$$.main.select(".".concat(config_classes.eventRects));Object(external_commonjs_d3_zoom_commonjs2_d3_zoom_amd_d3_zoom_root_d3_["zoomTransform"])(eventRects.node())!==external_commonjs_d3_zoom_commonjs2_d3_zoom_amd_d3_zoom_root_d3_["zoomIdentity"]&&$$.zoom.transform(eventRects,external_commonjs_d3_zoom_commonjs2_d3_zoom_amd_d3_zoom_root_d3_["zoomIdentity"]),$$.redraw({withTransition:!0,withUpdateXDomain:!0,withUpdateOrgXDomain:!0,withY:config.zoom_rescale})}}}),
// CONCATENATED MODULE: ./src/api/api.load.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(Chart_Chart.prototype,{
/**
   * Load data to the chart.<br><br>
   * You can specify multiple targets by giving an array that includes id as String. If no argument is given, all of targets will be toggles.
   * - <b>Note:</b>
   *   - unload should be used if some data needs to be unloaded simultaneously.
   *     If you call unload API soon after/before load instead of unload param, chart will not be rendered properly because of cancel of animation.<br>
   *   - done will be called after data loaded, but it's not after rendering.
   *     It's because rendering will finish after some transition and there is some time lag between loading and rendering
   * @method load
   * @instance
   * @memberof Chart
   * @param {Object} args The object can consist with following members:<br>
   *
   *    | Key | Description |
   *    | --- | --- |
   *    | - url<br>- json<br>- rows<br>- columns | The data will be loaded. If data that has the same target id is given, the chart will be updated. Otherwise, new target will be added |
   *    | data | Data objects to be loaded. Checkout the example. |
   *    | names | Same as data.names() |
   *    | xs | Same as data.xs option  |
   *    | classes | The classes specified by data.classes will be updated. classes must be Object that has target id as keys. |
   *    | categories | The categories specified by axis.x.categories or data.x will be updated. categories must be Array. |
   *    | axes | The axes specified by data.axes will be updated. axes must be Object that has target id as keys. |
   *    | colors | The colors specified by data.colors will be updated. colors must be Object that has target id as keys. |
   *    | headers |  Set request header if loading via `data.url`.<br>@see [data․headers](Options.html#.data%25E2%2580%25A4headers) |
   *    | keys |  Choose which JSON objects keys correspond to desired data.<br>**NOTE:** Only for JSON object given as array.<br>@see [data․keys](Options.html#.data%25E2%2580%25A4keys) |
   *    | mimeType |  Set 'json' if loading JSON via url.<br>@see [data․mimeType](Options.html#.data%25E2%2580%25A4mimeType) |
   *    | - type<br>- types | The type of targets will be updated. type must be String and types must be Object. |
   *    | unload | Specify the data will be unloaded before loading new data. If true given, all of data will be unloaded. If target ids given as String or Array, specified targets will be unloaded. If absent or false given, unload will not occur. |
   *    | done | The specified function will be called after data loaded.|
   * @see [Demo](https://naver.github.io/billboard.js/demo/#Data.DataFromURL)
   * @example
   * // Load data1 and unload data2 and data3
   * chart.load({
   *     columns: [
   *        ["data1", 100, 200, 150, ...],
   *        ...
   *    ],
   *    unload: ["data2", "data3"],
   *    url: "...",
   *    done: function() { ... }
   * });
   * @example
   * // myAPI.json
   * // {
   * //   "data1": [220, 240, 270, 250, 280],
   * //   "data2": [180, 150, 300, 70, 120]
   * // }
   *
   * chart.load({
   *     url: './data/myAPI.json',
   *     mimeType: "json",
   *
   *     // set request header if is needed
   *     headers: {
   *       "Content-Type": "text/json"
   *     }
   * });
   * @example
   * chart.load({
   *     data: [
   *       // equivalent as: columns: [["data1", 30, 200, 100]]
   *       {"data1": 30}, {"data1": 200}, {"data1": 100}
   *
   *       // or
   *       // equivalent as: columns: [["data1", 10, 20], ["data2", 13, 30]]
   *       // {"data1": 10, "data2": 13}, {"data1": 20, "data2": 30}}
   *     ]
   * });
   * @example
   * chart.load({
   *     json: [
   *          {name: "www.site1.com", upload: 800, download: 500, total: 400},
   *     ],
   *     keys: {
   *         x: "name",
   *         value: ["upload", "download"]
   *     }
   * });
   */
load:function(args){var $$=this.internal,config=$$.config;
// update xs if specified
// update names if exists
// update classes if exists
// update axes if exists
// update colors if exists
args.xs&&$$.addXs(args.xs),"names"in args&&this.data.names(args.names),"classes"in args&&Object.keys(args.classes).forEach((function(id){config.data_classes[id]=args.classes[id]})),"categories"in args&&$$.isCategorized()&&(config.axis_x_categories=args.categories),"axes"in args&&Object.keys(args.axes).forEach((function(id){config.data_axes[id]=args.axes[id]})),"colors"in args&&Object.keys(args.colors).forEach((function(id){config.data_colors[id]=args.colors[id]})),"unload"in args&&!1!==args.unload?$$.unload($$.mapToTargetIds(!0===args.unload?null:args.unload),(function(){return $$.loadFromArgs(args)})):$$.loadFromArgs(args)},
/**
   * Unload data to the chart.<br><br>
   * You can specify multiple targets by giving an array that includes id as String. If no argument is given, all of targets will be toggles.
   * - <b>Note:</b>
   * If you call load API soon after/before unload, unload param of load should be used. Otherwise chart will not be rendered properly because of cancel of animation.<br>
   * `done` will be called after data loaded, but it's not after rendering. It's because rendering will finish after some transition and there is some time lag between loading and rendering.
   * @method unload
   * @instance
   * @memberof Chart
   * @param {Object} args
   *  | key | Type | Description |
   *  | --- | --- | --- |
   *  | ids | String &vert; Array | Target id data to be unloaded. If not given, all data will be unloaded. |
   *  | done | Fuction | Callback after data is unloaded. |
   * @example
   *  // Unload data2 and data3
   *  chart.unload({
   *    ids: ["data2", "data3"],
   *    done: function() {
   *       // called after the unloaded
   *    }
   *  });
   */
unload:function(argsValue){var $$=this.internal,args=argsValue||{};isArray(args)?args={ids:args}:isString(args)&&(args={ids:[args]});var ids=$$.mapToTargetIds(args.ids);$$.unload(ids,(function(){$$.redraw({withUpdateOrgXDomain:!0,withUpdateXDomain:!0,withLegend:!0}),$$.removeCache(ids),args.done&&args.done()}))}});
// EXTERNAL MODULE: external {"commonjs":"d3-ease","commonjs2":"d3-ease","amd":"d3-ease","root":"d3"}
var external_commonjs_d3_ease_commonjs2_d3_ease_amd_d3_ease_root_d3_=__nested_webpack_require_7012__(13);
// CONCATENATED MODULE: ./src/api/api.flow.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
// CONCATENATED MODULE: ./src/api/api.transform.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
/**
 * Change the type of the chart.
 * @private
 * @param {String|Array} targetIds
 * @param {String} type
 * @param {Object} optionsForRedraw
 */
function transformTo(targetIds,type,optionsForRedraw){var $$=this,options=optionsForRedraw||{withTransitionForAxis:!$$.hasArcType()};
// this is needed when transforming to arc
options.withTransitionForTransform=!1,$$.transiting=!1,$$.setTargetType(targetIds,type),$$.updateTargets($$.data.targets),$$.updateAndRedraw(options)}extend(Chart_Chart.prototype,{
/**
   * Flow data to the chart.<br><br>
   * By this API, you can append new data points to the chart.
   * @method flow
   * @instance
   * @memberof Chart
   * @param {Object} args The object can consist with following members:<br>
   *
   *    | Key | Type | Description |
   *    | --- | --- | --- |
   *    | json | Object | Data as JSON format (@see [data․json](Options.html#.data%25E2%2580%25A4json)) |
   *    | rows | Array | Data in array as row format (@see [data․rows](Options.html#.data%25E2%2580%25A4json)) |
   *    | columns | Array | Data in array as column format (@see [data․columns](Options.html#.data%25E2%2580%25A4columns)) |
   *    | to | String | The lower x edge will move to that point. If not given, the lower x edge will move by the number of given data points |
   *    | length | Number | The lower x edge will move by the number of this argument |
   *    | duration | Number | The duration of the transition will be specified value. If not given, transition.duration will be used as default |
   *    | done | Function | The specified function will be called when flow ends |
   *
   * - **NOTE:**
   *   - If json, rows and columns given, the data will be loaded.
   *   - If data that has the same target id is given, the chart will be appended.
   *   - Otherwise, new target will be added. One of these is required when calling.
   *   - If json specified, keys is required as well as data.json.
   * 	 - If tab isn't visible(by evaluating `document.hidden`), will not be executed to prevent unnecessary work.
   * @example
   * // 2 data points will be apprended to the tail and popped from the head.
   * // After that, 4 data points will be appended and no data points will be poppoed.
   * chart.flow({
   *  columns: [
   *    ["x", "2018-01-11", "2018-01-21"],
   *    ["data1", 500, 200],
   *    ["data2", 100, 300],
   *    ["data3", 200, 120]
   *  ],
   *  to: "2013-01-11",
   *  done: function () {
   *    chart.flow({
   *      columns: [
   *        ["x", "2018-02-11", "2018-02-12", "2018-02-13", "2018-02-14"],
   *        ["data1", 200, 300, 100, 250],
   *        ["data2", 100, 90, 40, 120],
   *        ["data3", 100, 100, 300, 500]
   *      ],
   *      length: 2,
      *      duration: 1500
   *    });
   *  }
   * });
   */
flow:function(args){var data,domain,diff,to,$$=this.internal,length=0,tail=0;if((args.json||args.rows||args.columns)&&(data=$$.convertData(args)),data&&$$.isTabVisible()){var notfoundIds=[],orgDataCount=$$.getMaxDataCount(),targets=$$.convertDataToTargets(data,!0);$$.data.targets.forEach((function(t){for(var found=!1,i=0;i<targets.length;i++)if(t.id===targets[i].id){found=!0,t.values[t.values.length-1]&&(tail=t.values[t.values.length-1].index+1),length=targets[i].values.length;for(var _j3=0;_j3<length;_j3++)targets[i].values[_j3].index=tail+_j3,$$.isTimeSeries()||(targets[i].values[_j3].x=tail+_j3);t.values=t.values.concat(targets[i].values),targets.splice(i,1);break}found||notfoundIds.push(t.id)})),$$.data.targets.forEach((function(t){for(var _i=0;_i<notfoundIds.length;_i++)if(t.id===notfoundIds[_i]){tail=t.values[t.values.length-1].index+1;for(var _j4=0;_j4<length;_j4++)t.values.push({id:t.id,index:tail+_j4,x:$$.isTimeSeries()?$$.getOtherTargetX(tail+_j4):tail+_j4,value:null})}})),$$.data.targets.length&&targets.forEach((function(t){for(var missing=[],i=$$.data.targets[0].values[0].index;i<tail;i++)missing.push({id:t.id,index:i,x:$$.isTimeSeries()?$$.getOtherTargetX(i):i,value:null});t.values.forEach((function(v){v.index+=tail,$$.isTimeSeries()||(v.x+=tail)})),t.values=missing.concat(t.values)})),$$.data.targets=$$.data.targets.concat(targets);
// add remained
// check data count because behavior needs to change when it"s only one
// const dataCount = $$.getMaxDataCount();
var baseTarget=$$.data.targets[0],baseValue=baseTarget.values[0];isDefined(args.to)?(length=0,to=$$.isTimeSeries()?$$.parseDate(args.to):args.to,baseTarget.values.forEach((function(v){v.x<to&&length++}))):isDefined(args.length)&&(length=args.length),orgDataCount?1===orgDataCount&&$$.isTimeSeries()&&(diff=(baseTarget.values[baseTarget.values.length-1].x-baseValue.x)/2,domain=[new Date(+baseValue.x-diff),new Date(+baseValue.x+diff)]):(diff=$$.isTimeSeries()?baseTarget.values.length>1?baseTarget.values[baseTarget.values.length-1].x-baseValue.x:baseValue.x-$$.getXDomain($$.data.targets)[0]:1,domain=[baseValue.x-diff,baseValue.x]),domain&&$$.updateXDomain(null,!0,!0,!1,domain),$$.updateTargets($$.data.targets),$$.redraw({flow:{index:baseValue.index,length:length,duration:isValue(args.duration)?args.duration:$$.config.transition_duration,done:args.done,orgDataCount:orgDataCount},withLegend:!0,withTransition:orgDataCount>1,withTrimXDomain:!1,withUpdateXAxis:!0})}}}),extend(ChartInternal_ChartInternal.prototype,{
/**
   * Generate flow
   * @memberof ChartInternal
   * @private
   * @param {Object} args
   * @return {Function}
   */
generateFlow:function(args){var $$=this,config=$$.config;return function(){var translateX,targets=args.targets,flow=args.flow,_args$shape$type=args.shape.type,drawBar=_args$shape$type.bar,drawLine=_args$shape$type.line,drawArea=_args$shape$type.area,_args$shape$pos=args.shape.pos,cx=_args$shape$pos.cx,cy=_args$shape$pos.cy,xForText=_args$shape$pos.xForText,yForText=_args$shape$pos.yForText,xv=args.xv,duration=args.duration,scaleX=1,flowIndex=flow.index,flowLength=flow.length,flowStart=$$.getValueOnIndex($$.data.targets[0].values,flowIndex),flowEnd=$$.getValueOnIndex($$.data.targets[0].values,flowIndex+flowLength),orgDomain=$$.x.domain(),durationForFlow=flow.duration||duration,done=flow.done||function(){},wait=$$.generateWait(),xgrid=$$.xgrid||Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["selectAll"])([]),xgridLines=$$.xgridLines||Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["selectAll"])([]),mainRegion=$$.mainRegion||Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["selectAll"])([]),mainText=$$.mainText||Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["selectAll"])([]),mainBar=$$.mainBar||Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["selectAll"])([]),mainLine=$$.mainLine||Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["selectAll"])([]),mainArea=$$.mainArea||Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["selectAll"])([]),mainCircle=$$.mainCircle||Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["selectAll"])([]);$$.flowing=!0,$$.data.targets.forEach((function(d){d.values.splice(0,flowLength)}));
// update x domain to generate axis elements for flow
var domain=$$.updateXDomain(targets,!0,!0);// update elements related to x scale
$$.updateXGrid&&$$.updateXGrid(!0),flow.orgDataCount?translateX=1===flow.orgDataCount||(flowStart&&flowStart.x)===(flowEnd&&flowEnd.x)||$$.isTimeSeries()?$$.x(orgDomain[0])-$$.x(domain[0]):$$.x(flowStart.x)-$$.x(flowEnd.x):1===$$.data.targets[0].values.length?$$.isTimeSeries()?(flowStart=$$.getValueOnIndex($$.data.targets[0].values,0),flowEnd=$$.getValueOnIndex($$.data.targets[0].values,$$.data.targets[0].values.length-1),translateX=$$.x(flowStart.x)-$$.x(flowEnd.x)):translateX=diffDomain(domain)/2:translateX=$$.x(orgDomain[0])-$$.x(domain[0]),scaleX=diffDomain(orgDomain)/diffDomain(domain);var transform="translate(".concat(translateX,",0) scale(").concat(scaleX,",1)");$$.hideGridFocus();var gt=Object(external_commonjs_d3_transition_commonjs2_d3_transition_amd_d3_transition_root_d3_["transition"])().ease(external_commonjs_d3_ease_commonjs2_d3_ease_amd_d3_ease_root_d3_["easeLinear"]).duration(durationForFlow);wait.add([$$.axes.x.transition(gt).call((function(g){return $$.xAxis.setTransition(gt).create(g)})),mainBar.transition(gt).attr("transform",transform),mainLine.transition(gt).attr("transform",transform),mainArea.transition(gt).attr("transform",transform),mainCircle.transition(gt).attr("transform",transform),mainText.transition(gt).attr("transform",transform),mainRegion.filter($$.isRegionOnX).transition(gt).attr("transform",transform),xgrid.transition(gt).attr("transform",transform),xgridLines.transition(gt).attr("transform",transform)]),gt.call(wait,(function(){var isRotated=config.axis_rotated;// remove flowed elements
if(flowLength){for(var target={shapes:[],texts:[],eventRects:[]},i=0;i<flowLength;i++)target.shapes.push(".".concat(config_classes.shape,"-").concat(i)),target.texts.push(".".concat(config_classes.text,"-").concat(i)),target.eventRects.push(".".concat(config_classes.eventRect,"-").concat(i));["shapes","texts","eventRects"].forEach((function(v){$$.svg.selectAll(".".concat(config_classes[v])).selectAll(target[v]).remove()})),$$.svg.select(".".concat(config_classes.xgrid)).remove()}// draw again for removing flowed elements and reverting attr
if(xgrid.size()&&xgrid.attr("transform",null).attr($$.xgridAttr),xgridLines.attr("transform",null),xgridLines.select("line").attr("x1",isRotated?0:xv).attr("x2",isRotated?$$.width:xv),xgridLines.select("text").attr("x",isRotated?$$.width:0).attr("y",xv),mainBar.attr("transform",null).attr("d",drawBar),mainLine.attr("transform",null).attr("d",drawLine),mainArea.attr("transform",null).attr("d",drawArea),mainCircle.attr("transform",null),$$.isCirclePoint())mainCircle.attr("cx",cx).attr("cy",cy);else{var xFunc=function(d){return cx(d)-config.point_r},yFunc=function(d){return cy(d)-config.point_r};mainCircle.attr("x",xFunc).attr("y",yFunc).attr("cx",cx).attr("cy",cy)}mainText.attr("transform",null).attr("x",xForText).attr("y",yForText).style("fill-opacity",$$.opacityForText.bind($$)),mainRegion.attr("transform",null),mainRegion.select("rect").filter($$.isRegionOnX).attr("x",$$.regionX.bind($$)).attr("width",$$.regionWidth.bind($$)),config.interaction_enabled&&$$.redrawEventRect(),done(),$$.flowing=!1}))}}}),
// CONCATENATED MODULE: ./src/api/api.selection.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(Chart_Chart.prototype,{
/**
   * Get selected data points.<br><br>
   * By this API, you can get selected data points information. To use this API, data.selection.enabled needs to be set true.
   * @method selected
   * @instance
   * @memberof Chart
   * @param {String} [targetId] You can filter the result by giving target id that you want to get. If not given, all of data points will be returned.
   * @return {Array} dataPoint Array of the data points.<br>ex.) `[{x: 1, value: 200, id: "data1", index: 1, name: "data1"}, ...]`
   * @example
   *  // all selected data points will be returned.
   *  chart.selected();
   *  // --> ex.) [{x: 1, value: 200, id: "data1", index: 1, name: "data1"}, ... ]
   *
   *  // all selected data points of data1 will be returned.
   *  chart.selected("data1");
   */
selected:function(targetId){var $$=this.internal,dataPoint=[];return $$.main.selectAll(".".concat(config_classes.shapes+$$.getTargetSelectorSuffix(targetId))).selectAll(".".concat(config_classes.shape)).filter((function(){return Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this).classed(config_classes.SELECTED)})).each((function(d){return dataPoint.push(d)})),dataPoint},
/**
   * Set data points to be selected. (`[data.selection.enabled](Options.html#.data%25E2%2580%25A4selection%25E2%2580%25A4enabled) option should be set true to use this method)`
   * @method select
   * @instance
   * @memberof Chart
   * @param {String|Array} [ids] id value to get selected.
   * @param {Array} [indices] The index array of data points. If falsy value given, will select all data points.
   * @param {Boolean} [resetOther] Unselect already selected.
   * @example
   *  // select all data points
   *  chart.select();
   *
   *  // select all from 'data2'
   *  chart.select("data2");
   *
   *  // select all from 'data1' and 'data2'
   *  chart.select(["data1", "data2"]);
   *
   *  // select from 'data1', indices 2 and unselect others selected
   *  chart.select("data1", [2], true);
   *
   *  // select from 'data1', indices 0, 3 and 5
   *  chart.select("data1", [0, 3, 5]);
   */
select:function(ids,indices,resetOther){var $$=this.internal,config=$$.config;config.data_selection_enabled&&$$.main.selectAll(".".concat(config_classes.shapes)).selectAll(".".concat(config_classes.shape)).each((function(d,i){var shape=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this),id=d.data?d.data.id:d.id,toggle=$$.getToggle(this,d).bind($$),isTargetId=config.data_selection_grouped||!ids||ids.indexOf(id)>=0,isTargetIndex=!indices||indices.indexOf(i)>=0,isSelected=shape.classed(config_classes.SELECTED);
// line/area selection not supported yet
shape.classed(config_classes.line)||shape.classed(config_classes.area)||(isTargetId&&isTargetIndex?config.data_selection_isselectable(d)&&!isSelected&&toggle(!0,shape.classed(config_classes.SELECTED,!0),d,i):isDefined(resetOther)&&resetOther&&isSelected&&toggle(!1,shape.classed(config_classes.SELECTED,!1),d,i))}))},
/**
   * Set data points to be un-selected.
   * @method unselect
   * @instance
   * @memberof Chart
   * @param {String|Array} [ids] id value to be unselected.
   * @param {Array} [indices] The index array of data points. If falsy value given, will select all data points.
   * @example
   *  // unselect all data points
   *  chart.unselect();
   *
   *  // unselect all from 'data1'
   *  chart.unselect("data1");
   *
   *  // unselect from 'data1', indices 2
   *  chart.unselect("data1", [2]);
   */
unselect:function(ids,indices){var $$=this.internal,config=$$.config;config.data_selection_enabled&&$$.main.selectAll(".".concat(config_classes.shapes)).selectAll(".".concat(config_classes.shape)).each((function(d,i){var shape=Object(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["select"])(this),id=d.data?d.data.id:d.id,toggle=$$.getToggle(this,d).bind($$),isTargetId=config.data_selection_grouped||!ids||ids.indexOf(id)>=0,isTargetIndex=!indices||indices.indexOf(i)>=0,isSelected=shape.classed(config_classes.SELECTED);
// line/area selection not supported yet
shape.classed(config_classes.line)||shape.classed(config_classes.area)||isTargetId&&isTargetIndex&&config.data_selection_isselectable(d)&&isSelected&&toggle(!1,shape.classed(config_classes.SELECTED,!1),d,i)}))}}),extend(Chart_Chart.prototype,{
/**
   * Change the type of the chart.
   * @method transform
   * @instance
   * @memberof Chart
   * @param {String} type Specify the type to be transformed. The types listed in data.type can be used.
   * @param {String|Array} targetIds Specify targets to be transformed. If not given, all targets will be the candidate.
   * @example
   *  // all targets will be bar chart.
   *  chart.transform("bar");
   *
   *  // only data1 will be bar chart.
   *  chart.transform("bar", "data1");
   *
   *  // only data1 and data2 will be bar chart.
   *  chart.transform("bar", ["data1", "data2"]);
   */
transform:function(type,targetIds){var $$=this.internal,options=["pie","donut"].indexOf(type)>=0?{withTransform:!0}:null;transformTo.bind($$)(targetIds,type,options)}}),
// CONCATENATED MODULE: ./src/api/api.group.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(Chart_Chart.prototype,{
/**
   * Update groups for the targets.
   * @method groups
   * @instance
   * @memberof Chart
   * @param {Array} groups This argument needs to be an Array that includes one or more Array that includes target ids to be grouped.
   * @return {Array} Grouped data names array
   * @example
   *  // data1 and data2 will be a new group.
   *  chart.groups([
   *     ["data1", "data2"]
   *  ]);
   */
groups:function(_groups){var $$=this.internal,config=$$.config;return isUndefined(_groups)||(config.data_groups=_groups,$$.redraw()),config.data_groups}});
// CONCATENATED MODULE: ./src/api/api.grid.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
/**
 * Update x grid lines.
 * @method xgrids
 * @instance
 * @memberof Chart
 * @param {Array} grids X grid lines will be replaced with this argument. The format of this argument is the same as grid.x.lines.
 * @example
 *  // Show 2 x grid lines
 * chart.xgrids([
 *    {value: 1, text: "Label 1"},
 *    {value: 4, text: "Label 4"}
 * ]);
 */
var xgrids=function(grids){var $$=this.internal,config=$$.config;return grids?(config.grid_x_lines=grids,$$.redrawWithoutRescale(),config.grid_x_lines):config.grid_x_lines};extend(xgrids,{
/**
   * Add x grid lines.<br>
   * This API adds new x grid lines instead of replacing like xgrids.
   * @method xgrids․add
   * @instance
   * @memberof Chart
   * @param {Array|Object} grids New x grid lines will be added. The format of this argument is the same as grid.x.lines and it's possible to give an Object if only one line will be added.
   * @example
   *  // Add a new x grid line
   * chart.xgrids.add(
   *   {value: 4, text: "Label 4"}
   * );
   *
   * // Add new x grid lines
   * chart.xgrids.add([
   *   {value: 2, text: "Label 2"},
   *   {value: 4, text: "Label 4"}
   * ]);
   */
add:function(grids){return this.xgrids(this.internal.config.grid_x_lines.concat(grids||[]))},
/**
   * Remove x grid lines.<br>
   * This API removes x grid lines.
   * @method xgrids․remove
   * @instance
   * @memberof Chart
   * @param {Object} params This argument should include value or class. If value is given, the x grid lines that have specified x value will be removed. If class is given, the x grid lines that have specified class will be removed. If args is not given, all of x grid lines will be removed.
   * @example
   * // x grid line on x = 2 will be removed
   * chart.xgrids.remove({value: 2});
   *
   * // x grid lines that have 'grid-A' will be removed
   * chart.xgrids.remove({
   *   class: "grid-A"
   * });
   *
   * // all of x grid lines will be removed
   * chart.xgrids.remove();
   */
remove:function(params){
// TODO: multiple
this.internal.removeGridLines(params,!0)}});
/**
 * Update y grid lines.
 * @method ygrids
 * @instance
 * @memberof Chart
 * @param {Array} grids Y grid lines will be replaced with this argument. The format of this argument is the same as grid.y.lines.
 * @example
 *  // Show 2 y grid lines
 * chart.ygrids([
 *    {value: 100, text: "Label 1"},
 *    {value: 400, text: "Label 4"}
 * ]);
 */
var ygrids=function(grids){var $$=this.internal,config=$$.config;return grids?(config.grid_y_lines=grids,$$.redrawWithoutRescale(),config.grid_y_lines):config.grid_y_lines};extend(ygrids,{
/**
   * Add y grid lines.<br>
   * This API adds new y grid lines instead of replacing like ygrids.
   * @method ygrids․add
   * @instance
   * @memberof Chart
   * @param {Array|Object} grids New y grid lines will be added. The format of this argument is the same as grid.y.lines and it's possible to give an Object if only one line will be added.
   * @example
   *  // Add a new x grid line
   * chart.ygrids.add(
   *   {value: 400, text: "Label 4"}
   * );
   *
   * // Add new x grid lines
   * chart.ygrids.add([
   *   {value: 200, text: "Label 2"},
   *   {value: 400, text: "Label 4"}
   * ]);
   */
add:function(grids){return this.ygrids(this.internal.config.grid_y_lines.concat(grids||[]))},
/**
   * Remove y grid lines.<br>
   * This API removes x grid lines.
   * @method ygrids․remove
   * @instance
   * @memberof Chart
   * @param {Object} params This argument should include value or class. If value is given, the y grid lines that have specified y value will be removed. If class is given, the y grid lines that have specified class will be removed. If args is not given, all of y grid lines will be removed.
   * @example
   * // y grid line on y = 200 will be removed
   * chart.ygrids.remove({value: 200});
   *
   * // y grid lines that have 'grid-A' will be removed
   * chart.ygrids.remove({
   *   class: "grid-A"
   * });
   *
   * // all of y grid lines will be removed
   * chart.ygrids.remove();
   */
remove:function(params){
// TODO: multiple
this.internal.removeGridLines(params,!1)}}),extend(Chart_Chart.prototype,{xgrids:xgrids,ygrids:ygrids});
// CONCATENATED MODULE: ./src/api/api.region.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
/**
 * Update regions.
 * @method regions
 * @instance
 * @memberof Chart
 * @param {Array} regions Regions will be replaced with this argument. The format of this argument is the same as regions.
 * @return {Array} regions
 * @example
 * // Show 2 regions
 * chart.regions([
 *    {axis: "x", start: 5, class: "regionX"},
 *    {axis: "y", end: 50, class: "regionY"}
 * ]);
 */
var api_region_regions=function(_regions){var $$=this.internal,config=$$.config;return _regions?(config.regions=_regions,$$.redrawWithoutRescale(),_regions):config.regions};extend(api_region_regions,{
/**
   * Add new region.<br><br>
   * This API adds new region instead of replacing like regions.
   * @method regions․add
   * @instance
   * @memberof Chart
   * @param {Array|Object} regions New region will be added. The format of this argument is the same as regions and it's possible to give an Object if only one region will be added.
   * @return {Array} regions
   * @example
   * // Add a new region
   * chart.regions.add(
   *    {axis: "x", start: 5, class: "regionX"}
   * );
   *
   * // Add new regions
   * chart.regions.add([
   *    {axis: "x", start: 5, class: "regionX"},
   *    {axis: "y", end: 50, class: "regionY"}
   *]);
   */
add:function(regions){var $$=this.internal,config=$$.config;return regions?(config.regions=config.regions.concat(regions),$$.redrawWithoutRescale(),config.regions):config.regions},
/**
   * Remove regions.<br><br>
   * This API removes regions.
   * @method regions․remove
   * @instance
   * @memberof Chart
   * @param {Object} regions This argument should include classes. If classes is given, the regions that have one of the specified classes will be removed. If args is not given, all of regions will be removed.
   * @return {Array} regions Removed regions
   * @example
   * // regions that have 'region-A' or 'region-B' will be removed.
   * chart.regions.remove({
   *   classes: [
   *     "region-A", "region-B"
   *   ]
   * });
   *
   * // all of regions will be removed.
   * chart.regions.remove();
   */
remove:function(optionsValue){var $$=this.internal,config=$$.config,options=optionsValue||{},duration=getOption(options,"duration",config.transition_duration),classes=getOption(options,"classes",[config_classes.region]),regions=$$.main.select(".".concat(config_classes.regions)).selectAll(classes.map((function(c){return".".concat(c)})));return(duration?regions.transition().duration(duration):regions).style("opacity","0").remove(),regions=config.regions,Object.keys(options).length?(regions=regions.filter((function(region){var found=!1;return!region["class"]||(region["class"].split(" ").forEach((function(c){classes.indexOf(c)>=0&&(found=!0)})),!found)})),config.regions=regions):config.regions=[],regions}}),extend(Chart_Chart.prototype,{regions:api_region_regions});
// CONCATENATED MODULE: ./src/api/api.data.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
/**
 * Get data loaded in the chart.
 * @method data
 * @instance
 * @memberof Chart
 * @param {String|Array} targetIds If this argument is given, this API returns the specified target data. If this argument is not given, all of data will be returned.
 * @return {Array} Data objects
 * @example
 * // Get only data1 data
 * chart.data("data1");
 * // --> [{id: "data1", id_org: "data1", values: Array(6)}, ...]
 *
 * // Get data1 and data2 data
 * chart.data(["data1", "data2"]);
 *
 * // Get all data
 * chart.data();
 */
var api_data_data=function(targetIds){var targets=this.internal.data.targets;if(!isUndefined(targetIds)){var ids=isArray(targetIds)?targetIds:[targetIds];return targets.filter((function(t){return ids.some((function(v){return v===t.id}))}))}return targets};extend(api_data_data,{
/**
   * Get data shown in the chart.
   * @method data․shown
   * @instance
   * @memberof Chart
   * @param {String|Array} targetIds If this argument is given, this API filters the data with specified target ids. If this argument is not given, all shown data will be returned.
   * @return {Array} Data objects
   * @example
   * // Get shown data by filtering to include only data1 data
   * chart.data.shown("data1");
   * // --> [{id: "data1", id_org: "data1", values: Array(6)}, ...]
   *
   * // Get shown data by filtering to include data1 and data2 data
   * chart.data.shown(["data1", "data2"]);
   *
   * // Get all shown data
   * chart.data.shown();
   */
shown:function(targetIds){return this.internal.filterTargetsToShow(this.data(targetIds))},
/**
   * Get values of the data loaded in the chart.
   * @method data․values
   * @instance
   * @memberof Chart
   * @param {String|Array} targetIds This API returns the values of specified target. If this argument is not given, null will be retruned
   * @return {Array} Data values
   * @example
   * // Get data1 values
   * chart.data.values("data1");
   * // --> [10, 20, 30, 40]
   */
values:function(targetId){var flat=!(arguments.length>1&&void 0!==arguments[1])||arguments[1],values=null;if(targetId){var targets=this.data(targetId);targets&&isArray(targets)&&(values=[],targets.forEach((function(v){var dataValue=v.values.map((function(d){return d.value}));flat?values=values.concat(dataValue):values.push(dataValue)})))}return values},
/**
   * Get and set names of the data loaded in the chart.
   * @method data․names
   * @instance
   * @memberof Chart
   * @param {Object} names If this argument is given, the names of data will be updated. If not given, the current names will be returned. The format of this argument is the same as
   * @return {Object} Corresponding names according its key value, if specified names values.
   * @example
   * // Get current names
   * chart.data.names();
   * // --> {data1: "test1", data2: "test2"}
   *
   * // Update names
   * chart.data.names({
   *  data1: "New Name 1",
   *  data2: "New Name 2"
   *});
   */
names:function(_names){return this.internal.clearLegendItemTextBoxCache(),this.internal.updateDataAttributes("names",_names)},
/**
   * Get and set colors of the data loaded in the chart.
   * @method data․colors
   * @instance
   * @memberof Chart
   * @param {Object} colors If this argument is given, the colors of data will be updated. If not given, the current colors will be returned. The format of this argument is the same as [data.colors](./Options.html#.data%25E2%2580%25A4colors).
   * @return {Object} Corresponding data color value according its key value.
   * @example
   * // Get current colors
   * chart.data.colors();
   * // --> {data1: "#00c73c", data2: "#fa7171"}
   *
   * // Update colors
   * chart.data.colors({
   *  data1: "#FFFFFF",
   *  data2: "#000000"
   * });
   */
colors:function(_colors){return this.internal.updateDataAttributes("colors",_colors)},
/**
   * Get and set axes of the data loaded in the chart.
   * - **NOTE:** If all data is related to one of the axes, the domain of axis without related data will be replaced by the domain from the axis with related data
   * @method data․axes
   * @instance
   * @memberof Chart
   * @param {Object} axes If this argument is given, the axes of data will be updated. If not given, the current axes will be returned. The format of this argument is the same as
   * @return {Object} Corresponding axes value for data, if specified axes value.
   * @example
   * // Get current axes
   * chart.data.axes();
   * // --> {data1: "y"}
   *
   * // Update axes
   * chart.data.axes({
   *  data1: "y",
   *  data2: "y2"
   * });
   */
axes:function(_axes){return this.internal.updateDataAttributes("axes",_axes)},
/**
   * Get the minimum data value bound to the chart
   * @method data․min
   * @instance
   * @memberof Chart
   * @return {Array} Data objects
   * @example
   * // Get current axes
   * chart.data.min();
   * // --> [{x: 0, value: 30, id: "data1", index: 0}, ...]
   */
min:function(){return this.internal.getMinMaxData().min},
/**
   * Get the maximum data value bound to the chart
   * @method data․max
   * @instance
   * @memberof Chart
   * @return {Array} Data objects
   * @example
   * // Get current axes
   * chart.data.max();
   * // --> [{x: 3, value: 400, id: "data1", index: 3}, ...]
   */
max:function(){return this.internal.getMinMaxData().max}}),extend(Chart_Chart.prototype,{data:api_data_data}),
// CONCATENATED MODULE: ./src/api/api.category.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(Chart_Chart.prototype,{
/**
   * Set specified category name on category axis.
   * @method category
   * @instance
   * @memberof Chart
   * @param {Number} i index of category to be changed
   * @param {String} category category value to be changed
   * @example
   * chart.category(2, "Category 3");
   */
category:function(i,_category){var $$=this.internal,config=$$.config;return arguments.length>1&&(config.axis_x_categories[i]=_category,$$.redraw()),config.axis_x_categories[i]},
/**
   * Set category names on category axis.
   * @method categories
   * @instance
   * @memberof Chart
   * @param {Array} categories This must be an array that includes category names in string. If category names are included in the date by data.x option, this is not required.
   * @example
   * chart.categories([
   *      "Category 1", "Category 2", ...
   * ]);
   */
categories:function(_categories){var $$=this.internal,config=$$.config;return arguments.length?(config.axis_x_categories=_categories,$$.redraw(),config.axis_x_categories):config.axis_x_categories}}),
// CONCATENATED MODULE: ./src/api/api.color.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(Chart_Chart.prototype,{
/**
   * Get the color
   * @method color
   * @instance
   * @memberof Chart
   * @param {String} id id to get the color
   * @example
   * chart.color("data1");
   */
color:function(id){return this.internal.color(id);// more patterns
}}),
// CONCATENATED MODULE: ./src/api/api.x.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(Chart_Chart.prototype,{
/**
   * Get and set x values for the chart.
   * @method x
   * @instance
   * @memberof Chart
   * @param {Array} x If x is given, x values of every target will be updated. If no argument is given, current x values will be returned as an Object whose keys are the target ids.
   * @return {Object} xs
   * @example
   *  // Get current x values
   *  chart.x();
   *
   *  // Update x values for all targets
   *  chart.x([100, 200, 300, 400, ...]);
   */
x:function(_x){var $$=this.internal,isCategorized=$$.isCustomX()&&$$.isCategorized();return isArray(_x)&&(isCategorized?$$.api.categories(_x):($$.updateTargetX($$.data.targets,_x),$$.redraw({withUpdateOrgXDomain:!0,withUpdateXDomain:!0}))),isCategorized?$$.api.categories():$$.data.xs},
/**
   * Get and set x values for the chart.
   * @method xs
   * @instance
   * @memberof Chart
   * @param {Array} xs If xs is given, specified target's x values will be updated. If no argument is given, current x values will be returned as an Object whose keys are the target ids.
   * @return {Object} xs
   * @example
   *  // Get current x values
   *  chart.xs();
   *
   *  // Update x values for all targets
   *  chart.xs({
   *    data1: [10, 20, 30, 40, ...],
   *    data2: [100, 200, 300, 400, ...]
   *  });
   */
xs:function(_xs){var $$=this.internal;return isObject(_xs)&&($$.updateTargetXs($$.data.targets,_xs),$$.redraw({withUpdateOrgXDomain:!0,withUpdateXDomain:!0})),$$.data.xs}});
// CONCATENATED MODULE: ./src/api/api.axis.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
/**
 * Set the min/max value
 * @param {Chart} $$
 * @param {String} type
 * @param {Object} value
 * @return {undefined}
 * @private
 */
var setMinMax=function($$,type,value){var config=$$.config,axisX="axis_x_".concat(type),axisY="axis_y_".concat(type),axisY2="axis_y2_".concat(type);isDefined(value)&&(isObjectType(value)?(isValue(value.x)&&(config[axisX]=value.x),isValue(value.y)&&(config[axisY]=value.y),isValue(value.y2)&&(config[axisY2]=value.y2)):(config[axisY]=value,config[axisY2]=value),$$.redraw({withUpdateOrgXDomain:!0,withUpdateXDomain:!0}))},api_axis_getMinMax=function($$,type){var config=$$.config;return{x:config["axis_x_".concat(type)],y:config["axis_y_".concat(type)],y2:config["axis_y2_".concat(type)]}},api_axis_axis=extend((function(){}),{
/**
   * Get and set axis labels.
   * @method axis․labels
   * @instance
   * @memberof Chart
   * @param {Object} labels specified axis' label to be updated.
   * @example
   * // Update axis' label
   * chart.axis.labels({
   *   x: "New X Axis Label",
   *   y: "New Y Axis Label"
   * });
   */
labels:function(_labels){var $$=this.internal;arguments.length&&(Object.keys(_labels).forEach((function(axisId){$$.axis.setLabelText(axisId,_labels[axisId])})),$$.axis.updateLabels())},
/**
   * Get and set axis min value.
   * @method axis․min
   * @instance
   * @memberof Chart
   * @param {Object} min If min is given, specified axis' min value will be updated.<br>
   *     If no argument is given, the min values set on generating option for each axis will be returned.
   *     If not set any min values on generation, it will return `undefined`.
   * @example
   * // Update axis' min
   * chart.axis.min({
   *   x: -10,
   *   y: 1000,
   *   y2: 100
   * });
   */
min:function(_min){var $$=this.internal;return arguments.length?setMinMax($$,"min",_min):api_axis_getMinMax($$,"min")},
/**
   * Get and set axis max value.
   * @method axis․max
   * @instance
   * @memberof Chart
   * @param {Object} max If max is given, specified axis' max value will be updated.<br>
   *     If no argument is given, the max values set on generating option for each axis will be returned.
   *     If not set any max values on generation, it will return `undefined`.
   * @example
   * // Update axis' label
   * chart.axis.max({
   *    x: 100,
   *    y: 1000,
   *    y2: 10000
   * });
   */
max:function(_max){var $$=this.internal;return arguments.length?setMinMax($$,"max",_max):api_axis_getMinMax($$,"max")},
/**
   * Get and set axis min and max value.
   * @method axis․range
   * @instance
   * @memberof Chart
   * @param {Object} range If range is given, specified axis' min and max value will be updated. If no argument is given, the current min and max values for each axis will be returned.
   * @example
   * // Update axis' label
   * chart.axis.range({
   *   min: {
   *     x: -10,
   *     y: -1000,
   *     y2: -10000
   *   },
   *   max: {
   *     x: 100,
   *     y: 1000,
   *     y2: 10000
   *   },
   * });
   */
range:function(_range){var axis=this.axis;if(!arguments.length)return{max:axis.max(),min:axis.min()};isDefined(_range.max)&&axis.max(_range.max),isDefined(_range.min)&&axis.min(_range.min)}});
/**
 * Get the min/max value
 * @param {Chart} $$
 * @param {String} type
 * @return {{x, y, y2}}
 * @private
 */extend(Chart_Chart.prototype,{axis:api_axis_axis});
// CONCATENATED MODULE: ./src/api/api.legend.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
/**
 * Define legend
 * @ignore
 */
var legend=extend((function(){}),{
/**
   * Show legend for each target.
   * @method legend․show
   * @instance
   * @memberof Chart
   * @param {String|Array} targetIds
   * - If targetIds is given, specified target's legend will be shown.
   * - If only one target is the candidate, String can be passed.
   * - If no argument is given, all of target's legend will be shown.
   * @example
   * // Show legend for data1.
   * chart.legend.show("data1");
   *
   * // Show legend for data1 and data2.
   * chart.legend.show(["data1", "data2"]);
   *
   * // Show all legend.
   * chart.legend.show();
   */
show:function(targetIds){var $$=this.internal;$$.showLegend($$.mapToTargetIds(targetIds)),$$.updateAndRedraw({withLegend:!0})},
/**
   * Hide legend for each target.
   * @method legend․hide
   * @instance
   * @memberof Chart
   * @param {String|Array} targetIds
   * - If targetIds is given, specified target's legend will be hidden.
   * - If only one target is the candidate, String can be passed.
   * - If no argument is given, all of target's legend will be hidden.
   * @example
   * // Hide legend for data1.
   * chart.legend.hide("data1");
   *
   * // Hide legend for data1 and data2.
   * chart.legend.hide(["data1", "data2"]);
   *
   * // Hide all legend.
   * chart.legend.hide();
   */
hide:function(targetIds){var $$=this.internal;$$.hideLegend($$.mapToTargetIds(targetIds)),$$.updateAndRedraw({withLegend:!0})}});extend(Chart_Chart.prototype,{legend:legend}),
// CONCATENATED MODULE: ./src/api/api.chart.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
extend(Chart_Chart.prototype,{
/**
   * Resize the chart.
   * @method resize
   * @instance
   * @memberof Chart
   * @param {Object} size This argument should include width and height in pixels.
   * @example
   * // Resize to 640x480
   * chart.resize({
   *    width: 640,
   *    height: 480
   * });
   */
resize:function(size){var $$=this.internal,config=$$.config;$$.rendered&&(config.size_width=size?size.width:null,config.size_height=size?size.height:null,this.flush(!1,!0),$$.resizeFunction())},
/**
   * Force to redraw.
   * @method flush
   * @instance
   * @memberof Chart
   * @param {Boolean} [soft] For soft redraw.
   * @example
   * chart.flush();
   *
   * // for soft redraw
   * chart.flush(true);
   */
flush:function(soft,_isFromResize){var $$=this.internal;$$.rendered?(_isFromResize?$$.brush&&$$.brush.updateResize():$$.axis&&$$.axis.setOrient(),$$.zoomScale=null,soft?$$.redraw({withTransform:!0,withUpdateXDomain:!0,withUpdateOrgXDomain:!0,withLegend:!0}):$$.updateAndRedraw({withLegend:!0,withTransition:!1,withTransitionForTransform:!1})):$$.initToRender(!0)},
/**
   * Reset the chart object and remove element and events completely.
   * @method destroy
   * @instance
   * @memberof Chart
   * @example
   * chart.destroy();
   */
destroy:function(){var _this=this,$$=this.internal;return notEmpty($$)&&($$.callPluginHook("$willDestroy"),$$.charts.splice($$.charts.indexOf(this),1),$$.svg.select("*").interrupt(),$$.generateResize.timeout&&win.clearTimeout($$.generateResize.timeout),win.removeEventListener("resize",$$.resizeFunction),$$.selectChart.classed("bb",!1).html(""),Object.keys(this).forEach((function(key){"internal"===key&&Object.keys($$).forEach((function(k){$$[k]=null})),_this[key]=null,delete _this[key]}))),null},
/**
   * Get or set single config option value.
   * @method config
   * @instance
   * @memberof Chart
   * @param {String} name The option key name.
   * @param {*} [value] The value accepted for indicated option.
   * @param {Boolean} [redraw] Set to redraw with the new option changes.
   * - **NOTE:** Doesn't guarantee work in all circumstances. It can be applied for limited options only.
   * @example
   * // Getter
   * chart.config("gauge.max");
   *
   * // Setter
   * chart.config("gauge.max", 100);
   *
   * // Setter & redraw with the new option
   * chart.config("gauge.max", 100, true);
   */
config:function(name,value,redraw){var res,$$=this.internal,key=name&&name.replace(/\./g,"_");return key in $$.config&&(isDefined(value)?($$.config[key]=value,res=value,redraw&&this.flush()):res=$$.config[key]),res}});
// CONCATENATED MODULE: ./src/api/api.tooltip.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
/**
 * Define tooltip
 * @ignore
 */
var api_tooltip_tooltip=extend((function(){}),{
/**
   * Show tooltip
   * @method tooltip․show
   * @instance
   * @memberof Chart
   * @param {Object} args The object can consist with following members:<br>
   *
   *    | Key | Type | Description |
   *    | --- | --- | --- |
   *    | index | Number | Determine focus by index |
   *    | x | Number &vert; Date | Determine focus by x Axis index |
   *    | mouse | Array | Determine x and y coordinate value relative the targeted '.bb-event-rect' x Axis.<br>It should be used along with `data`, `index` or `x` value. The default value is set as `[0,0]` |
   *    | data | Object | When [data.xs](Options.html#.data%25E2%2580%25A4xs) option is used or [tooltip.grouped](Options.html#.tooltip) set to 'false', `should be used giving this param`.<br><br>**Key:**<br>- x {Number &verbar; Date}: x Axis value<br>- index {Number}: x Axis index (useless for data.xs)<br>- id {String}: data id<br>- value {Number}: The corresponding value for tooltip. |
   *
   * @example
   *  // show the 2nd x Axis coordinate tooltip
   *  chart.tooltip.show({
   *    index: 1
   *  });
   *
   *  // show tooltip for the 3rd x Axis in x:50 and y:100 coordinate of '.bb-event-rect' of the x Axis.
   *  chart.tooltip.show({
   *    x: 2,
   *    mouse: [50, 100]
   *  });
   *
   *  // show tooltip for timeseries x axis
   *  chart.tooltip.show({
   *    x: new Date("2018-01-02 00:00")
   *  });
   *
   *  // when data.xs is used
   *  chart.tooltip.show({
   *    data: {
   *        x: 3,  // x Axis value
   *        id: "data1",  // data id
   *        value: 500  // data value
   *    }
   *  });
   *
   *  // when data.xs isn't used, but tooltip.grouped=false is set
   *  chart.tooltip.show({
   *    data: {
   *        index: 3,  // or 'x' key value
   *        id: "data1",  // data id
   *        value: 500  // data value
   *    }
   *  });
   */
show:function(){var index,mouse,args=arguments.length>0&&void 0!==arguments[0]?arguments[0]:{},$$=this.internal;
// determine focus data
if(args.mouse&&(mouse=args.mouse),args.data){var y=$$.getYScale(args.data.id)(args.data.value);$$.isMultipleX()?mouse=[$$.x(args.data.x),y]:(!$$.config.tooltip_grouped&&(mouse=[0,y]),index=isValue(args.data.index)?args.data.index:$$.getIndexByX(args.data.x))}else isDefined(args.x)?index=$$.getIndexByX(args.x):isDefined(args.index)&&(index=args.index);// emulate events to show
("mouse"===$$.inputType?["mouseover","mousemove"]:["touchstart"]).forEach((function(eventName){$$.dispatchEvent(eventName,index,mouse)}))},
/**
   * Hide tooltip
   * @method tooltip․hide
   * @instance
   * @memberof Chart
   */
hide:function(){var $$=this.internal;// reset last touch point index
"touch"===$$.inputType&&$$.callOverOutForTouch(),$$.hideTooltip(!0),$$.hideGridFocus(),$$.unexpandCircles(),$$.unexpandBars()}});extend(Chart_Chart.prototype,{tooltip:api_tooltip_tooltip});
// CONCATENATED MODULE: ./src/api/api.export.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 */
/**
 * Encode to base64
 * @param {String} str
 * @return {String}
 * @private
 * @see https://developer.mozilla.org/ko/docs/Web/API/WindowBase64/Base64_encoding_and_decoding
 */
var b64EncodeUnicode=function(str){return btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g,(function(match,p){return String.fromCharCode("0x".concat(p))})))},nodeToSvgDataUrl=function(node,size){var serializer=new XMLSerializer,clone=node.cloneNode(!0),cssText=getCssRules(toArray(browser_doc.styleSheets)).filter((function(r){return r.cssText})).map((function(r){return r.cssText}));clone.setAttribute("xmlns",external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["namespaces"].xhtml);var nodeXml=serializer.serializeToString(clone),style=browser_doc.createElement("style");// escape css for XML
style.appendChild(browser_doc.createTextNode(cssText.join("\n")));var styleXml=serializer.serializeToString(style),dataStr='<svg xmlns="'.concat(external_commonjs_d3_selection_commonjs2_d3_selection_amd_d3_selection_root_d3_["namespaces"].svg,'" width="').concat(size.width,'" height="').concat(size.height,'">\n\t\t\t<foreignObject width="100%" height="100%">\n\t\t\t\t').concat(styleXml,"\n\t\t\t\t").concat(nodeXml.replace(/(url\()[^#]+/g,"$1"),"\n\t\t\t</foreignObject></svg>").replace("/\n/g","%0A");// foreignObject not supported in IE11 and below
// https://msdn.microsoft.com/en-us/library/hh834675(v=vs.85).aspx
return"data:image/svg+xml;base64,".concat(b64EncodeUnicode(dataStr))};
/**
 * Convert svg node to data url
 * @param {HTMLElement} node
 * @return {String}
 * @private
 */extend(Chart_Chart.prototype,{
/**
   * Export chart as an image.
   * - **NOTE:**
   *   - IE11 and below not work properly due to the lack of the feature(<a href="https://msdn.microsoft.com/en-us/library/hh834675(v=vs.85).aspx">foreignObject</a>) support
   *   - The basic CSS file(ex. billboard.css) should be at same domain as API call context to get correct styled export image.
   * @method export
   * @instance
   * @memberof Chart
   * @param {String} [mimeType=image/png] The desired output image format. (ex. 'image/png' for png, 'image/jpeg' for jpeg format)
   * @param {Function} [callback] The callback to be invoked when export is ready.
   * @return {String} dataURI
   * @example
   *  chart.export();
   *  // --> "data:image/svg+xml;base64,PHN..."
   *
   *  // Initialize the download automatically
   *  chart.export("image/png", dataUrl => {
   *     const link = document.createElement("a");
   *
   *     link.download = `${Date.now()}.png`;
   *     link.href = dataUrl;
   *     link.innerHTML = "Download chart as image";
   *
   *     document.body.appendChild(link);
   *  });
   */
export:function(mimeType,callback){var $$=this.internal,size={width:$$.currentWidth,height:$$.currentHeight},svgDataUrl=nodeToSvgDataUrl(this.element,size);if(isFunction(callback)){var img=new Image;img.crosssOrigin="Anonymous",img.onload=function(){var canvas=browser_doc.createElement("canvas"),ctx=canvas.getContext("2d");canvas.width=size.width,canvas.height=size.height,ctx.drawImage(img,0,0),callback(canvas.toDataURL(mimeType))},img.src=svgDataUrl}return svgDataUrl}});
// CONCATENATED MODULE: ./src/core.js
/**
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard project is licensed under the MIT license
 */
var _defaults={},bb={
/**
   * Version information
   * @property {String} version version
   * @example
   *    bb.version;  // "1.0.0"
   * @memberof bb
   */
version:"1.12.12",
/**
   * Generate chart
   * @param {Options} options chart options
   * @memberof bb
   * @return {Chart}
   * @see {@link Options} for different generation options
   * @see {@link Chart} for different methods API
   * @example
   *  <!-- chart holder -->
   * <div id="LineChart"></div>
   * @example
   *   // generate chart with options
   *  var chart = bb.generate({
   *      "bindto": "#LineChart"
   *      "data": {
   *          "columns": [
   *              ["data1", 30, 200, 100, 400, 150, 250],
   *              ["data2", 50, 20, 10, 40, 15, 25]
   *           ]
   *      }
   *  });
   *
   *  // call some API
   *  // ex) get the data of 'data1'
   *  chart.data("data1");
   */
generate:function(config){var options=mergeObj({},_defaults,config),inst=new Chart_Chart(options);return inst.internal.charts=this.instance,this.instance.push(inst),inst},
/**
   * Set or get global default options.
   * - **NOTE:**
   *   - The options values settings are valid within page context only.
   *   - If is called multiple times, will override the last value.
   * @param {Options} options chart options
   * @memberof bb
   * @return {Options}
   * @see {@link Options}
   * @example
   * // Set same option value as for `.generate()`
   * bb.defaults({
   *   data: {
   *     type: "bar"
   *   }
   * });
   *
   * bb.defaults();  // {data:{type: "bar"}}
   *
   * // data.type defaults to 'bar'
   * var chart = bb.generate({ ... });
   */
defaults:function(options){return isObject(options)&&(_defaults=options),_defaults},
/**
   * An array containing instance created
   * @property {Array} instance instance array
   * @example
   *  // generate charts
   *  var chart1 = bb.generate(...);
   *  var chart2 = bb.generate(...);
   *
   *  bb.instance;  // [ chart1, chart2, ... ]
   * @memberof bb
   */
instance:[],
/**
   * Namespace for plugins
   * @property {Object} plugin plugin namespace
   * @example
   *  // Stanford diagram plugin
   *  bb.plugin.stanford;
   * @memberof bb
   */
plugin:{},
/**
   * Internal chart object
   * @private
   */
chart:{fn:Chart_Chart.prototype,internal:{fn:ChartInternal_ChartInternal.prototype,axis:{fn:Axis_Axis.prototype}}}};
/**
 * @namespace bb
 * @version 1.12.12
 */
/* harmony default export */__nested_webpack_exports__["default"]=bb}
/******/])}));
//# sourceMappingURL=billboard.js.map
/***/},
/***/192098:
/***/function(module,__unused_webpack_exports,__webpack_require__){
/*!
 * Copyright (c) 2017 ~ present NAVER Corp.
 * billboard.js project is licensed under the MIT license
 * 
 * billboard.js, JavaScript chart library
 * https://naver.github.io/billboard.js/
 * 
 * @version 1.12.12
 * @requires billboard.js
 * @summary billboard.js plugin
 */
(function(root,factory){module.exports=factory(__webpack_require__(414469))})(0,(function(__WEBPACK_EXTERNAL_MODULE__10__){/******/
return function(modules){// webpackBootstrap
/******/ // The module cache
/******/var installedModules={};
/******/
/******/ // The require function
/******/function __nested_webpack_require_1052__(moduleId){
/******/
/******/ // Check if module is in cache
/******/if(installedModules[moduleId])
/******/return installedModules[moduleId].exports;
/******/
/******/ // Create a new module (and put it into the cache)
/******/var module=installedModules[moduleId]={
/******/i:moduleId,
/******/l:!1,
/******/exports:{}
/******/};
/******/
/******/ // Execute the module function
/******/
/******/
/******/ // Return the exports of the module
/******/return modules[moduleId].call(module.exports,module,module.exports,__nested_webpack_require_1052__),
/******/
/******/ // Flag the module as loaded
/******/module.l=!0,module.exports;
/******/}
/******/
/******/
/******/ // expose the modules object (__webpack_modules__)
/******/
/******/
/******/
/******/ // Load entry module and return exports
/******/return __nested_webpack_require_1052__.m=modules,
/******/
/******/ // expose the module cache
/******/__nested_webpack_require_1052__.c=installedModules,
/******/
/******/ // define getter function for harmony exports
/******/__nested_webpack_require_1052__.d=function(exports,name,getter){
/******/__nested_webpack_require_1052__.o(exports,name)||
/******/Object.defineProperty(exports,name,{enumerable:!0,get:getter})
/******/},
/******/
/******/ // define __esModule on exports
/******/__nested_webpack_require_1052__.r=function(exports){
/******/"undefined"!==typeof Symbol&&Symbol.toStringTag&&
/******/Object.defineProperty(exports,Symbol.toStringTag,{value:"Module"})
/******/,Object.defineProperty(exports,"__esModule",{value:!0})},
/******/
/******/ // create a fake namespace object
/******/ // mode & 1: value is a module id, require it
/******/ // mode & 2: merge all properties of value into the ns
/******/ // mode & 4: return value when already ns object
/******/ // mode & 8|1: behave like require
/******/__nested_webpack_require_1052__.t=function(value,mode){
/******/if(
/******/1&mode&&(value=__nested_webpack_require_1052__(value)),8&mode)return value;
/******/if(4&mode&&"object"===typeof value&&value&&value.__esModule)return value;
/******/var ns=Object.create(null);
/******/
/******/if(__nested_webpack_require_1052__.r(ns),
/******/Object.defineProperty(ns,"default",{enumerable:!0,value:value}),2&mode&&"string"!=typeof value)for(var key in value)__nested_webpack_require_1052__.d(ns,key,function(key){return value[key]}.bind(null,key));
/******/return ns;
/******/},
/******/
/******/ // getDefaultExport function for compatibility with non-harmony modules
/******/__nested_webpack_require_1052__.n=function(module){
/******/var getter=module&&module.__esModule?
/******/function(){return module["default"]}:
/******/function(){return module};
/******/
/******/return __nested_webpack_require_1052__.d(getter,"a",getter),getter;
/******/},
/******/
/******/ // Object.prototype.hasOwnProperty.call
/******/__nested_webpack_require_1052__.o=function(object,property){return Object.prototype.hasOwnProperty.call(object,property)},
/******/
/******/ // __webpack_public_path__
/******/__nested_webpack_require_1052__.p="",__nested_webpack_require_1052__(__nested_webpack_require_1052__.s=0);
/******/}
/************************************************************************/
/******/([
/* 0 */
/***/function(module,__nested_webpack_exports__,__nested_webpack_require_4528__){"use strict";__nested_webpack_require_4528__.r(__nested_webpack_exports__),
/* harmony export (binding) */__nested_webpack_require_4528__.d(__nested_webpack_exports__,"default",(function(){return BubbleCompare}));
/* harmony import */var _babel_runtime_helpers_esm_slicedToArray__WEBPACK_IMPORTED_MODULE_0__=__nested_webpack_require_4528__(21),_babel_runtime_helpers_esm_classCallCheck__WEBPACK_IMPORTED_MODULE_1__=__nested_webpack_require_4528__(3),_babel_runtime_helpers_esm_createClass__WEBPACK_IMPORTED_MODULE_2__=__nested_webpack_require_4528__(4),_babel_runtime_helpers_esm_assertThisInitialized__WEBPACK_IMPORTED_MODULE_3__=__nested_webpack_require_4528__(5),_babel_runtime_helpers_esm_inherits__WEBPACK_IMPORTED_MODULE_4__=__nested_webpack_require_4528__(23),_babel_runtime_helpers_esm_possibleConstructorReturn__WEBPACK_IMPORTED_MODULE_5__=__nested_webpack_require_4528__(6),_babel_runtime_helpers_esm_getPrototypeOf__WEBPACK_IMPORTED_MODULE_6__=__nested_webpack_require_4528__(8),_babel_runtime_helpers_esm_defineProperty__WEBPACK_IMPORTED_MODULE_7__=__nested_webpack_require_4528__(9),d3_selection__WEBPACK_IMPORTED_MODULE_8__=__nested_webpack_require_4528__(10),_Plugin__WEBPACK_IMPORTED_MODULE_9__=__nested_webpack_require_4528__(11);
/* harmony import */function _createSuper(Derived){var hasNativeReflectConstruct=_isNativeReflectConstruct();return function(){var result,Super=Object(_babel_runtime_helpers_esm_getPrototypeOf__WEBPACK_IMPORTED_MODULE_6__[/* default */"a"])(Derived);if(hasNativeReflectConstruct){var NewTarget=Object(_babel_runtime_helpers_esm_getPrototypeOf__WEBPACK_IMPORTED_MODULE_6__[/* default */"a"])(this).constructor;result=Reflect.construct(Super,arguments,NewTarget)}else result=Super.apply(this,arguments);return Object(_babel_runtime_helpers_esm_possibleConstructorReturn__WEBPACK_IMPORTED_MODULE_5__[/* default */"a"])(this,result)}}function _isNativeReflectConstruct(){if("undefined"===typeof Reflect||!Reflect.construct)return!1;if(Reflect.construct.sham)return!1;if("function"===typeof Proxy)return!0;try{return Date.prototype.toString.call(Reflect.construct(Date,[],(function(){}))),!0}catch(e){return!1}}
/**
 * Bubble compare diagram plugin.<br>
 * Compare data 3-dimensional ways: x-axis, y-axis & bubble-size.
 * - **NOTE:**
 *   - Plugins aren't built-in. Need to be loaded or imported to be used.
 *   - Non required modules from billboard.js core, need to be installed separately.
 * - **Required modules:**
 *   - [d3-selection](https://github.com/d3/d3-selection)
 * @class plugin-bubblecompare
 * @requires d3-selection
 * @param {Object} options bubble compare plugin options
 * @extends Plugin
 * @return {BubbleCompare}
 * @example
 *  var chart = bb.generate({
 *     data: {
 *        columns: [ ... ],
 *        type: "bubble"
 *     }
 *     ...
 *     plugins: [
 *        new bb.plugin.bubblecompare({
 *          minR: 11,
 *          maxR: 74,
 *          expandScale: 1.1
 *        }),
 *     ]
 *  });
  * @example
 *	import {bb} from "billboard.js";
 * import BubbleCompare from "billboard.js/dist/billboardjs-plugin-bubblecompare";
 *
 * bb.generate({
 *     plugins: [
 *        new BubbleCompare({ ... })
 *     ]
 * })
 */var BubbleCompare=function(_Plugin){function BubbleCompare(options){var _this;return Object(_babel_runtime_helpers_esm_classCallCheck__WEBPACK_IMPORTED_MODULE_1__[/* default */"a"])(this,BubbleCompare),_this=_super.call(this,options),Object(_babel_runtime_helpers_esm_possibleConstructorReturn__WEBPACK_IMPORTED_MODULE_5__[/* default */"a"])(_this,Object(_babel_runtime_helpers_esm_assertThisInitialized__WEBPACK_IMPORTED_MODULE_3__[/* default */"a"])(_this))}Object(_babel_runtime_helpers_esm_inherits__WEBPACK_IMPORTED_MODULE_4__[/* default */"a"])(BubbleCompare,_Plugin);var _super=_createSuper(BubbleCompare);return Object(_babel_runtime_helpers_esm_createClass__WEBPACK_IMPORTED_MODULE_2__[/* default */"a"])(BubbleCompare,[{key:"$init",value:function(){var $$=this.$$;$$.findClosest=this.findClosest.bind(this),$$.getBubbleR=this.getBubbleR.bind(this),$$.pointExpandedR=this.pointExpandedR.bind(this)}},{key:"pointExpandedR",value:function(d){var baseR=this.getBubbleR(d),_this$options$expandS=this.options.expandScale,expandScale=void 0===_this$options$expandS?1:_this$options$expandS;return BubbleCompare.raiseFocusedBubbleLayer(d),this.changeCursorPoint(),baseR*expandScale}},{key:"changeCursorPoint",value:function(){this.$$.svg.select(".bb-event-rect").style("cursor","pointer")}},{key:"findClosest",value:function(values,pos){var _this2=this,$$=this.$$;return values.filter((function(v){return v&&!$$.isBarType(v.id)})).reduce((function(acc,cur){var d=$$.dist(cur,pos);return d<_this2.getBubbleR(cur)?cur:acc}),0)}},{key:"getBubbleR",value:function(d){var _this3=this,_this$options=this.options,minR=_this$options.minR,maxR=_this$options.maxR,curVal=this.getZData(d);if(!curVal)return minR;var _this$$$$data$targets=this.$$.data.targets.reduce((function(_ref,cur){var _ref2=Object(_babel_runtime_helpers_esm_slicedToArray__WEBPACK_IMPORTED_MODULE_0__[/* default */"a"])(_ref,2),accMin=_ref2[0],accMax=_ref2[1],val=_this3.getZData(cur.values[0]);return[Math.min(accMin,val),Math.max(accMax,val)]}),[1e4,0]),_this$$$$data$targets2=Object(_babel_runtime_helpers_esm_slicedToArray__WEBPACK_IMPORTED_MODULE_0__[/* default */"a"])(_this$$$$data$targets,2),min=_this$$$$data$targets2[0],max=_this$$$$data$targets2[1],size=min>0&&max===min?0:curVal/max;return Math.abs(size)*(maxR-minR)+minR}},{key:"getZData",value:function(d){return this.$$.isBubbleZType(d)?this.$$.getBubbleZData(d.value,"z"):d.value}}],[{key:"raiseFocusedBubbleLayer",value:function(d){d.raise&&Object(d3_selection__WEBPACK_IMPORTED_MODULE_8__["select"])(d.node().parentNode.parentNode).raise()}}]),BubbleCompare}(_Plugin__WEBPACK_IMPORTED_MODULE_9__[/* default */"a"]);Object(_babel_runtime_helpers_esm_defineProperty__WEBPACK_IMPORTED_MODULE_7__[/* default */"a"])(BubbleCompare,"version","0.0.1")},
/* 1 */
/***/function(module,__nested_webpack_exports__,__nested_webpack_require_11684__){"use strict";
/* harmony export (binding) */__nested_webpack_require_11684__.d(__nested_webpack_exports__,"a",(function(){return _unsupportedIterableToArray}));
/* harmony import */var _arrayLikeToArray__WEBPACK_IMPORTED_MODULE_0__=__nested_webpack_require_11684__(2);function _unsupportedIterableToArray(o,minLen){if(o){if("string"===typeof o)return Object(_arrayLikeToArray__WEBPACK_IMPORTED_MODULE_0__[/* default */"a"])(o,minLen);var n=Object.prototype.toString.call(o).slice(8,-1);return"Object"===n&&o.constructor&&(n=o.constructor.name),"Map"===n||"Set"===n?Array.from(o):"Arguments"===n||/^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n)?Object(_arrayLikeToArray__WEBPACK_IMPORTED_MODULE_0__[/* default */"a"])(o,minLen):void 0}}
/***/},
/* 2 */
/***/function(module,__nested_webpack_exports__,__nested_webpack_require_12558__){"use strict";
/* harmony export (binding) */function _arrayLikeToArray(arr,len){(null==len||len>arr.length)&&(len=arr.length);for(var i=0,arr2=new Array(len);i<len;i++)arr2[i]=arr[i];return arr2}
/***/__nested_webpack_require_12558__.d(__nested_webpack_exports__,"a",(function(){return _arrayLikeToArray}))},
/* 3 */
/***/function(module,__nested_webpack_exports__,__nested_webpack_require_12980__){"use strict";
/* harmony export (binding) */function _classCallCheck(instance,Constructor){if(!(instance instanceof Constructor))throw new TypeError("Cannot call a class as a function")}
/***/__nested_webpack_require_12980__.d(__nested_webpack_exports__,"a",(function(){return _classCallCheck}))},
/* 4 */
/***/function(module,__nested_webpack_exports__,__nested_webpack_require_13364__){"use strict";
/* harmony export (binding) */function _defineProperties(target,props){for(var i=0;i<props.length;i++){var descriptor=props[i];descriptor.enumerable=descriptor.enumerable||!1,descriptor.configurable=!0,"value"in descriptor&&(descriptor.writable=!0),Object.defineProperty(target,descriptor.key,descriptor)}}function _createClass(Constructor,protoProps,staticProps){return protoProps&&_defineProperties(Constructor.prototype,protoProps),staticProps&&_defineProperties(Constructor,staticProps),Constructor}
/***/__nested_webpack_require_13364__.d(__nested_webpack_exports__,"a",(function(){return _createClass}))},
/* 5 */
/***/function(module,__nested_webpack_exports__,__nested_webpack_require_14148__){"use strict";
/* harmony export (binding) */function _assertThisInitialized(self){if(void 0===self)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return self}
/***/__nested_webpack_require_14148__.d(__nested_webpack_exports__,"a",(function(){return _assertThisInitialized}))},
/* 6 */
/***/function(module,__nested_webpack_exports__,__nested_webpack_require_14555__){"use strict";
/* harmony export (binding) */__nested_webpack_require_14555__.d(__nested_webpack_exports__,"a",(function(){return _possibleConstructorReturn}));
/* harmony import */var _helpers_esm_typeof__WEBPACK_IMPORTED_MODULE_0__=__nested_webpack_require_14555__(7),_assertThisInitialized__WEBPACK_IMPORTED_MODULE_1__=__nested_webpack_require_14555__(5);
/* harmony import */function _possibleConstructorReturn(self,call){return!call||"object"!==Object(_helpers_esm_typeof__WEBPACK_IMPORTED_MODULE_0__[/* default */"a"])(call)&&"function"!==typeof call?Object(_assertThisInitialized__WEBPACK_IMPORTED_MODULE_1__[/* default */"a"])(self):call}
/***/},
/* 7 */
/***/function(module,__nested_webpack_exports__,__nested_webpack_require_15306__){"use strict";
/* harmony export (binding) */function _typeof(obj){return _typeof="function"===typeof Symbol&&"symbol"===typeof Symbol.iterator?function(obj){return typeof obj}:function(obj){return obj&&"function"===typeof Symbol&&obj.constructor===Symbol&&obj!==Symbol.prototype?"symbol":typeof obj},_typeof(obj)}
/***/__nested_webpack_require_15306__.d(__nested_webpack_exports__,"a",(function(){return _typeof}))},
/* 8 */
/***/function(module,__nested_webpack_exports__,__nested_webpack_require_15939__){"use strict";
/* harmony export (binding) */function _getPrototypeOf(o){return _getPrototypeOf=Object.setPrototypeOf?Object.getPrototypeOf:function(o){return o.__proto__||Object.getPrototypeOf(o)},_getPrototypeOf(o)}
/***/__nested_webpack_require_15939__.d(__nested_webpack_exports__,"a",(function(){return _getPrototypeOf}))},
/* 9 */
/***/function(module,__nested_webpack_exports__,__nested_webpack_require_16377__){"use strict";
/* harmony export (binding) */function _defineProperty(obj,key,value){return key in obj?Object.defineProperty(obj,key,{value:value,enumerable:!0,configurable:!0,writable:!0}):obj[key]=value,obj}
/***/__nested_webpack_require_16377__.d(__nested_webpack_exports__,"a",(function(){return _defineProperty}))},
/* 10 */
/***/function(module,exports){module.exports=__WEBPACK_EXTERNAL_MODULE__10__;
/***/},
/* 11 */
/***/function(module,__nested_webpack_exports__,__nested_webpack_require_16961__){"use strict";
/* harmony export (binding) */__nested_webpack_require_16961__.d(__nested_webpack_exports__,"a",(function(){return Plugin}));
/* harmony import */var _babel_runtime_helpers_esm_classCallCheck__WEBPACK_IMPORTED_MODULE_0__=__nested_webpack_require_16961__(3),_babel_runtime_helpers_esm_createClass__WEBPACK_IMPORTED_MODULE_1__=__nested_webpack_require_16961__(4),_babel_runtime_helpers_esm_defineProperty__WEBPACK_IMPORTED_MODULE_2__=__nested_webpack_require_16961__(9),Plugin=function(){
/**
   * Version info string for plugin
   * @name version
   * @static
   * @memberof Plugin
   * @type {String}
   * @example
   *   bb.plugin.stanford.version;  // ex) 1.9.0
   */
/**
   * Constructor
   * @param {Any} config config option object
   * @private
   */
function Plugin(){var options=arguments.length>0&&void 0!==arguments[0]?arguments[0]:{};Object(_babel_runtime_helpers_esm_classCallCheck__WEBPACK_IMPORTED_MODULE_0__[/* default */"a"])(this,Plugin),this.options=options}
/**
   * Lifecycle hook for 'beforeInit' phase.
   * @private
   */return Object(_babel_runtime_helpers_esm_createClass__WEBPACK_IMPORTED_MODULE_1__[/* default */"a"])(Plugin,[{key:"$beforeInit",value:function(){}
/**
     * Lifecycle hook for 'init' phase.
     * @private
     */},{key:"$init",value:function(){}
/**
     * Lifecycle hook for 'afterInit' phase.
     * @private
     */},{key:"$afterInit",value:function(){}
/**
     * Lifecycle hook for 'redraw' phase.
     * @private
     */},{key:"$redraw",value:function(){}
/**
     * Lifecycle hook for 'willDestroy' phase.
     * @private
     */},{key:"$willDestroy",value:function(){var _this=this;Object.keys(this).forEach((function(key){_this[key]=null,delete _this[key]}))}}]),Plugin}();
/* harmony import */Object(_babel_runtime_helpers_esm_defineProperty__WEBPACK_IMPORTED_MODULE_2__[/* default */"a"])(Plugin,"version","1.12.12")},
/* 12 */
/* 13 */,
/* 14 */,
/* 15 */,
/* 16 */,
/* 17 */,
/* 18 */,
/* 19 */,
/* 20 */,
/* 21 */
/***/,function(module,__nested_webpack_exports__,__nested_webpack_require_19479__){"use strict";
// EXPORTS
// CONCATENATED MODULE: ./node_modules/@babel/runtime/helpers/esm/arrayWithHoles.js
function _arrayWithHoles(arr){if(Array.isArray(arr))return arr}
// CONCATENATED MODULE: ./node_modules/@babel/runtime/helpers/esm/iterableToArrayLimit.js
function _iterableToArrayLimit(arr,i){if("undefined"!==typeof Symbol&&Symbol.iterator in Object(arr)){var _arr=[],_n=!0,_d=!1,_e=void 0;try{for(var _s,_i=arr[Symbol.iterator]();!(_n=(_s=_i.next()).done);_n=!0)if(_arr.push(_s.value),i&&_arr.length===i)break}catch(err){_d=!0,_e=err}finally{try{_n||null==_i["return"]||_i["return"]()}finally{if(_d)throw _e}}return _arr}}
// EXTERNAL MODULE: ./node_modules/@babel/runtime/helpers/esm/unsupportedIterableToArray.js
__nested_webpack_require_19479__.d(__nested_webpack_exports__,"a",(function(){/* binding */return _slicedToArray}));var unsupportedIterableToArray=__nested_webpack_require_19479__(1);
// CONCATENATED MODULE: ./node_modules/@babel/runtime/helpers/esm/nonIterableRest.js
function _nonIterableRest(){throw new TypeError("Invalid attempt to destructure non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.")}
// CONCATENATED MODULE: ./node_modules/@babel/runtime/helpers/esm/slicedToArray.js
function _slicedToArray(arr,i){return _arrayWithHoles(arr)||_iterableToArrayLimit(arr,i)||Object(unsupportedIterableToArray["a"/* default */])(arr,i)||_nonIterableRest()}
/***/},
/* 22 */
/* 23 */
/***/,function(module,__nested_webpack_exports__,__nested_webpack_require_21223__){"use strict";
// EXPORTS
// CONCATENATED MODULE: ./node_modules/@babel/runtime/helpers/esm/setPrototypeOf.js
function _setPrototypeOf(o,p){return _setPrototypeOf=Object.setPrototypeOf||function(o,p){return o.__proto__=p,o},_setPrototypeOf(o,p)}
// CONCATENATED MODULE: ./node_modules/@babel/runtime/helpers/esm/inherits.js
function _inherits(subClass,superClass){if("function"!==typeof superClass&&null!==superClass)throw new TypeError("Super expression must either be null or a function");subClass.prototype=Object.create(superClass&&superClass.prototype,{constructor:{value:subClass,writable:!0,configurable:!0}}),superClass&&_setPrototypeOf(subClass,superClass)}
/***/__nested_webpack_require_21223__.d(__nested_webpack_exports__,"a",(function(){/* binding */return _inherits}))}
/******/])["default"]}));
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly9iYi5wbHVnaW4uW25hbWVdL3dlYnBhY2svdW5pdmVyc2FsTW9kdWxlRGVmaW5pdGlvbiIsIndlYnBhY2s6Ly9iYi5wbHVnaW4uW25hbWVdL3dlYnBhY2svYm9vdHN0cmFwIiwid2VicGFjazovL2JiLnBsdWdpbi5bbmFtZV0vLi9zcmMvcGx1Z2luL2J1YmJsZWNvbXBhcmUvaW5kZXguanMiLCJ3ZWJwYWNrOi8vYmIucGx1Z2luLltuYW1lXS8uL25vZGVfbW9kdWxlcy9AYmFiZWwvcnVudGltZS9oZWxwZXJzL2VzbS91bnN1cHBvcnRlZEl0ZXJhYmxlVG9BcnJheS5qcyIsIndlYnBhY2s6Ly9iYi5wbHVnaW4uW25hbWVdLy4vbm9kZV9tb2R1bGVzL0BiYWJlbC9ydW50aW1lL2hlbHBlcnMvZXNtL2FycmF5TGlrZVRvQXJyYXkuanMiLCJ3ZWJwYWNrOi8vYmIucGx1Z2luLltuYW1lXS8uL25vZGVfbW9kdWxlcy9AYmFiZWwvcnVudGltZS9oZWxwZXJzL2VzbS9jbGFzc0NhbGxDaGVjay5qcyIsIndlYnBhY2s6Ly9iYi5wbHVnaW4uW25hbWVdLy4vbm9kZV9tb2R1bGVzL0BiYWJlbC9ydW50aW1lL2hlbHBlcnMvZXNtL2NyZWF0ZUNsYXNzLmpzIiwid2VicGFjazovL2JiLnBsdWdpbi5bbmFtZV0vLi9ub2RlX21vZHVsZXMvQGJhYmVsL3J1bnRpbWUvaGVscGVycy9lc20vYXNzZXJ0VGhpc0luaXRpYWxpemVkLmpzIiwid2VicGFjazovL2JiLnBsdWdpbi5bbmFtZV0vLi9ub2RlX21vZHVsZXMvQGJhYmVsL3J1bnRpbWUvaGVscGVycy9lc20vcG9zc2libGVDb25zdHJ1Y3RvclJldHVybi5qcyIsIndlYnBhY2s6Ly9iYi5wbHVnaW4uW25hbWVdLy4vbm9kZV9tb2R1bGVzL0BiYWJlbC9ydW50aW1lL2hlbHBlcnMvZXNtL3R5cGVvZi5qcyIsIndlYnBhY2s6Ly9iYi5wbHVnaW4uW25hbWVdLy4vbm9kZV9tb2R1bGVzL0BiYWJlbC9ydW50aW1lL2hlbHBlcnMvZXNtL2dldFByb3RvdHlwZU9mLmpzIiwid2VicGFjazovL2JiLnBsdWdpbi5bbmFtZV0vLi9ub2RlX21vZHVsZXMvQGJhYmVsL3J1bnRpbWUvaGVscGVycy9lc20vZGVmaW5lUHJvcGVydHkuanMiLCJ3ZWJwYWNrOi8vYmIucGx1Z2luLltuYW1lXS9leHRlcm5hbCB7XCJjb21tb25qc1wiOlwiZDMtc2VsZWN0aW9uXCIsXCJjb21tb25qczJcIjpcImQzLXNlbGVjdGlvblwiLFwiYW1kXCI6XCJkMy1zZWxlY3Rpb25cIixcInJvb3RcIjpcImQzXCJ9Iiwid2VicGFjazovL2JiLnBsdWdpbi5bbmFtZV0vLi9zcmMvcGx1Z2luL1BsdWdpbi5qcyIsIndlYnBhY2s6Ly9iYi5wbHVnaW4uW25hbWVdLy4vbm9kZV9tb2R1bGVzL0BiYWJlbC9ydW50aW1lL2hlbHBlcnMvZXNtL2FycmF5V2l0aEhvbGVzLmpzIiwid2VicGFjazovL2JiLnBsdWdpbi5bbmFtZV0vLi9ub2RlX21vZHVsZXMvQGJhYmVsL3J1bnRpbWUvaGVscGVycy9lc20vaXRlcmFibGVUb0FycmF5TGltaXQuanMiLCJ3ZWJwYWNrOi8vYmIucGx1Z2luLltuYW1lXS8uL25vZGVfbW9kdWxlcy9AYmFiZWwvcnVudGltZS9oZWxwZXJzL2VzbS9ub25JdGVyYWJsZVJlc3QuanMiLCJ3ZWJwYWNrOi8vYmIucGx1Z2luLltuYW1lXS8uL25vZGVfbW9kdWxlcy9AYmFiZWwvcnVudGltZS9oZWxwZXJzL2VzbS9zbGljZWRUb0FycmF5LmpzIiwid2VicGFjazovL2JiLnBsdWdpbi5bbmFtZV0vLi9ub2RlX21vZHVsZXMvQGJhYmVsL3J1bnRpbWUvaGVscGVycy9lc20vc2V0UHJvdG90eXBlT2YuanMiLCJ3ZWJwYWNrOi8vYmIucGx1Z2luLltuYW1lXS8uL25vZGVfbW9kdWxlcy9AYmFiZWwvcnVudGltZS9oZWxwZXJzL2VzbS9pbmhlcml0cy5qcyJdLCJuYW1lcyI6WyJCdWJibGVDb21wYXJlIiwib3B0aW9ucyIsIiQkIiwiZmluZENsb3Nlc3QiLCJiaW5kIiwiZ2V0QnViYmxlUiIsInBvaW50RXhwYW5kZWRSIiwiZCIsImJhc2VSIiwiZXhwYW5kU2NhbGUiLCJyYWlzZUZvY3VzZWRCdWJibGVMYXllciIsImNoYW5nZUN1cnNvclBvaW50Iiwic3ZnIiwic2VsZWN0Iiwic3R5bGUiLCJ2YWx1ZXMiLCJwb3MiLCJmaWx0ZXIiLCJ2IiwiaXNCYXJUeXBlIiwiaWQiLCJyZWR1Y2UiLCJhY2MiLCJjdXIiLCJkaXN0IiwibWluUiIsIm1heFIiLCJjdXJWYWwiLCJnZXRaRGF0YSIsImRhdGEiLCJ0YXJnZXRzIiwiYWNjTWluIiwiYWNjTWF4IiwidmFsIiwiTWF0aCIsIm1pbiIsIm1heCIsInNpemUiLCJhYnMiLCJpc0J1YmJsZVpUeXBlIiwiZ2V0QnViYmxlWkRhdGEiLCJ2YWx1ZSIsInJhaXNlIiwiZDNTZWxlY3QiLCJub2RlIiwicGFyZW50Tm9kZSIsIlBsdWdpbiIsIk9iamVjdCIsImtleXMiLCJmb3JFYWNoIiwia2V5Il0sIm1hcHBpbmdzIjoiOzs7Ozs7Ozs7OztBQUFBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSwrQkFBK0IsbURBQW1EO0FBQ2xGLENBQUM7QUFDRCxPO1FDVkE7UUFDQTs7UUFFQTtRQUNBOztRQUVBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBOztRQUVBO1FBQ0E7O1FBRUE7UUFDQTs7UUFFQTtRQUNBO1FBQ0E7OztRQUdBO1FBQ0E7O1FBRUE7UUFDQTs7UUFFQTtRQUNBO1FBQ0E7UUFDQSwwQ0FBMEMsZ0NBQWdDO1FBQzFFO1FBQ0E7O1FBRUE7UUFDQTtRQUNBO1FBQ0Esd0RBQXdELGtCQUFrQjtRQUMxRTtRQUNBLGlEQUFpRCxjQUFjO1FBQy9EOztRQUVBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQSx5Q0FBeUMsaUNBQWlDO1FBQzFFLGdIQUFnSCxtQkFBbUIsRUFBRTtRQUNySTtRQUNBOztRQUVBO1FBQ0E7UUFDQTtRQUNBLDJCQUEyQiwwQkFBMEIsRUFBRTtRQUN2RCxpQ0FBaUMsZUFBZTtRQUNoRDtRQUNBO1FBQ0E7O1FBRUE7UUFDQSxzREFBc0QsK0RBQStEOztRQUVySDtRQUNBOzs7UUFHQTtRQUNBOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7O0FDbEZBO0FBQ0E7QUFFQTs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7O0lBdUNxQkEsYTtBQUdwQix5QkFBWUMsT0FBWixFQUFxQjtBQUFBOztBQUdwQiw2SkFGTUEsT0FFTjtBQUNBOzs7Ozs7Ozs0QkFFTztBQUFBLFVBQ0FDLEVBREEsR0FDTSxJQUROLENBQ0FBLEVBREE7QUFHUEEsUUFBRSxDQUFDQyxXQUFILEdBQWlCLEtBQUtBLFdBQUwsQ0FBaUJDLElBQWpCLENBQXNCLElBQXRCLENBSFYsRUFJUEYsRUFBRSxDQUFDRyxVQUFILEdBQWdCLEtBQUtBLFVBQUwsQ0FBZ0JELElBQWhCLENBQXFCLElBQXJCLENBSlQsRUFLUEYsRUFBRSxDQUFDSSxjQUFILEdBQW9CLEtBQUtBLGNBQUwsQ0FBb0JGLElBQXBCLENBQXlCLElBQXpCLENBTGI7QUFNUDs7O21DQUVjRyxDLEVBQUc7QUFDWCxVQUFBQyxLQUFLLEdBQUcsS0FBS0gsVUFBTCxDQUFnQkUsQ0FBaEIsQ0FBUjtBQUFBLGtDQUNvQixLQUFLTixPQUR6QixDQUNDUSxXQUREO0FBQUEsVUFDQ0EsV0FERCxzQ0FDZSxDQURmO0FBTU4sYUFIQVQsYUFBYSxDQUFDVSx1QkFBZCxDQUFzQ0gsQ0FBdEMsQ0FHQSxFQUZBLEtBQUtJLGlCQUFMLEVBRUEsRUFBT0gsS0FBSyxHQUFHQyxXQUFmO0FBQ0E7Ozt3Q0FNbUI7QUFDbkIsV0FBS1AsRUFBTCxDQUFRVSxHQUFSLENBQVlDLE1BQVosbUJBQXFDQyxLQUFyQyxDQUEyQyxRQUEzQyxFQUFxRCxTQUFyRCxDQURtQjtBQUVuQjs7O2dDQUVXQyxNLEVBQVFDLEcsRUFBSztBQUFBO0FBQUEsVUFDakJkLEVBRGlCLEdBQ1gsSUFEVyxDQUNqQkEsRUFEaUI7O0FBR3hCLGFBQU9hLE1BQU0sQ0FDWEUsTUFESyxDQUNFLFVBQUFDLENBQUM7QUFBQSxlQUFJQSxDQUFDLElBQUksQ0FBQ2hCLEVBQUUsQ0FBQ2lCLFNBQUgsQ0FBYUQsQ0FBQyxDQUFDRSxFQUFmLENBQVY7QUFBQSxPQURILEVBRUxDLE1BRkssQ0FFRSxVQUFDQyxHQUFELEVBQU1DLEdBQU4sRUFBYztBQUNyQixZQUFNaEIsQ0FBQyxHQUFHTCxFQUFFLENBQUNzQixJQUFILENBQVFELEdBQVIsRUFBYVAsR0FBYixDQUFWO0FBRUEsZUFBT1QsQ0FBQyxHQUFHLE1BQUksQ0FBQ0YsVUFBTCxDQUFnQmtCLEdBQWhCLENBQUosR0FBMkJBLEdBQTNCLEdBQWlDRCxHQUF4QztBQUNBLE9BTkssRUFNSCxDQU5HLENBQVA7QUFPQTs7OytCQUVVZixDLEVBQUc7QUFBQTtBQUFBLDBCQUNRLEtBQUtOLE9BRGI7QUFBQSxVQUNOd0IsSUFETSxpQkFDTkEsSUFETTtBQUFBLFVBQ0FDLElBREEsaUJBQ0FBLElBREE7QUFBQSxVQUVQQyxNQUZPLEdBRUUsS0FBS0MsUUFBTCxDQUFjckIsQ0FBZCxDQUZGOztBQUliLFVBQUksQ0FBQ29CLE1BQUwsRUFBYSxPQUFPRixJQUFQOztBQUpBLGtDQU1NLEtBQUt2QixFQUFMLENBQVEyQixJQUFSLENBQWFDLE9BQWIsQ0FBcUJULE1BQXJCLENBQ2xCLGdCQUFtQkUsR0FBbkIsRUFBMkI7QUFBQTtBQUFBLFlBQXpCUSxNQUF5QjtBQUFBLFlBQWpCQyxNQUFpQjtBQUFBLFlBQ3BCQyxHQURvQixHQUNkLE1BQUksQ0FBQ0wsUUFBTCxDQUFjTCxHQUFHLENBQUNSLE1BQUosQ0FBVyxDQUFYLENBQWQsQ0FEYzs7QUFHMUIsZUFBTyxDQUFDbUIsSUFBSSxDQUFDQyxHQUFMLENBQVNKLE1BQVQsRUFBaUJFLEdBQWpCLENBQUQsRUFBd0JDLElBQUksQ0FBQ0UsR0FBTCxDQUFTSixNQUFULEVBQWlCQyxHQUFqQixDQUF4QixDQUFQO0FBQ0EsT0FMaUIsRUFNbEIsQ0FBQyxHQUFELEVBQVEsQ0FBUixDQU5rQixDQU5OO0FBQUE7QUFBQSxVQU1ORSxHQU5NO0FBQUEsVUFNREMsR0FOQztBQUFBLFVBY1BDLElBZE8sR0FjQUYsR0FBRyxHQUFHLENBQU4sSUFBV0MsR0FBRyxLQUFLRCxHQUFuQixHQUF5QixDQUF6QixHQUE2QlIsTUFBTSxHQUFHUyxHQWR0Qzs7QUFnQmIsYUFBT0YsSUFBSSxDQUFDSSxHQUFMLENBQVNELElBQVQsS0FBa0JYLElBQUksR0FBR0QsSUFBekIsSUFBaUNBLElBQXhDO0FBQ0E7Ozs2QkFFUWxCLEMsRUFBRztBQUNYLGFBQU8sS0FBS0wsRUFBTCxDQUFRcUMsYUFBUixDQUFzQmhDLENBQXRCLElBQ04sS0FBS0wsRUFBTCxDQUFRc0MsY0FBUixDQUF1QmpDLENBQUMsQ0FBQ2tDLEtBQXpCLEVBQWdDLEdBQWhDLENBRE0sR0FFTmxDLENBQUMsQ0FBQ2tDLEtBRkg7QUFHQTs7OzRDQTNDOEJsQyxDLEVBQUc7QUFDakNBLE9BQUMsQ0FBQ21DLEtBQUYsSUFBV0MsMkRBQVEsQ0FBQ3BDLENBQUMsQ0FBQ3FDLElBQUYsR0FBU0MsVUFBVCxDQUFvQkEsVUFBckIsQ0FBUixDQUF5Q0gsS0FBekMsRUFEc0I7QUFFakM7O0VBN0J5Q0ksdUQ7O2tHQUF0QjlDLGE7Ozs7Ozs7OztBQzFDckI7QUFBQTtBQUFrRDtBQUNuQztBQUNmO0FBQ0Esb0NBQW9DLHlFQUFnQjtBQUNwRDtBQUNBO0FBQ0E7QUFDQSxzRkFBc0YseUVBQWdCO0FBQ3RHLEM7Ozs7Ozs7QUNSQTtBQUFlO0FBQ2Y7O0FBRUEsd0NBQXdDLFNBQVM7QUFDakQ7QUFDQTs7QUFFQTtBQUNBLEM7Ozs7Ozs7QUNSQTtBQUFlO0FBQ2Y7QUFDQTtBQUNBO0FBQ0EsQzs7Ozs7OztBQ0pBO0FBQUE7QUFDQSxpQkFBaUIsa0JBQWtCO0FBQ25DO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQUVlO0FBQ2Y7QUFDQTtBQUNBO0FBQ0EsQzs7Ozs7OztBQ2RBO0FBQWU7QUFDZjtBQUNBO0FBQ0E7O0FBRUE7QUFDQSxDOzs7Ozs7O0FDTkE7QUFBQTtBQUFBO0FBQStDO0FBQ2E7QUFDN0M7QUFDZixlQUFlLDJFQUFPO0FBQ3RCO0FBQ0E7O0FBRUEsU0FBUyw4RUFBcUI7QUFDOUIsQzs7Ozs7OztBQ1JBO0FBQWU7QUFDZjs7QUFFQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLEdBQUc7QUFDSDtBQUNBO0FBQ0E7QUFDQTs7QUFFQTtBQUNBLEM7Ozs7Ozs7QUNkQTtBQUFlO0FBQ2Y7QUFDQTtBQUNBO0FBQ0E7QUFDQSxDOzs7Ozs7O0FDTEE7QUFBZTtBQUNmO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLEtBQUs7QUFDTCxHQUFHO0FBQ0g7QUFDQTs7QUFFQTtBQUNBLEM7Ozs7OztBQ2JBLGlEOzs7Ozs7Ozs7Ozs7Ozs7QUNBQTs7Ozs7QUFJQTs7OztJQUlxQjhDLE07QUFDcEI7Ozs7Ozs7Ozs7QUFXQTs7Ozs7QUFLQSxvQkFBMEI7QUFBQSxRQUFkN0MsT0FBYyx1RUFBSixFQUFJO0FBQUEscUhBQ3pCLEtBQUtBLE9BQUwsR0FBZUEsT0FEVTtBQUV6QjtBQUVEOzs7Ozs7OztrQ0FJYyxDQUFFO0FBRWhCOzs7Ozs7OzRCQUlRLENBQUU7QUFFVjs7Ozs7OztpQ0FJYSxDQUFFO0FBRWY7Ozs7Ozs7OEJBSVUsQ0FBRTtBQUVaOzs7Ozs7O21DQUllO0FBQUE7O0FBQ2Q4QyxZQUFNLENBQUNDLElBQVAsQ0FBWSxJQUFaLEVBQWtCQyxPQUFsQixDQUEwQixVQUFBQyxHQUFHLEVBQUk7QUFDaEMsYUFBSSxDQUFDQSxHQUFELENBQUosR0FBWSxJQURvQixFQUVoQyxPQUFPLEtBQUksQ0FBQ0EsR0FBRCxDQUZxQjtBQUdoQyxPQUhELENBRGM7QUFLZDs7OztrR0F0RG1CSixNLGFBVUgsUzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7QUNsQkg7QUFDZjtBQUNBLEM7O0FDRmU7QUFDZjtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQUVBO0FBQ0EsNkNBQTZDLCtCQUErQjtBQUM1RTs7QUFFQTtBQUNBO0FBQ0EsR0FBRztBQUNIO0FBQ0E7QUFDQSxHQUFHO0FBQ0g7QUFDQTtBQUNBLEtBQUs7QUFDTDtBQUNBO0FBQ0E7O0FBRUE7QUFDQSxDOzs7OztBQ3pCZTtBQUNmO0FBQ0EsQzs7QUNGOEM7QUFDWTtBQUNZO0FBQ3RCO0FBQ2pDO0FBQ2YsU0FBUyxlQUFjLFNBQVMscUJBQW9CLFlBQVkscURBQTBCLFlBQVksZ0JBQWU7QUFDckgsQzs7Ozs7Ozs7Ozs7OztBQ05lO0FBQ2Y7QUFDQTtBQUNBO0FBQ0E7O0FBRUE7QUFDQSxDOztBQ1A4QztBQUMvQjtBQUNmO0FBQ0E7QUFDQTs7QUFFQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxHQUFHO0FBQ0gsa0JBQWtCLGVBQWM7QUFDaEMsQyIsImZpbGUiOiJiaWxsYm9hcmRqcy1wbHVnaW4tYnViYmxlY29tcGFyZS5qcyIsInNvdXJjZXNDb250ZW50IjpbIihmdW5jdGlvbiB3ZWJwYWNrVW5pdmVyc2FsTW9kdWxlRGVmaW5pdGlvbihyb290LCBmYWN0b3J5KSB7XG5cdGlmKHR5cGVvZiBleHBvcnRzID09PSAnb2JqZWN0JyAmJiB0eXBlb2YgbW9kdWxlID09PSAnb2JqZWN0Jylcblx0XHRtb2R1bGUuZXhwb3J0cyA9IGZhY3RvcnkocmVxdWlyZShcImQzLXNlbGVjdGlvblwiKSk7XG5cdGVsc2UgaWYodHlwZW9mIGRlZmluZSA9PT0gJ2Z1bmN0aW9uJyAmJiBkZWZpbmUuYW1kKVxuXHRcdGRlZmluZShcImJ1YmJsZWNvbXBhcmVcIiwgW1wiZDMtc2VsZWN0aW9uXCJdLCBmYWN0b3J5KTtcblx0ZWxzZSBpZih0eXBlb2YgZXhwb3J0cyA9PT0gJ29iamVjdCcpXG5cdFx0ZXhwb3J0c1tcImJ1YmJsZWNvbXBhcmVcIl0gPSBmYWN0b3J5KHJlcXVpcmUoXCJkMy1zZWxlY3Rpb25cIikpO1xuXHRlbHNlXG5cdFx0cm9vdFtcImJiXCJdID0gcm9vdFtcImJiXCJdIHx8IHt9LCByb290W1wiYmJcIl1bXCJwbHVnaW5cIl0gPSByb290W1wiYmJcIl1bXCJwbHVnaW5cIl0gfHwge30sIHJvb3RbXCJiYlwiXVtcInBsdWdpblwiXVtcImJ1YmJsZWNvbXBhcmVcIl0gPSBmYWN0b3J5KHJvb3RbXCJkM1wiXSk7XG59KSh0aGlzLCBmdW5jdGlvbihfX1dFQlBBQ0tfRVhURVJOQUxfTU9EVUxFX18xMF9fKSB7XG5yZXR1cm4gIiwiIFx0Ly8gVGhlIG1vZHVsZSBjYWNoZVxuIFx0dmFyIGluc3RhbGxlZE1vZHVsZXMgPSB7fTtcblxuIFx0Ly8gVGhlIHJlcXVpcmUgZnVuY3Rpb25cbiBcdGZ1bmN0aW9uIF9fd2VicGFja19yZXF1aXJlX18obW9kdWxlSWQpIHtcblxuIFx0XHQvLyBDaGVjayBpZiBtb2R1bGUgaXMgaW4gY2FjaGVcbiBcdFx0aWYoaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0pIHtcbiBcdFx0XHRyZXR1cm4gaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0uZXhwb3J0cztcbiBcdFx0fVxuIFx0XHQvLyBDcmVhdGUgYSBuZXcgbW9kdWxlIChhbmQgcHV0IGl0IGludG8gdGhlIGNhY2hlKVxuIFx0XHR2YXIgbW9kdWxlID0gaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0gPSB7XG4gXHRcdFx0aTogbW9kdWxlSWQsXG4gXHRcdFx0bDogZmFsc2UsXG4gXHRcdFx0ZXhwb3J0czoge31cbiBcdFx0fTtcblxuIFx0XHQvLyBFeGVjdXRlIHRoZSBtb2R1bGUgZnVuY3Rpb25cbiBcdFx0bW9kdWxlc1ttb2R1bGVJZF0uY2FsbChtb2R1bGUuZXhwb3J0cywgbW9kdWxlLCBtb2R1bGUuZXhwb3J0cywgX193ZWJwYWNrX3JlcXVpcmVfXyk7XG5cbiBcdFx0Ly8gRmxhZyB0aGUgbW9kdWxlIGFzIGxvYWRlZFxuIFx0XHRtb2R1bGUubCA9IHRydWU7XG5cbiBcdFx0Ly8gUmV0dXJuIHRoZSBleHBvcnRzIG9mIHRoZSBtb2R1bGVcbiBcdFx0cmV0dXJuIG1vZHVsZS5leHBvcnRzO1xuIFx0fVxuXG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlcyBvYmplY3QgKF9fd2VicGFja19tb2R1bGVzX18pXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLm0gPSBtb2R1bGVzO1xuXG4gXHQvLyBleHBvc2UgdGhlIG1vZHVsZSBjYWNoZVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5jID0gaW5zdGFsbGVkTW9kdWxlcztcblxuIFx0Ly8gZGVmaW5lIGdldHRlciBmdW5jdGlvbiBmb3IgaGFybW9ueSBleHBvcnRzXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLmQgPSBmdW5jdGlvbihleHBvcnRzLCBuYW1lLCBnZXR0ZXIpIHtcbiBcdFx0aWYoIV9fd2VicGFja19yZXF1aXJlX18ubyhleHBvcnRzLCBuYW1lKSkge1xuIFx0XHRcdE9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBuYW1lLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZ2V0dGVyIH0pO1xuIFx0XHR9XG4gXHR9O1xuXG4gXHQvLyBkZWZpbmUgX19lc01vZHVsZSBvbiBleHBvcnRzXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLnIgPSBmdW5jdGlvbihleHBvcnRzKSB7XG4gXHRcdGlmKHR5cGVvZiBTeW1ib2wgIT09ICd1bmRlZmluZWQnICYmIFN5bWJvbC50b1N0cmluZ1RhZykge1xuIFx0XHRcdE9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBTeW1ib2wudG9TdHJpbmdUYWcsIHsgdmFsdWU6ICdNb2R1bGUnIH0pO1xuIFx0XHR9XG4gXHRcdE9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCAnX19lc01vZHVsZScsIHsgdmFsdWU6IHRydWUgfSk7XG4gXHR9O1xuXG4gXHQvLyBjcmVhdGUgYSBmYWtlIG5hbWVzcGFjZSBvYmplY3RcbiBcdC8vIG1vZGUgJiAxOiB2YWx1ZSBpcyBhIG1vZHVsZSBpZCwgcmVxdWlyZSBpdFxuIFx0Ly8gbW9kZSAmIDI6IG1lcmdlIGFsbCBwcm9wZXJ0aWVzIG9mIHZhbHVlIGludG8gdGhlIG5zXG4gXHQvLyBtb2RlICYgNDogcmV0dXJuIHZhbHVlIHdoZW4gYWxyZWFkeSBucyBvYmplY3RcbiBcdC8vIG1vZGUgJiA4fDE6IGJlaGF2ZSBsaWtlIHJlcXVpcmVcbiBcdF9fd2VicGFja19yZXF1aXJlX18udCA9IGZ1bmN0aW9uKHZhbHVlLCBtb2RlKSB7XG4gXHRcdGlmKG1vZGUgJiAxKSB2YWx1ZSA9IF9fd2VicGFja19yZXF1aXJlX18odmFsdWUpO1xuIFx0XHRpZihtb2RlICYgOCkgcmV0dXJuIHZhbHVlO1xuIFx0XHRpZigobW9kZSAmIDQpICYmIHR5cGVvZiB2YWx1ZSA9PT0gJ29iamVjdCcgJiYgdmFsdWUgJiYgdmFsdWUuX19lc01vZHVsZSkgcmV0dXJuIHZhbHVlO1xuIFx0XHR2YXIgbnMgPSBPYmplY3QuY3JlYXRlKG51bGwpO1xuIFx0XHRfX3dlYnBhY2tfcmVxdWlyZV9fLnIobnMpO1xuIFx0XHRPYmplY3QuZGVmaW5lUHJvcGVydHkobnMsICdkZWZhdWx0JywgeyBlbnVtZXJhYmxlOiB0cnVlLCB2YWx1ZTogdmFsdWUgfSk7XG4gXHRcdGlmKG1vZGUgJiAyICYmIHR5cGVvZiB2YWx1ZSAhPSAnc3RyaW5nJykgZm9yKHZhciBrZXkgaW4gdmFsdWUpIF9fd2VicGFja19yZXF1aXJlX18uZChucywga2V5LCBmdW5jdGlvbihrZXkpIHsgcmV0dXJuIHZhbHVlW2tleV07IH0uYmluZChudWxsLCBrZXkpKTtcbiBcdFx0cmV0dXJuIG5zO1xuIFx0fTtcblxuIFx0Ly8gZ2V0RGVmYXVsdEV4cG9ydCBmdW5jdGlvbiBmb3IgY29tcGF0aWJpbGl0eSB3aXRoIG5vbi1oYXJtb255IG1vZHVsZXNcbiBcdF9fd2VicGFja19yZXF1aXJlX18ubiA9IGZ1bmN0aW9uKG1vZHVsZSkge1xuIFx0XHR2YXIgZ2V0dGVyID0gbW9kdWxlICYmIG1vZHVsZS5fX2VzTW9kdWxlID9cbiBcdFx0XHRmdW5jdGlvbiBnZXREZWZhdWx0KCkgeyByZXR1cm4gbW9kdWxlWydkZWZhdWx0J107IH0gOlxuIFx0XHRcdGZ1bmN0aW9uIGdldE1vZHVsZUV4cG9ydHMoKSB7IHJldHVybiBtb2R1bGU7IH07XG4gXHRcdF9fd2VicGFja19yZXF1aXJlX18uZChnZXR0ZXIsICdhJywgZ2V0dGVyKTtcbiBcdFx0cmV0dXJuIGdldHRlcjtcbiBcdH07XG5cbiBcdC8vIE9iamVjdC5wcm90b3R5cGUuaGFzT3duUHJvcGVydHkuY2FsbFxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5vID0gZnVuY3Rpb24ob2JqZWN0LCBwcm9wZXJ0eSkgeyByZXR1cm4gT2JqZWN0LnByb3RvdHlwZS5oYXNPd25Qcm9wZXJ0eS5jYWxsKG9iamVjdCwgcHJvcGVydHkpOyB9O1xuXG4gXHQvLyBfX3dlYnBhY2tfcHVibGljX3BhdGhfX1xuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5wID0gXCJcIjtcblxuXG4gXHQvLyBMb2FkIGVudHJ5IG1vZHVsZSBhbmQgcmV0dXJuIGV4cG9ydHNcbiBcdHJldHVybiBfX3dlYnBhY2tfcmVxdWlyZV9fKF9fd2VicGFja19yZXF1aXJlX18ucyA9IDApO1xuIiwiaW1wb3J0IHtzZWxlY3QgYXMgZDNTZWxlY3R9IGZyb20gXCJkMy1zZWxlY3Rpb25cIjtcbmltcG9ydCBQbHVnaW4gZnJvbSBcIi4uL1BsdWdpblwiO1xuXG4vKipcbiAqIEJ1YmJsZSBjb21wYXJlIGRpYWdyYW0gcGx1Z2luLjxicj5cbiAqIENvbXBhcmUgZGF0YSAzLWRpbWVuc2lvbmFsIHdheXM6IHgtYXhpcywgeS1heGlzICYgYnViYmxlLXNpemUuXG4gKiAtICoqTk9URToqKlxuICogICAtIFBsdWdpbnMgYXJlbid0IGJ1aWx0LWluLiBOZWVkIHRvIGJlIGxvYWRlZCBvciBpbXBvcnRlZCB0byBiZSB1c2VkLlxuICogICAtIE5vbiByZXF1aXJlZCBtb2R1bGVzIGZyb20gYmlsbGJvYXJkLmpzIGNvcmUsIG5lZWQgdG8gYmUgaW5zdGFsbGVkIHNlcGFyYXRlbHkuXG4gKiAtICoqUmVxdWlyZWQgbW9kdWxlczoqKlxuICogICAtIFtkMy1zZWxlY3Rpb25dKGh0dHBzOi8vZ2l0aHViLmNvbS9kMy9kMy1zZWxlY3Rpb24pXG4gKiBAY2xhc3MgcGx1Z2luLWJ1YmJsZWNvbXBhcmVcbiAqIEByZXF1aXJlcyBkMy1zZWxlY3Rpb25cbiAqIEBwYXJhbSB7T2JqZWN0fSBvcHRpb25zIGJ1YmJsZSBjb21wYXJlIHBsdWdpbiBvcHRpb25zXG4gKiBAZXh0ZW5kcyBQbHVnaW5cbiAqIEByZXR1cm4ge0J1YmJsZUNvbXBhcmV9XG4gKiBAZXhhbXBsZVxuICogIHZhciBjaGFydCA9IGJiLmdlbmVyYXRlKHtcbiAqICAgICBkYXRhOiB7XG4gKiAgICAgICAgY29sdW1uczogWyAuLi4gXSxcbiAqICAgICAgICB0eXBlOiBcImJ1YmJsZVwiXG4gKiAgICAgfVxuICogICAgIC4uLlxuICogICAgIHBsdWdpbnM6IFtcbiAqICAgICAgICBuZXcgYmIucGx1Z2luLmJ1YmJsZWNvbXBhcmUoe1xuICogICAgICAgICAgbWluUjogMTEsXG4gKiAgICAgICAgICBtYXhSOiA3NCxcbiAqICAgICAgICAgIGV4cGFuZFNjYWxlOiAxLjFcbiAqICAgICAgICB9KSxcbiAqICAgICBdXG4gKiAgfSk7XG4gICogQGV4YW1wbGVcbiAqXHRpbXBvcnQge2JifSBmcm9tIFwiYmlsbGJvYXJkLmpzXCI7XG4gKiBpbXBvcnQgQnViYmxlQ29tcGFyZSBmcm9tIFwiYmlsbGJvYXJkLmpzL2Rpc3QvYmlsbGJvYXJkanMtcGx1Z2luLWJ1YmJsZWNvbXBhcmVcIjtcbiAqXG4gKiBiYi5nZW5lcmF0ZSh7XG4gKiAgICAgcGx1Z2luczogW1xuICogICAgICAgIG5ldyBCdWJibGVDb21wYXJlKHsgLi4uIH0pXG4gKiAgICAgXVxuICogfSlcbiAqL1xuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBCdWJibGVDb21wYXJlIGV4dGVuZHMgUGx1Z2luIHtcblx0c3RhdGljIHZlcnNpb24gPSBgMC4wLjFgO1xuXG5cdGNvbnN0cnVjdG9yKG9wdGlvbnMpIHtcblx0XHRzdXBlcihvcHRpb25zKTtcblxuXHRcdHJldHVybiB0aGlzO1xuXHR9XG5cblx0JGluaXQoKSB7XG5cdFx0Y29uc3QgeyQkfSA9IHRoaXM7XG5cblx0XHQkJC5maW5kQ2xvc2VzdCA9IHRoaXMuZmluZENsb3Nlc3QuYmluZCh0aGlzKTtcblx0XHQkJC5nZXRCdWJibGVSID0gdGhpcy5nZXRCdWJibGVSLmJpbmQodGhpcyk7XG5cdFx0JCQucG9pbnRFeHBhbmRlZFIgPSB0aGlzLnBvaW50RXhwYW5kZWRSLmJpbmQodGhpcyk7XG5cdH1cblxuXHRwb2ludEV4cGFuZGVkUihkKSB7XG5cdFx0Y29uc3QgYmFzZVIgPSB0aGlzLmdldEJ1YmJsZVIoZCk7XG5cdFx0Y29uc3Qge2V4cGFuZFNjYWxlID0gMX0gPSB0aGlzLm9wdGlvbnM7XG5cblx0XHRCdWJibGVDb21wYXJlLnJhaXNlRm9jdXNlZEJ1YmJsZUxheWVyKGQpO1xuXHRcdHRoaXMuY2hhbmdlQ3Vyc29yUG9pbnQoKTtcblxuXHRcdHJldHVybiBiYXNlUiAqIGV4cGFuZFNjYWxlO1xuXHR9XG5cblx0c3RhdGljIHJhaXNlRm9jdXNlZEJ1YmJsZUxheWVyKGQpIHtcblx0XHRkLnJhaXNlICYmIGQzU2VsZWN0KGQubm9kZSgpLnBhcmVudE5vZGUucGFyZW50Tm9kZSkucmFpc2UoKTtcblx0fVxuXG5cdGNoYW5nZUN1cnNvclBvaW50KCkge1xuXHRcdHRoaXMuJCQuc3ZnLnNlbGVjdChgLmJiLWV2ZW50LXJlY3RgKS5zdHlsZShcImN1cnNvclwiLCBcInBvaW50ZXJcIik7XG5cdH1cblxuXHRmaW5kQ2xvc2VzdCh2YWx1ZXMsIHBvcykge1xuXHRcdGNvbnN0IHskJH0gPSB0aGlzO1xuXG5cdFx0cmV0dXJuIHZhbHVlc1xuXHRcdFx0LmZpbHRlcih2ID0+IHYgJiYgISQkLmlzQmFyVHlwZSh2LmlkKSlcblx0XHRcdC5yZWR1Y2UoKGFjYywgY3VyKSA9PiB7XG5cdFx0XHRcdGNvbnN0IGQgPSAkJC5kaXN0KGN1ciwgcG9zKTtcblxuXHRcdFx0XHRyZXR1cm4gZCA8IHRoaXMuZ2V0QnViYmxlUihjdXIpID8gY3VyIDogYWNjO1xuXHRcdFx0fSwgMCk7XG5cdH1cblxuXHRnZXRCdWJibGVSKGQpIHtcblx0XHRjb25zdCB7bWluUiwgbWF4Un0gPSB0aGlzLm9wdGlvbnM7XG5cdFx0Y29uc3QgY3VyVmFsID0gdGhpcy5nZXRaRGF0YShkKTtcblxuXHRcdGlmICghY3VyVmFsKSByZXR1cm4gbWluUjtcblxuXHRcdGNvbnN0IFttaW4sIG1heF0gPSB0aGlzLiQkLmRhdGEudGFyZ2V0cy5yZWR1Y2UoXG5cdFx0XHQoW2FjY01pbiwgYWNjTWF4XSwgY3VyKSA9PiB7XG5cdFx0XHRcdGNvbnN0IHZhbCA9IHRoaXMuZ2V0WkRhdGEoY3VyLnZhbHVlc1swXSk7XG5cblx0XHRcdFx0cmV0dXJuIFtNYXRoLm1pbihhY2NNaW4sIHZhbCksIE1hdGgubWF4KGFjY01heCwgdmFsKV07XG5cdFx0XHR9LFxuXHRcdFx0WzEwMDAwLCAwXVxuXHRcdCk7XG5cdFx0Y29uc3Qgc2l6ZSA9IG1pbiA+IDAgJiYgbWF4ID09PSBtaW4gPyAwIDogY3VyVmFsIC8gbWF4O1xuXG5cdFx0cmV0dXJuIE1hdGguYWJzKHNpemUpICogKG1heFIgLSBtaW5SKSArIG1pblI7XG5cdH1cblxuXHRnZXRaRGF0YShkKSB7XG5cdFx0cmV0dXJuIHRoaXMuJCQuaXNCdWJibGVaVHlwZShkKSA/XG5cdFx0XHR0aGlzLiQkLmdldEJ1YmJsZVpEYXRhKGQudmFsdWUsIFwielwiKSA6XG5cdFx0XHRkLnZhbHVlO1xuXHR9XG59XG4iLCJpbXBvcnQgYXJyYXlMaWtlVG9BcnJheSBmcm9tIFwiLi9hcnJheUxpa2VUb0FycmF5XCI7XG5leHBvcnQgZGVmYXVsdCBmdW5jdGlvbiBfdW5zdXBwb3J0ZWRJdGVyYWJsZVRvQXJyYXkobywgbWluTGVuKSB7XG4gIGlmICghbykgcmV0dXJuO1xuICBpZiAodHlwZW9mIG8gPT09IFwic3RyaW5nXCIpIHJldHVybiBhcnJheUxpa2VUb0FycmF5KG8sIG1pbkxlbik7XG4gIHZhciBuID0gT2JqZWN0LnByb3RvdHlwZS50b1N0cmluZy5jYWxsKG8pLnNsaWNlKDgsIC0xKTtcbiAgaWYgKG4gPT09IFwiT2JqZWN0XCIgJiYgby5jb25zdHJ1Y3RvcikgbiA9IG8uY29uc3RydWN0b3IubmFtZTtcbiAgaWYgKG4gPT09IFwiTWFwXCIgfHwgbiA9PT0gXCJTZXRcIikgcmV0dXJuIEFycmF5LmZyb20obyk7XG4gIGlmIChuID09PSBcIkFyZ3VtZW50c1wiIHx8IC9eKD86VWl8SSludCg/Ojh8MTZ8MzIpKD86Q2xhbXBlZCk/QXJyYXkkLy50ZXN0KG4pKSByZXR1cm4gYXJyYXlMaWtlVG9BcnJheShvLCBtaW5MZW4pO1xufSIsImV4cG9ydCBkZWZhdWx0IGZ1bmN0aW9uIF9hcnJheUxpa2VUb0FycmF5KGFyciwgbGVuKSB7XG4gIGlmIChsZW4gPT0gbnVsbCB8fCBsZW4gPiBhcnIubGVuZ3RoKSBsZW4gPSBhcnIubGVuZ3RoO1xuXG4gIGZvciAodmFyIGkgPSAwLCBhcnIyID0gbmV3IEFycmF5KGxlbik7IGkgPCBsZW47IGkrKykge1xuICAgIGFycjJbaV0gPSBhcnJbaV07XG4gIH1cblxuICByZXR1cm4gYXJyMjtcbn0iLCJleHBvcnQgZGVmYXVsdCBmdW5jdGlvbiBfY2xhc3NDYWxsQ2hlY2soaW5zdGFuY2UsIENvbnN0cnVjdG9yKSB7XG4gIGlmICghKGluc3RhbmNlIGluc3RhbmNlb2YgQ29uc3RydWN0b3IpKSB7XG4gICAgdGhyb3cgbmV3IFR5cGVFcnJvcihcIkNhbm5vdCBjYWxsIGEgY2xhc3MgYXMgYSBmdW5jdGlvblwiKTtcbiAgfVxufSIsImZ1bmN0aW9uIF9kZWZpbmVQcm9wZXJ0aWVzKHRhcmdldCwgcHJvcHMpIHtcbiAgZm9yICh2YXIgaSA9IDA7IGkgPCBwcm9wcy5sZW5ndGg7IGkrKykge1xuICAgIHZhciBkZXNjcmlwdG9yID0gcHJvcHNbaV07XG4gICAgZGVzY3JpcHRvci5lbnVtZXJhYmxlID0gZGVzY3JpcHRvci5lbnVtZXJhYmxlIHx8IGZhbHNlO1xuICAgIGRlc2NyaXB0b3IuY29uZmlndXJhYmxlID0gdHJ1ZTtcbiAgICBpZiAoXCJ2YWx1ZVwiIGluIGRlc2NyaXB0b3IpIGRlc2NyaXB0b3Iud3JpdGFibGUgPSB0cnVlO1xuICAgIE9iamVjdC5kZWZpbmVQcm9wZXJ0eSh0YXJnZXQsIGRlc2NyaXB0b3Iua2V5LCBkZXNjcmlwdG9yKTtcbiAgfVxufVxuXG5leHBvcnQgZGVmYXVsdCBmdW5jdGlvbiBfY3JlYXRlQ2xhc3MoQ29uc3RydWN0b3IsIHByb3RvUHJvcHMsIHN0YXRpY1Byb3BzKSB7XG4gIGlmIChwcm90b1Byb3BzKSBfZGVmaW5lUHJvcGVydGllcyhDb25zdHJ1Y3Rvci5wcm90b3R5cGUsIHByb3RvUHJvcHMpO1xuICBpZiAoc3RhdGljUHJvcHMpIF9kZWZpbmVQcm9wZXJ0aWVzKENvbnN0cnVjdG9yLCBzdGF0aWNQcm9wcyk7XG4gIHJldHVybiBDb25zdHJ1Y3Rvcjtcbn0iLCJleHBvcnQgZGVmYXVsdCBmdW5jdGlvbiBfYXNzZXJ0VGhpc0luaXRpYWxpemVkKHNlbGYpIHtcbiAgaWYgKHNlbGYgPT09IHZvaWQgMCkge1xuICAgIHRocm93IG5ldyBSZWZlcmVuY2VFcnJvcihcInRoaXMgaGFzbid0IGJlZW4gaW5pdGlhbGlzZWQgLSBzdXBlcigpIGhhc24ndCBiZWVuIGNhbGxlZFwiKTtcbiAgfVxuXG4gIHJldHVybiBzZWxmO1xufSIsImltcG9ydCBfdHlwZW9mIGZyb20gXCIuLi8uLi9oZWxwZXJzL2VzbS90eXBlb2ZcIjtcbmltcG9ydCBhc3NlcnRUaGlzSW5pdGlhbGl6ZWQgZnJvbSBcIi4vYXNzZXJ0VGhpc0luaXRpYWxpemVkXCI7XG5leHBvcnQgZGVmYXVsdCBmdW5jdGlvbiBfcG9zc2libGVDb25zdHJ1Y3RvclJldHVybihzZWxmLCBjYWxsKSB7XG4gIGlmIChjYWxsICYmIChfdHlwZW9mKGNhbGwpID09PSBcIm9iamVjdFwiIHx8IHR5cGVvZiBjYWxsID09PSBcImZ1bmN0aW9uXCIpKSB7XG4gICAgcmV0dXJuIGNhbGw7XG4gIH1cblxuICByZXR1cm4gYXNzZXJ0VGhpc0luaXRpYWxpemVkKHNlbGYpO1xufSIsImV4cG9ydCBkZWZhdWx0IGZ1bmN0aW9uIF90eXBlb2Yob2JqKSB7XG4gIFwiQGJhYmVsL2hlbHBlcnMgLSB0eXBlb2ZcIjtcblxuICBpZiAodHlwZW9mIFN5bWJvbCA9PT0gXCJmdW5jdGlvblwiICYmIHR5cGVvZiBTeW1ib2wuaXRlcmF0b3IgPT09IFwic3ltYm9sXCIpIHtcbiAgICBfdHlwZW9mID0gZnVuY3Rpb24gX3R5cGVvZihvYmopIHtcbiAgICAgIHJldHVybiB0eXBlb2Ygb2JqO1xuICAgIH07XG4gIH0gZWxzZSB7XG4gICAgX3R5cGVvZiA9IGZ1bmN0aW9uIF90eXBlb2Yob2JqKSB7XG4gICAgICByZXR1cm4gb2JqICYmIHR5cGVvZiBTeW1ib2wgPT09IFwiZnVuY3Rpb25cIiAmJiBvYmouY29uc3RydWN0b3IgPT09IFN5bWJvbCAmJiBvYmogIT09IFN5bWJvbC5wcm90b3R5cGUgPyBcInN5bWJvbFwiIDogdHlwZW9mIG9iajtcbiAgICB9O1xuICB9XG5cbiAgcmV0dXJuIF90eXBlb2Yob2JqKTtcbn0iLCJleHBvcnQgZGVmYXVsdCBmdW5jdGlvbiBfZ2V0UHJvdG90eXBlT2Yobykge1xuICBfZ2V0UHJvdG90eXBlT2YgPSBPYmplY3Quc2V0UHJvdG90eXBlT2YgPyBPYmplY3QuZ2V0UHJvdG90eXBlT2YgOiBmdW5jdGlvbiBfZ2V0UHJvdG90eXBlT2Yobykge1xuICAgIHJldHVybiBvLl9fcHJvdG9fXyB8fCBPYmplY3QuZ2V0UHJvdG90eXBlT2Yobyk7XG4gIH07XG4gIHJldHVybiBfZ2V0UHJvdG90eXBlT2Yobyk7XG59IiwiZXhwb3J0IGRlZmF1bHQgZnVuY3Rpb24gX2RlZmluZVByb3BlcnR5KG9iaiwga2V5LCB2YWx1ZSkge1xuICBpZiAoa2V5IGluIG9iaikge1xuICAgIE9iamVjdC5kZWZpbmVQcm9wZXJ0eShvYmosIGtleSwge1xuICAgICAgdmFsdWU6IHZhbHVlLFxuICAgICAgZW51bWVyYWJsZTogdHJ1ZSxcbiAgICAgIGNvbmZpZ3VyYWJsZTogdHJ1ZSxcbiAgICAgIHdyaXRhYmxlOiB0cnVlXG4gICAgfSk7XG4gIH0gZWxzZSB7XG4gICAgb2JqW2tleV0gPSB2YWx1ZTtcbiAgfVxuXG4gIHJldHVybiBvYmo7XG59IiwibW9kdWxlLmV4cG9ydHMgPSBfX1dFQlBBQ0tfRVhURVJOQUxfTU9EVUxFX18xMF9fOyIsIi8qKlxuICogQ29weXJpZ2h0IChjKSAyMDE3IH4gcHJlc2VudCBOQVZFUiBDb3JwLlxuICogYmlsbGJvYXJkLmpzIHByb2plY3QgaXMgbGljZW5zZWQgdW5kZXIgdGhlIE1JVCBsaWNlbnNlXG4gKi9cbi8qKlxuICogQmFzZSBjbGFzcyB0byBnZW5lcmF0ZSBiaWxsYm9hcmQuanMgcGx1Z2luXG4gKiBAY2xhc3MgUGx1Z2luXG4gKi9cbmV4cG9ydCBkZWZhdWx0IGNsYXNzIFBsdWdpbiB7XG5cdC8qKlxuXHQgKiBWZXJzaW9uIGluZm8gc3RyaW5nIGZvciBwbHVnaW5cblx0ICogQG5hbWUgdmVyc2lvblxuXHQgKiBAc3RhdGljXG5cdCAqIEBtZW1iZXJvZiBQbHVnaW5cblx0ICogQHR5cGUge1N0cmluZ31cblx0ICogQGV4YW1wbGVcblx0ICogICBiYi5wbHVnaW4uc3RhbmZvcmQudmVyc2lvbjsgIC8vIGV4KSAxLjkuMFxuXHQgKi9cblx0c3RhdGljIHZlcnNpb24gPSBcIjEuMTIuMTJcIjtcblxuXHQvKipcblx0ICogQ29uc3RydWN0b3Jcblx0ICogQHBhcmFtIHtBbnl9IGNvbmZpZyBjb25maWcgb3B0aW9uIG9iamVjdFxuXHQgKiBAcHJpdmF0ZVxuXHQgKi9cblx0Y29uc3RydWN0b3Iob3B0aW9ucyA9IHt9KSB7XG5cdFx0dGhpcy5vcHRpb25zID0gb3B0aW9ucztcblx0fVxuXG5cdC8qKlxuXHQgKiBMaWZlY3ljbGUgaG9vayBmb3IgJ2JlZm9yZUluaXQnIHBoYXNlLlxuXHQgKiBAcHJpdmF0ZVxuXHQgKi9cblx0JGJlZm9yZUluaXQoKSB7fVxuXG5cdC8qKlxuXHQgKiBMaWZlY3ljbGUgaG9vayBmb3IgJ2luaXQnIHBoYXNlLlxuXHQgKiBAcHJpdmF0ZVxuXHQgKi9cblx0JGluaXQoKSB7fVxuXG5cdC8qKlxuXHQgKiBMaWZlY3ljbGUgaG9vayBmb3IgJ2FmdGVySW5pdCcgcGhhc2UuXG5cdCAqIEBwcml2YXRlXG5cdCAqL1xuXHQkYWZ0ZXJJbml0KCkge31cblxuXHQvKipcblx0ICogTGlmZWN5Y2xlIGhvb2sgZm9yICdyZWRyYXcnIHBoYXNlLlxuXHQgKiBAcHJpdmF0ZVxuXHQgKi9cblx0JHJlZHJhdygpIHt9XG5cblx0LyoqXG5cdCAqIExpZmVjeWNsZSBob29rIGZvciAnd2lsbERlc3Ryb3knIHBoYXNlLlxuXHQgKiBAcHJpdmF0ZVxuXHQgKi9cblx0JHdpbGxEZXN0cm95KCkge1xuXHRcdE9iamVjdC5rZXlzKHRoaXMpLmZvckVhY2goa2V5ID0+IHtcblx0XHRcdHRoaXNba2V5XSA9IG51bGw7XG5cdFx0XHRkZWxldGUgdGhpc1trZXldO1xuXHRcdH0pO1xuXHR9XG59XG4iLCJleHBvcnQgZGVmYXVsdCBmdW5jdGlvbiBfYXJyYXlXaXRoSG9sZXMoYXJyKSB7XG4gIGlmIChBcnJheS5pc0FycmF5KGFycikpIHJldHVybiBhcnI7XG59IiwiZXhwb3J0IGRlZmF1bHQgZnVuY3Rpb24gX2l0ZXJhYmxlVG9BcnJheUxpbWl0KGFyciwgaSkge1xuICBpZiAodHlwZW9mIFN5bWJvbCA9PT0gXCJ1bmRlZmluZWRcIiB8fCAhKFN5bWJvbC5pdGVyYXRvciBpbiBPYmplY3QoYXJyKSkpIHJldHVybjtcbiAgdmFyIF9hcnIgPSBbXTtcbiAgdmFyIF9uID0gdHJ1ZTtcbiAgdmFyIF9kID0gZmFsc2U7XG4gIHZhciBfZSA9IHVuZGVmaW5lZDtcblxuICB0cnkge1xuICAgIGZvciAodmFyIF9pID0gYXJyW1N5bWJvbC5pdGVyYXRvcl0oKSwgX3M7ICEoX24gPSAoX3MgPSBfaS5uZXh0KCkpLmRvbmUpOyBfbiA9IHRydWUpIHtcbiAgICAgIF9hcnIucHVzaChfcy52YWx1ZSk7XG5cbiAgICAgIGlmIChpICYmIF9hcnIubGVuZ3RoID09PSBpKSBicmVhaztcbiAgICB9XG4gIH0gY2F0Y2ggKGVycikge1xuICAgIF9kID0gdHJ1ZTtcbiAgICBfZSA9IGVycjtcbiAgfSBmaW5hbGx5IHtcbiAgICB0cnkge1xuICAgICAgaWYgKCFfbiAmJiBfaVtcInJldHVyblwiXSAhPSBudWxsKSBfaVtcInJldHVyblwiXSgpO1xuICAgIH0gZmluYWxseSB7XG4gICAgICBpZiAoX2QpIHRocm93IF9lO1xuICAgIH1cbiAgfVxuXG4gIHJldHVybiBfYXJyO1xufSIsImV4cG9ydCBkZWZhdWx0IGZ1bmN0aW9uIF9ub25JdGVyYWJsZVJlc3QoKSB7XG4gIHRocm93IG5ldyBUeXBlRXJyb3IoXCJJbnZhbGlkIGF0dGVtcHQgdG8gZGVzdHJ1Y3R1cmUgbm9uLWl0ZXJhYmxlIGluc3RhbmNlLlxcbkluIG9yZGVyIHRvIGJlIGl0ZXJhYmxlLCBub24tYXJyYXkgb2JqZWN0cyBtdXN0IGhhdmUgYSBbU3ltYm9sLml0ZXJhdG9yXSgpIG1ldGhvZC5cIik7XG59IiwiaW1wb3J0IGFycmF5V2l0aEhvbGVzIGZyb20gXCIuL2FycmF5V2l0aEhvbGVzXCI7XG5pbXBvcnQgaXRlcmFibGVUb0FycmF5TGltaXQgZnJvbSBcIi4vaXRlcmFibGVUb0FycmF5TGltaXRcIjtcbmltcG9ydCB1bnN1cHBvcnRlZEl0ZXJhYmxlVG9BcnJheSBmcm9tIFwiLi91bnN1cHBvcnRlZEl0ZXJhYmxlVG9BcnJheVwiO1xuaW1wb3J0IG5vbkl0ZXJhYmxlUmVzdCBmcm9tIFwiLi9ub25JdGVyYWJsZVJlc3RcIjtcbmV4cG9ydCBkZWZhdWx0IGZ1bmN0aW9uIF9zbGljZWRUb0FycmF5KGFyciwgaSkge1xuICByZXR1cm4gYXJyYXlXaXRoSG9sZXMoYXJyKSB8fCBpdGVyYWJsZVRvQXJyYXlMaW1pdChhcnIsIGkpIHx8IHVuc3VwcG9ydGVkSXRlcmFibGVUb0FycmF5KGFyciwgaSkgfHwgbm9uSXRlcmFibGVSZXN0KCk7XG59IiwiZXhwb3J0IGRlZmF1bHQgZnVuY3Rpb24gX3NldFByb3RvdHlwZU9mKG8sIHApIHtcbiAgX3NldFByb3RvdHlwZU9mID0gT2JqZWN0LnNldFByb3RvdHlwZU9mIHx8IGZ1bmN0aW9uIF9zZXRQcm90b3R5cGVPZihvLCBwKSB7XG4gICAgby5fX3Byb3RvX18gPSBwO1xuICAgIHJldHVybiBvO1xuICB9O1xuXG4gIHJldHVybiBfc2V0UHJvdG90eXBlT2YobywgcCk7XG59IiwiaW1wb3J0IHNldFByb3RvdHlwZU9mIGZyb20gXCIuL3NldFByb3RvdHlwZU9mXCI7XG5leHBvcnQgZGVmYXVsdCBmdW5jdGlvbiBfaW5oZXJpdHMoc3ViQ2xhc3MsIHN1cGVyQ2xhc3MpIHtcbiAgaWYgKHR5cGVvZiBzdXBlckNsYXNzICE9PSBcImZ1bmN0aW9uXCIgJiYgc3VwZXJDbGFzcyAhPT0gbnVsbCkge1xuICAgIHRocm93IG5ldyBUeXBlRXJyb3IoXCJTdXBlciBleHByZXNzaW9uIG11c3QgZWl0aGVyIGJlIG51bGwgb3IgYSBmdW5jdGlvblwiKTtcbiAgfVxuXG4gIHN1YkNsYXNzLnByb3RvdHlwZSA9IE9iamVjdC5jcmVhdGUoc3VwZXJDbGFzcyAmJiBzdXBlckNsYXNzLnByb3RvdHlwZSwge1xuICAgIGNvbnN0cnVjdG9yOiB7XG4gICAgICB2YWx1ZTogc3ViQ2xhc3MsXG4gICAgICB3cml0YWJsZTogdHJ1ZSxcbiAgICAgIGNvbmZpZ3VyYWJsZTogdHJ1ZVxuICAgIH1cbiAgfSk7XG4gIGlmIChzdXBlckNsYXNzKSBzZXRQcm90b3R5cGVPZihzdWJDbGFzcywgc3VwZXJDbGFzcyk7XG59Il0sInNvdXJjZVJvb3QiOiIifQ==
/***/},
/***/348764:
/***/function(__unused_webpack_module,exports,__webpack_require__){"use strict";var base64=__webpack_require__(679742),ieee754=__webpack_require__(680645),customInspectSymbol="function"===typeof Symbol&&"function"===typeof Symbol["for"]?Symbol["for"]("nodejs.util.inspect.custom"):null;
/*!
 * The buffer module from node.js, for the browser.
 *
 * @author   Feross Aboukhadijeh <https://feross.org>
 * @license  MIT
 */
/* eslint-disable no-proto */exports.lW=Buffer,exports.h2=50;var K_MAX_LENGTH=2147483647;function typedArraySupport(){
// Can typed array instances can be augmented?
try{var arr=new Uint8Array(1),proto={foo:function(){return 42}};return Object.setPrototypeOf(proto,Uint8Array.prototype),Object.setPrototypeOf(arr,proto),42===arr.foo()}catch(e){return!1}}function createBuffer(length){if(length>K_MAX_LENGTH)throw new RangeError('The value "'+length+'" is invalid for option "size"');
// Return an augmented `Uint8Array` instance
var buf=new Uint8Array(length);return Object.setPrototypeOf(buf,Buffer.prototype),buf}
/**
 * The Buffer constructor returns instances of `Uint8Array` that have their
 * prototype changed to `Buffer.prototype`. Furthermore, `Buffer` is a subclass of
 * `Uint8Array`, so the returned instances will have all the node `Buffer` methods
 * and the `Uint8Array` methods. Square bracket notation works as expected -- it
 * returns a single octet.
 *
 * The `Uint8Array` prototype remains unmodified.
 */function Buffer(arg,encodingOrOffset,length){
// Common case.
if("number"===typeof arg){if("string"===typeof encodingOrOffset)throw new TypeError('The "string" argument must be of type string. Received type number');return allocUnsafe(arg)}return from(arg,encodingOrOffset,length)}// not used by this implementation
function from(value,encodingOrOffset,length){if("string"===typeof value)return fromString(value,encodingOrOffset);if(ArrayBuffer.isView(value))return fromArrayView(value);if(null==value)throw new TypeError("The first argument must be one of type string, Buffer, ArrayBuffer, Array, or Array-like Object. Received type "+typeof value);if(isInstance(value,ArrayBuffer)||value&&isInstance(value.buffer,ArrayBuffer))return fromArrayBuffer(value,encodingOrOffset,length);if("undefined"!==typeof SharedArrayBuffer&&(isInstance(value,SharedArrayBuffer)||value&&isInstance(value.buffer,SharedArrayBuffer)))return fromArrayBuffer(value,encodingOrOffset,length);if("number"===typeof value)throw new TypeError('The "value" argument must not be of type number. Received type number');var valueOf=value.valueOf&&value.valueOf();if(null!=valueOf&&valueOf!==value)return Buffer.from(valueOf,encodingOrOffset,length);var b=fromObject(value);if(b)return b;if("undefined"!==typeof Symbol&&null!=Symbol.toPrimitive&&"function"===typeof value[Symbol.toPrimitive])return Buffer.from(value[Symbol.toPrimitive]("string"),encodingOrOffset,length);throw new TypeError("The first argument must be one of type string, Buffer, ArrayBuffer, Array, or Array-like Object. Received type "+typeof value)}
/**
 * Functionally equivalent to Buffer(arg, encoding) but throws a TypeError
 * if value is a number.
 * Buffer.from(str[, encoding])
 * Buffer.from(array)
 * Buffer.from(buffer)
 * Buffer.from(arrayBuffer[, byteOffset[, length]])
 **/function assertSize(size){if("number"!==typeof size)throw new TypeError('"size" argument must be of type number');if(size<0)throw new RangeError('The value "'+size+'" is invalid for option "size"')}function alloc(size,fill,encoding){return assertSize(size),size<=0?createBuffer(size):void 0!==fill?"string"===typeof encoding?createBuffer(size).fill(fill,encoding):createBuffer(size).fill(fill):createBuffer(size)}
/**
 * Creates a new filled Buffer instance.
 * alloc(size[, fill[, encoding]])
 **/function allocUnsafe(size){return assertSize(size),createBuffer(size<0?0:0|checked(size))}
/**
 * Equivalent to Buffer(num), by default creates a non-zero-filled Buffer instance.
 * */function fromString(string,encoding){if("string"===typeof encoding&&""!==encoding||(encoding="utf8"),!Buffer.isEncoding(encoding))throw new TypeError("Unknown encoding: "+encoding);var length=0|byteLength(string,encoding),buf=createBuffer(length),actual=buf.write(string,encoding);return actual!==length&&(
// Writing a hex string, for example, that contains invalid characters will
// cause everything after the first invalid character to be ignored. (e.g.
// 'abxxcd' will be treated as 'ab')
buf=buf.slice(0,actual)),buf}function fromArrayLike(array){for(var length=array.length<0?0:0|checked(array.length),buf=createBuffer(length),i=0;i<length;i+=1)buf[i]=255&array[i];return buf}function fromArrayView(arrayView){if(isInstance(arrayView,Uint8Array)){var copy=new Uint8Array(arrayView);return fromArrayBuffer(copy.buffer,copy.byteOffset,copy.byteLength)}return fromArrayLike(arrayView)}function fromArrayBuffer(array,byteOffset,length){if(byteOffset<0||array.byteLength<byteOffset)throw new RangeError('"offset" is outside of buffer bounds');if(array.byteLength<byteOffset+(length||0))throw new RangeError('"length" is outside of buffer bounds');var buf;return buf=void 0===byteOffset&&void 0===length?new Uint8Array(array):void 0===length?new Uint8Array(array,byteOffset):new Uint8Array(array,byteOffset,length),
// Return an augmented `Uint8Array` instance
Object.setPrototypeOf(buf,Buffer.prototype),buf}function fromObject(obj){if(Buffer.isBuffer(obj)){var len=0|checked(obj.length),buf=createBuffer(len);return 0===buf.length||obj.copy(buf,0,0,len),buf}return void 0!==obj.length?"number"!==typeof obj.length||numberIsNaN(obj.length)?createBuffer(0):fromArrayLike(obj):"Buffer"===obj.type&&Array.isArray(obj.data)?fromArrayLike(obj.data):void 0}function checked(length){
// Note: cannot use `length < K_MAX_LENGTH` here because that fails when
// length is NaN (which is otherwise coerced to zero.)
if(length>=K_MAX_LENGTH)throw new RangeError("Attempt to allocate Buffer larger than maximum size: 0x"+K_MAX_LENGTH.toString(16)+" bytes");return 0|length}function SlowBuffer(length){return+length!=length&&(// eslint-disable-line eqeqeq
length=0),Buffer.alloc(+length)}function byteLength(string,encoding){if(Buffer.isBuffer(string))return string.length;if(ArrayBuffer.isView(string)||isInstance(string,ArrayBuffer))return string.byteLength;if("string"!==typeof string)throw new TypeError('The "string" argument must be one of type string, Buffer, or ArrayBuffer. Received type '+typeof string);var len=string.length,mustMatch=arguments.length>2&&!0===arguments[2];if(!mustMatch&&0===len)return 0;
// Use a for loop to avoid recursion
for(var loweredCase=!1;;)switch(encoding){case"ascii":case"latin1":case"binary":return len;case"utf8":case"utf-8":return utf8ToBytes(string).length;case"ucs2":case"ucs-2":case"utf16le":case"utf-16le":return 2*len;case"hex":return len>>>1;case"base64":return base64ToBytes(string).length;default:if(loweredCase)return mustMatch?-1:utf8ToBytes(string).length;// assume utf8
encoding=(""+encoding).toLowerCase(),loweredCase=!0}}function slowToString(encoding,start,end){var loweredCase=!1;
// No need to verify that "this.length <= MAX_UINT32" since it's a read-only
// property of a typed array.
// This behaves neither like String nor Uint8Array in that we set start/end
// to their upper/lower bounds if the value passed is out of range.
// undefined is handled specially as per ECMA-262 6th Edition,
// Section 13.3.3.7 Runtime Semantics: KeyedBindingInitialization.
// Return early if start > this.length. Done here to prevent potential uint32
// coercion fail below.
if((void 0===start||start<0)&&(start=0),start>this.length)return"";if((void 0===end||end>this.length)&&(end=this.length),end<=0)return"";
// Force coercion to uint32. This will also coerce falsey/NaN values to 0.
if(end>>>=0,start>>>=0,end<=start)return"";encoding||(encoding="utf8");while(1)switch(encoding){case"hex":return hexSlice(this,start,end);case"utf8":case"utf-8":return utf8Slice(this,start,end);case"ascii":return asciiSlice(this,start,end);case"latin1":case"binary":return latin1Slice(this,start,end);case"base64":return base64Slice(this,start,end);case"ucs2":case"ucs-2":case"utf16le":case"utf-16le":return utf16leSlice(this,start,end);default:if(loweredCase)throw new TypeError("Unknown encoding: "+encoding);encoding=(encoding+"").toLowerCase(),loweredCase=!0}}
// This property is used by `Buffer.isBuffer` (and the `is-buffer` npm package)
// to detect a Buffer instance. It's not possible to use `instanceof Buffer`
// reliably in a browserify context because there could be multiple different
// copies of the 'buffer' package in use. This method works even for Buffer
// instances that were created from another copy of the `buffer` package.
// See: https://github.com/feross/buffer/issues/154
function swap(b,n,m){var i=b[n];b[n]=b[m],b[m]=i}
// Finds either the first index of `val` in `buffer` at offset >= `byteOffset`,
// OR the last index of `val` in `buffer` at offset <= `byteOffset`.
// Arguments:
// - buffer - a Buffer to search
// - val - a string, Buffer, or number
// - byteOffset - an index into `buffer`; will be clamped to an int32
// - encoding - an optional encoding, relevant is val is a string
// - dir - true for indexOf, false for lastIndexOf
function bidirectionalIndexOf(buffer,val,byteOffset,encoding,dir){
// Empty buffer means no match
if(0===buffer.length)return-1;
// Normalize byteOffset
if("string"===typeof byteOffset?(encoding=byteOffset,byteOffset=0):byteOffset>2147483647?byteOffset=2147483647:byteOffset<-2147483648&&(byteOffset=-2147483648),byteOffset=+byteOffset,// Coerce to Number.
numberIsNaN(byteOffset)&&(
// byteOffset: it it's undefined, null, NaN, "foo", etc, search whole buffer
byteOffset=dir?0:buffer.length-1),
// Normalize byteOffset: negative offsets start from the end of the buffer
byteOffset<0&&(byteOffset=buffer.length+byteOffset),byteOffset>=buffer.length){if(dir)return-1;byteOffset=buffer.length-1}else if(byteOffset<0){if(!dir)return-1;byteOffset=0}
// Normalize val
// Finally, search either indexOf (if dir is true) or lastIndexOf
if("string"===typeof val&&(val=Buffer.from(val,encoding)),Buffer.isBuffer(val))
// Special case: looking for empty string/buffer always fails
return 0===val.length?-1:arrayIndexOf(buffer,val,byteOffset,encoding,dir);if("number"===typeof val)// Search for a byte value [0-255]
return val&=255,"function"===typeof Uint8Array.prototype.indexOf?dir?Uint8Array.prototype.indexOf.call(buffer,val,byteOffset):Uint8Array.prototype.lastIndexOf.call(buffer,val,byteOffset):arrayIndexOf(buffer,[val],byteOffset,encoding,dir);throw new TypeError("val must be string, number or Buffer")}function arrayIndexOf(arr,val,byteOffset,encoding,dir){var i,indexSize=1,arrLength=arr.length,valLength=val.length;if(void 0!==encoding&&(encoding=String(encoding).toLowerCase(),"ucs2"===encoding||"ucs-2"===encoding||"utf16le"===encoding||"utf-16le"===encoding)){if(arr.length<2||val.length<2)return-1;indexSize=2,arrLength/=2,valLength/=2,byteOffset/=2}function read(buf,i){return 1===indexSize?buf[i]:buf.readUInt16BE(i*indexSize)}if(dir){var foundIndex=-1;for(i=byteOffset;i<arrLength;i++)if(read(arr,i)===read(val,-1===foundIndex?0:i-foundIndex)){if(-1===foundIndex&&(foundIndex=i),i-foundIndex+1===valLength)return foundIndex*indexSize}else-1!==foundIndex&&(i-=i-foundIndex),foundIndex=-1}else for(byteOffset+valLength>arrLength&&(byteOffset=arrLength-valLength),i=byteOffset;i>=0;i--){for(var found=!0,j=0;j<valLength;j++)if(read(arr,i+j)!==read(val,j)){found=!1;break}if(found)return i}return-1}function hexWrite(buf,string,offset,length){offset=Number(offset)||0;var remaining=buf.length-offset;length?(length=Number(length),length>remaining&&(length=remaining)):length=remaining;var strLen=string.length;length>strLen/2&&(length=strLen/2);for(var i=0;i<length;++i){var parsed=parseInt(string.substr(2*i,2),16);if(numberIsNaN(parsed))return i;buf[offset+i]=parsed}return i}function utf8Write(buf,string,offset,length){return blitBuffer(utf8ToBytes(string,buf.length-offset),buf,offset,length)}function asciiWrite(buf,string,offset,length){return blitBuffer(asciiToBytes(string),buf,offset,length)}function base64Write(buf,string,offset,length){return blitBuffer(base64ToBytes(string),buf,offset,length)}function ucs2Write(buf,string,offset,length){return blitBuffer(utf16leToBytes(string,buf.length-offset),buf,offset,length)}function base64Slice(buf,start,end){return 0===start&&end===buf.length?base64.fromByteArray(buf):base64.fromByteArray(buf.slice(start,end))}function utf8Slice(buf,start,end){end=Math.min(buf.length,end);var res=[],i=start;while(i<end){var secondByte,thirdByte,fourthByte,tempCodePoint,firstByte=buf[i],codePoint=null,bytesPerSequence=firstByte>239?4:firstByte>223?3:firstByte>191?2:1;if(i+bytesPerSequence<=end)switch(bytesPerSequence){case 1:firstByte<128&&(codePoint=firstByte);break;case 2:secondByte=buf[i+1],128===(192&secondByte)&&(tempCodePoint=(31&firstByte)<<6|63&secondByte,tempCodePoint>127&&(codePoint=tempCodePoint));break;case 3:secondByte=buf[i+1],thirdByte=buf[i+2],128===(192&secondByte)&&128===(192&thirdByte)&&(tempCodePoint=(15&firstByte)<<12|(63&secondByte)<<6|63&thirdByte,tempCodePoint>2047&&(tempCodePoint<55296||tempCodePoint>57343)&&(codePoint=tempCodePoint));break;case 4:secondByte=buf[i+1],thirdByte=buf[i+2],fourthByte=buf[i+3],128===(192&secondByte)&&128===(192&thirdByte)&&128===(192&fourthByte)&&(tempCodePoint=(15&firstByte)<<18|(63&secondByte)<<12|(63&thirdByte)<<6|63&fourthByte,tempCodePoint>65535&&tempCodePoint<1114112&&(codePoint=tempCodePoint))}null===codePoint?(
// we did not generate a valid codePoint so insert a
// replacement char (U+FFFD) and advance only 1 byte
codePoint=65533,bytesPerSequence=1):codePoint>65535&&(
// encode to utf16 (surrogate pair dance)
codePoint-=65536,res.push(codePoint>>>10&1023|55296),codePoint=56320|1023&codePoint),res.push(codePoint),i+=bytesPerSequence}return decodeCodePointsArray(res)}
// Based on http://stackoverflow.com/a/22747272/680742, the browser with
// the lowest limit is Chrome, with 0x10000 args.
// We go 1 magnitude less, for safety
/**
 * If `Buffer.TYPED_ARRAY_SUPPORT`:
 *   === true    Use Uint8Array implementation (fastest)
 *   === false   Print warning and recommend using `buffer` v4.x which has an Object
 *               implementation (most compatible, even IE6)
 *
 * Browsers that support typed arrays are IE 10+, Firefox 4+, Chrome 7+, Safari 5.1+,
 * Opera 11.6+, iOS 4.2+.
 *
 * We report that the browser does not support typed arrays if the are not subclassable
 * using __proto__. Firefox 4-29 lacks support for adding new properties to `Uint8Array`
 * (See: https://bugzilla.mozilla.org/show_bug.cgi?id=695438). IE 10 lacks support
 * for __proto__ and has a buggy typed array implementation.
 */
Buffer.TYPED_ARRAY_SUPPORT=typedArraySupport(),!Buffer.TYPED_ARRAY_SUPPORT&&"undefined"!==typeof console&&console.error,Object.defineProperty(Buffer.prototype,"parent",{enumerable:!0,get:function(){if(Buffer.isBuffer(this))return this.buffer}}),Object.defineProperty(Buffer.prototype,"offset",{enumerable:!0,get:function(){if(Buffer.isBuffer(this))return this.byteOffset}}),Buffer.poolSize=8192,Buffer.from=function(value,encodingOrOffset,length){return from(value,encodingOrOffset,length)}
// Note: Change prototype *after* Buffer.from is defined to workaround Chrome bug:
// https://github.com/feross/buffer/pull/148
,Object.setPrototypeOf(Buffer.prototype,Uint8Array.prototype),Object.setPrototypeOf(Buffer,Uint8Array),Buffer.alloc=function(size,fill,encoding){return alloc(size,fill,encoding)},Buffer.allocUnsafe=function(size){return allocUnsafe(size)}
/**
 * Equivalent to SlowBuffer(num), by default creates a non-zero-filled Buffer instance.
 */,Buffer.allocUnsafeSlow=function(size){return allocUnsafe(size)},Buffer.isBuffer=function(b){return null!=b&&!0===b._isBuffer&&b!==Buffer.prototype;// so Buffer.isBuffer(Buffer.prototype) will be false
},Buffer.compare=function(a,b){if(isInstance(a,Uint8Array)&&(a=Buffer.from(a,a.offset,a.byteLength)),isInstance(b,Uint8Array)&&(b=Buffer.from(b,b.offset,b.byteLength)),!Buffer.isBuffer(a)||!Buffer.isBuffer(b))throw new TypeError('The "buf1", "buf2" arguments must be one of type Buffer or Uint8Array');if(a===b)return 0;for(var x=a.length,y=b.length,i=0,len=Math.min(x,y);i<len;++i)if(a[i]!==b[i]){x=a[i],y=b[i];break}return x<y?-1:y<x?1:0},Buffer.isEncoding=function(encoding){switch(String(encoding).toLowerCase()){case"hex":case"utf8":case"utf-8":case"ascii":case"latin1":case"binary":case"base64":case"ucs2":case"ucs-2":case"utf16le":case"utf-16le":return!0;default:return!1}},Buffer.concat=function(list,length){if(!Array.isArray(list))throw new TypeError('"list" argument must be an Array of Buffers');if(0===list.length)return Buffer.alloc(0);var i;if(void 0===length)for(length=0,i=0;i<list.length;++i)length+=list[i].length;var buffer=Buffer.allocUnsafe(length),pos=0;for(i=0;i<list.length;++i){var buf=list[i];if(isInstance(buf,Uint8Array))pos+buf.length>buffer.length?Buffer.from(buf).copy(buffer,pos):Uint8Array.prototype.set.call(buffer,buf,pos);else{if(!Buffer.isBuffer(buf))throw new TypeError('"list" argument must be an Array of Buffers');buf.copy(buffer,pos)}pos+=buf.length}return buffer},Buffer.byteLength=byteLength,Buffer.prototype._isBuffer=!0,Buffer.prototype.swap16=function(){var len=this.length;if(len%2!==0)throw new RangeError("Buffer size must be a multiple of 16-bits");for(var i=0;i<len;i+=2)swap(this,i,i+1);return this},Buffer.prototype.swap32=function(){var len=this.length;if(len%4!==0)throw new RangeError("Buffer size must be a multiple of 32-bits");for(var i=0;i<len;i+=4)swap(this,i,i+3),swap(this,i+1,i+2);return this},Buffer.prototype.swap64=function(){var len=this.length;if(len%8!==0)throw new RangeError("Buffer size must be a multiple of 64-bits");for(var i=0;i<len;i+=8)swap(this,i,i+7),swap(this,i+1,i+6),swap(this,i+2,i+5),swap(this,i+3,i+4);return this},Buffer.prototype.toString=function(){var length=this.length;return 0===length?"":0===arguments.length?utf8Slice(this,0,length):slowToString.apply(this,arguments)},Buffer.prototype.toLocaleString=Buffer.prototype.toString,Buffer.prototype.equals=function(b){if(!Buffer.isBuffer(b))throw new TypeError("Argument must be a Buffer");return this===b||0===Buffer.compare(this,b)},Buffer.prototype.inspect=function(){var str="",max=exports.h2;return str=this.toString("hex",0,max).replace(/(.{2})/g,"$1 ").trim(),this.length>max&&(str+=" ... "),"<Buffer "+str+">"},customInspectSymbol&&(Buffer.prototype[customInspectSymbol]=Buffer.prototype.inspect),Buffer.prototype.compare=function(target,start,end,thisStart,thisEnd){if(isInstance(target,Uint8Array)&&(target=Buffer.from(target,target.offset,target.byteLength)),!Buffer.isBuffer(target))throw new TypeError('The "target" argument must be one of type Buffer or Uint8Array. Received type '+typeof target);if(void 0===start&&(start=0),void 0===end&&(end=target?target.length:0),void 0===thisStart&&(thisStart=0),void 0===thisEnd&&(thisEnd=this.length),start<0||end>target.length||thisStart<0||thisEnd>this.length)throw new RangeError("out of range index");if(thisStart>=thisEnd&&start>=end)return 0;if(thisStart>=thisEnd)return-1;if(start>=end)return 1;if(start>>>=0,end>>>=0,thisStart>>>=0,thisEnd>>>=0,this===target)return 0;for(var x=thisEnd-thisStart,y=end-start,len=Math.min(x,y),thisCopy=this.slice(thisStart,thisEnd),targetCopy=target.slice(start,end),i=0;i<len;++i)if(thisCopy[i]!==targetCopy[i]){x=thisCopy[i],y=targetCopy[i];break}return x<y?-1:y<x?1:0},Buffer.prototype.includes=function(val,byteOffset,encoding){return-1!==this.indexOf(val,byteOffset,encoding)},Buffer.prototype.indexOf=function(val,byteOffset,encoding){return bidirectionalIndexOf(this,val,byteOffset,encoding,!0)},Buffer.prototype.lastIndexOf=function(val,byteOffset,encoding){return bidirectionalIndexOf(this,val,byteOffset,encoding,!1)},Buffer.prototype.write=function(string,offset,length,encoding){
// Buffer#write(string)
if(void 0===offset)encoding="utf8",length=this.length,offset=0;else if(void 0===length&&"string"===typeof offset)encoding=offset,length=this.length,offset=0;else{if(!isFinite(offset))throw new Error("Buffer.write(string, encoding, offset[, length]) is no longer supported");offset>>>=0,isFinite(length)?(length>>>=0,void 0===encoding&&(encoding="utf8")):(encoding=length,length=void 0)}var remaining=this.length-offset;if((void 0===length||length>remaining)&&(length=remaining),string.length>0&&(length<0||offset<0)||offset>this.length)throw new RangeError("Attempt to write outside buffer bounds");encoding||(encoding="utf8");for(var loweredCase=!1;;)switch(encoding){case"hex":return hexWrite(this,string,offset,length);case"utf8":case"utf-8":return utf8Write(this,string,offset,length);case"ascii":case"latin1":case"binary":return asciiWrite(this,string,offset,length);case"base64":
// Warning: maxLength not taken into account in base64Write
return base64Write(this,string,offset,length);case"ucs2":case"ucs-2":case"utf16le":case"utf-16le":return ucs2Write(this,string,offset,length);default:if(loweredCase)throw new TypeError("Unknown encoding: "+encoding);encoding=(""+encoding).toLowerCase(),loweredCase=!0}},Buffer.prototype.toJSON=function(){return{type:"Buffer",data:Array.prototype.slice.call(this._arr||this,0)}};var MAX_ARGUMENTS_LENGTH=4096;function decodeCodePointsArray(codePoints){var len=codePoints.length;if(len<=MAX_ARGUMENTS_LENGTH)return String.fromCharCode.apply(String,codePoints);// avoid extra slice()
// Decode in chunks to avoid "call stack size exceeded".
var res="",i=0;while(i<len)res+=String.fromCharCode.apply(String,codePoints.slice(i,i+=MAX_ARGUMENTS_LENGTH));return res}function asciiSlice(buf,start,end){var ret="";end=Math.min(buf.length,end);for(var i=start;i<end;++i)ret+=String.fromCharCode(127&buf[i]);return ret}function latin1Slice(buf,start,end){var ret="";end=Math.min(buf.length,end);for(var i=start;i<end;++i)ret+=String.fromCharCode(buf[i]);return ret}function hexSlice(buf,start,end){var len=buf.length;(!start||start<0)&&(start=0),(!end||end<0||end>len)&&(end=len);for(var out="",i=start;i<end;++i)out+=hexSliceLookupTable[buf[i]];return out}function utf16leSlice(buf,start,end){
// If bytes.length is odd, the last 8 bits must be ignored (same as node.js)
for(var bytes=buf.slice(start,end),res="",i=0;i<bytes.length-1;i+=2)res+=String.fromCharCode(bytes[i]+256*bytes[i+1]);return res}
/*
 * Need to make sure that buffer isn't trying to write out of bounds.
 */
function checkOffset(offset,ext,length){if(offset%1!==0||offset<0)throw new RangeError("offset is not uint");if(offset+ext>length)throw new RangeError("Trying to access beyond buffer length")}function checkInt(buf,value,offset,ext,max,min){if(!Buffer.isBuffer(buf))throw new TypeError('"buffer" argument must be a Buffer instance');if(value>max||value<min)throw new RangeError('"value" argument is out of bounds');if(offset+ext>buf.length)throw new RangeError("Index out of range")}function checkIEEE754(buf,value,offset,ext,max,min){if(offset+ext>buf.length)throw new RangeError("Index out of range");if(offset<0)throw new RangeError("Index out of range")}function writeFloat(buf,value,offset,littleEndian,noAssert){return value=+value,offset>>>=0,noAssert||checkIEEE754(buf,value,offset,4,34028234663852886e22,-34028234663852886e22),ieee754.write(buf,value,offset,littleEndian,23,4),offset+4}function writeDouble(buf,value,offset,littleEndian,noAssert){return value=+value,offset>>>=0,noAssert||checkIEEE754(buf,value,offset,8,17976931348623157e292,-17976931348623157e292),ieee754.write(buf,value,offset,littleEndian,52,8),offset+8}Buffer.prototype.slice=function(start,end){var len=this.length;start=~~start,end=void 0===end?len:~~end,start<0?(start+=len,start<0&&(start=0)):start>len&&(start=len),end<0?(end+=len,end<0&&(end=0)):end>len&&(end=len),end<start&&(end=start);var newBuf=this.subarray(start,end);
// Return an augmented `Uint8Array` instance
return Object.setPrototypeOf(newBuf,Buffer.prototype),newBuf},Buffer.prototype.readUintLE=Buffer.prototype.readUIntLE=function(offset,byteLength,noAssert){offset>>>=0,byteLength>>>=0,noAssert||checkOffset(offset,byteLength,this.length);var val=this[offset],mul=1,i=0;while(++i<byteLength&&(mul*=256))val+=this[offset+i]*mul;return val},Buffer.prototype.readUintBE=Buffer.prototype.readUIntBE=function(offset,byteLength,noAssert){offset>>>=0,byteLength>>>=0,noAssert||checkOffset(offset,byteLength,this.length);var val=this[offset+--byteLength],mul=1;while(byteLength>0&&(mul*=256))val+=this[offset+--byteLength]*mul;return val},Buffer.prototype.readUint8=Buffer.prototype.readUInt8=function(offset,noAssert){return offset>>>=0,noAssert||checkOffset(offset,1,this.length),this[offset]},Buffer.prototype.readUint16LE=Buffer.prototype.readUInt16LE=function(offset,noAssert){return offset>>>=0,noAssert||checkOffset(offset,2,this.length),this[offset]|this[offset+1]<<8},Buffer.prototype.readUint16BE=Buffer.prototype.readUInt16BE=function(offset,noAssert){return offset>>>=0,noAssert||checkOffset(offset,2,this.length),this[offset]<<8|this[offset+1]},Buffer.prototype.readUint32LE=Buffer.prototype.readUInt32LE=function(offset,noAssert){return offset>>>=0,noAssert||checkOffset(offset,4,this.length),(this[offset]|this[offset+1]<<8|this[offset+2]<<16)+16777216*this[offset+3]},Buffer.prototype.readUint32BE=Buffer.prototype.readUInt32BE=function(offset,noAssert){return offset>>>=0,noAssert||checkOffset(offset,4,this.length),16777216*this[offset]+(this[offset+1]<<16|this[offset+2]<<8|this[offset+3])},Buffer.prototype.readIntLE=function(offset,byteLength,noAssert){offset>>>=0,byteLength>>>=0,noAssert||checkOffset(offset,byteLength,this.length);var val=this[offset],mul=1,i=0;while(++i<byteLength&&(mul*=256))val+=this[offset+i]*mul;return mul*=128,val>=mul&&(val-=Math.pow(2,8*byteLength)),val},Buffer.prototype.readIntBE=function(offset,byteLength,noAssert){offset>>>=0,byteLength>>>=0,noAssert||checkOffset(offset,byteLength,this.length);var i=byteLength,mul=1,val=this[offset+--i];while(i>0&&(mul*=256))val+=this[offset+--i]*mul;return mul*=128,val>=mul&&(val-=Math.pow(2,8*byteLength)),val},Buffer.prototype.readInt8=function(offset,noAssert){return offset>>>=0,noAssert||checkOffset(offset,1,this.length),128&this[offset]?-1*(255-this[offset]+1):this[offset]},Buffer.prototype.readInt16LE=function(offset,noAssert){offset>>>=0,noAssert||checkOffset(offset,2,this.length);var val=this[offset]|this[offset+1]<<8;return 32768&val?4294901760|val:val},Buffer.prototype.readInt16BE=function(offset,noAssert){offset>>>=0,noAssert||checkOffset(offset,2,this.length);var val=this[offset+1]|this[offset]<<8;return 32768&val?4294901760|val:val},Buffer.prototype.readInt32LE=function(offset,noAssert){return offset>>>=0,noAssert||checkOffset(offset,4,this.length),this[offset]|this[offset+1]<<8|this[offset+2]<<16|this[offset+3]<<24},Buffer.prototype.readInt32BE=function(offset,noAssert){return offset>>>=0,noAssert||checkOffset(offset,4,this.length),this[offset]<<24|this[offset+1]<<16|this[offset+2]<<8|this[offset+3]},Buffer.prototype.readFloatLE=function(offset,noAssert){return offset>>>=0,noAssert||checkOffset(offset,4,this.length),ieee754.read(this,offset,!0,23,4)},Buffer.prototype.readFloatBE=function(offset,noAssert){return offset>>>=0,noAssert||checkOffset(offset,4,this.length),ieee754.read(this,offset,!1,23,4)},Buffer.prototype.readDoubleLE=function(offset,noAssert){return offset>>>=0,noAssert||checkOffset(offset,8,this.length),ieee754.read(this,offset,!0,52,8)},Buffer.prototype.readDoubleBE=function(offset,noAssert){return offset>>>=0,noAssert||checkOffset(offset,8,this.length),ieee754.read(this,offset,!1,52,8)},Buffer.prototype.writeUintLE=Buffer.prototype.writeUIntLE=function(value,offset,byteLength,noAssert){if(value=+value,offset>>>=0,byteLength>>>=0,!noAssert){var maxBytes=Math.pow(2,8*byteLength)-1;checkInt(this,value,offset,byteLength,maxBytes,0)}var mul=1,i=0;this[offset]=255&value;while(++i<byteLength&&(mul*=256))this[offset+i]=value/mul&255;return offset+byteLength},Buffer.prototype.writeUintBE=Buffer.prototype.writeUIntBE=function(value,offset,byteLength,noAssert){if(value=+value,offset>>>=0,byteLength>>>=0,!noAssert){var maxBytes=Math.pow(2,8*byteLength)-1;checkInt(this,value,offset,byteLength,maxBytes,0)}var i=byteLength-1,mul=1;this[offset+i]=255&value;while(--i>=0&&(mul*=256))this[offset+i]=value/mul&255;return offset+byteLength},Buffer.prototype.writeUint8=Buffer.prototype.writeUInt8=function(value,offset,noAssert){return value=+value,offset>>>=0,noAssert||checkInt(this,value,offset,1,255,0),this[offset]=255&value,offset+1},Buffer.prototype.writeUint16LE=Buffer.prototype.writeUInt16LE=function(value,offset,noAssert){return value=+value,offset>>>=0,noAssert||checkInt(this,value,offset,2,65535,0),this[offset]=255&value,this[offset+1]=value>>>8,offset+2},Buffer.prototype.writeUint16BE=Buffer.prototype.writeUInt16BE=function(value,offset,noAssert){return value=+value,offset>>>=0,noAssert||checkInt(this,value,offset,2,65535,0),this[offset]=value>>>8,this[offset+1]=255&value,offset+2},Buffer.prototype.writeUint32LE=Buffer.prototype.writeUInt32LE=function(value,offset,noAssert){return value=+value,offset>>>=0,noAssert||checkInt(this,value,offset,4,4294967295,0),this[offset+3]=value>>>24,this[offset+2]=value>>>16,this[offset+1]=value>>>8,this[offset]=255&value,offset+4},Buffer.prototype.writeUint32BE=Buffer.prototype.writeUInt32BE=function(value,offset,noAssert){return value=+value,offset>>>=0,noAssert||checkInt(this,value,offset,4,4294967295,0),this[offset]=value>>>24,this[offset+1]=value>>>16,this[offset+2]=value>>>8,this[offset+3]=255&value,offset+4},Buffer.prototype.writeIntLE=function(value,offset,byteLength,noAssert){if(value=+value,offset>>>=0,!noAssert){var limit=Math.pow(2,8*byteLength-1);checkInt(this,value,offset,byteLength,limit-1,-limit)}var i=0,mul=1,sub=0;this[offset]=255&value;while(++i<byteLength&&(mul*=256))value<0&&0===sub&&0!==this[offset+i-1]&&(sub=1),this[offset+i]=(value/mul>>0)-sub&255;return offset+byteLength},Buffer.prototype.writeIntBE=function(value,offset,byteLength,noAssert){if(value=+value,offset>>>=0,!noAssert){var limit=Math.pow(2,8*byteLength-1);checkInt(this,value,offset,byteLength,limit-1,-limit)}var i=byteLength-1,mul=1,sub=0;this[offset+i]=255&value;while(--i>=0&&(mul*=256))value<0&&0===sub&&0!==this[offset+i+1]&&(sub=1),this[offset+i]=(value/mul>>0)-sub&255;return offset+byteLength},Buffer.prototype.writeInt8=function(value,offset,noAssert){return value=+value,offset>>>=0,noAssert||checkInt(this,value,offset,1,127,-128),value<0&&(value=255+value+1),this[offset]=255&value,offset+1},Buffer.prototype.writeInt16LE=function(value,offset,noAssert){return value=+value,offset>>>=0,noAssert||checkInt(this,value,offset,2,32767,-32768),this[offset]=255&value,this[offset+1]=value>>>8,offset+2},Buffer.prototype.writeInt16BE=function(value,offset,noAssert){return value=+value,offset>>>=0,noAssert||checkInt(this,value,offset,2,32767,-32768),this[offset]=value>>>8,this[offset+1]=255&value,offset+2},Buffer.prototype.writeInt32LE=function(value,offset,noAssert){return value=+value,offset>>>=0,noAssert||checkInt(this,value,offset,4,2147483647,-2147483648),this[offset]=255&value,this[offset+1]=value>>>8,this[offset+2]=value>>>16,this[offset+3]=value>>>24,offset+4},Buffer.prototype.writeInt32BE=function(value,offset,noAssert){return value=+value,offset>>>=0,noAssert||checkInt(this,value,offset,4,2147483647,-2147483648),value<0&&(value=4294967295+value+1),this[offset]=value>>>24,this[offset+1]=value>>>16,this[offset+2]=value>>>8,this[offset+3]=255&value,offset+4},Buffer.prototype.writeFloatLE=function(value,offset,noAssert){return writeFloat(this,value,offset,!0,noAssert)},Buffer.prototype.writeFloatBE=function(value,offset,noAssert){return writeFloat(this,value,offset,!1,noAssert)},Buffer.prototype.writeDoubleLE=function(value,offset,noAssert){return writeDouble(this,value,offset,!0,noAssert)},Buffer.prototype.writeDoubleBE=function(value,offset,noAssert){return writeDouble(this,value,offset,!1,noAssert)}
// copy(targetBuffer, targetStart=0, sourceStart=0, sourceEnd=buffer.length)
,Buffer.prototype.copy=function(target,targetStart,start,end){if(!Buffer.isBuffer(target))throw new TypeError("argument should be a Buffer");
// Copy 0 bytes; we're done
if(start||(start=0),end||0===end||(end=this.length),targetStart>=target.length&&(targetStart=target.length),targetStart||(targetStart=0),end>0&&end<start&&(end=start),end===start)return 0;if(0===target.length||0===this.length)return 0;
// Fatal error conditions
if(targetStart<0)throw new RangeError("targetStart out of bounds");if(start<0||start>=this.length)throw new RangeError("Index out of range");if(end<0)throw new RangeError("sourceEnd out of bounds");
// Are we oob?
end>this.length&&(end=this.length),target.length-targetStart<end-start&&(end=target.length-targetStart+start);var len=end-start;return this===target&&"function"===typeof Uint8Array.prototype.copyWithin?
// Use built-in when available, missing from IE11
this.copyWithin(targetStart,start,end):Uint8Array.prototype.set.call(target,this.subarray(start,end),targetStart),len}
// Usage:
//    buffer.fill(number[, offset[, end]])
//    buffer.fill(buffer[, offset[, end]])
//    buffer.fill(string[, offset[, end]][, encoding])
,Buffer.prototype.fill=function(val,start,end,encoding){
// Handle string cases:
if("string"===typeof val){if("string"===typeof start?(encoding=start,start=0,end=this.length):"string"===typeof end&&(encoding=end,end=this.length),void 0!==encoding&&"string"!==typeof encoding)throw new TypeError("encoding must be a string");if("string"===typeof encoding&&!Buffer.isEncoding(encoding))throw new TypeError("Unknown encoding: "+encoding);if(1===val.length){var code=val.charCodeAt(0);("utf8"===encoding&&code<128||"latin1"===encoding)&&(
// Fast path: If `val` fits into a single byte, use that numeric value.
val=code)}}else"number"===typeof val?val&=255:"boolean"===typeof val&&(val=Number(val));
// Invalid ranges are not set to a default, so can range check early.
if(start<0||this.length<start||this.length<end)throw new RangeError("Out of range index");if(end<=start)return this;var i;if(start>>>=0,end=void 0===end?this.length:end>>>0,val||(val=0),"number"===typeof val)for(i=start;i<end;++i)this[i]=val;else{var bytes=Buffer.isBuffer(val)?val:Buffer.from(val,encoding),len=bytes.length;if(0===len)throw new TypeError('The value "'+val+'" is invalid for argument "value"');for(i=0;i<end-start;++i)this[i+start]=bytes[i%len]}return this}
// HELPER FUNCTIONS
// ================;
var INVALID_BASE64_RE=/[^+/0-9A-Za-z-_]/g;function base64clean(str){
// Node converts strings with length < 2 to ''
if(
// Node takes equal signs as end of the Base64 encoding
str=str.split("=")[0],
// Node strips out invalid characters like \n and \t from the string, base64-js does not
str=str.trim().replace(INVALID_BASE64_RE,""),str.length<2)return"";
// Node allows for non-padded base64 strings (missing trailing ===), base64-js does not
while(str.length%4!==0)str+="=";return str}function utf8ToBytes(string,units){var codePoint;units=units||1/0;for(var length=string.length,leadSurrogate=null,bytes=[],i=0;i<length;++i){
// is surrogate component
if(codePoint=string.charCodeAt(i),codePoint>55295&&codePoint<57344){
// last char was a lead
if(!leadSurrogate){
// no lead yet
if(codePoint>56319){
// unexpected trail
(units-=3)>-1&&bytes.push(239,191,189);continue}
// valid lead
if(i+1===length){
// unpaired lead
(units-=3)>-1&&bytes.push(239,191,189);continue}leadSurrogate=codePoint;continue}
// 2 leads in a row
if(codePoint<56320){(units-=3)>-1&&bytes.push(239,191,189),leadSurrogate=codePoint;continue}
// valid surrogate pair
codePoint=65536+(leadSurrogate-55296<<10|codePoint-56320)}else leadSurrogate&&(units-=3)>-1&&bytes.push(239,191,189);
// encode utf8
if(leadSurrogate=null,codePoint<128){if((units-=1)<0)break;bytes.push(codePoint)}else if(codePoint<2048){if((units-=2)<0)break;bytes.push(codePoint>>6|192,63&codePoint|128)}else if(codePoint<65536){if((units-=3)<0)break;bytes.push(codePoint>>12|224,codePoint>>6&63|128,63&codePoint|128)}else{if(!(codePoint<1114112))throw new Error("Invalid code point");if((units-=4)<0)break;bytes.push(codePoint>>18|240,codePoint>>12&63|128,codePoint>>6&63|128,63&codePoint|128)}}return bytes}function asciiToBytes(str){for(var byteArray=[],i=0;i<str.length;++i)
// Node's code seems to be doing this and not & 0x7F..
byteArray.push(255&str.charCodeAt(i));return byteArray}function utf16leToBytes(str,units){for(var c,hi,lo,byteArray=[],i=0;i<str.length;++i){if((units-=2)<0)break;c=str.charCodeAt(i),hi=c>>8,lo=c%256,byteArray.push(lo),byteArray.push(hi)}return byteArray}function base64ToBytes(str){return base64.toByteArray(base64clean(str))}function blitBuffer(src,dst,offset,length){for(var i=0;i<length;++i){if(i+offset>=dst.length||i>=src.length)break;dst[i+offset]=src[i]}return i}
// ArrayBuffer or Uint8Array objects from other contexts (i.e. iframes) do not pass
// the `instanceof` check but they should be treated as of that type.
// See: https://github.com/feross/buffer/issues/166
function isInstance(obj,type){return obj instanceof type||null!=obj&&null!=obj.constructor&&null!=obj.constructor.name&&obj.constructor.name===type.name}function numberIsNaN(obj){
// For IE11 support
return obj!==obj;// eslint-disable-line no-self-compare
}
// Create lookup table for `toString('hex')`
// See: https://github.com/feross/buffer/issues/219
var hexSliceLookupTable=function(){for(var alphabet="0123456789abcdef",table=new Array(256),i=0;i<16;++i)for(var i16=16*i,j=0;j<16;++j)table[i16+j]=alphabet[i]+alphabet[j];return table}()
/***/}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/chunk-common-7237a82e.js.map