//Pol Martorell Herrera
/**
 * @file PuntOrigen.java
 * @brief Classe que representa un node origen d'una xarxa de distribució d'aigua.
 */

/**
 * @class PuntOrigen
 * @brief Representa un node origen d'una xarxa de distribució d'aigua.
 * @extends Node_X
 */
public class PuntOrigen extends Node_X {

    // Descripció general: Node origen d'una xarxa de distribució d'aigua

    private float cabal; ///< Cabal d'aigua que surt de l'origen

    /**
     * @brief Constructor de la classe PuntOrigen.
     * @param id Identificador del node origen.
     * @param c Coordenades del node origen.
     * @pre ---
     * @post S'ha creat un nou origen amb identificador id i coordenades c.
     */
    public PuntOrigen(String id, Coordenades c) {
        super(id, c);
        cabal = 0;
    }

    /**
     * @brief Retorna el cabal d'aigua que surt de l'origen.
     * @return Cabal d'aigua que surt de l'origen.
     * @pre ---
     * @post Retorna el cabal d'aigua que surt de l'origen.
     */
    public float cabal() {
        return cabal;
    }

    /**
     * @brief Estableix el cabal d'aigua que surt de l'origen.
     * @param cabal Nou cabal d'aigua. Ha de ser major o igual a 0.
     * @pre Cabal >= 0
     * @post El cabal d'aigua que surt de l'origen és el cabal indicat.
     * @throws IllegalArgumentException Si cabal és menor que 0.
     */
    public void establirCabal(float cabal) {
        if (cabal < 0) {
            throw new IllegalArgumentException("Cabal negatiu");
        } else {
            this.cabal = cabal;
        }
    }
}