package game2048;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Popup extends JPanel {

	public static JPanel resetpopup, mainpopup, gameover;
	public static JLabel mainlbl, resetlbl, finalscore, overlbl, retry;
	public static ImageIcon	imgYes1, imgYes2, imgNo1, imgNo2;
	
	protected JButton btnYes, btnNo;
	
	public Popup() {
		setBackground(Color.white);
		setLayout(null);
	}

	public Popup(String str) {
		
		imgYes1 = new ImageIcon("Image/yesButton.png");
		imgYes2 = new ImageIcon("Image/clickedYesButton.png");
		imgNo1 = new ImageIcon("Image/noButton.png");
		imgNo2 = new ImageIcon("Image/clickedNoButton.png");
		
		btnYes = new JButton(imgYes1);
		btnYes.setBounds(50,80,75,35);
		btnYes.setBorderPainted(false);
		btnYes.setContentAreaFilled(false);
		btnYes.setFocusPainted(false);
		btnYes.setPressedIcon(imgYes2);
		add(btnYes);
		
		btnNo = new JButton(imgNo1);
		btnNo.setBounds(175,80,75,35);
		btnNo.setBorderPainted(false);
		btnNo.setContentAreaFilled(false);
		btnNo.setFocusPainted(false);
		btnNo.setPressedIcon(imgNo2);
		add(btnNo);
		
		if (str.equals("reset")) {
		
			setBackground(Color.white);
			setLayout(null);
			
			resetlbl = new JLabel("리셋하시겠습니까?");
			resetlbl.setBounds(50,35,200,35);
			resetlbl.setFont(new Font("돋움",Font.BOLD,15));
			resetlbl.setHorizontalAlignment(SwingConstants.CENTER);
			add(resetlbl);
		}
		
		else if (str.equals("main")) {
			
			setBackground(Color.white);
			setLayout(null);
			
			mainlbl = new JLabel("메인으로 돌아가시겠습니까?");
			mainlbl.setBounds(50,35,200,35);
			mainlbl.setFont(new Font("돋움",Font.BOLD,15));
			mainlbl.setHorizontalAlignment(SwingConstants.CENTER);
			add(mainlbl);
		}
		
		else if (str.equals("gameover")) {
			
			setBackground(Color.white);
			setLayout(null);
			
			overlbl = new JLabel("게임 오버!");
			overlbl.setBounds(50,7,200,30);
			overlbl.setForeground(Color.red);
			overlbl.setFont(new Font("궁서",Font.BOLD,20));
			overlbl.setHorizontalAlignment(SwingConstants.CENTER);
			add(overlbl);
			
			finalscore = new JLabel("최종 스코어: ");
			finalscore.setBounds(50,40,200,20);
			finalscore.setFont(new Font("돋움",Font.BOLD,15));
			finalscore.setHorizontalAlignment(SwingConstants.CENTER);
			add(finalscore);
			
			retry = new JLabel("다시 하시겠습니까?");
			retry.setBounds(50,55,200,20);
			retry.setFont(new Font("돋움",Font.BOLD,15));
			retry.setHorizontalAlignment(SwingConstants.CENTER);
			add(retry);
		}
	}
	
	public void addButtonListener(ActionListener listener) {
		btnYes.addActionListener(listener);
		btnNo.addActionListener(listener);
	} //addPageChangeListener()
}
