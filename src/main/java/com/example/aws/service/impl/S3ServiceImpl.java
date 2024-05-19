package com.example.aws.service.impl;


import org.springframework.stereotype.Service;

import com.example.aws.dto.FileDetailsDto;
import com.example.aws.service.S3Service;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3ServiceImpl implements S3Service{

    private S3Client s3Client;

    public S3ServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }
    
    @Override
    public byte[] getFile(String bucketName,String fileName){
    	try {
    	GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
    	
    	ResponseBytes<GetObjectResponse> s3ObjectResponseInputStream = s3Client.getObject(getObjectRequest, ResponseTransformer.toBytes());

        return s3ObjectResponseInputStream.asByteArray();
    	
    	}
    	catch (S3Exception e) {
            throw new RuntimeException("File not found: " + fileName, e);
        }
      }
    
    @Override
    public List<FileDetailsDto> listFiles(String bucketName) {
        ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);
        return listObjectsResponse.contents().stream()
        		.map(this::mapToFileDTO)
        		.collect(Collectors.toList());
    }
    
    @Override
    public void uploadFile(String bucketName,String fileName, InputStream inputStream, long contentLength) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to upload file: " + fileName, e);
        }
    }
    
    
    private FileDetailsDto mapToFileDTO(S3Object s3Object) {
        return new FileDetailsDto(
                s3Object.key(),
                s3Object.size(),
                s3Object.lastModified().toString()
        );
    }
}
