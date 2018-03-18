package me.dark.listener

import me.dark.Main
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
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

        if(Main.adminManager.getAdmins()!!.contains(player.uniqueId)) {
            if(Main.adminManager.getAdmins()!!.size > 1) {
                Main.adminManager.getAdmins()!!.remove(player.uniqueId)
            } else if (Main.adminManager.getAdmins()!!.size == 1) {
                Main.adminManager.getAdmins()!!.clear()
            }
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        if(event.item == null)
            return

        if(Main.adminManager.inAdmin(player.uniqueId)) {
            if(!event.item.hasItemMeta())
                return

            when (event.item.type) {
                Material.SLIME_BALL -> {
                    val inventory = Bukkit.createInventory(player, 54, "§b§nJogadores")

                    Bukkit.getOnlinePlayers().forEach { player1: Player? ->
                        if(!Main.adminManager.inAdmin(player1!!.uniqueId)) {
                            var head = ItemStack(Material.SKULL_ITEM, 3)
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

        if(Main.adminManager.inAdmin(player.uniqueId)) {
            if(event.rightClicked is Player) {
                val rightClicked = event.rightClicked as Player
                when (player.itemInHand.type) {
                    Material.AIR -> {
                        player.openInventory(rightClicked.inventory)
                    }

                    Material.BEDROCK -> {
                        player.performCommand("cage ${rightClicked.name}")
                    }
                }
            }
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as Player

        if(event.inventory.name.contains("§")) {
            event.isCancelled = true
            val currentItem = event.currentItem

            if(currentItem == null || currentItem.type == Material.AIR || !currentItem.hasItemMeta())
                return

            when(event.inventory.name) {
                "§b§nJogadores" -> {
                    val clicked = Bukkit.getPlayerExact(currentItem.itemMeta.displayName.replace("§a", ""))

                    if(event.click.name.toLowerCase().contains("right")) {
                        player.teleport(clicked.location)
                    } else if (event.click.name.toLowerCase().contains("left")) {
                        player.performCommand("cage ${clicked.name}")
                        player.teleport(clicked.location.add(0.0, 2.0, -2.0))

                        player.sendMessage("§fVocê prendeu o jogador: §c§o§n" + clicked.name)
                    }

                    player.closeInventory()
                }
            }
        }
    }
}