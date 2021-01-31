# Jpa Relations

Разбираемся с отображением jpa-аннотаций @OneToOne, @ManyToOne, @OneToMany, @JoinColum, @OrderColumn, 
их отображением на реляционную БД и формируемыми sql-запросами при работе на связке spring-jpa+hibernte.

# 1. OneToOne

## 1.1 Односторонняя направленность (ссылка только с одной сущности)
Entity:
```
@Entity
@Table(name ="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private Address address;

    ....
}

@Entity
@Table(name ="address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    ....

}
```

DSL:
```
create table public.address (
       id int8 not null,
       ....
       primary key (id)
    )

create table public.user (
       id int8 not null,
       address_id int8,
       ....
       primary key (id)
    )
```

### 1.1.1 Обращение через главную сущность:
```
User user = userDAO.findById(...).get()
user.getAddress().getXXX
```

Приводит к генерации запроса:
>  - в случае fetch = FetchType.EAGER:
> ```
>     select
>         user0_.id as id1_1_0_,
>         user0_.address_id as address_3_1_0_,
>         user0_.name as name2_1_0_,
>         address1_.id as id1_0_1_,
>         address1_.houseNumber as houseNum2_0_1_,
>         address1_.street as street3_0_1_ 
>     from
>         public.user user0_ 
>     left outer join
>         public.address address1_ 
>             on user0_.address_id=address1_.id 
>     where
>         user0_.id=?
> ```
> - в случае fetch = FetchType.LAZY  (@OneToOne(fetch = FetchType.LAZY)):
> ```
>   select
>       user0_.id as id1_1_0_,
>       user0_.address_id as address_3_1_0_,
>       user0_.name as name2_1_0_ 
>   from
>       public.user user0_ 
>   where
>       user0_.id=?
>   
>   select
>       address0_.id as id1_0_0_,
>       address0_.houseNumber as houseNum2_0_0_,
>       address0_.street as street3_0_0_ 
>   from
>       public.address address0_ 
>   where
>       address0_.id=?
> ```

### 1.1.2 Обращение через DAO:
```
Address address = addressDAO.findById(...).get();
User user = userDAO.findByAddress(address).get();
```
Приводит к генерации запроса (и в случае FetchType.EAGER и в случае FetchType.LAZY):
>```
>    select
>        address0_.id as id1_0_0_,
>        address0_.houseNumber as houseNum2_0_0_,
>        address0_.street as street3_0_0_ 
>    from
>        public.address address0_ 
>    where
>        address0_.id=?
>
>    select
>        user0_.id as id1_1_,
>        user0_.address_id as address_3_1_,
>        user0_.name as name2_1_ 
>    from
>        public.user user0_ 
>    where
>        user0_.address_id=?
>```

Поиск осуществляется по полю "user".address_id. Отсутсвие индекса на user.address_id приводит к **Seq Scan**!!

```
  ->  Seq Scan on "user" user0_  (cost=0.00..11.75 rows=1 width=532)
        Filter: (address_id = 1)
```

После добавления индекса(при наличии достаточного кол-ва записей(>1000)):
```
CREATE INDEX user_address_id_idx ON public."user"  using btree (address_id);
```

```
  ->  Index Scan using user_address_id_idx on "user" user0_  (cost=0.28..8.29 rows=1 width=46)
        Index Cond: (address_id = 1)
```



## 1.2 Двусторонняя направленность без mappedBy

Java-код:
```
@Entity
@Table(name ="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private Address address;

    ....
}

@Entity
@Table(name ="address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    ....

    @OneToOne
    private User user;
}
```

DDL SQL:
```
    create table public.address (
       id int8 not null,
        user_id int8,
        ..............
        primary key (id)
    )
    
    create table public.user (
       id int8 not null,
        address_id int8,
        ..............
        primary key (id)
    )
```

- Поведение аналогично двум односторонним связям OneToOne (см 1.1)

## 1.3 Двусторонняя направленность (с mappedBy)

Java-код:
```
@Entity
@Table(name ="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private Address address;

    ....
}

@Entity
@Table(name ="address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(mappedBy = "address")
    private User user;
   
    ....

}
```

