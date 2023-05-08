import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static java.lang.Integer.MAX_VALUE;

public class RecipeBook {

    List<SystemTable> systemTableList;
    List<RecipeEntry> recipeEntryList;

    public RecipeBook() {
        this.systemTableList = new ArrayList<>();
        this.recipeEntryList = new ArrayList<>();
    }

    boolean unitConversion(BufferedReader unitMatches) throws IOException {
        try {
            String line;

            int count = 0;

            boolean flag = true;

            while ((line = unitMatches.readLine()) != null) {

                String parts[] = line.split(" ");

                //read system measurement lines and create SystemTable object to hold their values
                if (count < 2) {

                    //checks if measurement system has an upper rounding integer value
                    if (parts.length == 4 || parts.length == 6) {
                        if (parts.length == 4) {

                            systemTableList.add(new SystemTable(parts[0], Integer.valueOf(parts[1]), Integer.valueOf(parts[2]),
                                    Integer.valueOf(parts[3])));

                            //checks if non-negative integers are added
                            if (Integer.valueOf(parts[1]) < 0 || Integer.valueOf(parts[2]) < 0 || Integer.valueOf(parts[3]) < 0) {
                                System.out.println("enter non-negative integers");
                                return false;
                            }
                        } else {

                            systemTableList.add(new SystemTable(parts[0], Integer.valueOf(parts[1]), Integer.valueOf(parts[2]),
                                    Integer.valueOf(parts[3]), Integer.valueOf(parts[4]), Integer.valueOf(parts[5])));


                            //checks if non-negative integers are added
                            if (Integer.valueOf(parts[1]) < 0 || Integer.valueOf(parts[2]) < 0 || Integer.valueOf(parts[3]) < 0 ||
                                    Integer.valueOf(parts[4]) < 0 || Integer.valueOf(parts[5]) < 0) {
                                System.out.println("enter non-negative integers");
                                return false;
                            }

                            if (Integer.valueOf(parts[1]) < 0 || Integer.valueOf(parts[2]) < 0 || Integer.valueOf(parts[3]) < 0) {
                                System.out.println("enter non-negative integers");
                                return false;
                            }
                        }

                        count++;
                        continue;
                    } else {
                        System.out.println("Please use the following format for measurement system lines: <system_name> " +
                                "<minimum_weight> <fraction1> <integer1> [<fraction2> <integer2>]");
                    }
                }
                //if both system names are the same
                if (systemTableList.get(0).getSystemName().equals(systemTableList.get(1).getSystemName())) {
                    System.out.println("Enter different measurement systems");
                    return false;
                }

                for (int i = 0; i < 2; i++) {

                    //add the unit conversion lines
                    if (!addConversionRow(parts, i)) {
                        flag = false;
                    }
                }
            }
            return flag;
        }
        //catches null pointer exceptions
        catch (NullPointerException e) {
            System.out.println("Please follow unit file format");
            return false;
        }
    }

    boolean addConversionRow(String[] parts, int i) {
        boolean flag = true;
        int fromQty, toQty;
        String fromUnit, toUnit;

        //checks if row of conversion has fewer than 4 fields
        if (parts.length < 4) {
            return false;
        }

        //For first(topmost entry on the file) measurement system, the first and second value
        // would be its quantity and unit and the third, fourth values would be the system it's converting to
        if (i == 0) {
            fromQty = Integer.valueOf(parts[2]);
            fromUnit = parts[3];
            toQty = Integer.valueOf(parts[0]);
            toUnit = parts[1];

            //For second measurement system, the third and fourth value would be its quantity and unit and the
            // first, second values would be the measurement system it's converting to
        } else {

            fromQty = Integer.valueOf(parts[0]);
            fromUnit = parts[1];
            toQty = Integer.valueOf(parts[2]);
            toUnit = parts[3];
        }


        ConversionRow currentRow = new ConversionRow(fromUnit, toUnit, fromQty, toQty);
        HashMap<ConversionRow, Double> conversionMap = systemTableList.get(i).getConversionMap();

        //if the map is empty, put the first value in
        if (conversionMap.size() == 0) {
            conversionMap.put(currentRow, currentRow.conversionFactor());

        }
        //if same conversion does not exist (like 240 ml = 1 cup, 250 ml -> 1 cup) put it in map
        if (!conversionMap.containsKey(currentRow)) {
            conversionMap.put(currentRow, currentRow.conversionFactor());
        } else {

            //if same conversion exist in map, check for variance.
            //checks if the same conversion between two units are already in the map
            for (ConversionRow trueRow : conversionMap.keySet()) {
                if (trueRow.getFromUnit().equals(currentRow.getFromUnit()) &&
                        trueRow.getToUnit().equals(currentRow.getToUnit())) {


                    double variance = checkVariance(trueRow.getFromQty(), trueRow.getToQty(),
                            currentRow.getFromQty(), currentRow.getToQty());

                    if (variance <= .05) {
                        conversionMap.put(currentRow, currentRow.conversionFactor());
                    } else {
                        flag = false;
                    }
                }
            }
        }


        return flag;
    }


