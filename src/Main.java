import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
//import java.util.Scanner;
//import javax.imageio.ImageIO;
import javax.swing.*;

public class Main extends JFrame implements KeyListener, MouseMotionListener, MouseListener, ActionListener{
	//arraylist of the classes created 
	ArrayList <Bullet> bullets = new ArrayList <Bullet>(); 
	ArrayList <Tower> towers = new ArrayList <Tower>(); 
	ArrayList <Enemy> enemies = new ArrayList <Enemy>(); 
	//arraylist of whether the circle should be displayed or not
	ArrayList <Boolean> visibleCircle = new ArrayList <Boolean>();
	//arraylist to determine which tower should fire
	ArrayList <Integer> fireFromTo = new ArrayList <Integer>();

	//high score
	ArrayList <Integer> score = new ArrayList <Integer>();
	ArrayList <String> name = new ArrayList <String>();

	Tower tower;
	Timer time = new Timer(25, this);

	JLabel tower1, tower2, tower3, statusBar;
	JPanel south, center, menuCenter, background; 
	Container c;
	Graphics gr;
	Font arial; 

	int mouseX, mouseY, menu, choice, fireAt, enemyFire, enemyCount, points, baseHealth, fireRate, finalPoints, numberOfEnemies;
	boolean towerOnMouse, collide, fire, circleTouch, outOfRange, explosion, launch;
	String username; 

	Image explosionImg;
	int explosionX, explosionY;

	//variables for enemy waves 
	int currentT;
	long initialT;
	int countdownT;
	int spawnRate;

	//variables for explosion
	long explosionStart;
	int explosionDur;

	//collision
	Rectangle bullet;
	Rectangle enemy;
	Rectangle weapon;

	public Main(){

		super("Candy Defense"); 
		//key listeners
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);	

		//initializes variables 
		choice = 0;
		enemyCount = 0;
		points = 100;
		numberOfEnemies = 25;
		finalPoints = points;
		baseHealth = 25; 
		towerOnMouse = false;
		collide = false;
		fire = false;
		circleTouch = false;
		launch = true;
		username = "";

		currentT = 0;
		initialT = 0;
		countdownT = 31;

		spawnRate = 400;

		explosionDur = 1500;

		//shows status bar
		statusBar = new JLabel();
		c = getContentPane();

		//converts towers into JLabel on a panel
		tower1 = new JLabel(new ImageIcon("weapon1.png"));
		tower2 = new JLabel(new ImageIcon("weapon2.png"));
		tower3 = new JLabel(new ImageIcon("weapon3.png"));
		tower1.setOpaque(false);
		tower2.setOpaque(false);
		tower3.setOpaque(false);
		south = new JPanel();
		center = new JPanel();
		south.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
		//store
		south.add(tower1);
		south.add(tower2);
		south.add(tower3);
		south.setOpaque(false);

