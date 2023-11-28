import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MANAGER {
	static Vector<HOMEPAGE> pagelist; //홈페이지 배열
	static String date = "2023-09-17";	//for HOMEPAGE Parser
	static String sqldate = "20230917";	//for SQL Query

	Connection conn;

	void Upload() {
		try {
			java.sql.Statement stmt = conn.createStatement();
			
			//If not exist today's table, Create table
			String sqlQuery = "CREATE TABLE IF NOT EXISTS INS" + sqldate + "(url VARCHAR(200) PRIMARY KEY NOT NULL, category TINYTEXT, number INT, title TINYTEXT, uploader TINYTEXT, timestamp DATETIME);";
			stmt.execute(sqlQuery);
			PreparedStatement pstmt = null;
			int item_count = 0;
			//insert item to table
	        String sql = "REPLACE INTO INS" + sqldate + "(url, category, number, title, uploader, timestamp) VALUES (?, ?, ?, ?, ?, (now()));";
			for(HOMEPAGE page : pagelist) {
				Iterator<ITEM> seek = page.itemlist.iterator();
				while(seek.hasNext()) {
					item_count++;
					ITEM item = seek.next();
					pstmt = conn.prepareStatement(sql);
				    pstmt.setString(1, page.url+item.url);
				    pstmt.setString(2, page.category);
				    pstmt.setInt(3, item.num);
				    pstmt.setString(4, item.title);
				    pstmt.setString(5, item.uploader);
				    pstmt.executeUpdate();
				}
			}
			stmt.close();
			Logwriter("MANAGER::Upload", "Upload Complete (upload size : " + item_count + ")");
			if (pstmt != null && !pstmt.isClosed())
                 pstmt.close();
		}
		catch (SQLException e) {
            e.printStackTrace();
        }
		catch (Exception e) {
			Logwriter("MANAGER::Upload", "<Exception>");
			System.out.println(e);
		}
	}
	void ValidationCheck() {
		//DB의 모든 URL을 리스트로 가져오고, Upload할 때 마다 제거하는 식으로 검증
		//리스트에 남아있는 것은 삭제된 게시글 -> DB에서 삭제.
		Vector<String> list = new Vector<String>(); 
        try{
			java.sql.Statement stmt = conn.createStatement();
			String sql = "SELECT url FROM INS" + sqldate + ";";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				String url = rs.getString(1);
				list.add(url);
			}
			
			for(HOMEPAGE page : pagelist) {
				Iterator<ITEM> seek = page.itemlist.iterator();
				while(seek.hasNext()) {
					ITEM item = seek.next();
					String url = page.url + item.url;
					if(list.contains(url)) {
						list.remove(url);
					}
				}
			}
			
			//List에 남아 있는 것은 삭제된 게시글이므로, DB에서 DELETE 수행
			int deleteSize = list.size();
			for(String url : list) {
				stmt.execute("DELETE FROM INS" + sqldate + " WHERE url='" + url +"';");		
			}
			stmt.close();
			Logwriter("MANAGER::ValidationCheck", "ValidationCheck Complete (remove size : " + deleteSize + ")");

        }
		catch(SQLException e){
            e.printStackTrace();
        }
	}	
	void CreateConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String ADDR = System.getenv("INS_MYSQL_ADDR");
			String ID = System.getenv("INS_MYSQL_ID");
			String PASSWORD = System.getenv("INS_MYSQL_PW");
			if(ADDR == null || ID == null || PASSWORD == null) {
		    	Logwriter("MANAGER::CreateConnection", "Check Environment Variable(DB)");
		    	System.exit(1);
			}
			conn = DriverManager.getConnection("jdbc:mysql://"+ ADDR, ID, PASSWORD);
	    	Logwriter("MANAGER::CreateConnection", "DB connection successful");
	    	return;
		}
	    catch (com.mysql.cj.jdbc.exceptions.CommunicationsException e) {
	    	Logwriter("MANAGER::CreateConnection", "CommunicationsException : Check DB Connection Information");
	        System.out.println(e);
	    }
	    catch (ClassNotFoundException e) {
	    	Logwriter("MANAGER::CreateConnection", "ClassNotFoundException : Failed to load JDBC driver");
	        System.out.println(e);
	    }
		catch (Exception e) {
			Logwriter("MANAGER::CreateConnection", "Exception");
			System.out.println(e);
		}
		Logwriter("MANAGER::CreateConnection", "TERMINATE");
		System.exit(1);

	}
	
	int GetNotificationSize() {
		int count = 0;
		for(HOMEPAGE page : pagelist) {
			count += page.itemlist.size();
		}
		return count;		
	}
	
	void Getnotice() {
		// 스레드 풀의 크기. 최소 1개 ~ 최대 8개
		int threadCount = 4;
		String strThreadCount = System.getenv("INS_THREAD_NUMBER");
		if(strThreadCount != null) {
			threadCount = Integer.parseInt(strThreadCount);
		}
		if(threadCount < 1 || threadCount > 8) {
			threadCount = 4;
	        Logwriter("MANAGER::Getnotice", "Number of threads limit exceeded. Set thread Number as 4");
		}
        ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);

        //작업을 스레드 풀에 제출
        for (HOMEPAGE page : pagelist) {
            threadPool.submit(new Runnable() {
                @Override
                public void run() {
                	page.Load();
                }
            });
        }
        //새로운 작업이 submit되는 것을 방지하고, 스레드 풀이 종료될 때 까지 대기
        //대기하지 않을 경우, 공지사항이 다 로드되기 전에 DB에 저장하는 단계가 진행됨.
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 스레드 풀을 완전히 종료
        threadPool.shutdownNow();
        
        //타임아웃에 따른 미처리된 홈페이지 파싱; 하나의 스레드에서 담당함.
        while(true) {
        	//만약 모든 홈페이지의 공지사항이 로드되었다면, 해당 반복문을 종료
        	boolean check = true;
        	for (HOMEPAGE page : pagelist) {
        		check = check && page.loadComplete;
        	}
        	if(check == true)
        		break;
        	
        	//로드가 안된 공지사항이 존재한다면 다시 로드를 시도함.
            for (HOMEPAGE page : pagelist) {
            	if(page.loadComplete == true)
            		continue;
                page.Load();
            }
            //타임아웃 시간을 2배씩 증가하면서 반복 수행
            HOMEPAGE.timeOut *= 2;
        }
        
        Logwriter("MANAGER::Getnotice", "Number of loaded : " + GetNotificationSize());
        
	}
	void Printnotice() {
        Logwriter("MANAGER::Printnotice", "Notice Size : " + GetNotificationSize());
		for(HOMEPAGE page : pagelist) 
			page.Print();
	}
	MANAGER(String filename){
		try{
			Setdate();
			pagelist = new Vector<HOMEPAGE>();
            FileReader filereader = new FileReader(filename);
            BufferedReader bufReader = new BufferedReader(filereader);
            String line = "";
            while((line = bufReader.readLine()) != null){
            	//Except blank line or comment
            	if(line.compareTo("") == 0 || line.charAt(0) == '#') continue;
            	String info[] = line.split(","); //CSV (Comma-separated values)
            	pagelist.add(new HOMEPAGE(info[0], info[1]));
                Logwriter("MANAGER", "Append URL : " + info[1] + "(" + info[0] + ")");
            }
            Logwriter("MANAGER::Constructor", "Number of URLs read : " + pagelist.size());
            bufReader.close();
            filereader.close();
            //CreateConnection();
            return;
        }
		catch (FileNotFoundException e) {
            Logwriter("MANAGER::Constructor", "<FileNotFoundException>Check URL file name!");
            System.out.println(e);
        }
		catch(IOException e){
            Logwriter("MANAGER::Constructor", "<IOException>");
            System.out.println(e);
        }
        Logwriter("MANAGER::Constructor", "TERMINATE");
		System.exit(1);

	}
	void Run() throws InterruptedException {
		int interval = 300000;
		String strInterval = System.getenv("INS_INTERVAL");
		if(strInterval != null) {
			interval = Integer.parseInt(strInterval);
		}
		while(true) {
			Setdate();
			Getnotice();
			//Upload();
			//ValidationCheck();
			Thread.sleep(interval);
		}
	}
	
	void Setdate() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		date = sdf.format(now);
		sqldate = sdf2.format(now);
		Logwriter("MANAGER::Setdate", "Set date to " + date);
	}
	static String Gettime() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(now);
	}
	static void Logwriter(String name, String msg) {
        System.out.println(Gettime() + " | [" + name + "] "+msg);
	}
	
}