import java.io.*;

public class AsdrSample {
    public static final int BASE_TOKEN_NUM = 301;

    public static final int TYPE = 301;
    public static final int VOID = 302;

    public static final int IDENT = 303;

    public static final int FUNC  = 304;
    public static final int WHILE = 305;
    public static final int IF    = 306;
    public static final int ELSE  = 307;

    public static final int NUM = 308;

    public static final String tokenList[] = {
        "TYPE", "VOID", "IDENT", "FUNC", "WHILE", "IF", "ELSE", "NUM"
    };
                                      
    /* referencia ao objeto Scanner gerado pelo JFLEX */
    private Yylex lexer;

    public ParserVal yylval;

    private static int laToken;
    private boolean debug;
  
    /* construtor da classe */
    public AsdrSample (Reader r) {
        lexer = new Yylex (r, this);
    }

    // ====================================== GRAMMAR ======================================
    private void Prog() {
        if (debug) System.out.println("Prog --> ListaDecl");
        ListaDecl();
    }

    private void ListaDecl() {
        if (laToken == TYPE) {
            if (debug) System.out.println("ListaDecl --> DeclVar ListaDecl");
            DeclVar();
            ListaDecl();
        }
        else if (laToken == FUNC) {
            if (debug) System.out.println("ListaDecl --> DeclFunc ListaDecl");
            DeclFun();
            ListaDecl();
        }
        else {
            // aceitar como vazio
            if (debug) System.out.println("ListaDecl --> *vazio*");
        }
    }

    private void DeclVar() {
        if (laToken == TYPE) {
            if (debug) System.out.println("DeclVar --> TYPE IDENT ;");
            verifica(TYPE);
            ListaIdent();
            verifica(';');
            DeclVar();
        }
        else {
            // aceitar como vazio
            if (debug) System.out.println("DeclVar --> *vazio*");
        }
    }

    private void DeclFun() {
        if (laToken == FUNC) {
            if (debug) System.out.println("DeclFun --> FUNC tipoOuVoid IDENT '(' FormalPar ')' '{' DeclVar ListaCmd '}' DeclFun");
            verifica(FUNC);
            tipoOuVoid();
            verifica(IDENT);
            verifica('(');
            FormalPar();
            verifica(')');
            verifica('{');
            DeclVar();
            ListaCmd();
            verifica('}');
            DeclFun();
        } else {
            // aceitar como vazio
            if (debug) System.out.println("DeclFun --> *vazio*");
        }
    }

    private void ListaIdent() {
        if (laToken == IDENT) {
            if (debug) System.out.println("ListaIdent --> IDENT RestoListaIdent");
            verifica(IDENT);
            RestoListaIdent();
        }
        else {
            this.yyerror("esperado IDENT na entrada");
        }
    }

    private void RestoListaIdent() {
        if (laToken == ',') {
            if (debug) System.out.println("RestoListaIdent --> , ListaIdent");
            verifica(',');
            ListaIdent();
        } else {
            // aceitar como vazio
            if (debug) System.out.println("RestoListaIdent --> *vazio*");
        }
    }

    private void tipoOuVoid() {
        if (laToken == TYPE) {
            if (debug) System.out.println("tipoOuVoid --> TYPE");
            verifica(TYPE);
        } else if (laToken == VOID) {
            if (debug) System.out.println("tipoOuVoid --> VOID");
            verifica(VOID);
        } else {
            this.yyerror("esperado TYPE ou VOID na entrada");
        }
    }

    private void FormalPar() {
        if (laToken == TYPE) {
            if (debug) System.out.println("FormalPar --> paramList");
            paramList();
        }
        else {
            // aceitar como vazio
            if (debug) System.out.println("FormalPar --> *vazio*");
        }
    }

    private void ListaCmd() {
        if (cmdFirst()) {
            if (debug) System.out.println("ListaCmd --> Cmd ListaCmd");
            Cmd();
            ListaCmd();
        }
        else {
            // aceitar como vazio
            if (debug) System.out.println("ListaCmd --> *vazio*");
        }
    }

    private void paramList() {
        if (laToken == TYPE) {
            if (debug) System.out.println("paramList --> TYPE IDENT RestoParamList");
            verifica(TYPE);
            verifica(IDENT);
            RestoParamList();
        } else {
            this.yyerror("esperado TYPE na entrada");
        }
    }

    private void RestoParamList() {
        if (laToken == ',') {
            if (debug) System.out.println("RestoParamList --> ',' paramList");
            verifica(',');
            paramList();
        }
        else {
            // aceitar como vazio
            if (debug) System.out.println("RestoParamList --> *vazio*");
        }
    }

    private void Bloco() {
        verifica('{');
        ListaCmd();
        verifica('}');
    }

