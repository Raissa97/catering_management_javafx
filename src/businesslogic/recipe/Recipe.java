package businesslogic.recipe;

import businesslogic.CatERing;
import businesslogic.menu.Menu;
import businesslogic.menu.MenuItem;
import businesslogic.task.SummarySheet;
import businesslogic.task.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import persistence.PersistenceManager;
import persistence.ResultHandler;
import ui.task.TaskManagement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Recipe {
    private static Map<Integer, Recipe> allRecipes = new HashMap<>();

    Recipe currentRecipe;
    private int id;
    String name;

    private Recipe() {
    }

    public Recipe(String name) {
        id = 0;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Recipe getCurrentRecipe() {
        return currentRecipe;
    }

    public void setCurrentRecipe(Recipe r) {
        currentRecipe = r;
    }

    public String toString() {
        return name;
    }

    public  ObservableList<Task> getTasksOfRecipe() {
        return Task.loadTaskForRecipe(CatERing.getInstance().getEventManager().getCurrentEvent().getId(), id);
    }

    public static Recipe getRecipeFromId(int idRecipe){
        Recipe recipe = new Recipe();
        if(allRecipes.containsKey(idRecipe)){
             recipe = allRecipes.get(idRecipe);
        }
        return recipe;
    }

    // STATIC METHODS FOR PERSISTENCE

    public static ObservableList<Recipe> loadAllRecipes() {
        String query = "SELECT * FROM Recipes";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                if (allRecipes.containsKey(id)) {
                    Recipe rec = allRecipes.get(id);
                    rec.name = rs.getString("name");
                } else {
                    Recipe rec = new Recipe(rs.getString("name"));
                    rec.id = id;
                    allRecipes.put(rec.id, rec);
                }
            }
        });
        ObservableList<Recipe> recipes =  FXCollections.observableArrayList(allRecipes.values());
        Collections.sort(recipes, new Comparator<Recipe>() {
            @Override
            public int compare(Recipe o1, Recipe o2) {
                return (o1.getName().compareTo(o2.getName()));
            }
        });
        return recipes;
    }

    public static ObservableList<Recipe> getAllRecipes() {
        return FXCollections.observableArrayList(allRecipes.values());
    }

    public static Recipe loadRecipeById(int id) {
        if (allRecipes.containsKey(id)) return allRecipes.get(id);
        Recipe rec = new Recipe();
        String query = "SELECT * FROM Recipes WHERE id = " + id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                rec.name = rs.getString("name");
                rec.id = id;
                allRecipes.put(rec.id, rec);
            }
        });
        return rec;
    }

    public static List<Recipe> loadRecipesForMenu(int approved_menu) {
        List<MenuItem> mItems = MenuItem.loadItemsFor(approved_menu);
        List<Recipe> recipes = getAllRecipes();
        List<Recipe> recipesForMenu = FXCollections.observableArrayList();
        for(MenuItem mi: mItems) {
            for (Recipe r : recipes) {
               if(mi.getItemRecipe()==r){
                   recipesForMenu.add(r);
               }
            }
        }
        //System.out.println(recipesForMenu);
        return recipesForMenu;
    }

}
