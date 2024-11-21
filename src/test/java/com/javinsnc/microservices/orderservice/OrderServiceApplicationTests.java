package com.javinsnc.microservices.orderservice;

import com.javinsnc.microservices.orderservice.stubs.InventoryClientStub;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.MySQLContainer;

import static org.hamcrest.MatcherAssert.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class OrderServiceApplicationTests {

	@ServiceConnection
	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.3.0");

	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	static {
		mySQLContainer.start();
	}

	@Test
	void shouldSubmitOrder() {
		String requestBody = """
				{
				    "id": 1,
				    "name": "bla",
				    "orderNumber": 5,
				    "skuCode": "iphone_15",
				    "price": 10,
				    "quantity": 1
				}
				""";

		InventoryClientStub.stubInventoryCall("iphone_15", 10);

		final var responseBodyString = RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.post("/api/order")
				.then()
				.statusCode(HttpStatus.CREATED.value())
				.extract().body().asString();

		assertThat(responseBodyString, Matchers.is("Order placed successfully"));
	}

}
