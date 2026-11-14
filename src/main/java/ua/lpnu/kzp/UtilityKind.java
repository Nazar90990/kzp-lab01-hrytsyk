package ua.lpnu.kzp;

import java.util.Locale;

/** Категорія комунальної послуги та її підпис для відображення. */
public enum UtilityKind {
    ELECTRICITY("Електроенергія", "кВт·год"),
    WATER("Вода", "м³"),
    OTHER("Інше", "одиниця");

    private final String label;
    private final String unit;

    UtilityKind(String label, String unit) {
        this.label = label;
        this.unit = unit;
    }

    /** @return локалізована назва категорії */
    public String label() {
        return label;
    }

    /** @return одиниця вимірювання категорії */
    public String unit() {
        return unit;
    }

    /**
     * Визначає категорію за назвою; невідомі значення належать до {@link #OTHER}.
     *
     * @param label назва категорії
     * @return відповідна категорія або {@link #OTHER}
     */
    public static UtilityKind fromLabel(String label) {
        if (label == null) {
            return OTHER;
        }
        return switch (label.trim().toLowerCase(Locale.ROOT)) {
            case "електроенергія", "electricity", "electricityreading" -> ELECTRICITY;
            case "вода", "water", "waterreading" -> WATER;
            default -> OTHER;
        };
    }
}
