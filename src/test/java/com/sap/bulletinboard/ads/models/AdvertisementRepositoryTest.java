package com.sap.bulletinboard.ads.models;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sap.bulletinboard.ads.config.EmbeddedDatabaseConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = EmbeddedDatabaseConfig.class)
public class AdvertisementRepositoryTest {

    @Inject
    private AdvertisementRepository repo;
    
    
    @Test
    public void shouldSetIdOnFirstSave() {
        Advertisement entity = new Advertisement();
        entity.setTitle("title");
        entity = repo.save(entity);
        assertThat(entity.getId(), is(notNullValue()));
    }
    
    @Test
    public void shouldSetCreatedTimestampOnFirstSaveOnly() throws InterruptedException{
        Advertisement entity = new Advertisement();
        entity.setTitle("title");
        
        entity = repo.save(entity);
        Timestamp timestampAfterCreation = entity.getCreatedAt();
        assertThat(timestampAfterCreation, is(notNullValue()));
        
        entity.setTitle("Updated Title");
        Thread.sleep(5); //Better: mock time!

        entity = repo.save(entity);
        Optional<Advertisement> entityAfterUpdate = repo.findById(entity.getId());
        assertThat(entityAfterUpdate.isPresent(), is(true));
        assertThat(entityAfterUpdate.get().getCreatedAt(), is(timestampAfterCreation));
     
    }
    
    @Test
    public void shouldSetUpdatedTimestampOnEveryUpdate() throws InterruptedException{
        Advertisement entity = new Advertisement();
        entity.setTitle("title");
        entity = repo.save(entity);
        
        entity.setTitle("Updated Title");
        entity = repo.save(entity);
        Timestamp timestampAfterFirstUpdate = entity.getUpdatedAt();
        assertThat(timestampAfterFirstUpdate, is(notNullValue()));
        
        Thread.sleep(5); //Better: mock time!
        
        entity.setTitle("Updated Title 2");
        entity = repo.save(entity);
        Timestamp timestampAfterSecondUpdate = entity.getUpdatedAt();
        assertThat(timestampAfterSecondUpdate, is(not(timestampAfterFirstUpdate)));
    }
    
    @Test(expected = JpaOptimisticLockingFailureException.class)
    public void shouldUseVersionForConflicts() {
        Advertisement entity = new Advertisement();
        entity.setTitle("some title");
        entity = repo.save(entity); // persists entity and sets initial version

        entity.setTitle("entity instance 1");
        repo.save(entity); // returns instance with updated version

        repo.save(entity); // tries to persist entity with outdated version
    }

    @Test
    public void shouldFindByTitle() {
        Advertisement entity = new Advertisement();
        String title = "Find me";

        entity.setTitle(title);
        repo.save(entity);
        
        Advertisement foundEntity = repo.findByTitle(title).get(0);
        assertThat(foundEntity.getTitle(), is(title));
    }
    
    @Test
    public void shouldFindByIdGE() {
        Advertisement entity = new Advertisement();
        String title = "Find me";

        entity.setTitle(title);
        
        repo.save(entity);
        Long lId = entity.getId();
        List<Advertisement> foundCollection = repo.findByIdGreaterThanEqual(lId);
        Advertisement foundEntity = foundCollection.get(0);
        assertThat(foundCollection.size(), is(1));
        assertThat(foundEntity.getTitle(), is(title));
    }
}
