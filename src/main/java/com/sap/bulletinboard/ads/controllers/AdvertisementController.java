package com.sap.bulletinboard.ads.controllers;

import org.springframework.http.MediaType; //provides constants for content types
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.context.annotation.RequestScope;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus; //enumeration for HTTP status codes
import com.sap.bulletinboard.ads.models.Advertisement;




@RestController
@RequestScope
@RequestMapping(AdvertisementController.PATH) 
public class AdvertisementController {
    public static final String PATH = "/api/v1/ads";
    private final Map<Long, Advertisement> ads = new HashMap<>(); //temporary data storage, key represents the ID
    
    @GetMapping
    public Iterable<Advertisement> advertisements() {
        return this.ads.values(); 
        
    }

    @GetMapping("/{id}")
    public Advertisement advertisementById(@PathVariable("id") Long id) {
        if (!this.ads.containsKey(id) ) {
            throw new NotFoundException("No ad with this id" );
        }
        return this.ads.get(id); 
    }

    /**
     * @RequestBody is bound to the method argument. HttpMessageConverter resolves method argument 
     * depending on the content type.
     */
    @PostMapping
    public ResponseEntity<Advertisement> add(@RequestBody Advertisement advertisement,
            UriComponentsBuilder uriComponentsBuilder) throws URISyntaxException{
        long lng = this.ads.size() + 1;
        UriComponents uriComponents = uriComponentsBuilder.path(PATH + "/{id}").buildAndExpand(lng);
        this.ads.put(lng, advertisement);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(uriComponents.getPath()));
      
        return (ResponseEntity<Advertisement>) ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(advertisement);
       //TODO return ResponseEntity with advertisement in the body, location header and HttpStatus.CREATED status code
    }  
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}