package com.ibm.mqclient.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ibm.mqclient.service.MQService;

public class MQClientControllerTest {
	
	MQClientController controller;
    MockMvc mockMvc;
    BeanFactory beanFactory;
    MQService mqServiceMock;
    
    @BeforeEach
    public void setup() {

        mqServiceMock = mock(MQService.class);
        beanFactory = mock(BeanFactory.class);
        controller = spy(new MQClientController(mqServiceMock));
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
    
    @Test
    @DisplayName("send hello world should return 200 status")
    public void when_send_hello_world_is_called_should_return_200_status() throws Exception {

    	String helloWorld = "Hello world";
    	when(mqServiceMock.sendHelloWorld()).thenReturn(helloWorld);
        mockMvc.perform(get("/api/send-hello-world"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", Matchers.equalTo(helloWorld)));              
    }
    
    @Test
    @DisplayName("receiving a message should return 200 status")
    public void when_receiving_a_message_should_return_200_status() throws Exception {

    	String mockReceiveMessage = "Hello world from mock";
    	when(mqServiceMock.receiveMessage()).thenReturn(mockReceiveMessage);
        mockMvc.perform(get("/api/recv"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", Matchers.equalTo(mockReceiveMessage)));              
    }
    
    
	@Test
    @DisplayName("send json body should return 200 status")
    public void send_json_string_should_return_200_status() throws Exception {

    	String jsonRequestBody = "{\"firstName\":\"gerry\",\"lastName\":\"kovan\"}";
    	when(mqServiceMock.sendJson(any())).thenReturn(jsonRequestBody);
   	
    	HttpHeaders httpHeaders = new HttpHeaders();
    	httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    	
        mockMvc.perform(post("/api/send-json").headers(httpHeaders).content(jsonRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", Matchers.equalTo("OK")))
                .andExpect(jsonPath("$.data", Matchers.equalTo(jsonRequestBody)));            
    }

}
