package jus.poc.prodcons.v3;

import java.io.IOException;
import java.util.ArrayList;

import jus.poc.prodcons.Acteur;
import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Simulateur;

/**
 * Created by matthieu on 06/12/15.
 */
public class TestProdCons extends Simulateur {

    private int nbProd;
    private int nbCons;
    private int nbBuffer;
    private int tempsMoyenProduction;
    private int deviationTempsMoyenProduction;
    private int tempsMoyenConsommation;
    private int deviationTempsMoyenConsommation;
    private int nombreMoyenDeProduction;
    private int deviationNombreMoyenDeProduction;
    private int nombreMoyenNbExemplaire;
    private int deviationNombreMoyenNbExemplaire;
    
    private ArrayList<Consommateur> consommateurs; 
    private ArrayList<Producteur> producteurs; 
    
    private ProdCons prodCons;
    private int typeConsommateur = 2;
    private int typeProducteur = 1;

	public TestProdCons(Observateur observateur) {
		super(observateur);
        init("options.xml");
        prodCons = new ProdCons(nbBuffer, nbProd);
        consommateurs = new ArrayList<>();
        producteurs = new ArrayList<>();
        
        /* 
         * added init(int nbProducteurs, int nbConsommateurs, int nbBuffers)
         * for objective 3 
        */
        try {
			observateur.init(nbProd, nbCons, nbBuffer);
		} catch (ControlException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        for(int i = 0; i<nbCons; i++){
        	try {
        		Consommateur c = new Consommateur(typeConsommateur, observateur,
						tempsMoyenConsommation, deviationTempsMoyenConsommation, prodCons,
						nombreMoyenNbExemplaire, deviationNombreMoyenNbExemplaire);
				consommateurs.add(c);
				/*
				 * added newConsommateur(Consommateur C)
				 * for objective 3
				 */
				observateur.newConsommateur(c);
			} catch (ControlException e) {
				e.printStackTrace();
			}
        }
        
        for(int i = 0; i<nbProd; i++){
        	try {
        		Producteur p = new Producteur(typeProducteur, observateur,
						tempsMoyenProduction, deviationTempsMoyenProduction, prodCons,
						nombreMoyenDeProduction, deviationNombreMoyenDeProduction);
				producteurs.add(p);
				/*
				 * added newProducteur(Producteur P)
				 * for objective 3
				 */
				observateur.newProducteur(p);
			} catch (ControlException e) {
				e.printStackTrace();
			}
        }
	}

	@Override
	protected void run() throws Exception {
		// TODO Auto-generated method stub		
		for(Producteur p : producteurs){
			p.start();
		}
		for(Consommateur c : consommateurs){
			c.start();
		}
	}

    public void init(String file){
        class Properties extends java.util.Properties{
            private static final long serialVersionUID = 1L;
            public int get(String key){
                return Integer.parseInt(getProperty(key));
            }

            public Properties(String file){
                try {
                    loadFromXML(ClassLoader.getSystemResourceAsStream(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Properties options = new Properties("jus/poc/prodcons/options/"+file);
        nbProd = options.get("nbProd");
        nbCons = options.get("nbCons");
        nbBuffer = options.get("nbBuffer");
        tempsMoyenProduction = options.get("tempsMoyenProduction");
        deviationTempsMoyenProduction = options.get("deviationTempsMoyenProduction");
        tempsMoyenConsommation = options.get("tempsMoyenConsommation");
        deviationTempsMoyenConsommation = options.get("deviationTempsMoyenConsommation");
        nombreMoyenDeProduction = options.get("nombreMoyenDeProduction");
        deviationNombreMoyenDeProduction = options.get("deviationNombreMoyenDeProduction");
        nombreMoyenNbExemplaire = options.get("nombreMoyenNbExemplaire");
        deviationNombreMoyenNbExemplaire = options.get("deviationNombreMoyenNbExemplaire");
    }

	public static void main(String[] args){
        new TestProdCons(new Observateur()).start();
    }
}