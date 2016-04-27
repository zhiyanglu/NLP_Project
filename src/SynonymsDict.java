import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class SynonymsDict {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		SynonymsDict sd = new SynonymsDict();
//		ArrayList<String> list = sd.getSynonyms("性别", false);
//		System.out.println(list);
		test();

	}
	public static void test() throws FileNotFoundException{
		SynonymsDict sd = new SynonymsDict();
		int count = 0;
		File file = new File("/Users/Lu/Desktop/properties_except_hum.txt");
		Scanner props = new Scanner(file);
		while(props.hasNextLine()){
			String line = props.nextLine();
			String words[] = line.split("\\s+");
			String word = words[0];
			ArrayList<String> list = sd.getRangeSynonyms(word, true);
			if(list.size() == 0) count++;
			System.out.println(word + ":\t" + list);
		}
		
		System.out.println("cannot find synonyms: " + count);

	}

	final static String dict_path = "/Users/Lu/Desktop/544_project/synonyms_dict_utf8.txt";

	Map<String, ArrayList<String>> word_codes;
	Map<String, ArrayList<String>> code_words;
	Map<String, Character> code_flag;
	Map<String, ArrayList<String>> code_level5;

	public SynonymsDict() {
		this.word_codes = new HashMap();
		this.code_words = new HashMap();
		this.code_flag = new HashMap();
		this.code_level5 = new HashMap();
		
		try {
			System.out.println("Reading model...");
			readModel();
			System.out.println("Finished");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void readModel() throws FileNotFoundException {
		File dict = new File(this.dict_path);
		Scanner data = new Scanner(dict);
		
		int counter = 100;
		
		while (data.hasNextLine()) {
			String line = data.nextLine();
			String[] words = line.split("\\s+");
			buildModel(words);
		}
		data.close();
	}

	private void buildModel(String[] words) {
		String code = words[0].substring(0, words[0].length() - 1);
		char flag = words[0].charAt(words[0].length() - 1);
		this.code_flag.put(code, flag);
		for (int i = 1; i < words.length; i++) {
			String word = words[i];
			if (!this.word_codes.containsKey(word)) {
				this.word_codes.put(word, new ArrayList<String>());
			}
			this.word_codes.get(word).add(code);
			
			if(!this.code_words.containsKey(code)) {
				this.code_words.put(code, new ArrayList<String>());
			}
			this.code_words.get(code).add(word);
		}
		
		String major_code = code.substring(0,5);
		if(!this.code_level5.containsKey(major_code)){
			this.code_level5.put(major_code, new ArrayList<String>());
		}
		this.code_level5.get(major_code).add(code);
	}
	
	/**
	 * 
	 * @param word : word to search
	 * @param findSameClass : set to true if only search words that are equal
	 * @return
	 */
	public ArrayList<String> getSynonyms(String word, boolean findSameClass){
		ArrayList<String> res = new ArrayList<String>();
		if(!this.word_codes.containsKey(word)) return res;
		
		for(String code : this.word_codes.get(word)){
			if(findSameClass && this.code_flag.get(code) != '=') continue;
			ArrayList<String> list = this.code_words.get(code);
			for(String syno_word : list) res.add(syno_word);
		}
		return res;
	}
	public ArrayList<String> getRangeSynonyms(String word, boolean findSameClass){
		ArrayList<String> res = new ArrayList<String>();
		if(!this.word_codes.containsKey(word)) return res;
		Set<String> codes = new HashSet<String>();
		for(String code : this.word_codes.get(word)){
			String majorcode = code.substring(0,5);
			for(String oth_code : this.code_level5.get(majorcode)) codes.add(oth_code);
		}
		
		for(String code : codes){
			if(findSameClass && this.code_flag.get(code) != '=') continue;
			ArrayList<String> list = this.code_words.get(code);
			for(String syno_word : list) res.add(syno_word);			
		}
		return res;
	}
}
