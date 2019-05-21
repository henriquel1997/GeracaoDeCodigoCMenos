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
    val codigoExpressao = mutableListOf<Byte>()

    if(ladoEsquerdo.isNotEmpty() && ladoDireito.isNotEmpty()){
        codigoExpressao += ladoEsquerdo

        if(operador == Tipo.SOMA || operador == Tipo.SUBTRACAO || operador == Tipo.MULTIPLICAO || operador == Tipo.DIVISAO){
            //STORE
            codigoExpressao.add(65, false)
            val pos = codigo.size + ladoEsquerdo.size + ladoDireito.size + 4
            codigoExpressao.add(pos, true)
        }

        codigoExpressao += ladoDireito

        when(operador){
            Tipo.SOMA -> {
                codigoExpressao.add(20, false)
                codigoExpressao.add(0, true)
            }

            Tipo.SUBTRACAO -> {
                codigoExpressao.add(21, false)
                codigoExpressao.add(0, true)
            }

            Tipo.MULTIPLICAO -> {
                codigoExpressao.add(22, false)
                codigoExpressao.add(0, true)
            }

            Tipo.DIVISAO -> {
                codigoExpressao.add(23, false)
                codigoExpressao.add(255, true)
            }

            Tipo.IGUAL -> {
                codigoExpressao.add(32, false)
            }

            Tipo.DIFERENTE -> {
                codigoExpressao.add(33, false)
            }

            Tipo.MAIOR -> {
                codigoExpressao.add(34, false)
            }

            Tipo.MAIORIGUAL -> {
                codigoExpressao.add(35, false)
            }

            Tipo.MENOR -> {
                codigoExpressao.add(36, false)
            }

            Tipo.MENORIGUAL -> {
                codigoExpressao.add(37, false)
            }

            else -> return listOf()
        }
    }

    return codigoExpressao
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

fun gerarCodigoWhile(expressao: List<Byte>, codigoEscopo: List<Byte>): List<Byte> {

    if(expressao.isEmpty() || codigoEscopo.isEmpty()) return emptyList()

    val codigoWhile = expressao.toMutableList()

    //Jump False
    codigoWhile.add(92, false)
    codigoWhile.add(codigo.size + expressao.size + codigoEscopo.size + 4, true)

    codigoWhile.addAll(codigoEscopo)

    //Jump
    codigoWhile.add(90, false)
    codigoWhile.add(codigo.size, true)

    return codigoWhile
}

fun gerarCodigoIf(expressao: List<Byte>, codigoEscopoIf: List<Byte>, codigoEscopoElse: List<Byte> = emptyList()): List<Byte> {
    if(expressao.isEmpty() || codigoEscopoIf.isEmpty()) return emptyList()

    val temElse = codigoEscopoElse.isNotEmpty()

    val codigoIf = expressao.toMutableList()

    //Jump False
    codigoIf.add(92, false)
    codigoIf.add(codigo.size + expressao.size + codigoEscopoIf.size + 3 + if(temElse) 3 else 0, true)

    codigoIf.addAll(codigoEscopoIf)

    if(temElse){
        //Jump
        codigoIf.add(90, false)
        codigoIf.add(codigo.size + expressao.size + codigoEscopoIf.size + codigoEscopoElse.size + 6, true)

        codigoIf.addAll(codigoEscopoElse)
    }

    return codigoIf
}

fun gerarInicioPrograma(){
    //LSP com a Memória do Programa
    codigo.add(79, false)
    codigo.add(tamanhoMemoriaPrograma, true)
}

fun gerarEspacoVariaveis(){
    if(variaveis.isNotEmpty()){
        //JUMP para o início do programa
        codigo.add(90, false)
        codigo.add(6 + (variaveis.size * 2), true)

        variaveis.forEach{ variavel ->
            codigo.add(variavel.value, true)
        }
    }
}

fun finalizarPrograma(){
    //Adicionando STOP ao fim do código
    codigo.add(97, false)

    File("$nomePrograma.OBJ").writeBytes((codigo).toByteArray())
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