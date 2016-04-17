package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

public class QuestionGenerator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		QuestionGenerator qg = new QuestionGenerator();
		File file = new File("/Users/Lu/Desktop/simple_questions.txt");
		try {
			PrintWriter pw = new PrintWriter(file);
			for(int i = 0; i < 5; i++){
				pw.println(qg.nextQuestion());
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		    String s = getQuestionForm(coarse_class, fine_class, entity, prop);
		    if(s.length() == 0 || s == null) return entity + "的" + prop + "是什么?";
		    else return s;
		}		
	}
	
	private String getQuestionForm(String coarse_class, String fine_class, String entity, String prop){
		//ask about human
		Random ran = new Random();
		ArrayList<String> parts = new ArrayList();
		StringBuilder sb = new StringBuilder();
		if(coarse_class.equals("_HUM")){
			if(ran.nextBoolean()){
				String[] question_word = {"是谁", "是哪个","叫什么","的名字是什么","叫什么名字","是哪个人"};
				sb.append(entity+"的"+prop+question_word[ran.nextInt(question_word.length)]);
			}else{
				String[] question_word = {"谁是", "哪个是","哪个人是"};
				sb.append(question_word[ran.nextInt(question_word.length)]+entity+"的"+prop);
			}
		}
		//ask about location
		else if(coarse_class.equals("_LOC")){
			if(fine_class.equals("地点列举")){				
				String[] question_word = {"有哪些地方","有哪些","都有什么地方","什么地方"};
				if(ran.nextBoolean()){
					sb.append(entity + "的" + prop + question_word[ran.nextInt(question_word.length)]);
				}else{
					sb.append(question_word[ran.nextInt(question_word.length)] + "是" + entity + "的" + prop);
				}
			}else{
				String[] question_word = {"在哪里","在哪儿","在什么地方"};
				sb.append(entity + "的" + prop + question_word[ran.nextInt(question_word.length)]);
			}
		}
		//ask about number
		else if(coarse_class.equals("_NUM")){
			//general
			ArrayList<String> pool = new ArrayList();
			if(prop.equals("GDP") || prop.equals("人口") || prop.equals("页数") || prop.equals("价格") || prop.equals("字数") || prop.equals("面积") || prop.equals("身高")){
				pool.add(entity + "的" + prop + "有多少");
			}
			if(prop.equals("GDP") || prop.equals("人口") || prop.equals("页数") || prop.equals("字数") || prop.equals("价格")  || prop.equals("邮编区码") || prop.equals("身高") || prop.equals("ISBN") || prop.equals("电话区码")){
				pool.add(entity + "的" + prop + "是多少");
			}
			if(prop.equals("GDP") || prop.equals("身材") || prop.equals("邮编区码") || prop.equals("ISBN") || prop.equals("电话区码")){
				pool.add(entity + "的" + prop + "是什么");
			}
			if(prop.equals("价格")){
				pool.add(entity + "卖多少钱");
				pool.add(entity + "有多贵");
			}
			if(prop.equals("字数")){
				pool.add(entity + "有多少字");
			}
			if(prop.equals("身高")){
				pool.add(entity + "有多高");
				pool.add(entity + "长多高");
			}
			if(pool.size() == 0){
				pool.add(entity + "的" + prop + "是什么");
			}
			sb.append(pool.get(ran.nextInt(pool.size())));
		}
		//ask about time
		else if(coarse_class.equals("_TIME")){
			String[] qw_dep = {"什么时候","什么时间"};
			if (ran.nextBoolean() || prop.indexOf("所处时代") != -1) {
				sb.append(entity);
				sb.append("的");
				sb.append(prop);
				sb.append("是");
				sb.append(qw_dep[ran.nextInt(qw_dep.length)]);
			} else {
				if (prop.indexOf("时间") != -1 || prop.indexOf("年月") != -1) {
					String verb = prop.substring(0, 2);
					sb.append(entity);
					sb.append("是");
					sb.append(qw_dep[ran.nextInt(qw_dep.length)]);
					sb.append(verb);
				} else if (prop.indexOf("片长") != -1) {
					sb.append(entity);
					sb.append("的");
					sb.append(prop);
					sb.append("有多久");
				}
			}
		}
		//ask about object
		else if(coarse_class.equals("_OBJ")){
			ArrayList<String> pool = new ArrayList();
			if(fine_class.equals("_OBJ_ENUM")){
				pool.add(entity + "的" + prop + "有什么");
				pool.add(entity + "的" + prop + "有哪些");
				pool.add(entity + "有哪些" + prop);
				pool.add(entity + "有什么" + prop);
			}else{
				pool.add(entity + "的" + prop + "是什么");
				pool.add(entity + "的" + prop + "是哪个");
				pool.add(entity + "的" + prop + "是哪一个");
			}
			sb.append(pool.get(ran.nextInt(pool.size())));
		}
		//ask about description
		else if(coarse_class.equals("_DES")){
			ArrayList<String> pool = new ArrayList();
			if(prop.equals("目") || prop.equals("界") || prop.equals("科") || prop.equals("纲") || prop.equals("门")){
				pool.add(entity + "是什么" + prop);
				pool.add(entity + "是哪个" + prop);
				pool.add(entity + "属于什么" + prop);
				pool.add(entity + "属于哪个" + prop);
			}else{
				if(!prop.equals("简介") && !prop.equals("ABSTRACT")){
					pool.add(entity + "是什么" + prop);
				}
				if(prop.equals("简介") || prop.equals("ABSTRACT") || prop.equals("URL")){
					pool.add(entity + "的简介是什么");
					pool.add(entity + "是谁");
				}else{
					pool.add(entity + "的" + prop + "是什么");					
				}
				if(prop.equals("性别")){
					pool.add(entity + "是男的还是女的");
				}
				if(prop.equals("民族") || prop.equals("血型") || prop.equals("星座")){
					pool.add(entity + "是哪个" + prop);
				}
			}
			sb.append(pool.get(ran.nextInt(pool.size())));
		}
		//ask about unknown things
		else{
			parts.add(entity);
			parts.add("的");
			parts.add(prop);
			parts.add("是什么");
			
		}
		return sb.toString();
	}

}
