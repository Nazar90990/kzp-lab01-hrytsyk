package ua.lpnu.kzp;

/** Показник споживання води. */
public final class WaterReading extends UtilityReading {

    /**
     * Створює показник води.
     *
     * @param meter назва або ідентифікатор лічильника
     * @param date дата у форматі YYYY-MM-DD
     * @param value споживання у м³
     * @param tariff тариф за м³
     */
    public WaterReading(String meter, String date, double value, double tariff) {
        super(meter, date, value, tariff, UtilityKind.WATER);
    }

    /** @return вартість за незмінною формулою value * tariff */
    @Override
    public double consumptionCost() {
        return value() * tariff();
    }
}