    Boolean recipe(String originalMeasurementSystem, BufferedReader recipeContent) throws IOException {


        //the first line of the file contains the name

        String line = recipeContent.readLine();

        if (line.length() == 0) {
            System.out.println("Please enter a title for your recipe");
            return false;
        }

        //pointer to differentiate between ingredient list and instruction section
        int section = 0;

        //creates a recipeEntry object with its name and measurement system
        RecipeEntry entry = new RecipeEntry(line, originalMeasurementSystem);

        //adds the new object in RecipeBook's recipeEntryList
        recipeEntryList.add(entry);

        //Iterates over the remaining lines of recipeContent file
        while ((line = recipeContent.readLine()) != null) {

            //increment section on blank lines
            if (line.length() == 0) {
                line = recipeContent.readLine();
                section++;
            }
            //section=1 is the ingredient list section
            if (section == 1) {
                //split ingredient lines on tab-spaces
                String parts[] = line.split("\t");
                double quantity;
                if (parts.length != 3) {
                    System.out.println("Please follow ingredient format, separate fields using tab");
                    return false;
                }
                //if the quantity contains an integer and a fraction
                if (parts[0].matches("^\\d+\\s+\\d+\\/\\d+$")) {
                    String[] values = parts[0].split(" |\\/");
                    int number = Integer.valueOf(values[0]);
                    double numerator = Double.valueOf(values[1]);
                    double denominator = Double.valueOf(values[2]);

                    quantity = toDouble(number, numerator, denominator);

                    //if the quantity contains only a fraction
                } else if (parts[0].matches("\\d+\\/\\d+")) {
                    String[] values = parts[0].split("\\/");

                    double numerator = Double.valueOf(values[0]);
                    double denominator = Double.valueOf(values[1]);
                    quantity = toDouble(0, numerator, denominator);

                    //if the quantity contains an integer or a double
                } else {
                    //if the quantity is in integer or a double
                    quantity = Double.valueOf(parts[0]);
                }
                String unit = parts[1];
                String ingredient = parts[2];
                //creates new IngredientRow object with args quantity, unit and ingredient
                IngredientRow newRow = new IngredientRow(quantity, unit, ingredient);

                //add the new object in current RecipeEntry's list
                entry.getIngredientRowList().add(newRow);
            }

            //instruction section
            if (section == 2) {
                entry.setInstructionLine(line);
            }

        }
        return true;
    }


    List<String> availableUnits() {
        ArrayList<String> availableUnitsList = new ArrayList<>();

        //creates a list of measurement system names from SystemTable objects stored in systemTableList
        for (SystemTable ut : systemTableList) {
            availableUnitsList.add(ut.getSystemName());
        }
        return availableUnitsList;
    }

    List<String> availableRecipes() {
        ArrayList<String> availableRecipeList = new ArrayList<>();

        //creates a list of recipes
        for (RecipeEntry entry : recipeEntryList) {
            availableRecipeList.add(entry.getRecipeName());
        }
        return availableRecipeList;
    }

    int convert(String recipeName, String targetMeasurementSystem, double scaleFactor, PrintWriter convertedRecipe) {

        try {

            RecipeEntry recipe = null;
            int returnFlag = 0;

            //search recipe from the recipeEntryList
            for (RecipeEntry r : recipeEntryList) {
                if (r.getRecipeName().equals(recipeName)) {
                    recipe = r;
                }
            }
            //print ingredient lines


            returnFlag = convertIngredientSection(targetMeasurementSystem, scaleFactor, convertedRecipe, recipeName, recipe);

            //print instruction line
            convertedRecipe.println();
            String instructionLine = recipe.getInstructionLine();

            String[] parts = instructionLine.split(" ");

            for (int i = 0; i < parts.length; i++) {
                try {
                    double qty = Double.parseDouble(parts[i]);
                    String unit = parts[i + 1];
                    String convertedString = convertInstructionSection(targetMeasurementSystem, qty, unit, scaleFactor);
                    if (convertedString.equals(" ")){
                        convertedRecipe.print(parts[i]+" "+parts[i+1]+" ");
                    } else {
                        convertedRecipe.print(convertedString + " ");
                    }
                    i++;
                } catch (NumberFormatException e) {
                    convertedRecipe.print(parts[i] + " ");
                }

            }


            convertedRecipe.close();
            return returnFlag;
        } catch (Exception e) {
            return 2;
        }
    }

