package paoo.System;


import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class Main {


    public static void main(String[] args) {
        //Creating the window
        JFrame window = new JFrame();

        //0-menu; 1- game; 2-help; 3-highscore
        int gameState = 0;

        //Creating the help JPanel
        HelpMenu helpMenu= new HelpMenu();

        //Creating the JPanel on which we draw
        Renderer renderer = new Renderer();

        //Creating the highscore JPanel
        Highscore highscore = new Highscore(renderer);

        //Create the menu JPanel
        Menu menu = new Menu();

        //this tells the game to close when the close button of the window is pressed
        window.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        window.setTitle("Game");
        window.setPreferredSize(new Dimension(Renderer.WIDTH, Renderer.HEIGHT));

        //Pack the window to the preferred size
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.setResizable(true);

        //Add the menu JPanel to the JFrame
        window.add(menu);

        //While the application is still running, it renders whatever needs based on the gameState
        while (renderer.getAppIsRunning()) {
            switch (gameState) {
                case 0:
                    menu.revalidate();
                    menu.repaint();
                    if (menu.getNextState() == 1) {
                        window.getContentPane().remove(menu);
                        renderer= new Renderer();
                        renderer.setIsRunning(true);
                        gameState=menu.getNextState();
                        menu.setNextState(0);
                        window.revalidate();
                        window.repaint();
                        window.getContentPane().add(renderer);
                        renderer.requestFocusInWindow();
                    }
                    if(menu.getNextState()==2){
                        window.remove(menu);
                        gameState=menu.getNextState();
                        menu.setNextState(0);
                        window.revalidate();
                        window.repaint();
                        window.add(helpMenu);
                        helpMenu.requestFocus();
                    }
                    if(menu.getNextState()==3){
                        renderer.setAppIsRunning(false);
                    }
                    break;
                case 1:
                    long lastTime = System.nanoTime(); //long 2^63
                    double nanoSecondConversion = 1000000000.0 / 60; //60 frames per second
                    double changeInSeconds = 0;
                    while (renderer.getIsRunning()) {
                        long now = System.nanoTime();
                        changeInSeconds += (now - lastTime) / nanoSecondConversion;
                        while (changeInSeconds >= 1) {
                            renderer.update();
                            changeInSeconds--;
                        }
                        renderer.revalidate();
                        renderer.repaint();
                        lastTime = now;
                    }
                    //Get the frame to the next state which is highscore every time
                    if(renderer.getNextState()) {
                        window.remove(renderer);
                        window.revalidate();
                        window.repaint();
                        window.add(highscore);
                        highscore.requestFocusInWindow();
                        renderer.setNextState(false);
                        gameState = 3;
                    }
                    break;
                case 2:
                    helpMenu.revalidate();
                    helpMenu.repaint();
                    if(helpMenu.getNextState()){
                        helpMenu.setNextState(false);
                        window.remove(helpMenu);
                        window.revalidate();
                        window.repaint();
                        window.add(menu);
                        gameState=0;
                        menu.requestFocusInWindow();
                    }
                    break;
                case 3:
                    highscore.revalidate();
                    highscore.repaint();
                    if(highscore.getNextState()){
                        highscore.setNextState(false);
                        window.remove(highscore);
                        window.revalidate();
                        window.repaint();
                        renderer=new Renderer();
                        window.add(menu);
                        gameState=0;
                        menu.requestFocusInWindow();
                    }
                    break;
                default:
                    break;
            }
        }

        window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));

    }

}
