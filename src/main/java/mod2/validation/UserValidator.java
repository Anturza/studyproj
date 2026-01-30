package mod2.validation;

import mod2.Entities.User;
import mod2.validation.chain.AgeProcessor;
import mod2.validation.chain.CreationDateProcessor;
import mod2.validation.chain.EmailProcessor;
import mod2.validation.chain.NameProcessor;
import mod2.validation.chain.ValidationProcessor;

public class UserValidator {

    public static boolean validate(User currentUser) {

        ValidationProcessor filterChain = new AgeProcessor(
                new CreationDateProcessor(
                        new NameProcessor(
                                new EmailProcessor(null))));

        return filterChain.isClear(currentUser);
    }

}
