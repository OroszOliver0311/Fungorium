package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.*;
import view.GameBoardView;
import view.InfoBoardView;

/**
 * A játék főablaka, amely tartalmazza a játékmezőt és az információs panelt.
 */
public class GameWindow extends JFrame {
    private int screenH;
    private int screenW;

    private static JPanel menu;
    private static GameBoardView gameBoard;
    private static InfoBoardView infoBoard;

    /**
     * Konstruktor, amely létrehozza a játékablakot a megadott méretekkel és komponensekkel.
     *
     * @param screenH a képernyő magassága (pixelben)
     * @param screenW a képernyő szélessége (pixelben)
     * @param gbv     a játékmező nézete (GameBoardView)
     * @param ibv     az információs panel nézete (InfoBoardView)
     */
    public GameWindow(int screenH, int screenW, GameBoardView gbv, InfoBoardView ibv) {
        this.screenH = screenH;
        this.screenW = screenW;

        gameBoard = gbv;

        infoBoard = ibv;
        ibv.setBackground(Color.GRAY);

        setTitle("Fungorium");
        setPreferredSize(new Dimension(screenH, screenW));
        setLayout(new BorderLayout());
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.add(gameBoard, BorderLayout.CENTER);
        this.add(infoBoard, BorderLayout.EAST);
    }

    /**
     * Beállítja az új játékmező nézetet (GameBoardView).
     *
     * @param gb az új játékmező nézet
     */
    public void addGameBoard(GameBoardView gb) {
        gameBoard = gb;
    }
}
