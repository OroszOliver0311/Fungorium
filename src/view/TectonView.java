package view;

import fungorium.Tecton;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import javax.swing.*;


/**
 * Egy Swing komponens, amely egy Tecton objektum vizuális nézetét reprezentálja.
 * Figyeli a Tecton állapotváltozásait, és ennek megfelelően frissíti a nézetet.
 */
public class TectonView extends JComponent implements Observer {
    private int x, y;               //Pozíció középpontja
    private final int size = 90;    //Kép mérete
    private Tecton tecton;          //Hozzá tartozó Tecton
    private ImageIcon image;        //Kép
    private boolean isSelected = false;

    /**
     * Létrehoz egy TectonView példányt adott pozícióval és hozzárendelt Tecton objektummal.
     *
     * @param x a középpont x-koordinátája
     * @param y a középpont y-koordinátája
     * @param tecton a hozzárendelt Tecton objektum
     */
    public TectonView(int x, int y, Tecton tecton) {
        this.x = x;
        this.y = y;
        this.tecton = tecton;
        if(tecton != null){
            tecton.addObserver(this);
            loadImage();
        }
    }

    /**
     * Betölti a Tecton típusának megfelelő képet.
     * A típustól függően választja ki a megfelelő képfájlt.
     */
    public void loadImage(){
        String type = tecton.getType();
        URL imgUrl;
        switch (type) {
            case "MULTITECTON" -> imgUrl = getClass().getResource("multi_circle.png");
            case "DRYTECTON" -> imgUrl = getClass().getResource("dry_circle.png");
            case "ZEROTECTON" -> imgUrl = getClass().getResource("zero_circle.png");
            case "NUTRITECTON" -> imgUrl = getClass().getResource("nutri_circle.png");
            case "SOLOTECTON" -> imgUrl = getClass().getResource("solo_circle.png");
            default -> { System.err.println("Wrong tecton type!");
                return;
            }
        }

        if (imgUrl == null) {
            System.err.println("Rossz az útvonal!");
        } else {
            image = new ImageIcon(imgUrl);
        } 
    }

    /**
     * Kirajzolja a komponenst, beleértve a Tecton képét és a kijelölést, ha szükséges.
     *
     * @param g a rajzoláshoz használt Graphics objektum
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image _image = image.getImage();
        if(isSelected)
            g.drawImage(_image, x - size/2 -5, y - size/2 -5, size + 10, size + 10, this);
        else
            g.drawImage(_image, x - size/2, y - size/2, size, size, this);
    }

    /**
     * Meghívódik, amikor a megfigyelt Tecton objektum frissül.
     * Újrarajzolja a nézetet.
     */
    @Override
    public void update() {
        if(isSelected) setSelected(false);
        //Ha törik a pozíciója kicsit menjen arrébb, szomszédoktól függően
            //Hozzon létre egy új TektonView-t
            //Adjon Edge-ket az új TektonViewhoz, majd törölje az átadottakat
            //Húzzon egy Edge-t az új TektonView és saját maga közé
    }

    //Getterek, Setterek

    /**
     * Visszaadja az x-koordinátát.
     *
     * @return az x pozíció
     */
    public int getX() {
        return x;
    }

    /**
     * Visszaadja az y-koordinátát.
     *
     * @return az y pozíció
     */
    public int getY() {
        return y;
    }

    /**
     * Visszaadja a hozzárendelt Tecton objektumot.
     *
     * @return a Tecton objektum
     */
    public Tecton getTecton() {
        return tecton;
    }

    /**
     * Beállítja az x-koordinátát.
     *
     * @param x az új x pozíció
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Beállítja az y-koordinátát.
     *
     * @param y az új y pozíció
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Beállítja a hozzárendelt Tecton objektumot.
     *
     * @param tecton az új Tecton objektum
     */
    public void setTecton(Tecton tecton) {
        this.tecton = tecton;
    }

    /**
     * Megadja, hogy a Tecton jelenleg ki van-e jelölve.
     *
     * @return true, ha ki van jelölve; különben false
     */
    public boolean isSelected() {
        return isSelected;
    }
    
    /**
     * Beállítja a Tecton kijelöltségi állapotát.
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