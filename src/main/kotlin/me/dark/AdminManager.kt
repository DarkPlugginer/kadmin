/*
 * Copyright (©) kadmin
 *
 * Projeto desenvolvido por Miguel Lukas
 * Todos os direitos Reservados
 *
 * Modificado em: 23/03/18 08:40
 */

package me.dark

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AdminManager {

    companion object {
        val admins: ArrayList<UUID>? = ArrayList()
        val inventory: HashMap<UUID, ArrayList<Array<out ItemStack>>> = HashMap()
    }

    @Suppress("DEPRECATION")
    fun set(player: Player) {
        if (admins?.contains(player.uniqueId)!!) {
            admins.remove(player.uniqueId)

            player.inventory.clear()
            player.inventory.armorContents = null

            player.inventory.contents = inventory[player.uniqueId]!![0]
            player.inventory.armorContents = inventory[player.uniqueId]!![1]

            player.resetTitle()
            player.sendTitle("§fModo §c§nADMIN", "§eSaiu")

            Bukkit.getOnlinePlayers().forEach { player1: Player? ->
                if (!player1?.canSee(player)!!)
                    player1.showPlayer(player)
            }

            player.gameMode = GameMode.SURVIVAL
            player.updateInventory()
        } else {
            admins.add(player.uniqueId)

            val list: ArrayList<Array<out ItemStack>> = ArrayList()
            list.add(player.inventory.contents)
            list.add(player.inventory.armorContents)
            inventory[player.uniqueId] = list

            player.inventory.clear()
            player.inventory.armorContents = null

            Bukkit.getOnlinePlayers().forEach { player1: Player? ->
                player1?.hidePlayer(player)
            }

            player.resetTitle()
            player.sendTitle("§fModo §c§nADMIN", "§aEntrou")

            player.gameMode = GameMode.CREATIVE

            player.inventory.setItem(3, createItem("§eJogadores", "§fLista de jogadores online", Material.SLIME_BALL))
            player.inventory.setItem(4, createItem("§eJail", "§fPrenda um jogador", Material.getMaterial(Main.instance!!.config.getString("Schematic.block"))))

            player.updateInventory()
        }
    }

    fun getAdmins(): ArrayList<UUID>? {
        return admins
    }

    fun inAdmin(uniqueId: UUID): Boolean {
        return getAdmins()!!.contains(uniqueId)
    }

    private fun createItem(name: String, desc: String, type: Material): ItemStack {
        val stack = ItemStack(type)
        val meta: ItemMeta? = stack.itemMeta
        meta?.displayName = name
        meta?.lore = Arrays.asList(desc)
        meta?.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_PLACED_ON)
        stack.itemMeta = meta
        return stack
    }
}