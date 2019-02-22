package org.sla;

public class Queue {

    private Object[] array;
    private int nextPutLocation = 0;
    private int nextGetLocation = 0;
    private int amountOfData;

    // Constructor
    Queue() {
        array = new Object[100];
        amountOfData = 0;
    }

    synchronized boolean put(Object obj) {
        if (amountOfData <= 99) {
            array[nextPutLocation] = obj;
            amountOfData = amountOfData + 1;
            //System.out.println("amountOfData = " + amountOfData);
            if (nextPutLocation == 99) {
                nextPutLocation = 0;
            } else {
                nextPutLocation = nextPutLocation + 1;
            }
            System.out.println("PUT " + obj);
            return true;
        }
        return false;
    }

    synchronized Object get() {
        if (amountOfData == 0) {
            return null;
        }
        int currentGet = nextGetLocation;
        if (nextGetLocation == 99) {
            nextGetLocation = 0;
        } else {
            nextGetLocation = nextGetLocation + 1;
        }
        amountOfData = amountOfData - 1;
        return array[currentGet];
    }
}
//