		background = new JPanel(){
			public void paintComponent(Graphics g){ // right now everything is being painted and repainted here. I know repaint is not supposed to be used for animation, but it's all we have for now since actionPerformed is not working.

				gr = g;
				super.paintComponent(g);

				if(choice == 0){ // menu
					g.drawImage((new ImageIcon("bg.png")).getImage(), 0, 0, null);

				}
				if (choice==1){ // ingame

					//asks user to enter score
					if(baseHealth == 0){			
						//displays high score 
						g.drawImage((new ImageIcon("highscores.png")).getImage(), 0, 0, null);
						arial = new Font("Arial Rounded MT Bold", Font.PLAIN, 40);
						background.setFont(arial);
						g.drawString(""+finalPoints, 460, 340);
						g.drawString(""+username, 330, 530);
						g.drawString("Type in your name.", 39, 49);
						g.drawString("Press Enter after you are done!", 39, 100);
						background.remove(south);

					}else{
						//displays background
						g.drawImage((new ImageIcon("gameBg.png")).getImage(), 0, 0,1000,700, null);
						g.drawImage((new ImageIcon("exit.png")).getImage(), 8, 585, null);
						//displays panel of store
						background.add(south, BorderLayout.SOUTH);

						if (tower!=null){
							tower.draw(g);
						}
						//displays radius of tower 
						for (int i=0;i<visibleCircle.size();i++){
							if (visibleCircle.get(i)){
								g.drawImage(new ImageIcon(getClass().getResource("circle.png")).getImage(), towers.get(i).xCoord-40, towers.get(i).yCoord-60, null);
							}
						}
						//displays the towers
						for(int i= 0; i < towers.size(); i++){
							towers.get(i).draw(gr);
						}

						//draws points
						g.drawString("Score: "+finalPoints, 840, 40);
						g.drawString("Money: "+points, 110, 40);
						g.drawString(""+baseHealth, 923, 453);
						g.drawString("Weapon Store", 418, 505);


						//draws timer
						g.drawImage((new ImageIcon("timer.png")).getImage(), 15, 20, null);

						//calls time method
						updateTime();

						//formats and display the timer number placement
						if(countdownT < 10)
							gr.drawString(""+countdownT, 53, 75);
						else 
							gr.drawString(""+countdownT, 44, 75);

						//painting bullets by calling the methods from bullet class 
						for(int i = 0; i < bullets.size(); i++){
							bullets.get(i).move();
							bullets.get(i).draw(gr);	
							bullets.get(i).updateVisible();
							if(bullets.get(i).visible == false){
								bullets.remove(i);
							}
						}

						//explosion for tower 2
						if (explosion==true){
							//delays the explosion image being shown so users can see the hit impact
							explosionImg = new ImageIcon("explosionwep2.png").getImage();
							if(System.currentTimeMillis()-explosionStart<explosionDur){
								g.drawImage(explosionImg,explosionX, explosionY,null);
							}else{
								explosion = false;
								explosionStart = 0;
							}
						}

						//painting enemies by calling method from enemy class
						for(int i = 0; i < enemies.size(); i++){
							enemies.get(i).moveEnemy();
							enemies.get(i).drawEnemy(gr);
							enemies.get(i).updateVisible();
							if(enemies.get(i).visible == false){
								//base will lose lives 
								baseHealth -= 1;
								enemies.remove(i);
							}
						}

						//detects collision if there is enemies in the arraylist
						if(enemies.size() >0){
							circleDetection();
							bulletEnemyDetection();
						}
					}
				}

				//instructions
				if(choice == 2){
					g.drawImage((new ImageIcon("help.png")).getImage(), 0, 0, null);
				}

				//high score
				if(choice == 3){
					//draws background image
					g.drawImage((new ImageIcon("highscoreBg.png")).getImage(), 0, 0, null);
					//display the top scores
					if(name.size() > 5){
						g.drawString("1. "+name.get(name.size()-1), 320, 351);//first place
						g.drawString(""+score.get(score.size()-1), 610, 351);
						g.drawString("2. "+name.get(name.size()-2), 320, 381);//second place
						g.drawString(""+score.get(score.size()-2), 610, 381);
						g.drawString("3. "+name.get(name.size()-3), 320, 411);//third place
						g.drawString(""+score.get(score.size()-3), 610, 411);
						g.drawString("4. "+name.get(name.size()-4), 320, 441);//fourth place
						g.drawString(""+score.get(score.size()-4), 610, 441);
						g.drawString("5. "+name.get(name.size()-5), 320, 471);//fifth place
						g.drawString(""+score.get(score.size()-5), 610, 471);
					}						
				}

				//exits
				if (choice == 4){
					System.exit(0);
				}

			}

		};

		background.setLayout(new BorderLayout());
		background.add(statusBar, BorderLayout.WEST);
		background.setOpaque(false);

		//adds fonts
		arial = new Font("Arial Rounded MT Bold", Font.PLAIN, 22);
		background.setFont(arial);
		background.setForeground(Color.WHITE);
		c.add(background);
		