DSL:
```
create table public.address (
       id int8 not null,
       ....
       primary key (id)
    )

create table public.user (
       id int8 not null,
       address_id int8,
       ....
       primary key (id)
    )
```

### 1.3.1 Обращение через главную сущность:
```
User user = userDAO.findById(...).get()
user.getAddress().getXXX
```

Приводит к генерации запроса:
>  - в случае fetch = FetchType.EAGER:
> ```
>     select
>         user0_.id as id1_1_0_,
>         user0_.address_id as address_3_1_0_,
>         user0_.name as name2_1_0_,
>         address1_.id as id1_0_1_,
>         address1_.houseNumber as houseNum2_0_1_,
>         address1_.street as street3_0_1_ 
>     from
>         public.user user0_ 
>     left outer join
>         public.address address1_ 
>             on user0_.address_id=address1_.id 
>     where
>         user0_.id=?
> ```

>  - в случае fetch = FetchType.LAZY:
> ```select
>         user0_.id as id1_1_0_,
>         user0_.address_id as address_3_1_0_,
>         user0_.name as name2_1_0_ 
>     from
>         public.user user0_ 
>     where
>         user0_.id=?
> 
>     select
>         address0_.id as id1_0_0_,
>         address0_.houseNumber as houseNum2_0_0_,
>         address0_.street as street3_0_0_ 
>     from
>         public.address address0_ 
>     where
>         address0_.id=?
> ```
**Различий по сравнению с 1.1.1 нет!**

### 1.3.2 Обращение через зависимую сущность
```
Address address = addressDAO.findById(ID).get();
address.getUser()
```
Приводит к генерации запроса:
>  - в случае fetch = FetchType.EAGER:
> ```
> select
>        address0_.id as id1_0_0_,
>        address0_.houseNumber as houseNum2_0_0_,
>         address0_.street as street3_0_0_,
>         user1_.id as id1_1_1_,
>         user1_.address_id as address_3_1_1_,
>         user1_.name as name2_1_1_ 
>     from
>         public.address address0_ 
>     left outer join
>         public.user user1_ 
>             on address0_.id=user1_.address_id 
>     where
>         address0_.id=?
> ```

>  - в случае fetch = FetchType.LAZY:
> ```
>     select
>         address0_.id as id1_0_0_,
>         address0_.houseNumber as houseNum2_0_0_,
>         address0_.street as street3_0_0_ 
>     from
>         public.address address0_ 
>     where
>         address0_.id=?
>  
>     select
>         user0_.id as id1_1_0_,
>         user0_.address_id as address_3_1_0_,
>         user0_.name as name2_1_0_ 
>     from
>         public.user user0_ 
>     where
>         user0_.address_id=?
> ```

**Выводы**:
- различий с поиском через DAO нет

# 2. ManyToMany

Связь реализуется через дополнительную таблицу (join table).
- Настоятельно рекомендуют использовать Set
- Работать только через LAZY
- Не использовать CASCASE.REMOVE и CASCADE.ALL


## 2.1 Ссылка только с одной сущности

Java-код:
```java
@Entity
@Table(name ="student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Course> courseList;

    ..............
}

@Entity
@Table(name ="course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    ................
}

```

> DDL SQL:
> ```
>     create table public.course (
>        id int8 not null,
>         cname varchar(255),
>         primary key (id)
>     )
>     
>     create table public.student (
>        id int8 not null,
>         sname varchar(255),
>         primary key (id)
>     )
>     
>     create table public.student_course (
>        Student_id int8 not null,
>         courseList_id int8 not null
>     )
> ```

### 2.1.1 Обращение через главную сущность

```
Student student1=studentDAO.findById(..).get();
List<Course> courseList= student1.getCourseList();
```
Приводит к генерации запросов в:
>  - и в случае fetch = FetchType.EAGER и для fetch = FetchType.LAZY
>
>```
>     select
>            student0_.id as id1_1_0_,
>            student0_.sname as sname2_1_0_ 
>        from
>            public.student student0_ 
>        where
>            student0_.id=?
>    
>        select
>            courselist0_.Student_id as Student_1_2_0_,
>            courselist0_.courseList_id as courseLi2_2_0_,
>            course1_.id as id1_0_1_,
>            course1_.cname as cname2_0_1_ 
>        from
>            public.student_course courselist0_ 
>        inner join
>            public.course course1_ 
>                on courselist0_.courseList_id=course1_.id 
>        where
>            courselist0_.Student_id=?
>
>```

