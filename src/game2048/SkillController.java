package game2048;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import game2048.PageController.ViewControllListener;

public class SkillController {
	
	protected PageView view;

	protected Toolkit tk;
	protected Cursor defaultCursor, skillCursor;
	protected Image imgDefaultCursor, imgSkillCursor;
	protected Point pt;
	
	protected SkillMouseListener mouseL; // ���콺 ��ġ�� ���� ��ų ��� ���� ������ �����ְ� ��ų�� ������ �� Listener ����
	
	public SkillController(PageView view) {
		this.view = view;
		view.addSkillListener(new SkillListener());
		
		mouseL = new SkillMouseListener(); // MouseListener ����
		view.gamePage.addMouseListener(mouseL); // add
		view.gamePage.addMouseMotionListener(mouseL); // MotionListener�� add
		
		tk = Toolkit.getDefaultToolkit();
		
		imgDefaultCursor = tk.getImage("Image/defaultCursor.png");
		imgSkillCursor = tk.getImage("Image/swordCursor.png");
		
		pt = new Point(1, 1); //Ŀ���� ������
		
		defaultCursor = tk.createCustomCursor(imgDefaultCursor, pt, "defaultCursor");
		skillCursor = tk.createCustomCursor(imgSkillCursor, pt, "skillCursor");
		
		view.setCursor(defaultCursor);
	} //DefinedCursor()
	
	public void changeCursor(boolean flag) {
		if(flag)	view.setCursor(skillCursor);
		else 		view.setCursor(defaultCursor);
	} //changeCursor()
	
	protected class SkillListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			Object obj = event.getSource();
			
			/* ��ų ��ư�� */
			if (obj == view.gamePage.btnSkill1 &&
				view.gamePage.nSkillPnt >= TileValue.SKILL1)
			{ // ��ĭ	
				if (view.gamePage.nSkill == 1)	view.gamePage.nSkill = 0;
				else							view.gamePage.nSkill = 1;
			}
			else if (obj == view.gamePage.btnSkill2 &&
					 view.gamePage.nSkillPnt >= TileValue.SKILL23)
			{ // ����
				if (view.gamePage.nSkill == 2)	view.gamePage.nSkill = 0;
				else							view.gamePage.nSkill = 2;
			}
			else if (obj == view.gamePage.btnSkill3 &&
					 view.gamePage.nSkillPnt >= TileValue.SKILL23)
			{ // ����
				if (view.gamePage.nSkill == 3)	view.gamePage.nSkill = 0;
				else							view.gamePage.nSkill = 3;
			}
			else if (obj == view.gamePage.btnSkill4 &&
					 view.gamePage.nSkillPnt >= TileValue.SKILL4) { // ����
				if (view.gamePage.nSkill == 4)	view.gamePage.nSkill = 0;
				else							view.gamePage.nSkill = 4;				
			} //if..else if
			
			if(view.gamePage.nSkill == 0)	changeCursor(false);
			else							changeCursor(true);
			
		} //actionPerformed()
	} //SkillControllListener class
	
	protected class SkillMouseListener implements MouseListener, MouseMotionListener {

		public void mouseClicked(MouseEvent event) {}
		public void mousePressed(MouseEvent event) {}
		public void mouseReleased(MouseEvent event) {
			if (view.gamePage.nSkill != 0) {// ���콺�� Ŭ�� �� ���� ������ ��ų�� �ִٸ�
				view.gamePage.skill(event); // ��ų �ߵ�!
				changeCursor(false);
			}
		}
		public void mouseEntered(MouseEvent event) {}
		public void mouseExited(MouseEvent event) {}
		public void mouseDragged(MouseEvent event) {}
		public void mouseMoved(MouseEvent event) {
			// ���콺�� �����϶� ���� ������ ��ų�� �ִٸ�
			if (view.gamePage.nSkill != 0)
				view.gamePage.previewSkill(event); // ��ų �ߵ� ���� ������ ȭ�鿡 ������
		}		
	} //SkillMouseListener class
} //SkillController class
