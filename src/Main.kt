import java.io.File

enum class Tipo{
    IDENTIFICADOR, NUMERO, COLESQ, COLDIR, INTEIRO, VOID, PARESQ, PARDIR, VIRGULA, PONTOEVIRGULA, IF, ELSE, WHILE,
    RETURN, READ, WRITE, VAR, RECEBE, MENORIGUAL, MENOR, MAIOR, MAIORIGUAL, IGUAL, DIFERENTE, SOMA, SUBTRACAO,
    MULTIPLICAO, DIVISAO
}

class Token (val tipo: Tipo, val valor: String, val linha:Int, val coluna:Int)

var programa = ""
var tokens = mutableListOf<Token>()

fun main(){
    val localArquivo = "C:\\Users\\Henrique\\IdeaProjects\\GeracaoDeCodigoCMenos\\src\\teste.cm"
    programa = File(localArquivo).readText()
    val tokens = analiseLexica(programa)
    tokens?.forEach {
        println("Tipo: ${it.tipo}, Valor: ${it.valor}")
    }
}