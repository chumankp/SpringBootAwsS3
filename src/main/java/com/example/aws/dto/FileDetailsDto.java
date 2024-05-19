package com.example.aws.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileDetailsDto {
	private String fileName;
    private long fileSize;
    private String lastModified;
}
