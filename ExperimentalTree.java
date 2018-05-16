

/* 
 * Matt Shaw 2017
creates a prediction suffix tree based on an array of elements passed in from main()
by first building the tree then finding motives of length L by eliminating nodes
based on thresholds of empirical and conditional probability.
 */

import java.util.*;

public class Experiment<E> {
	
	public Experiment() {}
	
	public Experiment(int L, float pMin, float r) {
		this.L = (int) L;
		this.pMin = (float) pMin;
		this.r = (float) r;
	}

	private int L;
	private float pMin;
	private float r;
	
	//smoothing factor. must be less than 1/elementsInFile.size()
	private float pseudoCount = (float)0.000001;
	
	//controls length of output
	private int generateLength = 1000;
	
	private ArrayList<ArrayList<Node<E>>> treeRows;
	private ArrayList<E> elementsInFile;
	private ArrayList<ArrayList<E>> generatedElements = new ArrayList<ArrayList<E>>();
	private ArrayList<E> singleGeneratedElements = new ArrayList<E>();
	private ArrayList<int[]> compressedList = new ArrayList<int[]>();
	private int compressedIndex = 0;
	
	private int[] matchCoord = new int[2];
	
	private int v = 0;
	
	
	void train(ArrayList <E> elements) {
		buildTree(elements);
		
		for(int j = 0; j < 100; j++) {
			System.out.println("j: " + j);
			System.out.println(elements.get(j));
		}
	}
	
	private void debug() {
		for(int h = 0; h < L + 1; h++) {
			for(int i = 0; i < treeRows.get(h).size(); i++) {
				System.out.println(treeRows.get(h).get(i).isASuffix());
				System.out.println(treeRows.get(h).get(i).getMotive());
			}
		}
	}
	
	private void buildTree(ArrayList <E> elements) {
		
		//newly initialize elementsInFile
		elementsInFile = new ArrayList <E>();
		
		//newly initialize tree
		treeRows = new ArrayList<ArrayList<Node<E>>>();
		treeRows.add(new ArrayList<Node<E>>());
		
		//add empty string motive
		treeRows.get(0).add(new Node<E>());
		
		//do the dirty work of building the trees with newly initialized stuff
		waterTree(elements);
	}
	
	private void calculateEmpProb(ArrayList <E> elements) {
		
		tallyElements(elements); 
		
		keepPassed();
	}
	
	private void calculateCondProb(ArrayList<E> elements) {
			
			for(int h = 2; h < L + 1; h++) {
				for(int i = 0; i < treeRows.get(h).size(); i++) {
					count(h, i, elements);
				}
			}
			
			for(int h = 2; h < L + 1; h++) {
				retainPassed(h);
			}
	}
	
	public void generateLots(int genLength)
	{
		ArrayList<E> isThisElemMe = new ArrayList<E>();
		Node<E> emptyStringNode = treeRows.get(0).get(0);
		ArrayList<E> tempArray = new ArrayList<E>();
		
		//generate a seed from the emptyString
		isThisElemMe = emptyStringNode.generate(isThisElemMe, elementsInFile, generatedElements, emptyStringNode);
		
		//create genLength number of elements
		for(int i = 0; i < genLength; i++) {
			//add the last generated element to generatedElements
			generatedElements.add(isThisElemMe);
			
			//create a tempArray that creates a subList of size L from the lastest elements in generatedelements
			if(generatedElements.get(i).size() > 0) {
				tempArray.add(generatedElements.get(i).get(0));
			}
			
			//keep the size of tempArray in check
			if(generatedElements.size() > L - 1 && tempArray.size() > L -1) {
				tempArray.remove(0);
			}
			
			//now generate the next symbol by passing in tempArray
			isThisElemMe = emptyStringNode.generate(tempArray, elementsInFile, generatedElements, emptyStringNode);
		}
		
		for(int i = 0; i < generatedElements.size(); i++) {
			for(int j = 0; j < generatedElements.get(i).size(); j++) {
				singleGeneratedElements.add(generatedElements.get(i).get(j));
			}
		}
		
		//System.out.println(generatedElements);
	}
	
	private void calculateGoToProbs(ArrayList<E> elements) {
		countAndSet(elements);
		//smoothData();
	}
	
