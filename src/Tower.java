import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class Tower {
	Image c, weapon;
	int xCoord, yCoord, weaponType;
	
	public Tower(int x, int y, int weaponType){ 
		
		//passes the values passed to the variables   
		xCoord = x;
		yCoord = y;
		
		this.weaponType = weaponType;
		
		c = new ImageIcon(getClass().getResource("circle.png")).getImage();
		//determines which image to load depending on weapon type
		if (weaponType==1)
			weapon = new ImageIcon("weapon1.png").getImage();
		else if (weaponType==2)
			weapon = new ImageIcon("weapon2.png").getImage();
		else if (weaponType==3)
			weapon = new ImageIcon("weapon3.png").getImage();
	}
	public void setX(int x){ //added setX and setY for towers, probably need them later.
		xCoord=x;
	}
	public void setY(int y){
		yCoord=y;
	}
	//draw method
	public void draw(Graphics g){
		g.drawImage(weapon, xCoord, yCoord, null);
	}


}
