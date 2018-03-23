/*
 * Copyright (©) kadmin
 *
 * Projeto desenvolvido por Miguel Lukas
 * Todos os direitos Reservados
 *
 * Modificado em: 23/03/18 09:31
 */

package me.dark.hack

import org.apache.commons.lang.Validate
import org.bukkit.BanList.Type
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import java.util.*
import java.util.concurrent.TimeUnit

class BanAPI {

    /**
     * Creates a new BanEntry. Player can be online or offline.
     *
     * <br></br>
     * Note: This can also create TempBans.
     *
     * <br></br>
     * Example: new BanAPI().addBan(player, sender, 10:d, null); This creates a
     * temp ban for 10 days with the default ban reason.
     *
     * @param player Player to ban.
     * @param banCreator Creator of the ban.
     * @param expirationDate Expiration date of the ban.
     * @param reason Reason for the ban. Default is "The ban hammer has spoken!"
     *
     *
     *
     * - Note: if expirationDate is null, the ban will be permanent.
     *
     *
     *
     * - Note: If reason is null, the reason will be
     * "The ban hammer has spoken!"
     *
     *
     *
     * <table border=1>
     * <caption>Expiration Date Information</caption>
     * <tr>
     * <th>Unit</th>
     * <th>Aliases</th>
    </tr> *
     * <tr>
     * <th>Seconds</th>
     * <th>seconds, sec, s, second</th>
    </tr> *
     * <tr>
     * <th>Minutes</th>
     * <th>minutes, min, m, minute</th>
    </tr> *
     * <tr>
     * <th>Hours</th>
     * <th>hours, hr, h, hrs, hour</th>
    </tr> *
     * <tr>
     * <th>Days</th>
     * <th>days, day, d</th>
    </tr> *
     * <tr>
     * <th>Weeks</th>
     * <th>weeks, week, w, wk, wks</th>
    </tr> *
     * <tr>
     * <th>Months</th>
     * <th>months, month, mth, mths</th>
    </tr> *
    </table> *
     */
    fun addBan(player: String, banCreator: CommandSender, expirationDate: String?, reason: String?) {
        var reason = reason
        Validate.notNull(player, "O jogador nao pode ser nulo!")
        val list = Bukkit.getBanList(Type.NAME)
        if (reason == null) {
            reason = "§cBanido pelo sistema"
        }
        if (expirationDate == null) {
            list.addBan(player, reason, null, banCreator.name)
            return
        }
        if (!expirationDate.contains(":")) {
            banCreator.sendMessage("§cFormato incorreto!")
            return
        }
        val split = expirationDate.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (!isNumber(split[0])) {
            banCreator.sendMessage("§cFormato incorreto!")
            return
        }
        val current = System.currentTimeMillis()
        var total: Long = 0
        val type = TimeType.getAliasUsed(split[1])
        if (type == null) {
            banCreator.sendMessage("§cNao e possivel banir. A unidade de tempo \"" + split[1] + "\" nao existe.")
            return
        }
        when (type) {
            BanAPI.TimeType.SECOND -> total += TimeUnit.SECONDS.toMillis(java.lang.Long.parseLong(split[0])) + current
            BanAPI.TimeType.MINUTE -> total += TimeUnit.MINUTES.toMillis(java.lang.Long.parseLong(split[0])) + current
            BanAPI.TimeType.HOUR -> total += TimeUnit.HOURS.toMillis(java.lang.Long.parseLong(split[0])) + current
            BanAPI.TimeType.DAY -> total += TimeUnit.DAYS.toMillis(java.lang.Long.parseLong(split[0])) + current
            BanAPI.TimeType.WEEK -> total += TimeUnit.DAYS.toMillis(java.lang.Long.parseLong(split[0])) * 7 + current
            BanAPI.TimeType.MONTH -> total += TimeUnit.DAYS.toMillis(java.lang.Long.parseLong(split[0])) * 31 + current

            else -> banCreator.sendMessage("§cFormato incorreto!")
        }
        list.addBan(player, reason, Date(total), banCreator.name)
        return
    }

