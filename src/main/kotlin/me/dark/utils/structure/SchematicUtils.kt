/*
 * Copyright (©) kadmin
 *
 * Projeto desenvolvido por Miguel Lukas
 * Todos os direitos Reservados
 *
 * Modificado em: 22/03/18 18:50
 */

package me.dark.utils.structure

import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools
import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import java.io.File
import java.io.FileInputStream

@Suppress("DEPRECATION")
class SchematicUtils(val file: File) {

    private var width: Short = 0
    private var length: Short = 0
    private var height: Short = 0

    private var blocks: ByteArray = ByteArray(0)
    private var data: ByteArray = ByteArray(0)

    fun pasteSchematic(location: Location, rmAir: Boolean): ArrayList<Block> {
        val inputStream = FileInputStream(file)
        val tagCompound: NBTTagCompound = NBTCompressedStreamTools.a(inputStream)

        width = tagCompound.getShort("Width")
        length = tagCompound.getShort("Length")
        height = tagCompound.getShort("Height")

        blocks = tagCompound.getByteArray("Blocks")
        data = tagCompound.getByteArray("Data")

        inputStream.close()

        val list: ArrayList<Block> = ArrayList()

        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until length) {
                    val index = y * width * length + z * width + x
                    val loc = Location(location.world, x + location.x - (width / 2), y + location.y, z + location.z - (length / 2))
                    val blocky: Int = blocks[index].toInt() and 0xFF

                    val material: Material = Material.getMaterial(blocky)
                    if (rmAir && material == Material.AIR)
                        continue

                    val block: Block = loc.block
                    block.type = material
                    block.data = data[index]
                    list.add(block)
                }
            }
        }

        return list
    }
}