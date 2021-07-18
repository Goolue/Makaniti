package com.goolue.makaniti.repos.dynamoDb

import com.goolue.makaniti.persisted.PersistedItem
import com.goolue.makaniti.persisted.PersistedItemType.SHIRT
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class ItemRepoRealDynamoDbTest {
  private val repo = ItemRepo(DynamoDbServiceProviderImpl.getFromConfig().blockingGet().getDynamoDbClient())

  @Test
  fun `saveItem should return true on successful save`() {
    assertTrue(repo.saveItem(PersistedItem("uid1")).blockingGet())
  }

  @Test
  fun `deleteItem should return true on successful deletion`() {
    assertTrue(repo.saveItem(PersistedItem("uid1")).blockingGet())
    assertTrue(repo.deleteItem("uid1").blockingGet())
  }

  @Test
  fun `deleteItem should return false on unsuccessful deletion`() {
    assertTrue(repo.deleteItem("non-existing").blockingGet())
  }

  @Test
  fun `fetchItem should return the item when exists`() {
    val item = PersistedItem("uid1", 10, "someStore", "blue", SHIRT)
    try {
      assertTrue(repo.saveItem(item).blockingGet())
      assertEquals(item, repo.fetchItemByUid(item.uid!!).blockingGet())
    } finally {
      assertTrue(repo.deleteItem(item.uid!!).blockingGet())
    }
  }
}
