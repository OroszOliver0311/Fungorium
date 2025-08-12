package view;

import controller.NewTectonListener;
import fungorium.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import view.GameBoardView.Edge;

/**
 * A játék tábla megjelenítéséért és kirajzolásáért felelős JPanel.
 * Megjeleníti a Tectonokat, Mushroom testeket, rovarokat, spórákat és az őket összekötő éleket.
 * Az osztály megfigyelőként (Observer) viselkedik a játék motor (GameEngine) változásaira,
 * és kezeli az új Tectonok létrejöttét a megjelenítésben is.
 */
public class GameBoardView extends JPanel implements Observer, NewTectonListener {
    private List<TectonView> tectonViews = new ArrayList<>();
    private List<MushBodyView> mushBodyViews = new ArrayList<>();
    private List<InsectView> insectViews = new ArrayList<>();
    private List<SporeView> sporeViews = new ArrayList<>();
    private List<MushThreadView> mushThreadViews = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>(); // Szomszédossági éleket tartalmazó lista
    private GameEngine engine;

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 780;
    private static final int GRID_SIZE = 7; // 7x7 grid => max 49 node
    private static final int CELL_WIDTH = WIDTH / GRID_SIZE;
    private static final int CELL_HEIGHT = HEIGHT / GRID_SIZE;
    private static final int MAX_DISTANCE = (CELL_HEIGHT + CELL_WIDTH + 120) / 2;

    /**
     * Konstruktor, amely létrehozza a GameBoardView-t a megadott GameEngine alapján.
     * Feliratkozik az engine-re observerként és új Tecton esemény figyelőként,
     * valamint inicializálja a grafikus elemeket és a véletlenszerű gráfot.
     * 
     * @param engine A játék motor, amely az adatokat szolgáltatja.
     */
    public GameBoardView(GameEngine engine) {
        this.engine = engine;
        engine.addObserver(this);
        engine.addNewTectonListener(this);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        // View-k létrehozása
        generateRandomGraph();
        createViews();

        // Panel tulajdonságok
        setFocusable(true);
        setRequestFocusEnabled(true);
        setLayout(null);
    }

    /**
     * Létrehozza az összes grafikus nézetet (view-t), amely a gameboardon megjelenik:
     * - TectonView-k
     * - MushBodyView-k
     * - InsectView-k
     * - MushThreadView-k
     * - SporeView-k
     */
    public void createViews() {
        // TectonView-k létrehozása
        mushBodyViews.removeAll(mushBodyViews);

        // MushBodyView-k létrehozása
        int i = 1;
        for (Mushroom mushroom : engine.getMushrooms()) {
            for (MushBody mb : mushroom.getMushBodies()) {
                TectonView location = findTectonView(mb.getLocation());
                MushBodyView bodyView = new MushBodyView(mb, location.getX(), location.getY());
                bodyView.loadImage("mushroom_" + i + ".png");
                mushBodyViews.add(bodyView);
            }
            i++;
        }

        insectViews.removeAll(insectViews);
        // InsectView-k létrehozása
        for (Insect insect : engine.getInsects()) {
            TectonView location = findTectonView(insect.getPosition());
            InsectView insectView = new InsectView(insect, location.getX(), location.getY());
            if (insect.getName().charAt(1) == '1') {
                insectView.loadImage("insect_1.png");
            } else if (insect.getName().charAt(1) == '2') {
                insectView.loadImage("insect_2.png");
            } else {
                insectView.loadImage("insect_3.png");
            }

            insectViews.add(insectView);
        }

        //ThreadViewk
        mushThreadViews.removeAll(mushThreadViews);
        for(Tecton tecton : engine.getTectons()){
            for(MushThread thread : tecton.getThreads()){
                TectonView locationStart = findTectonView(thread.getStart());
                TectonView locationEnd = findTectonView(thread.getEnd());
                Color color;
                if(thread.getMushBody().getName().substring(0, 2).equals("m0")){
                    color = Color.BLUE; 
                }else if(thread.getMushBody().getName().substring(0, 2).equals("m1")){
                    color = Color.RED;
                }else{
                    color = Color.GREEN;
                }
                MushThreadView threadView = new MushThreadView(thread, locationStart.getX(), locationStart.getY(), locationEnd.getX(), locationEnd.getY(),color);
                mushThreadViews.add(threadView);
            }
        }

        //SporeViewk
        sporeViews.removeAll(sporeViews);
        for(Tecton tecton : engine.getTectons()){
            for(MushSpore spore : tecton.getSpores()){
                TectonView location = findTectonView(tecton);
                SporeView sporeView = new SporeView(spore,location.getX(),location.getY(),Color.ORANGE);
                sporeViews.add(sporeView);
            }
        }
    }

