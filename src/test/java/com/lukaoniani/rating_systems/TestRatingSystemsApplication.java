package com.lukaoniani.rating_systems;

import org.springframework.boot.SpringApplication;

public class TestRatingSystemsApplication {

	public static void main(String[] args) {
		SpringApplication.from(RatingSystemsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