    private void Cmd() {
        if (blocoFirst()) {
            if (debug) System.out.println("Cmd --> Bloco");
            Bloco();
        }
        else if (laToken == WHILE) {
            if (debug) System.out.println("Cmd --> WHILE '(' E ')' Cmd");
            verifica(WHILE);
            verifica('(');
            E();
            verifica(')');
            Cmd();
        }
        else if (laToken == IDENT) {
            if (debug) System.out.println("Cmd --> IDENT '=' E");
            verifica(IDENT);
            verifica('=');
            E();
        }
        else if (laToken == IF) {
            if (debug) System.out.println("Cmd --> IF '(' E ')' Cmd RestoIf");
            verifica(IF);
            verifica('(');
            E();
            verifica(')');
            Cmd();
            RestoIf();
        }
        else {
            this.yyerror("esperado BLOCO, WHILE, IDENT ou IF na entrada");
        }
    }

    private void E() {
        T();
        RE();
    }

    private void RestoIf() {
        if (laToken == ELSE) {
            if (debug) System.out.println("RestoIf --> ELSE Cmd");
            verifica(ELSE);
            Cmd();
        }
        else {
            // aceitar como vazio
            if (debug) System.out.println("RestoIf --> *vazio*");
        }

    }

    public void T() {
        F();
        RT();
    }

    public void RE() {
        if (laToken == '+') {
            if (debug) System.out.println("RE --> '+' T RE");
            verifica('+');
            T();
            RE();
        }
        else if (laToken == '-') {
            if (debug) System.out.println("RE --> '-' T RE");
            verifica('-');
            T();
            RE();
        }
        else {
            // aceitar como vazio
            if (debug) System.out.println("RE --> *vazio*");
        }
    }

    public void F() {
        if (laToken == IDENT) {
            if (debug) System.out.println("F --> IDENT");
            verifica(IDENT);
        }
        else if (laToken == NUM) {
            if (debug) System.out.println("F --> NUM");
            verifica(NUM);
        }
        else if (laToken == '(') {
            if (debug) System.out.println("F --> '(' E ')'");
            verifica('(');
            E();
            verifica(')');
        }
        else {
            this.yyerror("esperado IDENT, NUM ou '(' na entrada");
        }
    }

    public void RT() {
        if (laToken == '*') {
            if (debug) System.out.println("RT --> '*' F RT");
            verifica('*');
            F();
            RT();
        }
        else if (laToken == '/') {
            if (debug) System.out.println("RT --> '/' F RT");
            verifica('/');
            F();
            RT();
        }
        else {
            // aceitar como vazio
            if (debug) System.out.println("RT --> *vazio*");
        }
    }

    // ====================================== HELPERS ======================================
    private boolean cmdFirst() {
        return laToken == IDENT || laToken == WHILE || laToken == IF || blocoFirst();
    }

    private boolean blocoFirst() {
        return laToken == '{';
    }
    
    private void verifica(int expected) {
        if (laToken == expected) {
            laToken = this.yylex();
            return;
        }

        String expStr = (
            (expected < BASE_TOKEN_NUM )?
            ""+(char)expected :
            tokenList[expected-BASE_TOKEN_NUM]
        );
            
        String laStr = (
            (laToken < BASE_TOKEN_NUM )?
            Character.toString((char) laToken) :
            tokenList[laToken-BASE_TOKEN_NUM]
        );

        this.yyerror("esperado token: " + expStr + " na entrada: " + laStr);
    }

    /* metodo de acesso ao Scanner gerado pelo JFLEX */
    private int yylex() {
        int retVal = -1;
        try {
            yylval = new ParserVal(0); //zera o valor do token
            retVal = lexer.yylex(); //le a entrada do arquivo e retorna um token
        } catch (IOException e) {
            System.err.println("IO Error:" + e);
        }
        return retVal; //retorna o token para o Parser 
    }

    /* metodo de manipulacao de erros de sintaxe */
    public void yyerror (String error) {
        System.err.println("Erro: " + error);
        System.err.println("Entrada rejeitada");
        System.out.println("\n\nFalhou!!!");
        System.exit(1);
    }

    public void setDebug(boolean trace) {
        debug = true;
    }

    /**
     * Runs the scanner on input files.
     *
     * This main method is the debugging routine for the scanner.
     * It prints debugging information about each returned token to
     * System.out until the end of file is reached, or an error occured.
     *
     * @param args   the command line, contains the filenames to run
     *               the scanner on.
     */
    public static void main(String[] args) {
        AsdrSample parser = null;
        try {
            if (args.length == 0)
                parser = new AsdrSample(new InputStreamReader(System.in));
            else 
                parser = new  AsdrSample( new java.io.FileReader(args[0]));

            parser.setDebug(false);
            laToken = parser.yylex();          

            parser.Prog(); // root of the grammar

            if (laToken== Yylex.YYEOF)
                System.out.println("\n\nSucesso!");
            else     
                System.out.println("\n\nFalhou - esperado EOF.");               
        }
        catch (java.io.FileNotFoundException e) {
            System.out.println("File not found : \""+args[0]+"\"");
        }
    }
}

