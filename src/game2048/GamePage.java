package game2048;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class GamePage extends JPanel implements Runnable {

	protected Tile[][] myTile; // ������Ʈ �������� ������ 2���� �迭
	protected Tile[][] movedTiles; // ������Ʈ �̵� �˰��� ���� 2���� �迭, �̵��� �Ϸ�� ������ ������ �����Ѵ�. �̵� �ִϸ��̼��� ������ myTile�� movedTiles�� ������Ʈ ���ش�.

	protected boolean bMovingNow; // ��ҿ��� false, ������Ʈ �̵��߿� true, ���� �̵� �ִϸ��̼��� ���� ������ üũ
	protected boolean bShowSkill; // skill ��� ������ ǥ���� ���� Ȯ�����ִ� ����
	
	protected Thread 			moveThread;	// �̵� �ִϸ��̼��� ���� �� Thread ����
	protected ButtonListener 	buttonL; // �����¿� �̵� ��ư, ���� ��ư, ���� ��ư�� ���� �� Listener ����
	protected JLabel 			lblMaxScr, lblLiveScr, lblLiveScr2, lblSkillPnt, lblSkill1, lblSkill2, lblSkill3, lblSkill4; // ���¸� ��� �� label ������
	protected JButton 			btnMain, btnReset, btnUp, btnDown, btnRight, btnLeft; // button ������
	protected ImageIcon 		imgBack, imgPlayer; // Background �̹���, Player �̹���, Monster �̹���
	protected int 				nMaxScr, nLiveScr; // ���ھ�, ��ų ����Ʈ ���� ���� ������ int�� ������
	
	protected int				lastCurX, lastCurY; // ��ų ���� ������ ������ �� ����, ���������� ���콺�� ��ġ�ߴ� ��ǥ ��ġ(0,0 ~ 5,5)�� ����

	protected int			nSkillPnt, nSkill;
	protected JButton		btnSkill1, btnSkill2, btnSkill3, btnSkill4;
	
	// �ִ� ������ ������ ���� ����¿� ���� ������
	private String		MaxScore, max;//������� �޾ƿ��� ���� ���� ����
	private int 		cnt;//
	private FileReader	filein;
	private FileWriter	fileout;
	
	// �˾� â Ŭ���� ����. ���� ����, ����, ���ӿ����� ����� �˾�
	protected Popup reset, main, overpopup;
	
	public GamePage() {
		setLayout(null);
		
		// ������Ʈ ������ ������ 2���� �迭 myTile ���� �� �ʱ�ȭ
		myTile = new Tile[TileValue.MAP_LEN][TileValue.MAP_LEN];
		movedTiles = new Tile[TileValue.MAP_LEN][TileValue.MAP_LEN];
		for (int y = 0; y < TileValue.MAP_LEN; y++) {
			for (int x = 0; x < TileValue.MAP_LEN; x++) {
				myTile[y][x] = new Tile(0, x, y);
			}
		}
		
		// �̵� �ִϸ��̼� ������ null�� �ʱ�ȭ
		moveThread = null;

		// ButonListener ����
		buttonL = new ButtonListener();
		
		// ���� ��������� �ִ� ������ �޾ƿ� 
		max = "";//ó�� max�� ��ĭ���� �ʱ�ȭ
		cnt = 0;//cnt�� 0���� �ʱ�ȭ
		fileout = null;//���� �ƿ��� �η� �ʱ�ȭ
		
		try {
			filein = new FileReader("./data_file.txt"); // ���� ��� ���
			 
			 while((cnt = filein.read())!= -1) {//-1�� ���ö� ���� ������ ������ �о��ش�
				 if(cnt == -1) break;//���� -1�� ������ �������ְ� ���������� ���
				 char readWord = (char) cnt;//readWord�� �Է¹��� cnt�� ���� char�� �� ��ȯ
				 max += readWord;//��Ʈ���� max�� �޾ƿ� readWord�� �־��ش�
			 }

			 filein.close();//���� �Է� ����
			}
		catch(IOException e){
			e.printStackTrace();//����ó��
		}
		
		nMaxScr = Integer.parseInt(max); //���� �ȿ� �ƹ� �����͵� ������ �ʱ�ȭ ����
		
		// ������ �ʱ�ȭ
		nLiveScr = 0;
		nSkillPnt = 0;
		nSkill = 0;
		
		lastCurX = 0;
		lastCurY = 0;
		
		bMovingNow = false;
		bShowSkill = false;

		// �˾� â ���� �� setVisible�� false���ش�.
		reset = new Popup("reset");
		reset.setBounds(125,310,300,130);
		add(reset);
		reset.setVisible(false);
		
		main = new Popup("main");
		main.setBounds(125,310,300,130);
		add(main);
		main.setVisible(false);
		
		overpopup = new Popup("gameover");
		overpopup.setBounds(125,310,300,130);
		add(overpopup);
		overpopup.setVisible(false);

		btnReset = new JButton(new ImageIcon("Image/resetButton.png"));
		btnReset.setBounds(390,20,60,60);
		btnReset.addActionListener(buttonL);
		btnReset.setBorderPainted(false); //�ܰ��� ���ֱ�
		btnReset.setContentAreaFilled(false); //ä��� ����
		btnReset.setFocusPainted(false); //���õ� �� �۾� �׵θ� ����
		btnReset.setPressedIcon(new ImageIcon("Image/pressedResetButton.png"));
		add(btnReset);

		btnMain = new JButton (new ImageIcon("Image/homeButton.png"));
		btnMain.setBounds(475,20,60,60);
		btnMain.addActionListener(buttonL);
		btnMain.setBorderPainted(false);
		btnMain.setContentAreaFilled(false);
		btnMain.setFocusPainted(false);
		btnMain.setPressedIcon(new ImageIcon("Image/pressedHomeButton.png"));
		add(btnMain);
	
		lblMaxScr = new JLabel("�ְ���: " + nMaxScr);
		lblMaxScr.setFont(new Font("����",Font.BOLD,23));
		lblMaxScr.setBounds(50,25,200,25);
		lblMaxScr.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblMaxScr);
		
		lblLiveScr = new JLabel("��������");
		lblLiveScr.setFont(new Font("����",Font.BOLD,28));
		lblLiveScr.setBounds(50,75,200,35);
		lblLiveScr.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblLiveScr);
		
		lblLiveScr2 = new JLabel(Integer.toString(nLiveScr));
		lblLiveScr2.setFont(new Font("����",Font.BOLD,52));
		lblLiveScr2.setBounds(50,100,200,80);
		lblLiveScr2.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblLiveScr2);
		
		lblSkillPnt = new JLabel("��ų����Ʈ : " + nSkillPnt);
		lblSkillPnt.setFont(new Font("����",Font.BOLD,26));
		lblSkillPnt.setBounds(280,140,225,35);
		add(lblSkillPnt);
		
		lblSkill1 = new JLabel("sp: " + TileValue.SKILL1);
		lblSkill1.setFont(new Font("����", Font.BOLD, 16));
		lblSkill1.setForeground(Color.white);
		lblSkill1.setBounds(20, 726, 80, 15);
		lblSkill1.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblSkill1);
		
		lblSkill2 = new JLabel("sp: " + TileValue.SKILL23);
		lblSkill2.setFont(new Font("����", Font.BOLD, 16));
		lblSkill2.setForeground(Color.white);
		lblSkill2.setBounds(110, 726, 80, 15);
		lblSkill2.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblSkill2);
		
		lblSkill3 = new JLabel("sp: " + TileValue.SKILL23);
		lblSkill3.setFont(new Font("����", Font.BOLD, 16));
		lblSkill3.setForeground(Color.white);
		lblSkill3.setBounds(200, 726, 80, 15);
		lblSkill3.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblSkill3);
		
		lblSkill4 = new JLabel("sp: " + TileValue.SKILL4);
		lblSkill4.setFont(new Font("����", Font.BOLD, 16));
		lblSkill4.setForeground(Color.white);
		lblSkill4.setBounds(290, 726, 80, 15);
		lblSkill4.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblSkill4);
		
		btnSkill1 = new JButton(new ImageIcon("Image/skillButton1.png"));
		btnSkill1.setFont(new Font("����",Font.BOLD,22));
		btnSkill1.setHorizontalAlignment(SwingConstants.CENTER);
		btnSkill1.setBounds(20,642,78,78);
		btnSkill1.setBorderPainted(false);
		btnSkill1.setContentAreaFilled(false);
		btnSkill1.setFocusPainted(false);
		btnSkill1.setPressedIcon(new ImageIcon("Image/clickedSkillButton1.png"));
		add(btnSkill1);
		
		//����
		btnSkill2=new JButton(new ImageIcon("Image/skillButton2.png"));
		btnSkill2.setFont(new Font("����",Font.BOLD,22));
		btnSkill2.setHorizontalAlignment(SwingConstants.CENTER);
		btnSkill2.setBounds(110,642,78,78);
		btnSkill2.setBorderPainted(false);
		btnSkill2.setContentAreaFilled(false);
		btnSkill2.setFocusPainted(false);
		btnSkill2.setPressedIcon(new ImageIcon("Image/clickedSkillButton2.png"));
		add(btnSkill2);
		
		//����
		btnSkill3 = new JButton(new ImageIcon("Image/skillButton3.png"));
		btnSkill3.setFont(new Font("����",Font.BOLD,22));
		btnSkill3.setHorizontalAlignment(SwingConstants.CENTER);
		btnSkill3.setBounds(200,642,78,78);
		btnSkill3.setBorderPainted(false);
		btnSkill3.setContentAreaFilled(false);
		btnSkill3.setFocusPainted(false);
		btnSkill3.setPressedIcon(new ImageIcon("Image/clickedSkillButton3.png"));
		add(btnSkill3);
		
		btnSkill4 = new JButton(new ImageIcon("Image/skillButton4.png"));
		btnSkill4.setFont(new Font("����",Font.BOLD,22));
		btnSkill4.setHorizontalAlignment(SwingConstants.CENTER);
		btnSkill4.setBounds(290,642,78,78);
		btnSkill4.setBorderPainted(false);
		btnSkill4.setContentAreaFilled(false);
		btnSkill4.setFocusPainted(false);
		btnSkill4.setPressedIcon(new ImageIcon("Image/clickedSkillButton4.png"));
		add(btnSkill4);
		
		btnLeft = new JButton(new ImageIcon("Image/left.png"));
		btnLeft.setBounds(383,690,48,48);
		btnLeft.addActionListener(buttonL);
		btnLeft.setBorderPainted(false);
		btnLeft.setContentAreaFilled(false);
		btnLeft.setFocusPainted(false);
		btnLeft.setPressedIcon(new ImageIcon("Image/pressedLeft.png"));
		add(btnLeft);
		
		btnRight = new JButton(new ImageIcon("Image/right.png"));
		btnRight.setBounds(489,690,48,48);
		btnRight.addActionListener(buttonL);
		btnRight.setBorderPainted(false);
		btnRight.setContentAreaFilled(false);
		btnRight.setFocusPainted(false);
		btnRight.setPressedIcon(new ImageIcon("Image/pressedRight.png"));
		add(btnRight);
				
		btnUp = new JButton(new ImageIcon("Image/up.png"));
		btnUp.setBounds(436,637,48,48);
		btnUp.addActionListener(buttonL);
		btnUp.setBorderPainted(false);
		btnUp.setContentAreaFilled(false);
		btnUp.setFocusPainted(false);
		btnUp.setPressedIcon(new ImageIcon("Image/pressedUp.png"));
		add(btnUp);
		
		btnDown = new JButton(new ImageIcon("Image/down.png"));
		btnDown.setBounds(436,690,48,48);
		btnDown.addActionListener(buttonL);
		btnDown.setBorderPainted(false);
		btnDown.setContentAreaFilled(false);
		btnDown.setFocusPainted(false);
		btnDown.setPressedIcon(new ImageIcon("Image/pressedDown.png"));
		add(btnDown);
		
		imgBack = new ImageIcon("Image/background70.png");
		imgPlayer = new ImageIcon("Image/fighter.png");
		
		// ���� ���� �� Tile 2���� Player �ʿ� add
		addObject(TileValue.TILE);
		addObject(TileValue.TILE);
		addObject(TileValue.PLAYER);
	}
	
	public void addPageChangeListener(ActionListener listener) {
		// �˾�â�� Button Listener�� add
		reset.addButtonListener(listener);
		main.addButtonListener(listener);
		overpopup.addButtonListener(listener);
	}
	
	public void addSkillControllListener(ActionListener listener) {
		btnSkill1.addActionListener(listener);
		btnSkill2.addActionListener(listener);
		btnSkill3.addActionListener(listener);
		btnSkill4.addActionListener(listener);;
	}
	
	public void setButtonFocusable() {
		btnMain.setFocusable(false);
		btnReset.setFocusable(false);
		btnUp.setFocusable(false);
		btnDown.setFocusable(false);
		btnRight.setFocusable(false);
		btnLeft.setFocusable(false);
		btnSkill1.setFocusable(false);
		btnSkill2.setFocusable(false);
		btnSkill3.setFocusable(false);
		btnSkill4.setFocusable(false);
	}

	// ������ �������ִ� �޼ҵ�
	public void reset() {
		
		// myTile[6][6] �� �°� ������ �ʱ�ȭ
		myTile = new Tile[TileValue.MAP_LEN][TileValue.MAP_LEN];
		movedTiles = new Tile[TileValue.MAP_LEN][TileValue.MAP_LEN];
		for (int y = 0; y < TileValue.MAP_LEN; y++) {
			for (int x = 0; x < TileValue.MAP_LEN; x++) {
				myTile[y][x] = new Tile(0, x, y);
			}
		}		
		moveThread = null;
		//���� ����� ���� �ʱ�ȭ
		max = "";
		cnt = 0;
		fileout = null;
		
		// txt���Ͽ��� �ְ������� ������������� �ҷ���
		try {
			filein = new FileReader("./data_file.txt"); // ���� ���
			 
			 while((cnt = filein.read())!= -1) {//-1�� ������������ cnt�� �ҷ��� ���� �־���
				 if(cnt == -1) break;//���� -1�� ������ �������ְ� �� �������� ���
				 char readWord = (char) cnt;//char�� �� ��ȯ
				 max += readWord;//�Է¹��� char���� ������ string�� Ÿ�� max�� �־��ش�
			 }

			 filein.close();//���� �ҷ����� ����
			}
		catch(IOException e){
			e.printStackTrace();//����ó�� �� ���� �۾�
		}
		nMaxScr = Integer.parseInt(max); //���� �ȿ� �ƹ� �����͵� ������ �ʱ�ȭ ����
		
		nLiveScr = 0;
		nSkillPnt = 0;
		nSkill = 0;
		
		lastCurX = 0;
		lastCurY = 0;
		
		bMovingNow = false;
		bShowSkill = false;
		
		lblMaxScr.setText("�ְ���: " + nMaxScr);
		lblLiveScr2.setText(Integer.toString(nLiveScr));
		lblSkillPnt.setText("��ų����Ʈ : " + nSkillPnt);
		
		addObject(TileValue.TILE);
		addObject(TileValue.TILE);
		addObject(TileValue.PLAYER);
		
		repaint();
	}
	
	// �̵� �ִϸ��̼��� �����ϴ� thread�� run �Լ�
	public void run() {
		
		boolean bRepeat = true; // �ݺ� check ����
		int nMovedTilesNum = 1, nCnt = 0; // ���� �����̰� �ִ� ������Ʈ ������ ����. �̵��ϴ� ������Ʈ ������ ���� �̵� �ӵ� ������ ���ؼ� ����
		float fMovedSpeed = 0.0003f; // �̵� �ӵ�
		
		while (bRepeat) {	
			bRepeat = false;
			
			for (int y = 0; y < TileValue.MAP_LEN; y++) {
				for (int x = 0; x < TileValue.MAP_LEN; x++) {
					if (myTile[y][x].bMoved) { // if ������Ʈ�� �̵��ؾ��Ѵٸ�

						bRepeat = true; // �� ���̶� �̵������� ��� �ݺ�
						nCnt++; // �̵����� Ÿ�� count

						Tile tile = myTile[y][x];
						
						// ������ �̵�
						if (tile.x < tile.desX) { // if ������Ʈ�� desX�� x���� ũ�ٸ� ���������� �̵��ؾ� �Ѵ�.
							tile.moveOffsetX = tile.moveOffsetX + fMovedSpeed * nMovedTilesNum; // �ӵ��� ��ü �̵� ������Ʈ ���� ���� moveOffset ����
							if (tile.moveOffsetX > (tile.desX - tile.x) * TileValue.TILE_SIZE) { // if moveOffset�� �̵��ؾ� �� ���̸� �Ѿ�ٸ�
								tile.bMoved = false; // bMoved��  false�� �ٲ㼭 �̵��� �����ٰ� �˸�
							}
						}
						// ���� �̵�
						else if (tile.x > tile.desX) {
							tile.moveOffsetX = tile.moveOffsetX - fMovedSpeed * nMovedTilesNum;
							if (tile.moveOffsetX < (tile.desX - tile.x) * TileValue.TILE_SIZE) {
								tile.bMoved = false;
							}
						}
						// �Ʒ��� �̵�
						else if (tile.y < tile.desY) {
							tile.moveOffsetY = tile.moveOffsetY + fMovedSpeed * nMovedTilesNum;
							if (tile.moveOffsetY > (tile.desY - tile.y) * TileValue.TILE_SIZE) {
								tile.bMoved = false;
							}
						}
						// ���� �̵�
						else if (tile.y > tile.desY) {
							tile.moveOffsetY = tile.moveOffsetY - fMovedSpeed * nMovedTilesNum;
							if (tile.moveOffsetY < (tile.desY - tile.y) * TileValue.TILE_SIZE) {
								tile.bMoved = false;
							}
						}
						repaint();
					}
				}
			}
			nMovedTilesNum = nCnt; // �̵� Ŭ������ �̵����� Ÿ�� �� update
			nCnt = 0;
		}
		
		// �̵� �� ������ �����ϰ� �ִ� movedTiles�� myTile�� ����
		for(int y = 0; y < TileValue.MAP_LEN; y++) {
			for(int x = 0; x < TileValue.MAP_LEN; x++) {
				myTile[y][x].setData(movedTiles[y][x]);
			}
		}
		movedTiles = null;
		
		// Tile 1�� �߰�, 1/3 Ȯ���� 2�� �߰�
		addObject(TileValue.TILE);
		if ((int)(Math.random() * 2) == 0)
			addObject(TileValue.TILE);
		
		// 20 �ۼ�Ʈ Ȯ���� ���� �߰�
		if ((int)(Math.random() * 5) == 0)
			addObject(TileValue.MONSTER);
		
		// �̵� �� �� �̻� ������ �� �ִ��� üũ���ִ� canMove() �޼ҵ� ����, ������ �� ���ٸ� gameOver �˾� �巯��
		if(!canMove()) {
			overpopup.finalscore.setText("���� ���ھ�: " + nLiveScr);
			overpopup.setVisible(true);
		}
		
		repaint();
		
		bMovingNow = false; // �̵��� �����ٰ� �˸�. �̵� ��ư �Է� ����
	}

	// ������Ʈ �̵� �ִϸ��̼� ���� �Լ� Thread�� run() ���ش�.
	private void startMoveThread() {
		moveThread = new Thread(this);		
		moveThread.start();
	}

	public void paintComponent(Graphics page) {
		super.paintComponent(page);

		page.drawImage(imgBack.getImage(), 0, 0, getWidth(), getHeight(), this);

		page.setColor(Color.black);
		page.drawRect(50, 180, 450, 450);
		page.drawLine(125, 180, 125, 630);
		page.drawLine(200, 180, 200, 630);
		page.drawLine(275, 180, 275, 630);
		page.drawLine(350, 180, 350, 630);
		page.drawLine(425, 180, 425, 630);
		page.drawLine(50, 255, 500, 255);
		page.drawLine(50, 330, 500, 330);
		page.drawLine(50, 405, 500, 405);
		page.drawLine(50, 480, 500, 480);
		page.drawLine(50, 555, 500, 555);

		// Tile �迭 ��ü�� ���� value�� 0�� �ƴϸ�, draw
		for (int y = 0; y < TileValue.MAP_LEN; y++) {
			for (int x = 0; x < TileValue.MAP_LEN; x++) {
				if (myTile[y][x].value != 0)
					drawObject(page, myTile[y][x]);
			}
		}
		
		// ���� ���õ� ��ų�� �����鼭(nSkill != 0) && ���콺 Ŀ���� ��ġ�� �� �ȿ� ���� ��� (0 <= x <= 5 && 0 <= y <= 5)
		if (nSkill != 0 && bShowSkill) {
			drawSkillPreview(page);
		}
	}
	
	private void drawSkillPreview(Graphics g) {
		
		Graphics2D g2 = (Graphics2D)g;
		int locX, locY;
		
		// ����� color ����
		g2.setStroke(new BasicStroke(3));
		g2.setColor(Color.red);
		
		// �Է� �� ��ų�� �������� �׷����� ��ų ������ �ٸ���.
		switch(nSkill) {
		case 1: // ���콺 ��ġ�� 1ĭ �׷���
			locX = TileValue.MAP_OFFSET_X + (lastCurX * TileValue.TILE_SIZE);
			locY = TileValue.MAP_OFFSET_Y + (lastCurY * TileValue.TILE_SIZE);
			g2.drawRect(locX, locY, TileValue.TILE_SIZE, TileValue.TILE_SIZE);
			break;
		case 2: // ���콺 ��ġ�� ���� 6ĭ �׷���
			locX = TileValue.MAP_OFFSET_X;
			locY = TileValue.MAP_OFFSET_Y + (lastCurY * TileValue.TILE_SIZE);
			g2.drawRect(locX, locY, TileValue.TILE_SIZE * TileValue.MAP_LEN, TileValue.TILE_SIZE);
			break;
		case 3: // ���콺 ��ġ�� ���� 6ĭ �׷���
			locX = TileValue.MAP_OFFSET_X + (lastCurX * TileValue.TILE_SIZE);
			locY = TileValue.MAP_OFFSET_Y;
			g2.drawRect(locX, locY, TileValue.TILE_SIZE, TileValue.TILE_SIZE * TileValue.MAP_LEN);
			break;
		case 4: // ���콺 ��ġ�� ���� 6ĭ, ���� 6ĭ �׷���
			locX = TileValue.MAP_OFFSET_X;
			locY = TileValue.MAP_OFFSET_Y + (lastCurY * TileValue.TILE_SIZE);
			g2.drawRect(locX, locY, TileValue.TILE_SIZE * TileValue.MAP_LEN, TileValue.TILE_SIZE);
			locX = TileValue.MAP_OFFSET_X + (lastCurX * TileValue.TILE_SIZE);
			locY = TileValue.MAP_OFFSET_Y;
			g2.drawRect(locX, locY, TileValue.TILE_SIZE, TileValue.TILE_SIZE * TileValue.MAP_LEN);
			break;			
		}
	}

	// Ÿ��, Player, Monster�� �׷��ش�
	private void drawObject(Graphics g, Tile tile) {

		Graphics2D g2 = (Graphics2D)g;
		
		int value = tile.value;
		int x = tile.x;
		int y = tile.y;

		// ��ǥ�� ���� ������Ʈ ��ġ ����
		float locX = TileValue.MAP_OFFSET_X + (x * TileValue.TILE_SIZE) + tile.moveOffsetX;
		float locY = TileValue.MAP_OFFSET_Y + (y * TileValue.TILE_SIZE) + tile.moveOffsetY;

		// Ÿ�Ͽ� �׷��� ������ string �����̴�. Ÿ���� value�� ���ڸ� ���Ŀ� ���� ���� ũ��� ��ġ�� �������ش�.
		final int fontSize = value < 100 ? 42 : value < 1000 ? 32 : 28; // Ÿ�� value�� ���� ��Ʈ ũ�� ����
		final Font font = new Font(TileValue.FONT_NAME, Font.BOLD, fontSize);
		final FontMetrics fm = getFontMetrics(font);

		// Ÿ�� value�� ���� string ��ġ ����. w,h�� ���� x,y offset�̴�.
		String s = Integer.toString(value);
		final int w = fm.stringWidth(s);
		final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];

		// draw <PLAYER>
		if (value == TileValue.PLAYER) { 
			g2.drawImage(imgPlayer.getImage(), (int)locX, (int)locY, this);
			g2.finalize();
		}
		// draw <MONSTER>
		else if (value == TileValue.MONSTER) {
			g2.drawImage(tile.getImg(), (int)locX, (int)locY, this);
		    g2.finalize();
		}
		// draw <TILE>
		else {
			// draw <Tile> background
			g2.setStroke(new BasicStroke(3));
			g2.setColor(tile.getBackgroundColor());
			g2.fillRoundRect((int)locX + 2, (int)locY + 2, TileValue.TILE_SIZE - 4, TileValue.TILE_SIZE - 4, 5, 5);
			if(tile.bNew) { // ���� ������ Ÿ���̸� ��Ȳ�� �׵α� �׷���
				g2.setStroke(new BasicStroke(4));
				g2.setColor(Color.orange);
			} else
				g2.setColor(Color.white);
			// Ÿ���� �׵θ� draw
			g2.drawRoundRect((int)locX + 2, (int)locY + 2, TileValue.TILE_SIZE - 4, TileValue.TILE_SIZE - 4, 5, 5);
			
			// string setting
			g2.setFont(font);
			g2.setColor(Color.black);

			locX += (TileValue.TILE_SIZE - w) / 2;
			locY += TileValue.TILE_SIZE - (TileValue.TILE_SIZE - h) / 2 - 2;
			
			// draw <Tile> string
			g2.drawString(s, (int)locX, (int)locY);
		}
	}
		
	// �Է¹��� type�� ���� Ÿ��, Player, Monster �� �ϳ��� add�Ѵ�.
	private void addObject(int type) {
		int x, y, rand;
		boolean bSpaced = false; // ���� �ʿ� �� ������ �ִ��� Ȯ���ϴ� ����
		
		// ���� �ʿ� �� ������ �ִ��� Ȯ��. ���� �ʿ� �ϳ��� value ���� 0�̸� �� ������ �ִ�. 
		for (y = 0; y < TileValue.MAP_LEN; y++) {
			for (x = 0; x < TileValue.MAP_LEN; x++) {
				if (myTile[y][x].value == 0)
					bSpaced = true;
			}
		}
		
		// �� ������ ������ return
		if (!bSpaced)
			return;
		
		do { // ���������� ���� ��ġ ���� (0,0) ~ (5,5)
			x = (int)(Math.random() * TileValue.MAP_LEN);
			y = (int)(Math.random() * TileValue.MAP_LEN);
		} while (myTile[y][x].value != 0);

		// �Էµ� type�� ���� value�� ����
		switch(type) {
		case TileValue.TILE:
			rand = (int)(Math.random() * 5);
			if (rand == 0)
				myTile[y][x].setValue(4);
			else
				myTile[y][x].setValue(2);
			myTile[y][x].bNew = true;
			break;
		case TileValue.PLAYER:
			myTile[y][x].setValue(TileValue.PLAYER); // 100
			break;
		case TileValue.MONSTER:
			myTile[y][x].setValue(TileValue.MONSTER); // 200
			myTile[y][x].setImg();
			break;
		}
		repaint();
	}
	
	// �� ��ü ������Ʈ�� �������� �̵�, �����¿� � ���� �Է¹޾Ƶ� �������� �̵���Ų��.
	// �����¿� �̵� �Լ��� ���� �������� �ʱ� �����̸�, angle�� �Է¹޾� ��ü ���� ȸ������ �������� �̵���Ų �� ������� ȸ���ϴ� ����̴�.
	// ex) ���������� �̵���, 180 angle�� �Է¹޾� Ÿ�� ���� 180�� ȸ���ϰ� �������� �̵��� �ٽ� ������� 180�� ȸ���Ѵ�.
	protected void moveLeft(int angle) {

		boolean bObjMoved = false; // Ÿ���� �� ���̶� �̵������� true, �ִϸ����� ���� üũ �� ����
		bMovingNow = true; // �̵��� ���۵ƴٰ� �˸�. �̵��� ����������(bMovingNow�� false�� �� ������) �ٸ� �Է��� ���� ���Ѵ�.
		
		// �������� �̵��� �� �ֵ��� angle ��ŭ ���� �� ��ü�� ȸ��, �����̵��� �� ����(angle == 0)
		if(angle != 0)
			myTile = rotate(myTile, angle);
		
		// �̵���Ų �� ������ ������ movedTiles �ʱ�ȭ
		movedTiles = new Tile[TileValue.MAP_LEN][TileValue.MAP_LEN];
		for (int y = 0; y < TileValue.MAP_LEN; y++) {
			for (int x = 0; x < TileValue.MAP_LEN; x++) {
				movedTiles[y][x] = new Tile(0, x, y);
			}
		}

		// ��� ��Ҹ� ���鼭 �������� �̵�
		for (int y = 0; y < TileValue.MAP_LEN; y++) {
			for (int x = 0; x < TileValue.MAP_LEN; x++) {
				int value = myTile[y][x].value;
				if (value != 0) { // if value�� 0�� �ƴϸ�, �� �� ��ġ�� object�� �����ϸ� �̵�
					int desX = x; // x�� ������ġ desX�� �̵� �� ��ġ, ó���� x�� �ʱ�ȭ
					
					while (desX > 0) { // desX�� ���� ���� ���� ������ �ݺ��Ѵ�.
						// �ڽ��� ���� ������Ʈ�� �̵����� �ʴ´�. �ݺ��� Ż��
						if (value == TileValue.MONSTER)
							break;						
						
						// ���� ���� 0�� �� : �� ĭ move
						else if (movedTiles[y][desX - 1].value == 0) {
							desX--;
						}
						// player�� monster�� �浹 : monster óġ
						else if (value == TileValue.PLAYER
								 && movedTiles[y][desX - 1].value == TileValue.MONSTER) {
							nSkillPnt += movedTiles[y][desX - 1].skillPnt; // ���� ������ ���� ��ų ����Ʈ ����
							lblSkillPnt.setText("��ų����Ʈ : " + nSkillPnt); // ��ų ������ label ������Ʈ
							movedTiles[y][desX - 1].setValue(0); // Monster ������Ʈ�� value�� 0�� set, �����Ѵ�.
							desX--;
						}						
						// ���� ���� ���� ������ �� : merge
						else if (movedTiles[y][desX - 1].value == myTile[y][x].value
								 && movedTiles[y][desX - 1].bCanChanged) { // �ڽŰ� ���� ������Ʈ�� ���� value�̰� && ���� ������Ʈ�� �̹� �̵����� �� ���� ������ �ʾҴٸ�
							// value �� �ι�� ����
							value *= 2;
							// ���� ����
							scoreUp(value);
							// �� �� merge�� ������ Ÿ���� �� �̻� change���� ���� 
							movedTiles[y][desX - 1].bCanChanged = false;
							desX--;
							
							break;
						}
						// �� ���� ��� == ������ �� : stop
						else {
							break;
						}
					}
					// �̵��� ������ ���� movedTiled ���� ����
					movedTiles[y][desX].setValue(value); // ����� value�� ������Ʈ
					if (value == TileValue.MONSTER) // ���Ϳ��ٸ� skillPnt�� movedTiles�� ����
						movedTiles[y][desX].skillPnt = myTile[y][x].skillPnt;
					if (x != desX) { // if x�� desX�� �ٸ��ٸ�, �� Ÿ���� �� ���̶� �̵��ߴٸ�
						myTile[y][x].bMoved = true; // �̵� �� Ÿ���̶�� ����(bMoved), �Ŀ� �ִϸ����ÿ� Ȱ��
						myTile[y][x].desX = desX; // desX �� ������Ʈ
						bObjMoved = true; // Ÿ���� �̵��ߴٰ� �˸�
					}
				}
			}
		}
		
		// ���� ���� ������� ȸ��
		if (angle != 0) {
			myTile = rotate(myTile, 360 - angle);
			movedTiles = rotate(movedTiles, 360 - angle);
		}
		
		// ���� myTile���� desX desY�� ���� ����ǰ�.
		// movedTiles���� �̵� �� ���� ���� ����Ǿ���.
		
		// �� ���̶� ������ Ÿ���� �ִٸ� moveThread ����
		if (bObjMoved) {
			startMoveThread();
		}
		
		// ������ Ÿ���� ������ bMovingNow�� false�� �ٲٰ� �̵��� �����ٰ� �˸�
		else
			bMovingNow = false;
	}

	// �Է¹��� angle�� ���� ��ü ���� ���� ȸ�� ��Ű�� �Լ�.
	// �����¿� �̵��� ��� ���� �Լ��� �����ϸ� �����ϹǷ�, �̵� �ø��� Tile �迭 ��ü�� �������� �̵��ϵ��� ȸ���Ѵ�.
	private Tile[][] rotate(Tile[][] arrTile, int angle) {
		
		Tile[][] newTiles = new Tile[TileValue.MAP_LEN][TileValue.MAP_LEN];
		int offsetX = TileValue.MAP_LEN - 1, offsetY = TileValue.MAP_LEN - 1; // ȸ���� ���� offset ���� 5,5�� �ʱ�ȭ
		
		// �Է� ���� �� �ִ� angle�� 90, 180, 270 �� �����̴�.
		if (angle == 90) {
			offsetX = 0;
		} else if (angle == 270) {
			offsetY = 0;
		}
		// else (angle == 180), offset == (5, 5)

		// angle�� ���� cos, sin�� �ʱ�ȭ
		double rad = Math.toRadians(angle);
		int cos = (int) Math.cos(rad);
		int sin = (int) Math.sin(rad);
		// offset�� cos, sin���� ���� ��ü Ÿ�� ���� ���� angle�� ���� ��ġ ����
		for (int y = 0; y < TileValue.MAP_LEN; y++) {
			for (int x = 0; x < TileValue.MAP_LEN; x++) {
				int newY = (y * cos) - (x * sin) + offsetY;
				int newX = (y * sin) + (x * cos) + offsetX;

				newTiles[newY][newX] = arrTile[y][x]; // Tile �迭�� ȸ������ newTiles�� ����
				
				// desX, desY ���� angle�� ���� ������ ��
				int desX = arrTile[y][x].desX;
				int desY = arrTile[y][x].desY;
				int newDesY = (desY * cos) - (desX * sin) + offsetY;
				int newDesX = (desY * sin) + (desX * cos) + offsetX;

				newTiles[newY][newX].x = newX;
				newTiles[newY][newX].y = newY; // x, y ��� ���� �� ����
				newTiles[newY][newX].desX = newDesX;
				newTiles[newY][newX].desY = newDesY; // desX, desY ��� ���� �� ����
			}
		}
		return newTiles;
	}
	
	// �� �̻� Ÿ�ϵ��� ������ �� �ִ��� üũ�ϴ� �Լ�
	private boolean canMove() {
		int angle = 0;
		boolean bMoved;
		
		// angle�� 90���� ������ ������ �� 4�� �ݺ��ϸ� ȸ���Ѵ�.
		while (angle <= 270) {
			bMoved = dummyMoveLeft(angle); // angle�� ���� Ÿ�� �̵��� �õ��Ѵ�. �� ���̶� �̵��ϸ� true ��ȯ
			if (bMoved)
				return true;
			
			angle += 90;
		}

		return false; // �����¿� �̵����� �� ���� �̵��� �����ٸ� false�� ��ȯ�� �̵��� �Ұ��ϴٰ� �˸�
	}
	
	// �����¿� �̵� �ִϸ��̼��� ���� �ڿ�, �� �̻� ������ �� �ִ��� üũ�ϱ� ���� ��ü Ÿ�� ���� �����¿�� �ѹ��� �̵��� �õ��Ѵ�. ��
	// ���� �̵��� �����ϴ� �Լ�	
	private boolean dummyMoveLeft(int angle) {

		// �̵� ������ Ȯ������ dummyTiles�� dummyMovedTiles ���� �� �ʱ�ȭ
		Tile[][] dummyTiles = new Tile[TileValue.MAP_LEN][TileValue.MAP_LEN];
		Tile[][] dummyMovedTiles = new Tile[TileValue.MAP_LEN][TileValue.MAP_LEN];
		for (int y = 0; y < TileValue.MAP_LEN; y++) {
			for (int x = 0; x < TileValue.MAP_LEN; x++) {
				dummyTiles[y][x] = new Tile(0, x, y);
				dummyTiles[y][x].setValue(myTile[y][x].value);
				dummyMovedTiles[y][x] = new Tile(0, x, y);
			}
		}

		// �������� �̵��� �� �ֵ��� angle ��ŭ ���� �� ��ü�� ȸ��, �����̵��� �� ����(angle == 0)
		if (angle != 0)
			dummyTiles = rotate(dummyTiles, angle);

		// ��� ��Ҹ� ���鼭 �������� �̵�
		for (int y = 0; y < TileValue.MAP_LEN; y++) {
			for (int x = 0; x < TileValue.MAP_LEN; x++) {
				int value = dummyTiles[y][x].value;
				
				/* �� �κ��� �⺻ logic�� moveLeft() �޼ҵ�� ���� */
				if (value != 0) {
					int desX = x;

					while (desX > 0) {
						if (value == TileValue.MONSTER)
							break;

						else if (dummyMovedTiles[y][desX - 1].value == 0) {
							desX--;
						}
						else if (value == TileValue.PLAYER && dummyMovedTiles[y][desX - 1].value == TileValue.MONSTER) {
							dummyMovedTiles[y][desX - 1].setValue(0);
							desX--;
						}
						else if (dummyMovedTiles[y][desX - 1].value == dummyTiles[y][x].value
								&& dummyMovedTiles[y][desX - 1].bCanChanged) {
							dummyMovedTiles[y][desX - 1].bCanChanged = false;
							desX--;

							break;
						}
						else {
							break;
						}
					}
					dummyMovedTiles[y][desX].setValue(value);
					
					 // �� ���̶� �̵��ߴٸ� true�� ��ȯ�� ���� ���� �̵� �����ϴٰ� �˸�
					if (x != desX) {
						return true;
					}
				}
			}
		}
		return false; // �� ���� �̵����� �ʾҴٸ� false ��ȯ
	}
	
	private void scoreUp(int value) {
		nLiveScr += value; // �޾ƿ� value ��ŭ ���� ����
		lblLiveScr2.setText(Integer.toString(nLiveScr)); // ���� label ������Ʈ
		
		// ���� ������ �ִ� ������ �Ѿ���� ������Ʈ
		if (nLiveScr > nMaxScr) {
			nMaxScr = nLiveScr;
		
			// �ִ� ���� ���� ����� ����, text ���Ͽ� ����
			try {
				fileout = new FileWriter("./data_file.txt");//���� ���
			
				MaxScore = Integer.toString(nMaxScr);
				
				fileout.write(MaxScore);	
				fileout.close();
			}
			
			catch(IOException e){
				e.printStackTrace();
			}
			lblMaxScr.setText("�ְ���: " + nMaxScr);
		}
	}

	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			
			// Ÿ���� �̵� ���̶�� ��ư �Է� ����
			if (bMovingNow)
				return;
			
			Object obj = event.getSource();
			
			/* �ɼ� ��ư�� */
			if (obj == btnReset) { // ���� ��ư �Է� �� ���� �˾� setVisible(true)
				reset.setVisible(true);
			}
			else if (obj == btnMain) { // ���� ��ư �Է� �� ���� �˾� setVisible(true)
				main.setVisible(true);
			}
			
			/* ����Ű �Է� */
			else if (obj == btnLeft) {
				moveLeft(0);					
			}
			else if (obj == btnRight) {
				moveLeft(180);
			}
			else if (obj == btnUp) {
				moveLeft(90);
			}
			else if (obj == btnDown) {
				moveLeft(270);
			}
		}
	}
	
	// ��ų �ߵ� ���� ������ ȭ�鿡 �����ִ� �޼ҵ�
	public void previewSkill(MouseEvent e) {
		int x, y;

		// ���� ���� ���۵Ǵ� ���� 0,0 �������� ���� ���콺 ��ġ�� x, y�� ����
		x = (e.getPoint().x - TileValue.MAP_OFFSET_X - 5);
		y = (e.getPoint().y - TileValue.MAP_OFFSET_Y - 26);
			
		// ���콺 ��ġ�� ���� Ÿ�� �� ��ǥ (0,0)~(5,5)�� x, y�� ����. (0,0)~(5,5)�� ������ ����� ���̿��� �ϴ� �����Ѵ�.
		if (x < 0)	x = -1;
		else		x = x / TileValue.TILE_SIZE;
		
		if (y < 0)	y = -1;
		else		y = y / TileValue.TILE_SIZE;
		
		// ���콺�� ���� �� ���� ��ġ�ϰ�(0,0 ~ 5,5) && ���������� ���콺�� ��ġ�� ���� �� ��ǥ�� ����Ǿ��ٸ�
		if (0 <= x && x <= TileValue.MAP_LEN - 1 && 0 <= y && y <= TileValue.MAP_LEN - 1) {			
			bShowSkill = true;
			lastCurX = x;
			lastCurY = y;			
			repaint();
		} else {
			bShowSkill = false;
			repaint();
		}
	}
		
	public void skill(MouseEvent e) {
		int x, y;
		
		// ���� ���� ���۵Ǵ� ���� 0,0 �������� ���� ���콺 ��ġ�� x, y�� ����
		x = (e.getPoint().x - TileValue.MAP_OFFSET_X - 5);
		y = (e.getPoint().y - TileValue.MAP_OFFSET_Y - 26);
		
		// ���콺 ��ġ�� ���� Ÿ�� �� ��ǥ (0,0)~(5,5)�� x, y�� ����. (0,0)~(5,5)�� ������ ����� ���̿��� �ϴ� �����Ѵ�.
		if (x < 0)	x = -1;
		else		x = x / TileValue.TILE_SIZE;
		
		if (y < 0)	y = -1;
		else		y = y / TileValue.TILE_SIZE;
		
		// ���콺�� ���� �� ���� ��ġ�Ѵٸ�(0,0 ~ 5,5)
		if (0 <= x && x <= TileValue.MAP_LEN - 1 && 0 <= y && y <= TileValue.MAP_LEN - 1) {
			switch (nSkill) {
			case 1:
				if (myTile[y][x].type == TileValue.TILE) { // ������ ��ġ�� Ÿ���� �ִٸ� ������Ʈ�� �ʱ�ȭ
					myTile[y][x] = new Tile(0, x, y);
				} else { // ��ġ�� Ÿ���� ���ٸ� �ߵ����� ����
					return;
				}
				nSkillPnt -= TileValue.SKILL1;
				break;
			case 2:
				// Ÿ�� ���� ��Ҹ� ���� Ÿ���� �ִ��� cnt�� üũ
				int cnt = 0;
				for (int i = 0; i < TileValue.MAP_LEN; i++) {
					if (myTile[y][i].type == TileValue.TILE) cnt++;
				}
				// cnt�� �ϳ��� �ִٸ� ��ų�� �ߵ��Ѵ�
				if (cnt > 0) {
					for (int i = 0; i < TileValue.MAP_LEN; i++) {
						if (myTile[y][i].type == TileValue.TILE)
							myTile[y][i] = new Tile(0, x, y);
					}
				} else {
					return;
				}
				nSkillPnt -= TileValue.SKILL23;
				break;
			case 3:
				// Ÿ�� ���� ��Ҹ� ���� Ÿ���� �ִ��� cnt�� üũ
				cnt = 0;
				for (int i = 0; i < TileValue.MAP_LEN; i++) {
					if (myTile[i][x].type == TileValue.TILE) cnt++;
				}
				// cnt�� �ϳ��� �ִٸ� ��ų�� �ߵ��Ѵ�
				if (cnt > 0) {
					for (int i = 0; i < TileValue.MAP_LEN; i++) {
						if (myTile[i][x].type == TileValue.TILE)
							myTile[i][x] = new Tile(0, x, y);
					}
				} else {
					return;
				}				
				nSkillPnt -= TileValue.SKILL23;
				break;
			case 4:
				// Ÿ�� ���� ��Ҹ� ���� Ÿ���� �ִ��� cnt�� üũ
				cnt = 0;
				for (int i = 0; i < TileValue.MAP_LEN; i++) {
					if (myTile[y][i].type == TileValue.TILE) cnt++;
				}
				for (int i = 0; i < TileValue.MAP_LEN; i++) {
					if (myTile[i][x].type == TileValue.TILE) cnt++;
				}
				// cnt�� �ϳ��� �ִٸ� ��ų�� �ߵ��Ѵ�
				if (cnt > 0) {
					for (int i = 0; i < TileValue.MAP_LEN; i++) {
						if (myTile[y][i].type == TileValue.TILE)
							myTile[y][i] = new Tile(0, x, y);
					}
					for (int i = 0; i < TileValue.MAP_LEN; i++) {
						if (myTile[i][x].type == TileValue.TILE)
							myTile[i][x] = new Tile(0, x, y);
					}
				} else {
					return;
				}				
				nSkillPnt -= TileValue.SKILL4;
				break;
			}
			nSkill = 0;
			lblSkillPnt.setText("��ų����Ʈ : " + nSkillPnt);
			repaint();
		}
	}
} //GamePage class