	private void printTree() {
		
		//iterate through rows and nodes and print motives
		for(int h = 0; h < L + 1; h++) {
			
			System.out.println("Tree Row: " + h);
			
			for(int i = 0; i < treeRows.get(h).size(); i++) {
				
				System.out.println("Node: " + treeRows.get(h).get(i).getMotive());
				
				for(int j = 0; j < treeRows.get(h).get(i).getSubNode().size(); j++) {
					
					if(!(treeRows.get(h).get(i).getSubNode().get(j) == null)) {
						System.out.println("Subnode: " + treeRows.get(h).get(i).getSubNode().get(j).getMotive());

					}
				}
			}
		}
	}
	
	private void waterTree(ArrayList<E> elements) {
		for(int h = 1; h < L + 1; h++) {
			
			//add unique elements for each row
			addNewElements(h, elements);
			
//			//alphabetize elements
//			alphabetize(elementsInFile);
			
			makeRows(h, elements);
			
			//add remaining elements as subnodes
			for(int i = 0; i < treeRows.get(h).size(); i++) {
				if(h > 1) {
					//add these as subnodes to elements that passed
					for(int j = 0; j < treeRows.get(h - 1).size(); j++) {
						if(treeRows.get(h).get(i).getMotive().contains(treeRows.get(h - 1).get(j).getMotive())) {
							System.out.println("hit");
							treeRows.get(h - 1).get(j).addSubNode(treeRows.get(h).get(i));
						}
					}
				}
			}
		}
	}
	
	private void addNewElements(int h, ArrayList<E> elements) {
		//add new row to tree
		treeRows.add(new ArrayList<Node<E>>());
		
		for(int i = elements.size() - 1; i > -1 + h; i--) {
			
			//add elements that are not already in file
			if(!isInFile(elements.get(i))) {
				elementsInFile.add(elements.get(i));
			}
		}
	}
	
	private void buildCompressedList(ArrayList<E> elements) {
		int elementPos = 0;
		
		boolean notFound = true;
		
		while(notFound) {
			
		}
		
	}

	private void makeRows(int h, ArrayList<E> elements) {
		int elementsPos = 0;
		
		//add newly alphabetized elements as subNode to empty string
		if(h <= 1) {
			for(int i = 0; i < elementsInFile.size(); i++) {
				ArrayList<E> tempArray = new ArrayList<E>();
				tempArray.add(elementsInFile.get(i));
				Node<E> tempNode = new Node<E>(tempArray);
				treeRows.get(h).add(tempNode);
			}
		}
		
		//add rows for L < 0
		if(h > 1) {
			for(int i = elements.size() - 1; i > -2 + h; i--) {
				ArrayList<E> tempArray = new ArrayList<E>();
				
				for(int j = 0; j < h; j++) {
					tempArray.add(elements.get(i-j));
				}
				
				if(!isANode(tempArray, h)) {
					treeRows.get(h).add(new Node<E>(tempArray));
					System.out.println(h);
				}
			}
		}
		
		//add remaining elements as subnodes
				for(int i = 0; i < treeRows.get(h).size(); i++) {
					if(h > 1) {
						//add these as subnodes to elements that passed
						for(int j = 0; j < treeRows.get(h - 1).size(); j++) {
							System.out.println("h: " + treeRows.get(h).get(i).getMotive());
							System.out.println("h-1: " +treeRows.get(h - 1).get(j).getMotive());
							if(treeRows.get(h).get(i).getMotive().contains(treeRows.get(h - 1).get(j).getMotive())) {
								System.out.println("hit");
								treeRows.get(h - 1).get(j).addSubNode(treeRows.get(h).get(i));
							}
						}
					}
				}
	}
	
	private void tallyElements(ArrayList<E> elements) {
		for(int h = 1; h < L + 1; h++) {
			for(int i = 0; i < treeRows.get(h).size(); i++) {	
				//initialize new counter for each node
				float counter = (float)0;
				
				for(int j = 0; j < elements.size() - h + 1; j++) {
						
					//new temporary ArrayList to store elements
					ArrayList<E> tempArray = new ArrayList<E>();
							
					//clone elements
					for(int k = 0; k < h; k++) {
						tempArray.add(elements.get(j + k));
					}
							
					//if element shows up in string, tally it
					if (tempArray.equals(treeRows.get(h).get(i).getMotive())) {
						counter = counter + 1;
					}
			
					/*
					set empirical probability to tally divided by elements.size,
					taking into account how L affects sample size
					*/
							
					treeRows.get(h).get(i).setEmpProb(counter/(float)(elements.size() - h + 1));
				}
			}
		}
	}
	
