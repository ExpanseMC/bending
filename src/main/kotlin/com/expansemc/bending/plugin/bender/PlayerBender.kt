package com.expansemc.bending.plugin.bender

import com.expansemc.bending.api.ability.*
import com.expansemc.bending.api.bender.Bender
import com.expansemc.bending.api.element.Element
import com.expansemc.bending.api.util.HotbarList
import com.expansemc.bending.plugin.Bending
import com.expansemc.bending.plugin.ability.SimpleAbilityTask
import com.expansemc.bending.plugin.util.IdentityHashTable
import com.google.common.collect.Sets
import com.google.common.collect.Table
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CancellationException
import java.util.concurrent.CompletableFuture
import java.util.logging.Level

data class PlayerBender(override val uniqueId: UUID) : Bender {

    private val passivesMap = IdentityHashMap<AbilityType, Ability>()

    private val waiting: Table<AbilityType, AbilityExecutionType, CompletableFuture<Void?>> = IdentityHashTable()

    private val cooldowns = IdentityHashMap<AbilityType, Long>()

    override val player: Player?
        get() = Bukkit.getPlayer(this.uniqueId)

    override val equipped = HotbarList<Ability>()

    override var selected: Ability?
        get() = this.player?.let { this.equipped[it.inventory.heldItemSlot] }
        set(value) {
            (this.player ?: throw RuntimeException("Player ($uniqueId) isn't online"))
                .let { this.equipped[it.inventory.heldItemSlot] = value }
        }

    override val passives: Collection<Ability>
        get() = this.passivesMap.values

    override fun getPassive(type: AbilityType): Ability? =
        this.passivesMap[type]

    override fun addPassive(ability: Ability): Boolean {
        this.passivesMap[ability.type] = ability
        return true
    }

    override fun removePassive(ability: Ability): Boolean =
        this.passivesMap.remove(ability.type, ability)

    override val running: MutableSet<SimpleAbilityTask> = Sets.newIdentityHashSet()

    override fun execute(ability: Ability, executionType: AbilityExecutionType): AbilityTask? {
        val player: Player = Bukkit.getPlayer(this.uniqueId) ?: return null

        if (executionType !in ability.type.executionTypes) {
            // This ability is executed differently.
            return null
        }

        val cont: CompletableFuture<Void?>? = this.waiting.remove(ability.type, executionType)
        if (cont != null) {
            // Found a waiting ability. Complete it instead.
            cont.complete(null)
            return null
        }

        if (this.hasCooldown(ability.type)) {
            // This ability is on cooldown.
            return null
        }

        val context = AbilityContext.empty()

        // Set the fall distance if the ability was executed from falling.
        if (executionType == AbilityExecutionTypes.FALL) {
            context[AbilityContextKeys.FALL_DISTANCE] = player.fallDistance
        }

        // Pre-load default values into the context.
        context[AbilityContextKeys.EXECUTION_TYPE] = executionType
        context[AbilityContextKeys.PLAYER] = player
        context[AbilityContextKeys.BENDER] = this

        try {
            // Check if the ability's own validation routine succeeds.
            if (!ability.validate(context, executionType)) {
                // Failed to validate.
                return null
            }
        } catch (e: Exception) {
            player.sendMessage(
                "${ChatColor.DARK_RED}An exception occurred while validating ${ability.type.name}. " +
                        "Please report this to the administrators."
            )
            return null
        }

        // TODO: post AbilityExecuteEvent

        // Set the cooldown to prevent ability spamming (if it's set).
        if (ability.cooldown > 0) {
            this.setCooldown(ability.type, ability.cooldown)
        }

        val task = SimpleAbilityTask(this, player, ability, context, executionType)
        try {
            // Execute the ability.
            ability.execute(context, executionType, task)
        } catch (e: Exception) {
            // Cancel if an exception was thrown.
            task.cancel()
            player.sendMessage(
                "${ChatColor.DARK_RED}An exception occurred while executing ${ability.type.name}. " +
                        "Please report this to the administrators."
            )
            return null
        }

        if (task.isDone) {
            // No task was scheduled for delayed execution. Finish now.
            return null
        }

        this.running += task
        return task
    }

    override fun waitForExecution(type: AbilityType, executionType: AbilityExecutionType): CompletableFuture<Void?> {
        val future = CompletableFuture<Void?>().whenComplete { _, t: Throwable? ->
            this.waiting.remove(type, executionType)

            if (t != null && t !is CancellationException) {
                Bending.PLUGIN.logger.log(Level.SEVERE, "Ability failed to execute", t)
            }
        }

        this.waiting[type, executionType]?.cancel(false)
        this.waiting.put(type, executionType, future)

        return future
    }

    override fun cancel(type: AbilityType): Boolean {
        var found = false
        val iter: MutableIterator<SimpleAbilityTask> = this.running.iterator()
        while (iter.hasNext()) {
            val task = iter.next()
            if (type === task.type) {
                iter.remove()
                task.cancel()
                found = true
            }
        }
        return found
    }

    override fun hasCooldown(type: AbilityType): Boolean {
        val cooldown: Long = this.cooldowns[type] ?: return false
        val current: Long = System.currentTimeMillis()

        if (cooldown > current) {
            // Cooldown is still active.
            return true
        } else {
            // Cooldown has passed.
            this.cooldowns.remove(type)
            return false
        }
    }

    override fun setCooldown(type: AbilityType, milliseconds: Long): Boolean {
        // TODO: post event?
        this.cooldowns[type] = System.currentTimeMillis() + milliseconds
        return true
    }

    override fun removeCooldown(type: AbilityType): Long {
        return this.cooldowns.remove(type) ?: 0
    }

    override val elements: MutableSet<Element> = Sets.newIdentityHashSet()

    override fun addElement(element: Element): Boolean = this.elements.add(element)

    override fun removeElement(element: Element): Boolean = this.elements.remove(element)
}