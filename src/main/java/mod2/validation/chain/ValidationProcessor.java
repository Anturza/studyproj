package mod2.validation.chain;

import mod2.Entities.User;
/** Abstract class meant to be extended by various implementations for validation chain. Suppose, we want to validate
 {@link User} on: name is not blank, email matches some pattern, age in some range, creation date not from future.*/
public abstract class ValidationProcessor {

    protected ValidationProcessor nextProcessor;

    public ValidationProcessor(ValidationProcessor authProcessor) {
        this.nextProcessor = authProcessor;
    }

    public boolean isClear(User user) {
        System.out.printf("\u001b[33m%s invoked\n\u001b[0m", this.getClass().getName()); // logging imitation for check
                                                                                // on which validation step did it fell
        return false;
    }


}
