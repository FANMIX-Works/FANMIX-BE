package com.fanmix.api.domain.community.repository;

import java.util.Optional;

import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;

import com.fanmix.api.domain.community.entity.Category;

public interface CategoryRepository extends JpaAttributeConverter<Category, Integer> {
	Optional<Category> findByName(String name);
}
