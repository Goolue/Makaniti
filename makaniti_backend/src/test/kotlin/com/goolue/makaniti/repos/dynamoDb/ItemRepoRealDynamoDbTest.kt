package com.goolue.makaniti.repos.dynamoDb

import com.goolue.makaniti.persisted.PersistedItem
import com.goolue.makaniti.persisted.PersistedItemType.SHIRT
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled("mocked version ItemRepoTest exists")
internal class ItemRepoRealDynamoDbTest {
  private val repo = ItemRepo(DynamoDbServiceProviderImpl.getFromConfig().blockingGet().getDynamoDbClient())

  @Test
  fun `save should return true on successful save`() {
    assertTrue(repo.save(PersistedItem("uid1")).blockingGet())
  }

  @Test
  fun `delete should return true on successful deletion`() {
    assertTrue(repo.save(PersistedItem("uid1")).blockingGet())
    assertTrue(repo.delete("uid1").blockingGet())
  }

  @Test
  fun `delete should return false on unsuccessful deletion`() {
    assertTrue(repo.delete("non-existing").blockingGet())
  }

  @Test
  fun `fetch should return the item when exists`() {
    val item = PersistedItem("uid1", 10, "someStore", "blue", SHIRT)
    try {
      assertTrue(repo.save(item).blockingGet())
      assertEquals(item, repo.fetchByUid(item.uid!!).blockingGet())
    } finally {
      assertTrue(repo.delete(item.uid!!).blockingGet())
    }
  }
}
