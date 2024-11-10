// Pol Martorell Herrera
/**
 * @file Node_X.java
 * @brief Classe que representa un node d'una xarxa de distribució d'aigua.
 */

/**
 * @class Node_X
 * @brief Representa un node d'una xarxa de distribució d'aigua.
 */
public class Node_X {

    // Descripció general: Node d'una xarxa de distribució d'aigua
    private String id;          ///< Identificador del node
    private Coordenades c;      ///< Coordenades del node
    private boolean aixetaOberta; ///< Estat de l'aixeta del node (oberta o tancada)

    /**
     * @brief Constructor de la classe Node_X.
     * @param id Identificador del node.
     * @param c Coordenades del node.
     * @pre ---
     * @post S'ha creat un node amb identificador id i coordenades c.
     */
    public Node_X(String id, Coordenades c) {
        this.aixetaOberta = true;
        this.id = id;
        this.c = c;
    }

    /**
     * @brief Retorna l'identificador del node.
     * @return Identificador del node.
     * @pre ---
     * @post Retorna l'identificador del node.
     */
    public String id() {
        return this.id;
    }

    /**
     * @brief Retorna les coordenades del node.
     * @return Coordenades del node.
     * @pre ---
     * @post Retorna les coordenades del node.
     */
    public Coordenades coordenades() {
        return this.c;
    }

    /**
     * @brief Diu si l'aixeta del node està oberta.
     * @return Estat de l'aixeta del node (true si està oberta, false si està tancada).
     * @pre ---
     * @post Diu si l'aixeta del node està oberta.
     */
    public boolean aixetaOberta() {
        return aixetaOberta;
    }

    /**
     * @brief Obre l'aixeta del node.
     * @pre ---
     * @post L'aixeta del node està oberta.
     */
    public void obrirAixeta() {
        aixetaOberta = true;
    }

    /**
     * @brief Tanca l'aixeta del node.
     * @pre ---
     * @post L'aixeta del node està tancada.
     */
    public void tancarAixeta() {
        aixetaOberta = false;
    }
}