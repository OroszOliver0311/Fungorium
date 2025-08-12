package view;

import fungorium.MushSpore;
import java.awt.*;
import javax.swing.*;

/**
 * Egy Swing komponens, amely egy MushSpore objektumot jelenít meg egy színes pötty formájában.
 * Figyeli a MushSpore változásait és újrarajzolja magát ennek megfelelően.
 */
public class SporeView extends JComponent implements Observer {
    private MushSpore spore;
    private int x,y;
    private Color color = Color.BLACK;
    private boolean isSelected = false;
    private final int DEFAULT_WIDTH = 10;
    private final int DEFAULT_HEIGHT = 10;

    /**
     *Létrehoz egy új SporeView példányt adott pozícióval, színnel és MushSpore objektummal.
     *
     * @param spore a megjelenítendő MushSpore objektum
     * @param x az x pozíció
     * @param y az y pozíció
     * @param color a spóra színe
     */    
    public SporeView(MushSpore spore, int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.spore = spore;
        if(spore != null)
            spore.addObserver(this);
    }

    public void loadImage(String path) {
        //Üres mert csak egy pötty lesz
    }

    /**
     * Értesítés érkezik a megfigyelt objektumtól, újrarajzolja a komponenst.
     */
    @Override
    public void update() {
        repaint();
    }

    /**
     * Kirajzolja a spórát egy kör formájában a megadott színnel és pozícióban.
     *
     * @param g a Graphics objektum, amelyre rajzolni kell
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(color);
        g.fillOval(x + 10, y + 10, DEFAULT_HEIGHT,DEFAULT_WIDTH); 
    }

    /**
     * Visszaadja a hozzárendelt MushSpore objektumot.
     *
     * @return a MushSpore objektum
     */
    public MushSpore getSpore() {
        return spore;
    }

    /**
     * Beállítja a hozzárendelt MushSpore objektumot.
     *
     * @param spore az új MushSpore objektum
     */
    public void setSpore(MushSpore spore) {
        // Remove observer from old spore
        if (this.spore != null) {
            try {
                this.spore.removeObserver(this);
            } catch (Exception e) {
                // Handle if removeObserver doesn't exist
            }
        }
        
        // Set new spore and add observer
        this.spore = spore;
        if (spore != null) {
            spore.addObserver(this);
        }
        
        // Update the view
        update();
    }
    
    /**
     * Megadja, hogy a MushSpore jelenleg ki van-e jelölve.
     *
     * @return true, ha ki van jelölve; különben false
     */
    public boolean isSelected() {
        return isSelected;
    }
    
    /**
     * Beállítja a MushSpore kijelöltségi állapotát.
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