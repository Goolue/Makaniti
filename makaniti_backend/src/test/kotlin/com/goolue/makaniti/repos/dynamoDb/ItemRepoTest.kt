package com.goolue.makaniti.repos.dynamoDb

import com.goolue.makaniti.persisted.PersistedItem
import com.goolue.makaniti.persisted.PersistedItemType.SKIRT
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.anyString
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import java.util.concurrent.CompletableFuture

internal class ItemRepoTest {
  companion object {
    private val table = mock<DynamoDbAsyncTable<PersistedItem>>()
    private val ddbClient = mock<DynamoDbEnhancedAsyncClient>() {
      on { table(anyString(), any<TableSchema<PersistedItem>>()) } doReturn table
    }

    val itemRepo = ItemRepo(ddbClient)
  }

  @Test
  fun `save should return true on successful save`() {
    val item = PersistedItem("uid1", 10, "storeUid", "blue", SKIRT)
    whenever(table.putItem(eq(item))).thenReturn(CompletableFuture.completedFuture(null))

    assertTrue(itemRepo.save(item).blockingGet())
  }

  @Test
  fun `save should return false on unsuccessful save`() {
    val item = PersistedItem("uid1", 10, "storeUid", "blue", SKIRT)
    whenever(table.putItem(eq(item))).thenReturn(CompletableFuture.failedFuture(NullPointerException()))

    assertFalse(itemRepo.save(item).blockingGet())
  }

  @Test
  fun `delete should return true on successful deletion`() {
    val uid = "someUid"
    val key = Key.builder().partitionValue(uid).build()
    whenever(table.deleteItem(eq(key))).thenReturn(CompletableFuture.completedFuture(null))

    assertTrue(itemRepo.delete(uid).blockingGet())
  }

  @Test
  fun `delete should return false on unsuccessful deletion`() {
    val uid = "someUid"
    val key = Key.builder().partitionValue(uid).build()
    whenever(table.deleteItem(eq(key))).thenReturn(CompletableFuture.failedFuture(NullPointerException()))

    assertFalse(itemRepo.delete(uid).blockingGet())
  }

  @Test
  fun `fetch should return the item when exists`() {
    val uid = "someUid"
    val item = PersistedItem(uid, 10, "storeUid", "blue", SKIRT)
    val key = Key.builder().partitionValue(uid).build()
    whenever(table.getItem(eq(key))).thenReturn(CompletableFuture.completedFuture(item))

    assertEquals(item, itemRepo.fetchByUid(uid).blockingGet())
  }

  @Test
  fun `fetch should return an empty Maybe when not exists`() {
    val uid = "someUid"
    val key = Key.builder().partitionValue(uid).build()
    whenever(table.getItem(eq(key))).thenReturn(CompletableFuture.completedFuture(null))

    assertTrue(itemRepo.fetchByUid(uid).isEmpty.blockingGet())
  }
}
