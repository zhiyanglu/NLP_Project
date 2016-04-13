package src;


import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import java.util.ArrayList;
import java.util.List;
public class Answerretrieval {

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
//				"{?people rdfs:label \"劳尔·内托\". "+
				"{?people rdfs:label" + key + ". "+
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
	public static String similarProperty(String QC){
		return "None";
	}
	
	public static void queryAnswer(String key,String Prpty){
		Model m = ModelFactory.createDefaultModel();
        // use the file manager to read an RDF document into the model
        FileManager.get().readModel(m, "all_instances.owl");
        String sparqlQueryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
				"PREFIX ckb: <http://cbk.org#>\n"+
				"PREFIX ont: <http://ckb.org/ontology/#>\n"+
				"SELECT ?people ?properties ?value\n"+
				"WHERE\n"+
//				"{?people rdfs:label \"劳尔·内托\". "+
				"{?people rdfs:label ?x . "+
				"?people ont:" + Prpty + "?value " +
				"FILTER regex(?x,\"" + key + "\")}";
        Query query = QueryFactory.create(sparqlQueryString) ;
        QueryExecution qexec = QueryExecutionFactory.create(query,m) ;
        ResultSet results = qexec.execSelect() ;
        for (;results.hasNext();) {
            QuerySolution soln = results.nextSolution();
            RDFNode x = soln.get("value");
            System.out.println(x);
        }
	}
	
	public static void main(String[] args){
		ArrayList<String> propertyList = new ArrayList<String>();
		propertyList = getAllProperty("\"劳尔·内托\"") ;
		for (String temp : propertyList){
			System.out.println(temp);
		}
		queryAnswer("\"劳尔·内托\"",propertyList.get(0));
	}

}
