//Natàlia Masgrau Vila

/**
 * @file Canonada.java
 * @brief Classe que representa una canonada de la xarxa de distribució d'aigua.
 */

/**
 * @class Canonada
 * @brief Representa una canonada que connecta dos nodes en una xarxa de distribució d'aigua.
 */
public class Canonada {
    // Descripció general: Canonada de la xarxa de distribució d'aigua

    private final Node_X node1; ///< Node d'inici de la canonada
    private final Node_X node2; ///< Node de destí de la canonada
    private float capacitat;    ///< Capacitat de la canonada
    private float cabal;        ///< Cabal d'aigua que circula per la canonada

    /**
     * @brief Constructor de la classe Canonada.
     * @param node1 Node d'inici de la canonada.
     * @param node2 Node de destí de la canonada.
     * @param capacitat Capacitat de la canonada. Ha de ser major que 0.
     * @pre Capacitat > 0
     * @post Crea una canonada que connecta node1 i node2 amb la capacitat indicada.
     */
    public Canonada(Node_X node1, Node_X node2, float capacitat) {
        this.node1 = node1;
        this.node2 = node2;
        this.capacitat = capacitat;
        this.cabal = 0;
    }

    /**
     * @brief Retorna el node d'inici de la canonada.
     * @return Node d'inici de la canonada.
     * @pre ---
     * @post Retorna el node d'inici de la canonada.
     */
    public Node_X node1() {
        return this.node1;
    }

    /**
     * @brief Retorna el node de destí de la canonada.
     * @return Node de destí de la canonada.
     * @pre ---
     * @post Retorna el node de destí de la canonada.
     */
    public Node_X node2() {
        return this.node2;
    }

    /**
     * @brief Retorna la capacitat de la canonada.
     * @return Capacitat de la canonada.
     * @pre ---
     * @post Retorna la capacitat de la canonada.
     */
    public float capacitat() {
        return this.capacitat;
    }

    /**
     * @brief Actualitza la capacitat de la canonada.
     * @param capacitat Nova capacitat de la canonada. Ha de ser major o igual a 0.
     * @pre Capacitat >= 0
     * @post La capacitat de la canonada és la capacitat indicada.
     * @throws IllegalArgumentException Si capacitat és menor que 0.
     */
    public void actualitzarCapacitat(float capacitat) {
        if (capacitat < 0) {
            throw new IllegalArgumentException("Capacitat no valida");
        } else {
            this.capacitat = capacitat;
        }
    }

    /**
     * @brief Retorna el cabal d'aigua que circula per la canonada.
     * @return Cabal d'aigua que circula per la canonada.
     * @pre ---
     * @post Retorna el cabal d'aigua que circula per la canonada.
     */
    public float cabal() {
        return this.cabal;
    }

    /**
     * @brief Actualitza el cabal d'aigua que circula per la canonada.
     * @param cabal Nou cabal d'aigua. Ha de ser major o igual a 0.
     * @pre Cabal >= 0
     * @post El cabal d'aigua que circula per la canonada és el cabal indicat.
     * @throws IllegalArgumentException Si cabal és menor que 0.
     */
    public void actualitzarCabal(float cabal) {
        if (cabal < 0) {
            throw new IllegalArgumentException("Cabal negatiu");
        } else {
            this.cabal = cabal;
        }
    }
}
