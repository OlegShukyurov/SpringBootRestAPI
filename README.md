# Weather Sensor REST API
____

REST API сервис принимающий данные от метеорологического датчика (для простоты буду называть его просто "сенсор").
Который измеряет температуру окружающего воздуха и может определять, идет дождь или нет. Каждый раз когда сенсор
делает очередное измерение, он отправляет HTTP запрос с данными в формате JSON на наш сервер, чтобы мы могли сохранять 
эти данные в БД и в дальнейшем их анализировать.
Проект выполнен в рамках [данного курса](https://www.udemy.com/course/spring-alishev/).

## Getting started
____

Эти инструкции помогут вам запустить копию проекта на вашем локальном компьютере для целей разработки и тестирования.

### Prerequisites

+ **Java 8+**
+ **Apache Maven 3.x**

### Installing

Клонировать проект

```bash
  git clone https://github.com/OlegShukyurov/SpringBootRestAPI
```

Собрать проект, запустить тесты

```bash
  mvn clean package
```

После успешной сборки запустить одним из двух способов

```bash
  java -jar target/SpringBootRestAPI.jar
```  
  или
```bash  
  mvn spring-boot:run
```

## Technical specifications
____

<p>
    <img src="https://github.com/OlegShukyurov/SpringBootRestAPI/blob/master/src/main/resources/static/tz1.png" alt="Фотография 1" width="400" height="330">
    <img src="https://github.com/OlegShukyurov/SpringBootRestAPI/blob/master/src/main/resources/static/tz2.png" alt="Фотография 2" width="400" height="330">
    <img src="https://github.com/OlegShukyurov/SpringBootRestAPI/blob/master/src/main/resources/static/tz3.png" alt="Фотография 3" width="400" height="330">
    <img src="https://github.com/OlegShukyurov/SpringBootRestAPI/blob/master/src/main/resources/static/tz4.png" alt="Фотография 4" width="400" height="330">
</p>

Весь функционал реализован в полном объеме.
Помимо основного задания, проект покрыт юнит и интеграционными тестами использующими H2 базу данных с активным профилем "dev"

## Screenshots
____
Для демонстрации реализовал клиентское приложение, чтобы не прикладывать скрины из Postman.
Зарегистрировал новый сенсор, добавил 1000 рандомных измерений (как описано в ТЗ), получил все измерения, преобразовал тело ответа в список и
вывел в консоль, вместе с количеством дождливых дней.

<p>
    <img src="https://github.com/OlegShukyurov/SpringBootRestAPI/blob/master/src/main/resources/static/demo2.png" alt="Фотография 1" width="500" height="330">
    <img src="https://github.com/OlegShukyurov/SpringBootRestAPI/blob/master/src/main/resources/static/demo.png" alt="Фотография 2" width="500" height="330">
</p>

## Build with
____
+ ***Java 11 SE*** 
+ ***Apache Maven***
+ ***Apache Tomcat***
+ ***Hibernate***, ***PostgreSQL***, ***H2***
+ ***Spring Core***, ***Spring MVC***, ***Spring Data JPA***, ***Spring Boot***, ***Spring Web***
+ ***JUnit***, ***Mockito***




