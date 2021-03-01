package businesslogic.recipe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import ui.task.TaskManagement;

public class RecipeManager {
    Recipe currentRecipe;
    TaskManagement taskListPaneController;

    public RecipeManager() {
        Recipe.loadAllRecipes();
    }

    public ObservableList<Recipe> getRecipes() {
        return FXCollections.unmodifiableObservableList(Recipe.getAllRecipes());
    }

    public Recipe getCurrentRecipe() {
        return currentRecipe;
    }

    public void setCurrentRecipe(Recipe r) {
        currentRecipe = r;
    }
}
