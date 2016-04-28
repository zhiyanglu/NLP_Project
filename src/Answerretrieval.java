

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.fnlp.nlp.cn.CNFactory;
import org.fnlp.util.exception.LoadModelException;
import org.jdom2.JDOMException;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.FileManager;

//import org.python.util.PythonInterpreter;
public class Answerretrieval {

	/**
	 * 
	 * @param question
	 * @return
	 * @throws LoadModelException 
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public String ask(String question) throws JDOMException, IOException, LoadModelException{
		String answer = null;		
		Map<String, String> tuple = parseQuestion(question);
		if(tuple.get("entity").equals("")){
			return "We do not understand your question";
		}
		
		ArrayList<String> possible_entities = getAllPossibleEntities(tuple.get("entity"));
		String entity = "";
		if(possible_entities.size() == 0){
			return "We do not know about " + tuple.get("entity");
		}else if(possible_entities.size() == 1){
			entity = possible_entities.get(0);
		}else{
			//select an entity by user
			System.out.println("What is the thing that you are asking about?");
			for(int i = 0; i < possible_entities.size(); i++){
				System.out.println((i+1) + ". " + possible_entities.get(i));
			}
			Scanner input = new Scanner(System.in);
			int index = input.nextInt();
			while(index > possible_entities.size()){
				System.out.println("choose again!");
				index = input.nextInt();
			}
			entity = possible_entities.get(index - 1);
		}
		
		String property = "";
		ArrayList<String> all_properties = getAllProperties(entity);
		if(tuple.get("property").equals("")){
			//show all property to user
			System.out.println("We cannot find the thing you are asking. Are you interested in those things about " + entity + "?");
			for(int i = 0; i < all_properties.size(); i++){
				System.out.println((i+1) + ". " + all_properties.get(i));
			}
			Scanner input = new Scanner(System.in);
			int index = input.nextInt();
			while(index > all_properties.size()){
				System.out.println("choose again!");
				index = input.nextInt();
			}
			property = all_properties.get(index -1);
		}else{
			
			//all_properties contains exact the raw property
			boolean notfind = true;
			for(String p : all_properties){
				if(p.equals(tuple.get("property"))){
					property = p;
					notfind = false;
					break;
				}
			}
						
			//get similar properties
			if(notfind){
				String synonym_property = getSimilarProperty(tuple.get("property"), all_properties);
				if(synonym_property.equals("")){
					return "We cannot find " + tuple.get("property") + " of " + tuple.get("entity");
				}else{
					property = synonym_property;
				}				
			}
		}
		
		answer = answerRetrieve(entity, property);
		
		return answer;
	}
	
	/**
	 * return class and property after raw parsing
	 * @return
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws LoadModelException 
	 */
	public Map<String, String> parseQuestion(String content) throws JDOMException, IOException, LoadModelException{
//		String content = "丁海的爱好什么？";
//		getQuery(content);
//		System.out.println(getSimpleQuery(content));
//		System.out.println(keyWord);
//		four-tuple as (QW,CW,QT,Entity) store in a hashmap
		Map<String,String> Tuple = new HashMap<>();
//		Entity Recognition
		Segment segment = HanLP.newSegment().enableNameRecognize(true);
		List<Term> termList = segment.seg(content);
		List<String> term2str = new ArrayList<String>();
	    System.out.println(termList);
	    for (int i=0; i < termList.size(); i++){
//			System.out.println(termList.get(i).toString());
			term2str.add(termList.get(i).toString());
			}
//	    analyze each component in the question sentence
//	    save the entity in the "entity" variable
	    String entity = "";
//	    save the question word in "question" variable
	    String question = "";
	    for (int i=0; i<term2str.size(); i++){
//	    	select the entity in the question sentence
//	    	check if the entity is a location name, which tag is "ns"
	    	if (term2str.get(i).substring(term2str.get(i).indexOf("/")+1,term2str.get(i).length()).equals("ns")){
//	    		System.out.println(term2str.get(i).substring(0,term2str.get(i).indexOf("/") ));
	    		entity = term2str.get(i);
	    	}
//	    	check if the entity is a Chinese name, which tag is "nr"
	    	if (term2str.get(i).substring(term2str.get(i).indexOf("/")+1,term2str.get(i).length()).equals("nr")){
//	    		System.out.println(term2str.get(i).substring(0,term2str.get(i).indexOf("/") ));
	    		entity = term2str.get(i);
	    	}
//	    	check if the entity is a Foreign name, which tag is "nrf" or "nrj"
	    	if (term2str.get(i).substring(term2str.get(i).indexOf("/")+1,term2str.get(i).length()).equals("nrf") || term2str.get(i).substring(term2str.get(i).indexOf("/")+1,term2str.get(i).length()).equals("nrj")){
//	    		System.out.println(term2str.get(i).substring(0,term2str.get(i).indexOf("/") ));
	    		entity = term2str.get(i);
	    	}
//	    	check if the entity is a organization name, which tag is "nt"
	    	if (term2str.get(i).substring(term2str.get(i).indexOf("/")+1,term2str.get(i).length()).equals("nt")){
//	    		System.out.println(term2str.get(i).substring(0,term2str.get(i).indexOf("/") ));
	    		entity = term2str.get(i);
	    	}
//	    	select the question word in the question sentence, which tag is "ry"
	    	if (term2str.get(i).contains("ry")){
//	    		System.out.println(term2str.get(i).substring(0,term2str.get(i).indexOf("/") ));
	    		question = term2str.get(i);
	    	}
	    }
//	    System.out.println(entity);
	    Tuple.put("Entity", entity);
//	    System.out.println(question);
	    Tuple.put("QW", question);
//	    find the center word by heuristic rule: finding the none closest to the entity,save in "center" variable
	    String center = "";
	    int index_entity = term2str.indexOf(entity);
//	    save all none words' index in the list<int>
	    List<Integer> index_noun = new ArrayList<Integer>();
	    for(int i=index_entity+1; i<term2str.size(); i++){
	    	if(term2str.get(i).substring(term2str.get(i).length()-2,term2str.get(i).length()).equals("/n") || term2str.get(i).substring(term2str.get(i).length()-3,term2str.get(i).length()).equals("/nz") || term2str.get(i).substring(term2str.get(i).indexOf("/"),term2str.get(i).length()).equals("/nnt")){
	    		index_noun.add(i);
	    	}
	    }
//	    find the closest noun to the entity
//	    use "min_dis" to record the minimum distance btw entity and noun, "min_index" to record the noun's index
	    if(index_noun.size()>0){
		    Integer min_dis = 100;
		    Integer min_index = 0;
		    for(int i=0; i<index_noun.size(); i++){
		    	if ((index_noun.get(i)-index_entity) < min_dis){
		    		min_dis = index_noun.get(i)-index_entity;
		    		min_index = index_noun.get(i);
		    	}
		    }
		    center = term2str.get(min_index);
	    }
//	    System.out.println(min_dis);
//	    System.out.println(min_index);
//	    record the center word in "center" var	   
	    System.out.println(center);
	    Tuple.put("CW", center);
//	    iterate Tuple
	    for(Map.Entry<String, String> entry : Tuple.entrySet()){ 
	    	System.out.printf("%s : %s %n", entry.getKey(), entry.getValue());
	    }
//	    condition 1: (QW,CW,Entity), QT=CW 
	    if(!Tuple.get("CW").equals("") && !Tuple.get("QW").equals("")){
//	    	if center word and question word are "时候" and "什么", then looking for the verb in the sentence
	    	if(Tuple.get("CW").substring(0, Tuple.get("CW").indexOf("/")).equals("时候") && Tuple.get("QW").substring(0, Tuple.get("QW").indexOf("/")).equals("什么")){
//	    		looking for verb except for "有"，"是"
	    		for (int i=0; i< term2str.size(); i++){
	    			if (term2str.get(i).substring(term2str.get(i).indexOf("/")+1, term2str.get(i).length()).contains("v")){
	    				if (!term2str.get(i).substring(term2str.get(i).indexOf("/")+1, term2str.get(i).length()).equals("vshi") && !term2str.get(i).substring(term2str.get(i).indexOf("/")+1, term2str.get(i).length()).equals("vyou")){
	    					Tuple.put("CW",term2str.get(i));
	    				}
	    			}
	    		}
	    		
	    	}
	    	Tuple.put("QT", Tuple.get("CW"));
//	    	handle question sentence "哪里人"
	    	if(Tuple.get("CW").substring(0, Tuple.get("CW").indexOf("/")).equals("人") && Tuple.get("QW").substring(0, Tuple.get("QW").indexOf("/")).equals("哪里")){
	    		Tuple.put("QT", "籍贯/");
	    	}
	    }
//	    condition 2: (CW,Entity), QT=CW
	    if(!Tuple.get("CW").equals("") && Tuple.get("QW").equals("")){
	    	Tuple.put("QT", Tuple.get("CW"));
	    }
//	    condition 3: (QW,Entity), QT= verb
//	    QW == "哪"
	    if(!Tuple.get("QW").equals("") && Tuple.get("CW").equals("")){
//		    if(Tuple.get("QW").substring(0,Tuple.get("QW").indexOf("/")).contains("哪")){
		    	for (int i=0; i< term2str.size(); i++){
	    			if (term2str.get(i).substring(term2str.get(i).indexOf("/")+1, term2str.get(i).length()).contains("v")){
	    				if (!term2str.get(i).substring(term2str.get(i).indexOf("/")+1, term2str.get(i).length()).equals("vshi") && !term2str.get(i).substring(term2str.get(i).indexOf("/")+1, term2str.get(i).length()).equals("vyou")){
	    					Tuple.put("CW",term2str.get(i));
	    					Tuple.put("QT", term2str.get(i));
	    					System.out.println(term2str.get(i));
	    				}
	    			}
	    		}	
//		    }
	    }
//	    condition 4: parsing can only get Entity, QT=rule-based
	    if(Tuple.get("QW").equals("") && Tuple.get("CW").equals("")){
	    	
	    	XMLParse parser = new XMLParse();
	    	
	    	String explain = parser.parseRule(content);
	    	if (explain == null) {
	    		System.out.println("Error: no rule found");
	    	}
	    	
	    	Tuple.put("QT", explain);
//	    	PythonInterpreter interpreter = new PythonInterpreter(); 
//	    	interpreter.execfile("/Users/weiweiduan/Downloads/xmlParse.py");
//	    	BufferedReader brTest = new BufferedReader(new FileReader("/Users/weiweiduan/Downloads/out.txt"));
//	        String text = brTest.readLine();
//	        System.out.println("Firstline is : " + text);
//	        Tuple.put("QT", text);
	    }
	    for(Map.Entry<String, String> entry : Tuple.entrySet()){ 
	    	System.out.printf("%s : %s %n", entry.getKey(), entry.getValue());
	    }
//	    2-Tuple as(QT, Entity) for query QT is property, Entity is class
	    Map<String,String> TupleQuery = new HashMap<>();
	    if (Tuple.get("QT") != ""){
	    	TupleQuery.put("property", Tuple.get("QT").substring(0, Tuple.get("QT").indexOf("/")));
	    }
	    else{
	    	TupleQuery.put("property","");
	    }
	    if (Tuple.get("Entity") != ""){
	    	TupleQuery.put("entity", Tuple.get("Entity").substring(0, Tuple.get("Entity").indexOf("/")));
	    }
	    else{
	    	TupleQuery.put("entity","");
	    }
	    for(Map.Entry<String, String> entry : TupleQuery.entrySet()){ 
	    	System.out.printf("%s : %s %n", entry.getKey(), entry.getValue());
	    }
//	    if hanlp cannot find entity, use fnlp
	    if(TupleQuery.get("entity").equals("")){
	    	String e = fnlpENR(content);
	    	if (e.length() > 2){
	    		TupleQuery.put("entity", e.substring(1, e.indexOf("=")));
	    	}
	    }
	    return TupleQuery;
	}
	
