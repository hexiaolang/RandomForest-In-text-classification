package service;

import method.Classify;

public class TestModel {

	public void testModel(String filepath1,String filepath2){//测试集目录、模型目录
		Classify cfy=new Classify();
		cfy.sort(filepath1, filepath2);
	}
}
