package controller;

import fungorium.Tecton;

public interface NewTectonListener {
    void onNewTectonAdded(Tecton orginal, Tecton newTecton);
}
