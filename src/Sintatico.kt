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
        if(tokens[cursor++].tipo == Tipo.IDENTIFICADOR){
            if(tokens[cursor++].tipo == Tipo.PONTOEVIRGULA){
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
    if(expressionStmt()) return true
    if(selectionStmt()) return true
    if(iterationStmt()) return true
    if(readStmt()) return true
    if(writeStmt()) return true
    return false
}

fun expressionStmt(): Boolean {
    val cursorInicio = cursor

    if(variable()){
        if(tokens[cursor++].tipo == Tipo.RECEBE){
            if(!simpleExpression()){
                cursor = cursorInicio
            }
        }else{
            cursor = cursorInicio
        }
    }else{
        cursor = cursorInicio
    }

    if(tokens[cursor++].tipo == Tipo.PONTOEVIRGULA){
        return true
    }
    cursor = cursorInicio
    return false
}

fun selectionStmt(): Boolean {
    val cursorInicio = cursor

    if(tokens[cursor++].tipo == Tipo.IF){
        if(tokens[cursor++].tipo == Tipo.PARESQ){
            if(simpleExpression()){
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

fun iterationStmt(): Boolean {
    val cursorInicio = cursor

    if(tokens[cursor++].tipo == Tipo.WHILE){
        if(tokens[cursor++].tipo == Tipo.PARESQ){
            if(simpleExpression()){
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

fun readStmt(): Boolean {
    val cursorInicio = cursor

    if(tokens[cursor++].tipo == Tipo.READ){
        if(variable()){
            if(tokens[cursor++].tipo == Tipo.PONTOEVIRGULA){
                return true
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
                return true
            }
        }
    }

    cursor = cursorInicio
    return false
}

fun variable(): Boolean {
    if(tokens[cursor++].tipo == Tipo.IDENTIFICADOR){
        return true
    }
    cursor--
    return false
}

fun simpleExpression(): Boolean {
    if(!additiveExpression()) return false

    while(relop()){
        if(!additiveExpression()){
            return false
        }
    }

    return true
}

fun additiveExpression(): Boolean {
    if(!term()) return false

    while(addop()){
        if(!term()){
            return false
        }
    }

    return true
}

fun addop(): Boolean{
    val token = tokens[cursor]
    if(token.tipo == Tipo.SOMA || token.tipo == Tipo.SUBTRACAO){
        cursor++
        return true
    }
    return false
}

fun relop(): Boolean {
    val token = tokens[cursor]
    if(token.tipo == Tipo.MENORIGUAL || token.tipo == Tipo.MENOR ||
       token.tipo == Tipo.MAIOR      || token.tipo == Tipo.MAIORIGUAL  ||
       token.tipo == Tipo.IGUAL      || token.tipo == Tipo.DIFERENTE){

        cursor++
        return true
    }
    return false
}

fun term(): Boolean {
    if(!factor()) return false

    while(multop()){
        if(!factor()){
            return false
        }
    }

    return true
}

fun multop(): Boolean {
    if(tokens[cursor].tipo == Tipo.MULTIPLICAO || tokens[cursor].tipo == Tipo.DIVISAO){
        cursor++
        return true
    }
    return false
}

fun factor(): Boolean {

    if(tokens[cursor].tipo == Tipo.NUMERO){
        cursor++
        return true
    }

    if(variable()) return true

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
    cursor = cursorInicio
    return false
}