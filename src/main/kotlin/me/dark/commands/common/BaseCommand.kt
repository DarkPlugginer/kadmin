/*
 * Copyright (©) kadmin
 *
 * Projeto desenvolvido por Miguel Lukas
 * Todos os direitos Reservados
 *
 * Modificado em: 19/03/18 13:10
 */

package me.dark.commands.common

import me.dark.utils.enums.Permission
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(RetentionPolicy.RUNTIME)
annotation class BaseCommand(val aliases: Array<String>,
                             val usage: String = "",
                             val desc: String = "",
                             val min: Int = 0,
                             val max: Int = 0,
                             val hidden: Boolean = false,
                             val permission: Permission)
