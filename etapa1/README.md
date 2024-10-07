# <span style="color:Darkorange">GlobalWaves Project - Part one

## <span style="color:Magenta">Main</span>

I created the Command class, which contains all possible fields of
commands, and the fields that remain empty will be null due to
JsonIgnoreProperties. Therefore, in commands, I will have all the
commands read from the tests.

## <span style="color:Magenta">Library</span>

The Library implements the GeneralStatistics interface, which contains
the relevant methods. In this class, I kept all the songs read from the
library in a Songs array, similarly for podcasts, and in the users
hashmap, I have all the users, with their name as the key. allPlaylists
will initially be an empty array, which will be created gradually by each
user. Here, I also have the getTop5 methods, which use a generic sorting
method.

## <span style="color:Magenta">User</span>

In this class, I implemented most of the logic. First of all, it implements
the PlayerCommand, PlaylistCommand, and SearchBarCommand interfaces.
Therefore, each user has a search bar, implemented in the SearchBar class,
which contains two specific functions: search and select. In the first
function, with the help of the generic matchFilters method, I search
through the user's songs, podcasts, and playlists, as well as through all
the existing playlists, for the first 5 results that match the filters.
The three classes, Podcast, Playlist, and Song, inherit from the
AudioFiles class, which has the matchFilters method implemented for
both podcasts and playlists, but which Song overrides.

In the Playlist class, I also have methods for the follow command,
which helps me keep a counter for the number of followers of the current
playlist, and the same goes for the Song class for the number of likes
received from users. In the Player class, I designed all the logic for
the player and implemented all its specific functions. In the
calculateStatus methods, which predominate in this class, I calculate
the status of the current audio file being played, depending on repeat.
Thus, I update the player's status at the beginning of each method in this
class and also update the lastCommandTimestamp, which I keep track of in
the calculate method. I chose all the members of this class to easily
calculate the player's status at any moment, and I use this also for
podcasts with the PodcastHistory class, where I store the episode and
the second where it left off. Another important method in Player is
next, which, depending on repeat and playMode, which represents what
is currently being played, either moves to the next file, repeats the
current file, repeats infinitely, or stops altogether.

Additionally, all the methods such as load, playPause, repeat, etc.,
are implemented in the User class according to the requirements, each
with its corresponding error messages.

### <span style="color:Aquamarine">User </span> implements:

    > SearchBarCommands
    > PlayerCommands
    > PlaylistCommands

### <span style="color:Aquamarine">Library </span> implements:

    > GeneralStatistics

### <span style="color:Aquamarine">Song,Playlist,Podcast </span> extend:

    > AudioFiles

## <span style="color:Darkorange">GeneralFeedback

This was a very interesting project that really tested my patience. I went
through many states, from frustration to joy, but in the end, I managed to
finish it.
