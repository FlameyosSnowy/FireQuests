@file:Suppress("unused")

package me.flame.quests.spigot.config

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import kotlin.reflect.KProperty

@Suppress("ClassName")
abstract class Config {

    /** Always assumed to be already loaded */
    abstract val config: ConfigurationSection

    open var autoSave: Boolean = true

    operator fun contains(key: String): Boolean =
        config.contains(key)

    operator fun get(key: String): Any? =
        config[key]

    operator fun set(key: String, value: Any?) {
        config.set(key, value)
        if (autoSave) onChange()
    }

    /**
     * Called when data mutates and autoSave is enabled.
     * Overridden by file-backed configs.
     */
    protected open fun onChange() {}

    inner class string(private val path: String, private val def: String = "") {
        operator fun getValue(ref: Any?, prop: KProperty<*>): String =
            config.getString(path, def)!!

        operator fun setValue(ref: Any?, prop: KProperty<*>, value: String) =
            set(path, value)
    }

    inner class int(private val path: String, private val def: Int = 0) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): Int =
            config.getInt(path, def)

        operator fun setValue(ref: Any?, prop: KProperty<*>, value: Int) =
            set(path, value)
    }

    inner class boolean(private val path: String, private val def: Boolean = false) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): Boolean =
            config.getBoolean(path, def)

        operator fun setValue(ref: Any?, prop: KProperty<*>, value: Boolean) =
            set(path, value)
    }

    inner class stringList(private val path: String) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): List<String> =
            config.getStringList(path)

        operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<String>) =
            set(path, value)
    }

    inner class section(private val path: String) {
        operator fun getValue(ref: Any?, prop: KProperty<*>): ConfigurationSection? =
            config.getConfigurationSection(path)

        operator fun setValue(ref: Any?, prop: KProperty<*>, value: ConfigurationSection?) =
            set(path, value)
    }
}

open class ConfigFile(
    protected val file: File
) : Config() {

    private var _config: FileConfiguration? = null

    override val config: ConfigurationSection
        get() = _config
            ?: error("Config not loaded. Call ConfigFile.reload() first.")

    open fun reload() {
        val loaded = YamlConfiguration.loadConfiguration(file)

        _config = loaded
    }

    open fun save() {
        val config = _config
            ?: error("Config not loaded. Call reload() first.")

        config.save(file)
    }

    override fun onChange() {
        save()
    }
}