		//starts timer
		time.start();
	}
	
	//high score method
	public void highscore() throws IOException{
		//opens text file
		BufferedReader in = new BufferedReader(new FileReader("highscore.txt"));
		String line, scores = "", name1 = "";
		int scores2;

		//reads the lines from the text file
		while ((line = in.readLine()) != null){
			scores = line.substring(line.indexOf(" ")+1);
			name1 = line.substring(0, line.indexOf(" "));
			//converts score into integer
			scores2 = Integer.parseInt(scores);
			//Adds the content to arrayLists
			name.add(name1);
			score.add(scores2);
		}

		//Close the input stream
		in.close();

		//sorts the names and scores
		for(int top = score.size()-1; top > 0; top--){
			int large = 0;
			for (int i = 1; i <= top; i++)
				if(score.get(i) > score.get(large)){
					large = i;
				}
			//sorts the names
			String temp1 = name.get(top);
			name.set(top, name.get(large));
			name.set(large, temp1);

			//sorts the numbers
			int temp = score.get(top);
			score.set(top, score.get(large));
			score.set(large, temp);
		}
	}

	//displays the update time until enemy waves comes
	public void updateTime(){
		currentT++;
		if(System.currentTimeMillis() - initialT >= 1000 && countdownT != 0){
			countdownT -= 1; 
			currentT = 0;
			initialT = System.currentTimeMillis();
		}
	}

	//bullet enemy detection
	public void bulletEnemyDetection(){
		if (enemies.size() > 0 && bullets.size() > 0){
			for(int i = 0; i < enemies.size(); i++){
				//converts enemy into a rectangle by calling rectangle method
				enemy = getBoundsEnemy(enemies.get(i));
				for(int j = 0; j < bullets.size(); j++){
					//converts bullets into a rectangle by calling rectangle method
					bullet = getBoundsBullet(bullets.get(j));
					//finds out if bullet and enemy collide calling the collision method
					collide = collision(bullet, enemy);
					if(collide){
						//bullet type 1 can only hit 1 enemy 
						if (bullets.get(j).bulletType==1){
							bullets.remove(j);
							enemies.get(i).maxHits--;
							if (enemies.get(i).maxHits<=0){
								//adds points
								if(enemies.get(i).enemyType == 1){
									points += 1;
									finalPoints +=1;
								}else if(enemies.get(i).enemyType == 2){
									points += 3;
									finalPoints+=3;
								}else if(enemies.get(i).enemyType == 3){
									points += 5;
									finalPoints+= 5;
								}

								enemies.remove(i);
							}
						//bullet type 2 will explode and touch more enemies, removing 1 life span 
						}else if (bullets.get(j).bulletType==2){
							explosion = true;
							explosionX = bullets.get(j).xPos;
							explosionY = bullets.get(j).yPos;
							explosionStart=System.currentTimeMillis();
							bullets.remove(j);
							//goes through arraylist of enemies and see if the bullet touched the enemies
							for (int a=0;a<enemies.size();a++){
								if (Math.abs(enemies.get(i).xPos - enemies.get(a).xPos) < 50){
									//removes one health for each enemy touches
									enemies.get(a).maxHits --;
								}
								if (enemies.get(a).maxHits<=0){
									//adds points depending on the enemy type
									if(enemies.get(i).enemyType == 1){
										points += 1;
										finalPoints += 1;
									}else if(enemies.get(i).enemyType == 2){
										points += 3;
										finalPoints += 3;
									}else if(enemies.get(i).enemyType == 3){
										points += 5;
										finalPoints += 5;
									}
									enemies.remove(a);
								}
							}
						//third bullet speed is much slower but kills every enemy in its way	
						}else if (bullets.get(j).bulletType==3){
							//adds points
							if(enemies.get(i).enemyType == 1){
								points += 1;
								finalPoints += 1;
							}else if(enemies.get(i).enemyType == 2){
								points += 3;
								finalPoints += 3;
							}else if(enemies.get(i).enemyType == 3){
								points += 5;
								finalPoints += 5;
							}

							enemies.remove(i);

						}
					}	
				}
			}
		}
	}

	//circle detection
	public void circleDetection(){
		if (enemies.size() > 0 && towers.size() > 0){
			for(int i = 0; i < enemies.size(); i++){
				enemy = getBoundsEnemy(enemies.get(i));
				for(int j = 0; j < towers.size(); j++){
					weapon = getBoundsWeapon(towers.get(j));
					//finds out if enemy is in range of the tower
					circleTouch = collision(weapon, enemy);
					if(circleTouch){
						boolean containsTowerAlready = false;
						//goes through fireFromto arraylist and see which tower should fire
						for (int a=2;a<fireFromTo.size();a+=3){
							if (fireFromTo.get(a)==j){
								fireFromTo.set(a-1,1);
								containsTowerAlready=true;
							}
						}
						if (!containsTowerAlready){
							fireFromTo.add(i);
							//enemyFire=i;
							fireFromTo.add(1);
							//fire = true;
							//tells program which tower to fire from
							fireFromTo.add(j);
							//fireAt = j;
						}

					}	
				}
			}
		}

	}

	//fires bullets at enemies
	public void fireAtEnemy(){
		for (int i=1;i<fireFromTo.size();i+=3){
			if(fireFromTo.get(i)==1){
				if (enemies.size()>0){ //if there are enemies on screen, aim at first enemy
					int x = towers.get(fireFromTo.get(i+1)).xCoord;
					int y = towers.get(fireFromTo.get(i+1)).yCoord;
					Bullet b = null;
					//depending on the type of tower type, different bullets are launched with different speed
					if (enemies.size()>fireFromTo.get(i-1)){
						if (towers.get(fireFromTo.get(i+1)).weaponType==1){
							if ((cumulativeTime-startTime)%400<=25){
								b = new Bullet(enemies.get(fireFromTo.get(i-1)).xPos,enemies.get(fireFromTo.get(i-1)).yPos, x, y, 20,1);
								bullets.add(b);
							}
						}if (towers.get(fireFromTo.get(i+1)).weaponType==2){
							if ((cumulativeTime-startTime)%500<=25){
								b = new Bullet(enemies.get(fireFromTo.get(i-1)).xPos,enemies.get(fireFromTo.get(i-1)).yPos, x, y, 10,2);
								bullets.add(b);
							}
						}if (towers.get(fireFromTo.get(i+1)).weaponType==3){
							if ((cumulativeTime-startTime)%4000<=25){
								b = new Bullet(enemies.get(fireFromTo.get(i-1)).xPos,enemies.get(fireFromTo.get(i-1)).yPos, x, y, 20,3);
								bullets.add(b);
							}
						}

					}
					fireFromTo.set(i,0);
				}
			}
		}
	}

	long startTime = System.currentTimeMillis();
	long cumulativeTime = startTime;

	public void actionPerformed(ActionEvent e){ 	

		if(e.getSource() == time){
			//bullet fire rate control
			cumulativeTime = startTime;
			long timePassed=System.currentTimeMillis() - cumulativeTime;
			cumulativeTime += timePassed;
			
			//speed at which the bullets fire 
			if ((cumulativeTime-startTime)%400<=25){
				fireAtEnemy();
			}
			if ((cumulativeTime-startTime)%spawnRate<=25){
				//launches the enemy when timer says 0
				if(countdownT == 0 && launch){
					int random = (int)(Math.random()*3)+1;
					Enemy en = new Enemy(0, 200, 5, random); // refer to Enemy class
					enemies.add(en);
					enemyCount++;
				}
				//once enemyCount reaches the number of enemies in a wave
				//gives user time to buy more towers, etc
				if (enemyCount == numberOfEnemies){
					if(enemies.size() == 0){
						enemies.clear();
						//resets variables  
						currentT = 0;
						initialT = 0;
						countdownT = 21; 
						//the number of enemies increases by 5 each time
						enemyCount = 0;
						numberOfEnemies += 5;
						//enemies will spawn faster after each wave
						if (spawnRate - 40 > 50){
							spawnRate -= 40;
						}
						launch = true;
					}else{
						launch = false;
					}
				}	
			}
			background.repaint();
		}
	}	
	//converts the images into rectangle 
	public Rectangle getBoundsBullet(Bullet a){
		//passed through x coord, y coord, width and height
		return new Rectangle(a.xPos, a.yPos, 40, 40);
	}
	public Rectangle getBoundsEnemy(Enemy a){
		return new Rectangle(a.xPos, a.yPos, 60, 60);
	}
	public Rectangle getBoundsWeapon(Tower a){
		return new Rectangle(a.xCoord, a.yCoord, 250, 250);
	}
	//checks if the rectangles touch each other and returns a boolean
	public boolean collision(Rectangle a, Rectangle b){
		if(a.intersects(b)){
			return true;
		}else{
			return false;
		}
	}

	//mouse events
	public void mouseClicked(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		if(choice == 0){
			//pressing play in menu
			if(mouseX > 754 && mouseX < 974 && mouseY > 111 && mouseY < 171){
				choice = 1;
				background.repaint();
			}
			//help/instructions
			if(mouseX > 754 && mouseX < 974 && mouseY > 220 && mouseY < 280){
				choice = 2;
				background.repaint();
			}
			//high scores screen
			if(mouseX > 754 && mouseX < 974 && mouseY > 314 && mouseY < 371){
				choice = 3;
				try {
					//runs high score method
					highscore();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				background.repaint();	
			}
			//exits screen
			if(mouseX > 754 && mouseX < 974 && mouseY > 400 && mouseY < 466){
				choice=4;
			}
		}
		
		//if user clicks on exit button in game
		if(choice == 1 && mouseX > 19 && mouseX < 85 && mouseY > 618 && mouseY < 682 ){
			baseHealth = 0;
		}

		//inside high score and instructions takes them to main menu
		if((choice == 3 || choice==2) && mouseX > 807 && mouseX < 995 && mouseY > 530 && mouseY < 698){
			choice = 0;
			score.clear();
			name.clear();
		}

		//sets tower visibility
		for (int i=0;i<towers.size();i++){
			if (!visibleCircle.get(i)){
				if (mouseX>towers.get(i).xCoord && mouseX < towers.get(i).xCoord+170 && mouseY > towers.get(i).yCoord && mouseY < towers.get(i).yCoord+122){
					visibleCircle.set(i, true);
				}
			}
			else{
				if (mouseX>towers.get(i).xCoord && mouseX < towers.get(i).xCoord+170 && mouseY > towers.get(i).yCoord && mouseY < towers.get(i).yCoord+122){
					visibleCircle.set(i, false);
				}
			}
		}
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {
		int mouseX=e.getX();
		int mouseY=e.getY();
		//user can only buy towers when timer is on
		if(choice == 1 && enemies.size() == 0){
			//tower type 1
			if (mouseX > 250 && mouseX < 390 && mouseY>590 && mouseY<670 && points-30 >= 0){
				towerOnMouse = true;
				//adds new tower to the position dragged and sees x and y positions, tower type
				tower = new Tower(mouseX-60, mouseY-60, 1);
				visibleCircle.add(false);

				//subtract points
				points -= 30;
			}
			//tower type 2
			else if (mouseX > 410 && mouseX < 550 && mouseY>590 && mouseY<670 && points-50 >= 0){
				towerOnMouse = true;
				tower = new Tower(mouseX-60, mouseY-60, 2);
				visibleCircle.add(false);

				//subtract points
				points -= 50;
			}
			//tower type 3
			else if (mouseX > 570 && mouseX < 710 && mouseY>590 && mouseY<670 && points - 70 >= 0){
				towerOnMouse = true;
				tower = new Tower(mouseX-60, mouseY-60, 3);
				visibleCircle.add(false);

				//subtract points 
				points -= 70;
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (towerOnMouse){
			towerOnMouse = false;
			towers.add(tower);
			tower = null;
		}		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		//finds the x and y coordinates of the mouse
		mouseX = e.getX();
		mouseY = e.getY();

		if (towerOnMouse){ // drags tower around on screen
			tower.setX(mouseX-60); //refer to Tower class
			tower.setY(mouseY-60);
			background.repaint();
		}
	}

	public void mouseMoved(MouseEvent e) {
		statusBar.setText(mouseX+" "+mouseY);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if(baseHealth == 0){
			if ((char)keyCode>='A' && (char)keyCode<='Z' && username.length() <= 11 && keyCode != KeyEvent.VK_BACK_SPACE && keyCode != KeyEvent.VK_ENTER){
				username += KeyEvent.getKeyText(keyCode);
				//deletes the last letter if user makes a mistake	
			}if(keyCode == (int)KeyEvent.VK_BACK_SPACE){
				if(username.length() != 0)
					username = username.substring(0, username.length()-1);
			}if(keyCode == (int)KeyEvent.VK_ENTER){
				//high scores
				try {
					//appends to highscore list 
					PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("highscore.txt", true)));
					//adds username and score to text file
					out.print(username+" "+finalPoints+"\n");
					out.close();
				} catch (IOException ex) {
					// catches exceptions
					ex.printStackTrace();
				}

				//variables refresh
				choice = 0;
				enemyCount = 0;
				points = 100;
				finalPoints=points;
				baseHealth = 25;
				numberOfEnemies = 25;
				username = "";
				towerOnMouse = false;
				collide = false;
				fire = false;
				circleTouch = false;
				launch = true;

				//adds fonts
				arial = new Font("Arial Rounded MT Bold", Font.PLAIN, 22);
				background.setFont(arial);

				//timer refresh
				currentT = 0;
				initialT = 0;
				countdownT = 31;
				spawnRate = 400;

				//clears the towers and enemies list
				towers.clear();
				enemies.clear();
				bullets.clear();
				visibleCircle.clear();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	public static void main(String[] args) {
		Main i = new Main();
		i.setSize(1000, 700);
		i.setVisible(true);
		i.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
	}

}
