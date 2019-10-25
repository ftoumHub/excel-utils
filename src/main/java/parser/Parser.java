package parser;

import org.apache.poi.ss.usermodel.Workbook;
import io.vavr.control.Either;

public interface Parser<A>  {

    Either<ParserError, A> parse(Workbook workbook, String name);
}
