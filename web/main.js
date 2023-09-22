const express = require('express');
const mysql = require('mysql2');
const app = express();
app.use('/static', express.static(__dirname + '/_include'));
app.use(express.json());

const db = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'qwer1234',
    database: 'INS',
});

db.connect((err) => {
    if (err) {
      console.error('MySQL 연결 오류:', err);
      process.exit();
    } else {
      console.log('MySQL 연결 성공');
    }
});

// request 이벤트 리스너를 설정합니다.
app.get('', (request, response) => {
    response.sendFile(__dirname + "/index.html");
});

app.get('/getData', (request, response) => {
    //날짜 원하는 날짜로 변경 and 최소한의 정렬 필요
    var date = request.query.date;
    date = "INS" + (date ? date : '20230922');
    console.log(date);

    //테이블명은 prepare statment를 적용할 수 없음.
    //직접 필터링해야함
    db.query('SELECT * FROM ' + date, (err, results) => {
        if (err) {
            console.error('해당 일자의 공지사항 테이블이 없습니다. (' + date + ')');
            response.status(200).json([]);
            return;
        }
        response.status(200).json(results);
    });
  });

const port = 80;
app.listen(port, () => {
    console.log(`Server running at http://127.0.0.1:${port}`);
});
