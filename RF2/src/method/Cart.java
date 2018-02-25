package method;

import version.BuildModelPanel;
import entity.*;
import tool.*;
import tool.ReadFile;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;

public class Cart {

	private static LinkedList<String> attribute = new LinkedList<String>(); // 存储属性的名称
	private static LinkedList<String[]> data = new LinkedList<String[]>();; // 原始数据
	private static LinkedList<String[]> subdata = new LinkedList<String[]>();// 有放回抽样产生的新的训练集
	public static LinkedList<String> reattribute = new LinkedList<String>();
	private static LinkedList<String> subAttribute = new LinkedList<String>();
	private static double[] subattributevalues;
	private static LinkedList<ArrayList<String>> subAttributevalue = new LinkedList<ArrayList<String>>();
	private static ArrayList<String> className = new ArrayList<>();// 存储类别名称

	public static TreeNode root = null;
	public static int attrnum = 0;// 存储属性的个数
	public static int[] subattr;// 表示属性是否已被分裂

	// 计算每个属性的Gini系数
	public double getGini(LinkedList<String[]> lines, int index) {
		double gini = 1.0;
		// 计算某个属性权重的平均值
		String[] line = null;
		double sumvalue = 0.0;
		LinkedList<int[]> exist = new LinkedList<int[]>();// 存在该属性的数据行
		double[] lowvalue = { 0, 0, 0, 0, 0, 0, 0, 0 };
		double[] highvalue = { 0, 0, 0, 0, 0, 0, 0, 0 };
		for (int n = 0; n < lines.size(); n++) {
			line = lines.get(n);
			int k = 1;
			for (; k < line.length; k = k + 2) {
				if (line[k].equals(subAttribute.get(index)))// 该属性权重不为0
				{
					sumvalue = Double.parseDouble(line[k + 1]);
					int[] position = new int[2];// 记录属性存在的行数和属性权重的位置
					position[0] = n;// 第n行
					position[1] = k + 1;// 第k+1位是属性权重
					exist.add(position);
					break;
				}
			}
			if (k >= line.length) { // 该属性权重为0（即属性权重<= 平均值 情况下各类别数据的数目）
				lowvalue[Integer.parseInt(line[line.length - 1])]++;
			}
		}

		if (sumvalue == 0) {
			subattr[index] = 1;
			attrnum--;
			return gini;
		}
		double avgvalue = sumvalue / lines.size();
		subattributevalues[index] = avgvalue;

		/*
		 * 取值分为两种情况：<= 和 > 统计<= 和 > 平均值清况下 各类的数目
		 */
		for (int i = 0; i < exist.size(); i++) {
			line = lines.get(exist.get(i)[0]);
			if (Double.parseDouble(line[exist.get(i)[1]]) <= avgvalue)
				lowvalue[Integer.parseInt(line[line.length - 1])]++;
			if (Double.parseDouble(line[exist.get(i)[1]]) > avgvalue)
				highvalue[Integer.parseInt(line[line.length - 1])]++;
		}
		gini = TheMath.getGini(lowvalue, highvalue);
		return gini;
	}

	// 寻找最小的Gini系数，将最小的属性定为当前节点，并返回该属性所在list的位置和Gini系数
	public MinGini getMinGini(LinkedList<String[]> lines) {
		if (lines == null || lines.size() <= 0) {
			return null;
		}
		MinGini minGini = new MinGini();
		double minvalue = 1.0;
		int minindex = -1;
		if (attrnum > 0) {
			for (int i = 0; i < subAttribute.size(); i++) {
				if (subattr[i] == 0)
					continue;
				Double tmp = getGini(lines, i);
				if (tmp < minvalue) {
					minvalue = tmp;
					minindex = i;
				}
			}
			if (minindex != -1)
				attrnum--;
		}
		minGini.setMingini(minvalue);
		minGini.setMinindex(minindex);
		return minGini;
	}

