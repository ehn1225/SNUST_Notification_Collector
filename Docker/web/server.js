const express = require('express');
const mysql = require('mysql2');
const app = express();
app.use('/static', express.static(__dirname + '/_include'));
app.use(express.json());

function formatDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  }

function LogWriter(message){
    const timestamp = new Date();
    const logMessage = `${formatDate(timestamp)} | ${message}`;
    console.log(logMessage);
}

const db = mysql.createConnection({
    host: process.env.INS_MYSQL_ADDR,
    user: process.env.INS_MYSQL_ID,
    password: process.env.INS_MYSQL_PW,
    database: process.env.MYSQL_DATABASE
});

db.connect((err) => {
    if (err) {
      console.error('MySQL DB 연결 오류:', err);
      process.exit();
    } else {
        LogWriter("MySQL DB 연결 성공")
    }
});

// request 이벤트 리스너를 설정합니다.
app.get('', (request, response) => {
    const clientIP = request.ip; 
    LogWriter(`${clientIP} Access index.html`)
    response.sendFile(__dirname + "/index.html");
});

app.get('/getData', (request, response) => {
    //날짜 원하는 날짜로 변경 and 최소한의 정렬 필요
    var date = request.query.date;
    const clientIP = request.ip; 
    LogWriter(`${clientIP} Request ${date} data`)

    const regex = /^\d{8}$/;
    const isMatch = regex.test(date);
    if (!isMatch) {
        console.log("INVALID VALUE : " + date);
        LogWriter(`${clientIP} Input INVALID VALUE : ${date}`)
        response.status(400).send('Bad Request');
        return;
    }

    db.query('SELECT * FROM INS' + date, (err, results) => {
        if (err) {
            LogWriter(`${clientIP} Request ${date} data, but Data No Exist`)
            response.status(200).json([]);
            return;
        }
        LogWriter(`${clientIP} Request ${date} data, Response Success`)
        response.status(200).json(results);
    });
  });

const port = process.env.WEB_SERVICE_PORT;
app.listen(port, () => {
    LogWriter(`Server running at http://127.0.0.1:${port}`)
});
