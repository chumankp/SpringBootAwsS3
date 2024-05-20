package com.example.aws.service;

import java.io.InputStream;
import java.util.List;

import com.example.aws.dto.FileDetailsDto;

public interface S3Service {
	
	byte[] getFile(String bucketName, String fileName);
	
	List<FileDetailsDto> listFiles(String bucketName);
	
	void uploadFile(String bucketName,String fileName, InputStream inputStream, long contentLength);

}
