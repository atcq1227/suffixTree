
/* 
 * Matt Shaw 2017
creates a prediction suffix tree based on an array of elements passed in from main()
by first building the tree then finding motives of length L by eliminating nodes
based on thresholds of empirical and conditional probability.
 */

import java.util.*;

public class SuffixTree<E> {
	
	public SuffixTree() {}
	
	public SuffixTree(int L) {
		this.L = (int) L;
	}
	
	public SuffixTree(int L, float pMin, float r) {
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
	
	private boolean isTheSame;
	private boolean isDifferent;
	
	private int[] matchCoord = new int[2];
	
	
	void train(ArrayList <E> elements) {
		//builds tree
		buildTree(elements);
		
		//builds singleGeneratedElements from compressed list to see if it's the same as the original
		convertFromCompressedList(compressedList);
		
		//if it's the same, print "true"
		System.out.println(isTheSame(elements));
		
		//corrupt the tree by changing each even node to the previous odd numbered node
		backtrackEven(treeRows);
		
		//randomize each odd numbered node
		randomOdd(treeRows);
		
		//convert again to see if it's different
		convertFromCompressedList(compressedList);
		
		//if it's different, print "true"
		System.out.println(isDifferent(elements));
	}
	
	public void isTheSame() {
		System.out.println(isTheSame);
	}
	
	public void isDifferent() {
		System.out.println(isDifferent);
	}
	
	private void convertFromCompressedList(ArrayList<int[]> compressedList) {
		//add to singleGeneratedElements based on suffix tree coordinates
		for(int i = compressedList.size() - 1; i > -1; i--) {
			singleGeneratedElements.add(treeRows.get(compressedList.get(i)[0]).get(compressedList.get(i)[1]).getMotive().get(treeRows.get(compressedList.get(i)[0]).get(compressedList.get(i)[1]).getMotive().size() - 1));
		}
	}
	
	private void backtrackEven(ArrayList<ArrayList<Node<E>>> treeRows) {
		for(int i = 1; i < treeRows.size(); i++) {
			for(int j = 0; j < treeRows.get(i).size(); j++) {
				if(j % 2 == 1) {
					E doop = treeRows.get(i).get(j - 1).getMotive().get(0);
					treeRows.get(i).get(j).getMotive().set(0, doop);
				}
			}
		}
	}
	
	private void randomOdd(ArrayList<ArrayList<Node<E>>> treeRows) {
		Random rand = new Random();
		for(int i = 1; i < treeRows.size(); i++) {
			for(int j = 0; j < treeRows.get(i).size(); j++) {
				if(j % 2 == 0) {
					byte[] randBytes = new byte[1];
					rand.nextBytes(randBytes);
					ArrayList<Byte> randBytesList = new ArrayList<Byte>();
					randBytesList.add(randBytes[0]);
					treeRows.get(i).get(j).getMotive().set(0, (E)randBytesList.get(0));
				}
			}
		}
	}
	
	private boolean isTheSame(ArrayList<E> elements) {
		boolean isTheSame = true;
		
		//compare the lists
		for(int i = 0; i < elements.size() - 5; i++) {
			if(!(elements.get(i) == singleGeneratedElements.get(i))) {
				isTheSame = false;
			}
		}
		
		return isTheSame;
	}
	
	private boolean isDifferent(ArrayList<E> elements) {
		boolean isDifferent = false;
		
		//compare the lists
		for(int i = 0; i < elements.size(); i++) {
			if(!(elements.get(i) == singleGeneratedElements.get(i))) {
				isDifferent = true;
			}
		}
		
		return isDifferent;
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
					
					//add coordinates of new node to compressedList
					compressedList.add(new int[2]);
					compressedList.get(compressedList.size() - 1)[0] = h;
					compressedList.get(compressedList.size() - 1)[1] = treeRows.get(h).size() - 1;
				}
				
				else if(isANode(tempArray, h)) {
					
					//add coordinates of exist node to compressedList
					compressedList.add(matchCoord);
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
