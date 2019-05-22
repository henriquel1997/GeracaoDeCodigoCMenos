var cursor = 0

fun program(): Boolean {

    try {
        gerarInicioPrograma()

        if(varDeclarationList()){
            gerarEspacoVariaveis()
        }

        return statementList()
    }catch (e: IndexOutOfBoundsException){}

    return false
}

fun varDeclarationList() : Boolean {
    if(!varDeclaration()) return false
    while(varDeclaration()){}
    return true
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

    while(cursor < tokens.size && statement()){}

    return true
}

fun statement(): Boolean {

    if(readStmt()) return true
    if(writeStmt()) return true
    if(expressionStmt()) return true
    if(selectionStmt()) return true
    if(iterationStmt()) return true

    return false
}

fun expressionStmt(): Boolean {
    val posicaoInicial = codigo.size
    val cursorInicio = cursor

    val nome = variable()
    if(nome != null){
        if(tokens[cursor++].tipo == Tipo.RECEBE){
            if(simpleExpression()){
                if(tokens[cursor++].tipo == Tipo.PONTOEVIRGULA){
                    return gerarCodigoRecebe(nome)
                }
            }
        }
    }

    desfazerCodigo(posicaoInicial)
    cursor = cursorInicio
    return false
}

fun selectionStmt(): Boolean {
    val posicaoInicial = codigo.size
    val cursorInicio = cursor

    if(tokens[cursor++].tipo == Tipo.IF){
        if(tokens[cursor++].tipo == Tipo.PARESQ){
            if(simpleExpression()){
                if(tokens[cursor++].tipo == Tipo.PARDIR){
                    if(tokens[cursor++].tipo == Tipo.CHAVEESQ){
                        val posFimIf = gerarCodigoIf()
                        if(statementList()){
                            if(tokens[cursor++].tipo == Tipo.CHAVEDIR){
                                if(tokens[cursor++].tipo == Tipo.ELSE){
                                    if(tokens[cursor++].tipo == Tipo.CHAVEESQ) {
                                        val posFimElse = gerarCodigoElse()
                                        if (statementList()) {
                                            if (tokens[cursor++].tipo == Tipo.CHAVEDIR) {
                                                //Gerar Código do IF ELSE aqui
                                                adicionarNaPosicao(posFimIf, codigo.size)
                                                adicionarNaPosicao(posFimElse, codigo.size)
                                                return true
                                            }
                                        }
                                    }
                                }else{
                                    //Gerar Código do IF comum aqui
                                    cursor--
                                    adicionarNaPosicao(posFimIf, codigo.size)
                                    return true
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    desfazerCodigo(posicaoInicial)
    cursor = cursorInicio
    return false
}

fun iterationStmt(): Boolean {
    val posicaoInicial = codigo.size
    val cursorInicio = cursor

    if(tokens[cursor++].tipo == Tipo.WHILE){
        if(tokens[cursor++].tipo == Tipo.PARESQ){
            if(simpleExpression()){
                if(tokens[cursor++].tipo == Tipo.PARDIR){
                    if(tokens[cursor++].tipo == Tipo.CHAVEESQ){
                        val posicao = gerarCodigoWhileInicio()
                        if(statementList()){
                            if(tokens[cursor++].tipo == Tipo.CHAVEDIR){
                                gerarCodigoWhileFim(posicaoInicial)
                                adicionarNaPosicao(posicao, codigo.size)
                                return true
                            }
                        }
                    }
                }
            }
        }
    }

    desfazerCodigo(posicaoInicial)
    cursor = cursorInicio
    return false
}

fun readStmt(): Boolean {
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
    return false
}

fun writeStmt(): Boolean {
    val cursorInicio = cursor

    if(tokens[cursor++].tipo == Tipo.WRITE){
        if(simpleExpression()){
            if(tokens[cursor++].tipo == Tipo.PONTOEVIRGULA){
                gerarCodigoWrite()
                return true
            }
        }
    }

    cursor = cursorInicio
    return false
}

fun variable(): String? {
    if(tokens[cursor].tipo == Tipo.IDENTIFICADOR){
        return tokens[cursor++].valor
    }
    return null
}

fun simpleExpression(): Boolean {
    val tamanhoInicial = codigo.size
    if(!additiveExpression()) return false

    var tipo = relop()
    while(tipo != null){

        if(!additiveExpression()){
            desfazerCodigo(tamanhoInicial)
            return false
        }

        gerarCodigoComparacao(tipo)

        tipo = relop()
    }

    return true
}

fun additiveExpression(): Boolean {
    val tamanhoInicial = codigo.size

    if(!term()) return false

    var tipo = addop()
    while(tipo != null){

        val posSalvar = gerarCodigoSalvarResultado()

        if(!term()){
            desfazerCodigo(tamanhoInicial)
            return false
        }

        val posicaoPrimeiroResultado = gerarCodigoOperacaoMatematica(tipo)

        adicionarNaPosicao(posSalvar, posicaoPrimeiroResultado)

        tipo = addop()
    }

    return true
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

fun term(): Boolean {
    val tamanhoInicial = codigo.size

    if(!factor()) return false

    var tipo = multop()
    while(tipo != null){

        gerarCodigoSalvarResultado()

        if(!factor()){
            desfazerCodigo(tamanhoInicial)
            return false
        }

        gerarCodigoOperacaoMatematica(tipo)

        tipo = multop()
    }

    return true
}

fun multop(): Tipo? {
    if(tokens[cursor].tipo == Tipo.MULTIPLICAO || tokens[cursor].tipo == Tipo.DIVISAO){
        return tokens[cursor++].tipo
    }
    return null
}

fun factor(): Boolean {

    if(tokens[cursor].tipo == Tipo.NUMERO){
        gerarCodigoNumero(tokens[cursor++].valor)
        return true
    }

    variable()?.let { nome ->
        return gerarCodigoVariavel(nome)
    }

    val posicaoInicio = codigo.size
    val cursorInicio = cursor
    if(tokens[cursor].tipo == Tipo.PARESQ){
        cursor++
        if(simpleExpression()){
            if(tokens[cursor].tipo == Tipo.PARDIR){
                cursor++
                return true
            }
        }

    }

    desfazerCodigo(posicaoInicio)
    cursor = cursorInicio
    return false
}