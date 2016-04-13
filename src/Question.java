package src;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Question {
	String question_sentence;
	List<Word> words;
	
	public Question(Element question){
		
		this.question_sentence = question.getAttributes().getNamedItem("cont").getNodeValue();
		words = new ArrayList();
		
		NodeList childList = question.getElementsByTagName("word");
		for(int j = 0; j < childList.getLength(); j++){
			Node word_node = childList.item(j);
			Word word = new Word(word_node);
			words.add(word);
		}
	}
	
	public String getSencente(){
		return this.question_sentence;
	}
	
	public void print(){
		System.out.println(this.question_sentence);
		for(Word word : words){
			word.print();
		}
	}
}
