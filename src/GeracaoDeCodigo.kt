import java.io.File
import java.lang.NumberFormatException
import java.nio.ByteBuffer
import kotlin.experimental.and

val codigo = mutableListOf<Byte>()
val variaveis = hashMapOf<String, Int>()

fun criarVariavel(nome: String, valor: String){
    if(!variaveis.contains(nome)){
        try {
            val inteiro = Integer.parseUnsignedInt(valor)
            if(inteiro in 0..65535){
                variaveis[nome] = inteiro
            }
        }catch (e: NumberFormatException){}
    }
}

fun getPosicaoVariavel(nome: String) = variaveis.keys.indexOf(nome)

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