package com.expansemc.bending.plugin.registry

import com.expansemc.bending.api.registry.FactoryRegistry
import java.util.*
import kotlin.NoSuchElementException

class SimpleFactoryRegistry : FactoryRegistry {

    private val factoryMap = IdentityHashMap<Class<out Any>, () -> Any>()

    override fun <T : Any> register(type: Class<T>, supplier: () -> T): FactoryRegistry {
        this.factoryMap[type] = supplier
        return this
    }

    override fun <T : Any> create(type: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return (this.factoryMap[type] ?: throw NoSuchElementException(type.name)).invoke() as T
    }
}