```
Hash Join  (cost=33.24..45.22 rows=6 width=540)
  Hash Cond: (course1_.id = courselist0_.courselist_id)
  ->  Seq Scan on course course1_  (cost=0.00..11.40 rows=140 width=524)
  ->  Hash  (cost=33.12..33.12 rows=9 width=16)
        ->  Seq Scan on student_course courselist0_  (cost=0.00..33.12 rows=9 width=16)
              Filter: (student_id = 1)
```

**Выводы:**
- inner join !!

### 2.1.2 Обращение через зависимую сущность

```
    @Query("select st from Student st join st.courseList scl where scl.id in :courseListIds")
    List<Student> findAllByCourseIds(@Param("courseListIds") Iterable<Long> courseListIds);
    .................

    Course course = courseDAO.findById(2l).get();
    List<Student> studentList=studentDAO.findAllByCourseIds(new LinkedList<Long>()
        {{
            add(course.getId());
        }}
    );

    studentList.size()
```
EAGER:

```
12:01:25.487 [main] DEBUG org.hibernate.SQL - 
    select
        course0_.id as id1_0_0_,
        course0_.cname as cname2_0_0_ 
    from
        public.course course0_ 
    where
        course0_.id=?

    select
        student0_.id as id1_1_,
        student0_.sname as sname2_1_ 
    from
        public.student student0_ 
    inner join
        public.student_course courselist1_ 
            on student0_.id=courselist1_.Student_id 
    inner join
        public.course course2_ 
            on courselist1_.courseList_id=course2_.id 
    where
        course2_.id in (
            ?
        )

    select
        courselist0_.Student_id as Student_1_2_0_,
        courselist0_.courseList_id as courseLi2_2_0_,
        course1_.id as id1_0_1_,
        course1_.cname as cname2_0_1_ 
    from
        public.student_course courselist0_ 
    inner join
        public.course course1_ 
            on courselist0_.courseList_id=course1_.id 
    where
        courselist0_.Student_id=?

    select
        courselist0_.Student_id as Student_1_2_0_,
        courselist0_.courseList_id as courseLi2_2_0_,
        course1_.id as id1_0_1_,
        course1_.cname as cname2_0_1_ 
    from
        public.student_course courselist0_ 
    inner join
        public.course course1_ 
            on courselist0_.courseList_id=course1_.id 
    where
        courselist0_.Student_id=?

```
**4!! запроса к БД (баг??)**

LAZY:
```
    select
        course0_.id as id1_0_0_,
        course0_.cname as cname2_0_0_ 
    from
        public.course course0_ 
    where
        course0_.id=?
 
    select
        student0_.id as id1_1_,
        student0_.sname as sname2_1_ 
    from
        public.student student0_ 
    inner join
        public.student_course courselist1_ 
            on student0_.id=courselist1_.Student_id 
    inner join
        public.course course2_ 
            on courselist1_.courseList_id=course2_.id 
    where
        course2_.id in (
            ?
        )
```

## 2.2 Ссылка с двух сторон

Java-код:
```
@Entity
@Table(name ="student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "student_course",
            joinColumns = { @JoinColumn(name = "fk_student") },
            inverseJoinColumns = { @JoinColumn(name = "fk_course") })
    private Set<Course> courseList;

    ..............
}

@Entity
@Table(name ="course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "courseList")
    private Set<Student> studentList;

    ................
}
```

DDL:
```
create table public.course (
       id int8 not null,
        cname varchar(255),
        primary key (id)
    )
    
    create table public.student (
       id int8 not null,
        sname varchar(255),
        primary key (id)
    )
    
    create table public.student_course (
       fk_student int8 not null,
        fk_course int8 not null,
        primary key (fk_student, fk_course)
    )
```

### 2.1.1 Обращение через главную сущность

```
Student student1=studentDAO.findById(4l).get();
Set<Course> courseList= student1.getCourseList();
courseList.size();
```

