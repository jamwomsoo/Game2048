package game2048;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class PageController{
	
	protected PageView view;
	public static Clip clip;
	
	public PageController(PageView view) {
		this.view = view;
		view.addControllerListener(new ViewControllListener());
		view.addKeyListener(new keyEvent());
		
		Play("Sound/intro.wav");
	} //PageController()
	
	protected class ViewControllListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			Object obj = event.getSource();
			
			if(obj == view.mainPage.btnStart) {
				view.change("game");
				
				clip.close();
				Play("Sound/game.wav");
			}
			else if(obj == view.mainPage.btnHowtoPlay) {
				view.change("howto");
			}
			else if(obj == view.mainPage.btnExit) {
				System.exit(0); //���α׷� ����
			}
			
			else if (obj == view.gamePage.reset.btnNo ||
					 obj == view.gamePage.main.btnNo) {
				view.gamePage.reset.setVisible(false);
				view.gamePage.main.setVisible(false);
			}
			else if (obj == view.gamePage.reset.btnYes ||
					 obj == view.gamePage.overpopup.btnYes) {
				view.gamePage.reset.setVisible(false);
				view.gamePage.overpopup.setVisible(false);
				view.gamePage.reset();
				
				clip.close();
				Play("Sound/game.wav");
			}
			else if(obj == view.gamePage.main.btnYes ||
					obj == view.gamePage.overpopup.btnNo)
			{
				view.gamePage.main.setVisible(false);
				view.gamePage.overpopup.setVisible(false);
				view.change("main");
				
				clip.close();
				Play("Sound/intro.wav");
			}
			
			else if(obj == view.howToPage.btnExit) {
				view.change("main");
				
				clip.close();
				Play("Sound/intro.wav");
			} //if..else if
			
		} //actionPerformed()
	} //ViewControllListener class
	
	protected class keyEvent implements KeyListener {
		public void keyPressed(KeyEvent event) { //Ű�� ������ ����
			
			// Ÿ���� �̵� ���̶�� Ű���� �Է� ����
			if (view.gamePage.bMovingNow)
				return;
			
			int keyCode = event.getKeyCode();
			
			switch(keyCode) {
			case KeyEvent.VK_LEFT:
				view.gamePage.moveLeft(0);
				break;
				
			case KeyEvent.VK_RIGHT:
				view.gamePage.moveLeft(180);
				break;
				
			case KeyEvent.VK_UP:
				view.gamePage.moveLeft(90);
				break;
				
			case KeyEvent.VK_DOWN:
				view.gamePage.moveLeft(270);
				break;
			}
		} //keyPressed()

		// ���� Ű�� ���� ����
		public void keyReleased(KeyEvent event) {} //keyReleased()
		// ���� Ű�� ���� ����, Unicode Ű�� �Էµ� ��쿡��
		public void keyTyped(KeyEvent event) {} //keyTyped()
	} //keyEvent class
	
	public static void Play(String fileName) {
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(new File(fileName));
        	clip = AudioSystem.getClip();
        	clip.stop();
        	clip.open(ais);
        	clip.loop(100);
            clip.start();
		}
		catch (Exception ex) {
        }
    } //Play()
	
} //PageController class
