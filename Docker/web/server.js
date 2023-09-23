const express = require('express');
const mysql = require('mysql2');
const app = express();
app.use('/static', express.static(__dirname + '/_include'));
app.use(express.json());

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
      console.log('MySQL DB 연결 성공');
    }
});

// request 이벤트 리스너를 설정합니다.
app.get('', (request, response) => {
    response.sendFile(__dirname + "/index.html");
});

app.get('/getData', (request, response) => {
    //날짜 원하는 날짜로 변경 and 최소한의 정렬 필요
    var date = request.query.date;

    const regex = /^\d{8}$/;
    const isMatch = regex.test(date);
    if (!isMatch) {
        console.log("INVALID VALUE : " + date);
        response.status(404).send('비정상적인 데이터 입력');
        return;
    }

    db.query('SELECT * FROM INS' + date, (err, results) => {
        if (err) {
            console.error("Data No Exist : " + date);
            response.status(200).json([]);
            return;
        }
        console.log("Data Response : " + date);
        response.status(200).json(results);
    });
  });

const port = process.env.WEB_SERVICE_PORT;
app.listen(port, () => {
    console.log(`Server running at http://127.0.0.1:${port}`);
});
