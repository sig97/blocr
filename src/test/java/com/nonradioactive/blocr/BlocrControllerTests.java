package com.nonradioactive.blocr;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.nonradioactive.blocr.web.BlocrController;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BlocrControllerTests {

	@Autowired
	private BlocrController blocrController;
	
	private MockMvc mockMvc;
	
	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders
                .standaloneSetup(blocrController)
                .build();
	}
	
	@Test
	public void synchronousCall() throws Exception {
		
		String type = "HS";
		String country = "SE";
		String expectedValue = "41";
		
		this.mockMvc.perform(MockMvcRequestBuilders.get("/painAndSuffering?type=" + type + "&country=" + country))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$").value(expectedValue));
	}
	
	
	@Test(timeout=1500)
	public void futureCall() throws Exception {
		
		String type = "HS";
		String country = "SE";
		String expectedValue = "82";
		
		this.mockMvc.perform(MockMvcRequestBuilders.get("/painAndSufferingFuture?type=" + type + "&country=" + country))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$").value(expectedValue));
	}
	
	
	@Test(timeout=1500)
	public void asyncCall() throws Exception {
		
		String type = "HS";
		String country = "SE";
		String expectedValue = "82";
		
		this.mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(
				mockMvc.perform(MockMvcRequestBuilders.get("/painAndSufferingAsync?type=" + type + "&country=" + country))
				.andExpect(MockMvcResultMatchers.request().asyncStarted())
				.andReturn()))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$").value(expectedValue));
	}
}
