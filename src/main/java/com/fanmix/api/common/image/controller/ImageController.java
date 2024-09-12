package com.fanmix.api.common.image.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fanmix.api.common.image.dto.response.ImageResponseDto;
import com.fanmix.api.common.image.service.ImageService;
import com.fanmix.api.common.response.Response;

import lombok.RequiredArgsConstructor;

/**
 * 이미지 컨트롤러
 * 원래는 컨트롤러 없이 서비스내에서 호출될 예정이지만
 * 프로젝트 시작 전 예시용 컨트롤러로 생성
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {
	private final ImageService imageService;

	@PostMapping
	public ResponseEntity<Response<Void>> saveImage(@RequestPart MultipartFile file) {
		imageService.saveImageAndReturnUrl(file);
		return ResponseEntity.ok(Response.success());
	}

	/**
	 * 파일 이름으로 이미지 정보를 가져온다.
	 * 지금은 DB랑 연결을 하지 않았기 때문에 예시로 넣어둔 maria.png 를 쿼리 스트링 안에 넣어주면 됨
	 * @return 이미지 정보
	 */
	@GetMapping
	public ResponseEntity<Response<ImageResponseDto>> getImageInfo(@RequestParam String fileName) {
		return ResponseEntity.ok(Response.success(imageService.getImageInfo(fileName)));
	}

}
