package jus.poc.prodcons;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by matthieu on 14/12/15.
 */
public final class Observateur {

    private boolean coherent = false;
    private int nbBuffers;
    private int nbproducteurs;
    private int nbconsommateurs;

    private final String traceName = "trace.txt";

    /**
     *  Enumère les différents types d'evenements
     */
    private enum EventType {
        PRODUCTION,
        DEPOT,
        RETRAIT,
        CONSOMMATION,

        ADD_PROD,
        ADD_CONS,
        INIT;
    }

    /**
     *  Objet Event permettant de stocker un évenement et ses attributs
     */
    private class Event{
        public EventType eventType;
        public _Acteur acteur;
        public Message message;

        public Event(EventType eventType, _Acteur acteur, Message message){
            this.eventType = eventType;
            this.acteur = acteur;
            this.message = message;
        }
    }

    private ArrayList<Event> listOfEvents;
    private ArrayList<_Consommateur> listOfConsommateur;
    private ArrayList<_Producteur> listOfProducteur;

    private ArrayList<String> producedMessages;
    private ArrayList<String> deposedMessages;

    private ArrayList<String> removedMessages;
    private ArrayList<String> consumedMessages;

    public boolean coherent() {
        return this.coherent;
    }

    public Observateur() {
    }

    /**
     *  Dans cette version, nous crééons des listes permettant de conserver différents types d'informations
     *
     * @param nbproducteurs
     * @param nbconsommateurs
     * @param nbBuffers
     * @throws ControlException
     */
    public void init(int nbproducteurs, int nbconsommateurs, int nbBuffers) throws ControlException {
        if(nbproducteurs > 0 && nbconsommateurs > 0 && nbBuffers > 0) {
            this.coherent = true;
            this.nbBuffers = nbBuffers;
            this.nbproducteurs = nbproducteurs;
            this.nbconsommateurs = nbconsommateurs;

            listOfEvents = new ArrayList<>();
            listOfConsommateur = new ArrayList<>();
            listOfProducteur = new ArrayList<>();

            producedMessages = new ArrayList<>();
            deposedMessages = new ArrayList<>();

            removedMessages = new ArrayList<>();
            consumedMessages = new ArrayList<>();

            Event e = new Event(EventType.INIT, null, null);
            listOfEvents.add(e);
            writeTraceLog(e);
        } else {
            throw new ControlException(this.getClass(), "init");
        }
    }

    /**
     *  Dans cette version nous rajoutons un test permettant de chercher si un Producteur est bien connu de l'observateur
     *  et n'est pas intervenu sans être repertorié
     *
     *  On garde en mémoire le message dans les messages produits pour la suite des tests
     *
     * @param p
     * @param m
     * @param tempsDeTraitement
     * @throws ControlException
     */
    public final synchronized void productionMessage(_Producteur p, Message m, int tempsDeTraitement) throws ControlException {
        if(p != null && m != null && tempsDeTraitement > 0 && this.coherent() && listOfProducteur.contains(p)) {
            producedMessages.add(m.toString());

            Event e = new Event(EventType.PRODUCTION, p, m);
            listOfEvents.add(e);
            writeTraceLog(e);
        } else {
            throw new ControlException(this.getClass(), "productionMessage");
        }
    }

    /**
     *  Dans cette version nous rajoutons un test permettant de chercher si un producteur est bien connu de l'observateur,
     *  et en plus de cela nous cherchons si le message à déposer a bien été produit au préalable (cohérence)
     *
     *  On garde en mémoire le message dans les messages déposés pour la suite des tests
     *  Nous supprimons aussi le message des messages produit afin d'empêcher le dépôt multiple d'un même message.
     *
     * @param p
     * @param m
     * @throws ControlException
     */
    public final synchronized void depotMessage(_Producteur p, Message m) throws ControlException {
        if(p != null && m != null && this.coherent() && listOfProducteur.contains(p) && producedMessages.contains(m.toString())
                && deposedMessages.size() < nbBuffers) {
            producedMessages.remove(producedMessages.indexOf(m.toString()));
            deposedMessages.add(m.toString());

            Event e = new Event(EventType.DEPOT, p, m);
            listOfEvents.add(e);
            writeTraceLog(e);
        } else {
            throw new ControlException(this.getClass(), "depotMessage");
        }
    }

