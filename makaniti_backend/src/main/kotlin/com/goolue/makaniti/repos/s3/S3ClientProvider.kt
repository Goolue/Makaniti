package com.goolue.makaniti.repos.s3

import software.amazon.awssdk.services.s3.S3AsyncClient

interface S3ClientProvider {
  fun getS3Client(): S3AsyncClient
}
