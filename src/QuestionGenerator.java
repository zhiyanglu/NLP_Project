package src;

import java.util.ArrayList;
import java.util.Random;

public class QuestionGenerator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		QuestionGenerator qg = new QuestionGenerator();
		qg.nextQuestion();
		
	}
	
	EntitySet es;
	public QuestionGenerator(){
		es = new EntitySet("/Users/Lu/Desktop/544_project/sample_instances.owl");
	}
	
	public String nextQuestion(){
		boolean notfind = true;
		Random ran = new Random();
		String q = "empty";
		
		while(notfind){
			String entity = es.getRandomEntity();
			ArrayList<String> properties = Answerretrieval.getAllProperty(entity);

//			System.out.println(entity);
//			for(int i = 0; i < properties.size(); i++){
//				System.out.println(properties.get(i));
//			}			

			String prop = properties.get(ran.nextInt(properties.size()));
			if(prop.equals("label") || prop.equals("subClassOf")){
				continue;
			}
			
			notfind = false;
			q = formQuestion(entity, prop);
			System.out.println(q);
		}		
		return q;
	}
	
	
	public String formQuestion(String entity, String prop){
		String s = "";
		s = entity + "的" + prop + "是什么?";
		return s;
	}

}
