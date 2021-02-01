package edu.pjatk.inn.coffeemaker;

import edu.pjatk.inn.coffeemaker.impl.Recipe;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sorcer.test.ProjectContext;
import org.sorcer.test.SorcerTestRunner;
import sorcer.core.provider.rendezvous.ServiceJobber;
import sorcer.service.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static sorcer.co.operator.*;
import static sorcer.co.operator.inVal;
import static sorcer.eo.operator.*;
import static sorcer.eo.operator.result;
import static sorcer.mo.operator.*;
import static sorcer.mo.operator.result;
import static sorcer.ent.operator.ent;
import static sorcer.ent.operator.invoker;
import static sorcer.so.operator.*;

@RunWith(SorcerTestRunner.class)
@ProjectContext("examples/coffeemaker")
public class NextGenCoffeeServiceTest {

    @Test
    public void testAddPersonalRecipe() throws Exception {

        Routine addRecipe = job("recipes",
                task("mocha", sig("addPersonalRecipe", NewGenCoffeeService.class), context(
                        ent("user/id", "s15204"),
                        ent("recipe/name", "mocha"),
                        ent("price", 100),
                        ent("amtCoffee", 8),
                        ent("amtMilk", 1),
                        ent("amtSugar", 1),
                        ent("amtChocolate", 2)
                )),
                task("macchiato", sig("addPersonalRecipe", NewGenCoffeeService.class), context(
                        ent("user/id", "s15204"),
                        ent("recipe/name", "macchiato"),
                        ent("price", 40),
                        ent("amtCoffee", 7),
                        ent("amtMilk", 1),
                        ent("amtSugar", 2),
                        ent("amtChocolate", 0)
                )));

        Context out = upcontext(exert(addRecipe));

        assertEquals(value(out, "coffeeMaker/recipe/s15204/mocha/added"), true);
        assertEquals(value(out, "coffeeMaker/recipe/s15204/macchiato/added"), true);
    }

    @Test
    public void testRemovePersonalRecipe() throws Exception {
        Routine addRecipe = job("recipes",
                task("mocha", sig("addPersonalRecipe", NewGenCoffeeService.class), context(
                        ent("user/id", "s15204"),
                        ent("recipe/name", "mocha"),
                        ent("price", 100),
                        ent("amtCoffee", 8),
                        ent("amtMilk", 1),
                        ent("amtSugar", 1),
                        ent("amtChocolate", 2)
                )));

        Context out = upcontext(exert(addRecipe));

        assertEquals(value(out, "coffeeMaker/recipe/s15204/mocha/added"), true);

        Routine removeRecipe = job("recipes",
                task("s15204/mocha", sig("removePersonalRecipe", NewGenCoffeeService.class), context(
                        ent("recipe/id","s15204/mocha")
                )));

        out = upcontext(exert(removeRecipe));

        assertEquals(value(out, "coffeeMaker/recipe/s15204/mocha/removed"), true);
    }

    @Test
    public void testOrderGlobalCoffee() throws Exception {

        Task orderCoffee = task("oc", sig("orderCoffee", NewGenCoffeeService.class), context(
                inVal("coffeeMaker/id", "lounge-3"),
                inVal("recipe/id","global/espresso"),
                inVal("user/id","s15158")
        ));

        Task confirmPayment = task("cp", sig("confirmPayment", NewGenCoffeeService.class), context(
                inVal("payment/type", "id"),
                inVal("transaction/id", "paypal-random-uuid")
        ));

        Task openContainer = task("op", sig("openCoffeeContainer", NewGenCoffeeService.class), context(
                inVal("coffeeMaker/id", "lounge-3")
        ));

        Job job = job("ordJb", sig("exert", ServiceJobber.class), orderCoffee, confirmPayment, openContainer,
                pipe(outPoint(orderCoffee, "order/id"), inPoint(confirmPayment, "order/id")),
                pipe(outPoint(confirmPayment, "order/id"), inPoint(openContainer, "order/id"))
        );

        Context out = upcontext(exert(job));

        assertEquals(value(out, "coffee/collected"), true);
        assertEquals(value(out, "recipe/id"), "global/espresso");
    }

    @Test
    public void testOrderPersonalCoffee() throws Exception {

        Task orderCoffee = task("oc", sig("orderCoffee", NewGenCoffeeService.class), context(
                inVal("coffeeMaker/id", "downstairs-2"),
                inVal("recipe/id","s15267/chocolate"),
                inVal("user/id","s15267")
        ));

        Task confirmPayment = task("cp", sig("confirmPayment", NewGenCoffeeService.class), context(
                inVal("payment/type", "creditCard"),
                inVal("transaction/id", "mastercard-random-uuid")
        ));

        Task openContainer = task("op", sig("openCoffeeContainer", NewGenCoffeeService.class), context(
                inVal("coffeeMaker/id", "downstairs-2")
        ));

        Job job = job("ordJb", sig("exert", ServiceJobber.class), orderCoffee, confirmPayment, openContainer,
                pipe(outPoint(orderCoffee, "order/id"), inPoint(confirmPayment, "order/id")),
                pipe(outPoint(confirmPayment, "order/id"), inPoint(openContainer, "order/id"))
        );

        Context out = upcontext(exert(job));

        assertEquals(value(out, "coffee/collected"), true);
        assertEquals(value(out, "recipe/id"), "s15267/chocolate");
    }
}
