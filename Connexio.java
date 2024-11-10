//Pol Martorell Herrera

/**
 * @file Connexio.java
 * @brief Classe que representa un node de connexió en una xarxa de distribució d'aigua.
 */

/**
 * @class Connexio
 * @brief Representa un node de connexió d'una xarxa de distribució d'aigua.
 * @extends Node_X
 */
public class Connexio extends Node_X {

    /**
     * @brief Constructor de la classe Connexio.
     * @param id Identificador del node de connexió.
     * @param c Coordenades del node de connexió.
     * @pre ---
     * @post S'ha creat un nou node de connexió amb identificador id i coordenades c.
     */
    public Connexio(String id, Coordenades c) {
        super(id, c);
    }
}