	/**
	 * query all possible entities with regular expression
	 * @param entity
	 * @return
	 */
	public ArrayList<String> getAllPossibleEntities(String entity){
		ArrayList<String> possible_entities = new ArrayList();
		Model m = ModelFactory.createDefaultModel();
        // use the file manager to read an RDF document into the model
        FileManager.get().readModel(m, "sample_instances.owl");
        String sparqlQueryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
				"PREFIX ckb: <http://cbk.org#>\n"+
				"PREFIX ont: <http://ckb.org/ontology/#>\n"+
				"SELECT ?people ?properties ?value\n"+
				"WHERE\n"+
//				"{?people rdfs:label \"劳尔·内托\". "+
				"{?people rdfs:label ?value . "+
				" FILTER regex(?value, \""+ entity +"\",'i')}";
        Query query = QueryFactory.create(sparqlQueryString) ;
        QueryExecution qexec = QueryExecutionFactory.create(query,m) ;
        ResultSet results = qexec.execSelect() ;
        for (;results.hasNext();) {
            QuerySolution soln = results.nextSolution();
            RDFNode x = soln.get("value");
            String word = x.toString();
            if(word.indexOf('[') != -1){
            	String temp = word.substring(word.indexOf("[")+1, word.indexOf("]"));
            	if (temp.indexOf(entity) != -1) continue;
            }
            possible_entities.add(x.toString());
//            System.out.println(x);
        }
        return possible_entities;
	}
	
