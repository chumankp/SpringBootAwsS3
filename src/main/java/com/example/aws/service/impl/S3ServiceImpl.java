package com.example.aws.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.aws.controller.AwsS3Controller;
import com.example.aws.dto.FileDetailsDto;
import com.example.aws.service.S3Service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3ServiceImpl implements S3Service {

	private static final Logger logger = LoggerFactory.getLogger(AwsS3Controller.class);

	private S3Client s3Client;

	public S3ServiceImpl(S3Client s3Client) {
		this.s3Client = s3Client;
	}

	@Override
	public byte[] getFile(String bucketName, String fileName) {
		logger.info("Attempting to retrieve file: {}", fileName);
		try {
			GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(fileName).build();

			byte[] fileData = s3Client.getObjectAsBytes(getObjectRequest).asByteArray();
			logger.info("Successfully retrieved file: {}", fileName);
			return fileData;

		} catch (NoSuchKeyException e) {
			logger.error("File not found: {}", fileName, e);
			throw e;
		} catch (Exception e) {
			logger.error("Error retrieving file: {}", fileName, e);
			throw e;
		}
	}

	@Override
	public List<FileDetailsDto> listFiles(String bucketName) {
		logger.info("Attempting to list files in bucket: {}", bucketName);
		try {
			ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder().bucket(bucketName).build();
			ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);
			List<FileDetailsDto> fileDetails = listObjectsResponse.contents().stream().map(this::mapToFileDTO)
					.collect(Collectors.toList());
			logger.info("Successfully listed files in bucket: {}", bucketName);
			return fileDetails;
		} catch (Exception e) {
			logger.error("Error listing files in bucket: {}", bucketName, e);
			throw e;
		}
	}

	@Override
	public void uploadFile(String bucketName, String fileName, InputStream inputStream, long contentLength) {
		logger.info("Attempting to upload file: {}", fileName);
		try {
			PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(fileName).build();

			s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
			logger.info("Successfully uploaded file: {}", fileName);
		} catch (S3Exception e) {
			logger.error("Error uploading file: {}", fileName, e);
			throw new RuntimeException("Failed to upload file: " + fileName, e);
		}
	}

	private FileDetailsDto mapToFileDTO(S3Object s3Object) {
		return new FileDetailsDto(s3Object.key(), s3Object.size(), s3Object.lastModified().toString());
	}
}
