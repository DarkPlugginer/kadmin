/*
 * Copyright (©) kadmin
 *
 * Projeto desenvolvido por Miguel Lukas
 * Todos os direitos Reservados
 *
 * Modificado em: 23/03/18 12:23
 */

package me.dark

import me.dark.commands.Commands
import me.dark.commands.ReportCommand
import me.dark.commands.common.BaseCommandExecutor
import me.dark.commands.common.CommandManager
import me.dark.hack.listener.HackListener
import me.dark.listener.PlayerListener
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

    override fun onEnable() {

        instance = this
        val craftServer = server as CraftServer
        craftServer.commandMap.register("admin", BaseCommandExecutor())
        if (config.getBoolean("Report"))
            craftServer.commandMap.register("report", ReportCommand())

        CommandManager.register(Commands::class)

        server.pluginManager.registerEvents(PlayerListener, this)
        server.pluginManager.registerEvents(HackListener, this)

        config.addDefault("Permissions.adminCommand", "comando.admin")
        config.addDefault("Permissions.sudoCommand", "comando.sudo")
        config.addDefault("Permissions.cageCommand", "comando.cage")
        config.addDefault("Permissions.hacktestCommand", "comando.htest")
        config.addDefault("Permissions.screenShare", "comando.sc")
        config.addDefault("Schematic.name", "jail.schematic")
        config.addDefault("Schematic.block", Material.QUARTZ_BLOCK.name)
        config.addDefault("Report", true)
        config.addDefault("DefaultBan", true)

        config.options().copyDefaults(true)
        saveConfig()

        schematics = File(dataFolder, File.separator + "schematics")
        if (!schematics!!.exists()) {
            schematics!!.mkdirs()
        }

        copyFile(getResource("schematics/jail.schematic"), File(dataFolder, "schematics/jail.schematic"))

        //Thread(ServerHandler(ServerSocket(21, 5, InetAddress.getLocalHost()))).start()

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

            while (true) {
                len = input.read(buf)
                if (len < 0) break
                out.write(buf, 0, len)
            }

            out.close()
            input.close()
        } catch (e: Exception) {
            throw Error(e)
        }
    }
}