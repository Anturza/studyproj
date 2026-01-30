package mod2.menu;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Menu {
    private String text;
    private Set<Handler> handlers;
    private String notHandledText;
    private Scanner sysin;
    private boolean isRunning;

    public Menu(String text, String notHandledText, Scanner sysin) {
        this.text = text;
        this.notHandledText = notHandledText;
        this.handlers = new HashSet<>();
        this.sysin = sysin;
        this.isRunning = false;
    }

    public void run() {
        isRunning = true;

        while (isRunning) {
            System.out.println(text);
            String input = sysin.nextLine().trim();

            boolean handled = false;
            for (Handler handler : handlers) {
                if (handler.handle(input)) {
                    handled = true;
                    break;
                }
            }

            if (!handled) {
                System.out.println(notHandledText);
            }
        }
    }

    public void stop() {
        isRunning = false;
    }

    public void addHandler(Handler handler) {
        handlers.add(handler);

    }
}
