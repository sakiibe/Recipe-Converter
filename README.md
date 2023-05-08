# Recipe-Converter

Overview:
This program converts and scales recipes to different measurement systems. The recipes and
measurement systems are read from files, and it outputs the converted recipe in a separate new
file, ready to be used.
Files and External data:
o A1.java: Contains the main method from where the program is executed.
o RecipeBook.java: This class reads conversion files, recipe files and converts, scales
and rounds values.
o SystemTable.java: Stores the measurement system name and system measurement
lines.
o ConversionRow.java: Stores conversion lines between units.
o RecipeEntry.java: Stores the name of the recipe, it’s measurement system, the
instruction lines and uses a HashMap to hold the ingredient lines of the recipe
o IngridientRow.java: Stores the quantity, unit and the ingredient of ingredient lines.
Data structures and their relations to each other
To store unit conversions:
List<SystemTable> systemTableList: An ArrayList that stores systemTableList objects.
HashMap<ConversionRow, Double> conversionMap: This HashMap gets created each time a
new systemTableList is instantiated, or in other words, for every measurement systems. For key
it uses ConversionRow object where the conversion lines between units are stored and it stores
the conversion factor between one unit to another as the value.
To store recipes:
List<RecipeEntry> recipeEntryList: An ArrayList that stores the names of recipes and contains
another arraylist to store the ingredient lines.
ArrayList<IngredientRow> ingredientRowList: An ArrayList that stores ingredientRow objects
which stores the quantity, unit and ingredient after reading lines from the ingredient section. It
gets instantiated each time a new recipe is entered, or in other words, a new recipyEntry is
created.
Assumptions
The recipe file will not contain additional ingredients after the instruction line.
Choices
I have used two separate objects to store data when reading from unit conversion file. I used one object to store the system measurement lines and had a hashmap that keeps its associated conversion lines as key and the conversion factor between those to units as value.
I made the same design choice when storing recipes. The object contains the name and measurement system, and each ingredient line was stored in its own separate object and kept in a list that the first object contains.
Key algorithms and design elements
unitConversion:
o Read file until null, skip line if met with blank line
o Split line on spaces
o Store the first two lines in one object
o Store the conversion lines in a list in the previous object
o If same conversion exists, check if its variance
▪ If variance is more than 5%, return false
o Else add
Recipe:
o Read file until null
o Add the first line as recipe name, and create a new object with the name and measurement system
o Change to ingredient section upon meeting blank line
o Change quantity to double and store quantity, unit and ingredient in a new object
▪ Add the new object to first objects list
o Return true if successful, false if met with exception or if something went wrong
Convert:
o If recipyEntryList contains an object where it’s recipeName is the same as the recipeName received as parameter, print recipe name and the unit it is going to be converted to
o Iterate over the ingredientRowList of the recipe
o Iterate over conversionRowList of the associated measurement system
o If that conversion list has the unit that the recipe wants to convert to,
▪ Convert the quantities for each line in the conversion file
• If the converted value is greater than 1, store it’s minimum value
• If the converted value is less than 1, store it’s maximum value
▪ After finding the converted value, round or break it into fraction
▪ Print it to file
o Check if instruction line contains any digits
o If digit is found, check if the string after that string has any unit. If it contains units, convert it
o Print instruction line
Limitations:
This program cannot convert the units of a recipe through an intermediate conversion scale.

