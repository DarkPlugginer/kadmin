package me.dark.commands

import me.dark.AdminManager
import me.dark.Main
import me.dark.utils.enums.CommandsMessages
import me.dark.utils.enums.ConsoleMessage
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

class AdminCommand : BukkitCommand("admin") {

    override fun execute(p0: CommandSender?, p1: String?, p2: Array<out String>?): Boolean {
        if(p0 !is Player) {
            p0?.sendMessage(ConsoleMessage.ONLY_PLAYERS.message)
            return true
        }

        if(p1?.toLowerCase().equals("admin")) {
            val player: Player = p0
            if(player.hasPermission(CommandsMessages.ADMIN_COMMAND.perm)) {
                AdminManager().set(player)
            }
        }

        return false
    }
}