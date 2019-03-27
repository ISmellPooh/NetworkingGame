package org.sla;

public class Projectile {
    int x;
    int y;
    int player;
    int deltaX;
    int deltaY;

    Projectile(int p, int newX, int newY, int dX, int dY) {
        x = newX;
        y = newY;
        player = p;
        deltaX = dX;
        deltaY = dY;
    }

    void setX(int newX) {
        x = newX;
    }
    void setY(int newY) {
        y = newY;
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    int getDeltaX() {
        return deltaX;
    }

    int getDeltaY() {
        return deltaY;
    }

}