LAZY:
```
select
        student0_.id as id1_1_0_,
        student0_.sname as sname2_1_0_ 
    from
        public.student student0_ 
    where
        student0_.id=?

    select
        courselist0_.fk_student as fk_stude1_2_0_,
        courselist0_.fk_course as fk_cours2_2_0_,
        course1_.id as id1_0_1_,
        course1_.cname as cname2_0_1_ 
    from
        public.student_course courselist0_ 
    inner join
        public.course course1_ 
            on courselist0_.fk_course=course1_.id 
    where
        courselist0_.fk_student=?
```

EAGER(тянет всю БД):
```
select
        student0_.id as id1_1_0_,
        student0_.sname as sname2_1_0_,
        courselist1_.fk_student as fk_stude1_2_1_,
        course2_.id as fk_cours2_2_1_,
        course2_.id as id1_0_2_,
        course2_.cname as cname2_0_2_ 
    from
        public.student student0_ 
    left outer join
        public.student_course courselist1_ 
            on student0_.id=courselist1_.fk_student 
    left outer join
        public.course course2_ 
            on courselist1_.fk_course=course2_.id 
    where
        student0_.id=?

    select
        studentlis0_.fk_course as fk_cours2_2_0_,
        studentlis0_.fk_student as fk_stude1_2_0_,
        student1_.id as id1_1_1_,
        student1_.sname as sname2_1_1_ 
    from
        public.student_course studentlis0_ 
    inner join
        public.student student1_ 
            on studentlis0_.fk_student=student1_.id 
    where
        studentlis0_.fk_course=?

    select
        studentlis0_.fk_course as fk_cours2_2_0_,
        studentlis0_.fk_student as fk_stude1_2_0_,
        student1_.id as id1_1_1_,
        student1_.sname as sname2_1_1_ 
    from
        public.student_course studentlis0_ 
    inner join
        public.student student1_ 
            on studentlis0_.fk_student=student1_.id 
    where
        studentlis0_.fk_course=?

    select
        courselist0_.fk_student as fk_stude1_2_0_,
        courselist0_.fk_course as fk_cours2_2_0_,
        course1_.id as id1_0_1_,
        course1_.cname as cname2_0_1_ 
    from
        public.student_course courselist0_ 
    inner join
        public.course course1_ 
            on courselist0_.fk_course=course1_.id 
    where
        courselist0_.fk_student=?

    select
        studentlis0_.fk_course as fk_cours2_2_0_,
        studentlis0_.fk_student as fk_stude1_2_0_,
        student1_.id as id1_1_1_,
        student1_.sname as sname2_1_1_ 
    from
        public.student_course studentlis0_ 
    inner join
        public.student student1_ 
            on studentlis0_.fk_student=student1_.id 
    where
        studentlis0_.fk_course=?
```


### 2.1.1 Обращение через зависимую сущность

```
Student student1=studentDAO.findById(4l).get();
Set<Course> courseList= student1.getCourseList();
courseList.size();
```

