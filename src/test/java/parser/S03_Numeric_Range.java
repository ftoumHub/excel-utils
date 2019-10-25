package parser;

import static io.vavr.API.println;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static parser.ParserUtils.*;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.junit.rules.ExpectedException;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Either;

public class S03_Numeric_Range {

    private static Workbook workbook;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void setup() throws IOException, InvalidFormatException {
        workbook = IO.load("example.xlsx");
    }

    @Test
    public void numericRangeVersion0() {
        // On peut mainteant facilement extraire une liste de valeur depuis un fichier excel
        List<Double> oilProdAsDouble = numericRangeV0(workbook, "OilProd");

        assertThat(oilProdAsDouble)
                .containsExactly(10.12, 12.34, 8.83, 6.23, 9.18, 12.36, 16.28, 18.25, 20.01);
    }

    @Test
    public void numericRangeV1FailsIfListDoesntContainsDouble() {
        // Si on appelle getNumericCell
        // numericRangeV1 n'est pas une fonction totale, on se prend une exception
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(JUnitMatchers.containsString("Cannot get a NUMERIC value from a STRING cell"));

        numericRangeV0(workbook, "PrimaryProduct");
    }

    /**
     * On construit une classe {@link ParserErrorClass} qui va représenter un type d'erreur possible.
     *
     * On utilise également {@ling SafeCell} qui est un wrapper "safe"
     */
    @Test
    public void numRangeWithSafeCell() {
        // On utilise maintenant SafeCell pour retourner un Either<ParserErrorX, Double> pour chaque cellule.
        Either<ParserErrorClass, Seq<Double>> oilProd = numericRangeV1(workbook, "OilProd");

        assertThat(oilProd.get().toJavaList())
                .containsExactly(10.12, 12.34, 8.83, 6.23, 9.18, 12.36, 16.28, 18.25, 20.01);
    }

    @Test
    public void cellErrorHandling() {
        Either<ParserErrorClass, Seq<Double>> primaryProdEither = numericRangeV1(workbook, "PrimaryProduct");

        assertEquals(primaryProdEither.getLeft().getMessage(), "Cannot get a NUMERIC value from a STRING cell");
    }

    @Test
    public void stillNotTotalFunction() {
        // numericRangeV1 n'est pas une fonction totale,
        // on peut de se prendre une exception si on recherche une plage de cellule inexistante
        thrown.expect(NullPointerException.class);
        numericRangeV1(workbook, "foo");
    }

    @Test
    public void numericRangeWithParserError() {
        // Dans numericRangeV2, on utilise l'interface ParserError...
        println(numericRangeV2(workbook, "OilProd"));
        println(numericRangeV2(workbook, "PrimaryProduct"));
        println(numericRangeV2(workbook, "foo"));
    }
}
