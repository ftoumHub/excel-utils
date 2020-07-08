package parser;

import io.vavr.collection.List;
import io.vavr.control.Either;
import org.junit.jupiter.api.Test;

import static io.vavr.API.Left;
import static io.vavr.API.Right;
import static io.vavr.API.println;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class S01_Introduction {

    /**
     * Un exemple de fonction "partielle", susceptible de retourner une exception.
     */
    // tag::estAdultePartielle[]
    public Boolean estAdultePartielle(Integer age) {
        if (age < 0) {
            throw new IllegalArgumentException("l'age doit être un entier positif");
        }
        return age >= 18;
    }
    // end::estAdultePartielle[]

    // tag::estAdultePartielleTest[]
    @Test
    public void estAdultePartielleTest() {
        assertFalse(estAdultePartielle(4));

        assertTrue(estAdultePartielle(25)); // true

        assertThrows(IllegalArgumentException.class, () -> estAdultePartielle(-10));
    }
    // end::estAdultePartielleTest[]

    /**
     * On a donc un problème car lorsqu'on appelle cette méthode, on ne sait pas si il s'agit
     * d'une fonction partielle ou totale (sauf si on indique throws ... dans la signature de la méthode).
     * On doit regarder l'implémentation ou la doc...
     *
     * Le seul moyen de traiter l'erreur est donc de catcher la ou les exceptions qui peuvent survenir.
     *
     * Une technique consiste à "augmenter" le type de retour en utilisant par exemple Either.
     * On indique dans la signature le type d'erreur.
     */

    // tag::estAdulte[]
    public Either<String, Boolean> estAdulte(Integer age) {
        if (age < 0) {
            return Either.left("l'age doit être un entier positif");
        } else {
            return Either.right(age >= 18);
        }
    }
    // end::estAdulte[]

    // tag::estAdulteTest[]
    @Test
    public void estAdulteTest() {
        assertEquals(Right(false), estAdulte(4));

        assertEquals(Right(true), estAdulte(25));

        assertEquals(Left("l'age doit être un entier positif"), estAdulte(-2));
    }
    // end::estAdulteTest[]


    @Test
    public void deriverDesFonctions() {
        println(List.empty());

        println(List.of(1, 2, 3));

        println(List.of(1, 2).appendAll(List.of(3, 4)));

        println(List.of(1, 2, 3, 4).reverse());

        println(List.of(List.of(1, 2), List.of(3, 4)).flatMap(i -> i));
    }
}
