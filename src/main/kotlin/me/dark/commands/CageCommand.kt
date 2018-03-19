/*
 * Copyright (©) Nano Team
 *
 * Projeto desenvolvido por Miguel Lukas
 * Todos os direitos Reservados
 *
 * Modificado em: 18/03/18 21:01
 * Criado em: 18/03/18 21:02
 */

package me.dark.commands

import me.dark.Main
import me.dark.utils.SchematicUtils
import me.dark.utils.enums.CommandsMessages
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import java.io.File

class CageCommand : BukkitCommand("cage") {

    override fun execute(p0: CommandSender?, p1: String?, p2: Array<out String>?): Boolean {


        if(p1?.toLowerCase().equals("cage")) {
            if(p0 !is Player) {
                if(p2!!.isEmpty()) {
                    p0!!.sendMessage(CommandsMessages.CAGE_COMMAND.usage)
                    return true
                } else if (p2.size == 1) {
                    if (Bukkit.getPlayerExact(p2[0]) == null) {
                        p0!!.sendMessage(CommandsMessages.CAGE_COMMAND.error)
                        return true
                    }

                    var targetPlayer: Player = Bukkit.getPlayerExact(p2[0])

                    val schematicUtils = SchematicUtils(File(Main.schematics, Main.instance!!.config.getString("Schematic.name")))
                    val blocks: ArrayList<Block> = schematicUtils.pasteSchematic(targetPlayer.location.add(0.0, 10.0, 0.0), true)
                    var found = false
                    blocks.forEach { block: Block ->
                        if(block.type == Material.getMaterial(Main.instance!!.config.getString("Schematic.block"))) {
                            targetPlayer.teleport(getCenter(block.location).add(0.0, 1.0, 0.0))
                            found = true
                            return@forEach
                        }
                    }
                    if(!found) {
                        targetPlayer.teleport(getCenter(targetPlayer.location.add(0.0, 11.0, 0.0)))
                    }
                }
                return true
            }

            val player: Player = p0
            if(player.hasPermission(CommandsMessages.CAGE_COMMAND.perm)) {
                if(p2!!.isEmpty()) {
                    player.sendMessage(CommandsMessages.CAGE_COMMAND.usage)
                    return true
                } else if (p2.size == 1) {
                    if (Bukkit.getPlayerExact(p2[0]) == null) {
                        p0.sendMessage(CommandsMessages.CAGE_COMMAND.error)
                        return true
                    }

                    var targetPlayer: Player = Bukkit.getPlayerExact(p2[0])

                    val schematicUtils = SchematicUtils(File(Main.schematics, Main.instance!!.config.getString("Schematic.name")))
                    val blocks: ArrayList<Block> = schematicUtils.pasteSchematic(targetPlayer.location.add(0.0, 10.0, 0.0), true)
                    var found = false
                    blocks.forEach { block: Block ->
                        if(block.type == Material.getMaterial(Main.instance!!.config.getString("Schematic.block"))) {
                            targetPlayer.teleport(getCenter(block.location).add(0.0, 1.0, 0.0))
                            found = true
                            return@forEach
                        }
                    }
                    if(!found) {
                        targetPlayer.teleport(getCenter(targetPlayer.location.add(0.0, 11.0, 0.0)))
                    }
                }
            }
        }
        return false
    }

    private fun getCenter(loc: Location): Location {
        return Location(loc.world,
                getRelativeCord(loc.blockX),
                getRelativeCord(loc.blockY),
                getRelativeCord(loc.blockZ))
    }

    private fun getRelativeCord(i: Int): Double {
        var d = i.toDouble()
        d = if (d < 0) d - .5 else d + .5
        return d
    }
}