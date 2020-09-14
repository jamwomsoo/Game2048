package game2048;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;

public class PageView extends JPanel {

	protected MainPage mainPage;
	protected GamePage gamePage;
	protected HowToPage howToPage;
	
	public PageView() {
		setPreferredSize(new Dimension(TileValue.WIDTH, TileValue.HEIGHT));
		setLayout(null);
		
		mainPage = new MainPage();
		mainPage.setBounds(0,0,TileValue.WIDTH,TileValue.HEIGHT);
		add(mainPage);
		
		gamePage = new GamePage();
		gamePage.setBounds(0,0,TileValue.WIDTH,TileValue.HEIGHT);
		gamePage.setVisible(false);
		add(gamePage);
		
		howToPage = new HowToPage();
		howToPage.setBounds(0,0,TileValue.WIDTH,TileValue.HEIGHT);
		howToPage.setVisible(false);
		add(howToPage);
		
	} //PageView()
	
	public void addControllerListener(ActionListener listener) {
		mainPage.addPageChangeListener(listener);
		gamePage.addPageChangeListener(listener);
		howToPage.addPageChangeListener(listener);
	} //addControllerListener()
	
	public void addSkillListener(ActionListener listener) {
		gamePage.addSkillControllListener(listener);
	} //addCursorListener()
	
	public void change(String page) {
		
		if (page.equals("main")) {
			mainPage.setVisible(true);
			gamePage.setVisible(false);
			howToPage.setVisible(false);
			
			setFocusable(false);
		}
		else if (page.equals("game")) {
			mainPage.setVisible(false);
			gamePage.setVisible(true);
			howToPage.setVisible(false);
			
			gamePage.setButtonFocusable();
			setFocusable(true); //KeyEvent를 받을 수 있도록
			requestFocus(); //포커스를 강제 지정
		}
		else if (page.equals("howto")) {
			mainPage.setVisible(false);
			gamePage.setVisible(false);
			howToPage.setVisible(true);
			
			setFocusable(false);
		} //if..else if
		
	} //change()
	
} //PageView class