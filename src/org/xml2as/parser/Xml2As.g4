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
	typeMeta? C_TYPE typeName C_BRACE_L
	(
		field
		| COMMENT
	)* C_BRACE_R
;

typeMeta
:
	C_BRACKET_L C_MAIN C_PAREN_L xpath = STRING C_PAREN_R C_BRACKET_R
;

field
:
	fieldMeta fieldType = typeName fieldName = typeName C_EQUALS fieldXPath =
	STRING C_SEMICOLON?
;

fieldMeta
:
	(
		listMeta
		| sliceMeta
	)*
;

listMeta
:
	C_BRACKET_L C_LIST
	(
		C_PAREN_L
		(
			key += typeName
			(
				C_COMMA key += typeName
			)*
		)? C_PAREN_R
	)? C_BRACKET_R
;

sliceMeta
:
	C_BRACKET_L prefix = C_SLICE C_PAREN_L sliceChar = STRING C_PAREN_R
	C_BRACKET_R
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
	| C_MAIN
	| C_TYPE
	| C_INT
	| C_UINT
	| C_BOOL
	| C_NUMBER
	| C_STRING
	| C_LIST
	| NAME
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

C_MAIN
:
	'Main'
;

C_LIST
:
	'List'
;

C_SLICE
:
	'Slice'
;

C_TYPE
:
	'type'
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
	)
	(
		'a' .. 'z'
		| 'A' .. 'Z'
		| '0' .. '9'
		| '_'
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
	