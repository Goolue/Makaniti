package com.goolue.makaniti.domain

enum class ItemType {
  SHIRT, PANTS, SKIRT, DRESS, BRA, SHOES
}

data class Item(val uid: String,
                val size: Int,
                val store: Store,
                val type: ItemType,
                val colour: String? = null)
