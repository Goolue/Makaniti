package com.goolue.makaniti.persisted

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey

@DynamoDbBean
data class PersistedStore(@get:DynamoDbPartitionKey var uid: String? = null, var name: String? = null)
