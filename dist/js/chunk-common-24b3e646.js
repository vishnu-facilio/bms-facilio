"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[12303],{
/***/624465:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){// CONCATENATED MODULE: ./node_modules/@emotion/memoize/dist/memoize.browser.esm.js
function memoize(fn){var cache={};return function(arg){return void 0===cache[arg]&&(cache[arg]=fn(arg)),cache[arg]}}
/* harmony default export */
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return is_prop_valid_browser_esm}});var memoize_browser_esm=memoize,reactPropsRegex=/^((children|dangerouslySetInnerHTML|key|ref|autoFocus|defaultValue|defaultChecked|innerHTML|suppressContentEditableWarning|suppressHydrationWarning|valueLink|accept|acceptCharset|accessKey|action|allow|allowUserMedia|allowPaymentRequest|allowFullScreen|allowTransparency|alt|async|autoComplete|autoPlay|capture|cellPadding|cellSpacing|challenge|charSet|checked|cite|classID|className|cols|colSpan|content|contentEditable|contextMenu|controls|controlsList|coords|crossOrigin|data|dateTime|default|defer|dir|disabled|download|draggable|encType|form|formAction|formEncType|formMethod|formNoValidate|formTarget|frameBorder|headers|height|hidden|high|href|hrefLang|htmlFor|httpEquiv|id|inputMode|integrity|is|keyParams|keyType|kind|label|lang|list|loop|low|marginHeight|marginWidth|max|maxLength|media|mediaGroup|method|min|minLength|multiple|muted|name|nonce|noValidate|open|optimum|pattern|placeholder|playsInline|poster|preload|profile|radioGroup|readOnly|referrerPolicy|rel|required|reversed|role|rows|rowSpan|sandbox|scope|scoped|scrolling|seamless|selected|shape|size|sizes|slot|span|spellCheck|src|srcDoc|srcLang|srcSet|start|step|style|summary|tabIndex|target|title|type|useMap|value|width|wmode|wrap|about|datatype|inlist|prefix|property|resource|typeof|vocab|autoCapitalize|autoCorrect|autoSave|color|itemProp|itemScope|itemType|itemID|itemRef|results|security|unselectable|accentHeight|accumulate|additive|alignmentBaseline|allowReorder|alphabetic|amplitude|arabicForm|ascent|attributeName|attributeType|autoReverse|azimuth|baseFrequency|baselineShift|baseProfile|bbox|begin|bias|by|calcMode|capHeight|clip|clipPathUnits|clipPath|clipRule|colorInterpolation|colorInterpolationFilters|colorProfile|colorRendering|contentScriptType|contentStyleType|cursor|cx|cy|d|decelerate|descent|diffuseConstant|direction|display|divisor|dominantBaseline|dur|dx|dy|edgeMode|elevation|enableBackground|end|exponent|externalResourcesRequired|fill|fillOpacity|fillRule|filter|filterRes|filterUnits|floodColor|floodOpacity|focusable|fontFamily|fontSize|fontSizeAdjust|fontStretch|fontStyle|fontVariant|fontWeight|format|from|fr|fx|fy|g1|g2|glyphName|glyphOrientationHorizontal|glyphOrientationVertical|glyphRef|gradientTransform|gradientUnits|hanging|horizAdvX|horizOriginX|ideographic|imageRendering|in|in2|intercept|k|k1|k2|k3|k4|kernelMatrix|kernelUnitLength|kerning|keyPoints|keySplines|keyTimes|lengthAdjust|letterSpacing|lightingColor|limitingConeAngle|local|markerEnd|markerMid|markerStart|markerHeight|markerUnits|markerWidth|mask|maskContentUnits|maskUnits|mathematical|mode|numOctaves|offset|opacity|operator|order|orient|orientation|origin|overflow|overlinePosition|overlineThickness|panose1|paintOrder|pathLength|patternContentUnits|patternTransform|patternUnits|pointerEvents|points|pointsAtX|pointsAtY|pointsAtZ|preserveAlpha|preserveAspectRatio|primitiveUnits|r|radius|refX|refY|renderingIntent|repeatCount|repeatDur|requiredExtensions|requiredFeatures|restart|result|rotate|rx|ry|scale|seed|shapeRendering|slope|spacing|specularConstant|specularExponent|speed|spreadMethod|startOffset|stdDeviation|stemh|stemv|stitchTiles|stopColor|stopOpacity|strikethroughPosition|strikethroughThickness|string|stroke|strokeDasharray|strokeDashoffset|strokeLinecap|strokeLinejoin|strokeMiterlimit|strokeOpacity|strokeWidth|surfaceScale|systemLanguage|tableValues|targetX|targetY|textAnchor|textDecoration|textRendering|textLength|to|transform|u1|u2|underlinePosition|underlineThickness|unicode|unicodeBidi|unicodeRange|unitsPerEm|vAlphabetic|vHanging|vIdeographic|vMathematical|values|vectorEffect|version|vertAdvY|vertOriginX|vertOriginY|viewBox|viewTarget|visibility|widths|wordSpacing|writingMode|x|xHeight|x1|x2|xChannelSelector|xlinkActuate|xlinkArcrole|xlinkHref|xlinkRole|xlinkShow|xlinkTitle|xlinkType|xmlBase|xmlns|xmlnsXlink|xmlLang|xmlSpace|y|y1|y2|yChannelSelector|z|zoomAndPan|for|class|autofocus)|(([Dd][Aa][Tt][Aa]|[Aa][Rr][Ii][Aa]|x)-.*))$/,index=memoize_browser_esm((function(prop){return reactPropsRegex.test(prop)||111
/* o */===prop.charCodeAt(0)&&110
/* n */===prop.charCodeAt(1)&&prop.charCodeAt(2)<91}
/* Z+1 */)),is_prop_valid_browser_esm=index},
/***/192954:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){// CONCATENATED MODULE: ./node_modules/@emotion/hash/dist/hash.browser.esm.js
/* eslint-disable */
// Inspired by https://github.com/garycourt/murmurhash-js
// Ported from https://github.com/aappleby/smhasher/blob/61a0530f28277f2e850bfc39600ce61d02b518de/src/MurmurHash2.cpp#L37-L86
function murmur2(str){for(
// 'm' and 'r' are mixing constants generated offline.
// They're not really 'magic', they just happen to work well.
// const m = 0x5bd1e995;
// const r = 24;
// Initialize the hash
var k,h=0,i=0,len=str.length// Mix 4 bytes at a time into the hash
;len>=4;++i,len-=4)k=255&str.charCodeAt(i)|(255&str.charCodeAt(++i))<<8|(255&str.charCodeAt(++i))<<16|(255&str.charCodeAt(++i))<<24,k=
/* Math.imul(k, m): */
1540483477*(65535&k)+(59797*(k>>>16)<<16),k^=
/* k >>> r: */
k>>>24,h=
/* Math.imul(k, m): */
1540483477*(65535&k)+(59797*(k>>>16)<<16)^
/* Math.imul(h, m): */
1540483477*(65535&h)+(59797*(h>>>16)<<16);// Handle the last few bytes of the input array
switch(len){case 3:h^=(255&str.charCodeAt(i+2))<<16;case 2:h^=(255&str.charCodeAt(i+1))<<8;case 1:h^=255&str.charCodeAt(i),h=
/* Math.imul(h, m): */
1540483477*(65535&h)+(59797*(h>>>16)<<16)}// Do a few final mixes of the hash to ensure the last few
// bytes are well-incorporated.
return h^=h>>>13,h=
/* Math.imul(h, m): */
1540483477*(65535&h)+(59797*(h>>>16)<<16),((h^h>>>15)>>>0).toString(36)}
/* harmony default export */
// EXPORTS
__webpack_require__.d(__webpack_exports__,{O:function(){/* binding */return serializeStyles}});var hash_browser_esm=murmur2,unitlessKeys={animationIterationCount:1,borderImageOutset:1,borderImageSlice:1,borderImageWidth:1,boxFlex:1,boxFlexGroup:1,boxOrdinalGroup:1,columnCount:1,columns:1,flex:1,flexGrow:1,flexPositive:1,flexShrink:1,flexNegative:1,flexOrder:1,gridRow:1,gridRowEnd:1,gridRowSpan:1,gridRowStart:1,gridColumn:1,gridColumnEnd:1,gridColumnSpan:1,gridColumnStart:1,msGridRow:1,msGridRowSpan:1,msGridColumn:1,msGridColumnSpan:1,fontWeight:1,lineHeight:1,opacity:1,order:1,orphans:1,tabSize:1,widows:1,zIndex:1,zoom:1,WebkitLineClamp:1,
// SVG-related properties
fillOpacity:1,floodOpacity:1,stopOpacity:1,strokeDasharray:1,strokeDashoffset:1,strokeMiterlimit:1,strokeOpacity:1,strokeWidth:1},unitless_browser_esm=unitlessKeys;// CONCATENATED MODULE: ./node_modules/@emotion/serialize/node_modules/@emotion/memoize/dist/memoize.browser.esm.js
function memoize(fn){var cache={};return function(arg){return void 0===cache[arg]&&(cache[arg]=fn(arg)),cache[arg]}}
/* harmony default export */var memoize_browser_esm=memoize,hyphenateRegex=/[A-Z]|^ms/g,animationRegex=/_EMO_([^_]+?)_([^]*?)_EMO_/g,isCustomProperty=function(property){return 45===property.charCodeAt(1)},isProcessableValue=function(value){return null!=value&&"boolean"!==typeof value},processStyleName=memoize_browser_esm((function(styleName){return isCustomProperty(styleName)?styleName:styleName.replace(hyphenateRegex,"-$&").toLowerCase()})),processStyleValue=function(key,value){switch(key){case"animation":case"animationName":if("string"===typeof value)return value.replace(animationRegex,(function(match,p1,p2){return cursor={name:p1,styles:p2,next:cursor},p1}))}return 1===unitless_browser_esm[key]||isCustomProperty(key)||"number"!==typeof value||0===value?value:value+"px"};function handleInterpolation(mergedProps,registered,interpolation,couldBeSelectorInterpolation){if(null==interpolation)return"";if(void 0!==interpolation.__emotion_styles)return interpolation;switch(typeof interpolation){case"boolean":return"";case"object":if(1===interpolation.anim)return cursor={name:interpolation.name,styles:interpolation.styles,next:cursor},interpolation.name;if(void 0!==interpolation.styles){var next=interpolation.next;if(void 0!==next)
// not the most efficient thing ever but this is a pretty rare case
// and there will be very few iterations of this generally
while(void 0!==next)cursor={name:next.name,styles:next.styles,next:cursor},next=next.next;var styles=interpolation.styles+";";return styles}return createStringFromObject(mergedProps,registered,interpolation);case"function":if(void 0!==mergedProps){var previousCursor=cursor,result=interpolation(mergedProps);return cursor=previousCursor,handleInterpolation(mergedProps,registered,result,couldBeSelectorInterpolation)}break;case"string":break}// finalize string values (regular strings and functions interpolated into css calls)
if(null==registered)return interpolation;var cached=registered[interpolation];return void 0===cached||couldBeSelectorInterpolation?interpolation:cached}function createStringFromObject(mergedProps,registered,obj){var string="";if(Array.isArray(obj))for(var i=0;i<obj.length;i++)string+=handleInterpolation(mergedProps,registered,obj[i],!1);else for(var _key in obj){var value=obj[_key];if("object"!==typeof value)null!=registered&&void 0!==registered[value]?string+=_key+"{"+registered[value]+"}":isProcessableValue(value)&&(string+=processStyleName(_key)+":"+processStyleValue(_key,value)+";");else if(!Array.isArray(value)||"string"!==typeof value[0]||null!=registered&&void 0!==registered[value[0]]){var interpolated=handleInterpolation(mergedProps,registered,value,!1);switch(_key){case"animation":case"animationName":string+=processStyleName(_key)+":"+interpolated+";";break;default:string+=_key+"{"+interpolated+"}"}}else for(var _i=0;_i<value.length;_i++)isProcessableValue(value[_i])&&(string+=processStyleName(_key)+":"+processStyleValue(_key,value[_i])+";")}return string}var cursor,labelPattern=/label:\s*([^\s;\n{]+)\s*;/g;var serializeStyles=function(args,registered,mergedProps){if(1===args.length&&"object"===typeof args[0]&&null!==args[0]&&void 0!==args[0].styles)return args[0];var stringMode=!0,styles="";cursor=void 0;var strings=args[0];null==strings||void 0===strings.raw?(stringMode=!1,styles+=handleInterpolation(mergedProps,registered,strings,!1)):styles+=strings[0];// we start at 1 since we've already handled the first arg
for(var i=1;i<args.length;i++)styles+=handleInterpolation(mergedProps,registered,args[i],46===styles.charCodeAt(styles.length-1)),stringMode&&(styles+=strings[i]);// using a global regex with .exec is stateful so lastIndex has to be reset each time
labelPattern.lastIndex=0;var match,identifierName="";// https://esbench.com/bench/5b809c2cf2949800a0f61fb5
while(null!==(match=labelPattern.exec(styles)))identifierName+="-"+// $FlowFixMe we know it's not null
match[1];var name=hash_browser_esm(styles)+identifierName;return{name:name,styles:styles,next:cursor}};
/***/},
/***/168087:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */f:function(){/* binding */return getRegisteredStyles}
/* harmony export */});function getRegisteredStyles(registered,registeredStyles,classNames){var rawClassName="";return classNames.split(" ").forEach((function(className){void 0!==registered[className]?registeredStyles.push(registered[className]):rawClassName+=className+" "})),rawClassName}},
/***/531891:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */s:function(){/* binding */return handleLegacySelectEvents},
/* harmony export */y:function(){/* binding */return createLegacyDataSelectAction}
/* harmony export */});
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(933051),_util_model_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(232234);
/* harmony import */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
// Legacy data selection action.
// Includes: pieSelect, pieUnSelect, pieToggleSelect, mapSelect, mapUnSelect, mapToggleSelect
function createLegacyDataSelectAction(seriesType,ecRegisterAction){function getSeriesIndices(ecModel,payload){var seriesIndices=[];return ecModel.eachComponent({mainType:"series",subType:seriesType,query:payload},(function(seriesModel){seriesIndices.push(seriesModel.seriesIndex)})),seriesIndices}(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6)([[seriesType+"ToggleSelect","toggleSelect"],[seriesType+"Select","select"],[seriesType+"UnSelect","unselect"]],(function(eventsMap){ecRegisterAction(eventsMap[0],(function(payload,ecModel,api){payload=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .extend */.l7)({},payload),api.dispatchAction((0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .extend */.l7)(payload,{type:eventsMap[1],seriesIndex:getSeriesIndices(ecModel,payload)}))}))}))}function handleSeriesLegacySelectEvents(type,eventPostfix,ecIns,ecModel,payload){var legacyEventName=type+eventPostfix;ecIns.isSilent(legacyEventName)||ecModel.eachComponent({mainType:"series",subType:"pie"},(function(seriesModel){for(var seriesIndex=seriesModel.seriesIndex,selectedMap=seriesModel.option.selectedMap,selected=payload.selected,i=0;i<selected.length;i++)if(selected[i].seriesIndex===seriesIndex){var data=seriesModel.getData(),dataIndex=(0,_util_model_js__WEBPACK_IMPORTED_MODULE_1__/* .queryDataIndex */.gO)(data,payload.fromActionPayload);ecIns.trigger(legacyEventName,{type:legacyEventName,seriesId:seriesModel.id,name:(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isArray */.kJ)(dataIndex)?data.getName(dataIndex[0]):data.getName(dataIndex),selected:(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isString */.HD)(selectedMap)?selectedMap:(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .extend */.l7)({},selectedMap)})}}))}function handleLegacySelectEvents(messageCenter,ecIns,api){messageCenter.on("selectchanged",(function(params){var ecModel=api.getModel();params.isFromClick?(handleSeriesLegacySelectEvents("map","selectchanged",ecIns,ecModel,params),handleSeriesLegacySelectEvents("pie","selectchanged",ecIns,ecModel,params)):"select"===params.fromAction?(handleSeriesLegacySelectEvents("map","selected",ecIns,ecModel,params),handleSeriesLegacySelectEvents("pie","selected",ecIns,ecModel,params)):"unselect"===params.fromAction&&(handleSeriesLegacySelectEvents("map","unselected",ecIns,ecModel,params),handleSeriesLegacySelectEvents("pie","unselected",ecIns,ecModel,params))}))}
/***/},
/***/223430:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Z:function(){/* binding */return defaultLoading}
/* harmony export */});
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(933051),_util_graphic_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(38154),_util_graphic_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(25293),_util_graphic_js__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(679750),_util_graphic_js__WEBPACK_IMPORTED_MODULE_4__=__webpack_require__(914826),PI=Math.PI;
/* harmony import */
/**
 * @param {module:echarts/ExtensionAPI} api
 * @param {Object} [opts]
 * @param {string} [opts.text]
 * @param {string} [opts.color]
 * @param {string} [opts.textColor]
 * @return {module:zrender/Element}
 */
function defaultLoading(api,opts){opts=opts||{},zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .defaults */.ce(opts,{text:"loading",textColor:"#000",fontSize:12,fontWeight:"normal",fontStyle:"normal",fontFamily:"sans-serif",maskColor:"rgba(255, 255, 255, 0.8)",showSpinner:!0,color:"#5470c6",spinnerRadius:10,lineWidth:5,zlevel:0});var group=new _util_graphic_js__WEBPACK_IMPORTED_MODULE_1__/* ["default"] */.Z,mask=new _util_graphic_js__WEBPACK_IMPORTED_MODULE_2__/* ["default"] */.Z({style:{fill:opts.maskColor},zlevel:opts.zlevel,z:1e4});group.add(mask);var arc,textContent=new _util_graphic_js__WEBPACK_IMPORTED_MODULE_3__/* ["default"] */.ZP({style:{text:opts.text,fill:opts.textColor,fontSize:opts.fontSize,fontWeight:opts.fontWeight,fontStyle:opts.fontStyle,fontFamily:opts.fontFamily},zlevel:opts.zlevel,z:10001}),labelRect=new _util_graphic_js__WEBPACK_IMPORTED_MODULE_2__/* ["default"] */.Z({style:{fill:"none"},textContent:textContent,textConfig:{position:"right",distance:10},zlevel:opts.zlevel,z:10001});return group.add(labelRect),opts.showSpinner&&(arc=new _util_graphic_js__WEBPACK_IMPORTED_MODULE_4__/* ["default"] */.Z({shape:{startAngle:-PI/2,endAngle:-PI/2+.1,r:opts.spinnerRadius},style:{stroke:opts.color,lineCap:"round",lineWidth:opts.lineWidth},zlevel:opts.zlevel,z:10001}),arc.animateShape(!0).when(1e3,{endAngle:3*PI/2}).start("circularInOut"),arc.animateShape(!0).when(1e3,{startAngle:3*PI/2}).delay(300).start("circularInOut"),group.add(arc)),// Inject resize
group.resize=function(){var textWidth=textContent.getBoundingRect().width,r=opts.showSpinner?opts.spinnerRadius:0,cx=(api.getWidth()-2*r-(opts.showSpinner&&textWidth?10:0)-textWidth)/2-(opts.showSpinner&&textWidth?0:5+textWidth/2)+(opts.showSpinner?0:textWidth/2)+(textWidth?0:r),cy=api.getHeight()/2;opts.showSpinner&&arc.setShape({cx:cx,cy:cy}),labelRect.setShape({x:cx-r,y:cy-r,width:2*r,height:2*r}),mask.setShape({x:0,y:0,width:api.getWidth(),height:api.getHeight()})},group.resize(),group}
/***/},
/***/498071:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony import */var tslib__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(570655),zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_4__=__webpack_require__(933051),_Model_js__WEBPACK_IMPORTED_MODULE_6__=__webpack_require__(812312),_util_component_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(942151),_util_clazz_js__WEBPACK_IMPORTED_MODULE_5__=__webpack_require__(834251),_util_model_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(232234),_util_layout_js__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(576172),inner=(0,_util_model_js__WEBPACK_IMPORTED_MODULE_0__/* .makeInner */.Yf)(),ComponentModel=
/** @class */
function(_super){function ComponentModel(option,parentModel,ecModel){var _this=_super.call(this,option,parentModel,ecModel)||this;return _this.uid=_util_component_js__WEBPACK_IMPORTED_MODULE_2__/* .getUID */.Kr("ec_cpt_model"),_this}return(0,tslib__WEBPACK_IMPORTED_MODULE_1__/* .__extends */.ZT)(ComponentModel,_super),ComponentModel.prototype.init=function(option,parentModel,ecModel){this.mergeDefaultAndTheme(option,ecModel)},ComponentModel.prototype.mergeDefaultAndTheme=function(option,ecModel){var layoutMode=_util_layout_js__WEBPACK_IMPORTED_MODULE_3__/* .fetchLayoutMode */.YD(this),inputPositionParams=layoutMode?_util_layout_js__WEBPACK_IMPORTED_MODULE_3__/* .getLayoutParams */.tE(option):{},themeModel=ecModel.getTheme();zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_4__/* .merge */.TS(option,themeModel.get(this.mainType)),zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_4__/* .merge */.TS(option,this.getDefaultOption()),layoutMode&&_util_layout_js__WEBPACK_IMPORTED_MODULE_3__/* .mergeLayoutParam */.dt(option,inputPositionParams,layoutMode)},ComponentModel.prototype.mergeOption=function(option,ecModel){zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_4__/* .merge */.TS(this.option,option,!0);var layoutMode=_util_layout_js__WEBPACK_IMPORTED_MODULE_3__/* .fetchLayoutMode */.YD(this);layoutMode&&_util_layout_js__WEBPACK_IMPORTED_MODULE_3__/* .mergeLayoutParam */.dt(this.option,option,layoutMode)},
/**
   * Called immediately after `init` or `mergeOption` of this instance called.
   */
ComponentModel.prototype.optionUpdated=function(newCptOption,isInit){},
/**
   * [How to declare defaultOption]:
   *
   * (A) If using class declaration in typescript (since echarts 5):
   * ```ts
   * import {ComponentOption} from '../model/option.js';
   * export interface XxxOption extends ComponentOption {
   *     aaa: number
   * }
   * export class XxxModel extends Component {
   *     static type = 'xxx';
   *     static defaultOption: XxxOption = {
   *         aaa: 123
   *     }
   * }
   * Component.registerClass(XxxModel);
   * ```
   * ```ts
   * import {inheritDefaultOption} from '../util/component.js';
   * import {XxxModel, XxxOption} from './XxxModel.js';
   * export interface XxxSubOption extends XxxOption {
   *     bbb: number
   * }
   * class XxxSubModel extends XxxModel {
   *     static defaultOption: XxxSubOption = inheritDefaultOption(XxxModel.defaultOption, {
   *         bbb: 456
   *     })
   *     fn() {
   *         let opt = this.getDefaultOption();
   *         // opt is {aaa: 123, bbb: 456}
   *     }
   * }
   * ```
   *
   * (B) If using class extend (previous approach in echarts 3 & 4):
   * ```js
   * let XxxComponent = Component.extend({
   *     defaultOption: {
   *         xx: 123
   *     }
   * })
   * ```
   * ```js
   * let XxxSubComponent = XxxComponent.extend({
   *     defaultOption: {
   *         yy: 456
   *     },
   *     fn: function () {
   *         let opt = this.getDefaultOption();
   *         // opt is {xx: 123, yy: 456}
   *     }
   * })
   * ```
   */
ComponentModel.prototype.getDefaultOption=function(){var ctor=this.constructor;// If using class declaration, it is different to travel super class
// in legacy env and auto merge defaultOption. So if using class
// declaration, defaultOption should be merged manually.
if(!(0,_util_clazz_js__WEBPACK_IMPORTED_MODULE_5__/* .isExtendedClass */.PT)(ctor))
// When using ts class, defaultOption must be declared as static.
return ctor.defaultOption;// FIXME: remove this approach?
var fields=inner(this);if(!fields.defaultOption){var optList=[],clz=ctor;while(clz){var opt=clz.prototype.defaultOption;opt&&optList.push(opt),clz=clz.superClass}for(var defaultOption={},i=optList.length-1;i>=0;i--)defaultOption=zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_4__/* .merge */.TS(defaultOption,optList[i],!0);fields.defaultOption=defaultOption}return fields.defaultOption},
/**
   * Notice: always force to input param `useDefault` in case that forget to consider it.
   * The same behavior as `modelUtil.parseFinder`.
   *
   * @param useDefault In many cases like series refer axis and axis refer grid,
   *        If axis index / axis id not specified, use the first target as default.
   *        In other cases like dataZoom refer axis, if not specified, measn no refer.
   */
ComponentModel.prototype.getReferringComponents=function(mainType,opt){var indexKey=mainType+"Index",idKey=mainType+"Id";return(0,_util_model_js__WEBPACK_IMPORTED_MODULE_0__/* .queryReferringComponents */.HZ)(this.ecModel,mainType,{index:this.get(indexKey,!0),id:this.get(idKey,!0)},opt)},ComponentModel.prototype.getBoxLayoutParams=function(){
// Consider itself having box layout configs.
var boxLayoutModel=this;return{left:boxLayoutModel.get("left"),top:boxLayoutModel.get("top"),right:boxLayoutModel.get("right"),bottom:boxLayoutModel.get("bottom"),width:boxLayoutModel.get("width"),height:boxLayoutModel.get("height")}},
/**
   * Get key for zlevel.
   * If developers don't configure zlevel. We will assign zlevel to series based on the key.
   * For example, lines with trail effect and progressive series will in an individual zlevel.
   */
ComponentModel.prototype.getZLevelKey=function(){return""},ComponentModel.prototype.setZLevel=function(zlevel){this.option.zlevel=zlevel},ComponentModel.protoInitialize=function(){var proto=ComponentModel.prototype;proto.type="component",proto.id="",proto.name="",proto.mainType="",proto.subType="",proto.componentIndex=0}(),ComponentModel}(_Model_js__WEBPACK_IMPORTED_MODULE_6__/* ["default"] */.Z);
/* harmony import */function getDependencies(componentType){var deps=[];return zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_4__/* .each */.S6(ComponentModel.getClassesByMainType(componentType),(function(clz){deps=deps.concat(clz.dependencies||clz.prototype.dependencies||[])})),// Ensure main type.
deps=zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_4__/* .map */.UI(deps,(function(type){return(0,_util_clazz_js__WEBPACK_IMPORTED_MODULE_5__/* .parseClassType */.u9)(type).main})),// Hack dataset for convenience.
"dataset"!==componentType&&zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_4__/* .indexOf */.cq(deps,"dataset")<=0&&deps.unshift("dataset"),deps}
/* harmony default export */(0,_util_clazz_js__WEBPACK_IMPORTED_MODULE_5__/* .mountExtend */.pw)(ComponentModel,_Model_js__WEBPACK_IMPORTED_MODULE_6__/* ["default"] */.Z),(0,_util_clazz_js__WEBPACK_IMPORTED_MODULE_5__/* .enableClassManagement */.au)(ComponentModel),_util_component_js__WEBPACK_IMPORTED_MODULE_2__/* .enableSubTypeDefaulter */.cj(ComponentModel),_util_component_js__WEBPACK_IMPORTED_MODULE_2__/* .enableTopologicalTravel */.jS(ComponentModel,getDependencies),__webpack_exports__.Z=ComponentModel},
/***/780669:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return Global}});
// EXTERNAL MODULE: ./node_modules/tslib/tslib.es6.js
var tslib_es6=__webpack_require__(570655),util=__webpack_require__(933051),model=__webpack_require__(232234),Model=__webpack_require__(812312),Component=__webpack_require__(498071),platform="";
// EXTERNAL MODULE: ./node_modules/zrender/lib/core/util.js
// Navigator not exists in node
"undefined"!==typeof navigator&&(
/* global navigator */
platform=navigator.platform||"");var decalColor="rgba(0, 0, 0, 0.2)",globalDefault={darkMode:"auto",
// backgroundColor: 'rgba(0,0,0,0)',
colorBy:"series",color:["#5470c6","#91cc75","#fac858","#ee6666","#73c0de","#3ba272","#fc8452","#9a60b4","#ea7ccc"],gradientColor:["#f6efa6","#d88273","#bf444c"],aria:{decal:{decals:[{color:decalColor,dashArrayX:[1,0],dashArrayY:[2,5],symbolSize:1,rotation:Math.PI/6},{color:decalColor,symbol:"circle",dashArrayX:[[8,8],[0,8,8,0]],dashArrayY:[6,0],symbolSize:.8},{color:decalColor,dashArrayX:[1,0],dashArrayY:[4,3],rotation:-Math.PI/4},{color:decalColor,dashArrayX:[[6,6],[0,6,6,0]],dashArrayY:[6,0]},{color:decalColor,dashArrayX:[[1,0],[1,6]],dashArrayY:[1,0,6,0],rotation:Math.PI/4},{color:decalColor,symbol:"triangle",dashArrayX:[[9,9],[0,9,9,0]],dashArrayY:[7,2],symbolSize:.75}]}},
// If xAxis and yAxis declared, grid is created by default.
// grid: {},
textStyle:{
// color: '#000',
// decoration: 'none',
// PENDING
fontFamily:platform.match(/^Win/)?"Microsoft YaHei":"sans-serif",
// fontFamily: 'Arial, Verdana, sans-serif',
fontSize:12,fontStyle:"normal",fontWeight:"normal"},
// http://blogs.adobe.com/webplatform/2014/02/24/using-blend-modes-in-html-canvas/
// https://developer.mozilla.org/en-US/docs/Web/API/CanvasRenderingContext2D/globalCompositeOperation
// Default is source-over
blendMode:null,stateAnimation:{duration:300,easing:"cubicOut"},animation:"auto",animationDuration:1e3,animationDurationUpdate:500,animationEasing:"cubicInOut",animationEasingUpdate:"cubicInOut",animationThreshold:2e3,
// Configuration for progressive/incremental rendering
progressiveThreshold:3e3,progressive:400,
// Threshold of if use single hover layer to optimize.
// It is recommended that `hoverLayerThreshold` is equivalent to or less than
// `progressiveThreshold`, otherwise hover will cause restart of progressive,
// which is unexpected.
// see example <echarts/test/heatmap-large.html>.
hoverLayerThreshold:3e3,
// See: module:echarts/scale/Time
useUTC:!1},sourceHelper=__webpack_require__(161772),internalOptionCreatorMap=(0,util/* createHashMap */.kW)();
/* harmony default export */function concatInternalOptions(ecModel,mainType,newCmptOptionList){var internalOptionCreator=internalOptionCreatorMap.get(mainType);if(!internalOptionCreator)return newCmptOptionList;var internalOptions=internalOptionCreator(ecModel);return internalOptions?newCmptOptionList.concat(internalOptions):newCmptOptionList}
// EXTERNAL MODULE: ./node_modules/echarts/lib/model/mixin/palette.js
var reCreateSeriesIndices,assertSeriesInitialized,initBase,palette=__webpack_require__(975494),OPTION_INNER_KEY="\0_ec_inner",OPTION_INNER_VALUE=1;var GlobalModel=
/** @class */
function(_super){function GlobalModel(){return null!==_super&&_super.apply(this,arguments)||this}return(0,tslib_es6/* __extends */.ZT)(GlobalModel,_super),GlobalModel.prototype.init=function(option,parentModel,ecModel,theme,locale,optionManager){theme=theme||{},this.option=null,// Mark as not initialized.
this._theme=new Model/* default */.Z(theme),this._locale=new Model/* default */.Z(locale),this._optionManager=optionManager},GlobalModel.prototype.setOption=function(option,opts,optionPreprocessorFuncs){var innerOpt=normalizeSetOptionInput(opts);this._optionManager.setOption(option,optionPreprocessorFuncs,innerOpt),this._resetOption(null,innerOpt)},
/**
   * @param type null/undefined: reset all.
   *        'recreate': force recreate all.
   *        'timeline': only reset timeline option
   *        'media': only reset media query option
   * @return Whether option changed.
   */
GlobalModel.prototype.resetOption=function(type,opt){return this._resetOption(type,normalizeSetOptionInput(opt))},GlobalModel.prototype._resetOption=function(type,opt){var optionChanged=!1,optionManager=this._optionManager;if(!type||"recreate"===type){var baseOption=optionManager.mountOption("recreate"===type);0,this.option&&"recreate"!==type?(this.restoreData(),this._mergeOption(baseOption,opt)):initBase(this,baseOption),optionChanged=!0}// By design, if `setOption(option2)` at the second time, and `option2` is a `ECUnitOption`,
// it should better not have the same props with `MediaUnit['option']`.
// Because either `option2` or `MediaUnit['option']` will be always merged to "current option"
// rather than original "baseOption". If they both override a prop, the result might be
// unexpected when media state changed after `setOption` called.
// If we really need to modify a props in each `MediaUnit['option']`, use the full version
// (`{baseOption, media}`) in `setOption`.
// For `timeline`, the case is the same.
if("timeline"!==type&&"media"!==type||this.restoreData(),!type||"recreate"===type||"timeline"===type){var timelineOption=optionManager.getTimelineOption(this);timelineOption&&(optionChanged=!0,this._mergeOption(timelineOption,opt))}if(!type||"recreate"===type||"media"===type){var mediaOptions=optionManager.getMediaOption(this);mediaOptions.length&&(0,util/* each */.S6)(mediaOptions,(function(mediaOption){optionChanged=!0,this._mergeOption(mediaOption,opt)}),this)}return optionChanged},GlobalModel.prototype.mergeOption=function(option){this._mergeOption(option,null)},GlobalModel.prototype._mergeOption=function(newOption,opt){var option=this.option,componentsMap=this._componentsMap,componentsCount=this._componentsCount,newCmptTypes=[],newCmptTypeMap=(0,util/* createHashMap */.kW)(),replaceMergeMainTypeMap=opt&&opt.replaceMergeMainTypeMap;function visitComponent(mainType){var newCmptOptionList=concatInternalOptions(this,mainType,model/* normalizeToArray */.kF(newOption[mainType])),oldCmptList=componentsMap.get(mainType),mergeMode=// `!oldCmptList` means init. See the comment in `mappingToExists`
oldCmptList?replaceMergeMainTypeMap&&replaceMergeMainTypeMap.get(mainType)?"replaceMerge":"normalMerge":"replaceAll",mappingResult=model/* mappingToExists */.ab(oldCmptList,newCmptOptionList,mergeMode);// Set mainType and complete subType.
model/* setComponentTypeToKeyInfo */.O0(mappingResult,mainType,Component/* default */.Z),// Empty it before the travel, in order to prevent `this._componentsMap`
// from being used in the `init`/`mergeOption`/`optionUpdated` of some
// components, which is probably incorrect logic.
option[mainType]=null,componentsMap.set(mainType,null),componentsCount.set(mainType,0);var tooltipExists,optionsByMainType=[],cmptsByMainType=[],cmptsCountByMainType=0;(0,util/* each */.S6)(mappingResult,(function(resultItem,index){var componentModel=resultItem.existing,newCmptOption=resultItem.newOption;if(newCmptOption){var isSeriesType="series"===mainType,ComponentModelClass=Component/* default */.Z.getClass(mainType,resultItem.keyInfo.subType,!isSeriesType);if(!ComponentModelClass)return;// TODO Before multiple tooltips get supported, we do this check to avoid unexpected exception.
if("tooltip"===mainType){if(tooltipExists)return void 0;tooltipExists=!0}if(componentModel&&componentModel.constructor===ComponentModelClass)componentModel.name=resultItem.keyInfo.name,// componentModel.settingTask && componentModel.settingTask.dirty();
componentModel.mergeOption(newCmptOption,this),componentModel.optionUpdated(newCmptOption,!1);else{
// PENDING Global as parent ?
var extraOpt=(0,util/* extend */.l7)({componentIndex:index},resultItem.keyInfo);componentModel=new ComponentModelClass(newCmptOption,this,this,extraOpt),// Assign `keyInfo`
(0,util/* extend */.l7)(componentModel,extraOpt),resultItem.brandNew&&(componentModel.__requireNewView=!0),componentModel.init(newCmptOption,this,this),// Call optionUpdated after init.
// newCmptOption has been used as componentModel.option
// and may be merged with theme and default, so pass null
// to avoid confusion.
componentModel.optionUpdated(null,!0)}}else componentModel&&(
// Consider where is no new option and should be merged using {},
// see removeEdgeAndAdd in topologicalTravel and
// ComponentModel.getAllClassMainTypes.
componentModel.mergeOption({},this),componentModel.optionUpdated({},!1));// If no both `resultItem.exist` and `resultItem.option`,
// either it is in `replaceMerge` and not matched by any id,
// or it has been removed in previous `replaceMerge` and left a "hole" in this component index.
componentModel?(optionsByMainType.push(componentModel.option),cmptsByMainType.push(componentModel),cmptsCountByMainType++):(
// Always do assign to avoid elided item in array.
optionsByMainType.push(void 0),cmptsByMainType.push(void 0))}),this),option[mainType]=optionsByMainType,componentsMap.set(mainType,cmptsByMainType),componentsCount.set(mainType,cmptsCountByMainType),// Backup series for filtering.
"series"===mainType&&reCreateSeriesIndices(this)}// If no series declared, ensure `_seriesIndices` initialized.
(0,sourceHelper/* resetSourceDefaulter */.md)(this),// If no component class, merge directly.
// For example: color, animaiton options, etc.
(0,util/* each */.S6)(newOption,(function(componentOption,mainType){null!=componentOption&&(Component/* default */.Z.hasClass(mainType)?mainType&&(newCmptTypes.push(mainType),newCmptTypeMap.set(mainType,!0)):
// globalSettingTask.dirty();
option[mainType]=null==option[mainType]?(0,util/* clone */.d9)(componentOption):(0,util/* merge */.TS)(option[mainType],componentOption,!0))})),replaceMergeMainTypeMap&&
// If there is a mainType `xxx` in `replaceMerge` but not declared in option,
// we trade it as it is declared in option as `{xxx: []}`. Because:
// (1) for normal merge, `{xxx: null/undefined}` are the same meaning as `{xxx: []}`.
// (2) some preprocessor may convert some of `{xxx: null/undefined}` to `{xxx: []}`.
replaceMergeMainTypeMap.each((function(val,mainTypeInReplaceMerge){Component/* default */.Z.hasClass(mainTypeInReplaceMerge)&&!newCmptTypeMap.get(mainTypeInReplaceMerge)&&(newCmptTypes.push(mainTypeInReplaceMerge),newCmptTypeMap.set(mainTypeInReplaceMerge,!0))})),Component/* default */.Z.topologicalTravel(newCmptTypes,Component/* default */.Z.getAllClassMainTypes(),visitComponent,this),this._seriesIndices||reCreateSeriesIndices(this)},
/**
   * Get option for output (cloned option and inner info removed)
   */
GlobalModel.prototype.getOption=function(){var option=(0,util/* clone */.d9)(this.option);return(0,util/* each */.S6)(option,(function(optInMainType,mainType){if(Component/* default */.Z.hasClass(mainType)){for(var opts=model/* normalizeToArray */.kF(optInMainType),realLen=opts.length,metNonInner=!1,i=realLen-1// Inner cmpts need to be removed.
// Inner cmpts might not be at last since ec5.0, but still
// compatible for users: if inner cmpt at last, splice the returned array.
;i>=0;i--)
// Remove options with inner id.
opts[i]&&!model/* isComponentIdInternal */.lY(opts[i])?metNonInner=!0:(opts[i]=null,!metNonInner&&realLen--);opts.length=realLen,option[mainType]=opts}})),delete option[OPTION_INNER_KEY],option},GlobalModel.prototype.getTheme=function(){return this._theme},GlobalModel.prototype.getLocaleModel=function(){return this._locale},GlobalModel.prototype.setUpdatePayload=function(payload){this._payload=payload},GlobalModel.prototype.getUpdatePayload=function(){return this._payload},
/**
   * @param idx If not specified, return the first one.
   */
GlobalModel.prototype.getComponent=function(mainType,idx){var list=this._componentsMap.get(mainType);if(list){var cmpt=list[idx||0];if(cmpt)return cmpt;if(null==idx)for(var i=0;i<list.length;i++)if(list[i])return list[i]}},
/**
   * @return Never be null/undefined.
   */
GlobalModel.prototype.queryComponents=function(condition){var mainType=condition.mainType;if(!mainType)return[];var result,index=condition.index,id=condition.id,name=condition.name,cmpts=this._componentsMap.get(mainType);return cmpts&&cmpts.length?(null!=index?(result=[],(0,util/* each */.S6)(model/* normalizeToArray */.kF(index),(function(idx){cmpts[idx]&&result.push(cmpts[idx])}))):result=null!=id?queryByIdOrName("id",id,cmpts):null!=name?queryByIdOrName("name",name,cmpts):(0,util/* filter */.hX)(cmpts,(function(cmpt){return!!cmpt})),filterBySubType(result,condition)):[]},
/**
   * The interface is different from queryComponents,
   * which is convenient for inner usage.
   *
   * @usage
   * let result = findComponents(
   *     {mainType: 'dataZoom', query: {dataZoomId: 'abc'}}
   * );
   * let result = findComponents(
   *     {mainType: 'series', subType: 'pie', query: {seriesName: 'uio'}}
   * );
   * let result = findComponents(
   *     {mainType: 'series',
   *     filter: function (model, index) {...}}
   * );
   * // result like [component0, componnet1, ...]
   */
GlobalModel.prototype.findComponents=function(condition){var query=condition.query,mainType=condition.mainType,queryCond=getQueryCond(query),result=queryCond?this.queryComponents(queryCond):(0,util/* filter */.hX)(this._componentsMap.get(mainType),(function(cmpt){return!!cmpt}));return doFilter(filterBySubType(result,condition));function getQueryCond(q){var indexAttr=mainType+"Index",idAttr=mainType+"Id",nameAttr=mainType+"Name";return!q||null==q[indexAttr]&&null==q[idAttr]&&null==q[nameAttr]?null:{mainType:mainType,
// subType will be filtered finally.
index:q[indexAttr],id:q[idAttr],name:q[nameAttr]}}function doFilter(res){return condition.filter?(0,util/* filter */.hX)(res,condition.filter):res}},GlobalModel.prototype.eachComponent=function(mainType,cb,context){var componentsMap=this._componentsMap;if((0,util/* isFunction */.mf)(mainType)){var ctxForAll_1=cb,cbForAll_1=mainType;componentsMap.each((function(cmpts,componentType){for(var i=0;cmpts&&i<cmpts.length;i++){var cmpt=cmpts[i];cmpt&&cbForAll_1.call(ctxForAll_1,componentType,cmpt,cmpt.componentIndex)}}))}else for(var cmpts=(0,util/* isString */.HD)(mainType)?componentsMap.get(mainType):(0,util/* isObject */.Kn)(mainType)?this.findComponents(mainType):null,i=0;cmpts&&i<cmpts.length;i++){var cmpt=cmpts[i];cmpt&&cb.call(context,cmpt,cmpt.componentIndex)}},
/**
   * Get series list before filtered by name.
   */
GlobalModel.prototype.getSeriesByName=function(name){var nameStr=model/* convertOptionIdName */.U5(name,null);return(0,util/* filter */.hX)(this._componentsMap.get("series"),(function(oneSeries){return!!oneSeries&&null!=nameStr&&oneSeries.name===nameStr}))},
/**
   * Get series list before filtered by index.
   */
GlobalModel.prototype.getSeriesByIndex=function(seriesIndex){return this._componentsMap.get("series")[seriesIndex]},
/**
   * Get series list before filtered by type.
   * FIXME: rename to getRawSeriesByType?
   */
GlobalModel.prototype.getSeriesByType=function(subType){return(0,util/* filter */.hX)(this._componentsMap.get("series"),(function(oneSeries){return!!oneSeries&&oneSeries.subType===subType}))},
/**
   * Get all series before filtered.
   */
GlobalModel.prototype.getSeries=function(){return(0,util/* filter */.hX)(this._componentsMap.get("series"),(function(oneSeries){return!!oneSeries}))},
/**
   * Count series before filtered.
   */
GlobalModel.prototype.getSeriesCount=function(){return this._componentsCount.get("series")},
/**
   * After filtering, series may be different
   * from raw series.
   */
GlobalModel.prototype.eachSeries=function(cb,context){assertSeriesInitialized(this),(0,util/* each */.S6)(this._seriesIndices,(function(rawSeriesIndex){var series=this._componentsMap.get("series")[rawSeriesIndex];cb.call(context,series,rawSeriesIndex)}),this)},
/**
   * Iterate raw series before filtered.
   *
   * @param {Function} cb
   * @param {*} context
   */
GlobalModel.prototype.eachRawSeries=function(cb,context){(0,util/* each */.S6)(this._componentsMap.get("series"),(function(series){series&&cb.call(context,series,series.componentIndex)}))},
/**
   * After filtering, series may be different.
   * from raw series.
   */
GlobalModel.prototype.eachSeriesByType=function(subType,cb,context){assertSeriesInitialized(this),(0,util/* each */.S6)(this._seriesIndices,(function(rawSeriesIndex){var series=this._componentsMap.get("series")[rawSeriesIndex];series.subType===subType&&cb.call(context,series,rawSeriesIndex)}),this)},
/**
   * Iterate raw series before filtered of given type.
   */
GlobalModel.prototype.eachRawSeriesByType=function(subType,cb,context){return(0,util/* each */.S6)(this.getSeriesByType(subType),cb,context)},GlobalModel.prototype.isSeriesFiltered=function(seriesModel){return assertSeriesInitialized(this),null==this._seriesIndicesMap.get(seriesModel.componentIndex)},GlobalModel.prototype.getCurrentSeriesIndices=function(){return(this._seriesIndices||[]).slice()},GlobalModel.prototype.filterSeries=function(cb,context){assertSeriesInitialized(this);var newSeriesIndices=[];(0,util/* each */.S6)(this._seriesIndices,(function(seriesRawIdx){var series=this._componentsMap.get("series")[seriesRawIdx];cb.call(context,series,seriesRawIdx)&&newSeriesIndices.push(seriesRawIdx)}),this),this._seriesIndices=newSeriesIndices,this._seriesIndicesMap=(0,util/* createHashMap */.kW)(newSeriesIndices)},GlobalModel.prototype.restoreData=function(payload){reCreateSeriesIndices(this);var componentsMap=this._componentsMap,componentTypes=[];componentsMap.each((function(components,componentType){Component/* default */.Z.hasClass(componentType)&&componentTypes.push(componentType)})),Component/* default */.Z.topologicalTravel(componentTypes,Component/* default */.Z.getAllClassMainTypes(),(function(componentType){(0,util/* each */.S6)(componentsMap.get(componentType),(function(component){!component||"series"===componentType&&isNotTargetSeries(component,payload)||component.restoreData()}))}))},GlobalModel.internalField=function(){reCreateSeriesIndices=function(ecModel){var seriesIndices=ecModel._seriesIndices=[];(0,util/* each */.S6)(ecModel._componentsMap.get("series"),(function(series){
// series may have been removed by `replaceMerge`.
series&&seriesIndices.push(series.componentIndex)})),ecModel._seriesIndicesMap=(0,util/* createHashMap */.kW)(seriesIndices)},assertSeriesInitialized=function(ecModel){0},initBase=function(ecModel,baseOption){
// Using OPTION_INNER_KEY to mark that this option cannot be used outside,
// i.e. `chart.setOption(chart.getModel().option);` is forbidden.
ecModel.option={},ecModel.option[OPTION_INNER_KEY]=OPTION_INNER_VALUE,// Init with series: [], in case of calling findSeries method
// before series initialized.
ecModel._componentsMap=(0,util/* createHashMap */.kW)({series:[]}),ecModel._componentsCount=(0,util/* createHashMap */.kW)();// If user spefied `option.aria`, aria will be enable. This detection should be
// performed before theme and globalDefault merge.
var airaOption=baseOption.aria;(0,util/* isObject */.Kn)(airaOption)&&null==airaOption.enabled&&(airaOption.enabled=!0),mergeTheme(baseOption,ecModel._theme.option),// TODO Needs clone when merging to the unexisted property
(0,util/* merge */.TS)(baseOption,globalDefault,!1),ecModel._mergeOption(baseOption,null)}}(),GlobalModel}(Model/* default */.Z);function isNotTargetSeries(seriesModel,payload){if(payload){var index=payload.seriesIndex,id=payload.seriesId,name_1=payload.seriesName;return null!=index&&seriesModel.componentIndex!==index||null!=id&&seriesModel.id!==id||null!=name_1&&seriesModel.name!==name_1}}function mergeTheme(option,theme){
// PENDING
// NOT use `colorLayer` in theme if option has `color`
var notMergeColorLayer=option.color&&!option.colorLayer;(0,util/* each */.S6)(theme,(function(themeItem,name){"colorLayer"===name&&notMergeColorLayer||Component/* default */.Z.hasClass(name)||("object"===typeof themeItem?option[name]=option[name]?(0,util/* merge */.TS)(option[name],themeItem,!1):(0,util/* clone */.d9)(themeItem):null==option[name]&&(option[name]=themeItem));// If it is component model mainType, the model handles that merge later.
// otherwise, merge them here.
}))}function queryByIdOrName(attr,idOrName,cmpts){
// Here is a break from echarts4: string and number are
// treated as equal.
if((0,util/* isArray */.kJ)(idOrName)){var keyMap_1=(0,util/* createHashMap */.kW)();return(0,util/* each */.S6)(idOrName,(function(idOrNameItem){if(null!=idOrNameItem){var idName=model/* convertOptionIdName */.U5(idOrNameItem,null);null!=idName&&keyMap_1.set(idOrNameItem,!0)}})),(0,util/* filter */.hX)(cmpts,(function(cmpt){return cmpt&&keyMap_1.get(cmpt[attr])}))}var idName_1=model/* convertOptionIdName */.U5(idOrName,null);return(0,util/* filter */.hX)(cmpts,(function(cmpt){return cmpt&&null!=idName_1&&cmpt[attr]===idName_1}))}function filterBySubType(components,condition){
// Using hasOwnProperty for restrict. Consider
// subType is undefined in user payload.
return condition.hasOwnProperty("subType")?(0,util/* filter */.hX)(components,(function(cmpt){return cmpt&&cmpt.subType===condition.subType})):components}function normalizeSetOptionInput(opts){var replaceMergeMainTypeMap=(0,util/* createHashMap */.kW)();return opts&&(0,util/* each */.S6)(model/* normalizeToArray */.kF(opts.replaceMerge),(function(mainType){replaceMergeMainTypeMap.set(mainType,!0)})),{replaceMergeMainTypeMap:replaceMergeMainTypeMap}}(0,util/* mixin */.jB)(GlobalModel,palette/* PaletteMixin */._);
/* harmony default export */var Global=GlobalModel;
/***/},
/***/812312:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return model_Model}});
// EXTERNAL MODULE: ./node_modules/zrender/lib/core/env.js
var env=__webpack_require__(966387),clazz=__webpack_require__(834251),makeStyleMapper=__webpack_require__(659066),AREA_STYLE_KEY_MAP=[["fill","color"],["shadowBlur"],["shadowOffsetX"],["shadowOffsetY"],["opacity"],["shadowColor"]],getAreaStyle=(0,makeStyleMapper/* default */.Z)(AREA_STYLE_KEY_MAP),AreaStyleMixin=
/** @class */
function(){function AreaStyleMixin(){}return AreaStyleMixin.prototype.getAreaStyle=function(excludes,includes){return getAreaStyle(this,excludes,includes)},AreaStyleMixin}(),labelStyle=__webpack_require__(336006),Text=__webpack_require__(679750),PATH_COLOR=["textStyle","color"],textStyleParams=["fontStyle","fontWeight","fontSize","fontFamily","padding","lineHeight","rich","width","height","overflow"],tmpText=new Text/* default */.ZP,TextStyleMixin=
/** @class */
function(){function TextStyleMixin(){}
/**
   * Get color property or get color from option.textStyle.color
   */
// TODO Callback
return TextStyleMixin.prototype.getTextColor=function(isEmphasis){var ecModel=this.ecModel;return this.getShallow("color")||(!isEmphasis&&ecModel?ecModel.get(PATH_COLOR):null)},
/**
   * Create font string from fontStyle, fontWeight, fontSize, fontFamily
   * @return {string}
   */
TextStyleMixin.prototype.getFont=function(){return(0,labelStyle/* getFont */.qT)({fontStyle:this.getShallow("fontStyle"),fontWeight:this.getShallow("fontWeight"),fontSize:this.getShallow("fontSize"),fontFamily:this.getShallow("fontFamily")},this.ecModel)},TextStyleMixin.prototype.getTextRect=function(text){for(var style={text:text,verticalAlign:this.getShallow("verticalAlign")||this.getShallow("baseline")},i=0;i<textStyleParams.length;i++)style[textStyleParams[i]]=this.getShallow(textStyleParams[i]);return tmpText.useStyle(style),tmpText.update(),tmpText.getBoundingRect()},TextStyleMixin}(),textStyle=TextStyleMixin,lineStyle=__webpack_require__(877515),itemStyle=__webpack_require__(489887),util=__webpack_require__(933051),Model=
/** @class */
function(){function Model(option,parentModel,ecModel){this.parentModel=parentModel,this.ecModel=ecModel,this.option=option}return Model.prototype.init=function(option,parentModel,ecModel){for(var rest=[],_i=3;_i<arguments.length;_i++)rest[_i-3]=arguments[_i]},
/**
   * Merge the input option to me.
   */
Model.prototype.mergeOption=function(option,ecModel){(0,util/* merge */.TS)(this.option,option,!0)},// `path` can be 'a.b.c', so the return value type have to be `ModelOption`
// TODO: TYPE strict key check?
// get(path: string | string[], ignoreParent?: boolean): ModelOption;
Model.prototype.get=function(path,ignoreParent){return null==path?this.option:this._doGet(this.parsePath(path),!ignoreParent&&this.parentModel)},Model.prototype.getShallow=function(key,ignoreParent){var option=this.option,val=null==option?option:option[key];if(null==val&&!ignoreParent){var parentModel=this.parentModel;parentModel&&(
// FIXME:TS do not know how to make it works
val=parentModel.getShallow(key))}return val},// `path` can be 'a.b.c', so the return value type have to be `Model<ModelOption>`
// getModel(path: string | string[], parentModel?: Model): Model;
// TODO 'a.b.c' is deprecated
Model.prototype.getModel=function(path,parentModel){var hasPath=null!=path,pathFinal=hasPath?this.parsePath(path):null,obj=hasPath?this._doGet(pathFinal):this.option;return parentModel=parentModel||this.parentModel&&this.parentModel.getModel(this.resolveParentPath(pathFinal)),new Model(obj,parentModel,this.ecModel)},
/**
   * If model has option
   */
Model.prototype.isEmpty=function(){return null==this.option},Model.prototype.restoreData=function(){},// Pending
Model.prototype.clone=function(){var Ctor=this.constructor;return new Ctor((0,util/* clone */.d9)(this.option))},// setReadOnly(properties): void {
// clazzUtil.setReadOnly(this, properties);
// }
// If path is null/undefined, return null/undefined.
Model.prototype.parsePath=function(path){return"string"===typeof path?path.split("."):path},// Resolve path for parent. Perhaps useful when parent use a different property.
// Default to be a identity resolver.
// Can be modified to a different resolver.
Model.prototype.resolveParentPath=function(path){return path},// FIXME:TS check whether put this method here
Model.prototype.isAnimationEnabled=function(){if(!env/* default */.Z.node&&this.option){if(null!=this.option.animation)return!!this.option.animation;if(this.parentModel)return this.parentModel.isAnimationEnabled()}},Model.prototype._doGet=function(pathArr,parentModel){var obj=this.option;if(!pathArr)return obj;for(var i=0;i<pathArr.length;i++)
// Ignore empty
if(pathArr[i]&&(// obj could be number/string/... (like 0)
obj=obj&&"object"===typeof obj?obj[pathArr[i]]:null,null==obj))break;return null==obj&&parentModel&&(obj=parentModel._doGet(this.resolveParentPath(pathArr),parentModel.parentModel)),obj},Model}();
// EXTERNAL MODULE: ./node_modules/echarts/lib/util/clazz.js
// Enable Model.extend.
(0,clazz/* enableClassExtend */.dm)(Model),(0,clazz/* enableClassCheck */.Qj)(Model),(0,util/* mixin */.jB)(Model,lineStyle/* LineStyleMixin */.K),(0,util/* mixin */.jB)(Model,itemStyle/* ItemStyleMixin */.D),(0,util/* mixin */.jB)(Model,AreaStyleMixin),(0,util/* mixin */.jB)(Model,textStyle);
/* harmony default export */var model_Model=Model;
/***/},
/***/43534:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony import */var _util_model_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(232234),zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(933051),QUERY_REG=/^(min|max)?(.+)$/,OptionManager=
/** @class */
function(){
// timeline.notMerge is not supported in ec3. Firstly there is rearly
// case that notMerge is needed. Secondly supporting 'notMerge' requires
// rawOption cloned and backuped when timeline changed, which does no
// good to performance. What's more, that both timeline and setOption
// method supply 'notMerge' brings complex and some problems.
// Consider this case:
// (step1) chart.setOption({timeline: {notMerge: false}, ...}, false);
// (step2) chart.setOption({timeline: {notMerge: true}, ...}, false);
function OptionManager(api){this._timelineOptions=[],this._mediaList=[],
/**
     * -1, means default.
     * empty means no media.
     */
this._currentMediaIndices=[],this._api=api}return OptionManager.prototype.setOption=function(rawOption,optionPreprocessorFuncs,opt){rawOption&&(
// That set dat primitive is dangerous if user reuse the data when setOption again.
(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6)((0,_util_model_js__WEBPACK_IMPORTED_MODULE_1__/* .normalizeToArray */.kF)(rawOption.series),(function(series){series&&series.data&&(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isTypedArray */.fU)(series.data)&&(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .setAsPrimitive */.s7)(series.data)})),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6)((0,_util_model_js__WEBPACK_IMPORTED_MODULE_1__/* .normalizeToArray */.kF)(rawOption.dataset),(function(dataset){dataset&&dataset.source&&(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isTypedArray */.fU)(dataset.source)&&(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .setAsPrimitive */.s7)(dataset.source)}))),// Caution: some series modify option data, if do not clone,
// it should ensure that the repeat modify correctly
// (create a new object when modify itself).
rawOption=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .clone */.d9)(rawOption);// FIXME
// If some property is set in timeline options or media option but
// not set in baseOption, a warning should be given.
var optionBackup=this._optionBackup,newParsedOption=parseRawOption(rawOption,optionPreprocessorFuncs,!optionBackup);this._newBaseOption=newParsedOption.baseOption,// For setOption at second time (using merge mode);
optionBackup?(
// FIXME
// the restore merge solution is essentially incorrect.
// the mapping can not be 100% consistent with ecModel, which probably brings
// potential bug!
// The first merge is delayed, because in most cases, users do not call `setOption` twice.
// let fakeCmptsMap = this._fakeCmptsMap;
// if (!fakeCmptsMap) {
//     fakeCmptsMap = this._fakeCmptsMap = createHashMap();
//     mergeToBackupOption(fakeCmptsMap, null, optionBackup.baseOption, null);
// }
// mergeToBackupOption(
//     fakeCmptsMap, optionBackup.baseOption, newParsedOption.baseOption, opt
// );
// For simplicity, timeline options and media options do not support merge,
// that is, if you `setOption` twice and both has timeline options, the latter
// timeline options will not be merged to the former, but just substitute them.
newParsedOption.timelineOptions.length&&(optionBackup.timelineOptions=newParsedOption.timelineOptions),newParsedOption.mediaList.length&&(optionBackup.mediaList=newParsedOption.mediaList),newParsedOption.mediaDefault&&(optionBackup.mediaDefault=newParsedOption.mediaDefault)):this._optionBackup=newParsedOption},OptionManager.prototype.mountOption=function(isRecreate){var optionBackup=this._optionBackup;return this._timelineOptions=optionBackup.timelineOptions,this._mediaList=optionBackup.mediaList,this._mediaDefault=optionBackup.mediaDefault,this._currentMediaIndices=[],(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .clone */.d9)(isRecreate?optionBackup.baseOption:this._newBaseOption)},OptionManager.prototype.getTimelineOption=function(ecModel){var option,timelineOptions=this._timelineOptions;if(timelineOptions.length){
// getTimelineOption can only be called after ecModel inited,
// so we can get currentIndex from timelineModel.
var timelineModel=ecModel.getComponent("timeline");timelineModel&&(option=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .clone */.d9)(// FIXME:TS as TimelineModel or quivlant interface
timelineOptions[timelineModel.getCurrentIndex()]))}return option},OptionManager.prototype.getMediaOption=function(ecModel){var ecWidth=this._api.getWidth(),ecHeight=this._api.getHeight(),mediaList=this._mediaList,mediaDefault=this._mediaDefault,indices=[],result=[];// No media defined.
if(!mediaList.length&&!mediaDefault)return result;// Multi media may be applied, the latter defined media has higher priority.
for(var i=0,len=mediaList.length;i<len;i++)applyMediaQuery(mediaList[i].query,ecWidth,ecHeight)&&indices.push(i);// FIXME
// Whether mediaDefault should force users to provide? Otherwise
// the change by media query can not be recorvered.
return!indices.length&&mediaDefault&&(indices=[-1]),indices.length&&!indicesEquals(indices,this._currentMediaIndices)&&(result=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .map */.UI)(indices,(function(index){return(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .clone */.d9)(-1===index?mediaDefault.option:mediaList[index].option)}))),// Otherwise return nothing.
this._currentMediaIndices=indices,result},OptionManager}();
/* harmony import */
/**
 * [RAW_OPTION_PATTERNS]
 * (Note: "series: []" represents all other props in `ECUnitOption`)
 *
 * (1) No prop "baseOption" declared:
 * Root option is used as "baseOption" (except prop "options" and "media").
 * ```js
 * option = {
 *     series: [],
 *     timeline: {},
 *     options: [],
 * };
 * option = {
 *     series: [],
 *     media: {},
 * };
 * option = {
 *     series: [],
 *     timeline: {},
 *     options: [],
 *     media: {},
 * }
 * ```
 *
 * (2) Prop "baseOption" declared:
 * If "baseOption" declared, `ECUnitOption` props can only be declared
 * inside "baseOption" except prop "timeline" (compat ec2).
 * ```js
 * option = {
 *     baseOption: {
 *         timeline: {},
 *         series: [],
 *     },
 *     options: []
 * };
 * option = {
 *     baseOption: {
 *         series: [],
 *     },
 *     media: []
 * };
 * option = {
 *     baseOption: {
 *         timeline: {},
 *         series: [],
 *     },
 *     options: []
 *     media: []
 * };
 * option = {
 *     // ec3 compat ec2: allow (only) `timeline` declared
 *     // outside baseOption. Keep this setting for compat.
 *     timeline: {},
 *     baseOption: {
 *         series: [],
 *     },
 *     options: [],
 *     media: []
 * };
 * ```
 */
function parseRawOption(// `rawOption` May be modified
rawOption,optionPreprocessorFuncs,isNew){var mediaDefault,baseOption,mediaList=[],declaredBaseOption=rawOption.baseOption,timelineOnRoot=rawOption.timeline,timelineOptionsOnRoot=rawOption.options,mediaOnRoot=rawOption.media,hasMedia=!!rawOption.media,hasTimeline=!!(timelineOptionsOnRoot||timelineOnRoot||declaredBaseOption&&declaredBaseOption.timeline);function doPreprocess(option){(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6)(optionPreprocessorFuncs,(function(preProcess){preProcess(option,isNew)}))}return declaredBaseOption?(baseOption=declaredBaseOption,// For merge option.
baseOption.timeline||(baseOption.timeline=timelineOnRoot)):((hasTimeline||hasMedia)&&(rawOption.options=rawOption.media=null),baseOption=rawOption),hasMedia&&(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isArray */.kJ)(mediaOnRoot)&&(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6)(mediaOnRoot,(function(singleMedia){singleMedia&&singleMedia.option&&(singleMedia.query?mediaList.push(singleMedia):mediaDefault||(
// Use the first media default.
mediaDefault=singleMedia))})),doPreprocess(baseOption),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6)(timelineOptionsOnRoot,(function(option){return doPreprocess(option)})),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6)(mediaList,(function(media){return doPreprocess(media.option)})),{baseOption:baseOption,timelineOptions:timelineOptionsOnRoot||[],mediaDefault:mediaDefault,mediaList:mediaList}}
/**
 * @see <http://www.w3.org/TR/css3-mediaqueries/#media1>
 * Support: width, height, aspectRatio
 * Can use max or min as prefix.
 */function applyMediaQuery(query,ecWidth,ecHeight){var realMap={width:ecWidth,height:ecHeight,aspectratio:ecWidth/ecHeight},applicable=!0;return(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6)(query,(function(value,attr){var matched=attr.match(QUERY_REG);if(matched&&matched[1]&&matched[2]){var operator=matched[1],realAttr=matched[2].toLowerCase();compare(realMap[realAttr],value,operator)||(applicable=!1)}})),applicable}function compare(real,expect,operator){return"min"===operator?real>=expect:"max"===operator?real<=expect:real===expect}function indicesEquals(indices1,indices2){
// indices is always order by asc and has only finite number.
return indices1.join(",")===indices2.join(",")}
/**
 * Consider case:
 * `chart.setOption(opt1);`
 * Then user do some interaction like dataZoom, dataView changing.
 * `chart.setOption(opt2);`
 * Then user press 'reset button' in toolbox.
 *
 * After doing that all of the interaction effects should be reset, the
 * chart should be the same as the result of invoke
 * `chart.setOption(opt1); chart.setOption(opt2);`.
 *
 * Although it is not able ensure that
 * `chart.setOption(opt1); chart.setOption(opt2);` is equivalents to
 * `chart.setOption(merge(opt1, opt2));` exactly,
 * this might be the only simple way to implement that feature.
 *
 * MEMO: We've considered some other approaches:
 * 1. Each model handles its self restoration but not uniform treatment.
 *     (Too complex in logic and error-prone)
 * 2. Use a shadow ecModel. (Performance expensive)
 *
 * FIXME: A possible solution:
 * Add a extra level of model for each component model. The inheritance chain would be:
 * ecModel <- componentModel <- componentActionModel <- dataItemModel
 * And all of the actions can only modify the `componentActionModel` rather than
 * `componentModel`. `setOption` will only modify the `ecModel` and `componentModel`.
 * When "resotre" action triggered, model from `componentActionModel` will be discarded
 * instead of recreating the "ecModel" from the "_optionBackup".
 */
// function mergeToBackupOption(
//     fakeCmptsMap: FakeComponentsMap,
//     // `tarOption` Can be null/undefined, means init
//     tarOption: ECUnitOption,
//     newOption: ECUnitOption,
//     // Can be null/undefined
//     opt: InnerSetOptionOpts
// ): void {
//     newOption = newOption || {} as ECUnitOption;
//     const notInit = !!tarOption;
//     each(newOption, function (newOptsInMainType, mainType) {
//         if (newOptsInMainType == null) {
//             return;
//         }
//         if (!ComponentModel.hasClass(mainType)) {
//             if (tarOption) {
//                 tarOption[mainType] = merge(tarOption[mainType], newOptsInMainType, true);
//             }
//         }
//         else {
//             const oldTarOptsInMainType = notInit ? normalizeToArray(tarOption[mainType]) : null;
//             const oldFakeCmptsInMainType = fakeCmptsMap.get(mainType) || [];
//             const resultTarOptsInMainType = notInit ? (tarOption[mainType] = [] as ComponentOption[]) : null;
//             const resultFakeCmptsInMainType = fakeCmptsMap.set(mainType, []);
//             const mappingResult = mappingToExists(
//                 oldFakeCmptsInMainType,
//                 normalizeToArray(newOptsInMainType),
//                 (opt && opt.replaceMergeMainTypeMap.get(mainType)) ? 'replaceMerge' : 'normalMerge'
//             );
//             setComponentTypeToKeyInfo(mappingResult, mainType, ComponentModel as ComponentModelConstructor);
//             each(mappingResult, function (resultItem, index) {
//                 // The same logic as `Global.ts#_mergeOption`.
//                 let fakeCmpt = resultItem.existing;
//                 const newOption = resultItem.newOption;
//                 const keyInfo = resultItem.keyInfo;
//                 let fakeCmptOpt;
//                 if (!newOption) {
//                     fakeCmptOpt = oldTarOptsInMainType[index];
//                 }
//                 else {
//                     if (fakeCmpt && fakeCmpt.subType === keyInfo.subType) {
//                         fakeCmpt.name = keyInfo.name;
//                         if (notInit) {
//                             fakeCmptOpt = merge(oldTarOptsInMainType[index], newOption, true);
//                         }
//                     }
//                     else {
//                         fakeCmpt = extend({}, keyInfo);
//                         if (notInit) {
//                             fakeCmptOpt = clone(newOption);
//                         }
//                     }
//                 }
//                 if (fakeCmpt) {
//                     notInit && resultTarOptsInMainType.push(fakeCmptOpt);
//                     resultFakeCmptsInMainType.push(fakeCmpt);
//                 }
//                 else {
//                     notInit && resultTarOptsInMainType.push(void 0);
//                     resultFakeCmptsInMainType.push(void 0);
//                 }
//             });
//         }
//     });
// }
/* harmony default export */__webpack_exports__.Z=OptionManager},
/***/993321:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */V:function(){/* binding */return SERIES_UNIVERSAL_TRANSITION_PROP}
/* harmony export */});
/* harmony import */var tslib__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(570655),zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__=__webpack_require__(933051),zrender_lib_core_env_js__WEBPACK_IMPORTED_MODULE_8__=__webpack_require__(966387),_util_model_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(232234),_Component_js__WEBPACK_IMPORTED_MODULE_5__=__webpack_require__(498071),_mixin_palette_js__WEBPACK_IMPORTED_MODULE_9__=__webpack_require__(975494),_model_mixin_dataFormat_js__WEBPACK_IMPORTED_MODULE_10__=__webpack_require__(961219),_util_layout_js__WEBPACK_IMPORTED_MODULE_4__=__webpack_require__(576172),_core_task_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(608674),_util_clazz_js__WEBPACK_IMPORTED_MODULE_11__=__webpack_require__(834251),_data_helper_sourceManager_js__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(536437),_component_tooltip_seriesFormatTooltip_js__WEBPACK_IMPORTED_MODULE_7__=__webpack_require__(453993),inner=_util_model_js__WEBPACK_IMPORTED_MODULE_0__/* .makeInner */.Yf();
/* harmony import */function getSelectionKey(data,dataIndex){return data.getName(dataIndex)||data.getId(dataIndex)}var SERIES_UNIVERSAL_TRANSITION_PROP="__universalTransitionEnabled",SeriesModel=
/** @class */
function(_super){function SeriesModel(){
// [Caution]: Because this class or desecendants can be used as `XXX.extend(subProto)`,
// the class members must not be initialized in constructor or declaration place.
// Otherwise there is bad case:
//   class A {xxx = 1;}
//   enableClassExtend(A);
//   class B extends A {}
//   var C = B.extend({xxx: 5});
//   var c = new C();
//   console.log(c.xxx); // expect 5 but always 1.
var _this=null!==_super&&_super.apply(this,arguments)||this;// ---------------------------------------
// Props about data selection
// ---------------------------------------
return _this._selectedDataIndicesMap={},_this}return(0,tslib__WEBPACK_IMPORTED_MODULE_1__/* .__extends */.ZT)(SeriesModel,_super),SeriesModel.prototype.init=function(option,parentModel,ecModel){this.seriesIndex=this.componentIndex,this.dataTask=(0,_core_task_js__WEBPACK_IMPORTED_MODULE_2__/* .createTask */.v)({count:dataTaskCount,reset:dataTaskReset}),this.dataTask.context={model:this},this.mergeDefaultAndTheme(option,ecModel);var sourceManager=inner(this).sourceManager=new _data_helper_sourceManager_js__WEBPACK_IMPORTED_MODULE_3__/* .SourceManager */.U(this);sourceManager.prepareSource();var data=this.getInitialData(option,ecModel);wrapData(data,this),this.dataTask.context.data=data,inner(this).dataBeforeProcessed=data,// If we reverse the order (make data firstly, and then make
// dataBeforeProcessed by cloneShallow), cloneShallow will
// cause data.graph.data !== data when using
// module:echarts/data/Graph or module:echarts/data/Tree.
// See module:echarts/data/helper/linkSeriesData
// Theoretically, it is unreasonable to call `seriesModel.getData()` in the model
// init or merge stage, because the data can be restored. So we do not `restoreData`
// and `setData` here, which forbids calling `seriesModel.getData()` in this stage.
// Call `seriesModel.getRawData()` instead.
// this.restoreData();
autoSeriesName(this),this._initSelectedMapFromData(data)},
/**
   * Util for merge default and theme to option
   */
SeriesModel.prototype.mergeDefaultAndTheme=function(option,ecModel){var layoutMode=(0,_util_layout_js__WEBPACK_IMPORTED_MODULE_4__/* .fetchLayoutMode */.YD)(this),inputPositionParams=layoutMode?(0,_util_layout_js__WEBPACK_IMPORTED_MODULE_4__/* .getLayoutParams */.tE)(option):{},themeSubType=this.subType;_Component_js__WEBPACK_IMPORTED_MODULE_5__/* ["default"] */.Z.hasClass(themeSubType)&&(themeSubType+="Series"),zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .merge */.TS(option,ecModel.getTheme().get(this.subType)),zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .merge */.TS(option,this.getDefaultOption()),// Default label emphasis `show`
_util_model_js__WEBPACK_IMPORTED_MODULE_0__/* .defaultEmphasis */.Cc(option,"label",["show"]),this.fillDataTextStyle(option.data),layoutMode&&(0,_util_layout_js__WEBPACK_IMPORTED_MODULE_4__/* .mergeLayoutParam */.dt)(option,inputPositionParams,layoutMode)},SeriesModel.prototype.mergeOption=function(newSeriesOption,ecModel){
// this.settingTask.dirty();
newSeriesOption=zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .merge */.TS(this.option,newSeriesOption,!0),this.fillDataTextStyle(newSeriesOption.data);var layoutMode=(0,_util_layout_js__WEBPACK_IMPORTED_MODULE_4__/* .fetchLayoutMode */.YD)(this);layoutMode&&(0,_util_layout_js__WEBPACK_IMPORTED_MODULE_4__/* .mergeLayoutParam */.dt)(this.option,newSeriesOption,layoutMode);var sourceManager=inner(this).sourceManager;sourceManager.dirty(),sourceManager.prepareSource();var data=this.getInitialData(newSeriesOption,ecModel);wrapData(data,this),this.dataTask.dirty(),this.dataTask.context.data=data,inner(this).dataBeforeProcessed=data,autoSeriesName(this),this._initSelectedMapFromData(data)},SeriesModel.prototype.fillDataTextStyle=function(data){
// Default data label emphasis `show`
// FIXME Tree structure data ?
// FIXME Performance ?
if(data&&!zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .isTypedArray */.fU(data))for(var props=["show"],i=0;i<data.length;i++)data[i]&&data[i].label&&_util_model_js__WEBPACK_IMPORTED_MODULE_0__/* .defaultEmphasis */.Cc(data[i],"label",props)},
/**
   * Init a data structure from data related option in series
   * Must be overridden.
   */
SeriesModel.prototype.getInitialData=function(option,ecModel){},
/**
   * Append data to list
   */
SeriesModel.prototype.appendData=function(params){
// FIXME ???
// (1) If data from dataset, forbidden append.
// (2) support append data of dataset.
var data=this.getRawData();data.appendData(params.data)},
/**
   * Consider some method like `filter`, `map` need make new data,
   * We should make sure that `seriesModel.getData()` get correct
   * data in the stream procedure. So we fetch data from upstream
   * each time `task.perform` called.
   */
SeriesModel.prototype.getData=function(dataType){var task=getCurrentTask(this);if(task){var data=task.context.data;return null==dataType?data:data.getLinkedData(dataType)}
// When series is not alive (that may happen when click toolbox
// restore or setOption with not merge mode), series data may
// be still need to judge animation or something when graphic
// elements want to know whether fade out.
return inner(this).data},SeriesModel.prototype.getAllData=function(){var mainData=this.getData();return mainData&&mainData.getLinkedDataAll?mainData.getLinkedDataAll():[{data:mainData}]},SeriesModel.prototype.setData=function(data){var task=getCurrentTask(this);if(task){var context=task.context;// Consider case: filter, data sample.
// FIXME:TS never used, so comment it
// if (context.data !== data && task.modifyOutputEnd) {
//     task.setOutputEnd(data.count());
// }
context.outputData=data,// Caution: setData should update context.data,
// Because getData may be called multiply in a
// single stage and expect to get the data just
// set. (For example, AxisProxy, x y both call
// getData and setDate sequentially).
// So the context.data should be fetched from
// upstream each time when a stage starts to be
// performed.
task!==this.dataTask&&(context.data=data)}inner(this).data=data},SeriesModel.prototype.getEncode=function(){var encode=this.get("encode",!0);if(encode)return zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .createHashMap */.kW(encode)},SeriesModel.prototype.getSourceManager=function(){return inner(this).sourceManager},SeriesModel.prototype.getSource=function(){return this.getSourceManager().getSource()},
/**
   * Get data before processed
   */
SeriesModel.prototype.getRawData=function(){return inner(this).dataBeforeProcessed},SeriesModel.prototype.getColorBy=function(){var colorBy=this.get("colorBy");return colorBy||"series"},SeriesModel.prototype.isColorBySeries=function(){return"series"===this.getColorBy()},
/**
   * Get base axis if has coordinate system and has axis.
   * By default use coordSys.getBaseAxis();
   * Can be overridden for some chart.
   * @return {type} description
   */
SeriesModel.prototype.getBaseAxis=function(){var coordSys=this.coordinateSystem;// @ts-ignore
return coordSys&&coordSys.getBaseAxis&&coordSys.getBaseAxis()},
/**
   * Default tooltip formatter
   *
   * @param dataIndex
   * @param multipleSeries
   * @param dataType
   * @param renderMode valid values: 'html'(by default) and 'richText'.
   *        'html' is used for rendering tooltip in extra DOM form, and the result
   *        string is used as DOM HTML content.
   *        'richText' is used for rendering tooltip in rich text form, for those where
   *        DOM operation is not supported.
   * @return formatted tooltip with `html` and `markers`
   *        Notice: The override method can also return string
   */
SeriesModel.prototype.formatTooltip=function(dataIndex,multipleSeries,dataType){return(0,_component_tooltip_seriesFormatTooltip_js__WEBPACK_IMPORTED_MODULE_7__/* .defaultSeriesFormatTooltip */.w)({series:this,dataIndex:dataIndex,multipleSeries:multipleSeries})},SeriesModel.prototype.isAnimationEnabled=function(){var ecModel=this.ecModel;// Disable animation if using echarts in node but not give ssr flag.
// In ssr mode, renderToString will generate svg with css animation.
if(zrender_lib_core_env_js__WEBPACK_IMPORTED_MODULE_8__/* ["default"] */.Z.node&&(!ecModel||!ecModel.ssr))return!1;var animationEnabled=this.getShallow("animation");return animationEnabled&&this.getData().count()>this.getShallow("animationThreshold")&&(animationEnabled=!1),!!animationEnabled},SeriesModel.prototype.restoreData=function(){this.dataTask.dirty()},SeriesModel.prototype.getColorFromPalette=function(name,scope,requestColorNum){var ecModel=this.ecModel,color=_mixin_palette_js__WEBPACK_IMPORTED_MODULE_9__/* .PaletteMixin */._.prototype.getColorFromPalette.call(this,name,scope,requestColorNum);// PENDING
return color||(color=ecModel.getColorFromPalette(name,scope,requestColorNum)),color},
/**
   * Use `data.mapDimensionsAll(coordDim)` instead.
   * @deprecated
   */
SeriesModel.prototype.coordDimToDataDim=function(coordDim){return this.getRawData().mapDimensionsAll(coordDim)},
/**
   * Get progressive rendering count each step
   */
SeriesModel.prototype.getProgressive=function(){return this.get("progressive")},
/**
   * Get progressive rendering count each step
   */
SeriesModel.prototype.getProgressiveThreshold=function(){return this.get("progressiveThreshold")},// PENGING If selectedMode is null ?
SeriesModel.prototype.select=function(innerDataIndices,dataType){this._innerSelect(this.getData(dataType),innerDataIndices)},SeriesModel.prototype.unselect=function(innerDataIndices,dataType){var selectedMap=this.option.selectedMap;if(selectedMap){var selectedMode=this.option.selectedMode,data=this.getData(dataType);if("series"===selectedMode||"all"===selectedMap)return this.option.selectedMap={},void(this._selectedDataIndicesMap={});for(var i=0;i<innerDataIndices.length;i++){var dataIndex=innerDataIndices[i],nameOrId=getSelectionKey(data,dataIndex);selectedMap[nameOrId]=!1,this._selectedDataIndicesMap[nameOrId]=-1}}},SeriesModel.prototype.toggleSelect=function(innerDataIndices,dataType){for(var tmpArr=[],i=0;i<innerDataIndices.length;i++)tmpArr[0]=innerDataIndices[i],this.isSelected(innerDataIndices[i],dataType)?this.unselect(tmpArr,dataType):this.select(tmpArr,dataType)},SeriesModel.prototype.getSelectedDataIndices=function(){if("all"===this.option.selectedMap)return[].slice.call(this.getData().getIndices());for(var selectedDataIndicesMap=this._selectedDataIndicesMap,nameOrIds=zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .keys */.XP(selectedDataIndicesMap),dataIndices=[],i=0;i<nameOrIds.length;i++){var dataIndex=selectedDataIndicesMap[nameOrIds[i]];dataIndex>=0&&dataIndices.push(dataIndex)}return dataIndices},SeriesModel.prototype.isSelected=function(dataIndex,dataType){var selectedMap=this.option.selectedMap;if(!selectedMap)return!1;var data=this.getData(dataType);return("all"===selectedMap||selectedMap[getSelectionKey(data,dataIndex)])&&!data.getItemModel(dataIndex).get(["select","disabled"])},SeriesModel.prototype.isUniversalTransitionEnabled=function(){if(this[SERIES_UNIVERSAL_TRANSITION_PROP])return!0;var universalTransitionOpt=this.option.universalTransition;// Quick reject
return!!universalTransitionOpt&&(!0===universalTransitionOpt||universalTransitionOpt&&universalTransitionOpt.enabled)},SeriesModel.prototype._innerSelect=function(data,innerDataIndices){var _a,_b,option=this.option,selectedMode=option.selectedMode,len=innerDataIndices.length;if(selectedMode&&len)if("series"===selectedMode)option.selectedMap="all";else if("multiple"===selectedMode){zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .isObject */.Kn(option.selectedMap)||(option.selectedMap={});for(var selectedMap=option.selectedMap,i=0;i<len;i++){var dataIndex=innerDataIndices[i],nameOrId=getSelectionKey(data,dataIndex);// TODO different types of data share same object.
selectedMap[nameOrId]=!0,this._selectedDataIndicesMap[nameOrId]=data.getRawIndex(dataIndex)}}else if("single"===selectedMode||!0===selectedMode){var lastDataIndex=innerDataIndices[len-1];nameOrId=getSelectionKey(data,lastDataIndex);option.selectedMap=(_a={},_a[nameOrId]=!0,_a),this._selectedDataIndicesMap=(_b={},_b[nameOrId]=data.getRawIndex(lastDataIndex),_b)}},SeriesModel.prototype._initSelectedMapFromData=function(data){
// Ignore select info in data if selectedMap exists.
// NOTE It's only for legacy usage. edge data is not supported.
if(!this.option.selectedMap){var dataIndices=[];data.hasItemOption&&data.each((function(idx){var rawItem=data.getRawDataItem(idx);rawItem&&rawItem.selected&&dataIndices.push(idx)})),dataIndices.length>0&&this._innerSelect(data,dataIndices)}},// /**
//  * @see {module:echarts/stream/Scheduler}
//  */
// abstract pipeTask: null
SeriesModel.registerClass=function(clz){return _Component_js__WEBPACK_IMPORTED_MODULE_5__/* ["default"] */.Z.registerClass(clz)},SeriesModel.protoInitialize=function(){var proto=SeriesModel.prototype;proto.type="series.__base__",proto.seriesIndex=0,proto.ignoreStyleOnData=!1,proto.hasSymbolVisual=!1,proto.defaultSymbol="circle",// Make sure the values can be accessed!
proto.visualStyleAccessPath="itemStyle",proto.visualDrawType="fill"}(),SeriesModel}(_Component_js__WEBPACK_IMPORTED_MODULE_5__/* ["default"] */.Z);
/**
 * MUST be called after `prepareSource` called
 * Here we need to make auto series, especially for auto legend. But we
 * do not modify series.name in option to avoid side effects.
 */
function autoSeriesName(seriesModel){
// User specified name has higher priority, otherwise it may cause
// series can not be queried unexpectedly.
var name=seriesModel.name;_util_model_js__WEBPACK_IMPORTED_MODULE_0__/* .isNameSpecified */.yu(seriesModel)||(seriesModel.name=getSeriesAutoName(seriesModel)||name)}function getSeriesAutoName(seriesModel){var data=seriesModel.getRawData(),dataDims=data.mapDimensionsAll("seriesName"),nameArr=[];return zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .each */.S6(dataDims,(function(dataDim){var dimInfo=data.getDimensionInfo(dataDim);dimInfo.displayName&&nameArr.push(dimInfo.displayName)})),nameArr.join(" ")}function dataTaskCount(context){return context.model.getRawData().count()}function dataTaskReset(context){var seriesModel=context.model;return seriesModel.setData(seriesModel.getRawData().cloneShallow()),dataTaskProgress}function dataTaskProgress(param,context){
// Avoid repeat cloneShallow when data just created in reset.
context.outputData&&param.end>context.outputData.count()&&context.model.getRawData().cloneShallow(context.outputData)}// TODO refactor
function wrapData(data,seriesModel){zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .each */.S6(zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .concatArray */.WW(data.CHANGABLE_METHODS,data.DOWNSAMPLE_METHODS),(function(methodName){data.wrapMethod(methodName,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .curry */.WA(onDataChange,seriesModel))}))}function onDataChange(seriesModel,newList){var task=getCurrentTask(seriesModel);return task&&
// Consider case: filter, selectRange
task.setOutputEnd((newList||this).count()),newList}function getCurrentTask(seriesModel){var scheduler=(seriesModel.ecModel||{}).scheduler,pipeline=scheduler&&scheduler.getPipeline(seriesModel.uid);if(pipeline){
// When pipline finished, the currrentTask keep the last
// task (renderTask).
var task=pipeline.currentTask;if(task){var agentStubMap=task.agentStubMap;agentStubMap&&(task=agentStubMap.get(seriesModel.uid))}return task}}
/* harmony default export */zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .mixin */.jB(SeriesModel,_model_mixin_dataFormat_js__WEBPACK_IMPORTED_MODULE_10__/* .DataFormatMixin */.X),zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .mixin */.jB(SeriesModel,_mixin_palette_js__WEBPACK_IMPORTED_MODULE_9__/* .PaletteMixin */._),(0,_util_clazz_js__WEBPACK_IMPORTED_MODULE_11__/* .mountExtend */.pw)(SeriesModel,_Component_js__WEBPACK_IMPORTED_MODULE_5__/* ["default"] */.Z),__webpack_exports__.Z=SeriesModel},
/***/961219:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */X:function(){/* binding */return DataFormatMixin},
/* harmony export */f:function(){/* binding */return normalizeTooltipFormatResult}
/* harmony export */});
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(933051),_data_helper_dataProvider_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(668540),_util_format_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(578988),DIMENSION_LABEL_REG=/\{@(.+?)\}/g,DataFormatMixin=
/** @class */
function(){function DataFormatMixin(){}
/**
   * Get params for formatter
   */return DataFormatMixin.prototype.getDataParams=function(dataIndex,dataType){var data=this.getData(dataType),rawValue=this.getRawValue(dataIndex,dataType),rawDataIndex=data.getRawIndex(dataIndex),name=data.getName(dataIndex),itemOpt=data.getRawDataItem(dataIndex),style=data.getItemVisual(dataIndex,"style"),color=style&&style[data.getItemVisual(dataIndex,"drawType")||"fill"],borderColor=style&&style.stroke,mainType=this.mainType,isSeries="series"===mainType,userOutput=data.userOutput&&data.userOutput.get();return{componentType:mainType,componentSubType:this.subType,componentIndex:this.componentIndex,seriesType:isSeries?this.subType:null,seriesIndex:this.seriesIndex,seriesId:isSeries?this.id:null,seriesName:isSeries?this.name:null,name:name,dataIndex:rawDataIndex,data:itemOpt,dataType:dataType,value:rawValue,color:color,borderColor:borderColor,dimensionNames:userOutput?userOutput.fullDimensions:null,encode:userOutput?userOutput.encode:null,
// Param name list for mapping `a`, `b`, `c`, `d`, `e`
$vars:["seriesName","name","value"]}},
/**
   * Format label
   * @param dataIndex
   * @param status 'normal' by default
   * @param dataType
   * @param labelDimIndex Only used in some chart that
   *        use formatter in different dimensions, like radar.
   * @param formatter Formatter given outside.
   * @return return null/undefined if no formatter
   */
DataFormatMixin.prototype.getFormattedLabel=function(dataIndex,status,dataType,labelDimIndex,formatter,extendParams){status=status||"normal";var data=this.getData(dataType),params=this.getDataParams(dataIndex,dataType);if(extendParams&&(params.value=extendParams.interpolatedValue),null!=labelDimIndex&&zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isArray */.kJ(params.value)&&(params.value=params.value[labelDimIndex]),!formatter){var itemModel=data.getItemModel(dataIndex);// @ts-ignore
formatter=itemModel.get("normal"===status?["label","formatter"]:[status,"label","formatter"])}if(zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isFunction */.mf(formatter))return params.status=status,params.dimensionIndex=labelDimIndex,formatter(params);if(zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isString */.HD(formatter)){var str=(0,_util_format_js__WEBPACK_IMPORTED_MODULE_1__/* .formatTpl */.kF)(formatter,params);// Support 'aaa{@[3]}bbb{@product}ccc'.
// Do not support '}' in dim name util have to.
return str.replace(DIMENSION_LABEL_REG,(function(origin,dimStr){var len=dimStr.length,dimLoose=dimStr;"["===dimLoose.charAt(0)&&"]"===dimLoose.charAt(len-1)&&(dimLoose=+dimLoose.slice(1,len-1));var val=(0,_data_helper_dataProvider_js__WEBPACK_IMPORTED_MODULE_2__/* .retrieveRawValue */.hk)(data,dataIndex,dimLoose);if(extendParams&&zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isArray */.kJ(extendParams.interpolatedValue)){var dimIndex=data.getDimensionIndex(dimLoose);dimIndex>=0&&(val=extendParams.interpolatedValue[dimIndex])}return null!=val?val+"":""}))}},
/**
   * Get raw value in option
   */
DataFormatMixin.prototype.getRawValue=function(idx,dataType){return(0,_data_helper_dataProvider_js__WEBPACK_IMPORTED_MODULE_2__/* .retrieveRawValue */.hk)(this.getData(dataType),idx)},
/**
   * Should be implemented.
   * @param {number} dataIndex
   * @param {boolean} [multipleSeries=false]
   * @param {string} [dataType]
   */
DataFormatMixin.prototype.formatTooltip=function(dataIndex,multipleSeries,dataType){},DataFormatMixin}();
/* harmony import */ // PENDING: previously we accept this type when calling `formatTooltip`,
// but guess little chance has been used outside. Do we need to backward
// compat it?
// type TooltipFormatResultLegacyObject = {
//     // `html` means the markup language text, either in 'html' or 'richText'.
//     // The name `html` is not appropriate because in 'richText' it is not a HTML
//     // string. But still support it for backward compatibility.
//     html: string;
//     markers: Dictionary<ColorString>;
// };
/**
 * For backward compat, normalize the return from `formatTooltip`.
 */
function normalizeTooltipFormatResult(result){var markupText,markupFragment;// let markers: Dictionary<ColorString>;
return zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isObject */.Kn(result)?result.type&&(markupFragment=result):markupText=result,{text:markupText,
// markers: markers || markersExisting,
frag:markupFragment}}
/***/},
/***/489887:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */D:function(){/* binding */return ItemStyleMixin},
/* harmony export */t:function(){/* binding */return ITEM_STYLE_KEY_MAP}
/* harmony export */});
/* harmony import */var _makeStyleMapper_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(659066),ITEM_STYLE_KEY_MAP=[["fill","color"],["stroke","borderColor"],["lineWidth","borderWidth"],["opacity"],["shadowBlur"],["shadowOffsetX"],["shadowOffsetY"],["shadowColor"],["lineDash","borderType"],["lineDashOffset","borderDashOffset"],["lineCap","borderCap"],["lineJoin","borderJoin"],["miterLimit","borderMiterLimit"]],getItemStyle=(0,_makeStyleMapper_js__WEBPACK_IMPORTED_MODULE_0__/* ["default"] */.Z)(ITEM_STYLE_KEY_MAP),ItemStyleMixin=
/** @class */
function(){function ItemStyleMixin(){}return ItemStyleMixin.prototype.getItemStyle=function(excludes,includes){return getItemStyle(this,excludes,includes)},ItemStyleMixin}();
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/},
/***/877515:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */K:function(){/* binding */return LineStyleMixin},
/* harmony export */v:function(){/* binding */return LINE_STYLE_KEY_MAP}
/* harmony export */});
/* harmony import */var _makeStyleMapper_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(659066),LINE_STYLE_KEY_MAP=[["lineWidth","width"],["stroke","color"],["opacity"],["shadowBlur"],["shadowOffsetX"],["shadowOffsetY"],["shadowColor"],["lineDash","type"],["lineDashOffset","dashOffset"],["lineCap","cap"],["lineJoin","join"],["miterLimit"]],getLineStyle=(0,_makeStyleMapper_js__WEBPACK_IMPORTED_MODULE_0__/* ["default"] */.Z)(LINE_STYLE_KEY_MAP),LineStyleMixin=
/** @class */
function(){function LineStyleMixin(){}return LineStyleMixin.prototype.getLineStyle=function(excludes){return getLineStyle(this,excludes)},LineStyleMixin}();
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/},
/***/659066:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Z:function(){/* binding */return makeStyleMapper}
/* harmony export */});
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(933051);
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
// TODO Parse shadow style
// TODO Only shallow path support
function makeStyleMapper(properties,ignoreParent){
// Normalize
for(var i=0;i<properties.length;i++)properties[i][1]||(properties[i][1]=properties[i][0]);return ignoreParent=ignoreParent||!1,function(model,excludes,includes){for(var style={},i=0;i<properties.length;i++){var propName=properties[i][1];if(!(excludes&&zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .indexOf */.cq(excludes,propName)>=0||includes&&zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .indexOf */.cq(includes,propName)<0)){var val=model.getShallow(propName,ignoreParent);null!=val&&(style[properties[i][0]]=val)}}// TODO Text or image?
return style}}
/***/},
/***/975494:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */_:function(){/* binding */return PaletteMixin},
/* harmony export */i:function(){/* binding */return getDecalFromPalette}
/* harmony export */});
/* harmony import */var _util_model_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(232234),innerColor=(0,_util_model_js__WEBPACK_IMPORTED_MODULE_0__/* .makeInner */.Yf)(),innerDecal=(0,_util_model_js__WEBPACK_IMPORTED_MODULE_0__/* .makeInner */.Yf)(),PaletteMixin=
/** @class */
function(){function PaletteMixin(){}return PaletteMixin.prototype.getColorFromPalette=function(name,scope,requestNum){var defaultPalette=(0,_util_model_js__WEBPACK_IMPORTED_MODULE_0__/* .normalizeToArray */.kF)(this.get("color",!0)),layeredPalette=this.get("colorLayer",!0);return getFromPalette(this,innerColor,defaultPalette,layeredPalette,name,scope,requestNum)},PaletteMixin.prototype.clearColorPalette=function(){clearPalette(this,innerColor)},PaletteMixin}();
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/function getDecalFromPalette(ecModel,name,scope,requestNum){var defaultDecals=(0,_util_model_js__WEBPACK_IMPORTED_MODULE_0__/* .normalizeToArray */.kF)(ecModel.get(["aria","decal","decals"]));return getFromPalette(ecModel,innerDecal,defaultDecals,null,name,scope,requestNum)}function getNearestPalette(palettes,requestColorNum){// TODO palettes must be in order
for(var paletteNum=palettes.length,i=0;i<paletteNum;i++)if(palettes[i].length>requestColorNum)return palettes[i];return palettes[paletteNum-1]}
/**
 * @param name MUST NOT be null/undefined. Otherwise call this function
 *             twise with the same parameters will get different result.
 * @param scope default this.
 * @return Can be null/undefined
 */function getFromPalette(that,inner,defaultPalette,layeredPalette,name,scope,requestNum){scope=scope||that;var scopeFields=inner(scope),paletteIdx=scopeFields.paletteIdx||0,paletteNameMap=scopeFields.paletteNameMap=scopeFields.paletteNameMap||{};// Use `hasOwnProperty` to avoid conflict with Object.prototype.
if(paletteNameMap.hasOwnProperty(name))return paletteNameMap[name];var palette=null!=requestNum&&layeredPalette?getNearestPalette(layeredPalette,requestNum):defaultPalette;// In case can't find in layered color palette.
if(palette=palette||defaultPalette,palette&&palette.length){var pickedPaletteItem=palette[paletteIdx];return name&&(paletteNameMap[name]=pickedPaletteItem),scopeFields.paletteIdx=(paletteIdx+1)%palette.length,pickedPaletteItem}}function clearPalette(that,inner){inner(that).paletteIdx=0,inner(that).paletteNameMap={}}
/***/},
/***/32702:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */b:function(){/* binding */return getCoordSysInfoBySeries}
/* harmony export */});
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(933051),_util_model_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(232234),CoordSysInfo=
/** @class */
function(){function CoordSysInfo(coordSysName){this.coordSysDims=[],this.axisMap=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .createHashMap */.kW)(),this.categoryAxisMap=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .createHashMap */.kW)(),this.coordSysName=coordSysName}return CoordSysInfo}();
/* harmony import */function getCoordSysInfoBySeries(seriesModel){var coordSysName=seriesModel.get("coordinateSystem"),result=new CoordSysInfo(coordSysName),fetch=fetchers[coordSysName];if(fetch)return fetch(seriesModel,result,result.axisMap,result.categoryAxisMap),result}var fetchers={cartesian2d:function(seriesModel,result,axisMap,categoryAxisMap){var xAxisModel=seriesModel.getReferringComponents("xAxis",_util_model_js__WEBPACK_IMPORTED_MODULE_1__/* .SINGLE_REFERRING */.C6).models[0],yAxisModel=seriesModel.getReferringComponents("yAxis",_util_model_js__WEBPACK_IMPORTED_MODULE_1__/* .SINGLE_REFERRING */.C6).models[0];result.coordSysDims=["x","y"],axisMap.set("x",xAxisModel),axisMap.set("y",yAxisModel),isCategory(xAxisModel)&&(categoryAxisMap.set("x",xAxisModel),result.firstCategoryDimIndex=0),isCategory(yAxisModel)&&(categoryAxisMap.set("y",yAxisModel),null==result.firstCategoryDimIndex&&(result.firstCategoryDimIndex=1))},singleAxis:function(seriesModel,result,axisMap,categoryAxisMap){var singleAxisModel=seriesModel.getReferringComponents("singleAxis",_util_model_js__WEBPACK_IMPORTED_MODULE_1__/* .SINGLE_REFERRING */.C6).models[0];result.coordSysDims=["single"],axisMap.set("single",singleAxisModel),isCategory(singleAxisModel)&&(categoryAxisMap.set("single",singleAxisModel),result.firstCategoryDimIndex=0)},polar:function(seriesModel,result,axisMap,categoryAxisMap){var polarModel=seriesModel.getReferringComponents("polar",_util_model_js__WEBPACK_IMPORTED_MODULE_1__/* .SINGLE_REFERRING */.C6).models[0],radiusAxisModel=polarModel.findAxisModel("radiusAxis"),angleAxisModel=polarModel.findAxisModel("angleAxis");result.coordSysDims=["radius","angle"],axisMap.set("radius",radiusAxisModel),axisMap.set("angle",angleAxisModel),isCategory(radiusAxisModel)&&(categoryAxisMap.set("radius",radiusAxisModel),result.firstCategoryDimIndex=0),isCategory(angleAxisModel)&&(categoryAxisMap.set("angle",angleAxisModel),null==result.firstCategoryDimIndex&&(result.firstCategoryDimIndex=1))},geo:function(seriesModel,result,axisMap,categoryAxisMap){result.coordSysDims=["lng","lat"]},parallel:function(seriesModel,result,axisMap,categoryAxisMap){var ecModel=seriesModel.ecModel,parallelModel=ecModel.getComponent("parallel",seriesModel.get("parallelIndex")),coordSysDims=result.coordSysDims=parallelModel.dimensions.slice();(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6)(parallelModel.parallelAxisIndex,(function(axisIndex,index){var axisModel=ecModel.getComponent("parallelAxis",axisIndex),axisDim=coordSysDims[index];axisMap.set(axisDim,axisModel),isCategory(axisModel)&&(categoryAxisMap.set(axisDim,axisModel),null==result.firstCategoryDimIndex&&(result.firstCategoryDimIndex=index))}))}};function isCategory(axisModel){return"category"===axisModel.get("type")}
/***/},
/***/328773:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return globalBackwardCompat}});
// EXTERNAL MODULE: ./node_modules/zrender/lib/core/util.js
var util=__webpack_require__(933051),model=__webpack_require__(232234),each=util/* each */.S6,isObject=util/* isObject */.Kn,POSSIBLE_STYLES=["areaStyle","lineStyle","nodeStyle","linkStyle","chordStyle","label","labelLine"];
// EXTERNAL MODULE: ./node_modules/echarts/lib/util/model.js
function compatEC2ItemStyle(opt){var itemStyleOpt=opt&&opt.itemStyle;if(itemStyleOpt)for(var i=0,len=POSSIBLE_STYLES.length;i<len;i++){var styleName=POSSIBLE_STYLES[i],normalItemStyleOpt=itemStyleOpt.normal,emphasisItemStyleOpt=itemStyleOpt.emphasis;normalItemStyleOpt&&normalItemStyleOpt[styleName]&&(opt[styleName]=opt[styleName]||{},opt[styleName].normal?util/* merge */.TS(opt[styleName].normal,normalItemStyleOpt[styleName]):opt[styleName].normal=normalItemStyleOpt[styleName],normalItemStyleOpt[styleName]=null),emphasisItemStyleOpt&&emphasisItemStyleOpt[styleName]&&(opt[styleName]=opt[styleName]||{},opt[styleName].emphasis?util/* merge */.TS(opt[styleName].emphasis,emphasisItemStyleOpt[styleName]):opt[styleName].emphasis=emphasisItemStyleOpt[styleName],emphasisItemStyleOpt[styleName]=null)}}function convertNormalEmphasis(opt,optType,useExtend){if(opt&&opt[optType]&&(opt[optType].normal||opt[optType].emphasis)){var normalOpt=opt[optType].normal,emphasisOpt=opt[optType].emphasis;normalOpt&&(// Timeline controlStyle has other properties besides normal and emphasis
useExtend?(opt[optType].normal=opt[optType].emphasis=null,util/* defaults */.ce(opt[optType],normalOpt)):opt[optType]=normalOpt),emphasisOpt&&(opt.emphasis=opt.emphasis||{},opt.emphasis[optType]=emphasisOpt,// Also compat the case user mix the style and focus together in ec3 style
// for example: { itemStyle: { normal: {}, emphasis: {focus, shadowBlur} } }
emphasisOpt.focus&&(opt.emphasis.focus=emphasisOpt.focus),emphasisOpt.blurScope&&(opt.emphasis.blurScope=emphasisOpt.blurScope))}}function removeEC3NormalStatus(opt){convertNormalEmphasis(opt,"itemStyle"),convertNormalEmphasis(opt,"lineStyle"),convertNormalEmphasis(opt,"areaStyle"),convertNormalEmphasis(opt,"label"),convertNormalEmphasis(opt,"labelLine"),// treemap
convertNormalEmphasis(opt,"upperLabel"),// graph
convertNormalEmphasis(opt,"edgeLabel")}function compatTextStyle(opt,propName){
// Check whether is not object (string\null\undefined ...)
var labelOptSingle=isObject(opt)&&opt[propName],textStyle=isObject(labelOptSingle)&&labelOptSingle.textStyle;if(textStyle){0;for(var i=0,len=model/* TEXT_STYLE_OPTIONS */.Td.length;i<len;i++){var textPropName=model/* TEXT_STYLE_OPTIONS */.Td[i];textStyle.hasOwnProperty(textPropName)&&(labelOptSingle[textPropName]=textStyle[textPropName])}}}function compatEC3CommonStyles(opt){opt&&(removeEC3NormalStatus(opt),compatTextStyle(opt,"label"),opt.emphasis&&compatTextStyle(opt.emphasis,"label"))}function processSeries(seriesOpt){if(isObject(seriesOpt)){compatEC2ItemStyle(seriesOpt),removeEC3NormalStatus(seriesOpt),compatTextStyle(seriesOpt,"label"),// treemap
compatTextStyle(seriesOpt,"upperLabel"),// graph
compatTextStyle(seriesOpt,"edgeLabel"),seriesOpt.emphasis&&(compatTextStyle(seriesOpt.emphasis,"label"),// treemap
compatTextStyle(seriesOpt.emphasis,"upperLabel"),// graph
compatTextStyle(seriesOpt.emphasis,"edgeLabel"));var markPoint=seriesOpt.markPoint;markPoint&&(compatEC2ItemStyle(markPoint),compatEC3CommonStyles(markPoint));var markLine=seriesOpt.markLine;markLine&&(compatEC2ItemStyle(markLine),compatEC3CommonStyles(markLine));var markArea=seriesOpt.markArea;markArea&&compatEC3CommonStyles(markArea);var data=seriesOpt.data;// Break with ec3: if `setOption` again, there may be no `type` in option,
// then the backward compat based on option type will not be performed.
if("graph"===seriesOpt.type){data=data||seriesOpt.nodes;var edgeData=seriesOpt.links||seriesOpt.edges;if(edgeData&&!util/* isTypedArray */.fU(edgeData))for(var i=0;i<edgeData.length;i++)compatEC3CommonStyles(edgeData[i]);util/* each */.S6(seriesOpt.categories,(function(opt){removeEC3NormalStatus(opt)}))}if(data&&!util/* isTypedArray */.fU(data))for(i=0;i<data.length;i++)compatEC3CommonStyles(data[i]);// mark point data
if(markPoint=seriesOpt.markPoint,markPoint&&markPoint.data){var mpData=markPoint.data;for(i=0;i<mpData.length;i++)compatEC3CommonStyles(mpData[i])}// mark line data
if(markLine=seriesOpt.markLine,markLine&&markLine.data){var mlData=markLine.data;for(i=0;i<mlData.length;i++)util/* isArray */.kJ(mlData[i])?(compatEC3CommonStyles(mlData[i][0]),compatEC3CommonStyles(mlData[i][1])):compatEC3CommonStyles(mlData[i])}// Series
"gauge"===seriesOpt.type?(compatTextStyle(seriesOpt,"axisLabel"),compatTextStyle(seriesOpt,"title"),compatTextStyle(seriesOpt,"detail")):"treemap"===seriesOpt.type?(convertNormalEmphasis(seriesOpt.breadcrumb,"itemStyle"),util/* each */.S6(seriesOpt.levels,(function(opt){removeEC3NormalStatus(opt)}))):"tree"===seriesOpt.type&&removeEC3NormalStatus(seriesOpt.leaves);// sunburst starts from ec4, so it does not need to compat levels.
}}function toArr(o){return util/* isArray */.kJ(o)?o:o?[o]:[]}function toObj(o){return(util/* isArray */.kJ(o)?o[0]:o)||{}}function globalCompatStyle(option,isTheme){each(toArr(option.series),(function(seriesOpt){isObject(seriesOpt)&&processSeries(seriesOpt)}));var axes=["xAxis","yAxis","radiusAxis","angleAxis","singleAxis","parallelAxis","radar"];isTheme&&axes.push("valueAxis","categoryAxis","logAxis","timeAxis"),each(axes,(function(axisName){each(toArr(option[axisName]),(function(axisOpt){axisOpt&&(compatTextStyle(axisOpt,"axisLabel"),compatTextStyle(axisOpt.axisPointer,"label"))}))})),each(toArr(option.parallel),(function(parallelOpt){var parallelAxisDefault=parallelOpt&&parallelOpt.parallelAxisDefault;compatTextStyle(parallelAxisDefault,"axisLabel"),compatTextStyle(parallelAxisDefault&&parallelAxisDefault.axisPointer,"label")})),each(toArr(option.calendar),(function(calendarOpt){convertNormalEmphasis(calendarOpt,"itemStyle"),compatTextStyle(calendarOpt,"dayLabel"),compatTextStyle(calendarOpt,"monthLabel"),compatTextStyle(calendarOpt,"yearLabel")})),// radar.name.textStyle
each(toArr(option.radar),(function(radarOpt){compatTextStyle(radarOpt,"name"),// Use axisName instead of name because component has name property
radarOpt.name&&null==radarOpt.axisName&&(radarOpt.axisName=radarOpt.name,delete radarOpt.name),null!=radarOpt.nameGap&&null==radarOpt.axisNameGap&&(radarOpt.axisNameGap=radarOpt.nameGap,delete radarOpt.nameGap)})),each(toArr(option.geo),(function(geoOpt){isObject(geoOpt)&&(compatEC3CommonStyles(geoOpt),each(toArr(geoOpt.regions),(function(regionObj){compatEC3CommonStyles(regionObj)})))})),each(toArr(option.timeline),(function(timelineOpt){compatEC3CommonStyles(timelineOpt),convertNormalEmphasis(timelineOpt,"label"),convertNormalEmphasis(timelineOpt,"itemStyle"),convertNormalEmphasis(timelineOpt,"controlStyle",!0);var data=timelineOpt.data;util/* isArray */.kJ(data)&&util/* each */.S6(data,(function(item){util/* isObject */.Kn(item)&&(convertNormalEmphasis(item,"label"),convertNormalEmphasis(item,"itemStyle"))}))})),each(toArr(option.toolbox),(function(toolboxOpt){convertNormalEmphasis(toolboxOpt,"iconStyle"),each(toolboxOpt.feature,(function(featureOpt){convertNormalEmphasis(featureOpt,"iconStyle")}))})),compatTextStyle(toObj(option.axisPointer),"label"),compatTextStyle(toObj(option.tooltip).axisPointer,"label")}// CONCATENATED MODULE: ./node_modules/echarts/lib/preprocessor/backwardCompat.js
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
function get(opt,path){for(var pathArr=path.split(","),obj=opt,i=0;i<pathArr.length;i++)if(obj=obj&&obj[pathArr[i]],null==obj)break;return obj}function set(opt,path,val,overwrite){for(var key,pathArr=path.split(","),obj=opt,i=0;i<pathArr.length-1;i++)key=pathArr[i],null==obj[key]&&(obj[key]={}),obj=obj[key];(overwrite||null==obj[pathArr[i]])&&(obj[pathArr[i]]=val)}function compatLayoutProperties(option){option&&(0,util/* each */.S6)(LAYOUT_PROPERTIES,(function(prop){prop[0]in option&&!(prop[1]in option)&&(option[prop[1]]=option[prop[0]])}))}var LAYOUT_PROPERTIES=[["x","left"],["y","top"],["x2","right"],["y2","bottom"]],COMPATITABLE_COMPONENTS=["grid","geo","parallel","legend","toolbox","title","visualMap","dataZoom","timeline"],BAR_ITEM_STYLE_MAP=[["borderRadius","barBorderRadius"],["borderColor","barBorderColor"],["borderWidth","barBorderWidth"]];function compatBarItemStyle(option){var itemStyle=option&&option.itemStyle;if(itemStyle)for(var i=0;i<BAR_ITEM_STYLE_MAP.length;i++){var oldName=BAR_ITEM_STYLE_MAP[i][1],newName=BAR_ITEM_STYLE_MAP[i][0];null!=itemStyle[oldName]&&(itemStyle[newName]=itemStyle[oldName])}}function compatPieLabel(option){option&&"edge"===option.alignTo&&null!=option.margin&&null==option.edgeDistance&&(option.edgeDistance=option.margin)}function compatSunburstState(option){option&&option.downplay&&!option.blur&&(option.blur=option.downplay)}function compatGraphFocus(option){option&&null!=option.focusNodeAdjacency&&(option.emphasis=option.emphasis||{},null==option.emphasis.focus&&(option.emphasis.focus="adjacency"))}function traverseTree(data,cb){if(data)for(var i=0;i<data.length;i++)cb(data[i]),data[i]&&traverseTree(data[i].children,cb)}function globalBackwardCompat(option,isTheme){globalCompatStyle(option,isTheme),// Make sure series array for model initialization.
option.series=(0,model/* normalizeToArray */.kF)(option.series),(0,util/* each */.S6)(option.series,(function(seriesOpt){if((0,util/* isObject */.Kn)(seriesOpt)){var seriesType=seriesOpt.type;if("line"===seriesType)null!=seriesOpt.clipOverflow&&(seriesOpt.clip=seriesOpt.clipOverflow);else if("pie"===seriesType||"gauge"===seriesType){null!=seriesOpt.clockWise&&(seriesOpt.clockwise=seriesOpt.clockWise),compatPieLabel(seriesOpt.label);var data=seriesOpt.data;if(data&&!(0,util/* isTypedArray */.fU)(data))for(var i=0;i<data.length;i++)compatPieLabel(data[i]);null!=seriesOpt.hoverOffset&&(seriesOpt.emphasis=seriesOpt.emphasis||{},(seriesOpt.emphasis.scaleSize=null)&&(seriesOpt.emphasis.scaleSize=seriesOpt.hoverOffset))}else if("gauge"===seriesType){var pointerColor=get(seriesOpt,"pointer.color");null!=pointerColor&&set(seriesOpt,"itemStyle.color",pointerColor)}else if("bar"===seriesType){compatBarItemStyle(seriesOpt),compatBarItemStyle(seriesOpt.backgroundStyle),compatBarItemStyle(seriesOpt.emphasis);data=seriesOpt.data;if(data&&!(0,util/* isTypedArray */.fU)(data))for(i=0;i<data.length;i++)"object"===typeof data[i]&&(compatBarItemStyle(data[i]),compatBarItemStyle(data[i]&&data[i].emphasis))}else if("sunburst"===seriesType){var highlightPolicy=seriesOpt.highlightPolicy;highlightPolicy&&(seriesOpt.emphasis=seriesOpt.emphasis||{},seriesOpt.emphasis.focus||(seriesOpt.emphasis.focus=highlightPolicy)),compatSunburstState(seriesOpt),traverseTree(seriesOpt.data,compatSunburstState)}else"graph"===seriesType||"sankey"===seriesType?compatGraphFocus(seriesOpt):"map"===seriesType&&(seriesOpt.mapType&&!seriesOpt.map&&(seriesOpt.map=seriesOpt.mapType),seriesOpt.mapLocation&&(0,util/* defaults */.ce)(seriesOpt,seriesOpt.mapLocation));null!=seriesOpt.hoverAnimation&&(seriesOpt.emphasis=seriesOpt.emphasis||{},seriesOpt.emphasis&&null==seriesOpt.emphasis.scale&&(seriesOpt.emphasis.scale=seriesOpt.hoverAnimation)),compatLayoutProperties(seriesOpt)}})),// dataRange has changed to visualMap
option.dataRange&&(option.visualMap=option.dataRange),(0,util/* each */.S6)(COMPATITABLE_COMPONENTS,(function(componentName){var options=option[componentName];options&&((0,util/* isArray */.kJ)(options)||(options=[options]),(0,util/* each */.S6)(options,(function(option){compatLayoutProperties(option)})))}))}
/***/},
/***/122528:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
function dataFilter(seriesType){return{seriesType:seriesType,reset:function(seriesModel,ecModel){var legendModels=ecModel.findComponents({mainType:"legend"});if(legendModels&&legendModels.length){var data=seriesModel.getData();data.filterSelf((function(idx){// If in any legend component the status is not selected.
for(var name=data.getName(idx),i=0;i<legendModels.length;i++)
// @ts-ignore FIXME: LegendModel
if(!legendModels[i].isSelected(name))return!1;return!0}))}}}}
/***/
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Z:function(){/* binding */return dataFilter}
/* harmony export */})},
/***/564088:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Z:function(){/* binding */return dataSample}
/* harmony export */});
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(933051),samplers={average:function(frame){for(var sum=0,count=0,i=0;i<frame.length;i++)isNaN(frame[i])||(sum+=frame[i],count++);// Return NaN if count is 0
return 0===count?NaN:sum/count},sum:function(frame){for(var sum=0,i=0;i<frame.length;i++)
// Ignore NaN
sum+=frame[i]||0;return sum},max:function(frame){for(var max=-1/0,i=0;i<frame.length;i++)frame[i]>max&&(max=frame[i]);// NaN will cause illegal axis extent.
return isFinite(max)?max:NaN},min:function(frame){for(var min=1/0,i=0;i<frame.length;i++)frame[i]<min&&(min=frame[i]);// NaN will cause illegal axis extent.
return isFinite(min)?min:NaN},
// TODO
// Median
nearest:function(frame){return frame[0]}},indexSampler=function(frame){return Math.round(frame.length/2)};
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/function dataSample(seriesType){return{seriesType:seriesType,
// FIXME:TS never used, so comment it
// modifyOutputEnd: true,
reset:function(seriesModel,ecModel,api){var data=seriesModel.getData(),sampling=seriesModel.get("sampling"),coordSys=seriesModel.coordinateSystem,count=data.count();// Only cartesian2d support down sampling. Disable it when there is few data.
if(count>10&&"cartesian2d"===coordSys.type&&sampling){var baseAxis=coordSys.getBaseAxis(),valueAxis=coordSys.getOtherAxis(baseAxis),extent=baseAxis.getExtent(),dpr=api.getDevicePixelRatio(),size=Math.abs(extent[1]-extent[0])*(dpr||1),rate=Math.round(count/size);if(isFinite(rate)&&rate>1){"lttb"===sampling&&seriesModel.setData(data.lttbDownSample(data.mapDimension(valueAxis.dim),1/rate));var sampler=void 0;(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isString */.HD)(sampling)?sampler=samplers[sampling]:(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isFunction */.mf)(sampling)&&(sampler=sampling),sampler&&
// Only support sample the first dim mapped from value axis.
seriesModel.setData(data.downSample(data.mapDimension(valueAxis.dim),1/rate,sampler,indexSampler))}}}}}
/***/},
/***/433809:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Z:function(){/* binding */return dataStack}
/* harmony export */});
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(933051),_util_number_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(785669);
/* harmony import */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
// (1) [Caution]: the logic is correct based on the premises:
//     data processing stage is blocked in stream.
//     See <module:echarts/stream/Scheduler#performDataProcessorTasks>
// (2) Only register once when import repeatedly.
//     Should be executed after series is filtered and before stack calculation.
function dataStack(ecModel){var stackInfoMap=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .createHashMap */.kW)();ecModel.eachSeries((function(seriesModel){var stack=seriesModel.get("stack");// Compatible: when `stack` is set as '', do not stack.
if(stack){var stackInfoList=stackInfoMap.get(stack)||stackInfoMap.set(stack,[]),data=seriesModel.getData(),stackInfo={
// Used for calculate axis extent automatically.
// TODO: Type getCalculationInfo return more specific type?
stackResultDimension:data.getCalculationInfo("stackResultDimension"),stackedOverDimension:data.getCalculationInfo("stackedOverDimension"),stackedDimension:data.getCalculationInfo("stackedDimension"),stackedByDimension:data.getCalculationInfo("stackedByDimension"),isStackedByIndex:data.getCalculationInfo("isStackedByIndex"),data:data,seriesModel:seriesModel};// If stacked on axis that do not support data stack.
if(!stackInfo.stackedDimension||!stackInfo.isStackedByIndex&&!stackInfo.stackedByDimension)return;stackInfoList.length&&data.setCalculationInfo("stackedOnSeries",stackInfoList[stackInfoList.length-1].seriesModel),stackInfoList.push(stackInfo)}})),stackInfoMap.each(calculateStack)}function calculateStack(stackInfoList){(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6)(stackInfoList,(function(targetStackInfo,idxInStack){var resultVal=[],resultNaN=[NaN,NaN],dims=[targetStackInfo.stackResultDimension,targetStackInfo.stackedOverDimension],targetData=targetStackInfo.data,isStackedByIndex=targetStackInfo.isStackedByIndex,stackStrategy=targetStackInfo.seriesModel.get("stackStrategy")||"samesign";// Should not write on raw data, because stack series model list changes
// depending on legend selection.
targetData.modify(dims,(function(v0,v1,dataIndex){var byValue,stackedDataRawIndex,sum=targetData.get(targetStackInfo.stackedDimension,dataIndex);// Consider `connectNulls` of line area, if value is NaN, stackedOver
// should also be NaN, to draw a appropriate belt area.
if(isNaN(sum))return resultNaN;isStackedByIndex?stackedDataRawIndex=targetData.getRawIndex(dataIndex):byValue=targetData.get(targetStackInfo.stackedByDimension,dataIndex);// If stackOver is NaN, chart view will render point on value start.
for(var stackedOver=NaN,j=idxInStack-1;j>=0;j--){var stackInfo=stackInfoList[j];// Has been optimized by inverted indices on `stackedByDimension`.
if(isStackedByIndex||(stackedDataRawIndex=stackInfo.data.rawIndexOf(stackInfo.stackedByDimension,byValue)),stackedDataRawIndex>=0){var val=stackInfo.data.getByRawIndex(stackInfo.stackResultDimension,stackedDataRawIndex);// Considering positive stack, negative stack and empty data
if("all"===stackStrategy||"positive"===stackStrategy&&val>0||"negative"===stackStrategy&&val<0||"samesign"===stackStrategy&&sum>=0&&val>0||"samesign"===stackStrategy&&sum<=0&&val<0){
// The sum has to be very small to be affected by the
// floating arithmetic problem. An incorrect result will probably
// cause axis min/max to be filtered incorrectly.
sum=(0,_util_number_js__WEBPACK_IMPORTED_MODULE_1__/* .addSafe */.S$)(sum,val),stackedOver=val;break}}}return resultVal[0]=sum,resultVal[1]=stackedOver,resultVal}))}))}
/***/},
/***/594401:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Z:function(){/* binding */return negativeDataFilter}
/* harmony export */});
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(933051);
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/function negativeDataFilter(seriesType){return{seriesType:seriesType,reset:function(seriesModel,ecModel){var data=seriesModel.getData();data.filterSelf((function(idx){
// handle negative value condition
var valueDim=data.mapDimension("value"),curValue=data.get(valueDim,idx);return!((0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isNumber */.hj)(curValue)&&!isNaN(curValue)&&curValue<0)}))}}}
/***/},
/***/406267:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */N:function(){/* binding */return install}
/* harmony export */});
/* harmony import */var zrender_lib_canvas_Painter_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(119451);
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/function install(registers){registers.registerPainter("canvas",zrender_lib_canvas_Painter_js__WEBPACK_IMPORTED_MODULE_0__/* ["default"] */.Z)}
/***/},
/***/408492:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */N:function(){/* binding */return install}
/* harmony export */});
/* harmony import */var zrender_lib_svg_Painter_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(12308);
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/function install(registers){registers.registerPainter("svg",zrender_lib_svg_Painter_js__WEBPACK_IMPORTED_MODULE_0__/* ["default"] */.Z)}
/***/},
/***/70103:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony import */var tslib__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(570655),_util_number_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(785669),_util_format_js__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(578988),_Scale_js__WEBPACK_IMPORTED_MODULE_4__=__webpack_require__(660379),_helper_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(665021),roundNumber=_util_number_js__WEBPACK_IMPORTED_MODULE_0__/* .round */.NM,IntervalScale=
/** @class */
function(_super){function IntervalScale(){var _this=null!==_super&&_super.apply(this,arguments)||this;return _this.type="interval",// Step is calculated in adjustExtent.
_this._interval=0,_this._intervalPrecision=2,_this}return(0,tslib__WEBPACK_IMPORTED_MODULE_1__/* .__extends */.ZT)(IntervalScale,_super),IntervalScale.prototype.parse=function(val){return val},IntervalScale.prototype.contain=function(val){return _helper_js__WEBPACK_IMPORTED_MODULE_2__/* .contain */.XS(val,this._extent)},IntervalScale.prototype.normalize=function(val){return _helper_js__WEBPACK_IMPORTED_MODULE_2__/* .normalize */.Fv(val,this._extent)},IntervalScale.prototype.scale=function(val){return _helper_js__WEBPACK_IMPORTED_MODULE_2__/* .scale */.bA(val,this._extent)},IntervalScale.prototype.setExtent=function(start,end){var thisExtent=this._extent;// start,end may be a Number like '25',so...
isNaN(start)||(thisExtent[0]=parseFloat(start)),isNaN(end)||(thisExtent[1]=parseFloat(end))},IntervalScale.prototype.unionExtent=function(other){var extent=this._extent;other[0]<extent[0]&&(extent[0]=other[0]),other[1]>extent[1]&&(extent[1]=other[1]),// unionExtent may called by it's sub classes
this.setExtent(extent[0],extent[1])},IntervalScale.prototype.getInterval=function(){return this._interval},IntervalScale.prototype.setInterval=function(interval){this._interval=interval,// Dropped auto calculated niceExtent and use user-set extent.
// We assume user wants to set both interval, min, max to get a better result.
this._niceExtent=this._extent.slice(),this._intervalPrecision=_helper_js__WEBPACK_IMPORTED_MODULE_2__/* .getIntervalPrecision */.lb(interval)},
/**
   * @param expandToNicedExtent Whether expand the ticks to niced extent.
   */
IntervalScale.prototype.getTicks=function(expandToNicedExtent){var interval=this._interval,extent=this._extent,niceTickExtent=this._niceExtent,intervalPrecision=this._intervalPrecision,ticks=[];// If interval is 0, return [];
if(!interval)return ticks;// Consider this case: using dataZoom toolbox, zoom and zoom.
var safeLimit=1e4;extent[0]<niceTickExtent[0]&&(expandToNicedExtent?ticks.push({value:roundNumber(niceTickExtent[0]-interval,intervalPrecision)}):ticks.push({value:extent[0]}));var tick=niceTickExtent[0];while(tick<=niceTickExtent[1]){if(ticks.push({value:tick}),// Avoid rounding error
tick=roundNumber(tick+interval,intervalPrecision),tick===ticks[ticks.length-1].value)
// Consider out of safe float point, e.g.,
// -3711126.9907707 + 2e-10 === -3711126.9907707
break;if(ticks.length>safeLimit)return[]}// Consider this case: the last item of ticks is smaller
// than niceTickExtent[1] and niceTickExtent[1] === extent[1].
var lastNiceTick=ticks.length?ticks[ticks.length-1].value:niceTickExtent[1];return extent[1]>lastNiceTick&&(expandToNicedExtent?ticks.push({value:roundNumber(lastNiceTick+interval,intervalPrecision)}):ticks.push({value:extent[1]})),ticks},IntervalScale.prototype.getMinorTicks=function(splitNumber){for(var ticks=this.getTicks(!0),minorTicks=[],extent=this.getExtent(),i=1;i<ticks.length;i++){var nextTick=ticks[i],prevTick=ticks[i-1],count=0,minorTicksGroup=[],interval=nextTick.value-prevTick.value,minorInterval=interval/splitNumber;while(count<splitNumber-1){var minorTick=roundNumber(prevTick.value+(count+1)*minorInterval);// For the first and last interval. The count may be less than splitNumber.
minorTick>extent[0]&&minorTick<extent[1]&&minorTicksGroup.push(minorTick),count++}minorTicks.push(minorTicksGroup)}return minorTicks},
/**
   * @param opt.precision If 'auto', use nice presision.
   * @param opt.pad returns 1.50 but not 1.5 if precision is 2.
   */
IntervalScale.prototype.getLabel=function(data,opt){if(null==data)return"";var precision=opt&&opt.precision;null==precision?precision=_util_number_js__WEBPACK_IMPORTED_MODULE_0__/* .getPrecision */.p8(data.value)||0:"auto"===precision&&(
// Should be more precise then tick.
precision=this._intervalPrecision);// (1) If `precision` is set, 12.005 should be display as '12.00500'.
// (2) Use roundNumber (toFixed) to avoid scientific notation like '3.5e-7'.
var dataNum=roundNumber(data.value,precision,!0);return _util_format_js__WEBPACK_IMPORTED_MODULE_3__/* .addCommas */.OD(dataNum)},
/**
   * @param splitNumber By default `5`.
   */
IntervalScale.prototype.calcNiceTicks=function(splitNumber,minInterval,maxInterval){splitNumber=splitNumber||5;var extent=this._extent,span=extent[1]-extent[0];if(isFinite(span)){// User may set axis min 0 and data are all negative
// FIXME If it needs to reverse ?
span<0&&(span=-span,extent.reverse());var result=_helper_js__WEBPACK_IMPORTED_MODULE_2__/* .intervalScaleNiceTicks */.Qf(extent,splitNumber,minInterval,maxInterval);this._intervalPrecision=result.intervalPrecision,this._interval=result.interval,this._niceExtent=result.niceTickExtent}},IntervalScale.prototype.calcNiceExtent=function(opt){var extent=this._extent;// If extent start and end are same, expand them
if(extent[0]===extent[1])if(0!==extent[0]){
// Expand extent
// Note that extents can be both negative. See #13154
var expandSize=Math.abs(extent[0]);// In the fowllowing case
//      Axis has been fixed max 100
//      Plus data are all 100 and axis extent are [100, 100].
// Extend to the both side will cause expanded max is larger than fixed max.
// So only expand to the smaller side.
opt.fixMax||(extent[1]+=expandSize/2),extent[0]-=expandSize/2}else extent[1]=1;var span=extent[1]-extent[0];// If there are no data and extent are [Infinity, -Infinity]
isFinite(span)||(extent[0]=0,extent[1]=1),this.calcNiceTicks(opt.splitNumber,opt.minInterval,opt.maxInterval);// let extent = this._extent;
var interval=this._interval;opt.fixMin||(extent[0]=roundNumber(Math.floor(extent[0]/interval)*interval)),opt.fixMax||(extent[1]=roundNumber(Math.ceil(extent[1]/interval)*interval))},IntervalScale.prototype.setNiceExtent=function(min,max){this._niceExtent=[min,max]},IntervalScale.type="interval",IntervalScale}(_Scale_js__WEBPACK_IMPORTED_MODULE_4__/* ["default"] */.Z);
/* harmony import */_Scale_js__WEBPACK_IMPORTED_MODULE_4__/* ["default"] */.Z.registerClass(IntervalScale),
/* harmony default export */__webpack_exports__.Z=IntervalScale},
/***/976304:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony import */var tslib__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(570655),zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_4__=__webpack_require__(933051),_Scale_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(660379),_util_number_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(785669),_helper_js__WEBPACK_IMPORTED_MODULE_5__=__webpack_require__(665021),_Interval_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(70103),scaleProto=_Scale_js__WEBPACK_IMPORTED_MODULE_0__/* ["default"] */.Z.prototype,intervalScaleProto=_Interval_js__WEBPACK_IMPORTED_MODULE_1__/* ["default"] */.Z.prototype,roundingErrorFix=_util_number_js__WEBPACK_IMPORTED_MODULE_2__/* .round */.NM,mathFloor=Math.floor,mathCeil=Math.ceil,mathPow=Math.pow,mathLog=Math.log,LogScale=
/** @class */
function(_super){function LogScale(){var _this=null!==_super&&_super.apply(this,arguments)||this;return _this.type="log",_this.base=10,_this._originalScale=new _Interval_js__WEBPACK_IMPORTED_MODULE_1__/* ["default"] */.Z,// FIXME:TS actually used by `IntervalScale`
_this._interval=0,_this}
/**
   * @param Whether expand the ticks to niced extent.
   */return(0,tslib__WEBPACK_IMPORTED_MODULE_3__/* .__extends */.ZT)(LogScale,_super),LogScale.prototype.getTicks=function(expandToNicedExtent){var originalScale=this._originalScale,extent=this._extent,originalExtent=originalScale.getExtent(),ticks=intervalScaleProto.getTicks.call(this,expandToNicedExtent);return zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_4__/* .map */.UI(ticks,(function(tick){var val=tick.value,powVal=_util_number_js__WEBPACK_IMPORTED_MODULE_2__/* .round */.NM(mathPow(this.base,val));// Fix #4158
return powVal=val===extent[0]&&this._fixMin?fixRoundingError(powVal,originalExtent[0]):powVal,powVal=val===extent[1]&&this._fixMax?fixRoundingError(powVal,originalExtent[1]):powVal,{value:powVal}}),this)},LogScale.prototype.setExtent=function(start,end){var base=mathLog(this.base);// log(-Infinity) is NaN, so safe guard here
start=mathLog(Math.max(0,start))/base,end=mathLog(Math.max(0,end))/base,intervalScaleProto.setExtent.call(this,start,end)},
/**
   * @return {number} end
   */
LogScale.prototype.getExtent=function(){var base=this.base,extent=scaleProto.getExtent.call(this);extent[0]=mathPow(base,extent[0]),extent[1]=mathPow(base,extent[1]);// Fix #4158
var originalScale=this._originalScale,originalExtent=originalScale.getExtent();return this._fixMin&&(extent[0]=fixRoundingError(extent[0],originalExtent[0])),this._fixMax&&(extent[1]=fixRoundingError(extent[1],originalExtent[1])),extent},LogScale.prototype.unionExtent=function(extent){this._originalScale.unionExtent(extent);var base=this.base;extent[0]=mathLog(extent[0])/mathLog(base),extent[1]=mathLog(extent[1])/mathLog(base),scaleProto.unionExtent.call(this,extent)},LogScale.prototype.unionExtentFromData=function(data,dim){
// TODO
// filter value that <= 0
this.unionExtent(data.getApproximateExtent(dim))},
/**
   * Update interval and extent of intervals for nice ticks
   * @param approxTickNum default 10 Given approx tick number
   */
LogScale.prototype.calcNiceTicks=function(approxTickNum){approxTickNum=approxTickNum||10;var extent=this._extent,span=extent[1]-extent[0];if(!(span===1/0||span<=0)){var interval=_util_number_js__WEBPACK_IMPORTED_MODULE_2__/* .quantity */.Xd(span),err=approxTickNum/span*interval;// Filter ticks to get closer to the desired count.
err<=.5&&(interval*=10);// Interval should be integer
while(!isNaN(interval)&&Math.abs(interval)<1&&Math.abs(interval)>0)interval*=10;var niceExtent=[_util_number_js__WEBPACK_IMPORTED_MODULE_2__/* .round */.NM(mathCeil(extent[0]/interval)*interval),_util_number_js__WEBPACK_IMPORTED_MODULE_2__/* .round */.NM(mathFloor(extent[1]/interval)*interval)];this._interval=interval,this._niceExtent=niceExtent}},LogScale.prototype.calcNiceExtent=function(opt){intervalScaleProto.calcNiceExtent.call(this,opt),this._fixMin=opt.fixMin,this._fixMax=opt.fixMax},LogScale.prototype.parse=function(val){return val},LogScale.prototype.contain=function(val){return val=mathLog(val)/mathLog(this.base),_helper_js__WEBPACK_IMPORTED_MODULE_5__/* .contain */.XS(val,this._extent)},LogScale.prototype.normalize=function(val){return val=mathLog(val)/mathLog(this.base),_helper_js__WEBPACK_IMPORTED_MODULE_5__/* .normalize */.Fv(val,this._extent)},LogScale.prototype.scale=function(val){return val=_helper_js__WEBPACK_IMPORTED_MODULE_5__/* .scale */.bA(val,this._extent),mathPow(this.base,val)},LogScale.type="log",LogScale}(_Scale_js__WEBPACK_IMPORTED_MODULE_0__/* ["default"] */.Z),proto=LogScale.prototype;
/* harmony import */function fixRoundingError(val,originalVal){return roundingErrorFix(val,_util_number_js__WEBPACK_IMPORTED_MODULE_2__/* .getPrecision */.p8(originalVal))}proto.getMinorTicks=intervalScaleProto.getMinorTicks,proto.getLabel=intervalScaleProto.getLabel,_Scale_js__WEBPACK_IMPORTED_MODULE_0__/* ["default"] */.Z.registerClass(LogScale),
/* harmony default export */__webpack_exports__.Z=LogScale},
/***/385043:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony import */var tslib__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(570655),_Scale_js__WEBPACK_IMPORTED_MODULE_4__=__webpack_require__(660379),_data_OrdinalMeta_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(251401),_helper_js__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(665021),zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(933051),OrdinalScale=
/** @class */
function(_super){function OrdinalScale(setting){var _this=_super.call(this,setting)||this;_this.type="ordinal";var ordinalMeta=_this.getSetting("ordinalMeta");// Caution: Should not use instanceof, consider ec-extensions using
// import approach to get OrdinalMeta class.
return ordinalMeta||(ordinalMeta=new _data_OrdinalMeta_js__WEBPACK_IMPORTED_MODULE_1__/* ["default"] */.Z({})),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .isArray */.kJ)(ordinalMeta)&&(ordinalMeta=new _data_OrdinalMeta_js__WEBPACK_IMPORTED_MODULE_1__/* ["default"] */.Z({categories:(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .map */.UI)(ordinalMeta,(function(item){return(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .isObject */.Kn)(item)?item.value:item}))})),_this._ordinalMeta=ordinalMeta,_this._extent=_this.getSetting("extent")||[0,ordinalMeta.categories.length-1],_this}return(0,tslib__WEBPACK_IMPORTED_MODULE_0__/* .__extends */.ZT)(OrdinalScale,_super),OrdinalScale.prototype.parse=function(val){
// Caution: Math.round(null) will return `0` rather than `NaN`
return null==val?NaN:(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .isString */.HD)(val)?this._ordinalMeta.getOrdinal(val):Math.round(val)},OrdinalScale.prototype.contain=function(rank){return rank=this.parse(rank),_helper_js__WEBPACK_IMPORTED_MODULE_3__/* .contain */.XS(rank,this._extent)&&null!=this._ordinalMeta.categories[rank]},
/**
   * Normalize given rank or name to linear [0, 1]
   * @param val raw ordinal number.
   * @return normalized value in [0, 1].
   */
OrdinalScale.prototype.normalize=function(val){return val=this._getTickNumber(this.parse(val)),_helper_js__WEBPACK_IMPORTED_MODULE_3__/* .normalize */.Fv(val,this._extent)},
/**
   * @param val normalized value in [0, 1].
   * @return raw ordinal number.
   */
OrdinalScale.prototype.scale=function(val){return val=Math.round(_helper_js__WEBPACK_IMPORTED_MODULE_3__/* .scale */.bA(val,this._extent)),this.getRawOrdinalNumber(val)},OrdinalScale.prototype.getTicks=function(){var ticks=[],extent=this._extent,rank=extent[0];while(rank<=extent[1])ticks.push({value:rank}),rank++;return ticks},OrdinalScale.prototype.getMinorTicks=function(splitNumber){},
/**
   * @see `Ordinal['_ordinalNumbersByTick']`
   */
OrdinalScale.prototype.setSortInfo=function(info){if(null!=info){for(var infoOrdinalNumbers=info.ordinalNumbers,ordinalsByTick=this._ordinalNumbersByTick=[],ticksByOrdinal=this._ticksByOrdinalNumber=[],tickNum=0,allCategoryLen=this._ordinalMeta.categories.length,len=Math.min(allCategoryLen,infoOrdinalNumbers.length);tickNum<len;++tickNum){var ordinalNumber=infoOrdinalNumbers[tickNum];ordinalsByTick[tickNum]=ordinalNumber,ticksByOrdinal[ordinalNumber]=tickNum}// Handle that `series.data` only covers part of the `axis.category.data`.
for(var unusedOrdinal=0;tickNum<allCategoryLen;++tickNum){while(null!=ticksByOrdinal[unusedOrdinal])unusedOrdinal++;ordinalsByTick.push(unusedOrdinal),ticksByOrdinal[unusedOrdinal]=tickNum}}else this._ordinalNumbersByTick=this._ticksByOrdinalNumber=null},OrdinalScale.prototype._getTickNumber=function(ordinal){var ticksByOrdinalNumber=this._ticksByOrdinalNumber;// also support ordinal out of range of `ordinalMeta.categories.length`,
// where ordinal numbers are used as tick value directly.
return ticksByOrdinalNumber&&ordinal>=0&&ordinal<ticksByOrdinalNumber.length?ticksByOrdinalNumber[ordinal]:ordinal},
/**
   * @usage
   * ```js
   * const ordinalNumber = ordinalScale.getRawOrdinalNumber(tickVal);
   *
   * // case0
   * const rawOrdinalValue = axisModel.getCategories()[ordinalNumber];
   * // case1
   * const rawOrdinalValue = this._ordinalMeta.categories[ordinalNumber];
   * // case2
   * const coord = axis.dataToCoord(ordinalNumber);
   * ```
   *
   * @param {OrdinalNumber} tickNumber index of display
   */
OrdinalScale.prototype.getRawOrdinalNumber=function(tickNumber){var ordinalNumbersByTick=this._ordinalNumbersByTick;// tickNumber may be out of range, e.g., when axis max is larger than `ordinalMeta.categories.length`.,
// where ordinal numbers are used as tick value directly.
return ordinalNumbersByTick&&tickNumber>=0&&tickNumber<ordinalNumbersByTick.length?ordinalNumbersByTick[tickNumber]:tickNumber},
/**
   * Get item on tick
   */
OrdinalScale.prototype.getLabel=function(tick){if(!this.isBlank()){var ordinalNumber=this.getRawOrdinalNumber(tick.value),cateogry=this._ordinalMeta.categories[ordinalNumber];// Note that if no data, ordinalMeta.categories is an empty array.
// Return empty if it's not exist.
return null==cateogry?"":cateogry+""}},OrdinalScale.prototype.count=function(){return this._extent[1]-this._extent[0]+1},OrdinalScale.prototype.unionExtentFromData=function(data,dim){this.unionExtent(data.getApproximateExtent(dim))},
/**
   * @override
   * If value is in extent range
   */
OrdinalScale.prototype.isInExtentRange=function(value){return value=this._getTickNumber(value),this._extent[0]<=value&&this._extent[1]>=value},OrdinalScale.prototype.getOrdinalMeta=function(){return this._ordinalMeta},OrdinalScale.prototype.calcNiceTicks=function(){},OrdinalScale.prototype.calcNiceExtent=function(){},OrdinalScale.type="ordinal",OrdinalScale}(_Scale_js__WEBPACK_IMPORTED_MODULE_4__/* ["default"] */.Z);
/* harmony import */_Scale_js__WEBPACK_IMPORTED_MODULE_4__/* ["default"] */.Z.registerClass(OrdinalScale),
/* harmony default export */__webpack_exports__.Z=OrdinalScale},
/***/660379:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony import */var _util_clazz_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(834251),Scale=
/** @class */
function(){function Scale(setting){this._setting=setting||{},this._extent=[1/0,-1/0]}return Scale.prototype.getSetting=function(name){return this._setting[name]},
/**
   * Set extent from data
   */
Scale.prototype.unionExtent=function(other){var extent=this._extent;other[0]<extent[0]&&(extent[0]=other[0]),other[1]>extent[1]&&(extent[1]=other[1])},
/**
   * Set extent from data
   */
Scale.prototype.unionExtentFromData=function(data,dim){this.unionExtent(data.getApproximateExtent(dim))},
/**
   * Get extent
   *
   * Extent is always in increase order.
   */
Scale.prototype.getExtent=function(){return this._extent.slice()},
/**
   * Set extent
   */
Scale.prototype.setExtent=function(start,end){var thisExtent=this._extent;isNaN(start)||(thisExtent[0]=start),isNaN(end)||(thisExtent[1]=end)},
/**
   * If value is in extent range
   */
Scale.prototype.isInExtentRange=function(value){return this._extent[0]<=value&&this._extent[1]>=value},
/**
   * When axis extent depends on data and no data exists,
   * axis ticks should not be drawn, which is named 'blank'.
   */
Scale.prototype.isBlank=function(){return this._isBlank},
/**
   * When axis extent depends on data and no data exists,
   * axis ticks should not be drawn, which is named 'blank'.
   */
Scale.prototype.setBlank=function(isBlank){this._isBlank=isBlank},Scale}();
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/_util_clazz_js__WEBPACK_IMPORTED_MODULE_0__/* .enableClassManagement */.au(Scale),
/* harmony default export */__webpack_exports__.Z=Scale},
/***/861618:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony import */var tslib__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(570655),_util_number_js__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(785669),_util_time_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(515015),_helper_js__WEBPACK_IMPORTED_MODULE_4__=__webpack_require__(665021),_Interval_js__WEBPACK_IMPORTED_MODULE_5__=__webpack_require__(70103),_Scale_js__WEBPACK_IMPORTED_MODULE_6__=__webpack_require__(660379),zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(933051),bisect=function(a,x,lo,hi){while(lo<hi){var mid=lo+hi>>>1;a[mid][1]<x?lo=mid+1:hi=mid}return lo},TimeScale=
/** @class */
function(_super){function TimeScale(settings){var _this=_super.call(this,settings)||this;return _this.type="time",_this}
/**
   * Get label is mainly for other components like dataZoom, tooltip.
   */return(0,tslib__WEBPACK_IMPORTED_MODULE_0__/* .__extends */.ZT)(TimeScale,_super),TimeScale.prototype.getLabel=function(tick){var useUTC=this.getSetting("useUTC");return(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .format */.WU)(tick.value,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .fullLeveledFormatter */.V8[(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .getDefaultFormatPrecisionOfInterval */.xC)((0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .getPrimaryTimeUnit */.Tj)(this._minLevelUnit))]||_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .fullLeveledFormatter */.V8.second,useUTC,this.getSetting("locale"))},TimeScale.prototype.getFormattedLabel=function(tick,idx,labelFormatter){var isUTC=this.getSetting("useUTC"),lang=this.getSetting("locale");return(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .leveledFormat */.k7)(tick,idx,labelFormatter,lang,isUTC)},
/**
   * @override
   */
TimeScale.prototype.getTicks=function(){var interval=this._interval,extent=this._extent,ticks=[];// If interval is 0, return [];
if(!interval)return ticks;ticks.push({value:extent[0],level:0});var useUTC=this.getSetting("useUTC"),innerTicks=getIntervalTicks(this._minLevelUnit,this._approxInterval,useUTC,extent);return ticks=ticks.concat(innerTicks),ticks.push({value:extent[1],level:0}),ticks},TimeScale.prototype.calcNiceExtent=function(opt){var extent=this._extent;// If extent start and end are same, expand them
// If there are no data and extent are [Infinity, -Infinity]
if(extent[0]===extent[1]&&(
// Expand extent
extent[0]-=_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .ONE_DAY */.s2,extent[1]+=_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .ONE_DAY */.s2),extent[1]===-1/0&&extent[0]===1/0){var d=new Date;extent[1]=+new Date(d.getFullYear(),d.getMonth(),d.getDate()),extent[0]=extent[1]-_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .ONE_DAY */.s2}this.calcNiceTicks(opt.splitNumber,opt.minInterval,opt.maxInterval)},TimeScale.prototype.calcNiceTicks=function(approxTickNum,minInterval,maxInterval){approxTickNum=approxTickNum||10;var extent=this._extent,span=extent[1]-extent[0];this._approxInterval=span/approxTickNum,null!=minInterval&&this._approxInterval<minInterval&&(this._approxInterval=minInterval),null!=maxInterval&&this._approxInterval>maxInterval&&(this._approxInterval=maxInterval);var scaleIntervalsLen=scaleIntervals.length,idx=Math.min(bisect(scaleIntervals,this._approxInterval,0,scaleIntervalsLen),scaleIntervalsLen-1);// Interval that can be used to calculate ticks
this._interval=scaleIntervals[idx][1],// Min level used when picking ticks from top down.
// We check one more level to avoid the ticks are to sparse in some case.
this._minLevelUnit=scaleIntervals[Math.max(idx-1,0)][0]},TimeScale.prototype.parse=function(val){
// val might be float.
return(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .isNumber */.hj)(val)?val:+_util_number_js__WEBPACK_IMPORTED_MODULE_3__/* .parseDate */.sG(val)},TimeScale.prototype.contain=function(val){return _helper_js__WEBPACK_IMPORTED_MODULE_4__/* .contain */.XS(this.parse(val),this._extent)},TimeScale.prototype.normalize=function(val){return _helper_js__WEBPACK_IMPORTED_MODULE_4__/* .normalize */.Fv(this.parse(val),this._extent)},TimeScale.prototype.scale=function(val){return _helper_js__WEBPACK_IMPORTED_MODULE_4__/* .scale */.bA(val,this._extent)},TimeScale.type="time",TimeScale}(_Interval_js__WEBPACK_IMPORTED_MODULE_5__/* ["default"] */.Z),scaleIntervals=[// Format                           interval
["second",_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .ONE_SECOND */.WT],["minute",_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .ONE_MINUTE */.yR],["hour",_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .ONE_HOUR */.dV],["quarter-day",6*_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .ONE_HOUR */.dV],["half-day",12*_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .ONE_HOUR */.dV],["day",1.2*_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .ONE_DAY */.s2],["half-week",3.5*_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .ONE_DAY */.s2],["week",7*_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .ONE_DAY */.s2],["month",31*_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .ONE_DAY */.s2],["quarter",95*_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .ONE_DAY */.s2],["half-year",_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .ONE_YEAR */.P5/2],["year",_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .ONE_YEAR */.P5]];
/* harmony import */function isUnitValueSame(unit,valueA,valueB,isUTC){var dateA=_util_number_js__WEBPACK_IMPORTED_MODULE_3__/* .parseDate */.sG(valueA),dateB=_util_number_js__WEBPACK_IMPORTED_MODULE_3__/* .parseDate */.sG(valueB),isSame=function(unit){return(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .getUnitValue */.q5)(dateA,unit,isUTC)===(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .getUnitValue */.q5)(dateB,unit,isUTC)},isSameYear=function(){return isSame("year")},isSameMonth=function(){return isSameYear()&&isSame("month")},isSameDay=function(){return isSameMonth()&&isSame("day")},isSameHour=function(){return isSameDay()&&isSame("hour")},isSameMinute=function(){return isSameHour()&&isSame("minute")},isSameSecond=function(){return isSameMinute()&&isSame("second")},isSameMilliSecond=function(){return isSameSecond()&&isSame("millisecond")};switch(unit){case"year":return isSameYear();case"month":return isSameMonth();case"day":return isSameDay();case"hour":return isSameHour();case"minute":return isSameMinute();case"second":return isSameSecond();case"millisecond":return isSameMilliSecond()}}// const primaryUnitGetters = {
//     year: fullYearGetterName(),
//     month: monthGetterName(),
//     day: dateGetterName(),
//     hour: hoursGetterName(),
//     minute: minutesGetterName(),
//     second: secondsGetterName(),
//     millisecond: millisecondsGetterName()
// };
// const primaryUnitUTCGetters = {
//     year: fullYearGetterName(true),
//     month: monthGetterName(true),
//     day: dateGetterName(true),
//     hour: hoursGetterName(true),
//     minute: minutesGetterName(true),
//     second: secondsGetterName(true),
//     millisecond: millisecondsGetterName(true)
// };
// function moveTick(date: Date, unitName: TimeUnit, step: number, isUTC: boolean) {
//     step = step || 1;
//     switch (getPrimaryTimeUnit(unitName)) {
//         case 'year':
//             date[fullYearSetterName(isUTC)](date[fullYearGetterName(isUTC)]() + step);
//             break;
//         case 'month':
//             date[monthSetterName(isUTC)](date[monthGetterName(isUTC)]() + step);
//             break;
//         case 'day':
//             date[dateSetterName(isUTC)](date[dateGetterName(isUTC)]() + step);
//             break;
//         case 'hour':
//             date[hoursSetterName(isUTC)](date[hoursGetterName(isUTC)]() + step);
//             break;
//         case 'minute':
//             date[minutesSetterName(isUTC)](date[minutesGetterName(isUTC)]() + step);
//             break;
//         case 'second':
//             date[secondsSetterName(isUTC)](date[secondsGetterName(isUTC)]() + step);
//             break;
//         case 'millisecond':
//             date[millisecondsSetterName(isUTC)](date[millisecondsGetterName(isUTC)]() + step);
//             break;
//     }
//     return date.getTime();
// }
// const DATE_INTERVALS = [[8, 7.5], [4, 3.5], [2, 1.5]];
// const MONTH_INTERVALS = [[6, 5.5], [3, 2.5], [2, 1.5]];
// const MINUTES_SECONDS_INTERVALS = [[30, 30], [20, 20], [15, 15], [10, 10], [5, 5], [2, 2]];
function getDateInterval(approxInterval,daysInMonth){return approxInterval/=_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .ONE_DAY */.s2,approxInterval>16?16:approxInterval>7.5?7:approxInterval>3.5?4:approxInterval>1.5?2:1}function getMonthInterval(approxInterval){var APPROX_ONE_MONTH=30*_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .ONE_DAY */.s2;return approxInterval/=APPROX_ONE_MONTH,approxInterval>6?6:approxInterval>3?3:approxInterval>2?2:1}function getHourInterval(approxInterval){return approxInterval/=_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .ONE_HOUR */.dV,approxInterval>12?12:approxInterval>6?6:approxInterval>3.5?4:approxInterval>2?2:1}function getMinutesAndSecondsInterval(approxInterval,isMinutes){return approxInterval/=isMinutes?_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .ONE_MINUTE */.yR:_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .ONE_SECOND */.WT,approxInterval>30?30:approxInterval>20?20:approxInterval>15?15:approxInterval>10?10:approxInterval>5?5:approxInterval>2?2:1}function getMillisecondsInterval(approxInterval){return _util_number_js__WEBPACK_IMPORTED_MODULE_3__/* .nice */.kx(approxInterval,!0)}function getFirstTimestampOfUnit(date,unitName,isUTC){var outDate=new Date(date);switch((0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .getPrimaryTimeUnit */.Tj)(unitName)){case"year":case"month":outDate[(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .monthSetterName */.vh)(isUTC)](0);case"day":outDate[(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .dateSetterName */.f5)(isUTC)](1);case"hour":outDate[(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .hoursSetterName */.En)(isUTC)](0);case"minute":outDate[(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .minutesSetterName */.eN)(isUTC)](0);case"second":outDate[(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .secondsSetterName */.rM)(isUTC)](0),outDate[(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .millisecondsSetterName */.cb)(isUTC)](0)}return outDate.getTime()}function getIntervalTicks(bottomUnitName,approxInterval,isUTC,extent){var safeLimit=1e4,unitNames=_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .timeUnits */.FW,iter=0;function addTicksInSpan(interval,minTimestamp,maxTimestamp,getMethodName,setMethodName,isDate,out){var date=new Date(minTimestamp),dateTime=minTimestamp,d=date[getMethodName]();// if (isDate) {
//     d -= 1; // Starts with 0;   PENDING
// }
while(dateTime<maxTimestamp&&dateTime<=extent[1])out.push({value:dateTime}),d+=interval,date[setMethodName](d),dateTime=date.getTime();// This extra tick is for calcuating ticks of next level. Will not been added to the final result
out.push({value:dateTime,notAdd:!0})}function addLevelTicks(unitName,lastLevelTicks,levelTicks){var newAddedTicks=[],isFirstLevel=!lastLevelTicks.length;if(!isUnitValueSame((0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .getPrimaryTimeUnit */.Tj)(unitName),extent[0],extent[1],isUTC)){isFirstLevel&&(lastLevelTicks=[{
// TODO Optimize. Not include so may ticks.
value:getFirstTimestampOfUnit(new Date(extent[0]),unitName,isUTC)},{value:extent[1]}]);for(var i=0;i<lastLevelTicks.length-1;i++){var startTick=lastLevelTicks[i].value,endTick=lastLevelTicks[i+1].value;if(startTick!==endTick){var interval=void 0,getterName=void 0,setterName=void 0,isDate=!1;switch(unitName){case"year":interval=Math.max(1,Math.round(approxInterval/_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .ONE_DAY */.s2/365)),getterName=(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .fullYearGetterName */.sx)(isUTC),setterName=(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .fullYearSetterName */.xL)(isUTC);break;case"half-year":case"quarter":case"month":interval=getMonthInterval(approxInterval),getterName=(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .monthGetterName */.CW)(isUTC),setterName=(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .monthSetterName */.vh)(isUTC);break;case"week":// PENDING If week is added. Ignore day.
case"half-week":case"day":interval=getDateInterval(approxInterval,31),// Use 32 days and let interval been 16
getterName=(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .dateGetterName */.xz)(isUTC),setterName=(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .dateSetterName */.f5)(isUTC),isDate=!0;break;case"half-day":case"quarter-day":case"hour":interval=getHourInterval(approxInterval),getterName=(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .hoursGetterName */.Wp)(isUTC),setterName=(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .hoursSetterName */.En)(isUTC);break;case"minute":interval=getMinutesAndSecondsInterval(approxInterval,!0),getterName=(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .minutesGetterName */.fn)(isUTC),setterName=(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .minutesSetterName */.eN)(isUTC);break;case"second":interval=getMinutesAndSecondsInterval(approxInterval,!1),getterName=(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .secondsGetterName */.MV)(isUTC),setterName=(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .secondsSetterName */.rM)(isUTC);break;case"millisecond":interval=getMillisecondsInterval(approxInterval),getterName=(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .millisecondsGetterName */.RZ)(isUTC),setterName=(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .millisecondsSetterName */.cb)(isUTC);break}addTicksInSpan(interval,startTick,endTick,getterName,setterName,isDate,newAddedTicks),"year"===unitName&&levelTicks.length>1&&0===i&&
// Add nearest years to the left extent.
levelTicks.unshift({value:levelTicks[0].value-interval})}}for(i=0;i<newAddedTicks.length;i++)levelTicks.push(newAddedTicks[i]);// newAddedTicks.length && console.log(unitName, newAddedTicks);
return newAddedTicks}}for(var levelsTicks=[],currentLevelTicks=[],tickCount=0,lastLevelTickCount=0,i=0;i<unitNames.length&&iter++<safeLimit;++i){var primaryTimeUnit=(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .getPrimaryTimeUnit */.Tj)(unitNames[i]);if((0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .isPrimaryTimeUnit */.$K)(unitNames[i])){addLevelTicks(unitNames[i],levelsTicks[levelsTicks.length-1]||[],currentLevelTicks);var nextPrimaryTimeUnit=unitNames[i+1]?(0,_util_time_js__WEBPACK_IMPORTED_MODULE_1__/* .getPrimaryTimeUnit */.Tj)(unitNames[i+1]):null;if(primaryTimeUnit!==nextPrimaryTimeUnit){if(currentLevelTicks.length){lastLevelTickCount=tickCount,// Remove the duplicate so the tick count can be precisely.
currentLevelTicks.sort((function(a,b){return a.value-b.value}));for(var levelTicksRemoveDuplicated=[],i_1=0;i_1<currentLevelTicks.length;++i_1){var tickValue=currentLevelTicks[i_1].value;0!==i_1&&currentLevelTicks[i_1-1].value===tickValue||(levelTicksRemoveDuplicated.push(currentLevelTicks[i_1]),tickValue>=extent[0]&&tickValue<=extent[1]&&tickCount++)}var targetTickNum=(extent[1]-extent[0])/approxInterval;// Added too much in this level and not too less in last level
if(tickCount>1.5*targetTickNum&&lastLevelTickCount>targetTickNum/1.5)break;// Only treat primary time unit as one level.
if(levelsTicks.push(levelTicksRemoveDuplicated),tickCount>targetTickNum||bottomUnitName===unitNames[i])break}// Reset if next unitName is primary
currentLevelTicks=[]}}}var levelsTicksInExtent=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .filter */.hX)((0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .map */.UI)(levelsTicks,(function(levelTicks){return(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .filter */.hX)(levelTicks,(function(tick){return tick.value>=extent[0]&&tick.value<=extent[1]&&!tick.notAdd}))})),(function(levelTicks){return levelTicks.length>0})),ticks=[],maxLevel=levelsTicksInExtent.length-1;for(i=0;i<levelsTicksInExtent.length;++i)for(var levelTicks=levelsTicksInExtent[i],k=0;k<levelTicks.length;++k)ticks.push({value:levelTicks[k].value,level:maxLevel-i});ticks.sort((function(a,b){return a.value-b.value}));// Remove duplicates
var result=[];for(i=0;i<ticks.length;++i)0!==i&&ticks[i].value===ticks[i-1].value||result.push(ticks[i]);return result}_Scale_js__WEBPACK_IMPORTED_MODULE_6__/* ["default"] */.Z.registerClass(TimeScale),
/* harmony default export */__webpack_exports__.Z=TimeScale},
/***/665021:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Fv:function(){/* binding */return normalize},
/* harmony export */Qf:function(){/* binding */return intervalScaleNiceTicks},
/* harmony export */XS:function(){/* binding */return contain},
/* harmony export */bA:function(){/* binding */return scale},
/* harmony export */lM:function(){/* binding */return isIntervalOrLogScale},
/* harmony export */lb:function(){/* binding */return getIntervalPrecision},
/* harmony export */r1:function(){/* binding */return increaseInterval}
/* harmony export */});
/* unused harmony exports isValueNice, fixExtent */
/* harmony import */var _util_number_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(785669);
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/function isIntervalOrLogScale(scale){return"interval"===scale.type||"log"===scale.type}
/**
 * @param extent Both extent[0] and extent[1] should be valid number.
 *               Should be extent[0] < extent[1].
 * @param splitNumber splitNumber should be >= 1.
 */function intervalScaleNiceTicks(extent,splitNumber,minInterval,maxInterval){var result={},span=extent[1]-extent[0],interval=result.interval=(0,_util_number_js__WEBPACK_IMPORTED_MODULE_0__/* .nice */.kx)(span/splitNumber,!0);null!=minInterval&&interval<minInterval&&(interval=result.interval=minInterval),null!=maxInterval&&interval>maxInterval&&(interval=result.interval=maxInterval);// Tow more digital for tick.
var precision=result.intervalPrecision=getIntervalPrecision(interval),niceTickExtent=result.niceTickExtent=[(0,_util_number_js__WEBPACK_IMPORTED_MODULE_0__/* .round */.NM)(Math.ceil(extent[0]/interval)*interval,precision),(0,_util_number_js__WEBPACK_IMPORTED_MODULE_0__/* .round */.NM)(Math.floor(extent[1]/interval)*interval,precision)];// Niced extent inside original extent
return fixExtent(niceTickExtent,extent),result}function increaseInterval(interval){var exp10=Math.pow(10,(0,_util_number_js__WEBPACK_IMPORTED_MODULE_0__/* .quantityExponent */.xW)(interval)),f=interval/exp10;// Increase interval
return f?2===f?f=3:3===f?f=5:
// f is 1 or 5
f*=2:f=1,(0,_util_number_js__WEBPACK_IMPORTED_MODULE_0__/* .round */.NM)(f*exp10)}
/**
 * @return interval precision
 */function getIntervalPrecision(interval){
// Tow more digital for tick.
return(0,_util_number_js__WEBPACK_IMPORTED_MODULE_0__/* .getPrecision */.p8)(interval)+2}function clamp(niceTickExtent,idx,extent){niceTickExtent[idx]=Math.max(Math.min(niceTickExtent[idx],extent[1]),extent[0])}// In some cases (e.g., splitNumber is 1), niceTickExtent may be out of extent.
function fixExtent(niceTickExtent,extent){!isFinite(niceTickExtent[0])&&(niceTickExtent[0]=extent[0]),!isFinite(niceTickExtent[1])&&(niceTickExtent[1]=extent[1]),clamp(niceTickExtent,0,extent),clamp(niceTickExtent,1,extent),niceTickExtent[0]>niceTickExtent[1]&&(niceTickExtent[0]=niceTickExtent[1])}function contain(val,extent){return val>=extent[0]&&val<=extent[1]}function normalize(val,extent){return extent[1]===extent[0]?.5:(val-extent[0])/(extent[1]-extent[0])}function scale(val,extent){return val*(extent[1]-extent[0])+extent[0]}
/***/},
/***/473450:
/***/function(__unused_webpack_module,__webpack_exports__){
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
var contrastColor="#B9B8CE",backgroundColor="#100C2A",axisCommon=function(){return{axisLine:{lineStyle:{color:contrastColor}},splitLine:{lineStyle:{color:"#484753"}},splitArea:{areaStyle:{color:["rgba(255,255,255,0.02)","rgba(255,255,255,0.05)"]}},minorSplitLine:{lineStyle:{color:"#20203B"}}}},colorPalette=["#4992ff","#7cffb2","#fddd60","#ff6e76","#58d9f9","#05c091","#ff8a45","#8d48e3","#dd79ff"],theme={darkMode:!0,color:colorPalette,backgroundColor:backgroundColor,axisPointer:{lineStyle:{color:"#817f91"},crossStyle:{color:"#817f91"},label:{
// TODO Contrast of label backgorundColor
color:"#fff"}},legend:{textStyle:{color:contrastColor}},textStyle:{color:contrastColor},title:{textStyle:{color:"#EEF1FA"},subtextStyle:{color:"#B9B8CE"}},toolbox:{iconStyle:{borderColor:contrastColor}},dataZoom:{borderColor:"#71708A",textStyle:{color:contrastColor},brushStyle:{color:"rgba(135,163,206,0.3)"},handleStyle:{color:"#353450",borderColor:"#C5CBE3"},moveHandleStyle:{color:"#B0B6C3",opacity:.3},fillerColor:"rgba(135,163,206,0.2)",emphasis:{handleStyle:{borderColor:"#91B7F2",color:"#4D587D"},moveHandleStyle:{color:"#636D9A",opacity:.7}},dataBackground:{lineStyle:{color:"#71708A",width:1},areaStyle:{color:"#71708A"}},selectedDataBackground:{lineStyle:{color:"#87A3CE"},areaStyle:{color:"#87A3CE"}}},visualMap:{textStyle:{color:contrastColor}},timeline:{lineStyle:{color:contrastColor},label:{color:contrastColor},controlStyle:{color:contrastColor,borderColor:contrastColor}},calendar:{itemStyle:{color:backgroundColor},dayLabel:{color:contrastColor},monthLabel:{color:contrastColor},yearLabel:{color:contrastColor}},timeAxis:axisCommon(),logAxis:axisCommon(),valueAxis:axisCommon(),categoryAxis:axisCommon(),line:{symbol:"circle"},graph:{color:colorPalette},gauge:{title:{color:contrastColor},axisLine:{lineStyle:{color:[[1,"rgba(207,212,219,0.2)"]]}},axisLabel:{color:contrastColor},detail:{color:"#EEF1FA"}},candlestick:{itemStyle:{color:"#f64e56",color0:"#54ea92",borderColor:"#f64e56",borderColor0:"#54ea92"}}};theme.categoryAxis.splitLine.show=!1,
/* harmony default export */__webpack_exports__.Z=theme},
/***/629594:
/***/function(__unused_webpack_module,__webpack_exports__){
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
var colorAll=["#37A2DA","#32C5E9","#67E0E3","#9FE6B8","#FFDB5C","#ff9f7f","#fb7293","#E062AE","#E690D1","#e7bcf3","#9d96f5","#8378EA","#96BFFF"];
/* harmony default export */__webpack_exports__.Z={color:colorAll,colorLayer:[["#37A2DA","#ffd85c","#fd7b5f"],["#37A2DA","#67E0E3","#FFDB5C","#ff9f7f","#E062AE","#9d96f5"],["#37A2DA","#32C5E9","#9FE6B8","#FFDB5C","#ff9f7f","#fb7293","#e7bcf3","#8378EA","#96BFFF"],colorAll]}},
/***/927443:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */v:function(){/* binding */return ECEventProcessor}
/* harmony export */});
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(933051),_clazz_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(834251),ECEventProcessor=
/** @class */
function(){function ECEventProcessor(){}return ECEventProcessor.prototype.normalizeQuery=function(query){var cptQuery={},dataQuery={},otherQuery={};// `query` is `mainType` or `mainType.subType` of component.
if(zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isString */.HD(query)){var condCptType=(0,_clazz_js__WEBPACK_IMPORTED_MODULE_1__/* .parseClassType */.u9)(query);// `.main` and `.sub` may be ''.
cptQuery.mainType=condCptType.main||null,cptQuery.subType=condCptType.sub||null}// `query` is an object, convert to {mainType, index, name, id}.
else{
// `xxxIndex`, `xxxName`, `xxxId`, `name`, `dataIndex`, `dataType` is reserved,
// can not be used in `compomentModel.filterForExposedEvent`.
var suffixes_1=["Index","Name","Id"],dataKeys_1={name:1,dataIndex:1,dataType:1};zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6(query,(function(val,key){for(var reserved=!1,i=0;i<suffixes_1.length;i++){var propSuffix=suffixes_1[i],suffixPos=key.lastIndexOf(propSuffix);if(suffixPos>0&&suffixPos===key.length-propSuffix.length){var mainType=key.slice(0,suffixPos);// Consider `dataIndex`.
"data"!==mainType&&(cptQuery.mainType=mainType,cptQuery[propSuffix.toLowerCase()]=val,reserved=!0)}}dataKeys_1.hasOwnProperty(key)&&(dataQuery[key]=val,reserved=!0),reserved||(otherQuery[key]=val)}))}return{cptQuery:cptQuery,dataQuery:dataQuery,otherQuery:otherQuery}},ECEventProcessor.prototype.filter=function(eventType,query){
// They should be assigned before each trigger call.
var eventInfo=this.eventInfo;if(!eventInfo)return!0;var targetEl=eventInfo.targetEl,packedEvent=eventInfo.packedEvent,model=eventInfo.model,view=eventInfo.view;// For event like 'globalout'.
if(!model||!view)return!0;var cptQuery=query.cptQuery,dataQuery=query.dataQuery;return check(cptQuery,model,"mainType")&&check(cptQuery,model,"subType")&&check(cptQuery,model,"index","componentIndex")&&check(cptQuery,model,"name")&&check(cptQuery,model,"id")&&check(dataQuery,packedEvent,"name")&&check(dataQuery,packedEvent,"dataIndex")&&check(dataQuery,packedEvent,"dataType")&&(!view.filterForExposedEvent||view.filterForExposedEvent(eventType,query.otherQuery,targetEl,packedEvent));function check(query,host,prop,propOnHost){return null==query[prop]||host[propOnHost||prop]===query[prop]}},ECEventProcessor.prototype.afterTrigger=function(){
// Make sure the eventInfo won't be used in next trigger.
this.eventInfo=null},ECEventProcessor}();
/* harmony import */},
/***/534584:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */o:function(){/* binding */return createWrap}
/* harmony export */});
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * Animate multiple elements with a single done-callback.
 *
 * @example
 *  animation
 *      .createWrap()
 *      .add(el1, {x: 10, y: 10})
 *      .add(el2, {shape: {width: 500}, style: {fill: 'red'}}, 400)
 *      .done(function () { // done })
 *      .start('cubicOut');
 */
var AnimationWrap=
/** @class */
function(){function AnimationWrap(){this._storage=[],this._elExistsMap={}}
/**
   * Caution: a el can only be added once, otherwise 'done'
   * might not be called. This method checks this (by el.id),
   * suppresses adding and returns false when existing el found.
   *
   * @return Whether adding succeeded.
   */return AnimationWrap.prototype.add=function(el,target,duration,delay,easing){return!this._elExistsMap[el.id]&&(this._elExistsMap[el.id]=!0,this._storage.push({el:el,target:target,duration:duration,delay:delay,easing:easing}),!0)},
/**
   * Only execute when animation done/aborted.
   */
AnimationWrap.prototype.finished=function(callback){return this._finishedCallback=callback,this},
/**
   * Will stop exist animation firstly.
   */
AnimationWrap.prototype.start=function(){for(var _this=this,count=this._storage.length,checkTerminate=function(){count--,count<=0&&(
// Guard.
_this._storage.length=0,_this._elExistsMap={},_this._finishedCallback&&_this._finishedCallback())},i=0,len=this._storage.length;i<len;i++){var item=this._storage[i];item.el.animateTo(item.target,{duration:item.duration,delay:item.delay,easing:item.easing,setToFinal:!0,done:checkTerminate,aborted:checkTerminate})}return this},AnimationWrap}();function createWrap(){return new AnimationWrap}
/***/},
/***/834251:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */PT:function(){/* binding */return isExtendedClass},
/* harmony export */Qj:function(){/* binding */return enableClassCheck},
/* harmony export */au:function(){/* binding */return enableClassManagement},
/* harmony export */dm:function(){/* binding */return enableClassExtend},
/* harmony export */pw:function(){/* binding */return mountExtend},
/* harmony export */u9:function(){/* binding */return parseClassType}
/* harmony export */});
/* harmony import */var tslib__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(570655),zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(933051),TYPE_DELIMITER=".",IS_CONTAINER="___EC__COMPONENT__CONTAINER___",IS_EXTENDED_CLASS="___EC__EXTENDED_CLASS___";
/* harmony import */
/**
 * Notice, parseClassType('') should returns {main: '', sub: ''}
 * @public
 */
function parseClassType(componentType){var ret={main:"",sub:""};if(componentType){var typeArr=componentType.split(TYPE_DELIMITER);ret.main=typeArr[0]||"",ret.sub=typeArr[1]||""}return ret}
/**
 * @public
 */function checkClassType(componentType){zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .assert */.hu(/^[a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)?$/.test(componentType),'componentType "'+componentType+'" illegal')}function isExtendedClass(clz){return!(!clz||!clz[IS_EXTENDED_CLASS])}
/**
 * Implements `ExtendableConstructor` for `rootClz`.
 *
 * @usage
 * ```ts
 * class Xxx {}
 * type XxxConstructor = typeof Xxx & ExtendableConstructor
 * enableClassExtend(Xxx as XxxConstructor);
 * ```
 */function enableClassExtend(rootClz,mandatoryMethods){rootClz.$constructor=rootClz,// FIXME: not necessary?
rootClz.extend=function(proto){var ExtendedClass,superClass=this;return isESClass(superClass)?ExtendedClass=
/** @class */
function(_super){function class_1(){return _super.apply(this,arguments)||this}return(0,tslib__WEBPACK_IMPORTED_MODULE_1__/* .__extends */.ZT)(class_1,_super),class_1}(superClass):(
// For backward compat, we both support ts class inheritance and this
// "extend" approach.
// The constructor should keep the same behavior as ts class inheritance:
// If this constructor/$constructor is not declared, auto invoke the super
// constructor.
// If this constructor/$constructor is declared, it is responsible for
// calling the super constructor.
ExtendedClass=function(){(proto.$constructor||superClass).apply(this,arguments)},zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .inherits */.XW(ExtendedClass,this)),zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .extend */.l7(ExtendedClass.prototype,proto),ExtendedClass[IS_EXTENDED_CLASS]=!0,ExtendedClass.extend=this.extend,ExtendedClass.superCall=superCall,ExtendedClass.superApply=superApply,ExtendedClass.superClass=superClass,ExtendedClass}}function isESClass(fn){return zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isFunction */.mf(fn)&&/^class\s/.test(Function.prototype.toString.call(fn))}
/**
 * A work around to both support ts extend and this extend mechanism.
 * on sub-class.
 * @usage
 * ```ts
 * class Component { ... }
 * classUtil.enableClassExtend(Component);
 * classUtil.enableClassManagement(Component, {registerWhenExtend: true});
 *
 * class Series extends Component { ... }
 * // Without calling `markExtend`, `registerWhenExtend` will not work.
 * Component.markExtend(Series);
 * ```
 */function mountExtend(SubClz,SupperClz){SubClz.extend=SupperClz.extend}// A random offset.
var classBase=Math.round(10*Math.random());
/**
 * Implements `CheckableConstructor` for `target`.
 * Can not use instanceof, consider different scope by
 * cross domain or es module import in ec extensions.
 * Mount a method "isInstance()" to Clz.
 *
 * @usage
 * ```ts
 * class Xxx {}
 * type XxxConstructor = typeof Xxx & CheckableConstructor;
 * enableClassCheck(Xxx as XxxConstructor)
 * ```
 */function enableClassCheck(target){var classAttr=["__\0is_clz",classBase++].join("_");target.prototype[classAttr]=!0,target.isInstance=function(obj){return!(!obj||!obj[classAttr])}}// superCall should have class info, which can not be fetched from 'this'.
// Consider this case:
// class A has method f,
// class B inherits class A, overrides method f, f call superApply('f'),
// class C inherits class B, does not override method f,
// then when method of class C is called, dead loop occurred.
function superCall(context,methodName){for(var args=[],_i=2;_i<arguments.length;_i++)args[_i-2]=arguments[_i];return this.superClass.prototype[methodName].apply(context,args)}function superApply(context,methodName,args){return this.superClass.prototype[methodName].apply(context,args)}
/**
 * Implements `ClassManager` for `target`
 *
 * @usage
 * ```ts
 * class Xxx {}
 * type XxxConstructor = typeof Xxx & ClassManager
 * enableClassManagement(Xxx as XxxConstructor);
 * ```
 */function enableClassManagement(target){
/**
   * Component model classes
   * key: componentType,
   * value:
   *     componentClass, when componentType is 'a'
   *     or Object.<subKey, componentClass>, when componentType is 'a.b'
   */
var storage={};function makeContainer(componentTypeInfo){var container=storage[componentTypeInfo.main];return container&&container[IS_CONTAINER]||(container=storage[componentTypeInfo.main]={},container[IS_CONTAINER]=!0),container}target.registerClass=function(clz){
// `type` should not be a "instance member".
// If using TS class, should better declared as `static type = 'series.pie'`.
// otherwise users have to mount `type` on prototype manually.
// For backward compat and enable instance visit type via `this.type`,
// we still support fetch `type` from prototype.
var componentFullType=clz.type||clz.prototype.type;if(componentFullType){checkClassType(componentFullType),// If only static type declared, we assign it to prototype mandatorily.
clz.prototype.type=componentFullType;var componentTypeInfo=parseClassType(componentFullType);if(componentTypeInfo.sub){if(componentTypeInfo.sub!==IS_CONTAINER){var container=makeContainer(componentTypeInfo);container[componentTypeInfo.sub]=clz}}else storage[componentTypeInfo.main]=clz}return clz},target.getClass=function(mainType,subType,throwWhenNotFound){var clz=storage[mainType];if(clz&&clz[IS_CONTAINER]&&(clz=subType?clz[subType]:null),throwWhenNotFound&&!clz)throw new Error(subType?"Component "+mainType+"."+(subType||"")+" is used but not imported.":mainType+".type should be specified.");return clz},target.getClassesByMainType=function(componentType){var componentTypeInfo=parseClassType(componentType),result=[],obj=storage[componentTypeInfo.main];return obj&&obj[IS_CONTAINER]?zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6(obj,(function(o,type){type!==IS_CONTAINER&&result.push(o)})):result.push(obj),result},target.hasClass=function(componentType){
// Just consider componentType.main.
var componentTypeInfo=parseClassType(componentType);return!!storage[componentTypeInfo.main]},
/**
   * @return Like ['aa', 'bb'], but can not be ['aa.xx']
   */
target.getAllClassMainTypes=function(){var types=[];return zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6(storage,(function(obj,type){types.push(type)})),types},
/**
   * If a main type is container and has sub types
   */
target.hasSubTypes=function(componentType){var componentTypeInfo=parseClassType(componentType),obj=storage[componentTypeInfo.main];return obj&&obj[IS_CONTAINER]}}// /**
//  * @param {string|Array.<string>} properties
//  */
// export function setReadOnly(obj, properties) {
// FIXME It seems broken in IE8 simulation of IE11
// if (!zrUtil.isArray(properties)) {
//     properties = properties != null ? [properties] : [];
// }
// zrUtil.each(properties, function (prop) {
//     let value = obj[prop];
//     Object.defineProperty
//         && Object.defineProperty(obj, prop, {
//             value: value, writable: false
//         });
//     zrUtil.isArray(obj[prop])
//         && Object.freeze
//         && Object.freeze(obj[prop]);
// });
// }
/***/},
/***/942151:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Kr:function(){/* binding */return getUID},
/* harmony export */ZL:function(){/* binding */return inheritDefaultOption},
/* harmony export */cj:function(){/* binding */return enableSubTypeDefaulter},
/* harmony export */jS:function(){/* binding */return enableTopologicalTravel}
/* harmony export */});
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(933051),_clazz_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(834251),base=Math.round(10*Math.random());
/* harmony import */
/**
 * @public
 * @param {string} type
 * @return {string}
 */
function getUID(type){
// Considering the case of crossing js context,
// use Math.random to make id as unique as possible.
return[type||"",base++].join("_")}
/**
 * Implements `SubTypeDefaulterManager` for `target`.
 */function enableSubTypeDefaulter(target){var subTypeDefaulters={};target.registerSubTypeDefaulter=function(componentType,defaulter){var componentTypeInfo=(0,_clazz_js__WEBPACK_IMPORTED_MODULE_0__/* .parseClassType */.u9)(componentType);subTypeDefaulters[componentTypeInfo.main]=defaulter},target.determineSubType=function(componentType,option){var type=option.type;if(!type){var componentTypeMain=(0,_clazz_js__WEBPACK_IMPORTED_MODULE_0__/* .parseClassType */.u9)(componentType).main;target.hasSubTypes(componentType)&&subTypeDefaulters[componentTypeMain]&&(type=subTypeDefaulters[componentTypeMain](option))}return type}}
/**
 * Implements `TopologicalTravelable<any>` for `entity`.
 *
 * Topological travel on Activity Network (Activity On Vertices).
 * Dependencies is defined in Model.prototype.dependencies, like ['xAxis', 'yAxis'].
 * If 'xAxis' or 'yAxis' is absent in componentTypeList, just ignore it in topology.
 * If there is circular dependencey, Error will be thrown.
 */function enableTopologicalTravel(entity,dependencyGetter){function makeDepndencyGraph(fullNameList){var graph={},noEntryList=[];return zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_1__/* .each */.S6(fullNameList,(function(name){var thisItem=createDependencyGraphItem(graph,name),originalDeps=thisItem.originalDeps=dependencyGetter(name),availableDeps=getAvailableDependencies(originalDeps,fullNameList);thisItem.entryCount=availableDeps.length,0===thisItem.entryCount&&noEntryList.push(name),zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_1__/* .each */.S6(availableDeps,(function(dependentName){zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_1__/* .indexOf */.cq(thisItem.predecessor,dependentName)<0&&thisItem.predecessor.push(dependentName);var thatItem=createDependencyGraphItem(graph,dependentName);zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_1__/* .indexOf */.cq(thatItem.successor,dependentName)<0&&thatItem.successor.push(name)}))})),{graph:graph,noEntryList:noEntryList}}function createDependencyGraphItem(graph,name){return graph[name]||(graph[name]={predecessor:[],successor:[]}),graph[name]}function getAvailableDependencies(originalDeps,fullNameList){var availableDeps=[];return zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_1__/* .each */.S6(originalDeps,(function(dep){zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_1__/* .indexOf */.cq(fullNameList,dep)>=0&&availableDeps.push(dep)})),availableDeps}
/**
   * @param targetNameList Target Component type list.
   *                       Can be ['aa', 'bb', 'aa.xx']
   * @param fullNameList By which we can build dependency graph.
   * @param callback Params: componentType, dependencies.
   * @param context Scope of callback.
   */
entity.topologicalTravel=function(targetNameList,fullNameList,callback,context){if(targetNameList.length){var result=makeDepndencyGraph(fullNameList),graph=result.graph,noEntryList=result.noEntryList,targetNameSet={};zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_1__/* .each */.S6(targetNameList,(function(name){targetNameSet[name]=!0}));while(noEntryList.length){var currComponentType=noEntryList.pop(),currVertex=graph[currComponentType],isInTargetNameSet=!!targetNameSet[currComponentType];isInTargetNameSet&&(callback.call(context,currComponentType,currVertex.originalDeps.slice()),delete targetNameSet[currComponentType]),zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_1__/* .each */.S6(currVertex.successor,isInTargetNameSet?removeEdgeAndAdd:removeEdge)}zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_1__/* .each */.S6(targetNameSet,(function(){var errMsg="";throw new Error(errMsg)}))}function removeEdge(succComponentType){graph[succComponentType].entryCount--,0===graph[succComponentType].entryCount&&noEntryList.push(succComponentType)}// Consider this case: legend depends on series, and we call
// chart.setOption({series: [...]}), where only series is in option.
// If we do not have 'removeEdgeAndAdd', legendModel.mergeOption will
// not be called, but only sereis.mergeOption is called. Thus legend
// have no chance to update its local record about series (like which
// name of series is available in legend).
function removeEdgeAndAdd(succComponentType){targetNameSet[succComponentType]=!0,removeEdge(succComponentType)}}}function inheritDefaultOption(superOption,subOption){
// See also `model/Component.ts#getDefaultOption`
return zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_1__/* .merge */.TS(zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_1__/* .merge */.TS({},superOption,!0),subOption,!0)}
/***/},
/***/974901:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */a:function(){/* binding */return parseConditionalExpression}
/* harmony export */});
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(933051),_log_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(470175),_data_helper_dataValueHelper_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(198407),RELATIONAL_EXPRESSION_OP_ALIAS_MAP={value:"eq",
// PENDING: not good for literal semantic?
"<":"lt","<=":"lte",">":"gt",">=":"gte","=":"eq","!=":"ne","<>":"ne"},RegExpEvaluator=
/** @class */
function(){function RegExpEvaluator(rVal){
// Support condVal: RegExp | string
var condValue=this._condVal=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isString */.HD)(rVal)?new RegExp(rVal):(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isRegExp */.Kj)(rVal)?rVal:null;if(null==condValue){var errMsg="";0,(0,_log_js__WEBPACK_IMPORTED_MODULE_1__/* .throwError */._y)(errMsg)}}return RegExpEvaluator.prototype.evaluate=function(lVal){var type=typeof lVal;return(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isString */.HD)(type)?this._condVal.test(lVal):!!(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isNumber */.hj)(type)&&this._condVal.test(lVal+"")},RegExpEvaluator}(),ConstConditionInternal=
/** @class */
function(){function ConstConditionInternal(){}return ConstConditionInternal.prototype.evaluate=function(){return this.value},ConstConditionInternal}(),AndConditionInternal=
/** @class */
function(){function AndConditionInternal(){}return AndConditionInternal.prototype.evaluate=function(){for(var children=this.children,i=0;i<children.length;i++)if(!children[i].evaluate())return!1;return!0},AndConditionInternal}(),OrConditionInternal=
/** @class */
function(){function OrConditionInternal(){}return OrConditionInternal.prototype.evaluate=function(){for(var children=this.children,i=0;i<children.length;i++)if(children[i].evaluate())return!0;return!1},OrConditionInternal}(),NotConditionInternal=
/** @class */
function(){function NotConditionInternal(){}return NotConditionInternal.prototype.evaluate=function(){return!this.child.evaluate()},NotConditionInternal}(),RelationalConditionInternal=
/** @class */
function(){function RelationalConditionInternal(){}return RelationalConditionInternal.prototype.evaluate=function(){// Relational cond follow "and" logic internally.
for(var needParse=!!this.valueParser,getValue=this.getValue,tarValRaw=getValue(this.valueGetterParam),tarValParsed=needParse?this.valueParser(tarValRaw):null,i=0// Call getValue with no `this`.
;i<this.subCondList.length;i++)if(!this.subCondList[i].evaluate(needParse?tarValParsed:tarValRaw))return!1;return!0},RelationalConditionInternal}();
/* harmony import */function parseOption(exprOption,getters){if(!0===exprOption||!1===exprOption){var cond=new ConstConditionInternal;return cond.value=exprOption,cond}var errMsg="";return isObjectNotArray(exprOption)||(0,_log_js__WEBPACK_IMPORTED_MODULE_1__/* .throwError */._y)(errMsg),exprOption.and?parseAndOrOption("and",exprOption,getters):exprOption.or?parseAndOrOption("or",exprOption,getters):exprOption.not?parseNotOption(exprOption,getters):parseRelationalOption(exprOption,getters)}function parseAndOrOption(op,exprOption,getters){var subOptionArr=exprOption[op],errMsg="";(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isArray */.kJ)(subOptionArr)||(0,_log_js__WEBPACK_IMPORTED_MODULE_1__/* .throwError */._y)(errMsg),subOptionArr.length||(0,_log_js__WEBPACK_IMPORTED_MODULE_1__/* .throwError */._y)(errMsg);var cond="and"===op?new AndConditionInternal:new OrConditionInternal;return cond.children=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .map */.UI)(subOptionArr,(function(subOption){return parseOption(subOption,getters)})),cond.children.length||(0,_log_js__WEBPACK_IMPORTED_MODULE_1__/* .throwError */._y)(errMsg),cond}function parseNotOption(exprOption,getters){var subOption=exprOption.not,errMsg="";isObjectNotArray(subOption)||(0,_log_js__WEBPACK_IMPORTED_MODULE_1__/* .throwError */._y)(errMsg);var cond=new NotConditionInternal;return cond.child=parseOption(subOption,getters),cond.child||(0,_log_js__WEBPACK_IMPORTED_MODULE_1__/* .throwError */._y)(errMsg),cond}function parseRelationalOption(exprOption,getters){for(var errMsg="",valueGetterParam=getters.prepareGetValue(exprOption),subCondList=[],exprKeys=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .keys */.XP)(exprOption),parserName=exprOption.parser,valueParser=parserName?(0,_data_helper_dataValueHelper_js__WEBPACK_IMPORTED_MODULE_2__/* .getRawValueParser */.o2)(parserName):null,i=0;i<exprKeys.length;i++){var keyRaw=exprKeys[i];if("parser"!==keyRaw&&!getters.valueGetterAttrMap.get(keyRaw)){var op=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(RELATIONAL_EXPRESSION_OP_ALIAS_MAP,keyRaw)?RELATIONAL_EXPRESSION_OP_ALIAS_MAP[keyRaw]:keyRaw,condValueRaw=exprOption[keyRaw],condValueParsed=valueParser?valueParser(condValueRaw):condValueRaw,evaluator=(0,_data_helper_dataValueHelper_js__WEBPACK_IMPORTED_MODULE_2__/* .createFilterComparator */.tR)(op,condValueParsed)||"reg"===op&&new RegExpEvaluator(condValueParsed);evaluator||(0,_log_js__WEBPACK_IMPORTED_MODULE_1__/* .throwError */._y)(errMsg),subCondList.push(evaluator)}}subCondList.length||// No relational operator always disabled in case of dangers result.
(0,_log_js__WEBPACK_IMPORTED_MODULE_1__/* .throwError */._y)(errMsg);var cond=new RelationalConditionInternal;return cond.valueGetterParam=valueGetterParam,cond.valueParser=valueParser,cond.getValue=getters.getValue,cond.subCondList=subCondList,cond}function isObjectNotArray(val){return(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isObject */.Kn)(val)&&!(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isArrayLike */.zG)(val)}var ConditionalExpressionParsed=
/** @class */
function(){function ConditionalExpressionParsed(exprOption,getters){this._cond=parseOption(exprOption,getters)}return ConditionalExpressionParsed.prototype.evaluate=function(){return this._cond.evaluate()},ConditionalExpressionParsed}();function parseConditionalExpression(exprOption,getters){return new ConditionalExpressionParsed(exprOption,getters)}
/***/},
/***/559361:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */I:function(){/* binding */return createOrUpdatePatternFromDecal}
/* harmony export */});
/* harmony import */var zrender_lib_core_WeakMap_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(510675),zrender_lib_core_LRU_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(392528),zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(933051),_number_js__WEBPACK_IMPORTED_MODULE_4__=__webpack_require__(785669),_symbol_js__WEBPACK_IMPORTED_MODULE_5__=__webpack_require__(241525),zrender_lib_canvas_graphic_js__WEBPACK_IMPORTED_MODULE_6__=__webpack_require__(497772),zrender_lib_core_platform_js__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(623132),decalMap=new zrender_lib_core_WeakMap_js__WEBPACK_IMPORTED_MODULE_0__/* ["default"] */.Z,decalCache=new zrender_lib_core_LRU_js__WEBPACK_IMPORTED_MODULE_1__/* ["default"] */.ZP(100),decalKeys=["symbol","symbolSize","symbolKeepAspect","color","backgroundColor","dashArrayX","dashArrayY","maxTileWidth","maxTileHeight"];
/* harmony import */
/**
 * Create or update pattern image from decal options
 *
 * @param {InnerDecalObject | 'none'} decalObject decal options, 'none' if no decal
 * @return {Pattern} pattern with generated image, null if no decal
 */
function createOrUpdatePatternFromDecal(decalObject,api){if("none"===decalObject)return null;var dpr=api.getDevicePixelRatio(),zr=api.getZr(),isSVG="svg"===zr.painter.type;decalObject.dirty&&decalMap["delete"](decalObject);var oldPattern=decalMap.get(decalObject);if(oldPattern)return oldPattern;var decalOpt=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .defaults */.ce)(decalObject,{symbol:"rect",symbolSize:1,symbolKeepAspect:!0,color:"rgba(0, 0, 0, 0.2)",backgroundColor:null,dashArrayX:5,dashArrayY:5,rotation:0,maxTileWidth:512,maxTileHeight:512});"none"===decalOpt.backgroundColor&&(decalOpt.backgroundColor=null);var pattern={repeat:"repeat"};return setPatternnSource(pattern),pattern.rotation=decalOpt.rotation,pattern.scaleX=pattern.scaleY=isSVG?1:1/dpr,decalMap.set(decalObject,pattern),decalObject.dirty=!1,pattern;function setPatternnSource(pattern){for(var cacheKey,keys=[dpr],isValidKey=!0,i=0;i<decalKeys.length;++i){var value=decalOpt[decalKeys[i]];if(null!=value&&!(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .isArray */.kJ)(value)&&!(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .isString */.HD)(value)&&!(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .isNumber */.hj)(value)&&"boolean"!==typeof value){isValidKey=!1;break}keys.push(value)}if(isValidKey){cacheKey=keys.join(",")+(isSVG?"-svg":"");var cache=decalCache.get(cacheKey);cache&&(isSVG?pattern.svgElement=cache:pattern.image=cache)}var ctx,dashArrayX=normalizeDashArrayX(decalOpt.dashArrayX),dashArrayY=normalizeDashArrayY(decalOpt.dashArrayY),symbolArray=normalizeSymbolArray(decalOpt.symbol),lineBlockLengthsX=getLineBlockLengthX(dashArrayX),lineBlockLengthY=getLineBlockLengthY(dashArrayY),canvas=!isSVG&&zrender_lib_core_platform_js__WEBPACK_IMPORTED_MODULE_3__/* .platformApi */.qW.createCanvas(),svgRoot=isSVG&&{tag:"g",attrs:{},key:"dcl",children:[]},pSize=getPatternSize();
/**
     * Get minimum length that can make a repeatable pattern.
     *
     * @return {Object} pattern width and height
     */
function getPatternSize(){for(
/**
       * For example, if dash is [[3, 2], [2, 1]] for X, it looks like
       * |---  ---  ---  ---  --- ...
       * |-- -- -- -- -- -- -- -- ...
       * |---  ---  ---  ---  --- ...
       * |-- -- -- -- -- -- -- -- ...
       * So the minimum length of X is 15,
       * which is the least common multiple of `3 + 2` and `2 + 1`
       * |---  ---  ---  |---  --- ...
       * |-- -- -- -- -- |-- -- -- ...
       */
var width=1,i=0,xlen=lineBlockLengthsX.length;i<xlen;++i)width=(0,_number_js__WEBPACK_IMPORTED_MODULE_4__/* .getLeastCommonMultiple */.nl)(width,lineBlockLengthsX[i]);var symbolRepeats=1;for(i=0,xlen=symbolArray.length;i<xlen;++i)symbolRepeats=(0,_number_js__WEBPACK_IMPORTED_MODULE_4__/* .getLeastCommonMultiple */.nl)(symbolRepeats,symbolArray[i].length);width*=symbolRepeats;var height=lineBlockLengthY*lineBlockLengthsX.length*symbolArray.length;return{width:Math.max(1,Math.min(width,decalOpt.maxTileWidth)),height:Math.max(1,Math.min(height,decalOpt.maxTileHeight))}}function brushDecal(){ctx&&(ctx.clearRect(0,0,canvas.width,canvas.height),decalOpt.backgroundColor&&(ctx.fillStyle=decalOpt.backgroundColor,ctx.fillRect(0,0,canvas.width,canvas.height)));for(var ySum=0,i=0;i<dashArrayY.length;++i)ySum+=dashArrayY[i];if(!(ySum<=0)){var y=-lineBlockLengthY,yId=0,yIdTotal=0,xId0=0;while(y<pSize.height){if(yId%2===0){var symbolYId=yIdTotal/2%symbolArray.length,x=0,xId1=0,xId1Total=0;while(x<2*pSize.width){var xSum=0;for(i=0;i<dashArrayX[xId0].length;++i)xSum+=dashArrayX[xId0][i];if(xSum<=0)
// Skip empty line
break;// E.g., [15, 5, 20, 5] draws only for 15 and 20
if(xId1%2===0){var size=.5*(1-decalOpt.symbolSize),left=x+dashArrayX[xId0][xId1]*size,top_1=y+dashArrayY[yId]*size,width=dashArrayX[xId0][xId1]*decalOpt.symbolSize,height=dashArrayY[yId]*decalOpt.symbolSize,symbolXId=xId1Total/2%symbolArray[symbolYId].length;brushSymbol(left,top_1,width,height,symbolArray[symbolYId][symbolXId])}x+=dashArrayX[xId0][xId1],++xId1Total,++xId1,xId1===dashArrayX[xId0].length&&(xId1=0)}++xId0,xId0===dashArrayX.length&&(xId0=0)}y+=dashArrayY[yId],++yIdTotal,++yId,yId===dashArrayY.length&&(yId=0)}}function brushSymbol(x,y,width,height,symbolType){var scale=isSVG?1:dpr,symbol=(0,_symbol_js__WEBPACK_IMPORTED_MODULE_5__/* .createSymbol */.th)(symbolType,x*scale,y*scale,width*scale,height*scale,decalOpt.color,decalOpt.symbolKeepAspect);if(isSVG){var symbolVNode=zr.painter.renderOneToVNode(symbol);symbolVNode&&svgRoot.children.push(symbolVNode)}else
// Paint to canvas for all other renderers.
(0,zrender_lib_canvas_graphic_js__WEBPACK_IMPORTED_MODULE_6__/* .brushSingle */.RV)(ctx,symbol)}}canvas&&(canvas.width=pSize.width*dpr,canvas.height=pSize.height*dpr,ctx=canvas.getContext("2d")),brushDecal(),isValidKey&&decalCache.put(cacheKey,canvas||svgRoot),pattern.image=canvas,pattern.svgElement=svgRoot,pattern.svgWidth=pSize.width,pattern.svgHeight=pSize.height}}
/**
 * Convert symbol array into normalized array
 *
 * @param {string | (string | string[])[]} symbol symbol input
 * @return {string[][]} normolized symbol array
 */function normalizeSymbolArray(symbol){if(!symbol||0===symbol.length)return[["rect"]];if((0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .isString */.HD)(symbol))return[[symbol]];for(var isAllString=!0,i=0;i<symbol.length;++i)if(!(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .isString */.HD)(symbol[i])){isAllString=!1;break}if(isAllString)return normalizeSymbolArray([symbol]);var result=[];for(i=0;i<symbol.length;++i)(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .isString */.HD)(symbol[i])?result.push([symbol[i]]):result.push(symbol[i]);return result}
/**
 * Convert dash input into dashArray
 *
 * @param {DecalDashArrayX} dash dash input
 * @return {number[][]} normolized dash array
 */function normalizeDashArrayX(dash){if(!dash||0===dash.length)return[[0,0]];if((0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .isNumber */.hj)(dash)){var dashValue=Math.ceil(dash);return[[dashValue,dashValue]]}
/**
   * [20, 5] should be normalized into [[20, 5]],
   * while [20, [5, 10]] should be normalized into [[20, 20], [5, 10]]
   */for(var isAllNumber=!0,i=0;i<dash.length;++i)if(!(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .isNumber */.hj)(dash[i])){isAllNumber=!1;break}if(isAllNumber)return normalizeDashArrayX([dash]);var result=[];for(i=0;i<dash.length;++i)if((0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .isNumber */.hj)(dash[i])){dashValue=Math.ceil(dash[i]);result.push([dashValue,dashValue])}else{dashValue=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .map */.UI)(dash[i],(function(n){return Math.ceil(n)}));dashValue.length%2===1?
// [4, 2, 1] means |----  -    -- |----  -    -- |
// so normalize it to be [4, 2, 1, 4, 2, 1]
result.push(dashValue.concat(dashValue)):result.push(dashValue)}return result}
/**
 * Convert dash input into dashArray
 *
 * @param {DecalDashArrayY} dash dash input
 * @return {number[]} normolized dash array
 */function normalizeDashArrayY(dash){if(!dash||"object"===typeof dash&&0===dash.length)return[0,0];if((0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .isNumber */.hj)(dash)){var dashValue_1=Math.ceil(dash);return[dashValue_1,dashValue_1]}var dashValue=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .map */.UI)(dash,(function(n){return Math.ceil(n)}));return dash.length%2?dashValue.concat(dashValue):dashValue}
/**
 * Get block length of each line. A block is the length of dash line and space.
 * For example, a line with [4, 1] has a dash line of 4 and a space of 1 after
 * that, so the block length of this line is 5.
 *
 * @param {number[][]} dash dash array of X or Y
 * @return {number[]} block length of each line
 */function getLineBlockLengthX(dash){return(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .map */.UI)(dash,(function(line){return getLineBlockLengthY(line)}))}function getLineBlockLengthY(dash){for(var blockLength=0,i=0;i<dash.length;++i)blockLength+=dash[i];return dash.length%2===1?2*blockLength:blockLength}
/***/},
/***/218310:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
function findEventDispatcher(target,det,returnFirstMatch){var found;while(target){if(det(target)&&(found=target,returnFirstMatch))break;target=target.__hostTarget||target.parent}return found}
/***/
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */o:function(){/* binding */return findEventDispatcher}
/* harmony export */})},
/***/578988:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */A0:function(){/* binding */return getTooltipMarker},
/* harmony export */Lz:function(){/* binding */return convertToColorString},
/* harmony export */MI:function(){/* binding */return windowOpen},
/* harmony export */MY:function(){/* binding */return normalizeCssArray},
/* harmony export */OD:function(){/* binding */return addCommas},
/* harmony export */kF:function(){/* binding */return formatTpl},
/* harmony export */uX:function(){/* binding */return makeValueReadable},
/* harmony export */zW:function(){/* binding */return toCamelCase}
/* harmony export */});
/* unused harmony exports formatTplSimple, formatTime, capitalFirst */
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(933051),zrender_lib_core_dom_js__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(284602),_number_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(785669),_time_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(515015);
/* harmony import */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * Add a comma each three digit.
 */
function addCommas(x){if(!(0,_number_js__WEBPACK_IMPORTED_MODULE_0__/* .isNumeric */.kE)(x))return zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_1__/* .isString */.HD(x)?x:"-";var parts=(x+"").split(".");return parts[0].replace(/(\d{1,3})(?=(?:\d{3})+(?!\d))/g,"$1,")+(parts.length>1?"."+parts[1]:"")}function toCamelCase(str,upperCaseFirst){return str=(str||"").toLowerCase().replace(/-(.)/g,(function(match,group1){return group1.toUpperCase()})),upperCaseFirst&&str&&(str=str.charAt(0).toUpperCase()+str.slice(1)),str}var normalizeCssArray=zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_1__/* .normalizeCssArray */.MY;
/**
 * Make value user readable for tooltip and label.
 * "User readable":
 *     Try to not print programmer-specific text like NaN, Infinity, null, undefined.
 *     Avoid to display an empty string, which users can not recognize there is
 *     a value and it might look like a bug.
 */function makeValueReadable(value,valueType,useUTC){var USER_READABLE_DEFUALT_TIME_PATTERN="{yyyy}-{MM}-{dd} {HH}:{mm}:{ss}";function stringToUserReadable(str){return str&&zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_1__/* .trim */.fy(str)?str:"-"}function isNumberUserReadable(num){return!(null==num||isNaN(num)||!isFinite(num))}var isTypeTime="time"===valueType,isValueDate=value instanceof Date;if(isTypeTime||isValueDate){var date=isTypeTime?(0,_number_js__WEBPACK_IMPORTED_MODULE_0__/* .parseDate */.sG)(value):value;if(!isNaN(+date))return(0,_time_js__WEBPACK_IMPORTED_MODULE_2__/* .format */.WU)(date,USER_READABLE_DEFUALT_TIME_PATTERN,useUTC);// In other cases, continue to try to display the value in the following code.
if(isValueDate)return"-"}if("ordinal"===valueType)return zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_1__/* .isStringSafe */.cd(value)?stringToUserReadable(value):zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_1__/* .isNumber */.hj(value)&&isNumberUserReadable(value)?value+"":"-";// By default.
var numericResult=(0,_number_js__WEBPACK_IMPORTED_MODULE_0__/* .numericToNumber */.FK)(value);return isNumberUserReadable(numericResult)?addCommas(numericResult):zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_1__/* .isStringSafe */.cd(value)?stringToUserReadable(value):"boolean"===typeof value?value+"":"-"}var TPL_VAR_ALIAS=["a","b","c","d","e","f","g"],wrapVar=function(varName,seriesIdx){return"{"+varName+(null==seriesIdx?"":seriesIdx)+"}"};
/**
 * Template formatter
 * @param {Array.<Object>|Object} paramsList
 */
function formatTpl(tpl,paramsList,encode){zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_1__/* .isArray */.kJ(paramsList)||(paramsList=[paramsList]);var seriesLen=paramsList.length;if(!seriesLen)return"";for(var $vars=paramsList[0].$vars||[],i=0;i<$vars.length;i++){var alias=TPL_VAR_ALIAS[i];tpl=tpl.replace(wrapVar(alias),wrapVar(alias,0))}for(var seriesIdx=0;seriesIdx<seriesLen;seriesIdx++)for(var k=0;k<$vars.length;k++){var val=paramsList[seriesIdx][$vars[k]];tpl=tpl.replace(wrapVar(TPL_VAR_ALIAS[k],seriesIdx),encode?(0,zrender_lib_core_dom_js__WEBPACK_IMPORTED_MODULE_3__/* .encodeHTML */.F1)(val):val)}return tpl}
/**
 * simple Template formatter
 */function getTooltipMarker(inOpt,extraCssText){var opt=zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_1__/* .isString */.HD(inOpt)?{color:inOpt,extraCssText:extraCssText}:inOpt||{},color=opt.color,type=opt.type;extraCssText=opt.extraCssText;var renderMode=opt.renderMode||"html";if(!color)return"";if("html"===renderMode)return"subItem"===type?'<span style="display:inline-block;vertical-align:middle;margin-right:8px;margin-left:3px;border-radius:4px;width:4px;height:4px;background-color:'+(0,zrender_lib_core_dom_js__WEBPACK_IMPORTED_MODULE_3__/* .encodeHTML */.F1)(color)+";"+(extraCssText||"")+'"></span>':'<span style="display:inline-block;margin-right:4px;border-radius:10px;width:10px;height:10px;background-color:'+(0,zrender_lib_core_dom_js__WEBPACK_IMPORTED_MODULE_3__/* .encodeHTML */.F1)(color)+";"+(extraCssText||"")+'"></span>';
// Should better not to auto generate style name by auto-increment number here.
// Because this util is usually called in tooltip formatter, which is probably
// called repeatedly when mouse move and the auto-increment number increases fast.
// Users can make their own style name by theirselves, make it unique and readable.
var markerId=opt.markerId||"markerX";return{renderMode:renderMode,content:"{"+markerId+"|}  ",style:"subItem"===type?{width:4,height:4,borderRadius:2,backgroundColor:color}:{width:10,height:10,borderRadius:5,backgroundColor:color}}}
/**
 * @deprecated Use `time/format` instead.
 * ISO Date format
 * @param {string} tpl
 * @param {number} value
 * @param {boolean} [isUTC=false] Default in local time.
 *           see `module:echarts/scale/Time`
 *           and `module:echarts/util/number#parseDate`.
 * @inner
 */
/**
 * @return Never be null/undefined.
 */
function convertToColorString(color,defaultColor){return defaultColor=defaultColor||"transparent",zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_1__/* .isString */.HD(color)?color:zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_1__/* .isObject */.Kn(color)&&color.colorStops&&(color.colorStops[0]||{}).color||defaultColor}
/**
 * open new tab
 * @param link url
 * @param target blank or self
 */function windowOpen(link,target){
/* global window */
if("_blank"===target||"blank"===target){var blank=window.open();blank.opener=null,blank.location.href=link}else window.open(link,target)}
/***/},
/***/851177:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){__webpack_require__.r(__webpack_exports__),
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Arc:function(){/* reexport safe */return zrender_lib_graphic_shape_Arc_js__WEBPACK_IMPORTED_MODULE_19__.Z},
/* harmony export */BezierCurve:function(){/* reexport safe */return zrender_lib_graphic_shape_BezierCurve_js__WEBPACK_IMPORTED_MODULE_18__.Z},
/* harmony export */BoundingRect:function(){/* reexport safe */return zrender_lib_core_BoundingRect_js__WEBPACK_IMPORTED_MODULE_26__.Z},
/* harmony export */Circle:function(){/* reexport safe */return zrender_lib_graphic_shape_Circle_js__WEBPACK_IMPORTED_MODULE_10__.Z},
/* harmony export */CompoundPath:function(){/* reexport safe */return zrender_lib_graphic_CompoundPath_js__WEBPACK_IMPORTED_MODULE_23__.Z},
/* harmony export */Ellipse:function(){/* reexport safe */return zrender_lib_graphic_shape_Ellipse_js__WEBPACK_IMPORTED_MODULE_11__.Z},
/* harmony export */Group:function(){/* reexport safe */return zrender_lib_graphic_Group_js__WEBPACK_IMPORTED_MODULE_20__.Z},
/* harmony export */Image:function(){/* reexport safe */return zrender_lib_graphic_Image_js__WEBPACK_IMPORTED_MODULE_3__.ZP},
/* harmony export */IncrementalDisplayable:function(){/* reexport safe */return zrender_lib_graphic_IncrementalDisplayable_js__WEBPACK_IMPORTED_MODULE_22__.Z},
/* harmony export */Line:function(){/* reexport safe */return zrender_lib_graphic_shape_Line_js__WEBPACK_IMPORTED_MODULE_17__.Z},
/* harmony export */LinearGradient:function(){/* reexport safe */return zrender_lib_graphic_LinearGradient_js__WEBPACK_IMPORTED_MODULE_24__.Z},
/* harmony export */OrientedBoundingRect:function(){/* reexport safe */return zrender_lib_core_OrientedBoundingRect_js__WEBPACK_IMPORTED_MODULE_27__.Z},
/* harmony export */Path:function(){/* reexport safe */return zrender_lib_graphic_Path_js__WEBPACK_IMPORTED_MODULE_1__.ZP},
/* harmony export */Point:function(){/* reexport safe */return zrender_lib_core_Point_js__WEBPACK_IMPORTED_MODULE_28__.Z},
/* harmony export */Polygon:function(){/* reexport safe */return zrender_lib_graphic_shape_Polygon_js__WEBPACK_IMPORTED_MODULE_14__.Z},
/* harmony export */Polyline:function(){/* reexport safe */return zrender_lib_graphic_shape_Polyline_js__WEBPACK_IMPORTED_MODULE_15__.Z},
/* harmony export */RadialGradient:function(){/* reexport safe */return zrender_lib_graphic_RadialGradient_js__WEBPACK_IMPORTED_MODULE_25__.Z},
/* harmony export */Rect:function(){/* reexport safe */return zrender_lib_graphic_shape_Rect_js__WEBPACK_IMPORTED_MODULE_16__.Z},
/* harmony export */Ring:function(){/* reexport safe */return zrender_lib_graphic_shape_Ring_js__WEBPACK_IMPORTED_MODULE_13__.Z},
/* harmony export */Sector:function(){/* reexport safe */return zrender_lib_graphic_shape_Sector_js__WEBPACK_IMPORTED_MODULE_12__.C},
/* harmony export */Text:function(){/* reexport safe */return zrender_lib_graphic_Text_js__WEBPACK_IMPORTED_MODULE_21__.ZP},
/* harmony export */applyTransform:function(){/* binding */return applyTransform},
/* harmony export */clipPointsByRect:function(){/* binding */return clipPointsByRect},
/* harmony export */clipRectByRect:function(){/* binding */return clipRectByRect},
/* harmony export */createIcon:function(){/* binding */return createIcon},
/* harmony export */extendPath:function(){/* binding */return extendPath},
/* harmony export */extendShape:function(){/* binding */return extendShape},
/* harmony export */getShapeClass:function(){/* binding */return getShapeClass},
/* harmony export */getTransform:function(){/* binding */return getTransform},
/* harmony export */groupTransition:function(){/* binding */return groupTransition},
/* harmony export */initProps:function(){/* reexport safe */return _animation_basicTransition_js__WEBPACK_IMPORTED_MODULE_0__.KZ},
/* harmony export */isElementRemoved:function(){/* reexport safe */return _animation_basicTransition_js__WEBPACK_IMPORTED_MODULE_0__.eq},
/* harmony export */lineLineIntersect:function(){/* binding */return lineLineIntersect},
/* harmony export */linePolygonIntersect:function(){/* binding */return linePolygonIntersect},
/* harmony export */makeImage:function(){/* binding */return makeImage},
/* harmony export */makePath:function(){/* binding */return makePath},
/* harmony export */mergePath:function(){/* binding */return mergePath},
/* harmony export */registerShape:function(){/* binding */return registerShape},
/* harmony export */removeElement:function(){/* reexport safe */return _animation_basicTransition_js__WEBPACK_IMPORTED_MODULE_0__.bX},
/* harmony export */removeElementWithFadeOut:function(){/* reexport safe */return _animation_basicTransition_js__WEBPACK_IMPORTED_MODULE_0__.XD},
/* harmony export */resizePath:function(){/* binding */return resizePath},
/* harmony export */setTooltipConfig:function(){/* binding */return setTooltipConfig},
/* harmony export */subPixelOptimize:function(){/* binding */return subPixelOptimize},
/* harmony export */subPixelOptimizeLine:function(){/* binding */return subPixelOptimizeLine},
/* harmony export */subPixelOptimizeRect:function(){/* binding */return subPixelOptimizeRect},
/* harmony export */transformDirection:function(){/* binding */return transformDirection},
/* harmony export */traverseElements:function(){/* binding */return traverseElements},
/* harmony export */updateProps:function(){/* reexport safe */return _animation_basicTransition_js__WEBPACK_IMPORTED_MODULE_0__.D}
/* harmony export */});
/* harmony import */var zrender_lib_tool_path_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(80073),zrender_lib_core_matrix_js__WEBPACK_IMPORTED_MODULE_5__=__webpack_require__(932892),zrender_lib_core_vector_js__WEBPACK_IMPORTED_MODULE_8__=__webpack_require__(45280),zrender_lib_graphic_Path_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(696997),zrender_lib_core_Transformable_js__WEBPACK_IMPORTED_MODULE_7__=__webpack_require__(487411),zrender_lib_graphic_Image_js__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(744535),zrender_lib_graphic_Group_js__WEBPACK_IMPORTED_MODULE_20__=__webpack_require__(38154),zrender_lib_graphic_Text_js__WEBPACK_IMPORTED_MODULE_21__=__webpack_require__(679750),zrender_lib_graphic_shape_Circle_js__WEBPACK_IMPORTED_MODULE_10__=__webpack_require__(469538),zrender_lib_graphic_shape_Ellipse_js__WEBPACK_IMPORTED_MODULE_11__=__webpack_require__(192797),zrender_lib_graphic_shape_Sector_js__WEBPACK_IMPORTED_MODULE_12__=__webpack_require__(497782),zrender_lib_graphic_shape_Ring_js__WEBPACK_IMPORTED_MODULE_13__=__webpack_require__(985795),zrender_lib_graphic_shape_Polygon_js__WEBPACK_IMPORTED_MODULE_14__=__webpack_require__(195094),zrender_lib_graphic_shape_Polyline_js__WEBPACK_IMPORTED_MODULE_15__=__webpack_require__(862514),zrender_lib_graphic_shape_Rect_js__WEBPACK_IMPORTED_MODULE_16__=__webpack_require__(25293),zrender_lib_graphic_shape_Line_js__WEBPACK_IMPORTED_MODULE_17__=__webpack_require__(522095),zrender_lib_graphic_shape_BezierCurve_js__WEBPACK_IMPORTED_MODULE_18__=__webpack_require__(54174),zrender_lib_graphic_shape_Arc_js__WEBPACK_IMPORTED_MODULE_19__=__webpack_require__(914826),zrender_lib_graphic_CompoundPath_js__WEBPACK_IMPORTED_MODULE_23__=__webpack_require__(752776),zrender_lib_graphic_LinearGradient_js__WEBPACK_IMPORTED_MODULE_24__=__webpack_require__(574438),zrender_lib_graphic_RadialGradient_js__WEBPACK_IMPORTED_MODULE_25__=__webpack_require__(836369),zrender_lib_core_BoundingRect_js__WEBPACK_IMPORTED_MODULE_26__=__webpack_require__(260479),zrender_lib_core_OrientedBoundingRect_js__WEBPACK_IMPORTED_MODULE_27__=__webpack_require__(841587),zrender_lib_core_Point_js__WEBPACK_IMPORTED_MODULE_28__=__webpack_require__(241610),zrender_lib_graphic_IncrementalDisplayable_js__WEBPACK_IMPORTED_MODULE_22__=__webpack_require__(691754),zrender_lib_graphic_helper_subPixelOptimize_js__WEBPACK_IMPORTED_MODULE_4__=__webpack_require__(824111),zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__=__webpack_require__(933051),_innerStore_js__WEBPACK_IMPORTED_MODULE_9__=__webpack_require__(30106),_animation_basicTransition_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(944292),mathMax=Math.max,mathMin=Math.min,_customShapeMap={};
/* harmony import */
/**
 * Extend shape with parameters
 */
function extendShape(opts){return zrender_lib_graphic_Path_js__WEBPACK_IMPORTED_MODULE_1__/* ["default"] */.ZP.extend(opts)}var extendPathFromString=zrender_lib_tool_path_js__WEBPACK_IMPORTED_MODULE_2__/* .extendFromString */.Pc;
/**
 * Extend path
 */function extendPath(pathData,opts){return extendPathFromString(pathData,opts)}
/**
 * Register a user defined shape.
 * The shape class can be fetched by `getShapeClass`
 * This method will overwrite the registered shapes, including
 * the registered built-in shapes, if using the same `name`.
 * The shape can be used in `custom series` and
 * `graphic component` by declaring `{type: name}`.
 *
 * @param name
 * @param ShapeClass Can be generated by `extendShape`.
 */function registerShape(name,ShapeClass){_customShapeMap[name]=ShapeClass}
/**
 * Find shape class registered by `registerShape`. Usually used in
 * fetching user defined shape.
 *
 * [Caution]:
 * (1) This method **MUST NOT be used inside echarts !!!**, unless it is prepared
 * to use user registered shapes.
 * Because the built-in shape (see `getBuiltInShape`) will be registered by
 * `registerShape` by default. That enables users to get both built-in
 * shapes as well as the shapes belonging to themsleves. But users can overwrite
 * the built-in shapes by using names like 'circle', 'rect' via calling
 * `registerShape`. So the echarts inner featrues should not fetch shapes from here
 * in case that it is overwritten by users, except that some features, like
 * `custom series`, `graphic component`, do it deliberately.
 *
 * (2) In the features like `custom series`, `graphic component`, the user input
 * `{tpye: 'xxx'}` does not only specify shapes but also specify other graphic
 * elements like `'group'`, `'text'`, `'image'` or event `'path'`. Those names
 * are reserved names, that is, if some user registers a shape named `'image'`,
 * the shape will not be used. If we intending to add some more reserved names
 * in feature, that might bring break changes (disable some existing user shape
 * names). But that case probably rarely happens. So we don't make more mechanism
 * to resolve this issue here.
 *
 * @param name
 * @return The shape class. If not found, return nothing.
 */function getShapeClass(name){if(_customShapeMap.hasOwnProperty(name))return _customShapeMap[name]}
/**
 * Create a path element from path data string
 * @param pathData
 * @param opts
 * @param rect
 * @param layout 'center' or 'cover' default to be cover
 */function makePath(pathData,opts,rect,layout){var path=zrender_lib_tool_path_js__WEBPACK_IMPORTED_MODULE_2__/* .createFromString */.iR(pathData,opts);return rect&&("center"===layout&&(rect=centerGraphic(rect,path.getBoundingRect())),resizePath(path,rect)),path}
/**
 * Create a image element from image url
 * @param imageUrl image url
 * @param opts options
 * @param rect constrain rect
 * @param layout 'center' or 'cover'. Default to be 'cover'
 */function makeImage(imageUrl,rect,layout){var zrImg=new zrender_lib_graphic_Image_js__WEBPACK_IMPORTED_MODULE_3__/* ["default"] */.ZP({style:{image:imageUrl,x:rect.x,y:rect.y,width:rect.width,height:rect.height},onload:function(img){if("center"===layout){var boundingRect={width:img.width,height:img.height};zrImg.setStyle(centerGraphic(rect,boundingRect))}}});return zrImg}
/**
 * Get position of centered element in bounding box.
 *
 * @param  rect         element local bounding box
 * @param  boundingRect constraint bounding box
 * @return element position containing x, y, width, and height
 */function centerGraphic(rect,boundingRect){
// Set rect to center, keep width / height ratio.
var height,aspect=boundingRect.width/boundingRect.height,width=rect.height*aspect;width<=rect.width?height=rect.height:(width=rect.width,height=width/aspect);var cx=rect.x+rect.width/2,cy=rect.y+rect.height/2;return{x:cx-width/2,y:cy-height/2,width:width,height:height}}var mergePath=zrender_lib_tool_path_js__WEBPACK_IMPORTED_MODULE_2__/* .mergePath */.AA;
/**
 * Resize a path to fit the rect
 * @param path
 * @param rect
 */function resizePath(path,rect){if(path.applyTransform){var pathRect=path.getBoundingRect(),m=pathRect.calculateTransform(rect);path.applyTransform(m)}}
/**
 * Sub pixel optimize line for canvas
 */function subPixelOptimizeLine(shape,lineWidth){return zrender_lib_graphic_helper_subPixelOptimize_js__WEBPACK_IMPORTED_MODULE_4__/* .subPixelOptimizeLine */._3(shape,shape,{lineWidth:lineWidth}),shape}
/**
 * Sub pixel optimize rect for canvas
 */function subPixelOptimizeRect(param){return zrender_lib_graphic_helper_subPixelOptimize_js__WEBPACK_IMPORTED_MODULE_4__/* .subPixelOptimizeRect */.Pw(param.shape,param.shape,param.style),param}
/**
 * Sub pixel optimize for canvas
 *
 * @param position Coordinate, such as x, y
 * @param lineWidth Should be nonnegative integer.
 * @param positiveOrNegative Default false (negative).
 * @return Optimized position.
 */var subPixelOptimize=zrender_lib_graphic_helper_subPixelOptimize_js__WEBPACK_IMPORTED_MODULE_4__/* .subPixelOptimize */.vu;
/**
 * Get transform matrix of target (param target),
 * in coordinate of its ancestor (param ancestor)
 *
 * @param target
 * @param [ancestor]
 */function getTransform(target,ancestor){var mat=zrender_lib_core_matrix_js__WEBPACK_IMPORTED_MODULE_5__/* .identity */.yR([]);while(target&&target!==ancestor)zrender_lib_core_matrix_js__WEBPACK_IMPORTED_MODULE_5__/* .mul */.dC(mat,target.getLocalTransform(),mat),target=target.parent;return mat}
/**
 * Apply transform to an vertex.
 * @param target [x, y]
 * @param transform Can be:
 *      + Transform matrix: like [1, 0, 0, 1, 0, 0]
 *      + {position, rotation, scale}, the same as `zrender/Transformable`.
 * @param invert Whether use invert matrix.
 * @return [x, y]
 */function applyTransform(target,transform,invert){return transform&&!(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .isArrayLike */.zG)(transform)&&(transform=zrender_lib_core_Transformable_js__WEBPACK_IMPORTED_MODULE_7__/* ["default"] */.ZP.getLocalTransform(transform)),invert&&(transform=zrender_lib_core_matrix_js__WEBPACK_IMPORTED_MODULE_5__/* .invert */.U_([],transform)),zrender_lib_core_vector_js__WEBPACK_IMPORTED_MODULE_8__/* .applyTransform */.Ne([],target,transform)}
/**
 * @param direction 'left' 'right' 'top' 'bottom'
 * @param transform Transform matrix: like [1, 0, 0, 1, 0, 0]
 * @param invert Whether use invert matrix.
 * @return Transformed direction. 'left' 'right' 'top' 'bottom'
 */function transformDirection(direction,transform,invert){
// Pick a base, ensure that transform result will not be (0, 0).
var hBase=0===transform[4]||0===transform[5]||0===transform[0]?1:Math.abs(2*transform[4]/transform[0]),vBase=0===transform[4]||0===transform[5]||0===transform[2]?1:Math.abs(2*transform[4]/transform[2]),vertex=["left"===direction?-hBase:"right"===direction?hBase:0,"top"===direction?-vBase:"bottom"===direction?vBase:0];return vertex=applyTransform(vertex,transform,invert),Math.abs(vertex[0])>Math.abs(vertex[1])?vertex[0]>0?"right":"left":vertex[1]>0?"bottom":"top"}function isNotGroup(el){return!el.isGroup}function isPath(el){return null!=el.shape}
/**
 * Apply group transition animation from g1 to g2.
 * If no animatableModel, no animation.
 */function groupTransition(g1,g2,animatableModel){if(g1&&g2){var elMap1=getElMap(g1);g2.traverse((function(el){if(isNotGroup(el)&&el.anid){var oldEl=elMap1[el.anid];if(oldEl){var newProp=getAnimatableProps(el);el.attr(getAnimatableProps(oldEl)),(0,_animation_basicTransition_js__WEBPACK_IMPORTED_MODULE_0__/* .updateProps */.D)(el,newProp,animatableModel,(0,_innerStore_js__WEBPACK_IMPORTED_MODULE_9__/* .getECData */.A)(el).dataIndex)}}}))}function getElMap(g){var elMap={};return g.traverse((function(el){isNotGroup(el)&&el.anid&&(elMap[el.anid]=el)})),elMap}function getAnimatableProps(el){var obj={x:el.x,y:el.y,rotation:el.rotation};return isPath(el)&&(obj.shape=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .extend */.l7)({},el.shape)),obj}}function clipPointsByRect(points,rect){
// FIXME: This way might be incorrect when graphic clipped by a corner
// and when element has a border.
return(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .map */.UI)(points,(function(point){var x=point[0];x=mathMax(x,rect.x),x=mathMin(x,rect.x+rect.width);var y=point[1];return y=mathMax(y,rect.y),y=mathMin(y,rect.y+rect.height),[x,y]}))}
/**
 * Return a new clipped rect. If rect size are negative, return undefined.
 */function clipRectByRect(targetRect,rect){var x=mathMax(targetRect.x,rect.x),x2=mathMin(targetRect.x+targetRect.width,rect.x+rect.width),y=mathMax(targetRect.y,rect.y),y2=mathMin(targetRect.y+targetRect.height,rect.y+rect.height);// If the total rect is cliped, nothing, including the border,
// should be painted. So return undefined.
if(x2>=x&&y2>=y)return{x:x,y:y,width:x2-x,height:y2-y}}function createIcon(iconStr,// Support 'image://' or 'path://' or direct svg path.
opt,rect){var innerOpts=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .extend */.l7)({rectHover:!0},opt),style=innerOpts.style={strokeNoScale:!0};if(rect=rect||{x:-1,y:-1,width:2,height:2},iconStr)return 0===iconStr.indexOf("image://")?(style.image=iconStr.slice(8),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .defaults */.ce)(style,rect),new zrender_lib_graphic_Image_js__WEBPACK_IMPORTED_MODULE_3__/* ["default"] */.ZP(innerOpts)):makePath(iconStr.replace("path://",""),innerOpts,rect,"center")}
/**
 * Return `true` if the given line (line `a`) and the given polygon
 * are intersect.
 * Note that we do not count colinear as intersect here because no
 * requirement for that. We could do that if required in future.
 */function linePolygonIntersect(a1x,a1y,a2x,a2y,points){for(var i=0,p2=points[points.length-1];i<points.length;i++){var p=points[i];if(lineLineIntersect(a1x,a1y,a2x,a2y,p[0],p[1],p2[0],p2[1]))return!0;p2=p}}
/**
 * Return `true` if the given two lines (line `a` and line `b`)
 * are intersect.
 * Note that we do not count colinear as intersect here because no
 * requirement for that. We could do that if required in future.
 */function lineLineIntersect(a1x,a1y,a2x,a2y,b1x,b1y,b2x,b2y){
// let `vec_m` to be `vec_a2 - vec_a1` and `vec_n` to be `vec_b2 - vec_b1`.
var mx=a2x-a1x,my=a2y-a1y,nx=b2x-b1x,ny=b2y-b1y,nmCrossProduct=crossProduct2d(nx,ny,mx,my);if(nearZero(nmCrossProduct))return!1;// `vec_m` and `vec_n` are intersect iff
//     existing `p` and `q` in [0, 1] such that `vec_a1 + p * vec_m = vec_b1 + q * vec_n`,
//     such that `q = ((vec_a1 - vec_b1) X vec_m) / (vec_n X vec_m)`
//           and `p = ((vec_a1 - vec_b1) X vec_n) / (vec_n X vec_m)`.
var b1a1x=a1x-b1x,b1a1y=a1y-b1y,q=crossProduct2d(b1a1x,b1a1y,mx,my)/nmCrossProduct;if(q<0||q>1)return!1;var p=crossProduct2d(b1a1x,b1a1y,nx,ny)/nmCrossProduct;return!(p<0||p>1)}
/**
 * Cross product of 2-dimension vector.
 */function crossProduct2d(x1,y1,x2,y2){return x1*y2-x2*y1}function nearZero(val){return val<=1e-6&&val>=-1e-6}function setTooltipConfig(opt){var itemTooltipOption=opt.itemTooltipOption,componentModel=opt.componentModel,itemName=opt.itemName,itemTooltipOptionObj=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .isString */.HD)(itemTooltipOption)?{formatter:itemTooltipOption}:itemTooltipOption,mainType=componentModel.mainType,componentIndex=componentModel.componentIndex,formatterParams={componentType:mainType,name:itemName,$vars:["name"]};formatterParams[mainType+"Index"]=componentIndex;var formatterParamsExtra=opt.formatterParamsExtra;formatterParamsExtra&&(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .each */.S6)((0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .keys */.XP)(formatterParamsExtra),(function(key){(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .hasOwn */.RI)(formatterParams,key)||(formatterParams[key]=formatterParamsExtra[key],formatterParams.$vars.push(key))}));var ecData=(0,_innerStore_js__WEBPACK_IMPORTED_MODULE_9__/* .getECData */.A)(opt.el);ecData.componentMainType=mainType,ecData.componentIndex=componentIndex,ecData.tooltipConfig={name:itemName,option:(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .defaults */.ce)({content:itemName,formatterParams:formatterParams},itemTooltipOptionObj)}}function traverseElement(el,cb){var stopped;// TODO
// Polyfill for fixing zrender group traverse don't visit it's root issue.
el.isGroup&&(stopped=cb(el)),stopped||el.traverse(cb)}function traverseElements(els,cb){if(els)if((0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_6__/* .isArray */.kJ)(els))for(var i=0;i<els.length;i++)traverseElement(els[i],cb);else traverseElement(els,cb)}// Register built-in shapes. These shapes might be overwritten
// by users, although we do not recommend that.
registerShape("circle",zrender_lib_graphic_shape_Circle_js__WEBPACK_IMPORTED_MODULE_10__/* ["default"] */.Z),registerShape("ellipse",zrender_lib_graphic_shape_Ellipse_js__WEBPACK_IMPORTED_MODULE_11__/* ["default"] */.Z),registerShape("sector",zrender_lib_graphic_shape_Sector_js__WEBPACK_IMPORTED_MODULE_12__/* ["default"] */.C),registerShape("ring",zrender_lib_graphic_shape_Ring_js__WEBPACK_IMPORTED_MODULE_13__/* ["default"] */.Z),registerShape("polygon",zrender_lib_graphic_shape_Polygon_js__WEBPACK_IMPORTED_MODULE_14__/* ["default"] */.Z),registerShape("polyline",zrender_lib_graphic_shape_Polyline_js__WEBPACK_IMPORTED_MODULE_15__/* ["default"] */.Z),registerShape("rect",zrender_lib_graphic_shape_Rect_js__WEBPACK_IMPORTED_MODULE_16__/* ["default"] */.Z),registerShape("line",zrender_lib_graphic_shape_Line_js__WEBPACK_IMPORTED_MODULE_17__/* ["default"] */.Z),registerShape("bezierCurve",zrender_lib_graphic_shape_BezierCurve_js__WEBPACK_IMPORTED_MODULE_18__/* ["default"] */.Z),registerShape("arc",zrender_lib_graphic_shape_Arc_js__WEBPACK_IMPORTED_MODULE_19__/* ["default"] */.Z)},
/***/30106:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */A:function(){/* binding */return getECData},
/* harmony export */Q:function(){/* binding */return setCommonECData}
/* harmony export */});
/* harmony import */var _model_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(232234),getECData=(0,_model_js__WEBPACK_IMPORTED_MODULE_0__/* .makeInner */.Yf)(),setCommonECData=function(seriesIndex,dataType,dataIdx,el){if(el){var ecData=getECData(el);// Add data index and series index for indexing the data by element
// Useful in tooltip
ecData.dataIndex=dataIdx,ecData.dataType=dataType,ecData.seriesIndex=seriesIndex,// TODO: not store dataIndex on children.
"group"===el.type&&el.traverse((function(child){var childECData=getECData(child);childECData.seriesIndex=seriesIndex,childECData.dataIndex=dataIdx,childECData.dataType=dataType}))}};
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/},
/***/576172:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */BZ:function(){/* binding */return box},
/* harmony export */ME:function(){/* binding */return getLayoutRect},
/* harmony export */QT:function(){/* binding */return copyLayoutParams},
/* harmony export */YD:function(){/* binding */return fetchLayoutMode},
/* harmony export */dt:function(){/* binding */return mergeLayoutParam},
/* harmony export */gN:function(){/* binding */return LOCATION_PARAMS},
/* harmony export */lq:function(){/* binding */return getAvailableSize},
/* harmony export */p$:function(){/* binding */return positionElement},
/* harmony export */tE:function(){/* binding */return getLayoutParams}
/* harmony export */});
/* unused harmony exports HV_NAMES, vbox, hbox, sizeCalculable */
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(933051),zrender_lib_core_BoundingRect_js__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(260479),_number_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(785669),_format_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(578988),each=zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6,LOCATION_PARAMS=["left","right","top","bottom","width","height"],HV_NAMES=[["width","left","right"],["height","top","bottom"]];
/* harmony import */function boxLayout(orient,group,gap,maxWidth,maxHeight){var x=0,y=0;null==maxWidth&&(maxWidth=1/0),null==maxHeight&&(maxHeight=1/0);var currentLineMaxSize=0;group.eachChild((function(child,idx){var nextX,nextY,rect=child.getBoundingRect(),nextChild=group.childAt(idx+1),nextChildRect=nextChild&&nextChild.getBoundingRect();if("horizontal"===orient){var moveX=rect.width+(nextChildRect?-nextChildRect.x+rect.x:0);nextX=x+moveX,// Wrap when width exceeds maxWidth or meet a `newline` group
// FIXME compare before adding gap?
nextX>maxWidth||child.newline?(x=0,nextX=moveX,y+=currentLineMaxSize+gap,currentLineMaxSize=rect.height):
// FIXME: consider rect.y is not `0`?
currentLineMaxSize=Math.max(currentLineMaxSize,rect.height)}else{var moveY=rect.height+(nextChildRect?-nextChildRect.y+rect.y:0);nextY=y+moveY,// Wrap when width exceeds maxHeight or meet a `newline` group
nextY>maxHeight||child.newline?(x+=currentLineMaxSize+gap,y=0,nextY=moveY,currentLineMaxSize=rect.width):currentLineMaxSize=Math.max(currentLineMaxSize,rect.width)}child.newline||(child.x=x,child.y=y,child.markRedraw(),"horizontal"===orient?x=nextX+gap:y=nextY+gap)}))}
/**
 * VBox or HBox layouting
 * @param {string} orient
 * @param {module:zrender/graphic/Group} group
 * @param {number} gap
 * @param {number} [width=Infinity]
 * @param {number} [height=Infinity]
 */var box=boxLayout;
/**
 * VBox layouting
 * @param {module:zrender/graphic/Group} group
 * @param {number} gap
 * @param {number} [width=Infinity]
 * @param {number} [height=Infinity]
 */zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .curry */.WA(boxLayout,"vertical"),zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .curry */.WA(boxLayout,"horizontal");
/**
 * If x or x2 is not specified or 'center' 'left' 'right',
 * the width would be as long as possible.
 * If y or y2 is not specified or 'middle' 'top' 'bottom',
 * the height would be as long as possible.
 */
function getAvailableSize(positionInfo,containerRect,margin){var containerWidth=containerRect.width,containerHeight=containerRect.height,x=(0,_number_js__WEBPACK_IMPORTED_MODULE_1__/* .parsePercent */.GM)(positionInfo.left,containerWidth),y=(0,_number_js__WEBPACK_IMPORTED_MODULE_1__/* .parsePercent */.GM)(positionInfo.top,containerHeight),x2=(0,_number_js__WEBPACK_IMPORTED_MODULE_1__/* .parsePercent */.GM)(positionInfo.right,containerWidth),y2=(0,_number_js__WEBPACK_IMPORTED_MODULE_1__/* .parsePercent */.GM)(positionInfo.bottom,containerHeight);return(isNaN(x)||isNaN(parseFloat(positionInfo.left)))&&(x=0),(isNaN(x2)||isNaN(parseFloat(positionInfo.right)))&&(x2=containerWidth),(isNaN(y)||isNaN(parseFloat(positionInfo.top)))&&(y=0),(isNaN(y2)||isNaN(parseFloat(positionInfo.bottom)))&&(y2=containerHeight),margin=_format_js__WEBPACK_IMPORTED_MODULE_2__/* .normalizeCssArray */.MY(margin||0),{width:Math.max(x2-x-margin[1]-margin[3],0),height:Math.max(y2-y-margin[0]-margin[2],0)}}
/**
 * Parse position info.
 */function getLayoutRect(positionInfo,containerRect,margin){margin=_format_js__WEBPACK_IMPORTED_MODULE_2__/* .normalizeCssArray */.MY(margin||0);var containerWidth=containerRect.width,containerHeight=containerRect.height,left=(0,_number_js__WEBPACK_IMPORTED_MODULE_1__/* .parsePercent */.GM)(positionInfo.left,containerWidth),top=(0,_number_js__WEBPACK_IMPORTED_MODULE_1__/* .parsePercent */.GM)(positionInfo.top,containerHeight),right=(0,_number_js__WEBPACK_IMPORTED_MODULE_1__/* .parsePercent */.GM)(positionInfo.right,containerWidth),bottom=(0,_number_js__WEBPACK_IMPORTED_MODULE_1__/* .parsePercent */.GM)(positionInfo.bottom,containerHeight),width=(0,_number_js__WEBPACK_IMPORTED_MODULE_1__/* .parsePercent */.GM)(positionInfo.width,containerWidth),height=(0,_number_js__WEBPACK_IMPORTED_MODULE_1__/* .parsePercent */.GM)(positionInfo.height,containerHeight),verticalMargin=margin[2]+margin[0],horizontalMargin=margin[1]+margin[3],aspect=positionInfo.aspect;// Align left and top
switch(// If width is not specified, calculate width from left and right
isNaN(width)&&(width=containerWidth-right-horizontalMargin-left),isNaN(height)&&(height=containerHeight-bottom-verticalMargin-top),null!=aspect&&(
// If width and height are not given
// 1. Graph should not exceeds the container
// 2. Aspect must be keeped
// 3. Graph should take the space as more as possible
// FIXME
// Margin is not considered, because there is no case that both
// using margin and aspect so far.
isNaN(width)&&isNaN(height)&&(aspect>containerWidth/containerHeight?width=.8*containerWidth:height=.8*containerHeight),// Calculate width or height with given aspect
isNaN(width)&&(width=aspect*height),isNaN(height)&&(height=width/aspect)),// If left is not specified, calculate left from right and width
isNaN(left)&&(left=containerWidth-right-width-horizontalMargin),isNaN(top)&&(top=containerHeight-bottom-height-verticalMargin),positionInfo.left||positionInfo.right){case"center":left=containerWidth/2-width/2-margin[3];break;case"right":left=containerWidth-width-horizontalMargin;break}switch(positionInfo.top||positionInfo.bottom){case"middle":case"center":top=containerHeight/2-height/2-margin[0];break;case"bottom":top=containerHeight-height-verticalMargin;break}// If something is wrong and left, top, width, height are calculated as NaN
left=left||0,top=top||0,isNaN(width)&&(
// Width may be NaN if only one value is given except width
width=containerWidth-horizontalMargin-left-(right||0)),isNaN(height)&&(
// Height may be NaN if only one value is given except height
height=containerHeight-verticalMargin-top-(bottom||0));var rect=new zrender_lib_core_BoundingRect_js__WEBPACK_IMPORTED_MODULE_3__/* ["default"] */.Z(left+margin[3],top+margin[0],width,height);return rect.margin=margin,rect}
/**
 * Position a zr element in viewport
 *  Group position is specified by either
 *  {left, top}, {right, bottom}
 *  If all properties exists, right and bottom will be igonred.
 *
 * Logic:
 *     1. Scale (against origin point in parent coord)
 *     2. Rotate (against origin point in parent coord)
 *     3. Translate (with el.position by this method)
 * So this method only fixes the last step 'Translate', which does not affect
 * scaling and rotating.
 *
 * If be called repeatedly with the same input el, the same result will be gotten.
 *
 * Return true if the layout happened.
 *
 * @param el Should have `getBoundingRect` method.
 * @param positionInfo
 * @param positionInfo.left
 * @param positionInfo.top
 * @param positionInfo.right
 * @param positionInfo.bottom
 * @param positionInfo.width Only for opt.boundingModel: 'raw'
 * @param positionInfo.height Only for opt.boundingModel: 'raw'
 * @param containerRect
 * @param margin
 * @param opt
 * @param opt.hv Only horizontal or only vertical. Default to be [1, 1]
 * @param opt.boundingMode
 *        Specify how to calculate boundingRect when locating.
 *        'all': Position the boundingRect that is transformed and uioned
 *               both itself and its descendants.
 *               This mode simplies confine the elements in the bounding
 *               of their container (e.g., using 'right: 0').
 *        'raw': Position the boundingRect that is not transformed and only itself.
 *               This mode is useful when you want a element can overflow its
 *               container. (Consider a rotated circle needs to be located in a corner.)
 *               In this mode positionInfo.width/height can only be number.
 */function positionElement(el,positionInfo,containerRect,margin,opt,out){var rect,h=!opt||!opt.hv||opt.hv[0],v=!opt||!opt.hv||opt.hv[1],boundingMode=opt&&opt.boundingMode||"all";if(out=out||el,out.x=el.x,out.y=el.y,!h&&!v)return!1;if("raw"===boundingMode)rect="group"===el.type?new zrender_lib_core_BoundingRect_js__WEBPACK_IMPORTED_MODULE_3__/* ["default"] */.Z(0,0,+positionInfo.width||0,+positionInfo.height||0):el.getBoundingRect();else if(rect=el.getBoundingRect(),el.needLocalTransform()){var transform=el.getLocalTransform();// Notice: raw rect may be inner object of el,
// which should not be modified.
rect=rect.clone(),rect.applyTransform(transform)}// The real width and height can not be specified but calculated by the given el.
var layoutRect=getLayoutRect(zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .defaults */.ce({width:rect.width,height:rect.height},positionInfo),containerRect,margin),dx=h?layoutRect.x-rect.x:0,dy=v?layoutRect.y-rect.y:0;// Because 'tranlate' is the last step in transform
// (see zrender/core/Transformable#getLocalTransform),
// we can just only modify el.position to get final result.
return"raw"===boundingMode?(out.x=dx,out.y=dy):(out.x+=dx,out.y+=dy),out===el&&el.markRedraw(),!0}
/**
 * @param option Contains some of the properties in HV_NAMES.
 * @param hvIdx 0: horizontal; 1: vertical.
 */function fetchLayoutMode(ins){var layoutMode=ins.layoutMode||ins.constructor.layoutMode;return zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isObject */.Kn(layoutMode)?layoutMode:layoutMode?{type:layoutMode}:null}
/**
 * Consider Case:
 * When default option has {left: 0, width: 100}, and we set {right: 0}
 * through setOption or media query, using normal zrUtil.merge will cause
 * {right: 0} does not take effect.
 *
 * @example
 * ComponentModel.extend({
 *     init: function () {
 *         ...
 *         let inputPositionParams = layout.getLayoutParams(option);
 *         this.mergeOption(inputPositionParams);
 *     },
 *     mergeOption: function (newOption) {
 *         newOption && zrUtil.merge(thisOption, newOption, true);
 *         layout.mergeLayoutParam(thisOption, newOption);
 *     }
 * });
 *
 * @param targetOption
 * @param newOption
 * @param opt
 */function mergeLayoutParam(targetOption,newOption,opt){var ignoreSize=opt&&opt.ignoreSize;!zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isArray */.kJ(ignoreSize)&&(ignoreSize=[ignoreSize,ignoreSize]);var hResult=merge(HV_NAMES[0],0),vResult=merge(HV_NAMES[1],1);function merge(names,hvIdx){var newParams={},newValueCount=0,merged={},mergedValueCount=0,enoughParamNumber=2;if(each(names,(function(name){merged[name]=targetOption[name]})),each(names,(function(name){
// Consider case: newOption.width is null, which is
// set by user for removing width setting.
hasProp(newOption,name)&&(newParams[name]=merged[name]=newOption[name]),hasValue(newParams,name)&&newValueCount++,hasValue(merged,name)&&mergedValueCount++})),ignoreSize[hvIdx])
// Only one of left/right is premitted to exist.
return hasValue(newOption,names[1])?merged[names[2]]=null:hasValue(newOption,names[2])&&(merged[names[1]]=null),merged;// Case: newOption: {width: ..., right: ...},
// or targetOption: {right: ...} and newOption: {width: ...},
// There is no conflict when merged only has params count
// little than enoughParamNumber.
if(mergedValueCount!==enoughParamNumber&&newValueCount){if(newValueCount>=enoughParamNumber)return newParams;
// Chose another param from targetOption by priority.
for(var i=0;i<names.length;i++){var name_1=names[i];if(!hasProp(newParams,name_1)&&hasProp(targetOption,name_1)){newParams[name_1]=targetOption[name_1];break}}return newParams}return merged}function hasProp(obj,name){return obj.hasOwnProperty(name)}function hasValue(obj,name){return null!=obj[name]&&"auto"!==obj[name]}function copy(names,target,source){each(names,(function(name){target[name]=source[name]}))}copy(HV_NAMES[0],targetOption,hResult),copy(HV_NAMES[1],targetOption,vResult)}
/**
 * Retrieve 'left', 'right', 'top', 'bottom', 'width', 'height' from object.
 */function getLayoutParams(source){return copyLayoutParams({},source)}
/**
 * Retrieve 'left', 'right', 'top', 'bottom', 'width', 'height' from object.
 * @param {Object} source
 * @return {Object} Result contains those props.
 */function copyLayoutParams(target,source){return source&&target&&each(LOCATION_PARAMS,(function(name){source.hasOwnProperty(name)&&(target[name]=source[name])})),target}
/***/},
/***/470175:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Sh:function(){/* binding */return deprecateLog},
/* harmony export */ZK:function(){/* binding */return warn},
/* harmony export */_y:function(){/* binding */return throwError}
/* harmony export */});
/* unused harmony exports log, error, deprecateReplaceLog, makePrintable */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
var storedLogs={},hasConsole="undefined"!==typeof console&&console.warn&&console.log;function outputLog(type,str,onlyOnce){if(hasConsole&&onlyOnce){if(storedLogs[str])return;storedLogs[str]=!0}// eslint-disable-next-line
}function warn(str,onlyOnce){outputLog("warn",str,onlyOnce)}function deprecateLog(str){0}
/**
 * @throws Error
 */
function throwError(msg){throw new Error(msg)}
/***/},
/***/232234:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */C4:function(){/* binding */return getDataItemValue},
/* harmony export */C6:function(){/* binding */return SINGLE_REFERRING},
/* harmony export */Cc:function(){/* binding */return defaultEmphasis},
/* harmony export */Co:function(){/* binding */return isDataItemOption},
/* harmony export */HZ:function(){/* binding */return queryReferringComponents},
/* harmony export */IL:function(){/* binding */return getAttribute},
/* harmony export */O0:function(){/* binding */return setComponentTypeToKeyInfo},
/* harmony export */P$:function(){/* binding */return setAttribute},
/* harmony export */Td:function(){/* binding */return TEXT_STYLE_OPTIONS},
/* harmony export */U5:function(){/* binding */return convertOptionIdName},
/* harmony export */U9:function(){/* binding */return getTooltipRenderMode},
/* harmony export */XI:function(){/* binding */return compressBatches},
/* harmony export */Yf:function(){/* binding */return makeInner},
/* harmony export */ab:function(){/* binding */return mappingToExists},
/* harmony export */gO:function(){/* binding */return queryDataIndex},
/* harmony export */iP:function(){/* binding */return MULTIPLE_REFERRING},
/* harmony export */kF:function(){/* binding */return normalizeToArray},
/* harmony export */lY:function(){/* binding */return isComponentIdInternal},
/* harmony export */pk:function(){/* binding */return interpolateRawValues},
/* harmony export */pm:function(){/* binding */return parseFinder},
/* harmony export */yu:function(){/* binding */return isNameSpecified},
/* harmony export */zH:function(){/* binding */return preParseFinder}
/* harmony export */});
/* unused harmony exports makeInternalComponentId, groupData */
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(933051),zrender_lib_core_env_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(966387),_number_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(785669);
/* harmony import */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
function interpolateNumber(p0,p1,percent){return(p1-p0)*percent+p0}
/**
 * Make the name displayable. But we should
 * make sure it is not duplicated with user
 * specified name, so use '\0';
 */var DUMMY_COMPONENT_NAME_PREFIX="series\0",INTERNAL_COMPONENT_ID_PREFIX="\0_ec_\0";
/**
 * If value is not array, then translate it to array.
 * @param  {*} value
 * @return {Array} [value] or value
 */
function normalizeToArray(value){return value instanceof Array?value:null==value?[]:[value]}
/**
 * Sync default option between normal and emphasis like `position` and `show`
 * In case some one will write code like
 *     label: {
 *          show: false,
 *          position: 'outside',
 *          fontSize: 18
 *     },
 *     emphasis: {
 *          label: { show: true }
 *     }
 */function defaultEmphasis(opt,key,subOpts){
// Caution: performance sensitive.
if(opt){opt[key]=opt[key]||{},opt.emphasis=opt.emphasis||{},opt.emphasis[key]=opt.emphasis[key]||{};// Default emphasis option from normal
for(var i=0,len=subOpts.length;i<len;i++){var subOptName=subOpts[i];!opt.emphasis[key].hasOwnProperty(subOptName)&&opt[key].hasOwnProperty(subOptName)&&(opt.emphasis[key][subOptName]=opt[key][subOptName])}}}var TEXT_STYLE_OPTIONS=["fontStyle","fontWeight","fontSize","fontFamily","rich","tag","color","textBorderColor","textBorderWidth","width","height","lineHeight","align","verticalAlign","baseline","shadowColor","shadowBlur","shadowOffsetX","shadowOffsetY","textShadowColor","textShadowBlur","textShadowOffsetX","textShadowOffsetY","backgroundColor","borderColor","borderWidth","borderRadius","padding"];// modelUtil.LABEL_OPTIONS = modelUtil.TEXT_STYLE_OPTIONS.concat([
//     'position', 'offset', 'rotate', 'origin', 'show', 'distance', 'formatter',
//     'fontStyle', 'fontWeight', 'fontSize', 'fontFamily',
//     // FIXME: deprecated, check and remove it.
//     'textStyle'
// ]);
/**
 * The method does not ensure performance.
 * data could be [12, 2323, {value: 223}, [1221, 23], {value: [2, 23]}]
 * This helper method retrieves value from data.
 */function getDataItemValue(dataItem){return!(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isObject */.Kn)(dataItem)||(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isArray */.kJ)(dataItem)||dataItem instanceof Date?dataItem:dataItem.value}
/**
 * data could be [12, 2323, {value: 223}, [1221, 23], {value: [2, 23]}]
 * This helper method determine if dataItem has extra option besides value
 */function isDataItemOption(dataItem){return(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isObject */.Kn)(dataItem)&&!(dataItem instanceof Array);// // markLine data can be array
// && !(dataItem[0] && isObject(dataItem[0]) && !(dataItem[0] instanceof Array));
}
/**
 * Mapping to existings for merge.
 *
 * Mode "normalMege":
 *     The mapping result (merge result) will keep the order of the existing
 *     component, rather than the order of new option. Because we should ensure
 *     some specified index reference (like xAxisIndex) keep work.
 *     And in most cases, "merge option" is used to update partial option but not
 *     be expected to change the order.
 *
 * Mode "replaceMege":
 *     (1) Only the id mapped components will be merged.
 *     (2) Other existing components (except internal components) will be removed.
 *     (3) Other new options will be used to create new component.
 *     (4) The index of the existing components will not be modified.
 *     That means their might be "hole" after the removal.
 *     The new components are created first at those available index.
 *
 * Mode "replaceAll":
 *     This mode try to support that reproduce an echarts instance from another
 *     echarts instance (via `getOption`) in some simple cases.
 *     In this scenario, the `result` index are exactly the consistent with the `newCmptOptions`,
 *     which ensures the component index referring (like `xAxisIndex: ?`) corrent. That is,
 *     the "hole" in `newCmptOptions` will also be kept.
 *     On the contrary, other modes try best to eliminate holes.
 *     PENDING: This is an experimental mode yet.
 *
 * @return See the comment of <MappingResult>.
 */
function mappingToExists(existings,newCmptOptions,mode){var isNormalMergeMode="normalMerge"===mode,isReplaceMergeMode="replaceMerge"===mode,isReplaceAllMode="replaceAll"===mode;existings=existings||[],newCmptOptions=(newCmptOptions||[]).slice();var existingIdIdxMap=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .createHashMap */.kW)();// Validate id and name on user input option.
(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6)(newCmptOptions,(function(cmptOption,index){(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isObject */.Kn)(cmptOption)||(newCmptOptions[index]=null)}));var result=prepareResult(existings,existingIdIdxMap,mode);// The array `result` MUST NOT contain elided items, otherwise the
// forEach will omit those items and result in incorrect result.
return(isNormalMergeMode||isReplaceMergeMode)&&mappingById(result,existings,existingIdIdxMap,newCmptOptions),isNormalMergeMode&&mappingByName(result,newCmptOptions),isNormalMergeMode||isReplaceMergeMode?mappingByIndex(result,newCmptOptions,isReplaceMergeMode):isReplaceAllMode&&mappingInReplaceAllMode(result,newCmptOptions),makeIdAndName(result),result}function prepareResult(existings,existingIdIdxMap,mode){var result=[];if("replaceAll"===mode)return result;// Do not use native `map` to in case that the array `existings`
// contains elided items, which will be omitted.
for(var index=0;index<existings.length;index++){var existing=existings[index];// Because of replaceMerge, `existing` may be null/undefined.
existing&&null!=existing.id&&existingIdIdxMap.set(existing.id,index),// For non-internal-componnets:
//     Mode "normalMerge": all existings kept.
//     Mode "replaceMerge": all existing removed unless mapped by id.
// For internal-components:
//     go with "replaceMerge" approach in both mode.
result.push({existing:"replaceMerge"===mode||isComponentIdInternal(existing)?null:existing,newOption:null,keyInfo:null,brandNew:null})}return result}function mappingById(result,existings,existingIdIdxMap,newCmptOptions){
// Mapping by id if specified.
(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6)(newCmptOptions,(function(cmptOption,index){if(cmptOption&&null!=cmptOption.id){var optionId=makeComparableKey(cmptOption.id),existingIdx=existingIdIdxMap.get(optionId);if(null!=existingIdx){var resultItem=result[existingIdx];(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .assert */.hu)(!resultItem.newOption,'Duplicated option on id "'+optionId+'".'),resultItem.newOption=cmptOption,// In both mode, if id matched, new option will be merged to
// the existings rather than creating new component model.
resultItem.existing=existings[existingIdx],newCmptOptions[index]=null}}}))}function mappingByName(result,newCmptOptions){
// Mapping by name if specified.
(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6)(newCmptOptions,(function(cmptOption,index){if(cmptOption&&null!=cmptOption.name)for(var i=0;i<result.length;i++){var existing=result[i].existing;if(!result[i].newOption&&existing&&(null==existing.id||null==cmptOption.id)&&!isComponentIdInternal(cmptOption)&&!isComponentIdInternal(existing)&&keyExistAndEqual("name",existing,cmptOption))return result[i].newOption=cmptOption,void(newCmptOptions[index]=null)}}))}function mappingByIndex(result,newCmptOptions,brandNew){(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6)(newCmptOptions,(function(cmptOption){if(cmptOption){// Find the first place that not mapped by id and not internal component (consider the "hole").
var resultItem,nextIdx=0;while(// Be `!resultItem` only when `nextIdx >= result.length`.
(resultItem=result[nextIdx])&&(// (1) Existing models that already have id should be able to mapped to. Because
// after mapping performed, model will always be assigned with an id if user not given.
// After that all models have id.
// (2) If new option has id, it can only set to a hole or append to the last. It should
// not be merged to the existings with different id. Because id should not be overwritten.
// (3) Name can be overwritten, because axis use name as 'show label text'.
resultItem.newOption||isComponentIdInternal(resultItem.existing)||// In mode "replaceMerge", here no not-mapped-non-internal-existing.
resultItem.existing&&null!=cmptOption.id&&!keyExistAndEqual("id",cmptOption,resultItem.existing)))nextIdx++;resultItem?(resultItem.newOption=cmptOption,resultItem.brandNew=brandNew):result.push({newOption:cmptOption,brandNew:brandNew,existing:null,keyInfo:null}),nextIdx++}}))}function mappingInReplaceAllMode(result,newCmptOptions){(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6)(newCmptOptions,(function(cmptOption){
// The feature "reproduce" requires "hole" will also reproduced
// in case that component index referring are broken.
result.push({newOption:cmptOption,brandNew:!0,existing:null,keyInfo:null})}))}
/**
 * Make id and name for mapping result (result of mappingToExists)
 * into `keyInfo` field.
 */function makeIdAndName(mapResult){
// We use this id to hash component models and view instances
// in echarts. id can be specified by user, or auto generated.
// The id generation rule ensures new view instance are able
// to mapped to old instance when setOption are called in
// no-merge mode. So we generate model id by name and plus
// type in view id.
// name can be duplicated among components, which is convenient
// to specify multi components (like series) by one name.
// Ensure that each id is distinct.
var idMap=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .createHashMap */.kW)();(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6)(mapResult,(function(item){var existing=item.existing;existing&&idMap.set(existing.id,item)})),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6)(mapResult,(function(item){var opt=item.newOption;// Force ensure id not duplicated.
(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .assert */.hu)(!opt||null==opt.id||!idMap.get(opt.id)||idMap.get(opt.id)===item,"id duplicates: "+(opt&&opt.id)),opt&&null!=opt.id&&idMap.set(opt.id,item),!item.keyInfo&&(item.keyInfo={})})),// Make name and id.
(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6)(mapResult,(function(item,index){var existing=item.existing,opt=item.newOption,keyInfo=item.keyInfo;if((0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isObject */.Kn)(opt)){if(// Name can be overwritten. Consider case: axis.name = '20km'.
// But id generated by name will not be changed, which affect
// only in that case: setOption with 'not merge mode' and view
// instance will be recreated, which can be accepted.
keyInfo.name=null!=opt.name?makeComparableKey(opt.name):existing?existing.name:DUMMY_COMPONENT_NAME_PREFIX+index,existing)keyInfo.id=makeComparableKey(existing.id);else if(null!=opt.id)keyInfo.id=makeComparableKey(opt.id);else{
// Consider this situatoin:
//  optionA: [{name: 'a'}, {name: 'a'}, {..}]
//  optionB [{..}, {name: 'a'}, {name: 'a'}]
// Series with the same name between optionA and optionB
// should be mapped.
var idNum=0;do{keyInfo.id="\0"+keyInfo.name+"\0"+idNum++}while(idMap.get(keyInfo.id))}idMap.set(keyInfo.id,item)}}))}function keyExistAndEqual(attr,obj1,obj2){var key1=convertOptionIdName(obj1[attr],null),key2=convertOptionIdName(obj2[attr],null);// See `MappingExistingItem`. `id` and `name` trade string equals to number.
return null!=key1&&null!=key2&&key1===key2}
/**
 * @return return null if not exist.
 */function makeComparableKey(val){return convertOptionIdName(val,"")}function convertOptionIdName(idOrName,defaultValue){return null==idOrName?defaultValue:(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isString */.HD)(idOrName)?idOrName:(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isNumber */.hj)(idOrName)||(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isStringSafe */.cd)(idOrName)?idOrName+"":defaultValue}function isNameSpecified(componentModel){var name=componentModel.name;// Is specified when `indexOf` get -1 or > 0.
return!(!name||!name.indexOf(DUMMY_COMPONENT_NAME_PREFIX))}
/**
 * @public
 * @param {Object} cmptOption
 * @return {boolean}
 */function isComponentIdInternal(cmptOption){return cmptOption&&null!=cmptOption.id&&0===makeComparableKey(cmptOption.id).indexOf(INTERNAL_COMPONENT_ID_PREFIX)}function setComponentTypeToKeyInfo(mappingResult,mainType,componentModelCtor){
// Set mainType and complete subType.
(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6)(mappingResult,(function(item){var newOption=item.newOption;(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isObject */.Kn)(newOption)&&(item.keyInfo.mainType=mainType,item.keyInfo.subType=determineSubType(mainType,newOption,item.existing,componentModelCtor))}))}function determineSubType(mainType,newCmptOption,existComponent,componentModelCtor){var subType=newCmptOption.type?newCmptOption.type:existComponent?existComponent.subType:componentModelCtor.determineSubType(mainType,newCmptOption);// tooltip, markline, markpoint may always has no subType
return subType}
/**
 * A helper for removing duplicate items between batchA and batchB,
 * and in themselves, and categorize by series.
 *
 * @param batchA Like: [{seriesId: 2, dataIndex: [32, 4, 5]}, ...]
 * @param batchB Like: [{seriesId: 2, dataIndex: [32, 4, 5]}, ...]
 * @return result: [resultBatchA, resultBatchB]
 */function compressBatches(batchA,batchB){var mapA={},mapB={};return makeMap(batchA||[],mapA),makeMap(batchB||[],mapB,mapA),[mapToArray(mapA),mapToArray(mapB)];function makeMap(sourceBatch,map,otherMap){for(var i=0,len=sourceBatch.length;i<len;i++){var seriesId=convertOptionIdName(sourceBatch[i].seriesId,null);if(null==seriesId)return;for(var dataIndices=normalizeToArray(sourceBatch[i].dataIndex),otherDataIndices=otherMap&&otherMap[seriesId],j=0,lenj=dataIndices.length;j<lenj;j++){var dataIndex=dataIndices[j];otherDataIndices&&otherDataIndices[dataIndex]?otherDataIndices[dataIndex]=null:(map[seriesId]||(map[seriesId]={}))[dataIndex]=1}}}function mapToArray(map,isData){var result=[];for(var i in map)if(map.hasOwnProperty(i)&&null!=map[i])if(isData)result.push(+i);else{var dataIndices=mapToArray(map[i],!0);dataIndices.length&&result.push({seriesId:i,dataIndex:dataIndices})}return result}}
/**
 * @param payload Contains dataIndex (means rawIndex) / dataIndexInside / name
 *                         each of which can be Array or primary type.
 * @return dataIndex If not found, return undefined/null.
 */function queryDataIndex(data,payload){return null!=payload.dataIndexInside?payload.dataIndexInside:null!=payload.dataIndex?(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isArray */.kJ)(payload.dataIndex)?(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .map */.UI)(payload.dataIndex,(function(value){return data.indexOfRawIndex(value)})):data.indexOfRawIndex(payload.dataIndex):null!=payload.name?(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isArray */.kJ)(payload.name)?(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .map */.UI)(payload.name,(function(value){return data.indexOfName(value)})):data.indexOfName(payload.name):void 0}
/**
 * Enable property storage to any host object.
 * Notice: Serialization is not supported.
 *
 * For example:
 * let inner = zrUitl.makeInner();
 *
 * function some1(hostObj) {
 *      inner(hostObj).someProperty = 1212;
 *      ...
 * }
 * function some2() {
 *      let fields = inner(this);
 *      fields.someProperty1 = 1212;
 *      fields.someProperty2 = 'xx';
 *      ...
 * }
 *
 * @return {Function}
 */function makeInner(){var key="__ec_inner_"+innerUniqueIndex++;return function(hostObj){return hostObj[key]||(hostObj[key]={})}}var innerUniqueIndex=(0,_number_js__WEBPACK_IMPORTED_MODULE_1__/* .getRandomIdBase */.jj)();
/**
 * The same behavior as `component.getReferringComponents`.
 */function parseFinder(ecModel,finderInput,opt){var _a=preParseFinder(finderInput,opt),mainTypeSpecified=_a.mainTypeSpecified,queryOptionMap=_a.queryOptionMap,others=_a.others,result=others,defaultMainType=opt?opt.defaultMainType:null;return!mainTypeSpecified&&defaultMainType&&queryOptionMap.set(defaultMainType,{}),queryOptionMap.each((function(queryOption,mainType){var queryResult=queryReferringComponents(ecModel,mainType,queryOption,{useDefault:defaultMainType===mainType,enableAll:!opt||null==opt.enableAll||opt.enableAll,enableNone:!opt||null==opt.enableNone||opt.enableNone});result[mainType+"Models"]=queryResult.models,result[mainType+"Model"]=queryResult.models[0]})),result}function preParseFinder(finderInput,opt){var finder;if((0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isString */.HD)(finderInput)){var obj={};obj[finderInput+"Index"]=0,finder=obj}else finder=finderInput;var queryOptionMap=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .createHashMap */.kW)(),others={},mainTypeSpecified=!1;return(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6)(finder,(function(value,key){
// Exclude 'dataIndex' and other illgal keys.
if("dataIndex"!==key&&"dataIndexInside"!==key){var parsedKey=key.match(/^(\w+)(Index|Id|Name)$/)||[],mainType=parsedKey[1],queryType=(parsedKey[2]||"").toLowerCase();if(mainType&&queryType&&!(opt&&opt.includeMainTypes&&(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .indexOf */.cq)(opt.includeMainTypes,mainType)<0)){mainTypeSpecified=mainTypeSpecified||!!mainType;var queryOption=queryOptionMap.get(mainType)||queryOptionMap.set(mainType,{});queryOption[queryType]=value}}else others[key]=value})),{mainTypeSpecified:mainTypeSpecified,queryOptionMap:queryOptionMap,others:others}}var SINGLE_REFERRING={useDefault:!0,enableAll:!1,enableNone:!1},MULTIPLE_REFERRING={useDefault:!1,enableAll:!0,enableNone:!0};function queryReferringComponents(ecModel,mainType,userOption,opt){opt=opt||SINGLE_REFERRING;var indexOption=userOption.index,idOption=userOption.id,nameOption=userOption.name,result={models:null,specified:null!=indexOption||null!=idOption||null!=nameOption};if(!result.specified){
// Use the first as default if `useDefault`.
var firstCmpt=void 0;return result.models=opt.useDefault&&(firstCmpt=ecModel.getComponent(mainType))?[firstCmpt]:[],result}return"none"===indexOption||!1===indexOption?((0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .assert */.hu)(opt.enableNone,'`"none"` or `false` is not a valid value on index option.'),result.models=[],result):(// `queryComponents` will return all components if
// both all of index/id/name are null/undefined.
"all"===indexOption&&((0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .assert */.hu)(opt.enableAll,'`"all"` is not a valid value on index option.'),indexOption=idOption=nameOption=null),result.models=ecModel.queryComponents({mainType:mainType,index:indexOption,id:idOption,name:nameOption}),result)}function setAttribute(dom,key,value){dom.setAttribute?dom.setAttribute(key,value):dom[key]=value}function getAttribute(dom,key){return dom.getAttribute?dom.getAttribute(key):dom[key]}function getTooltipRenderMode(renderModeOption){return"auto"===renderModeOption?zrender_lib_core_env_js__WEBPACK_IMPORTED_MODULE_2__/* ["default"] */.Z.domSupported?"html":"richText":renderModeOption||"html"}
/**
 * Group a list by key.
 */
/**
 * Interpolate raw values of a series with percent
 *
 * @param data         data
 * @param labelModel   label model of the text element
 * @param sourceValue  start value. May be null/undefined when init.
 * @param targetValue  end value
 * @param percent      0~1 percentage; 0 uses start value while 1 uses end value
 * @return             interpolated values
 *                     If `sourceValue` and `targetValue` are `number`, return `number`.
 *                     If `sourceValue` and `targetValue` are `string`, return `string`.
 *                     If `sourceValue` and `targetValue` are `(string | number)[]`, return `(string | number)[]`.
 *                     Other cases do not supported.
 */
function interpolateRawValues(data,precision,sourceValue,targetValue,percent){var isAutoPrecision=null==precision||"auto"===precision;if(null==targetValue)return targetValue;if((0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isNumber */.hj)(targetValue)){var value=interpolateNumber(sourceValue||0,targetValue,percent);return(0,_number_js__WEBPACK_IMPORTED_MODULE_1__/* .round */.NM)(value,isAutoPrecision?Math.max((0,_number_js__WEBPACK_IMPORTED_MODULE_1__/* .getPrecision */.p8)(sourceValue||0),(0,_number_js__WEBPACK_IMPORTED_MODULE_1__/* .getPrecision */.p8)(targetValue)):precision)}if((0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isString */.HD)(targetValue))return percent<1?sourceValue:targetValue;for(var interpolated=[],leftArr=sourceValue,rightArr=targetValue,length_1=Math.max(leftArr?leftArr.length:0,rightArr.length),i=0;i<length_1;++i){var info=data.getDimensionInfo(i);// Don't interpolate ordinal dims
if(info&&"ordinal"===info.type)
// In init, there is no `sourceValue`, but should better not to get undefined result.
interpolated[i]=(percent<1&&leftArr?leftArr:rightArr)[i];else{var leftVal=leftArr&&leftArr[i]?leftArr[i]:0,rightVal=rightArr[i];value=interpolateNumber(leftVal,rightVal,percent);interpolated[i]=(0,_number_js__WEBPACK_IMPORTED_MODULE_1__/* .round */.NM)(value,isAutoPrecision?Math.max((0,_number_js__WEBPACK_IMPORTED_MODULE_1__/* .getPrecision */.p8)(leftVal),(0,_number_js__WEBPACK_IMPORTED_MODULE_1__/* .getPrecision */.p8)(rightVal)):precision)}}return interpolated}
/***/},
/***/785669:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */FK:function(){/* binding */return numericToNumber},
/* harmony export */GM:function(){/* binding */return parsePercent},
/* harmony export */HD:function(){/* binding */return getPercentSeats},
/* harmony export */M9:function(){/* binding */return getPixelPrecision},
/* harmony export */NM:function(){/* binding */return round},
/* harmony export */NU:function(){/* binding */return linearMap},
/* harmony export */S$:function(){/* binding */return addSafe},
/* harmony export */Xd:function(){/* binding */return quantity},
/* harmony export */YM:function(){/* binding */return MAX_SAFE_INTEGER},
/* harmony export */dt:function(){/* binding */return asc},
/* harmony export */jj:function(){/* binding */return getRandomIdBase},
/* harmony export */kE:function(){/* binding */return isNumeric},
/* harmony export */kx:function(){/* binding */return nice},
/* harmony export */mW:function(){/* binding */return isRadianAroundZero},
/* harmony export */nR:function(){/* binding */return reformIntervals},
/* harmony export */nl:function(){/* binding */return getLeastCommonMultiple},
/* harmony export */p8:function(){/* binding */return getPrecision},
/* harmony export */sG:function(){/* binding */return parseDate},
/* harmony export */wW:function(){/* binding */return remRadian},
/* harmony export */xW:function(){/* binding */return quantityExponent}
/* harmony export */});
/* unused harmony exports getPrecisionSafe, getPercentWithPrecision, quantile, getGreatestCommonDividor */
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(933051),RADIAN_EPSILON=1e-4,ROUND_SUPPORTED_PRECISION_MAX=20;
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/*
* A third-party license is embedded for some of the code in this file:
* The method "quantile" was copied from "d3.js".
* (See more details in the comment of the method below.)
* The use of the source code of this file is also subject to the terms
* and consitions of the license of "d3.js" (BSD-3Clause, see
* </licenses/LICENSE-d3>).
*/function _trim(str){return str.replace(/^\s+|\s+$/g,"")}
/**
 * Linear mapping a value from domain to range
 * @param  val
 * @param  domain Domain extent domain[0] can be bigger than domain[1]
 * @param  range  Range extent range[0] can be bigger than range[1]
 * @param  clamp Default to be false
 */function linearMap(val,domain,range,clamp){var d0=domain[0],d1=domain[1],r0=range[0],r1=range[1],subDomain=d1-d0,subRange=r1-r0;if(0===subDomain)return 0===subRange?r0:(r0+r1)/2;// Avoid accuracy problem in edge, such as
// 146.39 - 62.83 === 83.55999999999999.
// See echarts/test/ut/spec/util/number.js#linearMap#accuracyError
// It is a little verbose for efficiency considering this method
// is a hotspot.
if(clamp)if(subDomain>0){if(val<=d0)return r0;if(val>=d1)return r1}else{if(val>=d0)return r0;if(val<=d1)return r1}else{if(val===d0)return r0;if(val===d1)return r1}return(val-d0)/subDomain*subRange+r0}
/**
 * Convert a percent string to absolute number.
 * Returns NaN if percent is not a valid string or number
 */function parsePercent(percent,all){switch(percent){case"center":case"middle":percent="50%";break;case"left":case"top":percent="0%";break;case"right":case"bottom":percent="100%";break}return zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isString */.HD(percent)?_trim(percent).match(/%$/)?parseFloat(percent)/100*all:parseFloat(percent):null==percent?NaN:+percent}function round(x,precision,returnStr){return null==precision&&(precision=10),// Avoid range error
precision=Math.min(Math.max(0,precision),ROUND_SUPPORTED_PRECISION_MAX),// PENDING: 1.005.toFixed(2) is '1.00' rather than '1.01'
x=(+x).toFixed(precision),returnStr?x:+x}
/**
 * Inplacd asc sort arr.
 * The input arr will be modified.
 */function asc(arr){return arr.sort((function(a,b){return a-b})),arr}
/**
 * Get precision.
 */function getPrecision(val){if(val=+val,isNaN(val))return 0;// It is much faster than methods converting number to string as follows
//      let tmp = val.toString();
//      return tmp.length - 1 - tmp.indexOf('.');
// especially when precision is low
// Notice:
// (1) If the loop count is over about 20, it is slower than `getPrecisionSafe`.
//     (see https://jsbench.me/2vkpcekkvw/1)
// (2) If the val is less than for example 1e-15, the result may be incorrect.
//     (see test/ut/spec/util/number.test.ts `getPrecision_equal_random`)
if(val>1e-14)for(var e=1,i=0;i<15;i++,e*=10)if(Math.round(val*e)/e===val)return i;return getPrecisionSafe(val)}
/**
 * Get precision with slow but safe method
 */function getPrecisionSafe(val){
// toLowerCase for: '3.4E-12'
var str=val.toString().toLowerCase(),eIndex=str.indexOf("e"),exp=eIndex>0?+str.slice(eIndex+1):0,significandPartLen=eIndex>0?eIndex:str.length,dotIndex=str.indexOf("."),decimalPartLen=dotIndex<0?0:significandPartLen-1-dotIndex;// Consider scientific notation: '3.4e-12' '3.4e+12'
return Math.max(0,decimalPartLen-exp)}
/**
 * Minimal dicernible data precisioin according to a single pixel.
 */function getPixelPrecision(dataExtent,pixelExtent){var log=Math.log,LN10=Math.LN10,dataQuantity=Math.floor(log(dataExtent[1]-dataExtent[0])/LN10),sizeQuantity=Math.round(log(Math.abs(pixelExtent[1]-pixelExtent[0]))/LN10),precision=Math.min(Math.max(-dataQuantity+sizeQuantity,0),20);return isFinite(precision)?precision:20}
/**
 * Get a data of given precision, assuring the sum of percentages
 * in valueList is 1.
 * The largest remainder method is used.
 * https://en.wikipedia.org/wiki/Largest_remainder_method
 *
 * @param valueList a list of all data
 * @param idx index of the data to be processed in valueList
 * @param precision integer number showing digits of precision
 * @return percent ranging from 0 to 100
 */
/**
 * Get a data of given precision, assuring the sum of percentages
 * in valueList is 1.
 * The largest remainder method is used.
 * https://en.wikipedia.org/wiki/Largest_remainder_method
 *
 * @param valueList a list of all data
 * @param precision integer number showing digits of precision
 * @return {Array<number>}
 */
function getPercentSeats(valueList,precision){var sum=zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .reduce */.u4(valueList,(function(acc,val){return acc+(isNaN(val)?0:val)}),0);if(0===sum)return[];var digits=Math.pow(10,precision),votesPerQuota=zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .map */.UI(valueList,(function(val){return(isNaN(val)?0:val)/sum*digits*100})),targetSeats=100*digits,seats=zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .map */.UI(votesPerQuota,(function(votes){
// Assign automatic seats.
return Math.floor(votes)})),currentSum=zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .reduce */.u4(seats,(function(acc,val){return acc+val}),0),remainder=zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .map */.UI(votesPerQuota,(function(votes,idx){return votes-seats[idx]}));// Has remainding votes.
while(currentSum<targetSeats){for(
// Find next largest remainder.
var max=Number.NEGATIVE_INFINITY,maxId=null,i=0,len=remainder.length;i<len;++i)remainder[i]>max&&(max=remainder[i],maxId=i);// Add a vote to max remainder.
++seats[maxId],remainder[maxId]=0,++currentSum}return zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .map */.UI(seats,(function(seat){return seat/digits}))}
/**
 * Solve the floating point adding problem like 0.1 + 0.2 === 0.30000000000000004
 * See <http://0.30000000000000004.com/>
 */function addSafe(val0,val1){var maxPrecision=Math.max(getPrecision(val0),getPrecision(val1)),sum=val0+val1;// const multiplier = Math.pow(10, maxPrecision);
// return (Math.round(val0 * multiplier) + Math.round(val1 * multiplier)) / multiplier;
// // PENDING: support more?
return maxPrecision>ROUND_SUPPORTED_PRECISION_MAX?sum:round(sum,maxPrecision)}// Number.MAX_SAFE_INTEGER, ie do not support.
var MAX_SAFE_INTEGER=9007199254740991;
/**
 * To 0 - 2 * PI, considering negative radian.
 */function remRadian(radian){var pi2=2*Math.PI;return(radian%pi2+pi2)%pi2}
/**
 * @param {type} radian
 * @return {boolean}
 */function isRadianAroundZero(val){return val>-RADIAN_EPSILON&&val<RADIAN_EPSILON}// eslint-disable-next-line
var TIME_REG=/^(?:(\d{4})(?:[-\/](\d{1,2})(?:[-\/](\d{1,2})(?:[T ](\d{1,2})(?::(\d{1,2})(?::(\d{1,2})(?:[.,](\d+))?)?)?(Z|[\+\-]\d\d:?\d\d)?)?)?)?)?$/;// jshint ignore:line
/**
 * @param value valid type: number | string | Date, otherwise return `new Date(NaN)`
 *   These values can be accepted:
 *   + An instance of Date, represent a time in its own time zone.
 *   + Or string in a subset of ISO 8601, only including:
 *     + only year, month, date: '2012-03', '2012-03-01', '2012-03-01 05', '2012-03-01 05:06',
 *     + separated with T or space: '2012-03-01T12:22:33.123', '2012-03-01 12:22:33.123',
 *     + time zone: '2012-03-01T12:22:33Z', '2012-03-01T12:22:33+8000', '2012-03-01T12:22:33-05:00',
 *     all of which will be treated as local time if time zone is not specified
 *     (see <https://momentjs.com/>).
 *   + Or other string format, including (all of which will be treated as local time):
 *     '2012', '2012-3-1', '2012/3/1', '2012/03/01',
 *     '2009/6/12 2:00', '2009/6/12 2:05:08', '2009/6/12 2:05:08.123'
 *   + a timestamp, which represent a time in UTC.
 * @return date Never be null/undefined. If invalid, return `new Date(NaN)`.
 */function parseDate(value){if(value instanceof Date)return value;if(zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isString */.HD(value)){
// Different browsers parse date in different way, so we parse it manually.
// Some other issues:
// new Date('1970-01-01') is UTC,
// new Date('1970/01/01') and new Date('1970-1-01') is local.
// See issue #3623
var match=TIME_REG.exec(value);if(!match)
// return Invalid Date.
return new Date(NaN);// Use local time when no timezone offset is specified.
if(match[8]){var hour=+match[4]||0;return"Z"!==match[8].toUpperCase()&&(hour-=+match[8].slice(0,3)),new Date(Date.UTC(+match[1],+(match[2]||1)-1,+match[3]||1,hour,+(match[5]||0),+match[6]||0,match[7]?+match[7].substring(0,3):0))}
// match[n] can only be string or undefined.
// But take care of '12' + 1 => '121'.
return new Date(+match[1],+(match[2]||1)-1,+match[3]||1,+match[4]||0,+(match[5]||0),+match[6]||0,match[7]?+match[7].substring(0,3):0)}return null==value?new Date(NaN):new Date(Math.round(value))}
/**
 * Quantity of a number. e.g. 0.1, 1, 10, 100
 *
 * @param val
 * @return
 */function quantity(val){return Math.pow(10,quantityExponent(val))}
/**
 * Exponent of the quantity of a number
 * e.g., 1234 equals to 1.234*10^3, so quantityExponent(1234) is 3
 *
 * @param val non-negative value
 * @return
 */function quantityExponent(val){if(0===val)return 0;var exp=Math.floor(Math.log(val)/Math.LN10);
/**
   * exp is expected to be the rounded-down result of the base-10 log of val.
   * But due to the precision loss with Math.log(val), we need to restore it
   * using 10^exp to make sure we can get val back from exp. #11249
   */return val/Math.pow(10,exp)>=10&&exp++,exp}
/**
 * find a “nice” number approximately equal to x. Round the number if round = true,
 * take ceiling if round = false. The primary observation is that the “nicest”
 * numbers in decimal are 1, 2, and 5, and all power-of-ten multiples of these numbers.
 *
 * See "Nice Numbers for Graph Labels" of Graphic Gems.
 *
 * @param  val Non-negative value.
 * @param  round
 * @return Niced number
 */function nice(val,round){var nf,exponent=quantityExponent(val),exp10=Math.pow(10,exponent),f=val/exp10;// Fix 3 * 0.1 === 0.30000000000000004 issue (see IEEE 754).
// 20 is the uppper bound of toFixed.
return nf=round?f<1.5?1:f<2.5?2:f<4?3:f<7?5:10:f<1?1:f<2?2:f<3?3:f<5?5:10,val=nf*exp10,exponent>=-20?+val.toFixed(exponent<0?-exponent:0):val}
/**
 * This code was copied from "d3.js"
 * <https://github.com/d3/d3/blob/9cc9a875e636a1dcf36cc1e07bdf77e1ad6e2c74/src/arrays/quantile.js>.
 * See the license statement at the head of this file.
 * @param ascArr
 */
/**
 * Order intervals asc, and split them when overlap.
 * expect(numberUtil.reformIntervals([
 *     {interval: [18, 62], close: [1, 1]},
 *     {interval: [-Infinity, -70], close: [0, 0]},
 *     {interval: [-70, -26], close: [1, 1]},
 *     {interval: [-26, 18], close: [1, 1]},
 *     {interval: [62, 150], close: [1, 1]},
 *     {interval: [106, 150], close: [1, 1]},
 *     {interval: [150, Infinity], close: [0, 0]}
 * ])).toEqual([
 *     {interval: [-Infinity, -70], close: [0, 0]},
 *     {interval: [-70, -26], close: [1, 1]},
 *     {interval: [-26, 18], close: [0, 1]},
 *     {interval: [18, 62], close: [0, 1]},
 *     {interval: [62, 150], close: [0, 1]},
 *     {interval: [150, Infinity], close: [0, 0]}
 * ]);
 * @param list, where `close` mean open or close
 *        of the interval, and Infinity can be used.
 * @return The origin list, which has been reformed.
 */
function reformIntervals(list){list.sort((function(a,b){return littleThan(a,b,0)?-1:1}));for(var curr=-1/0,currClose=1,i=0;i<list.length;){for(var interval=list[i].interval,close_1=list[i].close,lg=0;lg<2;lg++)interval[lg]<=curr&&(interval[lg]=curr,close_1[lg]=lg?1:1-currClose),curr=interval[lg],currClose=close_1[lg];interval[0]===interval[1]&&close_1[0]*close_1[1]!==1?list.splice(i,1):i++}return list;function littleThan(a,b,lg){return a.interval[lg]<b.interval[lg]||a.interval[lg]===b.interval[lg]&&(a.close[lg]-b.close[lg]===(lg?-1:1)||!lg&&littleThan(a,b,1))}}
/**
 * [Numeric is defined as]:
 *     `parseFloat(val) == val`
 * For example:
 * numeric:
 *     typeof number except NaN, '-123', '123', '2e3', '-2e3', '011', 'Infinity', Infinity,
 *     and they rounded by white-spaces or line-terminal like ' -123 \n ' (see es spec)
 * not-numeric:
 *     null, undefined, [], {}, true, false, 'NaN', NaN, '123ab',
 *     empty string, string with only white-spaces or line-terminal (see es spec),
 *     0x12, '0x12', '-0x12', 012, '012', '-012',
 *     non-string, ...
 *
 * @test See full test cases in `test/ut/spec/util/number.js`.
 * @return Must be a typeof number. If not numeric, return NaN.
 */function numericToNumber(val){var valFloat=parseFloat(val);return valFloat==val&&(0!==valFloat||!zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isString */.HD(val)||val.indexOf("x")<=0)?valFloat:NaN}
/**
 * Definition of "numeric": see `numericToNumber`.
 */function isNumeric(val){return!isNaN(numericToNumber(val))}
/**
 * Use random base to prevent users hard code depending on
 * this auto generated marker id.
 * @return An positive integer.
 */function getRandomIdBase(){return Math.round(9*Math.random())}
/**
 * Get the greatest common divisor.
 *
 * @param {number} a one number
 * @param {number} b the other number
 */function getGreatestCommonDividor(a,b){return 0===b?a:getGreatestCommonDividor(b,a%b)}
/**
 * Get the least common multiple.
 *
 * @param {number} a one number
 * @param {number} b the other number
 */function getLeastCommonMultiple(a,b){return null==a?b:null==b?a:a*b/getGreatestCommonDividor(a,b)}
/***/},
/***/839529:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony import */var tslib__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(570655),_graphic_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(696997),SausageShape=
/** @class */
function(){function SausageShape(){this.cx=0,this.cy=0,this.r0=0,this.r=0,this.startAngle=0,this.endAngle=2*Math.PI,this.clockwise=!0}return SausageShape}(),SausagePath=
/** @class */
function(_super){function SausagePath(opts){var _this=_super.call(this,opts)||this;return _this.type="sausage",_this}return(0,tslib__WEBPACK_IMPORTED_MODULE_0__/* .__extends */.ZT)(SausagePath,_super),SausagePath.prototype.getDefaultShape=function(){return new SausageShape},SausagePath.prototype.buildPath=function(ctx,shape){var cx=shape.cx,cy=shape.cy,r0=Math.max(shape.r0||0,0),r=Math.max(shape.r,0),dr=.5*(r-r0),rCenter=r0+dr,startAngle=shape.startAngle,endAngle=shape.endAngle,clockwise=shape.clockwise,PI2=2*Math.PI,lessThanCircle=clockwise?endAngle-startAngle<PI2:startAngle-endAngle<PI2;lessThanCircle||(
// Normalize angles
startAngle=endAngle-(clockwise?PI2:-PI2));var unitStartX=Math.cos(startAngle),unitStartY=Math.sin(startAngle),unitEndX=Math.cos(endAngle),unitEndY=Math.sin(endAngle);lessThanCircle?(ctx.moveTo(unitStartX*r0+cx,unitStartY*r0+cy),ctx.arc(unitStartX*rCenter+cx,unitStartY*rCenter+cy,dr,-Math.PI+startAngle,startAngle,!clockwise)):ctx.moveTo(unitStartX*r+cx,unitStartY*r+cy),ctx.arc(cx,cy,r,startAngle,endAngle,!clockwise),ctx.arc(unitEndX*rCenter+cx,unitEndY*rCenter+cy,dr,endAngle-2*Math.PI,endAngle-Math.PI,!clockwise),0!==r0&&ctx.arc(cx,cy,r0,endAngle,startAngle,clockwise)},SausagePath}(_graphic_js__WEBPACK_IMPORTED_MODULE_1__/* ["default"] */.ZP);
/* harmony import */
/* harmony default export */__webpack_exports__.Z=SausagePath},
/***/726357:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */$l:function(){/* binding */return handleGlobalMouseOverForHighDown},
/* harmony export */Av:function(){/* binding */return isHighDownDispatcher},
/* harmony export */C5:function(){/* binding */return getAllSelectedIndices},
/* harmony export */CX:function(){/* binding */return HOVER_STATE_BLUR},
/* harmony export */Gl:function(){/* binding */return setStatesFlag},
/* harmony export */Hg:function(){/* binding */return SELECT_ACTION_TYPE},
/* harmony export */Ib:function(){/* binding */return enableHoverFocus},
/* harmony export */JQ:function(){/* binding */return UNSELECT_ACTION_TYPE},
/* harmony export */Ki:function(){/* binding */return HIGHLIGHT_ACTION_TYPE},
/* harmony export */L1:function(){/* binding */return SPECIAL_STATES},
/* harmony export */MA:function(){/* binding */return setDefaultStateProxy},
/* harmony export */Mh:function(){/* binding */return leaveEmphasis},
/* harmony export */Nj:function(){/* binding */return setAsHighDownDispatcher},
/* harmony export */RW:function(){/* binding */return getHighlightDigit},
/* harmony export */SJ:function(){/* binding */return leaveSelect},
/* harmony export */SX:function(){/* binding */return enterBlur},
/* harmony export */T5:function(){/* binding */return allLeaveBlur},
/* harmony export */UL:function(){/* binding */return blurSeriesFromHighlightPayload},
/* harmony export */VP:function(){/* binding */return leaveBlur},
/* harmony export */WO:function(){/* binding */return setStatesStylesFromModel},
/* harmony export */XX:function(){/* binding */return enterSelect},
/* harmony export */aG:function(){/* binding */return isSelectChangePayload},
/* harmony export */ci:function(){/* binding */return updateSeriesElementSelection},
/* harmony export */e9:function(){/* binding */return savePathStates},
/* harmony export */fD:function(){/* binding */return enterEmphasis},
/* harmony export */iK:function(){/* binding */return TOGGLE_SELECT_ACTION_TYPE},
/* harmony export */k5:function(){/* binding */return toggleHoverEmphasis},
/* harmony export */oJ:function(){/* binding */return findComponentHighDownDispatchers},
/* harmony export */og:function(){/* binding */return toggleSelectionFromPayload},
/* harmony export */qc:function(){/* binding */return DISPLAY_STATES},
/* harmony export */vF:function(){/* binding */return enableHoverEmphasis},
/* harmony export */wU:function(){/* binding */return HOVER_STATE_EMPHASIS},
/* harmony export */xp:function(){/* binding */return isHighDownPayload},
/* harmony export */xr:function(){/* binding */return handleGlobalMouseOutForHighDown},
/* harmony export */yx:function(){/* binding */return DOWNPLAY_ACTION_TYPE},
/* harmony export */zI:function(){/* binding */return blurComponent},
/* harmony export */zr:function(){/* binding */return Z2_EMPHASIS_LIFT}
/* harmony export */});
/* unused harmony exports HOVER_STATE_NORMAL, Z2_SELECT_LIFT, clearStates, enterEmphasisWhenMouseOver, leaveEmphasisWhenMouseOut, blurSeries, disableHoverEmphasis, enableComponentHighDownFeatures */
/* harmony import */var zrender_lib_core_LRU_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(392528),zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(933051),_innerStore_js__WEBPACK_IMPORTED_MODULE_5__=__webpack_require__(30106),zrender_lib_tool_color_js__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(321092),_model_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(232234),zrender_lib_graphic_Path_js__WEBPACK_IMPORTED_MODULE_4__=__webpack_require__(696997),_highlightNextDigit=1,_highlightKeyMap={},getSavedStates=(0,_model_js__WEBPACK_IMPORTED_MODULE_0__/* .makeInner */.Yf)(),getComponentStates=(0,_model_js__WEBPACK_IMPORTED_MODULE_0__/* .makeInner */.Yf)(),HOVER_STATE_NORMAL=0,HOVER_STATE_BLUR=1,HOVER_STATE_EMPHASIS=2,SPECIAL_STATES=["emphasis","blur","select"],DISPLAY_STATES=["normal","emphasis","blur","select"],Z2_EMPHASIS_LIFT=10,Z2_SELECT_LIFT=9,HIGHLIGHT_ACTION_TYPE="highlight",DOWNPLAY_ACTION_TYPE="downplay",SELECT_ACTION_TYPE="select",UNSELECT_ACTION_TYPE="unselect",TOGGLE_SELECT_ACTION_TYPE="toggleSelect";
/* harmony import */function hasFillOrStroke(fillOrStroke){return null!=fillOrStroke&&"none"!==fillOrStroke}// Most lifted color are duplicated.
var liftedColorCache=new zrender_lib_core_LRU_js__WEBPACK_IMPORTED_MODULE_1__/* ["default"] */.ZP(100);function liftColor(color){if((0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .isString */.HD)(color)){var liftedColor=liftedColorCache.get(color);return liftedColor||(liftedColor=zrender_lib_tool_color_js__WEBPACK_IMPORTED_MODULE_3__/* .lift */.xb(color,-.1),liftedColorCache.put(color,liftedColor)),liftedColor}// Change nothing.
if((0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .isGradientObject */.Qq)(color)){var ret=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .extend */.l7)({},color);return ret.colorStops=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .map */.UI)(color.colorStops,(function(stop){return{offset:stop.offset,color:zrender_lib_tool_color_js__WEBPACK_IMPORTED_MODULE_3__/* .lift */.xb(stop.color,-.1)}})),ret}return color}function doChangeHoverState(el,stateName,hoverStateEnum){el.onHoverStateChange&&(el.hoverState||0)!==hoverStateEnum&&el.onHoverStateChange(stateName),el.hoverState=hoverStateEnum}function singleEnterEmphasis(el){
// Only mark the flag.
// States will be applied in the echarts.ts in next frame.
doChangeHoverState(el,"emphasis",HOVER_STATE_EMPHASIS)}function singleLeaveEmphasis(el){
// Only mark the flag.
// States will be applied in the echarts.ts in next frame.
el.hoverState===HOVER_STATE_EMPHASIS&&doChangeHoverState(el,"normal",HOVER_STATE_NORMAL)}function singleEnterBlur(el){doChangeHoverState(el,"blur",HOVER_STATE_BLUR)}function singleLeaveBlur(el){el.hoverState===HOVER_STATE_BLUR&&doChangeHoverState(el,"normal",HOVER_STATE_NORMAL)}function singleEnterSelect(el){el.selected=!0}function singleLeaveSelect(el){el.selected=!1}function updateElementState(el,updater,commonParam){updater(el,commonParam)}function traverseUpdateState(el,updater,commonParam){updateElementState(el,updater,commonParam),el.isGroup&&el.traverse((function(child){updateElementState(child,updater,commonParam)}))}function setStatesFlag(el,stateName){switch(stateName){case"emphasis":el.hoverState=HOVER_STATE_EMPHASIS;break;case"normal":el.hoverState=HOVER_STATE_NORMAL;break;case"blur":el.hoverState=HOVER_STATE_BLUR;break;case"select":el.selected=!0}}
/**
 * If we reuse elements when rerender.
 * DON'T forget to clearStates before we update the style and shape.
 * Or we may update on the wrong state instead of normal state.
 */function getFromStateStyle(el,props,toStateName,defaultValue){for(var style=el.style,fromState={},i=0;i<props.length;i++){var propName=props[i],val=style[propName];fromState[propName]=null==val?defaultValue&&defaultValue[propName]:val}for(i=0;i<el.animators.length;i++){var animator=el.animators[i];animator.__fromStateTransition&&animator.__fromStateTransition.indexOf(toStateName)<0&&"style"===animator.targetName&&animator.saveTo(fromState,props)}return fromState}function createEmphasisDefaultState(el,stateName,targetStates,state){var hasSelect=targetStates&&(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .indexOf */.cq)(targetStates,"select")>=0,cloned=!1;if(el instanceof zrender_lib_graphic_Path_js__WEBPACK_IMPORTED_MODULE_4__/* ["default"] */.ZP){var store=getSavedStates(el),fromFill=hasSelect&&store.selectFill||store.normalFill,fromStroke=hasSelect&&store.selectStroke||store.normalStroke;if(hasFillOrStroke(fromFill)||hasFillOrStroke(fromStroke)){state=state||{};var emphasisStyle=state.style||{};// inherit case
"inherit"===emphasisStyle.fill?(cloned=!0,state=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .extend */.l7)({},state),emphasisStyle=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .extend */.l7)({},emphasisStyle),emphasisStyle.fill=fromFill):!hasFillOrStroke(emphasisStyle.fill)&&hasFillOrStroke(fromFill)?(cloned=!0,// Not modify the original value.
state=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .extend */.l7)({},state),emphasisStyle=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .extend */.l7)({},emphasisStyle),// Already being applied 'emphasis'. DON'T lift color multiple times.
emphasisStyle.fill=liftColor(fromFill)):!hasFillOrStroke(emphasisStyle.stroke)&&hasFillOrStroke(fromStroke)&&(cloned||(state=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .extend */.l7)({},state),emphasisStyle=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .extend */.l7)({},emphasisStyle)),emphasisStyle.stroke=liftColor(fromStroke)),state.style=emphasisStyle}}if(state&&null==state.z2){cloned||(state=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .extend */.l7)({},state));var z2EmphasisLift=el.z2EmphasisLift;state.z2=el.z2+(null!=z2EmphasisLift?z2EmphasisLift:Z2_EMPHASIS_LIFT)}return state}function createSelectDefaultState(el,stateName,state){
// const hasSelect = indexOf(el.currentStates, stateName) >= 0;
if(state&&null==state.z2){state=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .extend */.l7)({},state);var z2SelectLift=el.z2SelectLift;state.z2=el.z2+(null!=z2SelectLift?z2SelectLift:Z2_SELECT_LIFT)}return state}function createBlurDefaultState(el,stateName,state){var hasBlur=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .indexOf */.cq)(el.currentStates,stateName)>=0,currentOpacity=el.style.opacity,fromState=hasBlur?null:getFromStateStyle(el,["opacity"],stateName,{opacity:1});state=state||{};var blurStyle=state.style||{};return null==blurStyle.opacity&&(
// clone state
state=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .extend */.l7)({},state),blurStyle=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .extend */.l7)({
// Already being applied 'emphasis'. DON'T mul opacity multiple times.
opacity:hasBlur?currentOpacity:.1*fromState.opacity},blurStyle),state.style=blurStyle),state}function elementStateProxy(stateName,targetStates){var state=this.states[stateName];if(this.style){if("emphasis"===stateName)return createEmphasisDefaultState(this,stateName,targetStates,state);if("blur"===stateName)return createBlurDefaultState(this,stateName,state);if("select"===stateName)return createSelectDefaultState(this,stateName,state)}return state}
/**
 * Set hover style (namely "emphasis style") of element.
 * @param el Should not be `zrender/graphic/Group`.
 * @param focus 'self' | 'selfInSeries' | 'series'
 */function setDefaultStateProxy(el){el.stateProxy=elementStateProxy;var textContent=el.getTextContent(),textGuide=el.getTextGuideLine();textContent&&(textContent.stateProxy=elementStateProxy),textGuide&&(textGuide.stateProxy=elementStateProxy)}function enterEmphasisWhenMouseOver(el,e){!shouldSilent(el,e)&&!el.__highByOuter&&traverseUpdateState(el,singleEnterEmphasis)}function leaveEmphasisWhenMouseOut(el,e){!shouldSilent(el,e)&&!el.__highByOuter&&traverseUpdateState(el,singleLeaveEmphasis)}function enterEmphasis(el,highlightDigit){el.__highByOuter|=1<<(highlightDigit||0),traverseUpdateState(el,singleEnterEmphasis)}function leaveEmphasis(el,highlightDigit){!(el.__highByOuter&=~(1<<(highlightDigit||0)))&&traverseUpdateState(el,singleLeaveEmphasis)}function enterBlur(el){traverseUpdateState(el,singleEnterBlur)}function leaveBlur(el){traverseUpdateState(el,singleLeaveBlur)}function enterSelect(el){traverseUpdateState(el,singleEnterSelect)}function leaveSelect(el){traverseUpdateState(el,singleLeaveSelect)}function shouldSilent(el,e){return el.__highDownSilentOnTouch&&e.zrByTouch}function allLeaveBlur(api){var model=api.getModel(),leaveBlurredSeries=[],allComponentViews=[];model.eachComponent((function(componentType,componentModel){var componentStates=getComponentStates(componentModel),isSeries="series"===componentType,view=isSeries?api.getViewOfSeriesModel(componentModel):api.getViewOfComponentModel(componentModel);!isSeries&&allComponentViews.push(view),componentStates.isBlured&&(
// Leave blur anyway
view.group.traverse((function(child){singleLeaveBlur(child)})),isSeries&&leaveBlurredSeries.push(componentModel)),componentStates.isBlured=!1})),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .each */.S6)(allComponentViews,(function(view){view&&view.toggleBlurSeries&&view.toggleBlurSeries(leaveBlurredSeries,!1,model)}))}function blurSeries(targetSeriesIndex,focus,blurScope,api){var ecModel=api.getModel();function leaveBlurOfIndices(data,dataIndices){for(var i=0;i<dataIndices.length;i++){var itemEl=data.getItemGraphicEl(dataIndices[i]);itemEl&&leaveBlur(itemEl)}}if(blurScope=blurScope||"coordinateSystem",null!=targetSeriesIndex&&focus&&"none"!==focus){var targetSeriesModel=ecModel.getSeriesByIndex(targetSeriesIndex),targetCoordSys=targetSeriesModel.coordinateSystem;targetCoordSys&&targetCoordSys.master&&(targetCoordSys=targetCoordSys.master);var blurredSeries=[];ecModel.eachSeries((function(seriesModel){var sameSeries=targetSeriesModel===seriesModel,coordSys=seriesModel.coordinateSystem;coordSys&&coordSys.master&&(coordSys=coordSys.master);var sameCoordSys=coordSys&&targetCoordSys?coordSys===targetCoordSys:sameSeries;// If there is no coordinate system. use sameSeries instead.
if(!(// Not blur other series if blurScope series
"series"===blurScope&&!sameSeries||"coordinateSystem"===blurScope&&!sameCoordSys||"series"===focus&&sameSeries)){var view=api.getViewOfSeriesModel(seriesModel);if(view.group.traverse((function(child){
// For the elements that have been triggered by other components,
// and are still required to be highlighted,
// because the current is directly forced to blur the element,
// it will cause the focus self to be unable to highlight, so skip the blur of this element.
child.__highByOuter&&sameSeries&&"self"===focus||singleEnterBlur(child)})),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .isArrayLike */.zG)(focus))leaveBlurOfIndices(seriesModel.getData(),focus);else if((0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .isObject */.Kn)(focus))for(var dataTypes=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .keys */.XP)(focus),d=0;d<dataTypes.length;d++)leaveBlurOfIndices(seriesModel.getData(dataTypes[d]),focus[dataTypes[d]]);blurredSeries.push(seriesModel),getComponentStates(seriesModel).isBlured=!0}})),ecModel.eachComponent((function(componentType,componentModel){if("series"!==componentType){var view=api.getViewOfComponentModel(componentModel);view&&view.toggleBlurSeries&&view.toggleBlurSeries(blurredSeries,!0,ecModel)}}))}}function blurComponent(componentMainType,componentIndex,api){if(null!=componentMainType&&null!=componentIndex){var componentModel=api.getModel().getComponent(componentMainType,componentIndex);if(componentModel){getComponentStates(componentModel).isBlured=!0;var view=api.getViewOfComponentModel(componentModel);view&&view.focusBlurEnabled&&view.group.traverse((function(child){singleEnterBlur(child)}))}}}function blurSeriesFromHighlightPayload(seriesModel,payload,api){var seriesIndex=seriesModel.seriesIndex,data=seriesModel.getData(payload.dataType);if(data){var dataIndex=(0,_model_js__WEBPACK_IMPORTED_MODULE_0__/* .queryDataIndex */.gO)(data,payload);// Pick the first one if there is multiple/none exists.
dataIndex=((0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .isArray */.kJ)(dataIndex)?dataIndex[0]:dataIndex)||0;var el=data.getItemGraphicEl(dataIndex);if(!el){var count=data.count(),current=0;// If data on dataIndex is NaN.
while(!el&&current<count)el=data.getItemGraphicEl(current++)}if(el){var ecData=(0,_innerStore_js__WEBPACK_IMPORTED_MODULE_5__/* .getECData */.A)(el);blurSeries(seriesIndex,ecData.focus,ecData.blurScope,api)}else{
// If there is no element put on the data. Try getting it from raw option
// TODO Should put it on seriesModel?
var focus_1=seriesModel.get(["emphasis","focus"]),blurScope=seriesModel.get(["emphasis","blurScope"]);null!=focus_1&&blurSeries(seriesIndex,focus_1,blurScope,api)}}}function findComponentHighDownDispatchers(componentMainType,componentIndex,name,api){var ret={focusSelf:!1,dispatchers:null};if(null==componentMainType||"series"===componentMainType||null==componentIndex||null==name)return ret;var componentModel=api.getModel().getComponent(componentMainType,componentIndex);if(!componentModel)return ret;var view=api.getViewOfComponentModel(componentModel);if(!view||!view.findHighDownDispatchers)return ret;for(var focusSelf,dispatchers=view.findHighDownDispatchers(name),i=0// At presnet, the component (like Geo) only blur inside itself.
// So we do not use `blurScope` in component.
;i<dispatchers.length;i++)if("self"===(0,_innerStore_js__WEBPACK_IMPORTED_MODULE_5__/* .getECData */.A)(dispatchers[i]).focus){focusSelf=!0;break}return{focusSelf:focusSelf,dispatchers:dispatchers}}function handleGlobalMouseOverForHighDown(dispatcher,e,api){var ecData=(0,_innerStore_js__WEBPACK_IMPORTED_MODULE_5__/* .getECData */.A)(dispatcher),_a=findComponentHighDownDispatchers(ecData.componentMainType,ecData.componentIndex,ecData.componentHighDownName,api),dispatchers=_a.dispatchers,focusSelf=_a.focusSelf;// If `findHighDownDispatchers` is supported on the component,
// highlight/downplay elements with the same name.
dispatchers?(focusSelf&&blurComponent(ecData.componentMainType,ecData.componentIndex,api),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .each */.S6)(dispatchers,(function(dispatcher){return enterEmphasisWhenMouseOver(dispatcher,e)}))):(
// Try blur all in the related series. Then emphasis the hoverred.
// TODO. progressive mode.
blurSeries(ecData.seriesIndex,ecData.focus,ecData.blurScope,api),"self"===ecData.focus&&blurComponent(ecData.componentMainType,ecData.componentIndex,api),// Other than series, component that not support `findHighDownDispatcher` will
// also use it. But in this case, highlight/downplay are only supported in
// mouse hover but not in dispatchAction.
enterEmphasisWhenMouseOver(dispatcher,e))}function handleGlobalMouseOutForHighDown(dispatcher,e,api){allLeaveBlur(api);var ecData=(0,_innerStore_js__WEBPACK_IMPORTED_MODULE_5__/* .getECData */.A)(dispatcher),dispatchers=findComponentHighDownDispatchers(ecData.componentMainType,ecData.componentIndex,ecData.componentHighDownName,api).dispatchers;dispatchers?(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .each */.S6)(dispatchers,(function(dispatcher){return leaveEmphasisWhenMouseOut(dispatcher,e)})):leaveEmphasisWhenMouseOut(dispatcher,e)}function toggleSelectionFromPayload(seriesModel,payload,api){if(isSelectChangePayload(payload)){var dataType=payload.dataType,data=seriesModel.getData(dataType),dataIndex=(0,_model_js__WEBPACK_IMPORTED_MODULE_0__/* .queryDataIndex */.gO)(data,payload);(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .isArray */.kJ)(dataIndex)||(dataIndex=[dataIndex]),seriesModel[payload.type===TOGGLE_SELECT_ACTION_TYPE?"toggleSelect":payload.type===SELECT_ACTION_TYPE?"select":"unselect"](dataIndex,dataType)}}function updateSeriesElementSelection(seriesModel){var allData=seriesModel.getAllData();(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .each */.S6)(allData,(function(_a){var data=_a.data,type=_a.type;data.eachItemGraphicEl((function(el,idx){seriesModel.isSelected(idx,type)?enterSelect(el):leaveSelect(el)}))}))}function getAllSelectedIndices(ecModel){var ret=[];return ecModel.eachSeries((function(seriesModel){var allData=seriesModel.getAllData();(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_2__/* .each */.S6)(allData,(function(_a){_a.data;var type=_a.type,dataIndices=seriesModel.getSelectedDataIndices();if(dataIndices.length>0){var item={dataIndex:dataIndices,seriesIndex:seriesModel.seriesIndex};null!=type&&(item.dataType=type),ret.push(item)}}))})),ret}
/**
 * Enable the function that mouseover will trigger the emphasis state.
 *
 * NOTE:
 * This function should be used on the element with dataIndex, seriesIndex.
 *
 */function enableHoverEmphasis(el,focus,blurScope){setAsHighDownDispatcher(el,!0),traverseUpdateState(el,setDefaultStateProxy),enableHoverFocus(el,focus,blurScope)}function disableHoverEmphasis(el){setAsHighDownDispatcher(el,!1)}function toggleHoverEmphasis(el,focus,blurScope,isDisabled){isDisabled?disableHoverEmphasis(el):enableHoverEmphasis(el,focus,blurScope)}function enableHoverFocus(el,focus,blurScope){var ecData=(0,_innerStore_js__WEBPACK_IMPORTED_MODULE_5__/* .getECData */.A)(el);null!=focus?(
// TODO dataIndex may be set after this function. This check is not useful.
// if (ecData.dataIndex == null) {
//     if (__DEV__) {
//         console.warn('focus can only been set on element with dataIndex');
//     }
// }
// else {
ecData.focus=focus,ecData.blurScope=blurScope):ecData.focus&&(ecData.focus=null)}var OTHER_STATES=["emphasis","blur","select"],defaultStyleGetterMap={itemStyle:"getItemStyle",lineStyle:"getLineStyle",areaStyle:"getAreaStyle"};
/**
 * Set emphasis/blur/selected states of element.
 */
function setStatesStylesFromModel(el,itemModel,styleType,// default itemStyle
getter){styleType=styleType||"itemStyle";for(var i=0;i<OTHER_STATES.length;i++){var stateName=OTHER_STATES[i],model=itemModel.getModel([stateName,styleType]),state=el.ensureState(stateName);// Let it throw error if getterType is not found.
state.style=getter?getter(model):model[defaultStyleGetterMap[styleType]]()}}
/**
 *
 * Set element as highlight / downplay dispatcher.
 * It will be checked when element received mouseover event or from highlight action.
 * It's in change of all highlight/downplay behavior of it's children.
 *
 * @param el
 * @param el.highDownSilentOnTouch
 *        In touch device, mouseover event will be trigger on touchstart event
 *        (see module:zrender/dom/HandlerProxy). By this mechanism, we can
 *        conveniently use hoverStyle when tap on touch screen without additional
 *        code for compatibility.
 *        But if the chart/component has select feature, which usually also use
 *        hoverStyle, there might be conflict between 'select-highlight' and
 *        'hover-highlight' especially when roam is enabled (see geo for example).
 *        In this case, `highDownSilentOnTouch` should be used to disable
 *        hover-highlight on touch device.
 * @param asDispatcher If `false`, do not set as "highDownDispatcher".
 */function setAsHighDownDispatcher(el,asDispatcher){var disable=!1===asDispatcher,extendedEl=el;// Make `highDownSilentOnTouch` and `onStateChange` only work after
// `setAsHighDownDispatcher` called. Avoid it is modified by user unexpectedly.
el.highDownSilentOnTouch&&(extendedEl.__highDownSilentOnTouch=el.highDownSilentOnTouch),// Simple optimize, since this method might be
// called for each elements of a group in some cases.
disable&&!extendedEl.__highDownDispatcher||(
// Emphasis, normal can be triggered manually by API or other components like hover link.
// el[method]('emphasis', onElementEmphasisEvent)[method]('normal', onElementNormalEvent);
// Also keep previous record.
extendedEl.__highByOuter=extendedEl.__highByOuter||0,extendedEl.__highDownDispatcher=!disable)}function isHighDownDispatcher(el){return!(!el||!el.__highDownDispatcher)}
/**
 * Enable component highlight/downplay features:
 * + hover link (within the same name)
 * + focus blur in component
 */
/**
 * Support highlight/downplay record on each elements.
 * For the case: hover highlight/downplay (legend, visualMap, ...) and
 * user triggered highlight/downplay should not conflict.
 * Only all of the highlightDigit cleared, return to normal.
 * @param {string} highlightKey
 * @return {number} highlightDigit
 */
function getHighlightDigit(highlightKey){var highlightDigit=_highlightKeyMap[highlightKey];return null==highlightDigit&&_highlightNextDigit<=32&&(highlightDigit=_highlightKeyMap[highlightKey]=_highlightNextDigit++),highlightDigit}function isSelectChangePayload(payload){var payloadType=payload.type;return payloadType===SELECT_ACTION_TYPE||payloadType===UNSELECT_ACTION_TYPE||payloadType===TOGGLE_SELECT_ACTION_TYPE}function isHighDownPayload(payload){var payloadType=payload.type;return payloadType===HIGHLIGHT_ACTION_TYPE||payloadType===DOWNPLAY_ACTION_TYPE}function savePathStates(el){var store=getSavedStates(el);store.normalFill=el.style.fill,store.normalStroke=el.style.stroke;var selectState=el.states.select||{};store.selectFill=selectState.style&&selectState.style.fill||null,store.selectStroke=selectState.style&&selectState.style.stroke||null}
/***/},
/***/605248:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Sv:function(){/* binding */return isEC4CompatibleStyle},
/* harmony export */Tw:function(){/* binding */return convertFromEC4CompatibleStyle},
/* harmony export */n8:function(){/* binding */return convertToEC4StyleForCustomSerise}
/* harmony export */});
/* unused harmony export warnDeprecated */
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(933051);
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * Whether need to call `convertEC4CompatibleStyle`.
 */
function isEC4CompatibleStyle(style,elType,hasOwnTextContentOption,hasOwnTextConfig){
// Since echarts5, `RectText` is separated from its host element and style.text
// does not exist any more. The compat work brings some extra burden on performance.
// So we provide:
// `legacy: true` force make compat.
// `legacy: false`, force do not compat.
// `legacy` not set: auto detect whether legacy.
//     But in this case we do not compat (difficult to detect and rare case):
//     Becuse custom series and graphic component support "merge", users may firstly
//     only set `textStrokeWidth` style or secondly only set `text`.
return style&&(style.legacy||!1!==style.legacy&&!hasOwnTextContentOption&&!hasOwnTextConfig&&"tspan"!==elType&&("text"===elType||(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(style,"text")))}
/**
 * `EC4CompatibleStyle` is style that might be in echarts4 format or echarts5 format.
 * @param hostStyle The properties might be modified.
 * @return If be text el, `textContentStyle` and `textConfig` will not be returned.
 *         Otherwise a `textContentStyle` and `textConfig` will be created, whose props area
 *         retried from the `hostStyle`.
 */function convertFromEC4CompatibleStyle(hostStyle,elType,isNormal){var textConfig,textContent,textContentStyle,srcStyle=hostStyle;if("text"===elType)textContentStyle=srcStyle;else{textContentStyle={},(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(srcStyle,"text")&&(textContentStyle.text=srcStyle.text),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(srcStyle,"rich")&&(textContentStyle.rich=srcStyle.rich),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(srcStyle,"textFill")&&(textContentStyle.fill=srcStyle.textFill),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(srcStyle,"textStroke")&&(textContentStyle.stroke=srcStyle.textStroke),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(srcStyle,"fontFamily")&&(textContentStyle.fontFamily=srcStyle.fontFamily),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(srcStyle,"fontSize")&&(textContentStyle.fontSize=srcStyle.fontSize),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(srcStyle,"fontStyle")&&(textContentStyle.fontStyle=srcStyle.fontStyle),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(srcStyle,"fontWeight")&&(textContentStyle.fontWeight=srcStyle.fontWeight),textContent={type:"text",style:textContentStyle,
// ec4 does not support rectText trigger.
// And when text position is different in normal and emphasis
// => hover text trigger emphasis;
// => text position changed, leave mouse pointer immediately;
// That might cause incorrect state.
silent:!0},textConfig={};var hasOwnPos=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(srcStyle,"textPosition");isNormal?textConfig.position=hasOwnPos?srcStyle.textPosition:"inside":hasOwnPos&&(textConfig.position=srcStyle.textPosition),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(srcStyle,"textPosition")&&(textConfig.position=srcStyle.textPosition),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(srcStyle,"textOffset")&&(textConfig.offset=srcStyle.textOffset),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(srcStyle,"textRotation")&&(textConfig.rotation=srcStyle.textRotation),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(srcStyle,"textDistance")&&(textConfig.distance=srcStyle.textDistance)}return convertEC4CompatibleRichItem(textContentStyle,hostStyle),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6)(textContentStyle.rich,(function(richItem){convertEC4CompatibleRichItem(richItem,richItem)})),{textConfig:textConfig,textContent:textContent}}
/**
 * The result will be set to `out`.
 */function convertEC4CompatibleRichItem(out,richItem){richItem&&(// (1) For simplicity, make textXXX properties (deprecated since ec5) has
// higher priority. For example, consider in ec4 `borderColor: 5, textBorderColor: 10`
// on a rect means `borderColor: 4` on the rect and `borderColor: 10` on an attached
// richText in ec5.
// (2) `out === richItem` if and only if `out` is text el or rich item.
// So we can overwrite existing props in `out` since textXXX has higher priority.
richItem.font=richItem.textFont||richItem.font,(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"textStrokeWidth")&&(out.lineWidth=richItem.textStrokeWidth),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"textAlign")&&(out.align=richItem.textAlign),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"textVerticalAlign")&&(out.verticalAlign=richItem.textVerticalAlign),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"textLineHeight")&&(out.lineHeight=richItem.textLineHeight),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"textWidth")&&(out.width=richItem.textWidth),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"textHeight")&&(out.height=richItem.textHeight),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"textBackgroundColor")&&(out.backgroundColor=richItem.textBackgroundColor),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"textPadding")&&(out.padding=richItem.textPadding),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"textBorderColor")&&(out.borderColor=richItem.textBorderColor),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"textBorderWidth")&&(out.borderWidth=richItem.textBorderWidth),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"textBorderRadius")&&(out.borderRadius=richItem.textBorderRadius),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"textBoxShadowColor")&&(out.shadowColor=richItem.textBoxShadowColor),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"textBoxShadowBlur")&&(out.shadowBlur=richItem.textBoxShadowBlur),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"textBoxShadowOffsetX")&&(out.shadowOffsetX=richItem.textBoxShadowOffsetX),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"textBoxShadowOffsetY")&&(out.shadowOffsetY=richItem.textBoxShadowOffsetY))}
/**
 * Convert to pure echarts4 format style.
 * `itemStyle` will be modified, added with ec4 style properties from
 * `textStyle` and `textConfig`.
 *
 * [Caveat]: For simplicity, `insideRollback` in ec4 does not compat, where
 * `styleEmphasis: {textFill: 'red'}` will remove the normal auto added stroke.
 */function convertToEC4StyleForCustomSerise(itemStl,txStl,txCfg){var out=itemStl;// See `custom.ts`, a trick to set extra `textPosition` firstly.
out.textPosition=out.textPosition||txCfg.position||"inside",null!=txCfg.offset&&(out.textOffset=txCfg.offset),null!=txCfg.rotation&&(out.textRotation=txCfg.rotation),null!=txCfg.distance&&(out.textDistance=txCfg.distance);var isInside=out.textPosition.indexOf("inside")>=0,hostFill=itemStl.fill||"#000";convertToEC4RichItem(out,txStl);var textFillNotSet=null==out.textFill;return isInside?textFillNotSet&&(out.textFill=txCfg.insideFill||"#fff",!out.textStroke&&txCfg.insideStroke&&(out.textStroke=txCfg.insideStroke),!out.textStroke&&(out.textStroke=hostFill),null==out.textStrokeWidth&&(out.textStrokeWidth=2)):(textFillNotSet&&(out.textFill=itemStl.fill||txCfg.outsideFill||"#000"),!out.textStroke&&txCfg.outsideStroke&&(out.textStroke=txCfg.outsideStroke)),out.text=txStl.text,out.rich=txStl.rich,(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6)(txStl.rich,(function(richItem){convertToEC4RichItem(richItem,richItem)})),out}function convertToEC4RichItem(out,richItem){richItem&&((0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"fill")&&(out.textFill=richItem.fill),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"stroke")&&(out.textStroke=richItem.fill),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"lineWidth")&&(out.textStrokeWidth=richItem.lineWidth),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"font")&&(out.font=richItem.font),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"fontStyle")&&(out.fontStyle=richItem.fontStyle),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"fontWeight")&&(out.fontWeight=richItem.fontWeight),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"fontSize")&&(out.fontSize=richItem.fontSize),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"fontFamily")&&(out.fontFamily=richItem.fontFamily),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"align")&&(out.textAlign=richItem.align),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"verticalAlign")&&(out.textVerticalAlign=richItem.verticalAlign),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"lineHeight")&&(out.textLineHeight=richItem.lineHeight),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"width")&&(out.textWidth=richItem.width),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"height")&&(out.textHeight=richItem.height),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"backgroundColor")&&(out.textBackgroundColor=richItem.backgroundColor),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"padding")&&(out.textPadding=richItem.padding),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"borderColor")&&(out.textBorderColor=richItem.borderColor),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"borderWidth")&&(out.textBorderWidth=richItem.borderWidth),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"borderRadius")&&(out.textBorderRadius=richItem.borderRadius),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"shadowColor")&&(out.textBoxShadowColor=richItem.shadowColor),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"shadowBlur")&&(out.textBoxShadowBlur=richItem.shadowBlur),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"shadowOffsetX")&&(out.textBoxShadowOffsetX=richItem.shadowOffsetX),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"shadowOffsetY")&&(out.textBoxShadowOffsetY=richItem.shadowOffsetY),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"textShadowColor")&&(out.textShadowColor=richItem.textShadowColor),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"textShadowBlur")&&(out.textShadowBlur=richItem.textShadowBlur),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"textShadowOffsetX")&&(out.textShadowOffsetX=richItem.textShadowOffsetX),(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .hasOwn */.RI)(richItem,"textShadowOffsetY")&&(out.textShadowOffsetY=richItem.textShadowOffsetY))}},
/***/241525:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Cq:function(){/* binding */return normalizeSymbolOffset},
/* harmony export */Pw:function(){/* binding */return symbolBuildProxies},
/* harmony export */th:function(){/* binding */return createSymbol},
/* harmony export */zp:function(){/* binding */return normalizeSymbolSize}
/* harmony export */});
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_4__=__webpack_require__(933051),_graphic_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(696997),_graphic_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(522095),_graphic_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(25293),_graphic_js__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(469538),_graphic_js__WEBPACK_IMPORTED_MODULE_6__=__webpack_require__(851177),zrender_lib_core_BoundingRect_js__WEBPACK_IMPORTED_MODULE_7__=__webpack_require__(260479),zrender_lib_contain_text_js__WEBPACK_IMPORTED_MODULE_5__=__webpack_require__(780423),_number_js__WEBPACK_IMPORTED_MODULE_8__=__webpack_require__(785669),Triangle=_graphic_js__WEBPACK_IMPORTED_MODULE_0__/* ["default"] */.ZP.extend({type:"triangle",shape:{cx:0,cy:0,width:0,height:0},buildPath:function(path,shape){var cx=shape.cx,cy=shape.cy,width=shape.width/2,height=shape.height/2;path.moveTo(cx,cy-height),path.lineTo(cx+width,cy+height),path.lineTo(cx-width,cy+height),path.closePath()}}),Diamond=_graphic_js__WEBPACK_IMPORTED_MODULE_0__/* ["default"] */.ZP.extend({type:"diamond",shape:{cx:0,cy:0,width:0,height:0},buildPath:function(path,shape){var cx=shape.cx,cy=shape.cy,width=shape.width/2,height=shape.height/2;path.moveTo(cx,cy-height),path.lineTo(cx+width,cy),path.lineTo(cx,cy+height),path.lineTo(cx-width,cy),path.closePath()}}),Pin=_graphic_js__WEBPACK_IMPORTED_MODULE_0__/* ["default"] */.ZP.extend({type:"pin",shape:{
// x, y on the cusp
x:0,y:0,width:0,height:0},buildPath:function(path,shape){var x=shape.x,y=shape.y,w=shape.width/5*3,h=Math.max(w,shape.height),r=w/2,dy=r*r/(h-r),cy=y-h+r+dy,angle=Math.asin(dy/r),dx=Math.cos(angle)*r,tanX=Math.sin(angle),tanY=Math.cos(angle),cpLen=.6*r,cpLen2=.7*r;path.moveTo(x-dx,cy+dy),path.arc(x,cy,r,Math.PI-angle,2*Math.PI+angle),path.bezierCurveTo(x+dx-tanX*cpLen,cy+dy+tanY*cpLen,x,y-cpLen2,x,y),path.bezierCurveTo(x,y-cpLen2,x-dx+tanX*cpLen,cy+dy+tanY*cpLen,x-dx,cy+dy),path.closePath()}}),Arrow=_graphic_js__WEBPACK_IMPORTED_MODULE_0__/* ["default"] */.ZP.extend({type:"arrow",shape:{x:0,y:0,width:0,height:0},buildPath:function(ctx,shape){var height=shape.height,width=shape.width,x=shape.x,y=shape.y,dx=width/3*2;ctx.moveTo(x,y),ctx.lineTo(x+dx,y+height),ctx.lineTo(x,y+height/4*3),ctx.lineTo(x-dx,y+height),ctx.lineTo(x,y),ctx.closePath()}}),symbolCtors={line:_graphic_js__WEBPACK_IMPORTED_MODULE_1__/* ["default"] */.Z,rect:_graphic_js__WEBPACK_IMPORTED_MODULE_2__/* ["default"] */.Z,roundRect:_graphic_js__WEBPACK_IMPORTED_MODULE_2__/* ["default"] */.Z,square:_graphic_js__WEBPACK_IMPORTED_MODULE_2__/* ["default"] */.Z,circle:_graphic_js__WEBPACK_IMPORTED_MODULE_3__/* ["default"] */.Z,diamond:Diamond,pin:Pin,arrow:Arrow,triangle:Triangle},symbolShapeMakers={line:function(x,y,w,h,shape){shape.x1=x,shape.y1=y+h/2,shape.x2=x+w,shape.y2=y+h/2},rect:function(x,y,w,h,shape){shape.x=x,shape.y=y,shape.width=w,shape.height=h},roundRect:function(x,y,w,h,shape){shape.x=x,shape.y=y,shape.width=w,shape.height=h,shape.r=Math.min(w,h)/4},square:function(x,y,w,h,shape){var size=Math.min(w,h);shape.x=x,shape.y=y,shape.width=size,shape.height=size},circle:function(x,y,w,h,shape){
// Put circle in the center of square
shape.cx=x+w/2,shape.cy=y+h/2,shape.r=Math.min(w,h)/2},diamond:function(x,y,w,h,shape){shape.cx=x+w/2,shape.cy=y+h/2,shape.width=w,shape.height=h},pin:function(x,y,w,h,shape){shape.x=x+w/2,shape.y=y+h/2,shape.width=w,shape.height=h},arrow:function(x,y,w,h,shape){shape.x=x+w/2,shape.y=y+h/2,shape.width=w,shape.height=h},triangle:function(x,y,w,h,shape){shape.cx=x+w/2,shape.cy=y+h/2,shape.width=w,shape.height=h}},symbolBuildProxies={};
/* harmony import */(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_4__/* .each */.S6)(symbolCtors,(function(Ctor,name){symbolBuildProxies[name]=new Ctor}));var SymbolClz=_graphic_js__WEBPACK_IMPORTED_MODULE_0__/* ["default"] */.ZP.extend({type:"symbol",shape:{symbolType:"",x:0,y:0,width:0,height:0},calculateTextPosition:function(out,config,rect){var res=(0,zrender_lib_contain_text_js__WEBPACK_IMPORTED_MODULE_5__/* .calculateTextPosition */.wI)(out,config,rect),shape=this.shape;return shape&&"pin"===shape.symbolType&&"inside"===config.position&&(res.y=rect.y+.4*rect.height),res},buildPath:function(ctx,shape,inBundle){var symbolType=shape.symbolType;if("none"!==symbolType){var proxySymbol=symbolBuildProxies[symbolType];proxySymbol||(
// Default rect
symbolType="rect",proxySymbol=symbolBuildProxies[symbolType]),symbolShapeMakers[symbolType](shape.x,shape.y,shape.width,shape.height,proxySymbol.shape),proxySymbol.buildPath(ctx,proxySymbol.shape,inBundle)}}});// Provide setColor helper method to avoid determine if set the fill or stroke outside
function symbolPathSetColor(color,innerColor){if("image"!==this.type){var symbolStyle=this.style;this.__isEmptyBrush?(symbolStyle.stroke=color,symbolStyle.fill=innerColor||"#fff",// TODO Same width with lineStyle in LineView
symbolStyle.lineWidth=2):"line"===this.shape.symbolType?symbolStyle.stroke=color:symbolStyle.fill=color,this.markRedraw()}}
/**
 * Create a symbol element with given symbol configuration: shape, x, y, width, height, color
 */function createSymbol(symbolType,x,y,w,h,color,// whether to keep the ratio of w/h,
keepAspect){
// TODO Support image object, DynamicImage.
var symbolPath,isEmpty=0===symbolType.indexOf("empty");return isEmpty&&(symbolType=symbolType.substr(5,1).toLowerCase()+symbolType.substr(6)),symbolPath=0===symbolType.indexOf("image://")?_graphic_js__WEBPACK_IMPORTED_MODULE_6__.makeImage(symbolType.slice(8),new zrender_lib_core_BoundingRect_js__WEBPACK_IMPORTED_MODULE_7__/* ["default"] */.Z(x,y,w,h),keepAspect?"center":"cover"):0===symbolType.indexOf("path://")?_graphic_js__WEBPACK_IMPORTED_MODULE_6__.makePath(symbolType.slice(7),{},new zrender_lib_core_BoundingRect_js__WEBPACK_IMPORTED_MODULE_7__/* ["default"] */.Z(x,y,w,h),keepAspect?"center":"cover"):new SymbolClz({shape:{symbolType:symbolType,x:x,y:y,width:w,height:h}}),symbolPath.__isEmptyBrush=isEmpty,// TODO Should deprecate setColor
symbolPath.setColor=symbolPathSetColor,color&&symbolPath.setColor(color),symbolPath}function normalizeSymbolSize(symbolSize){return(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_4__/* .isArray */.kJ)(symbolSize)||(symbolSize=[+symbolSize,+symbolSize]),[symbolSize[0]||0,symbolSize[1]||0]}function normalizeSymbolOffset(symbolOffset,symbolSize){if(null!=symbolOffset)return(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_4__/* .isArray */.kJ)(symbolOffset)||(symbolOffset=[symbolOffset,symbolOffset]),[(0,_number_js__WEBPACK_IMPORTED_MODULE_8__/* .parsePercent */.GM)(symbolOffset[0],symbolSize[0])||0,(0,_number_js__WEBPACK_IMPORTED_MODULE_8__/* .parsePercent */.GM)((0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_4__/* .retrieve2 */.pD)(symbolOffset[1],symbolOffset[0]),symbolSize[1])||0]}
/***/},
/***/800270:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */P2:function(){/* binding */return throttle},
/* harmony export */T9:function(){/* binding */return createOrUpdate},
/* harmony export */ZH:function(){/* binding */return clear}
/* harmony export */});
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
var ORIGIN_METHOD="\0__throttleOriginMethod",RATE="\0__throttleRate",THROTTLE_TYPE="\0__throttleType";
/**
 * @public
 * @param {(Function)} fn
 * @param {number} [delay=0] Unit: ms.
 * @param {boolean} [debounce=false]
 *        true: If call interval less than `delay`, only the last call works.
 *        false: If call interval less than `delay, call works on fixed rate.
 * @return {(Function)} throttled fn.
 */
function throttle(fn,delay,debounce){var currCall,diff,scope,args,debounceNextCall,lastCall=0,lastExec=0,timer=null;function exec(){lastExec=(new Date).getTime(),timer=null,fn.apply(scope,args||[])}delay=delay||0;var cb=function(){for(var cbArgs=[],_i=0;_i<arguments.length;_i++)cbArgs[_i]=arguments[_i];currCall=(new Date).getTime(),scope=this,args=cbArgs;var thisDelay=debounceNextCall||delay,thisDebounce=debounceNextCall||debounce;debounceNextCall=null,diff=currCall-(thisDebounce?lastCall:lastExec)-thisDelay,clearTimeout(timer),// Here we should make sure that: the `exec` SHOULD NOT be called later
// than a new call of `cb`, that is, preserving the command order. Consider
// calculating "scale rate" when roaming as an example. When a call of `cb`
// happens, either the `exec` is called dierectly, or the call is delayed.
// But the delayed call should never be later than next call of `cb`. Under
// this assurance, we can simply update view state each time `dispatchAction`
// triggered by user roaming, but not need to add extra code to avoid the
// state being "rolled-back".
thisDebounce?timer=setTimeout(exec,thisDelay):diff>=0?exec():timer=setTimeout(exec,-diff),lastCall=currCall};
/**
   * Clear throttle.
   * @public
   */return cb.clear=function(){timer&&(clearTimeout(timer),timer=null)},
/**
   * Enable debounce once.
   */
cb.debounceNextCall=function(debounceDelay){debounceNextCall=debounceDelay},cb}
/**
 * Create throttle method or update throttle rate.
 *
 * @example
 * ComponentView.prototype.render = function () {
 *     ...
 *     throttle.createOrUpdate(
 *         this,
 *         '_dispatchAction',
 *         this.model.get('throttle'),
 *         'fixRate'
 *     );
 * };
 * ComponentView.prototype.remove = function () {
 *     throttle.clear(this, '_dispatchAction');
 * };
 * ComponentView.prototype.dispose = function () {
 *     throttle.clear(this, '_dispatchAction');
 * };
 *
 */function createOrUpdate(obj,fnAttr,rate,throttleType){var fn=obj[fnAttr];if(fn){var originFn=fn[ORIGIN_METHOD]||fn,lastThrottleType=fn[THROTTLE_TYPE],lastRate=fn[RATE];if(lastRate!==rate||lastThrottleType!==throttleType){if(null==rate||!throttleType)return obj[fnAttr]=originFn;fn=obj[fnAttr]=throttle(originFn,rate,"debounce"===throttleType),fn[ORIGIN_METHOD]=originFn,fn[THROTTLE_TYPE]=throttleType,fn[RATE]=rate}return fn}}
/**
 * Clear throttle. Example see throttle.createOrUpdate.
 */function clear(obj,fnAttr){var fn=obj[fnAttr];fn&&fn[ORIGIN_METHOD]&&(
// Clear throttle
fn.clear&&fn.clear(),obj[fnAttr]=fn[ORIGIN_METHOD])}
/***/},
/***/515015:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */$K:function(){/* binding */return isPrimaryTimeUnit},
/* harmony export */CW:function(){/* binding */return monthGetterName},
/* harmony export */En:function(){/* binding */return hoursSetterName},
/* harmony export */FW:function(){/* binding */return timeUnits},
/* harmony export */MV:function(){/* binding */return secondsGetterName},
/* harmony export */P5:function(){/* binding */return ONE_YEAR},
/* harmony export */RZ:function(){/* binding */return millisecondsGetterName},
/* harmony export */Tj:function(){/* binding */return getPrimaryTimeUnit},
/* harmony export */V8:function(){/* binding */return fullLeveledFormatter},
/* harmony export */WT:function(){/* binding */return ONE_SECOND},
/* harmony export */WU:function(){/* binding */return format},
/* harmony export */Wp:function(){/* binding */return hoursGetterName},
/* harmony export */cb:function(){/* binding */return millisecondsSetterName},
/* harmony export */dV:function(){/* binding */return ONE_HOUR},
/* harmony export */eN:function(){/* binding */return minutesSetterName},
/* harmony export */f5:function(){/* binding */return dateSetterName},
/* harmony export */fn:function(){/* binding */return minutesGetterName},
/* harmony export */k7:function(){/* binding */return leveledFormat},
/* harmony export */q5:function(){/* binding */return getUnitValue},
/* harmony export */rM:function(){/* binding */return secondsSetterName},
/* harmony export */s2:function(){/* binding */return ONE_DAY},
/* harmony export */sx:function(){/* binding */return fullYearGetterName},
/* harmony export */vh:function(){/* binding */return monthSetterName},
/* harmony export */xC:function(){/* binding */return getDefaultFormatPrecisionOfInterval},
/* harmony export */xL:function(){/* binding */return fullYearSetterName},
/* harmony export */xz:function(){/* binding */return dateGetterName},
/* harmony export */yR:function(){/* binding */return ONE_MINUTE}
/* harmony export */});
/* unused harmony exports defaultLeveledFormatter, primaryTimeUnits, pad, getUnitFromValue */
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(933051),_number_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(785669),_core_locale_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(975212),_model_Model_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(812312),ONE_SECOND=1e3,ONE_MINUTE=60*ONE_SECOND,ONE_HOUR=60*ONE_MINUTE,ONE_DAY=24*ONE_HOUR,ONE_YEAR=365*ONE_DAY,defaultLeveledFormatter={year:"{yyyy}",month:"{MMM}",day:"{d}",hour:"{HH}:{mm}",minute:"{HH}:{mm}",second:"{HH}:{mm}:{ss}",millisecond:"{HH}:{mm}:{ss} {SSS}",none:"{yyyy}-{MM}-{dd} {HH}:{mm}:{ss} {SSS}"},fullDayFormatter="{yyyy}-{MM}-{dd}",fullLeveledFormatter={year:"{yyyy}",month:"{yyyy}-{MM}",day:fullDayFormatter,hour:fullDayFormatter+" "+defaultLeveledFormatter.hour,minute:fullDayFormatter+" "+defaultLeveledFormatter.minute,second:fullDayFormatter+" "+defaultLeveledFormatter.second,millisecond:defaultLeveledFormatter.none},primaryTimeUnits=["year","month","day","hour","minute","second","millisecond"],timeUnits=["year","half-year","quarter","month","week","half-week","day","half-day","quarter-day","hour","minute","second","millisecond"];
/* harmony import */function pad(str,len){return str+="","0000".substr(0,len-str.length)+str}function getPrimaryTimeUnit(timeUnit){switch(timeUnit){case"half-year":case"quarter":return"month";case"week":case"half-week":return"day";case"half-day":case"quarter-day":return"hour";default:
// year, minutes, second, milliseconds
return timeUnit}}function isPrimaryTimeUnit(timeUnit){return timeUnit===getPrimaryTimeUnit(timeUnit)}function getDefaultFormatPrecisionOfInterval(timeUnit){switch(timeUnit){case"year":case"month":return"day";case"millisecond":return"millisecond";default:
// Also for day, hour, minute, second
return"second"}}function format(// Note: The result based on `isUTC` are totally different, which can not be just simply
// substituted by the result without `isUTC`. So we make the param `isUTC` mandatory.
time,template,isUTC,lang){var date=_number_js__WEBPACK_IMPORTED_MODULE_0__/* .parseDate */.sG(time),y=date[fullYearGetterName(isUTC)](),M=date[monthGetterName(isUTC)]()+1,q=Math.floor((M-1)/3)+1,d=date[dateGetterName(isUTC)](),e=date["get"+(isUTC?"UTC":"")+"Day"](),H=date[hoursGetterName(isUTC)](),h=(H-1)%12+1,m=date[minutesGetterName(isUTC)](),s=date[secondsGetterName(isUTC)](),S=date[millisecondsGetterName(isUTC)](),localeModel=lang instanceof _model_Model_js__WEBPACK_IMPORTED_MODULE_1__/* ["default"] */.Z?lang:(0,_core_locale_js__WEBPACK_IMPORTED_MODULE_2__/* .getLocaleModel */.G8)(lang||_core_locale_js__WEBPACK_IMPORTED_MODULE_2__/* .SYSTEM_LANG */.sO)||(0,_core_locale_js__WEBPACK_IMPORTED_MODULE_2__/* .getDefaultLocaleModel */.Li)(),timeModel=localeModel.getModel("time"),month=timeModel.get("month"),monthAbbr=timeModel.get("monthAbbr"),dayOfWeek=timeModel.get("dayOfWeek"),dayOfWeekAbbr=timeModel.get("dayOfWeekAbbr");return(template||"").replace(/{yyyy}/g,y+"").replace(/{yy}/g,pad(y%100+"",2)).replace(/{Q}/g,q+"").replace(/{MMMM}/g,month[M-1]).replace(/{MMM}/g,monthAbbr[M-1]).replace(/{MM}/g,pad(M,2)).replace(/{M}/g,M+"").replace(/{dd}/g,pad(d,2)).replace(/{d}/g,d+"").replace(/{eeee}/g,dayOfWeek[e]).replace(/{ee}/g,dayOfWeekAbbr[e]).replace(/{e}/g,e+"").replace(/{HH}/g,pad(H,2)).replace(/{H}/g,H+"").replace(/{hh}/g,pad(h+"",2)).replace(/{h}/g,h+"").replace(/{mm}/g,pad(m,2)).replace(/{m}/g,m+"").replace(/{ss}/g,pad(s,2)).replace(/{s}/g,s+"").replace(/{SSS}/g,pad(S,3)).replace(/{S}/g,S+"")}function leveledFormat(tick,idx,formatter,lang,isUTC){var template=null;if(zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_3__/* .isString */.HD(formatter))
// Single formatter for all units at all levels
template=formatter;else if(zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_3__/* .isFunction */.mf(formatter))
// Callback formatter
template=formatter(tick.value,idx,{level:tick.level});else{var defaults=zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_3__/* .extend */.l7({},defaultLeveledFormatter);if(tick.level>0)for(var i=0;i<primaryTimeUnits.length;++i)defaults[primaryTimeUnits[i]]="{primary|"+defaults[primaryTimeUnits[i]]+"}";var mergedFormatter=formatter?!1===formatter.inherit?formatter:zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_3__/* .defaults */.ce(formatter,defaults):defaults,unit=getUnitFromValue(tick.value,isUTC);if(mergedFormatter[unit])template=mergedFormatter[unit];else if(mergedFormatter.inherit){
// Unit formatter is not defined and should inherit from bigger units
var targetId=timeUnits.indexOf(unit);for(i=targetId-1;i>=0;--i)if(mergedFormatter[unit]){template=mergedFormatter[unit];break}template=template||defaults.none}if(zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_3__/* .isArray */.kJ(template)){var levelId=null==tick.level?0:tick.level>=0?tick.level:template.length+tick.level;levelId=Math.min(levelId,template.length-1),template=template[levelId]}}return format(new Date(tick.value),template,isUTC,lang)}function getUnitFromValue(value,isUTC){var date=_number_js__WEBPACK_IMPORTED_MODULE_0__/* .parseDate */.sG(value),M=date[monthGetterName(isUTC)]()+1,d=date[dateGetterName(isUTC)](),h=date[hoursGetterName(isUTC)](),m=date[minutesGetterName(isUTC)](),s=date[secondsGetterName(isUTC)](),S=date[millisecondsGetterName(isUTC)](),isSecond=0===S,isMinute=isSecond&&0===s,isHour=isMinute&&0===m,isDay=isHour&&0===h,isMonth=isDay&&1===d,isYear=isMonth&&1===M;return isYear?"year":isMonth?"month":isDay?"day":isHour?"hour":isMinute?"minute":isSecond?"second":"millisecond"}function getUnitValue(value,unit,isUTC){var date=zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_3__/* .isNumber */.hj(value)?_number_js__WEBPACK_IMPORTED_MODULE_0__/* .parseDate */.sG(value):value;switch(unit=unit||getUnitFromValue(value,isUTC),unit){case"year":return date[fullYearGetterName(isUTC)]();case"half-year":return date[monthGetterName(isUTC)]()>=6?1:0;case"quarter":return Math.floor((date[monthGetterName(isUTC)]()+1)/4);case"month":return date[monthGetterName(isUTC)]();case"day":return date[dateGetterName(isUTC)]();case"half-day":return date[hoursGetterName(isUTC)]()/24;case"hour":return date[hoursGetterName(isUTC)]();case"minute":return date[minutesGetterName(isUTC)]();case"second":return date[secondsGetterName(isUTC)]();case"millisecond":return date[millisecondsGetterName(isUTC)]()}}function fullYearGetterName(isUTC){return isUTC?"getUTCFullYear":"getFullYear"}function monthGetterName(isUTC){return isUTC?"getUTCMonth":"getMonth"}function dateGetterName(isUTC){return isUTC?"getUTCDate":"getDate"}function hoursGetterName(isUTC){return isUTC?"getUTCHours":"getHours"}function minutesGetterName(isUTC){return isUTC?"getUTCMinutes":"getMinutes"}function secondsGetterName(isUTC){return isUTC?"getUTCSeconds":"getSeconds"}function millisecondsGetterName(isUTC){return isUTC?"getUTCMilliseconds":"getMilliseconds"}function fullYearSetterName(isUTC){return isUTC?"setUTCFullYear":"setFullYear"}function monthSetterName(isUTC){return isUTC?"setUTCMonth":"setMonth"}function dateSetterName(isUTC){return isUTC?"setUTCDate":"setDate"}function hoursSetterName(isUTC){return isUTC?"setUTCHours":"setHours"}function minutesSetterName(isUTC){return isUTC?"setUTCMinutes":"setMinutes"}function secondsSetterName(isUTC){return isUTC?"setUTCSeconds":"setSeconds"}function millisecondsSetterName(isUTC){return isUTC?"setUTCMilliseconds":"setMilliseconds"}
/***/},
/***/894279:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */J5:function(){/* binding */return SOURCE_FORMAT_TYPED_ARRAY},
/* harmony export */RA:function(){/* binding */return SOURCE_FORMAT_UNKNOWN},
/* harmony export */Wc:function(){/* binding */return SERIES_LAYOUT_BY_ROW},
/* harmony export */XD:function(){/* binding */return SOURCE_FORMAT_ARRAY_ROWS},
/* harmony export */cy:function(){/* binding */return SOURCE_FORMAT_ORIGINAL},
/* harmony export */f7:function(){/* binding */return VISUAL_DIMENSIONS},
/* harmony export */fY:function(){/* binding */return SERIES_LAYOUT_BY_COLUMN},
/* harmony export */hL:function(){/* binding */return SOURCE_FORMAT_KEYED_COLUMNS},
/* harmony export */qb:function(){/* binding */return SOURCE_FORMAT_OBJECT_ROWS}
/* harmony export */});
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(933051),VISUAL_DIMENSIONS=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .createHashMap */.kW)(["tooltip","label","itemName","itemId","itemGroupId","seriesName"]),SOURCE_FORMAT_ORIGINAL="original",SOURCE_FORMAT_ARRAY_ROWS="arrayRows",SOURCE_FORMAT_OBJECT_ROWS="objectRows",SOURCE_FORMAT_KEYED_COLUMNS="keyedColumns",SOURCE_FORMAT_TYPED_ARRAY="typedArray",SOURCE_FORMAT_UNKNOWN="unknown",SERIES_LAYOUT_BY_COLUMN="column",SERIES_LAYOUT_BY_ROW="row";
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/},
/***/980887:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */o:function(){/* binding */return createFloat32Array}
/* harmony export */});
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(933051),supportFloat32Array="undefined"!==typeof Float32Array,Float32ArrayCtor=supportFloat32Array?Float32Array:Array;
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/* global Float32Array */function createFloat32Array(arg){return(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isArray */.kJ)(arg)?supportFloat32Array?new Float32Array(arg):arg:new Float32ArrayCtor(arg);// Else is number
}
/***/},
/***/975797:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_7__=__webpack_require__(933051),zrender_lib_graphic_Group_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(38154),_util_component_js__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(942151),_util_clazz_js__WEBPACK_IMPORTED_MODULE_8__=__webpack_require__(834251),_util_model_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(232234),_util_states_js__WEBPACK_IMPORTED_MODULE_6__=__webpack_require__(726357),_core_task_js__WEBPACK_IMPORTED_MODULE_4__=__webpack_require__(608674),_chart_helper_createRenderPlanner_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(95682),_util_graphic_js__WEBPACK_IMPORTED_MODULE_5__=__webpack_require__(851177),inner=_util_model_js__WEBPACK_IMPORTED_MODULE_0__/* .makeInner */.Yf(),renderPlanner=(0,_chart_helper_createRenderPlanner_js__WEBPACK_IMPORTED_MODULE_1__/* ["default"] */.Z)(),ChartView=
/** @class */
function(){function ChartView(){this.group=new zrender_lib_graphic_Group_js__WEBPACK_IMPORTED_MODULE_2__/* ["default"] */.Z,this.uid=_util_component_js__WEBPACK_IMPORTED_MODULE_3__/* .getUID */.Kr("viewChart"),this.renderTask=(0,_core_task_js__WEBPACK_IMPORTED_MODULE_4__/* .createTask */.v)({plan:renderTaskPlan,reset:renderTaskReset}),this.renderTask.context={view:this}}return ChartView.prototype.init=function(ecModel,api){},ChartView.prototype.render=function(seriesModel,ecModel,api,payload){0},
/**
   * Highlight series or specified data item.
   */
ChartView.prototype.highlight=function(seriesModel,ecModel,api,payload){var data=seriesModel.getData(payload&&payload.dataType);data&&toggleHighlight(data,payload,"emphasis")},
/**
   * Downplay series or specified data item.
   */
ChartView.prototype.downplay=function(seriesModel,ecModel,api,payload){var data=seriesModel.getData(payload&&payload.dataType);data&&toggleHighlight(data,payload,"normal")},
/**
   * Remove self.
   */
ChartView.prototype.remove=function(ecModel,api){this.group.removeAll()},
/**
   * Dispose self.
   */
ChartView.prototype.dispose=function(ecModel,api){},ChartView.prototype.updateView=function(seriesModel,ecModel,api,payload){this.render(seriesModel,ecModel,api,payload)},// FIXME never used?
ChartView.prototype.updateLayout=function(seriesModel,ecModel,api,payload){this.render(seriesModel,ecModel,api,payload)},// FIXME never used?
ChartView.prototype.updateVisual=function(seriesModel,ecModel,api,payload){this.render(seriesModel,ecModel,api,payload)},
/**
   * Traverse the new rendered elements.
   *
   * It will traverse the new added element in progressive rendering.
   * And traverse all in normal rendering.
   */
ChartView.prototype.eachRendered=function(cb){(0,_util_graphic_js__WEBPACK_IMPORTED_MODULE_5__.traverseElements)(this.group,cb)},ChartView.markUpdateMethod=function(payload,methodName){inner(payload).updateMethod=methodName},ChartView.protoInitialize=function(){var proto=ChartView.prototype;proto.type="chart"}(),ChartView}();
/* harmony import */
/**
 * Set state of single element
 */
function elSetState(el,state,highlightDigit){el&&(0,_util_states_js__WEBPACK_IMPORTED_MODULE_6__/* .isHighDownDispatcher */.Av)(el)&&("emphasis"===state?_util_states_js__WEBPACK_IMPORTED_MODULE_6__/* .enterEmphasis */.fD:_util_states_js__WEBPACK_IMPORTED_MODULE_6__/* .leaveEmphasis */.Mh)(el,highlightDigit)}function toggleHighlight(data,payload,state){var dataIndex=_util_model_js__WEBPACK_IMPORTED_MODULE_0__/* .queryDataIndex */.gO(data,payload),highlightDigit=payload&&null!=payload.highlightKey?(0,_util_states_js__WEBPACK_IMPORTED_MODULE_6__/* .getHighlightDigit */.RW)(payload.highlightKey):null;null!=dataIndex?(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_7__/* .each */.S6)(_util_model_js__WEBPACK_IMPORTED_MODULE_0__/* .normalizeToArray */.kF(dataIndex),(function(dataIdx){elSetState(data.getItemGraphicEl(dataIdx),state,highlightDigit)})):data.eachItemGraphicEl((function(el){elSetState(el,state,highlightDigit)}))}function renderTaskPlan(context){return renderPlanner(context.model)}function renderTaskReset(context){var seriesModel=context.model,ecModel=context.ecModel,api=context.api,payload=context.payload,progressiveRender=seriesModel.pipelineContext.progressiveRender,view=context.view,updateMethod=payload&&inner(payload).updateMethod,methodName=progressiveRender?"incrementalPrepareRender":updateMethod&&view[updateMethod]?updateMethod:"render";return"render"!==methodName&&view[methodName](seriesModel,ecModel,api,payload),progressMethodMap[methodName]}_util_clazz_js__WEBPACK_IMPORTED_MODULE_8__/* .enableClassExtend */.dm(ChartView,["dispose"]),_util_clazz_js__WEBPACK_IMPORTED_MODULE_8__/* .enableClassManagement */.au(ChartView);var progressMethodMap={incrementalPrepareRender:{progress:function(params,context){context.view.incrementalRender(params,context.model,context.ecModel,context.api,context.payload)}},render:{
// Put view.render in `progress` to support appendData. But in this case
// view.render should not be called in reset, otherwise it will be called
// twise. Use `forceFirstProgress` to make sure that view.render is called
// in any cases.
forceFirstProgress:!0,progress:function(params,context){context.view.render(context.model,context.ecModel,context.api,context.payload)}}};
/* harmony default export */__webpack_exports__.Z=ChartView},
/***/833166:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony import */var zrender_lib_graphic_Group_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(38154),_util_component_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(942151),_util_clazz_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(834251),ComponentView=
/** @class */
function(){function ComponentView(){this.group=new zrender_lib_graphic_Group_js__WEBPACK_IMPORTED_MODULE_0__/* ["default"] */.Z,this.uid=_util_component_js__WEBPACK_IMPORTED_MODULE_1__/* .getUID */.Kr("viewComponent")}return ComponentView.prototype.init=function(ecModel,api){},ComponentView.prototype.render=function(model,ecModel,api,payload){},ComponentView.prototype.dispose=function(ecModel,api){},ComponentView.prototype.updateView=function(model,ecModel,api,payload){// Do nothing;
},ComponentView.prototype.updateLayout=function(model,ecModel,api,payload){// Do nothing;
},ComponentView.prototype.updateVisual=function(model,ecModel,api,payload){// Do nothing;
},
/**
   * Hook for toggle blur target series.
   * Can be used in marker for blur or leave blur the markers
   */
ComponentView.prototype.toggleBlurSeries=function(seriesModels,isBlur,ecModel){// Do nothing;
},
/**
   * Traverse the new rendered elements.
   *
   * It will traverse the new added element in progressive rendering.
   * And traverse all in normal rendering.
   */
ComponentView.prototype.eachRendered=function(cb){var group=this.group;group&&group.traverse(cb)},ComponentView}();
/* harmony import */_util_clazz_js__WEBPACK_IMPORTED_MODULE_2__/* .enableClassExtend */.dm(ComponentView),_util_clazz_js__WEBPACK_IMPORTED_MODULE_2__/* .enableClassManagement */.au(ComponentView),
/* harmony default export */__webpack_exports__.Z=ComponentView},
/***/672019:
/***/function(__unused_webpack_module,__webpack_exports__){
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * LegendVisualProvider is an bridge that pick encoded color from data and
 * provide to the legend component.
 */
var LegendVisualProvider=
/** @class */
function(){function LegendVisualProvider(// Function to get data after filtered. It stores all the encoding info
getDataWithEncodedVisual,// Function to get raw data before filtered.
getRawData){this._getDataWithEncodedVisual=getDataWithEncodedVisual,this._getRawData=getRawData}return LegendVisualProvider.prototype.getAllNames=function(){var rawData=this._getRawData();// We find the name from the raw data. In case it's filtered by the legend component.
// Normally, the name can be found in rawData, but can't be found in filtered data will display as gray.
return rawData.mapArray(rawData.getName)},LegendVisualProvider.prototype.containName=function(name){var rawData=this._getRawData();return rawData.indexOfName(name)>=0},LegendVisualProvider.prototype.indexOfName=function(name){
// Only get data when necessary.
// Because LegendVisualProvider constructor may be new in the stage that data is not prepared yet.
// Invoking Series#getData immediately will throw an error.
var dataWithEncodedVisual=this._getDataWithEncodedVisual();return dataWithEncodedVisual.indexOfName(name)},LegendVisualProvider.prototype.getItemVisual=function(dataIndex,key){
// Get encoded visual properties from final filtered data.
var dataWithEncodedVisual=this._getDataWithEncodedVisual();return dataWithEncodedVisual.getItemVisual(dataIndex,key)},LegendVisualProvider}();
/* harmony default export */__webpack_exports__.Z=LegendVisualProvider},
/***/159937:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(933051),zrender_lib_tool_color_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(321092),_util_number_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(785669),each=zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6,isObject=zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isObject */.Kn,CATEGORY_DEFAULT_VISUAL_INDEX=-1,VisualMapping=
/** @class */
function(){function VisualMapping(option){var mappingMethod=option.mappingMethod,visualType=option.type,thisOption=this.option=zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .clone */.d9(option);this.type=visualType,this.mappingMethod=mappingMethod,this._normalizeData=normalizers[mappingMethod];var visualHandler=VisualMapping.visualHandlers[visualType];this.applyVisual=visualHandler.applyVisual,this.getColorMapper=visualHandler.getColorMapper,this._normalizedToVisual=visualHandler._normalizedToVisual[mappingMethod],"piecewise"===mappingMethod?(normalizeVisualRange(thisOption),preprocessForPiecewise(thisOption)):"category"===mappingMethod?thisOption.categories?preprocessForSpecifiedCategory(thisOption):normalizeVisualRange(thisOption,!0):(
// mappingMethod === 'linear' or 'fixed'
zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .assert */.hu("linear"!==mappingMethod||thisOption.dataExtent),normalizeVisualRange(thisOption))}return VisualMapping.prototype.mapValueToVisual=function(value){var normalized=this._normalizeData(value);return this._normalizedToVisual(normalized,value)},VisualMapping.prototype.getNormalizer=function(){return zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .bind */.ak(this._normalizeData,this)},
/**
   * List available visual types.
   *
   * @public
   * @return {Array.<string>}
   */
VisualMapping.listVisualTypes=function(){return zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .keys */.XP(VisualMapping.visualHandlers)},// /**
//  * @public
//  */
// static addVisualHandler(name, handler) {
//     visualHandlers[name] = handler;
// }
/**
   * @public
   */
VisualMapping.isValidType=function(visualType){return VisualMapping.visualHandlers.hasOwnProperty(visualType)},
/**
   * Convenient method.
   * Visual can be Object or Array or primary type.
   */
VisualMapping.eachVisual=function(visual,callback,context){zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isObject */.Kn(visual)?zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6(visual,callback,context):callback.call(context,visual)},VisualMapping.mapVisual=function(visual,callback,context){var isPrimary,newVisual=zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isArray */.kJ(visual)?[]:zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isObject */.Kn(visual)?{}:(isPrimary=!0,null);return VisualMapping.eachVisual(visual,(function(v,key){var newVal=callback.call(context,v,key);isPrimary?newVisual=newVal:newVisual[key]=newVal})),newVisual},
/**
   * Retrieve visual properties from given object.
   */
VisualMapping.retrieveVisuals=function(obj){var hasVisual,ret={};return obj&&each(VisualMapping.visualHandlers,(function(h,visualType){obj.hasOwnProperty(visualType)&&(ret[visualType]=obj[visualType],hasVisual=!0)})),hasVisual?ret:null},
/**
   * Give order to visual types, considering colorSaturation, colorAlpha depends on color.
   *
   * @public
   * @param {(Object|Array)} visualTypes If Object, like: {color: ..., colorSaturation: ...}
   *                                     IF Array, like: ['color', 'symbol', 'colorSaturation']
   * @return {Array.<string>} Sorted visual types.
   */
VisualMapping.prepareVisualTypes=function(visualTypes){if(zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isArray */.kJ(visualTypes))visualTypes=visualTypes.slice();else{if(!isObject(visualTypes))return[];var types_1=[];each(visualTypes,(function(item,type){types_1.push(type)})),visualTypes=types_1}return visualTypes.sort((function(type1,type2){
// color should be front of colorSaturation, colorAlpha, ...
// symbol and symbolSize do not matter.
return"color"===type2&&"color"!==type1&&0===type1.indexOf("color")?1:-1})),visualTypes},
/**
   * 'color', 'colorSaturation', 'colorAlpha', ... are depends on 'color'.
   * Other visuals are only depends on themself.
   */
VisualMapping.dependsOn=function(visualType1,visualType2){return"color"===visualType2?!(!visualType1||0!==visualType1.indexOf(visualType2)):visualType1===visualType2},
/**
   * @param value
   * @param pieceList [{value: ..., interval: [min, max]}, ...]
   *                         Always from small to big.
   * @param findClosestWhenOutside Default to be false
   * @return index
   */
VisualMapping.findPieceIndex=function(value,pieceList,findClosestWhenOutside){// value has the higher priority.
for(var possibleI,abs=1/0,i=0,len=pieceList.length;i<len;i++){var pieceValue=pieceList[i].value;if(null!=pieceValue){if(pieceValue===value||zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isString */.HD(pieceValue)&&pieceValue===value+"")return i;findClosestWhenOutside&&updatePossible(pieceValue,i)}}for(i=0,len=pieceList.length;i<len;i++){var piece=pieceList[i],interval=piece.interval,close_1=piece.close;if(interval){if(interval[0]===-1/0){if(littleThan(close_1[1],value,interval[1]))return i}else if(interval[1]===1/0){if(littleThan(close_1[0],interval[0],value))return i}else if(littleThan(close_1[0],interval[0],value)&&littleThan(close_1[1],value,interval[1]))return i;findClosestWhenOutside&&updatePossible(interval[0],i),findClosestWhenOutside&&updatePossible(interval[1],i)}}if(findClosestWhenOutside)return value===1/0?pieceList.length-1:value===-1/0?0:possibleI;function updatePossible(val,index){var newAbs=Math.abs(val-value);newAbs<abs&&(abs=newAbs,possibleI=index)}},VisualMapping.visualHandlers={color:{applyVisual:makeApplyVisual("color"),getColorMapper:function(){var thisOption=this.option;return zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .bind */.ak("category"===thisOption.mappingMethod?function(value,isNormalized){return!isNormalized&&(value=this._normalizeData(value)),doMapCategory.call(this,value)}:function(value,isNormalized,out){
// If output rgb array
// which will be much faster and useful in pixel manipulation
var returnRGBArray=!!out;return!isNormalized&&(value=this._normalizeData(value)),out=zrender_lib_tool_color_js__WEBPACK_IMPORTED_MODULE_1__/* .fastLerp */.Uu(value,thisOption.parsedVisual,out),returnRGBArray?out:zrender_lib_tool_color_js__WEBPACK_IMPORTED_MODULE_1__/* .stringify */.Pz(out,"rgba")},this)},_normalizedToVisual:{linear:function(normalized){return zrender_lib_tool_color_js__WEBPACK_IMPORTED_MODULE_1__/* .stringify */.Pz(zrender_lib_tool_color_js__WEBPACK_IMPORTED_MODULE_1__/* .fastLerp */.Uu(normalized,this.option.parsedVisual),"rgba")},category:doMapCategory,piecewise:function(normalized,value){var result=getSpecifiedVisual.call(this,value);return null==result&&(result=zrender_lib_tool_color_js__WEBPACK_IMPORTED_MODULE_1__/* .stringify */.Pz(zrender_lib_tool_color_js__WEBPACK_IMPORTED_MODULE_1__/* .fastLerp */.Uu(normalized,this.option.parsedVisual),"rgba")),result},fixed:doMapFixed}},colorHue:makePartialColorVisualHandler((function(color,value){return zrender_lib_tool_color_js__WEBPACK_IMPORTED_MODULE_1__/* .modifyHSL */.ox(color,value)})),colorSaturation:makePartialColorVisualHandler((function(color,value){return zrender_lib_tool_color_js__WEBPACK_IMPORTED_MODULE_1__/* .modifyHSL */.ox(color,null,value)})),colorLightness:makePartialColorVisualHandler((function(color,value){return zrender_lib_tool_color_js__WEBPACK_IMPORTED_MODULE_1__/* .modifyHSL */.ox(color,null,null,value)})),colorAlpha:makePartialColorVisualHandler((function(color,value){return zrender_lib_tool_color_js__WEBPACK_IMPORTED_MODULE_1__/* .modifyAlpha */.m8(color,value)})),decal:{applyVisual:makeApplyVisual("decal"),_normalizedToVisual:{linear:null,category:doMapCategory,piecewise:null,fixed:null}},opacity:{applyVisual:makeApplyVisual("opacity"),_normalizedToVisual:createNormalizedToNumericVisual([0,1])},liftZ:{applyVisual:makeApplyVisual("liftZ"),_normalizedToVisual:{linear:doMapFixed,category:doMapFixed,piecewise:doMapFixed,fixed:doMapFixed}},symbol:{applyVisual:function(value,getter,setter){var symbolCfg=this.mapValueToVisual(value);setter("symbol",symbolCfg)},_normalizedToVisual:{linear:doMapToArray,category:doMapCategory,piecewise:function(normalized,value){var result=getSpecifiedVisual.call(this,value);return null==result&&(result=doMapToArray.call(this,normalized)),result},fixed:doMapFixed}},symbolSize:{applyVisual:makeApplyVisual("symbolSize"),_normalizedToVisual:createNormalizedToNumericVisual([0,1])}},VisualMapping}();
/* harmony import */function preprocessForPiecewise(thisOption){var pieceList=thisOption.pieceList;thisOption.hasSpecialVisual=!1,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6(pieceList,(function(piece,index){piece.originIndex=index,// piece.visual is "result visual value" but not
// a visual range, so it does not need to be normalized.
null!=piece.visual&&(thisOption.hasSpecialVisual=!0)}))}function preprocessForSpecifiedCategory(thisOption){
// Hash categories.
var categories=thisOption.categories,categoryMap=thisOption.categoryMap={},visual=thisOption.visual;// Process visual map input.
if(each(categories,(function(cate,index){categoryMap[cate]=index})),!zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isArray */.kJ(visual)){var visualArr_1=[];zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isObject */.Kn(visual)?each(visual,(function(v,cate){var index=categoryMap[cate];visualArr_1[null!=index?index:CATEGORY_DEFAULT_VISUAL_INDEX]=v})):
// Is primary type, represents default visual.
visualArr_1[CATEGORY_DEFAULT_VISUAL_INDEX]=visual,visual=setVisualToOption(thisOption,visualArr_1)}// Remove categories that has no visual,
// then we can mapping them to CATEGORY_DEFAULT_VISUAL_INDEX.
for(var i=categories.length-1;i>=0;i--)null==visual[i]&&(delete categoryMap[categories[i]],categories.pop())}function normalizeVisualRange(thisOption,isCategory){var visual=thisOption.visual,visualArr=[];zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isObject */.Kn(visual)?each(visual,(function(v){visualArr.push(v)})):null!=visual&&visualArr.push(visual);var doNotNeedPair={color:1,symbol:1};isCategory||1!==visualArr.length||doNotNeedPair.hasOwnProperty(thisOption.type)||(
// Do not care visualArr.length === 0, which is illegal.
visualArr[1]=visualArr[0]),setVisualToOption(thisOption,visualArr)}function makePartialColorVisualHandler(applyValue){return{applyVisual:function(value,getter,setter){
// Only used in HSL
var colorChannel=this.mapValueToVisual(value);// Must not be array value
setter("color",applyValue(getter("color"),colorChannel))},_normalizedToVisual:createNormalizedToNumericVisual([0,1])}}function doMapToArray(normalized){var visual=this.option.visual;return visual[Math.round((0,_util_number_js__WEBPACK_IMPORTED_MODULE_2__/* .linearMap */.NU)(normalized,[0,1],[0,visual.length-1],!0))]||{};// TODO {}?
}function makeApplyVisual(visualType){return function(value,getter,setter){setter(visualType,this.mapValueToVisual(value))}}function doMapCategory(normalized){var visual=this.option.visual;return visual[this.option.loop&&normalized!==CATEGORY_DEFAULT_VISUAL_INDEX?normalized%visual.length:normalized]}function doMapFixed(){
// visual will be convert to array.
return this.option.visual[0]}
/**
 * Create mapped to numeric visual
 */function createNormalizedToNumericVisual(sourceExtent){return{linear:function(normalized){return(0,_util_number_js__WEBPACK_IMPORTED_MODULE_2__/* .linearMap */.NU)(normalized,sourceExtent,this.option.visual,!0)},category:doMapCategory,piecewise:function(normalized,value){var result=getSpecifiedVisual.call(this,value);return null==result&&(result=(0,_util_number_js__WEBPACK_IMPORTED_MODULE_2__/* .linearMap */.NU)(normalized,sourceExtent,this.option.visual,!0)),result},fixed:doMapFixed}}function getSpecifiedVisual(value){var thisOption=this.option,pieceList=thisOption.pieceList;if(thisOption.hasSpecialVisual){var pieceIndex=VisualMapping.findPieceIndex(value,pieceList),piece=pieceList[pieceIndex];if(piece&&piece.visual)return piece.visual[this.type]}}function setVisualToOption(thisOption,visualArr){return thisOption.visual=visualArr,"color"===thisOption.type&&(thisOption.parsedVisual=zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .map */.UI(visualArr,(function(item){var color=zrender_lib_tool_color_js__WEBPACK_IMPORTED_MODULE_1__/* .parse */.Qc(item);return color||[0,0,0,1]}))),visualArr}
/**
 * Normalizers by mapping methods.
 */var normalizers={linear:function(value){return(0,_util_number_js__WEBPACK_IMPORTED_MODULE_2__/* .linearMap */.NU)(value,this.option.dataExtent,[0,1],!0)},piecewise:function(value){var pieceList=this.option.pieceList,pieceIndex=VisualMapping.findPieceIndex(value,pieceList,!0);if(null!=pieceIndex)return(0,_util_number_js__WEBPACK_IMPORTED_MODULE_2__/* .linearMap */.NU)(pieceIndex,[0,pieceList.length-1],[0,1],!0)},category:function(value){var index=this.option.categories?this.option.categoryMap[value]:value;// ordinal value
return null==index?CATEGORY_DEFAULT_VISUAL_INDEX:index},fixed:zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .noop */.ZT};function littleThan(close,a,b){return close?a<=b:a<b}
/* harmony default export */__webpack_exports__.Z=VisualMapping},
/***/880526:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Z:function(){/* binding */return decalVisual}
/* harmony export */});
/* harmony import */var _util_decal_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(559361);
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/function decalVisual(ecModel,api){ecModel.eachRawSeries((function(seriesModel){if(!ecModel.isSeriesFiltered(seriesModel)){var data=seriesModel.getData();data.hasItemVisual()&&data.each((function(idx){var decal=data.getItemVisual(idx,"decal");if(decal){var itemStyle=data.ensureUniqueItemVisual(idx,"style");itemStyle.decal=(0,_util_decal_js__WEBPACK_IMPORTED_MODULE_0__/* .createOrUpdatePatternFromDecal */.I)(decal,api)}}));var decal=data.getVisual("decal");if(decal){var style=data.getVisual("style");style.decal=(0,_util_decal_js__WEBPACK_IMPORTED_MODULE_0__/* .createOrUpdatePatternFromDecal */.I)(decal,api)}}}))}
/***/},
/***/726211:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
function getItemVisualFromData(data,dataIndex,key){switch(key){case"color":var style=data.getItemVisual(dataIndex,"style");return style[data.getVisual("drawType")];case"opacity":return data.getItemVisual(dataIndex,"style").opacity;case"symbol":case"symbolSize":case"liftZ":return data.getItemVisual(dataIndex,key);default:0}}function getVisualFromData(data,key){switch(key){case"color":var style=data.getVisual("style");return style[data.getVisual("drawType")];case"opacity":return data.getVisual("style").opacity;case"symbol":case"symbolSize":case"liftZ":return data.getVisual(key);default:0}}function setItemVisualFromData(data,dataIndex,key,value){switch(key){case"color":
// Make sure not sharing style object.
var style=data.ensureUniqueItemVisual(dataIndex,"style");style[data.getVisual("drawType")]=value,// Mark the color has been changed, not from palette anymore
data.setItemVisual(dataIndex,"colorFromPalette",!1);break;case"opacity":data.ensureUniqueItemVisual(dataIndex,"style").opacity=value;break;case"symbol":case"symbolSize":case"liftZ":data.setItemVisual(dataIndex,key,value);break;default:0}}
/***/
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */LZ:function(){/* binding */return setItemVisualFromData},
/* harmony export */Or:function(){/* binding */return getItemVisualFromData},
/* harmony export */UL:function(){/* binding */return getVisualFromData}
/* harmony export */})},
/***/646256:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */$P:function(){/* binding */return dataColorPaletteTask},
/* harmony export */TD:function(){/* binding */return seriesStyleTask},
/* harmony export */Tn:function(){/* binding */return dataStyleTask}
/* harmony export */});
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_4__=__webpack_require__(933051),_model_mixin_makeStyleMapper_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(659066),_model_mixin_itemStyle_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(489887),_model_mixin_lineStyle_js__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(877515),_model_Model_js__WEBPACK_IMPORTED_MODULE_5__=__webpack_require__(812312),_util_model_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(232234),inner=(0,_util_model_js__WEBPACK_IMPORTED_MODULE_0__/* .makeInner */.Yf)(),defaultStyleMappers={itemStyle:(0,_model_mixin_makeStyleMapper_js__WEBPACK_IMPORTED_MODULE_1__/* ["default"] */.Z)(_model_mixin_itemStyle_js__WEBPACK_IMPORTED_MODULE_2__/* .ITEM_STYLE_KEY_MAP */.t,!0),lineStyle:(0,_model_mixin_makeStyleMapper_js__WEBPACK_IMPORTED_MODULE_1__/* ["default"] */.Z)(_model_mixin_lineStyle_js__WEBPACK_IMPORTED_MODULE_3__/* .LINE_STYLE_KEY_MAP */.v,!0)},defaultColorKey={lineStyle:"stroke",itemStyle:"fill"};
/* harmony import */function getStyleMapper(seriesModel,stylePath){var styleMapper=seriesModel.visualStyleMapper||defaultStyleMappers[stylePath];return styleMapper||defaultStyleMappers.itemStyle}function getDefaultColorKey(seriesModel,stylePath){
// return defaultColorKey[stylePath] ||
var colorKey=seriesModel.visualDrawType||defaultColorKey[stylePath];return colorKey||"fill"}var seriesStyleTask={createOnAllSeries:!0,performRawSeries:!0,reset:function(seriesModel,ecModel){var data=seriesModel.getData(),stylePath=seriesModel.visualStyleAccessPath||"itemStyle",styleModel=seriesModel.getModel(stylePath),getStyle=getStyleMapper(seriesModel,stylePath),globalStyle=getStyle(styleModel),decalOption=styleModel.getShallow("decal");decalOption&&(data.setVisual("decal",decalOption),decalOption.dirty=!0);// TODO
var colorKey=getDefaultColorKey(seriesModel,stylePath),color=globalStyle[colorKey],colorCallback=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_4__/* .isFunction */.mf)(color)?color:null,hasAutoColor="auto"===globalStyle.fill||"auto"===globalStyle.stroke;// Get from color palette by default.
if(!globalStyle[colorKey]||colorCallback||hasAutoColor){
// Note: If some series has color specified (e.g., by itemStyle.color), we DO NOT
// make it effect palette. Because some scenarios users need to make some series
// transparent or as background, which should better not effect the palette.
var colorPalette=seriesModel.getColorFromPalette(// TODO series count changed.
seriesModel.name,null,ecModel.getSeriesCount());globalStyle[colorKey]||(globalStyle[colorKey]=colorPalette,data.setVisual("colorFromPalette",!0)),globalStyle.fill="auto"===globalStyle.fill||(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_4__/* .isFunction */.mf)(globalStyle.fill)?colorPalette:globalStyle.fill,globalStyle.stroke="auto"===globalStyle.stroke||(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_4__/* .isFunction */.mf)(globalStyle.stroke)?colorPalette:globalStyle.stroke}// Only visible series has each data be visual encoded
if(data.setVisual("style",globalStyle),data.setVisual("drawType",colorKey),!ecModel.isSeriesFiltered(seriesModel)&&colorCallback)return data.setVisual("colorFromPalette",!1),{dataEach:function(data,idx){var dataParams=seriesModel.getDataParams(idx),itemStyle=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_4__/* .extend */.l7)({},globalStyle);itemStyle[colorKey]=colorCallback(dataParams),data.setItemVisual(idx,"style",itemStyle)}}}},sharedModel=new _model_Model_js__WEBPACK_IMPORTED_MODULE_5__/* ["default"] */.Z,dataStyleTask={createOnAllSeries:!0,performRawSeries:!0,reset:function(seriesModel,ecModel){if(!seriesModel.ignoreStyleOnData&&!ecModel.isSeriesFiltered(seriesModel)){var data=seriesModel.getData(),stylePath=seriesModel.visualStyleAccessPath||"itemStyle",getStyle=getStyleMapper(seriesModel,stylePath),colorKey=data.getVisual("drawType");return{dataEach:data.hasItemOption?function(data,idx){
// Not use getItemModel for performance considuration
var rawItem=data.getRawDataItem(idx);if(rawItem&&rawItem[stylePath]){sharedModel.option=rawItem[stylePath];var style=getStyle(sharedModel),existsStyle=data.ensureUniqueItemVisual(idx,"style");(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_4__/* .extend */.l7)(existsStyle,style),sharedModel.option.decal&&(data.setItemVisual(idx,"decal",sharedModel.option.decal),sharedModel.option.decal.dirty=!0),colorKey in style&&data.setItemVisual(idx,"colorFromPalette",!1)}}:null}}}},dataColorPaletteTask={performRawSeries:!0,overallReset:function(ecModel){
// Each type of series uses one scope.
// Pie and funnel are using different scopes.
var paletteScopeGroupByType=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_4__/* .createHashMap */.kW)();ecModel.eachSeries((function(seriesModel){var colorBy=seriesModel.getColorBy();if(!seriesModel.isColorBySeries()){var key=seriesModel.type+"-"+colorBy,colorScope=paletteScopeGroupByType.get(key);colorScope||(colorScope={},paletteScopeGroupByType.set(key,colorScope)),inner(seriesModel).scope=colorScope}})),ecModel.eachSeries((function(seriesModel){if(!seriesModel.isColorBySeries()&&!ecModel.isSeriesFiltered(seriesModel)){var dataAll=seriesModel.getRawData(),idxMap={},data=seriesModel.getData(),colorScope=inner(seriesModel).scope,stylePath=seriesModel.visualStyleAccessPath||"itemStyle",colorKey=getDefaultColorKey(seriesModel,stylePath);data.each((function(idx){var rawIdx=data.getRawIndex(idx);idxMap[rawIdx]=idx})),// Iterate on data before filtered. To make sure color from palette can be
// Consistent when toggling legend.
dataAll.each((function(rawIdx){var idx=idxMap[rawIdx],fromPalette=data.getItemVisual(idx,"colorFromPalette");// Get color from palette for each data only when the color is inherited from series color, which is
// also picked from color palette. So following situation is not in the case:
// 1. series.itemStyle.color is set
// 2. color is encoded by visualMap
if(fromPalette){var itemStyle=data.ensureUniqueItemVisual(idx,"style"),name_1=dataAll.getName(rawIdx)||rawIdx+"",dataCount=dataAll.count();itemStyle[colorKey]=seriesModel.getColorFromPalette(name_1,colorScope,dataCount)}}))}}))}}},
/***/534586:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */m:function(){/* binding */return dataSymbolTask},
/* harmony export */n:function(){/* binding */return seriesSymbolTask}
/* harmony export */});
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(933051),SYMBOL_PROPS_WITH_CB=["symbol","symbolSize","symbolRotate","symbolOffset"],SYMBOL_PROPS=SYMBOL_PROPS_WITH_CB.concat(["symbolKeepAspect"]),seriesSymbolTask={createOnAllSeries:!0,
// For legend.
performRawSeries:!0,reset:function(seriesModel,ecModel){var data=seriesModel.getData();if(seriesModel.legendIcon&&data.setVisual("legendIcon",seriesModel.legendIcon),seriesModel.hasSymbolVisual){for(var symbolOptions={},symbolOptionsCb={},hasCallback=!1,i=0;i<SYMBOL_PROPS_WITH_CB.length;i++){var symbolPropName=SYMBOL_PROPS_WITH_CB[i],val=seriesModel.get(symbolPropName);(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isFunction */.mf)(val)?(hasCallback=!0,symbolOptionsCb[symbolPropName]=val):symbolOptions[symbolPropName]=val}// Only visible series has each data be visual encoded
if(symbolOptions.symbol=symbolOptions.symbol||seriesModel.defaultSymbol,data.setVisual((0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .extend */.l7)({legendIcon:seriesModel.legendIcon||symbolOptions.symbol,symbolKeepAspect:seriesModel.get("symbolKeepAspect")},symbolOptions)),!ecModel.isSeriesFiltered(seriesModel)){var symbolPropsCb=(0,zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .keys */.XP)(symbolOptionsCb);return{dataEach:hasCallback?dataEach:null}}}function dataEach(data,idx){for(var rawValue=seriesModel.getRawValue(idx),params=seriesModel.getDataParams(idx),i=0;i<symbolPropsCb.length;i++){var symbolPropName=symbolPropsCb[i];data.setItemVisual(idx,symbolPropName,symbolOptionsCb[symbolPropName](rawValue,params))}}}},dataSymbolTask={createOnAllSeries:!0,
// For legend.
performRawSeries:!0,reset:function(seriesModel,ecModel){if(seriesModel.hasSymbolVisual&&!ecModel.isSeriesFiltered(seriesModel))// Only visible series has each data be visual encoded
{var data=seriesModel.getData();return{dataEach:data.hasItemOption?dataEach:null}}function dataEach(data,idx){for(var itemModel=data.getItemModel(idx),i=0;i<SYMBOL_PROPS.length;i++){var symbolPropName=SYMBOL_PROPS[i],val=itemModel.getShallow(symbolPropName,!0);null!=val&&data.setItemVisual(idx,symbolPropName,val)}}}};
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/},
/***/182300:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(933051),visualDefault={
/**
   * @public
   */
get:function(visualType,key,isCategory){var value=zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .clone */.d9((defaultOption[visualType]||{})[key]);return isCategory&&zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .isArray */.kJ(value)?value[value.length-1]:value}},defaultOption={color:{active:["#006edd","#e0ffff"],inactive:["rgba(0,0,0,0)"]},colorHue:{active:[0,360],inactive:[0,0]},colorSaturation:{active:[.3,1],inactive:[0,0]},colorLightness:{active:[.9,.5],inactive:[0,0]},colorAlpha:{active:[.3,1],inactive:[0,0]},opacity:{active:[.3,1],inactive:[0,0]},symbol:{active:["circle","roundRect","diamond"],inactive:["none"]},symbolSize:{active:[10,50],inactive:[0,0]}};
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * AUTO-GENERATED FILE. DO NOT MODIFY.
 */
/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
/**
 * @file Visual mapping.
 */
/* harmony default export */__webpack_exports__.Z=visualDefault},
/***/30801:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Z7:function(){/* binding */return incrementalApplyVisual},
/* harmony export */jO:function(){/* binding */return replaceVisualOption},
/* harmony export */qD:function(){/* binding */return createVisualMappings}
/* harmony export */});
/* unused harmony export applyVisual */
/* harmony import */var zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(933051),_VisualMapping_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(159937),_helper_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(726211),each=zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6;
/* harmony import */function hasKeys(obj){if(obj)for(var name_1 in obj)if(obj.hasOwnProperty(name_1))return!0}function createVisualMappings(option,stateList,supplementVisualOption){var visualMappings={};return each(stateList,(function(state){var mappings=visualMappings[state]=createMappings();each(option[state],(function(visualData,visualType){if(_VisualMapping_js__WEBPACK_IMPORTED_MODULE_1__/* ["default"] */.Z.isValidType(visualType)){var mappingOption={type:visualType,visual:visualData};supplementVisualOption&&supplementVisualOption(mappingOption,state),mappings[visualType]=new _VisualMapping_js__WEBPACK_IMPORTED_MODULE_1__/* ["default"] */.Z(mappingOption),// Prepare a alpha for opacity, for some case that opacity
// is not supported, such as rendering using gradient color.
"opacity"===visualType&&(mappingOption=zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .clone */.d9(mappingOption),mappingOption.type="colorAlpha",mappings.__hidden.__alphaForOpacity=new _VisualMapping_js__WEBPACK_IMPORTED_MODULE_1__/* ["default"] */.Z(mappingOption))}}))})),visualMappings;function createMappings(){var Creater=function(){};// Make sure hidden fields will not be visited by
// object iteration (with hasOwnProperty checking).
Creater.prototype.__hidden=Creater.prototype;var obj=new Creater;return obj}}function replaceVisualOption(thisOption,newOption,keys){
// Visual attributes merge is not supported, otherwise it
// brings overcomplicated merge logic. See #2853. So if
// newOption has anyone of these keys, all of these keys
// will be reset. Otherwise, all keys remain.
var has;zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6(keys,(function(key){newOption.hasOwnProperty(key)&&hasKeys(newOption[key])&&(has=!0)})),has&&zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6(keys,(function(key){newOption.hasOwnProperty(key)&&hasKeys(newOption[key])?thisOption[key]=zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .clone */.d9(newOption[key]):delete thisOption[key]}))}
/**
 * @param stateList
 * @param visualMappings
 * @param list
 * @param getValueState param: valueOrIndex, return: state.
 * @param scope Scope for getValueState
 * @param dimension Concrete dimension, if used.
 */
// ???! handle brush?
/**
 * @param data
 * @param stateList
 * @param visualMappings <state, Object.<visualType, module:echarts/visual/VisualMapping>>
 * @param getValueState param: valueOrIndex, return: state.
 * @param dim dimension or dimension index.
 */
function incrementalApplyVisual(stateList,visualMappings,getValueState,dim){var visualTypesMap={};return zrender_lib_core_util_js__WEBPACK_IMPORTED_MODULE_0__/* .each */.S6(stateList,(function(state){var visualTypes=_VisualMapping_js__WEBPACK_IMPORTED_MODULE_1__/* ["default"] */.Z.prepareVisualTypes(visualMappings[state]);visualTypesMap[state]=visualTypes})),{progress:function(params,data){var dimIndex,dataIndex;function getVisual(key){return(0,_helper_js__WEBPACK_IMPORTED_MODULE_2__/* .getItemVisualFromData */.Or)(data,dataIndex,key)}function setVisual(key,value){(0,_helper_js__WEBPACK_IMPORTED_MODULE_2__/* .setItemVisualFromData */.LZ)(data,dataIndex,key,value)}null!=dim&&(dimIndex=data.getDimensionIndex(dim));var store=data.getStore();while(null!=(dataIndex=params.next())){var rawDataItem=data.getRawDataItem(dataIndex);// Consider performance
// @ts-ignore
if(!rawDataItem||!1!==rawDataItem.visualMap)for(var value=null!=dim?store.get(dimIndex,dataIndex):dataIndex,valueState=getValueState(value),mappings=visualMappings[valueState],visualTypes=visualTypesMap[valueState],i=0,len=visualTypes.length;i<len;i++){var type=visualTypes[i];mappings[type]&&mappings[type].applyVisual(value,getVisual,setVisual)}}}}}
/***/}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/chunk-common-24b3e646.js.map