	private void keepPassed() {
		for(int h = 1; h < L + 1; h++) {
			
			//new temporary ArrayList for manipulate-a-ning
			ArrayList<Node<E>> toKeep = new ArrayList<Node<E>>();
			
			for(int i = 0; i < treeRows.get(h).size(); i++) {
				
				/*
				populate temporary array with current treeRow and test whether
				empProb exceeds pMin
				*/
				
				if(treeRows.get(h).get(i).getEmpProb() > pMin) {
					toKeep.add(treeRows.get(h).get(i));
				}
				
			}
			
			//retain all members that pass
			treeRows.get(h).retainAll(toKeep);
		}
	}
	
	public void doUnitTest(String string) {
		
		Experiment<Character> suffixTree = new Experiment<Character>(3, (float)0.1, 2);
		
		//train based on the string formatted for train() to accept
		suffixTree.train(preparedString(string));
	}
	
	private ArrayList<Character> preparedString(String string) {
		String test = string;
		
		char[] charArray1 = test.toCharArray();
		
		ArrayList<Character> charArrayList1 = new ArrayList<Character>();
		
		//turn string into arrayList of characters
		for(int i = 0; i < charArray1.length; i++) {
			charArrayList1.add(charArray1[i]);
		}
		
		return charArrayList1;
	}
	
	private void count(int h, int i, ArrayList<E> elements) {
		//initialize counters to count all the different probabilities
		float counter1 = (float)0;
		float counter2 = (float)0;	
		float counter3 = (float)0;
		float counter4 = (float)0;
		
		//intialize temporary arrays to count with
		ArrayList<E> tempArray = new ArrayList<E>();
		ArrayList<E> tempRow = new ArrayList<E>();
		
		for(int j = 0; j < elements.size() - h + 1; j++) {
			tempArray.clear();
			tempRow.clear();
			
			//populate temporary arrays
			for(int k = 0; k < h; k++) {
				tempArray.add(elements.get(j+k));
				tempRow.add(treeRows.get(h).get(i).getMotive().get(k));
			}
			
			//figure out which element comes after each n+1 order motive
			if(tempArray.equals(tempRow) && treeRows.get(h).get(i).getGoesTo() == null) {
				treeRows.get(h).get(i).setGoesTo(elements.get(j + h));
			}
			
			//count how many times each n+1 order motive COULD 
			//go to that element
			if(tempArray.equals(tempRow)) {
				counter2++;
			}
		}
		
		for(int j = 0; j < elements.size() - h; j++) {
			tempArray.clear();
			tempRow.clear();
			
			//populate temporary arrays
			for(int k = 0; k < h; k++) {
				tempArray.add(elements.get(j + k));
				tempRow.add(treeRows.get(h).get(i).getMotive().get(k));
			}
			
			tempArray.add(elements.get(j+h));
			tempRow.add(treeRows.get(h).get(i).getGoesTo());
			
			//figure out how many times the n+1 order motive DOES 
			//go to that element
			if(tempArray.equals(tempRow)) {
				counter1++;
			}
			
			//convert to n order motive
			tempArray.remove(0);
			tempRow.remove(0);
			
			//figure out how many times the n order motive goes to
			//the goesTo() element
			if(tempArray.equals(tempRow)) {
				counter3++;
			}
		}
		
		for(int j = 0; j < elements.size() - h + 2; j++) {
			tempArray.clear();
			tempRow.clear();
			
			//populate temp arrays
			for(int k = 0; k < h - 1; k++) {
				tempArray.add(elements.get(j + k));
				tempRow.add(treeRows.get(h).get(i).getMotive().get(k + 1));
			}
			
			//figure out how many times the n order motive COULD go to
			//the goesTo() element
			if(tempArray.equals(tempRow)) {
				counter4++;
			}
		}
		
		tempArray.clear();
		
		treeRows.get(h).get(i).setCondProb((counter1/counter2)/(counter3/counter4));
	}
	
