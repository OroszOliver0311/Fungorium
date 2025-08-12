package view;

import fungorium.Insect;
import java.awt.*;
import java.net.URL;
import javax.swing.*;

/**
 * InsectView egy grafikus komponens, amely egy Insect objektumot jelenít meg,
 * és figyeli annak állapotát a megjelenítés frissítése érdekében.
 */
public class InsectView extends JComponent implements Observer {
    private Insect insect;
    private int x, y;
    private ImageIcon image;
    private boolean isSelected = false;
    private final int DEFAULT_WIDTH = 25;
    private final int DEFAULT_HEIGHT = 25;

    /**
     * Konstruktor, amely létrehoz egy InsectView-t egy adott rovarhoz és pozícióhoz.
     *
     * @param insect Megjelenítendő rovar objektum
     * @param x Az x koordináta a komponens elhelyezéséhez
     * @param y Az y koordináta a komponens elhelyezéséhez
     */
    public InsectView(Insect insect, int x, int y) {
        this.x = x;
        this.y = y;
        this.insect = insect;
        if(insect != null)
            insect.addObserver(this);
    }

    /**
     * Betölti az adott elérési útvonalról származó képet és szükség szerint átméretezi.
     *
     * @param url Az erőforrás elérési útvonala, ahol a kép található
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
     * Az Observer interfész update metódusa, amely értesítést kap az Insect állapotváltozásairól.
     * Frissíti a megjelenést és újrarajzolja a komponenst.
     */
    @Override
    public void update() {
        // Update the view based on insect properties
        updateAppearance();
        repaint();
    }
    
    
    private void updateAppearance() {
        
    }

    /**
     * Megjeleníti a rovar képét a komponens adott koordinátáin.
     *
     * @param g A Graphics objektum, amelyen a rajzolás történik
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        String insectName = insect.getName().substring(0,2);
        if(insectName.equals("i0")){
            g.drawImage(image.getImage(), x - 3*DEFAULT_HEIGHT/2, y - DEFAULT_WIDTH/2, this);
        } else if (insectName.equals("i1")) {
            g.drawImage(image.getImage(), x + DEFAULT_HEIGHT/2, y - DEFAULT_WIDTH/2, this);
        } else {
            g.drawImage(image.getImage(), x - DEFAULT_HEIGHT/2, y - DEFAULT_WIDTH/4, this);
        }
    }

    /**
     * Visszaadja az aktuálisan megjelenített Insect objektumot.
     *
     * @return Az Insect objektum
     */
    public Insect getInsect() {
        return insect;
    }

    /**
     * Beállít egy új Insect objektumot a nézethez, eltávolítva a megfigyelőt a régi objektumról,
     * és feliratkozik az újra, majd frissíti a nézetet.
     *
     * @param insect Az új Insect objektum
     */
    public void setInsect(Insect insect) {
        // Remove observer from old insect
        if (this.insect != null) {
            // If not, you'll need to handle this differently
            try {
                this.insect.removeObserver(this);
            } catch (Exception e) {
                // Handle if removeObserver doesn't exist
            }
        }
        
        // Set new insect and add observer
        this.insect = insect;
        if (insect != null) {
            insect.addObserver(this);
        }
        
        // Update the view
        update();
    }
    
    /**
     * Lekérdezi, hogy a komponens ki van-e jelölve.
     *
     * @return true, ha ki van jelölve; különben false
     */
    public boolean isSelected() {
        return isSelected;
    }
    
    /**
     * Beállítja a komponens kijelöltségét.
     *
     * @param selected Az új kijelöltségi állapot
     */
    public void setSelected(boolean selected) {
        if (this.isSelected != selected) {
            this.isSelected = selected;
            repaint();
        }
    }
    
    /**
     * Visszaadja a komponens preferált méretét, amely az aktuális kép mérete vagy az alapértelmezett méret.
     *
     * @return A komponens preferált mérete
     */
    @Override
    public Dimension getPreferredSize() {
        if (image != null) {
            return new Dimension(image.getIconWidth(), image.getIconHeight());
        }
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}