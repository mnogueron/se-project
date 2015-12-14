package jus.poc.prodcons.v6;

import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Message;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import static jus.poc.prodcons.v6.TestProdCons.AnsiColor;

/**
 * Created by matthieu on 06/12/15.
 */
public class ProdCons implements Tampon {

	private static Logger LOGGER = Logger.getLogger(ProdCons.class.getName());

	public static void initLogger(){
		LOGGER.setUseParentHandlers(false);
		LOGGER.addHandler(new TestProdCons.LogConsoleHandler());
	}
	
	private Message[] buffer;
	private int in;
	private int out;
	
	private int nbProd;
	// changed ArrayList to CopyOnWriteArrayList which is thread-safe
	private CopyOnWriteArrayList<Producteur> prodFinished;

	private Observateur observateur;

	private int count;
	// Create a new lock for objective 5
	private Lock lock = new ReentrantLock();
	// Create 2 conditions for objective 5
	private final Condition notEmpty = lock.newCondition();
	private final Condition notFull = lock.newCondition();

	/**
	 * ProdCons constructor
	 * @param bufferSize
	 * @param nbProd
	 * @param observateur
	 */
	public ProdCons(int bufferSize, int nbProd, Observateur observateur) {
		this.observateur = observateur;
		in = 0;
		out = 0;
		prodFinished = new CopyOnWriteArrayList<>();
		this.nbProd = nbProd;
		buffer = new Message[bufferSize];

		for(int i=0; i<taille(); i++){
			buffer[i] = null;
		}
	}

	public void setProductionFinished(Producteur p){
		prodFinished.add(p);
	}
	
	public boolean productionIsFinished(){
		return prodFinished.size() == nbProd && enAttente() == 0;
	}

	@Override
	// added a lock as the method shouldn't be synchronized anymore
	// for objective 5
	public int enAttente() {
		lock.lock();
		int nbBusy = 0;
		try{
			for(int i=0; i<taille(); i++){
				if (buffer[i] != null){
					nbBusy++;
				}
			}
		}
		finally{
			lock.unlock();
		}
		return nbBusy;
	}

	@Override
	public Message get(_Consommateur c) throws InterruptedException, ControlException {
		lock.lock();
		Message m;
		count = enAttente();
        
        try{
	        while(count == 0){
	        	// signal all notEmpty if productionIsFinished
	        	if(productionIsFinished()){
	        		notEmpty.signalAll();
	        		return null;
	        	}
	        	notEmpty.await();
	        }
	        
			m = buffer[out];
			
			/*
			 * add retraitMessage(Consommateur C, Message M)
			 * for objective 3
			 */
			observateur.retraitMessage(c, m);
			
			buffer[out] = null;
			out = (out+1)%taille();
			count--;

			LOGGER.log(Level.INFO, "{0}[{1}] \tconsumes: \t\t{2}{3}",
					new Object[]{AnsiColor.CYAN, c.identification(), m, AnsiColor.RESET});
			
			// notifies the producer that there is space available in the buffer
			notFull.signal();
        }
        finally{
			lock.unlock();
        }
			
		return m;
	}

	@Override
	public void put(_Producteur p, Message m) throws InterruptedException, ControlException {
		lock.lock();
		count = enAttente();
		
		try{
			while(count == taille()){
				notFull.await();
			}
			
			buffer[in] = m;
				
			/*
			 * add depotMessage(Producteur P, Message M)
			 * for objective 3 
			 */
			observateur.depotMessage(p, m);
			
			in = (in + 1) % taille();
			count++;

			LOGGER.log(Level.INFO, "{0}[{1}] \tproduces: \t\t{2}{3}",
					new Object[]{AnsiColor.PURPLE, p.identification(), m, AnsiColor.RESET});
			
			// notifies the consumer that data is available to read
			notEmpty.signal();
		}
		finally{
			lock.unlock();
		}
	}

	@Override
	public int taille() {
		return buffer.length;
	}

}
