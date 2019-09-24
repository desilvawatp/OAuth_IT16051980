package com.sliit.mooc;

import org.springframework.web.multipart.MultipartFile;

public class File {


	private MultipartFile multipartFile;

	public MultipartFile getMultipartFile() {
		return multipartFile;
	}

	public void setMultipartFile(MultipartFile multipartFile) {
		this.multipartFile = multipartFile;
	}
}
