package method;

import entity.Point;
import java.util.List;

public class TheMath {
	
	//信息熵的计算公式，这里仅是离散型二项分布的熵计算
	/**
	 * 
	 * @param S 样本总数
	 * @param li 各个事件发生的次数
	 * @return
	 */
	//计算训练样本的总信息量
	public static Double getEntropy(double S,double[] value) {
		// TODO Auto-generated method stub
		Double entropy=new Double(0.0);
		for(int i=0;i<value.length;i++){
			entropy=entropy+sigma(value[i],S);
		}
		return entropy;
	}
	//信息增益公式
	/**
	 * @param 此公式完全参照  《机器学习(Tom.Mitchell著)》3.4节
	 * @param entropyS S的信息熵
	 * @param S 传入的总数
	 * @param lasv有两个参数， sv 是sv的个数,sv.entropysv是entropy(sv)
	 * @return 返回信息增益
	 */
	
	public static Double getGain(Double entropyS,int S,List<Point> lasv){
		//System.out.println("entropyS="+entropyS+"    S="+S+"    lasv.size="+lasv.size());
		Double gain=new Double(0.0);
		Double enSum=new Double(0.0);
		for(int i=0;i<lasv.size();i++){
			Point p=lasv.get(i);
			enSum=enSum+((p.getSv()/Double.valueOf(S))*p.getEntropySv());
		}
		gain=entropyS-enSum;
		return gain;
	}
	//公式 -pi*log2(x)
	public static Double sigma(Double x, Double total)
	{
		if (x == 0)
		{
			return 0.0;
		}
		double x_pi = getProbability(x,total);
		return -(x_pi*logYBase2(x_pi));
	}

	//取2为底的对数
	public static double logYBase2(double y)
	{
		return Math.log(y) / Math.log(2);
	}
	
	//等可能事件概率
	public static double getProbability(double x, double total)
	{
		return x * Double.parseDouble("1.0") / total;
	}
	public static double getGini(double[] lowvalue,double[] highvalue){
		double gini=-1.0;
		double lsum=0.0,hsum=0.0,sum=0.0;
		double sub1=0.0,sub2=0.0;
		for(int i=0;i<lowvalue.length;i++){
			lsum+=lowvalue[i];
			hsum+=highvalue[i];
		}
		sum=lsum+hsum;
		for(int i=0;i<lowvalue.length;i++){
			sub1+=Math.pow(lowvalue[i]/lsum, 2);
			sub2+=Math.pow(highvalue[i]/hsum, 2);
		}
		gini=(lsum/sum)*(1-sub1)+(hsum/sum)*(1-sub2);
		return gini;
	}
	public static int getRandom(int[] repeat,int S){
		int random = (int)(Math.random()*(S-1));
		if(repeat[random]==0)
			return random;
		else
			return getRandom(repeat,S);
	}
}
