import java.util.ArrayList;
import java.util.Arrays;

public class BigGenerator {

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		
		String str = "abracadabra";
		
		SuffixTree tree = new SuffixTree(new ArrayList<Object>(Arrays.asList(str.split(""))), 3, (float)0.1, 2);
		
		tree.makeDictionary();
		tree.filterOnEmpProb();
		
		System.out.println("elapsed time: " + (System.currentTimeMillis() - startTime));
	}

}
