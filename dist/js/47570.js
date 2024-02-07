"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[47570],{
/***/647570:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{setupMode:function(){/* binding */return setupMode},setupMode1:function(){/* binding */return setupMode1}});
// EXTERNAL MODULE: ./node_modules/monaco-editor/esm/vs/language/html/fillers/monaco-editor-core.js
var integer,uinteger,Position,Range,Location,LocationLink,Color,ColorInformation,ColorPresentation,FoldingRangeKind,FoldingRange,DiagnosticRelatedInformation,DiagnosticSeverity,DiagnosticTag,CodeDescription,Diagnostic,Command,TextEdit,ChangeAnnotation,ChangeAnnotationIdentifier,AnnotatedTextEdit,TextDocumentEdit,CreateFile,RenameFile,DeleteFile,WorkspaceEdit,monaco_editor_core=__webpack_require__(247650),STOP_WHEN_IDLE_FOR=12e4,WorkerManager=/** @class */function(){function WorkerManager(defaults){var _this=this;this._defaults=defaults,this._worker=null,this._idleCheckInterval=window.setInterval((function(){return _this._checkIfIdle()}),3e4),this._lastUsedTime=0,this._configChangeListener=this._defaults.onDidChange((function(){return _this._stopWorker()}))}return WorkerManager.prototype._stopWorker=function(){this._worker&&(this._worker.dispose(),this._worker=null),this._client=null},WorkerManager.prototype.dispose=function(){clearInterval(this._idleCheckInterval),this._configChangeListener.dispose(),this._stopWorker()},WorkerManager.prototype._checkIfIdle=function(){if(this._worker){var timePassedSinceLastUsed=Date.now()-this._lastUsedTime;timePassedSinceLastUsed>STOP_WHEN_IDLE_FOR&&this._stopWorker()}},WorkerManager.prototype._getClient=function(){return this._lastUsedTime=Date.now(),this._client||(this._worker=monaco_editor_core/* editor */.j6.createWebWorker({
// module that exports the create() method and returns a `HTMLWorker` instance
moduleId:"vs/language/html/htmlWorker",
// passed in to the create() method
createData:{languageSettings:this._defaults.options,languageId:this._defaults.languageId},label:this._defaults.languageId}),this._client=this._worker.getProxy()),this._client},WorkerManager.prototype.getLanguageServiceWorker=function(){for(var _client,_this=this,resources=[],_i=0;_i<arguments.length;_i++)resources[_i]=arguments[_i];return this._getClient().then((function(client){_client=client})).then((function(_){if(_this._worker)return _this._worker.withSyncedResources(resources)})).then((function(_){return _client}))},WorkerManager}();(function(integer){integer.MIN_VALUE=-2147483648,integer.MAX_VALUE=2147483647})(integer||(integer={})),function(uinteger){uinteger.MIN_VALUE=0,uinteger.MAX_VALUE=2147483647}(uinteger||(uinteger={})),function(Position){
/**
     * Creates a new Position literal from the given line and character.
     * @param line The position's line.
     * @param character The position's character.
     */
function create(line,character){return line===Number.MAX_VALUE&&(line=uinteger.MAX_VALUE),character===Number.MAX_VALUE&&(character=uinteger.MAX_VALUE),{line:line,character:character}}
/**
     * Checks whether the given literal conforms to the [Position](#Position) interface.
     */
function is(value){var candidate=value;return Is.objectLiteral(candidate)&&Is.uinteger(candidate.line)&&Is.uinteger(candidate.character)}Position.create=create,Position.is=is}(Position||(Position={})),function(Range){function create(one,two,three,four){if(Is.uinteger(one)&&Is.uinteger(two)&&Is.uinteger(three)&&Is.uinteger(four))return{start:Position.create(one,two),end:Position.create(three,four)};if(Position.is(one)&&Position.is(two))return{start:one,end:two};throw new Error("Range#create called with invalid arguments["+one+", "+two+", "+three+", "+four+"]")}
/**
     * Checks whether the given literal conforms to the [Range](#Range) interface.
     */
function is(value){var candidate=value;return Is.objectLiteral(candidate)&&Position.is(candidate.start)&&Position.is(candidate.end)}Range.create=create,Range.is=is}(Range||(Range={})),function(Location){
/**
     * Creates a Location literal.
     * @param uri The location's uri.
     * @param range The location's range.
     */
function create(uri,range){return{uri:uri,range:range}}
/**
     * Checks whether the given literal conforms to the [Location](#Location) interface.
     */
function is(value){var candidate=value;return Is.defined(candidate)&&Range.is(candidate.range)&&(Is.string(candidate.uri)||Is.undefined(candidate.uri))}Location.create=create,Location.is=is}(Location||(Location={})),function(LocationLink){
/**
     * Creates a LocationLink literal.
     * @param targetUri The definition's uri.
     * @param targetRange The full range of the definition.
     * @param targetSelectionRange The span of the symbol definition at the target.
     * @param originSelectionRange The span of the symbol being defined in the originating source file.
     */
function create(targetUri,targetRange,targetSelectionRange,originSelectionRange){return{targetUri:targetUri,targetRange:targetRange,targetSelectionRange:targetSelectionRange,originSelectionRange:originSelectionRange}}
/**
     * Checks whether the given literal conforms to the [LocationLink](#LocationLink) interface.
     */
function is(value){var candidate=value;return Is.defined(candidate)&&Range.is(candidate.targetRange)&&Is.string(candidate.targetUri)&&(Range.is(candidate.targetSelectionRange)||Is.undefined(candidate.targetSelectionRange))&&(Range.is(candidate.originSelectionRange)||Is.undefined(candidate.originSelectionRange))}LocationLink.create=create,LocationLink.is=is}(LocationLink||(LocationLink={})),function(Color){
/**
     * Creates a new Color literal.
     */
function create(red,green,blue,alpha){return{red:red,green:green,blue:blue,alpha:alpha}}
/**
     * Checks whether the given literal conforms to the [Color](#Color) interface.
     */
function is(value){var candidate=value;return Is.numberRange(candidate.red,0,1)&&Is.numberRange(candidate.green,0,1)&&Is.numberRange(candidate.blue,0,1)&&Is.numberRange(candidate.alpha,0,1)}Color.create=create,Color.is=is}(Color||(Color={})),function(ColorInformation){
/**
     * Creates a new ColorInformation literal.
     */
function create(range,color){return{range:range,color:color}}
/**
     * Checks whether the given literal conforms to the [ColorInformation](#ColorInformation) interface.
     */
function is(value){var candidate=value;return Range.is(candidate.range)&&Color.is(candidate.color)}ColorInformation.create=create,ColorInformation.is=is}(ColorInformation||(ColorInformation={})),function(ColorPresentation){
/**
     * Creates a new ColorInformation literal.
     */
function create(label,textEdit,additionalTextEdits){return{label:label,textEdit:textEdit,additionalTextEdits:additionalTextEdits}}
/**
     * Checks whether the given literal conforms to the [ColorInformation](#ColorInformation) interface.
     */
function is(value){var candidate=value;return Is.string(candidate.label)&&(Is.undefined(candidate.textEdit)||TextEdit.is(candidate))&&(Is.undefined(candidate.additionalTextEdits)||Is.typedArray(candidate.additionalTextEdits,TextEdit.is))}ColorPresentation.create=create,ColorPresentation.is=is}(ColorPresentation||(ColorPresentation={})),function(FoldingRangeKind){
/**
     * Folding range for a comment
     */
FoldingRangeKind["Comment"]="comment",
/**
     * Folding range for a imports or includes
     */
FoldingRangeKind["Imports"]="imports",
/**
     * Folding range for a region (e.g. `#region`)
     */
FoldingRangeKind["Region"]="region"}(FoldingRangeKind||(FoldingRangeKind={})),function(FoldingRange){
/**
     * Creates a new FoldingRange literal.
     */
function create(startLine,endLine,startCharacter,endCharacter,kind){var result={startLine:startLine,endLine:endLine};return Is.defined(startCharacter)&&(result.startCharacter=startCharacter),Is.defined(endCharacter)&&(result.endCharacter=endCharacter),Is.defined(kind)&&(result.kind=kind),result}
/**
     * Checks whether the given literal conforms to the [FoldingRange](#FoldingRange) interface.
     */
function is(value){var candidate=value;return Is.uinteger(candidate.startLine)&&Is.uinteger(candidate.startLine)&&(Is.undefined(candidate.startCharacter)||Is.uinteger(candidate.startCharacter))&&(Is.undefined(candidate.endCharacter)||Is.uinteger(candidate.endCharacter))&&(Is.undefined(candidate.kind)||Is.string(candidate.kind))}FoldingRange.create=create,FoldingRange.is=is}(FoldingRange||(FoldingRange={})),function(DiagnosticRelatedInformation){
/**
     * Creates a new DiagnosticRelatedInformation literal.
     */
function create(location,message){return{location:location,message:message}}
/**
     * Checks whether the given literal conforms to the [DiagnosticRelatedInformation](#DiagnosticRelatedInformation) interface.
     */
function is(value){var candidate=value;return Is.defined(candidate)&&Location.is(candidate.location)&&Is.string(candidate.message)}DiagnosticRelatedInformation.create=create,DiagnosticRelatedInformation.is=is}(DiagnosticRelatedInformation||(DiagnosticRelatedInformation={})),function(DiagnosticSeverity){
/**
     * Reports an error.
     */
DiagnosticSeverity.Error=1,
/**
     * Reports a warning.
     */
DiagnosticSeverity.Warning=2,
/**
     * Reports an information.
     */
DiagnosticSeverity.Information=3,
/**
     * Reports a hint.
     */
DiagnosticSeverity.Hint=4}(DiagnosticSeverity||(DiagnosticSeverity={})),function(DiagnosticTag){
/**
     * Unused or unnecessary code.
     *
     * Clients are allowed to render diagnostics with this tag faded out instead of having
     * an error squiggle.
     */
DiagnosticTag.Unnecessary=1,
/**
     * Deprecated or obsolete code.
     *
     * Clients are allowed to rendered diagnostics with this tag strike through.
     */
DiagnosticTag.Deprecated=2}(DiagnosticTag||(DiagnosticTag={})),function(CodeDescription){function is(value){var candidate=value;return void 0!==candidate&&null!==candidate&&Is.string(candidate.href)}CodeDescription.is=is}(CodeDescription||(CodeDescription={})),function(Diagnostic){
/**
     * Creates a new Diagnostic literal.
     */
function create(range,message,severity,code,source,relatedInformation){var result={range:range,message:message};return Is.defined(severity)&&(result.severity=severity),Is.defined(code)&&(result.code=code),Is.defined(source)&&(result.source=source),Is.defined(relatedInformation)&&(result.relatedInformation=relatedInformation),result}
/**
     * Checks whether the given literal conforms to the [Diagnostic](#Diagnostic) interface.
     */
function is(value){var _a,candidate=value;return Is.defined(candidate)&&Range.is(candidate.range)&&Is.string(candidate.message)&&(Is.number(candidate.severity)||Is.undefined(candidate.severity))&&(Is.integer(candidate.code)||Is.string(candidate.code)||Is.undefined(candidate.code))&&(Is.undefined(candidate.codeDescription)||Is.string(null===(_a=candidate.codeDescription)||void 0===_a?void 0:_a.href))&&(Is.string(candidate.source)||Is.undefined(candidate.source))&&(Is.undefined(candidate.relatedInformation)||Is.typedArray(candidate.relatedInformation,DiagnosticRelatedInformation.is))}Diagnostic.create=create,Diagnostic.is=is}(Diagnostic||(Diagnostic={})),function(Command){
/**
     * Creates a new Command literal.
     */
function create(title,command){for(var args=[],_i=2;_i<arguments.length;_i++)args[_i-2]=arguments[_i];var result={title:title,command:command};return Is.defined(args)&&args.length>0&&(result.arguments=args),result}
/**
     * Checks whether the given literal conforms to the [Command](#Command) interface.
     */
function is(value){var candidate=value;return Is.defined(candidate)&&Is.string(candidate.title)&&Is.string(candidate.command)}Command.create=create,Command.is=is}(Command||(Command={})),function(TextEdit){
/**
     * Creates a replace text edit.
     * @param range The range of text to be replaced.
     * @param newText The new text.
     */
function replace(range,newText){return{range:range,newText:newText}}
/**
     * Creates a insert text edit.
     * @param position The position to insert the text at.
     * @param newText The text to be inserted.
     */
function insert(position,newText){return{range:{start:position,end:position},newText:newText}}
/**
     * Creates a delete text edit.
     * @param range The range of text to be deleted.
     */
function del(range){return{range:range,newText:""}}function is(value){var candidate=value;return Is.objectLiteral(candidate)&&Is.string(candidate.newText)&&Range.is(candidate.range)}TextEdit.replace=replace,TextEdit.insert=insert,TextEdit.del=del,TextEdit.is=is}(TextEdit||(TextEdit={})),function(ChangeAnnotation){function create(label,needsConfirmation,description){var result={label:label};return void 0!==needsConfirmation&&(result.needsConfirmation=needsConfirmation),void 0!==description&&(result.description=description),result}function is(value){var candidate=value;return void 0!==candidate&&Is.objectLiteral(candidate)&&Is.string(candidate.label)&&(Is.boolean(candidate.needsConfirmation)||void 0===candidate.needsConfirmation)&&(Is.string(candidate.description)||void 0===candidate.description)}ChangeAnnotation.create=create,ChangeAnnotation.is=is}(ChangeAnnotation||(ChangeAnnotation={})),function(ChangeAnnotationIdentifier){function is(value){var candidate=value;return"string"===typeof candidate}ChangeAnnotationIdentifier.is=is}(ChangeAnnotationIdentifier||(ChangeAnnotationIdentifier={})),function(AnnotatedTextEdit){
/**
     * Creates an annotated replace text edit.
     *
     * @param range The range of text to be replaced.
     * @param newText The new text.
     * @param annotation The annotation.
     */
function replace(range,newText,annotation){return{range:range,newText:newText,annotationId:annotation}}
/**
     * Creates an annotated insert text edit.
     *
     * @param position The position to insert the text at.
     * @param newText The text to be inserted.
     * @param annotation The annotation.
     */
function insert(position,newText,annotation){return{range:{start:position,end:position},newText:newText,annotationId:annotation}}
/**
     * Creates an annotated delete text edit.
     *
     * @param range The range of text to be deleted.
     * @param annotation The annotation.
     */
function del(range,annotation){return{range:range,newText:"",annotationId:annotation}}function is(value){var candidate=value;return TextEdit.is(candidate)&&(ChangeAnnotation.is(candidate.annotationId)||ChangeAnnotationIdentifier.is(candidate.annotationId))}AnnotatedTextEdit.replace=replace,AnnotatedTextEdit.insert=insert,AnnotatedTextEdit.del=del,AnnotatedTextEdit.is=is}(AnnotatedTextEdit||(AnnotatedTextEdit={})),function(TextDocumentEdit){
/**
     * Creates a new `TextDocumentEdit`
     */
function create(textDocument,edits){return{textDocument:textDocument,edits:edits}}function is(value){var candidate=value;return Is.defined(candidate)&&OptionalVersionedTextDocumentIdentifier.is(candidate.textDocument)&&Array.isArray(candidate.edits)}TextDocumentEdit.create=create,TextDocumentEdit.is=is}(TextDocumentEdit||(TextDocumentEdit={})),function(CreateFile){function create(uri,options,annotation){var result={kind:"create",uri:uri};return void 0===options||void 0===options.overwrite&&void 0===options.ignoreIfExists||(result.options=options),void 0!==annotation&&(result.annotationId=annotation),result}function is(value){var candidate=value;return candidate&&"create"===candidate.kind&&Is.string(candidate.uri)&&(void 0===candidate.options||(void 0===candidate.options.overwrite||Is.boolean(candidate.options.overwrite))&&(void 0===candidate.options.ignoreIfExists||Is.boolean(candidate.options.ignoreIfExists)))&&(void 0===candidate.annotationId||ChangeAnnotationIdentifier.is(candidate.annotationId))}CreateFile.create=create,CreateFile.is=is}(CreateFile||(CreateFile={})),function(RenameFile){function create(oldUri,newUri,options,annotation){var result={kind:"rename",oldUri:oldUri,newUri:newUri};return void 0===options||void 0===options.overwrite&&void 0===options.ignoreIfExists||(result.options=options),void 0!==annotation&&(result.annotationId=annotation),result}function is(value){var candidate=value;return candidate&&"rename"===candidate.kind&&Is.string(candidate.oldUri)&&Is.string(candidate.newUri)&&(void 0===candidate.options||(void 0===candidate.options.overwrite||Is.boolean(candidate.options.overwrite))&&(void 0===candidate.options.ignoreIfExists||Is.boolean(candidate.options.ignoreIfExists)))&&(void 0===candidate.annotationId||ChangeAnnotationIdentifier.is(candidate.annotationId))}RenameFile.create=create,RenameFile.is=is}(RenameFile||(RenameFile={})),function(DeleteFile){function create(uri,options,annotation){var result={kind:"delete",uri:uri};return void 0===options||void 0===options.recursive&&void 0===options.ignoreIfNotExists||(result.options=options),void 0!==annotation&&(result.annotationId=annotation),result}function is(value){var candidate=value;return candidate&&"delete"===candidate.kind&&Is.string(candidate.uri)&&(void 0===candidate.options||(void 0===candidate.options.recursive||Is.boolean(candidate.options.recursive))&&(void 0===candidate.options.ignoreIfNotExists||Is.boolean(candidate.options.ignoreIfNotExists)))&&(void 0===candidate.annotationId||ChangeAnnotationIdentifier.is(candidate.annotationId))}DeleteFile.create=create,DeleteFile.is=is}(DeleteFile||(DeleteFile={})),function(WorkspaceEdit){function is(value){var candidate=value;return candidate&&(void 0!==candidate.changes||void 0!==candidate.documentChanges)&&(void 0===candidate.documentChanges||candidate.documentChanges.every((function(change){return Is.string(change.kind)?CreateFile.is(change)||RenameFile.is(change)||DeleteFile.is(change):TextDocumentEdit.is(change)})))}WorkspaceEdit.is=is}(WorkspaceEdit||(WorkspaceEdit={}));var TextDocumentIdentifier,VersionedTextDocumentIdentifier,OptionalVersionedTextDocumentIdentifier,TextDocumentItem,MarkupKind,MarkupContent,CompletionItemKind,InsertTextFormat,CompletionItemTag,InsertReplaceEdit,InsertTextMode,CompletionItem,CompletionList,MarkedString,Hover,ParameterInformation,SignatureInformation,DocumentHighlightKind,DocumentHighlight,SymbolKind,SymbolTag,SymbolInformation,DocumentSymbol,CodeActionKind,CodeActionContext,CodeAction,CodeLens,FormattingOptions,DocumentLink,SelectionRange,TextEditChangeImpl=/** @class */function(){function TextEditChangeImpl(edits,changeAnnotations){this.edits=edits,this.changeAnnotations=changeAnnotations}return TextEditChangeImpl.prototype.insert=function(position,newText,annotation){var edit,id;if(void 0===annotation?edit=TextEdit.insert(position,newText):ChangeAnnotationIdentifier.is(annotation)?(id=annotation,edit=AnnotatedTextEdit.insert(position,newText,annotation)):(this.assertChangeAnnotations(this.changeAnnotations),id=this.changeAnnotations.manage(annotation),edit=AnnotatedTextEdit.insert(position,newText,id)),this.edits.push(edit),void 0!==id)return id},TextEditChangeImpl.prototype.replace=function(range,newText,annotation){var edit,id;if(void 0===annotation?edit=TextEdit.replace(range,newText):ChangeAnnotationIdentifier.is(annotation)?(id=annotation,edit=AnnotatedTextEdit.replace(range,newText,annotation)):(this.assertChangeAnnotations(this.changeAnnotations),id=this.changeAnnotations.manage(annotation),edit=AnnotatedTextEdit.replace(range,newText,id)),this.edits.push(edit),void 0!==id)return id},TextEditChangeImpl.prototype.delete=function(range,annotation){var edit,id;if(void 0===annotation?edit=TextEdit.del(range):ChangeAnnotationIdentifier.is(annotation)?(id=annotation,edit=AnnotatedTextEdit.del(range,annotation)):(this.assertChangeAnnotations(this.changeAnnotations),id=this.changeAnnotations.manage(annotation),edit=AnnotatedTextEdit.del(range,id)),this.edits.push(edit),void 0!==id)return id},TextEditChangeImpl.prototype.add=function(edit){this.edits.push(edit)},TextEditChangeImpl.prototype.all=function(){return this.edits},TextEditChangeImpl.prototype.clear=function(){this.edits.splice(0,this.edits.length)},TextEditChangeImpl.prototype.assertChangeAnnotations=function(value){if(void 0===value)throw new Error("Text edit change is not configured to manage change annotations.")},TextEditChangeImpl}(),ChangeAnnotations=/** @class */function(){function ChangeAnnotations(annotations){this._annotations=void 0===annotations?Object.create(null):annotations,this._counter=0,this._size=0}return ChangeAnnotations.prototype.all=function(){return this._annotations},Object.defineProperty(ChangeAnnotations.prototype,"size",{get:function(){return this._size},enumerable:!1,configurable:!0}),ChangeAnnotations.prototype.manage=function(idOrAnnotation,annotation){var id;if(ChangeAnnotationIdentifier.is(idOrAnnotation)?id=idOrAnnotation:(id=this.nextId(),annotation=idOrAnnotation),void 0!==this._annotations[id])throw new Error("Id "+id+" is already in use.");if(void 0===annotation)throw new Error("No annotation provided for id "+id);return this._annotations[id]=annotation,this._size++,id},ChangeAnnotations.prototype.nextId=function(){return this._counter++,this._counter.toString()},ChangeAnnotations}();
/**
 * A helper class
 */ /** @class */(function(){function WorkspaceChange(workspaceEdit){var _this=this;this._textEditChanges=Object.create(null),void 0!==workspaceEdit?(this._workspaceEdit=workspaceEdit,workspaceEdit.documentChanges?(this._changeAnnotations=new ChangeAnnotations(workspaceEdit.changeAnnotations),workspaceEdit.changeAnnotations=this._changeAnnotations.all(),workspaceEdit.documentChanges.forEach((function(change){if(TextDocumentEdit.is(change)){var textEditChange=new TextEditChangeImpl(change.edits,_this._changeAnnotations);_this._textEditChanges[change.textDocument.uri]=textEditChange}}))):workspaceEdit.changes&&Object.keys(workspaceEdit.changes).forEach((function(key){var textEditChange=new TextEditChangeImpl(workspaceEdit.changes[key]);_this._textEditChanges[key]=textEditChange}))):this._workspaceEdit={}}Object.defineProperty(WorkspaceChange.prototype,"edit",{
/**
         * Returns the underlying [WorkspaceEdit](#WorkspaceEdit) literal
         * use to be returned from a workspace edit operation like rename.
         */
get:function(){return this.initDocumentChanges(),void 0!==this._changeAnnotations&&(0===this._changeAnnotations.size?this._workspaceEdit.changeAnnotations=void 0:this._workspaceEdit.changeAnnotations=this._changeAnnotations.all()),this._workspaceEdit},enumerable:!1,configurable:!0}),WorkspaceChange.prototype.getTextEditChange=function(key){if(OptionalVersionedTextDocumentIdentifier.is(key)){if(this.initDocumentChanges(),void 0===this._workspaceEdit.documentChanges)throw new Error("Workspace edit is not configured for document changes.");var textDocument={uri:key.uri,version:key.version},result=this._textEditChanges[textDocument.uri];if(!result){var edits=[],textDocumentEdit={textDocument:textDocument,edits:edits};this._workspaceEdit.documentChanges.push(textDocumentEdit),result=new TextEditChangeImpl(edits,this._changeAnnotations),this._textEditChanges[textDocument.uri]=result}return result}if(this.initChanges(),void 0===this._workspaceEdit.changes)throw new Error("Workspace edit is not configured for normal text edit changes.");result=this._textEditChanges[key];if(!result){edits=[];this._workspaceEdit.changes[key]=edits,result=new TextEditChangeImpl(edits),this._textEditChanges[key]=result}return result},WorkspaceChange.prototype.initDocumentChanges=function(){void 0===this._workspaceEdit.documentChanges&&void 0===this._workspaceEdit.changes&&(this._changeAnnotations=new ChangeAnnotations,this._workspaceEdit.documentChanges=[],this._workspaceEdit.changeAnnotations=this._changeAnnotations.all())},WorkspaceChange.prototype.initChanges=function(){void 0===this._workspaceEdit.documentChanges&&void 0===this._workspaceEdit.changes&&(this._workspaceEdit.changes=Object.create(null))},WorkspaceChange.prototype.createFile=function(uri,optionsOrAnnotation,options){if(this.initDocumentChanges(),void 0===this._workspaceEdit.documentChanges)throw new Error("Workspace edit is not configured for document changes.");var annotation,operation,id;if(ChangeAnnotation.is(optionsOrAnnotation)||ChangeAnnotationIdentifier.is(optionsOrAnnotation)?annotation=optionsOrAnnotation:options=optionsOrAnnotation,void 0===annotation?operation=CreateFile.create(uri,options):(id=ChangeAnnotationIdentifier.is(annotation)?annotation:this._changeAnnotations.manage(annotation),operation=CreateFile.create(uri,options,id)),this._workspaceEdit.documentChanges.push(operation),void 0!==id)return id},WorkspaceChange.prototype.renameFile=function(oldUri,newUri,optionsOrAnnotation,options){if(this.initDocumentChanges(),void 0===this._workspaceEdit.documentChanges)throw new Error("Workspace edit is not configured for document changes.");var annotation,operation,id;if(ChangeAnnotation.is(optionsOrAnnotation)||ChangeAnnotationIdentifier.is(optionsOrAnnotation)?annotation=optionsOrAnnotation:options=optionsOrAnnotation,void 0===annotation?operation=RenameFile.create(oldUri,newUri,options):(id=ChangeAnnotationIdentifier.is(annotation)?annotation:this._changeAnnotations.manage(annotation),operation=RenameFile.create(oldUri,newUri,options,id)),this._workspaceEdit.documentChanges.push(operation),void 0!==id)return id},WorkspaceChange.prototype.deleteFile=function(uri,optionsOrAnnotation,options){if(this.initDocumentChanges(),void 0===this._workspaceEdit.documentChanges)throw new Error("Workspace edit is not configured for document changes.");var annotation,operation,id;if(ChangeAnnotation.is(optionsOrAnnotation)||ChangeAnnotationIdentifier.is(optionsOrAnnotation)?annotation=optionsOrAnnotation:options=optionsOrAnnotation,void 0===annotation?operation=DeleteFile.create(uri,options):(id=ChangeAnnotationIdentifier.is(annotation)?annotation:this._changeAnnotations.manage(annotation),operation=DeleteFile.create(uri,options,id)),this._workspaceEdit.documentChanges.push(operation),void 0!==id)return id}})();(function(TextDocumentIdentifier){
/**
     * Creates a new TextDocumentIdentifier literal.
     * @param uri The document's uri.
     */
function create(uri){return{uri:uri}}
/**
     * Checks whether the given literal conforms to the [TextDocumentIdentifier](#TextDocumentIdentifier) interface.
     */
function is(value){var candidate=value;return Is.defined(candidate)&&Is.string(candidate.uri)}TextDocumentIdentifier.create=create,TextDocumentIdentifier.is=is})(TextDocumentIdentifier||(TextDocumentIdentifier={})),function(VersionedTextDocumentIdentifier){
/**
     * Creates a new VersionedTextDocumentIdentifier literal.
     * @param uri The document's uri.
     * @param uri The document's text.
     */
function create(uri,version){return{uri:uri,version:version}}
/**
     * Checks whether the given literal conforms to the [VersionedTextDocumentIdentifier](#VersionedTextDocumentIdentifier) interface.
     */
function is(value){var candidate=value;return Is.defined(candidate)&&Is.string(candidate.uri)&&Is.integer(candidate.version)}VersionedTextDocumentIdentifier.create=create,VersionedTextDocumentIdentifier.is=is}(VersionedTextDocumentIdentifier||(VersionedTextDocumentIdentifier={})),function(OptionalVersionedTextDocumentIdentifier){
/**
     * Creates a new OptionalVersionedTextDocumentIdentifier literal.
     * @param uri The document's uri.
     * @param uri The document's text.
     */
function create(uri,version){return{uri:uri,version:version}}
/**
     * Checks whether the given literal conforms to the [OptionalVersionedTextDocumentIdentifier](#OptionalVersionedTextDocumentIdentifier) interface.
     */
function is(value){var candidate=value;return Is.defined(candidate)&&Is.string(candidate.uri)&&(null===candidate.version||Is.integer(candidate.version))}OptionalVersionedTextDocumentIdentifier.create=create,OptionalVersionedTextDocumentIdentifier.is=is}(OptionalVersionedTextDocumentIdentifier||(OptionalVersionedTextDocumentIdentifier={})),function(TextDocumentItem){
/**
     * Creates a new TextDocumentItem literal.
     * @param uri The document's uri.
     * @param languageId The document's language identifier.
     * @param version The document's version number.
     * @param text The document's text.
     */
function create(uri,languageId,version,text){return{uri:uri,languageId:languageId,version:version,text:text}}
/**
     * Checks whether the given literal conforms to the [TextDocumentItem](#TextDocumentItem) interface.
     */
function is(value){var candidate=value;return Is.defined(candidate)&&Is.string(candidate.uri)&&Is.string(candidate.languageId)&&Is.integer(candidate.version)&&Is.string(candidate.text)}TextDocumentItem.create=create,TextDocumentItem.is=is}(TextDocumentItem||(TextDocumentItem={})),function(MarkupKind){
/**
     * Plain text is supported as a content format
     */
MarkupKind.PlainText="plaintext",
/**
     * Markdown is supported as a content format
     */
MarkupKind.Markdown="markdown"}(MarkupKind||(MarkupKind={})),function(MarkupKind){
/**
     * Checks whether the given value is a value of the [MarkupKind](#MarkupKind) type.
     */
function is(value){var candidate=value;return candidate===MarkupKind.PlainText||candidate===MarkupKind.Markdown}MarkupKind.is=is}(MarkupKind||(MarkupKind={})),function(MarkupContent){
/**
     * Checks whether the given value conforms to the [MarkupContent](#MarkupContent) interface.
     */
function is(value){var candidate=value;return Is.objectLiteral(value)&&MarkupKind.is(candidate.kind)&&Is.string(candidate.value)}MarkupContent.is=is}(MarkupContent||(MarkupContent={})),function(CompletionItemKind){CompletionItemKind.Text=1,CompletionItemKind.Method=2,CompletionItemKind.Function=3,CompletionItemKind.Constructor=4,CompletionItemKind.Field=5,CompletionItemKind.Variable=6,CompletionItemKind.Class=7,CompletionItemKind.Interface=8,CompletionItemKind.Module=9,CompletionItemKind.Property=10,CompletionItemKind.Unit=11,CompletionItemKind.Value=12,CompletionItemKind.Enum=13,CompletionItemKind.Keyword=14,CompletionItemKind.Snippet=15,CompletionItemKind.Color=16,CompletionItemKind.File=17,CompletionItemKind.Reference=18,CompletionItemKind.Folder=19,CompletionItemKind.EnumMember=20,CompletionItemKind.Constant=21,CompletionItemKind.Struct=22,CompletionItemKind.Event=23,CompletionItemKind.Operator=24,CompletionItemKind.TypeParameter=25}(CompletionItemKind||(CompletionItemKind={})),function(InsertTextFormat){
/**
     * The primary text to be inserted is treated as a plain string.
     */
InsertTextFormat.PlainText=1,
/**
     * The primary text to be inserted is treated as a snippet.
     *
     * A snippet can define tab stops and placeholders with `$1`, `$2`
     * and `${3:foo}`. `$0` defines the final tab stop, it defaults to
     * the end of the snippet. Placeholders with equal identifiers are linked,
     * that is typing in one will update others too.
     *
     * See also: https://microsoft.github.io/language-server-protocol/specifications/specification-current/#snippet_syntax
     */
InsertTextFormat.Snippet=2}(InsertTextFormat||(InsertTextFormat={})),function(CompletionItemTag){
/**
     * Render a completion as obsolete, usually using a strike-out.
     */
CompletionItemTag.Deprecated=1}(CompletionItemTag||(CompletionItemTag={})),function(InsertReplaceEdit){
/**
     * Creates a new insert / replace edit
     */
function create(newText,insert,replace){return{newText:newText,insert:insert,replace:replace}}
/**
     * Checks whether the given literal conforms to the [InsertReplaceEdit](#InsertReplaceEdit) interface.
     */
function is(value){var candidate=value;return candidate&&Is.string(candidate.newText)&&Range.is(candidate.insert)&&Range.is(candidate.replace)}InsertReplaceEdit.create=create,InsertReplaceEdit.is=is}(InsertReplaceEdit||(InsertReplaceEdit={})),function(InsertTextMode){
/**
     * The insertion or replace strings is taken as it is. If the
     * value is multi line the lines below the cursor will be
     * inserted using the indentation defined in the string value.
     * The client will not apply any kind of adjustments to the
     * string.
     */
InsertTextMode.asIs=1,
/**
     * The editor adjusts leading whitespace of new lines so that
     * they match the indentation up to the cursor of the line for
     * which the item is accepted.
     *
     * Consider a line like this: <2tabs><cursor><3tabs>foo. Accepting a
     * multi line completion item is indented using 2 tabs and all
     * following lines inserted will be indented using 2 tabs as well.
     */
InsertTextMode.adjustIndentation=2}(InsertTextMode||(InsertTextMode={})),function(CompletionItem){
/**
     * Create a completion item and seed it with a label.
     * @param label The completion item's label
     */
function create(label){return{label:label}}CompletionItem.create=create}(CompletionItem||(CompletionItem={})),function(CompletionList){
/**
     * Creates a new completion list.
     *
     * @param items The completion items.
     * @param isIncomplete The list is not complete.
     */
function create(items,isIncomplete){return{items:items||[],isIncomplete:!!isIncomplete}}CompletionList.create=create}(CompletionList||(CompletionList={})),function(MarkedString){
/**
     * Creates a marked string from plain text.
     *
     * @param plainText The plain text.
     */
function fromPlainText(plainText){return plainText.replace(/[\\`*_{}[\]()#+\-.!]/g,"\\$&");// escape markdown syntax tokens: http://daringfireball.net/projects/markdown/syntax#backslash
}
/**
     * Checks whether the given value conforms to the [MarkedString](#MarkedString) type.
     */
function is(value){var candidate=value;return Is.string(candidate)||Is.objectLiteral(candidate)&&Is.string(candidate.language)&&Is.string(candidate.value)}MarkedString.fromPlainText=fromPlainText,MarkedString.is=is}(MarkedString||(MarkedString={})),function(Hover){
/**
     * Checks whether the given value conforms to the [Hover](#Hover) interface.
     */
function is(value){var candidate=value;return!!candidate&&Is.objectLiteral(candidate)&&(MarkupContent.is(candidate.contents)||MarkedString.is(candidate.contents)||Is.typedArray(candidate.contents,MarkedString.is))&&(void 0===value.range||Range.is(value.range))}Hover.is=is}(Hover||(Hover={})),function(ParameterInformation){
/**
     * Creates a new parameter information literal.
     *
     * @param label A label string.
     * @param documentation A doc string.
     */
function create(label,documentation){return documentation?{label:label,documentation:documentation}:{label:label}}ParameterInformation.create=create}(ParameterInformation||(ParameterInformation={})),function(SignatureInformation){function create(label,documentation){for(var parameters=[],_i=2;_i<arguments.length;_i++)parameters[_i-2]=arguments[_i];var result={label:label};return Is.defined(documentation)&&(result.documentation=documentation),Is.defined(parameters)?result.parameters=parameters:result.parameters=[],result}SignatureInformation.create=create}(SignatureInformation||(SignatureInformation={})),function(DocumentHighlightKind){
/**
     * A textual occurrence.
     */
DocumentHighlightKind.Text=1,
/**
     * Read-access of a symbol, like reading a variable.
     */
DocumentHighlightKind.Read=2,
/**
     * Write-access of a symbol, like writing to a variable.
     */
DocumentHighlightKind.Write=3}(DocumentHighlightKind||(DocumentHighlightKind={})),function(DocumentHighlight){
/**
     * Create a DocumentHighlight object.
     * @param range The range the highlight applies to.
     */
function create(range,kind){var result={range:range};return Is.number(kind)&&(result.kind=kind),result}DocumentHighlight.create=create}(DocumentHighlight||(DocumentHighlight={})),function(SymbolKind){SymbolKind.File=1,SymbolKind.Module=2,SymbolKind.Namespace=3,SymbolKind.Package=4,SymbolKind.Class=5,SymbolKind.Method=6,SymbolKind.Property=7,SymbolKind.Field=8,SymbolKind.Constructor=9,SymbolKind.Enum=10,SymbolKind.Interface=11,SymbolKind.Function=12,SymbolKind.Variable=13,SymbolKind.Constant=14,SymbolKind.String=15,SymbolKind.Number=16,SymbolKind.Boolean=17,SymbolKind.Array=18,SymbolKind.Object=19,SymbolKind.Key=20,SymbolKind.Null=21,SymbolKind.EnumMember=22,SymbolKind.Struct=23,SymbolKind.Event=24,SymbolKind.Operator=25,SymbolKind.TypeParameter=26}(SymbolKind||(SymbolKind={})),function(SymbolTag){
/**
     * Render a symbol as obsolete, usually using a strike-out.
     */
SymbolTag.Deprecated=1}(SymbolTag||(SymbolTag={})),function(SymbolInformation){
/**
     * Creates a new symbol information literal.
     *
     * @param name The name of the symbol.
     * @param kind The kind of the symbol.
     * @param range The range of the location of the symbol.
     * @param uri The resource of the location of symbol, defaults to the current document.
     * @param containerName The name of the symbol containing the symbol.
     */
function create(name,kind,range,uri,containerName){var result={name:name,kind:kind,location:{uri:uri,range:range}};return containerName&&(result.containerName=containerName),result}SymbolInformation.create=create}(SymbolInformation||(SymbolInformation={})),function(DocumentSymbol){
/**
     * Creates a new symbol information literal.
     *
     * @param name The name of the symbol.
     * @param detail The detail of the symbol.
     * @param kind The kind of the symbol.
     * @param range The range of the symbol.
     * @param selectionRange The selectionRange of the symbol.
     * @param children Children of the symbol.
     */
function create(name,detail,kind,range,selectionRange,children){var result={name:name,detail:detail,kind:kind,range:range,selectionRange:selectionRange};return void 0!==children&&(result.children=children),result}
/**
     * Checks whether the given literal conforms to the [DocumentSymbol](#DocumentSymbol) interface.
     */
function is(value){var candidate=value;return candidate&&Is.string(candidate.name)&&Is.number(candidate.kind)&&Range.is(candidate.range)&&Range.is(candidate.selectionRange)&&(void 0===candidate.detail||Is.string(candidate.detail))&&(void 0===candidate.deprecated||Is.boolean(candidate.deprecated))&&(void 0===candidate.children||Array.isArray(candidate.children))&&(void 0===candidate.tags||Array.isArray(candidate.tags))}DocumentSymbol.create=create,DocumentSymbol.is=is}(DocumentSymbol||(DocumentSymbol={})),function(CodeActionKind){
/**
     * Empty kind.
     */
CodeActionKind.Empty="",
/**
     * Base kind for quickfix actions: 'quickfix'
     */
CodeActionKind.QuickFix="quickfix",
/**
     * Base kind for refactoring actions: 'refactor'
     */
CodeActionKind.Refactor="refactor",
/**
     * Base kind for refactoring extraction actions: 'refactor.extract'
     *
     * Example extract actions:
     *
     * - Extract method
     * - Extract function
     * - Extract variable
     * - Extract interface from class
     * - ...
     */
CodeActionKind.RefactorExtract="refactor.extract",
/**
     * Base kind for refactoring inline actions: 'refactor.inline'
     *
     * Example inline actions:
     *
     * - Inline function
     * - Inline variable
     * - Inline constant
     * - ...
     */
CodeActionKind.RefactorInline="refactor.inline",
/**
     * Base kind for refactoring rewrite actions: 'refactor.rewrite'
     *
     * Example rewrite actions:
     *
     * - Convert JavaScript function to class
     * - Add or remove parameter
     * - Encapsulate field
     * - Make method static
     * - Move method to base class
     * - ...
     */
CodeActionKind.RefactorRewrite="refactor.rewrite",
/**
     * Base kind for source actions: `source`
     *
     * Source code actions apply to the entire file.
     */
CodeActionKind.Source="source",
/**
     * Base kind for an organize imports source action: `source.organizeImports`
     */
CodeActionKind.SourceOrganizeImports="source.organizeImports",
/**
     * Base kind for auto-fix source actions: `source.fixAll`.
     *
     * Fix all actions automatically fix errors that have a clear fix that do not require user input.
     * They should not suppress errors or perform unsafe fixes such as generating new types or classes.
     *
     * @since 3.15.0
     */
CodeActionKind.SourceFixAll="source.fixAll"}(CodeActionKind||(CodeActionKind={})),function(CodeActionContext){
/**
     * Creates a new CodeActionContext literal.
     */
function create(diagnostics,only){var result={diagnostics:diagnostics};return void 0!==only&&null!==only&&(result.only=only),result}
/**
     * Checks whether the given literal conforms to the [CodeActionContext](#CodeActionContext) interface.
     */
function is(value){var candidate=value;return Is.defined(candidate)&&Is.typedArray(candidate.diagnostics,Diagnostic.is)&&(void 0===candidate.only||Is.typedArray(candidate.only,Is.string))}CodeActionContext.create=create,CodeActionContext.is=is}(CodeActionContext||(CodeActionContext={})),function(CodeAction){function create(title,kindOrCommandOrEdit,kind){var result={title:title},checkKind=!0;return"string"===typeof kindOrCommandOrEdit?(checkKind=!1,result.kind=kindOrCommandOrEdit):Command.is(kindOrCommandOrEdit)?result.command=kindOrCommandOrEdit:result.edit=kindOrCommandOrEdit,checkKind&&void 0!==kind&&(result.kind=kind),result}function is(value){var candidate=value;return candidate&&Is.string(candidate.title)&&(void 0===candidate.diagnostics||Is.typedArray(candidate.diagnostics,Diagnostic.is))&&(void 0===candidate.kind||Is.string(candidate.kind))&&(void 0!==candidate.edit||void 0!==candidate.command)&&(void 0===candidate.command||Command.is(candidate.command))&&(void 0===candidate.isPreferred||Is.boolean(candidate.isPreferred))&&(void 0===candidate.edit||WorkspaceEdit.is(candidate.edit))}CodeAction.create=create,CodeAction.is=is}(CodeAction||(CodeAction={})),function(CodeLens){
/**
     * Creates a new CodeLens literal.
     */
function create(range,data){var result={range:range};return Is.defined(data)&&(result.data=data),result}
/**
     * Checks whether the given literal conforms to the [CodeLens](#CodeLens) interface.
     */
function is(value){var candidate=value;return Is.defined(candidate)&&Range.is(candidate.range)&&(Is.undefined(candidate.command)||Command.is(candidate.command))}CodeLens.create=create,CodeLens.is=is}(CodeLens||(CodeLens={})),function(FormattingOptions){
/**
     * Creates a new FormattingOptions literal.
     */
function create(tabSize,insertSpaces){return{tabSize:tabSize,insertSpaces:insertSpaces}}
/**
     * Checks whether the given literal conforms to the [FormattingOptions](#FormattingOptions) interface.
     */
function is(value){var candidate=value;return Is.defined(candidate)&&Is.uinteger(candidate.tabSize)&&Is.boolean(candidate.insertSpaces)}FormattingOptions.create=create,FormattingOptions.is=is}(FormattingOptions||(FormattingOptions={})),function(DocumentLink){
/**
     * Creates a new DocumentLink literal.
     */
function create(range,target,data){return{range:range,target:target,data:data}}
/**
     * Checks whether the given literal conforms to the [DocumentLink](#DocumentLink) interface.
     */
function is(value){var candidate=value;return Is.defined(candidate)&&Range.is(candidate.range)&&(Is.undefined(candidate.target)||Is.string(candidate.target))}DocumentLink.create=create,DocumentLink.is=is}(DocumentLink||(DocumentLink={})),function(SelectionRange){
/**
     * Creates a new SelectionRange
     * @param range the range.
     * @param parent an optional parent.
     */
function create(range,parent){return{range:range,parent:parent}}function is(value){var candidate=value;return void 0!==candidate&&Range.is(candidate.range)&&(void 0===candidate.parent||SelectionRange.is(candidate.parent))}SelectionRange.create=create,SelectionRange.is=is}(SelectionRange||(SelectionRange={}));var TextDocument;
/**
 * @deprecated Use the text document from the new vscode-languageserver-textdocument package.
 */(function(TextDocument){
/**
     * Creates a new ITextDocument literal from the given uri and content.
     * @param uri The document's uri.
     * @param languageId  The document's language Id.
     * @param content The document's content.
     */
function create(uri,languageId,version,content){return new FullTextDocument(uri,languageId,version,content)}
/**
     * Checks whether the given literal conforms to the [ITextDocument](#ITextDocument) interface.
     */
function is(value){var candidate=value;return!!(Is.defined(candidate)&&Is.string(candidate.uri)&&(Is.undefined(candidate.languageId)||Is.string(candidate.languageId))&&Is.uinteger(candidate.lineCount)&&Is.func(candidate.getText)&&Is.func(candidate.positionAt)&&Is.func(candidate.offsetAt))}function applyEdits(document,edits){for(var text=document.getText(),sortedEdits=mergeSort(edits,(function(a,b){var diff=a.range.start.line-b.range.start.line;return 0===diff?a.range.start.character-b.range.start.character:diff})),lastModifiedOffset=text.length,i=sortedEdits.length-1;i>=0;i--){var e=sortedEdits[i],startOffset=document.offsetAt(e.range.start),endOffset=document.offsetAt(e.range.end);if(!(endOffset<=lastModifiedOffset))throw new Error("Overlapping edit");text=text.substring(0,startOffset)+e.newText+text.substring(endOffset,text.length),lastModifiedOffset=startOffset}return text}function mergeSort(data,compare){if(data.length<=1)
// sorted
return data;var p=data.length/2|0,left=data.slice(0,p),right=data.slice(p);mergeSort(left,compare),mergeSort(right,compare);var leftIdx=0,rightIdx=0,i=0;while(leftIdx<left.length&&rightIdx<right.length){var ret=compare(left[leftIdx],right[rightIdx]);
// smaller_equal -> take left to preserve order
data[i++]=ret<=0?left[leftIdx++]:right[rightIdx++]}while(leftIdx<left.length)data[i++]=left[leftIdx++];while(rightIdx<right.length)data[i++]=right[rightIdx++];return data}TextDocument.create=create,TextDocument.is=is,TextDocument.applyEdits=applyEdits})(TextDocument||(TextDocument={}));
/**
 * @deprecated Use the text document from the new vscode-languageserver-textdocument package.
 */
var Is,FullTextDocument=/** @class */function(){function FullTextDocument(uri,languageId,version,content){this._uri=uri,this._languageId=languageId,this._version=version,this._content=content,this._lineOffsets=void 0}return Object.defineProperty(FullTextDocument.prototype,"uri",{get:function(){return this._uri},enumerable:!1,configurable:!0}),Object.defineProperty(FullTextDocument.prototype,"languageId",{get:function(){return this._languageId},enumerable:!1,configurable:!0}),Object.defineProperty(FullTextDocument.prototype,"version",{get:function(){return this._version},enumerable:!1,configurable:!0}),FullTextDocument.prototype.getText=function(range){if(range){var start=this.offsetAt(range.start),end=this.offsetAt(range.end);return this._content.substring(start,end)}return this._content},FullTextDocument.prototype.update=function(event,version){this._content=event.text,this._version=version,this._lineOffsets=void 0},FullTextDocument.prototype.getLineOffsets=function(){if(void 0===this._lineOffsets){for(var lineOffsets=[],text=this._content,isLineStart=!0,i=0;i<text.length;i++){isLineStart&&(lineOffsets.push(i),isLineStart=!1);var ch=text.charAt(i);isLineStart="\r"===ch||"\n"===ch,"\r"===ch&&i+1<text.length&&"\n"===text.charAt(i+1)&&i++}isLineStart&&text.length>0&&lineOffsets.push(text.length),this._lineOffsets=lineOffsets}return this._lineOffsets},FullTextDocument.prototype.positionAt=function(offset){offset=Math.max(Math.min(offset,this._content.length),0);var lineOffsets=this.getLineOffsets(),low=0,high=lineOffsets.length;if(0===high)return Position.create(0,offset);while(low<high){var mid=Math.floor((low+high)/2);lineOffsets[mid]>offset?high=mid:low=mid+1}
// low is the least x for which the line offset is larger than the current offset
// or array.length if no line offset is larger than the current offset
var line=low-1;return Position.create(line,offset-lineOffsets[line])},FullTextDocument.prototype.offsetAt=function(position){var lineOffsets=this.getLineOffsets();if(position.line>=lineOffsets.length)return this._content.length;if(position.line<0)return 0;var lineOffset=lineOffsets[position.line],nextLineOffset=position.line+1<lineOffsets.length?lineOffsets[position.line+1]:this._content.length;return Math.max(Math.min(lineOffset+position.character,nextLineOffset),lineOffset)},Object.defineProperty(FullTextDocument.prototype,"lineCount",{get:function(){return this.getLineOffsets().length},enumerable:!1,configurable:!0}),FullTextDocument}();// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/language/html/languageFeatures.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
// --- completion ------
function fromPosition(position){if(position)return{character:position.column-1,line:position.lineNumber-1}}function fromRange(range){if(range)return{start:fromPosition(range.getStartPosition()),end:fromPosition(range.getEndPosition())}}function toRange(range){if(range)return new monaco_editor_core/* Range */.e6(range.start.line+1,range.start.character+1,range.end.line+1,range.end.character+1)}function isInsertReplaceEdit(edit){return"undefined"!==typeof edit.insert&&"undefined"!==typeof edit.replace}function toCompletionItemKind(kind){var mItemKind=monaco_editor_core/* languages */.Mj.CompletionItemKind;switch(kind){case CompletionItemKind.Text:return mItemKind.Text;case CompletionItemKind.Method:return mItemKind.Method;case CompletionItemKind.Function:return mItemKind.Function;case CompletionItemKind.Constructor:return mItemKind.Constructor;case CompletionItemKind.Field:return mItemKind.Field;case CompletionItemKind.Variable:return mItemKind.Variable;case CompletionItemKind.Class:return mItemKind.Class;case CompletionItemKind.Interface:return mItemKind.Interface;case CompletionItemKind.Module:return mItemKind.Module;case CompletionItemKind.Property:return mItemKind.Property;case CompletionItemKind.Unit:return mItemKind.Unit;case CompletionItemKind.Value:return mItemKind.Value;case CompletionItemKind.Enum:return mItemKind.Enum;case CompletionItemKind.Keyword:return mItemKind.Keyword;case CompletionItemKind.Snippet:return mItemKind.Snippet;case CompletionItemKind.Color:return mItemKind.Color;case CompletionItemKind.File:return mItemKind.File;case CompletionItemKind.Reference:return mItemKind.Reference}return mItemKind.Property}function toTextEdit(textEdit){if(textEdit)return{range:toRange(textEdit.range),text:textEdit.newText}}function toCommand(c){return c&&"editor.action.triggerSuggest"===c.command?{id:c.command,title:c.title,arguments:c.arguments}:void 0}(function(Is){var toString=Object.prototype.toString;function defined(value){return"undefined"!==typeof value}function undefined(value){return"undefined"===typeof value}function boolean(value){return!0===value||!1===value}function string(value){return"[object String]"===toString.call(value)}function number(value){return"[object Number]"===toString.call(value)}function numberRange(value,min,max){return"[object Number]"===toString.call(value)&&min<=value&&value<=max}function integer(value){return"[object Number]"===toString.call(value)&&-2147483648<=value&&value<=2147483647}function uinteger(value){return"[object Number]"===toString.call(value)&&0<=value&&value<=2147483647}function func(value){return"[object Function]"===toString.call(value)}function objectLiteral(value){
// Strictly speaking class instances pass this check as well. Since the LSP
// doesn't use classes we ignore this for now. If we do we need to add something
// like this: `Object.getPrototypeOf(Object.getPrototypeOf(x)) === null`
return null!==value&&"object"===typeof value}function typedArray(value,check){return Array.isArray(value)&&value.every(check)}Is.defined=defined,Is.undefined=undefined,Is.boolean=boolean,Is.string=string,Is.number=number,Is.numberRange=numberRange,Is.integer=integer,Is.uinteger=uinteger,Is.func=func,Is.objectLiteral=objectLiteral,Is.typedArray=typedArray})(Is||(Is={}));var CompletionAdapter=/** @class */function(){function CompletionAdapter(_worker){this._worker=_worker}return Object.defineProperty(CompletionAdapter.prototype,"triggerCharacters",{get:function(){return[".",":","<",'"',"=","/"]},enumerable:!1,configurable:!0}),CompletionAdapter.prototype.provideCompletionItems=function(model,position,context,token){var resource=model.uri;return this._worker(resource).then((function(worker){return worker.doComplete(resource.toString(),fromPosition(position))})).then((function(info){if(info){var wordInfo=model.getWordUntilPosition(position),wordRange=new monaco_editor_core/* Range */.e6(position.lineNumber,wordInfo.startColumn,position.lineNumber,wordInfo.endColumn),items=info.items.map((function(entry){var item={label:entry.label,insertText:entry.insertText||entry.label,sortText:entry.sortText,filterText:entry.filterText,documentation:entry.documentation,command:toCommand(entry.command),detail:entry.detail,range:wordRange,kind:toCompletionItemKind(entry.kind)};return entry.textEdit&&(isInsertReplaceEdit(entry.textEdit)?item.range={insert:toRange(entry.textEdit.insert),replace:toRange(entry.textEdit.replace)}:item.range=toRange(entry.textEdit.range),item.insertText=entry.textEdit.newText),entry.additionalTextEdits&&(item.additionalTextEdits=entry.additionalTextEdits.map(toTextEdit)),entry.insertTextFormat===InsertTextFormat.Snippet&&(item.insertTextRules=monaco_editor_core/* languages */.Mj.CompletionItemInsertTextRule.InsertAsSnippet),item}));return{isIncomplete:info.isIncomplete,suggestions:items}}}))},CompletionAdapter}();
// --- hover ------
function isMarkupContent(thing){return thing&&"object"===typeof thing&&"string"===typeof thing.kind}function toMarkdownString(entry){return"string"===typeof entry?{value:entry}:isMarkupContent(entry)?"plaintext"===entry.kind?{value:entry.value.replace(/[\\`*_{}[\]()#+\-.!]/g,"\\$&")}:{value:entry.value}:{value:"```"+entry.language+"\n"+entry.value+"\n```\n"}}function toMarkedStringArray(contents){if(contents)return Array.isArray(contents)?contents.map(toMarkdownString):[toMarkdownString(contents)]}var HoverAdapter=/** @class */function(){function HoverAdapter(_worker){this._worker=_worker}return HoverAdapter.prototype.provideHover=function(model,position,token){var resource=model.uri;return this._worker(resource).then((function(worker){return worker.doHover(resource.toString(),fromPosition(position))})).then((function(info){if(info)return{range:toRange(info.range),contents:toMarkedStringArray(info.contents)}}))},HoverAdapter}();
// --- document highlights ------
function toHighlighKind(kind){var mKind=monaco_editor_core/* languages */.Mj.DocumentHighlightKind;switch(kind){case DocumentHighlightKind.Read:return mKind.Read;case DocumentHighlightKind.Write:return mKind.Write;case DocumentHighlightKind.Text:return mKind.Text}return mKind.Text}var DocumentHighlightAdapter=/** @class */function(){function DocumentHighlightAdapter(_worker){this._worker=_worker}return DocumentHighlightAdapter.prototype.provideDocumentHighlights=function(model,position,token){var resource=model.uri;return this._worker(resource).then((function(worker){return worker.findDocumentHighlights(resource.toString(),fromPosition(position))})).then((function(items){if(items)return items.map((function(item){return{range:toRange(item.range),kind:toHighlighKind(item.kind)}}))}))},DocumentHighlightAdapter}();
// --- document symbols ------
function toSymbolKind(kind){var mKind=monaco_editor_core/* languages */.Mj.SymbolKind;switch(kind){case SymbolKind.File:return mKind.Array;case SymbolKind.Module:return mKind.Module;case SymbolKind.Namespace:return mKind.Namespace;case SymbolKind.Package:return mKind.Package;case SymbolKind.Class:return mKind.Class;case SymbolKind.Method:return mKind.Method;case SymbolKind.Property:return mKind.Property;case SymbolKind.Field:return mKind.Field;case SymbolKind.Constructor:return mKind.Constructor;case SymbolKind.Enum:return mKind.Enum;case SymbolKind.Interface:return mKind.Interface;case SymbolKind.Function:return mKind.Function;case SymbolKind.Variable:return mKind.Variable;case SymbolKind.Constant:return mKind.Constant;case SymbolKind.String:return mKind.String;case SymbolKind.Number:return mKind.Number;case SymbolKind.Boolean:return mKind.Boolean;case SymbolKind.Array:return mKind.Array}return mKind.Function}var DocumentSymbolAdapter=/** @class */function(){function DocumentSymbolAdapter(_worker){this._worker=_worker}return DocumentSymbolAdapter.prototype.provideDocumentSymbols=function(model,token){var resource=model.uri;return this._worker(resource).then((function(worker){return worker.findDocumentSymbols(resource.toString())})).then((function(items){if(items)return items.map((function(item){return{name:item.name,detail:"",containerName:item.containerName,kind:toSymbolKind(item.kind),tags:[],range:toRange(item.location.range),selectionRange:toRange(item.location.range)}}))}))},DocumentSymbolAdapter}(),DocumentLinkAdapter=/** @class */function(){function DocumentLinkAdapter(_worker){this._worker=_worker}return DocumentLinkAdapter.prototype.provideLinks=function(model,token){var resource=model.uri;return this._worker(resource).then((function(worker){return worker.findDocumentLinks(resource.toString())})).then((function(items){if(items)return{links:items.map((function(item){return{range:toRange(item.range),url:item.target}}))}}))},DocumentLinkAdapter}();function fromFormattingOptions(options){return{tabSize:options.tabSize,insertSpaces:options.insertSpaces}}var DocumentFormattingEditProvider=/** @class */function(){function DocumentFormattingEditProvider(_worker){this._worker=_worker}return DocumentFormattingEditProvider.prototype.provideDocumentFormattingEdits=function(model,options,token){var resource=model.uri;return this._worker(resource).then((function(worker){return worker.format(resource.toString(),null,fromFormattingOptions(options)).then((function(edits){if(edits&&0!==edits.length)return edits.map(toTextEdit)}))}))},DocumentFormattingEditProvider}(),DocumentRangeFormattingEditProvider=/** @class */function(){function DocumentRangeFormattingEditProvider(_worker){this._worker=_worker}return DocumentRangeFormattingEditProvider.prototype.provideDocumentRangeFormattingEdits=function(model,range,options,token){var resource=model.uri;return this._worker(resource).then((function(worker){return worker.format(resource.toString(),fromRange(range),fromFormattingOptions(options)).then((function(edits){if(edits&&0!==edits.length)return edits.map(toTextEdit)}))}))},DocumentRangeFormattingEditProvider}(),RenameAdapter=/** @class */function(){function RenameAdapter(_worker){this._worker=_worker}return RenameAdapter.prototype.provideRenameEdits=function(model,position,newName,token){var resource=model.uri;return this._worker(resource).then((function(worker){return worker.doRename(resource.toString(),fromPosition(position),newName)})).then((function(edit){return toWorkspaceEdit(edit)}))},RenameAdapter}();function toWorkspaceEdit(edit){if(edit&&edit.changes){var resourceEdits=[];for(var uri in edit.changes)for(var _uri=monaco_editor_core/* Uri */.Sf.parse(uri),_i=0,_a=edit.changes[uri];_i<_a.length;_i++){var e=_a[_i];resourceEdits.push({resource:_uri,edit:{range:toRange(e.range),text:e.newText}})}return{edits:resourceEdits}}}var FoldingRangeAdapter=/** @class */function(){function FoldingRangeAdapter(_worker){this._worker=_worker}return FoldingRangeAdapter.prototype.provideFoldingRanges=function(model,context,token){var resource=model.uri;return this._worker(resource).then((function(worker){return worker.getFoldingRanges(resource.toString(),context)})).then((function(ranges){if(ranges)return ranges.map((function(range){var result={start:range.startLine+1,end:range.endLine+1};return"undefined"!==typeof range.kind&&(result.kind=toFoldingRangeKind(range.kind)),result}))}))},FoldingRangeAdapter}();function toFoldingRangeKind(kind){switch(kind){case FoldingRangeKind.Comment:return monaco_editor_core/* languages */.Mj.FoldingRangeKind.Comment;case FoldingRangeKind.Imports:return monaco_editor_core/* languages */.Mj.FoldingRangeKind.Imports;case FoldingRangeKind.Region:return monaco_editor_core/* languages */.Mj.FoldingRangeKind.Region}}var SelectionRangeAdapter=/** @class */function(){function SelectionRangeAdapter(_worker){this._worker=_worker}return SelectionRangeAdapter.prototype.provideSelectionRanges=function(model,positions,token){var resource=model.uri;return this._worker(resource).then((function(worker){return worker.getSelectionRanges(resource.toString(),positions.map(fromPosition))})).then((function(selectionRanges){if(selectionRanges)return selectionRanges.map((function(selectionRange){var result=[];while(selectionRange)result.push({range:toRange(selectionRange.range)}),selectionRange=selectionRange.parent;return result}))}))},SelectionRangeAdapter}();// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/language/html/htmlMode.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
function setupMode1(defaults){var client=new WorkerManager(defaults),worker=function(){for(var uris=[],_i=0;_i<arguments.length;_i++)uris[_i]=arguments[_i];return client.getLanguageServiceWorker.apply(client,uris)},languageId=defaults.languageId;
// all modes
monaco_editor_core/* languages */.Mj.registerCompletionItemProvider(languageId,new CompletionAdapter(worker)),monaco_editor_core/* languages */.Mj.registerHoverProvider(languageId,new HoverAdapter(worker)),monaco_editor_core/* languages */.Mj.registerDocumentHighlightProvider(languageId,new DocumentHighlightAdapter(worker)),monaco_editor_core/* languages */.Mj.registerLinkProvider(languageId,new DocumentLinkAdapter(worker)),monaco_editor_core/* languages */.Mj.registerFoldingRangeProvider(languageId,new FoldingRangeAdapter(worker)),monaco_editor_core/* languages */.Mj.registerDocumentSymbolProvider(languageId,new DocumentSymbolAdapter(worker)),monaco_editor_core/* languages */.Mj.registerSelectionRangeProvider(languageId,new SelectionRangeAdapter(worker)),monaco_editor_core/* languages */.Mj.registerRenameProvider(languageId,new RenameAdapter(worker)),
// only html
"html"===languageId&&(monaco_editor_core/* languages */.Mj.registerDocumentFormattingEditProvider(languageId,new DocumentFormattingEditProvider(worker)),monaco_editor_core/* languages */.Mj.registerDocumentRangeFormattingEditProvider(languageId,new DocumentRangeFormattingEditProvider(worker)))}function setupMode(defaults){var disposables=[],providers=[],client=new WorkerManager(defaults);disposables.push(client);var worker=function(){for(var uris=[],_i=0;_i<arguments.length;_i++)uris[_i]=arguments[_i];return client.getLanguageServiceWorker.apply(client,uris)};function registerProviders(){var languageId=defaults.languageId,modeConfiguration=defaults.modeConfiguration;disposeAll(providers),modeConfiguration.completionItems&&providers.push(monaco_editor_core/* languages */.Mj.registerCompletionItemProvider(languageId,new CompletionAdapter(worker))),modeConfiguration.hovers&&providers.push(monaco_editor_core/* languages */.Mj.registerHoverProvider(languageId,new HoverAdapter(worker))),modeConfiguration.documentHighlights&&providers.push(monaco_editor_core/* languages */.Mj.registerDocumentHighlightProvider(languageId,new DocumentHighlightAdapter(worker))),modeConfiguration.links&&providers.push(monaco_editor_core/* languages */.Mj.registerLinkProvider(languageId,new DocumentLinkAdapter(worker))),modeConfiguration.documentSymbols&&providers.push(monaco_editor_core/* languages */.Mj.registerDocumentSymbolProvider(languageId,new DocumentSymbolAdapter(worker))),modeConfiguration.rename&&providers.push(monaco_editor_core/* languages */.Mj.registerRenameProvider(languageId,new RenameAdapter(worker))),modeConfiguration.foldingRanges&&providers.push(monaco_editor_core/* languages */.Mj.registerFoldingRangeProvider(languageId,new FoldingRangeAdapter(worker))),modeConfiguration.selectionRanges&&providers.push(monaco_editor_core/* languages */.Mj.registerSelectionRangeProvider(languageId,new SelectionRangeAdapter(worker))),modeConfiguration.documentFormattingEdits&&providers.push(monaco_editor_core/* languages */.Mj.registerDocumentFormattingEditProvider(languageId,new DocumentFormattingEditProvider(worker))),modeConfiguration.documentRangeFormattingEdits&&providers.push(monaco_editor_core/* languages */.Mj.registerDocumentRangeFormattingEditProvider(languageId,new DocumentRangeFormattingEditProvider(worker)))}return registerProviders(),disposables.push(asDisposable(providers)),asDisposable(disposables)}function asDisposable(disposables){return{dispose:function(){return disposeAll(disposables)}}}function disposeAll(disposables){while(disposables.length)disposables.pop().dispose()}
/***/}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/47570.js.map