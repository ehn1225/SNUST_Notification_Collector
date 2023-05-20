import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.*;
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
			String temp[] = new String[10];
			temp = doc.title().split(" ");
			category = temp[temp.length - 1];
			Makeitem(doc.select("tr.body_tr"), 1);
			MANAGER.totalnotice += itemlist.size();
			MANAGER.Logwriter("HOMEPAGE::Load", category + '(' + itemlist.size() + ')');
		}
		catch(Exception e){
			MANAGER.Logwriter("HOMEPAGE::Load", "<Exception> " + url);
			System.out.println(e);
		}
	}
	
	void Makeitem(Elements body, int depth){
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
				if (MANAGER.date.compareTo(date) != 0) continue; //if upload date is not today, drop
				if(depth != 1 && number == "") continue;//ignore second page notification
				
				//Save Item
				itemlist.add(new ITEM(number,title,uploader,itemurl));
				
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
