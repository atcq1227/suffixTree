
public class Node {
	float empProb;
	float condProb;
	Object motif;
	
	Node(){}
	
	Node(Object motif, float empProb) {
		this.motif = motif;
		this.empProb = empProb;
	}
	
	public Object getMotif() {
		return motif;
	}
	public void setMotif(Object motif) {
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
}