	public void createDTree() {
		root = new TreeNode();
		MinGini minGini = getMinGini(subdata);
		if (minGini == null)
			System.out.println("没有数据集，请检查！");
		if (minGini.getMingini() == 0)// 说明数据集属于同一类
		{
			String[] line = subdata.get(0);
			String nodename = className.get(Integer.parseInt(line[line.length - 1]));
			root.setNodename(nodename);
			root.setNodeindex(line[line.length - 1]);
		} else if (minGini.getMingini() == 1.0) {// 属性集合为空
			// 返回作为叶子结点，样本类别中类别个数最多的类别标记为该节点类别
			int[] li = new int[className.size()];
			for (int i = 0; i < subdata.size(); i++) {
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
		} else {
			int minKey = minGini.getMinindex();
			String nodename = subAttribute.get(minKey);
			root.setNodename(nodename);
			root.setNodevalue(subattributevalues[minKey]);
			GetSublines(subdata, root, minKey);
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
		InsertNode(node, newlines1, 0);
		InsertNode(node, newlines2, 1);
	}

	public void InsertNode(TreeNode node, LinkedList<String[]> newlines, int flag) {

		if (newlines.size() <= 0) {
			return;
		}
		if (newlines.size() == 1) {//叶子节点
			TreeNode subnode = new TreeNode();
			subnode.setFatherAttribute(flag);
			String[] line = newlines.get(0);
			String nodename = className.get(Integer.parseInt(line[line.length - 1]));
			subnode.setNodename(nodename);
			subnode.setNodeindex(line[line.length - 1]);
			node.addChild(subnode);
			return;
		}
			MinGini minGini = getMinGini(newlines);
			double mingini = minGini.getMingini();
			int minindex = minGini.getMinindex();
			if (mingini == 0)// 说明数据集属于同一类
			{
				TreeNode subnode = new TreeNode();
				subnode.setFatherAttribute(flag);
				String[] line = newlines.get(0);
				String nodename = className.get(Integer.parseInt(line[line.length - 1]));
				subnode.setNodename(nodename);
				subnode.setNodeindex(line[line.length - 1]);
				node.addChild(subnode);
			} else if (mingini == 1.0) {// 属性集合为空
				// 返回作为叶子结点，样本类别中类别个数最多的类别标记为该节点类别
				int[] li = new int[className.size()];
				for (int i = 0; i < newlines.size(); i++) {
					String[] line = newlines.get(i);
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
				TreeNode subnode = new TreeNode();
				subnode.setFatherAttribute(flag);
				String nodename = className.get(index);
				subnode.setNodename(nodename);
				subnode.setNodeindex(Integer.toString(index));
				node.addChild(subnode);
			} else {
				TreeNode subnode = new TreeNode();
				subnode.setFatherAttribute(flag);
				String nodename = subAttribute.get(minindex);
				subnode.setNodename(nodename);
				subnode.setNodevalue(subattributevalues[minindex]);
				node.addChild(subnode);
				GetSublines(newlines, subnode, minindex);
			}
	}

	ArrayList<TreeNode> RF = new ArrayList<TreeNode>();// 各个决策树的根节点的集合

	public void createForest(String filepath1, String filepath2) {
		BuildModelPanel.jta_result.append("正在验证目录信息\n" + "正在读取训练集数据\n");
		BuildModelPanel.jta_result.paintImmediately(BuildModelPanel.jta_result.getBounds());

		ReadFile.readARFF(filepath1, className, attribute, reattribute, data);

		for (int i = 0; i < data.size(); i++) {
			String[] line2 = data.get(i);
			for (int j = 0; j < className.size(); j++)
				if (line2[line2.length - 1].equals(className.get(j))) {
					line2[line2.length - 1] = j + "";
					break;
				}
		}

		BuildModelPanel.jta_result.append("正在建立随机森林\n");
		BuildModelPanel.jta_result.paintImmediately(BuildModelPanel.jta_result.getBounds());

		long start = System.currentTimeMillis();
		for (int i = 1; i <= 100; i++)// N 棵决策树
		{
			root = null;
			getSubdata();
			getSubattr();
			createDTree();
			RF.add(root);
		}
		long end = System.currentTimeMillis();
		BuildModelPanel.jta_result.append("随机森林建立完成    耗时:" + (end - start) + "ms\n正在验证\n");
		BuildModelPanel.jta_result.paintImmediately(BuildModelPanel.jta_result.getBounds());

		test();
		String type = "Cart算法";
		ModelRW.writeXML(filepath2, className, reattribute, attribute, RF, type);

		BuildModelPanel.jta_result.append("\n\n模型存储完成");
		BuildModelPanel.jta_result.paintImmediately(BuildModelPanel.jta_result.getBounds());

	}

	public static LinkedList<Integer> result = new LinkedList<>();// 每一条数据的决策结果集
	public static LinkedList<Integer> results = new LinkedList<>();// 所有数据的决策结果集
	ArrayList<Integer> dataclass = new ArrayList<>();// 记录数据的实际分类情况
	static String resu = null;

	public void test() {
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
		double asum = 0.0;// 各类被正确分类的样本数

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
		BuildModelPanel.jta_result
				.append("模型验证结果\n\n某类被正确分类样本数：a\n" + "被分类器分为该类的样本数：b\n" + "某类实际样本数：c\n" + "召回率：recall\n"
						+ "准确率：precision\n\n" + "类标签         a       b       c       recall  precision   F1值\n");
		double recall, precision, f;
		ArrayList<Double> rl = new ArrayList<>();
		ArrayList<Double> pl = new ArrayList<>();
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
			asum += a[i];
			rl.add(recall);
			pl.add(precision);
		}
		double macro_F1 = 0.0, micro_F1 = 0.0;
		recall = 0.0;
		precision = 0.0;
		for (int i = 0; i < rl.size(); i++) {
			recall += rl.get(i);
			precision += pl.get(i);
		}
		recall = recall / (double) rl.size();
		precision = precision / (double) pl.size();
		macro_F1 = 2 * precision * recall / (precision + recall);
		recall = asum / (double) dataclass.size();
		precision = asum / (double) dataclass.size();
		micro_F1 = 2 * precision * recall / (precision + recall);
		BuildModelPanel.jta_result.append("\n宏平均值:" + df.format(macro_F1) + "    微平均值：" + df.format(micro_F1));
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
		int S = attribute.size();
		int k = (int) Math.sqrt(S);
		attrnum = k;
		subattr = new int[k];
		subattributevalues = new double[k];
		int random;
		int[] repeat = new int[attribute.size()];// 用于判断随机数是否重复 1表示已存在
		for (int i = 0; i < k; i++) {
			random = TheMath.getRandom(repeat, S);
			subAttribute.add(attribute.get(random));
			repeat[random] = 1;
			subattr[i] = 1;
		}
	}
}
