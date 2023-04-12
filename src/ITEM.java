public class ITEM {
	int num = 0;
	String title = "";
	String uploader = "";
	String url = "";
	ITEM(String num, String title, String uploader, String url){
		this.num =  num.length()== 0 ? 0 : Integer.parseInt(num);
		this.title = title;
		this.uploader = uploader;
		this.url = url;
	}
}
