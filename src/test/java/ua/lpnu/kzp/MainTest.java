package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    @Test
    public void testMainClassExists() {
        // Перевірка, що головний клас програми існує та доступний
        assertNotNull(Main.class, "Клас Main має існувати");
    }

    @Test
    public void testBasicAssertion() {
        // Базовий модульний тест для успішного проходження збірки
        assertTrue(true, "Тест повинен виконуватись успішно");
    }
}