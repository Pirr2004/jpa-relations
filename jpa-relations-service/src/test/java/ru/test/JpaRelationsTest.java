package ru.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.test.entity.FirstEntity;
import ru.test.jpa.FirstEntityDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableConfigurationProperties(value = JpaConfiguration.class)
@TestPropertySource("classpath:application.properties")
@ContextConfiguration(classes = {DatabaseTestConfiguration.class})
public class JpaRelationsTest {

    @Autowired
    private FirstEntityDAO firstEntityDAO;

    @Test
    @Transactional
    @Rollback(false)
    public void test() {
        firstEntityDAO.save(FirstEntity.builder().name("name").build());
    }
}