	/**
	 * get all properties of the entity in knowledge base
	 * @param entity
	 * @return
	 */
	public ArrayList<String> getAllProperties(String entity){
		// TODO Auto-generated method stub
		Model m = ModelFactory.createDefaultModel();
        // use the file manager to read an RDF document into the model
        FileManager.get().readModel(m, "sample_instances.owl");
        String sparqlQueryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
				"PREFIX ckb: <http://cbk.org#>\n"+
				"PREFIX ont: <http://ckb.org/ontology/#>\n"+
				"SELECT ?people ?properties ?value\n"+
				"WHERE\n"+
//				"{?people rdfs:label \"劳尔·内托\". "+
				"{?people rdfs:label \"" + entity + "\". "+
				"?people ?properties ?value }" ;
        Query query = QueryFactory.create(sparqlQueryString) ;
        QueryExecution qexec = QueryExecutionFactory.create(query,m) ;
        ResultSet results = qexec.execSelect() ;
//      add all properties concerning the object in an Arraylist
        ArrayList<String> list = new ArrayList<String>();
        for (;results.hasNext();) {
            QuerySolution soln = results.nextSolution();
            RDFNode x = soln.get("properties");
            String r = x.asNode().getLocalName();
            if (!r.equals("type")){
            list.add(r);
            }
        }
//      print all the property
        for (int i = 0;i<list.size();i++){
	    	System.out.println(list.get(i));
	    }
    	System.out.println();

        return list;		
	}
	
