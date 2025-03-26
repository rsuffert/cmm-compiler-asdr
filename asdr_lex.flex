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
ALGARISM=[0-9]

%%

"$TRACE_ON"   { yyparser.setDebug(true); }
"$TRACE_OFF"  { yyparser.setDebug(false); }

"int"     { return AsdrSample.INT; }
"double"  { return AsdrSample.DOUBLE; }
"boolean" { return AsdrSample.BOOLEAN; }
"void"    { return AsdrSample.VOID; }

"func"  { return AsdrSample.FUNC; }
"while" { return AsdrSample.WHILE; }
"if"    { return AsdrSample.IF; }
"else"  { return AsdrSample.ELSE; }

[:jletter:][:jletterdigit:]* { return AsdrSample.IDENT; }  

{ALGARISM}+(\.{ALGARISM}+)?  { return AsdrSample.NUM; }

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

. {
  System.err.println(String.format(
      "%sLEXICAL ERROR: invalid token '%s' at (%d,%d)%s",
      ConsoleColors.RED, yytext(), yyline, yychar, ConsoleColors.RESET
  ));
  System.exit(1);
}