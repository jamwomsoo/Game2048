package game2048;

import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;

public class Tile {

	protected int value; // 타일의 value
	protected int type; // player == 200, monster == 300, 나머지는 Tile
	protected int x, y; // 타일의 위치 (y, x) [0 ~ 5]
	protected int desX, desY; // 타일 이동 시 도작 지점의 좌표, 이동 애니메이션에 쓰인다.
	protected float moveOffsetX, moveOffsetY; // 타일 이동 애니메이션에 쓰일 offset 값
	protected int skillPnt;
	protected boolean bCanChanged; // 타일은 한 번 이상 병합되지 않는다. 값이 변했는지 체크해주는 boolean 변수
	protected boolean bMoved; // 타일이 움직였는지 체크해주는 boolean 변수
	protected boolean bNew;	// 새로 생성된 타일인지 확인홰주는 boolean 변수. 새로 생성된 파일은 오렌지색 테두리가 보인다.
	protected Image img;
	
	// default 생성자
	public Tile() {}
		
	// 일반적으로 사용하는 생성자 value값과 x, y 값을 파라미터로 받는다.
	public Tile(int value, int x, int y) {
		this.value = value;
		
		if(value != 0 && value != 200 && value != 300)
			type = 100;
		else
			type = value;
		
		desX = this.x = x;
		desY = this.y = y;
			
		moveOffsetX = 0;
		moveOffsetY = 0;
			
		skillPnt = 0;
			
		bCanChanged = true;
		bMoved = false;
		bNew = false;
	} //Tile()
		
	public void setData(Tile tile) {
		this.value = tile.value;
		this.type = tile.type;
		this.x = tile.x;
		this.y = tile.y;
		this.desX = tile.desX;
		this.desY = tile.desY;
		this.moveOffsetX = tile.moveOffsetX;
		this.moveOffsetY = tile.moveOffsetY;
		this.skillPnt = tile.skillPnt;
		this.bCanChanged = tile.bCanChanged;
		this.bMoved = tile.bMoved;
		this.bNew = tile.bNew;
	} //setData()
		
	public Image getImg() { return img; }
	public void setImg() { //몬스터 블록이 생성될 때만 호출, 랜덤 이미지 저장
		int i = (int)(Math.random() * 10) + 1; // 1 ~ 10
		ImageIcon icon;
				
		if (1 <= i  && i <= 7) { // 70프로 확률, 스킬포인트 1 몬스터
			icon = new ImageIcon("" + TileValue.Path[0]);
			skillPnt = 1;
		} else if (8 <= i  && i <= 9) { // 20프로 확률, 스킬포인트 2 몬스터
			icon = new ImageIcon("" + TileValue.Path[1]);
			skillPnt = 2;
		} else { // 10프로 확률, 스킬포인트 3 몬스터
			icon = new ImageIcon("" + TileValue.Path[2]);
			skillPnt = 3;
		}
		img = icon.getImage();
	} //setImg()
	
	public void setValue(int value) {
		this.value = value;
		if (value != 0 && value != 200 && value != 300)
			type = 100;
		else
			type = value;
	} //setValue()
		
	
	// 타일의 value값에 따라서 정해진 color를 반환
	public Color getBackgroundColor() {
		switch (value) {
			case 2:    return new Color(0xffffcc);
			case 4:    return new Color(0xffff77);
			case 8:    return new Color(0xffcc66);
			case 16:   return new Color(0xff9999);
			case 32:   return new Color(0xff7766);
			case 64:   return new Color(0xff6600);
			case 128:  return new Color(0xff0033);
			case 256:  return new Color(0xff33cc);
			case 512:  return new Color(0xff33ff);
		}
		return new Color(0xcdc1b4);
	} //getBackgroundColor()
	
} //Tile class
