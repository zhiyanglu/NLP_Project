import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.dependency.CRFDependencyParser;

public class questionparsing {
	public static List<String> getSimpleQuery(String inStr){
		List<String> keywordList = HanLP.extractKeyword(inStr, 10);
		List<String> final_keyword = new ArrayList<>();
		for(int i=0;i<inStr.length();i++){
			for(int j=0;j<keywordList.size();j++){
				if(keywordList.get(j).indexOf(inStr.charAt(i)) == 0){
					final_keyword.add(keywordList.get(j));
				}
			}
		}
		return final_keyword;
	}
	
	public static void getQuery(String inStr){
		CoNLLSentence l = CRFDependencyParser.compute(inStr);
//		List<String> phraseList = HanLP.extractPhrase(inStr, 10);
		Map<String,List<String>> keyWord = new HashMap<>();
		List<String> result = new ArrayList<>();
		System.out.println(l);
		for(int i =0; i<l.getWordArray().length;i++){
//			List<String> temp = new ArrayList<>();
//			temp.add(l.getWordArray()[i].CPOSTAG);
//			temp.add(l.getWordArray()[i].DEPREL);
//			keyWord.put(l.getWordArray()[i].NAME,temp);
			if(l.getWordArray()[i].CPOSTAG.contains("rys")){
				//µØµãÒÉÎÊ´Ê
				System.out.println(l.getWordArray()[i].NAME);
			}
			if(l.getWordArray()[i].CPOSTAG.contains("ry")){
				//Ê±¼äÒÉÎÊ´Ê
				System.out.println(l.getWordArray()[i].NAME);
			}
			if(l.getWordArray()[i].CPOSTAG.contains("ns")){
				//µØµãÃû´Ê
				System.out.println(l.getWordArray()[i].NAME);
			}
			
			if(l.getWordArray()[i].DEPREL.contains("ºËÐÄ³É·Ö") && l.getWordArray()[i].POSTAG.equals("v")){
				//ºËÐÄ´Ê»ã
				System.out.println(l.getWordArray()[i].NAME);
			}
			if(l.getWordArray()[i].POSTAG.equals("n") && l.getWordArray()[i].DEPREL.contains("ÏÞ¶¨")){
				//ÐÞÊÎºËÐÄ´Ê»ã
				System.out.println(l.getWordArray()[i].NAME);
			}
			if(l.getWordArray()[i].POSTAG.equals("n") && !(l.getWordArray()[i].DEPREL.contains("ÏÞ¶¨"))){
				//ÆÕÍ¨Ãû´Ê´Ê»ã
				System.out.println(l.getWordArray()[i].NAME);
			}
		}
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String content = "北京在哪里";
		getQuery(content);
		System.out.println(getSimpleQuery(content));
//		System.out.println(keyWord);
	}

}
