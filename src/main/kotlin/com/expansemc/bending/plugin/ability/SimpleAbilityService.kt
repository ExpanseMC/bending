package com.expansemc.bending.plugin.ability

import com.expansemc.bending.api.ability.AbilityService

class SimpleAbilityService : AbilityService {
    override val tickDelay: Long get() = 1
}