package parser;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;

import static io.vavr.API.Right;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static parser.ParserUtils.numeric;

/**
 * Si on veut généraliser le principe du parseur, on se rend compte que l'API que l'on souhaite construire va ressembler à ça :
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
 */
public class S04_Parsing_API extends WithExampleWorkbook{

    @Test
    public void rewrite_Numeric_From_Scratch() {
        //assertEquals(numericFromScratch().parse(workbook, "ExplorationFee").get(), Double.valueOf(1.4));

        assertEquals(numeric().parse(workbook, "ExplorationFee").get(),
                Double.valueOf(1.4));
    }

    // Slide Numeric
    @Test
    public void reusing_previous_numericRange() {
        // numeric utilise flatMap sur l'either retourné par numericRangeV2
        // On transforme ainsi un Either<ParserError, Seq<Double>> en Either<ParserError, Double>
        // flatMap va retourner la première erreur rencontrée sous forme de ParserError ou bien enchainer les traitements
        assertEquals(numeric().parse(workbook, "ExplorationFee"),
                     Right(Double.valueOf(1.4)));

        assertThat(numeric().parse(workbook, "OilProd").getLeft())
                   .isInstanceOf(ParserError.InvalidFormat.class);

        assertThat(numeric().parse(workbook, "foo").getLeft())
                   .isInstanceOf(ParserError.MissingName.class);
    }

}
