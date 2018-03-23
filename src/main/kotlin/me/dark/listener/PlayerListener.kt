/*
 * Copyright (©) kadmin
 *
 * Projeto desenvolvido por Miguel Lukas
 * Todos os direitos Reservados
 *
 * Modificado em: 23/03/18 12:25
 */

package me.dark.listener

import me.dark.AdminManager
import me.dark.Main
import me.dark.hack.BanAPI
import me.dark.utils.enums.Permission
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Bat
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
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.util.*

object PlayerListener : Listener {

    val inventory: HashMap<UUID, ArrayList<Array<out ItemStack>>> = HashMap()

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
            BanAPI().addBan(player.name, banner, null, "§fDeslogou durante uma §eSCREEN-SHARE")

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
                    var slots = 0

                    when {
                        Bukkit.getOnlinePlayers().size <= 27 -> slots = 27
                        Bukkit.getOnlinePlayers().size in 28..36 -> slots = 36
                        Bukkit.getOnlinePlayers().size in 37..55 -> slots = 54
                    }

                    val inventory = Bukkit.createInventory(player, slots, "§b§nJogadores")

                    Bukkit.getOnlinePlayers().forEach { player1: Player? ->
                        if (!Main.adminManager.inAdmin(player1!!.uniqueId)) {
                            val head = ItemStack(Material.SKULL_ITEM)
                            head.durability = 3.toShort()
                            val meta = head.itemMeta as SkullMeta
                            meta.owner = player1.name
                            meta.displayName = "§a" + player1.name
                            meta.lore = Arrays.asList("", "§fClique com o botao §c§oDireito §fPara teleportar", "", "§fClique com o botão esquerdo para §c§oPrender", "", "§fClique com o botão esquerdo + shift para §aSS")
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
            if (player.inventory == event.clickedInventory) {
                event.isCancelled = true
                return
            }

            event.isCancelled = true
            val currentItem = event.currentItem

            if (currentItem == null || currentItem.type == Material.AIR || !currentItem.hasItemMeta())
                return

            when (event.inventory.name) {
                "§b§nJogadores" -> {
                    val clicked = Bukkit.getPlayerExact(currentItem.itemMeta.displayName.replace("§a", ""))

                    if (event.isShiftClick) {
                        player.performCommand("admin ss ${clicked.name}")
                        player.closeInventory()
                        return
                    }

                    if (event.isLeftClick) {
                        player.teleport(clicked.location)
                    } else if (event.isRightClick) {
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

                    inventory.setItem(10, createItem("§fReportar por: §b§nNoFall", "", Material.FEATHER, 0))
                    inventory.setItem(12, createItem("§fReportar por: §b§nAutoSoup", "", Material.MUSHROOM_SOUP, 0))
                    inventory.setItem(14, createItem("§fReportar por: §b§nForceField", "", Material.IRON_FENCE, 0))
                    inventory.setItem(26, createItem("§fReportar por outro motivo", "", Material.REDSTONE, 0))

                    while (inventory.firstEmpty() != -1)
                        inventory.setItem(inventory.firstEmpty(), createItem("§akAdmin", "", Material.STAINED_GLASS_PANE, 5))

                    player.openInventory(inventory)

                    player.setMetadata("report", FixedMetadataValue(Main.instance, clicked.name))
                }

                "§c§nHack-Test §ePlayers" -> {
                    player.closeInventory()

                    val clicked = Bukkit.getPlayerExact(currentItem.itemMeta.displayName.replace("§a", ""))

                    val inventory = Bukkit.createInventory(player, 27, "§c§nHack-Test §eHacks")

                    inventory.setItem(10, createItem("§fTestar: §c§nNoFall", "", Material.FEATHER, 0))
                    inventory.setItem(12, createItem("§fTestar: §c§nAutoSoup", "", Material.MUSHROOM_SOUP, 0))
                    inventory.setItem(14, createItem("§fTestar: §c§nForceField", "", Material.IRON_FENCE, 0))

                    while (inventory.firstEmpty() != -1)
                        inventory.setItem(inventory.firstEmpty(), createItem("§akAdmin", "", Material.STAINED_GLASS_PANE, 14))

                    player.setMetadata("htest", FixedMetadataValue(Main.instance, clicked))

                    player.openInventory(inventory)
                }

                "§bReports §f- §nMotivos" -> {
                    if (currentItem.type == Material.STAINED_GLASS_PANE)
                        return

                    if (currentItem.type == Material.REDSTONE) {
                        player.sendMessage("§fPor favor, especifique o §c§nmotivo §fno chat")
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
                            player1.sendMessage("Player: §c${player.name}")
                            player1.sendMessage("Reportado: §c${player.getMetadata("report")[0].value() as String}")
                            player1.sendMessage("Motivo: §c$reason")
                        }
                    }

                    player.removeMetadata("report", Main.instance)
                    player.closeInventory()
                    player.sendMessage("§fReport enviado com §bsucesso§f!")
                }

                "§c§nHack-Test §eHacks" -> {
                    player.closeInventory()

                    if (currentItem.type == Material.STAINED_GLASS_PANE)
                        return

                    val hack = currentItem.itemMeta.displayName.replace("§fTestar: §c§n", "").toLowerCase()
                    val target = player.getMetadata("htest")[0].value() as Player

                    when (hack) {
                        "nofall" -> {
                            target.setMetadata("nfall", FixedMetadataValue(Main.instance, true))
                            target.velocity = Vector(0.0, 1.5, 0.0)
                        }

                        "autosoup" -> {
                            val list: ArrayList<Array<out ItemStack>> = ArrayList()
                            list.add(player.inventory.contents)
                            list.add(player.inventory.armorContents)
                            inventory[target.uniqueId] = list

                            val health = target.health

                            target.inventory.clear()
                            target.updateInventory()
                            target.damage(4.0)
                            target.health = 20.0

                            target.inventory.setItem(Random().nextInt(target.inventory.size), ItemStack(Material.MUSHROOM_SOUP))

                            object : BukkitRunnable() {
                                override fun run() {
                                    if (target.inventory.contains(Material.MUSHROOM_SOUP)) {
                                        player.sendMessage("§c§nHack-Test §e> §fO jogador §c${target.name} §fNão aparenta estar usando auto-soup")
                                    } else {
                                        player.sendMessage("§c§nHack-Test §e> §fO jogador §c${target.name} §fPode estar usando auto-soup")
                                        player.performCommand("admin cage ${target.name}}")
                                    }

                                    target.health = health
                                    target.inventory.contents = inventory[target.uniqueId]!![0]
                                    target.inventory.armorContents = inventory[target.uniqueId]!![1]

                                    target.updateInventory()
                                }
                            }.runTaskLater(Main.instance, 20L)
                        }

                        "forcefield" -> {
                            val bat = player.world.spawn(target.location.subtract(1.0, 0.0, 1.0), Bat::class.java)
                            bat.health = (bat.maxHealth - bat.maxHealth) + 1.0

                            object : BukkitRunnable() {
                                override fun run() {
                                    if (bat.isDead) {
                                        player.sendMessage("§c§nHack-Test §e> §fO jogador §c${target.name} §fPode estar usando force-field")
                                        player.performCommand("admin cage ${target.name}}")
                                    } else {
                                        player.sendMessage("§c§nHack-Test §e> §fO jogador §c${target.name} §fNão aparenta estar usando force-field")
                                    }
                                }
                            }.runTaskLater(Main.instance, 20L)
                        }
                    }

                    player.removeMetadata("htest", Main.instance)
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

        if (player.hasMetadata("screenshare"))
            event.isCancelled = true
    }

    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val player = event.player

        if (AdminManager().inAdmin(player.uniqueId)) {
            event.isCancelled = true
            player.updateInventory()
        }

        if (player.hasMetadata("screenshare"))
            event.isCancelled = true
    }

    @EventHandler
    fun onPlayerPickUpItem(event: PlayerPickupItemEvent) {
        val player = event.player

        if (player.hasMetadata("screenshare"))
            event.isCancelled = true
    }

    private fun createItem(name: String, desc: String, type: Material, id: Int): ItemStack {
        val stack = ItemStack(type)
        if (id > 0)
            stack.durability = id.toShort()
        val meta: ItemMeta? = stack.itemMeta
        meta?.displayName = name
        meta?.lore = Arrays.asList(desc)
        meta?.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_PLACED_ON)
        stack.itemMeta = meta
        return stack
    }
}