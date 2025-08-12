package view;

import fungorium.MushBody;
import java.awt.*;
import java.net.URL;
import javax.swing.*;

/**
 * A gombatest (MushBody) megjelenítéséért felelős Swing komponens.
 * Kép segítségével ábrázolja a gombát a megadott pozícióban, és figyeli annak változásait.
 */
public class MushBodyView extends JComponent implements Observer {
    private MushBody mushBody;
    private int x, y;
    private ImageIcon image;
    private boolean isSelected = false;
    private final int DEFAULT_WIDTH = 25;
    private final int DEFAULT_HEIGHT = 25;

    /**
     * Létrehoz egy MushBodyView példányt a megadott pozícióval és MushBody objektummal.
     *
     * @param mushBody a megjelenítendő MushBody példány
     * @param x az x-koordináta
     * @param y az y-koordináta
     */
    public MushBodyView(MushBody mushBody, int x, int y) {
        this.x = x;
        this.y = y;
        this.mushBody = mushBody;
        if(mushBody != null)
            mushBody.addObserver(this);
    }

    /**
     * Betölti és átméretezi a képet, amely a gombatestet reprezentálja.
     *
     * @param url a kép elérési útvonala az erőforrások között
     */
    public void loadImage(String url) {
        try {
            // Try to load the image from the URL
            URL imageUrl = getClass().getResource(url);
            if (imageUrl != null) {
                image = new ImageIcon(imageUrl);
                // Resize the image if needed
                if (image.getIconWidth() > 0 && image.getIconHeight() > 0) {
                    Image scaledImage = image.getImage().getScaledInstance(
                            DEFAULT_WIDTH, DEFAULT_HEIGHT, Image.SCALE_SMOOTH);
                    image = new ImageIcon(scaledImage);
                }
            } else {
                System.err.println("Image not found: " + url);
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
        }
    }

    /**
     * Frissíti a megjelenítést, amikor a MushBody állapota megváltozik.
     */
    @Override
    public void update() {
        // Update the view based on mushBody properties
        updateAppearance();
        repaint();
    }
    
    
    private void updateAppearance() {
       
    }

    /**
     * Kirajzolja a MushBodyView-t a komponensre, a betöltött képet használva.
     *
     * @param g a rajzoláshoz használt Graphics objektum
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(image.getImage(), x - DEFAULT_HEIGHT/2, y - DEFAULT_WIDTH, this);
    }

    /**
     * Visszaadja az aktuálisan hozzárendelt MushBody példányt.
     *
     * @return a MushBody objektum
     */
    public MushBody getMushBody() {
        return mushBody;
    }

    /**
     * Beállítja az új MushBody példányt, eltávolítja a régit megfigyelésből, és újrarajzolja a nézetet.
     *
     * @param mushBody az új MushBody példány
     */
    public void setMushBody(MushBody mushBody) {
        // Remove observer from old mushBody
        if (this.mushBody != null) {
            try {
                this.mushBody.removeObserver(this);
            } catch (Exception e) {
                // Handle if removeObserver doesn't exist
                
            }
        }
        
        // Set new mushBody and add observer
        this.mushBody = mushBody;
        if (mushBody != null) {
            mushBody.addObserver(this);
        }
        
        // Update the view
        update();
    }
    
    /**
     * Megadja, hogy a komponens ki van-e jelölve.
     *
     * @return true, ha ki van jelölve, különben false
     */
    public boolean isSelected() {
        return isSelected;
    }
    
    /**
     * Beállítja a kijelöltség állapotát, és szükség esetén újrarajzolja a komponenst.
     *
     * @param selected kijelölt állapot
     */
    public void setSelected(boolean selected) {
        if (this.isSelected != selected) {
            this.isSelected = selected;
            repaint();
        }
    }

    
    /**
     * Visszaadja a komponens preferált méretét a betöltött kép mérete alapján.
     *
     * @return a preferált méret
     */
    @Override
    public Dimension getPreferredSize() {
        if (image != null) {
            return new Dimension(image.getIconWidth(), image.getIconHeight());
        }
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}