LAZY:
```
select
        course0_.id as id1_0_0_,
        course0_.cname as cname2_0_0_ 
    from
        public.course course0_ 
    where
        course0_.id=?

    select
        studentlis0_.fk_course as fk_cours2_2_0_,
        studentlis0_.fk_student as fk_stude1_2_0_,
        student1_.id as id1_1_1_,
        student1_.sname as sname2_1_1_ 
    from
        public.student_course studentlis0_ 
    inner join
        public.student student1_ 
            on studentlis0_.fk_student=student1_.id 
    where
        studentlis0_.fk_course=?
```
EAGER:
```
select
        course0_.id as id1_0_0_,
        course0_.cname as cname2_0_0_,
        studentlis1_.fk_course as fk_cours2_2_1_,
        student2_.id as fk_stude1_2_1_,
        student2_.id as id1_1_2_,
        student2_.sname as sname2_1_2_ 
    from
        public.course course0_ 
    left outer join
        public.student_course studentlis1_ 
            on course0_.id=studentlis1_.fk_course 
    left outer join
        public.student student2_ 
            on studentlis1_.fk_student=student2_.id 
    where
        course0_.id=?

    select
        courselist0_.fk_student as fk_stude1_2_0_,
        courselist0_.fk_course as fk_cours2_2_0_,
        course1_.id as id1_0_1_,
        course1_.cname as cname2_0_1_ 
    from
        public.student_course courselist0_ 
    inner join
        public.course course1_ 
            on courselist0_.fk_course=course1_.id 
    where
        courselist0_.fk_student=?

    select
        studentlis0_.fk_course as fk_cours2_2_0_,
        studentlis0_.fk_student as fk_stude1_2_0_,
        student1_.id as id1_1_1_,
        student1_.sname as sname2_1_1_ 
    from
        public.student_course studentlis0_ 
    inner join
        public.student student1_ 
            on studentlis0_.fk_student=student1_.id 
    where
        studentlis0_.fk_course=?

    select
        courselist0_.fk_student as fk_stude1_2_0_,
        courselist0_.fk_course as fk_cours2_2_0_,
        course1_.id as id1_0_1_,
        course1_.cname as cname2_0_1_ 
    from
        public.student_course courselist0_ 
    inner join
        public.course course1_ 
            on courselist0_.fk_course=course1_.id 
    where
        courselist0_.fk_student=?

    select
        studentlis0_.fk_course as fk_cours2_2_0_,
        studentlis0_.fk_student as fk_stude1_2_0_,
        student1_.id as id1_1_1_,
        student1_.sname as sname2_1_1_ 
    from
        public.student_course studentlis0_ 
    inner join
        public.student student1_ 
            on studentlis0_.fk_student=student1_.id 
    where
        studentlis0_.fk_course=?
```


# 3. OneToMany/ManyToOne

**!!! Performance Antipatterns of One To ManyAssociation in Hibernate**

* Bag semantics -> List / Collection + @OneToMany -> One Element Added: 1 delete, N inserts , One Element Removed: 1 delete, N inserts
* List semantics -> List + @OneToMany + @IndexColumn / @OrderColumn -> One Element Added: 1 insert, M updates, One Element Removed: 1 delete, M updates
* Set semantics -> Set + @OneToMany -> One Element Added: 1 insert , One Element Removed: 1 delete

## 3.1 Ссылка только со стороны главной сущности
java:
```
@Entity
@Table(name ="owner")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany( fetch = FetchType.LAZY)
    private Set<Accaunt> accounts;
    ...
}

@Entity
@Table(name ="accaunt")
public class Accaunt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

 ....
}


```

sql:  - связь через таблицу!!
```
create table public.accaunt (
       id int8 not null,
        primary key (id)
    )
   
    create table public.owner (
       id int8 not null,
        primary key (id)
    )

    create table public.owner_accaunt (
       Owner_id int8 not null,
        account_id int8 not null,
        primary key (Owner_id, account_id)
    )
```

После добавления 
```
    @OneToMany( fetch = FetchType.LAZY)
    @JoinColumn(name="owner_fk")
    private Set<Accaunt> account;
```

sql: - связь через поле owner_fk
```
    create table public.accaunt (
       id int8 not null,
        owner_fk int8,
        primary key (id)
    )
    
    create table public.owner (
       id int8 not null,
        primary key (id)
    )
```

Далее работаем через joinColumn:

### 3.1.1 Обращение через главную сущность
```
Owner owner = ownerDAO.findById(...).get();
owner.getAccount().size();
```
и в случае и в случае с EAGER/LAZY:
sql:
```
    select
        owner0_.id as id1_1_0_ 
    from
        public.owner owner0_ 
    where
        owner0_.id=?

    select
        account0_.owner_fk as owner_fk2_0_0_,
        account0_.id as id1_0_0_,
        account0_.id as id1_0_1_  // --?????????????
    from
        public.accaunt account0_ 
    where
        account0_.owner_fk=?


```

### 3.1.1 Обращение через зависимую сущность

```
    @Query("select o from Owner o join o.accounts a where a=:accaunt")
    Optional<Owner> findByAccaunt(@Param("accaunt") Accaunt accaunt);

    .......

    Accaunt accaunt = accauntDAO.findById(1l).get();
    Owner owner = ownerDAO.findByAccaunt(accaunt).get();

```

sql LAZY:

