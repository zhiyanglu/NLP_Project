import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.util.IteratorIterable;

public class XMLParse {
	
	public File inputFile;
	SAXBuilder jdomBuilder;
	Document jdomDocument;
			
	public XMLParse() throws JDOMException, IOException {
		inputFile = new File("src/rule.xml");
		jdomBuilder = new SAXBuilder();
		jdomDocument = jdomBuilder.build(inputFile);		
	}
	
	public String parseRule(String content) {
		Element root = jdomDocument.getRootElement();
		List<Element> ruleList = root.getChildren();
		
		for (int i = 0; i < ruleList.size(); i++) {
			String explain = ruleList.get(i).getChildText("explain");
			Element unit = ruleList.get(i).getChild("condition").getChild("unit");
			
			String operate = unit.getChildText("operate");
			String offset = unit.getChildText("object1");
			String object2 = unit.getChildText("object2");
			
			int idx = content.indexOf(operate);
			
			if (idx != -1 && content.indexOf(object2) == idx + Integer.parseInt(offset) + (operate.length() - 1)) {
				return explain + "/";
			}
		}
		return null;
	}
	
	
	public static void main(String[] args) throws JDOMException, IOException {	
		
		
		XMLParse mp = new XMLParse();
		System.out.println(mp.parseRule("姚明是干什么的"));
		
		
//		File inputFile = new File("src/rule.xml");
//        SAXBuilder jdomBuilder = new SAXBuilder();
//  
//        // jdomDocument is the JDOM2 Object
//        Document jdomDocument = jdomBuilder.build(inputFile);
//  
//        System.out.println(jdomDocument.getRootElement().getName()); 
//        Element root = jdomDocument.getRootElement();
//  
//        // The Element class extends Content class which is NamespaceAware. We
//        // see what namespace this element introduces.
//        // the getContent method traverses through the document and gets all the
//        // contents. We print the CType (an enumeration identifying the Content
//        // Type), value and class of the Content. we print only the
//        // first two values, since this is only an example.
//        
//        List<Content> rootContents = root.getContent();
//        List<Element> rootElements = root.getChildren();
//        
//        
//        for (int i = 0; i < rootElements.size(); i++) {
//        	System.out.println(rootElements.get(i).getText());
//        }
        
//        int len = rootContents.size();
//        for (int i = 0; i < len; i++) {
//            Content content = rootContents.get(i);
////            System.out.println("CType " + content.getCType());
////            System.out.println("Class " + content.getClass());
//            System.out.println("content:" + content.getValue().trim());
//        }
    }
}
