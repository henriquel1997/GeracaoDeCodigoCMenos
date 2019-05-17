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

            if(tokens[cursor++].tipo == Tipo.COLESQ){

                if(tokens[cursor].tipo == Tipo.NUMERO){
                    cursor++
                }

                if(tokens[cursor++].tipo == Tipo.COLDIR){
                    return true
                }
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
    if(!param()) return false
    while(param()){
        //Geração de código aqui
    }
    return true
}

fun param(): Boolean {
    val cursorInicio = cursor

    if(typeSpecifier()){
        if(tokens[cursor++].tipo == Tipo.IDENTIFICADOR){

            if(tokens[cursor++].tipo == Tipo.COLESQ){

                if(tokens[cursor++].tipo == Tipo.COLDIR){
                    return true
                }
            }
        }
    }

    cursor = cursorInicio
    return false
}

fun compoundStmt(): Boolean {
    if(!localDeclarations()) return false
    if(!statementList()) return false
    while(localDeclarations()){
        if(!statementList()) return false
    }
    return true
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
    if(assignmentStmt()) return true
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

//TODO: Não está na gramática
fun assignmentStmt(): Boolean {
    return false
}

fun returnStmt(): Boolean {
    val cursorInicio = cursor

    if(tokens[cursor++].tipo == Tipo.RETURN){
        if(expression()){
            if(tokens[cursor++].tipo == Tipo.PONTOEVIRGULA){
                return true
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
    if(!varRep()) return false
    if(!simpleExpression()) return false
    return true
}

fun varRep(): Boolean {
    val cursorInicio = cursor
    while(variable()){
        if(tokens[cursor++].tipo != Tipo.RECEBE){
            cursor = cursorInicio
            return false
        }
    }

    return true
}


//TODO: Pode ter variável sem os colchetes?
fun variable(): Boolean {
    val cursorInicio = cursor

    if(tokens[cursor++].tipo == Tipo.IDENTIFICADOR){
        if(tokens[cursor++].tipo == Tipo.COLESQ){
            if(expression()){
                if(tokens[cursor++].tipo == Tipo.COLDIR){
                    return true
                }
            }
        }
    }

    cursor = cursorInicio
    return false
}

fun simpleExpression(): Boolean {
    if(additiveExpression()) return true

    if(relop()){
        if(additiveExpression()){
            while(additiveExpression()){
                //Geração de código aqui
            }
            return true
        }
    }

    return false
}

fun additiveExpression(): Boolean {
    if(term()){
        additiveExpressionLinha()
        return true
    }
    return false
}

fun additiveExpressionLinha(): Boolean {
    if(addop() && term()){
        while (addop()){
            if(!term()) return false
        }
        return true
    }
    return false
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

//TODO: Falta implementar
fun term(): Boolean {
    return false
}