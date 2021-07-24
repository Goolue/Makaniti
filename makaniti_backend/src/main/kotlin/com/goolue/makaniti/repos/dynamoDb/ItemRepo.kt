package com.goolue.makaniti.repos.dynamoDb

import com.goolue.makaniti.persisted.AbstractRepo
import com.goolue.makaniti.persisted.PersistedItem
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema


class ItemRepo(ddbClient: DynamoDbEnhancedAsyncClient) : AbstractRepo<PersistedItem>() {
  private val itemsTable = ddbClient.table("makaniti_items", TableSchema.fromBean(PersistedItem::class.java))

  override fun getTable(): DynamoDbAsyncTable<PersistedItem> = itemsTable
}
