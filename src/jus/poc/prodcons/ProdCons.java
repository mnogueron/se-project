package jus.poc.prodcons;

/**
 * Created by matthieu on 06/12/15.
 */
public class ProdCons implements Tampon {
	
	private Message[] buffer;
	private int nbBusy;
	private int in;
	private int out ;
	
	/**
	 * ProdCons constructor 
	 * @param bufferSize
	 */
	public ProdCons(int bufferSize) {
		nbBusy = 0;
		in = 0;
		out = 0;
		buffer = new Message[bufferSize];
		for(int i=0; i<taille(); i++){
			buffer[i] = null;
		}
	}

	@Override
	public int enAttente() {
		for(int i=0; i<taille(); i++){
			if (buffer[i] != null){
				nbBusy++;
			}
		}
		return nbBusy;
	}

	@Override
	public Message get(_Consommateur c) {
		while(nbBusy <= 0){
			try {
				c.wait();
			} catch (InterruptedException e) {}
		}
		
		Message m = buffer[out];
		out = (out-1)%taille();
		nbBusy--;
		notify();
			
		return m;
	}

	@Override
	public void put(_Producteur p, Message m) {
		while(nbBusy >= taille()){
			try {
				p.wait();
			} catch (InterruptedException e) {}
		}
		
		buffer[in] = m;
		in = (in+1)%taille();
		nbBusy++;
		notify();
	}

	@Override
	public int taille() {
		return buffer.length;
	}
}
