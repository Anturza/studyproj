package mod2.validation.chain;

import mod2.Entities.User;

public class AgeProcessor extends ValidationProcessor {

    public AgeProcessor(ValidationProcessor authProcessor) {
        super(authProcessor);
    }

    @Override
    public boolean isClear(User user) {
        super.isClear(user);
        int age = user.getAge();
        if (age < 0 || age > 150) {
            return false;
        }

        if (nextProcessor == null) { // значит, что в цепочке больше нет проверяющих процессоров
            return true;
        } else {
            return nextProcessor.isClear(user); // иначе - передать юзера на проверку дальше
        }
    }
}
