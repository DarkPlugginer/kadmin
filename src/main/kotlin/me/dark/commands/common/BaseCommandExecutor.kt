/*
 * Copyright (©) kadmin
 *
 * Projeto desenvolvido por Miguel Lukas
 * Todos os direitos Reservados
 *
 * Modificado em: 19/03/18 15:05
 */

package me.dark.commands.common

import me.dark.AdminManager
import me.dark.utils.enums.Permission
import org.apache.commons.lang.ArrayUtils
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import java.util.*

class BaseCommandExecutor : BukkitCommand("admin") {

    override fun execute(sender: CommandSender, commandLabel: String, strings: Array<String>): Boolean {

        if (sender is ConsoleCommandSender) {
            sender.sendMessage(CommandManager.error.toString() + "Esse comando só pode ser executado por um player!")
            return true
        }

        if (strings.isEmpty()) {
            if (Permission.has(Permission.ADMIN_COMMAND, sender)) {
                val player = sender as Player
                AdminManager().set(player)
                return true
            }
            return true
        }

        if (CommandManager.getCommand(strings[0]) == null) {
            sender.sendMessage("${CommandManager.error} O comando especificado nao foi encontrado!")
            return true
        }


        val command = CommandManager.getCommand(strings[0])
        val commandArgs = ArrayUtils.remove(strings, 0)

        if (command!!.permission != Permission.NONE && !Permission.has(command.permission, sender)) {
            sender.sendMessage(CommandManager.error.toString() + "Você nao possui permissão para executar esse comando.")
            return true
        }

        if (commandArgs.size < command.min || commandArgs.size > command.max && command.max != -1) {
            sender.sendMessage(CommandManager.error.toString() + "Use: /" + commandLabel + " " + command.aliases[0] + " " + command.usage)
            return true
        }

        CommandManager.execute(command, sender, commandLabel, commandArgs)
        return true
    }

    override fun tabComplete(sender: CommandSender?, alias: String?, args: Array<out String>?): ArrayList<String> {
        val list = super.tabComplete(sender, alias, args)

        for (command in CommandManager.getCommands()) {
            for (alias in command.aliases) {
                list.add(alias)
            }
        }

        return list as ArrayList<String>
    }
}
