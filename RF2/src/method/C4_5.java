package method;

import version.BuildModelPanel;
import entity.*;
import tool.*;
import tool.ReadFile;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class C4_5 {

	private static LinkedList<String> attribute = new LinkedList<String>(); // 存储属性的名称
	private static LinkedList<String[]> data = new LinkedList<String[]>();; // 原始数据
	private static LinkedList<String[]> subdata = new LinkedList<String[]>();// 有放回抽样产生的新的训练集
	public static LinkedList<String> reattribute = new LinkedList<String>();
	private static LinkedList<String> subAttribute = new LinkedList<String>();
	private static double[] subattributevalues;
	private static LinkedList<ArrayList<String>> subAttributevalue = new LinkedList<ArrayList<String>>();
	private static ArrayList<String> className=new ArrayList<>();//存储类别名称

	public static TreeNode root=null;
	public static int attrnum=0;// 存储属性的个数  
	public static int[] subattr;//表示属性是否已被分裂

	Double entropyS;//训练样本的信息量

	/**
	 * 
	 * @param lines
	 *            传入要分析的数据集
	 * @param index
	 *            哪个属性？attribute的index
	 */
	public Double getTotalGain(LinkedList<String[]> lines) {//计算训练样本的信息量
		double[] li=new double[className.size()];
		for(int i=0;i<lines.size();i++){
			String[] line=lines.get(i);
			li[Integer.parseInt(line[line.length-1])]++;
		}
		double S=Double.valueOf(lines.size());
		// 计算Entropy(S)
		entropyS = TheMath.getEntropy(S, li);// 训练样本的信息量
		return entropyS;
	}

	//计算信息增益率
	public Double getIgr(LinkedList<String[]> lines, int index) {
		Double igr=-1.0;
		Double gain = -1.0;

		// 下面计算gain各个属性的信息增益
		List<Point> lasv = new ArrayList<Point>();
		double sumvalue=0.0;
		double avgvalue=0.0;
		String[] line=null;
		double[] lowvalue={0,0,0,0,0,0,0,0};
		double[] highvalue={0,0,0,0,0,0,0,0};
		LinkedList<int[]> exist=new LinkedList<int[]>();//存在该属性的数据行
		//计算某个属性权重的平均值
		for(int n = 0;n < lines.size();n++){
			line=lines.get(n);
			int k = 1;
			for(; k < line.length; k = k + 2){
				if (line[k].equals(subAttribute.get(index)))//该属性权重不为0
				{
					sumvalue+=Double.parseDouble(line[k+1]);
					int[] position=new int[2];//记录属性存在的行数和属性权重的位置
					position[0]=n;//第n行
					position[1]=k+1;//第k+1位是属性权重
					exist.add(position);
					break;
				}
			}
			if(k >= line.length){ //该属性权重为0（即属性权重<= 平均值 情况下各类别数据的数目）
				lowvalue[Integer.parseInt(line[line.length-1])]++;
			}
		}
		avgvalue=sumvalue/lines.size();
		subattributevalues[index]=avgvalue;
		
		/*
		 * 取值分为两种情况：<= 和 >
		 * 统计<= 和 > 平均值清况下  各类的数目
		 */
		for(int i=0;i<exist.size();i++){
			line=lines.get(exist.get(i)[0]);
			if(Double.parseDouble(line[exist.get(i)[1]])<=avgvalue)
				lowvalue[Integer.parseInt(line[line.length-1])]++;
			if(Double.parseDouble(line[exist.get(i)[1]])> avgvalue)
				highvalue[Integer.parseInt(line[line.length-1])]++;
		}
		Double Sv1 = 0.0;
		//计算每个取值情况子集下的信息量
		
		//<=平均值情况下信息量
		for(int i=0;i<lowvalue.length;i++)
			Sv1+=lowvalue[i];
		Double entropySv = TheMath.getEntropy(Sv1,lowvalue);
		Point lp = new Point();
		lp.setSv(Sv1);//该属性某个取值存在的数据集数目
		lp.setEntropySv(entropySv);//该属性 在某个取值下 的信息量
		lasv.add(lp);
		
		double Sv2 = 0.0;
		//>平均值情况下信息量
		for(int i=0;i<highvalue.length;i++)
			Sv2+=highvalue[i];
		entropySv = TheMath.getEntropy(Sv2,highvalue);
		Point hp = new Point();
		hp.setSv(Sv2);//该属性某个取值存在的数据集数目
		hp.setEntropySv(entropySv);//该属性 在某个取值下 的信息量
		lasv.add(hp);
		
		gain = TheMath.getGain(entropyS, lines.size(), lasv);//该属性的信息增益
		//计算分裂信息度量SplitInfo
		double Sv=Sv1+Sv2;
		double splitInfo=TheMath.sigma(Sv1, Sv)+TheMath.sigma(Sv2, Sv);
		igr=gain/splitInfo;
		return igr;
		
	}
	

	// 寻找最大的信息增益率,将最大的属性定为当前节点，并返回该属性所在list的位置和gain值
	public MaxIgr getMaxIgr(LinkedList<String[]> lines) {
		if (lines == null || lines.size() <= 0) {
			return null;
		}
		MaxIgr maxIgr = new MaxIgr();
		Double maxvalue = 0.0;
		int maxindex = -1;
		entropyS = getTotalGain(lines);
		if (entropyS == 0)// 说明训练数据集属于同一类
		{
			maxvalue = -1.0;
		} 
		else {
			if (attrnum > 0) {
				for (int i = 0; i < subAttribute.size(); i++) {
					if(subattr[i]==0)
						continue;
					Double tmp = getIgr(lines, i);
					if (maxvalue < tmp) {
						maxvalue = tmp;
						maxindex = i;
					}
				}
				attrnum--;
				if(maxindex==(-1))//未用属性值全为0
					maxvalue=-2.0;
				else 
				    subattr[maxindex]=0;
			} // if(attrum>0)
			else {// 如果属性列表为空
				maxvalue = -2.0;
			}
		}
		maxIgr.setMaxigr(maxvalue);
		maxIgr.setMaxindex(maxindex);
		return maxIgr;
	}


	public void createDTree() {
		root = new TreeNode();
		MaxIgr maxIgr = getMaxIgr(subdata);
		if (maxIgr == null) {
			System.out.println("没有数据集，请检查!");
		}
		if (maxIgr.getMaxigr() == -1.0)// 说明训练数据集属于同一类
		{
			String[] line = subdata.get(0);
			String nodename = className.get(Integer.parseInt(line[line.length-1]));
			root.setNodename(nodename);
			root.setNodeindex(line[line.length - 1]);
		} 
		else if (maxIgr.getMaxigr() == -2.0) {// 属性集合为空
			// 返回作为叶子结点，样本类别中类别个数最多的类别标记为该节点类别
			int[] li=new int[className.size()];
			for(int i=0;i<subdata.size();i++){
				String[] line = subdata.get(i);
				li[Integer.parseInt(line[line.length - 1])]++;
			}
			int max = li[0];
			int index = 0;
			for (int i = 1; i < li.length; i++) {
				if (li[i] > max) {
					max = li[i];
					index = i;
				}
			}
			String nodename = className.get(index);
			root.setNodename(nodename);
			root.setNodeindex(Integer.toString(index));
		}
		else {
			int maxKey = maxIgr.getMaxindex();
			String nodename = subAttribute.get(maxKey);
			root.setNodename(nodename);
			root.setNodevalue(subattributevalues[maxKey]);
			GetSublines(subdata, root, maxKey);
		}
	}

	/**
	 * 
	 * @param lines
	 *            传入的数据集，作为新的递归数据集
	 * @param node
	 *            深入此节点
	 * @param index
	 *            属性位置
	 */
	public void GetSublines(LinkedList<String[]> lines, TreeNode node, int index) {
		double attvalue = node.getNodevalue();
		LinkedList<String[]> newlines1 = new LinkedList<String[]>();
		LinkedList<String[]> newlines2 = new LinkedList<String[]>();
		for (int i = 0; i < lines.size(); i++) {
			String[] line = lines.get(i);
			int k = 1;
			for (; k < line.length; k = k + 2) {
				if (line[k].equals(subAttribute.get(index))) {
					if (Double.parseDouble(line[k + 1]) <= attvalue)
						newlines1.add(line);
					else
						newlines2.add(line);
					break;
				}
			}
			if (k >= line.length)
				newlines1.add(line);
		}
		InsertNode(node,newlines1,0);
		InsertNode(node,newlines2,1);
	}

	public void InsertNode(TreeNode node,LinkedList<String[]> newlines,int flag){
		
		if (newlines.size() <= 0 ) {
			return;
		}
		if(newlines.size()==1){
			TreeNode subnode = new TreeNode();
			subnode.setFatherAttribute(flag);
			// 叶子节点是yes还是no?取新行中最后一个必是其名称,因为只有完全是yes,或完全是no的情况下才会是叶子节点
			String[] line = newlines.get(0);
			String nodename = className.get(Integer.parseInt(line[line.length - 1]));
			subnode.setNodename(nodename);
			subnode.setNodeindex(line[line.length-1]);
			node.addChild(subnode);
			return;
		}
		MaxIgr maxIgr = getMaxIgr(newlines);
		double maxigr = maxIgr.getMaxigr();
		Integer maxKey = maxIgr.getMaxindex();
		if (maxigr== -1.0)// 说明训练数据集属于同一类
		{
			TreeNode subnode = new TreeNode();
			subnode.setFatherAttribute(flag);
			// 叶子节点是yes还是no?取新行中最后一个必是其名称,因为只有完全是yes,或完全是no的情况下才会是叶子节点
			String[] line = newlines.get(0);
			String nodename = className.get(Integer.parseInt(line[line.length - 1]));
			subnode.setNodename(nodename);
			subnode.setNodeindex(line[line.length-1]);
			node.addChild(subnode);
		}
		else if (maxigr== -2.0) {// 属性集合为空
			// 返回作为叶子结点，样本类别中类别个数最多的类别标记为该节点类别
			int[] li=new int[className.size()];
			for(int i=0;i<newlines.size();i++){
				String[] line = newlines.get(i);
				li[Integer.parseInt(line[line.length - 1])]++;
			}
			int max = li[0];
			int ind = 0;
			for (int i = 1; i < li.length; i++) {
				if (li[i] > max) {
					max = li[i];
					ind = i;
				}
			}
			TreeNode subnode = new TreeNode();
			subnode.setFatherAttribute(flag);
			String nodename = className.get(ind);
			subnode.setNodename(nodename);
			subnode.setNodeindex(Integer.toString(ind));
			node.addChild(subnode);
		}
		else if(maxigr==0){
			// 不等于0继续递归，等于0说明是叶子节点，结束递归。
				TreeNode subnode = new TreeNode();
				subnode.setFatherAttribute(flag);
				// 叶子节点是yes还是no?取新行中最后一个必是其名称,因为只有完全是yes,或完全是no的情况下才会是叶子节点
				String[] line = newlines.get(0);
				String nodename =className.get(Integer.parseInt(line[line.length-1])) ;
				subnode.setNodename(nodename);
				subnode.setNodeindex(line[line.length - 1]);
				node.addChild(subnode);
		}
		else{
			TreeNode subnode = new TreeNode();
			subnode.setFatherAttribute(flag);
			String nodename = subAttribute.get(maxKey);
			subnode.setNodename(nodename);
			subnode.setNodevalue(subattributevalues[maxKey]);
			node.addChild(subnode);
			// 不等于0，继续递归
			GetSublines(newlines, subnode, maxKey);
		}
	}

	ArrayList<TreeNode> RF=new ArrayList<TreeNode>();//各个决策树的根节点的集合
	public void createForest(String filepath1,String filepath2) {
		BuildModelPanel.jta_result.append("正在验证目录信息\n"+"正在读取训练集数据\n");
		BuildModelPanel.jta_result.paintImmediately(BuildModelPanel.jta_result.getBounds());
		
		ReadFile.readARFF(filepath1, className, attribute, reattribute, data);

		for (int i = 0; i < data.size(); i++) {
			String[] line2=data.get(i);
			for(int j=0;j<className.size();j++)
				if(line2[line2.length-1].equals(className.get(j))){
					line2[line2.length-1]=j+"";
					break;
				}
		}
		
		BuildModelPanel.jta_result.append("正在建立随机森林\n");
		BuildModelPanel.jta_result.paintImmediately(BuildModelPanel.jta_result.getBounds());
		
		long start=System.currentTimeMillis();
		for(int i=1;i<=100;i++)//N 棵决策树
		{
			root=null;
			getSubdata();
			getSubattr();
			createDTree();
			RF.add(root);
		}
		long end=System.currentTimeMillis();
		BuildModelPanel.jta_result.append("随机森林建立完成    耗时:"+(end-start)+"ms\n正在验证\n");
		BuildModelPanel.jta_result.paintImmediately(BuildModelPanel.jta_result.getBounds());
		test();
		String type="C4.5算法";
		ModelRW.writeXML(filepath2,className, reattribute, attribute, RF,type);
		BuildModelPanel.jta_result.append("\n\n模型存储完成");
		BuildModelPanel.jta_result.paintImmediately(BuildModelPanel.jta_result.getBounds());
		
	}

	public static LinkedList<Integer> result = new LinkedList<>();// 每一条数据的决策结果集
	public static LinkedList<Integer> results = new LinkedList<>();// 所有数据的决策结果集
	ArrayList<Integer> dataclass = new ArrayList<>();// 记录数据的实际分类情况
	static String resu = null;
	
	public void test(){
		for (int i = 0; i < data.size(); i++) {
			result.clear();
			String[] line = data.get(i);
			dataclass.add(Integer.parseInt(line[line.length - 1]));
			double[][] temp = new double[5010][2];
			for (int j = 1; j < line.length - 2; j = j + 2) {// 获取属性的位置
				temp[Integer.parseInt(line[j])][0] = 1;// 0位记录该属性是否存在
				temp[Integer.parseInt(line[j])][1] = Double.parseDouble(line[j + 1]);// 1位存该属性的权重
			}

			// 决策树进行判断类别
			for (int k = 0; k < RF.size(); k++) {
				resu = null;// 存储每次分类的结果
				classify(temp, RF.get(k));
				result.add(Integer.parseInt(resu));// 存储每一条数据 经过随机森林分类后的结果
			}
			results.add(count(result));// 将每一条数据 决策树结果投票最多的结果存储
		}
		
		// 统计分类结果
		double[] a = new double[className.size()];// 某类被正确分类的样本数
		double[] b = new double[className.size()];// 分类器分为该类的样本数
		double[] c = new double[className.size()];// 该类实际的样本数
		double asum=0.0;//各类被正确分类的样本数

		int tempd = 0;
		int tempr = 0;
		for (int i = 0; i < dataclass.size(); i++) {
			tempd = dataclass.get(i);
			tempr = results.get(i);
			c[tempd]++;
			b[tempr]++;
			if (tempd == tempr)// 被正确分类的数据行
				a[tempd]++;
		}

		// 显示分类结果
		BuildModelPanel.jta_result.append("模型验证结果\n\n某类被正确分类样本数：a\n" + "被分类器分为该类的样本数：b\n" + "某类实际样本数：c\n" + "召回率：recall\n"
				+ "准确率：precision\n\n" + "类标签         a       b       c       recall  precision   F1值\n");
		double recall, precision, f;
		ArrayList<Double> rl=new ArrayList<>();
		ArrayList<Double> pl=new ArrayList<>();
		String aa, bb, cc, r, p, f1;
		int len = 0;
		DecimalFormat df = new DecimalFormat("0.000");
		for (int i = 0; i < className.size(); i++) {
			recall = 0;
			precision = 0;
			f = 0;
			aa = Double.toString(a[i]);
			bb = Double.toString(b[i]);
			cc = Double.toString(c[i]);
			String str = className.get(i);
			len = 15 - str.length();
			for (int j = 0; j < len; j++)
				str += " ";
			str += aa;
			len = 8 - aa.length();
			for (int j = 0; j < len; j++)
				str += " ";
			str += bb;
			len = 8 - bb.length();
			for (int j = 0; j < len; j++)
				str += " ";
			str += cc;
			len = 8 - cc.length();
			for (int j = 0; j < len; j++)
				str += " ";
			// 防止除数为0的情况
			if (c[i] != 0)
				recall = a[i] / c[i];
			if (b[i] != 0)
				precision = a[i] / b[i];
			if (b[i] != 0 && c[i] != 0)
				f = 2 * precision * recall / (precision + recall);
			r = df.format(recall);
			p = df.format(precision);
			f1 = df.format(f);
			str += r;
			len = 10 - r.length();
			for (int j = 0; j < len; j++)
				str += " ";
			str += p;
			len = 10 - p.length();
			for (int j = 0; j < len; j++)
				str += " ";
			str += f1 + "   \n";
			BuildModelPanel.jta_result.append(str);
			BuildModelPanel.jta_result.paintImmediately(BuildModelPanel.jta_result.getBounds());
			asum+=a[i];
			rl.add(recall);
			pl.add(precision);
		}
		double macro_F1=0.0,micro_F1=0.0;
		recall=0.0;precision=0.0;
		for(int i=0;i<rl.size();i++){
			recall+=rl.get(i);
			precision+=pl.get(i);
		}
		recall=recall/(double)rl.size();
		precision=precision/(double)pl.size();
		macro_F1=2 * precision * recall / (precision + recall);
		recall=asum/(double)dataclass.size();
		precision=asum/(double)dataclass.size();
		micro_F1=2 * precision * recall / (precision + recall);
		BuildModelPanel.jta_result.append("\n宏平均值:"+df.format(macro_F1)
		    +"    微平均值："+df.format(micro_F1));
		BuildModelPanel.jta_result.paintImmediately(BuildModelPanel.jta_result.getBounds());
	}
	
	public void classify(double[][] temp, TreeNode node) {
		if (node.getLeftchild() == null && node.getRightchild() == null) {
			resu = node.getNodeindex();
		} else {
			// 从根节点开始遍历判断
			if (temp[Integer.valueOf(node.getNodename())][0] == 1
					&& temp[Integer.valueOf(node.getNodename())][0] > node.getNodevalue())// 存在，属性值为1
				classify(temp, node.getRightchild());
			else
				classify(temp, node.getLeftchild());
		}
	}

	public static int count(LinkedList<Integer> result) {
		int[] f = new int[className.size()];
		int max, index;
		for (int i = 0; i < result.size(); i++)
			f[result.get(i)]++;
		max = f[0];
		index = 0;
		for (int i = 1; i < f.length; i++) {
			if (f[i] > max) {
				max = f[i];
				index = i;
			}
		}
		return index;
	}
	
	public void getSubdata() {
		subdata.clear();
		int M = data.size();
		int random;
		for (int i = 0; i < M; i++) {
			random = (int) (Math.random() * (data.size() - 1));
			subdata.add(data.get(random));
		}
	}
	
	public void getSubattr() {
		subAttribute.clear();
		subAttributevalue.clear();
		int S=attribute.size();
		int k = (int) Math.sqrt(S);
		attrnum = k;
		subattr=new int[k];
		subattributevalues=new double[k];
		int random;
		int[] repeat=new int[attribute.size()];//用于判断随机数是否重复  1表示已存在
		for (int i = 0; i < k; i++) {
			random=TheMath.getRandom(repeat,S);
			subAttribute.add(attribute.get(random));
			repeat[random]=1;
			subattr[i]=1;
			}
	}
	
	
}
