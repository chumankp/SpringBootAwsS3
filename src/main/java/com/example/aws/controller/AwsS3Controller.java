package com.example.aws.controller;

import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.example.aws.dto.FileDetailsDto;
import com.example.aws.service.impl.S3ServiceImpl;

import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@RestController
@RequestMapping("/files")
public class AwsS3Controller {

	private static final Logger logger = LoggerFactory.getLogger(AwsS3Controller.class);

	@Value("${aws.s3.bucket}")
	private String bucketName;

	private S3ServiceImpl s3Service;

	public AwsS3Controller(S3ServiceImpl s3Service) {
		this.s3Service = s3Service;
	}

	@GetMapping("/{fileName}")
	public ResponseEntity<?> getFile(@PathVariable String fileName) {
		try {
			byte[] fileStream = s3Service.getFile(bucketName, fileName);
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
			headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
			return new ResponseEntity<>(fileStream, headers, HttpStatus.OK);
		} catch (NoSuchKeyException e) {
			logger.error("File not found: {}", fileName, e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found: " + fileName);
		} catch (Exception e) {
			logger.error("Error retrieving file: {}", fileName, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving file: " + fileName);
		}
	}

	@GetMapping("/details")
	public List<FileDetailsDto> listFiles() {
		logger.info("Received request to list files in bucket: {}", bucketName);
		List<FileDetailsDto> filesDetails = s3Service.listFiles(bucketName);
		logger.info("Returning {} files from bucket {}", filesDetails.size(), bucketName);
		return filesDetails;
	}

	@PostMapping
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
		try (InputStream inputStream = file.getInputStream()) {
			s3Service.uploadFile(bucketName, file.getOriginalFilename(), inputStream, file.getSize());
			return ResponseEntity.status(HttpStatus.CREATED).body("File uploaded successfully");
		} catch (Exception e) {
			logger.error("Error uploading file: {}", file.getOriginalFilename(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file");
		}
	}

}
