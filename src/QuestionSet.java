package cs544;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class QuestionSet {

	List<Question> question_set;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		QuestionSet set = new QuestionSet("/Users/Lu/Desktop/544_project/question_XML_Sample.xml");
		set.print_set();
	}
	
	public QuestionSet(String filePath){
		this.question_set = new ArrayList<Question>();
		try{
			File inputFile = new File(filePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			Element root = doc.getDocumentElement();
						
			NodeList questionList = root.getElementsByTagName("sent");
			
			
			for(int i = 0; i < questionList.getLength(); i++){
				Element question = (Element)questionList.item(i);
				Question new_question = new Question(question);
				this.question_set.add(new_question);
			}


		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void print_set(){
		for(Question question: this.question_set){
			question.print();
		}
	}
	
	public List<Question> getList(){
		return this.question_set;
	}
	

}
