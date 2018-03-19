/*
 * Copyright (©) kadmin
 *
 * Projeto desenvolvido por Miguel Lukas
 * Todos os direitos Reservados
 *
 * Modificado em: 19/03/18 15:40
 */

package me.dark.utils.enums

import me.dark.Main
import org.bukkit.command.CommandSender

enum class Permission {

    NONE,
    ADMIN_COMMAND,
    CAGE_COMMAND,
    HACKTEST_COMMAND,
    SUDO_COMMAND,
    SCREEN_SHARE;

    companion object {

        private fun getPermission(permission: Permission): String {
            var name: String = permission.toString().toLowerCase().replace("_", "").replaceFirst("c", "C")
            if (permission == CAGE_COMMAND)
                name = "cageCommand"

            return Main.instance!!.config.getString("Permissions.$name")
        }

        fun has(permission: Permission, target: CommandSender): Boolean {
            return target.hasPermission(getPermission(permission))
        }
    }
}