    /**
     * Unbans a player.
     *
     * @param target This is the player to unban.
     *
     *
     *
     * - Note: Add a check in your command method to see if the
     * banlist contains the target.
     *
     */
    fun unban(target: String) {
        Validate.notNull(target, "Target cannot be null.")
        val list = Bukkit.getBanList(Type.NAME)
        Validate.notNull(list.getBanEntry(target), "Cannot find BanList entry \"$target\"")
        list.pardon(target)
    }

    /**
     * Checks if a target is still banned or not.
     *
     * @param target This is the player that is being checked for the ban
     * expire.
     * @return Returns false if still banned, true otherwise.
     *
     *
     *
     * - Note: Use this method in a login event. To check if a player is
     * still banned.
     *
     */
    fun isBanExpired(target: String): Boolean {
        Validate.notNull(target, "Target cannot be null.")
        val entry = Bukkit.getBanList(Type.NAME).getBanEntry(target)
        Validate.notNull(entry, "Cannot find BanList entry \"$target\"")
        val currentTime = System.currentTimeMillis() //Gets the current time
        return getExpirationLong(target) <= currentTime
    }

    /**
     * Gets the expiration date from a ban entry.
     *
     * @param target Target to get the expiration date from.
     * @return Returns the expiration date as a long in milliseconds.
     */
    fun getExpirationLong(target: String): Long {
        Validate.notNull(target, "Target cannot be null.")
        val entry = Bukkit.getBanList(Type.NAME).getBanEntry(target)
        Validate.notNull(entry, "Cannot find BanList entry \"$target\"")
        return entry.expiration.time
    }

    /**
     * Gets the expiration date from a ban entry.
     *
     * @param target Target top get the expiration date from.
     * @return Returns the expiration date as a long in proper format.
     */
    fun getExpirationDate(target: String): Date {
        Validate.notNull(target, "Target cannot be null.")
        val entry = Bukkit.getBanList(Type.NAME).getBanEntry(target)
        Validate.notNull(entry, "Cannot find BanList entry \"$target\"")
        return entry.expiration
    }

    /**
     * Gets the reason for the ban.
     *
     * @param target The target to get the ban reason from.
     * @return Returns the reason for the ban.
     */
    fun getReason(target: String): String {
        Validate.notNull(target, "Target cannot be null.")
        val entry = Bukkit.getBanList(Type.NAME).getBanEntry(target)
        Validate.notNull(entry, "Cannot find BanList entry \"$target\"")
        return entry.reason
    }

    /**
     * Checks if a value is a number or not.
     *
     * @param string The string to check.
     * @return True if its a number, false otherwise.
     */
    private fun isNumber(string: String): Boolean {
        try {
            Integer.parseInt(string)
            return true
        } catch (e: NumberFormatException) {
            return false
        }

    }

    /**
     * Time type is an enum of all the possible values for the ban length. You
     * can add more aliases to the list as well.
     */
    enum class TimeType(
            /**
             * Gets the aliases from a given TimeType
             *
             * @return A list of the aliases
             */
            var alias: List<String>) {
        SECOND(Arrays.asList<String>("s", "seconds", "second", "sec")),
        MINUTE(Arrays.asList<String>("m", "minutes", "minute", "min")),
        HOUR(Arrays.asList<String>("h", "hours", "hour", "hr", "hrs")),
        DAY(Arrays.asList<String>("d", "days", "day")),
        WEEK(Arrays.asList<String>("w", "weeks", "week", "wk", "wks")),
        MONTH(Arrays.asList<String>("months", "month", "mth", "mths"));

        companion object {

            /**
             * Gets the alias used in the temp-ban
             *
             * @param input Input to get alias from.
             * @return Returns the TimeType the alias came from
             */
            fun getAliasUsed(input: String): TimeType? {
                for (alias in TimeType.values()) {
                    for (value in alias.alias) {
                        if (value == input) {
                            return alias
                        }
                    }
                }
                return null
            }
        }
    }
}