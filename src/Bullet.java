import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

public class Bullet {
	//variables
	int vx, vy, xPos, yPos, speed, d,bulletType;
	boolean visible, collide;
	Image bullet, explosion; 
	
	//coordinates of enemy and starting position
	public Bullet(int shootToX, int shootToY, int x, int y, int speed, int bulletType){
		//determines the type of bullet is needed
		if (bulletType==1){
			bullet = new ImageIcon(getClass().getResource("bullet.png")).getImage();
		}else if (bulletType==2){
			bullet = new ImageIcon(getClass().getResource("bullet2.png")).getImage();
			
		}else if (bulletType==3){
			bullet = new ImageIcon(getClass().getResource("bullet3.png")).getImage();
		}
		//variables initialization
		xPos = x;
		yPos = y;
		this.speed = speed;
		this.bulletType=bulletType;
		setSpeed(shootToX, shootToY);
		visible = true; 
		collide = false;
	}
	public void setSpeed(int shootToX, int shootToY){
		//shoots at enemies 
		d = (int) Math.sqrt(Math.pow((shootToX-xPos),2)+Math.pow((shootToY-yPos),2));
		int h=shootToY-yPos;
		int w=shootToX-xPos;
		vx=w*speed/d;
		vy=h*speed/d;
	}
	
	//draw method
	public void draw(Graphics g){
		g.drawImage(bullet, xPos+60, yPos+60, 25, 25, null);

	}
	public void move(){
		//moves bullets according to the setSpeed method
		xPos += vx;
		yPos += vy;
	}
	public void updateVisible(){
		//if bullets goes off the screen, then it will disappear
		if(xPos > 1000 || xPos < 0 || yPos > 567 || yPos < 0 ){
			visible = false;
		}
	}

}
