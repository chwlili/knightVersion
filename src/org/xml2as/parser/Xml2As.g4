grammar Xml2As;

xml2
:
	(
		input = inputDef pack = packDef
		| pack = packDef input = inputDef
		| pack = packDef
		| input = inputDef
	)
	(
		types += type
		| enums += enumType
		| COMMENT
	)*
;

inputDef
:
	COMMENT* C_INPUT url = STRING C_SEMICOLON
;

packDef
:
	COMMENT* C_PACKAGE pack = packName? C_SEMICOLON?
;

type
:
	metas = meta* C_TYPE typeName C_BRACE_L
	(
		typeField
		| COMMENT
	)* C_BRACE_R
;

typeField
:
	metas = meta* fieldType = typeName fieldName = typeName C_EQUALS fieldXPath =
	STRING C_SEMICOLON?
;

enumType
:
	metas = meta* C_ENUM typeName C_BRACE_L
	(
		enumField
		| COMMENT
	)* C_BRACE_R
;

enumField
:
	metas = meta* fieldName = typeName C_EQUALS fieldValue = STRING C_SEMICOLON?
;

meta
:
	C_BRACKET_L prefix = typeName
	(
		C_PAREN_L
		(
			params += metaParam
			(
				C_COMMA params += metaParam
			)*
		)? C_PAREN_R
	)? C_BRACKET_R
;

metaParam
:
	value = paramValue
;

packName
:
	typeName
	(
		'.' typeName
	)*
;

typeName
:
	C_INPUT
	| C_TYPE
	| C_ENUM
	| C_INT
	| C_UINT
	| C_BOOL
	| C_NUMBER
	| C_STRING
	| NAME
;

paramValue
:
	C_INPUT
	| C_TYPE
	| C_ENUM
	| C_INT
	| C_UINT
	| C_BOOL
	| C_NUMBER
	| C_STRING
	| NAME
	| STRING
;

C_BRACKET_L
:
	'['
;

C_BRACKET_R
:
	']'
;

C_PAREN_L
:
	'('
;

C_PAREN_R
:
	')'
;

C_BRACE_L
:
	'{'
;

C_BRACE_R
:
	'}'
;

C_ANGLE_L
:
	'<'
;

C_ANGLE_R
:
	'>'
;

C_EQUALS
:
	'='
;

C_COMMA
:
	','
;

C_SEMICOLON
:
	';'
;

C_INPUT
:
	'input'
;

C_PACKAGE
:
	'package'
;

C_TYPE
:
	'type'
;

C_ENUM
:
	'enum'
;

C_INT
:
	'int'
;

C_UINT
:
	'uint'
;

C_BOOL
:
	'Boolean'
;

C_NUMBER
:
	'Number'
;

C_STRING
:
	'String'
;

NAME
:
	(
		'a' .. 'z'
		| 'A' .. 'Z'
		| '_'
		| '$'
	)
	(
		'a' .. 'z'
		| 'A' .. 'Z'
		| '0' .. '9'
		| '_'
		| '$'
	)*
;

STRING
:
	'"' .*? '"'
;

COMMENT
:
	'/*' .*? '*/'
	| '//' .*?
	(
		'\r'
		| '\n'
	)
;

WS
:
	(
		' '
		| '\t'
		| '\r'
		| '\n'
	)+ -> skip
;
	