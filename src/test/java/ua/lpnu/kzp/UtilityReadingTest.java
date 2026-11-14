package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class UtilityReadingTest {

    @Test
    void parsesBothRequiredSubtypesWithNormalizedLabelsAndEnumKinds() {
        UtilityReading electricity = UtilityReading.fromCsv("  ЕЛЕКТРОЕНЕРГІЯ ;2026-09-01;10;4.32");
        UtilityReading water = UtilityReading.fromCsv("water;2026-09-02;2;30.50");

        assertInstanceOf(ElectricityReading.class, electricity);
        assertInstanceOf(WaterReading.class, water);
        assertEquals(UtilityKind.ELECTRICITY, electricity.kind());
        assertEquals(UtilityKind.WATER, water.kind());
        assertEquals("кВт·год", electricity.kind().unit());
        assertEquals("м³", water.kind().unit());
    }

    @Test
    void eachSubtypeImplementsTheConsumptionCostOperation() {
        List<UtilityReading> readings = List.of(
                new ElectricityReading("Електроенергія", "2026-09-01", 10.0, 4.32),
                new WaterReading("Вода", "2026-09-02", 2.0, 30.50));

        assertEquals(43.2, readings.get(0).consumptionCost(), 0.000001);
        assertEquals(61.0, readings.get(1).consumptionCost(), 0.000001);

        UtilityReportCalculator.Summary summary = UtilityReportCalculator.calculate(readings);
        assertEquals(2, summary.validCount());
        assertEquals(12.0, summary.totalConsumption(), 0.000001);
        assertEquals(104.2, summary.totalCost(), 0.000001);
        assertEquals(10.0, summary.maximumConsumption());
    }

    @Test
    void rejectsSubtypeLabelsThatDoNotMatchTheirKind() {
        assertThrows(IllegalArgumentException.class,
                () -> new ElectricityReading("Вода", "2026-09-01", 1.0, 1.0));
        assertThrows(IllegalArgumentException.class,
                () -> new WaterReading("Електроенергія", "2026-09-01", 1.0, 1.0));
    }

    @Test
    void preservesUnknownLabelsAndTheLegacyUtilityRecordFactory() {
        UtilityReading gas = UtilityReading.fromCsv("Газ;2026-09-03;45.0;7.96");
        UtilityRecord legacyWater = UtilityRecord.fromCsv("Вода;2026-09-02;12.3;30.50");

        assertInstanceOf(UtilityRecord.class, gas);
        assertEquals(UtilityKind.OTHER, gas.kind());
        assertInstanceOf(UtilityRecord.class, legacyWater);
        assertEquals("UtilityRecord{meter='Вода', date='2026-09-02', value=12.30, tariff=30.50}",
                legacyWater.toString());
        assertEquals(45.0 * 7.96, gas.consumptionCost(), 0.000001);
    }

    @Test
    void equalityIsSymmetricSubtypeAwareAndConsistentInHashSet() {
        UtilityReading first = new WaterReading("Вода", "2026-09-02", 12.3, 30.50);
        UtilityReading duplicate = new WaterReading("Вода", "2026-09-02", 12.3, 30.50);
        UtilityReading differentSubtype = new UtilityRecord("Вода", "2026-09-02", 12.3, 30.50);
        UtilityReading differentValue = new WaterReading("Вода", "2026-09-02", 12.4, 30.50);

        assertTrue(first.equals(duplicate));
        assertTrue(duplicate.equals(first));
        assertEquals(first.hashCode(), duplicate.hashCode());
        assertFalse(first.equals(differentSubtype));
        assertFalse(differentSubtype.equals(first));
        assertFalse(first.equals(differentValue));
        assertEquals(2, new HashSet<>(List.of(first, duplicate, differentSubtype)).size());
        assertEquals(Set.of(first, differentSubtype), new HashSet<>(List.of(first, duplicate, differentSubtype)));
    }

    @Test
    void retainsSharedInvariantValidationAndParserErrorOrder() {
        assertThrows(IllegalArgumentException.class,
                () -> UtilityReading.fromCsv("Вода;2026-09-01;помилка;-1"));
        assertThrows(IllegalArgumentException.class,
                () -> UtilityReading.fromCsv("Вода;не-дата;-1;1"));
        assertThrows(IllegalArgumentException.class,
                () -> new WaterReading(" ", "2026-09-01", 1.0, 1.0));
        assertThrows(IllegalArgumentException.class,
                () -> new ElectricityReading("Електроенергія", "2026-09-01", Double.NaN, 1.0));
    }

    @Test
    void preservesTheControlFileReport() throws IOException {
        List<UtilityReading> readings = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        List<String> lines = FileReport.readLines(Path.of("data", "input.csv"));
        for (int index = 0; index < lines.size(); index++) {
            try {
                readings.add(UtilityReading.fromCsv(lines.get(index)));
            } catch (IllegalArgumentException exception) {
                errors.add("Рядок %d: %s".formatted(index + 1, exception.getMessage()));
            }
        }

        UtilityReportCalculator.Summary summary = UtilityReportCalculator.calculate(readings);
        assertEquals(3, summary.validCount());
        assertEquals(202.80, summary.totalConsumption(), 0.000001);
        assertEquals(1361.91, summary.totalCost(), 0.000001);
        assertEquals(145.50, summary.maximumConsumption(), 0.000001);
        assertEquals(2, errors.size());

        String expected = Files.readString(Path.of("out", "report-lab01-baseline.txt"));
        String actual = ReportFormatter.format(summary, errors);
        assertEquals(normalizeLineEndings(expected), normalizeLineEndings(actual));
    }

    private static String normalizeLineEndings(String text) {
        return text.replace("\r\n", "\n").replace('\r', '\n');
    }
}
