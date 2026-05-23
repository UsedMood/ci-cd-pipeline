Feature: Markdown kezelés
  Scenario: Fájl feltöltése API-n sikeres
    Given Az alkalmazás backendje fut
    When Feltöltök egy új fájlt a megadott végponton
    Then Látom, hogy a fájl elérhető az alkalmazásban

  Scenario: Markdown dokumentum HTML-lé konvertálása API-n sikeres
    Given Az alkalmazás backendje fut
    When Feltöltök egy markdown dokumentumot HTML konverzióhoz
    Then Látom, hogy a dokumentum tartalma HTML formátumban elérhető
