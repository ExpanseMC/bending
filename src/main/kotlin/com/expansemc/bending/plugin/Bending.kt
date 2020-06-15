package com.expansemc.bending.plugin

import com.expansemc.bending.api.BendingService
import com.expansemc.bending.api.ability.*
import com.expansemc.bending.api.bender.Bender
import com.expansemc.bending.api.bender.BenderService
import com.expansemc.bending.api.config.AbilityConfig
import com.expansemc.bending.api.element.Element
import com.expansemc.bending.api.registry.CatalogRegistry
import com.expansemc.bending.api.registry.FactoryRegistry
import com.expansemc.bending.api.registry.register
import com.expansemc.bending.api.registry.registerAll
import com.expansemc.bending.api.util.NamespacedKeys
import com.expansemc.bending.api.util.NamespacedKeys.bending
import com.expansemc.bending.plugin.ability.*
import com.expansemc.bending.plugin.bender.SimpleBenderService
import com.expansemc.bending.plugin.command.BendingCommand
import com.expansemc.bending.plugin.config.SimpleAbilityConfig
import com.expansemc.bending.plugin.element.SimpleElement
import com.expansemc.bending.plugin.listener.AbilityListener
import com.expansemc.bending.plugin.registry.SimpleCatalogRegistry
import com.expansemc.bending.plugin.registry.SimpleFactoryRegistry
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import me.lucko.commodore.Commodore
import me.lucko.commodore.CommodoreProvider
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.PluginCommand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerLoadEvent
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.logging.Level
import javax.swing.text.html.parser.Entity

class Bending : JavaPlugin(), Listener {

    companion object {
        lateinit var PLUGIN: Plugin
            private set
    }

    override fun onEnable() {
        PLUGIN = this

        registerServices()
        registerFactories()
        registerDefaultAbilityContextKeys()
        registerDefaultAbilityExecutionTypes()
        registerDefaultElements()
        registerListeners()
        registerCommands()
    }

    private fun registerServices() {
        this.logger.info("Registering services...")

        Bukkit.getServicesManager().apply {
            register(
                CatalogRegistry::class.java,
                SimpleCatalogRegistry(), this@Bending, ServicePriority.Highest
            )
            register(
                FactoryRegistry::class.java,
                SimpleFactoryRegistry(), this@Bending, ServicePriority.Highest
            )
            register(AbilityService::class.java, SimpleAbilityService(), this@Bending, ServicePriority.Highest)
            register(BenderService::class.java, SimpleBenderService(), this@Bending, ServicePriority.Highest)
            register(BendingService::class.java, SimpleBendingService(this@Bending), this@Bending, ServicePriority.Highest)
        }
    }

    private fun registerFactories() {
        this.logger.info("Registering factories...")

        FactoryRegistry.instance
            .register<AbilityConfig.Builder>(SimpleAbilityConfig::Builder)
            .register<AbilityContext>(::SimpleAbilityContext)
            .register<AbilityContextKey.Builder<*>> { SimpleAbilityContextKey.Builder<Any>() }
            .register<AbilityExecutionType.Builder>(SimpleAbilityExecutionType::Builder)
            .register<AbilityType.Builder>(SimpleAbilityType::Builder)
            .register<Element.Builder>(SimpleElement::Builder)
    }

    private fun registerDefaultAbilityContextKeys() {
        this.logger.info("Registering default ability context keys...")

        CatalogRegistry.instance
            .registerAll(
                AbilityContextKey.builder<Collection<Location>>()
                    .key(bending("affected_locations"))
                    .build(),
                AbilityContextKey.builder<Collection<Entity>>()
                    .key(bending("affected_entities"))
                    .build(),
                AbilityContextKey.builder<Bender>()
                    .key(bending("bender"))
                    .build(),
                AbilityContextKey.builder<Location>()
                    .key(bending("current_location"))
                    .build(),
                AbilityContextKey.builder<Vector>()
                    .key(bending("direction"))
                    .build(),
                AbilityContextKey.builder<AbilityExecutionType>()
                    .key(bending("execution_type"))
                    .build(),
                AbilityContextKey.builder<Float>()
                    .key(bending("fall_distance"))
                    .build(),
                AbilityContextKey.builder<Location>()
                    .key(bending("origin"))
                    .build(),
                AbilityContextKey.builder<Player>()
                    .key(bending("player"))
                    .build()
            )
    }

