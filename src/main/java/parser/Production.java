package parser;

import io.vavr.collection.Seq;

public class Production {

    private final Seq<Double> oil;
    private final Seq<Double> gas;

    public Production(Seq<Double> oil, Seq<Double> gas) {
        this.oil = oil;
        this.gas = gas;
    }

    @Override
    public String toString() {
        return "Production(" + oil +  ", " + gas + ")";
    }
}
