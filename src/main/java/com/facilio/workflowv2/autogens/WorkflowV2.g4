grammar WorkflowV2;

parse
 : function_block EOF
 ;

function_block
 : data_type function_name_declare OPEN_PARANTHESIS (function_param)*(COMMA function_param)* CLOSE_PARANTHESIS OPEN_BRACE block CLOSE_BRACE
 ;
 
function_name_declare: VAR ;

function_param: data_type VAR ;

data_type
 : op=(VOID | DATA_TYPE_STRING | DATA_TYPE_NUMBER | DATA_TYPE_DECIMAL | DATA_TYPE_BOOLEAN)
 ;
 
 block
 : statement*
 ;

statement
 : assignment
 | if_statement
 | for_each_statement
 | log
 | stand_alone_expr SEMICOLON
 | function_return
 | OTHER {System.err.println("unknown char: " + $OTHER.text);}
 ;

assignment
 : VAR (OPEN_BRACKET atom CLOSE_BRACKET)* ASSIGN expr SEMICOLON
 ;

if_statement
 : IF condition_block (ELSE IF condition_block)* (ELSE statement_block)?
 ;

condition_block
 : OPEN_PARANTHESIS expr CLOSE_PARANTHESIS statement_block
 ;

statement_block
 : OPEN_BRACE block CLOSE_BRACE
 | statement
 ;
 
for_each_statement
 : FOR_EACH VAR(COMMA VAR)* 'in' expr statement_block
 ;

log
 : LOG expr SEMICOLON
 ;
 
function_return
 : RETURN expr SEMICOLON
 ;
 
 
expr
 : MINUS expr                           																			#unaryMinusExpr
 | NOT expr                             																			#notExpr
 | expr op=(MULT | DIV | MOD) expr  	    																		#arithmeticFirstPrecedenceExpr
 | expr op=(PLUS | MINUS) expr  	    																			#arithmeticSecondPrecedenceExpr
 | expr op=(LTEQ | GTEQ | LT | GT | EQ | NEQ) expr     			    												#relationalExpr
 | expr op=(AND | OR) expr                        																	#booleanExpr
 | stand_alone_expr																									#standAloneStatements
 | atom                                				    															#atomExpr
 | db_param																											#dbParamInitialization
 | criteria																											#criteriaInitialization
 ;
 
stand_alone_expr
 : (VAR OPEN_PARANTHESIS CLOSE_PARANTHESIS)+																		#moduleInitialization
 | 'Module' OPEN_PARANTHESIS atom CLOSE_PARANTHESIS																	#customModuleInitialization
 | 'NameSpace' OPEN_PARANTHESIS atom CLOSE_PARANTHESIS																#nameSpaceInitialization
 | list_opperations																									#listOpp
 | map_opperations																									#mapOpps
 | atom '.' VAR OPEN_PARANTHESIS (expr)*(COMMA expr)* CLOSE_PARANTHESIS												#dataTypeSpecificFunction
 ;

atom
 : OPEN_PARANTHESIS expr CLOSE_PARANTHESIS 												#paranthesisExpr
 | (INT | FLOAT)  																		#numberAtom
 | (TRUE | FALSE) 																		#booleanAtom
 | STRING         																		#stringAtom
 | NULL           						    											#nullAtom
 | VAR           						    											#varAtom
 | VAR OPEN_BRACKET atom CLOSE_BRACKET													#listSymbolOperation
 | VAR '.' VAR ('.' VAR)*																#mapSymbolOperation
 ;
 
list_opperations
 : (OPEN_BRACKET CLOSE_BRACKET)+														#listInitialisation
 ;
 
map_opperations
 : (OPEN_BRACE CLOSE_BRACE)+															#mapInitialisation
 ;	

db_param
 : OPEN_BRACE db_param_criteria (db_param_field)* (db_param_aggr)* (db_param_limit)* (db_param_range)* (db_param_sort)* CLOSE_BRACE
 ;
 
db_param_criteria
 : 'criteria' COLON criteria (COMMA)*
 ;

db_param_field
 : 'field' COLON atom (COMMA)*
 ;
 
db_param_aggr
 : 'aggregation' COLON atom (COMMA)*
 ;
 
db_param_limit
 : 'limit' COLON atom (COMMA)*
 ;
 
db_param_range
 : 'range' COLON atom 'to' atom (COMMA)*
 ;
 
db_param_sort
 : 'orderBy' COLON atom op=('asc' | 'desc') (COMMA)*
 ;
 
criteria
 : OPEN_BRACKET condition CLOSE_BRACKET									
 ;
 
condition																				
 : condition_atom																		
 | condition op=(AND | OR) condition													
 | OPEN_PARANTHESIS condition CLOSE_PARANTHESIS											
 ;
 
condition_atom
 : VAR op=(LTEQ | GTEQ | LT | GT | EQ | NEQ) atom
 ;
 
VOID : 'void';
DATA_TYPE_STRING : 'String';
DATA_TYPE_NUMBER : 'Number';
DATA_TYPE_DECIMAL : 'Decimal';
DATA_TYPE_BOOLEAN : 'Boolean';
RETURN : 'return';
  
OR : '||';
DOT : '.';
AND : '&&';
EQ : '==';
NEQ : '!=';
GT : '>';
LT : '<';
GTEQ : '>=';
LTEQ : '<=';
PLUS : '+';
MINUS : '-';
MULT : '*';
DIV : '/';
MOD : '%';
POW : '^';
NOT : '!';
COMMA : ',';

SEMICOLON : ';';
COLON : ':';
ASSIGN : '=';
OPEN_PARANTHESIS : '(';
CLOSE_PARANTHESIS : ')';
OPEN_BRACE : '{';
CLOSE_BRACE : '}';
OPEN_BRACKET : '[';
CLOSE_BRACKET : ']';

TRUE : 'true';
FALSE : 'false';
NULL : 'null';
IF : 'if';
ELSE : 'else';
FOR_EACH : 'for each';
LOG : 'log';

VAR : [a-zA-Z_] [a-zA-Z_0-9]*;
 
INT : [0-9]+;

FLOAT : [0-9]+ '.' [0-9]* | '.' [0-9]+;

STRING : '"' (~["\r\n] | '""')* '"' ;

COMMENT : '//' ~[\r\n]* -> skip ;

SPACE : [ \t\r\n] -> skip ;

OTHER : . ;