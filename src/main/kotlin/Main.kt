package calculator

class Calculator() {
    fun start() {
        while (true) {
            val input = readln().trim()
            when {
                input == "" -> continue
                "/.+".toRegex().matches(input) -> println(checkingCommand(input) ?: break)
                else -> println(count(input.split(' ').toMutableList()))
            }
        }
        println("Bye!")
    }

    //check: is the input command correct?
    fun checkingCommand(input: String): String? {
        return when (input) {
            "/exit" -> null
            "/help" -> "The program calculates the sum of numbers"
            else -> "Unknown command"
        }
    }

    //compress math signs and count expression
    fun count(inputList: MutableList<String>): String {
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
                inputList[0] = operation(inputList[1], inputList[0].toInt(), inputList[2].toInt())!!.toString()
            } catch (e: Exception) {
                return "Invalid expression"
            }
            inputList.removeAt(2)
            inputList.removeAt(1)
        }
        return inputList[0].toIntOrNull()?.toString() ?: "Invalid expression"
    }

    //choose math operations
    fun operation(operation: String, a: Int, b: Int): Int? {
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


