package game2048;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MainPage extends JPanel {
	
	private ImageIcon titleImage;
	private JLabel lblProgrammedBy;
	
	protected JButton btnStart, btnExit, btnHowtoPlay;
	
	public MainPage() {
		setLayout(null); 
		
		btnStart = new JButton (new ImageIcon("Image/startButton.png"));
		btnStart.setBounds(135, 380, 280, 80);
		btnStart.setBorderPainted(false);
		btnStart.setContentAreaFilled(false);
		btnStart.setFocusPainted(false);
		btnStart.setPressedIcon(new ImageIcon("Image/clickedStartButton.png")); //버튼을 누른 상태일 때 출력되는 이미지 설정
		add(btnStart);
		
		btnHowtoPlay = new JButton (new ImageIcon("Image/HowtoPlayButton.png"));
		btnHowtoPlay.setBounds(135, 480, 280, 80);
		btnHowtoPlay.setBorderPainted(false);
		btnHowtoPlay.setContentAreaFilled(false);
		btnHowtoPlay.setFocusPainted(false);
		btnHowtoPlay.setPressedIcon(new ImageIcon("Image/clickedHowtoPlayButton.png"));
		add(btnHowtoPlay);
		
		btnExit = new JButton (new ImageIcon("Image/exitButton.png"));
		btnExit.setBounds(135, 580, 280, 80);
		btnExit.setBorderPainted(false);
		btnExit.setContentAreaFilled(false);
		btnExit.setFocusPainted(false);
		btnExit.setPressedIcon(new ImageIcon("Image/clickedExitButton.png"));
		add(btnExit);
		
		titleImage = new ImageIcon("Image/title.png");
		
		lblProgrammedBy = new JLabel("Programmed by Group 1");
		lblProgrammedBy.setBounds(175, 725, 200, 15);
		lblProgrammedBy.setFont(new Font("고딕", Font.PLAIN, 15));
		lblProgrammedBy.setForeground(Color.white);
		lblProgrammedBy.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblProgrammedBy);
		
	} //MainPage()
	
	public void paintComponent(Graphics page) {
		super.paintComponent(page);
		
		page.drawImage(titleImage.getImage(), 0, 0, null);
		setOpaque(false); //배경이 보이도록 하기 위해
						  //불투명하게 지정
		
	} //paintComponent()
	
	public void addPageChangeListener(ActionListener listener) {
		btnStart.addActionListener(listener);
		btnExit.addActionListener(listener);
		btnHowtoPlay.addActionListener(listener);
	} //addPageChangeListener()
	
} //MainPage class
