package ua.lpnu.kzp;

/** Один перевірений запис про комунальне споживання. */
public record UtilityRecord(String meter, String date, double value, double tariff) {
}