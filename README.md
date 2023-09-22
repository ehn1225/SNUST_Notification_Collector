﻿# SNUST(Seoul National University of Science and Technology) Notification Collector

### 서울과학기술대학교의 메인 홈페이지와 부속(학과)홈페이지 130여개의 게시판을 파싱하는 프로그램입니다.
- 서울과학기술대학교의 메인 홈페이지 및 부속 홈페이지에 올라오는 공지사항과 게시판을 주기적으로 파싱하여 최신 게시글만 보여줍니다.
- 다수의 게시판에 올라온 공지사항을 간편하게 확인할 수 있으며, 교육 프로그램 참가자 모집 등 신속함을 요구하는 공지사항을 빠르게 확인할 수 있습니다.
- Docker를 이용하여 배포함으로써 누구나 쉽게 자신만의 서버를 생성할 수 있으며, 원하는 게시판만 파싱하도록 설정할 수 있습니다.

## 기능
- 공지사항 파싱 기능
- 원하는 게시판만 파싱하도록 설정하는 기능
- 파싱 주기를 설정할 수 있는 기능
- 공지사항이 삭제될 경우, 삭제된 게시글을 DB에서 삭제하는 기능
- 원하는 일자, 원하는 게시판의 게시글을 볼 수 있는 기능 (구현예정)

## 개발 환경 및 사용한 기술
- Windows 11 x64
- Eclipse IDE for Java Developers - 2023-03
- JavaSE-11
  - json-simple-1.1.1
  - jsoup-1.15.4
  - mysql-connector-j-8.0.32
- Ubuntu 22.04 (Back-End)
  - Docker 24.0.5
  - mysql(image) : 8.0.33
  - Node.js v18.18.0

## 실행 방법
- 도커 설치 방법
  - ```sudo apt install docker.io```
- 도커 권한 부여
  - ```sudo usermod -aG docker $USER && newgrp docker```
- 도커 컴포즈 설치
  - ```sudo apt install docker-compose```
- 도커 실행
  - ```docker-compose up -d```
- 도커 종료
  - ```docker-compose down```
- 도커 운영 상태 확인
  - ```docker-compose ps```
- 프로젝트 재빌드
  - ```docker-compose up --build --force-recreate -d```
- mysql 직접 접속
  - ```mysql -h 127.0.0.1 -P 3306 -u root -p``` (비밀번호는 docker-compose.yml 파일에 기재되어 있음)
  - ```docker-compose.yml``` db에 ports 항목 추가 (포트 3306:3306)

## 과거 프로젝트
- WEB(1차, 2차) : [Intelligent Notification Server(INS)](https://github.com/ehn1225/Projects/tree/master/Intelligent_Notification_Server(INS))
- 윈도우_1차 : [information_receiver](https://github.com/ehn1225/Projects/tree/master/SeoulTech_Notice_1st_Gen)
- 윈도우_2차 : [SeoulTech_Notice_2Gen](https://github.com/ehn1225/Projects/tree/master/SeoulTech_Notice_2nd_Gen)

   
## 실행 화면
- 추가예정
