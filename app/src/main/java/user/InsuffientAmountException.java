package user;

/**
 * Created by victor on 10/9/2017.
 */

public class InsuffientAmountException extends Exception {

    public InsuffientAmountException (){
        super("Sorry, Please Recharge and try again!");
    }
}
