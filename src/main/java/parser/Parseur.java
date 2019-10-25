package parser;

import java.util.function.Function;

import org.apache.poi.ss.usermodel.Workbook;

import io.vavr.control.Either;

public abstract class Parseur<A>  {

    private final Parseur<A> _self = this;

    public abstract Either<ParserError, A> parse(Workbook workbook);

    public <B> Parseur<B> flatMap(Function<A, Parseur<B>> f) {
        return new Parseur<B>() {
            @Override
            public Either<ParserError, B> parse(Workbook workbook) {
                return _self.parse(workbook).flatMap(a -> f.apply(a).parse(workbook));
            }
        };
    }

    public <A, B> Parseur<B> flatMapF(Function<A, Either<ParserError, B>> f) {
        return flatMap(a -> ParseurUtils.lift(f.apply((A)a)));
    }

}
