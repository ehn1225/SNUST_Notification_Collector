import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.*;
import java.util.Iterator;
import java.util.Vector;

public class HOMEPAGE {
	String url="";
	String category = "";
	Vector <ITEM> itemlist = new Vector<ITEM>();
	
	HOMEPAGE(String URL){
		this.url = URL;
	}
	
	void Load(){
		try {
			itemlist.clear();
			Document doc = Jsoup.connect(url).get();
			category = doc.title(); //.replace("서울과학기술대학교 ", "").replace("- ", "");
			Makeitem(doc.select("tr.body_tr"), 1);
			MANAGER.totalnotice += itemlist.size();
		}
		catch(Exception e){
			MANAGER.Logwriter("HOMEPAGE::Load", "<Exception> " + url);
			System.out.println(e);
		}
	}
	
	void Makeitem(Elements body, int depth){
		ITEM item;
		int count = body.size();
		try {
			for(int i = 0; i < count; i++) {
				Elements temp = body.get(i).select("td");
				int size = temp.size(); //6이 정상
				if(size != 6) continue;
				String number = temp.select(".dn1").text();
				String itemurl = body.get(i).select("a").attr("href");
				String title = temp.select(".dn2").text();
				String uploader = temp.select(".dn4").text();
				String date = temp.select(".dn5").text();
				if (MANAGER.date.compareTo(temp.get(4).text()) != 0) continue; //if upload date is not today, drop
				if(depth != 1 && number == "") continue;//ignore second page notification
				
				//Save Item
				item = new ITEM(number,title,uploader,itemurl);
				itemlist.add(item);
				
				//are there more notifications left? repeat next page
				//for safety, max depth = 5
				if( i == count -1 && depth < 5) {
					String suburl = url + "?do=list&page=" + (depth + 1);
					Document newdoc = Jsoup.connect(suburl).get();
					Makeitem(newdoc.select("tr.body_tr"), depth+1);
	         	}				
			}
		}
 		catch(Exception e){
			MANAGER.Logwriter("HOMEPAGE::Makeitem", "<Exception> " + url);
			System.out.println(e);
 		}
	}
	
	void Print(){
		for(ITEM item : itemlist)
			MANAGER.Logwriter("HOMEPAGE::Print", category + " | " + item.num + " | " + item.title + " | " + item.uploader + " | " + item.url);
	}
}
