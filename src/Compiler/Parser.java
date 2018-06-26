/*	##################################
	##### Grammar Rules for Tiny #####
	##################################
	
	### High-level program structures
	
		Program -> MethodDec1 MethodDec1*
		Type -> INT | REAL | STRING
		MethodDec1 -> Type [MAIN] Id '(' FormalParams ')' Block
		FormalParams -> [FormalParam ( ',' FormalParam )*]
		FormalParam -> Type Id
	
	### Statements
	
		Block -> BEGIN Statement+ END
	
		Statement -> Block
					| LocalVarDec1
					| AssignStmt
					| ReturnStmt
					| IfStmt
					| WriteStmt
					| ReadStmt
	
		LocalVarDec1 -> Type Id ',' | Type AssignStmt
		AssignStmt -> Id := Expression ';'
					| Id := QString ';'
		ReturnStmt -> RETURN Expression ';'
		IfStmt -> IF '(' BoolExpression ')' Statement
				| IF '(' BoolExpression ')' Statement ELSE Statement
		WriteStmt -> WRITE '(' Expression ','  QString ')' ';'
		ReadStmt -> READ '(' Id ',' QString ')' ';'
		QString is any sequence of characters except double quote itself, enclosed in double quotes.
	
	### Expressions
	
		Expression -> MultiplicativeExpr (( '+'  | '-' ) MultiplicativeExpr)*
		MultiplicativeExpr -> PrimaryExpr (( '*' | '/' ) PrimaryExpr)*
		PrimaryExpr -> Num     // Integer or Real numbers
					| Id
					| '(' Expression ')'
					| Id '(' ActualParams ')'
		BoolExpression -> Expression '==' Expression
						| Expression '!=' Expression
		ActualParams -> [Expression ( ',' Expression)*]
	
*/






package Compiler;

/**
 *
 * @author joker
 */


public class Parser {
    
}
