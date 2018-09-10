import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.*;

public class SuffixTree {
	private
		int L, r;
		float pmin;
	
		List<Object> input;
		List<Object> output;
		
		Map<Object, Integer> dictionary;
		
		Map<List<Object>, Node> nodeDictionary;
		
	public
		SuffixTree() {}
		
		SuffixTree(ArrayList<Object> input, int L, float pmin, int r) {
			this.input = input;
			this.L = L;
			this.pmin = pmin;
			this.r = r;
			this.dictionary = new HashMap<Object, Integer>();
			this.nodeDictionary = new HashMap<List<Object>, Node>();
		}
		
		void train() {
			makeDictionary();
			filter();
			buildTree();
			calculateGoToProbs();
		}
		
		void makeDictionary() {
			input.add("$");
			
			for(int h = L; h > 0; h--) {
				for(int i = 0; i < input.size() - (h - 1); i++) {
					Object motif = input.subList(i, i + h);
					
					if(dictionary.containsKey(motif)) {
						dictionary.replace(motif, dictionary.get(motif), dictionary.get(motif) + 1);
					}
					
					else {
						dictionary.put(new ArrayList<Object>(input.subList(i, i + h)), 1);
					}
				}
			}
		}
		
		void filter() {
			
			dictionary.forEach(
					
				(k,v) -> {
					
					Node tempNode = new Node((List<Object>)k);
					tempNode.setEmpProb((float)v / (float)(input.size() - ((List<Object>)k).size() + 1));
					
					if(tempNode.getMotif().size() > 1) { 
						float a = tempNode.getEmpProb();
						float aAndB = dictionary.get(tempNode.getMotif().subList(1, tempNode.getMotif().size())) / (float)(input.size() - ((List<Object>)k).size() + 1);
						
						tempNode.setCondProb(aAndB / a);
					}
					
					if(shouldKeep(tempNode)) {
						nodeDictionary.put(tempNode.getMotif(), tempNode);
					}
				}
					
			);
			
		}
		
		void buildTree() {	
			Iterator it = nodeDictionary.entrySet().iterator();
			
			while(it.hasNext()) {
				HashMap.Entry pair = (HashMap.Entry) it.next();
				
				List<Object> currentMotif = (List<Object>)pair.getKey();
				Node currentNode = new Node(currentMotif);
				
				if(canBeNextNode(currentNode)) {
					connectNodes(currentNode);
				}
			}
		}
		
		void calculateGoToProbs() {
			String str1 = input.toString().replaceAll("([\\[|\\] ,  ])", "");
			
			nodeDictionary.forEach(
					
				(k1,v1) -> {
					
					dictionary.forEach(
							
						(k2,v2) -> {
							if(((List<Object>)k2).size() == 1) {
								
								String str2 = nodeDictionary.get(k1).getMotif().toString().replaceAll("([\\[|\\] ,  ])", "").concat(k2.toString().replaceAll("([\\[|\\] ,  ])", ""));
								
								float frequency = (float)StringUtils.countMatches(str1, str2) / (float)(input.size() - str2.length() + 2);
								
								nodeDictionary.get(k1).getGoesTo().put((List<Object>)k2, frequency / nodeDictionary.get(k1).getEmpProb());
							
							}
						}
						
					);
				}
				
			);
		}
		
		void printTree() {
			nodeDictionary.forEach(
					
				(k, v) -> {
					System.out.println("node: " + v.getMotif());
					System.out.println("empProb: " + v.getEmpProb());
					
//					if(v.getNextNodes().size() > 0) {
//						for(Node o: v.getNextNodes()) {
//							System.out.println("	subNode: " + o.getMotif());
//							System.out.println("");
//						}
//					}
					
					v.getGoesTo().forEach(
						
						(l, n) -> {
							System.out.println("	Goes to:" + l);
							System.out.println("	Prob:" + n);
							System.out.println("");
						}
						
					);
				}	
					
			);
		}
		
		void generate() {
			output = new ArrayList<Object>();
			
			Node currentNode = new Node();
			
			currentNode = generateFromEmptyString();
			
			//this needs to be able to traverse the tree correctly and handle empty context/infinite loop cases
			while(!(output.get(output.size() - 1) == "$") && currentNode != null) {
				
				
				currentNode = generateNextNode(currentNode);
			}
			
			System.out.println(output);
		}
		
		Node generateFromEmptyString() {
			Random rand = new Random();
			
			float random = rand.nextFloat();
			
			float sum = 0f;
			
			Iterator it = dictionary.entrySet().iterator();
			
			while(it.hasNext()) {
				HashMap.Entry pair = (HashMap.Entry) it.next();
				
				if(pair.getKey().toString().length() == 3) {
					
					float toAdd = ((Integer)pair.getValue()).floatValue() / (float)input.size();
					sum += toAdd;
				
					if(random < sum) {
						//this should be adding the last motif - but it don't
						output.addAll((List<Object>) pair.getKey());
						
						return nodeDictionary.get((List<Object>) pair.getKey());
					}
				}
			}
			
			return null;
		}
		
		Node generateNextNode(Node currentNode) {
			Random rand = new Random();
			
			float random = rand.nextFloat();
			
			float sum = 0f;
			
			Iterator it = currentNode.getGoesTo().entrySet().iterator();
			
			while(it.hasNext()) {
				HashMap.Entry pair = (HashMap.Entry) it.next();
				
				float toAdd = (float)pair.getValue();
				sum += toAdd;
			
				if(random < sum) {
					//this should be adding the last motif - but it don't
					output.addAll((List<Object>) pair.getKey());
					
					if(output.size() > currentNode.getMotif().size()) {
						if(currentNode.getNextNodes().containsKey(output.subList(output.size() - currentNode.getMotif().size() - 1, output.size()))) {
							currentNode = currentNode.getNextNodes().get(output.subList(output.size() - currentNode.getMotif().size(), output.size()));
						}
					}
					
					return nodeDictionary.get((List<Object>) pair.getKey());
				}
			}
			
			return null;
		}
		
		void connectNodes(Node node) {
			Node parentNode = new Node((List<Object>) node.getMotif().subList(0, node.getMotif().size() - 1));
			
			nodeDictionary.get(parentNode.getMotif()).getNextNodes().put(node.getMotif(), node);
		}
		
		boolean canBeNextNode(Node node) {
			return node.getMotif().size() > 1 && nodeDictionary.containsKey(node.getMotif().subList(0, node.getMotif().size() - 1));
		}

		Map<Object, Integer> getDictionary() {
			return dictionary;
		}

		void setDictionary(Map<Object, Integer> dictionary) {
			this.dictionary = dictionary;
		}
		
		boolean shouldKeep(Node node) {
			return node.getEmpProb() > pmin && (node.getCondProb() > r || node.getCondProb() == 0.0);
		}
		
}
