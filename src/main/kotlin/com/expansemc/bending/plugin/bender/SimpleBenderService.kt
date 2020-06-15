package com.expansemc.bending.plugin.bender

import com.expansemc.bending.api.bender.Bender
import com.expansemc.bending.api.bender.BenderService
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class SimpleBenderService : BenderService {

    private val bendersById = ConcurrentHashMap<UUID, Bender>()

    override val benders: Collection<Bender>
        get() = this.bendersById.values

    override fun getBender(uniqueId: UUID): Bender? =
        this.bendersById[uniqueId]

    override fun getOrCreateBender(player: Player): Bender =
        this.bendersById.computeIfAbsent(player.uniqueId, ::PlayerBender)
}