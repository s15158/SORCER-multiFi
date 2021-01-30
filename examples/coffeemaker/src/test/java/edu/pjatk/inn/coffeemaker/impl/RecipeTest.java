package edu.pjatk.inn.coffeemaker.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sorcer.test.ProjectContext;
import org.sorcer.test.SorcerTestRunner;
import sorcer.core.context.ServiceContext;
import sorcer.service.Context;
import sorcer.service.ContextException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SorcerTestRunner.class)
@ProjectContext("examples/coffeemaker")
public class RecipeTest {

    private static final String RECIPE_NAME = "Name";
    private static final int RECIPE_PRICE = 12;
    private static final int RECIPE_COFFEE_AMOUNT = 11;
    private static final int RECIPE_MILK_AMOUNT = 13;
    private static final int RECIPE_SUGAR_AMOUNT = 25;
    private static final int RECIPE_CHOCOLATE_AMOUNT = 37;

    @Test
    public void testGetRecipe() throws ContextException {
        Context context = getRecipeContext();
        Recipe recipe = getRecipe();

        assertTrue(areRecipesEqual(recipe, Recipe.getRecipe(context)));
    }

    @Test
    public void testGetContext() throws ContextException {
        Context context = getRecipeContext();
        Recipe recipe = getRecipe();

        assertEquals(context, Recipe.getContext(recipe));
    }

    private boolean areRecipesEqual(Recipe recipe1, Recipe recipe2) {
        return recipe1.getName().equals(recipe2.getName()) &&
                recipe1.getPrice() == recipe2.getPrice() &&
                recipe1.getAmtCoffee() == recipe2.getAmtCoffee() &&
                recipe1.getAmtMilk() == recipe2.getAmtMilk() &&
                recipe1.getAmtSugar() == recipe2.getAmtSugar() &&
                recipe1.getAmtChocolate() == recipe2.getAmtChocolate();
    }

    private Context getRecipeContext() throws ContextException {
        Context context = new ServiceContext<>();
        context.putValue("key", RECIPE_NAME);
        context.putValue("price", RECIPE_PRICE);
        context.putValue("amtCoffee", RECIPE_COFFEE_AMOUNT);
        context.putValue("amtMilk", RECIPE_MILK_AMOUNT);
        context.putValue("amtSugar", RECIPE_SUGAR_AMOUNT);
        context.putValue("amtChocolate", RECIPE_CHOCOLATE_AMOUNT);
        return context;
    }

    private Recipe getRecipe() {
        Recipe recipe = new Recipe();
        recipe.setName(RECIPE_NAME);
        recipe.setPrice(RECIPE_PRICE);
        recipe.setAmtCoffee(RECIPE_COFFEE_AMOUNT);
        recipe.setAmtMilk(RECIPE_MILK_AMOUNT);
        recipe.setAmtSugar(RECIPE_SUGAR_AMOUNT);
        recipe.setAmtChocolate(RECIPE_CHOCOLATE_AMOUNT);
        return recipe;
    }
}