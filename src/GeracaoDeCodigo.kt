import java.io.File
import java.lang.NumberFormatException
import java.nio.ByteBuffer
import kotlin.experimental.and

val codigo = mutableListOf<Byte>()
val variaveis = hashMapOf<String, Int>()

fun criarVariavel(nome: String, valor: String){
    if(!variaveis.contains(nome)){
        stringToInt(valor)?.let { variaveis[nome] = it }
    }
}

fun getPosicaoVariavel(nome: String): Int? {
    val pos = variaveis.keys.indexOf(nome)
    return if(pos >= 0){
        pos + 6
    }else{
        null
    }
}

fun gerarCodigoRecebe(nomeVariavel: String): List<Byte> {
    val codigo = mutableListOf<Byte>()
    getPosicaoVariavel(nomeVariavel)?.let { pos ->
        codigo.add(65, false)
        codigo.add(pos, true)
    }
    return codigo
}

fun gerarCodigoExpressao(ladoEsquerdo: List<Byte>, operador: Tipo, ladoDireito: List<Byte>): List<Byte> {
    val codigo = mutableListOf<Byte>()

    codigo += ladoEsquerdo

    if(operador == Tipo.SOMA || operador == Tipo.SUBTRACAO || operador == Tipo.MULTIPLICAO || operador == Tipo.DIVISAO){
        //STORE
        codigo.add(65, false)
        //TODO: Definir como ele vai saber a posição que ele deve salvar o resultado da primeira operação
        val pos = 0
        codigo.add(pos, true)
    }

    codigo += ladoDireito

    when(operador){
        Tipo.SOMA -> {
            codigo.add(20, false)
            codigo.add(0, true)
        }

        Tipo.SUBTRACAO -> {
            codigo.add(21, false)
            codigo.add(0, true)
        }

        Tipo.MULTIPLICAO -> {
            codigo.add(22, false)
            codigo.add(0, true)
        }

        Tipo.DIVISAO -> {
            codigo.add(23, false)
            codigo.add(255, true)
        }

        Tipo.IGUAL -> {
            codigo.add(22, false)
        }

        else -> return listOf()
    }

    return codigo
}

fun gerarCodigoNumero(valor: String): List<Byte> {
    val codigo = mutableListOf<Byte>()
    stringToInt(valor)?.let { inteiro ->
        //LDI
        codigo.add(68, false)
        codigo.add(inteiro, true)
    }
    return codigo
}

fun gerarCodigoVariavel(nome: String): List<Byte> {
    val codigo = mutableListOf<Byte>()
    getPosicaoVariavel(nome)?.let { posicao ->
        //LOAD
        codigo.add(64, false)
        codigo.add(posicao, true)

    }
    return codigo
}

fun gerarCodigoRead(nomeVariavel: String): List<Byte> {
    val codigo = mutableListOf<Byte>()
    getPosicaoVariavel(nomeVariavel)?.let { posicao ->
        //INPUT
        codigo.add(87, false)
        //STORE
        codigo.add(65, false)
        codigo.add(posicao, true)
    }
    return codigo
}

fun gerarCodigoWrite(expressao: List<Byte>): List<Byte> {
    val codigo = expressao.toMutableList()
    if(codigo.isNotEmpty()){
        //OUTPUT
        codigo.add(88, false)
    }
    return codigo
}

fun gerarPrograma(){

    val codigoVariaveis = mutableListOf<Byte>()

    //LSP com a Memória do Programa
    codigoVariaveis.add(79, false)
    codigoVariaveis.add(tamanhoMemoriaPrograma, true)

    if(variaveis.isNotEmpty()){
        //JUMP para o início do programa
        codigoVariaveis.add(90, false)
        codigoVariaveis.add(6 + (variaveis.size * 2), true)

        variaveis.forEach{ variavel ->
            codigoVariaveis.add(variavel.value, true)
        }
    }

    //Adicionando STOP ao fim do código
    codigo.add(97, false)

    File("$nomePrograma.OBJ").writeBytes((codigoVariaveis + codigo).toByteArray())
}

fun stringToInt(valor: String): Int? {
    try {
        val inteiro = Integer.parseUnsignedInt(valor)
        if(inteiro in 0..65535){
            return inteiro
        }
    }catch (e: NumberFormatException){}

    return null
}

fun intToByteList(int:Int): List<Byte> {
    val array = ByteBuffer.allocate(4).putInt(int).array()
    return listOf(array[3], array[2])
}

fun MutableList<Byte>.add(int: Int, twoBytes: Boolean){
    if(twoBytes){
        if(int in 0..65535){
            this += intToByteList(int)
        }
    }else{
        if(int in 0..255){
            this.add(int.toByte().and(Byte.MAX_VALUE))
        }
    }
}