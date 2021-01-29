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
import ru.test.onetomany.entity.Accaunt;
import ru.test.onetomany.entity.Owner;
import ru.test.onetomany.jpa.AccauntDAO;
import ru.test.onetomany.jpa.OwnerDAO;

import java.util.HashSet;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableConfigurationProperties(value = OneToManyJpaConfiguration.class)
@TestPropertySource("classpath:application.properties")
@ContextConfiguration(classes = {DatabaseTestConfiguration.class})
public class OneToManyRelationsTest {

    @Autowired
    private OwnerDAO ownerDAO;

    @Autowired
    private AccauntDAO accauntDAO;

    private Logger logger = LoggerFactory.getLogger(OneToManyRelationsTest.class);

    @Test
    @Transactional
    @Rollback(false)
    public void test() {
        /*for (int i=0;i<300;i++){
            Owner owner = Owner.builder().name("name " + i).build();

            ownerDAO.save(owner);
            for (int j=0;j<3;j++) {
                Accaunt accaunt = Accaunt.builder().build();
                accaunt.setOwner(owner);
                accauntDAO.save(accaunt);
            }

        }*/



       /*Accaunt accaunt = accauntDAO.findById(2l).get();
       Owner owner =accaunt.getOwner();

       owner.getName();
*/
        Owner owner = ownerDAO.findById(1l).get();
        List<Accaunt> accauntList = accauntDAO.findAllByOwner(owner);
        accauntList.size();






        /*for (int i=0; i<500;i++) {
            User user = User.builder().name("Что имя тебе мое").build();
            Address address = Address.builder().houseNumber("111a").street("Улица").build();
            user.setAddress(address);
            userDAO.save(user);
        }*/

        /*Course course1 = Course.builder().cname("курс 1").build();
        Course course2 = Course.builder().cname("курс 2").build();
        Course course3 = Course.builder().cname("курс 3").build();
        courseDAO.saveAndFlush(course1);
        courseDAO.saveAndFlush(course2);
        courseDAO.saveAndFlush(course3);


        Student student1= Student.builder().sname("имя 1").courseList(new HashSet<Course>(){{
            add(course1);
            add(course2);
            add(course3);
        }}).build();

        studentDAO.saveAndFlush(student1);

        Student student2= Student.builder().sname("имя 2").courseList(new HashSet<Course>(){{
            add(course1);
            add(course2);
        }}).build();

        studentDAO.saveAndFlush(student2);*/


        //Student student1=studentDAO.findById(4l).get();
        //Set<Course> courseList= student1.getCourseList();
        //logger.warn("{}",courseList.size());

       // Course course = courseDAO.findById(2l).get();
       // Set<Student> studentSet = course.getStudentList();
       // studentSet.size();

        /*
        List<Student> studentList=studentDAO.findAllByCourseIds(new LinkedList<Long>()
            {{
                add(course.getId());
            }}
        );

        logger.warn("size:" + studentList.size()); */


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
