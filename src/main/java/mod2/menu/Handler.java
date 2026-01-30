package mod2.menu;

import java.util.Objects;

public class Handler {
    Runnable action;
    protected String key;

    public Handler(String key, Runnable action) {
        this.key = key;
        this.action = action;
    }

    public boolean handle(String input) {
        if (input.equals(key)) {
            this.action.run();
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Handler handler = (Handler) o;
        return Objects.equals(key, handler.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }
}

