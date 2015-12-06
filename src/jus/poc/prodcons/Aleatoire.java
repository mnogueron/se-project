package jus.poc.prodcons;

/**
 * Created by matthieu on 06/12/15.
 */
public class Aleatoire {
	
	protected java.util.Random var;
	protected int moyenne;
	protected int deviation;
	protected int borneInf;
	protected int borneSup;
	
	// constructor
	public Aleatoire(int moyenne, int deviation){
		
	}
	
	public int next(){
		return 1;
	}
	
	public static int[] valeurs(int size, int moyenne, int deviation) throws ControlException {
		return null;
	}
	
	public static int valeur(int moyenne, int deviation){
		return 1;
	}
}
