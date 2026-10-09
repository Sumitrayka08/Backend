package com.agrinexus;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "JWT_SECRET=TestSecretKeyForMavenBuildsWithSufficientLength32Bytes!"
})
class AgrinexusBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
