
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class QuestionGenerator implements Runnable {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		QuestionGenerator qg = new QuestionGenerator();
		
		File file = new File("/Users/Lu/Desktop/question_set"+Thread.currentThread().getName()+".txt");
		try {
			PrintWriter pw = new PrintWriter(file);
			for(int i = 0; i < 10; i++){
				ArrayList<String> list = qg.getQuestionForAll();
				for(String q : list){
					pw.println(q);
				}				
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return;
	}
	
	EntitySet es;
	PropertyClassify pc;
	PropertyFineClassify pfc;
	Answerretrieval ar;
	public QuestionGenerator(){
		es = new EntitySet("/Users/Lu/Desktop/544_project/sample_instances.owl");
		pc = new PropertyClassify();
		pfc = new PropertyFineClassify();
		ar = new Answerretrieval();
	}
	
	public String nextQuestion(){
		boolean notfind = true;
		Random ran = new Random();
		String q = "empty";
		
		while(notfind){
			String entity = es.getRandomEntity();
			ArrayList<String> properties = (ArrayList<String>) ar.getAllProperties(entity);

//			System.out.println(entity);
//			for(int i = 0; i < properties.size(); i++){
//				System.out.println(properties.get(i));
//			}			

			String prop = properties.get(ran.nextInt(properties.size()));
			if(prop.equals("label") || prop.equals("subClassOf")){
				continue;
			}
			
			notfind = false;
			ArrayList<String> pool = formQuestion(entity, prop);
			q = pool.get(ran.nextInt(pool.size()));
		}
		return q;
	}
	
	public ArrayList<String> getQuestionForAll(){
		ArrayList<String> res = new ArrayList<String>();

		boolean notfind = true;
		
		while(notfind){
			String entity = es.getRandomEntity();
			ArrayList<String> properties = (ArrayList<String>) ar.getAllProperties(entity);
			Set<String> selected = new HashSet<String>();

			for(int i = 0; i < properties.size(); i++){
				String prop = properties.get(i);
				if(!prop.equals("label") && ! prop.equals("subClassOf") && !selected.contains(prop)){
					selected.add(prop);
				}
			}			
			
			if(selected.size() == 0){
				continue;
			}
			
			notfind = false;
			res.add(entity);
			for(String prop : selected){
				ArrayList<String> pool = formQuestion(entity, prop);
//				res.add(prop);
				for(int j = 0; j < pool.size(); j++){
					res.add(pool.get(j));
				}				
			}
			res.add("--end--");
		}
		
		return res;
	}
	
	public ArrayList<String> formQuestion(String entity, String prop){
		int class_index = -1;
		for(int i = 0; i < pc.option.length; i++){
			if(pc.map.get(pc.option[i]).contains(prop)){
				class_index = i;
				break;
			}
		}
		
		ArrayList<String> res = new ArrayList();

		String coarse_class = "_Unknown";
		String fine_class = "_Unknown";
		if(this.pfc.all_properties.contains(prop)){
			coarse_class = this.pfc.coarse_class.get(prop);
			fine_class = this.pfc.fine_class.get(prop);
		}
		
		if(coarse_class.equals("_Unknown")){
			String s = "";
			s = entity + "的" + prop + "是什么?";
			res.add(s);
			return res;
		}else{
		    ArrayList<String> s = getQuestionForm(coarse_class, fine_class, entity, prop);
		    return s;
		}		
	}
	
	private ArrayList<String> getQuestionForm(String coarse_class, String fine_class, String entity, String prop){
		Random ran = new Random();
		StringBuilder sb = new StringBuilder();
		ArrayList<String> pool = new ArrayList();

		
		//ask about human
		if(coarse_class.equals("_HUM")){
			if(fine_class.equals("_HUM_ENUM")){
				pool.add(entity + "的" + prop + "有哪些人");
				pool.add(entity + "的" + prop + "都有哪些人");
				pool.add(entity + "的" + prop + "有谁");
				pool.add(entity + "的" + prop + "都有谁");
				pool.add(entity + "的" + prop + "都是谁");
				pool.add("有哪些人是" + entity + "的" + prop);
				pool.add("有谁是" + entity + "的" + prop);
			}else if(fine_class.equals("_HUM_DESC")){
				String de = prop.indexOf("其") == -1 ? "的" : "";
				pool.add(entity + de + prop + "是谁");
				pool.add(entity + de + prop + "是哪个");
				pool.add(entity + de + prop + "是哪个人");
				pool.add(entity + de + prop + "是哪一个");
				pool.add(entity + de + prop + "叫什么");
				pool.add(entity + de + prop + "的名字是什么");
				pool.add(entity + de + prop + "叫什么名字");
				
				pool.add("谁是" + entity + de + prop);
				pool.add("哪个人是" + entity + de + prop);
				pool.add("哪个是" + entity + de + prop);
			}else{
				pool.add(entity + "的" + prop + "是谁");
			}
			sb.append(pool.get(ran.nextInt(pool.size())));
		}
		//ask about location
		else if(coarse_class.equals("_LOC")){
			if(fine_class.equals("_LOC_ENUM")){				
				pool.add(entity + "的" + prop + "有哪些地方");
				pool.add(entity + "的" + prop + "有哪些");
				pool.add(entity + "的" + prop + "都有什么地方");
				pool.add(entity + "的" + prop + "有什么地方");
				pool.add("有哪些地方是" + entity + "的" + prop);
				pool.add("哪些地方是" + entity + "的" + prop);
				pool.add("都有哪些地方是" + entity + "的" + prop);
				pool.add("有什么地方是" + entity + "的" + prop);
				pool.add("都有什么地方是" + entity + "的" + prop);
			}else{
				pool.add(entity + "的" + prop + "是什么");
				if(fine_class.equals("_CITY")){
					pool.add(entity + "的" + prop + "是哪个城市");
					pool.add(entity + "的" + prop + "是什么城市");
					pool.add(entity + "的" + prop + "是什么市");
					pool.add(entity + "的" + prop + "在什么市");
					pool.add(entity + "的" + prop + "是哪里");
				}
				if(fine_class.equals("_ADDRESS")){
					pool.add(entity + "在哪里");
					pool.add(entity + "在哪儿");
					pool.add(entity + "在哪个地方");
					pool.add(entity + "在什么地方");
					pool.add(entity + "在什么位置");
					pool.add(entity + "的" + prop + "在哪里");
					pool.add(entity + "的" + prop + "在哪儿");
					pool.add(entity + "的" + prop + "是什么地方");
					pool.add(entity + "的" + prop + "在什么地方");
				}
				if(fine_class.equals("_COUNTRY")){
					pool.add(entity + "的" + prop + "是哪儿");
					pool.add(entity + "的" + prop + "是哪个国家");
					pool.add(entity + "是哪个国家的");
					if(!prop.equals("国籍")){
						pool.add(entity + "在哪里");
						pool.add(entity + "在哪儿");						
					}
				}
				if(fine_class.equals("_LOC_OTHR")){
					pool.add(entity + "的" + prop + "在哪儿");
					pool.add(entity + "的" + prop + "是哪儿");
					pool.add(entity + "的" + prop + "在哪里");
					pool.add(entity + "的" + prop + "在什么地方");
				}
			}
			sb.append(pool.get(ran.nextInt(pool.size())));
		}
		//ask about number
		else if(coarse_class.equals("_NUM")){
			//general
			if(fine_class.equals("_QUANTITY")){
				pool.add(entity + "的" + prop + "有多少");
				pool.add(entity + "的" + prop + "是多少");
				int index = prop.indexOf("数");
				String quat = (String) (index == -1 ? prop : prop.subSequence(0, index));
				pool.add(entity + "有多少" + quat);
			}else if(fine_class.equals("_PRICE")){
				pool.add(entity + "的" + prop + "是多少");
				pool.add(entity + "的" + prop + "是多少钱");
				pool.add(entity + "的" + prop + "有多少");				
				pool.add(entity + "的" + prop + "有多少钱");				
				pool.add(entity + "卖多少钱");
				pool.add(entity + "有多贵");
				pool.add(entity + "价值多少");
			}else if(fine_class.equals("_NUMBER")){
				pool.add(entity + "的" + prop + "是多少");
				pool.add(entity + "的" + prop + "是什么");				
			}else if(fine_class.equals("_AREA")){
				pool.add(entity + "的" + prop + "是多少");
				pool.add(entity + "的" + prop + "有多大");
			}else if(fine_class.equals("_NUM_OTHR")){
				pool.add(entity + "的" + prop + "是多少");
				if(prop.equals("身高")){
					pool.add(entity + "的" + prop + "有多少");
					pool.add(entity + prop + "有多少");
					pool.add(entity + "的" + prop + "是多少");
					pool.add(entity + prop + "是多少");
					pool.add(entity + "有多高");
					pool.add(entity + "长多高");
					pool.add(entity + "个子有多高");
				}
			}else{
				pool.add(entity + "的" + prop + "是多少");
			}
			sb.append(pool.get(ran.nextInt(pool.size())));
		}
		//ask about time
		else if(coarse_class.equals("_TIME")){
			if (ran.nextBoolean() || prop.indexOf("所处时代") != -1) {
				pool.add(entity + "的" + prop + "是什么时候");
				pool.add(entity + "的" + prop + "是什么时间");
			} else {
				if (prop.indexOf("时间") != -1 || prop.indexOf("年月") != -1) {
					String verb = prop.substring(0, 2);
					pool.add(entity + "是什么时候" + verb);
					pool.add(entity + "是什么时间" + verb);
				} else if (prop.indexOf("片长") != -1) {
					pool.add(entity + "的" + prop + "有多久");
				}
			}
		}
		//ask about object
		else if(coarse_class.equals("_OBJ")){
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
			if(prop.equals("目") || prop.equals("界") || prop.equals("科") || prop.equals("纲") || prop.equals("门")){
				pool.add(entity + "是什么" + prop);
				pool.add(entity + "是哪个" + prop);
				pool.add(entity + "属于什么" + prop);
				pool.add(entity + "属于哪个" + prop);
			}else{
				if(!prop.equals("简介") && !prop.equals("ABSTRACT") && !prop.equals("URL")){
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
		}
		//ask about unknown things
		else{
			pool.add(entity + "的" + prop + "是什么");
		}
		return pool;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		File file = new File("/Users/Lu/Desktop/simple_question_s"+Thread.currentThread().getName()+".txt");
		try {
			PrintWriter pw = new PrintWriter(file);
			for(int i = 0; i < 20; i++){
				pw.println(nextQuestion());
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
