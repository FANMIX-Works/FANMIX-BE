package com.fanmix.api.common.image.service;

import static com.fanmix.api.common.image.exception.ImageErrorCode.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fanmix.api.common.image.dto.response.ImageResponseDto;
import com.fanmix.api.common.image.exception.ImageException;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Operations;
import io.awspring.cloud.s3.S3Resource;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.exception.SdkClientException;

@Service
@Slf4j
public class ImageService {

	private final List<String> allowExtensionList = Arrays.asList(".jpg", ".jpeg", ".png");
	private final List<String> allowMediaTypeList = Arrays.asList("image/jpg", MediaType.IMAGE_JPEG_VALUE,
		MediaType.IMAGE_PNG_VALUE);

	private final S3Operations s3Operations;
	private final String bucketName;
	private final String cloudfrontUrl;

	public ImageService(
		S3Operations s3Operations,
		@Value("${spring.cloud.aws.s3.bucket}") String bucketName,
		@Value("${spring.cloud.aws.cloudfront.url}") String cloudfrontUrl) {
		this.s3Operations = s3Operations;
		this.bucketName = bucketName;
		this.cloudfrontUrl = cloudfrontUrl;
	}

	public String saveImageAndReturnUrl(MultipartFile imageFile) {
		Optional.ofNullable(imageFile)
			.orElseThrow(() -> new ImageException(EMPTY_IMAGE_FILE));

		String originName = imageFile.getOriginalFilename();
		if (!isImageFile(imageFile)) {
			throw new ImageException(INVALID_EXTENSION_IMAGE_FILE);
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd'T'HH:mm:ss");
		String timestamp = LocalDateTime.now().format(formatter);
		String uploadName = timestamp + "-" + originName;
		S3Resource resource = uploadImageToS3(imageFile, uploadName);

		return cloudfrontUrl + resource.getFilename();
		// 현재는 db에 저장을 하지 않아 반환하고 return 값을 사용하진 않지만 나중에 로직에서 사용할 수 있음
	}

	private boolean isImageFile(MultipartFile imageFile) {
		String originName = imageFile.getOriginalFilename();
		int extensionIndex = originName.lastIndexOf(".");

		if (extensionIndex == -1) {
			log.error("[ImageService validateImageFileExtension] image extension not found. imageName: {}", originName);
			throw new ImageException(NO_EXTENSION_IMAGE_FILE);
		}

		String ext = originName.substring(extensionIndex).toLowerCase();
		String contentType = imageFile.getContentType().toLowerCase();

		if (!allowExtensionList.contains(ext) || !allowMediaTypeList.contains(contentType)) {
			log.error(
				"[ImageService isImageFile] image extension or content type is invalid. "
					+ "imageName: {}, ext: {}, contentType: {}", originName, ext, contentType);
			return false;
		}
		return true;
	}

	private S3Resource uploadImageToS3(MultipartFile image, String uploadName) {
		try {
			ObjectMetadata objectMetadata = ObjectMetadata.builder()
				.contentType(image.getContentType())
				.contentLength(image.getSize())
				.contentDisposition("inline")
				.build();
			return s3Operations.upload(bucketName, uploadName, image.getInputStream(), objectMetadata);
		} catch (IOException | SdkClientException e) {
			throw new ImageException(FAILED_UPLOADING_IMAGE_FILE);
		}
	}

	/*
	 * 파일 이름 받아서 정보 가져옴
	 * 얘도 쓸일은 없지만 예시용 메소드
	 * 나중에 삭제될 수 있음
	 */
	public ImageResponseDto getImageInfo(String fileName) {
		S3Resource resource = s3Operations.download(bucketName, fileName);
		String filename = resource.getFilename();
		return ImageResponseDto.of(fileName, resource.getDescription(), cloudfrontUrl + filename);
	}
}
