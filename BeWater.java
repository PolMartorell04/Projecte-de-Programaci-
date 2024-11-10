//Natalia Masgrau Vila
/**
 * @file BeWater.java
 * @brief Programa principal de simulació de xarxes de distribució d'aigua.
 */

/**
 * @class BeWater
 * @brief Classe abstracta que representa el programa principal de simulació de xarxes de distribució d'aigua.
 *
 * La classe BeWater conté el punt d'entrada principal per a executar la simulació.
 * Implementa el mètode main que inicialitza la simulació en mode text.
 */
public abstract class BeWater {

    /**
     * @brief Punt d'entrada principal per a executar la simulació.
     *
     * Aquest mètode inicialitza un objecte SimuladorModeText i invoca el mètode simular
     * passant-li els arguments proporcionats per l'usuari.
     *
     * @param args Arguments de la línia de comandes. El primer argument ha de ser el fitxer de configuració de la xarxa.
     *             El segon argument ha de ser el fitxer de comandes per a la simulació.
     */
    public static void main(String[] args) {
        SimuladorModeText simulador = new SimuladorModeText();
        System.out.println("Be water, my friend");
        simulador.simular(args[0], args[1]);
    }
}
