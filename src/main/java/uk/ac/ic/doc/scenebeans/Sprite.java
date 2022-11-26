package uk.ac.ic.doc.scenebeans;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;

public class Sprite extends SceneGraphBase implements Primitive, ImageObserver {
    private URL _src = null;
    private double _hotspot_x = 0.0D;
    private double _hotspot_y = 0.0D;
    private transient Image _image = null;
    private Shape _last_drawn = null;

    public Shape getShape(Graphics2D paramGraphics2D) {
        if (this._image == null)
            return new Rectangle2D.Double(0.0D, 0.0D, 0.0D, 0.0D);
        return new Rectangle2D.Double(-(this._hotspot_x + 1.0D), -(this._hotspot_y + 1.0D), (this._image.getWidth(this) + 2), (this._image.getHeight(this) + 2));
    }

    public Shape getLastDrawnShape() {
        return this._last_drawn;
    }

    public Image getImage() {
        return this._image;
    }

    public void setSrc(URL paramURL) {
        this._src = paramURL;
        setDirty(true);
        reloadImage();
    }

    public URL getSrc() {
        return this._src;
    }

    public Point2D getHotspot() {
        return new Point2D.Double(this._hotspot_x, this._hotspot_y);
    }

    public void setHotspot(Point2D paramPoint2D) {
        this._hotspot_x = paramPoint2D.getX();
        this._hotspot_y = paramPoint2D.getY();
        setDirty(true);
    }

    public double getHotspotX() {
        return this._hotspot_x;
    }

    public void setHotspotX(double paramDouble) {
        this._hotspot_x = paramDouble;
        setDirty(true);
    }

    public double getHotspotY() {
        return this._hotspot_y;
    }

    public void setHotspotY(double paramDouble) {
        this._hotspot_y = paramDouble;
        setDirty(true);
    }

    public void accept(SceneGraphProcessor paramSceneGraphProcessor) {
        paramSceneGraphProcessor.process(this);
    }

    public void draw(Graphics2D paramGraphics2D) {
        paramGraphics2D.drawImage(this._image, -((int) this._hotspot_x), -((int) this._hotspot_y), null);
        this._last_drawn = getShape(paramGraphics2D);
        setDirty(false);
    }

    public boolean imageUpdate(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
        setDirty(true);
        if ((paramInt1 & 0xC0) != 0) {
            this._image = null;
            return false;
        }
        return ((paramInt1 & 0x20) == 0);
    }

    private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
        paramObjectInputStream.defaultReadObject();
        reloadImage();
    }

    private void reloadImage() {
        if (this._image != null)
            this._image.flush();
        this._image = Toolkit.getDefaultToolkit().createImage(this._src);
        Toolkit.getDefaultToolkit().prepareImage(this._image, -1, -1, this);
    }

    class Hotspot implements PointBehaviourListener {
        private final Sprite this$0;

        Hotspot(Sprite this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(Point2D param1Point2D) {
            this.this$0.setHotspot(param1Point2D);
        }
    }

    public PointBehaviourListener newHotspotAdapter() {
        return new Hotspot(this);
    }

    class HotspotX implements DoubleBehaviourListener {
        private final Sprite this$0;

        HotspotX(Sprite this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setHotspotX(param1Double);
        }
    }

    public DoubleBehaviourListener newHotspotXAdapter() {
        return new HotspotX(this);
    }

    class HotspotY implements DoubleBehaviourListener {
        private final Sprite this$0;

        HotspotY(Sprite this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setHotspotY(param1Double);
        }
    }

    public DoubleBehaviourListener newHotspotYAdapter() {
        return new HotspotY(this);
    }
}
