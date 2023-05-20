public class MAIN {
	public static void main(String[] args) {
		MANAGER.Logwriter("INS_MAIN", "START");
		try{
			MANAGER manager = new MANAGER("URLs.txt");//URL이 저장된 파일명
			manager.Run();
		}
		catch (Exception e) {
			System.out.println(e);
		}
		MANAGER.Logwriter("INS_MAIN", "TERMINATE");
	}
}
