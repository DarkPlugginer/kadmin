/*
 * Copyright (©) kadmin
 *
 * Projeto desenvolvido por Miguel Lukas
 * Todos os direitos Reservados
 *
 * Modificado em: 19/03/18 20:26
 */

package me.dark.commands

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

        val inventory = Bukkit.createInventory(commandSender, 54, "§b§nReports")

        Bukkit.getOnlinePlayers().forEach { player1: Player? ->
            //if (!Main.adminManager.inAdmin(player1!!.uniqueId)) {
            var head = ItemStack(Material.SKULL_ITEM)
            head.durability = 3.toShort()
            var meta = head.itemMeta as SkullMeta
            meta.owner = player1!!.name
            meta.displayName = "§a" + player1.name
            meta.lore = Arrays.asList("", "§fClique para §cReportar")
            head.itemMeta = meta
            inventory.setItem(inventory.firstEmpty(), head)
            //}
        }

        commandSender.openInventory(inventory)

        return false

    }
}
