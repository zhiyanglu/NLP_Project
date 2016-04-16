package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class PropertyClassify {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PropertyClassify pc = new PropertyClassify();
		pc.trainning("/Users/Lu/Desktop/properties.txt");
	}
	
	
	
	Set<String> HUM = new HashSet<String>();
	Set<String> LOC = new HashSet<String>();
	Set<String> NUM = new HashSet<String>();
	Set<String> TIME = new HashSet<String>();
	Set<String> OBJ = new HashSet<String>();
	Set<String> DES = new HashSet<String>();
	Set<String> Unknown = new HashSet<String>();
	Set<String> total = new HashSet<String>();
	Map<String, Set> map = new HashMap<String, Set>();
	String[] option = {"_HUM","_LOC","_NUM","_TIME","_OBJ","_DES","_Unknown"};
	
	public PropertyClassify(){
		map.put("_HUM", this.HUM);
		map.put("_LOC", this.LOC);
		map.put("_NUM", this.NUM);
		map.put("_TIME", this.TIME);
		map.put("_OBJ", this.OBJ);
		map.put("_DES", this.DES);
		map.put("_Unknown", this.Unknown);
		readCurrentModel();
	}
	
	private void readCurrentModel(){
		File file = new File("/Users/Lu/Desktop/NLP_Project/property_Classification.txt");
		try {
			Scanner data = new Scanner(file);
			String key = "";
			while(data.hasNext()){
				String word = data.nextLine();
				if(map.containsKey(word)){
					key = word;
					continue;
				}else{
					map.get(key).add(word);
					total.add(word);
				}
			}
			data.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void trainning(String data_path){
		File file = new File(data_path);
		try {
			Scanner data = new Scanner(file);
			Scanner keyboard = new Scanner(System.in);

			while(data.hasNext()){
				String word = data.nextLine();
				if(this.total.contains(word)) continue;
				System.out.println("1.人物 2.地点 3.数字 4.时间 5.实体 6.描述 7.未知 0.结束");
				System.out.println("Choose category for: " + word);
				int opt = keyboard.nextInt();
				
				if(opt == 0 || opt > 7){
					writeModel();
					break;
				}
				
				map.get(this.option[opt-1]).add(word);
			}
			data.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void writeModel(){
		File file = new File("/Users/Lu/Desktop/NLP_Project/property_Classification.txt");
		try {
			PrintWriter output = new PrintWriter(file);
			for(int i = 0; i < this.option.length; i++){
				output.println(option[i]);
				Set<String> temp = map.get(option[i]);
				for(String word : temp){
					output.println(word);
				}
			}
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
