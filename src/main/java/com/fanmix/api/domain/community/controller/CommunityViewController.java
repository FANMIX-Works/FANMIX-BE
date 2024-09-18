package com.fanmix.api.domain.community.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fanmix.api.domain.community.repository.CommunityRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CommunityViewController {
	private final CommunityRepository communityRepository;
}
