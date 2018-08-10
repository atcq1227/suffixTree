import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class SuffixTree {
	private
		List<Object> input;
		List<Node> nodes;
		Map<Object, Integer> dictionary;
		
		int L, r;
		float pmin;
		
	public
		SuffixTree() {}
		
		SuffixTree(ArrayList<Object> input, int L, float pmin, int r) {
			this.input = input;
			this.L = L;
			this.pmin = pmin;
			this.r = r;
			this.dictionary = new HashMap<Object, Integer>();
		}
		
		void makeDictionary() {
			
			for(int h = L + 1; h > 0; h--) {
				for(int i = 0; i < input.size() - (h - 1); i++) {
					Object motif = input.subList(i, i + h);
					
					if(dictionaryKeysContains(motif)) {
						dictionary.replace(motif, dictionary.get(motif), dictionary.get(motif) + 1);
					}
					
					else {
						dictionary.put(new ArrayList<Object>(input.subList(i, i + h)), 1);
					}
				}
			}
			
		}
		
		void filter() {
			filterOnEmpProb();
			filterOnCondProb();
		}
		
		void filterOnEmpProb() {
			
			Iterator it = dictionary.entrySet().iterator();
			nodes = new ArrayList<>();
			
			while(it.hasNext()) {
				HashMap.Entry pair = (HashMap.Entry) it.next();
				
				float empProb = Integer.parseInt(pair.getValue().toString()) / (float)input.size();
				
				if(empProb >= pmin && ((List<Object>) pair.getKey()).size() <= L) {
					nodes.add(new Node(pair.getKey(), empProb));
				}
				
				it.remove();
			}
			
			for(Node n:nodes) {
				System.out.println(n.getMotif());
			}
			
		}
		
		void filterOnCondProb() {

		}
		
		private boolean dictionaryKeysContains(Object obj) {
			return dictionary.containsKey(obj);
		}

		List<Node> getNodes() {
			return nodes;
		}

		void setNodes(List<Node> nodes) {
			this.nodes = nodes;
		}

		Map<Object, Integer> getDictionary() {
			return dictionary;
		}

		void setDictionary(Map<Object, Integer> dictionary) {
			this.dictionary = dictionary;
		}
		
}
