package ua.lpnu.kzp;

import java.nio.file.Path;

/** Розбирає параметри командного рядка програми. */
final class CommandLineParser {

    private CommandLineParser() {
    }

    static Command parse(String[] args) {
        if (contains(args, "--version")) {
            return new Command(Action.VERSION, null, null);
        }
        if (contains(args, "--help")) {
            return new Command(Action.HELP, null, null);
        }

        Path input = Path.of("data", "input.csv");
        Path output = Path.of("out", "report.txt");
        for (int index = 0; index < args.length; index++) {
            if ("--input".equals(args[index]) || "--output".equals(args[index])) {
                if (index + 1 >= args.length || args[index + 1].startsWith("--")) {
                    throw new IllegalArgumentException("після %s потрібен шлях".formatted(args[index]));
                }
                String option = args[index++];
                Path path = Path.of(args[index]);
                if ("--input".equals(option)) {
                    input = path;
                } else {
                    output = path;
                }
            } else {
                throw new IllegalArgumentException("невідомий параметр: %s".formatted(args[index]));
            }
        }
        return new Command(Action.RUN, input, output);
    }

    static String helpText() {
        return """
                Використання: java -jar <застосунок.jar> [--help] [--version] [--input <файл>] [--output <файл>]
                За замовчуванням: data/input.csv -> out/report.txt
                """;
    }

    private static boolean contains(String[] args, String value) {
        for (String argument : args) {
            if (value.equals(argument)) {
                return true;
            }
        }
        return false;
    }

    enum Action {
        HELP,
        VERSION,
        RUN
    }

    record Command(Action action, Path input, Path output) {
    }
}
