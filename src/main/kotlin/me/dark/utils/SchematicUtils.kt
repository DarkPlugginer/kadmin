/*
 * Copyright (©) Nano Team
 *
 * Projeto desenvolvido por Miguel Lukas
 * Todos os direitos Reservados
 *
 * Modificado em: 18/03/18 21:01
 * Criado em: 18/03/18 21:02
 */

package me.dark.utils

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

    fun pasteSchematic(location: Location, rmAir: Boolean) : ArrayList<Block> {
        var inputStream = FileInputStream(file)
        var tagCompound: NBTTagCompound = NBTCompressedStreamTools.a(inputStream)

        width = tagCompound.getShort("Width")
        length = tagCompound.getShort("Length")
        height = tagCompound.getShort("Height")

        blocks = tagCompound.getByteArray("Blocks")
        data = tagCompound.getByteArray("Data")

        inputStream.close()

        var list: ArrayList<Block> = ArrayList()

        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until length) {
                    var index = y * width * length + z * width + x
                    val loc = Location(location.world, x + location.x -(width/2), y + location.y, z + location.z - (length/2))
                    var blocky: Int = blocks[index].toInt() and  0xFF

                    var material: Material = Material.getMaterial(blocky)
                    if(rmAir && material == Material.AIR)
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