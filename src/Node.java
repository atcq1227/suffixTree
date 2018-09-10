import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
	float empProb;
	float condProb;
	
	List<Object> motif;
	
	Map<List<Object>, Node> nextNodes;
	
	Map<List<Object>, Float> goesTo;

	Node() {
		this.motif = new ArrayList<Object>();
		this.nextNodes = new HashMap<List<Object>, Node>();
		this.goesTo = new HashMap<List<Object>, Float>();
	}
	
	Node(List<Object> motif) {
		this.motif = motif;
		this.nextNodes = new HashMap<List<Object>, Node>();
		this.goesTo = new HashMap<List<Object>, Float>();
	}
	
	public List<Object> getMotif() {
		return motif;
	}
	
	public void setMotif(List<Object> motif) {
		this.motif = motif;
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
	
	public Map<List<Object>, Node> getNextNodes() {
		return nextNodes;
	}

	public void setNextNodes(Map<List<Object>, Node> nextNodes) {
		this.nextNodes = nextNodes;
	}
	
	public Map<List<Object>, Float> getGoesTo() {
		return goesTo;
	}

	public void setGoesTo(Map<List<Object>, Float> goesTo) {
		this.goesTo = goesTo;
	}
	
}
