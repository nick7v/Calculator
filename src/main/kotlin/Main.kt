package calculator

class Calculator() {
    private val variablesMap = mutableMapOf<String, Int>() // it stores names and values of input variables

    fun start() {
        while (true) {
            val input = readln().trim()
            when {
                input == "" -> continue
                input.startsWith("/") -> println(checkingCommand(input) ?: break) // commands
                input.contains("=")-> varAssign(input.split("=").map { it.trim() }) // variable assignment
                "[a-zA-Z]+".toRegex().matches(input)-> { // printing the value of a variable
                    println(variablesMap.getOrDefault(input, null) ?: "Unknown variable")
                }
                else -> println(count(input.split(' ').toMutableList())) // calculation
            }
        }
        println("Bye!")
    }

    // variable assignment
    private fun varAssign(inputList: List<String>) {
        if("[a-zA-Z]+".toRegex().matches(inputList[0])) {
            if (inputList.size == 2) {
                // try to assign the number to the variable. if it isn't the number, check - is it a name of variable?
                try { variablesMap[inputList[0]] = inputList[1].toIntOrNull() ?: variablesMap[inputList[1]]!! }
                catch (e: Exception) { println("Invalid assignment") }
            }
            else println("Invalid assignment")
        }
        else println("Invalid identifier")
    }

    //check: is the input command correct?
    private fun checkingCommand(input: String): String? {
        return when (input) {
            "/exit" -> null
            "/help" -> "The program calculates the sum of numbers"
            else -> "Unknown command"
        }
    }

    //compress math signs and count expression
    private fun count(inputList: MutableList<String>): String {
        for (signIndex in 1..inputList.size - 1 step 2) { // loop compress math signs from user input
            while (inputList[signIndex].length != 1) {
                inputList[signIndex] = inputList[signIndex]
                    .replace("-+", "-")
                    .replace("+-", "-")
                    .replace("--", "+")
                    .replace("++", "+")
            }
        }
        while (inputList.size != 1) { // loop count user input expression and return result to main
            try {
                inputList[0] = operation(inputList[1],
                    inputList[0].toIntOrNull() ?: variablesMap[inputList[0]]!!,
                    inputList[2].toIntOrNull() ?: variablesMap[inputList[2]]!!)!!.toString()
            } catch (e: Exception) {
                return "Invalid expression"
            }
            inputList.removeAt(2)
            inputList.removeAt(1)
        }
        return inputList[0].toIntOrNull()?.toString() ?: "Invalid expression"
    }

    //choose math operations
    private fun operation(operation: String, a: Int, b: Int): Int? {
        return when (operation) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> a / b
            "%" -> a % b
            else -> null
        }
    }
}

fun main() {
    Calculator().start()
}


