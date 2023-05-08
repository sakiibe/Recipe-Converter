import java.util.HashMap;

public class SystemTable {
    private String systemName;
    private int minWeight;
    private int fraction1;
    private int number1;
    private int fraction2;
    private int number2;

    HashMap<ConversionRow, Double> conversionMap;

    public SystemTable(String systemName, int minWeight, int fraction1, int number1){
        this.systemName=systemName;
        this.minWeight=minWeight;
        this.fraction1=fraction1;
        this.number1=number1;
        this.fraction2=0;
        this.number2=0;
        this.conversionMap=new HashMap<>();
    }
    public SystemTable(String systemName, int minWeight, int fraction1, int number1, int fraction2, int number2){
        this.systemName=systemName;
        this.minWeight=minWeight;
        this.fraction1=fraction1;
        this.fraction2=fraction2;
        this.number1=number1;
        this.number2=number2;
        this.conversionMap=new HashMap<>();
    }


    public String getSystemName() {
        return systemName;
    }

    public int getMinWeight() {
        return minWeight;
    }

    public int getFraction1() {
        return fraction1;
    }

    public int getNumber1() {
        return number1;
    }

    public int getFraction2() {
        return fraction2;
    }

    public int getNumber2() {
        return number2;
    }

    public HashMap<ConversionRow,Double> getConversionMap(){
        return this.conversionMap;
    }

    public void addToConversionMap(ConversionRow ct, Double conversionFactor){
        this.conversionMap.put(ct,conversionFactor);
    }
    @Override
    public boolean equals(Object comparedObject) {
        if (this == comparedObject) {
            return true;
        }
        if (!(comparedObject instanceof ConversionRow)) {
            return false;
        }

        SystemTable comparedTable = (SystemTable) comparedObject;

        //only need to check if the units are the same

        if (this.systemName.equals(comparedTable.systemName)){
            return true;
        }

        return false;
    }
}
