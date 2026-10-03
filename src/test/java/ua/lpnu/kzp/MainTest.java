package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    void parsesUkrainianMeterRecord() {
        UtilityRecord record = UtilityRecordParser.parse("Електроенергія;2026-09-01;145.5;4.32");

        assertEquals("Електроенергія", record.meter());
        assertEquals(145.5, record.value());
        assertEquals(4.32, record.tariff());
    }

    @Test
    void calculatesFourIndicators() {
        List<UtilityRecord> records = List.of(
                UtilityRecordParser.parse("Вода;2026-09-02;12.3;30.50"),
                UtilityRecordParser.parse("Газ;2026-09-03;45.0;7.96"));

        UtilityReportCalculator.Summary summary = UtilityReportCalculator.calculate(records);

        assertEquals(2, summary.validCount());
        assertEquals(57.3, summary.totalConsumption(), 0.000001);
        assertEquals(12.3 * 30.5 + 45.0 * 7.96, summary.totalCost(), 0.000001);
        assertEquals(45.0, summary.maximumConsumption());
        assertEquals(28.65, summary.averageConsumption(), 0.000001);
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
    void formatsNumbersWithDotAndTwoDecimalPlaces() {
        UtilityReportCalculator.Summary summary = UtilityReportCalculator.calculate(
                List.of(UtilityRecordParser.parse("Вода;2026-09-02;12.3;30.50")));

        String report = ReportFormatter.format(summary, List.of("Рядок 2: тестова помилка"));

        org.junit.jupiter.api.Assertions.assertTrue(report.contains("Сумарне споживання: 12.30"));
        org.junit.jupiter.api.Assertions.assertTrue(report.contains("Загальна вартість: 375.15 грн"));
        org.junit.jupiter.api.Assertions.assertTrue(report.contains("Рядок 2: тестова помилка"));
    }
}