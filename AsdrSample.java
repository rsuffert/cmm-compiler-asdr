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

            // parser.Prog() // root of the grammar

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

