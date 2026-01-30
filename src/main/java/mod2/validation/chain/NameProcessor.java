package mod2.validation.chain;

import mod2.entities.User;

public class NameProcessor extends ValidationProcessor {
    public NameProcessor(ValidationProcessor authProcessor) {
        super(authProcessor);
    }

    @Override
    public boolean isClear(User user) {
        super.isClear(user);
        if (user.getName() == null || user.getName().toString().trim().isEmpty()) {
            return false;
        }

        if (nextProcessor == null) {
            return true;
        } else {
            return nextProcessor.isClear(user);
        }
    }
}
