# Jpa Relations

Разбираемся с отображением jpa-аннотаций @OneToOne, @ManyToOne, @OneToMany, @JoinColum, @OrderColumn, 
их отображением на реляционную БД и формируемыми sql-запросами.

# 1. OneToOne

## 1.1 Двусторонняя направленность (с mappedBy)

Java-код:
```
@Entity
@Table(name ="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
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

### 1.1.1 <a name="OneToOneMain"></a> Обращение через главную сущность
```
User user = ...
user.getAddress()
```

```
select
        user0_.id as id1_1_0_,
        user0_.address_id as address_3_1_0_,
        user0_.name as name2_1_0_,
        address1_.id as id1_0_1_,
        address1_.houseNumber as houseNum2_0_1_,
        address1_.street as street3_0_1_ 
    from
        public.user user0_ 
    left outer join
        public.address address1_ 
            on user0_.address_id=address1_.id 
    where
        user0_.id=1
```
Поиск осуществляется по PK таблицы "user"

### 1.1.2 Обращение через зависимую сущность
```
Address address = ...
address.getUser()
```
получается sql-запрос:
```
select
        user0_.id as id1_1_1_,
        user0_.address_id as address_3_1_1_,
        user0_.name as name2_1_1_,
        address1_.id as id1_0_0_,
        address1_.houseNumber as houseNum2_0_0_,
        address1_.street as street3_0_0_ 
    from
        public.user user0_ 
    left outer join
        public.address address1_
            on user0_.address_id=address1_.id 
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

## 1.2 Односторонняя направленность
Java-код:
```
@Entity
@Table(name ="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
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

DDL SQL **не меняется!!**
(см п1.1)

### 1.2.1 Обращение через главную сущность
Аналогично п 1.1.1

### 1.2.2 Поиск через DAO
```
Address address = ...
User user = userDAO.findByAddress(address).get();
```
получается sql-запрос:
```
 select
        user0_.id as id1_1_,
        user0_.address_id as address_3_1_,
        user0_.name as name2_1_ 
    from
        public.user user0_ 
    where
        user0_.address_id=?
```

##### <a name="References"></a> Ссылки 
[One-to-One Relationship in JPA](https://www.baeldung.com/jpa-one-to-one)