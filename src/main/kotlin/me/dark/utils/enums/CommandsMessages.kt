package me.dark.utils.enums

import me.dark.Main

enum class CommandsMessages constructor(val perm: String, val error: String, val usage: String) {

    ADMIN_COMMAND(Main.instance!!.config.getString("Permissions.adminCommand"), "", ""),
    CAGE_COMMAND(Main.instance!!.config.getString("Permissions.cageCommand"), Main.instance!!.getStringInConfig("Erros.cageError"), Main.instance!!.getStringInConfig("Usages.cageUsage")),
    SUMO_COMMAND(Main.instance!!.config.getString("Permissions.sumoCommand"), Main.instance!!.getStringInConfig("Erros.sumoError"), Main.instance!!.getStringInConfig("Usages.sumoUsage")),
}