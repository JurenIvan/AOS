Radovi na cesti
---------------

### Zadatak

Stari most je uski most i stoga postavlja ograničenja na promet. Na njemu istovremeno smije biti najviše 3 automobila koji voze u istom smjeru. Simulirati automobile procesom **_Auto_**  koji obavlja niže navedene radnje. Napisati program koji stvara _N_  automobila, gdje je _N_ proizvoljan broj između 5 i 100 koji se određuje prilikom pokretanja programa te svakom automobilu dodjeljuje registarsku oznaku. Smjer se automobilu određuje nasumično.

Proces semafor određuje koji automobili će prijeći most, a početni smjer prijelaza se određuje nasumično te se zatim izmjenjuju. Prijelazak mosta se omogućuje kada se zabilježi 3 zahtjeva za prijelaz u trenutnom smjeru ili prođe X milisekundi, gdje je X slučajan broj između 500 i 1000. Prijelaz mosta traje Y milisekundi gdje je Y broj između 1000 i 3000.

Procesi međusobno komuniciraju uz pomoć **reda poruka** koristeći **raspodijeljeni centralizirani protokol**, gdje je **proces _Semafor_** odgovoran za međusobno isključivanje.

Proces Auto(registarska oznaka, smjer) {  
    // smjer = 0 ili 1  
    // registarska oznaka je redni broj automobila u sustavu  
    spavaj Z milisekundi; // Z je slučajan broj između 100 i 2000  
    pošalji zahtjev za prijelaz mosta i ispiši("Automobil registarska\_oznaka čeka na prelazak preko mosta");  
    po primitku poruke "Prijeđi" ispiši("Automobil registarska\_oznaka se popeo na most");  
    po primitku poruke "Prešao" ispiši("Automobil registarska\_oznaka je prešao most.");  
}

Napomene:

*   Obavezno komentirati izvorni tekst programa (programski kod).
*   Sve što u zadatku nije zadano, riješiti na proizvoljan način.