    int convertIngredientSection(String targetMeasurementSystem, double scaleFactor, PrintWriter convertedRecipe, String recipeName, RecipeEntry recipe) {

        int returnFlag = 0;

        for (SystemTable targetSystem : systemTableList) {

            //print recipe title with new measurement system in parentheses
            if (targetSystem.getSystemName().equals(targetMeasurementSystem)) {
                convertedRecipe.println(recipeName + " (" + targetMeasurementSystem + ")\n");

                //measurement system lines
                int minWeight = targetSystem.getMinWeight();
                int fraction1 = targetSystem.getFraction1();
                int number1 = targetSystem.getNumber1();
                int fraction2 = targetSystem.getFraction2();
                int number2 = targetSystem.getNumber2();

                //recipe ingredient section
                for (IngredientRow ingredientRow : recipe.getIngredientRowList()) {
                    double recipeQty = ingredientRow.getQuantity();
                    String qty = "";
                    String qtyFraction = "";
                    HashMap<ConversionRow, Double> conversionMap = targetSystem.getConversionMap();

                    //hold minimum integer if converted value in greater than 1
                    int minInteger = MAX_VALUE;

                    //hold minimum fraction if converted value in less than 1
                    double maxFraction = -1;

                    int denominator = targetSystem.getFraction1();
                    String unit = "";
                    String unitFraction = "";
                    String unitInteger = "";
                    String ingredient = ingredientRow.getIngredient();

                    boolean flagRound = false;
                    boolean flagOriginal = false;

                    for (ConversionRow conversionRow : conversionMap.keySet()) {
                        if (conversionRow.getFromUnit().equals(ingredientRow.getUnit())) {


                            //calculate the converted value
                            double doubleQty = scaleFactor * conversionRow.conversionFactor() * recipeQty;

                            //if double value is less than or equal to minimum weight, represent it in fraction
//                            if (doubleQty<= minWeight) {
                            if (minWeight == 0 || doubleQty <= minWeight) {

                                String stringQty = toFraction(doubleQty, fraction1);

                                //if value has an integer and a fraction, print lowest value and corresponding unit
                                if (stringQty.matches("^\\d+\\s+\\d+\\/\\d+$")) {
                                    String[] parts = stringQty.split(" |\\/");
                                    int currentInteger = Integer.valueOf(parts[0]);
                                    if (currentInteger < minInteger && currentInteger > 0) {
                                        minInteger = currentInteger;
                                        qty = stringQty;
                                        unit = conversionRow.getToUnit();
                                        ingredient = ingredientRow.getIngredient();
                                    }
                                }
                                //if value has an integer and a fraction, print lowest value and corresponding unit
                                else if (stringQty.matches("\\d+\\/\\d+")) {
                                    if (doubleQty > maxFraction) {
                                        qtyFraction = stringQty;
                                        unitFraction = conversionRow.getToUnit();
                                        ingredient = ingredientRow.getIngredient();
                                    }
                                }
                            }
                            //rounding value if it's greater than minimum weight
                            else if (doubleQty > minWeight) {
                                flagRound = true;

                                int intQty = (int) (number2 * (Math.round(doubleQty / number2)));

                                //if the value is rounded to 0, change it to the ceiling value
                                if (intQty == 0) {
                                    intQty = (int) (number2 * (Math.ceil(doubleQty / number2)));
                                }

                                //check variance after rounding
                                double variance = checkVariance(doubleQty, intQty);

                                if (variance > .05) {
                                    returnFlag = 1;
                                }

                                convertedRecipe.println(intQty + "\t" + conversionRow.getToUnit() + "\t" + ingredient);
                            }

                        } //if we need to print in same units
                        if (conversionRow.getToUnit().equals(ingredientRow.getUnit())) {
                            flagOriginal = true;
                        }

                    }
                    //print to same units with scaling
                    if (flagOriginal == true) {

                        String scaledValue = toFraction(ingredientRow.getQuantity() * scaleFactor, denominator);
                        convertedRecipe.println(scaledValue + "\t" + ingredientRow.getUnit() + "\t" + ingredientRow.getIngredient());
                    }
                    if (flagRound == false) {
                        if (minInteger != MAX_VALUE) {
                            convertedRecipe.println(qty + "\t" + unit + "\t" + ingredient);
                        } else if (maxFraction != -1) {
                            convertedRecipe.println(qtyFraction + "\t" + unitFraction + "\t" + ingredient);
                        }
                    }
                }
            }

        }
        return returnFlag;
    }