	private void retainPassed(int h) {
		
		ArrayList<Node<E>> toKeep = new ArrayList<Node<E>>();
		
		//if conditional probability is greater than r, add to temp array toKeep
		for(int i = 0; i < treeRows.get(h).size(); i++) {
			if(treeRows.get(h).get(i).getCondProb() > r) {
				toKeep.add(treeRows.get(h).get(i));
			}
		}
		
		//keep all elements in toKeep
		treeRows.get(h).retainAll(toKeep);
		
		//add remaining elements as subnodes
		for(int i = 0; i < treeRows.get(h).size(); i++) {
			if(h > 1) {
				//add these as subnodes to elements that passed
				for(int j = 0; j < treeRows.get(h - 1).size(); j++) {
					if(treeRows.get(h - 1).get(j).getMotive().get(h - 2) == treeRows.get(h).get(i).getMotive().get(h - 1)) {
						treeRows.get(h - 1).get(j).addSubNode(treeRows.get(h).get(i));
					}
				}
			}
		}
	}
	
	private boolean isInFile(E element) {
		boolean isInFile = false;
		
		int matchInt = 0;
		
		//check to see if element is in file
		//if it is, set isInFile to true
		for(int j = 0; j < elementsInFile.size(); j++) {
			if(elementsInFile.contains(element)) {
				isInFile = true;
			}
		}
		
		return isInFile;
	}
	
	private boolean isANode(ArrayList<E> motive, int h) {
		boolean isANode = false;
		
		
		//check to see if element is already a node
		//if it is, set isANode to true
		for(int i = 0; i < treeRows.get(h).size(); i++) {
			for(int j = 0; j < treeRows.get(h).size(); j++) {
				if(treeRows.get(h).get(j).getMotive().equals(motive)) {
					isANode = true;
					matchCoord[0] = h;
					matchCoord[1] = j;
				}
			}
		}
		
		return isANode;
	}
	
	void alphabetize(ArrayList<E> elementsInFile) {
		if(elementsInFile.get(0) instanceof Integer) {
			float[] elementsArray = new float[elementsInFile.size()];
			ArrayList<E> tempArray = new ArrayList<E>();
			
			//turn ArrayList into int array
			for(int i = 0; i < elementsInFile.size(); i++) {
				elementsArray[i] = (int)elementsInFile.get(i);
			}
			
			Arrays.sort(elementsArray);
			
			//convert int array back to temp ArrayList
			for(int i = 0; i < elementsInFile.size(); i++) {
				for(int j = 0; j < elementsInFile.size(); j++) {
					if(elementsArray[i] == (int)elementsInFile.get(j)) {
						tempArray.add(elementsInFile.get(j));
						break;
					}
				}
			}
			
			elementsInFile.clear();
			
			//repopulate elementsInFile with sorted values
			for(int i = 0; i < tempArray.size(); i++) {
				elementsInFile.add(tempArray.get(i));
			}
		}
		
		if(elementsInFile.get(0) instanceof Character) {
			List<Character> list = new ArrayList<Character>();
			
			//turn into list so Collections can work with it
			for(int i = 0; i < elementsInFile.size(); i++) {
				list.add((Character)elementsInFile.get(i));
				Collections.sort(list);
			}
			
			elementsInFile.clear();
			
			//repopulate with ordered list
			for(int i = 0; i < list.size(); i++) {
				elementsInFile.add((E)list.get(i));
			}
		}
		
		if(elementsInFile.get(0) instanceof String) {
			List<String> list = new ArrayList<String>();
			
			//turn into list so Collections can work with it
			for(int i = 0; i < elementsInFile.size(); i++) {
				list.add((String)elementsInFile.get(i));
				Collections.sort(list);
			}
			
			elementsInFile.clear();
			
			//repopulate with ordered list
			for(int i = 0; i < list.size(); i++) {
				elementsInFile.add((E)list.get(i));
			}
		}
	}
	
