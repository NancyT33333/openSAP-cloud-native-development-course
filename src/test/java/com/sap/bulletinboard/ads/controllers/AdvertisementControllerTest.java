package com.sap.bulletinboard.ads.controllers;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.bulletinboard.ads.config.WebAppContextConfig;
import com.sap.bulletinboard.ads.models.Advertisement;

import junit.framework.Assert;
import net.minidev.json.JSONObject;
import com.sap.bulletinboard.ads.models.Advertisement;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { WebAppContextConfig.class })
@WebAppConfiguration
//@formatter:off
public class AdvertisementControllerTest {
    
    private static final String LOCATION = "Location";
    private static final String SOME_TITLE = "MyNewAdvertisement";

    @Inject
    WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void create() throws Exception {
        mockMvc.perform(buildPostRequest(SOME_TITLE))
                .andExpect(status().isCreated())
                .andExpect(header().string(LOCATION, is(not(""))))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.title", is(SOME_TITLE))); // requires com.jayway.jsonpath:json-path
    }

    @Test
    public void readAll() throws Exception {
        mockMvc.perform(buildPostRequest(SOME_TITLE));
        mockMvc.perform(buildGetRequest())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8));     
      
    }
    
    //creating a new advertisement with title set to null or  ""
    @Test
    public void emptyTitle() throws Exception {
        mockMvc.perform(buildPostRequest(""))
            .andExpect(status().isBadRequest());

        mockMvc.perform(buildPostRequest(null))
            .andExpect(status().isBadRequest());
      
      
    }
    
//     tests put call to unspecific address "/api/v1/ads/"
    @Test
    public void putToUnspecifiedPath() throws Exception {
       
        mockMvc.perform(buildPutRequest())
        .andExpect(status().isMethodNotAllowed());      
        
    }
    //  tests put to specific existing item path (item is updated)
    // updated item's title equals to new one
    @Test
    public void putToExistingPath() throws Exception {       
        JSONObject json = new JSONObject();
        json.put("title", "value1");
        
        MvcResult postres = mockMvc.perform(post(AdvertisementController.PATH).content(toJson(json)).contentType(APPLICATION_JSON_UTF8)).andReturn();
      
        String newEntryLocation = postres.getResponse().getHeader("Location");
        newEntryLocation = getIdFromLocation(newEntryLocation);
        Advertisement advertisement = new Advertisement();
        advertisement.setTitle("Changed Title");
        MockHttpServletResponse postres2 = mockMvc.perform(buildPutByIdRequest(newEntryLocation, advertisement))
                    .andExpect(status().isOk())
                    .andReturn().getResponse();
        
        assertThat(new ObjectMapper().readValue(postres2.getContentAsString(), Advertisement.class).getTitle(), equalTo(advertisement.getTitle() ));
    }
    // tests mass delete
  @Test
  public void deleteAll() throws Exception {
    JSONObject json = new JSONObject();
    json.put("title", "value1");
    
        mockMvc.perform(post(AdvertisementController.PATH).content(toJson(json))
                                    .contentType(APPLICATION_JSON_UTF8))
                                    .andExpect(status().is2xxSuccessful());
    
    mockMvc.perform(buildDelete())
    .andExpect(status().is2xxSuccessful());
   mockMvc.perform(buildGetRequest( ))
      .andExpect(jsonPath("$.length()", is(0)));      
      
  }
  //  tests delete of specific item
  @Test
  public void deleteById() throws Exception {
     mockMvc.perform(buildPostRequest("1"));
     mockMvc.perform(buildGetByIdRequest("1"))
     .andExpect(status().is2xxSuccessful());
      mockMvc.perform(buildDeleteByIdRequest("1"))
          .andExpect(status().is2xxSuccessful());   
      mockMvc.perform(buildGetByIdRequest("1"))
          .andExpect(status().isNotFound());
      
  }
  
  @Test
  public void getByNegativeId() throws Exception {
      mockMvc.perform(buildGetByIdRequest("-1"))
      .andExpect(status().isBadRequest());
  }
  
  //  tests delete of non existing specific item
  @Test
  public void deleteByIdNonExisting() throws Exception {
     
    
      mockMvc.perform(buildDeleteByIdRequest("1000"))
          .andExpect(status().isNotFound());   
     
      
  }

    //  tries to retrieve object with nonexisting ID using GET request to /4711
    @Test
    public void readByIdNotFound() throws Exception {
       
        mockMvc.perform(buildGetByIdRequest( "4711"))
        .andExpect(status().isNotFound());      
        
    }

