package jus.poc.prodcons;

import java.io.IOException;

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

	public TestProdCons(Observateur observateur) {
		super(observateur);
        init("options.xml");
	}

	@Override
	protected void run() throws Exception {
		// TODO Auto-generated method stub

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

        Properties options = new Properties("jus/poc/prodcons/options"+file);
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
