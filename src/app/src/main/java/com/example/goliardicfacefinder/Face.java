package com.example.goliardicfacefinder;

import java.util.List;

class Face {
    public int leftX, topY, rightX, bottomY;
    public String AgeClass;
    public double Age;
    public double AgeClassificationConfidence;
    public Face(int leftX, int topY, int rightX, int bottomY, String AgeClass, double Age, double AgeClassificationConfidence){
        this.leftX = leftX;
        this.topY = topY;
        this.rightX = rightX;
        this.bottomY = bottomY;
        this.AgeClass =AgeClass;
        this.Age = Age;
        this.AgeClassificationConfidence=AgeClassificationConfidence;
    }
}