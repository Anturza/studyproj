package mod2.validation.chain;

import mod2.Entities.User;

import java.time.LocalDateTime;

public class CreationDateProcessor extends ValidationProcessor {

    public CreationDateProcessor(ValidationProcessor authProcessor) {
        super(authProcessor);
    }

    @Override
    public boolean isClear(User user) {
        super.isClear(user);
        LocalDateTime createdAt = user.getCreated();
        if (createdAt != null && createdAt.isAfter(LocalDateTime.now())) {
            return false;
        }

        if (nextProcessor == null) { // значит, что в цепочке больше нет проверяющих процессоров
            return true;
        } else {
            return nextProcessor.isClear(user); // иначе - передать юзера на проверку дальше
        }
    }
}
