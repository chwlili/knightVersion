grammar Xml2As;

xml2
:
	COMMENT* C_PACKAGE packName? C_SEMICOLON?
	(
		type
		| COMMENT
	)*
;

type
:
	input? C_TYPE typeName C_BRACE_L
	(
		field
		| COMMENT
	)* C_BRACE_R
;

input
:
	C_BRACKET_L C_INPUT C_PAREN_L C_FILE C_EQUALS filePath = STRING
	(
		C_COMMA C_NODE C_EQUALS nodePath = STRING
	)? C_PAREN_R C_BRACKET_R
;

field
:
	(
		nativeType
		| listType
		| hashType
	) typeName C_EQUALS nodePath = STRING C_SEMICOLON?
;

nativeType
:
	typeName
;

listType
:
	C_LIST C_ANGLE_L typeName C_ANGLE_R
;

hashType
:
	C_HASH C_ANGLE_L typeName C_ANGLE_R
	(
		C_PAREN_L params += NAME
		(
			C_COMMA params += NAME
		)* C_PAREN_R
	)?
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
	| C_FILE
	| C_NODE
	| C_TYPE
	| C_INT
	| C_UINT
	| C_BOOL
	| C_NUMBER
	| C_STRING
	| C_LIST
	| C_HASH
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

C_FILE
:
	'file'
;

C_NODE
:
	'node'
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

C_LIST
:
	'List'
;

C_HASH
:
	'Hash'
;

C_PACKAGE
:
	'package'
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
	