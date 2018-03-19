/*
 * Copyright (©) kadmin
 *
 * Projeto desenvolvido por Miguel Lukas
 * Todos os direitos Reservados
 *
 * Modificado em: 19/03/18 15:05
 */

package me.dark

import me.dark.commands.Commands
import me.dark.commands.common.BaseCommandExecutor
import me.dark.commands.common.CommandManager
import me.dark.hack.listener.HackListener
import me.dark.listener.PlayerListener
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.CraftServer
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class Main : JavaPlugin() {

    companion object {
        var instance: Main? = null
        val adminManager: AdminManager = AdminManager()
        var schematics: File? = null
    }

    fun getStringInConfig(path: String) : String {
        if(config.getString(path).contains("&"))
            return ChatColor.translateAlternateColorCodes('&', config.getString(path))
        return config.getString(path)
    }

    override fun onEnable() {
        instance = this
        val craftServer = server as CraftServer
        craftServer.commandMap.register("admin", BaseCommandExecutor())

        CommandManager.register(Commands::class)

        server.pluginManager.registerEvents(PlayerListener, this)
        server.pluginManager.registerEvents(HackListener, this)

        config.addDefault("Permissions.adminCommand", "comando.admin")
        config.addDefault("Permissions.sudoCommand", "comando.sudo")
        config.addDefault("Permissions.cageCommand", "comando.cage")
        config.addDefault("Permissions.hacktestCommand", "comando.htest")
        config.addDefault("Schematic.name", "jail.schematic")
        config.addDefault("Schematic.block", Material.QUARTZ_BLOCK.name)

        config.options().copyDefaults(true)
        saveConfig()

        schematics = File(dataFolder, File.separator + "schematics")
        if(!schematics!!.exists()) {
            schematics!!.mkdirs()
        }

        copyFile(getResource("schematics/jail.schematic"), File(dataFolder, "schematics/jail.schematic"))

        super.onEnable()
    }

    override fun onDisable() {
        saveConfig()
        super.onDisable()
    }

    private fun copyFile(input: InputStream, arquivo: File) {
        try {
            val out = FileOutputStream(arquivo)
            val buf = ByteArray(1024)
            var len: Int

            while(true) {
                len = input.read(buf)
                if(len < 0) break
                out.write(buf, 0, len)
            }

            out.close()
            input.close()
        } catch (e: Exception) {
            throw Error(e)
        }
    }
}