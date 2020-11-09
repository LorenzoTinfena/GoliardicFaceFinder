package com.example.goliardicfacefinder;

import java.util.List;

class ApiResponde{
    public List<Face> facesAges;
}
class Face {
    public int leftX, topY, rightX, bottomY;
    public String AgeClass;
    public int Age;
    public String gender;
    public double GenderClassificationConfidence, AgeClassificationConfidence;
}