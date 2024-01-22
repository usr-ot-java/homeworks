package com.otus;

public class Box <N extends Number >{
    public N object;

    public Box(N object) {
        this.object = object;
    }

    public boolean isSame(Box<?> anotherBox) {
        return object.equals(anotherBox);
    }
}
