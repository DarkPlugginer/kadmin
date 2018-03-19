/*
 * Copyright (©) kadmin
 *
 * Projeto desenvolvido por Miguel Lukas
 * Todos os direitos Reservados
 *
 * Modificado em: 19/03/18 15:05
 */

package me.dark.commands.common

import me.dark.commands.Commands
import org.bukkit.ChatColor
import java.lang.reflect.Method
import java.util.*
import kotlin.reflect.KClass

object CommandManager {
    val light = ChatColor.GREEN
    val dark = ChatColor.DARK_GREEN
    val neutral = ChatColor.WHITE
    val highlight = ChatColor.AQUA
    val extra = ChatColor.DARK_RED
    val error = ChatColor.RED
    val warning = ChatColor.YELLOW

    private val commands = LinkedHashMap<BaseCommand, Method>()

    fun register(clazz: KClass<Commands>) {
        val methods = clazz.javaObjectType.declaredMethods

        for (method in methods) {
            if (method.isAnnotationPresent(BaseCommand::class.java)) {
                commands[method.getAnnotation(BaseCommand::class.java)] = method
            }
        }
    }

    fun unregister(clazz: Class<*>) {
        val methods = clazz.methods

        for (method in methods) {
            if (method.isAnnotationPresent(BaseCommand::class.java)) {
                commands.remove(method.getAnnotation(BaseCommand::class.java))
            }
        }
    }

    fun unregisterAll() {
        commands.clear()
    }

    fun getCommands(): LinkedList<BaseCommand> {
        val baseCommands = LinkedList<BaseCommand>()
        baseCommands.addAll(commands.keys)

        return baseCommands
    }

    fun getCommand(label: String): BaseCommand? {
        commands.keys.forEach { baseCommand: BaseCommand ->
            baseCommand.aliases.forEach { s ->
                if (s.equals(label, ignoreCase = true)) {
                    return baseCommand
                    return@forEach
                }
            }
        }
        return null
    }

    fun execute(command: BaseCommand, vararg args: Any) {
        try {
            commands[command]!!.invoke(commands[command]!!.declaringClass.newInstance(), *args)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}