```
select
        accaunt0_.id as id1_0_0_ 
    from
        public.accaunt accaunt0_ 
    where
        accaunt0_.id=?
 
    select
        owner0_.id as id1_1_ 
    from
        public.owner owner0_ 
    inner join
        public.accaunt accounts1_ 
            on owner0_.id=accounts1_.owner_fk 
    where
        accounts1_.id=?
```

sql EAGER:

**Опять три запроса!!!**

```
    select
        accaunt0_.id as id1_0_0_ 
    from
        public.accaunt accaunt0_ 
    where
        accaunt0_.id=?

    select
        owner0_.id as id1_1_ 
    from
        public.owner owner0_ 
    inner join
        public.accaunt accounts1_ 
            on owner0_.id=accounts1_.owner_fk 
    where
        accounts1_.id=?

    select
        accounts0_.owner_fk as owner_fk2_0_0_,
        accounts0_.id as id1_0_0_,
        accounts0_.id as id1_0_1_ 
    from
        public.accaunt accounts0_ 
    where
        accounts0_.owner_fk=?

```

## 3.2 Ссылка со стороны главной сущности и со стороны зависимой с mappedBy
java:
```
@Entity
@Table(name ="owner")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany( fetch = FetchType.LAZY,mappedBy = "owner")
    private Set<Accaunt> accounts;
}


@Entity
@Table(name ="accaunt")
public class Accaunt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name="owner_fk")
    private Owner owner;
    
    ..................
}

```

sql:
```
    create table public.accaunt (
       id int8 not null,
        owner_fk int8,
        primary key (id)
    )
    
    create table public.owner (
       id int8 not null,
        primary key (id)
    )

```
### 3.2.1 Обращение через главную сущность
```
Owner owner = ownerDAO.findById(...).get();
owner.getAccount().size();
```
LAZY:
```
    select
        accaunt0_.id as id1_0_0_,
        accaunt0_.owner_fk as owner_fk2_0_0_ 
    from
        public.accaunt accaunt0_ 
    where
        accaunt0_.id=?

    select
        owner0_.id as id1_1_ 
    from
        public.owner owner0_ 
    inner join
        public.accaunt accounts1_ 
            on owner0_.id=accounts1_.owner_fk 
    where
        accounts1_.id=?

```

EAGER:
```
    select
        accaunt0_.id as id1_0_0_,
        accaunt0_.owner_fk as owner_fk2_0_0_,
        owner1_.id as id1_1_1_ 
    from
        public.accaunt accaunt0_ 
    left outer join
        public.owner owner1_ 
            on accaunt0_.owner_fk=owner1_.id 
    where
        accaunt0_.id=?

    select
        accounts0_.owner_fk as owner_fk2_0_0_,
        accounts0_.id as id1_0_0_,
        accounts0_.id as id1_0_1_,
        accounts0_.owner_fk as owner_fk2_0_1_ 
    from
        public.accaunt accounts0_ 
    where
        accounts0_.owner_fk=?

```


### 3.2.2 Обращение через зависимую сущность
```
    Accaunt accaunt = accauntDAO.findById(2l).get();
       Owner owner =accaunt.getOwner();
       owner.getName();  // Необходимо обращение к полю таблицы. Иначе LAZY вообще обойдется одним запросом 
```

sql LAZY
```
 select
        accaunt0_.id as id1_0_0_,
        accaunt0_.owner_fk as owner_fk2_0_0_ 
    from
        public.accaunt accaunt0_ 
    where
        accaunt0_.id=?

    select
        owner0_.id as id1_1_0_,
        owner0_.name as name2_1_0_ 
    from
        public.owner owner0_ 
    where
        owner0_.id=?

```
sql EAGER:
```
select
        accaunt0_.id as id1_0_0_,
        accaunt0_.owner_fk as owner_fk2_0_0_,
        owner1_.id as id1_1_1_,
        owner1_.name as name2_1_1_ 
    from
        public.accaunt accaunt0_ 
    left outer join
        public.owner owner1_ 
            on accaunt0_.owner_fk=owner1_.id 
    where
        accaunt0_.id=?

    select
        accounts0_.owner_fk as owner_fk2_0_0_,
        accounts0_.id as id1_0_0_,
        accounts0_.id as id1_0_1_,
        accounts0_.owner_fk as owner_fk2_0_1_ 
    from
        public.accaunt accounts0_ 
    where
        accounts0_.owner_fk=?
```
**Странные запросы!!**

