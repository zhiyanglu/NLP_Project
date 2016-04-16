package src;

import java.util.List;

import org.w3c.dom.Node;

public class Word {
	private String cont;
	private String POS;
	private String ne;
	private String wsd;
	private String[] wsdexp;
	private String parent;
	private String relate;

	public Word(Node word){
		this.cont = word.getAttributes().getNamedItem("cont").getNodeValue();
		this.POS = word.getAttributes().getNamedItem("pos").getNodeValue();
		this.ne = word.getAttributes().getNamedItem("ne").getNodeValue();
		this.wsd = word.getAttributes().getNamedItem("wsd").getNodeValue();
		this.wsdexp = word.getAttributes().getNamedItem("wsdexp").getNodeValue().split("_");
		this.parent = word.getAttributes().getNamedItem("parent").getNodeValue();
		this.relate = word.getAttributes().getNamedItem("relate").getNodeValue();
	}
	
	public String getPos(){
		return this.POS;
	}
	public String getCont(){
		return this.cont;
	}
	public void print(){
		this.print_cont();
	}
	public void print_cont(){
		System.out.println(this.cont);
	}

}
