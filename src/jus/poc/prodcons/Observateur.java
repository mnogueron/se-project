package jus.poc.prodcons;

import jus.poc.prodcons.*;

/**
 * Created by matthieu on 14/12/15.
 */
public final class Observateur {

    private boolean coherent = false;

    public boolean coherent() {
        return this.coherent;
    }

    public Observateur() {
    }

    public void init(int nbproducteurs, int nbconsommateurs, int nbBuffers) throws ControlException {
        if(nbproducteurs > 0 && nbconsommateurs > 0 && nbBuffers > 0) {
            this.coherent = true;
        } else {
            throw new ControlException(this.getClass(), "init");
        }
    }

    public final synchronized void productionMessage(_Producteur p, Message m, int tempsDeTraitement) throws ControlException {
        if(p != null && m != null && tempsDeTraitement > 0 && this.coherent()) {

        } else {
            throw new ControlException(this.getClass(), "productionMessage");
        }
    }

    public final synchronized void depotMessage(_Producteur p, Message m) throws ControlException {
        if(p != null && m != null && this.coherent()) {

        } else {
            throw new ControlException(this.getClass(), "depotMessage");
        }
    }

    public final synchronized void retraitMessage(_Consommateur c, Message m) throws ControlException {
        if(c != null && m != null && this.coherent()) {

        } else {
            throw new ControlException(this.getClass(), "retraitMessage");
        }
    }

    public final synchronized void consommationMessage(_Consommateur c, Message m, int tempsDeTraitement) throws ControlException {
        if(c != null && m != null && this.coherent()) {

        } else {
            throw new ControlException(this.getClass(), "consommationMessage");
        }
    }

    public final synchronized void newProducteur(_Producteur p) throws ControlException {
        if(p != null && this.coherent()) {

        } else {
            throw new ControlException(this.getClass(), "newProducteur");
        }
    }

    public final synchronized void newConsommateur(_Consommateur c) throws ControlException {
        if(c != null && this.coherent()) {

        } else {
            throw new ControlException(this.getClass(), "newConsommateur");
        }
    }
}