	/**
	 * return the similar property found in the synonyms dict
	 * @param prop
	 * @return
	 */
	public String getSimilarProperty(String prop, ArrayList<String> all_properties){
		String word = "";
		SynonymsDict SD = new SynonymsDict();
		ArrayList<String> synos = SD.getRangeSynonyms(prop, false);
		
		Set<String> all_props = new HashSet<String>(all_properties);
		for(String syn : synos){
			if(all_props.contains(syn)){
				word = syn;
				break;
			}
		}
		return word;	
	}
	
	/**
	 * query the knowledge base to get the answer
	 * @param entity
	 * @param property
	 * @return
	 */
	public String answerRetrieve(String entity, String property){
		System.out.println("Qeury:" + entity + "  " + property);

		String answer = "";
		Model m = ModelFactory.createDefaultModel();
        // use the file manager to read an RDF document into the model
        FileManager.get().readModel(m, "sample_instances.owl");
        String sparqlQueryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
				"PREFIX ckb: <http://cbk.org#>\n"+
				"PREFIX ont: <http://ckb.org/ontology/#>\n"+
				"SELECT ?value\n"+
				"WHERE\n"+
//				"{?people rdfs:label \"劳尔·内托\". "+
				"{?people rdfs:label \""+ entity +"\". "+
				"?people ont:" + property + " ?value}";
        Query query = QueryFactory.create(sparqlQueryString) ;
        QueryExecution qexec = QueryExecutionFactory.create(query,m) ;
        ResultSet results = qexec.execSelect() ;
        for (;results.hasNext();) {
            QuerySolution soln = results.nextSolution();
            RDFNode x = soln.get("value");
            answer = x.toString();
            break;
        }
        if(answer.indexOf("http:") != -1){
        	answer = answer.substring(answer.indexOf('#') + 1);
        }
        
        return answer.equals("") ? "find no answer #4" : answer;
	}
	
	/**
	 * FNLP entity recognition
	 * @param sentence
	 * @return
	 * @throws LoadModelException
	 * @throws JDOMException
	 */
	public String fnlpENR(String sentence) throws LoadModelException, JDOMException {
	    // 创建中文处理工厂对象，并使用“models”目录下的模型文件初始化
	    CNFactory factory = CNFactory.getInstance("models");

	    // 使用标注器对包含实体名的句子进行标注，得到结果
	    HashMap<String, String> result = factory.ner(sentence);

	    // 显示标注结果
	    return result.toString();
	}
//	analyze question sentence

	
	public static void main(String[] args) throws IOException, LoadModelException, JDOMException{
//		QuestionGenerator qg = new QuestionGenerator();
//		String q = qg.nextQuestion();

		Answerretrieval ar = new Answerretrieval();
		System.out.println(ar.ask("姚明的爷爷是谁"));
//		System.out.println(ar.parseQuestion(q));
	}

}
