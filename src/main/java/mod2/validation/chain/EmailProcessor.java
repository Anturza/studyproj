package mod2.validation.chain;

import mod2.entities.User;

public class EmailProcessor extends ValidationProcessor {

    public EmailProcessor(ValidationProcessor authProcessor) {
        super(authProcessor);
    }

    @Override
    public boolean isClear(User user) {
        super.isClear(user);

        if (!user.getEmail().matches("^$|^[\\w-\\.]+@[\\w-]+(\\.[\\w-]+)*\\.[a-z]{2,}$")) {
            return false;
        }

        if (nextProcessor == null) {
            return true;
        } else {
            return nextProcessor.isClear(user);
        }
    }
}
