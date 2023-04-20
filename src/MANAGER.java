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
	static String date = "";
	Connection conn;

	void Upload() {
		try {
			java.sql.Statement stmt = conn.createStatement();
	        
			//오늘 날짜로 된 테이블이 없다면 생성	        
	        String sqlQuery = "CREATE TABLE IF NOT EXISTS " + date + 
	        		"(url TEXT NOT NULL PRIMARY KEY, category TEXT, number INTEGER, title TEXT, uploader TEXT, timestamp TEXT);";
			stmt.execute(sqlQuery);
			
			PreparedStatement pstmt = null;
	        String sql = "INSERT INTO " + date + "(url, category, number, title, uploader, timestamp) VALUES "
	        		+ "(?, ?, ?, ?, ?, (datetime('now', 'localtime')) WHERE NOT EXISTS(SELECT URL FROM " + date + " WHERE URL=?);";
			
			for(HOMEPAGE page : pagelist) {
				Iterator<ITEM> seek = page.itemlist.iterator();
				while(seek.hasNext()) {
					ITEM item = seek.next();
					 pstmt = conn.prepareStatement(sql);
				     pstmt.setString(1, page.url+item.url);
				     pstmt.setString(2, page.category);
				     pstmt.setInt(3, item.num);
				     pstmt.setString(4, item.title);
				     pstmt.setString(5, item.uploader);
				     pstmt.setString(6, page.url+item.url);
				     pstmt.executeUpdate();
				}
			}		
			stmt.execute("Replace into UpdateTime values(datetime('now', 'localtime'))");		
			stmt.close();
			Logwriter("MANAGER::Upload", "Complete sql upload.");
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
		//DB의 모든 URL 리스트 순회하면서 URL에 접속되는지 -> 조회수가 증가할 수 있음
		//DB의 모든 URL을 리스트로 가져오고, Upload할 때 마다 제거하는 식으로 검증
		//리스트에 남아있는 것은 삭제된 게시글 -> DB에서 삭제.
		Vector<String> list = new Vector<String>(); 
        try{
			java.sql.Statement stmt = conn.createStatement();
			String sql = "SELECT url FROM " + date;
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				String url = rs.getString(1);
				list.add(url);
			}
			
			for(HOMEPAGE page : pagelist) {
				Iterator<ITEM> seek = page.itemlist.iterator();
				while(seek.hasNext()) {
					ITEM item = seek.next();
					if(list.contains(item.url)) {
						list.remove(item.url);
					}
				}
			}
			
			//List에 남아 있는 것은 삭제된 게시글이므로, DB에서 DELETE 수행
			int deleteSize = list.size();
			for(String url : list) {
				stmt.execute("DELETE FROM " + date + " WHERE url='" + url +"';");		
			}
			stmt.close();
			Logwriter("MANAGER::ValidationCheck", "ValidationCheck Complete(" + deleteSize + "item removed)");

        }
		catch(SQLException e){
            e.printStackTrace();
        }
	}	
	void CreateConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String ADDR = "192.168.12.12";
			String ID = "";
			String PASSWORD = "";
			conn = DriverManager.getConnection("jdbc:mysql://"+ ADDR, ID, PASSWORD);
	    	Logwriter("MANAGER::CreateConnection", "DB connection successful");
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
        }catch (FileNotFoundException e) {
            Logwriter("MANAGER::Constructor", "<FileNotFoundException>Check URL file name!");
            System.out.println(e);
			System.exit(1);
        }
		catch(IOException e){
            Logwriter("MANAGER::Constructor", "<IOException>");
            System.out.println(e);
			System.exit(1);
        }
	}
	void Run() throws InterruptedException {
			Setdate();
			Getnotice();
			Upload();
			ValidationCheck();
	}
	void Setdate() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		date = sdf.format(now);
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
