package edu.pjatk.inn.coffeemaker;

import edu.pjatk.inn.coffeemaker.impl.Inventory;
import edu.pjatk.inn.coffeemaker.impl.Recipe;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sorcer.test.ProjectContext;
import org.sorcer.test.SorcerTestRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SorcerTestRunner.class)
@ProjectContext("examples/coffeemaker")
public class InventoryTest {
    private Inventory inventory;
    private Recipe espresso, sugarBomb;

    @Before
    public void setUp() {
        inventory = new Inventory();

        espresso = new Recipe();
        espresso.setName("espresso");
        espresso.setPrice(50);
        espresso.setAmtCoffee(6);
        espresso.setAmtMilk(1);
        espresso.setAmtSugar(1);
        espresso.setAmtChocolate(0);

        sugarBomb = new Recipe();
        sugarBomb.setName("sugar bomb");
        sugarBomb.setPrice(200);
        sugarBomb.setAmtCoffee(5);
        sugarBomb.setAmtMilk(5);
        sugarBomb.setAmtSugar(1000);
        sugarBomb.setAmtChocolate(5);
    }

    @Test
    public void testEnoughIngredients() {
        assertTrue(inventory.enoughIngredients(espresso));
        assertFalse(inventory.enoughIngredients(sugarBomb));
    }
}
