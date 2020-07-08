package parser;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import org.junit.jupiter.api.Test;

import static io.vavr.API.println;
import static parser.CParser.map;
import static parser.CParser.product;
import static parser.CParserUtils.numeric;
import static parser.CParserUtils.numericRange;
import static parser.CParserUtils.production;
import static parser.ParserUtils.numeric;
import static parser.ParserUtils.properFlatMapOfEither;

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
    public void curriedParser() {
        // numeric utilise flatMap sur l'either retourné par numericRangeV2
        // On transforme ainsi un Either<ParserError, Seq<Double>> en Either<ParserError, Double>

        println(numericRange("OilProd").parse(workbook));
        println(numeric("OilProd").parse(workbook));
        println(numeric("ExplorationFee").parse(workbook));
        println(numericRange("foo").parse(workbook));
    }

    @Test
    public void implementingProduction() {
        println(CParserUtils.production_V0().parse(workbook));
        println(CParserUtils.production2().parse(workbook));

        final CParser<Seq<Double>> oil = numericRange("OilProd");
        final CParser<Seq<Double>> gas = numericRange("GasProd");
        final CParser<Seq<Double>> foo = numericRange("foo");

        println(product(oil, gas).parse(workbook));
        println(product(oil, foo).parse(workbook));

        final CParser<OilProduction> oilProduction = map(numericRange("OilProd"), o -> new OilProduction(o));
        println(oilProduction.parse(workbook));

        println(production().parse(workbook));
    }


    class OilProduction {
         private Seq<Double> value;

        public OilProduction(Seq<Double> value) {
            this.value = value;
        }
    }
}
