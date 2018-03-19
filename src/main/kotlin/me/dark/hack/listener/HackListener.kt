/*
 * Copyright (©) kadmin
 *
 * Projeto desenvolvido por Miguel Lukas
 * Todos os direitos Reservados
 *
 * Modificado em: 19/03/18 15:57
 */

package me.dark.hack.listener

import me.dark.Main
import me.dark.hack.BanAPI
import me.dark.hack.checker.AutoSoupCheck
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

object HackListener : Listener {

    private val check: HashMap<UUID, AutoSoupCheck> = HashMap()

    @EventHandler
    fun onPlayerPreLogin(event: AsyncPlayerPreLoginEvent) {
        if(event.kickMessage.contains("ban")) {
            event.kickMessage = "Res: " + SimpleDateFormat().format(BanAPI().getExpirationDate(event.name))
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        check[event.player.uniqueId] = AutoSoupCheck()
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val currentItem = event.currentItem

        if(currentItem != null && currentItem.type == Material.MUSHROOM_SOUP)
            check[event.whoClicked.uniqueId]!!.setLastClick()
    }

    @EventHandler
    fun onCloseInventory(event: InventoryCloseEvent) {
        check[event.player.uniqueId]!!.setLastClose()
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        if(event.item != null && event.item.type == Material.MUSHROOM_SOUP) {
            event.item.type = Material.BOWL
            val autoSoupCheck = check[player.uniqueId]
            autoSoupCheck!!.setLastInteract()

            if(!autoSoupCheck.isLegit(AutoSoupCheck.CheckMoment.INTERACT)) {
                BanAPI().addBan(player.name, Bukkit.getServer().consoleSender, null, "§cMotivo: §fUso de §c§n§oHACK §fAUTO-SOUP")
                player.kickPlayer("§cVocê foi banido!" + System.getProperty("line.separator") + "§cMotivo: §fUso de §c§n§oHACK §fAUTO-SOUP")
            }
        }
    }

    @EventHandler
    fun onPlayerHeldItem(event: PlayerItemHeldEvent) {
        val player = event.player
        val itemInHand = player.itemInHand

        if(itemInHand != null && itemInHand.type == Material.MUSHROOM_SOUP) {
            val autoSoupCheck = check[player.uniqueId]
            autoSoupCheck!!.setHeld()

            if(!autoSoupCheck.isLegit(AutoSoupCheck.CheckMoment.HELD)) {
                BanAPI().addBan(player.name, Bukkit.getServer().consoleSender, null, "cMotivo: §fUso de §c§n§oHACK §fAUTO-SOUP")
                player.kickPlayer("§cVocê foi banido!" + System.getProperty("line.separator") + "§cMotivo: §fUso de §c§n§oHACK §fAUTO-SOUP")
            }
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        check.remove(event.player.uniqueId)
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        val player = event.entity

        if(player is Player) {
            if(player.hasMetadata("nfall") && event.cause == EntityDamageEvent.DamageCause.FALL) {
                if(event.damage < 3.0) {
                    BanAPI().addBan(player.name, Bukkit.getServer().consoleSender, null, "cMotivo: §fUso de §c§n§oHACK §fAUTO-SOUP")
                    player.kickPlayer("§cVocê foi banido!" + System.getProperty("line.separator") + "§cMotivo: §fUso de §c§n§oHACK §fnNo-Fall")
                } else {
                    player.removeMetadata("nfall", Main.instance)
                }
            }
        }
    }
}