    /**
     * Dans cette version nous rajoutons un test permettant de chercher si un consommateur est bien connu de l'observateur,
     *  et en plus de cela nous cherchons si le message à retirer a bien été déposé au préalable (cohérence)
     *
     *  On garde en mémoire le message dans les messages retiré pour la suite des tests
     *  Nous supprimons aussi le message des messages déposés afin d'empêcher le retrait multiple d'un même message.
     *
     * @param c
     * @param m
     * @throws ControlException
     */
    public final synchronized void retraitMessage(_Consommateur c, Message m) throws ControlException {
        if(c != null && m != null && this.coherent() && listOfConsommateur.contains(c)
                && deposedMessages.contains(m.toString())) {
            deposedMessages.remove(deposedMessages.indexOf(m.toString()));
            removedMessages.add(m.toString());

            Event e = new Event(EventType.RETRAIT, c, m);
            listOfEvents.add(e);
            writeTraceLog(e);
        } else {
            throw new ControlException(this.getClass(), "retraitMessage");
        }
    }

    /**
     * Dans cette version nous rajoutons un test permettant de chercher si un consommateur est bien connu de l'observateur,
     *  et en plus de cela nous cherchons si le message à consommer a bien été retiré au préalable (cohérence)
     *
     *  On garde en mémoire le message dans les messages consommé pour la suite des tests
     *  Nous supprimons aussi le message des messages retirés afin d'empêcher la consommation multiple d'un même message.
     *
     *  Ceci peux poser problème pour la v4 étant donné qu'on ajoute un message de taille X une fois, or consommationMessage est appelé à chaque
     *  fois qu'un consommateur consomme, donc il est possible qu'on retire plus d'une fois le même message des messages retirés. De cette façon
     *  retraitMessage doit être appelé à chaque fois qu'un consommateur retire un message, même si le message n'est pas supprimé du buffer.
     *
     *  Ce qui fait que le message sera présent X fois dans les messages retirés et ce qui permettra de valider la possibilité de retirer X fois le même message.
     *
     *  Ce problème se propage aux autres fonctions étant donné que chacune de ces fonctions sont dépendentes des précédentes.
     *
     * @param c
     * @param m
     * @param tempsDeTraitement
     * @throws ControlException
     */
    public final synchronized void consommationMessage(_Consommateur c, Message m, int tempsDeTraitement) throws ControlException {
        if(c != null && m != null && tempsDeTraitement > 0 && this.coherent() && listOfConsommateur.contains(c)
                && removedMessages.contains(m.toString())) {
            removedMessages.remove(removedMessages.indexOf(m.toString()));
            consumedMessages.add(m.toString());

            Event e = new Event(EventType.CONSOMMATION, c, m);
            listOfEvents.add(e);
            writeTraceLog(e);
        } else {
            throw new ControlException(this.getClass(), "consommationMessage");
        }
    }

    /**
     *  Dans cette version on garde en mémoire les producteur s'enregistrant
     * @param p
     * @throws ControlException
     */
    public final synchronized void newProducteur(_Producteur p) throws ControlException {
        if(p != null && this.coherent() && listOfProducteur.size() < nbproducteurs) {
            listOfProducteur.add(p);

            Event e = new Event(EventType.ADD_PROD, p, null);
            listOfEvents.add(e);
            writeTraceLog(e);
        } else {
            throw new ControlException(this.getClass(), "newProducteur");
        }
    }

    /**
     *  Dans cette version on garde en mémoire les consommateurs s'enregistrant
     * @param c
     * @throws ControlException
     */
    public final synchronized void newConsommateur(_Consommateur c) throws ControlException {
        if(c != null && this.coherent() && listOfConsommateur.size() < nbconsommateurs) {
            listOfConsommateur.add(c);

            Event e = new Event(EventType.ADD_CONS, c, null);
            listOfEvents.add(e);
            writeTraceLog(e);
        } else {
            throw new ControlException(this.getClass(), "newConsommateur");
        }
    }

    private final void writeTraceLog(Event e){
        /*File f = new File(traceName);
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(f, true)));
            pw.println(e.eventType+"\t\t"+((e.acteur != null)?e.acteur.identification():"")+"\t"
                    +((e.message != null)?e.message:""));
            pw.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }*/
    }
}
