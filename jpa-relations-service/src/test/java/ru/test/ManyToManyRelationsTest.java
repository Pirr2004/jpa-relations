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
import ru.test.manytomany.entity.Course;
import ru.test.manytomany.entity.Student;
import ru.test.manytomany.jpa.CourseDAO;
import ru.test.manytomany.jpa.StudentDAO;

import java.util.LinkedList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableConfigurationProperties(value = ManyToManyJpaConfiguration.class)
@TestPropertySource("classpath:application.properties")
@ContextConfiguration(classes = {DatabaseTestConfiguration.class})
public class ManyToManyRelationsTest {

    @Autowired
    private StudentDAO studentDAO;

    @Autowired
    private CourseDAO courseDAO;

    private Logger logger = LoggerFactory.getLogger(ManyToManyRelationsTest.class);

    @Test
    @Transactional
    @Rollback(false)
    public void test() {
        /*for (int i=0; i<500;i++) {
            User user = User.builder().name("Что имя тебе мое").build();
            Address address = Address.builder().houseNumber("111a").street("Улица").build();
            user.setAddress(address);
            userDAO.save(user);
        }*/
        /*Student student= Student.builder().sname("имя").courseList(new LinkedList<Course>(){{
            add(Course.builder().cname("курс 1").build());
            add(Course.builder().cname("курс 2").build());
            add(Course.builder().cname("курс 3").build());
        }}).build();

        studentDAO.saveAndFlush(student); */

        Student student1=studentDAO.findById(1l).get();

        List<Course> courseList= student1.getCourseList();

        logger.warn("size:" + courseList.size());


       // Address address = addressDAO.findById(2l).get();
       // User user = userDAO.findByAddress(address).get();
       // logger.error("ID {}", user.getAddress().getStreet());


        //User user = userDAO.findById(1l).get();
        //logger.error("ID {}", user.getAddress().getStreet());

       //Address address = addressDAO.findById(2l).get();   //.findByStreet("Улица").get();

        //logger.error("ID {}", address.getUser());

     //   UserBuilder.

       // firstEntityDAO.save(FirstEntity.builder().name("name").build());
    }
}
