package fungorium;

import javax.swing.SwingUtilities;
import view.Menu;

/**
 * @author Benkő Orsolya
 * @author Gere Gábor
 * @author Tóth Dominik
 * @author Orosz Olivér
 * @author Horváth Ernő Zoltán
 * @version 1.0
 */
public class App {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Menu());
    }
}
