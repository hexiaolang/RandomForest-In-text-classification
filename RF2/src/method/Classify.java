package method;

import entity.*;
import tool.ModelRW;
import tool.ReadFile;
import version.TestModelPanel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;

public class Classify {

	private static LinkedList<String> attribute = new LinkedList<String>(); // 存储属性的名称
	private static LinkedList<String[]> data = new LinkedList<String[]>();; // 原始数据
	private static LinkedList<String> reattribute = new LinkedList<String>();
	private static ArrayList<String> className = new ArrayList<>();
	public static LinkedList<Integer> result = new LinkedList<>();// 每一条数据的决策结果集
	public static LinkedList<Integer> results = new LinkedList<>();// 所有数据的决策结果集

	static String resu = null;

	public void sort(String filepath1, String filepath2) {
		TestModelPanel.jta_result.append("正在验证目录信息...\n" + "正在读取测试集数据...\n");
		TestModelPanel.jta_result.paintImmediately(TestModelPanel.jta_result.getBounds());

		ReadFile.readARFF(filepath1, className, attribute, reattribute, data);
		
		TestModelPanel.jta_result.append("读取测试集数据完成\n正在读取待测试模型...\n");
		TestModelPanel.jta_result.paintImmediately(TestModelPanel.jta_result.getBounds());

		ArrayList<String> classesName = new ArrayList<>();// 存储xml文件中的类标签
		LinkedList<String> attributes = new LinkedList<>();// 存储xml文件中的属性标签
		ArrayList<TreeNode> trees = new ArrayList<>();// 存储xml文件中的树节点
		String type = ModelRW.parserXml(classesName, attributes, trees);
		TestModelPanel.jta_result.append("待测试模型读取完成\n该模型决策树构造方法为："
		        +type+"\n正在对测试集数据分类...\n");
		TestModelPanel.jta_result.paintImmediately(TestModelPanel.jta_result.getBounds());

		ArrayList<Integer> dataclass = new ArrayList<>();// 记录数据的实际分类情况
		results.clear();
		dataclass.clear();
		long start = System.currentTimeMillis();
		for (int i = 0; i < data.size(); i++) {
			result.clear();
			String[] line = data.get(i);
			for (int c = 0; c < className.size(); c++)
				if (line[line.length - 1].equals(className.get(c))) {
					dataclass.add(c);
					break;
				}
			double[][] temp = new double[5010][2];
			for (int j = 1; j < line.length - 2; j = j + 2) {// 获取属性的位置
				temp[Integer.parseInt(line[j])][0] = 1;// 0位记录该属性是否存在
				temp[Integer.parseInt(line[j])][1] = Double.parseDouble(line[j + 1]);// 1位存该属性的权重
			}

			// 决策树进行判断类别
			for (int k = 0; k < trees.size(); k++) {
				resu = null;// 存储每次分类的结果
				classify(temp, trees.get(k));
				result.add(Integer.parseInt(resu));// 存储每一条数据 经过随机森林分类后的结果
			}
			results.add(count(result));// 将每一条数据 决策树结果投票最多的结果存储
		}
		long end = System.currentTimeMillis();
		TestModelPanel.jta_result.append("测试集数据分类完成\n数据分类耗时:"+(end-start)+"ms\n正在统计分类结果...\n");
		TestModelPanel.jta_result.paintImmediately(TestModelPanel.jta_result.getBounds());
		
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
		TestModelPanel.jta_result.append("分类结果统计完成\n\n某类被正确分类样本数：a\n" + "被分类器分为该类的样本数：b\n" + "某类实际样本数：c\n" + "召回率：recall\n"
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
			TestModelPanel.jta_result.append(str);
			TestModelPanel.jta_result.paintImmediately(TestModelPanel.jta_result.getBounds());
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
		TestModelPanel.jta_result.append("\n宏平均值:"+df.format(macro_F1)
		    +"    微平均值："+df.format(micro_F1));
		TestModelPanel.jta_result.paintImmediately(TestModelPanel.jta_result.getBounds());

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
}
