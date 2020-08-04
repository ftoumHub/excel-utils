package parser;

import io.vavr.collection.Seq;
import io.vavr.control.Either;
import org.junit.jupiter.api.Test;

import static io.vavr.API.println;
import static org.assertj.core.api.Assertions.assertThat;
import static parser.CurriedParserUtils.numeric;
import static parser.CurriedParserUtils.numericRange;


public class S06_Curried_Parser extends WithExampleWorkbook{

    @Test
    public void curriedParser() {
        final Either<ParserError, Seq<Double>> oilProdAsDouble = numericRange("OilProd").parse(workbook);

        assertThat(oilProdAsDouble.get())
                .containsExactly(10.12, 12.34, 8.83, 6.23, 9.18, 12.36, 16.28, 18.25, 20.01);

        println(numeric("OilProd").parse(workbook));

        println(numeric("ExplorationFee").parse(workbook));

        println(numericRange("foo").parse(workbook));
    }
}
