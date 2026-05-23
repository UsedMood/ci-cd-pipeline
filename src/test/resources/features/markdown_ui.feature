@ui
Feature: Markdown UI ellenőrzés

  Scenario: A felhasználó fel tud tölteni egy fájlt és azonnal látja azt a UI-on
    Given A főoldalon vagyok
    When Feltöltök egy új fájlt
    Then Látom, hogy a feltöltés sikeres
    Then Látom, hogy a fájl kereshető

  Scenario: A markdown dokumentum tartalma HTML formában jelenik meg
    Given Egy feltöltött dokumentum kereshető a UI-on
    When Megnyitom a dokumentumot
    Then A tartalom HTML formában jelenik meg
