package version;

import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.SwingConstants;
import java.awt.SystemColor;

public class Index {

	private JFrame frame;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Index window = new Index();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Index() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(400, 100, 700, 550);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel jl1=new JLabel("基于随机森林的文本自动分类系统");
		jl1.setFont(new Font("宋体", Font.PLAIN, 24));
		jl1.setHorizontalAlignment(SwingConstants.CENTER);
		jl1.setPreferredSize(new Dimension(0, 60));
		frame.getContentPane().add(jl1, BorderLayout.NORTH);
		
		//中部区域
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("宋体", Font.PLAIN, 20));
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel IPanel=new JPanel();
		tabbedPane.add("  主页  ", IPanel);
		IPanel.setLayout(new GridLayout(6, 0));
		IPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));
		
		JLabel lblNewLabel = new JLabel("文本自动分类系统说明");
		lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		IPanel.add(lblNewLabel);
		
		JTextArea jta1=new JTextArea();
		jta1.setBackground(SystemColor.control);
		jta1.setLineWrap(true);
		jta1.setEditable(false);
		jta1.setFont(new Font("宋体", Font.PLAIN, 16));
		jta1.setText("1.本系统共有三个功能，请在选项卡菜单栏进行选择，包括：建立模型，测试模型，文本分类。");
		IPanel.add(jta1);
		
		JTextArea jta2=new JTextArea();
		jta2.setBackground(SystemColor.control);
		jta2.setLineWrap(true);
		jta2.setEditable(false);
		jta2.setFont(new Font("宋体", Font.PLAIN, 14));
		jta2.setText("2.建立模型：");
		IPanel.add(jta2);
		
		JTextArea jta3=new JTextArea();
		jta3.setBackground(SystemColor.control);
		jta3.setLineWrap(true);
		jta3.setEditable(false);
		jta3.setFont(new Font("宋体", Font.PLAIN, 14));
		jta3.setText("3.测试模型：");
		IPanel.add(jta3);
		
		JTextArea jta4=new JTextArea();
		jta4.setBackground(SystemColor.control);
		jta4.setLineWrap(true);
		jta4.setEditable(false);
		jta4.setFont(new Font("宋体", Font.PLAIN, 14));
		jta4.setText("4.文本自动分类：");
		IPanel.add(jta4);
		
		JLabel lblNewLabel_2 = new JLabel("系统版本号1.0");
		lblNewLabel_2.setFont(new Font("宋体", Font.PLAIN, 12));
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		IPanel.add(lblNewLabel_2); 
		
		BuildModelPanel bmp=new BuildModelPanel();
		tabbedPane.add("  建立模型  ", bmp.initialize());
		
		
		TestModelPanel tmp=new TestModelPanel();
		tabbedPane.add("  测试模型  ", tmp.initialize());
		
		UseModelPanel ump=new UseModelPanel();
		tabbedPane.add("  文本自动分类  ", ump.initialize());
	}

}
