Szerepkör: Viselkedj Senior Java Fejlesztőként.

Feladat: Hozz létre egy Java webalkalmazást "MD-Searcher" névvel, amely WAR fájlként
buildelhető és külső servlet konténerbe (pl. Tomcat, Jetty) telepíthető.
Az alkalmazás szkenneljen be egy könyvtárat Markdown fájlok után, indexelje azokat
memóriában, és biztosítson egy keresési felületet REST API-n és egy egyszerű statikus
fronton keresztül.

Technológiai Stack:
- Nyelv: Java 11 (Kerülni kell a Records, Sealed Classes és Pattern Matching
  funkciókat)
- Build eszköz: Maven
- Csomagolás: WAR (maven-war-plugin)
- Markdown feldolgozás: Flexmark-java
- Tesztelés: JUnit 5 és Mockito
- Frontend: Egyszerű statikus HTML/Vanilla JS (WEB-INF-en kívül elhelyezve)
- Servlet API: Jakarta Servlet API 5.0 (provided scope)

Alapfunkciók és Követelmények:

Fájlbetöltés: Az alkalmazás indításakor (ServletContextListener) rekurzívan
szkennelje be a konfigurálható könyvtárat (alapértelmezett: ./data) .md fájlok után.

Indexelés:
- Markdown fájlok feldolgozásával vonja ki a Címet (első H1 vagy fájlnév)
  és a Tartalmat.
- A feldolgozott dokumentumokat szálbiztos, memóriában tárolt adatszerkezetben tárolja.
- A service-eket a ServletContext attribútumaiban kell tárolni (DI konténer nélkül).

Keresési logika:
- Implementáljon egy SearchService-t, amely egy lekérdezési sztringet vár.
- Adjon vissza egy alapvető relevancia-pontszám alapján rangsorolt dokumentumlistát
  (cím egyezés = 2 pont, törzs egyezés = 1 pont).

REST API (HttpServlet-ek):
- GET /api/search?q={query}: JSON formátumú keresési eredménylistát ad vissza.
- GET /api/document/{id}: Visszaadja egy adott dokumentum HTML-ben renderelt tartalmát.
- POST /api/upload: Multipart fájlfeltöltés kezelése, automatikus újraindexeléssel.

Statikus Frontend:
- Egy index.html fájl keresősávval és eredménymegjelenítő területtel.
- Vanilla JavaScript (Fetch API) a backenddel való kommunikációhoz.

Kódminőség:
- Standard POJO-k getter/setter/konstruktor metódusokkal.
- Tiszta, rétegzett architektúra (Model, Service, Servlet).
- Legyen egy main, hogy az alkalmazás IDE-ből is futtatható legyen
  (beágyazott Jetty csak a Main.java-ban, compile scope-ban
  hozzáadva).

Tesztelési Követelmény:
Biztosíts egységteszteket a SearchService-hez és a Markdown feldolgozási logikához
JUnit 5 segítségével. A kód legyen kellően szétcsatolt, hogy tesztelhető legyen
a szerver elindítása nélkül.

Projektstruktúra:
- Generáld le a szabványos Maven webapp struktúrát.
- A pom.xml-ben packaging=war legyen beállítva.
- A Jakarta Servlet API legyen provided scope-ban.
- Az alkalmazás WAR-ként deployolható legyen, de Main.java-val IDE-ből is futtatható.