//  creates new advertisement using POST, then retrieve it using GET {/id}
    @Test
    public void readById() throws Exception {
        JSONObject json = new JSONObject();
        json.put("title", "value1");
        
        MvcResult postres = mockMvc.perform(post(AdvertisementController.PATH).content(toJson(json)).contentType(APPLICATION_JSON_UTF8)).andReturn();
      
        String newEntryLocation = postres.getResponse().getHeader("Location");
        newEntryLocation = getIdFromLocation(newEntryLocation);
        
        mockMvc.perform(get(AdvertisementController.PATH + "/" + newEntryLocation)).andExpect(status().is2xxSuccessful());      

    }
    
//  creates new advertisement using POST, then check, that Location header points to a valid URL (starting with http://)
    @Test
    public void checkValidLocation() throws Exception {
        JSONObject json = new JSONObject();
        json.put("title", "value1");
        
        MvcResult postres = mockMvc.perform(post(AdvertisementController.PATH).content(toJson(json)).contentType(APPLICATION_JSON_UTF8)).andReturn();
      
        String newEntryLocation = postres.getResponse().getHeader("Location");
//        newEntryLocation = getIdFromLocation(newEntryLocation);
        if (newEntryLocation.lastIndexOf("http://") < 0) {
            fail("not absolute Location Header");
        }
        
        mockMvc.perform(get(newEntryLocation)).andExpect(status().is2xxSuccessful());      
    }
    
    private MockHttpServletRequestBuilder buildPostRequest(String adsTitle) throws Exception {
        Advertisement advertisement = new Advertisement();
        advertisement.setTitle(adsTitle);

        // post the advertisement as a JSON entity in the request body
        return post(AdvertisementController.PATH).content(toJson(advertisement)).contentType(APPLICATION_JSON_UTF8);
    }
    
    private MockHttpServletRequestBuilder buildGetRequest() throws Exception {
        return get(AdvertisementController.PATH).contentType(APPLICATION_JSON_UTF8);
    }
    
    private MockHttpServletRequestBuilder buildPutRequest() throws Exception {
        return put(AdvertisementController.PATH).content("{}").contentType(APPLICATION_JSON_UTF8);
    }
    
    private MockHttpServletRequestBuilder buildPutByIdRequest(String id, Advertisement ad) throws Exception {      
        return put(AdvertisementController.PATH + "/" + id).content(toJson(ad)).contentType(APPLICATION_JSON_UTF8);
    }
    
    private MockHttpServletRequestBuilder buildDeleteByIdRequest(String id) throws Exception {      
        return delete(AdvertisementController.PATH + "/" + id);
    }
    private MockHttpServletRequestBuilder buildDelete() throws Exception {      
        return delete(AdvertisementController.PATH );
    }
    
    private MockHttpServletRequestBuilder buildGetByIdRequest(String sId) throws Exception {
        return get(AdvertisementController.PATH + "/" + sId ).contentType(APPLICATION_JSON_UTF8);
    }
    
    private String toJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    private String getIdFromLocation(String location) {
        return location.substring(location.lastIndexOf('/') + 1);
    }

    private <T> T convertJsonContent(MockHttpServletResponse response, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String contentString = response.getContentAsString();
        return objectMapper.readValue(contentString, clazz);
    }
}