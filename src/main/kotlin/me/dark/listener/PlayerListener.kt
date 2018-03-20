/*
 * Copyright (©) kadmin
 *
 * Projeto desenvolvido por Miguel Lukas
 * Todos os direitos Reservados
 *
 * Modificado em: 19/03/18 20:36
 */

package me.dark.listener

import me.dark.AdminManager
import me.dark.Main
import me.dark.hack.BanAPI
import me.dark.utils.enums.Permission
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.metadata.FixedMetadataValue
import java.util.*

object PlayerListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.joinMessage = null

        val player = event.player

        Main.adminManager.getAdmins()!!.forEach { uuid: UUID ->
            player.hidePlayer(Bukkit.getPlayer(uuid))
        }

        player.inventory.clear()
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        event.quitMessage = null

        val player = event.player

        if (Main.adminManager.getAdmins()!!.contains(player.uniqueId)) {
            if (Main.adminManager.getAdmins()!!.size > 1) {
                Main.adminManager.getAdmins()!!.remove(player.uniqueId)
            } else if (Main.adminManager.getAdmins()!!.size == 1) {
                Main.adminManager.getAdmins()!!.clear()
            }
            player.inventory.clear()
        }

        if (player.hasMetadata("screenshare")) {
            val blocks: ArrayList<Block> = player.getMetadata("caged")[0].value() as ArrayList<Block>

            blocks.forEach { block: Block ->
                block.type = Material.AIR
            }

            val banner = Bukkit.getPlayerExact(player.getMetadata("screenshare")[0].value() as String)
            BanAPI().addBan(player.name, banner, null, "§cMotivo: §fDeslogou durante uma §eSCREEN-SHARE")

            player.removeMetadata("screenshare", Main.instance)
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
        } else if (player.hasMetadata("report")) {
            event.isCancelled = true

            Bukkit.getOnlinePlayers().forEach { player1: Player? ->
                if (Permission.has(Permission.ADMIN_COMMAND, player1!!)) {
                    player1.resetTitle()
                    player1.sendTitle("§fNovo §e§o§nReport", "")

                    player1.sendMessage("Player: ${player.name}")
                    player1.sendMessage("Reportado: ${player.getMetadata("report")[0].value() as String}")
                    player1.sendMessage("Motivo: ${event.message}")
                }
            }

            player.removeMetadata("report", Main.instance)

            for (i in 0 until 100)
                player.sendMessage(" ")

            player.sendMessage("§fReport enviado com §bsucesso§f!")
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

                else -> {
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

                    else -> {
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

                "§b§nReports" -> {
                    player.closeInventory()

                    val clicked = Bukkit.getPlayerExact(currentItem.itemMeta.displayName.replace("§a", ""))
                    val inventory = Bukkit.createInventory(player, 27, "§bReports §f- §nMotivos")

                    inventory.setItem(10, createItem("§fReportar por: §b§nNoFall", "", Material.FEATHER, 0, false))
                    inventory.setItem(12, createItem("§fReportar por: §b§nAutoSoup", "", Material.MUSHROOM_SOUP, 0, false))
                    inventory.setItem(14, createItem("§fReportar por: §b§nForceField", "", Material.IRON_FENCE, 0, false))
                    inventory.setItem(26, createItem("§fReportar por outro motivo", "", Material.REDSTONE, 0, false))

                    while (inventory.firstEmpty() != -1)
                        inventory.setItem(inventory.firstEmpty(), createItem("§akAdmin", "", Material.STAINED_GLASS_PANE, 5, true))

                    player.openInventory(inventory)

                    player.setMetadata("report", FixedMetadataValue(Main.instance, clicked.name))
                }

                "§bReports §f- §nMotivos" -> {
                    if (currentItem.type == Material.REDSTONE) {
                        player.sendMessage("§fPor favor, especifique o motivo no chat")
                        player.sendMessage(" ")

                        player.closeInventory()
                        return
                    }

                    val reason = currentItem.itemMeta.displayName.replace("§fReportar por: §b§n", "")

                    Bukkit.getOnlinePlayers().forEach { player1: Player? ->
                        if (Permission.has(Permission.ADMIN_COMMAND, player1!!)) {
                            player1.resetTitle()
                            player1.sendTitle("§fNovo §e§o§nReport", "")

                            player1.sendMessage("§fNovo §e§oReport")
                            player1.sendMessage("Player: §c§n${player.name}")
                            player1.sendMessage("Reportado: §c${player.getMetadata("report")[0].value() as String}")
                            player1.sendMessage("Motivo: §c$reason")
                        }
                    }

                    player.removeMetadata("report", Main.instance)

                    player.closeInventory()

                    player.sendMessage("§fReport enviado com §bsucesso§f!")
                }

                else -> {
                }
            }
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player

        if (AdminManager().inAdmin(player.uniqueId) && event.itemInHand.type == Material.getMaterial(Main.instance!!.config.getString("Schematic.block")) && event.itemInHand.hasItemMeta()) {
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

    private fun createItem(name: String, desc: String, type: Material, id: Int, hasId: Boolean): ItemStack {
        var stack = ItemStack(type)
        if (hasId)
            stack.durability = id.toShort()
        var meta: ItemMeta? = stack.itemMeta
        meta?.displayName = name
        meta?.lore = Arrays.asList(desc)
        meta?.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_PLACED_ON)
        stack.itemMeta = meta
        return stack
    }
}