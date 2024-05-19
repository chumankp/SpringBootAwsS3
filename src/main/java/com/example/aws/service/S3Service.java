package com.example.aws.service;

import java.io.InputStream;
import java.util.List;

import com.example.aws.dto.FileDetailsDto;

import software.amazon.awssdk.services.s3.model.S3Object;

public interface S3Service {
	
	byte[] getFile(String bucketName, String fileName);
	
	List<FileDetailsDto> listFiles(String bucketName);
	
	void uploadFile(String bucketName,String fileName, InputStream inputStream, long contentLength);

}
