import java.io.*;
import java.util.Scanner;

public class A1 {

    public static void main(String[] args) throws IOException {

        Scanner sc= new Scanner(System.in);

        System.out.println("Enter unit conversion file name:");
        String unitFile=sc.nextLine();

        System.out.println("Enter recipe file name:");
        String recipe= sc.nextLine();

        BufferedReader unitReader= new BufferedReader(new FileReader(unitFile));
        BufferedReader recipeReader= new BufferedReader(new FileReader(recipe));


        PrintWriter recipeWriter= new PrintWriter(new FileWriter("Converted Recipe.txt"));
        RecipeBook r= new RecipeBook();
        r.unitConversion(unitReader);
        System.out.println("available units: "+ r.availableUnits());

        r.recipe("metric", recipeReader);
        System.out.println("available recipes: "+r.availableRecipes());
        System.out.println(r.convert("Paper-mache paste","imperial",2.0,recipeWriter));

    }
}
