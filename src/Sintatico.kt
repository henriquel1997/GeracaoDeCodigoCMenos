var cursor = 0

fun program(): Boolean {
    return declarationList() && cursor == tokens.size
}

fun declarationList(): Boolean {
    if(!declaration()) return false
    while(declaration()){
        //Geração de código aqui
    }
    return true
}

fun declaration(): Boolean {
    val cursorInicio = cursor
    try {
        if(varDeclaration() || statement()) return true
    }catch (e: IndexOutOfBoundsException){}

    cursor = cursorInicio
    return false
}


fun varDeclaration(): Boolean {
    val cursorInicio = cursor

    if(tokens[cursor++].tipo == Tipo.INTEIRO){
        if(tokens[cursor].tipo == Tipo.IDENTIFICADOR){
            val nome = tokens[cursor].valor
            cursor++

            var numero = "0"
            if(tokens[cursor].tipo == Tipo.RECEBE){
                cursor++
                if(tokens[cursor].tipo == Tipo.NUMERO){
                    numero = tokens[cursor].valor
                    cursor++
                }else{
                    cursor = cursorInicio
                    return false
                }
            }

            if(tokens[cursor++].tipo == Tipo.PONTOEVIRGULA){
                criarVariavel(nome, numero)
                return true
            }
        }
    }

    cursor = cursorInicio
    return false
}

fun statementList(): Boolean {
    if(!statement()) return false
    while(statement()){
        //Geração de código aqui
    }
    return true
}

fun statement(): Boolean {
    //if(expressionStmt()) return true
    if(selectionStmt()) return true
    if(iterationStmt()) return true
    //if(readStmt()) return true
    //if(writeStmt()) return true
    return false
}

fun expressionStmt(): List<Byte> {
    val cursorInicio = cursor

    val nome = variable()
    if(nome != null){
        if(tokens[cursor++].tipo == Tipo.RECEBE){
            val expressao = simpleExpression()
            if(expressao.isNotEmpty()){
                if(tokens[cursor++].tipo == Tipo.PONTOEVIRGULA){
                    return gerarCodigoRecebe(nome)
                }
            }
        }
    }
    cursor = cursorInicio
    return emptyList()
}

//TODO: Geração de código do if / else
fun selectionStmt(): Boolean {
    val cursorInicio = cursor

    if(tokens[cursor++].tipo == Tipo.IF){
        if(tokens[cursor++].tipo == Tipo.PARESQ){
            val expressao = simpleExpression()
            if(expressao.isNotEmpty()){
                if(tokens[cursor++].tipo == Tipo.PARDIR){
                    if(tokens[cursor++].tipo == Tipo.CHAVEESQ){
                        if(statementList()){
                            if(tokens[cursor++].tipo == Tipo.CHAVEDIR){
                                if(tokens[cursor++].tipo == Tipo.ELSE){
                                    if(tokens[cursor++].tipo == Tipo.CHAVEESQ) {
                                        if (statementList()) {
                                            if (tokens[cursor++].tipo == Tipo.CHAVEDIR) {
                                                return true
                                            }
                                        }
                                    }
                                }else{
                                    cursor--
                                    return true
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    cursor = cursorInicio
    return false
}

//TODO: Geração de código do While
fun iterationStmt(): Boolean {
    val cursorInicio = cursor

    if(tokens[cursor++].tipo == Tipo.WHILE){
        if(tokens[cursor++].tipo == Tipo.PARESQ){
            val expressao = simpleExpression()
            if(expressao.isNotEmpty()){
                if(tokens[cursor++].tipo == Tipo.PARDIR){
                    if(tokens[cursor++].tipo == Tipo.CHAVEESQ){
                        if(statementList()){
                            if(tokens[cursor++].tipo == Tipo.CHAVEDIR){
                                return true
                            }
                        }
                    }
                }
            }
        }
    }

    cursor = cursorInicio
    return false
}

fun readStmt(): List<Byte> {
    val cursorInicio = cursor

    if(tokens[cursor++].tipo == Tipo.READ){
        val nome = variable()
        if(nome != null){
            if(tokens[cursor++].tipo == Tipo.PONTOEVIRGULA){
                return gerarCodigoRead(nome)
            }
        }
    }

    cursor = cursorInicio
    return emptyList()
}

fun writeStmt(): List<Byte> {
    val cursorInicio = cursor

    if(tokens[cursor++].tipo == Tipo.WRITE){
        val expressao = simpleExpression()
        if(expressao.isNotEmpty()){
            if(tokens[cursor++].tipo == Tipo.PONTOEVIRGULA){
                return gerarCodigoWrite(expressao)
            }
        }
    }

    cursor = cursorInicio
    return emptyList()
}

fun variable(): String? {
    if(tokens[cursor].tipo == Tipo.IDENTIFICADOR){
        return tokens[cursor++].valor
    }
    return null
}

fun simpleExpression(): List<Byte> {
    var codigo = additiveExpression()
    if(codigo.isEmpty()) return emptyList()

    var tipo = relop()
    while(tipo != null){
        val expressaoDireita = additiveExpression()
        if(expressaoDireita.isEmpty()) return emptyList()

        codigo = gerarCodigoExpressao(codigo, tipo, expressaoDireita)

        tipo = relop()
    }

    return codigo
}

fun additiveExpression(): List<Byte> {
    var codigo = term()
    if(codigo.isEmpty()) return emptyList()

    var tipo = addop()
    while(tipo != null){
        val termoDireita = term()
        if(termoDireita.isEmpty()) return emptyList()
        codigo = gerarCodigoExpressao(codigo, tipo, termoDireita)

        tipo = addop()
    }

    return codigo
}

fun addop(): Tipo? {
    if(tokens[cursor].tipo == Tipo.SOMA || tokens[cursor].tipo == Tipo.SUBTRACAO){
        return tokens[cursor++].tipo
    }
    return null
}

fun relop(): Tipo? {
    val token = tokens[cursor]
    if(token.tipo == Tipo.MENORIGUAL || token.tipo == Tipo.MENOR ||
       token.tipo == Tipo.MAIOR      || token.tipo == Tipo.MAIORIGUAL  ||
       token.tipo == Tipo.IGUAL      || token.tipo == Tipo.DIFERENTE){

        return tokens[cursor++].tipo
    }
    return null
}

fun term(): List<Byte> {
    var codigo = factor()
    if(codigo.isEmpty()) return emptyList()

    var tipo = multop()
    while(tipo != null){
        val fatorDireita = factor()
        if(fatorDireita.isEmpty()) return emptyList()

        codigo = gerarCodigoExpressao(codigo, tipo, fatorDireita)

        tipo = multop()
    }

    return codigo
}

fun multop(): Tipo? {
    if(tokens[cursor].tipo == Tipo.MULTIPLICAO || tokens[cursor].tipo == Tipo.DIVISAO){
        return tokens[cursor++].tipo
    }
    return null
}

fun factor(): List<Byte> {

    if(tokens[cursor].tipo == Tipo.NUMERO){
        return gerarCodigoNumero(tokens[cursor++].valor)
    }

    variable()?.let { nome ->
        return gerarCodigoVariavel(nome)
    }

    val cursorInicio = cursor
    if(tokens[cursor].tipo == Tipo.PARESQ){
        cursor++
        val expressao = simpleExpression()
        if(expressao.isNotEmpty()){
            if(tokens[cursor].tipo == Tipo.PARDIR){
                cursor++
                return expressao
            }
        }

    }
    cursor = cursorInicio
    return emptyList()
}