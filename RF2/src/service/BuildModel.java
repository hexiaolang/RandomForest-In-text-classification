package service;

import method.C4_5;
import method.Cart;
import method.Id3;

public class BuildModel {

	public void buildModel(String filepath1,String filepath2,int method){//训练集目录、模型存储目录
		if(method==0){
			Id3 id3=new Id3();
			id3.createForest(filepath1, filepath2);
		}
		if(method==1){
			C4_5 c45=new C4_5();
			c45.createForest(filepath1, filepath2);
		}
		if(method==2){
			Cart cart=new Cart();
			cart.createForest(filepath1, filepath2);
		}
	}
}
