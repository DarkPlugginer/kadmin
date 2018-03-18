package me.dark.commands

import me.dark.Main
import me.dark.utils.enums.CommandsMessages
import me.dark.utils.enums.ConsoleMessage
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue

class HackTest : BukkitCommand("hacktest") {

    override fun execute(p0: CommandSender?, p1: String?, p2: Array<out String>?): Boolean {
        if(p0 !is Player) {
            p0?.sendMessage(ConsoleMessage.ONLY_PLAYERS.message)
            return true
        }

        if (p1?.toLowerCase().equals("hacktest")) {
            val player: Player = p0
            if (player.hasPermission(CommandsMessages.SUMO_COMMAND.perm)) {
                if (p2!!.size == 1) {
                    if (Bukkit.getPlayerExact(p2[0]) == null) {
                        player.sendMessage(CommandsMessages.SUMO_COMMAND.error)
                        return true
                    }

                    var targetPlayer: Player = Bukkit.getPlayerExact(p2[0])
                    player.setMetadata("hacktest", FixedMetadataValue(Main.instance, targetPlayer))

                    val inventory = Bukkit.createInventory(player, 36, "§b§nHack-Test")


                    player.openInventory(inventory)

                } else {
                    player.sendMessage(CommandsMessages.SUMO_COMMAND.usage)
                }
            }
        }
        return false
    }
}