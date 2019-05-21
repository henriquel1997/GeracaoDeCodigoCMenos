import java.io.File

enum class Tipo{
    IDENTIFICADOR, NUMERO, COLESQ, COLDIR, INTEIRO, PARESQ, PARDIR, VIRGULA, PONTOEVIRGULA, IF, ELSE, WHILE, RETURN,
    READ, WRITE, RECEBE, MENORIGUAL, MENOR, MAIOR, MAIORIGUAL, IGUAL, DIFERENTE, SOMA, SUBTRACAO, MULTIPLICAO, DIVISAO, CHAVEESQ, CHAVEDIR
}

class Token (val tipo: Tipo, val valor: String, val linha:Int, val coluna:Int)

var nomePrograma = "programa"
var programa = ""
val tamanhoMemoriaPrograma = 1000
var tokens = mutableListOf<Token>()

fun main(){
    //val localArquivo = "C:\\Users\\Henrique\\IdeaProjects\\GeraçãoDeCódigoCMenos\\teste.cm"
    val localArquivo = "/Users/henriquedelima/IdeaProjects/GeracaoDeCodigoCMenos/teste.cm"

    val arquivo = File(localArquivo)
    nomePrograma = arquivo.name

    val posPonto = nomePrograma.indexOfFirst { it == '.' }
    if(posPonto > 0){
        nomePrograma = nomePrograma.substring(0, posPonto)
    }

    programa = arquivo.readText()

    analiseLexica(programa)?.let {
        tokens = it
    }

    tokens.forEach {
        println("Tipo: ${it.tipo}, Valor: ${it.valor}")
    }

    if(program()){
        println("Sucesso análise sintática.")
        finalizarPrograma()
    }else{
        println("Falha na análise sintática.")
    }
}