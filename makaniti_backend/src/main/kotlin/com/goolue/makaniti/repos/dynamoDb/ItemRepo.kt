package com.goolue.makaniti.repos.dynamoDb

import com.goolue.makaniti.persisted.PersistedItem
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import utils.logger


class ItemRepo(ddbClient: DynamoDbEnhancedAsyncClient) {
  private val table = ddbClient.table("makaniti_items", TableSchema.fromBean(PersistedItem::class.java))

  fun saveItem(item: PersistedItem): Single<Boolean> =
    Maybe.fromFuture(table.putItem(item))
      .map { true }
      .defaultIfEmpty(true)
      .onErrorReturn {
        false
      }
      .doOnError {
        logger().warn("error saving item $item into db. error: $it")
      }
      .doOnSuccess {
        logger().debug("successfully saved item $item")
      }

  fun deleteItem(uid: String): Single<Boolean> {
    val key = Key.builder().partitionValue(uid).build()
    return Maybe.fromFuture(table.deleteItem(key))
      .map { true }
      .defaultIfEmpty(true)
      .onErrorReturn {
        false
      }
      .doOnError {
        logger().warn("error deleting item $uid into db. error: $it")
      }
      .doOnSuccess {
        logger().debug("successfully deleted item $uid")
      }
  }

  fun fetchItemByUid(uid: String): Maybe<PersistedItem> {
    val key = Key.builder().partitionValue(uid).build()
    return Maybe.fromFuture(table.getItem(key))
      .doOnError {
        logger().warn("error fetching item $uid. error: $it")
      }
  }
}
