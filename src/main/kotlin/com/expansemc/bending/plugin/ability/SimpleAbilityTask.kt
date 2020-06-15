package com.expansemc.bending.plugin.ability

import com.expansemc.bending.api.ability.*
import com.expansemc.bending.api.collision.CollisionRegion
import com.expansemc.bending.api.collision.CollisionResult
import com.expansemc.bending.plugin.bender.PlayerBender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

data class SimpleAbilityTask(
    override val bender: PlayerBender,
    override val player: Player,
    override val ability: Ability,
    override val context: AbilityContext,
    override val executionType: AbilityExecutionType
) : AbilityTask {

    override var currentTask: BukkitTask? = null
        set(value) {
            field?.cancel()
            field = value
        }

    override val type: AbilityType get() = this.ability.type

    override var collisionRegion: CollisionRegion? = null

    override fun collide(): CollisionResult {
        TODO()
    }

    override val isDone: Boolean get() = this.currentTask == null

    override val isCancelled: Boolean get() = this.currentTask?.isCancelled == true

    override fun cancel() {
        val wasRunning = this.currentTask.let { it != null && !it.isCancelled }
        this.currentTask = null
        if (wasRunning) {
            this.ability.cleanup(this.context, this.executionType)
            this.bender.running.remove(this)
        }
    }
}