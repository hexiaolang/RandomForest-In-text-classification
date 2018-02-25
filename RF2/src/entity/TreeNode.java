package entity;

public class TreeNode {
	private String nodename; // 节点名称（属性的编号）
	private String nodeindex;//属性的编号 或 类别的编号
	private double nodevalue;//节点分支的属性值
	private TreeNode leftChild=null;
	private TreeNode rightChild=null;
	private int fatherAttribute=-1; // 此节点是父类的哪具属性的分支

	public String getNodename() {
		return nodename;
	}
	public void setNodename(String nodename) {
		this.nodename = nodename;
	}
	public String getNodeindex() {
		return nodeindex;
	}
	public void setNodeindex(String nodeindex) {
		this.nodeindex = nodeindex;
	}
	public double getNodevalue(){
		return nodevalue;
	}
	public void setNodevalue(double nodevalue){
		this.nodevalue=nodevalue;
	}
	public TreeNode getLeftchild(){
		return leftChild;
	}
	public void setLeftchild(TreeNode leftChild){
		this.leftChild=leftChild;
	}
	public TreeNode getRightchild(){
		return rightChild;
	}
	public void setRightChild(TreeNode rightChild){
		this.rightChild=rightChild;
	}
	public int getFatherAttribute() {
		return fatherAttribute;
	}
	public void setFatherAttribute(int fatherAttribute) {
		this.fatherAttribute = fatherAttribute;
	}
	/**
     * 添加一个节点
     * @param child
     */
    public void addChild(TreeNode child) {
    	if(child.getFatherAttribute()==0)
    		this.leftChild=child;
    	if(child.getFatherAttribute()==1)
    		this.rightChild=child;
    }
    /**
    *  是否存在着该节点,存在返回该节点，不存在返回空
    * @param name
    * @return
    */
   /* public TreeNode findChild(String name) {
        List<TreeNode> children = this.getChildren();
        if (children != null) {
            for (TreeNode child : children) {
                if (child.getName().equals(name)) {
                    return child;
                }
            }
        }
        return null;
    }
	*/
}
