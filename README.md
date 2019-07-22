---
tags: jpa, spring, boot
---
학습용 Spring Boot + JPA 환경 만들기
===

### 목표 
책 154쪽 부터 시작 하는 실전 예제 `1.요구사항 분석과 기본 매핑` 를 실습 해야하는데 책의 환경 구성은 올드한 면이 있다.


시작 하기전에 준비물로 로컬에 MySQL 설치가 필요 하다. 
MySQL 구성 설명은 생략 한다.
> id : root / password : 1 로 구성했다고 가정 한다.
> jpa_study 스키마가 있다고 가정 한다.



### 목차
진행 순서는 다음과 같다.
1. Boot 프로젝트 생성 (Initializer)
2. IDE import
3. Project run
4. EntityManager 샘플 만들기



## Boot 프로젝트 생성 (Initializer)
1. https://start.spring.io/ 접속
![Imgur](https://i.imgur.com/fZsx3TM.png)

2. maven Group 및 Artifact 입력
> 예제는 Group 은 dev.bonjugi 를, Artifact 는 my-study 를 입력 했다. 

3. Dependencies 에서 web, jpa, mysql, lombok 을 검색해서 추가

4. 하단에 Generate the project 누르면 프로젝트 zip파일이 다운로드 된다.


## IDE import
IDE로 프로젝트를 import 하고 maven compile 되는지 확인 한다.


## Project run
Spring Boot는 MySQL 을 의존성에 추가 해 주는것 만으로도 커넥션 풀을 만들려고 시도 한다.
때문에 MySQL datasource를 설정 하지 않으면 풀 생성에 실패 하므로 application.properties에 관련 설정을 추가 해준다.
```
# log level (com.bonjugi는 패키지명에 맞춰서)
logging.level.root=info
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=trace
logging.level.com.bonjugi=debug

# sql을 보여주되, 포맷 해서 보여주도록 한다.
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.use_sql_comments=true

# datasource
spring.datasource.url=jdbc:mysql://localhost:3306/jpa_study
spring.datasource.username=root
spring.datasource.password=1

# 하이버네이트 설정
spring.jpa.hibernate.ddl-auto=create

# Mysql 을 쓰기위한 방언 설정
spring.jpa.hibernate.use-new-id-generator-mappings=false
spring.jpa.database-platform=org.hibernate.dialect.MySQL5InnoDBDialect

```
> 은근슬쩍 datasource 외에도 이것저것 등록 했다.
> 각각 설정의 설명은 다음 기회에.

이제 run 해보면 콘솔에 찍히는 배너와 함께 잘 구동 되는 것을 확인 할수 있다.

IDE 에서 run 해도 되고, `mvn clean package` 로 jar파일을 만든 후, `java -jar 패키지이름.jar` 로 구동 시켜도 된다. 
> jar 패키징은 boot 의 초 강점 이다.

## EntityManager 샘플 만들기
책의 예제는 아직 spring-data-jpa 로 추상화된 repository를 이용하지 않고, EntityManager를 직접 사용하고 있다.
때문에 예제코드 처럼 코딩하기 위해 아래와 같이 샘플코드를 만들어 보겠다.

### 1. Member (Entity) 클래스 생성
/src/main/java 밑에, 기본패키지 (제 경우 dev.bonjugi.mystudy) 밑에, member.entity 패키지를 만든 후, 아래의 클래스 생성.


```java=
@Entity
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private String city;
    private String street;
    private String zipcode;

    public Member(String name) {
        this.name = name;
    }
}
```
> 참고로, hibernate 관련 어노테이션은 java.persistence 를 사용해야 한다.

### 2. MemberTest 테스트 생성
/src/test/java 밑에, 기본패키지 (제 경우 dev.bonjugi.mystudy) 밑에, member.entity 패키지를 만든 후, 아래의 클래스를 등록 한다.

> 사실 Member 엔티티를 테스트할 필요는 없다. 이미 hibernate에서 무수히 많은 테스트들이 있기 때문에.
> 우리는 메소드들과 친해지기 위해서 학습용으로 이리저리 써보기 위해 만드는 것 이다.

```java=
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Rollback(false)
public class MemberTest {

     @PersistenceContext
     private EntityManager em;

     @Test
     public void 등록후_조회(){

          // given
          Member bonjugi = new Member("bonjugi");
          em.persist(bonjugi);

          // when
          Member find = em.find(Member.class, bonjugi.getId());

          // then
          assertThat(find).isEqualTo(bonjugi);
     }
}
```
> 테스트 돌려보면 잘 동작한다.

간단히 설명하자면,

#### @RunWith(SpringRunner.class)
테스트는 러너와 함께 동작시킬수 있다. Junit 은 @Test들이 각각의 인스턴스로 매번 새롭게 생성 되는데, SpringRunner가 ApplicationContext를 계속 생성하지 않고 공유할수 있게 해준다.
[TDD 세미나 3부 - Spring 에서의 TDD
](https://hackmd.io/@bonjugi/rJP2UJwoE) 에서 좀더 자세하게 설명한적 있다.

#### @SpringBootTest
@ContextConfiguration 설정의 상위호환 개념.
Spring Test 에서applicationContext 들을 모두 로드해서 쓰기 위해 @ContextConfiguration을 쓴적 있다.
Boot 가 많은 컨벤션으로 눈에 보이지않는 의존성,설정들을 알아서 해주고 있기 때문에 이 어노테이션 만으로도 통합된 환경의 모든 설정들을 가져올수 있다. 대신 그만큼 무거워서 단위테스트에는 적합하지 않다.

이 외에도 @DataJpaTest, @WebMvcTest 등 Boot에서 지원하는 컨텍스설정 어노테이션들이 많은데 [Yun님 블로그](https://cheese10yun.github.io/spring-boot-test/) 에 잘 정리되어 있다.

#### @DataJpaTest (번외)
영속성 관련만 테스트 할거면 `@DataJpaTest` 를 써야하는데, 이유는 크게 다음과 같다.
- 통합환경을 모두 로드하지 않아서 비교적 가볍다.
- Embedded DB를 (h2) 사용할수 있다.
- @Transactional 이 기본으로 포함 되어 있다.

`@DataJpaTest` 는 중요한 개념이지만, 아직은 통합 환경에서 동작원리를 이해하는게 목적이므로 `@SpringBootTest` 를 사용 하겠다.

#### @Transactional
Spring은 Test시 `@Transactional` 이 걸려 있으면 자동으로 Rollback 시켜준다. 
> @Rollback(false) 로 디저블 할수도 있다.
테스트가 다른테스트에 영향을 주지 않기 위한 Junit의 노력이다.
참고로 @Transactional 이 클래스에 걸려있으면 모든 메소드에 @Transactional 한 것과 같다.

만약 @Transactional 이 없는 상태에서 em.persist(bonjugi) 를 수행하면 에러가 발생한다. 


```java
//@Transactional
```

```
javax.persistence.TransactionRequiredException
:No EntityManager with actual transaction available for current thread 
- cannot reliably process 'persist' call
```

#### @Rollback(false)
@Transactional 에서도 설명 했지만 자동 Rollback 기능을 디저블 한다. 패키지명이 org.springframework.test.annotation.test 인데, 경로만 봐도 테스트 전용임을 알수 있다.
우리 테스트 환경에서는 H2 인메모리 DB 가 아닌, Real DB를 사용 하여 결과물을 직접 봐야하므로 롤백하지 않도록 한다

#### @PersistenceContext
@Autowired 와 유사하다.
Spring에서 관리중인 EntityManager 을 주입하는건데, 차이는 `persistence-unit name` 을 지정할수 있다고 한다.
> 예시를 봐서는 데이터소스를 선택할수 있게 해주는듯 하다.
> 아직 unitName 속성을 활용하진 않겠지만 @Autowired보다 좀더 구체적인 주입 방식 이므로 되도록 @PersistenceContext 를 쓰자

```
<persistence-unit name="myUnit">
    <provider>org.hibernate.ejb.HibernatePersistence</provider>
<jta-data-source>java:/MYUnitDatasource</jta-data-source>
```

```java=
@PersistenceContext(unitName="myUnit")
```



#### assertThat()

```java=
assertThat(find).isEqualTo(bonjugi);
```
단정문은 요즘 대세인 [AssertJ](https://joel-costigliola.github.io/assertj/) 를 사용했다.
Spring Boot Test 의존성에 들어 있어, 별도의 추가 없이 사용 가능하다.
