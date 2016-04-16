package src;

import java.util.ArrayList;
import java.util.Random;

public class QuestionGenerator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		QuestionGenerator qg = new QuestionGenerator();
		System.out.println(qg.nextQuestion());
	}
	
	EntitySet es;
	PropertyClassify pc;
	PropertyFineClassify pfc;
	public QuestionGenerator(){
		es = new EntitySet("/Users/Lu/Desktop/544_project/sample_instances.owl");
		pc = new PropertyClassify();
		pfc = new PropertyFineClassify();
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

		String coarse_class = "_Unknown";
		String fine_class = "_Unknown";
		if(this.pfc.all_properties.contains(prop)){
			coarse_class = this.pfc.coarse_class.get(prop);
			fine_class = this.pfc.fine_class.get(prop);
		}
		
		if(coarse_class.equals("_Unknown")){
			String s = "";
			s = entity + "的" + prop + "是什么?";
			return s;
		}else{
			ArrayList<String> words = getQuestionForm(coarse_class, fine_class, entity, prop);
			StringBuilder sb = new StringBuilder();
			for(String word : words){
				sb.append(word);
			}
			return sb.toString();
		}		
	}
	
	private ArrayList<String> getQuestionForm(String coarse_class, String fine_class, String entity, String prop){
		//ask about human
		Random ran = new Random();
		ArrayList<String> parts = new ArrayList();
		if(coarse_class.equals("_HUM")){
			String[] question_word = {"是谁", "是哪个","叫什么","的名字是什么","叫什么名字","是哪个人"};
			parts.add(entity);
			parts.add("的");
			parts.add(prop);
			parts.add(question_word[ran.nextInt(question_word.length)]);
		}
		//ask about location
		else if(coarse_class.equals("_LOC")){
			String[] question_word = {"在哪里","在哪儿","在什么地方"};
			parts.add(entity);
			if(ran.nextBoolean()){
				parts.add(question_word[ran.nextInt(question_word.length)]);
			}else{
				parts.add("的");
				parts.add(prop);
				parts.add(question_word[ran.nextInt(question_word.length)]);
			}			
		}
		//ask about number
		else if(coarse_class.equals("_NUM")){
			String[] qw_dep = {"是多少","有多少"};
			parts.add(entity);
			parts.add("的");
			parts.add(prop);
			parts.add(qw_dep[ran.nextInt(qw_dep.length)]);
		}
		//ask about time
		else if(coarse_class.equals("_TIME")){
			String[] qw_dep = {"是什么时候","是什么时间"};
			parts.add(entity);
			parts.add("的");
			parts.add(prop);
			parts.add(qw_dep[ran.nextInt(qw_dep.length)]);			
		}
		//ask about object
		else if(coarse_class.equals("_OBJ")){
			parts.add(entity);
			parts.add("的");
			parts.add(prop);
			parts.add("是什么");			
		}
		//ask about description
		else if(coarse_class.equals("_DES")){
			parts.add(entity);
			parts.add("的");
			parts.add(prop);
			parts.add("是什么");						
		}
		//ask about unknown things
		else{
			parts.add(entity);
			parts.add("的");
			parts.add(prop);
			parts.add("是什么");			
		}
		
		
		return parts;
	}

}
