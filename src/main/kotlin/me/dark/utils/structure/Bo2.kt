/*
 * Copyright (©) kadmin
 *
 * Projeto desenvolvido por Miguel Lukas
 * Todos os direitos Reservados
 *
 * Modificado em: 23/03/18 08:47
 */

package me.dark.utils.structure

import me.dark.Main
import net.minecraft.server.v1_8_R3.BlockPosition
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.scheduler.BukkitRunnable
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*

class Bo2 {

    private fun addBlockUpdate(location: Location) {
        blocksForUpdate.add(location)
    }

    fun startUpdate() {
        object : BukkitRunnable() {
            override fun run() {
                if (!blocksForUpdate.isEmpty()) {
                    val world = (Bukkit.getWorlds()[0] as CraftWorld).handle
                    for (location in blocksForUpdate) {
                        world.notify(BlockPosition(location.blockX, location.blockY, location.blockZ))
                    }
                    blocksForUpdate.clear()
                }
            }
        }.runTaskTimer(Main.instance, 1, 1)
    }

    /* Spawn BO2 */

    fun spawn(location: Location, file: File): List<Block> {
        val time = System.currentTimeMillis()
        val reader: BufferedReader
        val blocks = ArrayList<Block>()
        try {
            reader = BufferedReader(FileReader(file))
            var line: String?
            while (reader.readLine() != null) {
                line = reader.readLine()

                if (!line!!.contains(",") || !line.contains(":")) {
                    continue
                }

                val parts = line.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val coordinates = parts[0].split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val blockData = parts[1].split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                setBlockFast(location.world, location.blockX + Integer.valueOf(coordinates[0]),
                        location.blockY + Integer.valueOf(coordinates[2]), location.blockZ + Integer.valueOf(coordinates[1]),
                        Integer.valueOf(blockData[0]), if (blockData.size > 1) java.lang.Byte.valueOf(blockData[1]) else 0)
                blocks.add(location.world.getBlockAt(location.blockX + Integer.valueOf(coordinates[0]),
                        location.blockY + Integer.valueOf(coordinates[2]), location.blockZ + Integer.valueOf(coordinates[1])))
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        println("BO2 spawnada em " + (time - System.currentTimeMillis()) + "ms")
        return blocks
    }

    /*

    fun load(location: Location, file: File): List<FutureBlock> {
        val reader: BufferedReader
        val blocks = ArrayList<FutureBlock>()
        try {
            reader = BufferedReader(FileReader(file))
            var line: String?
            while (reader.readLine() != null) {
                line = reader.readLine()

                if (!line!!.contains(",") || !line.contains(":")) {
                    continue
                }
                val parts = line.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val coordinates = parts[0].split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val blockData = parts[1].split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                blocks.add(
                        FutureBlock(location.clone().add(Integer.valueOf(coordinates[0]).toDouble(), Integer.valueOf(coordinates[2]).toDouble(), Integer.valueOf(coordinates[1]).toDouble()),
                                Integer.valueOf(blockData[0]), if (blockData.size > 1) java.lang.Byte.valueOf(blockData[1]) else 0))
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return blocks
    }
    */

    /* Set methods */

    private fun setBlockFast(world: World, x: Int, y: Int, z: Int, blockId: Int, data: Byte): Boolean {
        if (y >= 255 || y < 0) {
            return false
        }
        val w = (world as CraftWorld).handle
        w.getChunkAt(x shr 4, z shr 4)
        addBlockUpdate(Location(Bukkit.getWorlds()[0], x.toDouble(), y.toDouble(), z.toDouble()))
        return true
    }

    /* FutureBlock Utils */

    inner class FutureBlock(private val location: Location, val id: Int, val data: Byte) {
        fun place() {
            location.block.setTypeIdAndData(id, data, true)
        }
    }

    companion object {

        private val blocksForUpdate = HashSet<Location>()
    }
}
