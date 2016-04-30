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
	
	
//	public static void main(String[] args) throws JDOMException, IOException {			
//		XMLParse mp = new XMLParse();
//		System.out.println(mp.parseRule("姚明是干什么的"));
//    }
}
