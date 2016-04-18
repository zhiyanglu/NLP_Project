package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class PropertyFineClassify {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PropertyFineClassify pfc = new PropertyFineClassify();
//		pfc.trainFromUnclassfied();
	}

	Set<String> all_properties = new HashSet<String>();
	Map<String, String> coarse_class = new HashMap();
	Map<String, String> fine_class = new HashMap();
	
	String[] options = {"_HUM","_LOC","_NUM","_TIME","_OBJ","_DES","_Unknown"};
	//特定人物 团体机构 人物描述 人物列举 人物其他
	String[] hum_options = {"_HUM_SPECI","_ORG","_HUM_DESC","_HUM_ENUM","_HUM_OTHR"};
	//星球 城市 大陆 国家 省 河流 湖泊 山脉 大洋 岛屿 地点列举 地址 地点其他
	String[] loc_options = {"_PLANET", "_CITY","_CONTINENT","_COUNTRY","_PROVINCY","_RIVER","_LAKE","_MOUNTAIN","_OCEAN","_ISLAND","_LOC_ENUM","_ADDRESS","_LOC_OTHR"};
	//号码 数量 价格 百分比 距离 重量 温度 年龄 面积 频率 速度 范围 顺序 数字列举 数字其他
	String[] num_options = {"_NUMBER","_QUANTITY","_PRICE","_PERCENT","_DISTANCE","_WEIGHT","_TEMP","_AGE","_AREA","_FREQ","_SPEED","_RANGE","_SEQUENCE","_NUM_ENUM","_NUM_OTHR"};
	//年 月 日 时间 时间范围 时间列举 时间其他
	String[] time_options = {"_YEAR","_MONTH","_DAY","_TIME","_TIME_RANGE","_TIME_ENUM","_TIME_OTHR"};
	//动物 植物 食物 颜色 货币 语言文字 物质 机械 交通工具 宗教 娱乐 实体列举 实体其他
	String[] obj_options = {"_ANIMAL","_PLANT","_FOOD","_COLOR","_MONEY","_LANG","_OBJECT","_MACHINE","_TRANS","_RELIGION","_ENTERTAIN","_OBJ_ENUM","_OBJ_OTHR"};
	//简写 意义 方法 原因 定义 描述其他
	String[] des_options = {"_ABBRV","_MEAN","_METHOD","_REASON","_DEF","_DES_OTHR"};
	
	Set<String> all_class = new HashSet();
	
	
	public PropertyFineClassify(){
		for(int i = 0; i < options.length; i++){
			all_class.add(options[i]);
		}
		
		readFineModel();
	}
	
	
	private void readFineModel(){
		File file = new File("/Users/Lu/Desktop/NLP_Project/propty_class_two_level.txt");
		try {
			Scanner data = new Scanner(file);
			while(data.hasNext()){
				String line = data.nextLine();
				String[] words = line.split("\\s+");
				all_properties.add(words[0]);
				coarse_class.put(words[0], words[1]);
				fine_class.put(words[0], words[2]);
				if(words[1].equals("_TIME")){
					System.out.println(words[0] + " " + words[2]);
				}
			}
			data.close();
		} catch (FileNotFoundException e){
			e.printStackTrace();
		}
	}
	
	public void trainFromClassfied(){
		File file = new File("/Users/Lu/Desktop/NLP_Project/property_Classification.txt");
		try {
			Scanner data = new Scanner(file);
			String key = "";
			while(data.hasNext()){
				String word = data.nextLine();
				if(this.all_properties.contains(word)) continue;
				
				if(this.all_class.contains(word)){
					key = word;
					continue;
				}else{
					String fine = selectFineClass(word,key);
					if(fine.equals("exit")) break;

					this.all_properties.add(word);
					this.coarse_class.put(word, key);					
					this.fine_class.put(word, fine);
				}				
			}
			data.close();
			this.writeModel();
		} catch (FileNotFoundException e){
			e.printStackTrace();
		}
	}
	
	public void trainFromUnclassfied(){
		File file = new File("/Users/Lu/Desktop/properties.txt");
		try {
			Scanner data = new Scanner(file);
			while(data.hasNext()){
				String word = data.nextLine();
				if(this.all_properties.contains(word)) continue;
				
				Scanner keyboard = new Scanner(System.in);
				System.out.println("1.人物 2.地点 3.数字 4.时间 5.实体 6.描述 7.未知 0.结束");
				System.out.println("Choose category for: " + word);
				int opt = keyboard.nextInt();
				if(opt == 0) break;
				String key = this.options[opt-1];
				String fine = selectFineClass(word, key);
				if(fine.equals("exit")) break;
				
				this.all_properties.add(word);
				this.coarse_class.put(word, key);					
				this.fine_class.put(word, fine);
			}
			data.close();
			this.writeModel();
		} catch (FileNotFoundException e){
			e.printStackTrace();
		}
	}
	
	private String selectFineClass(String word, String key){
		System.out.println("Choose fine class for: " + word);
		Scanner keyboard = new Scanner(System.in);
		if(key.equals("_HUM")){
			return "_HUM_DESC";
			
//			System.out.println("1.特定人物 2.团体机构 3.人物描述 4.人物列举 5.人物其他");
//			int sel = keyboard.nextInt();
//			if(sel == 0) return "exit";
//			return this.hum_options[sel-1];
		}else if(key.equals("_LOC")){
			System.out.println("1.星球 2.城市 3.大陆 4.国家 5.省 6.河流 7.湖泊 8.山脉 9.大洋 10.岛屿 11.地点列举 12.地址 13.地点其他");
			int sel = keyboard.nextInt();
			if(sel == 0) return "exit";
			return this.loc_options[sel-1];
		}else if(key.equals("_NUM")){
			System.out.println("1.号码 2.数量 3.价格 4.百分比 5.距离 6.重量 7.温度 8.年龄 9.面积 10.频率 11.速度 12.范围 13.顺序 14.数字列举 15.数字其他");
			int sel = keyboard.nextInt();
			if(sel == 0) return "exit";
			return this.num_options[sel-1];
		}else if(key.equals("_TIME")){
			System.out.println("1.年 2.月 3.日 4.时间 5.时间范围 6.时间列举 7.时间其他");
			int sel = keyboard.nextInt();
			if(sel == 0) return "exit";
			return this.time_options[sel-1];
		}else if(key.equals("_OBJ")){
			System.out.println("1.动物 2.植物 3.食物 4.颜色 5.货币 6.语言文字 7.物质 8.机械 9.交通工具 10.宗教 11.娱乐 12.实体列举 13.实体其他");
			int sel = keyboard.nextInt();
			if(sel == 0) return "exit";
			return this.obj_options[sel-1];
		}else if(key.equals("_DES")){
			System.out.println("1.简写 2.意义 3.方法 4.原因 5.定义 6.描述其他");
			int sel = keyboard.nextInt();
			if(sel == 0) return "exit";
			return this.des_options[sel-1];
		}else{
			return "_Uknown";
		}
	}
	
	private void writeModel(){
		File file = new File("/Users/Lu/Desktop/NLP_Project/propty_class_two_level.txt");

		try {
			PrintWriter pw = new PrintWriter(file);
			for(String prop : this.all_properties){
				pw.println(prop + " " + this.coarse_class.get(prop) + " " + this.fine_class.get(prop));
			}
			pw.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
