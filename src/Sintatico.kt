var cursor = 0

fun program(): Boolean {
    return declarationList()
}

fun declarationList(): Boolean {
    if(!declaration()) return false
    while(declaration()){
        //Geração de código aqui
    }
    return true
}

fun declaration(): Boolean {
    if(varDeclaration() || funDeclaration()) return true
    return false
}


fun varDeclaration(): Boolean {
    val cursorInicio = cursor

    if(typeSpecifier()){
        if(tokens[cursor++].tipo == Tipo.IDENTIFICADOR){

            if(tokens[cursor].tipo == Tipo.COLESQ){
                cursor++

                if(tokens[cursor++].tipo == Tipo.NUMERO){
                    if(tokens[cursor++].tipo == Tipo.COLDIR){
                        //Geração de código aqui
                    }else{
                        cursor = cursorInicio
                        return false
                    }
                }else{
                    cursor = cursorInicio
                    return false
                }
            }

            if(tokens[cursor++].tipo == Tipo.PONTOEVIRGULA){
                return true
            }

        }
    }

    cursor = cursorInicio
    return false
}

fun typeSpecifier(): Boolean {
    val token = tokens[cursor]
    if(token.tipo == Tipo.INTEIRO || token.tipo == Tipo.VOID){
        cursor++
        return true
    }
    return false
}

fun funDeclaration(): Boolean {
    val cursorInicio = cursor

    if(!typeSpecifier()) return false

    if(tokens[cursor++].tipo != Tipo.IDENTIFICADOR){
        cursor = cursorInicio
        return false
    }
    if(tokens[cursor++].tipo != Tipo.PARESQ){
        cursor = cursorInicio
        return false
    }
    if(!params()){
        cursor = cursorInicio
        return false
    }
    if(tokens[cursor++].tipo != Tipo.PARDIR){
        cursor = cursorInicio
        return false
    }
    if(!compoundStmt()){
        cursor = cursorInicio
        return false
    }

    return true
}

fun params(): Boolean {
    if(tokens[cursor].tipo == Tipo.VOID){
        cursor++
        return true
    }

    if(paramList()) return true

    return false
}

fun paramList(): Boolean {
    val cursorInicio = cursor
    if(!param()) return false
    if(tokens[cursor++].tipo != Tipo.VIRGULA){
        cursor--
        return false
    }

    while(param()){
        if(tokens[cursor++].tipo != Tipo.VIRGULA){
            cursor = cursorInicio
            return false
        }
    }

    return true
}

fun param(): Boolean {
    val cursorInicio = cursor

    if(typeSpecifier()){
        if(tokens[cursor++].tipo == Tipo.IDENTIFICADOR){

            if(tokens[cursor].tipo == Tipo.COLESQ){
                cursor++

                return if(tokens[cursor++].tipo == Tipo.COLDIR){
                    true
                }else{
                    cursor = cursorInicio
                    false
                }
            }

            return true
        }
    }

    cursor = cursorInicio
    return false
}

fun compoundStmt(): Boolean {
    val cursorInicio = cursor

    if(tokens[cursor++].tipo == Tipo.CHAVEESQ){
        if(localDeclarations()){
            if(statementList()){
                if(tokens[cursor++].tipo == Tipo.CHAVEDIR){
                    return true
                }
            }
        }

    }

    cursor = cursorInicio
    return false
}

fun localDeclarations(): Boolean {
    if(!varDeclaration()) return false
    while(varDeclaration()){
        //Geração de código aqui
    }
    return true
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
    if(compoundStmt()) return true
    if(selectionStmt()) return true
    if(iterationStmt()) return true
    if(returnStmt()) return true
    if(readStmt()) return true
    if(writeStmt()) return true
    return false
}

fun expressionStmt(): Boolean {
    expression()
    if(tokens[cursor++].tipo == Tipo.PONTOEVIRGULA){
        return true
    }
    cursor--
    return false
}

fun selectionStmt(): Boolean {
    val cursorInicio = cursor

    if(tokens[cursor++].tipo == Tipo.IF){
        if(tokens[cursor++].tipo == Tipo.PARESQ){
            if(expression()){
                if(tokens[cursor++].tipo == Tipo.PARDIR){
                    if(statement()){
                        if(tokens[cursor++].tipo == Tipo.ELSE){
                            if(statement()){
                                return true
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

    cursor = cursorInicio
    return false
}

fun iterationStmt(): Boolean {
    val cursorInicio = cursor

    if(tokens[cursor++].tipo == Tipo.WHILE){
        if(tokens[cursor++].tipo == Tipo.PARESQ){
            if(expression()){
                if(tokens[cursor++].tipo == Tipo.PARDIR){
                    if(statement()){
                        return true
                    }
                }
            }
        }
    }

    cursor = cursorInicio
    return false
}

fun returnStmt(): Boolean {
    val cursorInicio = cursor

    if(tokens[cursor++].tipo == Tipo.RETURN){
        if(expression()){
            //Geração de código aqui
        }

        if(tokens[cursor++].tipo == Tipo.PONTOEVIRGULA){
            return true
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
        if(variable()){
            if(tokens[cursor++].tipo == Tipo.PONTOEVIRGULA){
                return true
            }
        }
    }

    cursor = cursorInicio
    return false
}

fun expression(): Boolean {

    if(simpleExpression()) return true

    if(variable()){
        if(tokens[cursor].tipo == Tipo.RECEBE){
            cursor++
            if (expression()){
                return true
            }
            cursor--
        }
    }

    return false
}


fun variable(): Boolean {
    val cursorInicio = cursor

    if(tokens[cursor++].tipo == Tipo.IDENTIFICADOR){
        if(tokens[cursor].tipo == Tipo.COLESQ){
            cursor++
            return if(expression()){
                if(tokens[cursor++].tipo == Tipo.COLDIR){
                    true
                }else{
                    cursor = cursorInicio
                    false
                }
            }else{
                cursor = cursorInicio
                false
            }
        }

        return true
    }

    cursor = cursorInicio
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
    if(call()) return true

    val cursorInicio = cursor
    if(tokens[cursor].tipo == Tipo.PARESQ){
        cursor++
        if(expression()){
            if(tokens[cursor].tipo == Tipo.PARDIR){
                cursor++
                return true
            }
        }

    }
    cursor = cursorInicio
    return false
}

fun call(): Boolean {
    val cursorInicio = cursor

    if(tokens[cursor++].tipo == Tipo.IDENTIFICADOR){
        if(tokens[cursor++].tipo == Tipo.PARESQ){
            if(args()){
                if(tokens[cursor++].tipo == Tipo.PARDIR){
                    return true
                }
            }
        }
    }

    cursor = cursorInicio
    return false
}

fun args(): Boolean {
    argslist()
    return true
}

fun argslist(): Boolean {
    val cursorInicio = cursor

    if(!expression()) return false
    if(tokens[cursor++].tipo != Tipo.VIRGULA){
        cursor--
        return false
    }

    while(expression()){
        if(tokens[cursor++].tipo != Tipo.VIRGULA){
            cursor = cursorInicio
            return false
        }
    }

    return true
}