package com.expansemc.bending.plugin.registry

import com.expansemc.bending.api.registry.CatalogRegistry
import com.expansemc.bending.api.registry.CatalogType
import com.expansemc.bending.api.registry.NamedCatalogType
import com.google.common.collect.Table
import com.google.common.collect.Tables
import org.bukkit.NamespacedKey
import java.util.*
import kotlin.collections.HashMap

class SimpleCatalogRegistry : CatalogRegistry {

    @Suppress("RemoveExplicitTypeArguments")
    private val catalogTable: Table<Class<out CatalogType>, NamespacedKey, CatalogType> =
        Tables.newCustomTable<Class<out CatalogType>, NamespacedKey, CatalogType>(IdentityHashMap()) { HashMap() }

    @Suppress("UNCHECKED_CAST")
    override fun <T : CatalogType> get(type: Class<T>, key: NamespacedKey): T? = this.catalogTable[type, key] as T?

    @Suppress("UNCHECKED_CAST")
    override fun <T : NamedCatalogType> getByName(type: Class<T>, name: String, ignoreCase: Boolean): Collection<T> =
        this.getAllOf(type).asSequence()
            .filterIsInstance<NamedCatalogType>()
            .filter { it.name.equals(name, ignoreCase) }
            .toList() as Collection<T>

    @Suppress("UNCHECKED_CAST")
    override fun <T : CatalogType> getAllOf(type: Class<T>): Collection<T> = this.catalogTable.row(type).values as Collection<T>

    override fun <T : CatalogType> register(type: Class<T>, catalogType: T): CatalogRegistry {
        this.catalogTable.put(type, catalogType.key, catalogType)
        return this
    }
}