package parser;

import static io.vavr.API.println;
import static parser.ParserUtils.properFlatMapOfEither;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Jusqu'à présent, nous avons utilisé Either.flatMap pour transformer
 * un Either<ParserError, <A>> en Either<ParserError, <B>>.
 *
 * Nous allons maintenant
 */
public class S05_FlatMap {

    private static Workbook workbook;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void setup() throws IOException, InvalidFormatException {
        workbook = IO.load("example.xlsx");
    }

    @Test
    public void properFlatMapEither() {
        println(properFlatMapOfEither("18")); // Right(18)

        println(properFlatMapOfEither(null)); // Left(empty)

        println(properFlatMapOfEither("Test")); // Left(Test)
    }

    @Test
    public void flatMapNumericRange() {
        // numeric utilise flatMap sur l'either retourné par numericRangeV2
        // On transforme ainsi un Either<ParserError, Seq<Double>> en Either<ParserError, Double>

        println((ParseurUtils.numericRange("OilProd").parse(workbook)));
        println((ParseurUtils.numeric("OilProd").parse(workbook)));
        println((ParseurUtils.numeric("ExplorationFee").parse(workbook)));
        println((ParseurUtils.numericRange("DTC").parse(workbook)));
        println((ParseurUtils.numericRange("OilProd").parse(workbook)));
    }


}
