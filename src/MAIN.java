import java.util.Scanner;

public class MAIN {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage	: INS_SERVER [Mode]");
			System.err.println("Mode	: 0 : Auto, 1 : Manually");
			System.err.println("Sample	: INS_SERVER 0");
			System.err.println("for Repeat, use cron");
			System.exit(1);
		}
		
		Scanner sc = new Scanner(System.in);
		MANAGER manager = new MANAGER("URLs.txt");//URL이 저장된 파일명
		try{
			int argument = Integer.parseInt(args[0]);
			if(argument == 0) {
				MANAGER.Logwriter("MAIN(AUTO)", "자동 동작");
				manager.Run();
			}
			else if (argument == 1){
				while(true) {
					System.out.println("기능을 선택하세요.");
					System.out.println("1. MANUAL::Getnotice");
					System.out.println("2. MANUAL::Upload");
					System.out.println("3. MANUAL::Printnotice");
					System.out.println("4. MANUAL::Setdate");
					System.out.println("5. WEATHER::Getweather");
					System.out.println("6. WEATHER::Upload");
					System.out.println("7. WEATHER::Printweather");
					System.out.println("8. HOMEPAGE Test");
					System.out.print("메뉴 번호 입력 : ");
					int subinput = sc.nextInt();
					switch(subinput) {
						case 1:
							manager.Getnotice();
							MANAGER.Logwriter("MAIN(MANUAL)", "Execute Getnotice");
							break;
						case 2:
							manager.Upload();
							MANAGER.Logwriter("MAIN(MANUAL)", "Execute Upload");
							break;
						case 3:
							manager.Printnotice();
							MANAGER.Logwriter("MAIN(MANUAL)", "Execute Printnotice");
							break;
						case 4:
							manager.Setdate();
							MANAGER.Logwriter("MAIN(MANUAL)", "Execute Setdate");
							break;
						case 5:
							WEATHER weather = new WEATHER();
							weather.Getweather();
							weather.GetAirQuality();
							weather.Printweather();
							weather.Upload();
							//manager.weather.Getweather();
							MANAGER.Logwriter("MAIN(MANUAL)", "Execute Getweather");
							break;
						case 6:
							//manager.weather.Upload();
							MANAGER.Logwriter("MAIN(MANUAL)", "Execute Upload");
							break;
						case 7:
							//manager.weather.Printweather();
							MANAGER.Logwriter("MAIN(MANUAL)", "Execute Printweather");
							break;
						case 8:
							System.out.println("Input Test URL : ");
							sc.nextLine();
							String tmp = sc.nextLine();
							HOMEPAGE home = new HOMEPAGE(tmp);
							home.Load();
							home.Print();
							MANAGER.Logwriter("MAIN(MANUAL)", "Execute HOMEPAGE TEST");
							break;							
						default:
							MANAGER.Logwriter("MAIN(MANUAL)", "Wrong Input Value(input : " +  subinput + ")");
							MANAGER.Logwriter("MAIN", "TERMINATE");
							System.exit(1);
							break;
					}
				}
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
		
		MANAGER.Logwriter("MAIN", "TERMINATE");
		sc.close();
	}
}
