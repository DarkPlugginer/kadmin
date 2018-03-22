/*
 * Copyright (©) kadmin
 *
 * Projeto desenvolvido por Miguel Lukas
 * Todos os direitos Reservados
 *
 * Modificado em: 20/03/18 13:52
 */

package me.dark.commands

import me.dark.Main
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

class ReportCommand : BukkitCommand("report") {

    override fun execute(commandSender: CommandSender, s: String, strings: Array<String>): Boolean {
        if (commandSender !is Player) return true

        var slots = 0

        when {
            Bukkit.getOnlinePlayers().size <= 27 -> slots = 27
            Bukkit.getOnlinePlayers().size in 28..36 -> slots = 36
            Bukkit.getOnlinePlayers().size in 37..55 -> slots = 54
        }

        val inventory = Bukkit.createInventory(commandSender, slots, "§b§nReports")

        Bukkit.getOnlinePlayers().forEach { player1: Player? ->
            if (!Main.adminManager.inAdmin(player1!!.uniqueId) && player1 != commandSender) {
                var head = ItemStack(Material.SKULL_ITEM)
                head.durability = 3.toShort()
                var meta = head.itemMeta as SkullMeta
                meta.owner = player1.name
                meta.displayName = "§a" + player1.name
                meta.lore = Arrays.asList("", "§fClique para §cReportar")
                head.itemMeta = meta
                inventory.setItem(inventory.firstEmpty(), head)
            }
        }

        commandSender.openInventory(inventory)

        return false

    }
}