## 3.3 ManyToOne без обратной связи
прим. @ManyToOne не имеет поля mappedBy
java:
```
@Entity
@Table(name ="owner")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    ....
}

@Entity
@Table(name ="accaunt")
public class Accaunt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Owner owner;

}

```

sql:
```
    create table public.accaunt (
       id int8 not null,
        owner_id int8,
        primary key (id)
    )

    
    create table public.owner (
       id int8 not null,
        name varchar(255),
        primary key (id)
    )
```
### 3.3.1 Обращение через главную сущность
```
        Accaunt accaunt = accauntDAO.findById(2l).get();
       Owner owner =accaunt.getOwner();
       owner.getName();
```
LAZY:
```
select
        accaunt0_.id as id1_0_0_,
        accaunt0_.owner_id as owner_id2_0_0_ 
    from
        public.accaunt accaunt0_ 
    where
        accaunt0_.id=?

    select
        owner0_.id as id1_1_0_,
        owner0_.name as name2_1_0_ 
    from
        public.owner owner0_ 
    where
        owner0_.id=?
```


EAGER:
```
select
        accaunt0_.id as id1_0_0_,
        accaunt0_.owner_id as owner_id2_0_0_,
        owner1_.id as id1_1_1_,
        owner1_.name as name2_1_1_ 
    from
        public.accaunt accaunt0_ 
    left outer join
        public.owner owner1_ 
            on accaunt0_.owner_id=owner1_.id 
    where
        accaunt0_.id=?
```

### 3.3.1 Обращение через зависимую сущность
```
    Owner owner = ownerDAO.findById(..).get();
    List<Accaunt> accauntList = accauntDAO.findAllByOwner(owner);
    accauntList.size();
```

LAZY:
```
select
        owner0_.id as id1_1_0_,
        owner0_.name as name2_1_0_ 
    from
        public.owner owner0_ 
    where
        owner0_.id=?
 
    select
        accaunt0_.id as id1_0_,
        accaunt0_.owner_id as owner_id2_0_ 
    from
        public.accaunt accaunt0_ 
    where
        accaunt0_.owner_id=?
```

EAGER:
```
select
        owner0_.id as id1_1_0_,
        owner0_.name as name2_1_0_ 
    from
        public.owner owner0_ 
    where
        owner0_.id=?

    select
        accaunt0_.id as id1_0_,
        accaunt0_.owner_id as owner_id2_0_ 
    from
        public.accaunt accaunt0_ 
    where
        accaunt0_.owner_id=?
```



* Bag semantics -> List / Collection + @OneToMany -> One Element Added: 1 delete, N inserts , One Element Removed: 1 delete, N inserts
* List semantics -> List + @OneToMany + @IndexColumn / @OrderColumn -> One Element Added: 1 insert, M updates, One Element Removed: 1 delete, M updates
* Set semantics -> Set + @OneToMany -> One Element Added: 1 insert , One Element Removed: 1 delete


Проверка 1: 

* Bag semantics -> List / Collection + @OneToMany -> One Element Added: 1 delete, N inserts , One Element Removed: 1 delete, N inserts

## Случай 1
java:
```
@Entity
@Table(name ="accaunt")
public class Accaunt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Owner owner;

}

@Entity
@Table(name ="owner")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "owner")
    private List<Accaunt> accaunts;

    @Column
    private String name;
}
```


```

        Owner owner = ownerDAO.findById(1L).get();
        Accaunt accaunt = Accaunt.builder().build();
        
        // owner.getAccaunts().add(accaunt)  не работает потому что MAPPED BY!!!
        accaunt.setOwner(owner);  
        accauntDAO.save(accaunt);

```

 sql - никаких N inserts!!:
```
    select
        owner0_.id as id1_1_0_,
        owner0_.name as name2_1_0_ 
    from
        public.owner owner0_ 
    where
        owner0_.id=?
    select
        nextval ('public.hibernate_sequence')

    insert 
    into
        public.accaunt
        (owner_id, id) 
    values
        (?, ?)
```

