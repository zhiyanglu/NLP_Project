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
	PropertyClassify pc;
	public QuestionGenerator(){
		es = new EntitySet("/Users/Lu/Desktop/544_project/sample_instances.owl");
		pc = new PropertyClassify();
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
		int class_index = -1;
		for(int i = 0; i < pc.option.length; i++){
			if(pc.map.get(pc.option[i]).contains(prop)){
				class_index = i;
				break;
			}
		}
		if(class_index == -1) return "";
		
		ArrayList<String> words = getQuestionForm(class_index);
		
		String s = "";
		s = entity + "的" + prop + "是什么?";
		
		return s;
	}
	
	private ArrayList<String> getQuestionForm(int class_index){
		//ask about human
		if(class_index == 0){
			
		}
		//ask about location
		else if(class_index == 1){
			
		}
		//ask about number
		else if(class_index == 2){
			
		}
		//ask about time
		else if(class_index == 3){
			
		}
		//ask about object
		else if(class_index == 4){
			
		}
		//ask about description
		else if(class_index == 5){
			
		}
		//ask about unknown things
		else{
			
		}
		
		
		return null;
	}

}
