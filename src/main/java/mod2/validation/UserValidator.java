package mod2.validation;

import mod2.entities.User;
import mod2.validation.chain.AgeProcessor;
import mod2.validation.chain.CreationDateProcessor;
import mod2.validation.chain.EmailProcessor;
import mod2.validation.chain.NameProcessor;
import mod2.validation.chain.ValidationProcessor;
/** Utility class helps to validate User data by passing it through the chain of processors before writing in database*/
public class UserValidator {

    public static boolean validate(User currentUser) {

        ValidationProcessor filterChain = new AgeProcessor(
                new CreationDateProcessor(
                        new NameProcessor(
                                new EmailProcessor(null))));

        return filterChain.isClear(currentUser);
    }

}
