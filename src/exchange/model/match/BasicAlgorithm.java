package exchange.model.match;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public  class BasicAlgorithm extends MatchMaker {
	public BasicAlgorithm(String account){
		connection=null;
		this.account=account;
	}
	
	static Connection connection;
	private String[] favoritesSkill;
	private String account;
	private String region;
	private int regionNum=0;
	class skillCard{ //依照表格排序地區  (魔術數字)
		int number;//編號
		String type_name;//技能名稱
		int skill_id;  //技能ID
		String region;  //居住地
		int distance;     //距離
		int attitude_score;//教學態度
		int profession_score;//技能程度
		int teaching_score;//教學技巧
		int frequency_score;//教學頻率
		int satisfication_score;//整體滿意度
	}

	class Area{ //依照表格排序地區  (魔術數字)
		int distance;
		String placeName;
		Area(int distance, String placeName) { this.distance=distance; this.placeName=placeName; }
	}
	Area[][] area = new Area[][]{{new Area(0,"基隆"),new Area(1,"台北"),new Area(2,"桃園"),new Area(2,"宜蘭"),new Area(3,"新竹"),new Area(4,"苗栗"),new Area(4,"花蓮"),new Area(5,"台中"),new Area(6,"彰化"),new Area(6,"南投"),new Area(7,"雲林"),new Area(7,"台東"),new Area(8,"嘉義"),new Area(9,"台南"),new Area(10,"高雄"),new Area(11,"屏東")}, //基隆
								 {new Area(0,"台北"),new Area(1,"基隆"),new Area(1,"桃園"),new Area(1,"宜蘭"),new Area(2,"新竹"),new Area(3,"苗栗"),new Area(3,"花蓮"),new Area(4,"台中"),new Area(5,"彰化"),new Area(5,"南投"),new Area(6,"雲林"),new Area(6,"台東"),new Area(7,"嘉義"),new Area(8,"台南"),new Area(9,"高雄"),new Area(10,"屏東")}, //台北	
	
								};

	
    public  void match(){};
    public  void creatMateSet(){
    	openConnection();
    	try {
			setFavoriteSkill();
			setRegion();
			getMatchSkill();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	    	
    	closeConnection();
    	
    };
    
    
    private void setRegion()throws SQLException { //取得地區與距離
		String sql="SELECT region FROM accounts where accounts.user_id='"+account+"';";
		PreparedStatement statement = connection.prepareStatement(sql);
		ResultSet result = statement.executeQuery();
		while (result.next()) {
			region=result.getString("region");
		}
		
		switch (region) {//加入地區距離 先土法煉鋼
		case "基隆":
			regionNum=0;
			break;
		case "台北":
			regionNum=1;
			break;
		case "桃園":
			regionNum=2;
			break;
		case "新竹":
			regionNum=3;
			break;
		case "苗栗":
			regionNum=4;
			break;
		case "台中":
			regionNum=5;
			break;
		case "彰化":
			regionNum=6;
			break;
		case "南投":
			regionNum=7;
			break;
		case "雲林":
			regionNum=8;
			break;
		case "嘉義":
			regionNum=9;
			break;
		case "台南":
			regionNum=10;
			break;
		case "高雄":
			regionNum=11;
			break;
		case "屏東":
			regionNum=12;
			break;
		case "宜蘭":
			regionNum=13;
			break;
		case "花蓮":
			regionNum=14;
			break;
		case "台東":
			regionNum=15;
			break;		
		}	
	}
    
    private void setFavoriteSkill()throws SQLException { //取得興趣技能以做搜尋
		String sql="SELECT count(type_name)as num FROM exchange.favorites where favorites.account='"+account+"';";
		PreparedStatement statement = connection.prepareStatement(sql);
		ResultSet result = statement.executeQuery();
		while (result.next()) {
			favoritesSkill=new String[Integer.parseInt(result.getString("num"))];
		}
		sql="SELECT type_name FROM exchange.favorites where favorites.account='"+account+"';";
		statement = connection.prepareStatement(sql);
		result = statement.executeQuery();
		int i=0;
		while (result.next()) {
			favoritesSkill[i++]=result.getString("type_name");		
		}	
		
		
	}
    
    private void openConnection() {
		System.out.println("-------- MySQL JDBC Connection ------------");
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("MySQL JDBC Driver not found !!");
			return;
		}
		System.out.println("MySQL JDBC Driver Registered!");

		try {
			connection = DriverManager // jdbc:mysql://localhost:3306/data
					.getConnection("jdbc:mysql://localhost:3306/exchange?useUnicode=true&characterEncoding=UTF-8",
							"root", "a2n5h011oj");
			System.out.println("SQL Connection to database established!");

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			System.out.println(e.getMessage());
			return;
		}

	}

	private void getMatchSkill() throws SQLException {
		int skillNum=0;
		int lastdistance=0;
		int nowdistance=0;
		int nowRegionNum=0;
		int limit=50;
		do{
			// SQL
			String sql = "select skills.*,accounts.region from skills,accounts where skills.account=accounts.user_id and skills.bad_tag=false and datediff(CURRENT_DATE(),accounts.recent_log) <3 and accounts.user_id!='"+account+"' ";
					
			
			
			for(int i=0;i<favoritesSkill.length;i++){  //加入興趣技能
				
				if(favoritesSkill.length>1){
					if(i==0){
						sql=sql+"and (skills.type_name='"+favoritesSkill[i]+"' "; //多個興趣開頭+小括號
					}
					else if(i==favoritesSkill.length-1){
						sql=sql+"or skills.type_name='"+favoritesSkill[i]+"') ";//多個興趣結尾+小括號
					}
					else{
						sql=sql+"or skills.type_name='"+favoritesSkill[i]+"' ";
					}
				}
				else{
					sql=sql+"and skills.type_name='"+favoritesSkill[i]+"' ";
				}
			}
			
			
			
			for(int j=nowRegionNum;j<16;j++){  //加入地區
				nowdistance=area[regionNum][j].distance;
				if(nowdistance!=lastdistance){ //判斷是否還有距離相同之地區
					sql=sql+")";
					nowRegionNum=j;
					lastdistance=nowdistance;
					break;
				}
				
				if(j==nowRegionNum){ //第一筆
					sql=sql+"and (accounts.region='"+area[regionNum][j].placeName+"' ";
				}
				else{ //第二筆之後
					sql=sql+"or accounts.region='"+area[regionNum][j].placeName+"' ";
				}
				lastdistance=nowdistance;
				//System.out.println(area[regionNum][j].placeName); //測試地名
				if(j==15){
					sql=sql+")";
					nowRegionNum=j+1;
				}
				
			}
			
			sql=sql+" order by rand(now()) limit "+limit+"; "; 
			
			
			System.out.println(sql);
			// 不知道是什麼
			PreparedStatement statement = connection.prepareStatement(sql);
			// 執行SQL存入結果
			ResultSet result = statement.executeQuery();
	
			// 印出來
			//System.out.println("type_name\t region\t attitude_score\t profession_score\t teaching_score\t frequency_score\t satisfication_score\t ");
			
			while (result.next()) { //顯示當前抓了幾筆資料  預計在這將資料存入陣列
				limit--; //計算還需抓幾筆資料
				//distance=area[regionNum][nowRegionNum-1].distance
				System.out.println(result.getString("type_name")+ "\t"+result.getString("region")+ "\t"+result.getString("attitude_score")+ "\t"+result.getString("profession_score")+ "\t"+result.getString("teaching_score")+ "\t"+result.getString("frequency_score")+ "\t"+result.getString("satisfication_score")+ "\t");
			}
			//System.out.println(limit); 測試用
			
		}while(limit>0&&nowRegionNum<=15); //抓到50筆技能或全部地區資料抓完後跳出
	}

	
	
	private  void closeConnection() {

		try {
			if (connection != null)
				connection.close();
			System.out.println("Connection closed !!");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
    
}