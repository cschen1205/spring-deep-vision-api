package com.github.cschen1205.objdetect;

import com.github.cschen1205.objdetect.models.DetectedObj;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;


public class ObjectDetectorDemo {
    public static void main(String[] args) throws Exception {
        ObjectDetector detector = new ObjectDetector();

        detector.loadModel();

        BufferedImage img = ImageIO.read(new File("images/test.jpg"));

        List<DetectedObj> result = detector.detectObjects(img);

        System.out.println("There are " + result.size() + " objects detected");
        for(int i=0; i < result.size(); ++i){
            System.out.println("# " + (i + 1) + ": " + result.get(i));
        }
    }








}