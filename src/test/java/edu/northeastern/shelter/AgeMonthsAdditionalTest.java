package edu.northeastern.shelter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("current")
class AgeMonthsAdditionalTest {

  @Test
  void ageJustBelowMaximumHasCorrectBreakdown() {
    AgeMonths age = AgeMonths.of(AgeMonths.MAX_MONTHS - 1);

    assertEquals(39, age.years());
    assertEquals(11, age.remainderMonths());
    assertEquals("39 years, 11 months", age.toString());
  }

  @Test
  void negativeAgeMessageMentionsRejectedValue() {
    IntakeException exception =
        assertThrows(IntakeException.class, () -> AgeMonths.of(-1));

    assertTrue(exception
        .getMessage()
        .contains("-1"));
  }
}