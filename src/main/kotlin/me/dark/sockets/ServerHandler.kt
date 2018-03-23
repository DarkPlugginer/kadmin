/*
 * Copyright (©) kadmin
 *
 * Projeto desenvolvido por Miguel Lukas
 * Todos os direitos Reservados
 *
 * Modificado em: 23/03/18 12:25
 */

package me.dark.sockets

import org.bukkit.Bukkit
import java.io.IOException
import java.io.PrintStream
import java.net.ServerSocket
import java.net.Socket
import java.util.Scanner
import kotlin.collections.ArrayList
import kotlin.collections.dropLastWhile
import kotlin.collections.forEach
import kotlin.collections.toTypedArray

class ServerHandler(private val serverSocket: ServerSocket) : Runnable {

    companion object {
        private var client: Socket? = null
        private var clients: ArrayList<Socket>? = null
    }

    init {
        println("Server escutando na porta: " + serverSocket.localPort)
        clients = ArrayList()
    }

    override fun run() {
        while (true) {
            try {
                client = serverSocket.accept()

                println("Novo cliente: ${client!!.inetAddress.hostAddress}")

                val scanner = Scanner(client!!.getInputStream())
                while (scanner.hasNext()) {
                    val str = scanner.next()

                    when {
                        str.startsWith("*") -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), str.replace("*", ""))
                        str.startsWith("/") -> Bukkit.getServer().consoleSender.sendMessage(str.replace("/", ""))
                        str.startsWith("-") -> Bukkit.getServer().onlinePlayers.forEach { o -> o.sendMessage(str.replace("-", "")) }
                        str.startsWith("!") -> {
                            val name = str.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                            val message = str.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]

                            if (Bukkit.getPlayerExact(name) == null) {
                                PrintStream(client!!.getOutputStream()).println("O jogador requisitado nao se encontra online.")
                                return
                            }

                            Bukkit.getPlayerExact(name).sendMessage(message.replace("!", ""))
                        }
                    }
                }
            } catch (e: IOException) {
                System.err.println(e.message)
            }
        }
    }
}
