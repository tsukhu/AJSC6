package ${package}.service;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.att.ajsc.common.utility.SystemPropertiesLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class HelloTest {

	@LocalServerPort
	private int port;
	@Value("${server.contextPath}")
	private String context;

	static{
		SystemPropertiesLoader.addSystemProperties(); 
	}
	
	@Autowired
	private Client client;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void getAllTest() {

		Response response = client.target("http://localhost:" + port).path(context + "/service/hello").request()
				.accept(MediaType.APPLICATION_JSON.toString()).get();

		assertEquals(200, response.getStatus());

		String actualResponseEntity = response.readEntity(String.class);
		String expectedResponseEntity = "Hello World!";
		assertEquals(expectedResponseEntity, actualResponseEntity);
	}
}