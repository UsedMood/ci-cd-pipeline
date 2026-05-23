Feature: Markdown kezelés
  Scenario: Fájl feltöltése API-n sikeres
    Given Az alkalmazás backendje fut
    When Feltöltök egy új fájlt a megadott végponton
    Then Látom, hogy a fájl elérhető az alkalmazásban

  Scenario: Markdown dokumentum tartalma HTML-lé konvertálható
    Given Az alkalmazás backendje fut
    When Feltöltök egy új fájlt a megadott végponton
    Then A dokumentum tartalma HTML formátumban lekérdezhető
