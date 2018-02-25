package version;

import service.TestModel;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class UseModelPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	/**
	 * Create the panel.
	 */

	private static boolean flag = false;

	JPanel UPanel = new JPanel();
	public static JTextArea jta_result = new JTextArea();
	JButton jbtn1, jbtn3;
	JTextArea jta1;
	JComboBox<String> comboBox = new JComboBox<String>();
	JComboBox<String> comboBox2 = new JComboBox<String>();
	String[] lists = null;
	String filePath1, filePath2;// 训练集目录，模型存储目录

	public JPanel initialize() {

		UPanel.setLayout(new BorderLayout());
		UPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

		JPanel leftPanel = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		leftPanel.setLayout(gb);
		Border b1 = BorderFactory.createEmptyBorder(10, 10, 20, 10);
		Border b2 = BorderFactory.createTitledBorder("操作");
		leftPanel.setBorder(BorderFactory.createCompoundBorder(b2, b1));

		JLabel jlbl1 = new JLabel("文本路径：");
		jlbl1.setFont(new Font("宋体", Font.PLAIN, 14));

		jbtn1 = new JButton("选择");
		jbtn1.setFont(new Font("宋体", Font.PLAIN, 14));

		jta1 = new JTextArea();
		jta1.setFont(new Font("宋体", Font.PLAIN, 14));
		jta1.setLineWrap(true);
		jta1.setBounds(20, 50, 300, 50);

		JLabel jlbl2 = new JLabel("决策树构造算法：");
		jlbl2.setFont(new Font("宋体", Font.PLAIN, 14));

		comboBox.addItem("ID3");
		comboBox.addItem("C4_5");
		comboBox.addItem("Cart");

		JLabel jlbl3 = new JLabel("文本分类模型：");
		jlbl3.setFont(new Font("宋体", Font.PLAIN, 14));

		lists = new File("./ID3/").list();
		for (int i = 0; i < lists.length; i++)
			comboBox2.addItem(lists[i]);

		jbtn3 = new JButton("分类");
		jbtn3.setFont(new Font("宋体", Font.PLAIN, 14));

		// GridBagLayout 布局
		// 该GridBagConstraints控制的GUI组件左对齐
		gbc.anchor = GridBagConstraints.WEST;
		// 该GridBagConstraints控制的GUI组件纵向、横向扩大的权重是1
		gbc.weighty = 1;
		gbc.weightx = 1;
		// 该GridBagConstraints控制的GUI组件将横向跨一个网格，纵向跨一个网格
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 60;
		gbc.weighty = 15;
		gb.setConstraints(jlbl1, gbc);
		leftPanel.add(jlbl1);
		gbc.weightx = 40;
		gb.setConstraints(jbtn1, gbc);
		leftPanel.add(jbtn1);

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 1;// 第二行第一列
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weighty = 30;
		gb.setConstraints(jta1, gbc);
		leftPanel.add(jta1);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 2;// 第三行第一列
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 50;
		gbc.weighty = 15;
		gb.setConstraints(jlbl2, gbc);
		leftPanel.add(jlbl2);
		gbc.gridx = 1;
		gbc.gridy = 2;// 第三行第二列
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 50;
		gb.setConstraints(comboBox, gbc);
		leftPanel.add(comboBox);

		gbc.gridx = 0;
		gbc.gridy = 3;// 第四行第一列
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 50;
		gbc.weighty = 15;
		gb.setConstraints(jlbl3, gbc);
		leftPanel.add(jlbl3);
		gbc.gridx = 1;
		gbc.gridy = 3;// 第四行第二列
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 50;
		gb.setConstraints(comboBox2, gbc);
		leftPanel.add(comboBox2);

		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx = 0;
		gbc.gridy = 4;// 第五行第一列
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 100;
		gbc.weighty = 25;
		gb.setConstraints(jbtn3, gbc);
		leftPanel.add(jbtn3);

		JScrollPane jsp = new JScrollPane();
		jta_result.setEditable(false);
		jta_result.setFont(new Font("宋体", Font.PLAIN, 14));
		jsp.setViewportView(jta_result);
		Border b11 = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		Border b22 = BorderFactory.createTitledBorder("结果");
		jsp.setBorder(BorderFactory.createCompoundBorder(b22, b11));

		leftPanel.setPreferredSize(new Dimension(220, 0));
		UPanel.add(leftPanel, BorderLayout.WEST);
		UPanel.add(jsp, BorderLayout.CENTER);

		// 为按钮添加事件监听器
		jbtn1.addActionListener(this);
		jbtn3.addActionListener(this);
		comboBox.addActionListener(this);

		return UPanel;

	}

	// ********************事件处理********************
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == jbtn1) {
			if (flag) {
				JOptionPane.showMessageDialog(null, "正在进行分类！", "", JOptionPane.ERROR_MESSAGE);
			} else {
				JFileChooser filechooser = new JFileChooser();
				int returnVal = filechooser.showOpenDialog(filechooser);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					filePath1 = filechooser.getSelectedFile().getAbsolutePath();
					jta1.setText(filePath1);
				}
			}
		}
		if (e.getSource() == comboBox) {
			if (flag) {
				JOptionPane.showMessageDialog(null, "正在进行分类！", "", JOptionPane.ERROR_MESSAGE);
			} else {
				int index = comboBox.getSelectedIndex();
				String[] lists = null;
				if (index == 0) {
					lists = new File("./ID3").list();
				}
				if (index == 1) {
					lists = new File("./C4_5").list();
				}
				if (index == 2) {
					lists = new File("./CART").list();
				}
				comboBox2.removeAllItems();
				for (int i = 0; i < lists.length; i++)
					comboBox2.addItem(lists[i]);
			}

		}
		if (e.getSource() == jbtn3) {// 建立模型
			if (flag) {
				JOptionPane.showMessageDialog(null, "正在进行分类！", "", JOptionPane.ERROR_MESSAGE);
			} else {
				flag = true;
				TestModel tm = new TestModel();
				if (jta1.getText().isEmpty())
					JOptionPane.showMessageDialog(null, "训练集数据路径不能为空！", "", JOptionPane.ERROR_MESSAGE);
				else {
					String filepath = System.getProperty("user.dir") + "\\" + comboBox.getSelectedItem() + "\\"
							+ comboBox2.getSelectedItem();
					jta_result.setText("文本路径：" + jta1.getText() + "\n分类模型路径：" + filepath + "\n");
					jta_result.paintImmediately(jta_result.getBounds());
					tm.testModel(jta1.getText(), filepath);
					flag=false;
				}
			}
		}
	}

}
