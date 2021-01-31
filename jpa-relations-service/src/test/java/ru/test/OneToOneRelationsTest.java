package ru.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.test.onetoone.entity.Address;
import ru.test.onetoone.entity.AddressExt;
import ru.test.onetoone.entity.User;
import ru.test.onetoone.jpa.AddressDAO;
import ru.test.onetoone.jpa.AddressExtDAO;
import ru.test.onetoone.jpa.UserDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableConfigurationProperties(value = OneToOneJpaConfiguration.class)
@TestPropertySource("classpath:application.properties")
@ContextConfiguration(classes = {DatabaseTestConfiguration.class})
public class OneToOneRelationsTest {

    @Autowired
    private AddressDAO addressDAO;

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private AddressExtDAO addressExtDAO;

    private Logger logger = LoggerFactory.getLogger(OneToOneRelationsTest.class);

    @Test
    @Transactional
    @Rollback(false)
    public void test() {
        /* for (int i=0; i<1000;i++) {
            User user = User.builder().name("Что имя тебе мое").build();
            AddressExt ext = AddressExt.builder().build();
            addressExtDAO.save(ext);
            Address address = Address.builder().houseNumber("111a").street("Улица").addressExt(ext).build();
            addressDAO.save(address);
            user.setAddress(address);
            userDAO.save(user);
        } */

        //User user = userDAO.findById(1l).get();
       // Address address = addressDAO.findById(2l).get();
       // address.getUser().getName();
        //User user = userDAO.findByAddress(address).get();
        //logger.error("ID {}", user.getAddress().getStreet());
        //logger.error("ID {}", address.getUser().getName());


       // User user = userDAO.findById(3l).get();
       // logger.error("ID {}", user.getAddress().getStreet());

       //Address address = addressDAO.findById(2l).get();   //.findByStreet("Улица").get();

        //logger.error("ID {}", address.getUser());

     //   UserBuilder.

       // firstEntityDAO.save(FirstEntity.builder().name("name").build());
    }
}
