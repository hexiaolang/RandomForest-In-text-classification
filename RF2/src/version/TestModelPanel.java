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
import javax.swing.JFileChooser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestModelPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	/**
	 * Create the panel.
	 */
	private static boolean flag = false;

	JPanel TPanel = new JPanel();
	public static JTextArea jta_result = new JTextArea();
	JButton jbtn1, jbtn2, jbtn3;
	JTextArea jta1, jta2;
	String filePath1, filePath2;// 测试集目录，待测模型目录

	public JPanel initialize() {

		TPanel.setLayout(new BorderLayout());
		TPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

		JPanel leftPanel = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		leftPanel.setLayout(gb);
		Border b1 = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		Border b2 = BorderFactory.createTitledBorder("操作");
		leftPanel.setBorder(BorderFactory.createCompoundBorder(b2, b1));

		JLabel jlbl1 = new JLabel("测试集路径：");
		jlbl1.setFont(new Font("宋体", Font.PLAIN, 14));

		jbtn1 = new JButton("选择");
		jbtn1.setFont(new Font("宋体", Font.PLAIN, 14));

		jta1 = new JTextArea();
		jta1.setFont(new Font("宋体", Font.PLAIN, 14));
		jta1.setLineWrap(true);
		jta1.setBounds(20, 50, 300, 50);

		JLabel jlbl2 = new JLabel("待测模型路径：");
		jlbl2.setFont(new Font("宋体", Font.PLAIN, 14));

		jbtn2 = new JButton("选择");
		jbtn2.setFont(new Font("宋体", Font.PLAIN, 14));

		jta2 = new JTextArea();
		jta2.setFont(new Font("宋体", Font.PLAIN, 14));
		jta2.setLineWrap(true);

		jbtn3 = new JButton("测试模型");
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
		gbc.weighty = 10;
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
		gbc.weighty = 15;
		gb.setConstraints(jta1, gbc);
		leftPanel.add(jta1);

		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 2; // 第三行第一列
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 60;
		gbc.weighty = 10;
		gb.setConstraints(jlbl2, gbc);
		leftPanel.add(jlbl2);
		gbc.gridx = 1;
		gbc.gridy = 2;// 第三行第二列
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 40;
		gb.setConstraints(jbtn2, gbc);
		leftPanel.add(jbtn2);

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 3;// 第四行第一列
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weighty = 15;
		gb.setConstraints(jta2, gbc);
		leftPanel.add(jta2);

		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx = 0;
		gbc.gridy = 4;// 第五行第一列
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
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
		TPanel.add(leftPanel, BorderLayout.WEST);
		TPanel.add(jsp, BorderLayout.CENTER);

		// 为按钮添加事件监听器
		jbtn1.addActionListener(this);
		jbtn2.addActionListener(this);
		jbtn3.addActionListener(this);

		return TPanel;

	}

	// ********************事件处理********************
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jbtn1) {
			if (flag) {
				JOptionPane.showMessageDialog(null, "正在测试模型！", "", JOptionPane.ERROR_MESSAGE);
			} else {
				JFileChooser filechooser = new JFileChooser();
				int returnVal = filechooser.showOpenDialog(filechooser);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					filePath1 = filechooser.getSelectedFile().getAbsolutePath();
					jta1.setText(filePath1);
				}
			}
		}
		if (e.getSource() == jbtn2) {
			if (flag) {
				JOptionPane.showMessageDialog(null, "正在测试模型！", "", JOptionPane.ERROR_MESSAGE);
			} else {
				JFileChooser filechooser = new JFileChooser();
				int returnVal = filechooser.showOpenDialog(filechooser);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					filePath2 = filechooser.getSelectedFile().getAbsolutePath();
					jta2.setText(filePath2);
				}
			}
		}
		if (e.getSource() == jbtn3) {// 测试模型
			if (flag) {
				JOptionPane.showMessageDialog(null, "正在测试模型！", "", JOptionPane.ERROR_MESSAGE);
			} else {
				flag = true;
				TestModel tm = new TestModel();
				if (jta1.getText().isEmpty())
					JOptionPane.showMessageDialog(null, "测试集数据路径不能为空！", "", JOptionPane.ERROR_MESSAGE);
				if (jta2.getText().isEmpty())
					JOptionPane.showMessageDialog(null, "待测模型路径不能为空！", "", JOptionPane.ERROR_MESSAGE);
				else {
					jta_result.setText("测试集数据文件路径：" + jta1.getText() + "\n待测模型路径：" + jta2.getText() + "\n");
					jta_result.paintImmediately(jta_result.getBounds());
					tm.testModel(jta1.getText(), jta2.getText());
					flag=false;
				}
			}
		}
	}

}
