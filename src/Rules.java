import java.awt.Rectangle;


public class Rules implements Runnable {

	private boolean started = false;
	private boolean finishedBlink1 = false;
	private boolean finishedBlink2 = false;
	private boolean finished = false;														
	private int score = 0;
	private int maxScoreLeft = 0;
	private int seconds = 0;
	private int minutes = 0;
	private boolean pause = false;
	private boolean restart = false;
	private boolean startMenu = true;
	private int difficultyLevel=0;	//0=easy, 1=normal, 2=hard
	private  int difficultyLevel_thread;
	
	private String menuSelection = "Start game";

	private Snake snake;
	private Rules rules;
	private Food food;


	public void startGame(Rules rules) {										
		started = true;
		Thread t = new Thread(rules);
		menuSelection = "Restart game";

		switch(rules.getDifficultyLevel()) {
			case 0:
				rules.setDifficultyLevel_thread(300);
				break;
			case 1:
				rules.setDifficultyLevel_thread(200);
				break;
			case 2:
				rules.setDifficultyLevel_thread(100);
				break;		
		}
		
		t.start();
	}
	
	public void restartGame() {
		snake.setSnakeX(300);
		snake.setSnakeY(275);
		snake.setSnakeLeft(false);
		snake.setSnakeUp(false);
		snake.setSnakeDown(false);
		snake.setSnakeRight(true);
		rules.setScore(0);
		rules.setSeconds(0);
		rules.setMinutes(0);
		rules.setMaxScoreLeft(100);
		rules.setPause(false);
		snake.getList().clear();
		snake.getList().add(new Rectangle(snake.getSnakeX(), snake.getSnakeY(), 6, 6));
	}


