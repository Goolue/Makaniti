package com.goolue.makaniti.repos.s3

import io.reactivex.rxjava3.core.Single
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.util.*
import java.util.stream.Collectors

internal class S3RepoRealS3Test {
  private val s3 = S3Repo(S3ClientProviderImpl.getFromConfig().blockingGet().getS3Client())

  @Test
  fun `isBucketExists should return true when bucket exists`() {
    assertTrue(s3.isBucketExists("dummy-testing-bucket-111").blockingGet())
  }

  @Test
  fun `isBucketExists should return false when bucket does not exists`() {
    assertFalse(s3.isBucketExists("I do not exist").blockingGet())
  }

  @Test
  fun `createBucket should return true on successful bucket creation`() {
    val bucketName = UUID.randomUUID().toString()
    try {
      assertFalse(s3.isBucketExists(bucketName).blockingGet())
      assertTrue(s3.createBucket(bucketName).blockingGet())
      assertTrue(s3.isBucketExists(bucketName).blockingGet())
    } finally {
      assertTrue(s3.deleteBucket(bucketName).blockingGet())
    }
  }

  @Test
  fun `createBucket should return false on illegal bucket name`() {
    val illegalBucketName =  "I am_illegal$$$"
    assertFalse(s3.isBucketExists(illegalBucketName).blockingGet())
    assertFalse(s3.createBucket(illegalBucketName).blockingGet())
    assertFalse(s3.isBucketExists(illegalBucketName).blockingGet())
  }

  @Test
  fun `createItemInBucket should return true on successful item creation`() {
    val randomBucket = createRandomBucket()
    if (randomBucket.isEmpty) fail("random bucket was not created")

    val bucketName = randomBucket.get()
    val item = UUID.randomUUID().toString()
    val itemContent = this::class.java.getResource("/s3Resources/someFile1.json").readBytes()

    try {
      assertTrue(s3.createItemInBucket(bucketName, item, itemContent).blockingGet())
    } finally {
      assertTrue(s3.deleteBucket(bucketName).blockingGet())
    }
  }

  @Test
  fun `listBucketItemNames should return all items in bucket`() {
    val randomBucket = createRandomBucket()
    if (randomBucket.isEmpty) fail("random bucket was not created")

    val bucketName = randomBucket.get()
    val itemContent = this::class.java.getResource("/s3Resources/someFile1.json").readBytes()
    val expected = (1..5).toList().map { UUID.randomUUID().toString() }.sorted()

    try {
      createItemsInBucket(expected, bucketName, itemContent)
      s3.listBucketItemNames(bucketName).blockingStream().sorted()
        .use { stream ->
          val actualItems = stream.collect(Collectors.toList())
          assertEquals(expected, actualItems)
        }
    } finally {
      assertTrue(s3.deleteBucket(bucketName).blockingGet())
    }
  }

  @Test
  fun `deleteItemInBucketBucket should return true on successful item deletion`() {
    val randomBucket = createRandomBucket()
    if (randomBucket.isEmpty) fail("random bucket was not created")
    val bucketName = randomBucket.get()

    try {
      val itemContent = this::class.java.getResource("/s3Resources/someFile1.json").readBytes()
      val item = UUID.randomUUID().toString()
      createItemsInBucket(listOf(item), bucketName, itemContent)
      assertTrue(s3.isBucketExists(bucketName).blockingGet())
      assertTrue(s3.deleteItemInBucketBucket(bucketName, item).blockingGet())
      assertTrue(s3.isBucketExists(bucketName).blockingGet())
    } finally {
      assertTrue(s3.deleteBucket(bucketName).blockingGet())
    }
  }

  @Test
  fun `deleteBucket should return true on successful bucket deletion`() {
    val randomBucket = createRandomBucket()
    if (randomBucket.isEmpty) fail("random bucket was not created")
    val bucketName = randomBucket.get()

    val itemContent = this::class.java.getResource("/s3Resources/someFile1.json").readBytes()
    val items = (1..5).toList().map { UUID.randomUUID().toString() }.sorted()
    createItemsInBucket(items, bucketName, itemContent)

    assertTrue(s3.deleteBucket(bucketName).blockingGet())
  }

  private fun createItemsInBucket(items: List<String>, bucketName: String, itemContent: ByteArray) {
    val createItems = items
      .map { s3.createItemInBucket(bucketName, it, itemContent) }

    return Single.merge(createItems)
      .onErrorComplete { fail(it) }
      .blockingSubscribe()
  }

  private fun createRandomBucket(): Optional<String> {
    val bucketName = UUID.randomUUID().toString()
    return if (s3.createBucket(bucketName).blockingGet()) Optional.of(bucketName) else Optional.empty()
  }

}
