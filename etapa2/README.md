# <span style="color:Darkorange">GlobalWaves Project - Stage 2 - Pagination

## <span style="color:Magenta">Main</span>

In addition to the first stage, I added the new commands to the handleCommand
method, and for each command, I checked, before calling it, if the user for
whom the command is being executed exists, except for general commands, where
the user is not specified. I also used the Singleton design pattern to ensure
there is only one instance of the Library class, and after each test, I reset
the instance to null to avoid having data from the previous test.

## <span style="color:Magenta">Library</span>

I added most of the new commands in this class. I have 3 hashmaps where I store
regular users, artists, and hosts, with their names as the key. Deleting a user
was a bit more complex because I had to check if someone was on their page. For
an artist or host, I checked if someone was listening to their album or any
song from the album (for the artist), or if someone was listening to a podcast
(for the host). For a regular user, I checked if anyone was listening to their
playlist or any song in it. After these checks, I deleted the audio files from
the lists and verified if any user had liked or followed them, retracting these
accordingly. I also removed the likes or follows given by the deleted user to
other songs or playlists. For each user type, I added/removed/displayed
announcements, events, merchandise, albums, and so on, according to the command.

## <span style="color:Magenta">Page</span>

In the userPages package, there are 4 types of pages that a regular user
can be on. All of them extend the abstract class Page and implement the
PageAccept interface, which is used to implement the Visitor design pattern.
In the PagePrinter class, which implements the PageVisitor interface, I
implemented the visiting methods for each page type, which are used when a
regular user wants to display the page they are on. Each page has a listener
counter that increases or decreases depending on who is on the respective page,
and I did the same for audio files, which have a listener counter.

## <span style="color:Magenta">User</span>

In this class, I added specific methods for a regular user and a reference to
all existing albums sent to the SearchBar to enable searching through albums.

## <span style="color:Magenta">Player</span>

In Player, I implemented the specific commands for a playlist and albums,
which behave similarly.

## <span style="color:Magenta">Publicity</span>

The Announcement, Event, and Merch classes, specific to an artist or a
host, extend the Publicity class, where common variables and methods are
found.

### <span style="color:Aquamarine">User </span> implements:

    > SearchBarCommands
    > PlayerCommands
    > PlaylistCommands

### <span style="color:Aquamarine">Library </span> implements:

    > GeneralStatistics
    > Uses the Singleton design pattern to have a single instance
    > of the Library class

### <span style="color:Aquamarine">Song, Podcast, AudioFilesCollection:

    > AudioFiles

### <span style="color:Aquamarine">Album, Playlist</span> extend:

    > AudioFilesCollection

### <span style="color:Aquamarine">ArtistPage, HomePage, HostPage,

### <span style="color:Aquamarine">LikedContentPage</span> extend:

    > Page

### <span style="color:Aquamarine">ArtistPage, HomePage, HostPage,

### <span style="color:Aquamarine">LikedContentPage</span> implement:

    > PageAccept

### <span style="color:Aquamarine">PagePrinter</span> implements:

    > PageVisitor
    > Uses the Visitor design pattern to display page content

### <span style="color:Aquamarine">Announcement, Event, Merch</span> extend:

    > Publicity

## <span style="color:Darkorange">GeneralFeedback</span>

It was an interesting continuation of the first part. I can say it went much
better than the first one, mainly because the base was already done, and we
just had to develop it further.
