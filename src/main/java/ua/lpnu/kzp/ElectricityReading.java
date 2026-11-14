package ua.lpnu.kzp;

/** Показник споживання електроенергії. */
public final class ElectricityReading extends UtilityReading {

    /**
     * Створює показник електроенергії.
     *
     * @param meter назва або ідентифікатор лічильника
     * @param date дата у форматі YYYY-MM-DD
     * @param value споживання в кВт·год
     * @param tariff тариф за кВт·год
     */
    public ElectricityReading(String meter, String date, double value, double tariff) {
        super(meter, date, value, tariff, UtilityKind.ELECTRICITY);
    }

    /** @return вартість за незмінною формулою value * tariff */
    @Override
    public double consumptionCost() {
        return value() * tariff();
    }
}