## Случай 2

java:
```
@Entity
@Table(name ="owner")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany
    private List<Accaunt> accaunts;

    ....
}

@Entity
@Table(name ="accaunt")
public class Accaunt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

   ...
}
```

ddl:
```
create table public.accaunt (
       id int8 not null,
        primary key (id)
    )

    
    create table public.owner (
       id int8 not null,
        name varchar(255),
        primary key (id)
    )
    
    create table public.owner_accaunt (
       Owner_id int8 not null,
        accaunts_id int8 not null
    )
```

Код:
```
        Owner owner = ownerDAO.findById(1L).get();
        Accaunt accaunt = Accaunt.builder().build();
        accauntDAO.save(accaunt);
        owner.getAccaunts().add(accaunt);
```

sql:
```
select
        owner0_.id as id1_1_0_,
        owner0_.name as name2_1_0_ 
    from
        public.owner owner0_ 
    where
        owner0_.id=?


    select
        nextval ('public.hibernate_sequence')

    select
        accaunts0_.Owner_id as Owner_id1_2_0_,
        accaunts0_.accaunts_id as accaunts2_2_0_,
        accaunt1_.id as id1_0_1_ 
    from
        public.owner_accaunt accaunts0_ 
    inner join
        public.accaunt accaunt1_ 
            on accaunts0_.accaunts_id=accaunt1_.id 
    where
        accaunts0_.Owner_id=?

    insert 
    into
        public.accaunt
        (id) 
    values
        (?)



    delete 
    from
        public.owner_accaunt 
    where
        Owner_id=?

    insert 
    into
        public.owner_accaunt
        (Owner_id, accaunts_id) 
    values
        (?, ?)

    insert 
    into
        public.owner_accaunt
        (Owner_id, accaunts_id) 
    values
        (?, ?)

    insert 
    into
        public.owner_accaunt
        (Owner_id, accaunts_id) 
    values
        (?, ?)

    insert 
    into
        public.owner_accaunt
        (Owner_id, accaunts_id) 
    values
        (?, ?)

```

**Вывод** Вот оно !!!  One Element Added: 1 delete, N inserts при работе через таблицу
Аналогично с удалением

При работе через joincolumn эффекта не наблюдается!!



**Выводы**
Работать через lazy
Использовать по возможности join column
При использовании join table всегда использовать Set<> (или просто всегда использовать Set)
Не забывать выставлять индексы на fk-поля





##### <a name="References"></a> Ссылки 
* [Spring Data JPA - Reference Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation)
* [One-to-One Relationship in JPA](https://www.baeldung.com/jpa-one-to-one)
* [Basic Many-To-Many](https://www.baeldung.com/jpa-many-to-many)
* [When and how to use query-specific fetching](https://thorben-janssen.com/best-practices-for-many-to-many-associations-with-hibernate-and-jpa/)
* [Why you should avoid CascadeType.REMOVE for to-many associations and what to do instead](https://thorben-janssen.com/avoid-cascadetype-delete-many-assocations/)
* [Performance Antipatterns of One To ManyAssociation in Hibernate](https://annals-csis.org/proceedings/2013/pliks/322.pdf)

```
select
        user0_.id as id1_2_0_,
        user0_.address_id as address_3_2_0_,
        user0_.name as name2_2_0_,
        address1_.id as id1_0_1_,
        address1_.addressExt_id as addressE4_0_1_,
        address1_.houseNumber as houseNum2_0_1_,
        address1_.street as street3_0_1_,
        addressext2_.id as id1_1_2_,
        addressext2_.name as name2_1_2_ 
    from
        public.user user0_ 
    left outer join
        public.address address1_ 
            on user0_.address_id=address1_.id 
    left outer join
        public.addressext addressext2_ 
            on address1_.addressExt_id=addressext2_.id 
    where
        user0_.id=?
```

```
    create table address (
       id int8 not null,
        houseNumber varchar(255),
        street varchar(255),
        user_id int8,
        primary key (id)
    )

    create table user (
       id int8 not null,
        name varchar(255),
        primary key (id)
    )
```