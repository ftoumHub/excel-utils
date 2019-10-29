package parser;

import static io.vavr.API.println;

import org.junit.Test;

import io.vavr.collection.List;
import io.vavr.control.Either;

public class S01_Introduction {

    /**
     * Ceci est une fonction "partielle"
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
        println(estAdultePartielle(4)); // false

        println(estAdultePartielle(25)); // true

        //println(estAdultePartielle(-10)); // java.lang.IllegalArgumentException
    }
    // end::estAdultePartielleTest[]

    /**
     * On a donc un problème car lorsqu'on appelle cette méthode, on ne sait pas si il s'agit
     * d'une fonction partielle ou totale. Rien ne nous l'indique.
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
        println(estAdulte(4));  // Right(false)

        println(estAdulte(25)); // Right(true)

        println(estAdulte(-2)); // Left(l'age doit être un entier positif)
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