    /**
     * Véletlenszerűen generál egy síkbarajzolható gráfot a Tectonok pozícióinak meghatározásához.
     * A gráf csúcsai a TectonView-k, élei pedig az egymáshoz közeli csúcsok közötti kapcsolatok.
     */
    private void generateRandomGraph() {
        Random rand = new Random();
        List<Tecton> tectons = engine.getTectons();

        // 1. Véletlen node-ok
        int idx = 0;
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int cellX = col * CELL_WIDTH;
                int cellY = row * CELL_HEIGHT;

                // Pozíció a cellán belül, minimális margóval
                int margin = 33;
                int x = cellX + margin + rand.nextInt(CELL_WIDTH - 2 * margin);
                int y = cellY + margin + rand.nextInt(CELL_HEIGHT - 2 * margin);
                if (idx < tectons.size()) {
                    TectonView tv = new TectonView(x, y, tectons.get(idx));
                    tectonViews.add(tv);
                    sporeViews.add(new SporeView(new StunSpore(10), x, y, Color.RED));
                    idx++;
                }
            }
        }

        // 2. Él-generálás közelség alapján
        for (int i = 0; i < tectonViews.size(); i++) {
            TectonView a = tectonViews.get(i);
            for (int j = i + 1; j < tectonViews.size(); j++) {
                TectonView b = tectonViews.get(j);
                double dist = Math.hypot(a.getX() - b.getX(), a.getY() - b.getY());
                if (dist <= MAX_DISTANCE) {
                    Edge newEdge = new Edge(a, b);
                    if (!intersect(newEdge)) { // Ha nem metsz egy korábbi élt sem
                        edges.add(new Edge(a, b));
                        a.getTecton().addNeighbor(b.getTecton()); // Szomszédosság beállítása a két tekton között
                    }
                }
            }
        }
    }

    /**
     * Megkeresi a TectonView-t, amelyhez a megadott Tecton tartozik.
     * 
     * @param t A keresett Tecton objektum.
     * @return A hozzá tartozó TectonView, vagy null ha nem található.
     */
    public TectonView findTectonView(Tecton t) {
        for (TectonView tw : tectonViews) {
            if (tw.getTecton().equals(t))
                return tw;
        }
        return null;
    }

    /**
     * A JPanel komponensek kirajzolása.
     * Kirajzolja a gráf éleit, valamint az összes megjelenítendő elemet:
     * Tectonokat, Mushroom testeket, rovarokat, spórákat és szálakat.
     * 
     * @param g A Graphics objektum, amire rajzolni kell.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Szomszédossági élek kirajzolása
        g.setColor(Color.GRAY);
        for (Edge edge : edges) {
            g.drawLine(edge.a.getX(), edge.a.getY(), edge.b.getX(), edge.b.getY());
        }

        // MushThreadView-k kirajzolása
        for (MushThreadView mtv : mushThreadViews) {
            mtv.paintComponent(g);
        }

        // Tectonok kirajzolása
        for (TectonView tv : tectonViews) {
            tv.paintComponent(g);
        }

        // MushBody-k kirajzolása
        for (MushBodyView mbv : mushBodyViews) {
            mbv.paintComponent(g);
        }

        // InsectView-k kirajzolása
        for (InsectView iv : insectViews) {
            iv.paintComponent(g);
        }

        // SporeView-k kirajzolása
        for (SporeView sv : sporeViews) {
            sv.paintComponent(g);
        }

    }

    /**
     * Új Tecton hozzáadásakor frissíti a gráfot, új TectonView-t hoz létre,
     * és módosítja a kapcsolódó éleket és szomszédokat a modellben és a nézetben.
     * 
     * @param original Az eredeti Tecton, amelyből új Tecton jön létre.
     * @param newTecton Az újonnan létrehozott Tecton.
     */
    @Override
    public void onNewTectonAdded(Tecton original, Tecton newTecton) {
        TectonView originalTectonView = null;
        for (TectonView tv : tectonViews){
            if (tv.getTecton().equals(original))
                originalTectonView = tv;
        }

        //Melyik szomszédossági élek tartoznak az eredeti TectonViewhoz
        List<Edge> originalEdges = new ArrayList<>();  //Itt az a az original
        for (Edge e : edges){
            if(e.a.equals(originalTectonView))
                originalEdges.add(e);
            if(e.b.equals(originalTectonView)){
                TectonView tmp = e.a;
                e.a = e.b;
                e.b = tmp;
                originalEdges.add(e);
            }
                
        }

        int x = originalTectonView.getX();
        int y = originalTectonView.getY();
        int offsetX = 0;
        int offsetY = 24;

        List<Edge> closerToOriginal = new ArrayList<>();
        List<Edge> closerToOffset = new ArrayList<>();

        for (Edge edge : originalEdges) {
            double distToOriginal = Math.hypot(edge.b.getX() - (x - offsetX), edge.b.getY() - (y - offsetY));
            double distToOffset = Math.hypot(edge.b.getX() - (x + offsetX), edge.b.getY() - (y + offsetY));

            if (distToOriginal <= distToOffset) {
                closerToOriginal.add(edge);
            } else {
                closerToOffset.add(edge);
            }
        }

        // Új tectonView létrehozása
        TectonView newTectonView = new TectonView(x + offsetX, y + offsetY, newTecton);
        tectonViews.add(newTectonView);
        for(Edge e : closerToOffset){
            e.b.getTecton().removeNeighbor(original);   // Modellbeli szomszédok elvétele az eredeti Tectontól
            e.b.getTecton().addNeighbor(newTecton);     // Modellbeli szomszédok hozzáadása az új tektonhoz
            //e.a = newTectonView;                        //View-hoz új tectonView végpont hozzáadása
            Edge newEdge = new Edge(newTectonView, e.b);
            edges.remove(e);
            edges.add(newEdge);
        }

        // Az eredeti TectoView koordinátáinak eltolása
        originalTectonView.setX(x - offsetX);
        originalTectonView.setY(y - offsetY);

        // Két TectonView közötti Edge létrehozása
        edges.add(new Edge(originalTectonView, newTectonView));
        createViews();
        repaint();
    }

    /**
     * Segédosztály, amely a gráf éleit reprezentálja két TectonView között.
     */
    static class Edge {
        TectonView a, b;

        Edge(TectonView a, TectonView b) {
            this.a = a;
            this.b = b;
        }
    }

    // Élek rajzolásához szükséges metódusok és segédosztályok

    /**
     * Ellenőrzi, hogy az adott él metszi-e a meglévő gráf éleit.
     * 
     * @param edge Vizsgált él.
     * @return true, ha metszi valamelyik meglévő élt, false különben.
     */
    public boolean intersect(Edge edge) {
        for (Edge e : edges) {
            if (edgesIntersect(e, edge))
                return true;
        }
        return false;
    }

    /**
     * Ellenőrzi, hogy két él (Edge) metszi-e egymást.
     * Az éleket kissé levágja a végpontok közelében, hogy elkerülje a csúcsoknál való
     * metszés téves detektálását, majd a levágott szakaszokat vizsgálja.
     * 
     * @param e1 Az első vizsgált él.
     * @param e2 A második vizsgált él.
     * @return true, ha az élek metszik egymást, false különben.
     */
    private boolean edgesIntersect(Edge e1, Edge e2) {
        // Levágott e1 élszakasz
        Point p1 = shortenSegment(e1.a.getX(), e1.a.getY(), e1.b.getX(), e1.b.getY(), 0.1, true);
        Point p2 = shortenSegment(e1.b.getX(), e1.b.getY(), e1.a.getX(), e1.a.getY(), 0.1, true);

        // Levágott e2 élszakasz
        Point q1 = shortenSegment(e2.a.getX(), e2.a.getY(), e2.b.getX(), e2.b.getY(), 0.1, true);
        Point q2 = shortenSegment(e2.b.getX(), e2.b.getY(), e2.a.getX(), e2.a.getY(), 0.1, true);

        return Line2D.linesIntersect(
                p1.x, p1.y, p2.x, p2.y,
                q1.x, q1.y, q2.x, q2.y);
    }

    /**
     * Egy vonalszakaszt levág a végpontjai közelében a metszés vizsgálatához.
     * 
     * @param x1 A szakasz kezdőpontjának X koordinátája.
     * @param y1 A szakasz kezdőpontjának Y koordinátája.
     * @param x2 A szakasz végpontjának X koordinátája.
     * @param y2 A szakasz végpontjának Y koordinátája.
     * @param cut A levágás mértéke (távolság a végponttól).
     * @param forward Ha true, a kezdőpont felől levág; ha false, a végpont felől.
     * @return Az új pont, amely a levágott szakaszon van.
     */
    private Point shortenSegment(double x1, double y1, double x2, double y2, double cut, boolean forward) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double len = Math.sqrt(dx * dx + dy * dy);
        double ratio = cut / len;

        double newX = x1 + (forward ? 1 : -1) * dx * ratio;
        double newY = y1 + (forward ? 1 : -1) * dy * ratio;

        return new Point(newX, newY);
    }

    /**
     * Egyszerű segédosztály, amely egy kétdimenziós pontot reprezentál lebegőpontos koordinátákkal.
     */
    private static class Point {
        double x, y;

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    // Getterek, Setterek

    /**
     * Visszaadja a TectonView objektumok listáját, amelyek a játéktáblán megjelenített tectonokat reprezentálják.
     * 
     * @return a tectonView-k listája
     */
    public List<TectonView> getTectonViews() {
        return tectonViews;
    }

    /**
     * Visszaadja a MushBodyView objektumok listáját, amelyek a megjelenített gombatesteket reprezentálják.
     * 
     * @return a mushBodyView-k listája
     */
    public List<MushBodyView> getMushBodyViews() {
        return mushBodyViews;
    }

    /**
     * Visszaadja az InsectView objektumok listáját, amelyek a megjelenített rovarokat reprezentálják.
     * 
     * @return az insectView-k listája
     */
    public List<InsectView> getInsectViews() {
        return insectViews;
    }

    /**
     * Visszaadja a SporeView objektumok listáját, amelyek a megjelenített spórákat reprezentálják.
     * 
     * @return a sporeView-k listája
     */
    public List<SporeView> getSporeViews() {
        return sporeViews;
    }

    /**
     * Visszaadja a MushThreadView objektumok listáját, amelyek a megjelenített gombafonálokat reprezentálják.
     * 
     * @return a mushThreadView-k listája
     */
    public List<MushThreadView> getMushThreadViews() {
        return mushThreadViews;
    }

    /**
     * Beállítja a megjelenített gombafonálokat reprezentáló MushThreadView objektumok listáját.
     * 
     * @param mushThreadViews az új MushThreadView lista
     */
    public void setMushThreadViews(List<MushThreadView> mushThreadViews) {
        this.mushThreadViews = mushThreadViews;
    }

    /**
     * Beállítja a játéktáblán megjelenített tectonokat reprezentáló TectonView objektumok listáját.
     * 
     * @param tectonViews az új TectonView lista
     */
    public void setTectonViews(List<TectonView> tectonViews) {
        this.tectonViews = tectonViews;
    }

    /**
     * Beállítja a megjelenített gombatesteket reprezentáló MushBodyView objektumok listáját.
     * 
     * @param mushBodyViews az új MushBodyView lista
     */
    public void setMushBodyViews(List<MushBodyView> mushBodyViews) {
        this.mushBodyViews = mushBodyViews;
    }

    /**
     * Beállítja a megjelenített rovarokat reprezentáló InsectView objektumok listáját.
     * 
     * @param insectViews az új InsectView lista
     */
    public void setInsectViews(List<InsectView> insectViews) {
        this.insectViews = insectViews;
    }

    /**
     * Beállítja a megjelenített spórákat reprezentáló SporeView objektumok listáját.
     * 
     * @param sporeViews az új SporeView lista
     */
    public void setSporeViews(List<SporeView> sporeViews) {
        this.sporeViews = sporeViews;
    }

    /**
     * Frissíti a megjelenítést és újrarajzolja a gameboardot.
     * A megfigyelő mintának megfelelően hívódik meg az engine állapotának változásakor.
     */
    @Override
    public void update() {
        createViews();
        repaint();
    }
}