# <span style="color:Darkorange">Proiect GlobalWaves  - Etapa 1


## <span style="color:Magenta">Main</span>
   Am facut clasa Command care contine toate campurile posibile ale comenzilor, 
iar campurile care raman goale o sa fie null datorita JsonIgnoreProperties,
asadar in commands o sa am toate comenzile citite din teste.

## <span style="color:Magenta">Library</span>
Library implementeaza interfata GeneralStatistics ce contine metodele aferente.
In aceasta clasa am pastrat in arrayul de Songs toate melodiile citite din
library, in podcasts la fel si in hashmapul users am toti utilizatorii, cheia
fiind numele lor, iar allPlaylists va fi initial un array gol, care va fi
creat pe parcurs de fiecare user. Aici mai am si metodele de getTop5 care se
folosesc de o metoda generica de sortare.

## <span style="color:Magenta">User
In aceasta clasa am facut cam toata logica. In primul rand acesta implementeaza
interfetele PlayerCommand, PlaylistCommand si SearchBarCommand. Asadar, fiecare
user are un Search Bar, implementat in clasa <span style="color:pink">SearchBar
</span>, care detine cele doua functii specifice: search si select. In prima
functie, cu ajutorul metodei generice matchFilters, caut in melodii, podcasturi
si playlisturile utilizatorului, respectiv in toate playlisturile existente,
primele 5 rezultate care se potrivesc filtrelor. Cele trei clase, Podcast, 
Playlist si Song mostenesc clasa AudioFiles care are implementata metoda de
MatchFilters comuna pentru Podcast si playlist, dar pe care Song o suprascrie.

In playlist mai am si metodele pentru comanda follow care ma ajuta sa tin un
contor pentru numarul de urmaritori ai playlistului curent si acelasi lucru in
clasa Song pentru numarul de likeuri primite de la utilizatori.
In clasa <span style="color:pink">Player</span>, am gandit toata logica pentru
player si am implementat toate functiile specifice acestuia. In metodele de
calculate status, care predomina in aceasta clasa am calculat in functie de
repeat, pentru fisierul audio curent care ruleaza starea in care acesta afla.
Astfel actualizez statusul playerului la inceputul fiecarei metode din aceasta
clasa si actualizez de asemenea si lastCommandTimestamp de care tin cont in
metoda de calculate. Am ales toti membri din aceasta clasa pentru a-mi fi usor
sa calculez starea in care se afla playerul in orice moment, de asta ma
folosesc la podcast si de clasa PodcastHistory in care retin episodul si
secunda la care a ramas. O alta metoda importanta din Player este next, care
de asemenea, in functie de reapeat si de playmode, care reprezinta ce se
ruleaza in momentul curent, trece mai departe, repeta fisierul curent, se
repeta la infinit sau se opreste de tot.

De asemenea, toate metodele de load, playPause, repeat etc. sunt implementate
in  clasa User conform cerintelor, fiecare avand mesajele de eroare aferente.

### <span style="color:Aquamarine">User </span> implements:
    > SearchBarCommands
    > PlayerCommands
    > PlaylistCommands
### <span style="color:Aquamarine">Library </span> implements:
    > GeneralStatistics
### <span style="color:Aquamarine">Song,Playlist,Podcast </span> extend:
    > AudioFiles

## <span style="color:Darkorange">GeneralFeedback
A fost o tema foarte interesanta, care chiar mi-a pus rabdarea la incercare, am
trecut prin multe stari de la frustrare la bucurie, dar intr-un final am reusit
cu greu sa o termin. Mi s-a parut cam lunga si am avut multe de implementat
pentru o prima tema dar a fost o experienta interesanta.