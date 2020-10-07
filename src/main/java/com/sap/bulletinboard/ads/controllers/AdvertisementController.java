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
import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus; //enumeration for HTTP status codes

import com.fasterxml.jackson.annotation.JsonProperty;
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
    public static final String PATH_PAGES = PATH + "/pages/";
    public static final int FIRST_PAGE_ID = 0;
    // allows server side optimization e.g. via caching
    public static final int DEFAULT_PAGE_SIZE = 20;
//    private static final Map<Long, Advertisement> ads = new HashMap<>(); // temporary data storage, key represents the
    private AdvertisementRepository adRepository;
                                                                         // ID
    @Inject
    public AdvertisementController(AdvertisementRepository repository) {
        this.adRepository = repository;
    }
    
    
    @GetMapping
    public ResponseEntity<AdvertisementList> advertisements() {
        return advertisementsForPage(FIRST_PAGE_ID);
    }
    
    @GetMapping("/pages/{pageId}") 
    public ResponseEntity<AdvertisementList> advertisementsForPage(@PathVariable("pageId") int pageId) {

        Page<Advertisement> page = adRepository.findAll(new PageRequest(pageId, DEFAULT_PAGE_SIZE));

        return new ResponseEntity<AdvertisementList>(new AdvertisementList(page.getContent()),
                buildLinkHeader(page, PATH_PAGES), HttpStatus.OK);
    }
    
    public static HttpHeaders buildLinkHeader(Page<?> page, String path) {
        StringBuilder linkHeader = new StringBuilder();
        if (page.hasPrevious()) {
            int prevNumber = page.getNumber() - 1;
            linkHeader.append("<").append(path).append(prevNumber).append(">; rel=\"previous\"");
            if (!page.isLast())
                linkHeader.append(", ");
        }
        if (page.hasNext()) {
            int nextNumber = page.getNumber() + 1;
            linkHeader.append("<").append(path).append(nextNumber).append(">; rel=\"next\"");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LINK, linkHeader.toString());
        return headers;
    }

    public static class AdvertisementList {
        @JsonProperty("value")
        public List<Advertisement> advertisements = new ArrayList<>();
        public AdvertisementList(Iterable<Advertisement> ads) {
            ads.forEach(advertisements::add);
        }
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