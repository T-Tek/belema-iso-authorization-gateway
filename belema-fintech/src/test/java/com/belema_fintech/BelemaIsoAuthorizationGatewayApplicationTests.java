package com.belema_fintech;

import com.belema_fintech.testConfig.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestConfig.class)
class BelemaIsoAuthorizationGatewayApplicationTests {

	@Test
	void contextLoads() {
	}

}
