package parser;

import java.util.List;

public class Production {

    List<Double> oil;
    List<Double> gas;

    public Production(List<Double> oil, List<Double> gas) {
        this.oil = oil;
        this.gas = gas;
    }

    public List<Double> getOil() {
        return oil;
    }

    public void setOil(List<Double> oil) {
        this.oil = oil;
    }

    public List<Double> getGas() {
        return gas;
    }

    public void setGas(List<Double> gas) {
        this.gas = gas;
    }

    @Override
    public String toString() {
        return "Production(" + io.vavr.collection.List.of(oil) +  ", " + io.vavr.collection.List.of(gas) + ")";
    }
}
