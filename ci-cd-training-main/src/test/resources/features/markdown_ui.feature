@ui
Feature: Markdown UI ellenőrzés

  Scenario: A felhasználó fel tud tölteni egy fájlt és azonnal látja azt a UI-on
    Given A főoldalon vagyok
    When Feltöltök egy új fájlt
    Then Látom, hogy a feltöltés sikeres
    Then Látom, hogy a fájl kereshető

  Scenario: A markdown dokumentum HTML formában megjelenik
    Given A főoldalon vagyok
    When Feltöltök egy markdown dokumentumot
    And Megnyitom a dokumentumot
    Then HTML formában látom a dokumentum tartalmát
