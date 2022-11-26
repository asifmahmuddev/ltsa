package gov.nasa.arc.ase.ltl;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;

class ImageCanvas extends Canvas {
    Image image = null;
    float degree = 1.0F;
    MediaTracker tracker = new MediaTracker(this);
    int canvasHeight = 600;
    int canvasWidth = 600;
    ImagePanel pane;

    public ImageCanvas(ImagePanel paramImagePanel) {
        this.pane = paramImagePanel;
    }

    public void setImage(Image paramImage) {
        this.image = paramImage;
        this.tracker.addImage(this.image, 0);
    }

    public void reduce() {
        this.degree -= 0.1F;
        if (this.degree < 0.5D)
            this.degree = 0.5F;
    }

    public void increase() {
        this.degree += 0.1F;
        if (this.degree > 2.0F)
            this.degree = 2.0F;
    }

    public void setNormal() {
        this.degree = 1.0F;
    }

    public void paint(Graphics paramGraphics) {
        if (this.image != null) {
            int i = this.image.getWidth(this);
            int j = this.image.getHeight(this);
            int k = Math.round(i * this.degree);
            int m = Math.round(j * this.degree);
            if (k > getWidth() || m > getHeight()) {
                setSize(k + 5, m + 5);
                this.canvasWidth = k + 5;
                this.canvasHeight = m + 5;
            }
            if (this.tracker.statusID(0, false) == 8) {
                paramGraphics.drawImage(this.image, 0, 0, k, m, this);
                this.pane.doLayout();
            }
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(this.canvasWidth, this.canvasHeight);
    }
}
