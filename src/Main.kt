import java.io.File

enum class Tipo{
    IDENTIFICADOR,
    NUMERO,
    COLESQ,
    COLDIR,
    INTEIRO,
    VOID,
    PARESQ,
    PARDIR,
    VIRGULA,
    PONTOEVIRGULA,
    IF,
    ELSE,
    WHILE,
    RETURN,
    READ,
    WRITE,
    VAR,
    RECEBE,
    MENORIGUAL,
    MENOR,
    MAIOR,
    MAIORIGUAL,
    IGUAL,
    DIFERENTE,
    SOMA,
    SUBTRACAO,
    MULTIPLICAO,
    DIVISAO
}

class Token (val tipo: Tipo, val valor: String)

fun main(){
    val localArquivo = "C:\\Users\\Henrique\\IdeaProjects\\GeraçãoDeCódigoCMenos\\src\\teste.cm"
    val programa = File(localArquivo).readText()
    val tokens = analiseLexica(programa)
    tokens?.forEach {
        println("Tipo: ${it.tipo}, Valor: ${it.valor}")
    }
}

fun analiseLexica(string: String) : MutableList<Token>? {
    val lista = mutableListOf<Token>()

    var i = 0

    while(i < string.length){

        val char = string[i]

        if(i == 39){
            print("")
        }

        if(char.isWhitespace()){
            i++
            continue
        }

        when(char){
            '[' -> lista.add(Token(Tipo.COLESQ, char.toString()))
            ']' -> lista.add(Token(Tipo.COLDIR, char.toString()))
            '(' -> lista.add(Token(Tipo.PARESQ, char.toString()))
            ')' -> lista.add(Token(Tipo.PARDIR, char.toString()))
            ',' -> lista.add(Token(Tipo.VIRGULA, char.toString()))
            ';' -> lista.add(Token(Tipo.PONTOEVIRGULA, char.toString()))
            '=' -> {
                if(string[i+1] != '='){
                    lista.add(Token(Tipo.RECEBE, char.toString()))
                }else{
                    lista.add(Token(Tipo.IGUAL, "=="))
                    i++
                }
            }
            '<' -> {
                if(string[i+1] != '='){
                    lista.add(Token(Tipo.MENOR, char.toString()))
                }else{
                    lista.add(Token(Tipo.MENORIGUAL, "<="))
                    i++
                }
            }
            '>' -> {
                if(string[i+1] != '='){
                    lista.add(Token(Tipo.MAIOR, char.toString()))
                }else{
                    lista.add(Token(Tipo.MAIORIGUAL, ">="))
                    i++
                }
            }
            '+' -> lista.add(Token(Tipo.SOMA, char.toString()))
            '-' -> lista.add(Token(Tipo.SUBTRACAO, char.toString()))
            '*' -> lista.add(Token(Tipo.MULTIPLICAO, char.toString()))
            '/' -> lista.add(Token(Tipo.DIVISAO, char.toString()))

            else -> {
                val token =  identificarTokenString(string, i)
                if(token != null){
                    i += token.valor.length - 1
                    lista.add(token)
                }else{
                    println("Erro na posição $i")
                    return null
                }
            }
        }

        i++
    }

    return lista
}

fun identificarTokenString(string: String, pos: Int): Token? {

    return when {
        stringEncaixa(string, pos, "if") -> Token(Tipo.IF, "if")
        stringEncaixa(string, pos, "!=") -> Token(Tipo.DIFERENTE, "!=")
        stringEncaixa(string, pos, "int") -> Token(Tipo.INTEIRO, "int")
        stringEncaixa(string, pos, "var") -> Token(Tipo.VAR, "var")
        stringEncaixa(string, pos, "void") -> Token(Tipo.VOID, "void")
        stringEncaixa(string, pos, "else") -> Token(Tipo.ELSE, "else")
        stringEncaixa(string, pos, "read") -> Token(Tipo.READ, "read")
        stringEncaixa(string, pos, "write") -> Token(Tipo.WRITE, "write")
        stringEncaixa(string, pos, "while") -> Token(Tipo.WHILE, "while")
        stringEncaixa(string, pos, "return") -> Token(Tipo.RETURN, "return")
        else -> {
            val subString = getPalavra(pos, string)
            if(subString.isNotEmpty()){
                if(subString.toIntOrNull() != null){
                    Token(Tipo.NUMERO, subString)
                }else{
                    if(subString[0].isLetter()){
                        Token(Tipo.IDENTIFICADOR, subString)
                    }else{
                        null
                    }
                }
            }else{
                null
            }
        }
    }

}

fun stringEncaixa(string: String, pos: Int, subString: String): Boolean {
    val fim = pos + subString.length
    if(fim < string.length){
        return string.substring(pos, fim) == subString
    }
    return false
}

fun getPalavra(pos: Int, string: String): String {
    var palavra = ""

    for(i in pos..string.length){
        val char = string[i]
        if(char.isLetterOrDigit()){
            palavra += char
        }else{
            break
        }
    }

    return palavra
}