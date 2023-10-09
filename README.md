# SNUST(Seoul National University of Science and Technology) Notification Collector

### 서울과학기술대학교의 메인 홈페이지와 부속(학과)홈페이지 130여개의 게시판을 파싱하는 프로그램입니다.
- 서울과학기술대학교의 메인 홈페이지 및 부속 홈페이지에 올라오는 공지사항과 게시판을 주기적으로 파싱하여 최신 게시글만 보여줍니다.
- 다수의 게시판에 올라온 공지사항을 간편하게 확인할 수 있으며, 교육 프로그램 참가자 모집 등 신속함을 요구하는 공지사항을 빠르게 확인할 수 있습니다.
- Docker를 이용하여 배포함으로써 누구나 쉽게 자신만의 서버를 생성할 수 있으며, 자신이 원하는 게시판만 파싱하도록 설정할 수 있습니다.

## 기능
- 130여개의 공지사항 게시판 게시글 파싱 기능
- 원하는 게시판만 파싱하도록 설정하는 기능
- 공지사항이 삭제될 경우, 삭제된 게시글을 DB에서 삭제하는 기능
- 원하는 일자의 게시글을 조회할 수 있는 기능

## 개발 환경 및 사용한 기술
- Windows 11 x64
- Eclipse IDE for Java Developers - 2023-03
- JavaSE-11
  - [jsoup-1.15.4](https://jsoup.org/news/release-1.15.4)
  - [mysql-connector-j-8.0.32, JDBC](https://www.mysql.com/products/connector/)
- Ubuntu 22.04 (Back-End)
  - Docker 24.0.5
  - mysql(image) latest (개발 시점 : 8.0.33)
  - Node(image) 18.18-alpine
  - java(image) openjdk11:alpine-jre

## Java 프로그램 빌드 방법
- Ecilpse IDE를 이용하여 프로젝트 로드
- file -> export -> Java/Runnable JAR file 선택 후 finish 클릭

## Docker 설치 방법
- Docker 설치
  - ```sudo apt install docker.io```
- Docker 권한 부여
  - ```sudo usermod -aG docker $USER && newgrp docker```
- Docker Compose 설치
  - ```sudo apt install docker-compose```

## 실행 방법
- 프로젝트 실행
  - ```SNUST_Notification_Collector\Docker``` 경로로 이동
  - ```docker-compose up -d```; [-d] : 백그라운드 실행
- 프로젝트 종료
  - ```docker-compose stop``` 또는 ```docker-compose down```
- 프로젝트 동작 상태 확인
  - ```docker-compose ps```
- 프로젝트 다시 빌드
  - ```docker-compose up --build --force-recreate -d```
- mysql 직접 접속
  - ```mysql -h 127.0.0.1 -P 3306 -u root -p```
  - ```docker-compose.yml``` 파일에서 db의 ```ports``` 주석 해제

## docker-compose 환경변수
### MYSQL
- ```MYSQL_ROOT_PASSWORD``` : mysql DB root 계정 비밀번호
- ```MYSQL_DATABASE``` : 사용할 DB 이름
- ```ports``` : 직접 DB로 접속할 때 사용할 포트

### JAVA
- ```INS_MYSQL_ADDR``` : mysql DB의 주소와 포트, 데이터베이스 이름
- ```INS_MYSQL_ID``` : mysql DB의 계정 ID
- ```INS_MYSQL_PW``` : mysql DB의 계정 비밀번호
- ```INS_INTERVAL```  : 파싱 주기(초 단위)

### node.js
- ```INS_MYSQL_ADDR``` : mysql DB의 주소
- ```INS_MYSQL_ID``` : mysql DB 접속에 사용할 ID
- ```INS_MYSQL_PW``` : mysql DB 접속에 사용할 비밀번호
- ```MYSQL_DATABASE``` : 사용할 DB 이름
- ```WEB_SERVICE_PORT``` : node.js 웹 서비스 포트


## 과거 프로젝트
- WEB(1차, 2차) : [Intelligent Notification Server(INS)](https://github.com/ehn1225/Projects/tree/master/Intelligent_Notification_Server(INS))
- 윈도우_1차 : [information_receiver](https://github.com/ehn1225/Projects/tree/master/SeoulTech_Notice_1st_Gen)
- 윈도우_2차 : [SeoulTech_Notice_2Gen](https://github.com/ehn1225/Projects/tree/master/SeoulTech_Notice_2nd_Gen)

   
## 실행 화면
- <img src="https://github.com/ehn1225/SNUST_Notification_Collector/assets/5174517/a3139c4a-bddc-49b2-af96-6b5d17d0c1d4" width="700"/>
- <img src="https://github.com/ehn1225/SNUST_Notification_Collector/assets/5174517/200a72c4-7e93-4f54-a258-78d25926e568" width="700"/>