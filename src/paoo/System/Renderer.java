package paoo.System;

import paoo.Entities.GameObject;
import paoo.Entities.Monster;
import paoo.Entities.Player;
import paoo.Map.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class Renderer extends JPanel {

    private int level=1;
    private Player player;
    private Map map;
    private long timeElapsed = 0;
    private int score=0;
    private boolean isRunning=false;
    private boolean appIsRunning;
    private boolean nextState=false;

    /**
     * Defining the resolution of the app
     * getting the height on a width/16*9 scale so it has the 16:9 ratio
     */
    public static int WIDTH=1400;
    public static int HEIGHT=WIDTH/16*9;

    /**
     * The array list to be iterated and handle all the game objects in game
     */
    private ArrayList<GameObject> gameObjects=new ArrayList<>();


    public Renderer(){
        super();
        appIsRunning=true;
        map= new Map();
        this.player=new Player(100,100,10,4);

        //set the size of the window
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(null);
        setVisible(true);
        requestFocusInWindow(true);



        //set text
        setFont(new Font("Arial", Font.PLAIN, 30));
        setForeground(Color.WHITE);

        timeElapsed=System.currentTimeMillis();




        //Adding the mouse listener on the JPanel so we can handle the mouse events
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_S || e.getKeyCode()==KeyEvent.VK_DOWN ){
                    player.setMovingDown(true);
                }
                if(e.getKeyCode()==KeyEvent.VK_A || e.getKeyCode()==KeyEvent.VK_LEFT ){
                    player.setMovingLeft(true);
                }
                if(e.getKeyCode()==KeyEvent.VK_D || e.getKeyCode()==KeyEvent.VK_RIGHT){
                    player.setMovingRight(true);
                }
                if(e.getKeyCode()==KeyEvent.VK_W || e.getKeyCode()==KeyEvent.VK_UP){
                    player.setMovingUp(true);
                }
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    player.setShooting(true);
                }
                if(e.getKeyCode()==KeyEvent.VK_N && !isRunning){
                    nextState=true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_S || e.getKeyCode()==KeyEvent.VK_DOWN){
                    player.setMovingDown(false);
                }
                if(e.getKeyCode()==KeyEvent.VK_A || e.getKeyCode()==KeyEvent.VK_LEFT){
                    player.setMovingLeft(false);
                }
                if(e.getKeyCode()==KeyEvent.VK_D || e.getKeyCode()==KeyEvent.VK_RIGHT){
                    player.setMovingRight(false);
                }
                if(e.getKeyCode()==KeyEvent.VK_W || e.getKeyCode()==KeyEvent.VK_UP){
                    player.setMovingUp(false);
                }
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    player.setShooting(false);
                }
            }
        });

    }


    @Override
    public Dimension getPreferredSize(){
        if(isPreferredSizeSet()){
            return super.getPreferredSize();
        }
        return new Dimension(WIDTH,HEIGHT);
    }

    /**
     * Override the paintComponent method in the JPanel so it renders the way we want
     * @param g the graphics component of the JPanel
     */
    @Override
    public void paintComponent(Graphics g){

        //If the game is still running then render the map, player and gameObjects else show game over text
        if(isRunning) {
            //Call the render method for the map
            map.render(g,level);

            //Call the render method for the player
            player.render(g);

            //Iterate all the game objects and calling the render method
            for(int index=0; index<gameObjects.size(); ++index) {
                gameObjects.get(index).render(g);
            }

            //Drawing Strings on the JPanel for player score and health
            g.drawString("Score: " + score, 70, 100);
            g.drawString("Player health: " + player.getPlayerHealth(), 70, 70);
            g.drawString("Level: "+level,70,130);
            //Calling the sync method for the toolkit to stop the slow generating graphics for linux builds
            //This method takes some cpu cycles, but otherwise the game gets slower on linux distributions
            Toolkit.getDefaultToolkit().sync();
        }
        else{
            if(level==3 && score>=level*10){
                g.drawString("You won", Renderer.WIDTH/2-100,Renderer.HEIGHT/2);
            }
            else {
                g.drawString("Game over", Renderer.WIDTH / 2 - 100, Renderer.HEIGHT / 2);
            }
            g.drawString("Press N to continue", Renderer.WIDTH/2-150,Renderer.HEIGHT/2+100);
        }
    }

    public ArrayList getObjects(){
        return gameObjects;
    }

    /**
     * This method adds a game object to the game
     * @param object= the game object to be added in the game
     */
    public void addObject(GameObject object){
        gameObjects.add(object);
    }


    /**
     * Getter method for the map
     * @return the map
     */
    public Map getMap(){
        return map;
    }


    /**
     * The update method iterates all the game objects and updates it based on its behaviour
     */
    public void update(){

        //Update the game while it is running
        if(isRunning) {
            //Updating the player
            player.update(this);
            if(score >= level*10)
            {
                //player.setPlayerHealth(10);
                this.player=new Player(100,100,10,4);
                score=0;
                setLevel(level+1);
            }

            //Generating a random number for x,y coordinates of the monster
            Random r = new Random();
            int xMonster = r.nextInt(Renderer.WIDTH - 48 * 3);
            xMonster += 60;
            int yMonster = r.nextInt(Renderer.HEIGHT - 48 * 3);
            yMonster += 60;

            //Spawn monster on a 3 sec delay
            if (System.currentTimeMillis() - timeElapsed >= 3000) {
                timeElapsed = System.currentTimeMillis();
                Monster spawnMonster = new Monster(xMonster, yMonster, 3, 3);
                if (!map.checkCollision(spawnMonster.getSprite(),level)) {
                    addObject(spawnMonster);
                }
            }

            for (int index = 0; index < gameObjects.size(); ++index) {
                gameObjects.get(index).update(this);
            }
            if(player.getPlayerHealth() <= 0){
                isRunning=false;
            }
            if(level==3 && score>=level*10){
                isRunning=false;
            }
        }
    }


    public int getScore(){
        return score;
    }

    public void setScore(int score){
        this.score=score;
    }
    public Player getPlayer(){
        return player;
    }

    public boolean getIsRunning(){
        return isRunning;
    }

    public void setIsRunning(boolean value){
        isRunning=value;
    }

    public boolean getAppIsRunning(){
        return appIsRunning;
    }

    public void setAppIsRunning(boolean value){
        appIsRunning=value;
    }

    public boolean getNextState(){
        return nextState;
    }

    public void setNextState(boolean value){
        nextState=value;
    }

    public int getLevel(){
        return level;
    }

    public void setLevel(int level){
        this.level=level;
    }
}
