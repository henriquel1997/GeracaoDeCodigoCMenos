import java.io.File
import java.lang.NumberFormatException
import java.nio.ByteBuffer
import kotlin.experimental.and

var codigo = mutableListOf<Byte>()
val variaveis = hashMapOf<String, Int>()

fun desfazerCodigo(tamanhoAnteriorCodigo: Int){
    codigo = codigo.subList(0, tamanhoAnteriorCodigo)
}

fun adicionarNaPosicao(posicao: Int, valor: Int){
    val bytes = intToByteList(valor)
    codigo[posicao] = bytes[0]
    codigo[posicao + 1] = bytes[1]
}

fun criarVariavel(nome: String, valor: String){
    if(!variaveis.contains(nome)){
        stringToInt(valor)?.let { variaveis[nome] = it }
    }
}

fun getPosicaoVariavel(nome: String): Int? {
    val pos = variaveis.keys.indexOf(nome)
    return if(pos >= 0){
        (2 * pos) + 6
    }else{
        null
    }
}

fun gerarCodigoRecebe(nomeVariavel: String): Boolean {
    getPosicaoVariavel(nomeVariavel)?.let { pos ->
        codigo.add(65, false)
        codigo.add(pos, true)
        return true
    }
    return false
}

fun gerarCodigoSalvarResultado(): Int {
    //STORE - Guarda o topo da pilha como inteiro para realizar a operação matemática
    codigo.add(65, false)
    codigo.add(0, true)
    //Retorna a posição no código onde deve ser escrito o parametro da operação
    return codigo.size - 2
}

fun gerarCodigoOperacaoMatematica(operador: Tipo): Int {
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

        else -> {}
    }

    return codigo.size - 2
}

fun gerarCodigoComparacao(operador: Tipo){
    when(operador){
        Tipo.IGUAL -> {
            codigo.add(32, false)
        }

        Tipo.DIFERENTE -> {
            codigo.add(33, false)
        }

        Tipo.MAIOR -> {
            codigo.add(34, false)
        }

        Tipo.MAIORIGUAL -> {
            codigo.add(35, false)
        }

        Tipo.MENOR -> {
            codigo.add(36, false)
        }

        Tipo.MENORIGUAL -> {
            codigo.add(37, false)
        }

        else -> {}
    }
}

fun gerarCodigoNumero(valor: String): Boolean{
    stringToInt(valor)?.let { inteiro ->
        //LDI
        codigo.add(68, false)
        codigo.add(inteiro, true)
        return true
    }
    return false
}

fun gerarCodigoVariavel(nome: String): Boolean {
    getPosicaoVariavel(nome)?.let { posicao ->
        //LOAD
        codigo.add(64, false)
        codigo.add(posicao, true)
        return true
    }
    return false
}

fun gerarCodigoRead(nomeVariavel: String): Boolean {
    getPosicaoVariavel(nomeVariavel)?.let { posicao ->
        //INPUT
        codigo.add(87, false)
        //STORE
        codigo.add(65, false)
        codigo.add(posicao, true)
        return true
    }
    return false
}

fun gerarCodigoWrite(){
    //OUTPUT
    codigo.add(88, false)
}

fun gerarCodigoWhileInicio(): Int{
    //Jump False
    codigo.add(92, false)
    codigo.add(0, true)


    return codigo.size -2
}

fun gerarCodigoWhileFim(inicioWhile: Int){
    //Jump
    codigo.add(90, false)
    codigo.add(inicioWhile, true)
}

fun gerarCodigoIf(): Int {
    //Jump False
    codigo.add(92, false)
    codigo.add(0, true)
    return codigo.size - 2
}

fun gerarCodigoElse(): Int {
    //Jump
    codigo.add(90, false)
    codigo.add(0, true)
    return codigo.size - 2
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