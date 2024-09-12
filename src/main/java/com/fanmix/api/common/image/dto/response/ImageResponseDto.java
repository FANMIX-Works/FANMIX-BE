package com.fanmix.api.common.image.dto.response;

public record ImageResponseDto(
	String name,
	String description,
	String imageUrl
) {
	public static ImageResponseDto of(String name, String description, String imageUrl) {
		return new ImageResponseDto(name, description, imageUrl);
	}
}
