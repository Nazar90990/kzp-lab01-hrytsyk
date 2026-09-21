package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Головний клас для лабораторної роботи №1 (Варіант 9: Комунальні показники).
 */
public final class Main {

    private Main() {
        // Забороняє створення екземплярів службового класу.
    }

    /**
     * Точка входу до програми.
     *
     * @param args аргументи командного рядка
     */
    public static void main(String[] args) {
        if (args.length > 0 && "--help".equals(args[0])) {
            System.out.printf("Використання: java -jar lab01.jar [--help] [--input <файл>] [--output <файл>]%n");
            return;
        }

        Path inputPath = Path.of("data", "input.csv");
        Path outputPath = Path.of("out", "report.txt");

        // Перевірка аргументів командного рядка для входу/виходу
        for (int i = 0; i < args.length; i++) {
            if ("--input".equals(args[i]) && i + 1 < args.length) {
                inputPath = Path.of(args[i + 1]);
            } else if ("--output".equals(args[i]) && i + 1 < args.length) {
                outputPath = Path.of(args[i + 1]);
            }
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(inputPath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.printf("Помилка читання файлу %s: %s%n", inputPath, e.getMessage());
            return;
        }

        List<String> errors = new ArrayList<>();
        int validCount = 0;
        double totalConsumption = 0.0;
        double totalCost = 0.0;
        double maxConsumption = Double.NEGATIVE_INFINITY;

        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index).trim();
            if (line.isEmpty()) {
                continue;
            }

            // Формат варіанта 9: meter;value;date;tariff (4 поля)
            String[] fields = line.split(";", -1);
            if (fields.length != 4) {
                errors.add("Рядок %d: очікується 4 поля, отримано %d".formatted(index + 1, fields.length));
                continue;
            }

            if (fields[0].isBlank() || fields[2].isBlank()) {
                errors.add("Рядок %d: порожня назва лічильника або дата".formatted(index + 1));
                continue;
            }

            try {
                double value = Double.parseDouble(fields[1]);
                double tariff = Double.parseDouble(fields[3]);

                if (value < 0 || tariff < 0) {
                    errors.add("Рядок %d: від'ємне числове значення".formatted(index + 1));
                    continue;
                }

                validCount++;
                totalConsumption += value;
                totalCost += value * tariff;
                maxConsumption = Math.max(maxConsumption, value);

            } catch (NumberFormatException exception) {
                errors.add("Рядок %d: числове поле має помилковий формат".formatted(index + 1));
            }
        }

        if (validCount == 0) {
            maxConsumption = 0.0;
        }

        // Формування звіту
        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append(String.format(Locale.ROOT, "=== ЗВІТ (Варіант 9: Комунальні показники) ===%n"));
        reportBuilder.append(String.format(Locale.ROOT, "Коректних записів: %d%n", validCount));
        reportBuilder.append(String.format(Locale.ROOT, "Сумарне споживання: %.2f%n", totalConsumption));
        reportBuilder.append(String.format(Locale.ROOT, "Загальна вартість: %.2f грн%n", totalCost));
        reportBuilder.append(String.format(Locale.ROOT, "Найбільше споживання: %.2f%n", maxConsumption));
        reportBuilder.append(String.format(Locale.ROOT, "Помилок: %d%n", errors.size()));
        for (String err : errors) {
            reportBuilder.append(err).append(System.lineSeparator());
        }

        String report = reportBuilder.toString();

        // Вивід у консоль
        System.out.print(report);

        // Запис у файл звіту
        try {
            Path parent = outputPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(outputPath, report, StandardCharsets.UTF_8);
            System.out.printf("Звіт успішно записано у файл: %s%n", outputPath);
        } catch (IOException e) {
            System.err.printf("Помилка запису файлу звіту: %s%n", e.getMessage());
        }
    }
}