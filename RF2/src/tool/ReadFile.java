package tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import version.BuildModelPanel;

public class ReadFile {

	public static void readARFF(String filepath,ArrayList<String> className, LinkedList<String> attribute,
			LinkedList<String> reattribute,LinkedList<String[]> data ) {
		try {
			File file=new File(filepath);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			int index = 0;
			String[] temp;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("@decision")) {
					if (line.isEmpty())
						continue;
					String[] tmp = line.split("[{,}]");
					for(int i=1;i<tmp.length;i++)
						className.add(tmp[i]);
				}
				if (line.startsWith("@attribute")) {
					if (line.isEmpty())
						continue;
					temp = line.split("[{'}]");
					attribute.add(index + "");
					reattribute.add(temp[1]);
					index++;
				} else if (line.startsWith("@data")) {
					while ((line = br.readLine()) != null) {
						if (line.isEmpty())
							continue;
						String[] row = line.split("[{, }]");
						data.add(row);// row从1开始存储数据
					}
				} else {
					continue;
				}
			}
			br.close();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