	public void finishGame() {															
		started = false;

		//create blink animation 
		for(int i=0; i<4; i++) {			
			finishedBlink1 = true;
			GUI.f1.repaint();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finishedBlink1 = false;
			finishedBlink2 = true;
			GUI.f1.repaint();
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finishedBlink2 = false;
		}
		
		//display "game over" and score
		finishedBlink1 = false;
		finishedBlink2 = false;
		finished = true;
		menuSelection = "Restart game";											
		GUI.f1.repaint();
		
	}
	

	
	public void run() {
		
		snake = new Snake();
		food = new Food();
		food.setFood(food);
		SnakeGame.gui.setFood(food);							
		SnakeGame.gui.setSnake(snake);
		
		snake.getList().add(new Rectangle(snake.getSnakeX(), snake.getSnakeY(), 0, 0));
		snake.getList().add(new Rectangle(snake.getSnakeX(), snake.getSnakeY(), 0, 0));
		snake.getList().add(new Rectangle(snake.getSnakeX(), snake.getSnakeY(), 0, 0));
		snake.getList().add(new Rectangle(snake.getSnakeX(), snake.getSnakeY(), 0, 0));
		snake.getList().add(new Rectangle(snake.getSnakeX(), snake.getSnakeY(), 0, 0));

		long startTime = System.currentTimeMillis();
		long currentTime = 0;

		//game loop
		while(rules.started==true) {
			
				if(rules.isPause()==false) {

					collisionWall();
					
					placeFood();
					
					if(rules.getMaxScoreLeft()>0) {
						rules.setMaxScoreLeft(rules.getMaxScoreLeft()-1);
					}
				
					collisionFood();
						
					collisionSnake();
					
					snake.getList().add(new Rectangle(snake.getSnakeX(), snake.getSnakeY(), 6, 6));
					snake.getList().remove(0);
					
					//time-info
					currentTime = System.currentTimeMillis();
					if(currentTime - startTime >= 901) {						
						if(rules.getSeconds()<59) {
							rules.setSeconds(rules.getSeconds()+1);
						}else if(rules.getSeconds()>=59) {
							rules.setSeconds(0);
							rules.setMinutes(rules.getMinutes()+1);
						}	
						startTime += currentTime - startTime + 100;
					}
				}
			
			GUI.f1.repaint();
			

			try {
				Thread.sleep(rules.getDifficultyLevel_thread());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	public void collisionWall() {
		if(snake.isSnakeUp()==true) {
			if(snake.getSnakeY()<20) {
				finishGame();
			}else{
				snake.setSnakeY(snake.getSnakeY() - 15);
			}
			
		}else if(snake.isSnakeDown()==true) {
			if(snake.getSnakeY()>335) {
				finishGame();
			}else {
				snake.setSnakeY(snake.getSnakeY() + 15);
			}
		}else if(snake.isSnakeLeft()==true) {
			if(snake.getSnakeX()<20) {
				finishGame();
			}else {
				snake.setSnakeX(snake.getSnakeX() - 15);
			}
		}else if(snake.isSnakeRight()==true) {
			if(snake.getSnakeX()>370) {
				finishGame();
			}else {
				snake.setSnakeX(snake.getSnakeX() + 15);
			}
		}
	}
	
	
	
	public void collisionSnake() {
		for(int i=1; i<snake.getList().size()-1; i++) {
			
			if(i+1<snake.getList().size()) {
				if(snake.getList().get(0).intersects(snake.getList().get(i+1))) {
					finishGame();
				}
			}
		}
	}
	
	public void collisionFood() {
		if(Math.abs(food.getFoodX()-snake.getSnakeX())<=8  &&  Math.abs(food.getFoodY()-snake.getSnakeY())<=8) {
			food.setFoodPlaced(false);

			snake.getList().add(new Rectangle(snake.getSnakeX(), snake.getSnakeY(), 6, 6));

			rules.score += rules.getMaxScoreLeft();
		}
	}
	
	
	public void placeFood() {
		if(food.isFoodPlaced() == false) {
			food.setFoodX((int) (35+Math.random()*335));
			food.setFoodY((int) (35+Math.random()*315));
			food.setFoodPlaced(true);
			rules.maxScoreLeft = 100;
		}
	}
	
	
	
	public Snake getSnake() {
		return snake;
	}


	public void setSnake(Snake snake) {
		this.snake = snake;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public Rules getrules() {
		return rules;
	}


	public void setRules(Rules rules) {
		this.rules = rules;
	}

	public boolean isFinishedBlink1() {
		return finishedBlink1;
	}


	public void setFinishedBlink1(boolean finishedBlink1) {
		this.finishedBlink1 = finishedBlink1;
	}


	public boolean isFinishedBlink2() {
		return finishedBlink2;
	}


	public void setFinishedBlink2(boolean finishedBlink2) {
		this.finishedBlink2 = finishedBlink2;
	}


	public boolean isPause() {
		return pause;
	}


	public void setPause(boolean pause) {
		this.pause = pause;
	}


	public boolean isRestart() {
		return restart;
	}


	public void setRestart(boolean restart) {
		this.restart = restart;
	}


	public String getMenuSelection() {
		return menuSelection;
	}


	public void setMenuSelection(String menuSelection) {
		this.menuSelection = menuSelection;
	}

	public int getMaxScoreLeft() {
		return maxScoreLeft;
	}

	public void setMaxScoreLeft(int maxScoreLeft) {
		this.maxScoreLeft = maxScoreLeft;
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}


	public boolean isStartmenu() {
		return startMenu;
	}

	public void setStartmenu(boolean startmenu) {
		this.startMenu = startmenu;
	}

	public int getDifficultyLevel() {
		return difficultyLevel;
	}

	public void setDifficultyLevel(int difficultyLevel) {
		this.difficultyLevel = difficultyLevel;
	}

	public int getDifficultyLevel_thread() {
		return difficultyLevel_thread;
	}

	public void setDifficultyLevel_thread(int difficultyLevel_thread) {
		this.difficultyLevel_thread = difficultyLevel_thread;
	}

}
