package edu.pjatk.inn.coffeemaker;

import edu.pjatk.inn.coffeemaker.impl.CoffeeMaker;
import edu.pjatk.inn.coffeemaker.impl.Inventory;
import edu.pjatk.inn.coffeemaker.impl.Recipe;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sorcer.test.ProjectContext;
import org.sorcer.test.SorcerTestRunner;
import sorcer.service.ContextException;
import sorcer.service.Routine;

import static org.junit.Assert.*;
import static sorcer.eo.operator.*;
import static sorcer.so.operator.exec;

/**
 * @author Mike Sobolewski
 */
@RunWith(SorcerTestRunner.class)
@ProjectContext("examples/coffeemaker")
public class CoffeeMakerTest {
    private final static Logger logger = LoggerFactory.getLogger(CoffeeMakerTest.class);

    private CoffeeMaker coffeeMaker;
    private Inventory inventory;
    private Recipe espresso, mocha, macchiato, americano;

    @Before
    public void setUp() throws ContextException {
        coffeeMaker = new CoffeeMaker();
        inventory = coffeeMaker.checkInventory();

        espresso = new Recipe();
        espresso.setName("espresso");
        espresso.setPrice(50);
        espresso.setAmtCoffee(6);
        espresso.setAmtMilk(1);
        espresso.setAmtSugar(1);
        espresso.setAmtChocolate(0);

        mocha = new Recipe();
        mocha.setName("mocha");
        mocha.setPrice(100);
        mocha.setAmtCoffee(8);
        mocha.setAmtMilk(1);
        mocha.setAmtSugar(1);
        mocha.setAmtChocolate(2);

        macchiato = new Recipe();
        macchiato.setName("macchiato");
        macchiato.setPrice(40);
        macchiato.setAmtCoffee(7);
        macchiato.setAmtMilk(1);
        macchiato.setAmtSugar(2);
        macchiato.setAmtChocolate(0);

        americano = new Recipe();
        americano.setName("americano");
        americano.setPrice(40);
        americano.setAmtCoffee(7);
        americano.setAmtMilk(1);
        americano.setAmtSugar(2);
        americano.setAmtChocolate(0);
    }

    @Test
    public void testAddRecipe() {
        assertTrue(coffeeMaker.addRecipe(espresso));
    }

    @Test
    public void testContextCoffee() throws ContextException {
        assertTrue(espresso.getAmtCoffee() == 6);
    }

    @Test
    public void testContextMilk() throws ContextException {
        assertTrue(espresso.getAmtMilk() == 1);
    }

    @Test
    public void addRecipe() throws Exception {
        coffeeMaker.addRecipe(mocha);
        assertEquals(coffeeMaker.getRecipeForName("mocha").getName(), "mocha");
    }

    @Test
    public void editRecipe() throws Exception {
        Recipe myRecipe = new Recipe();
        myRecipe.setName("favourite");
        myRecipe.setAmtCoffee(3);
        myRecipe.setAmtMilk(2);
        myRecipe.setAmtSugar(1);
        myRecipe.setAmtChocolate(3);

        coffeeMaker.addRecipe(espresso);
        coffeeMaker.addRecipe(mocha);

        // Recipe does not exist
        assertFalse(coffeeMaker.editRecipe(myRecipe, americano));

        // Recipe under chosen name already exists
        assertFalse(coffeeMaker.editRecipe(espresso, mocha));

        // Price and/or units of coffee/sugar/milk/chocolate are not positive
        myRecipe.setAmtCoffee(-3);
        myRecipe.setAmtMilk(-2);
        myRecipe.setAmtSugar(-1);
        myRecipe.setAmtChocolate(-3);

        assertFalse(coffeeMaker.editRecipe(espresso, myRecipe));
    }

    @Test
    public void deleteRecipe() throws Exception {
        coffeeMaker.addRecipe(espresso);
        assertEquals(coffeeMaker.getRecipeForName("espresso").getName(), "espresso");

        coffeeMaker.deleteRecipe(espresso);
        assertNull(coffeeMaker.getRecipeForName("espresso"));
    }

    @Test
    public void addInventory() throws Exception {
        int[] base = {inventory.getCoffee(), inventory.getMilk(), inventory.getSugar(), inventory.getChocolate()};
        int[] inv = {15, 10, 5, 2};

        coffeeMaker.addInventory(inv[0], inv[1], inv[2], inv[3]);
        int[] expected = {
                base[0] + inv[0],
                base[1] + inv[1],
                base[2] + inv[2],
                base[3] + inv[3]};
        int[] actual = {inventory.getCoffee(), inventory.getMilk(), inventory.getSugar(), inventory.getChocolate()};
        assertArrayEquals(expected, actual);

        assertFalse(coffeeMaker.addInventory(-inv[0], -inv[1], -inv[2], -inv[3]));
    }

    @Test
    public void addContextRecipe() throws Exception {
        coffeeMaker.addRecipe(Recipe.getContext(mocha));
        assertEquals(coffeeMaker.getRecipeForName("mocha").getName(), "mocha");
    }

    @Test
    public void addServiceRecipe() throws Exception {
        Routine cmt = task(sig("addRecipe", coffeeMaker),
                context(types(Recipe.class), args(espresso),
                        result("recipe/added")));

        logger.info("isAdded: " + exec(cmt));
        assertEquals(coffeeMaker.getRecipeForName("espresso").getName(), "espresso");
    }

    @Test
    public void addRecipes() throws Exception {
        coffeeMaker.addRecipe(mocha);
        coffeeMaker.addRecipe(macchiato);
        coffeeMaker.addRecipe(americano);

        assertEquals(coffeeMaker.getRecipeForName("mocha").getName(), "mocha");
        assertEquals(coffeeMaker.getRecipeForName("macchiato").getName(), "macchiato");
        assertEquals(coffeeMaker.getRecipeForName("americano").getName(), "americano");
        assertFalse(coffeeMaker.addRecipe(espresso));
    }

    @Test
    public void makeCoffee() throws Exception {
        int funds = 200;

        int[] base = {
                inventory.getCoffee(),
                inventory.getMilk(),
                inventory.getSugar(),
                inventory.getChocolate()};

        coffeeMaker.addRecipe(mocha);
        assertEquals(coffeeMaker.makeCoffee(mocha, funds), funds - mocha.getPrice());

        int[] expected = {
                inventory.getCoffee(),
                inventory.getMilk(),
                inventory.getSugar(),
                inventory.getChocolate()};

        int[] actual = {
                base[0] - mocha.getAmtCoffee(),
                base[1] - mocha.getAmtMilk(),
                base[2] - mocha.getAmtSugar(),
                base[3] - mocha.getAmtChocolate()};
        assertArrayEquals(expected, actual);

        assertEquals(coffeeMaker.makeCoffee(mocha, funds), funds);
    }

    @Test
    public void checkInventory() throws Exception {
        Inventory makerInventory = coffeeMaker.checkInventory();
        int[] actual = {
                makerInventory.getCoffee(),
                makerInventory.getMilk(),
                makerInventory.getSugar(),
                makerInventory.getChocolate()
        };

        int[] expected = {
                inventory.getCoffee(),
                inventory.getMilk(),
                inventory.getSugar(),
                inventory.getChocolate()
        };

        assertArrayEquals(expected, actual);
        assertEquals(coffeeMaker.checkInventory(), inventory);
    }

}

