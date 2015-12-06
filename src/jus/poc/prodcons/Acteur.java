package jus.poc.prodcons;

/**
 * Created by matthieu on 06/12/15.
 */
public abstract class Acteur implements _Acteur {
	
	
	private static int consommateurIdentifaction;
	protected int deviationTempsDeTraitement;
	private int identification;
	protected int moyenneTempsDeTraitement;
	protected Observateur observateur;
	private static int producteurIdentifaction;
	protected static int typeConsommateur;
	protected static int typeProducteur;
	
	protected Acteur(int type,
            Observateur observateur,
            int moyenneTempsDeTraitement,
            int deviationTempsDeTraitement)
     throws ControlException {
		
	}
	
    public int deviationTempsDeTraitement(){
    	
    	return 1;
    }
    
    public int identification() {
    	
    	return 1;
    }
    
    public int moyenneTempsDeTraitement(){
    	
    	return 1;
    }
    
    public abstract int nombreDeMessages();
	
	
	
}
