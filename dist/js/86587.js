"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[86587],{
/***/586587:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){__webpack_require__.r(__webpack_exports__),
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */conf:function(){/* binding */return conf},
/* harmony export */language:function(){/* binding */return language}
/* harmony export */});
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
var conf={comments:{lineComment:"//",blockComment:["(*","*)"]},brackets:[["{","}"],["[","]"],["(",")"],["var","end_var"],["var_input","end_var"],["var_output","end_var"],["var_in_out","end_var"],["var_temp","end_var"],["var_global","end_var"],["var_access","end_var"],["var_external","end_var"],["type","end_type"],["struct","end_struct"],["program","end_program"],["function","end_function"],["function_block","end_function_block"],["action","end_action"],["step","end_step"],["initial_step","end_step"],["transaction","end_transaction"],["configuration","end_configuration"],["tcp","end_tcp"],["recource","end_recource"],["channel","end_channel"],["library","end_library"],["folder","end_folder"],["binaries","end_binaries"],["includes","end_includes"],["sources","end_sources"]],autoClosingPairs:[{open:"[",close:"]"},{open:"{",close:"}"},{open:"(",close:")"},{open:"/*",close:"*/"},{open:"'",close:"'",notIn:["string_sq"]},{open:'"',close:'"',notIn:["string_dq"]},{open:"var_input",close:"end_var"},{open:"var_output",close:"end_var"},{open:"var_in_out",close:"end_var"},{open:"var_temp",close:"end_var"},{open:"var_global",close:"end_var"},{open:"var_access",close:"end_var"},{open:"var_external",close:"end_var"},{open:"type",close:"end_type"},{open:"struct",close:"end_struct"},{open:"program",close:"end_program"},{open:"function",close:"end_function"},{open:"function_block",close:"end_function_block"},{open:"action",close:"end_action"},{open:"step",close:"end_step"},{open:"initial_step",close:"end_step"},{open:"transaction",close:"end_transaction"},{open:"configuration",close:"end_configuration"},{open:"tcp",close:"end_tcp"},{open:"recource",close:"end_recource"},{open:"channel",close:"end_channel"},{open:"library",close:"end_library"},{open:"folder",close:"end_folder"},{open:"binaries",close:"end_binaries"},{open:"includes",close:"end_includes"},{open:"sources",close:"end_sources"}],surroundingPairs:[{open:"{",close:"}"},{open:"[",close:"]"},{open:"(",close:")"},{open:'"',close:'"'},{open:"'",close:"'"},{open:"var",close:"end_var"},{open:"var_input",close:"end_var"},{open:"var_output",close:"end_var"},{open:"var_in_out",close:"end_var"},{open:"var_temp",close:"end_var"},{open:"var_global",close:"end_var"},{open:"var_access",close:"end_var"},{open:"var_external",close:"end_var"},{open:"type",close:"end_type"},{open:"struct",close:"end_struct"},{open:"program",close:"end_program"},{open:"function",close:"end_function"},{open:"function_block",close:"end_function_block"},{open:"action",close:"end_action"},{open:"step",close:"end_step"},{open:"initial_step",close:"end_step"},{open:"transaction",close:"end_transaction"},{open:"configuration",close:"end_configuration"},{open:"tcp",close:"end_tcp"},{open:"recource",close:"end_recource"},{open:"channel",close:"end_channel"},{open:"library",close:"end_library"},{open:"folder",close:"end_folder"},{open:"binaries",close:"end_binaries"},{open:"includes",close:"end_includes"},{open:"sources",close:"end_sources"}],folding:{markers:{start:new RegExp("^\\s*#pragma\\s+region\\b"),end:new RegExp("^\\s*#pragma\\s+endregion\\b")}}},language={defaultToken:"",tokenPostfix:".st",ignoreCase:!0,brackets:[{token:"delimiter.curly",open:"{",close:"}"},{token:"delimiter.parenthesis",open:"(",close:")"},{token:"delimiter.square",open:"[",close:"]"}],keywords:["if","end_if","elsif","else","case","of","to","__try","__catch","__finally","do","with","by","while","repeat","end_while","end_repeat","end_case","for","end_for","task","retain","non_retain","constant","with","at","exit","return","interval","priority","address","port","on_channel","then","iec","file","uses","version","packagetype","displayname","copyright","summary","vendor","common_source","from","extends"],constant:["false","true","null"],defineKeywords:["var","var_input","var_output","var_in_out","var_temp","var_global","var_access","var_external","end_var","type","end_type","struct","end_struct","program","end_program","function","end_function","function_block","end_function_block","interface","end_interface","method","end_method","property","end_property","namespace","end_namespace","configuration","end_configuration","tcp","end_tcp","resource","end_resource","channel","end_channel","library","end_library","folder","end_folder","binaries","end_binaries","includes","end_includes","sources","end_sources","action","end_action","step","initial_step","end_step","transaction","end_transaction"],typeKeywords:["int","sint","dint","lint","usint","uint","udint","ulint","real","lreal","time","date","time_of_day","date_and_time","string","bool","byte","word","dword","array","pointer","lword"],operators:["=",">","<",":",":=","<=",">=","<>","&","+","-","*","**","MOD","^","or","and","not","xor","abs","acos","asin","atan","cos","exp","expt","ln","log","sin","sqrt","tan","sel","max","min","limit","mux","shl","shr","rol","ror","indexof","sizeof","adr","adrinst","bitadr","is_valid","ref","ref_to"],builtinVariables:[],builtinFunctions:["sr","rs","tp","ton","tof","eq","ge","le","lt","ne","round","trunc","ctd","сtu","ctud","r_trig","f_trig","move","concat","delete","find","insert","left","len","replace","right","rtc"],
// we include these common regular expressions
symbols:/[=><!~?:&|+\-*\/\^%]+/,
// C# style strings
escapes:/\\(?:[abfnrtv\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})/,
// The main tokenizer for our languages
tokenizer:{root:[[/(\.\.)/,"delimiter"],[/\b(16#[0-9A-Fa-f\_]*)+\b/,"number.hex"],[/\b(2#[01\_]+)+\b/,"number.binary"],[/\b(8#[0-9\_]*)+\b/,"number.octal"],[/\b\d*\.\d+([eE][\-+]?\d+)?\b/,"number.float"],[/\b(L?REAL)#[0-9\_\.e]+\b/,"number.float"],[/\b(BYTE|(?:D|L)?WORD|U?(?:S|D|L)?INT)#[0-9\_]+\b/,"number"],[/\d+/,"number"],[/\b(T|DT|TOD)#[0-9:-_shmyd]+\b/,"tag"],[/\%(I|Q|M)(X|B|W|D|L)[0-9\.]+/,"tag"],[/\%(I|Q|M)[0-9\.]*/,"tag"],[/\b[A-Za-z]{1,6}#[0-9]+\b/,"tag"],[/\b(TO_|CTU_|CTD_|CTUD_|MUX_|SEL_)[A_Za-z]+\b/,"predefined"],[/\b[A_Za-z]+(_TO_)[A_Za-z]+\b/,"predefined"],[/[;]/,"delimiter"],[/[.]/,{token:"delimiter",next:"@params"}],
// identifiers and keywords
[/[a-zA-Z_]\w*/,{cases:{"@operators":"operators","@keywords":"keyword","@typeKeywords":"type","@defineKeywords":"variable","@constant":"constant","@builtinVariables":"predefined","@builtinFunctions":"predefined","@default":"identifier"}}],{include:"@whitespace"},[/[{}()\[\]]/,"@brackets"],[/"([^"\\]|\\.)*$/,"string.invalid"],[/"/,{token:"string.quote",bracket:"@open",next:"@string_dq"}],[/'/,{token:"string.quote",bracket:"@open",next:"@string_sq"}],[/'[^\\']'/,"string"],[/(')(@escapes)(')/,["string","string.escape","string"]],[/'/,"string.invalid"]],params:[[/\b[A-Za-z0-9_]+\b(?=\()/,{token:"identifier",next:"@pop"}],[/\b[A-Za-z0-9_]+\b/,"variable.name","@pop"]],comment:[[/[^\/*]+/,"comment"],[/\/\*/,"comment","@push"],["\\*/","comment","@pop"],[/[\/*]/,"comment"]],comment2:[[/[^\(*]+/,"comment"],[/\(\*/,"comment","@push"],["\\*\\)","comment","@pop"],[/[\(*]/,"comment"]],whitespace:[[/[ \t\r\n]+/,"white"],[/\/\/.*$/,"comment"],[/\/\*/,"comment","@comment"],[/\(\*/,"comment","@comment2"]],string_dq:[[/[^\\"]+/,"string"],[/@escapes/,"string.escape"],[/\\./,"string.escape.invalid"],[/"/,{token:"string.quote",bracket:"@close",next:"@pop"}]],string_sq:[[/[^\\']+/,"string"],[/@escapes/,"string.escape"],[/\\./,"string.escape.invalid"],[/'/,{token:"string.quote",bracket:"@close",next:"@pop"}]]}}}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/86587.js.map