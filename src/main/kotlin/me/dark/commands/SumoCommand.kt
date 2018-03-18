package me.dark.commands

import me.dark.Main
import me.dark.utils.enums.CommandsMessages
import me.dark.utils.enums.ConsoleMessage
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

class SumoCommand : BukkitCommand("sumo") {

    override fun execute(p0: CommandSender?, p1: String?, p2: Array<out String>?): Boolean {
        if(p0 !is Player) {
            p0?.sendMessage(ConsoleMessage.ONLY_PLAYERS.message)
            return true
        }


        if (p1?.toLowerCase().equals("sumo")) {
            val player: Player = p0
            if (player.hasPermission(CommandsMessages.SUMO_COMMAND.perm)) {
                if (p2!!.size > 2) {
                    if (Bukkit.getPlayerExact(p2[0]) == null) {
                        player.sendMessage(CommandsMessages.SUMO_COMMAND.error)
                        return true
                    }

                    var targetPlayer: Player = Bukkit.getPlayerExact(p2[0])

                    val builder = StringBuilder()
                    for (i in 1 until p2.size) {
                        if (i > 1)
                            builder.append(" ")
                        builder.append(p2[i])
                    }

                    val allArgs = builder.toString()
                    targetPlayer.chat(allArgs)
                } else {
                    player.sendMessage(CommandsMessages.SUMO_COMMAND.usage)
                }
            }
        }
        return false
    }
}