package com.expansemc.bending.plugin.util

import com.google.common.collect.Table
import com.google.common.collect.Tables
import java.util.*

fun <R, C, V> IdentityHashTable(): Table<R, C, V> =
    Tables.newCustomTable(IdentityHashMap<R, Map<C, V>>()) { IdentityHashMap<C, V>() }