package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Головний клас консольної програми для комунальних показників.
 */
public final class Main {

     private static final String VERSION = "1.0.0";

    private Main() {
        // Забороняє створення екземплярів службового класу.
    }

    /**
     * Точка входу до програми.
     *
     * @param args аргументи командного рядка
     */
    public static void main(String[] args) {
        System.exit(run(args));
    }

    private static int run(String[] args) {
        if (contains(args, "--version")) {
            String build = System.getProperty("ci.build.number", "local");
            System.out.printf("lab01 version %s (CI build: %s)%n", VERSION, build);
            return 0;
        }
        if (contains(args, "--help")) {
            printHelp();
            return 0;
        }

        CliOptions options;
        try {
            options = CliOptions.parse(args);
        } catch (IllegalArgumentException exception) {
            System.err.println("Помилка аргументів: %s".formatted(exception.getMessage()));
            return 2;
        }

        List<String> lines;
        try {
            lines = FileReport.readLines(options.input());
        } catch (IOException exception) {
            System.err.printf("Помилка читання файлу %s: %s%n", options.input(), exception.getMessage());
            return 1;
        }

        List<UtilityRecord> records = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        for (int index = 0; index < lines.size(); index++) {
            try {
                records.add(UtilityRecordParser.parse(lines.get(index)));
            } catch (IllegalArgumentException exception) {
                errors.add("Рядок %d: %s".formatted(index + 1, exception.getMessage()));
            }
        }

        UtilityReportCalculator.Summary summary = UtilityReportCalculator.calculate(records);
        String report = ReportFormatter.format(summary, errors);
        System.out.print(report);

        try {
            FileReport.writeReport(options.output(), report);
            System.out.printf("Звіт успішно записано у файл: %s%n", options.output());
        } catch (IOException exception) {
            System.err.printf("Помилка запису файлу звіту: %s%n", exception.getMessage());
            return 1;
        }
        return 0;
    }

    private static boolean contains(String[] args, String value) {
        for (String argument : args) {
            if (value.equals(argument)) {
                return true;
            }
        }
        return false;
    }

    private static void printHelp() {
        System.out.println("Використання: java -jar lab01-1.0.0.jar [--help] [--version] [--input <файл>] [--output <файл>]");
        System.out.println("За замовчуванням: data/input.csv -> out/report.txt");
    }

    private record CliOptions(Path input, Path output) {
        private static CliOptions parse(String[] args) {
            Path input = Path.of("data", "input.csv");
            Path output = Path.of("out", "report.txt");
            for (int index = 0; index < args.length; index++) {
                if ("--input".equals(args[index]) || "--output".equals(args[index])) {
                    if (index + 1 >= args.length || args[index + 1].startsWith("--")) {
                        throw new IllegalArgumentException("після %s потрібен шлях".formatted(args[index]));
                    }
                    Path path = Path.of(args[++index]);
                    if ("--input".equals(args[index - 1])) {
                        input = path;
                    } else {
                        output = path;
                    }
                } else {
                    throw new IllegalArgumentException("невідомий параметр: %s".formatted(args[index]));
                }
            }
            return new CliOptions(input, output);
        }
    }
}