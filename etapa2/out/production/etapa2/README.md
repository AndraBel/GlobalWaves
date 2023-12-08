# <span style="color:Darkorange">Proiect GlobalWaves - Etapa 2 - Pagination</span>

## <span style="color:Magenta">Main</span>
In plus fata de prima etapa am adaugat in metoda handleCommand si noile comenzi,
iar pentru fiecare comanda am verificat, inainte de a o apela, daca utilizatorul
pentru care se va efectua comanda exista, exceptand comenzile generale, pentru
care utilizatorul nu este specificat. De asemenea, am folosit design pattern-ul
Singleton pentru a avea o singura instanta a clasei Library, iar dupa fiecare
test setez instanta la null pentru a nu avea datele de la testul anterior.

## <span style="color:Magenta">Library</span>
Am adaugat majoritatea comenzilor noi in aceasta clasa. Am 3 hashmapuri in care
tin utilizatorii normali, artistii si hoastii, iar cheia este numele acestora.
O parte mai complexa mi s-a parut stergereau unui utilizator, pentru ca a
trebuit verificat daca se afla cineva pe pagina lui, pentru artist si host,
daca cineva ii asculta albumul sau orice melodie din album pentru artist, daca
asculta cineva vreun podcast la host, iar la user normal daca ii asculta cineva
playlistul sau orice melodie din playlist. In urma acestor verificari am sters
si fisierele audio din liste, si am verificat daca exista vreun user care le-a
dat like sau follow pentru a-l retrage. De asemenea, am retras si din partea 
utilizatorului sters likeurile sau followurile date altor melodii/playlisturi.
In rest, pentru fiecare tipuri de utilizator am adaugat/sters/afisat conform
comenzii anunturi, evenimente, merchuri, albume si asa mai departe.

## <span style="color:Magenta">Page</span>
In pachetul userPages exista cele 4 tipuri de pagini pe care se poate afla un
utilizator normal. Toate acestea extind clasa abstracta Page si implementeaza 
interfata PageAccept folosita pentru a implementa design pattern-ul Visitor.
In clasa PagePrinter care implementeaza interfata PageVisitor, sunt implementate
metodele de vizitare pentru fiecare tip de pagina, care vor fi folosite atunci
cand un user normal vrea sa afiseze pagina pe care se afla. Fiecare pagina are
un numar de ascultatori salvat care creste si scade in functie de cine este 
pe pagina respectiva, la fel am facut si pentru fisierele audio care au un 
contor pentru numarul de ascultatori.

## <span style="color:Magenta">User
In aceasta clasa am adaugat metodele specifice unui user normal si o referinta
la toate albumele existente trimisa in SearchBar pentru a putea efectua cautarea
pe albume.

## <span style="color:Magenta">Player
In PLayer am facut comenzile specifice unui playlist si pentru albume care se
comporta aproximativ la fel.

## <span style="color:Magenta">Publicity
Clase Announcement, Event si Merch specifice unui artist sau unui host, extind
clasa Publicity unde se gasesc variabilele si metodele comune acestora.

### <span style="color:Aquamarine">User </span> implements:
    > SearchBarCommands
    > PlayerCommands
    > PlaylistCommands
### <span style="color:Aquamarine">Library </span> implements:
    > GeneralStatistics
    > se foloseste design pattern-ul Singleton pentru a avea o singura instanta
    > a clasei Library
### <span style="color:Aquamarine">Song, Podcast, AudioFilesCollection:
    > AudioFiles
### <span style="color:Aquamarine">Album, Playlist</span> extend:
    > AudioFilesCollection
### <span style="color:Aquamarine">ArtistPage, HomePage, HostPage,
### <span style="color:LikedContentPage</span> extend:
    > Page
### <span style="color:Aquamarine">ArtistPage, HomePage, HostPage,
### <span style="color:LikedContentPage</span> implement:    
> PageAccept
### <span style="color:Aquamarine">PagePrinter</span> implements:
    > PageVisitor
    > se foloseste design pattern-ul Visitor pentru a afisa continutul paginii
### <span style="color:Aquamarine">Announcement, Event, Merch</span> extend:
    > Publicity

## <span style="color:Darkorange">GeneralFeedback</span>
A fost o continuare interesanta a primei parti, pot spune ca a mers mult mai
bine decat prima totusi, pentru faptul ca era deja facuta baza, trebuia doar sa
dezvoltam.





