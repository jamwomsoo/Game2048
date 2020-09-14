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

	protected Tile[][] myTile; // 오브젝트 정보들을 저장할 2차원 배열
	protected Tile[][] movedTiles; // 오브젝트 이동 알고리즘에 쓰일 2차원 배열, 이동이 완료된 상태의 정보를 저장한다. 이동 애니메이션이 끝나면 myTile을 movedTiles로 업데이트 해준다.

	protected boolean bMovingNow; // 평소에는 false, 오브젝트 이동중엔 true, 현재 이동 애니메이션이 실행 중인지 체크
	protected boolean bShowSkill; // skill 사용 지역을 표시해 줄지 확인해주는 변수
	
	protected Thread 			moveThread;	// 이동 애니메이션을 실행 할 Thread 변수
	protected ButtonListener 	buttonL; // 상하좌우 이동 버튼, 리셋 버튼, 메인 버튼을 관리 할 Listener 변수
	protected JLabel 			lblMaxScr, lblLiveScr, lblLiveScr2, lblSkillPnt, lblSkill1, lblSkill2, lblSkill3, lblSkill4; // 상태를 띄어 줄 label 변수들
	protected JButton 			btnMain, btnReset, btnUp, btnDown, btnRight, btnLeft; // button 변수들
	protected ImageIcon 		imgBack, imgPlayer; // Background 이미지, Player 이미지, Monster 이미지
	protected int 				nMaxScr, nLiveScr; // 스코어, 스킬 포인트 등의 값을 저장할 int형 변수들
	
	protected int				lastCurX, lastCurY; // 스킬 예상 지점을 보여줄 때 쓰임, 마지막으로 마우스가 위치했던 좌표 위치(0,0 ~ 5,5)를 저장

	protected int			nSkillPnt, nSkill;
	protected JButton		btnSkill1, btnSkill2, btnSkill3, btnSkill4;
	
	// 최대 점수를 저장할 파일 입출력에 쓰일 변수들
	private String		MaxScore, max;//입출력을 받아오기 위한 변수 선언
	private int 		cnt;//
	private FileReader	filein;
	private FileWriter	fileout;
	
	// 팝업 창 클래스 선언. 각각 리셋, 메인, 게임오버를 물어보는 팝업
	protected Popup reset, main, overpopup;
	
	public GamePage() {
		setLayout(null);
		
		// 오브젝트 정보를 저장할 2차원 배열 myTile 생성 및 초기화
		myTile = new Tile[TileValue.MAP_LEN][TileValue.MAP_LEN];
		movedTiles = new Tile[TileValue.MAP_LEN][TileValue.MAP_LEN];
		for (int y = 0; y < TileValue.MAP_LEN; y++) {
			for (int x = 0; x < TileValue.MAP_LEN; x++) {
				myTile[y][x] = new Tile(0, x, y);
			}
		}
		
		// 이동 애니메이션 스레드 null로 초기화
		moveThread = null;

		// ButonListener 생성
		buttonL = new ButtonListener();
		
		// 파일 입출력으로 최대 점수를 받아옴 
		max = "";//처음 max를 빈칸으로 초기화
		cnt = 0;//cnt는 0으로 초기화
		fileout = null;//파일 아웃을 널로 초기화
		
		try {
			filein = new FileReader("./data_file.txt"); // 파일 상대 경로
			 
			 while((cnt = filein.read())!= -1) {//-1이 나올때 까지 파일의 내용을 읽어준다
				 if(cnt == -1) break;//만약 -1이 나오면 중지해주고 그전까지만 출력
				 char readWord = (char) cnt;//readWord에 입력받은 cnt의 값을 char로 형 변환
				 max += readWord;//스트링인 max에 받아온 readWord를 넣어준다
			 }

			 filein.close();//파일 입력 종료
			}
		catch(IOException e){
			e.printStackTrace();//예외처리
		}
		
		nMaxScr = Integer.parseInt(max); //파일 안에 아무 데이터도 없으면 초기화 에러
		
		// 변수들 초기화
		nLiveScr = 0;
		nSkillPnt = 0;
		nSkill = 0;
		
		lastCurX = 0;
		lastCurY = 0;
		
		bMovingNow = false;
		bShowSkill = false;

		// 팝업 창 생성 후 setVisible을 false해준다.
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
		btnReset.setBorderPainted(false); //외곽선 없애기
		btnReset.setContentAreaFilled(false); //채우기 없음
		btnReset.setFocusPainted(false); //선택될 때 글씨 테두리 없앰
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
	
		lblMaxScr = new JLabel("최고점: " + nMaxScr);
		lblMaxScr.setFont(new Font("돋움",Font.BOLD,23));
		lblMaxScr.setBounds(50,25,200,25);
		lblMaxScr.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblMaxScr);
		
		lblLiveScr = new JLabel("현재점수");
		lblLiveScr.setFont(new Font("돋움",Font.BOLD,28));
		lblLiveScr.setBounds(50,75,200,35);
		lblLiveScr.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblLiveScr);
		
		lblLiveScr2 = new JLabel(Integer.toString(nLiveScr));
		lblLiveScr2.setFont(new Font("돋움",Font.BOLD,52));
		lblLiveScr2.setBounds(50,100,200,80);
		lblLiveScr2.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblLiveScr2);
		
		lblSkillPnt = new JLabel("스킬포인트 : " + nSkillPnt);
		lblSkillPnt.setFont(new Font("돋움",Font.BOLD,26));
		lblSkillPnt.setBounds(280,140,225,35);
		add(lblSkillPnt);
		
		lblSkill1 = new JLabel("sp: " + TileValue.SKILL1);
		lblSkill1.setFont(new Font("돋움", Font.BOLD, 16));
		lblSkill1.setForeground(Color.white);
		lblSkill1.setBounds(20, 726, 80, 15);
		lblSkill1.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblSkill1);
		
		lblSkill2 = new JLabel("sp: " + TileValue.SKILL23);
		lblSkill2.setFont(new Font("돋움", Font.BOLD, 16));
		lblSkill2.setForeground(Color.white);
		lblSkill2.setBounds(110, 726, 80, 15);
		lblSkill2.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblSkill2);
		
		lblSkill3 = new JLabel("sp: " + TileValue.SKILL23);
		lblSkill3.setFont(new Font("돋움", Font.BOLD, 16));
		lblSkill3.setForeground(Color.white);
		lblSkill3.setBounds(200, 726, 80, 15);
		lblSkill3.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblSkill3);
		
		lblSkill4 = new JLabel("sp: " + TileValue.SKILL4);
		lblSkill4.setFont(new Font("돋움", Font.BOLD, 16));
		lblSkill4.setForeground(Color.white);
		lblSkill4.setBounds(290, 726, 80, 15);
		lblSkill4.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblSkill4);
		
		btnSkill1 = new JButton(new ImageIcon("Image/skillButton1.png"));
		btnSkill1.setFont(new Font("돋움",Font.BOLD,22));
		btnSkill1.setHorizontalAlignment(SwingConstants.CENTER);
		btnSkill1.setBounds(20,642,78,78);
		btnSkill1.setBorderPainted(false);
		btnSkill1.setContentAreaFilled(false);
		btnSkill1.setFocusPainted(false);
		btnSkill1.setPressedIcon(new ImageIcon("Image/clickedSkillButton1.png"));
		add(btnSkill1);
		
		//가로
		btnSkill2=new JButton(new ImageIcon("Image/skillButton2.png"));
		btnSkill2.setFont(new Font("돋움",Font.BOLD,22));
		btnSkill2.setHorizontalAlignment(SwingConstants.CENTER);
		btnSkill2.setBounds(110,642,78,78);
		btnSkill2.setBorderPainted(false);
		btnSkill2.setContentAreaFilled(false);
		btnSkill2.setFocusPainted(false);
		btnSkill2.setPressedIcon(new ImageIcon("Image/clickedSkillButton2.png"));
		add(btnSkill2);
		
		//세로
		btnSkill3 = new JButton(new ImageIcon("Image/skillButton3.png"));
		btnSkill3.setFont(new Font("돋움",Font.BOLD,22));
		btnSkill3.setHorizontalAlignment(SwingConstants.CENTER);
		btnSkill3.setBounds(200,642,78,78);
		btnSkill3.setBorderPainted(false);
		btnSkill3.setContentAreaFilled(false);
		btnSkill3.setFocusPainted(false);
		btnSkill3.setPressedIcon(new ImageIcon("Image/clickedSkillButton3.png"));
		add(btnSkill3);
		
		btnSkill4 = new JButton(new ImageIcon("Image/skillButton4.png"));
		btnSkill4.setFont(new Font("돋움",Font.BOLD,22));
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
		
		// 게임 시작 시 Tile 2개와 Player 맵에 add
		addObject(TileValue.TILE);
		addObject(TileValue.TILE);
		addObject(TileValue.PLAYER);
	}
	
	public void addPageChangeListener(ActionListener listener) {
		// 팝업창의 Button Listener들 add
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

	// 게임을 리셋해주는 메소드
	public void reset() {
		
		// myTile[6][6] 등 온갖 변수들 초기화
		myTile = new Tile[TileValue.MAP_LEN][TileValue.MAP_LEN];
		movedTiles = new Tile[TileValue.MAP_LEN][TileValue.MAP_LEN];
		for (int y = 0; y < TileValue.MAP_LEN; y++) {
			for (int x = 0; x < TileValue.MAP_LEN; x++) {
				myTile[y][x] = new Tile(0, x, y);
			}
		}		
		moveThread = null;
		//파일 출력을 위한 초기화
		max = "";
		cnt = 0;
		fileout = null;
		
		// txt파일에서 최고점수를 파일입출력으로 불러옴
		try {
			filein = new FileReader("./data_file.txt"); // 파일 경로
			 
			 while((cnt = filein.read())!= -1) {//-1이 나오기전까지 cnt에 불러온 값을 넣어줌
				 if(cnt == -1) break;//만약 -1이 나오면 중지해주고 그 전까지만 출력
				 char readWord = (char) cnt;//char로 형 변환
				 max += readWord;//입력받은 char형의 값들을 string인 타입 max에 넣어준다
			 }

			 filein.close();//파일 불러오기 중지
			}
		catch(IOException e){
			e.printStackTrace();//예외처리 를 위한 작업
		}
		nMaxScr = Integer.parseInt(max); //파일 안에 아무 데이터도 없으면 초기화 에러
		
		nLiveScr = 0;
		nSkillPnt = 0;
		nSkill = 0;
		
		lastCurX = 0;
		lastCurY = 0;
		
		bMovingNow = false;
		bShowSkill = false;
		
		lblMaxScr.setText("최고점: " + nMaxScr);
		lblLiveScr2.setText(Integer.toString(nLiveScr));
		lblSkillPnt.setText("스킬포인트 : " + nSkillPnt);
		
		addObject(TileValue.TILE);
		addObject(TileValue.TILE);
		addObject(TileValue.PLAYER);
		
		repaint();
	}
	
	// 이동 애니메이션을 실행하는 thread의 run 함수
	public void run() {
		
		boolean bRepeat = true; // 반복 check 변수
		int nMovedTilesNum = 1, nCnt = 0; // 현재 움직이고 있는 오브젝트 개수를 저장. 이동하는 오브젝트 개수에 따른 이동 속도 보정을 위해서 쓰임
		float fMovedSpeed = 0.0003f; // 이동 속도
		
		while (bRepeat) {	
			bRepeat = false;
			
			for (int y = 0; y < TileValue.MAP_LEN; y++) {
				for (int x = 0; x < TileValue.MAP_LEN; x++) {
					if (myTile[y][x].bMoved) { // if 오브젝트가 이동해야한다면

						bRepeat = true; // 한 번이라도 이동했으면 계속 반복
						nCnt++; // 이동중인 타일 count

						Tile tile = myTile[y][x];
						
						// 오른쪽 이동
						if (tile.x < tile.desX) { // if 오브젝트의 desX가 x보다 크다면 오른쪽으로 이동해야 한다.
							tile.moveOffsetX = tile.moveOffsetX + fMovedSpeed * nMovedTilesNum; // 속도와 전체 이동 오브젝트 수에 따라서 moveOffset 변경
							if (tile.moveOffsetX > (tile.desX - tile.x) * TileValue.TILE_SIZE) { // if moveOffset이 이동해야 할 길이를 넘어섰다면
								tile.bMoved = false; // bMoved를  false로 바꿔서 이동이 끝났다고 알림
							}
						}
						// 왼쪽 이동
						else if (tile.x > tile.desX) {
							tile.moveOffsetX = tile.moveOffsetX - fMovedSpeed * nMovedTilesNum;
							if (tile.moveOffsetX < (tile.desX - tile.x) * TileValue.TILE_SIZE) {
								tile.bMoved = false;
							}
						}
						// 아래로 이동
						else if (tile.y < tile.desY) {
							tile.moveOffsetY = tile.moveOffsetY + fMovedSpeed * nMovedTilesNum;
							if (tile.moveOffsetY > (tile.desY - tile.y) * TileValue.TILE_SIZE) {
								tile.bMoved = false;
							}
						}
						// 위로 이동
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
			nMovedTilesNum = nCnt; // 이동 클럭마다 이동중인 타일 수 update
			nCnt = 0;
		}
		
		// 이동 후 정보를 저장하고 있는 movedTiles을 myTile에 대입
		for(int y = 0; y < TileValue.MAP_LEN; y++) {
			for(int x = 0; x < TileValue.MAP_LEN; x++) {
				myTile[y][x].setData(movedTiles[y][x]);
			}
		}
		movedTiles = null;
		
		// Tile 1개 추가, 1/3 확률로 2개 추가
		addObject(TileValue.TILE);
		if ((int)(Math.random() * 2) == 0)
			addObject(TileValue.TILE);
		
		// 20 퍼센트 확률로 몬스터 추가
		if ((int)(Math.random() * 5) == 0)
			addObject(TileValue.MONSTER);
		
		// 이동 후 더 이상 움직일 수 있는지 체크해주는 canMove() 메소드 실행, 움직일 수 없다면 gameOver 팝업 드러남
		if(!canMove()) {
			overpopup.finalscore.setText("최종 스코어: " + nLiveScr);
			overpopup.setVisible(true);
		}
		
		repaint();
		
		bMovingNow = false; // 이동이 끝났다고 알림. 이동 버튼 입력 가능
	}

	// 오브젝트 이동 애니메이션 실행 함수 Thread를 run() 해준다.
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

		// Tile 배열 전체를 돌며 value가 0이 아니면, draw
		for (int y = 0; y < TileValue.MAP_LEN; y++) {
			for (int x = 0; x < TileValue.MAP_LEN; x++) {
				if (myTile[y][x].value != 0)
					drawObject(page, myTile[y][x]);
			}
		}
		
		// 현재 선택된 스킬이 있으면서(nSkill != 0) && 마우스 커서의 위치가 맵 안에 있을 경우 (0 <= x <= 5 && 0 <= y <= 5)
		if (nSkill != 0 && bShowSkill) {
			drawSkillPreview(page);
		}
	}
	
	private void drawSkillPreview(Graphics g) {
		
		Graphics2D g2 = (Graphics2D)g;
		int locX, locY;
		
		// 굵기와 color 설정
		g2.setStroke(new BasicStroke(3));
		g2.setColor(Color.red);
		
		// 입력 된 스킬의 종류마다 그려지는 스킬 범위가 다르다.
		switch(nSkill) {
		case 1: // 마우스 위치의 1칸 그려짐
			locX = TileValue.MAP_OFFSET_X + (lastCurX * TileValue.TILE_SIZE);
			locY = TileValue.MAP_OFFSET_Y + (lastCurY * TileValue.TILE_SIZE);
			g2.drawRect(locX, locY, TileValue.TILE_SIZE, TileValue.TILE_SIZE);
			break;
		case 2: // 마우스 위치의 가로 6칸 그려짐
			locX = TileValue.MAP_OFFSET_X;
			locY = TileValue.MAP_OFFSET_Y + (lastCurY * TileValue.TILE_SIZE);
			g2.drawRect(locX, locY, TileValue.TILE_SIZE * TileValue.MAP_LEN, TileValue.TILE_SIZE);
			break;
		case 3: // 마우스 위치의 세로 6칸 그려짐
			locX = TileValue.MAP_OFFSET_X + (lastCurX * TileValue.TILE_SIZE);
			locY = TileValue.MAP_OFFSET_Y;
			g2.drawRect(locX, locY, TileValue.TILE_SIZE, TileValue.TILE_SIZE * TileValue.MAP_LEN);
			break;
		case 4: // 마우스 위치의 가로 6칸, 세로 6칸 그려짐
			locX = TileValue.MAP_OFFSET_X;
			locY = TileValue.MAP_OFFSET_Y + (lastCurY * TileValue.TILE_SIZE);
			g2.drawRect(locX, locY, TileValue.TILE_SIZE * TileValue.MAP_LEN, TileValue.TILE_SIZE);
			locX = TileValue.MAP_OFFSET_X + (lastCurX * TileValue.TILE_SIZE);
			locY = TileValue.MAP_OFFSET_Y;
			g2.drawRect(locX, locY, TileValue.TILE_SIZE, TileValue.TILE_SIZE * TileValue.MAP_LEN);
			break;			
		}
	}

	// 타일, Player, Monster를 그려준다
	private void drawObject(Graphics g, Tile tile) {

		Graphics2D g2 = (Graphics2D)g;
		
		int value = tile.value;
		int x = tile.x;
		int y = tile.y;

		// 좌표에 따른 오브젝트 위치 설정
		float locX = TileValue.MAP_OFFSET_X + (x * TileValue.TILE_SIZE) + tile.moveOffsetX;
		float locY = TileValue.MAP_OFFSET_Y + (y * TileValue.TILE_SIZE) + tile.moveOffsetY;

		// 타일에 그려줄 숫자의 string 설정이다. 타일의 value가 몇자리 수냐에 따라서 글자 크기와 위치를 조정해준다.
		final int fontSize = value < 100 ? 42 : value < 1000 ? 32 : 28; // 타일 value에 따라 폰트 크기 설정
		final Font font = new Font(TileValue.FONT_NAME, Font.BOLD, fontSize);
		final FontMetrics fm = getFontMetrics(font);

		// 타일 value에 따라 string 위치 설정. w,h은 각각 x,y offset이다.
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
			if(tile.bNew) { // 새로 생성된 타일이면 주황색 테두기 그려줌
				g2.setStroke(new BasicStroke(4));
				g2.setColor(Color.orange);
			} else
				g2.setColor(Color.white);
			// 타일의 테두리 draw
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
		
	// 입력받은 type에 따라 타일, Player, Monster 중 하나를 add한다.
	private void addObject(int type) {
		int x, y, rand;
		boolean bSpaced = false; // 게임 맵에 빈 공간에 있는지 확인하는 변수
		
		// 게임 맵에 빈 공간이 있는지 확인. 게임 맵에 하나라도 value 값이 0이면 빈 공간이 있다. 
		for (y = 0; y < TileValue.MAP_LEN; y++) {
			for (x = 0; x < TileValue.MAP_LEN; x++) {
				if (myTile[y][x].value == 0)
					bSpaced = true;
			}
		}
		
		// 빈 공간이 없으면 return
		if (!bSpaced)
			return;
		
		do { // 오브젝스의 랜덤 위치 생성 (0,0) ~ (5,5)
			x = (int)(Math.random() * TileValue.MAP_LEN);
			y = (int)(Math.random() * TileValue.MAP_LEN);
		} while (myTile[y][x].value != 0);

		// 입력된 type에 따라 value값 설정
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
	
	// 맵 전체 오브젝트를 왼쪽으로 이동, 상하좌우 어떤 값을 입력받아도 왼쪽으로 이동시킨다.
	// 상하좌우 이동 함수를 따로 구현하지 않기 위함이며, angle을 입력받아 전체 맵을 회전시켜 왼쪽으로 이동시킨 후 원래대로 회전하는 방식이다.
	// ex) 오른쪽으로 이동시, 180 angle을 입력받아 타일 맵을 180도 회전하고 왼쪽으로 이동후 다시 원래대로 180도 회전한다.
	protected void moveLeft(int angle) {

		boolean bObjMoved = false; // 타일이 한 번이라도 이동했으면 true, 애니메이팅 실행 체크 때 사용됨
		bMovingNow = true; // 이동이 시작됐다고 알림. 이동이 끝날때까지(bMovingNow가 false가 될 때까지) 다른 입력을 받지 못한다.
		
		// 왼쪽으로 이동할 수 있도록 angle 만큼 게임 맵 전체를 회전, 왼쪽이동일 땐 제외(angle == 0)
		if(angle != 0)
			myTile = rotate(myTile, angle);
		
		// 이동시킨 후 정보를 저장할 movedTiles 초기화
		movedTiles = new Tile[TileValue.MAP_LEN][TileValue.MAP_LEN];
		for (int y = 0; y < TileValue.MAP_LEN; y++) {
			for (int x = 0; x < TileValue.MAP_LEN; x++) {
				movedTiles[y][x] = new Tile(0, x, y);
			}
		}

		// 모든 요소를 돌면서 왼쪽으로 이동
		for (int y = 0; y < TileValue.MAP_LEN; y++) {
			for (int x = 0; x < TileValue.MAP_LEN; x++) {
				int value = myTile[y][x].value;
				if (value != 0) { // if value가 0이 아니면, 즉 그 위치에 object가 존재하면 이동
					int desX = x; // x는 현재위치 desX는 이동 후 위치, 처음엔 x로 초기화
					
					while (desX > 0) { // desX가 왼쪽 벽에 붙을 때까지 반복한다.
						// 자신이 몬스터 오브젝트면 이동하지 않는다. 반복문 탈출
						if (value == TileValue.MONSTER)
							break;						
						
						// 왼쪽 값이 0일 때 : 한 칸 move
						else if (movedTiles[y][desX - 1].value == 0) {
							desX--;
						}
						// player가 monster와 충돌 : monster 처치
						else if (value == TileValue.PLAYER
								 && movedTiles[y][desX - 1].value == TileValue.MONSTER) {
							nSkillPnt += movedTiles[y][desX - 1].skillPnt; // 몬스터 종류에 따라 스킬 포인트 증가
							lblSkillPnt.setText("스킬포인트 : " + nSkillPnt); // 스킬 포인터 label 업데이트
							movedTiles[y][desX - 1].setValue(0); // Monster 오브젝트의 value를 0로 set, 삭제한다.
							desX--;
						}						
						// 왼쪽 값이 같은 숫자일 때 : merge
						else if (movedTiles[y][desX - 1].value == myTile[y][x].value
								 && movedTiles[y][desX - 1].bCanChanged) { // 자신과 왼쪽 오브젝트가 같은 value이고 && 왼쪽 오브젝트가 이번 이동에서 한 번도 변하지 않았다면
							// value 값 두배로 증가
							value *= 2;
							// 점수 갱신
							scoreUp(value);
							// 한 번 merge를 실행한 타일은 더 이상 change되지 않음 
							movedTiles[y][desX - 1].bCanChanged = false;
							desX--;
							
							break;
						}
						// 그 외의 경우 == 막혔을 때 : stop
						else {
							break;
						}
					}
					// 이동한 정보에 따라 movedTiled 정보 갱신
					movedTiles[y][desX].setValue(value); // 변경된 value로 업데이트
					if (value == TileValue.MONSTER) // 몬스터였다면 skillPnt도 movedTiles에 대입
						movedTiles[y][desX].skillPnt = myTile[y][x].skillPnt;
					if (x != desX) { // if x와 desX가 다르다면, 즉 타일이 한 번이라도 이동했다면
						myTile[y][x].bMoved = true; // 이동 된 타일이라고 설정(bMoved), 후에 애니메이팅에 활용
						myTile[y][x].desX = desX; // desX 값 업데이트
						bObjMoved = true; // 타일이 이동했다고 알림
					}
				}
			}
		}
		
		// 게임 맵을 원래대로 회전
		if (angle != 0) {
			myTile = rotate(myTile, 360 - angle);
			movedTiles = rotate(movedTiles, 360 - angle);
		}
		
		// 이제 myTile에는 desX desY의 값이 저장되고.
		// movedTiles에는 이동 된 후의 값이 저장되었다.
		
		// 한 번이라도 움직인 타일이 있다면 moveThread 실행
		if (bObjMoved) {
			startMoveThread();
		}
		
		// 움직인 타일이 없으면 bMovingNow를 false로 바꾸고 이동이 끝났다고 알림
		else
			bMovingNow = false;
	}

	// 입력받은 angle에 따라 전체 게임 맵을 회전 시키는 함수.
	// 상하좌우 이동을 모두 따로 함수로 구현하면 복잡하므로, 이동 시마다 Tile 배열 전체를 왼쪽으로 이동하도록 회전한다.
	private Tile[][] rotate(Tile[][] arrTile, int angle) {
		
		Tile[][] newTiles = new Tile[TileValue.MAP_LEN][TileValue.MAP_LEN];
		int offsetX = TileValue.MAP_LEN - 1, offsetY = TileValue.MAP_LEN - 1; // 회전에 쓰일 offset 값을 5,5로 초기화
		
		// 입력 받을 수 있는 angle은 90, 180, 270 세 가지이다.
		if (angle == 90) {
			offsetX = 0;
		} else if (angle == 270) {
			offsetY = 0;
		}
		// else (angle == 180), offset == (5, 5)

		// angle에 따른 cos, sin값 초기화
		double rad = Math.toRadians(angle);
		int cos = (int) Math.cos(rad);
		int sin = (int) Math.sin(rad);
		// offset과 cos, sin값에 따라서 전체 타일 맵을 돌며 angle에 따른 위치 변경
		for (int y = 0; y < TileValue.MAP_LEN; y++) {
			for (int x = 0; x < TileValue.MAP_LEN; x++) {
				int newY = (y * cos) - (x * sin) + offsetY;
				int newX = (y * sin) + (x * cos) + offsetX;

				newTiles[newY][newX] = arrTile[y][x]; // Tile 배열을 회전시켜 newTiles에 저장
				
				// desX, desY 값도 angle에 따라 변경해 줌
				int desX = arrTile[y][x].desX;
				int desY = arrTile[y][x].desY;
				int newDesY = (desY * cos) - (desX * sin) + offsetY;
				int newDesX = (desY * sin) + (desX * cos) + offsetX;

				newTiles[newY][newX].x = newX;
				newTiles[newY][newX].y = newY; // x, y 멤버 변수 값 변경
				newTiles[newY][newX].desX = newDesX;
				newTiles[newY][newX].desY = newDesY; // desX, desY 멤버 변수 값 변경
			}
		}
		return newTiles;
	}
	
	// 더 이상 타일들이 움직일 수 있는지 체크하는 함수
	private boolean canMove() {
		int angle = 0;
		boolean bMoved;
		
		// angle을 90도씩 옆으로 돌리며 총 4번 반복하며 회전한다.
		while (angle <= 270) {
			bMoved = dummyMoveLeft(angle); // angle에 따라 타일 이동을 시도한다. 한 번이라도 이동하면 true 반환
			if (bMoved)
				return true;
			
			angle += 90;
		}

		return false; // 상하좌우 이동에서 한 번의 이동도 없었다면 false를 반환해 이동이 불가하다고 알림
	}
	
	// 상하좌우 이동 애니메이션이 끝난 뒤에, 더 이상 움직일 수 있는지 체크하기 위해 전체 타일 맵을 상하좌우로 한번씩 이동을 시도한다. 그
	// 논리적 이동을 실행하는 함수	
	private boolean dummyMoveLeft(int angle) {

		// 이동 가능을 확인해줄 dummyTiles와 dummyMovedTiles 생성 및 초기화
		Tile[][] dummyTiles = new Tile[TileValue.MAP_LEN][TileValue.MAP_LEN];
		Tile[][] dummyMovedTiles = new Tile[TileValue.MAP_LEN][TileValue.MAP_LEN];
		for (int y = 0; y < TileValue.MAP_LEN; y++) {
			for (int x = 0; x < TileValue.MAP_LEN; x++) {
				dummyTiles[y][x] = new Tile(0, x, y);
				dummyTiles[y][x].setValue(myTile[y][x].value);
				dummyMovedTiles[y][x] = new Tile(0, x, y);
			}
		}

		// 왼쪽으로 이동할 수 있도록 angle 만큼 게임 맵 전체를 회전, 왼쪽이동일 땐 제외(angle == 0)
		if (angle != 0)
			dummyTiles = rotate(dummyTiles, angle);

		// 모든 요소를 돌면서 왼쪽으로 이동
		for (int y = 0; y < TileValue.MAP_LEN; y++) {
			for (int x = 0; x < TileValue.MAP_LEN; x++) {
				int value = dummyTiles[y][x].value;
				
				/* 이 부분의 기본 logic은 moveLeft() 메소드와 같음 */
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
					
					 // 한 번이라도 이동했다면 true를 반환해 게임 맵이 이동 가능하다고 알림
					if (x != desX) {
						return true;
					}
				}
			}
		}
		return false; // 한 번도 이동하지 않았다면 false 반환
	}
	
	private void scoreUp(int value) {
		nLiveScr += value; // 받아온 value 만큼 점수 증가
		lblLiveScr2.setText(Integer.toString(nLiveScr)); // 점수 label 업데이트
		
		// 현재 점수가 최대 점수를 넘어섰으면 업데이트
		if (nLiveScr > nMaxScr) {
			nMaxScr = nLiveScr;
		
			// 최대 점수 파일 입출력 실행, text 파일에 저장
			try {
				fileout = new FileWriter("./data_file.txt");//파일 경로
			
				MaxScore = Integer.toString(nMaxScr);
				
				fileout.write(MaxScore);	
				fileout.close();
			}
			
			catch(IOException e){
				e.printStackTrace();
			}
			lblMaxScr.setText("최고점: " + nMaxScr);
		}
	}

	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			
			// 타일이 이동 중이라면 버튼 입력 무시
			if (bMovingNow)
				return;
			
			Object obj = event.getSource();
			
			/* 옵션 버튼들 */
			if (obj == btnReset) { // 리셋 버튼 입력 시 리셋 팝업 setVisible(true)
				reset.setVisible(true);
			}
			else if (obj == btnMain) { // 메인 버튼 입력 시 메인 팝업 setVisible(true)
				main.setVisible(true);
			}
			
			/* 방향키 입력 */
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
	
	// 스킬 발동 예상 지점을 화면에 보여주는 메소드
	public void previewSkill(MouseEvent e) {
		int x, y;

		// 게임 맵이 시작되는 곳을 0,0 기준으로 현재 마우스 위치를 x, y에 저장
		x = (e.getPoint().x - TileValue.MAP_OFFSET_X - 5);
		y = (e.getPoint().y - TileValue.MAP_OFFSET_Y - 26);
			
		// 마우스 위치에 따라 타일 위 좌표 (0,0)~(5,5)를 x, y에 저장. (0,0)~(5,5)의 범위를 벗어나는 값이여도 일단 저장한다.
		if (x < 0)	x = -1;
		else		x = x / TileValue.TILE_SIZE;
		
		if (y < 0)	y = -1;
		else		y = y / TileValue.TILE_SIZE;
		
		// 마우스가 게임 맵 위에 위치하고(0,0 ~ 5,5) && 마지막으로 마우스가 위치한 게임 위 좌표가 변경되었다면
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
		
		// 게임 맵이 시작되는 곳을 0,0 기준으로 현재 마우스 위치를 x, y에 저장
		x = (e.getPoint().x - TileValue.MAP_OFFSET_X - 5);
		y = (e.getPoint().y - TileValue.MAP_OFFSET_Y - 26);
		
		// 마우스 위치에 따라 타일 위 좌표 (0,0)~(5,5)를 x, y에 저장. (0,0)~(5,5)의 범위를 벗어나는 값이여도 일단 저장한다.
		if (x < 0)	x = -1;
		else		x = x / TileValue.TILE_SIZE;
		
		if (y < 0)	y = -1;
		else		y = y / TileValue.TILE_SIZE;
		
		// 마우스가 게임 맵 위에 위치한다면(0,0 ~ 5,5)
		if (0 <= x && x <= TileValue.MAP_LEN - 1 && 0 <= y && y <= TileValue.MAP_LEN - 1) {
			switch (nSkill) {
			case 1:
				if (myTile[y][x].type == TileValue.TILE) { // 선택한 위치에 타일이 있다면 오브젝트를 초기화
					myTile[y][x] = new Tile(0, x, y);
				} else { // 위치에 타일이 없다면 발동하지 않음
					return;
				}
				nSkillPnt -= TileValue.SKILL1;
				break;
			case 2:
				// 타일 범위 요소를 돌며 타일이 있는지 cnt로 체크
				int cnt = 0;
				for (int i = 0; i < TileValue.MAP_LEN; i++) {
					if (myTile[y][i].type == TileValue.TILE) cnt++;
				}
				// cnt가 하나라도 있다면 스킬을 발동한다
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
				// 타일 범위 요소를 돌며 타일이 있는지 cnt로 체크
				cnt = 0;
				for (int i = 0; i < TileValue.MAP_LEN; i++) {
					if (myTile[i][x].type == TileValue.TILE) cnt++;
				}
				// cnt가 하나라도 있다면 스킬을 발동한다
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
				// 타일 범위 요소를 돌며 타일이 있는지 cnt로 체크
				cnt = 0;
				for (int i = 0; i < TileValue.MAP_LEN; i++) {
					if (myTile[y][i].type == TileValue.TILE) cnt++;
				}
				for (int i = 0; i < TileValue.MAP_LEN; i++) {
					if (myTile[i][x].type == TileValue.TILE) cnt++;
				}
				// cnt가 하나라도 있다면 스킬을 발동한다
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
			lblSkillPnt.setText("스킬포인트 : " + nSkillPnt);
			repaint();
		}
	}
} //GamePage class
