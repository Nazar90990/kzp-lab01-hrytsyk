package ua.lpnu.kzp;

/**
 * Точка входу: делегує розбір параметрів і запуск контролера.
 */
public final class Main {

    private Main() {
    }

    /**
     * Точка входу до програми.
     *
     * @param args аргументи командного рядка
     */
    public static void main(String[] args) {
        System.exit(run(args));
    }

    static int run(String[] args) {
        try {
            CommandLineParser.Command command = CommandLineParser.parse(args);
            return switch (command.action()) {
                case HELP -> {
                    System.out.println(CommandLineParser.helpText());
                    yield 0;
                }
                case VERSION -> {
                    String build = System.getProperty("ci.build.number", "local");
                    System.out.printf("lab01 version %s (CI build: %s)%n", BuildVersion.current(), build);
                    yield 0;
                }
                case RUN -> new ApplicationController().run(command.input(), command.output());
            };
        } catch (IllegalArgumentException exception) {
            System.err.println("Помилка аргументів: %s".formatted(exception.getMessage()));
            return 2;
        }
    }
}