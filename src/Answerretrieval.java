import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CoreSynonymDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.python.util.PythonInterpreter;
public class Answerretrieval {

//	analyze question sentence
	public static Map<String,String> Question (String str) throws IOException{
//		String content = "丁海的爱好什么？";
		String content = str;
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
	    for(int i=index_entity; i<term2str.size(); i++){
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
	    		Tuple.put("QT", "国籍,籍贯");
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
//	    	PythonInterpreter interpreter = new PythonInterpreter(); 
//	    	interpreter.execfile("/Users/Lu/Desktop/xmlParse.py");
	    	BufferedReader brTest = new BufferedReader(new FileReader("/Users/Lu/Desktop/out.txt"));
	        String text = brTest.readLine();
	        System.out.println("Firstline is : " + text);
	        Tuple.put("QT", text);
	    }
	    for(Map.Entry<String, String> entry : Tuple.entrySet()){ 
	    	System.out.printf("%s : %s %n", entry.getKey(), entry.getValue());
	    }
//	    2-Tuple as(QT, Entity) for query QT is property, Entity is class
	    Map<String,String> TupleQuery = new HashMap<>();
	    TupleQuery.put("property", Tuple.get("QT").substring(0, Tuple.get("QT").indexOf("/")));
	    TupleQuery.put("class", Tuple.get("Entity").substring(0, Tuple.get("Entity").indexOf("/")));
	    for(Map.Entry<String, String> entry : TupleQuery.entrySet()){ 
	    	System.out.printf("%s : %s %n", entry.getKey(), entry.getValue());
	    }
	    return TupleQuery;

	}
	public static ArrayList<String> getAllProperty(String key) {
		// TODO Auto-generated method stub
		Model m = ModelFactory.createDefaultModel();
        // use the file manager to read an RDF document into the model
        FileManager.get().readModel(m, "sample_instances.owl");
        String sparqlQueryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
				"PREFIX ckb: <http://cbk.org#>\n"+
				"PREFIX ont: <http://ckb.org/ontology/#>\n"+
				"SELECT ?people ?properties ?value\n"+
				"WHERE\n"+
				"{?people rdfs:label \"" + key + "\". "+
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
        return list;
	}
	
//	calculate which property is the most likely that the user query
	public static String similarProperty(String QT,List<String> Property){
		List<Long> SimilarityScore = new ArrayList<Long>();
		for (String b : Property) {
		        SimilarityScore.add(CoreSynonymDictionary.distance(QT, b));
		}
		Long i = Collections.min(SimilarityScore);
		Integer index_min = SimilarityScore.indexOf(i);
		return Property.get(index_min);
	}
	
	public static void queryAnswer(String key,String Prpty){
		Model m = ModelFactory.createDefaultModel();
        // use the file manager to read an RDF document into the model
        FileManager.get().readModel(m, "sample_instances.owl");
        String sparqlQueryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
				"PREFIX ckb: <http://cbk.org#>\n"+
				"PREFIX ont: <http://ckb.org/ontology/#>\n"+
				"SELECT ?people ?properties ?value\n"+
				"WHERE\n"+
//				"{?people rdfs:label \"劳尔·内托\". "+
				"{?people rdfs:label"+ key +". "+
				"?people ont:" + Prpty + "?value}";
        Query query = QueryFactory.create(sparqlQueryString) ;
        QueryExecution qexec = QueryExecutionFactory.create(query,m) ;
        ResultSet results = qexec.execSelect() ;
        for (;results.hasNext();) {
            QuerySolution soln = results.nextSolution();
            RDFNode x = soln.get("value");
            System.out.println(x);
        }
	}
	
	public static void main(String[] args) throws IOException{
		List<String> propertyList = new ArrayList<String>();
		propertyList = getAllProperty("劳尔·内托") ;
		for (String temp : propertyList){
			System.out.println(temp);
		}
//		queryAnswer("\"劳尔·内托\"",propertyList.get(0));
//		Map<String,String> QuestionParsing = Question("丁海有多高");
//		System.out.println("\""+QuestionParsing.get("class")+"\"");
//		List<String> AllProperty = getAllProperty("\""+QuestionParsing.get("class")+"\"");
//		String PropertyQuery = similarProperty(QuestionParsing.get("property"),AllProperty);
//		queryAnswer("\""+QuestionParsing.get("class")+"\"",PropertyQuery);
	}

}
