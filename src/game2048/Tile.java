package game2048;

import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;

public class Tile {

	protected int value; // Ÿ���� value
	protected int type; // player == 200, monster == 300, �������� Tile
	protected int x, y; // Ÿ���� ��ġ (y, x) [0 ~ 5]
	protected int desX, desY; // Ÿ�� �̵� �� ���� ������ ��ǥ, �̵� �ִϸ��̼ǿ� ���δ�.
	protected float moveOffsetX, moveOffsetY; // Ÿ�� �̵� �ִϸ��̼ǿ� ���� offset ��
	protected int skillPnt;
	protected boolean bCanChanged; // Ÿ���� �� �� �̻� ���յ��� �ʴ´�. ���� ���ߴ��� üũ���ִ� boolean ����
	protected boolean bMoved; // Ÿ���� ���������� üũ���ִ� boolean ����
	protected boolean bNew;	// ���� ������ Ÿ������ Ȯ��ȳ�ִ� boolean ����. ���� ������ ������ �������� �׵θ��� ���δ�.
	protected Image img;
	
	// default ������
	public Tile() {}
		
	// �Ϲ������� ����ϴ� ������ value���� x, y ���� �Ķ���ͷ� �޴´�.
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
	public void setImg() { //���� ����� ������ ���� ȣ��, ���� �̹��� ����
		int i = (int)(Math.random() * 10) + 1; // 1 ~ 10
		ImageIcon icon;
				
		if (1 <= i  && i <= 7) { // 70���� Ȯ��, ��ų����Ʈ 1 ����
			icon = new ImageIcon("" + TileValue.Path[0]);
			skillPnt = 1;
		} else if (8 <= i  && i <= 9) { // 20���� Ȯ��, ��ų����Ʈ 2 ����
			icon = new ImageIcon("" + TileValue.Path[1]);
			skillPnt = 2;
		} else { // 10���� Ȯ��, ��ų����Ʈ 3 ����
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
		
	
	// Ÿ���� value���� ���� ������ color�� ��ȯ
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
