/*
 * Copyright (©) kadmin
 *
 * Projeto desenvolvido por Miguel Lukas
 * Todos os direitos Reservados
 *
 * Modificado em: 19/03/18 15:05
 */

package me.dark.commands

import me.dark.AdminManager
import me.dark.Main
import me.dark.commands.common.BaseCommand
import me.dark.commands.common.CommandManager
import me.dark.utils.SchematicUtils
import me.dark.utils.enums.Permission
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import java.io.File

class Commands {

    @BaseCommand(usage = "admin", aliases = ["adm", "admin"], desc = "Comando usado para acessar o modo admin", hidden = false, permission = Permission.ADMIN_COMMAND)
    fun adminCommand(sender: CommandSender, commandLabel: String, strings: Array<String>) {
        if (sender is Player) {
            AdminManager().set(sender)
        }
    }

    @BaseCommand(usage = "[jogador]", aliases = ["cage", "cg"], desc = "Comando usado para prender um jogador", permission = Permission.CAGE_COMMAND, min = 1, max = 1)
    fun cageCommand(sender: CommandSender, commandLabel: String, strings: Array<String>) {
        if (sender !is Player) return

        if (Bukkit.getPlayerExact(strings[0]) == null) {
            sender.sendMessage("${CommandManager.error} Este jogador não pode ser encontrado!")
            return
        }

        var targetPlayer: Player = Bukkit.getPlayerExact(strings[0])

        val schematicUtils = SchematicUtils(File(Main.schematics, Main.instance!!.config.getString("Schematic.name")))
        val blocks: ArrayList<Block> = schematicUtils.pasteSchematic(targetPlayer.location.add(0.0, 10.0, 0.0), true)
        var found = false
        blocks.forEach { block: Block ->
            if (block.type == Material.getMaterial(Main.instance!!.config.getString("Schematic.block"))) {
                targetPlayer.teleport(getCenter(block.location.add(0.0, 1.0, 0.0)))
                found = true
                return@forEach
            }
        }
        if (!found) {
            targetPlayer.teleport(getCenter(targetPlayer.location.add(0.0, 11.0, 0.0)))
        }
    }

    @BaseCommand(usage = "[jogador]", aliases = ["htest", "hacktest"], desc = "Comando usado testar um jogador", permission = Permission.HACKTEST_COMMAND, min = 1, max = 1)
    fun hackTestCommand(sender: CommandSender, commandLabel: String, strings: Array<String>) {
        if (sender !is Player) return

        if (Bukkit.getPlayerExact(strings[0]) == null) {
            sender.sendMessage("${CommandManager.error} Este jogador não pode ser encontrado!")
            return
        }

        var targetPlayer: Player = Bukkit.getPlayerExact(strings[0])
        sender.setMetadata("hacktest", FixedMetadataValue(Main.instance, targetPlayer))

        val inventory = Bukkit.createInventory(sender, 36, "§b§nHack-Test")

        sender.openInventory(inventory)
    }

    @BaseCommand(usage = "[jogador] [mensagem]", aliases = ["sudo", "sumo"], desc = "Comando usado forçar o jogador a fazer algo", permission = Permission.SUDO_COMMAND, min = 2, max = 10)
    fun sudoCommand(sender: CommandSender, commandLabel: String, strings: Array<String>) {
        if (sender !is Player) return

        if (Bukkit.getPlayerExact(strings[0]) == null) {
            sender.sendMessage("${CommandManager.error} Este jogador não pode ser encontrado!")
            return
        }

        var targetPlayer: Player = Bukkit.getPlayerExact(strings[0])

        val builder = StringBuilder()
        for (i in 1 until strings.size) {
            if (i > 1)
                builder.append(" ")
            builder.append(strings[i])
        }

        val allArgs = builder.toString()
        targetPlayer.chat(allArgs)
    }

    //TODO: tools
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