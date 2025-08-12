package controller;

import fungorium.Insect;
import fungorium.Mushroom;
import java.util.List;
import view.MushBodyView;
import view.MushThreadView;
import view.SporeView;
import view.TectonView;

/**
 * Adatátviteli objektum a játék állapotának mentéséhez és továbbításához.
 * Tartalmazza a játék aktuális elemeinek listáit és a kör számát.
 */
public class GameStateDTO {
    public List<TectonView> tectons;
    public List<MushThreadView> threads;
    public List<MushBodyView> mushBodies;
    public List<Insect> insects;
    public List<SporeView> spores;
    public int round;
    public List<Mushroom> mushrooms;
}