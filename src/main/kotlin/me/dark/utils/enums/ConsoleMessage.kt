/*
 * Copyright (©) Nano Team
 *
 * Projeto desenvolvido por Miguel Lukas
 * Todos os direitos Reservados
 *
 * Modificado em: 18/03/18 21:01
 * Criado em: 18/03/18 21:02
 */

package me.dark.utils.enums

import me.dark.Main

enum class ConsoleMessage constructor(val message: String){

    ONLY_PLAYERS(Main.instance!!.config.getString("Erros.consoleError"))
}