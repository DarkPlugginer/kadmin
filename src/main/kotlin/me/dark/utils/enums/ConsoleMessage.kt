package me.dark.utils.enums

import me.dark.Main

enum class ConsoleMessage constructor(val message: String){

    ONLY_PLAYERS(Main.instance!!.config.getString("Erros.consoleError"))
}