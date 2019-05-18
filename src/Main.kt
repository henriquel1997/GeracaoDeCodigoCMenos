import java.io.File

enum class Tipo{
    IDENTIFICADOR, NUMERO, COLESQ, COLDIR, INTEIRO, VOID, PARESQ, PARDIR, VIRGULA, PONTOEVIRGULA, IF, ELSE, WHILE, RETURN,
    READ, WRITE, RECEBE, MENORIGUAL, MENOR, MAIOR, MAIORIGUAL, IGUAL, DIFERENTE, SOMA, SUBTRACAO, MULTIPLICAO, DIVISAO, CHAVEESQ, CHAVEDIR
}

class Token (val tipo: Tipo, val valor: String, val linha:Int, val coluna:Int)

var programa = ""
var tokens = mutableListOf<Token>()

fun main(){
    val localArquivo = "C:\\Users\\Henrique\\IdeaProjects\\GeracaoDeCodigoCMenos\\src\\teste.cm"

    programa = File(localArquivo).readText()
    analiseLexica(programa)?.let {
        tokens = it
    }

    tokens.forEach {
        println("Tipo: ${it.tipo}, Valor: ${it.valor}")
    }

    println()
    println("Resultado sintático: ${program()}")

}