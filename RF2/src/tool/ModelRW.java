package tool;

import entity.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ModelRW {

	static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	static DocumentBuilder builder = null;

	static int Num=1;

	public static void writeXML(String filepath,ArrayList<String> classesName, LinkedList<String> reattribute, LinkedList<String> attributes,
			ArrayList<TreeNode> RF,String type) {
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			String filename;
			filename=filepath+"\\model"+Num+".xml";
			File file=new File(filename);
			if(file.exists()){
				filename=filepath+"\\model"+Num+"(2).xml";
			}
			Num++;
			// 根节点写入document
			Element root = document.createElement("Model");
			root.setAttribute("Type", type);
			document.appendChild(root);
			Element className = document.createElement("ClassesName");
			if (classesName != null) {
				for (int i = 0; i < classesName.size(); i++) {
					Element name = document.createElement("ClassName");
					name.setTextContent(classesName.get(i));
					className.appendChild(name);
				}
			}

			// 特征词节点
			Element attribute = document.createElement("Attributes");
			// 写入特征词的名称
			for (int i = 0; i < attributes.size(); i++) {
				// 新建节点
				Element attr = document.createElement("Attribute");
				// 设置节点信息
				attr.setTextContent(reattribute.get(Integer.parseInt(attributes.get(i))));
				// 添加到集合中
				attribute.appendChild(attr);
			}

			// 随机森林节点
			Element rf = document.createElement("Trees");

			// 写入随机决策树
			for(int i=0;i<RF.size();i++){
				Element tree=document.createElement("Tree");
			    traverseTree(document, RF.get(i), tree);
			    rf.appendChild(tree);
			}

			// 添加
			root.appendChild(className);
			root.appendChild(attribute);
			root.appendChild(rf);

			/** 将document中的内容写入文件中 */
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			// 编码
			DOMSource source = new DOMSource(document);
			PrintWriter pw = new PrintWriter(new FileOutputStream(filename));
			StreamResult result = new StreamResult(pw);
			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void traverseTree(Document document, TreeNode treeNode, Element tree) {
		// TODO Auto-generated method stub
		if (treeNode == null) {
			return;
		}
		Element node = document.createElement("treeNode");
		if (treeNode.getFatherAttribute() == 0) {
			node.setAttribute("leftChild", "true");
		} else {
			node.setAttribute("leftChild", "false");
		}
		// 设置信息
		if (treeNode.getLeftchild() == null && treeNode.getRightchild() == null) {
			// 叶子节点 添加的信息有
			node.setAttribute("leaveNode", "true");
			node.setAttribute("classValue", treeNode.getNodename());
			node.setAttribute("classIndex", treeNode.getNodeindex());

		} else {
			// 非叶子节点
			node.setAttribute("leaveNode", "false");
			node.setAttribute("nodeName", treeNode.getNodename());
			node.setAttribute("nodeValue", Double.toString(treeNode.getNodevalue()));

		}
		tree.appendChild(node);
		traverseTree(document, treeNode.getLeftchild(), node);
		traverseTree(document, treeNode.getRightchild(), node);
	}

	public static String parserXml(ArrayList<String> classesName, LinkedList<String> attributes,
			ArrayList<TreeNode> trees) {
		String type = null;
		try {
			String filePath = "./model/model1.xml";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(filePath);
			// 其实获得的是Data节点 因为document只有一个孩子节点
			NodeList DataList = document.getChildNodes();
			for (int i = 0; i < DataList.getLength(); i++) {
				Node data = DataList.item(i);
				if (!data.getNodeName().equals("Model")) {
					continue;
				}
				type=data.getAttributes().item(0).getTextContent();
				// 名称为 Data的节点
				NodeList dataChildList = data.getChildNodes();
				for (int j = 0; j < dataChildList.getLength(); j++) {

					// 获得名称为Features的节点和Trees的节点
					Node dataChild = dataChildList.item(j);
					if (dataChild.getNodeName().equals("ClassesName")) {
						NodeList nameList = dataChild.getChildNodes();
						for (int m = 0; m < nameList.getLength(); m++) {
							Node node = nameList.item(m);
							if (node.getNodeName().equals("ClassName")) {
								classesName.add(node.getTextContent());
							}
						}
					} else if (dataChild.getNodeName().equals("Attributes")) {
						// 获得名称为Features的节点
						// 该添加特征值了
						NodeList attrList = dataChild.getChildNodes();
						for (int m = 0; m < attrList.getLength(); m++) {
							Node attr = attrList.item(m);
							if (attr.getNodeName().equals("Attribute")) {
								attributes.add(attr.getTextContent());
							}

						}

					} else if (dataChild.getNodeName().equals("Trees")) {
						// 获得名称为Trees的节点 该建树了
						NodeList treeList = dataChild.getChildNodes();
						for (int m = 0; m < treeList.getLength(); m++) {
							Node tree = treeList.item(m);
							if (tree.getNodeName().equals("Tree")) {
								NodeList tns=tree.getChildNodes();
								for(int t=0;t<tns.getLength();t++){
									Node tn=tns.item(t);
									if(tn.getNodeName().equals("treeNode")){
										TreeNode treeNode=new TreeNode();
										createTree(treeNode, tn);
										trees.add(treeNode);
									}
								}
							}
						}

					}
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (ParserConfigurationException e) {
			System.out.println(e.getMessage());
		} catch (SAXException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return type;
	}

	private static void createTree(TreeNode treeNode, Node tree) {
		if (treeNode == null) {
			return;
		}
		NamedNodeMap attributes = tree.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node node = attributes.item(i);
			if (node.getNodeName().equals("classValue")) {
				treeNode.setNodename(node.getTextContent());

			}else if(node.getNodeName().equals("classIndex")){
				treeNode.setNodeindex(node.getTextContent());
				
			}else if (node.getNodeName().equals("nodeName")) {
				treeNode.setNodename(node.getTextContent());

			} else if (node.getNodeName().equals("nodeValue")) {
				treeNode.setNodevalue(Double.parseDouble(node.getTextContent()));

			}
		}
		// 处理孩子 不要忘了他的孩子的组成
		NodeList childList = tree.getChildNodes();
		for (int i = 0; i < childList.getLength(); i++) {
			Node node = childList.item(i);
			if (node.getNodeName().equals("treeNode")) {
				// 树节点
				TreeNode LeftOrRightNode = new TreeNode();
				NamedNodeMap atts = node.getAttributes();
				for (int j = 0; j < atts.getLength(); j++) {
					if (atts.item(j).getNodeName().equals("leftChild")) {
						if (atts.item(j).getNodeValue().equals("true")) {
							treeNode.setLeftchild(LeftOrRightNode);
							createTree(treeNode.getLeftchild(), node);
						} else if (atts.item(j).getNodeValue().equals("false")) {
							treeNode.setRightChild(LeftOrRightNode);
							createTree(treeNode.getRightchild(), node);

						}
					}
				}
			}
		}

	}
}
