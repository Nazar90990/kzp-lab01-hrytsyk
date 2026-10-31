package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    void commandLineParserUsesDefaultPaths() {
        CommandLineParser.Command command = CommandLineParser.parse(new String[0]);

        assertEquals(CommandLineParser.Action.RUN, command.action());
        assertEquals(java.nio.file.Path.of("data", "input.csv"), command.input());
        assertEquals(java.nio.file.Path.of("out", "report.txt"), command.output());
    }

    @Test
    void commandLineParserReadsSpecifiedPaths() {
        CommandLineParser.Command command = CommandLineParser.parse(
                new String[]{"--input", "source.csv", "--output", "result.txt"});

        assertEquals(java.nio.file.Path.of("source.csv"), command.input());
        assertEquals(java.nio.file.Path.of("result.txt"), command.output());
    }

    @Test
    void commandLineParserRecognizesHelpAndVersionBeforeOtherOptions() {
        assertEquals(CommandLineParser.Action.HELP,
                CommandLineParser.parse(new String[]{"--unknown", "--help"}).action());
        assertEquals(CommandLineParser.Action.VERSION,
                CommandLineParser.parse(new String[]{"--unknown", "--version"}).action());
    }

    @Test
    void commandLineParserRejectsMissingPathsAndUnknownOptions() {
        assertThrows(IllegalArgumentException.class,
                () -> CommandLineParser.parse(new String[]{"--input"}));
        assertThrows(IllegalArgumentException.class,
                () -> CommandLineParser.parse(new String[]{"--other"}));
    }

    @Test
    void buildVersionIsLoadedFromTheMavenFilteredResource() {
        assertNotNull(BuildVersion.current());
        assertFalse(BuildVersion.current().isBlank());
    }

    @Test
    void parsesUkrainianMeterRecord() {
        UtilityRecord record = UtilityRecord.fromCsv("Електроенергія;2026-09-01;145.5;4.32");

        assertEquals("Електроенергія", record.meter());
        assertEquals(145.5, record.value());
        assertEquals(4.32, record.tariff());
    }

    @Test
    void calculatesVariantNineIndicators() {
        List<UtilityRecord> records = List.of(
                UtilityRecordParser.parse("Вода;2026-09-02;12.3;30.50"),
                UtilityRecordParser.parse("Газ;2026-09-03;45.0;7.96"));

        UtilityReportCalculator.Summary summary = UtilityReportCalculator.calculate(records);

        assertEquals(2, summary.validCount());
        assertEquals(57.3, summary.totalConsumption(), 0.000001);
        assertEquals(12.3 * 30.5 + 45.0 * 7.96, summary.totalCost(), 0.000001);
        assertEquals(45.0, summary.maximumConsumption());
    }

    @Test
    void rejectsMalformedRowsWithoutCrashingTheParser() {
        assertThrows(IllegalArgumentException.class,
                () -> UtilityRecordParser.parse("Газ;2026-09-03;помилка;7.96"));
        assertThrows(IllegalArgumentException.class,
                () -> UtilityRecordParser.parse("Газ;2026-09-03;-1;7.96"));
        assertThrows(IllegalArgumentException.class,
                () -> UtilityRecordParser.parse("Газ;03.09.2026;45;7.96"));
        assertThrows(IllegalArgumentException.class,
                () -> UtilityRecordParser.parse("Газ;2026-09-03;45"));
    }

    @Test
    void constructorRejectsNullEmptyAndInvalidValues() {
        assertThrows(IllegalArgumentException.class,
                () -> new UtilityRecord(null, "2026-09-01", 1.0, 1.0));
        assertThrows(IllegalArgumentException.class,
                () -> new UtilityRecord(" ", "2026-09-01", 1.0, 1.0));
        assertThrows(IllegalArgumentException.class,
                () -> new UtilityRecord("Вода", "", 1.0, 1.0));
        assertThrows(IllegalArgumentException.class,
                () -> new UtilityRecord("Вода", "2026-09-01", -1.0, 1.0));
        assertThrows(IllegalArgumentException.class,
                () -> new UtilityRecord("Вода", "2026-09-01", 1.0, -1.0));
        assertThrows(IllegalArgumentException.class,
                () -> new UtilityRecord("Вода", "2026-09-01", Double.NaN, 1.0));
        assertThrows(IllegalArgumentException.class,
                () -> new UtilityRecord("Вода", "2026-09-01", 1.0, Double.POSITIVE_INFINITY));
    }

    @Test
    void factoryRejectsWrongFieldCountAndInvalidNumbers() {
        assertThrows(IllegalArgumentException.class,
                () -> UtilityRecord.fromCsv("Вода;2026-09-01;1"));
        assertThrows(IllegalArgumentException.class,
                () -> UtilityRecord.fromCsv("Вода;2026-09-01;помилка;2"));
        assertThrows(IllegalArgumentException.class,
                () -> UtilityRecord.fromCsv("Вода;2026-09-01;1;-2"));
        assertThrows(IllegalArgumentException.class,
                () -> UtilityRecord.fromCsv("Вода;01.09.2026;1;2"));
    }

    @Test
    void utilityRecordProvidesValueAccessorsAndStableValueSemantics() {
        UtilityRecord first = new UtilityRecord("Вода", "2026-09-01", 12.3, 30.5);
        UtilityRecord second = UtilityRecord.fromCsv("Вода;2026-09-01;12.3;30.5");

        assertEquals("Вода", first.getMeter());
        assertEquals("2026-09-01", first.getDate());
        assertEquals(12.3, first.getValue());
        assertEquals(30.5, first.getTariff());
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.toString().contains("value=12.30"));
        assertFalse(first.toString().contains("average"));
    }

    @Test
    void summaryRecordHasValueSemantics() {
        UtilityReportCalculator.Summary first = new UtilityReportCalculator.Summary(1, 2.0, 3.0, 2.0);
        UtilityReportCalculator.Summary second = new UtilityReportCalculator.Summary(1, 2.0, 3.0, 2.0);

        assertEquals(first, second);
        assertEquals(1, first.validCount());
        assertEquals(3.0, first.totalCost());
    }

    @Test
    void formatsNumbersWithDotAndTwoDecimalPlaces() {
        UtilityReportCalculator.Summary summary = UtilityReportCalculator.calculate(
                List.of(UtilityRecordParser.parse("Вода;2026-09-02;12.3;30.50")));

        String report = ReportFormatter.format(summary, List.of("Рядок 2: тестова помилка"));

        org.junit.jupiter.api.Assertions.assertTrue(report.contains("Сумарне споживання: 12.30"));
        org.junit.jupiter.api.Assertions.assertTrue(report.contains("Загальна вартість: 375.15 грн"));
        org.junit.jupiter.api.Assertions.assertFalse(report.contains("Середнє споживання"));
        org.junit.jupiter.api.Assertions.assertTrue(report.contains("Рядок 2: тестова помилка"));
    }
}