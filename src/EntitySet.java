package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class EntitySet {
	Set<String> entity_set;
	Map<Integer, String> entity_map;
	Set<String> property_set;
	int index;
	int property_count;
	public EntitySet(String path){
		this.entity_set = new HashSet();
		this.entity_map = new HashMap<Integer, String>();
		this.index = 0;
		
		this.property_set = new HashSet();
		this.property_count = 0;
		
		File file = new File(path);
		try {
			Scanner input = new Scanner(file);
			while(input.hasNext()){
				String line = input.nextLine().trim();
				//get entity
				if(line.indexOf("rdfs:label") != -1){
					String temp = line.substring(line.indexOf('>')+1);
					String word = temp.substring(0, temp.indexOf('<')).trim();
					entity_set.add(word);
					entity_map.put(index, word);
					index++;
				}
				//get property
				if(line.indexOf("ont:") != -1){
					int start = line.indexOf(':') + 1;
					int end = Math.min(line.indexOf('>'), line.indexOf(' '));
					end = end == -1 ? line.indexOf('>') : end;
//					System.out.println(line + " " + start + " " + end);
					String prop = line.substring(start, end);
					this.property_set.add(prop);
					this.property_count++;
				}
			}
			input.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public String getRandomEntity(){
		Random ran = new Random();
		return entity_map.get(ran.nextInt(index));
	}
	
	public static void main(String[] args){
		EntitySet es = new EntitySet("/Users/Lu/Desktop/544_project/sample_instances.owl");
		File file = new File("/Users/Lu/Desktop/entities.txt");
		File file_2 = new File("/Users/Lu/Desktop/properties.txt");
		try {
			Writer output = new PrintWriter(file);
			Writer output_2 = new PrintWriter(file_2);
			for(String entity : es.entity_set){
				output.write(entity + "\n");
			}
			for(String property : es.property_set){
				output_2.write(property + "\n");
			}
			output.close();
			output_2.close();			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
