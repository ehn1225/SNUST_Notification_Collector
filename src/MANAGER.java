import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class MANAGER {
	static int totalnotice = 0; //아이템의 개수
	Vector<HOMEPAGE> pagelist; //페이지 배열
	static String date = "";
	//WEATHER weather;

	void Upload() {
		//DB 최신화 로직 필요
		//또한, 매일 새로운 테이블로 구분함으로써 과거의 공지도 볼 수 있게하기
		//날짜별 테이블 생성 및 truncate 대신 replace와 유효성 검사.
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn;
			final String ADDR = ""; // Ex)192.168.0.122:3306/NOTICE
			final String ID = ""; //Insert your database account
			final String PASSWORD = "";
			conn = DriverManager.getConnection("jdbc:mysql://"+ ADDR, ID, PASSWORD);
			
	        java.sql.Statement stmt = conn.createStatement();
			stmt.execute("TRUNCATE TABLE notice");
			PreparedStatement pstmt = null;
	        String sql = "insert into notice values(?,?,?,?,?,?);";
			
			int i = 1;
			for(HOMEPAGE page : pagelist) {
				Iterator<ITEM> seek = page.itemlist.iterator();
				while(seek.hasNext()) {
					ITEM item = seek.next();
					 pstmt = conn.prepareStatement(sql);
				     pstmt.setInt(1, i++); //auto increse 사용하거나 없애기.
				     pstmt.setString(2, page.category);
				     pstmt.setInt(3, item.num);
				     pstmt.setString(4, item.title);
				     pstmt.setString(5, item.uploader);
				     pstmt.setString(6, page.url+item.url);
				     pstmt.executeUpdate();
				}
			}		
			stmt.execute("TRUNCATE TABLE noticeuptime");
			stmt.executeUpdate("insert into noticeuptime values('" + Gettime() + "')");		
			stmt.close();
			Logwriter("MANAGER::Upload", "Complete sql upload.");
			if (pstmt != null && !pstmt.isClosed())
                 pstmt.close();
		}
	    catch (ClassNotFoundException e) {
	    	Logwriter("MANAGER::Upload", "<ClassNotFoundException>");
	        System.out.println(e);
	    }
		catch (SQLException e) {
            e.printStackTrace();
        }
		catch (Exception e) {
			Logwriter("MANAGER::Upload", "<Exception>");
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
			//weather = new WEATHER();
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
			//weather.Getweather();
			//weather.GetAirQuality();
			//weather.Upload();
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
