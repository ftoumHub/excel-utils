package parser;

import static io.vavr.API.println;

import org.junit.Test;

import io.vavr.collection.List;
import io.vavr.control.Either;

public class S01_Introduction {

    /**
     * Ceci est une fonction "partielle"
     */
    public Boolean estAdultePartielle(Integer age) {
        if (age < 0) {
            throw new IllegalArgumentException("l'age doit être un entier positif");
        }
        return age >= 18;
    }

    @Test
    public void estAdultePartielleTest() {
        println(estAdultePartielle(4));

        println(estAdultePartielle(25));

        //println(estAdultePartielle(-10));
    }

    /**
     * On a donc un problème car lorsqu'on appelle cette méthode, on ne sait pas si il s'agit d'une fonction partielle
     * ou totale. Rien ne nous l'indique. On doit regarder l'implémentation ou la doc...
     * Le seul moyen de traiter l'erreur est donc de catcher la ou les exceptions qui peuvent survenir.
     *
     * Une technique consiste à "augmenter" le type de retour en utilisant par exemple Either.
     * On indique dans la signature le type d'erreur.
     */

    public Either<String, Boolean> estAdulte(Integer age) {
        return age < 0 ? Either.left("l'age doit être un entier positif") : Either.right(age >= 18);
    }

    @Test
    public void estAdulteTest() {
        println(estAdulte(4));

        println(estAdulte(25));

        println(estAdulte(-2));
    }


    @Test
    public void deriverDesFonctions() {
        println(List.empty());

        println(List.of(1, 2, 3));

        println(List.of(1, 2).appendAll(List.of(3, 4)));

        println(List.of(1, 2, 3, 4).reverse());

        println(List.of(List.of(1, 2), List.of(3, 4)).flatMap(i -> i));
    }
}
