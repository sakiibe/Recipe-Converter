import java.util.Objects;

public class ConversionRow {
    private String fromUnit;
    private String toUnit;
    private double fromQty;
    private double toQty;


    public ConversionRow(String fromUnit, String toUnit){
        this.fromUnit=fromUnit;
        this.toUnit=toUnit;
    }
    public ConversionRow(String fromUnit, String toUnit, double fromQty, double toQty){
        this.fromUnit=fromUnit;
        this.toUnit=toUnit;
        this.fromQty=fromQty;
        this.toQty=toQty;
    }

    public String getFromUnit() {
        return fromUnit;
    }

    public String getToUnit() {
        return toUnit;
    }

    public double getFromQty() {
        return fromQty;
    }

    public double getToQty() {
        return toQty;
    }

    public double conversionFactor(){
        return toQty/fromQty;
    }
    @Override
    public boolean equals(Object comparedObject) {
        if (this == comparedObject) {
            return true;
        }
        if (!(comparedObject instanceof ConversionRow)) {
            return false;
        }

        ConversionRow comparedTable = (ConversionRow) comparedObject;

        //only need to check if the units are the same

        if (this.fromUnit.equals(comparedTable.fromUnit) &&
                this.toUnit.equals(comparedTable.toUnit)){
            return true;
        }

        return false;
    }
    @Override
    public int hashCode(){
        return Objects.hash(fromUnit,toUnit);
    }
}