    private fun registerDefaultElements() {
        this.logger.info("Registering default elements...")

        CatalogRegistry.instance
            .registerAll(
                Element.builder()
                    .key(bending("air"))
                    .name("Air")
                    .color(ChatColor.GRAY)
                    .build(),
                Element.builder()
                    .key(bending("chi"))
                    .name("Chi")
                    .color(ChatColor.YELLOW)
                    .build(),
                Element.builder()
                    .key(bending("earth"))
                    .name("Earth")
                    .color(ChatColor.GREEN)
                    .build(),
                Element.builder()
                    .key(bending("fire"))
                    .name("Fire")
                    .color(ChatColor.RED)
                    .build(),
                Element.builder()
                    .key(bending("water"))
                    .name("Water")
                    .color(ChatColor.AQUA)
                    .build()
            )
    }

    private fun registerDefaultAbilityExecutionTypes() {
        this.logger.info("Registering default ability execution types...")

        CatalogRegistry.instance
            .registerAll(
                AbilityExecutionType.builder()
                    .key(bending("fall"))
                    .build(),
                AbilityExecutionType.builder()
                    .key(bending("jump"))
                    .build(),
                AbilityExecutionType.builder()
                    .key(bending("left_click"))
                    .build(),
                AbilityExecutionType.builder()
                    .key(bending("passive"))
                    .build(),
                AbilityExecutionType.builder()
                    .key(bending("sneak"))
                    .build(),
                AbilityExecutionType.builder()
                    .key(bending("sprint_off"))
                    .build(),
                AbilityExecutionType.builder()
                    .key(bending("sprint_on"))
                    .build()
            )
    }

    private fun registerDefaultAbilityConfig() {
        this.logger.info("Registering default ability config...")

        val configPath: Path = this.dataFolder.toPath().resolve("abilities").resolve("default.conf")

        if (Files.notExists(configPath)) {
            Files.createDirectories(configPath.parent)
            Bending::class.java.getResourceAsStream("default.conf").use {
                Files.copy(it, configPath)
            }
        }

        val loader = HoconConfigurationLoader.builder().setPath(configPath).build()

        val node: ConfigurationNode = try {
            loader.load()
        } catch (e: IOException) {
            this.logger.log(Level.SEVERE, "Failed to load default.conf", e)
            loader.createEmptyNode()
        }

        val abilityMap: IdentityHashMap<AbilityType, ConfigurationNode> = node.childrenMap
            .mapNotNull { (key: Any, child: ConfigurationNode) ->
                val type: AbilityType? = AbilityType[NamespacedKeys.of(key.toString())]

                if (type == null) {
                    this.logger.warning("Unknown ability type in config ($configPath): $key")
                    null
                } else {
                    type to child
                }
            }
            .toMap(IdentityHashMap())

        CatalogRegistry.instance.register(
            AbilityConfig.builder()
                .key(bending("default"))
                .provider(abilityMap::get)
                .build()
        )
    }

    private fun registerListeners() {
        this.logger.info("Registering listeners...")

        Bukkit.getPluginManager().registerEvents(AbilityListener(), this)
        Bukkit.getPluginManager().registerEvents(ScoreboardManager(), this)
        Bukkit.getPluginManager().registerEvents(this, this)
    }

    private fun registerCommands() {
        this.logger.info("Registering commands...")

        val commandBending: PluginCommand = getCommand("bending")!!
        commandBending.setExecutor(BendingCommand())

        if (CommodoreProvider.isSupported()) {
            val commodore: Commodore = CommodoreProvider.getCommodore(this)

            val node: LiteralArgumentBuilder<*> = literal<Any?>("bending")
                .then(literal("help"))
                .then(literal<Any?>("bind")
                    .then(RequiredArgumentBuilder.argument("ability", StringArgumentType.string())))
                .then(literal("clear"))
                .then(literal("display"))
                .then(literal("elements"))
                .then(literal<Any?>("list")
                    .then(RequiredArgumentBuilder.argument("element", StringArgumentType.string())))

            commodore.register(commandBending, node)
        }
    }

    override fun onDisable() {

    }

    @EventHandler
    fun onServerLoad(event: ServerLoadEvent) {
        if (event.type != ServerLoadEvent.LoadType.STARTUP) return

        registerDefaultAbilityConfig()
    }
}