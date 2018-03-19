/*
 * Copyright (©) kadmin
 *
 * Projeto desenvolvido por Miguel Lukas
 * Todos os direitos Reservados
 *
 * Modificado em: 19/03/18 16:00
 */

package me.dark.listener

import me.dark.AdminManager
import me.dark.Main
import me.dark.hack.BanAPI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

object PlayerListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        Main.adminManager.getAdmins()!!.forEach { uuid: UUID ->
            player.hidePlayer(Bukkit.getPlayer(uuid))
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player

        if (Main.adminManager.getAdmins()!!.contains(player.uniqueId)) {
            if (Main.adminManager.getAdmins()!!.size > 1) {
                Main.adminManager.getAdmins()!!.remove(player.uniqueId)
            } else if (Main.adminManager.getAdmins()!!.size == 1) {
                Main.adminManager.getAdmins()!!.clear()
            }
        }

        if (player.hasMetadata("screenshare")) {
            val blocks: ArrayList<Block> = player.getMetadata("caged")[0].value() as ArrayList<Block>

            blocks.forEach { block: Block ->
                block.type = Material.AIR
            }

            player.removeMetadata("caged", Main.instance)

            val banner = player.getMetadata("screenshare")[0].value() as Player
            BanAPI().addBan(player.name, banner, null, "§cMotivo: §fDeslogou durante uma §eSCREEN-SHARE")
        }
    }

    @EventHandler
    fun onAsyncPlayerChat(event: AsyncPlayerChatEvent) {
        val player = event.player

        if (player.hasMetadata("sc") or (player.hasMetadata("screenshare"))) {
            event.isCancelled = true

            if (player.hasMetadata("sc")) {
                val name = player.getMetadata("sc")[0].value() as String
                Bukkit.getPlayerExact(name).sendMessage("§f[§eSCREEN-SHARE§f] §c${player.name} §f${event.message}")
            } else if (player.hasMetadata("screenshare")) {
                val name = player.getMetadata("screenshare")[0].value() as String
                Bukkit.getPlayerExact(name).sendMessage("§f[§eSCREEN-SHARE§f] §c${player.name} §f${event.message}")
            }
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        if (event.item == null)
            return

        if (Main.adminManager.inAdmin(player.uniqueId)) {
            if (!event.item.hasItemMeta())
                return

            when (event.item.type) {
                Material.SLIME_BALL -> {
                    val inventory = Bukkit.createInventory(player, 54, "§b§nJogadores")

                    Bukkit.getOnlinePlayers().forEach { player1: Player? ->
                        if (!Main.adminManager.inAdmin(player1!!.uniqueId)) {
                            var head = ItemStack(Material.SKULL_ITEM)
                            head.durability = 3.toShort()
                            var meta = head.itemMeta as SkullMeta
                            meta.owner = player1.name
                            meta.displayName = "§a" + player1.name
                            meta.lore = Arrays.asList("", "§fClique com o botao §c§o§nDireito §fPara teleportar", "", "§fClique com o botão esquerdo parar §c§o§nPrender")
                            head.itemMeta = meta
                            inventory.setItem(inventory.firstEmpty(), head)
                        }
                    }

                    player.openInventory(inventory)
                }
            }
        }
    }

    @EventHandler
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        val player = event.player

        if (Main.adminManager.inAdmin(player.uniqueId)) {
            if (event.rightClicked is Player) {
                val rightClicked = event.rightClicked as Player
                when (player.itemInHand.type) {
                    Material.AIR -> {
                        player.openInventory(rightClicked.inventory)
                    }

                    Material.BEDROCK -> {
                        player.performCommand("admin cage ${rightClicked.name}")
                    }
                }
            }
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as Player

        if (event.inventory.name.contains("§")) {
            event.isCancelled = true
            val currentItem = event.currentItem

            if (currentItem == null || currentItem.type == Material.AIR || !currentItem.hasItemMeta())
                return

            when (event.inventory.name) {
                "§b§nJogadores" -> {
                    val clicked = Bukkit.getPlayerExact(currentItem.itemMeta.displayName.replace("§a", ""))

                    if (event.click.name.toLowerCase().contains("left")) {
                        player.teleport(clicked.location)
                    } else if (event.click.name.toLowerCase().contains("right")) {
                        player.performCommand("admin cage ${clicked.name}")
                        player.teleport(clicked.location.add(0.0, 0.0, -3.0))

                        player.sendMessage("§fVocê prendeu o jogador: §c§o§n" + clicked.name)
                    }

                    player.closeInventory()
                }
            }
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player

        if (AdminManager().inAdmin(player.uniqueId) && event.itemInHand.type == Material.BEDROCK && event.itemInHand.hasItemMeta()) {
            event.isCancelled = true
            player.updateInventory()
        }
    }

    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val player = event.player

        if (AdminManager().inAdmin(player.uniqueId)) {
            event.isCancelled = true
            player.updateInventory()
        }
    }
}