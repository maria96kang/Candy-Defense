import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Enemy {
	//variables
	Image enemy;
	int enemyType, xPos, yPos, speedEnemy, hits, maxHits; 
	boolean visible;
	public Enemy(int x, int y, int speedE, int type){
		//initializes variables
		visible = true;
		hits = 0;
		xPos = x;
		yPos = y;
		speedEnemy = speedE; 
		enemyType = type;
		//finds out the type of enemy that is on screen and loads images
		if(enemyType == 1){
			enemy = new ImageIcon(getClass().getResource("enemy1.png")).getImage();
			maxHits = 1;
		}else if(enemyType == 2){
			enemy = new ImageIcon(getClass().getResource("enemy2.png")).getImage();
			maxHits = 3; 
		}else if (enemyType == 3){
			enemy = new ImageIcon(getClass().getResource("enemy3.png")).getImage();
			maxHits = 5;
		}
	}
	//draws the enemies on screen
	public void drawEnemy(Graphics g){
		g.drawImage(enemy, xPos, yPos, 40, 40, null);
	}
	public void moveEnemy(){
		//path of enemies, hard coded
		if (yPos>470){
			xPos+=speedEnemy;
		}else if (xPos > 800){
			yPos+=speedEnemy;
		}else if (yPos < 200){
			xPos+=speedEnemy;
		}else if(xPos > 400){
			yPos-=speedEnemy;
		}else if(yPos > 410){
			xPos+=speedEnemy; 
		}else if(xPos > 200 && xPos<=400){
			yPos+=speedEnemy;
		}else if (xPos<= 200){
			xPos+=speedEnemy;
		}
	}
	//updates visibility
	public void updateVisible(){
		if(hits >= maxHits ){
			visible = false;	
		}else if(xPos > 1000 || xPos < 0 || yPos > 567 || yPos < 0 ){
			visible = false;
		}
	}
}
