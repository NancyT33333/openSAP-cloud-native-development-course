package com.sap.bulletinboard.ads.controllers;

//import org.springframework.http.MediaType; //provides constants for content types
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.context.annotation.RequestScope;

import java.net.URI;
import java.net.URISyntaxException;
//import java.util.HashMap;
//import java.util.Map;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus; //enumeration for HTTP status codes

//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.bulletinboard.ads.models.Advertisement;
import com.sap.bulletinboard.ads.models.AdvertisementRepository;

@RestController
@RequestScope
@Validated
@RequestMapping(AdvertisementController.PATH)
public class AdvertisementController {
    public static final String PATH = "/api/v1/ads";
//    private static final Map<Long, Advertisement> ads = new HashMap<>(); // temporary data storage, key represents the
    private AdvertisementRepository adRepository;
                                                                         // ID
    @Inject
    public AdvertisementController(AdvertisementRepository repository) {
        this.adRepository = repository;
    }
    @GetMapping
    public Iterable<Advertisement> advertisements() {
        return adRepository.findAll();

    }

    @GetMapping("/{id}")
    
    public Advertisement advertisementById(@PathVariable("id") @Min(0) Long id) {
        if (!adRepository.exists(id) ) {
            throw new NotFoundException("No ad with this id");
        }
        return adRepository.findOne(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Advertisement> advertisementPutById(@PathVariable("id") Long id, @Valid @RequestBody Advertisement body,   
            UriComponentsBuilder uriComponentsBuilder) throws URISyntaxException {

        if (!adRepository.exists(id) ) {
            throw new NotFoundException("No ad with this id");
        }

        if (body.getId()!= null && id != body.getId()) {
            throw new InconsistentException("Ids don't match");
        }
        
        Advertisement updAd = adRepository.findOne(id);
        updAd.setTitle(body.getTitle());
      
      
        updAd = adRepository.save(updAd);
        
        UriComponents uriComponents = uriComponentsBuilder.path(PATH + "/{id}").buildAndExpand(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(uriComponents.getPath()));
        return (ResponseEntity<Advertisement>) ResponseEntity.status(HttpStatus.OK).headers(headers)
                .body(updAd);
    }
    
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void advertisementDelById(@PathVariable("id") Long id) {
        if(!adRepository.exists(id) ) {
            throw new NotFoundException("No ad with this id");
        }
        adRepository.delete(id);

    }

    // Mass deleting of ads
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping()
    public void advertisementDeleteGeneral() {

        adRepository.deleteAll();
    }

    /**
     * @RequestBody is bound to the method argument. HttpMessageConverter resolves method argument depending on the
     *              content type. 
     *              returns ResponseEntity with advertisement in the body, location header and HttpStatus.CREATED status 
     *              code
     */
    @PostMapping
    public ResponseEntity<Advertisement> add(@Valid @RequestBody Advertisement advertisement,
        UriComponentsBuilder uriComponentsBuilder) throws URISyntaxException {
        Advertisement newAd = adRepository.save( advertisement);
        long lng = newAd.getId();
        UriComponents uriComponents = uriComponentsBuilder.path(PATH + "/{id}").buildAndExpand(lng);
      
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(uriComponents.getPath()));

        return (ResponseEntity<Advertisement>) ResponseEntity.status(HttpStatus.CREATED).headers(headers)
                .body(newAd);
        
    }

}