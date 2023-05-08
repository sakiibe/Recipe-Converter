public class IngredientRow {
    private double quantity;
    private String unit;
    private String ingredient;

    public IngredientRow(double quantity, String unit ,String ingredient){
        this.quantity=quantity;
        this.unit=unit;
        this.ingredient=ingredient;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String getIngredient() {
        return ingredient;
    }
}
