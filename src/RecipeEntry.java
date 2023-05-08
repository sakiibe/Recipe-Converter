import java.util.ArrayList;

public class RecipeEntry {
    private String recipeName;
    private String originalMeasurementSystem;
    private ArrayList<IngredientRow> ingredientRowList;
    private String instructionLine;

    public RecipeEntry(String recipeName, String originalMeasurementSystem){
        this.recipeName=recipeName;
        this.originalMeasurementSystem=originalMeasurementSystem;
        this.ingredientRowList=new ArrayList<>();
    }

    public String getRecipeName(){
        return this.recipeName;
    }

    public String getOriginalMeasurementSystem() {
        return originalMeasurementSystem;
    }

    public String getInstructionLine() {
        return instructionLine;
    }

    public ArrayList<IngredientRow> getIngredientRowList(){
        return this.ingredientRowList;
    }


    public void setInstructionLine(String instruction){
        this.instructionLine=instruction;
    }

}
