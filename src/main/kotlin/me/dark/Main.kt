package me.dark

import me.dark.hack.listener.HackListener
import me.dark.listener.PlayerListener
import me.dark.utils.ClassGetter
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
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
        println(1)
        val craftServer = server as CraftServer

        val getter = ClassGetter(this)
        for (clazz in getter.getClassesForPackage("me.dark.commands")) {
            craftServer.commandMap.register(clazz.simpleName.toLowerCase().replace("command", ""), clazz.newInstance() as Command?)
        }

        server.pluginManager.registerEvents(PlayerListener, this)
        server.pluginManager.registerEvents(HackListener, this)

        config.addDefault("Permissions.adminCommand", "comando.admin")
        config.addDefault("Permissions.sumoCommand", "comando.sumo")
        config.addDefault("Permissions.cageCommand", "comando.cage")
        config.addDefault("Usages.cageUsage", "Use /cage jogador")
        config.addDefault("Usages.sumoUsage", "Use /sumo jogador mensagem")
        config.addDefault("Erros.consoleError", "Apenas jogadores!")
        config.addDefault("Erros.cageError", "Erro, este jogador é inválido")
        config.addDefault("Erros.sumoError", "Erro, este jogador é inválido")
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