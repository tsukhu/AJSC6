package ${package}.service;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.att.ajsc.common.utility.SystemPropertiesLoader;

import ${package}.Application;
import ${package}.model.HelloWorld;
import ${package}.service.rs.RestService;
import com.att.ajsc.common.utility.SystemPropertiesLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
@WebAppConfiguration
@SpringBootTest
public class HelloTest {
	
	static{
		SystemPropertiesLoader.addSystemProperties(); 
	}

	@Autowired
	RestService service;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testQuickHello() throws Exception {
		HelloWorld helloWorld = service.getQuickHello("test");
		assertEquals("Hello test!", helloWorld.getMessage());
	}
}
