package jus.poc.prodcons.v1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import jus.poc.prodcons.Acteur;
import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Simulateur;

/**
 * Created by matthieu on 06/12/15.
 */
public class TestProdCons extends Simulateur {

    private Logger LOGGER = Logger.getLogger(TestProdCons.class.getName());

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
        LOGGER.info("Initialize local variables");
        init("options.xml");
        prodCons = new ProdCons(nbBuffer, nbProd);
        consommateurs = new ArrayList<>();
        producteurs = new ArrayList<>();

        LOGGER.info("Create all Consommateur");
        for(int i = 0; i<nbCons; i++){
        	try {
				consommateurs.add(new Consommateur(typeConsommateur, observateur,
						tempsMoyenConsommation, deviationTempsMoyenConsommation, prodCons,
						nombreMoyenNbExemplaire, deviationNombreMoyenNbExemplaire));
			} catch (ControlException e) {
				e.printStackTrace();
			}
        }

        LOGGER.info("Create all Producteur");
        for(int i = 0; i<nbProd; i++){
        	try {
				producteurs.add(new Producteur(typeProducteur, observateur,
						tempsMoyenProduction, deviationTempsMoyenProduction, prodCons,
						nombreMoyenDeProduction, deviationNombreMoyenDeProduction));
			} catch (ControlException e) {
				e.printStackTrace();
			}
        }
	}

	@Override
	protected void run() throws Exception {
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
        if(args.length > 0){
            if(args[0].equals("-Ddebug=0")){
                LogManager.getLogManager().reset();
            }
        }
        new TestProdCons(new Observateur()).start();
    }
}