	private void countAndSet(ArrayList<E> elements) {
		
		for(int h = 0; h < treeRows.size(); h++) {
		
			float[] tempArray = new float[elementsInFile.size()];
			
			if(h < 1) {
				for(int i = 0; i < elementsInFile.size(); i++) {
					float counter = 0;
					for(int j = 0; j < elements.size(); j++) {
						if(elementsInFile.get(i) == elements.get(j)) {
							counter++;
						}
					}
					tempArray[i] = counter/elements.size();
				}
				
				treeRows.get(0).get(0).setProbs(tempArray);
			}
			
			if(h > 0) {
				for(int i = 0; i < treeRows.get(h).size(); i++) {
					
					tempArray = new float[elementsInFile.size()];
					
					for(int g = 0; g < elementsInFile.size(); g++) {
						
						float counter = 0;
					
						for(int j = 0; j < elements.size() - h; j++) {
						
							ArrayList<E> tempMotive = new ArrayList<E>();
							ArrayList<E> tempRow = new ArrayList<E>();
						
							for(int k = 0; k < treeRows.get(h).get(i).getMotive().size(); k++) {
								tempMotive.add(treeRows.get(h).get(i).getMotive().get(k));
								tempRow.add(elements.get(j + k));
							}
						
							tempMotive.add(elementsInFile.get(g));
							tempRow.add(elements.get(j+h));
							
						
							if(tempMotive.equals(tempRow)) {
								counter++;
							}
						}
						tempArray[g] = counter/elementCount(elements, h, i);
					}
					treeRows.get(h).get(i).setProbs(tempArray);
				}
			}
		}
		
		//add row 1 as subnodes to emptyString
		for(int i = 0; i < treeRows.get(1).size(); i++) {
			treeRows.get(0).get(0).addSubNode(treeRows.get(1).get(i));
		}
	}
	
	private float elementCount(ArrayList<E> elements, int h, int i) {	
		//initialize new counter for each node
		float counter = (float)0;
				
		for(int j = 0; j < elements.size() - h + 1; j++) {
						
			//new temporary ArrayList to store elements
			ArrayList<E> tempArray = new ArrayList<E>();
							
			//clone elements
			for(int k = 0; k < h; k++) {
				tempArray.add(elements.get(j + k));
			}
						
			//if element shows up in string, tally it
			if (tempArray.equals(treeRows.get(h).get(i).getMotive())) {
				counter = counter + 1;
			}
		}
		return counter;
	}
	
	private void smoothData() {
		ArrayList<float[]> arrays = new ArrayList<>();
		
		int posCounter = 0;
		
		for(int h = 0; h < L + 1; h++) {
			for(int i = 0; i < treeRows.get(h).size(); i++) {
				
				float[] tempArray = new float[elementsInFile.size()];
				
				for(int j = 0; j < elementsInFile.size(); j++) {
					tempArray[j] = (treeRows.get(h).get(i).getProbs()[j] * (1 - (elementsInFile.size() * pseudoCount))) + pseudoCount;
				}
				
				posCounter++;
				
				arrays.add(tempArray);
				
				treeRows.get(h).get(i).setProbs(arrays.get(posCounter - 1));
			}
		}
	}

	public float getPMin() {
		return pMin;
	}

	public void setPMin(float PMin) {
		this.pMin = PMin;
	}

	public int getL() {
		return L;
	}

	public void setL(int L) {
		this.L = L;
	}

	public float getR() {
		return r;
	}

	public void setR(float r) {
		this.r = r;
	}

	public ArrayList<ArrayList<E>> getGeneratedElements() {
		return generatedElements;
	}

	public void setGeneratedElements(ArrayList<ArrayList<E>> generatedElements) {
		this.generatedElements = generatedElements;
	}
	
	public ArrayList<E> getSingleGeneratedElements() {
		return singleGeneratedElements;
	}

	public void setSingleGeneratedElements(ArrayList<E> singleGeneratedElements) {
		this.singleGeneratedElements = singleGeneratedElements;
	}
	
	public int getGenerateLength() {
		return generateLength;
	}

	public void setGenerateLength(int generateLength) {
		this.generateLength = generateLength;
	}

	public ArrayList<int[]> getCompressedList() {
		return compressedList;
	}

	public void setCompressedList(ArrayList<int[]> compressedList) {
		this.compressedList = compressedList;
	}

}
