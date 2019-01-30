package org.sla;

public class Controller {
    int heroX, heroY;
    int newHeroX, newHeroY;

       if (keyPressed) {
        if ((key == CODED) && (keyCode == UP)) {
            newHeroY = heroY - 2;
        }
        if ((key == CODED) && (keyCode == DOWN)) {
            newHeroY = heroY + 2;
        }
        if ((key == CODED) && (keyCode == LEFT)){
            newHeroX = heroX - 2;
        }
        if ((key == CODED) && (keyCode == RIGHT)){
            newHeroX = heroX + 2;
        }

    }



}
