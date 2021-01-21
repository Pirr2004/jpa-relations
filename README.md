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







!!!!!!!!!!!!!!!!!!!!!!!!!!!!

----------------------------------------------














## 1.1 Односторонняя направленность (ссылка только с одной сущности)
Java-код:
```
@Entity
@Table(name ="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
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

DDL SQL:
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

### 1.1.1 Обращение через главную сущность
```
User user = userDAO.findById(...).get()
user.getAddress().getXXX
```
Генерируемый запрос:   
```
    select
            user0_.id as id1_1_0_,
            user0_.address_id as address_3_1_0_,
            user0_.name as name2_1_0_ 
        from
            public.user user0_ 
        where
            user0_.id=?
    
    select
            address0_.id as id1_0_0_,
            address0_.houseNumber as houseNum2_0_0_,
            address0_.street as street3_0_0_ 
        from
            public.address address0_ 
        where
            address0_.id=?
```

**Выводы**
- Поиск по PK таблиц user и address 

### 1.1.2 Поиск через DAO
```
Address address = addressDAO.findById(...).get();
User user = userDAO.findByAddress(address).get();
```
получается sql-запрос:
```
    select
            address0_.id as id1_0_0_,
            address0_.houseNumber as houseNum2_0_0_,
            address0_.street as street3_0_0_ 
        from
            public.address address0_ 
        where
            address0_.id=?
    
        select
            user0_.id as id1_1_,
            user0_.address_id as address_3_1_,
            user0_.name as name2_1_ 
        from
            public.user user0_ 
        where
            user0_.address_id=?
```
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


**Выводы:**
- Фильтрация по полю user.address_id. (требуется создание индекса)


## 1.2 Двусторонняя направленность (без mappedBy)
Java-код:
```
@Entity
@Table(name ="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
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

    @OneToOne(fetch = FetchType.LAZY)
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

**Выводы**
- Поведение аналогично двум односторонним связям OneToOne (см 1.2) 

## 1.3 Двусторонняя направленность (с mappedBy)

Java-код:
```
@Entity
@Table(name ="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Address address;

    ....
}

@Entity
@Table(name ="address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(mappedBy = "address",fetch = FetchType.LAZY)
    private User user;
   
    ....

}
```

DDL SQL:
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

### 1.3.1 Обращение через главную сущность
```
User user = userDAO.fincById(..).get()
user.getAddress().getXXX(..)
```

```
 select
        user0_.id as id1_1_0_,
        user0_.address_id as address_3_1_0_,
        user0_.name as name2_1_0_ 
    from
        public.user user0_ 
    where
        user0_.id=?

    select
        address0_.id as id1_0_0_,
        address0_.houseNumber as houseNum2_0_0_,
        address0_.street as street3_0_0_ 
    from
        public.address address0_ 
    where
        address0_.id=?
```

**Выводы:**
- Поиск осуществляется по PK таблиц "user" и "address"


### 1.3.2 Обращение через зависимую сущность
```
Address address = addressDAO.findById(ID).get();
address.getUser()
```
получается sql-запросы:
```
    select
            address0_.id as id1_0_0_,
            address0_.houseNumber as houseNum2_0_0_,
            address0_.street as street3_0_0_ 
        from
            public.address address0_ 
        where
            address0_.id=?
    
        select
            user0_.id as id1_1_0_,
            user0_.address_id as address_3_1_0_,
            user0_.name as name2_1_0_ 
        from
            public.user user0_ 
        where
            user0_.address_id=?
```

**Выводы**
- При работе в режиме LAZY разницы нет разницы что работать через DAO, что OneToOne(mappedBy=...).

# 2. ManyToMany

Связь реализуется через дополнительную таблицу (join table).

## 2.1 Ссылка только с одной сущности

Java-код:
```
@Entity
@Table(name ="student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany
    private List<Course> courseList;

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

DDL SQL:
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
       Student_id int8 not null,
        courseList_id int8 not null
    )
```

### 2.1.1 Обращение через главную сущность

```
List<Course> courseList= student1.getCourseList();
```

```
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
        courselist0_.Student_id=1
```

```
Hash Join  (cost=33.24..45.22 rows=6 width=540)
  Hash Cond: (course1_.id = courselist0_.courselist_id)
  ->  Seq Scan on course course1_  (cost=0.00..11.40 rows=140 width=524)
  ->  Hash  (cost=33.12..33.12 rows=9 width=16)
        ->  Seq Scan on student_course courselist0_  (cost=0.00..33.12 rows=9 width=16)
              Filter: (student_id = 1)
```

##### <a name="References"></a> Ссылки 
[One-to-One Relationship in JPA](https://www.baeldung.com/jpa-one-to-one)
[Basic Many-To-Many](https://www.baeldung.com/jpa-many-to-many)