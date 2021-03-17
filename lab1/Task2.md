Baza podataka
-------------

### Zadatak

Pretpostavimo da u sustavu imamo _N_ procesa i jednu bazu podataka (u ovom slučaju baza se simulira nizom struktura podataka) koja za svaki proces struktura podataka procesa sadrži identifikator procesa, vrijednost logičkog sata procesa te broj ulazaka u kritički odsječak procesa. Neka je baza podataka dijeljena između procesa na način da je promjena vrijednosti od strane jednog procesa vidljiva svim ostalim procesima. Pristup bazi podataka predstavlja kritički odsječak: najviše jedan proces u svakom trenutku može biti u kritičkom odsječku. Unutar kritičkog odsječka, svaki proces ponavlja 5 puta sljedeće radnje.

1.  U bazi podataka, ažurira svoju vrijednost logičkog sata trenutnom i inkrementira svoj broj ulazaka u kritički odsječak.
2.  Ispiše sadržaj cijele (ne samo svog unosa) baze podataka na standardni izlaz.
3.  Spava X milisekundi gdje je X je slučajan broj između 100 i 2000

Na početku glavni proces stvara _N_  procesa (broj _N_ se zadaje i može biti u intervalu \[3,10\]) koji dalje međusobno komuniciraju običnim ili imenovanim **cjevovodima** (svejedno). Sinkronizirajte pristupanje bazi podataka koristeći

*   **Lamportov raspodijeljeni protokol** (rješavaju studenti čija je **zadnja** znamenka JMBAG **parna**) ili
*   **protokol Ricarta i Agrawala** (rješavaju studenti čija je **zadnja** znamenka JMBAG **neparna**).

Napomene:

*   Bazu podataka možete definirati kao "struct db\_entry database\[N\]".
*   Za dijeljenje baze podataka između procesa koristiti zajednički spremnik (sustavski pozive mmap ili shmat).
*   Svi procesi ispisuju poruku koju šalju i poruku koju primaju.
*   Obavezno komentirati izvorni tekst programa (programski kod).
*   _Sve što u zadatku nije zadano, riješiti na proizvoljan način._