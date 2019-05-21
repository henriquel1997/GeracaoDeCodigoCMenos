//fun analiseLexica(string: String) : MutableList<Token>? {
//    val lista = mutableListOf<Token>()
//
//    var i = 0
//    var linha = 1
//
//    while(i < string.length){
//
//        val char = string[i]
//
//        if(char.isWhitespace()){
//
//            if(char == '\n'){
//                linha++
//            }
//
//            i++
//            continue
//        }
//
//        when(char){
//            '[' -> lista.add(Token(Tipo.COLESQ, char.toString(), linha, i))
//            ']' -> lista.add(Token(Tipo.COLDIR, char.toString(), linha, i))
//            '(' -> lista.add(Token(Tipo.PARESQ, char.toString(), linha, i))
//            ')' -> lista.add(Token(Tipo.PARDIR, char.toString(), linha, i))
//            '{' -> lista.add(Token(Tipo.CHAVEESQ, char.toString(), linha, i))
//            '}' -> lista.add(Token(Tipo.CHAVEDIR, char.toString(), linha, i))
//            ',' -> lista.add(Token(Tipo.VIRGULA, char.toString(), linha, i))
//            ';' -> lista.add(Token(Tipo.PONTOEVIRGULA, char.toString(), linha, i))
//            '=' -> {
//                if(string[i+1] != '='){
//                    lista.add(Token(Tipo.RECEBE, char.toString(), linha, i))
//                }else{
//                    lista.add(Token(Tipo.IGUAL, "==", linha, i))
//                    i++
//                }
//            }
//            '<' -> {
//                if(string[i+1] != '='){
//                    lista.add(Token(Tipo.MENOR, char.toString(), linha, i))
//                }else{
//                    lista.add(Token(Tipo.MENORIGUAL, "<=", linha, i))
//                    i++
//                }
//            }
//            '>' -> {
//                if(string[i+1] != '='){
//                    lista.add(Token(Tipo.MAIOR, char.toString(), linha, i))
//                }else{
//                    lista.add(Token(Tipo.MAIORIGUAL, ">=", linha, i))
//                    i++
//                }
//            }
//            '+' -> lista.add(Token(Tipo.SOMA, char.toString(), linha, i))
//            '-' -> lista.add(Token(Tipo.SUBTRACAO, char.toString(), linha, i))
//            '*' -> lista.add(Token(Tipo.MULTIPLICAO, char.toString(), linha, i))
//            '/' -> {
//
//                if(string[i+1] != '*'){
//                    lista.add(Token(Tipo.DIVISAO, char.toString(), linha, i))
//                }else{
//                    while(string[i] != '*' || string[i+1] != '/'){
//                        if(i < string.length - 1){
//                            i++
//                        }else{
//                            return lista
//                        }
//                    }
//                    i += 2
//                }
//            }
//
//            else -> {
//                val token =  identificarTokenString(string, i, linha)
//                if(token != null){
//                    i += token.valor.length - 1
//                    lista.add(token)
//                }else{
//                    println("Erro na linha $linha")
//                    return null
//                }
//            }
//        }
//
//        i++
//    }
//
//    return lista
//}
//
//fun identificarTokenString(string: String, pos: Int, linha: Int): Token? {
//
//    return when {
//        stringEncaixa(string, pos, "if") -> Token(Tipo.IF, "if", linha, pos)
//        stringEncaixa(string, pos, "!=") -> Token(Tipo.DIFERENTE, "!=", linha, pos)
//        stringEncaixa(string, pos, "int") -> Token(Tipo.INTEIRO, "int", linha, pos)
//        stringEncaixa(string, pos, "void") -> Token(Tipo.VOID, "void", linha, pos)
//        stringEncaixa(string, pos, "else") -> Token(Tipo.ELSE, "else", linha, pos)
//        stringEncaixa(string, pos, "read") -> Token(Tipo.READ, "read", linha, pos)
//        stringEncaixa(string, pos, "write") -> Token(Tipo.WRITE, "write", linha, pos)
//        stringEncaixa(string, pos, "while") -> Token(Tipo.WHILE, "while", linha, pos)
//        stringEncaixa(string, pos, "return") -> Token(Tipo.RETURN, "return", linha, pos)
//        else -> {
//            val subString = getPalavra(pos, string)
//            if(subString.isNotEmpty()){
//                if(subString.toIntOrNull() != null){
//                    Token(Tipo.NUMERO, subString, linha, pos)
//                }else{
//                    if(subString[0].isLetter()){
//                        Token(Tipo.IDENTIFICADOR, subString, linha, pos)
//                    }else{
//                        null
//                    }
//                }
//            }else{
//                null
//            }
//        }
//    }
//
//}
//
//fun stringEncaixa(string: String, pos: Int, subString: String): Boolean {
//    val fim = pos + subString.length
//    if(fim < string.length){
//        return string.substring(pos, fim) == subString
//    }
//    return false
//}
//
//fun getPalavra(pos: Int, string: String): String {
//    var palavra = ""
//
//    for(i in pos..string.length){
//        val char = string[i]
//        if(char.isLetterOrDigit()){
//            palavra += char
//        }else{
//            break
//        }
//    }
//
//    return palavra
//}