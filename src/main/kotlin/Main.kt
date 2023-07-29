package calculator

import kotlin.math.pow

const val VARIABLE = "([a-zA-Z]+)" // one or more letters
const val OPERAND = "($VARIABLE|(\\d+))" // one or more letters OR one or more numbers
const val VALID_OPERAND = "((^([+-]?$OPERAND))|$OPERAND)" // operand with ^(power) and maybe with +- sign OR without any signs
const val OPERATOR = "(([+-]+)|([*/^]))" // one or more + - signs OR only one * / ^ sign
class Calculator() {
    private val variablesMap = mutableMapOf<String, Int>() // it stores names and values of input variables

    fun start() {
        while (true) {
            val input = readln().trim()
            try {
                when {
                    input == "" -> continue
                    input.startsWith("/") -> println(checkingCommand(input) ?: break) // commands
                    input.contains("=") -> varAssign(input.split("=").map { it.trim() }) // variable assignment
                    input.isVariable() ->  println(input.getVarValue()) // printing the value of a variable
                    else -> input.expression() // calculation
                }
            } catch (e: Exception) { println(e.message) }
        }
        println("Bye!")
    }

    //calculation: count expression after transformation to postfix
    private fun String.expression() {
        if (isValidExpression()) {
            val postfix = toPostfix()
            val stack = ArrayDeque<Int>()
            for (element in postfix) {
                if (element.isOperand()) stack.push(element.getVarValue())
                else stack.push(operation(element, stack.pop(), stack.pop()))
            }
            println(stack.pop())
        }
        else throw Exception("Invalid expression")
    }

    //convert expression to Postfix notation
    private fun String.toPostfix(): List<String> {
        val result = mutableListOf<String>()
        val stack = ArrayDeque<String>()
        val elements = Regex("$VALID_OPERAND|$OPERATOR|([()])")
            .findAll(this.replace("\\s+".toRegex(), "")) // removing spaces
            .map { it.value     //compress double math signs ++ -- +-
                .replace("--", "+")
                .replace("+-", "-")
                .replace("-+", "-")
                .replace("[+]+".toRegex(), "+") }
        for (element in elements) {
            if (element.isOperand()) result.add(element) // if element is a number or letter(s) - add it to result
            else if (element == "(") stack.push(element) // adding "(" to the end of the stack
            else if (element == ")") {
                // this loop cuts the expression enclosed in "()" from the stack to the result
                while (!(stack.isEmpty() || stack.peek() == "(")) {
                    result.add(stack.pop())
                }

                // this try catch expression tries to remove the last element "(" from the stack,
                // BUT the empty stack case means the math expression is bad, so an exception is thrown
                try {
                    stack.pop()
                } catch (e: Exception) {
                    throw Exception("Invalid expression")
                }
            }
            else { // if element is Operator
                while (!(stack.isEmpty() || stack.peek() == "(" || element.hasHigherPrecedence(stack.peek()))) {
                    result.add(stack.pop())
                }
                stack.push(element)
            }
        }

        while (!stack.isEmpty()) {
            stack.pop().let {
                if (it == "(") throw Exception("Invalid expression")
                else result.add(it)
            }
        }

        return result
    }


    // variable assignment
    private fun varAssign(inputList: List<String>) {
        if(inputList[0].isVariable()) {
            if (inputList.size == 2) {
                // try to assign the number to the variable. if it isn't the number, check - is it a name of variable?
                try { variablesMap[inputList[0]] = inputList[1].toIntOrNull() ?: variablesMap[inputList[1]]!! }
                catch (e: Exception) { throw Exception("Invalid assignment") }
            }
            else throw Exception("Invalid assignment")
        }
        else throw Exception("Invalid identifier")
    }

    //check: is the input command correct?
    private fun checkingCommand(input: String): String? {
        return when (input) {
            "/exit" -> null
            "/help" -> "The program calculates the sum of numbers"
            else -> "Unknown command"
        }
    }

    //choose math operations
    private fun operation(op: String, x: Int, y: Int) = when (op) {
        "+" -> y + x
        "-" -> y - x
        "*" -> y * x
        "/" -> y / x
        "^" -> y.toDouble().pow(x).toInt()
        else -> throw Exception("Invalid expression")
    }

    //fun compares the precedence and return true if this precedence higher then other
    private fun String.hasHigherPrecedence(other: String): Boolean {
        val precedence = mapOf("(" to 1, "^" to 2, "*" to 3, "/" to 3, "+" to 4, "-" to 4)
        return precedence[this]!! < precedence[other]!!
    }

    //if (String is the variable) return it, else if (String is the Integer) return it, else throw Exception
    private fun String.getVarValue() = variablesMap.getOrElse(this) { this.toIntOrNull() ?: throw Exception("Unknown variable") }
    private fun String.isValidExpression() = "$VALID_OPERAND(\\s*$OPERATOR\\s*$VALID_OPERAND)*".toRegex()
        .matches(this.replace("[()]".toRegex(), ""))
    private fun String.isOperand() = VALID_OPERAND.toRegex().matches(this)
    private fun String.isVariable() = VARIABLE.toRegex().matches(this)

    private fun <T> ArrayDeque<T>.push(item: T) = this.addLast(item) //adding item to the end of the array
    private fun <T> ArrayDeque<T>.peek(): T = this[this.lastIndex] //it returns the last item of the array
    private fun <T> ArrayDeque<T>.pop(): T = this.removeLast() // return the last item and remove it from the array
}

fun main() {
    Calculator().start()
}


