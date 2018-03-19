/*
 * Copyright (©) kadmin
 *
 * Projeto desenvolvido por Miguel Lukas
 * Todos os direitos Reservados
 *
 * Modificado em: 19/03/18 12:33
 */

package me.dark.utils

import org.bukkit.plugin.Plugin
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URLDecoder
import java.util.*
import java.util.jar.JarFile

class ClassGetter(private val instance: Plugin) {

    var todasAsClasses = ArrayList<Class<*>>()
    private var jarName: String? = null

    init {

        try {
            var path = instance.javaClass.protectionDomain.codeSource.location.path
            path = URLDecoder.decode(path, "UTF-8")
            val paths = path.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            jarName = paths[paths.size - 1]
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

    }

    fun getAllClasses() {
        try {
            val jarPath = "plugins/" + jarName!!
            val jarFile = JarFile(jarPath)

            val entries = jarFile.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                val entryName = entry.name
                var className: String? = null
                if (entryName.endsWith(".class")) {
                    className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "")
                }
                if (className != null) {
                    todasAsClasses.add(this.loadClass(className)!!)
                }
            }
        } catch (e: IOException) {
            throw RuntimeException("Unexpected IOException reading JAR File", e)
        }
    }

    fun getClassesForPackage(pkgname: String): ArrayList<Class<*>> {
        val classes = ArrayList<Class<*>>()

        val src = instance.javaClass.protectionDomain.codeSource
        if (src != null) {
            val resource = src.location
            resource.path
            try {
                processJarfile(pkgname, classes)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

        }
        return classes
    }

    private fun loadClass(className: String): Class<*>? {
        try {
            return Class.forName(className)
        } catch (e: ClassNotFoundException) {
            throw RuntimeException("Unexpected ClassNotFoundException loading class '$className'")
        } catch (e: NoClassDefFoundError) {
        } catch (e: NoSuchMethodError) {

        }

        return null
    }

    @Throws(FileNotFoundException::class)
    private fun processJarfile(pkgname: String, classes: ArrayList<Class<*>>) {
        val relPath = pkgname.replace('.', '/')
        var jarPath: String = "plugins/" + jarName!!
        val jarFile: JarFile
        try {
            jarFile = JarFile(jarPath)
        } catch (e: IOException) {
            throw RuntimeException("Unexpected IOException reading JAR File '$jarPath'", e)
        }

        val entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            val entryName = entry.name
            var className: String? = null
            if (entryName.endsWith(".class") && entryName.startsWith(relPath)
                    && entryName.length > relPath.length + "/".length) {
                className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "")
            }
            if (className != null) {
                classes.add(this.loadClass(className)!!)
            }
        }
    }
}
