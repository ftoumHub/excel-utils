package parser;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import static io.vavr.API.Right;
import static io.vavr.API.println;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Jusqu'à présent, nous avons utilisé Either.flatMap pour transformer
 * un Either<ParserError, <A>> en Either<ParserError, <B>>.
 *
 * Nous allons maintenant utiliser un flatMap sur Parser
 */
public class S05_FlatMap_On_Parser extends WithExampleWorkbook{

    @Test
    public void properFlatMapEither() {
        println(properFlatMapOfEither("18")); // Right(18)

        println(properFlatMapOfEither(null)); // Left(empty)

        println(properFlatMapOfEither("Test")); // Left(Test)
    }

    @Test
    public void build_numeric_with_flatMap_on_parser() {
        // numeric utilise flatMap sur Parser
        assertEquals(ParserUtils.numeric().parse(workbook, "ExplorationFee"),
                Right(1.4));

        assertThat(ParserUtils.numeric().parse(workbook, "OilProd").getLeft())
                .isInstanceOf(ParserError.InvalidFormat.class);

        assertThat(ParserUtils.numeric().parse(workbook, "foo").getLeft())
                .isInstanceOf(ParserError.MissingName.class);
    }


    /**
     * Exemple d'utilisation de flatMap sur Either
     */
    public static Either<String, Integer> properFlatMapOfEither(String age) {
        return Option
                .of(age)
                .map(Either::<String, String>right)
                .getOrElse(Either.left("empty"))
                .flatMap(S05_FlatMap_On_Parser::eitherWrapper);
    }

    private static Either<String, Integer> eitherWrapper(String value) {
        return Try
                .of(() -> value)
                .map(v -> Either.<String, Integer>right(Integer.valueOf(v)))
                .getOrElse(() -> Either.left(value));
    }
}
