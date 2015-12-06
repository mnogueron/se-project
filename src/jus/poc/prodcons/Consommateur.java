package jus.poc.prodcons;

/**
 * Created by matthieu on 06/12/15.
 */
public class Consommateur extends Acteur implements _Consommateur {

    @Override
    public void run() {

    }

    @Override
    public int deviationTempsDeTraitement() {
        return 0;
    }

    @Override
    public int identification() {
        return 0;
    }

    @Override
    public int moyenneTempsDeTraitement() {
        return 0;
    }

    @Override
    public int nombreDeMessages() {
        return 0;
    }
}
