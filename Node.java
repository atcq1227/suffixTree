

//Matt Shaw 2017

import java.awt.List;
import java.util.ArrayList;
import java.util.Random;

public class Node<E> {

	Node(){}
	
	Node(ArrayList<E> motive) {
		this.motive = motive;
	}
	
	//motive related to node
	private ArrayList<E> motive = new ArrayList<E>();
	
	//empirical probability of motive appearing in ArrayList of elements in main()
	private float empProb;
	
	//conditional probability of motive appearing in ArrayList of elements in main()
	private float condProb;
	
	//controls size of infinite loop check. Making it too small make cause stack overflow
	private int infiniteLoopSize = 5;
	
	//controls how many times a loop has to occur for it to qualify as "infinite"
	private int infLoopOccurences = 5;
	
	//controls probabilites for how to handle infinite loop case. higher numbers make generating from emptyString more likely. must be less than 1
	private float infThres = (float)0.5;
	
	//controls probabilites for how to handle empty context case. higher numbers make generating from emptyString more likely. must be less than 1
	private float empConThres = (float)0.5;
	
	//empirical probability of motive going to first all first order motives
	private float[] probs;
	
	
	//reference to subnodes
	private ArrayList<Node<E>> nextNodes = new ArrayList<Node<E>>();
	
	//the element that appears first after the motive in the ArrayList of elements in main()
	private E goesTo;
	
	private int v;
	
	public void addSubNode(Node<E> subNode) {
		nextNodes.add(subNode);
	}
	
	public void deleteSubNode(int index) {
		nextNodes.remove(index);
	}
	
	public ArrayList<Node<E>> getSubNode() {
		return nextNodes;
	}
	
	public void setMotive(ArrayList<E> motive) {
		this.motive = motive;
	}
	
	public ArrayList<E> getMotive() {
		return motive;
	}
	
	public void setProbs(float[] probs) {
		this.probs = probs;
	}
	
	public float[] getProbs() {
		return probs;
	}
	
	public void setNextNodes(ArrayList<Node<E>> nextNodes) {
		this.nextNodes = nextNodes;
	}
	
	public ArrayList<Node<E>> getNextNodes() {
		return nextNodes;
	}

	public float getEmpProb() {
		return empProb;
	}

	public void setEmpProb(float empProb) {
		this.empProb = empProb;
	}

	public float getCondProb() {
		return condProb;
	}

	public void setCondProb(float condProb) {
		this.condProb = condProb;
	}

	public E getGoesTo() {
		return goesTo;
	}

	public void setGoesTo(E goesTo) {
		this.goesTo = goesTo;
	}
	
	public float getProbSum() {
		float sum = 0;
		
		for(int i = 0; i < probs.length; i++) {
			sum += probs[i];
		}
		
		return sum;
	}
	
	public boolean isASuffix() {
		return !(nextNodes.size() < 1);
	}
	
	ArrayList<E> generateNextSymbol(ArrayList<E> elementsInFile)
	{
		Random rand = new Random();
		float x = rand.nextFloat();
		boolean isOver = false;
		int posCount = 0;
		float sum = probs[0];
		
		while(!isOver) {
			
			//compare random to probability that elem1 will go to elem2
			if(sum >= x || posCount == probs.length - 1) {
				isOver = true;
			}
			
			else {
				sum += probs[posCount];
				posCount++;
			}
		}
		
		ArrayList<E> nextSymbol = new ArrayList<E>();
		nextSymbol.add(elementsInFile.get(posCount));
		
		return nextSymbol;
	}
	
