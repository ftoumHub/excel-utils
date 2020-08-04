package parser;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Iterator;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Either;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.function.Function;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.For;
import static io.vavr.API.Match;
import static io.vavr.control.Either.right;
import static libs.ExcelUtils.getArea;
import static libs.ExcelUtils.getSafeCell;
import static parser.CParser.fail;
import static parser.CParser.flatMap;
import static parser.CParser.map;
import static parser.CParser.product;
import static parser.CParser.success;

public class CParserUtils {

    private CParserUtils() {}

    public static CParser<Seq<Double>> numericRange(String name) {

        Function<Seq<SafeCell>, Either<ParserError, Seq<Double>>> _ =
                safeCells -> Either.sequenceRight(safeCells.map(SafeCell::asDouble).toList());

        return range(name).flatMapF(safeCells -> _.apply((Seq<SafeCell>)safeCells));
    }

    static CParser<Double> numeric(String name) {
        return flatMap(numericRange(name), d -> Match(d.asJava().size()).of(
                Case($(1), success(d.head())),
                Case($(), fail(new ParserError.InvalidFormat(name, "Single Numeric", "0 or more than 1 value")))
        ));
    }

    static CParser<Seq<SafeCell>> range(String name) {
        return new CParser<Seq<SafeCell>>() {
            @Override
            public Either<ParserError, Seq<SafeCell>> parse(Workbook workbook) {
                return getArea(workbook, name)
                        .fold(Either::left,
                            areaRef -> Either.sequenceRight(List.of(areaRef.getAllReferencedCells())
                                                .map(cellRef -> getSafeCell(workbook, cellRef)).toList()));
            }
        };
    }

    static Tuple product_ko(Workbook workbook) {

        Iterator<Tuple2<Seq<Double>, Seq<Double>>> t = For(
                numericRange("OilProd").parse(workbook),
                numericRange("GasProd").parse(workbook)
        ).yield(
                (oilParseur, gasParseur) -> Tuple.of(oilParseur, gasParseur)
        );

        return t.get();
    }

    public static CParser<Production> production_V0() {
        final CParser<Tuple2<Seq<Double>, Seq<Double>>> product = product(numericRange("OilProd"), numericRange("GasProd"));

        return new CParser<Production>() {
            @Override
            public Either<ParserError, Production> parse(Workbook workbook) {
                return product.parse(workbook)
                        .flatMap(t -> right(new Production(t._1, t._2)));
            }
        };
    }

    public static CParser<Production> production2() {
        return numericRange("OilProd")
                .flatMap(oil ->
                        flatMap(numericRange("GasProd"), gas ->
                                success(new Production(oil, gas))));
    }

    public static CParser<Production> production() {
        return map(product(numericRange("OilProd"), numericRange("GasProd")), t -> new Production(t._1, t._2));
    }
}
