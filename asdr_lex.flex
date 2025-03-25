%%

%{
  private AsdrSample yyparser;

  public Yylex(java.io.Reader r, AsdrSample yyparser) {
    this(r);
    this.yyparser = yyparser;
  }


%} 

%integer
%line
%char

WHITE_SPACE_CHAR=[\n\r\ \t\b\012]

%%

"$TRACE_ON"   { yyparser.setDebug(true); }
"$TRACE_OFF"  { yyparser.setDebug(false); }

"int"     |
"double"  |
"boolean" { return AsdrSample.TYPE; }
"VOID"    { return AsdrSample.VOID; }

"FUNC"  { return AsdrSample.FUNC; }
"while" { return AsdrSample.WHILE; }
"if"    { return AsdrSample.IF; }
"else"  { return AsdrSample.ELSE; }

[:jletter:][:jletterdigit:]* { return AsdrSample.IDENT; }  

[:jdigit:]+(\.[:jdigit:]+)? 	{ return AsdrSample.NUM; }

";" |
"(" |
")" |
"{" |
"}" |
"," |
"=" |
"+" |
"-" |
"*" |
"/" { return yytext().charAt(0); } 

{WHITE_SPACE_CHAR}+ { }

. { System.out.println("Erro lexico: caracter invalido: <" + yytext() + ">"); }