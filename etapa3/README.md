# <span style="color:Darkorange">Proiect GlobalWaves - Etapa 3 - Analytics & Recommendations


## <span style="color:Magenta">Main</span>
In main nu am adaugat mare lucru, doar noile comenzi de la aceasta etapa, iar
dupa terminarea testului efectiv, am trecut prin toti userii si am calculat
statusul curent, urmand sa apelez functia de end program, care afiseaza 
monetizarea pentru artisti in functie de ascultarile userilor.

## <span style="color:Magenta">Library</span>
O sa incep cu notificarile, deoarece cu ele am inceput rezolvarea etapei.
Pentru ele am folosit design patternul Observer, unde userul este un observer,
iar artistul, respectiv hostul sunt un subject. Lista de observeri este
actualizata in momentul in care se face subscribe sau unsubscribe. De fiecare
data cand artistul/hostul realizeaza o actiune, apelez functia de
notifyObserver, care se duce prin toti observerii artistului/hostului respectiv
si da update la resultsArrayul de notificari al userulul. Un al doilea design
pattern pe care l-am implementat este Strategy, folosit pentru metodele legate
de recomandari. In functie de comanda data, setez strategia corespunzator si
o execut. Exact la fel procedez si pentru comenzile de wrapped, unde de
asemenea implementez design patternul Strategy. Pentru a explica putin logica
folosita pentru wrapped, m-am folosit de clasa UsersHistory, in care am
pastrat istoricul fiecarui utilizator, tot ce a ascultat el in player am
contorizat in aceasta clasa in diferite hashmapuri. Aici am de asemenea
metodele pentru monetizarea premium si cea free. Pentru cea premium, de 
fiecare data cand un user da cancel la abonament, plateste pentru toate 
melodiile ascultate in perioada de abonament, iar pentru cea freedupa
terminarea unui ad, de care se tine cont in player, se vor plati artistii
ascultati intre 2 aduri date.

## <span style="color:Magenta">User</span>
Am mai adaugat in User cateva metode legate de navigarea intre pagini, de
cumparare a unui merch de la un artist si de load pentru diverse recomandari.
De asemenea am schimbat cateva lucruri si la search pentru album, deoarece
inainte tineam minte doar numele albumului dupa search si select si l cautam
pe urma in library pentru a-i da play, acum tin minte tot albumul pentru a-l
cauta si dupa artist.

## <span style="color:Magenta">Player</span>
In aceasta clasa am adaugat o contorizare a melodiilor care sunt ascultate
intre ad-uri, avand grija si la cazurile speciale, daca melodia nu s-a
terminat si urmeaza un load sau daca a fost dat load la un album inainte de
adbreak si trebuie sa tin minte melodia la care a ramas inainte de adbreak
pentru a relua ascultarea dupa advertisment.

### <span style="color:Aquamarine">User </span> implements:
    > SearchBarCommands
    > PlayerCommands
    > PlaylistCommands
    > Observer
### <span style="color:Aquamarine">Library </span> implements:
    > GeneralStatistics
    > se foloseste design pattern-ul Singleton pentru a avea o singura instanta
    > se foloseste design pattern-ul Observer pentru notificari
    > se foloseste design pattern-ul Strategy pentru recomandari
    > se foloseste design pattern-ul Strategy pentru wrapped
    > a clasei Library
### <span style="color:Aquamarine">Song, Podcast, AudioFilesCollection:
    > AudioFiles
### <span style="color:Aquamarine">Album, Playlist</span> extend:
    > AudioFilesCollection
### <span style="color:Aquamarine">ArtistPage, HomePage, HostPage,
### <span style="color:Aquamarine">LikedContentPage</span> extend:
    > Page
### <span style="color:Aquamarine">ArtistPage, HomePage, HostPage,
### <span style="color:Aquamarine">PagePrinter</span> implement:
    > PageVisitor
    > se foloseste design pattern-ul Visitor pentru a afisa continutul paginii
### <span style="color:Aquamarine">Announcement, Event, Merch</span> extend:
    > Publicity
### <span style="color:Aquamarine">Notifications </span> implements:
    > Observer
### <span style="color:Aquamarine">Artist, Host </span> implement:
    > Subject
### <span style="color:Aquamarine">FansPlaylistRecommendation,
### <span style="color:Aquamarine">RandomPlaylistRecommendation,
### <span style="color:Aquamarine">RandomSongRecommendation </span> implement:
    > RecommendationStrategy
### <span style="color:Aquamarine">ArtistStrategy, UserStrategy,
### <span style="color:Aquamarine">HostStrategy</span> implement:
    > AllUsersStrategy

## <span style="color:Darkorange">GeneralFeedback</span>
A fost mult mai complexa si mai complicata aceasta etapa, mi-a pus cu siguranta
rabdarea la incercare:)) Din pacate nu am reusit sa termin tot ce mi-am propus,
timpul nu a fost de partea mea, dar am incercat sa fac cat mai multe lucruri.
Niste explicatii aditionale pentru unele comenzi ar fi fost de mare ajutor,
poate niste exemple, dar oricum m-am descurcat cu ce am mai vazut pe forum.