	ArrayList<E> generate(ArrayList<E> isThisElemMe, ArrayList<E> elementsInFile, ArrayList<ArrayList<E>> generatedElements, Node<E> emptyStringNode)
	{
		//handle infinite loops
//		if(infiniteLoop(generatedElements)) {
//			return handleInfiniteLoop(isThisElemMe, elementsInFile, generatedElements, emptyStringNode);
//		}
		
		//if a match is passed in, generate the next symbol
		if(isThisElemMe.equals(motive))
		{
			return generateNextSymbol(elementsInFile);
		}
		
		else
		{
			if(isThisElemMe.size() <= motive.size())
			{
				return new ArrayList<E>();
			}
			
			else
			{
				ArrayList<E> newMotive = new ArrayList<E>();
				
				int index = 0;
				
				//go down the tree and search for motive
				while(newMotive.isEmpty() && index < nextNodes.size()) {
					newMotive = nextNodes.get(index).generate(isThisElemMe, elementsInFile, generatedElements, emptyStringNode);
					index++;
				}
				
				//if a motive isn't found, truncate the element so a match can be found for it
				if(newMotive.size() <= 0) {
					isThisElemMe.remove(0);
					
					while(newMotive.isEmpty() && index < nextNodes.size()) {
						newMotive = nextNodes.get(index).generate(isThisElemMe, elementsInFile, generatedElements, emptyStringNode);
						index++;
					}
				}
				
				//if nothing is found at all, this is the empty context case. handle it here
				if(isThisElemMe.size() <= 0 && generatedElements.size() > 2) {
					newMotive = handleEmptyContext(isThisElemMe, elementsInFile, generatedElements, emptyStringNode);
				}
				
				return newMotive;
			}
		}
	}
	
	boolean infiniteLoop(ArrayList<ArrayList<E>> generatedElements) {
		boolean isInf = false;
		
		int motiveCount = 0;
		
		boolean isOver = false;
		
		ArrayList<ArrayList<E>> tempArray = new ArrayList<ArrayList<E>>();
		
		int index = 0;
		
		//don't do this at the very beginning
		if(generatedElements.size() > infiniteLoopSize * 2) {
			
			//add to tempArray to compare to genElemSlice
			for(int i = generatedElements.size() - (int)infiniteLoopSize - 1; i < generatedElements.size(); i++) {
				tempArray.add(generatedElements.get(i));	
			}
			
			//compare the latest elements to each slice of genElem until/if an infintite loop is found
			while(!isOver && index < generatedElements.size() - infiniteLoopSize) {
				ArrayList<ArrayList<E>> genElemSlice = new ArrayList<ArrayList<E>>();
				
				for(int j = 0; j < infiniteLoopSize + 1; j++) {
					genElemSlice.add(generatedElements.get(index + j));
				}
				
				if(tempArray.equals(genElemSlice)) {
					motiveCount++;
				}
				
				if(motiveCount >= infLoopOccurences) {
					isInf = true;
					isOver = true;
				}
				
				index++;
			}
		}
		
		return isInf;
	}
	
	ArrayList<E> handleInfiniteLoop(ArrayList<E> isThisElemMe, ArrayList<E> elementsInFile, ArrayList<ArrayList<E>> generatedElements, Node<E> emptyStringNode) {
		Random rand = new Random();
		float dice = rand.nextFloat();
		
		ArrayList<E> newMotive = new ArrayList<E>();
		
		//rollback
		if(dice < infThres) {
			newMotive = emptyStringNode.generate(generatedElements.get(generatedElements.size() - 2), elementsInFile, generatedElements, emptyStringNode);
		}
		
		//go back to empty string
		else if(dice >= infThres) {
			newMotive = emptyStringNode.generate(emptyStringNode.getMotive(), elementsInFile, generatedElements, emptyStringNode);
		}
		
		return newMotive;
	}
	
	ArrayList<E> handleEmptyContext(ArrayList<E> isThisElemMe, ArrayList<E> elementsInFile, ArrayList<ArrayList<E>> generatedElements, Node<E> emptyStringNode) {
		Random rand = new Random();
		float dice = rand.nextFloat();
		
		ArrayList<E> newMotive = new ArrayList<E>();
		
		//rollback
		if(dice < empConThres) {
			newMotive = emptyStringNode.generate(generatedElements.get(generatedElements.size() - 2), elementsInFile, generatedElements, emptyStringNode);
		}
		
		//go back to empty string
		else if(dice >= empConThres) {
			newMotive = emptyStringNode.generate(emptyStringNode.getMotive(), elementsInFile, generatedElements, emptyStringNode);
		}
		
		return newMotive;
	}

	public int getV() {
		return v;
	}

	public void setV(int v) {
		this.v = v;
	}
}

