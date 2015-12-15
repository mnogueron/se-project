package jus.poc.prodcons.v4;

import jus.poc.prodcons.*;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static jus.poc.prodcons.v4.TestProdCons.AnsiColor;

/**
 * Created by matthieu on 06/12/15.
 */
public class ProdCons implements Tampon {
	
	private static Logger LOGGER = Logger.getLogger(ProdCons.class.getName());

    public static void initLogger(){
        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(new TestProdCons.LogConsoleHandler());
    }
	
	private MessageX[] buffer;
	private int in;
	private int out;
	
	private int nbProd;
	private int nbCons;

	private ArrayList<Producteur> prodFinished;
	private ArrayList<Consommateur> consFinished;

	private File fp, fc;
	private Observateur observateur;

	/**
	 * ProdCons constructor 
	 * @param bufferSize
	 */
	public ProdCons(int bufferSize, int nbProd, int nbCons, Observateur observateur) {
		this.observateur = observateur;
		in = 0;
		out = 0;

		prodFinished = new ArrayList<>();
		consFinished = new ArrayList<>();

		this.nbProd = nbProd;
		this.nbCons = nbCons;

		buffer = new MessageX[bufferSize];
		for(int i=0; i<taille(); i++){
			buffer[i] = null;
		}
		fp = new File(taille());
		fc = new File(enAttente());
	}
	
	public void setProductionFinished(Producteur p){
		synchronized (this) {
			prodFinished.add(p);
		}

		if(productionIsFinished()){
			try {
				fc.reveiller(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void setConsommationFinished(Consommateur c){
		synchronized (this){
			consFinished.add(c);
		}

        if(consommationIsFinished()){
			try {
				fp.reveiller(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized boolean productionIsFinished(){
		return prodFinished.size() == nbProd && enAttente() == 0;
	}

	public synchronized boolean consommationIsFinished(){
		return consFinished.size() == nbCons;
	}

	@Override
	public synchronized int enAttente() {
		int nbBusy = 0;
		for(int i=0; i<taille(); i++){
			if (buffer[i] != null){
				nbBusy++;
			}
		}
		return nbBusy;
	}

	@Override
	public Message get(_Consommateur c) throws InterruptedException, ControlException {
		fc.attendre(1);

        if (productionIsFinished()) {
            fc.reveiller(0);
            return null;
        }

		MessageX m;
		synchronized (this){
			m = buffer[out];
            m.removeExemplaire(1);

			observateur.retraitMessage(c, m);

			if(m.isEmpty()) {
				buffer[out] = null;
				out = (out + 1) % taille();

                fc.removeBlocked(m);
                fp.removeBlocked(m);
			}
            else{
                fc.addBlocked(m, Thread.currentThread().getId());
            }

			LOGGER.log(Level.INFO, "{0}[{1}] \tconsumes: \t\t{2}{3}",
                    new Object[]{AnsiColor.CYAN, c.identification(), m, AnsiColor.RESET});
		}

        fp.reveiller(1);
        return m;
	}

	@Override
	public void put(_Producteur p, Message m) throws InterruptedException, ControlException {
		fp.attendre(((MessageX)m).getNbExemplaire());

        if(consommationIsFinished()){
            fp.reveiller(0);
            return;
        }

		synchronized (this) {
			buffer[in] = (MessageX)m;

			for(int i=0; i<((MessageX) m).getNbExemplaire(); i++){
				observateur.depotMessage(p, m);
			}
			
			in = (in + 1) % taille();
            LOGGER.log(Level.INFO, "{0}[{1}] \tproduces: \t\t{2}   \t[{3}]{4}",
                    new Object[]{AnsiColor.PURPLE, p.identification(), m, ((MessageX) m).getNbExemplaire(),
                            AnsiColor.RESET});
			fp.addBlocked((MessageX) m, Thread.currentThread().getId());
		}

		fc.reveiller(((MessageX) m).getNbExemplaire());
	}

	@Override
	public int taille() {
		return buffer.length;
	}

	private class File{

		private int residu;
        private ConcurrentHashMap<MessageX, ArrayList<Long>> idBlocked;

		public File(int residu){
			this.residu = residu;
            idBlocked = new ConcurrentHashMap<>();
		}

		public synchronized void attendre(int nbExemplaire) throws InterruptedException {
			while(residu == 0 || isBlocked()){
                if(ProdCons.this.productionIsFinished() || ProdCons.this.consommationIsFinished()){
                    notifyAll();
					return;
				}
				wait();
			}
            residu -= nbExemplaire;
		}

		public synchronized void reveiller(int nbExemplaire) throws InterruptedException {
            notifyAll();
            residu += nbExemplaire;
		}

        public boolean isBlocked(){
            boolean isBlocked = false;
            for(ArrayList<Long> al : idBlocked.values()){
                isBlocked = isBlocked || al.contains(Thread.currentThread().getId());
            }
            return isBlocked;
        }

        public void addBlocked(MessageX m, long id){
            //LOGGER.info("\t\t\tAdd "+id+" for "+m.toString() + " " + m.isEmpty());
            if (idBlocked.containsKey(m)) {
                idBlocked.get(m).add(id);
            } else {
                ArrayList<Long> ids = new ArrayList<>();
                ids.add(id);
                idBlocked.put(m, ids);
            }
        }

        public void removeBlocked(MessageX m){
            //LOGGER.info("\t\t\tRemove "+m.toString());
            idBlocked.remove(m);
        }
	}
}
