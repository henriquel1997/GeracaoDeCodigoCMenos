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

    if(getToken(cursor++)?.tipo == Tipo.INTEIRO){
        val identificador = getToken(cursor)
        if(identificador?.tipo == Tipo.IDENTIFICADOR){
            val nome = identificador.valor
            cursor++

            var numero = "0"
            if(getToken(cursor)?.tipo == Tipo.RECEBE){
                cursor++
                val tokenNumero = getToken(cursor)
                if(tokenNumero?.tipo == Tipo.NUMERO){
                    numero = tokenNumero.valor
                    cursor++
                }else{
                    cursor = cursorInicio
                    return false
                }
            }

            if(getToken(cursor++)?.tipo == Tipo.PONTOEVIRGULA){
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
        if(getToken(cursor++)?.tipo == Tipo.RECEBE){
            if(simpleExpression()){
                if(getToken(cursor++)?.tipo == Tipo.PONTOEVIRGULA){
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

    if(getToken(cursor++)?.tipo == Tipo.IF){
        if(getToken(cursor++)?.tipo == Tipo.PARESQ){
            if(simpleExpression()){
                if(getToken(cursor++)?.tipo == Tipo.PARDIR){
                    if(getToken(cursor++)?.tipo == Tipo.CHAVEESQ){
                        val posFimIf = gerarCodigoIf()
                        if(statementList()){
                            if(getToken(cursor++)?.tipo == Tipo.CHAVEDIR){
                                if(getToken(cursor++)?.tipo == Tipo.ELSE){
                                    if(getToken(cursor++)?.tipo == Tipo.CHAVEESQ) {
                                        val posFimElse = gerarCodigoElse()
                                        if (statementList()) {
                                            if (getToken(cursor++)?.tipo == Tipo.CHAVEDIR) {
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

    if(getToken(cursor++)?.tipo == Tipo.WHILE){
        if(getToken(cursor++)?.tipo == Tipo.PARESQ){
            if(simpleExpression()){
                if(getToken(cursor++)?.tipo == Tipo.PARDIR){
                    if(getToken(cursor++)?.tipo == Tipo.CHAVEESQ){
                        val posicao = gerarCodigoWhileInicio()
                        if(statementList()){
                            if(getToken(cursor++)?.tipo == Tipo.CHAVEDIR){
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

    if(getToken(cursor++)?.tipo == Tipo.READ){
        val nome = variable()
        if(nome != null){
            if(getToken(cursor++)?.tipo == Tipo.PONTOEVIRGULA){
                return gerarCodigoRead(nome)
            }
        }
    }

    cursor = cursorInicio
    return false
}

fun writeStmt(): Boolean {
    val cursorInicio = cursor

    if(getToken(cursor++)?.tipo == Tipo.WRITE){
        if(simpleExpression()){
            if(getToken(cursor++)?.tipo == Tipo.PONTOEVIRGULA){
                gerarCodigoWrite()
                return true
            }
        }
    }

    cursor = cursorInicio
    return false
}

fun variable(): String? {
    if(getToken(cursor)?.tipo == Tipo.IDENTIFICADOR){
        return getToken(cursor++)?.valor
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
    if(getToken(cursor)?.tipo == Tipo.SOMA || getToken(cursor)?.tipo == Tipo.SUBTRACAO){
        return getToken(cursor++)?.tipo
    }
    return null
}

fun relop(): Tipo? {
    val token = getToken(cursor)
    if(token?.tipo == Tipo.MENORIGUAL || token?.tipo == Tipo.MENOR ||
       token?.tipo == Tipo.MAIOR      || token?.tipo == Tipo.MAIORIGUAL  ||
       token?.tipo == Tipo.IGUAL      || token?.tipo == Tipo.DIFERENTE){
        cursor++
        return token.tipo
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
    if(getToken(cursor)?.tipo == Tipo.MULTIPLICAO || getToken(cursor)?.tipo == Tipo.DIVISAO){
        return getToken(cursor++)?.tipo
    }
    return null
}

fun factor(): Boolean {

    val numero = getToken(cursor)
    if(numero?.tipo == Tipo.NUMERO){
        cursor++
        return gerarCodigoNumero(numero.valor)
    }

    variable()?.let { nome ->
        return gerarCodigoVariavel(nome)
    }

    val posicaoInicio = codigo.size
    val cursorInicio = cursor
    if(getToken(cursor)?.tipo == Tipo.PARESQ){
        cursor++
        if(simpleExpression()){
            if(getToken(cursor)?.tipo == Tipo.PARDIR){
                cursor++
                return true
            }
        }

    }

    desfazerCodigo(posicaoInicio)
    cursor = cursorInicio
    return false
}

fun getToken(position: Int): Token? {
    if(position < tokens.size){
        return tokens[position]
    }
    return null
}