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

public class MANAGER {
	static int totalnotice = 0; //아이템의 개수
	Vector<HOMEPAGE> pagelist; //홈페이지 배열
	static String date = "2023-05-04";	//for HOMEPAGE Parser
	static String sqldate = "20230504";	//for SQL Query

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
			String ADDR = System.getenv("INS_MYSQL_ADDR");		//"100.94.42.47:3306/INS";
			String ID = System.getenv("INS_MYSQL_ID");			//"root";
			String PASSWORD = System.getenv("INS_MYSQL_PW");	//"qwer1234";
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
	void Getnotice() {
		totalnotice = 0;
		for(HOMEPAGE page : pagelist)
			page.Load();
        Logwriter("MANAGER::Getnotice", "Number of loaded : " + totalnotice);
	}
	void Printnotice() {
        Logwriter("MANAGER::Printnotice", "Total : " + totalnotice);
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
            	pagelist.add(new HOMEPAGE(line));
                //Logwriter("MANAGER", "Append URL : " + line);
            }
            Logwriter("MANAGER::Constructor", "Number of URLs read : " + pagelist.size());
            bufReader.close();
            filereader.close();
            CreateConnection();
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
			Upload();
			ValidationCheck();
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
