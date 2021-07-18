package com.goolue.makaniti.persisted

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey

enum class PersistedItemType {
  SHIRT, PANTS, SKIRT, DRESS, BRA, SHOES
}

@DynamoDbBean
data class PersistedItem(@get:DynamoDbPartitionKey var uid: String? = null,
                         var size: Int? = null,
                         var storeUid: String? = null,
                         var colour: String? = null,
                         var type: PersistedItemType? = null)
