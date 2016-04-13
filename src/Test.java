package cs544;

import java.util.List;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.CRF.CRFSegment;
import com.hankcs.hanlp.seg.common.Term;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub		
		QuestionSet set = new QuestionSet("/Users/Lu/Desktop/544_project/question_XML_Sample.xml");
		for(int i = 0; i < 600; i++){
			Question q = set.getList().get(i);
			String content = q.getSencente();
			List<String> keywordList = HanLP.extractKeyword(content, 5);
			System.out.println(content);
			System.out.println(keywordList);
			System.out.println();
		}
		
	}

}