    String convertInstructionSection(String targetMeasurementSystem, double recipeQty, String fromUnit, double scaleFactor) {
        for (SystemTable targetSystem : systemTableList) {

            if (targetSystem.getSystemName().equals(targetMeasurementSystem)) {

                //measurement system lines
                int minWeight = targetSystem.getMinWeight();
                int fraction1 = targetSystem.getFraction1();
                int number1 = targetSystem.getNumber1();
                int fraction2 = targetSystem.getFraction2();
                int number2 = targetSystem.getNumber2();


                String qty = "";
                String qtyFraction = "";
                HashMap<ConversionRow, Double> conversionMap = targetSystem.getConversionMap();


                //hold minimum integer if converted value in greater than 1
                int minInteger = MAX_VALUE;

                //hold minimum fraction if converted value in less than 1
                double maxFraction = -1;

                int denominator = targetSystem.getFraction1();
                String unit = "";
                String unitFraction = "";
                String unitInteger = "";

                boolean flagRound = false;
                boolean flagOriginal = false;

                for (ConversionRow conversionRow : conversionMap.keySet()) {
                    if (conversionRow.getFromUnit().equals(fromUnit)) {


                        //calculate the converted value
                        double doubleQty = scaleFactor * conversionRow.conversionFactor() * recipeQty;

                        //if double value is less than or equal to minimum weight, represent it in fraction
//                            if (doubleQty<= minWeight) {
                        if (minWeight == 0 || doubleQty <= minWeight) {

                            String stringQty = toFraction(doubleQty, fraction1);

                            //if value has an integer and a fraction, print lowest value and corresponding unit
                            if (stringQty.matches("^\\d+\\s+\\d+\\/\\d+$")) {
                                String[] parts = stringQty.split(" |\\/");
                                int currentInteger = Integer.valueOf(parts[0]);
                                if (currentInteger < minInteger && currentInteger > 0) {
                                    minInteger = currentInteger;
                                    qty = stringQty;
                                    unit = conversionRow.getToUnit();
                                }
                            }
                            //if value has an integer and a fraction, print lowest value and corresponding unit
                            else if (stringQty.matches("\\d+\\/\\d+")) {
                                if (doubleQty > maxFraction) {
                                    qtyFraction = stringQty;
                                    unitFraction = conversionRow.getToUnit();
                                }
                            }
                        }
                        //rounding value if it's greater than minimum weight
                        else if (doubleQty > minWeight) {
                            flagRound = true;

                            int intQty = (int) (number2 * (Math.round(doubleQty / number2)));

                            //if the value is rounded to 0, change it to the ceiling value
                            if (intQty == 0) {
                                intQty = (int) (number2 * (Math.ceil(doubleQty / number2)));
                            }

                            //check variance after rounding
                            double variance = checkVariance(doubleQty, intQty);


                            return (intQty + " " + conversionRow.getToUnit());
                        }

                    } //if we need to print in same units
                    if (conversionRow.getToUnit().equals(fromUnit)) {
                        flagOriginal = true;
                    }


                    //print to same unit with scaling
                    if (flagOriginal == true) {

                        String scaledValue = toFraction(recipeQty * scaleFactor, denominator);
                        return (scaledValue + " " + fromUnit);
                    }
                    if (flagRound == false) {
                        if (minInteger != MAX_VALUE) {
                            return (qty + " " + unit);
                        } else if (maxFraction != -1) {
                            return (qtyFraction + " " + unitFraction);
                        }
                    }
                }
            }

        }
        return " ";
    }

    //return variance in conversion lines
    double checkVariance(double fromQty1, double toQty1, double fromQt2, double toQt2) {

        fromQt2 = fromQt2 * (toQty1 / toQt2);

        double variance = (Math.abs(fromQty1 - fromQt2)) / fromQty1;
        return variance;
    }
    //return variance between true value and rounded value
    double checkVariance(double double1, int int1) {
        double variance = Math.abs(double1 - int1);
        return variance / double1;
    }

    double toDouble(int number, double numerator, double denominator) {


        double fraction = numerator / denominator;
        return (number + fraction);
    }

    String toFraction(double qty, int denominator) {
        int integer = (int) qty;

        // convert double to fraction
        double decimal = qty - integer;
        int numerator = (int) (denominator * decimal);

        //calculate gcd
        int gcd = GCD(numerator, denominator);
        numerator = numerator / gcd;
        denominator = denominator / gcd;

        if (numerator != 0) {
            if (integer != 0) {
                String fraction = integer + " " + numerator + "/" + denominator;
                return fraction;
            } else {
                //when integer is 0, only return numerator and denominator
                String fraction = numerator + "/" + denominator;
                return fraction;
            }
        } else {
            //when numerator is 0, only return the integer
            String fraction = "" + integer;
            return fraction;
        }

    }

    int GCD(int a, int b) {
        if (b == 0) {
            return a;
        }
        return GCD(b, a % b);
    }


}
