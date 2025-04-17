package com.lukaoniani.ratingsystems;

import org.springframework.boot.SpringApplication;

public class TestRatingSystemsApplication {

	public static void main(String[] args) {
		SpringApplication.from(RatingSystemsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
