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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus; //enumeration for HTTP status codes

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.bulletinboard.ads.models.Advertisement;
import com.sap.bulletinboard.ads.models.AdvertisementRepository;
//import com.sap.bulletinboard.ads.services.StatisticsServiceClient;
//import com.sap.bulletinboard.ads.services.UserServiceClient;
import com.sap.hcp.cf.logging.common.customfields.CustomField;

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
//    ID
    private AdvertisementRepository adRepository;
    private Logger logger;  
    
    
//    private UserServiceClient userServiceClient;   
//    private StatisticsServiceClient statisticsServiceClient;


//     StatisticsServiceClient statisticsServiceClient 
//    UserServiceClient userServiceClient 
    @Inject
    public AdvertisementController(AdvertisementRepository repository) {
        this.adRepository = repository;
        Logger logger = LoggerFactory.getLogger(getClass());
        this.logger = logger;
//        this.userServiceClient = userServiceClient;
//        this.statisticsServiceClient = statisticsServiceClient; 
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
        MDC.put("endpoind adressed", PATH + id); 
        logger.info("get request received for id {}", id);
//        statisticsServiceClient.advertisementIsShown(id);
        if (!adRepository.exists(id) ) {            
            NotFoundException notFoundException = new NotFoundException("No ad with id" + id);         
            logger.warn("request failed", notFoundException);
            throw notFoundException;            
            
        }
        logger.info("found {}", adRepository.findOne(id).toString());
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
            NotFoundException notFoundException = new NotFoundException("No ad with id" + id);         
            logger.warn("request failed", notFoundException);
            throw notFoundException;            
        }
        adRepository.delete(id);

    }

    // Mass deleting of ads
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping()
    public void advertisementDeleteGeneral() {
        this.logger.info("demonstration of custom fields, not part of message", CustomField.customField("potentially suspicious action", "delete all"));
        this.logger.info("demonstration of custom fields, part of message: {}", CustomField.customField("potentially suspicious action", "delete all"));
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
        UriComponentsBuilder uriComponentsBuilder) throws URISyntaxException, NotPremiumUserException {
        // создаём маркер technical 
        Marker technicalMarker = MarkerFactory.getMarker("TECHNICAL");
//        if (userServiceClient.isPremiumUser("42")) {
            Advertisement newAd = adRepository.save( advertisement);
            long lng = newAd.getId();
            UriComponents uriComponents = uriComponentsBuilder.path(PATH + "/{id}").buildAndExpand(lng);        
            
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(new URI(uriComponents.getPath()));
            
            logger.info(technicalMarker, "Created advertisement, version {}", newAd.getVersion());
            return (ResponseEntity<Advertisement>) ResponseEntity.status(HttpStatus.CREATED).headers(headers)
                    .body(newAd);
//        }  else {
//            throw new NotPremiumUserException(null);
//        }
        
    }

}