package jus.poc.prodcons;

/**
 * Created by matthieu on 06/12/15.
 */
public class Observateur {

    private boolean coherent;
    private static String ControlClass;

    private boolean operationnel;

    public Observateur(){

    }

    public boolean coherent(){
        return coherent;
    }

    public void consommationMessage(_Consommateur c, Message m, int tempsDeTraitement){

    }

    public void depotMessage(_Producteur p, Message m){

    }

    public void init(int nbproducteurs, int nbconsommateurs, int nbBuffers){

    }

    public void newConsommateur(_Consommateur c){

    }

    public void newProducteur(_Producteur p){

    }

    public void productionMessage(_Producteur p, Message m, int tempsDeTraitement){

    }

    public void retraitMessage(_Consommateur c, Message m){

    }
}
