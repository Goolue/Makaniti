package com.goolue.makaniti.repos.dynamoDb

import com.goolue.makaniti.persisted.AbstractRepo
import com.goolue.makaniti.persisted.PersistedStore
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema

// TODO add tests with mocks
class StoreRepo(ddbClient: DynamoDbEnhancedAsyncClient) : AbstractRepo<PersistedStore>() {
  private val storeTable = ddbClient.table("makaniti_stores", TableSchema.fromBean(PersistedStore::class.java))

  override fun getTable(): DynamoDbAsyncTable<PersistedStore> = storeTable
}
