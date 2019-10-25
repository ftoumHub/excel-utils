package parser;

import static org.junit.Assert.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static parser.ParserUtils.numeric;
import static parser.ParserUtils.numericFromScratch;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Si on veut généraliser le principe du parser, on se rend compte que l'API que l'on souhaite construire va ressembler à ça :
 *
 * Either<ParserError, Seq<Double>>  numericRange(Workbook workbook, String name)
 * Either<ParserError, Seq<Integer>> intRange(Workbook workbook, String name)
 * Either<ParserError, Seq<String>>  stringRange(Workbook workbook, String name)
 *
 * Either<ParserError, Double>  numeric(Workbook workbook, String name)
 * Either<ParserError, Integer> int(Workbook workbook, String name)
 * Either<ParserError, String>  string(Workbook workbook, String name)
 *
 * D'ou l'idée de créer l'interface {@link Parser#parse(Workbook, String)}
 *
 */
public class S04_Parsing_API {

    private static Workbook workbook;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void setup() throws IOException, InvalidFormatException {
        workbook = IO.load("example.xlsx");
    }

    @Test
    public void rewriteNumericFromScratch() {
        assertEquals(Double.valueOf(numericFromScratch().parse(workbook, "ExplorationFee").get()),
                     Double.valueOf(1.4));
    }

    // Slide Numeric
    @Test
    public void reusingPreviousNumericRangeV2() {
        // numeric utilise flatMap sur l'either retourné par numericRangeV2
        // On transforme ainsi un Either<ParserError, Seq<Double>> en Either<ParserError, Double>

        assertEquals(Double.valueOf(numeric().parse(workbook, "ExplorationFee").get()),
                     Double.valueOf(1.4));

        assertThat(numeric().parse(workbook, "OilProd").getLeft())
                   .isInstanceOf(ParserError.InvalidFormat.class);

        assertThat(numeric().parse(workbook, "DTC").getLeft())
                   .isInstanceOf(ParserError.MissingName.class);
    }


}
