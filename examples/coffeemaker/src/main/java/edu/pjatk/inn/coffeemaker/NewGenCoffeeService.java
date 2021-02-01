package edu.pjatk.inn.coffeemaker;

import sorcer.service.Context;
import sorcer.service.ContextException;

import java.rmi.RemoteException;

public interface NewGenCoffeeService {

    public Context addPersonalRecipe(Context context) throws RemoteException, ContextException;

    public Context removePersonalRecipe(Context context) throws RemoteException, ContextException;

    public Context editPersonalRecipe(Context context) throws RemoteException, ContextException;

    public Context getPersonalRecipes(Context context) throws RemoteException, ContextException;

    public Context orderCoffee(Context context) throws RemoteException, ContextException;

    public Context openCoffeeContainer(Context context) throws RemoteException, ContextException;

    public Context confirmPayment(Context context) throws RemoteException, ContextException;
}
