package com.sap.bulletinboard.ads.controllers;

import org.springframework.http.MediaType; //provides constants for content types
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
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus; //enumeration for HTTP status codes

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.bulletinboard.ads.models.Advertisement;

@RestController
@RequestScope
@Validated
@RequestMapping(AdvertisementController.PATH)
public class AdvertisementController {
    public static final String PATH = "/api/v1/ads";
    private static final Map<Long, Advertisement> ads = new HashMap<>(); // temporary data storage, key represents the
                                                                         // ID

    @GetMapping
    public Iterable<Advertisement> advertisements() {
        return AdvertisementController.ads.values();

    }

    @GetMapping("/{id}")
    
    public Advertisement advertisementById(@PathVariable("id") @Min(0) Long id) {
        if (!this.ads.containsKey(id)) {
            throw new NotFoundException("No ad with this id");
        }
        return this.ads.get(id);
    }

    @PutMapping("/{id}")
    public Advertisement advertisementPutById(@PathVariable("id") Long id, @Valid @RequestBody Advertisement body) {

        if (!this.ads.containsKey(id)) {
            throw new NotFoundException("No ad with this id");
        }
        this.ads.remove(id);
        this.ads.put(id, body);
        return this.ads.get(id);
    }
    
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void advertisementDelById(@PathVariable("id") Long id) {
        if (!this.ads.containsKey(id)) {
            throw new NotFoundException("No ad with this id");
        }
        this.ads.remove(id);

    }

    // Mass deleting of ads
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping()
    public void advertisementDeleteGeneral() {

        this.ads.clear();
    }

    /**
     * @RequestBody is bound to the method argument. HttpMessageConverter resolves method argument depending on the
     *              content type.
     */
    @PostMapping
    public ResponseEntity<Advertisement> add(@Valid @RequestBody Advertisement advertisement,
            UriComponentsBuilder uriComponentsBuilder) throws URISyntaxException {
        long lng = this.ads.size() + 1;
        UriComponents uriComponents = uriComponentsBuilder.path(PATH + "/{id}").buildAndExpand(lng);
        this.ads.put(lng, advertisement);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(uriComponents.getPath()));

        return (ResponseEntity<Advertisement>) ResponseEntity.status(HttpStatus.CREATED).headers(headers)
                .body(advertisement);
        // TODO return ResponseEntity with advertisement in the body, location header and HttpStatus.CREATED status code
    }

}