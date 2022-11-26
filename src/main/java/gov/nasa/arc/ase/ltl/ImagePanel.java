package gov.nasa.arc.ase.ltl;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.ScrollPane;

class ImagePanel extends ScrollPane {
    ImageCanvas canvas;
    Image image;

    public ImagePanel() {
        this.canvas = new ImageCanvas(this);
        add(this.canvas);
        setSize(600, 600);
    }

    public void setImage(Image paramImage) {
        this.canvas.setImage(paramImage);
    }

    public void paint(Graphics paramGraphics) {
        this.canvas.repaint();
    }

    public void reduce() {
        this.canvas.reduce();
    }

    public void increase() {
        this.canvas.increase();
    }

    public void setNormal() {
        this.canvas.setNormal();
    }
}
