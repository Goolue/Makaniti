package com.goolue.makaniti.repos.dynamoDb

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient

interface DynamoDbServiceProvider {
  fun getDynamoDbClient(): DynamoDbEnhancedAsyncClient
}
