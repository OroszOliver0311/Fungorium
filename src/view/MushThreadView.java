package view;

import fungorium.MushThread;
import java.awt.*;
import javax.swing.*;

/**
 * Egy Swing komponens, amely egy MushThread objektumot reprezentál vizuálisan egy vonallal.
 * A vonal két pont között jelenik meg, és színe a szál típusát tükrözi.
 */
public class MushThreadView extends JComponent implements Observer {
    private MushThread thread;
    private int pos1X, pos1Y;
    private int pos2X, pos2Y;
    private boolean isSelected = false;
    private final int DEFAULT_THICKNESS = 3;
    private Color threadColor = new Color(139, 69, 19); // Brown color by default

    /**
     * Létrehoz egy MushThreadView példányt, amely a megadott koordináták között jeleníti meg a szálat.
     *
     * @param thread a vizualizált MushThread objektum
     * @param pos1X az első pont x-koordinátája
     * @param pos1Y az első pont y-koordinátája
     * @param pos2X a második pont x-koordinátája
     * @param pos2Y a második pont y-koordinátája
     * @param color a szál színe
     */
    public MushThreadView(MushThread thread, int pos1X, int pos1Y, int pos2X, int pos2Y, Color color) {
        this.pos1X = pos1X;
        this.pos1Y = pos1Y;
        this.pos2X = pos2X;
        this.pos2Y = pos2Y;
        this.threadColor = color;
        this.thread = thread;
        if (thread != null)
            thread.addObserver(this);
    }

    // Method kept for compatibility with interface
    public void loadImage(String path) {
        // Not used - this implementation only draws lines
    }

    /**
     * Meghívódik, ha a megfigyelt objektum állapota megváltozik.
     * Újrarajzolja a komponenst.
     */
    @Override
    public void update() {
        repaint();
    }
    

    /**
     * Kirajzolja a MushThread objektumot vonalként a megadott színnel és pozícióval.
     *
     * @param g a rajzoláshoz használt Graphics objektum
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(threadColor);
        g2.setStroke(new BasicStroke(DEFAULT_THICKNESS));
        
        int dx = pos2X - pos1X;
        int dy = pos2Y - pos1Y;
        
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length == 0) return; // avoid division by zero
        
        double nx = -dy / length;
        double ny = dx / length;
        
        int offsetX = (int) (nx * 5);
        int offsetY = (int) (ny * 5);
        if(threadColor.equals(Color.RED)){
             g2.drawLine(pos1X + offsetX, pos1Y + offsetY, pos2X + offsetX, pos2Y + offsetY);
        } else if (threadColor.equals(Color.BLUE)){
            g2.drawLine(pos1X - offsetX, pos1Y - offsetY, pos2X - offsetX, pos2Y - offsetY);
        } else {
            g2.drawLine(pos1X, pos1Y, pos2X, pos2Y);
        }
        
    }

    /**
     * Visszaadja a jelenleg hozzárendelt MushThread objektumot.
     *
     * @return a MushThread objektum
     */
    public MushThread getThread() {
        return thread;
    }

    /**
     * Beállítja a megjelenítendő MushThread objektumot, eltávolítja a korábbit, és újrarajzolja a komponenst.
     *
     * @param thread az új MushThread objektum
     */
    public void setThread(MushThread thread) {
        // Remove observer from old thread
        if (this.thread != null) {
            try {
                this.thread.removeObserver(this);
            } catch (Exception e) {
                // Handle if removeObserver doesn't exist
            }
        }

        // Set new thread and add observer
        this.thread = thread;
        if (thread != null) {
            thread.addObserver(this);
        }

        // Update the view
        update();
    }

    /**
     * Kiszámítja a legkisebb távolságot a vonal (szál) és egy adott pont között.
     *
     * @param p a vizsgált pont
     * @return a távolság a vonaltól
     */
     public double distanceToPoint(Point p) {
        // Pont és szakasz távolsága (standard képlet)
        double A = p.x - pos1X;
        double B = p.y - pos1Y;
        double C = p.x - pos2X;
        double D = p.y - pos2Y;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = len_sq != 0 ? dot / len_sq : -1;

        double xx, yy;

        if (param < 0) {
            xx = pos1X;
            yy = pos1Y;
        } else if (param > 1) {
            xx = pos2X;
            yy = pos2Y;
        } else {
            xx = pos1X + param * C;
            yy = pos2Y + param * D;
        }

        double dx = p.x - xx;
        double dy = p.y - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Megadja, hogy ki van-e jelölve a MushThreadView.
     *
     * @return true, ha ki van jelölve; különben false
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Beállítja a kijelöltség állapotát, és újrarajzolja a komponenst, ha változás történt.
     *
     * @param selected true, ha ki kell jelölni; false, ha nem
     */
    public void setSelected(boolean selected) {
        if (this.isSelected != selected) {
            this.isSelected = selected;
            repaint();
        }
    }
}