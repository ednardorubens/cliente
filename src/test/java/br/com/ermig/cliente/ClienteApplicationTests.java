package br.com.ermig.cliente;

import org.junit.jupiter.api.Test;

class ClienteApplicationTests {

	@Test
	void contextLoads() {
		ClienteApplication.main(new String[] {
			"--spring.profiles.active=test",
			"--spring.main.web-environment=false",
			"--spring.cloud.discovery.enabled=false",
			"--logging.level.root=OFF",
			"--logging.level.org.hibernate.type=OFF"
        });
	}

}
