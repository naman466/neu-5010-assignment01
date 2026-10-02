package edu.northeastern.shelter;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("current")
class AnimalAdditionalTest {

  @Test
  void nullNameMessageMentionsName() {
    IntakeException exception =
        assertThrows(
            IntakeException.class,
            () ->
                new Animal(
                    null,
                    Species.CAT,
                    AgeMonths.of(12),
                    LocalDate.of(2026, 9, 21)));

    assertTrue(exception
        .getMessage()
        .contains("name"));
  }

  @Test
  void nullSpeciesMessageMentionsSpecies() {
    IntakeException exception =
        assertThrows(
            IntakeException.class,
            () ->
                new Animal(
                    "Luna",
                    null,
                    AgeMonths.of(12),
                    LocalDate.of(2026, 9, 21)));

    assertTrue(exception
        .getMessage()
        .contains("species"));
  }
}